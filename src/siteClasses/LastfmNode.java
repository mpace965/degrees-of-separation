package siteClasses;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonObject;

public class LastfmNode implements Node {

	private ArrayList<Node> connections;
	private JsonObject json;
	private Double match;
	private String mbid;
	private String name;
	private HashMap<String, Integer> tags;

	public LastfmNode(String name, String mbid) {
		this.connections = null;
		this.json = null;
		this.match = null;
		this.mbid = mbid;
		this.name = name;
		this.tags = null;
	}
	public LastfmNode(String name, String mbid, Double match) {
		this.connections = null;
		this.json = null;
		this.match = match;
		this.mbid = mbid;
		this.name = name;
		this.tags = null;
	}

	// Getters

	public ArrayList<Node> getConnections() {
		return connections;
	}
	public JsonObject getJson() {
		return json;
	}
	public Double getMatch() {
		return match;
	}
	public String getMbid() {
		return mbid;
	}
	public String getName() {
		return name;
	}
	public HashMap<String, Integer> getTags() {
		return tags;
	}

	// Setters

	public void setConnections(ArrayList<Node> connections) {
		this.connections = connections;
	}
	public void setJson(JsonObject json) {
		this.json = json;
	}
	public void setTags(HashMap<String, Integer> tags) {
		this.tags = tags;
	}

	public String toString() {
		return this.getName();
	}

	@Override
	public String getNodeID() {
		return LastfmNode.getID(this.name, this.mbid);
	}

	// static methods
	
	private static final String splitter = "|MBID|";
	public static String getID(String name, String mbid) {
		if (name == null || name.length() < 1)
			return null;
		name = name.trim();
		name = name.concat(LastfmNode.splitter);
		if (mbid != null) 
			return name.concat(mbid.trim());
		else 
			return name;
	}
	public static String getNamefromID(String id) {
		if (id == null || id.length() < LastfmNode.splitter.length())
			return null;
		
		int index = id.lastIndexOf(LastfmNode.splitter);
		if (index < 0)
			return null;
		
		String name = id.substring(0, index);
		return name;
	}
	public static String getMbidfromID(String id) {
		if (id == null || id.length() < LastfmNode.splitter.length())
			return null;
		
		int index = id.lastIndexOf(LastfmNode.splitter) + LastfmNode.splitter.length();
		if (index >= id.length())
			return null;
		
		String mbid = id.substring(index);
		return mbid;
	}
}
