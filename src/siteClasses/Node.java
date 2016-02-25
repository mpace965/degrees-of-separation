package siteClasses;

public interface Node {
	public boolean isConnected(Node node);
	public void getNeighbors();
	public void getJSON();
}
