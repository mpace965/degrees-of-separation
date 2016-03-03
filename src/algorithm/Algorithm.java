package algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import siteClasses.*;
import org.jgrapht.util.*;

public class Algorithm {
	public static ArrayList<Node> processConnection(Site site) {
		FibonacciHeapNode<Node> start = site.getStart().getFibNode();
		FibonacciHeapNode<Node> end = site.getEnd().getFibNode();

		FibonacciHeap<Node> heap = new FibonacciHeap<Node>();
		HashMap<Node, Node> prev = new HashMap<Node, Node>();

		heap.insert(start, 0);
		prev.put(start.getData(), null);

		FibonacciHeapNode<Node> node;
		while (!heap.isEmpty()) {
			node = heap.removeMin();
			for (Node neighbor : node.getData().getConnections()) {
				if (neighbor.equals(end.getData())) {
					prev.put(end.getData(), node.getData());
					return flip(prev, end.getData());
				}
				else if (!prev.containsKey(neighbor)) {
					heap.insert(neighbor.getFibNode(), node.getKey() + 1);
					prev.put(neighbor, node.getData());
				}
				else {
					if (neighbor.getFibNode().getKey() > node.getKey() + 1) {
						heap.decreaseKey(neighbor.getFibNode(), node.getKey() + 1);
						prev.put(neighbor, node.getData());
					}
				}
			}
		}

		return null;
	}

	private static ArrayList<Node> flip(HashMap<Node, Node> prev, Node end) {
		ArrayList<Node> list = new ArrayList<Node>();

		Node curr = end;
		while (curr != null) {
			list.add(curr);
			curr = prev.get(curr);
		}
		
		for (int i = 0; i < list.size() / 2; i++) {
			Node temp = list.get(i);
			list.set(i, list.get(list.size() - 1 - i));
			list.set(list.size() - 1 - i, temp);
		}

		return list;
	}
}
