package databaseInterfacing;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/** Interfacer object used to interact with the database */

public class DBInterfacer {
	private OrientGraph graph;
	
	/**
	 * Constructor for Interfacer
	 * @param database	The database that you want to connect to
	 * @param username	The username you want to connect with
	 * @param password	The password you want to connect with
	 */
	public DBInterfacer(String database, String username, String password) {
		graph = new OrientGraph(database, username, password);
	}
	
	/**
	 * Adds a vertex with no initial properties to graph
	 * @return ID that is associated with that vertex
	 */
	public Object addVertex() {
		try {
			Vertex v = graph.addVertex(null);
			return v.getId();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sets the properties of a vertex v using the props list
	 * @param vertexid	ID used to look up the vertex
	 * @param props		List of props that are to be set to the vertex
	 * 					must be an even number of props
	 * 					Ex: ["name", "John", "band", "Beatles"]
	 * @return 1 if success, 0 if fail
	 */
	public int setVertexProperties(Object vertexid, String[] props) {
		if (props.length % 2 != 0)
			return 0;
		
		try {
			Vertex v = graph.getVertex(vertexid);
			
			int i = 0;
			while (i < props.length) {
				v.setProperty(props[i], props[i + 1]);
				i += 2;
			}
			return 1;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Adds a connection between 2 Vertices to the graph
	 * @return 1 if success, 0 if fail
	 */
	public Object addNewConnection(String name1, String name2) {
		try {
			Vertex v1 = graph.addVertex(null);
			v1.setProperty("name", name1);
			
			Vertex v2 = graph.addVertex(null);
			v2.setProperty("name", name2);
			
			Edge connection = graph.addEdge(null, v1, v2, "connected");
			return connection.getId();
		} 
		catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Removes all Vertices in the graph
	 * @return 1 if removed, 0 if empty
	 */
	public int removeAllConnections() {
		if (graph.countVertices() == 0)
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
}
