package siteClasses;

import java.util.ArrayList;

public class AdjListNode implements Node {
	private ArrayList<Node> connections;
	private String nodeID;

	public AdjListNode(String nodeID) {
		this.connections = null;
		this.nodeID = nodeID;
	}

	// Getters
	
	public ArrayList<Node> getConnections() {
		return connections;
	}
	public String getNodeID() {
		return nodeID;
	}
	public Integer getNodeVal() {
		return Integer.parseInt(nodeID);
	}

	// Setters
	
	public void setConnections(ArrayList<Node> connections) {
		this.connections = connections;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

}