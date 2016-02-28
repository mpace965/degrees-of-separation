package algorithm;

import java.util.HashMap;

import siteClasses.Node;

/**
 * @author Ryan Davis
 * @date 2/28/16
 * 
 * Time Constraints:
 * getMin() = O(1)
 * removeMin() = O(logn)
 * update() = O(logn) (HashMap used to get location in O(1) amortized)
 */
public class Heap {	
	// heap implemented in an array
	private Node[] heap;

	// current max size of heap
	private int maxSize = 500;

	// size of heap, and 1 more than index of last element
	private int size = 0;	

	// Contains the current index (in the heap)
	// of the song at all times. Referenced by songName
	private HashMap<Integer, Node> locator = new HashMap<Integer, Node>();	

	public Heap() {
		// create new array with initial maxSize = 10;
		heap = new Node[maxSize];
	}

	/**
	 * @return size of array
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return true is size < 1
	 */
	public boolean isEmpty() {
		return size < 1;
	}

	/**
	 * @return minimum popularity song from heap
	 */
	public Node getMin() {
		if (size > 0) {
			return heap[0];
		}
		return null;
	}

	/**
	 * @param songName
	 * @return Song Object in O(1)
	 */
	public Node getNode(Integer nodeID) {
		return locator.get(nodeID);
	}

	/**
	 * @param songName
	 * @return true/false
	 */
	public boolean containsNode(Integer nodeID) {
		return locator.containsKey(nodeID);
	}

	/**
	 * removes minimum popularity song from heap
	 * replaces it with the last song in the heap
	 * and sifts down until heap is valid
	 * @return minimum popularity song from heap
	 * @throws min size error
	 */
	public Node removeMin() throws Exception {
		if (size < 1) {	
			return null;
		}

		// takes out minimum song
		Node min = heap[0];
		// update min's location in locator to null
		locator.put(min.getNodeID(), null);
		// decrease size
		size--;
		// get last song in heap, set position to null
		Node minSwapNode = heap[size];
		heap[size] = null;

		// insert last song into first position
		int loc = 0;
		heap[loc] = minSwapNode;
		minSwapNode.setLocation(loc);

		// sift heap until valid
		siftDown(minSwapNode);	// O(logn)

		// return minimum value that was removed
		return min;
	}

	/**
	 * Updates song in O(logn) time
	 * @param songName
	 * @param additionalPlays
	 * @param additionalLikes
	 */
	public void update(Integer nodeID, Integer distance) {
		// gets song location in O(1) time from the HashMap
		Node song = locator.get(nodeID);

		// Send to bottom of the heap (max value);
		song.setDistance(Integer.MAX_VALUE);
		siftDown(song);	// O(logn) 

		// set proper values to sift up
		song.setDistance(distance);
		siftUp(song);
	}

	/**
	 * Adds song and sifts upward as necessary in O(logn)
	 * @param song
	 * @throws max size error
	 */
	public void add(Node node) {
		// add's song and location in O(1)
		locator.put(node.getNodeID(), node);
		// add song to heap
		heap[size] = node;
		node.setLocation(size);
		// bubbleUp song until valid heap
		siftUp(node);
		// increment size
		size++;
	}

	private boolean siftUp(Node song) {
		// TODO
		// O(1) to get location in heap of song
		int location = song.getLocation();
		int loc = location;
		return true;
	}

	private boolean siftDown(Node song) {
		// TODO
		int location = song.getLocation();
		int loc = location;
		Node swap = song;

		// determines how to go about bubbling down
		// whether it be max route or min route
		boolean minRoute = loc % 2 == 0;
		return true;
	}

	private void swap(int index1, int index2) {
		// simply swaps two values in the array
		if (index1 != index2 && index1 >= 0 && index2 >= 0) {
			Node node1 = heap[index1];
			Node node2 = heap[index2];
			heap[index1] = node2;
			heap[index2] = node1;
			node1.setLocation(index2);
			node2.setLocation(index1);
		}
	}
	
	private void resize() {
		
	}

	public String toString() {
		StringBuilder build = new StringBuilder();
		for (int i = 0, round = 2, n = 1; i < this.size; i++, n++) {
			build.append(String.format("%12s /// ", heap[i].toString()));
			if (n % round == 0) {
				n = 0;
				round *= 2;
				build.append("\n");
			}
		}
		return build.toString();
	} 
}