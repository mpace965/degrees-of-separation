package siteClasses;

public interface Site {
	
	/**
	 * @param start
	 * @param end
	 * @return heuristicCost from start node to end node
	 * @throws Exception if nodes are incompatible for the given site class.
	 */
	public abstract double heuristicCost(Node start, Node end) throws Exception;
	
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
	public abstract void setStartAndEndNodes(String start, String end);
}