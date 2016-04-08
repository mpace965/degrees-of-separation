package siteClasses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import API.LastfmArtist;
import API.LastfmTag;

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

	public double heuristicMultiplier(Node p, Node n) {
		// returns the difference that the "match" variable will be adjusted by
		if (n == null)
			return 1d;
		if (heuristicConstant == null) 
			heuristicConstant = heuristicCost(this.start, this.end);

		LastfmNode node = (LastfmNode) n;
		LastfmNode prev = (LastfmNode) p;
		if (node.getTags() == null) 
			populateTags(node);
		if (prev.getTags() == null)
			populateTags(prev);

		double nodeToEnd = heuristicCost(node, this.end);
		double nodeToPrev = heuristicCost(node, prev);

		double ret = Math.abs(nodeToEnd - this.heuristicConstant);

		if (node.equals(this.start)) {
			double prevToEnd = heuristicCost(prev, this.end);
			return (Math.abs(prevToEnd - nodeToEnd) / nodeToPrev);
		}
		else {
			return (ret / nodeToPrev);
		}
	}

	public double heuristicCost(Node n) {
		// returns the difference that the "match" variable will be adjusted by
		if (n == null)
			return 1d;
		if (heuristicConstant == null) 
			heuristicConstant = heuristicCost(this.start, this.end);

		LastfmNode node = (LastfmNode) n;
		if (node.getTags() == null) 
			populateTags(node);

		double nodeToEnd = heuristicCost(node, this.end);
		//		double nodeToPrev = heuristicCost(node, this.start);

		double ret = Math.abs(nodeToEnd - this.heuristicConstant);

		if (nodeToEnd > this.heuristicConstant) {
			// middles
			ret = 0 - ret;
			//			if (nodeToStart > this.heuristicConstant) 	// middle left
			//			else 										// middle right
		}
		//		else {
		//			if (nodeToEnd > nodeToStart) 	// far left side
		//			else 							// far right side
		//			
		//		}

		return ret;
	}

	public double heuristicCost(LastfmNode start, LastfmNode end) {
		// estimates match value a artist.getSimilar call
		// where artist = startNode and endNode is a similar artist
		// i.e. how similar endnode is to startnode
		double tag1tot = (double) start.getTagTotal();
		double tag2tot = (double) end.getTagTotal();
		if (tag1tot == 0 || tag2tot == 0)
			return 1d;
		HashMap<String, Integer> tag1 = start.getTags();
		HashMap<String, Integer> tag2 = end.getTags();

		double incommon = 0;
		Integer temp, min;
		String tag;
		HashMap.Entry<String, Integer> entry;
		Iterator<HashMap.Entry<String, Integer>> it = tag1.entrySet().iterator();
		while (it.hasNext()) {
			entry = it.next();
			tag = entry.getKey();
			min = entry.getValue();

			temp = tag2.get(tag);
			if (temp != null) {
				if (temp < min)
					min = temp;
				incommon += min.intValue();
			}
		}

		double ret = (2d * incommon) / (tag1tot + tag2tot); 
		return ret;
	}

	public void populateConnections(Node n) {
		LastfmNode node = (LastfmNode) n;
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		JsonObject json;
		JsonArray similar;
		try {
			json = getSimilarArtistsJson(node);
			similar = json.getAsJsonObject("similarartists").getAsJsonArray("artist");
		}
		catch (Exception e) {
			System.err.println("Problem in populate info");
			e.printStackTrace();
			return;
		}

		JsonObject tempObj;
		String name, mbid;
		Double match;
		for (JsonElement elem : similar) {
			tempObj = elem.getAsJsonObject();
			name = tempObj.has("name") ? tempObj.get("name").getAsString() : null;
			mbid = tempObj.has("mbid") ? tempObj.get("mbid").getAsString() : null;
			match = tempObj.has("match") ? tempObj.get("match").getAsDouble() : null;

			LastfmNode tempNode = new LastfmNode(mbid, name, match);
			if (allNodes.containsKey(tempNode.getNameUrl())) 
				node.addConnection(allNodes.get(tempNode.getNameUrl()));
			else if (allNodes.containsKey(tempNode.getMbid())) 
				node.addConnection(allNodes.get(tempNode.getMbid()));
			else {
				allNodes.put(tempNode.getNodeID(), tempNode);
				node.addConnection(tempNode);
			}
		}
	}

	public void populateInfo(LastfmNode node) {
		try {
			if (node.getJson() != null) {
				System.err.println("Populate info: node already has json");
				return;
			}
			JsonObject json = getInfoJson(node);
			String mbid = json.getAsJsonObject("artist").get("mbid").getAsString();
			node.setMbid(mbid);
			node.setJson(json);
		}
		catch (Exception e) {
			System.err.println("Problem in populate info");
			e.printStackTrace();
			return;
		}
	}

	public ArrayList<LastfmArtist> toLastfmArtists(ArrayList<Node> nodes) {
		// TODO cast to lastfmnode
		ArrayList<LastfmArtist> artists = new ArrayList<LastfmArtist>();
//		for (LastfmNode node : nodes) {
//			populateInfo(node);
////			String mbid = json.getAsJsonObject("artist").get("mbid").getAsString();
//		}
//		LastfmNode lfmN = (LastfmNode) n;
//		lfmN.getJson();
//		
//		LastfmArtist artist = new LastfmArtist();
//		ArrayList<LastfmTag> tags = artist.getTags();
//		
//		artist.setName(null);
//		artist.setImage(null);
//		artist.setListeners(0);
//		artist.setPlaycount(0);
//		artist.setBio(null);
//		
//		for (LastfmTag t : tags) {
//			artist.addTag(t);
//		}
		return artists;
	}

	public void populateTags(LastfmNode node) {
		if (node.getTags() != null && node.getTags().size() > 0)
			return;
		JsonArray tags;
		try {
			JsonObject json = getTagsJson(node);
			tags = json.getAsJsonObject("toptags").getAsJsonArray("tag");
		}
		catch (Exception e) {
			System.err.println("Problem in populate tags");
			e.printStackTrace();
			return;
		}

		JsonObject tempObj;
		String tag;
		Integer count;
		for (JsonElement elem : tags) {
			tempObj = elem.getAsJsonObject();
			tag = tempObj.has("name") ? tempObj.get("name").getAsString() : null;
			count = tempObj.has("count") ? tempObj.get("count").getAsInt() : null;
			if (tag != null && count != null) {
				node.addTag(tag, count);
			}
		}
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

	public String setStartAndEndNodes(String startName, String endName) {
		this.start = null;
		this.end = null;
		LastfmNode start = new LastfmNode(null, startName);
		LastfmNode end = new LastfmNode(null, endName);

		populateTags(start);
		populateTags(end);
		populateInfo(start);
		populateInfo(end);

		if (start.getTags() == null && end.getTags() == null) 
			return startName + " and " + endName;
		else if (start.getTags() == null) 
			return startName;
		else if (end.getTags() == null) 
			return endName;

		if (allNodes.containsKey(start.getNodeID())) 
			this.start = (LastfmNode) allNodes.get(start.getNodeID());
		else 
			this.start = start;
		if (allNodes.containsKey(end.getNodeID())) 
			this.end = (LastfmNode) allNodes.get(end.getNodeID());
		else 
			this.end = end;
		return null;
	}

	private JsonObject getInfoJson(LastfmNode node) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&format=json";
		if (node.getMbid() != null) 
			url += "&mbid=";
		else 
			url += "&artist=";
		url += node.getNodeID();
		url += "&api_key=" + apiKey;
		return getJson(url);
	}

	private JsonObject getSimilarArtistsJson(LastfmNode node) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&format=json";
		if (node.getMbid() != null) 
			url += "&mbid=";
		else 
			url += "&artist=";
		url += node.getNodeID();
		url += "&api_key=" + apiKey;
		return getJson(url);
	}

	private JsonObject getTagsJson(LastfmNode node) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getTopTags&format=json";
		if (node.getMbid() != null) 
			url += "&mbid=";
		else 
			url += "&artist=";
		url += node.getNodeID();
		url += "&api_key=" + apiKey;
		return getJson(url);
	}

	private JsonObject getJson(String url) throws Exception {
		JsonObject simArt = null;

		long currentTime = System.currentTimeMillis();
		if (apiTimer != null) {
			while (currentTime - apiTimer < 500l) {
				currentTime = System.currentTimeMillis();
			}
		}
		apiTimer = currentTime;

		// Builds a buffered reader to interpret input received from the API
		// request
		URL lastfmGetSimilar = new URL(url);
		URLConnection lfmSim = lastfmGetSimilar.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(lfmSim.getInputStream()));

		// Reads in a string received from the API requests
		StringBuilder builder = new StringBuilder();
		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
		}
		fileAccesses++;
		System.err.println("api calls: " + fileAccesses);

		// Closes the buffered reader
		in.close();

		// Converts the string to a Json object
		JsonParser parser = new JsonParser();
		simArt = parser.parse(builder.toString()).getAsJsonObject();

		return simArt;
	}
}
