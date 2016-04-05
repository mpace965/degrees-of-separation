package siteClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LastfmSite implements Site {

	private int fileAccesses = 0;
	private HashMap<String, Node> allNodes;
	private Node start;
	private Node end;
	final String apiKey = "c6c45e68f6b2a663da996fc504cf9f8b";

	public LastfmSite() {
		this.allNodes = new HashMap<String, Node>();
	}

	public double heuristicCost(Node start, Node end) {
		return 1d;
	}

	
	//Will return a json object of the similar artists to the string passed in
	private JsonObject makeJson(String a) {
		String urlStart = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&format=json";
		String artist = "&artist=" + a;
		String key = "&api_key=" + "c6c45e68f6b2a663da996fc504cf9f8b";
		String url = urlStart + artist + key;
		JsonObject simArt = null;

		// Builds a buffered reader to interpret input received from the API
		// request
		try {
			URL lastfmGetSimilar = new URL(url);
			URLConnection lfmSim = lastfmGetSimilar.openConnection();
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(
					lfmSim.getInputStream()));

			// Reads in a string received from the API requests
			StringBuilder builder = new StringBuilder();
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				builder.append(inputLine);
			}

			// Closes the buffered reader
			in.close();

			// Converts the string to a Json object
			JsonParser parser = new JsonParser();
			simArt = parser.parse(builder.toString()).getAsJsonObject();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simArt;
	}

	
	
	public void populateConnections(Node node) {
		//If the node isnt already in the hasmap then it adds it
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		//casts the node input to a lastfmNode and populates the json object
		//if it isnt already there for some reason
		fileAccesses++;
		LastfmNode temp = (LastfmNode) node;
		if (temp.getJson() == null) { 
			temp.setJson(makeJson(temp.getNodeID()));
		}


		
		//creates a string to hold the similar artists and populates that with
		//the following code below
		String parts[] = new String[1024];
		JsonObject jobject = temp.getJson();
		int i = 0;
		String connectedNames[] = new String [1024];
		JsonObject similar = jobject.getAsJsonObject("similarartists");
		for (JsonElement artist : similar.getAsJsonArray("artist")) {
			connectedNames[i] = artist.getAsJsonObject().get("name").toString();
			connectedNames[i] = connectedNames[i].substring(1, connectedNames[i].length()-1);
			i++;
		}

		//goes through the stored names obtained from above and creates a new
		//node for each connection
		for (int a = 0; a < parts.length; a++) {

			//creating a new node
			LastfmNode hold = new LastfmNode(parts[a]);
			String id = hold.getNodeID();
			//if the new node is already in the hash table then a new connection is added
			//to the node
			if (allNodes.containsKey(id)) {
				node.addConnection(hold);
			}
			//If the node is not in the hash map then the info of holding node is
			//populated and added to the hashmap
			else {
				fileAccesses++;
				hold.setJson(makeJson(id));
				node.addConnection(hold);
				allNodes.put(id, hold);
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

	public void resetAccessCount() {
		this.fileAccesses = 0;
	}

	public void setStartAndEndNodes(String start, String end) {

		if (allNodes.containsKey(start)) {
			this.start = allNodes.get(start);
		} else {

			allNodes.put(start, new LastfmNode(start));
			this.start = allNodes.get(start);
		}

		if (allNodes.containsKey(end)) {
			this.end = allNodes.get(end);
		} else {
			allNodes.put(end, new LastfmNode(end));
			this.end = allNodes.get(end);
		}

	}
}
