package API;

import java.util.ArrayList;

public class AdjacencyListConnectResponse {
	private int nodeCount;
	private ArrayList<AdjacencyListEdge> edgeList;
	private ArrayList<Integer> nodeValues;
	
	public AdjacencyListConnectResponse(){
		this.edgeList = new ArrayList<AdjacencyListEdge>();
		this.nodeValues = new ArrayList<Integer>();
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
	
	public ArrayList<Integer> getNodeValues() {
		return nodeValues;
	}
	
	public void addNodeValue(int value) {
		this.nodeValues.add(value);
	}
}
