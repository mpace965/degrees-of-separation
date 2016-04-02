package siteClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class AdjListSite implements Site {
	private String filePath;
	private int fileAccesses = 0;
	private HashMap<String, Node> allNodes;
	private Double heuristicConstant;
	private Node start;
	private Node end;

	public AdjListSite(String filePath) {
		this.allNodes = new HashMap<String, Node>();
		this.filePath = filePath;
	}

	public void populateConnections(Node node) {
		// parses the file and adds the connections
		if (!allNodes.containsKey(node.getNodeID())) {
			allNodes.put(node.getNodeID(), node);
		}
		try {
			FileReader fileReader = new FileReader(this.filePath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			fileAccesses++;
			
			String nodeStr = node.getNodeID();
			String line;
			boolean reachedEnd = false;
			boolean hitSection = false;
			AdjListNode temp;

			while (!reachedEnd && (line = bufferedReader.readLine()) != null) {
				Position pos = getPosition(line, nodeStr);
				if (pos != Position.NONE) {
					if (pos == Position.FIRST) {
						hitSection = true;
						String idstr = getSecondNode(line);
						if (allNodes.containsKey(idstr)) {
							node.addConnection(allNodes.get(idstr));
						}
						else {
							temp = new AdjListNode(idstr);
							node.addConnection(temp);
							allNodes.put(idstr, temp);
						}
					}
					else if (pos == Position.SECOND) {
						String idstr = getFirstNode(line);
						if (allNodes.containsKey(idstr)) {
							node.addConnection(allNodes.get(idstr));
						}
						else {
							temp = new AdjListNode(idstr);
							node.addConnection(temp);
							allNodes.put(idstr, temp);
						}
					}
				}
				else if (hitSection) {
					reachedEnd = true;
				}
			}   
			bufferedReader.close();         
		}
		catch(Exception e) {
			// file error
		}
	}
	
	public double heuristicCost(Node start, Node end){
		if (heuristicConstant == null)
			return 1d;
		
		// heuristic based off an inverse normal curve 
		// with the mean equal to the end point
		// with SD given by the heuristicConsant
		
		// the denominator of the function
		double den = heuristicConstant * Math.sqrt(2d * Math.PI);
		
		// building up the numerator
		double num = 2d * Math.pow((double) heuristicConstant, 2d);
		num = Math.pow((Integer) start.getNodeVal() - (Integer) end.getNodeVal(), 2d);
		num = 0 - num;
		num = Math.pow(Math.E, num);
		
		// return final product a number between 1 and 0
		return num / den;
	}

	/**
	 * sets the standard deviation for the inverse normal distribution
	 * @param stddev
	 */
	public void setHeuristicConstant(double stddev) {
		this.heuristicConstant = stddev;
	}
	
	public void setStartAndEndNodes(String start, String end) {
		if (allNodes.containsKey(start)) {
			this.start = allNodes.get(start);
		}
		else {
			allNodes.put(start, new AdjListNode(start));
			this.start = allNodes.get(start);
		}
		
		if (allNodes.containsKey(end)) {
			this.end = allNodes.get(end);
		}
		else {
			allNodes.put(end, new AdjListNode(end));
			this.end = allNodes.get(end);
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
	
	// helper methods to parse the file
	
	private String getFirstNode(String line) {
		line = line.trim();
		int indexOfSpace = line.indexOf(' ');
		if (indexOfSpace == -1)
			indexOfSpace = line.indexOf('\t');
		return line.substring(0, indexOfSpace);
	}

	private String getSecondNode(String line) {
		line = line.trim();
		int indexOfSpace = line.indexOf(' ');
		if (indexOfSpace == -1)
			indexOfSpace = line.indexOf('\t');
		return line.substring(indexOfSpace + 1);
	}

	private Position getPosition(String line, String str) {
		if (str.equals(getFirstNode(line)))
			return Position.FIRST;
		if (str.equals(getSecondNode(line)))
			return Position.SECOND;
		return Position.NONE;
	}

	private enum Position {
		FIRST, SECOND, NONE;
	}
}