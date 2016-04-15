package siteClasses;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class ThesaurusNode implements Node {

	private ArrayList<Node> connections;
	private String nodeID;
	private JsonObject jsonOb;

	public ThesaurusNode(String nodeID) {
		this.nodeID = nodeID;
		this.jsonOb = null;
		this.connections = null;
	}

	// Getters
	
	public ArrayList<Node> getConnections() {
		return connections;
	}
	public String getNodeID() {
		return nodeID;
	}
	public JsonObject getJsonOb() {
		return jsonOb;
	}
	
	// Setters
	
	public void setConnections(ArrayList<Node> connections) {
		this.connections = connections;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	public void setJsonOb(JsonObject jsonOb) {
		this.jsonOb = jsonOb;
	}
	
	public String toString() {
		return this.nodeID;
	}

}
