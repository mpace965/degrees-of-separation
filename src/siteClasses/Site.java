package siteClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Site {
	private String filePath;
	private HashMap<Integer, Node> allNodes;
	private Node start;
	private Node end;

	public Site(String filePath) {
		this.filePath = filePath;
		this.allNodes = new HashMap<Integer, Node>();
	}

	public void setConnections(Node source) {
		if (!allNodes.containsKey(source.getNodeID())) {
			allNodes.put(source.getNodeID(), source);
		}
		try {
			FileReader fileReader = new FileReader(this.filePath);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String nodeStr = source.getNodeID().toString();
			String line;
			boolean reachedEnd = false;
			boolean hitSection = false;
			Node temp;

			while (!reachedEnd && (line = bufferedReader.readLine()) != null) {
				Position pos = getPosition(line, nodeStr);
				if (pos != Position.NONE) {
					if (pos == Position.FIRST) {
						hitSection = true;
						Integer id = Integer.parseInt(getSecondNode(line));
						if (allNodes.containsKey(id)) {
							source.addConnection(allNodes.get(id));
						}
						else {
							temp = new Node(id);
							source.addConnection(temp);
							allNodes.put(id, temp);
						}
					}
					else if (pos == Position.SECOND) {
						Integer id = Integer.parseInt(getFirstNode(line));
						if (allNodes.containsKey(id)) {
							source.addConnection(allNodes.get(id));
						}
						else {
							temp = new Node(id);
							source.addConnection(temp);
							allNodes.put(id, temp);
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

	public void setStart(Integer id) {
		if (allNodes.containsKey(id)) {
			this.start = allNodes.get(id);
			return;
		}
		else {
			// TODO search through file and check that node exists
			this.start = new Node(id);
			allNodes.put(id, this.start);
		}
	}
	public void setEnd(Integer id) {
		if (allNodes.containsKey(id)) {
			this.end = allNodes.get(id);
			return;
		}
		else {
			this.end = new Node(id);
			allNodes.put(id, this.end);
		}
	}
	
	public Node getStart() {
		return this.start;
	}
	public Node getEnd() {
		return this.end;
	}

	private String getFirstNode(String line) {
		line = line.trim();
		int indexOfSpace = line.indexOf(' ');
		return line.substring(0, indexOfSpace);
	}

	private String getSecondNode(String line) {
		line = line.trim();
		int indexOfSpace = line.indexOf(' ');
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
