import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import siteClasses.AdjListNode;
import siteClasses.AdjListSite;
import siteClasses.LastfmNode;
import siteClasses.LastfmSite;
import siteClasses.Node;
import siteClasses.Site;
import siteClasses.ThesaurusNode;
import API.AdjacencyListConnectResponse;
import API.Edge;
import API.LastfmArtist;
import API.LastfmConnectResponse;
import algorithm.Algorithm;

import com.google.gson.Gson;

import databaseInterfacing.DBInterfacer;
import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {

	public static final String MIME_JSON = "application/json";
	
	public static ArrayList<ArrayList<Node>> recentConnections;
	public static int maxRecentConnections;
	
	public static String database, username, password;
	public static int maxDBNodes;
	public static double cachePurgePrecent;
	
	// Note: if statisticKeys is modified, be sure to modify initalizeStatistics and updateStatistics
	public static HashMap<String, Object> statisticMap;
	public static String[] statisticKeys = { "TotalConnectionChains", "TotalConnections", "TotalDBNodes", 
			"AverageChainLength", "LongestChainLength", "ShortestChainLength", "TotalChainLength", 
			"AverageComputationTime", "LongestComputationTime", "ShortestComputationTime", "TotalComputationTime" };

	public WebApp() throws IOException {
		super("localhost", 8000, new File("client/"), false);

		recentConnections = new ArrayList<ArrayList<Node>>();
		maxRecentConnections = 50;
		
		database = "remote:localhost/Connections";
		username = "root";
		password = "team4";
		maxDBNodes = 10000000;
		cachePurgePrecent = 0.2;
		
		initalizeStatistics();
	}

	public static void main(String[] args) {
		try {
			ServerRunner.executeInstance(new WebApp());
		} catch (IOException ioe) {
			System.err.println("Couldn't start server:\n" + ioe);
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		if (session.getHeaders().get("accept").contains(MIME_JSON)) {
			return handleAPIRequest(session);
		} else {
			return super.serve(session);
		}
	}

	private Response handleAPIRequest(IHTTPSession session) {
		String uri = session.getUri();
		Gson gson = new Gson();
		Response r = null;

		switch (uri) {
			case "/api/connectAdjacency": {
				String fileSeparator = System.getProperty("file.separator");
				AdjListSite adjListSite = new AdjListSite("docs" + fileSeparator + "facebook_combined.txt");
				r = connectAdjacency(session, gson, adjListSite);
				break;
			}
			
			case "/api/connectLastfm": {
				LastfmSite lastfmSite = new LastfmSite();
				r = connectLastfm(session, gson, lastfmSite);
				break;
			}
			
			case "/api/getStatistics": {
				r = getStatistics(session, gson);
				break;
			}
			
			case "/api/recentConnections": {
				r = getRecentConnections(session, gson);
				break;
			}
			
			default: {
				r = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_JSON, "{}");
			}
		}

		return r;
	}
	
	private void initalizeStatistics() {
		// Initialize statistics in db to ensure they are present and correct
		Object[] statisticInitialVals = { 0, 0, 0, 0.00, 0, 0, 0, 0.00, 0.00, 0.00, 0.00 };
		Object[] statVals = null;
		try {
			DBInterfacer db = new DBInterfacer(database, username, password);
			statVals = db.initializeStatistics(statisticKeys, statisticInitialVals);
			db.close();
			
			for (int i = 0; i < statisticKeys.length; i++) {
				statisticMap.put(statisticKeys[i], statVals[i]);
			}
			
		} catch (Exception e) {
			System.err.println("Error: Could not initialize statistics in db");
		}
	}
	
	private void updateStatistics(ArrayList<Node> nodes, ArrayList<Node> allNodes, Long algTime) {
		// Get all current statistics		
		Integer totalConnectionChains = (Integer)statisticMap.get("TotalConnectionChains");
		//Integer totalConnections = (Integer)statisticMap.get("TotalConnections");
		Integer totalDBNodes = (Integer)statisticMap.get("TotalDBNodes");
		Integer longestChainLength = (Integer)statisticMap.get("LongestChainLength");
		Integer shortestChainLength = (Integer)statisticMap.get("ShortestChainLength");
		Integer totalChainLength = (Integer)statisticMap.get("TotalChainLength");
		Long longestComputationTime = (Long)statisticMap.get("LongestComputationTime");
		Long shortestComputationTime = (Long)statisticMap.get("ShortestComputationTime");
		Long totalComputationTime = (Long)statisticMap.get("TotalComputationTime");
		
		// Update all statistics
		totalConnectionChains++;
		totalDBNodes += allNodes.size();
		if (nodes.size() < shortestChainLength || shortestChainLength == 0)
			shortestChainLength = nodes.size();
		if (nodes.size() > longestChainLength || longestChainLength == 0)
			longestChainLength = nodes.size();
		totalChainLength += nodes.size();
		if (algTime < shortestComputationTime || shortestComputationTime == 0)
			shortestComputationTime = algTime;
		if (algTime > longestComputationTime || longestComputationTime == 0)
			longestComputationTime = algTime;
		totalComputationTime += algTime;
		
		// Set all new statistics
		statisticMap.put("TotalConnectionChains", totalConnectionChains);
		statisticMap.put("TotalDBNodes", totalDBNodes);
		statisticMap.put("AverageChainLength", (double)totalChainLength / (double)totalConnectionChains);
		statisticMap.put("LongestChainLength", longestChainLength);
		statisticMap.put("ShortestChainLength", shortestChainLength);
		statisticMap.put("TotalChainLength", totalChainLength);
	}
	
	private ArrayList<Node> checkRecentConnections(Site site) {
		Node n1 = site.getStartNode();
		Node n2 = site.getEndNode();

		for (ArrayList<Node> subRecent : recentConnections) {
			String headID = subRecent.get(0).getNodeID();
			String tailID = subRecent.get(subRecent.size() - 1).getNodeID();
			String n1ID = n1.getNodeID();
			String n2ID = n2.getNodeID();

			// Check if same class
			if (n1.getClass().isInstance(subRecent.get(0))) {
				if (n1ID.equals(headID) && n2ID.equals(tailID)) {
					return subRecent;
				} else if (n1ID.equals(tailID) && n2ID.equals(headID)) {
					ArrayList<Node> reversedList = new ArrayList<Node>(subRecent);
					Collections.reverse(reversedList);
					return reversedList;
				}
			}
		}

		return null;
	}

	private void addRecentConnection(ArrayList<Node> nodes) {
		recentConnections.add(nodes);

		if (recentConnections.size() > maxRecentConnections) {
			int removeAmount = (int) (maxRecentConnections * .2);
			for (int i = 0; i < removeAmount; i++) {
				recentConnections.remove(i);
			}
		}
	}

	private ArrayList<Node> checkDB(Site site) {
		try {
			ArrayList<Node> nodes;
			Node n1 = site.getStartNode();
			Node n2 = site.getEndNode();
	
			DBInterfacer db = new DBInterfacer(database, username, password);
			nodes = db.shortestPath(n1, n2);
			db.close();
	
			return nodes;
		} catch (Exception e) {
			return null;
		}
	}

	private Response connectLastfm(IHTTPSession session, Gson gson, LastfmSite lastfmSite) {
		LastfmConnectResponse c = new LastfmConnectResponse();
		Map<String, String> parms = session.getParms();
		String beginString = parms.get("begin");
		String endString = parms.get("end");
		ArrayList<Node> nodes = null;
		ArrayList<Node> allNodes = null;
		Long algTime1, algTime2, algTimeDiff = null;

		lastfmSite.setStartAndEndNodes(beginString, endString);
		
		// Check recents
		nodes = checkRecentConnections(lastfmSite);

		if (nodes == null) {
			// Check DB for connection before algorithm
			nodes = checkDB(lastfmSite);
			
			if (nodes == null) {
				algTime1 = System.currentTimeMillis();
				nodes = Algorithm.processConnectionLastfmSite(lastfmSite);
				algTime2 = System.currentTimeMillis();
				algTimeDiff = algTime2 - algTime1;
				
				if (nodes == null || nodes.size() == 0) {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "No match was found from these inputs.");
				}
				
				allNodes = new ArrayList<Node>(lastfmSite.getAllNodes().values());
				
				InsertNodesInDBThread t1 = new InsertNodesInDBThread(allNodes, database, username, password,
						maxDBNodes, cachePurgePrecent);
				t1.start();
			}
		}
		
		updateStatistics(nodes, allNodes, algTimeDiff);		
		
		InsertStatisticsInDBThread t2 = new InsertStatisticsInDBThread(database, username, password, statisticKeys, statisticMap);
		t2.start();
		
		addRecentConnection(nodes);
		
		c.setNodeCount(nodes.size());
		
		ArrayList<LastfmArtist> artists = lastfmSite.toLastfmArtists(nodes);
		for (LastfmArtist artist : artists) {		
			c.addNodeValue(artist);
		}
		
		for (int i = 0; i < artists.size() - 1; i++) {
			c.addEdge(new Edge(i, i + 1));
		}
		
		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(c));
	}

	private Response connectAdjacency(IHTTPSession session, Gson gson, AdjListSite site) {
		AdjacencyListConnectResponse c = new AdjacencyListConnectResponse();
		Map<String, String> parms = session.getParms();
		String beginString = parms.get("begin");
		String endString = parms.get("end");
		int begin, end;
		ArrayList<Node> nodes, allNodes;

		try {
			begin = Integer.parseInt(beginString);
			end = Integer.parseInt(endString);
		} catch (NumberFormatException nfe) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "One of your inputs was not a number.");
		}
		
		if (begin > 4040 || end > 4040 || begin < 0 || end < 0) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Try a number in the range [0, 4040].");
		}

		site.setStartAndEndNodes(beginString, endString);

		// Check recents
		nodes = checkRecentConnections(site);

		if (nodes == null) {
			// Check DB for connection before algorithm
			nodes = checkDB(site);
			
			if (nodes == null) {
				nodes = Algorithm.processConnection(site);
				
				if (nodes == null || nodes.size() == 0) {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "No match was found from these inputs.");
				}
				
				allNodes = new ArrayList<Node>(site.getAllNodes().values());
				
				InsertNodesInDBThread t1 = new InsertNodesInDBThread(allNodes, database, username, password,
						maxDBNodes, cachePurgePrecent);
				t1.start();
			}
		}
		
		InsertStatisticsInDBThread t2 = new InsertStatisticsInDBThread(database, username, password, statisticKeys, statisticMap);
		t2.start();
		
		addRecentConnection(nodes);

		c.setNodeCount(nodes.size());

		for (Node n : nodes) {
			c.addNodeValue(n.getNodeID());
		}

		for (int i = 0; i < nodes.size() - 1; i++) {
			c.addEdge(new Edge(i, i + 1));
		}

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(c));
	}

	private Response getRecentConnections(IHTTPSession session, Gson gson) {
		ArrayList<String> connections = new ArrayList<String>();
		StringBuilder builder;

		for (ArrayList<Node> subRecent : recentConnections) {
			builder = new StringBuilder();

			if (subRecent.get(0) instanceof AdjListNode) {
				builder.append("Adjacency List: ");
			} else if (subRecent.get(0) instanceof LastfmNode) {
				builder.append("Last.fm: ");
			} else if (subRecent.get(0) instanceof ThesaurusNode) {
				builder.append("Thesaurus: ");
			}

			for (int i = 0; i < subRecent.size(); i++) {
				if (subRecent.get(i) instanceof LastfmNode) {
					builder.append( ((LastfmNode) subRecent.get(i)).getName() );
				} else {
					builder.append(subRecent.get(i).getNodeID());
				}
				
				if (i != subRecent.size() - 1) {
					builder.append("->");
				}
			}
			connections.add(builder.toString());
		}

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(connections));
	}
	
	private Response getStatistics(IHTTPSession session, Gson gson) {
		String[] ReadableStats = new String[statisticKeys.length];
		
		ReadableStats[0] = "Total Connections Chains Made: " + statisticMap.get(statisticKeys[0]).toString();
		ReadableStats[1] = "Total Connections Made: " + statisticMap.get(statisticKeys[1]).toString();
		ReadableStats[2] = "Total Nodes in our Database: " + statisticMap.get(statisticKeys[2]).toString();
		ReadableStats[3] = "Average Connection Chain Length: " + statisticMap.get(statisticKeys[3]).toString();
		ReadableStats[4] = "Longest Connection Chain Length: " + statisticMap.get(statisticKeys[4]).toString();
		ReadableStats[5] = "Shortest Connection Chain Length: " + statisticMap.get(statisticKeys[5]).toString();
		ReadableStats[6] = "Total Connection Chain Length: " + statisticMap.get(statisticKeys[6]).toString();
		ReadableStats[7] = "Average Compuation Time: " + statisticMap.get(statisticKeys[7]).toString();
		ReadableStats[8] = "Longest Computation Time: " + statisticMap.get(statisticKeys[8]).toString();
		ReadableStats[9] = "Shortest Computation Time: " + statisticMap.get(statisticKeys[9]).toString();
		ReadableStats[10] = "Total Compuation Time: " + statisticMap.get(statisticKeys[10]).toString();
				
		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(ReadableStats));
	}
}

class InsertNodesInDBThread extends Thread {
	private ArrayList<Node> nodes;
	private String database, username, password;
	private int maxDBNodes;
	private double cachePurgePrecent;

	InsertNodesInDBThread(ArrayList<Node> nodes, String database, String username, String password,
			int maxDBNodes, double cachePurgePrecent) {
		this.nodes = nodes;
		this.database = database;
		this.username = username;
		this.password = password;
		this.maxDBNodes = maxDBNodes;
		this.cachePurgePrecent = cachePurgePrecent;
	}

	public void run() {
		try {
			DBInterfacer db = new DBInterfacer(database, username, password, maxDBNodes, cachePurgePrecent);
						
			// Add all nodes to db
			ArrayList<Node> connections;

			db.addVertices(nodes);
			for (Node n1 : nodes) {
				connections = n1.getConnections();
				
				if (connections == null) {
					continue;
				}
				
				for (Node n2 : connections) {
					db.addConnection(n1, n2);
				}
			}

			db.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: Could not add nodes to database");
		}
	}

}

class InsertStatisticsInDBThread extends Thread {
	private String database, username, password;
	private String[] statisticKeys;
	private HashMap<String, Object> statisticMap;
	
	public InsertStatisticsInDBThread(String database, String username, String password,
			String[] statisticKeys, HashMap<String, Object> statisticMap) {
		this.database = database;
		this.username = username;
		this.password = password;
		this.statisticKeys = statisticKeys;
		this.statisticMap = statisticMap;
	}
	
	public void run() {
		try {
			DBInterfacer db = new DBInterfacer(database, username, password);
			
			// Update number of connections made in db
			for (String statisticKey : statisticKeys) {
				db.setStatistic(statisticKey, statisticMap.get(statisticKey));
			}
			
			db.close();
		} catch (Exception e) {
			System.err.println("Error: Could not add statistics to database");
		}
	}
	
}
