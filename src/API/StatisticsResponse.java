package API;

public class StatisticsResponse {
	private int totalConnectionChains;
	private int totalConnections;
	private int totalDBNodes;
	private double averageChainLength;
	private int longestChainLength;
	private int shortestChainLength;
	private int totalChainLength;
	private double averageComputationTime;
	private double longestComputationTime;
	private double shortestComputationTime;
	private double totalComputationTime;
	
	public int getTotalConnectionChains() {
		return totalConnectionChains;
	}
	public void setTotalConnectionChains(int totalConnectionChains) {
		this.totalConnectionChains = totalConnectionChains;
	}
	public int getTotalConnections() {
		return totalConnections;
	}
	public void setTotalConnections(int totalConnections) {
		this.totalConnections = totalConnections;
	}
	public int getTotalDBNodes() {
		return totalDBNodes;
	}
	public void setTotalDBNodes(int totalDBNodes) {
		this.totalDBNodes = totalDBNodes;
	}
	public double getAverageChainLength() {
		return averageChainLength;
	}
	public void setAverageChainLength(double averageChainLength) {
		this.averageChainLength = averageChainLength;
	}
	public int getLongestChainLength() {
		return longestChainLength;
	}
	public void setLongestChainLength(int longestChainLength) {
		this.longestChainLength = longestChainLength;
	}
	public int getShortestChainLength() {
		return shortestChainLength;
	}
	public void setShortestChainLength(int shortestChainLength) {
		this.shortestChainLength = shortestChainLength;
	}
	public int getTotalChainLength() {
		return totalChainLength;
	}
	public void setTotalChainLength(int totalChainLength) {
		this.totalChainLength = totalChainLength;
	}
	public double getAverageComputationTime() {
		return averageComputationTime;
	}
	public void setAverageComputationTime(double averageComputationTime) {
		this.averageComputationTime = averageComputationTime;
	}
	public double getLongestComputationTime() {
		return longestComputationTime;
	}
	public void setLongestComputationTime(double longestComputationTime) {
		this.longestComputationTime = longestComputationTime;
	}
	public double getShortestComputationTime() {
		return shortestComputationTime;
	}
	public void setShortestComputationTime(double shortestComputationTime) {
		this.shortestComputationTime = shortestComputationTime;
	}
	public double getTotalComputationTime() {
		return totalComputationTime;
	}
	public void setTotalComputationTime(double totalComputationTime) {
		this.totalComputationTime = totalComputationTime;
	}
}
