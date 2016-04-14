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
import siteClasses.ThesaurusNode;

/** Interfacer object used to interact with the database */

public class DBInterfacer {
	private OrientGraph graph;
	private long currentNodes, maxNodes;
	private double purgePercent;
	
	/**
	 * Constructor for Interfacer (Only use when adding & removing nodes)
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
	 * Constructor for Interfacer (Use for every case other than adding & removing nodes)
	 * @param database	The database that you want to connect to
	 * @param username	The username you want to connect with
	 * @param password	The password you want to connect with
	 */
	public DBInterfacer(String database, String username, String password) {
		try {
			this.graph = new OrientGraph(database, username, password);
			this.currentNodes = graph.countVertices();
			this.maxNodes = -1;
			this.purgePercent = 0.0;
		}
		catch (Exception e) {
			this.graph = null;
			this.currentNodes = 0;
			this.maxNodes = 0;
			this.purgePercent = 0.0;
		}
	}
	
	/**
	 * Gets the vertex by its RID
	 * @param Value	value used to look up vertex
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
	 * Gets the class name for a specific node
	 * @param n	Node to check class name
	 * @return	Class name
	 */
	public String getClassName(Node n) {
		String className = null;

		if (n instanceof AdjListNode) {
			className = "AdjListNode";
		} else if (n instanceof LastfmNode) {
			className = "LastfmNode";
		} else if (n instanceof ThesaurusNode) {
			className = "ThesaurusNode";
		}
		
		return className;
	}
	
	/**
	 * Adds a vertex for each of the supplied nodes
	 * @param nodes	List of nodes to be added
	 * @return True if success otherwise false
	 */
	public boolean addVertices(ArrayList<Node> nodes) {
		String className = getClassName(nodes.get(0));
		
		try {
			// Get current node and add to graph
			for (int i = 0; i < nodes.size(); i++) {
				Node cNode = nodes.get(i);
				
				if (getVertexByID(className, cNode.getNodeID()) != null) {
					continue;
				}
				
				Vertex v = graph.addVertex(className, className);
				v.setProperty("ID", cNode.getNodeID());
				
				// Cache management
				currentNodes++;
				if (currentNodes >= maxNodes && maxNodes != -1)
					cachePurge();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds a Connection between the 2 nodes
	 * @param n1	First node to connect
	 * @param n2	Second node to connect
	 * @return	True if success otherwise false
	 */
	public boolean addConnection(Node n1, Node n2) {
		String className = getClassName(n1);
		
		try {
			// Get the 2 vertexes to add
			Vertex v1 = getVertexByID(className, n1.getNodeID());
			Vertex v2 = getVertexByID(className, n2.getNodeID());
			
			boolean found = false;
			Iterable<Vertex> v1Connections = v1.getVertices(Direction.BOTH);
			for (Vertex vc : v1Connections) {
				if (vc.getProperty("ID").equals(v2.getProperty("ID"))) {
					found = true;
				}
			}
			
			if (!found)
				graph.addEdge(className, v1, v2, "Connection");
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Finds the shortest path between 2 nodes
	 * @param n1	Starting node
	 * @param n2	Ending node
	 * @return List of nodes
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Node> shortestPath(Node n1, Node n2) {
		try {
			String className = getClassName(n1);
			
			Vertex v1 = getVertexByID(className, n1.getNodeID());
			Vertex v2 = getVertexByID(className, n2.getNodeID());
			
			// Possibly change to support only specific edge...
			String query = "select expand(shortestPath) from "
					+ "(select shortestPath(" + v1.getId() + ", "
					+ v2.getId() + ", 'BOTH'))";
			
			ArrayList<Node> nodes = new ArrayList<Node>();
			Iterable<Vertex> result = (Iterable<Vertex>) graph.command(
					  new OSQLSynchQuery<Vertex>(query)).execute();
			
			if (n1 instanceof AdjListNode) {
				for (Vertex v : result)
					nodes.add(new AdjListNode(v.getProperty("ID").toString()));
			} else if (n1 instanceof LastfmNode) {
				for (Vertex v : result)
					nodes.add(new LastfmNode(v.getProperty("ID").toString(), null));
			} else if (n1 instanceof ThesaurusNode) {
				for (Vertex v : result)
					nodes.add(new ThesaurusNode(v.getProperty("ID").toString()));
			}
			
			if (nodes.size() == 0)
				return null;
			
			return nodes;
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Path does not exist (yet)");
			return null;
		}
	}
	
	public boolean initializeStatistics(String[] keys, String[] values) {
		Iterator<Vertex> stats = graph.getVerticesOfClass("Statistics").iterator();

		// If there are any stats then they have already been initialized
		if (stats.hasNext()) {
			return false;
		}
		
		setStatistics(keys, values);
		
		return true;
	}
	
	/**
	 * Sets the given statistics to the values specified
	 * (Each key must be gotten individually to ensure correct order)
	 * @param keys		Statistic types
	 * @param values	Values of Statistic
	 * @return	True if success otherwise false
	 */
	public boolean setStatistics(String[] keys, String[] values) {
		for (int i = 0; i < keys.length; i++) {
			try {
				Vertex v;
				String[] statStr = { "Statistic" };
				String[] keyStr = { keys[i] };
				Iterator<Vertex> stat = graph.getVertices("Statistics", statStr, keyStr).iterator();
				
				if (stat.hasNext()) {
					v = stat.next();
					v.setProperty("Statistic", keys[i]);
					v.setProperty("Value", values[i]);
				} else {
					v = graph.addVertex("Statistics", "Statistics");
					v.setProperty("Statistic", keys[i]);
					v.setProperty("Value", values[i]);
				}				
			} catch (Exception e) {
				System.err.println("Error: Could not set statistic");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the statistics by the specified keys
	 * (Each key must be gotten individually to ensure correct order)
	 * @param keys	Statistic types
	 * @return	Value of the statistics
	 */
	public String[] getStatistics(String[] keys) {
		String[] values = new String[keys.length];
		
		for (int i = 0; i < keys.length; i++) {
			try {
				String[] statStr = { "Statistic" };
				String[] keyStr = { keys[i] };
				Iterator<Vertex> stat = graph.getVertices("Statistics", statStr, keyStr).iterator();
				
				if (!stat.hasNext())
					return null;
				
				values[i] = stat.next().getProperty("Value");
			} catch (Exception e) {
				System.err.println("Error: Could not get statistic - " + keys[i]);
				return null;
			}
		}
		return values;
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
