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
		
//		boolean flipped = false;

		if (start.getConnections() == null)
			site.populateConnections(start);
		if (end.getConnections() == null)
			site.populateConnections(end);

		if (end.getConnections().size() < start.getConnections().size()) {
//			flipped = true;
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

		fscore.insert(fibNodes.get(start), site.heuristicCost(start));
		gscore.put(start, 0);
		prev.put(start, null);

		Node node;
		while (!fscore.isEmpty()) {
			// remove min and add to closed set
			node = fscore.removeMin().getData();
			closedSet.add(node);

			// corner case of the nodes being the same
			if (end.equals(node)) {
				ArrayList<Node> nodes = flip(prev, end);
				return nodes;
			} else if (endSet.contains(node)) {
				prev.put(end, node);
				ArrayList<Node> nodes = flip(prev, end);
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
					ArrayList<Node> nodes = flip(prev, end);
					return nodes;
				}
				else if (end.equals(neighbor)) {
					prev.put(end, neighbor);
					ArrayList<Node> nodes = flip(prev, end);
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
					fscore.decreaseKey(fibNodes.get(neighbor), gtemp + site.heuristicCost(neighbor));
				else 
					fscore.insert(fibNodes.get(neighbor), gtemp + site.heuristicCost(neighbor));

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
	 * @param site
	 * @return short path connection between the two nodes
	 */
	public static LastfmNode start;
	public static ArrayList<Node> processConnectionLastfmSite(LastfmSite site) {
		// keeps a reference of every nodes' fibonacciheapnode
		HashMap<Node, FibonacciHeapNode<Node>> fibNodes = 
				new HashMap<Node, FibonacciHeapNode<Node>>();

		start = (LastfmNode) site.getStartNode();
		LastfmNode end = (LastfmNode) site.getEndNode();

		// TODO: Check to make sure the SiteClass matches the proper Node class
		if (start == null || end == null) {
			return null;
		}
		//		else if (site instanceof AdjListSite) { 
		//			if (!(start instanceof AdjListNode) || !(end instanceof AdjListNode)) 
		//				return null;
		//		}
		//		else if (site instanceof LastfmSite) {
		//			if (!(start instanceof LastfmNode) || !(end instanceof LastfmNode)) 
		//				return null;
		//		}
		//		else 
		//			return null;

		if (start.getConnections() == null)
			site.populateConnections(start);
		if (end.getConnections() == null)
			site.populateConnections(end);

		// adds more optimization by checking node against
		// a set in O(1) rather than checking against a single node
		HashSet<Node> endSet = new HashSet<Node>();
		endSet.addAll(end.getConnections());

		fibNodes.put(start, new FibonacciHeapNode<Node>(start));
		fibNodes.put(end, new FibonacciHeapNode<Node>(end));

		// holds the fscore (heuristic score) of the nodes
		FibonacciHeap<Node> fscore = new FibonacciHeap<Node>();

		// holds the gscore (heuristic distance) of the nodes
		HashMap<Node, Double> openSet = new HashMap<Node, Double>();

		// holds a connection from a node to its previous node
		HashMap<Node, Node> prev = new HashMap<Node, Node>();

		// contains the used set of nodes for the algorithm
		HashSet<Node> closedSet = new HashSet<Node>();

		fscore.insert(fibNodes.get(start), 1d);
		openSet.put(start, 1d);
		prev.put(start, start.getConnections().get(start.getConnections().size() - 1));

		LastfmNode node; 
		double heuristicAdjustment, heuristicMultiplier, heur;
		while (!fscore.isEmpty()) {
			// remove min and add to closed set
			FibonacciHeapNode<Node> f = fscore.removeMin();
			node = (LastfmNode) f.getData();
			closedSet.add(node);
			System.err.println(node + " : " + f.getKey());

			// corner case of the nodes being the same
			if (end.equals(node)) {
				return flip(prev, end);
			} else if (endSet.contains(node)) {
				prev.put(end, node);
				return flip(prev, end);
			}

			// computer heuristic adjustment
			heuristicAdjustment = site.heuristicCost(node);
			heuristicMultiplier = site.heuristicMultiplier(prev.get(node), node);
			if (node.equals(start)) {
				prev.put(node, null);
			}

			// check if connections are null, populate if the are
			if (node.getConnections() == null)
				site.populateConnections(node);
			ArrayList<Node> connections = node.getConnections();
			Node neighbor;

			// iterate over this node's neighbors
			for (int i = 0; connections != null && i < connections.size(); i++) {
				neighbor = connections.get(i);
				if (closedSet.contains(neighbor))
					continue;

				// precheck neighbor against end set to bypass an extra step if found
				if (endSet.contains(neighbor)) {
					prev.put(neighbor, node);
					prev.put(end, neighbor);
					ArrayList<Node> nodes = flip(prev, end);
					return nodes;
				}
				else if (end.equals(neighbor)) {
					prev.put(end, neighbor);
					return flip(prev, end);
				}

				// calculate new distance
				LastfmNode temp = (LastfmNode) neighbor;
				heur = (1 - ((1 - temp.getMatch()) * heuristicMultiplier)) + heuristicAdjustment;
				if (heur < 0) {
					System.out.println("negative");
				}

				// if new distance is greater than old distance, no need to check it
				if (openSet.containsKey(neighbor) && heur >= openSet.get(neighbor)) 
					continue;

//				System.err.println(neighbor + " : " + heur);
				// give this node a fibonacciheap node if it doesn't have one
				if (!fibNodes.containsKey(neighbor)) 
					fibNodes.put(neighbor, new FibonacciHeapNode<Node>(neighbor));

				// if it is already in the heap, decrease it, else just add it
				if (openSet.containsKey(neighbor))
					fscore.decreaseKey(fibNodes.get(neighbor), heur);
				else 
					fscore.insert(fibNodes.get(neighbor), heur);

				// add connection from node to previous and its new distance
				prev.put(neighbor, node);
				openSet.put(neighbor, heur);
			}
		}
		// will return null if no connection is found 
		// and all nodes are exhausted
		return null;
	}

	/**
	 * The algorithm will end up with a connection from the end to the beginning
	 * this will flip it around to make it in the proper connection order
	 * @param prev
	 * @param end
	 * @return in order connection of nodes
	 */
	private static ArrayList<Node> flip(HashMap<Node, Node> prev, Node end) {
		ArrayList<Node> list = new ArrayList<Node>();

		Node curr = end;
		while (curr != null) {
			list.add(curr);
			curr = prev.get(curr);
			if (curr.equals(start)) {
				list.add(start);
				break;
			}
		}

			for (int i = 0; i < list.size() / 2; i++) {
				Node temp = list.get(i);
				list.set(i, list.get(list.size() - 1 - i));
				list.set(list.size() - 1 - i, temp);
			}

		return list;
	}
}
