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
	private String nameUrl;
	private HashMap<String, Integer> tags;
	private Integer tagTotal = 0;
	
	public LastfmNode(String mbid, String name) {
		this.connections = null;
		this.json = null;
		this.match = null;
		this.mbid = mbid;
		this.name = name;
		this.nameUrl = formatName(name);
		this.tags = null;
	}
	public LastfmNode(String mbid, String name, Double match) {
		this.connections = null;
		this.json = null;
		this.match = match;
		this.mbid = mbid;
		this.name = name;
		this.nameUrl = formatName(name);
		this.tags = null;
	}

	public ArrayList<Node> getConnections() {
		return this.connections;
	}
	public Double getMatch() {
		return this.match;
	}
	public String getMbid() {
		return this.mbid;
	}
	public String getName() {
		return this.name;
	}
	public String getNameUrl() {
		return this.nameUrl;
	}
	public String getNodeID() {
		if (this.mbid == null)
			return this.nameUrl;
		else
			return this.mbid;
	}
	public JsonObject getJson() {
		return this.json;
	}
	public HashMap<String, Integer> getTags() {
		return this.tags;
	}
	public Integer getTagTotal() {
		return this.tagTotal;
	}
	
	public boolean addConnection(Node node) {
		if (this.connections == null)
			this.connections = new ArrayList<Node>();
		return this.connections.add(node);
	}
	public void addTag(String tag, int count) {
		if (this.tags == null)
			this.tags = new HashMap<String, Integer>();
		if (this.tagTotal == null)
			this.tagTotal = 0;
		this.tagTotal += count;
		this.tags.put(tag, count);
	}
	
	public void setJson(JsonObject json) {
		this.json = json;
	}
	public void setMatch(Double match) {
		this.match = match;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String toString() {
		return this.getName();
	}
	
	private String formatName(String str) {
		str = str.trim();
		return str.replaceAll(" ", "%20");
	}
}
