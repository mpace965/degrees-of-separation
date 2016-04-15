package siteClasses;

import java.util.ArrayList;

public interface Node {
	
	/**
	 * Set the connections for the Node
	 * @param connections
	 */
	public abstract void setConnections(ArrayList<Node> connections);
	
	/**
	 * Always check if this value is null before operating on it
	 * @return connections if they've been populated, null otherwise
	 */
	public abstract ArrayList<Node> getConnections();
	
	/**
	 * @return String value of the nodeID
	 */
	public abstract String getNodeID();
	
	/**
	 * @return String for debugging purposes
	 */
	public abstract String toString();
	
}