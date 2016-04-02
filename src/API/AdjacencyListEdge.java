package API;

public class AdjacencyListEdge {
	private int source;
	private int target;
	
	public AdjacencyListEdge(){
	}
	
	public AdjacencyListEdge(int source, int target) {
		this.setSource(source);
		this.setTarget(target);
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}
}