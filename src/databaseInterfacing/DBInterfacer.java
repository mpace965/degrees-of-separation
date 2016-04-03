package databaseInterfacing;

import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import siteClasses.AdjListNode;
import siteClasses.LastfmNode;
import siteClasses.Node;

/** Interfacer object used to interact with the database */

public class DBInterfacer {
	private OrientGraph graph;
	private long currentNodes, maxNodes;
	private double purgePercent;
	
	/**
	 * Constructor for Interfacer
	 * @param database	The database that you want to connect to
	 * @param username	The username you want to connect with
	 * @param password	The password you want to connect with
	 * @param maxNodes	Maximum number of nodes until there is a cache purge
	 * @param purgeP	Percentage of the cache that is purged
	 */
	public DBInterfacer(String database, String username, String password, long maxNodes, double purgeP) {
		try {
			this.graph = new OrientGraph(database, username, password);
			this.currentNodes = graph.countVertices();
			this.maxNodes = maxNodes;
			this.purgePercent = purgeP;
		}
		catch (Exception e) {
			this.graph = null;
			this.currentNodes = 0;
			this.maxNodes = 0;
			this.purgePercent = 0.0;
		}
	}
	
	/**
	 * Adds a vertex with no fields
	 * @param className	Name of class and cluster that the node will be added to
	 * @return ID that is associated with that vertex or 0 if failed
	 */
	public Object addVertex(String className) {
		try {
			Vertex v = graph.addVertex(className, className);
			
			currentNodes++;
			if (currentNodes >= maxNodes)
				cachePurge();
			
			return v.getId();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Adds a vertex for each of the supplied nodes
	 * @param nodes	List of nodes to be added
	 * @return List of RIDs that are associated with the new nodes
	 */
	public ArrayList<Object> addVertices(ArrayList<Node> nodes) {
		ArrayList<Object> RIDs = new ArrayList<Object>();
		String className = null;
		
		if (nodes.get(0) instanceof AdjListNode) {
			className = "AdjListNode";
		} else if (nodes.get(0) instanceof LastfmNode) {
			className = "LastfmNode";
		}
		
		try {
			for (int i = 0; i < nodes.size(); i++) {
				// Get current node and add to graph
				Node cNode = nodes.get(i);
				Vertex v = graph.addVertex(className, className);
				
				// Format the date and time
				//DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				//Date date = new Date();
				//System.out.println(df.format(date));
				
				// Set the properties
				v.setProperty("ID", cNode.getNodeID());
				//v.setProperty("Date", date);
				
				// Cache management
				currentNodes++;
				if (currentNodes >= maxNodes)
					cachePurge();
				
				// Return the RID of the new node
				RIDs.add(v.getId());
			}
		} catch (Exception e) {
			return null;
		}
		
		return RIDs;
	}
	
	/**
	 * Gets the neighbors that are connected to a given vertex
	 * @param id	Id of the vertex
	 * @return List of neighbors
	 */
	public Iterable<Vertex> getConnectedNeighbors(Object id) {
		return graph.getVertex(id).getVertices(Direction.BOTH);
	}
	
	/**
	 * @param nodes	List of nodes to connect
	 * @return true if success otherwise false
	 */
	public boolean addConnections(ArrayList<Node> nodes) {
		String id = null;
		
		if (nodes.get(0) instanceof AdjListNode) {
			id = "class: AdjListNode";
		} else if (nodes.get(0) instanceof LastfmNode) {
			id = "class: LastfmNode";
		}
		
		try {
			for (int i = 0; i < nodes.size() - 1; i++) {
				// Get the 2 consecutive vertexes to add
				Vertex v1 = graph.getVertices("ID",
						nodes.get(i).getNodeID()).iterator().next();
				Vertex v2 = graph.getVertices("ID",
						nodes.get(i + 1).getNodeID()).iterator().next();
				
				graph.addEdge(id, v1, v2, "Connection");
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds a connection between 2 Vertices to the graph
	 * @return ID of new connection
	 */
	public Object connect(String edgeName, Object id1, Object id2) {
		try {
			Vertex v1 = graph.getVertex(id1);
			Vertex v2 = graph.getVertex(id2);
			
			Edge connection = graph.addEdge(edgeName, v1, v2, "connection");
			return connection.getId();
		}
		catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Purges the oldest percent of the cache
	 */
	public void cachePurge() {
		long purgeAmount = (long) (currentNodes * purgePercent);
		
		Iterator<Vertex> vIter = graph.getVertices().iterator();
		
		while (purgeAmount > 0) {
			graph.removeVertex((Vertex) vIter.next());
			purgeAmount--;
		}
	}
	
	/**
	 * Removes all Vertices in the graph
	 * @return 1 if removed, 0 if empty
	 */
	public int removeAllConnections() {
		
		if (currentNodes == 0)
			return 0;
		
		Iterable<Vertex> vertices = graph.getVertices();
		
		for (Vertex v : vertices) {
			graph.removeVertex(v);
		}
		
		return 1;
	}
	
	/**
	 * Shutdown the server
	 */
	public void close() {
		graph.shutdown();
	}
	
	/**
	 * Prints the IDs of the vertices
	 */
	public String toString() {
		Iterable<Vertex> vertices = graph.getVertices();
		String toStr = "";
		
		for (Vertex v : vertices) {
			toStr += v.getId() + "\n";
		}
		
		return toStr;
	}
}
