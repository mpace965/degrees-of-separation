package API;

import java.util.ArrayList;

public class AdjacencyListConnectResponse {
	private int nodeCount;
	private ArrayList<AdjacencyListEdge> edgeList;
	private ArrayList<String> nodeValues;
	
	public AdjacencyListConnectResponse(){
		this.edgeList = new ArrayList<AdjacencyListEdge>();
		this.nodeValues = new ArrayList<String>();
	}
	
	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public ArrayList<AdjacencyListEdge> getEdgeList() {
		return edgeList;
	}

	public void addEdge(AdjacencyListEdge edge) {
		this.edgeList.add(edge);
	}
	
	public ArrayList<String> getNodeValues() {
		return nodeValues;
	}
	
	public void addNodeValue(String value) {
		this.nodeValues.add(value);
	}
}
