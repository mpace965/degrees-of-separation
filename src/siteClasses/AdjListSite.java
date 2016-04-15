package siteClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AdjListSite implements Site {
	private String filePath;
	private int fileAccesses = 0;
	private HashMap<String, Node> allNodes;
	private Node start;
	private Node end;

	public AdjListSite(String filePath) {
		this.allNodes = new HashMap<String, Node>();
		this.filePath = filePath;
	}
	
	public void setFilePath (String path) {
		this.filePath = path;
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
	public HashMap<String, Node> getAllNodes() {
		return this.allNodes;
	}
	public void resetAccessCount() {
		this.fileAccesses = 0;
	}
	
	// heuristic
	
	public double heuristicCost(Node startNode){
		Node endNode = this.end;
		double heuristicConstant = 500d;

		AdjListNode start = (AdjListNode) startNode;
		AdjListNode end = (AdjListNode) endNode;

		// heuristic based off an inverse normal curve 
		// with the mean equal to the end point
		// with SD given by the heuristicConsant

		// the denominator of the function
		double den = heuristicConstant * Math.sqrt(2d * Math.PI);

		// building up the numerator
		double num = 2d * Math.pow(heuristicConstant, 2d);
		num = (Math.pow((Integer) start.getNodeVal() - (Integer) end.getNodeVal(), 2d)) / num;
		num = 0 - num;
		num = Math.pow(Math.E, num) * 1000;

		// return final product a number between 1 and 0
		return 1d - (num / den);
	}

	// file accessing methods
	
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
			ArrayList<Node> connections = new ArrayList<Node>();

			while (!reachedEnd && (line = bufferedReader.readLine()) != null) {
				Position pos = getPosition(line, nodeStr);
				if (pos != Position.NONE) {
					if (pos == Position.FIRST) {
						hitSection = true;
						String idstr = getSecondNode(line);
						if (allNodes.containsKey(idstr)) {
							connections.add(allNodes.get(idstr));
						}
						else {
							temp = new AdjListNode(idstr);
							connections.add(temp);
							allNodes.put(idstr, temp);
						}
					}
					else if (pos == Position.SECOND) {
						String idstr = getFirstNode(line);
						if (allNodes.containsKey(idstr)) {
							connections.add(allNodes.get(idstr));
						}
						else {
							temp = new AdjListNode(idstr);
							connections.add(temp);
							allNodes.put(idstr, temp);
						}
					}
				}
				else if (hitSection) {
					reachedEnd = true;
				}
			}   
			node.setConnections(connections);
			bufferedReader.close();         
		}
		catch(Exception e) {
			// file error
		}
	}

	public boolean findNode(String node) {
		// parses the file and adds the connections
		if (allNodes.containsKey(node)) {
			return true;
		}
		try {
			FileReader fileReader = new FileReader(this.filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			fileAccesses++;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				Position pos = getPosition(line, node);
				if (pos == Position.FIRST || pos == Position.SECOND) {
					bufferedReader.close(); 
					return true;
				}
			}   
			bufferedReader.close();         
		}
		catch(Exception e) {
			return false;
		}
		return false;
	}

	public void setStartAndEndNodes(String start, String end) throws Exception {
		// verify that the nodes exist in the file
		if (!findNode(start))
			throw new Exception("Start node DNE");
		if (!findNode(end))
			throw new Exception("End node DNE");
		
		// next check if nodes are in hashmap
		// map.get() returns null if key not found
		this.start = allNodes.get(start);
		this.end = allNodes.get(end);
		
		// if still null, create new nodes
		if (this.start == null) {
			this.start = new AdjListNode(start);
			this.allNodes.put(start, this.start);
		}
		if (this.end == null) {
			this.end = new AdjListNode(end);
			this.allNodes.put(end, this.end);
		}
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