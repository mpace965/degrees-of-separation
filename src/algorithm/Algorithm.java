package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import siteClasses.AdjListNode;
import siteClasses.AdjListSite;
import siteClasses.LastfmNode;
import siteClasses.LastfmSite;
import siteClasses.Node;
import siteClasses.Site;
import databaseInterfacing.DBInterfacer;



public class Algorithm {
	/**
	 * @param site
	 * @param start
	 * @param end
	 * @return shortest path connection between the two nodes
	 */
	public static ArrayList<Node> processConnection(Site site) {
		// keeps a reference of every nodes' fibonacciheapnode
		HashMap<Node, FibonacciHeapNode<Node>> fibNodes = 
				new HashMap<Node, FibonacciHeapNode<Node>>();

		Node start = site.getStartNode();
		Node end = site.getEndNode();
		
		// TODO: Check to make sure the SiteClass matches the proper Node class
		if (site instanceof AdjListSite) 
			if (!(start instanceof AdjListNode) || !(end instanceof AdjListNode)) 
				return null;
		else if (site instanceof LastfmSite) 
			if (!(start instanceof LastfmNode) || !(end instanceof LastfmNode)) 
				return null;
		else 
			return null;
		
		boolean flipped = false;

		if (start.getConnections() == null)
			site.populateConnections(start);
		if (end.getConnections() == null)
			site.populateConnections(end);

		if (end.getConnections().size() < start.getConnections().size()) {
			flipped = true;
			start = site.getEndNode();
			end = site.getStartNode();
		}
		// adds more optimization by checking node against
		// a set in O(1) rather than checking against a single node
		HashSet<Node> endSet = new HashSet<Node>();
		endSet.addAll(end.getConnections());

		fibNodes.put(start, new FibonacciHeapNode<Node>(start));
		fibNodes.put(end, new FibonacciHeapNode<Node>(end));

		// holds the fscore (heuristic score) of the nodes
		FibonacciHeap<Node> fscore = new FibonacciHeap<Node>();

		// holds the gscore (actual distance) of the nodes
		HashMap<Node, Integer> gscore = new HashMap<Node, Integer>();

		// holds a connection from a node to its previous node
		HashMap<Node, Node> prev = new HashMap<Node, Node>();

		// contains the used set of nodes for the algorithm
		HashSet<Node> closedSet = new HashSet<Node>();

		fscore.insert(fibNodes.get(start), site.heuristicCost(start, end));
		gscore.put(start, 0);
		prev.put(start, null);

		Node node;
		while (!fscore.isEmpty()) {
			// remove min and add to closed set
			node = fscore.removeMin().getData();
			closedSet.add(node);

			// corner case of the nodes being the same
			if (end.equals(node)) {
				ArrayList<Node> nodes = flip(prev, end, flipped);
				insertIntoDatabase(nodes);
				return nodes;
			} else if (endSet.contains(node)) {
				prev.put(end, node);
				ArrayList<Node> nodes = flip(prev, end, flipped);
				insertIntoDatabase(nodes);
				return nodes;
			}

			// check if connections are null, populate if the are
			if (node.getConnections() == null)
				site.populateConnections(node);

			// iterate over this node's neighbors
			for (Node neighbor : node.getConnections()) {
				if (closedSet.contains(neighbor))
					continue;

				// precheck neighbor against end set to bypass an extra step if found
				if (endSet.contains(neighbor)) {
					prev.put(neighbor, node);
					prev.put(end, neighbor);
					ArrayList<Node> nodes = flip(prev, end, flipped);
					insertIntoDatabase(nodes);
					return nodes;
				}
				else if (end.equals(neighbor)) {
					prev.put(end, neighbor);
					ArrayList<Node> nodes = flip(prev, end, flipped);
					insertIntoDatabase(nodes);
					return nodes;
				}

				// calculate new distance
				int gtemp = gscore.get(node) + 1;

				// if new distance is greater than old distance, no need to check it
				if (gscore.containsKey(neighbor) && gtemp >= gscore.get(neighbor)) 
					continue;

				// give this node a fibonacciheap node if it doesn't have one
				if (!fibNodes.containsKey(neighbor)) 
					fibNodes.put(neighbor, new FibonacciHeapNode<Node>(neighbor));

				// if it is already in the heap, decrease it, else just add it
				if (gscore.containsKey(neighbor))
					fscore.decreaseKey(fibNodes.get(neighbor), gtemp + site.heuristicCost(neighbor, end));
				else 
					fscore.insert(fibNodes.get(neighbor), gtemp + site.heuristicCost(neighbor, end));

				// add connection from node to previous and its new distance
				prev.put(neighbor, node);
				gscore.put(neighbor, gtemp);
			}
		}
		// will return null if no connection is found 
		// and all nodes are exhausted
		return null;
	}
	
	/**
	 * Inserts a complete list of connected nodes into the database.
	 * @param nodes
	 */
	private static void insertIntoDatabase(ArrayList<Node> nodes) {
	
		
		DBInterfacer interfacer = null;
		try {
			interfacer = new DBInterfacer("remote:localhost/Connections", "root", "team4", 40, 0.2);
		} catch (Exception e) {
			return;
		}
		
		String[] props = {"name"};
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node n1 = nodes.get(i);
			Node n2 = nodes.get(i + 1);
			
			Object[] values1 = {n1.getNodeID().toString()};
			Object[] values2 = {n2.getNodeID().toString()};
			Object node1 = interfacer.addVertex("Node", props, values1);
			Object node2 = interfacer.addVertex("Node", props, values2);
			interfacer.addNewConnection("Connection", node1, node2);
		}
		
		interfacer.close();
	
	}

	/**
	 * The algorithm will end up with a connection from the end to the beginning
	 * this will flip it around to make it in the proper connection order
	 * @param prev
	 * @param end
	 * @return in order connection of nodes
	 */
	private static ArrayList<Node> flip(HashMap<Node, Node> prev, Node end, boolean flipped) {
		ArrayList<Node> list = new ArrayList<Node>();

		Node curr = end;
		while (curr != null) {
			list.add(curr);
			curr = prev.get(curr);
		}

		if (!flipped)
			for (int i = 0; i < list.size() / 2; i++) {
				Node temp = list.get(i);
				list.set(i, list.get(list.size() - 1 - i));
				list.set(list.size() - 1 - i, temp);
			}

		return list;
	}
}
