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
	}

	public JsonObject getJson() {
		return this.jsonOb;
	}
	
	public void setJson(JsonObject a) {
		this.jsonOb = a;
	}

	public boolean addConnection(Node node) {
		if (this.connections == null)
			this.connections = new ArrayList<Node>();
		return this.connections.add(node);
	}

	public ArrayList<Node> getConnections() {
		return this.connections;
	}

	public String getNodeID() {
		return this.nodeID;
	}

	public Object getNodeVal() {
		return this.getJson();
	}

}
