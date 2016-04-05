package API;

import java.util.ArrayList;

public abstract class ConnectResponse<T> {
	private int nodeCount;
	private ArrayList<Edge> edgeList;
	private ArrayList<T> nodeValues;
	
	public ConnectResponse(){
		this.edgeList = new ArrayList<Edge>();
		this.nodeValues = new ArrayList<T>();
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public ArrayList<Edge> getEdgeList() {
		return edgeList;
	}

	public void addEdge(Edge edge) {
		this.edgeList.add(edge);
	}

	public ArrayList<T> getNodeValues() {
		return nodeValues;
	}

	public void addNodeValue(T value) {
		this.nodeValues.add(value);
	}
	
	
}
