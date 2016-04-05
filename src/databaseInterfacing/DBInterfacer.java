package databaseInterfacing;

import java.util.ArrayList;
import java.util.Iterator;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
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
	 * @param value	value used to look up vertex
	 * @return Vertex with value attribute
	 */
	public Vertex getVertexByID(String className, String value) {
		String[] ids = new String[] {"ID"};
		Object[] values = new Object[] {value};
		Iterable<Vertex> vertices = graph.getVertices(className, ids, values);

		if (!vertices.iterator().hasNext())
			return null;
		
		return vertices.iterator().next();
	}
	
	/**
	 * Adds a vertex for each of the supplied nodes
	 * @param nodes				List of nodes to be added
	 * @param recentConnection	True if recent connection otherwise false
	 * @return True if success otherwise false
	 */
	public boolean addVertices(ArrayList<Node> nodes, boolean recentConnection) {
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
				
				if (getVertexByID(className, cNode.getNodeID()) != null) {
					continue;
				}
				
				Vertex v;
				if (recentConnection) {
					v = graph.addVertex("RecentNode", "RecentNode");
					v.setProperty("ID", cNode.getNodeID());
					v.setProperty("NodeType", className);
				} else {
					v = graph.addVertex(className, className);
					v.setProperty("ID", cNode.getNodeID());
				}
				
				// Cache management
				currentNodes++;
				if (currentNodes >= maxNodes)
					cachePurge();
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds a Connection between the 2 nodes
	 * @param n1				First node to connect
	 * @param n2				Second node to connect
	 * @param recentConnection	True if recent connection otherwise false
	 * @return	True if success otherwise false
	 */
	public boolean addConnection(Node n1, Node n2, boolean recentConnection) {
		String id = null;
		String connection = null;
		
		
		// Removed class!!!!! check if errors in adding
		if (recentConnection) {
			id = "RecentNode";
			connection = "RecentConnection";
		} else if (n1 instanceof AdjListNode) {
			id = "AdjListNode";
			connection = "AdjListConnection";
		} else if (n1 instanceof LastfmNode) {
			id = "LastfmNode";
			connection = "LastfmConnection";
		}
		
		try {
			// Get the 2 vertexes to add
			Vertex v1 = getVertexByID(id, n1.getNodeID());
			Vertex v2 = getVertexByID(id, n2.getNodeID());
			
			boolean found = false;
			Iterable<Vertex> v1Connections = v1.getVertices(Direction.BOTH);
			for (Vertex vc : v1Connections) {
				if (vc.getProperty("ID").equals(v2.getProperty("ID"))) {
					found = true;
				}
			}
			
			if (!found)
				graph.addEdge(id, v1, v2, connection);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param n1				Starting node
	 * @param n2				Ending node
	 * @param recentConnection	True if recent connection otherwise false
	 * @return List of nodes
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Node> shortestPath(Node n1, Node n2, boolean recentConnection) {
		try {
			String className = null;
			
			if (recentConnection) {
				className = "RecentNode";
			} else if (n1 instanceof AdjListNode) {
				className = "AdjListNode";
			} else if (n1 instanceof LastfmNode) {
				className = "LastfmNode";
			}
			
			Vertex v1 = getVertexByID(className, n1.getNodeID());
			Vertex v2 = getVertexByID(className, n2.getNodeID());
			
			String query = "select expand(shortestPath) from "
					+ "(select shortestPath(" + v1.getId()
					+ ", " + v2.getId() + ", 'BOTH', '" 
					+ className + "'))";
			
			ArrayList<Node> nodes = new ArrayList<Node>();
			Iterable<Vertex> result = (Iterable<Vertex>) graph.command(
					  new OSQLSynchQuery<Vertex>(query)).execute();
			
			if (n1 instanceof AdjListNode) {
				for (Vertex v : result)
					nodes.add(new AdjListNode(v.getProperty("ID")));
			} else if (n1 instanceof LastfmNode) {
				for (Vertex v : result)
					nodes.add(new LastfmNode(v.getProperty("ID")));
			}
			
			if (nodes.size() == 0)
				return null;
			
			return nodes;
		} catch (Exception e) {
			System.err.println("Path does not exist (yet)");
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
