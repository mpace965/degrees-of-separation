package siteClasses;

import java.util.HashMap;

public class LastfmSite implements Site {

	private int fileAccesses = 0;
	private HashMap<String, Node> allNodes;
	private Node start;
	private Node end;
	final String apiKey = "c6c45e68f6b2a663da996fc504cf9f8b";

	public LastfmSite() {
		this.allNodes = new HashMap<String, Node>();
	}

	public double heuristicCost(Node start, Node end) {
		return 1d;
	}

	public void populateConnections(Node node) {
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		fileAccesses++;
		LastfmNode temp = new LastfmNode(node.getNodeID());

		// TODO: why split the JSON object when you can use
		// Json.get(String memeberName) to get it's elements?
		// String manipulation is generally not good practice
		// if there are other was to do it.
		String[] parts = temp.getJson().toString().split(",");

		for (int a = 0; a < parts.length; a++) {
			if (parts[a].contains("name")) {
				if (a == 0) {
					fileAccesses++;
					// TODO: again string manipulation is 
					// generally not good practice
					LastfmNode hold = new LastfmNode(parts[a].substring(38, parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} 
					else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
				} else {
					fileAccesses++;
					// TODO: again string manipulation is 
					// generally not good practice
					LastfmNode hold = new LastfmNode(parts[a].substring(9, parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} 
					else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
				}
			}
		}
	}
	
	public HashMap<String, Node> getAllNodes() {
		return allNodes;
	}

	public Node getStartNode() {
		return this.start;
	}

	public Node getEndNode() {
		return this.end;
	}

	public int getAccessCount() {
		return this.fileAccesses;
	}

	public void resetAccessCount() {
		this.fileAccesses = 0;
	}

	public void setStartAndEndNodes(String start, String end) {


		if (allNodes.containsKey(start)) {
			this.start = allNodes.get(start);
		}
		else {

			allNodes.put(start, new LastfmNode(start));
			this.start = allNodes.get(start);
		}

		if (allNodes.containsKey(end)) {
			this.end = allNodes.get(end);
		}
		else {
			allNodes.put(end, new LastfmNode(end));
			this.end = allNodes.get(end);
		}

		// TODO Auto-generated method stub

	}
}
