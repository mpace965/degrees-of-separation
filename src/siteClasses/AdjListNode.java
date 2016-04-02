package siteClasses;

import java.util.ArrayList;

public class AdjListNode implements Node {
	private ArrayList<Node> connections;
	private String nodeID;

	public AdjListNode(String nodeID) {
		this.nodeID = nodeID;
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
	public Integer getNodeVal() {
		return Integer.parseInt(this.nodeID);
	}
	public String toString() {
		return this.nodeID.toString();
	}
}