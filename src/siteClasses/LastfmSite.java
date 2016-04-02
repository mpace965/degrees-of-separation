package siteClasses;

import java.util.HashMap;

public class LastfmSite implements Site {

	private int fileAccesses = 0;
	private HashMap<String, Node> allNodes;
	private Double heuristicConstant;
	private Node start;
	private Node end;
	final String apiKey = "c6c45e68f6b2a663da996fc504cf9f8b";

	
	public LastfmSite(Double heuristicConstant) {
		this.allNodes = new HashMap<String, Node>();
		this.heuristicConstant = heuristicConstant;
	}
	
	
	public double heuristicCost(Node start, Node end) {
		if (heuristicConstant == null)
			return 1d;
		
		// heuristic based off an inverse normal curve 
		// with the mean equal to the end point
		// with SD given by the heuristicConsant
		
		// the denominator of the function
		double den = heuristicConstant * Math.sqrt(2d * Math.PI);
		
		// building up the numerator
		double num = 2d * Math.pow((double) heuristicConstant, 2d);
		num = Math.pow(Integer.parseInt(start.getNodeID()) - Integer.parseInt(end.getNodeID()), 2d);
		num = 0 - num;
		num = Math.pow(Math.E, num);
		
		// return final product a number between 1 and 0
		return num / den;
	}

	public void populateConnections(Node node) {
		// TODO Auto-generated method stub
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}

		fileAccesses++;
		LastfmNode temp = new LastfmNode(node.getNodeID());

		String[] parts = temp.getJson().toString().split(",");

		for (int a = 0; a < parts.length; a++) {
			if (parts[a].contains("name")) {
				if (a == 0) {
					fileAccesses++;
					LastfmNode hold = new LastfmNode(parts[a].substring(38,
							parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
				} else {
					fileAccesses++;
					LastfmNode hold = new LastfmNode(parts[a].substring(9,
							parts[a].length() - 1));
					String id = hold.getNodeID();
					if (allNodes.containsKey(id)) {
						node.addConnection(hold);
					} else {
						node.addConnection(hold);
						allNodes.put(id, temp);
					}
				}
			}
		}
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
