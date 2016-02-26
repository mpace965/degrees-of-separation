package siteClasses;

import java.util.ArrayList;

public class Node {
	private static Site site;
	private ArrayList<Node> connections;
	private Integer nodeID;
	
	public Node(Integer nodeID) {
		this.connections = new ArrayList<Node>();
		this.nodeID = nodeID;
	}
	
	public boolean addConnection(Node node) {
		return this.connections.add(node);
	}
	public boolean addConnection(ArrayList<Node> arr) {
		return this.connections.addAll(arr);
	}
	
	public ArrayList<Node> getConnections() {
		return this.connections;
	}
	
	public Integer getNodeID() {
		return this.nodeID;
	}
	
	public String toString() {
		return nodeID.toString();
	}
}
