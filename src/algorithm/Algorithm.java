package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import siteClasses.*;

public class Algorithm {
	public static ArrayList<Node> processConnection(Node start, Node end) {
		HashMap<Node, Integer> dist = new HashMap<Node, Integer>();
		HashMap<Node, Node> prev = new HashMap<Node, Node>();

		dist.put(start, 0);

		Node node;
		while (!dist.isEmpty()) {
			node = getMin(dist);
			if (node.equals(end))
				return flip(prev, end);

			for (Node neighbor : node.getConnections()) {
				if (dist.get(neighbor) == null || dist.get(neighbor) > dist.get(node) + 1) {
					dist.put(neighbor, dist.get(node) + 1);
					prev.put(neighbor, node);
				}
			}
			dist.remove(node);
		}

		return null;
	}

	private static Node getMin(HashMap<Node, Integer> dist) {
		Iterator<Entry<Node, Integer>> it = dist.entrySet().iterator();

		Integer min = Integer.MAX_VALUE;
		Node minNode = null;
		Entry<Node, Integer> temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getValue() < min) {
				min = temp.getValue();
				minNode = temp.getKey();
			}
			else if (temp.getValue() == min) {
				if (temp.getKey().getNodeID() < minNode.getNodeID()) {
					min = temp.getValue();
					minNode = temp.getKey();
				}
			}
		}

		return minNode;
	}

	private static ArrayList<Node> flip(HashMap<Node, Node> prev, Node end) {
		ArrayList<Node> list = new ArrayList<Node>();

		Node curr = end;
		while (curr != null) {
			list.add(curr);
			curr = prev.get(curr);
		}

		return list;
	}
}
