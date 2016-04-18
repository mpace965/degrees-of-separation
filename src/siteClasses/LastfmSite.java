package siteClasses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import API.LastfmArtist;
import API.LastfmTag;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LastfmSite implements Site {

	private HashMap<String, Node> allNodes;
	private final String apiKey = "c6c45e68f6b2a663da996fc504cf9f8b";
	private Long apiTimer;
	private int fileAccesses = 0;
	private LastfmNode start;
	private LastfmNode end;
	private Double heuristicConstant;

	public LastfmSite() {
		this.allNodes = new HashMap<String, Node>();
		this.apiTimer = null;
		this.start = null;
		this.end = null;
		this.heuristicConstant = null;
	}

	public Node getStartNode() {
		return this.start;
	}

	public Node getEndNode() {
		return this.end;
	}

	public int getAccessCount() {
		return this.fileAccesses;
	}

	public HashMap<String, Node> getAllNodes() {
		return this.allNodes;
	}

	public void resetAccessCount() {
		this.fileAccesses = 0;
	}

	public void setStartAndEndNodes(String startStr, String endStr) throws Exception {
		if (startStr == null || endStr == null || startStr.length() < 1 || endStr.length() < 1)
			throw new Exception("Invalid parameters");

		// check if nodes exist
		JsonObject startJson = getInfoJson(startStr, null);
		JsonObject endJson = getInfoJson(endStr, null);
		String startName = getNameFromJson(startJson);
		String endName = getNameFromJson(endJson);

		if (startName == null)
			throw new Exception("Start node DNE");
		if (endName == null)
			throw new Exception("End node DNE");

		// get mbid if it has it
		String startMbid = getMbidFromJson(startJson);
		String endMbid = getMbidFromJson(endJson);

		// check if they exist in the hashmap
		String startID = LastfmNode.getID(startName, startMbid);
		String endID = LastfmNode.getID(endName, endMbid);

		this.start = (LastfmNode) this.allNodes.get(startID);
		this.end = (LastfmNode) this.allNodes.get(endID);

		// if they're still null at this point, create new nodes
		if (this.start == null) {
			this.start = new LastfmNode(startName, startMbid);
			this.allNodes.put(this.start.getNodeID(), this.start);
		}
		if (this.end == null) {
			this.end = new LastfmNode(endName, startMbid);
			this.allNodes.put(this.end.getNodeID(), this.end);
		}
		
		// set heuristic constant based on start and end
		this.heuristicConstant = heuristicCompare(this.start, this.end);
	}

	// Heuristics

	public double heuristicMultiplier(Node p, Node n) {
		// returns the multiplier that the "match" variable will be adjusted by
		if (n == null)
			return 1d;

		LastfmNode node = (LastfmNode) n;
		LastfmNode prev = (LastfmNode) p;

		Double nodeToEnd = heuristicCompare(node, this.end);
		Double nodeToPrev = heuristicCompare(node, prev);

		double ret = Math.abs(nodeToEnd - this.heuristicConstant);

		if (node.equals(this.start)) {
			Double prevToEnd = heuristicCompare(prev, this.end);
			return (Math.abs(prevToEnd - nodeToEnd) / nodeToPrev);
		}
		else {
			return (ret / nodeToPrev);
		}
	}

	public double heuristicDifference(Node n) {
		// returns the difference that the "match" variable will be adjusted by
		if (n == null)
			return 0d;

		LastfmNode node = (LastfmNode) n;

		Double nodeToEnd = heuristicCompare(node, this.end);
		if (nodeToEnd == null)
			return 0d;
		
		double ret = Math.abs(nodeToEnd - this.heuristicConstant);

		if (nodeToEnd > this.heuristicConstant) {
			// middles
			ret = 0 - ret;
			//			if (nodeToStart > this.heuristicConstant) 	// middle left
			//			else 										// middle right
		}
		//	else {	if (nodeToEnd > nodeToStart) 	// far left side
		//			else 							// far right side

		return ret;
	}

	public Double heuristicCompare(LastfmNode start, LastfmNode end) {
		// estimates match value a artist.getSimilar call
		// where artist = startNode and endNode is a similar artist
		// i.e. how similar endnode is to startnode
		if (start == null || end == null)
			return null;
		
		if (start.getTags() == null)
			populateTags(start);
		if (end.getTags() == null)
			populateTags(end);
		if (start.getTagCount() == null || start.getTagCount() < 1
				|| end.getTagCount() == null || end.getTagCount() < 1
				|| start.getTags().size() < 1 || end.getTags().size() < 1)
			return null;
		
		HashMap<String, Integer> tag1 = start.getTags();
		HashMap<String, Integer> tag2 = end.getTags();
		Integer tag1Count = start.getTagCount();
		Integer tag2Count = end.getTagCount();

		int incommon = 0;
		Map.Entry<String, Integer> entry;
		Iterator<Map.Entry<String, Integer>> it = tag1.entrySet().iterator();
		while (it.hasNext()) {
			entry = it.next();
			String tag = entry.getKey();
			int min = entry.getValue();

			Integer temp = tag2.get(tag);
			if (temp != null) {
				if (temp < min)
					min = temp;
				incommon += min;
			}
		}

		double ret = (2d * incommon) / (tag1Count + tag2Count); 
		return ret;
	}

	// API calls

	public void populateConnections(Node n) {
		if (n == null) 
			return;

		LastfmNode node = (LastfmNode) n;
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		// get Json object
		JsonObject json = null;
		try {
			json = getConnectionsJson(node);
		}
		catch (Exception e) {
			System.err.printf("Could not get connections json for node: %s\n", node.toString());
			System.err.printf("Error message: %s\n", e.getMessage());
		}

		ArrayList<Node> connections = new ArrayList<Node>();
		node.setConnections(connections);

		// null checks all along the way
		if (json == null || !json.has("similarartists")) 
			return;
		JsonObject similarartists = json.getAsJsonObject("similarartists");

		if (!similarartists.has("artist")) 
			return;
		JsonArray artists = similarartists.getAsJsonArray("artist");

		for (JsonElement elem : artists) {
			JsonObject artist = elem.getAsJsonObject();

			// invalid if node doesn't have a name or match%
			if (!artist.has("name") || !artist.has("match"))
				continue;

			String name = artist.get("name").getAsString();
			Double match = artist.get("match").getAsDouble();
			String mbid = artist.has("mbid") ? artist.get("mbid").getAsString() : null;
			if (name == null || name.length() < 1 || match == null)
				continue;
			if (mbid != null && mbid.length() < 1)
				mbid = null;

			// check if node already exists
			String id = LastfmNode.getID(name, mbid);
			Node neighbor = allNodes.get(id);

			if (neighbor == null) 
				neighbor = new LastfmNode(name, mbid, match);

			connections.add(neighbor);
		}
	}

	public void populateTags(LastfmNode node) {
		if (node == null)
			return;

		// get Json object
		JsonObject json = null;
		try {
			json = getTagsJson(node);
		}
		catch (Exception e) {
			System.err.printf("Could not get tags json for node: %s\n", node.toString());
			System.err.printf("Error message: %s\n", e.getMessage());
		}
		
		HashMap<String, Integer> tagMap = new HashMap<String, Integer>();
		node.setTags(tagMap);

		if (json == null || !json.has("toptags")) 
			return;
		JsonObject toptags = json.getAsJsonObject("toptags");

		if (!toptags.has("tag"))
			return;
		JsonArray tags = toptags.getAsJsonArray("tag");
		
		Integer tagCount = 0;
		for (JsonElement elem : tags) {
			JsonObject tag = elem.getAsJsonObject();
			
			if (!tag.has("name") || !tag.has("count"))
				continue;
			
			String name = tag.get("name").getAsString();
			int count = tag.get("count").getAsInt();
			
			if (count == 0 || name == null || name.length() < 1)
				continue;
			tagMap.put(name, count);
			tagCount += count;
		}
		node.setTagCount(tagCount);
	}

	private String getNameFromJson(JsonObject json) {
		if (json == null || !json.has("artist"))	
			return null;
		JsonObject artist = json.get("artist").getAsJsonObject();

		if (!artist.has("name"))	
			return null;
		String name = artist.get("name").getAsString();

		if (name == null || name.length() < 1)
			return null;

		return name;
	}
	private String getMbidFromJson(JsonObject json) {
		if (json == null || !json.has("artist"))	
			return null;
		JsonObject artist = json.get("artist").getAsJsonObject();

		if (!artist.has("mbid"))	
			return null;
		String mbid = artist.get("mbid").getAsString();

		if (mbid == null || mbid.length() < 1)
			return null;

		return mbid;
	}
	private JsonObject getInfoJson(LastfmNode node) throws Exception {
		if (node == null) 	
			return null;
		return getInfoJson(node.getName(), node.getMbid());
	}
	private JsonObject getConnectionsJson(LastfmNode node) throws Exception {
		if (node == null)
			return null;
		return getConnectionsJson(node.getName(), node.getMbid());
	}
	private JsonObject getTagsJson(LastfmNode node) throws Exception {
		if (node == null)
			return null;
		return getTagsJson(node.getName(), node.getMbid());
	}

	private JsonObject getInfoJson(String name, String mbid) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&format=json&api_key=" + apiKey;
		return getJsonObject(url, name, mbid);
	}
	private JsonObject getConnectionsJson(String name, String mbid) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&format=json&api_key=" + apiKey;
		return getJsonObject(url, name, mbid);
	}
	private JsonObject getTagsJson(String name, String mbid) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getTopTags&format=json&api_key=" + apiKey;
		return getJsonObject(url, name, mbid);
	}
	private JsonObject getJsonObject(String url, String name, String mbid) throws Exception {
		if (url == null || (name == null && mbid == null))
			throw new Exception("url or name and mbid are null");

		if (name != null && mbid != null) {
			JsonObject json = null;
			Exception except = new Exception();
			// try both
			try {
				json = getJson(url + "&artist=" + URLEncoder.encode(name, "UTF-8") + "&mbid=" + mbid);
			}
			catch (Exception e) {
				except = e;
			}
			if (json != null)
				return json;

			// try just mbid
			try {
				json = getJson(url + "&mbid=" + mbid);
			}
			catch (Exception e) {
				except = e;
			}
			if (json != null)
				return json;

			// try just name
			try {
				json = getJson(url + "&artist=" + URLEncoder.encode(name, "UTF-8"));
			}
			catch (Exception e) {
				except = e;
			}
			if (json != null)
				return json;
			else 
				throw except;
		}

		if (name != null) 
			return getJson(url + "&artist=" + URLEncoder.encode(name, "UTF-8"));
		if (mbid != null) 
			return getJson(url + "&mbid=" + mbid);
		return null;
	}
	private JsonObject getJson(String url) throws Exception {
		JsonObject simArt = null;

		long currentTime = System.currentTimeMillis();
		if (apiTimer == null)
			apiTimer = currentTime;
		while (currentTime - apiTimer < 200l) 
			currentTime = System.currentTimeMillis();
		apiTimer = currentTime;

		// Builds a buffered reader to interpret input received from the API
		// request
		URL lastfmGetSimilar = new URL(url);
		URLConnection lfmSim = lastfmGetSimilar.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(lfmSim.getInputStream()));

		StringBuilder builder = new StringBuilder();
		String inputLine = null;
		try {
			// Reads in a string received from the API requests
			while ((inputLine = in.readLine()) != null) {
				builder.append(inputLine);
			}
			fileAccesses++;
			System.err.println("api calls: " + fileAccesses);
		}
		finally {
			// Closes the buffered reader
			in.close();
		}

		// Converts the string to a Json object
		JsonParser parser = new JsonParser();
		simArt = parser.parse(builder.toString()).getAsJsonObject();

		if (simArt.has("error")) {
			throw new Exception(simArt.get("message").getAsString());
		}

		return simArt;
	}

	// For front-end

	public ArrayList<LastfmArtist> toLastfmArtists(ArrayList<Node> nodes) {
		ArrayList<LastfmArtist> artists = new ArrayList<LastfmArtist>();

		for (Node node : nodes) {
			LastfmNode lastfmNode = (LastfmNode) node;
			if (lastfmNode == null)
				return null;

			JsonObject json = null;
			try {
				json = getInfoJson(lastfmNode);
			}
			catch (Exception e) {
				System.err.printf("Could not get info json for node: %s\n", lastfmNode.toString());
				System.err.printf("Error message: %s\n", e.getMessage());
			}
			if (json == null)
				return null;
			lastfmNode.setJson(json);

			if (!json.has("artist"))
				return null;
			JsonObject jsonArtist = json.get("artist").getAsJsonObject();

			if (!jsonArtist.has("name"))
				return null;

			LastfmArtist artist = new LastfmArtist();
			artist.setName(jsonArtist.get("name").getAsString());

			try {
				artist.setListeners(jsonArtist.get("stats").getAsJsonObject().get("listeners").getAsInt());
				artist.setPlaycount(jsonArtist.get("stats").getAsJsonObject().get("playcount").getAsInt());
				artist.setBio(jsonArtist.get("bio").getAsJsonObject().get("summary").getAsString());
				artist.setImage(jsonArtist.get("image").getAsJsonArray().get(2).getAsJsonObject().get("#text").getAsString());

				JsonArray tags = jsonArtist.get("tags").getAsJsonObject().get("tag").getAsJsonArray();
				for (int i = 0; i < tags.size(); i++) {
					String name = tags.get(i).getAsJsonObject().get("name").getAsString();
					String url = tags.get(i).getAsJsonObject().get("url").getAsString();
					artist.addTag(new LastfmTag(name, url));
				}
			}
			catch (Exception e) {
				// Do nothing, errors in here are OK
			}

			artists.add(artist);
		}

		return artists;
	}
}
