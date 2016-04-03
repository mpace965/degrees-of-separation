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
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		fileAccesses++;
		LastfmNode temp = (LastfmNode) node;
		temp.setJson(makeJson(temp.getNodeID()));


		String parts[] = new String[1024];
		JsonObject z = temp.getJson();
		int i = 0;
		JsonObject similar = z.getAsJsonObject("similarartists");
		for (JsonElement x : similar.getAsJsonArray("artist")) {
			parts[i] = x.getAsJsonObject().get("name").toString();
			parts[i] = parts[i].substring(1, parts[i].length() - 1);
			System.out.println(parts[i]);
			i++;
		}

		for (int a = 0; a < parts.length; a++) {

			fileAccesses++;

			LastfmNode hold = new LastfmNode(parts[a]);
			String id = hold.getNodeID();
			hold.setJson(makeJson(id));
			if (allNodes.containsKey(id)) {
				node.addConnection(hold);
			} else {
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
