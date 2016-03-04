package siteClasses;

public interface Site {
	
	/**
	 * adds a node to this site's set of nodes
	 * @param node
	 */
	public abstract void addNode(Node node);
	
	/**
	 * @param node
	 * @return estimated heuristic cost between start and end nodes
	 */
	public abstract double heuristicCost(Node start, Node end);
	
	/**
	 * populates the connections list of this node
	 * @param node
	 */
	public abstract void populateConnections(Node node);
	
	/**
	 * @return count of accesses to file/API/etc. for one iteration of the algorithm
	 */
	public abstract int getAccessCount();
}
