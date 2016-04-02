package siteClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LastfmNode implements Node {

	private ArrayList<Node> connections;
	private String nodeID;
	private JsonObject jsonOb;

	public LastfmNode(String nodeID) {
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

	// TODO: All api requests should be done directly 
	// from the site class, not the node class. 
	// The node class is supposed to be a simple Java object

}
