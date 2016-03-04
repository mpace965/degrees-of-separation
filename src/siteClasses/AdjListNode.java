package siteClasses;

import java.util.ArrayList;

public class AdjListNode implements Node {
	private ArrayList<Node> connections;
	private Integer nodeID;

	public AdjListNode(Integer nodeID) {
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
	public Integer getNodeID() {
		return this.nodeID;
	}
	public Integer getNodeValue() {
		return getNodeID();
	}
	public String toString() {
		return nodeID.toString();
	}
}
