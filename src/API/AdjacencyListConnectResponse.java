package API;

import java.util.ArrayList;

public class AdjacencyListConnectResponse {
	private int nodeCount;
	private ArrayList<AdjacencyListEdge> edgeList;
	
	public AdjacencyListConnectResponse(){
		this.edgeList = new ArrayList<AdjacencyListEdge>();
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
}
