package databaseInterfacing;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

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
	 */
	public DBInterfacer(String database, String username, String password, long maxNodes) {
		this.graph = new OrientGraph(database, username, password);
		this.currentNodes = graph.countVertices();
		this.maxNodes = maxNodes;
		this.purgePercent = 0.2;
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
	 * Adds a vertex with the supplied fields
	 * @param className	Name of class and cluster that the node will be added to
	 * @param vertexid	ID used to look up the vertex
	 * @param props		List of props that are to be set to the vertex
	 * 					must be an even number of props
	 * 					Ex: ["name", "John", "band", "Beatles"]
	 * @return ID that is associated with that vertex or 0 if failed
	 */
	public Object addVertex(String className, String[] names, Object[] values) {
		if (names.length % 2 != 0 || values.length % 2 != 0)
			return null;
		
		try {
			Vertex v = graph.addVertex(className, className);
			
			for (int i = 0; i < names.length; i++)
				v.setProperty(names[i], values[i]);
			
			currentNodes++;
			if (currentNodes >= maxNodes)
				cachePurge();
			
			return v.getId();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public Iterable<Vertex> getVerticesByFieldProps(String label, String[] names, Object[] values) {
		return graph.getVertices("Node", names, values);
	}
	
	/**
	 * Adds a connection between 2 Vertices to the graph
	 * @return 1 if success, 0 if fail
	 */
	public Object addNewConnection(String edgeName, Object id1, Object id2) {
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
	
	public void close() {
		graph.shutdown();
	}
	
	public String toString() {
		Iterable<Vertex> vertices = graph.getVertices();
		String toStr = "";
		
		for (Vertex v : vertices) {
			toStr += v.getId() + "\n";
		}
		
		return toStr;
	}
}
