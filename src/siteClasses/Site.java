package siteClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Site {
	private String filePath;
	public Site(String filePath) {
		this.filePath = filePath;
	}
	
	public void setConnections(Node source) {
		if (source.getConnections().size() > 0) 
			return;

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
						temp = new Node(id);
						source.addConnection(temp);
					}
					else if (pos == Position.SECOND) {
						Integer id = Integer.parseInt(getFirstNode(line));
						temp = new Node(id);
						source.addConnection(temp);
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
