package siteClasses;

import java.util.HashMap;

public interface Site {
	
	/**
	 * @param node
	 * @return heuristicCost from node to end node in siteclass
	 */
	public abstract double heuristicCost(Node node);
	
	/**
	 * populates the connections list of this node
	 * @param node
	 */
	public abstract void populateConnections(Node node);
	
	/**
	 * @return count of accesses to file/API/etc. for one iteration of the algorithm
	 */
	public abstract int getAccessCount();
	
	public abstract Node getStartNode();
	public abstract Node getEndNode();
	public abstract String setStartAndEndNodes(String start, String end);
	
	public abstract HashMap<String, Node> getAllNodes();
}