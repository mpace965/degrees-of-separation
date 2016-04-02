package siteClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

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
	
	private JsonObject makeJson(String a) {
		String urlStart = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&format=json";
		String artist = "&artist=" + a;
		String key = "&api_key=" + "c6c45e68f6b2a663da996fc504cf9f8b";
		String url = urlStart + artist + key;
		JsonObject simArt = null;
		
		// Builds a buffered reader to interpret input received from the API request
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
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		fileAccesses++;
		LastfmNode temp = new LastfmNode(node.getNodeID());
		temp.setJson(makeJson(temp.getNodeID()));
		

		// TODO: why split the JSON object when you can use
		// Json.get(String memeberName) to get it's elements?
		// String manipulation is generally not good practice
		// if there are other was to do it.
		String[] parts = temp.getJson().toString().split(",");

		for (int a = 0; a < parts.length; a++) {
				if (a == 0) {
					fileAccesses++;
					// TODO: again string manipulation is 
					// generally not good practice
					
					temp.getJson().get("name");
					LastfmNode hold = new LastfmNode(parts[a].substring(38, parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} 
					else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
				} else {
					fileAccesses++;
					// TODO: again string manipulation is 
					// generally not good practice
					LastfmNode hold = new LastfmNode(parts[a].substring(9, parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} 
					else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
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
		}
		else {

			allNodes.put(start, new LastfmNode(start));
			this.start = allNodes.get(start);
		}

		if (allNodes.containsKey(end)) {
			this.end = allNodes.get(end);
		}
		else {
			allNodes.put(end, new LastfmNode(end));
			this.end = allNodes.get(end);
		}

	}
}
