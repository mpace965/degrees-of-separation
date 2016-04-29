import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import API.StatisticsResponse;
import API.RecentResponse;
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
	public static ConcurrentHashMap<String, Object> statisticMap;
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
		
		statisticMap = new ConcurrentHashMap<String, Object>();
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
		if (session.getMethod() == Method.POST) {
			return uploadFile(session);
		}
		
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
			
			case "/api/uploadFile": {
				r = uploadFile(session);
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
		Object[] statisticInitialVals = { 0, 0, 0, 0.0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0 };
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
		Long algTime1, algTime2, algTimeDiff = 0l;

		try {
			lastfmSite.setStartAndEndNodes(beginString, endString);
		} catch (Exception e) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Could not find one or both of the artists you searched for.");
		}
		
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
						maxDBNodes, cachePurgePrecent, statisticMap);
				
				t1.start();
			}
			
			UpdateAndInsertStatisticsInDBThread t2 = new UpdateAndInsertStatisticsInDBThread(database, username, password,
					statisticKeys, statisticMap, nodes, (double)algTimeDiff / 1000.0);
			t2.start();
		}
		
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

		try {
			site.setStartAndEndNodes(beginString, endString);
		} catch (Exception e) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "The nodes you entered do not exist");
		}

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
						maxDBNodes, cachePurgePrecent, statisticMap);
				t1.start();
			}
			addRecentConnection(nodes);
		}

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
		ArrayList<RecentResponse> connections = new ArrayList<RecentResponse>();

		for (ArrayList<Node> subRecent : recentConnections) {
			RecentResponse r = new RecentResponse();

			if (subRecent.get(0) instanceof AdjListNode) {
				r.setSite("Adjacency List");
			} else if (subRecent.get(0) instanceof LastfmNode) {
				r.setSite("Last.fm");
			} else if (subRecent.get(0) instanceof ThesaurusNode) {
				r.setSite("Thesaurus");
			}
			
			if (subRecent.get(0) instanceof LastfmNode) {
				r.setBegin( ((LastfmNode) subRecent.get(0)).getName() );
				r.setEnd( ((LastfmNode) subRecent.get(subRecent.size() - 1)).getName() );
			} else {
				r.setBegin(subRecent.get(0).getNodeID());
				r.setEnd(subRecent.get(subRecent.size() - 1).getNodeID());
			}
			
			connections.add(r);
		}

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(connections));
	}
	
	private Response getStatistics(IHTTPSession session, Gson gson) {
		StatisticsResponse sr = new StatisticsResponse();
		
		sr.setTotalConnectionChains((int) statisticMap.get(statisticKeys[0]));
		sr.setTotalConnections((int) statisticMap.get(statisticKeys[1]));
		sr.setTotalDBNodes((int) statisticMap.get(statisticKeys[2]));
		sr.setAverageChainLength((double) statisticMap.get(statisticKeys[3]));
		sr.setLongestChainLength((int) statisticMap.get(statisticKeys[4]));
		sr.setShortestChainLength((int) statisticMap.get(statisticKeys[5]));
		sr.setTotalChainLength((int) statisticMap.get(statisticKeys[6]));
		sr.setAverageComputationTime((double) statisticMap.get(statisticKeys[7]));
		sr.setLongestComputationTime((double) statisticMap.get(statisticKeys[8]));
		sr.setShortestComputationTime((double) statisticMap.get(statisticKeys[9]));
		sr.setTotalComputationTime((double) statisticMap.get(statisticKeys[10]));

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(sr));
	}
	
	private Response uploadFile(IHTTPSession session) {
		try {
	        Map<String, String> files = new HashMap<String, String>();
	        session.parseBody(files);
	        
	        Set<String> names = files.keySet();
	        for(String name : names){
	        	System.out.println(name);
	        	
	            String location = files.get(name);

	            File tempfile = new File(location);
	            Files.copy(tempfile.toPath(), new File("../uploads" + name).toPath(), StandardCopyOption.REPLACE_EXISTING);
	        }
	        
		} catch (Exception e) {
			System.err.println("Could not upload file.");
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Could not upload file.");
		}
		
		return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "File was successfully uploaded.");
	}
}

class InsertNodesInDBThread extends Thread {
	private ArrayList<Node> nodes;
	private String database, username, password;
	private int maxDBNodes;
	private double cachePurgePrecent;
	private ConcurrentHashMap<String, Object> statisticMap;

	InsertNodesInDBThread(ArrayList<Node> nodes, String database, String username, String password,
			int maxDBNodes, double cachePurgePrecent, ConcurrentHashMap<String, Object> statisticMap) {
		this.nodes = nodes;
		this.database = database;
		this.username = username;
		this.password = password;
		this.maxDBNodes = maxDBNodes;
		this.cachePurgePrecent = cachePurgePrecent;
		this.statisticMap = statisticMap;
	}

	public void run() {
		try {
			DBInterfacer db = new DBInterfacer(database, username, password, maxDBNodes, cachePurgePrecent);
						
			// Add all nodes to db
			ArrayList<Node> connections;

			db.addVertices(nodes);
			statisticMap.put("TotalDBNodes", db.countTotalNodes() - statisticMap.size());
			
			for (Node n1 : nodes) {
				connections = n1.getConnections();
				
				if (connections == null) {
					continue;
				}
				
				for (Node n2 : connections) {
					db.addConnection(n1, n2);
				}
			}
			statisticMap.put("TotalConnections", db.countTotalEdges());
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: Could not add nodes to database");
		}
	}

}

class UpdateAndInsertStatisticsInDBThread extends Thread {
	private DBInterfacer db;
	private String database, username, password;
	private String[] statisticKeys;
	private ConcurrentHashMap<String, Object> statisticMap;
	private ArrayList<Node> nodes;
	private Double algTime;
	
	public UpdateAndInsertStatisticsInDBThread(String database, String username, String password,
			String[] statisticKeys, ConcurrentHashMap<String, Object> statisticMap, 
			ArrayList<Node> nodes, Double algTime) {
		this.database = database;
		this.username = username;
		this.password = password;
		this.statisticKeys = statisticKeys;
		this.statisticMap = statisticMap;
		this.nodes = nodes;
		this.algTime = algTime;
	}
	
	public synchronized void updateStatistics(ArrayList<Node> nodes, Double algTime) {
		Math.round(algTime);
		// Get all current statistics
		Integer totalConnectionChains = (Integer)statisticMap.get("TotalConnectionChains");
		Integer totalConnections;
		Integer totalDBNodes;
		Integer totalChainLength = (Integer)statisticMap.get("TotalChainLength");
		Double totalComputationTime = (Double)statisticMap.get("TotalComputationTime");
		Double averageChainLength;
		Integer longestChainLength = (Integer)statisticMap.get("LongestChainLength");
		Integer shortestChainLength = (Integer)statisticMap.get("ShortestChainLength");
		Double averageComputationTime;
		Double longestComputationTime = (Double)statisticMap.get("LongestComputationTime");
		Double shortestComputationTime = (Double)statisticMap.get("ShortestComputationTime");
		
		// Update all statistics
		totalConnectionChains++;
		totalConnections = db.countTotalEdges();
		totalDBNodes = db.countTotalNodes() - statisticMap.size();
		totalChainLength += nodes.size();
		totalComputationTime += algTime;
		averageChainLength = (double)totalChainLength / (double)totalConnectionChains;
		if (nodes.size() < shortestChainLength || shortestChainLength == 0)
			shortestChainLength = nodes.size();
		if (nodes.size() > longestChainLength || longestChainLength == 0)
			longestChainLength = nodes.size();
		averageComputationTime = (double)totalComputationTime / (double)totalConnectionChains;
		if (algTime != 0.0 && (algTime < shortestComputationTime || shortestComputationTime == 0.0))
			shortestComputationTime = algTime;
		if (algTime != 0.0 && (algTime > longestComputationTime || longestComputationTime == 0.0))
			longestComputationTime = algTime;
		
		// Set all new statistics
		statisticMap.put("TotalConnectionChains", totalConnectionChains);
		statisticMap.put("TotalConnections", totalConnections);
		statisticMap.put("TotalDBNodes", totalDBNodes);
		statisticMap.put("TotalChainLength", totalChainLength);
		statisticMap.put("TotalComputationTime", totalComputationTime);
		statisticMap.put("AverageChainLength", averageChainLength);
		statisticMap.put("LongestChainLength", longestChainLength);
		statisticMap.put("ShortestChainLength", shortestChainLength);
		statisticMap.put("AverageComputationTime", averageComputationTime);
		statisticMap.put("LongestComputationTime", longestComputationTime);
		statisticMap.put("ShortestComputationTime", shortestComputationTime);
	}
	
	public synchronized void insertStatistics() {
		// Update number of connections made in db
		for (String statisticKey : statisticKeys) {
			db.setStatistic(statisticKey, statisticMap.get(statisticKey));
		}
	}
	
	public void run() {
		try {
			db = new DBInterfacer(database, username, password);
			updateStatistics(nodes, algTime);
			insertStatistics();
			db.close();
		} catch (Exception e) {
			System.err.println("Error: Could not add statistics to database");
		}
	}
	
}
