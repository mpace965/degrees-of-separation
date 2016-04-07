
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import siteClasses.AdjListNode;
import siteClasses.AdjListSite;
import siteClasses.LastfmNode;
import siteClasses.Node;
import siteClasses.Site;
import API.AdjacencyListConnectResponse;
import API.AdjacencyListEdge;
import algorithm.Algorithm;
import databaseInterfacing.DBInterfacer;

import com.google.gson.Gson;

import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {
	
	public static final String MIME_JSON = "application/json";
	public static ArrayList<ArrayList <Node>> recentConnections;

	public WebApp() throws IOException {
		super("cs307team4.cs.purdue.edu", 80, new File("client/"), false);
		
		recentConnections = new ArrayList<ArrayList <Node>>();
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
		String fileSeparator = System.getProperty("file.separator");
		AdjListSite site = new AdjListSite("docs" + fileSeparator + "facebook_combined.txt");
		Response r = null;
		
		switch (uri) {
			case "/api/connectAdjacency": {
				r = connectAdjacency(session, gson, site);
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
	
	private ArrayList<Node> checkRecentConnections(Site site) {
		Node n1 = site.getStartNode();
		Node n2 = site.getEndNode();
		
		for (ArrayList<Node> subRecent : recentConnections) {
			String headID = subRecent.get(0).getNodeID();
			String tailID = subRecent.get(subRecent.size() - 1).getNodeID();
			String n1ID = n1.getNodeID();
			String n2ID = n2.getNodeID();
			
			if (n1ID.equals(headID) && n2ID.equals(tailID)) {
				return subRecent;
			} else if (n1ID.equals(tailID) && n2ID.equals(headID)) {
				ArrayList<Node> reversedList = new ArrayList<Node>(subRecent);
				Collections.reverse(reversedList);
				return reversedList;
			}
		}
		
		return null;
	}
	
	private void addRecentConnection(ArrayList<Node> nodes) {
		recentConnections.add(nodes);
		
		if (recentConnections.size() > 50) {
			for (int i = 0; i < 10; i++) {
				recentConnections.remove(i);
			}
		}
	}
	
	private ArrayList<Node> checkDB(Site site) {
		ArrayList<Node> nodes;
		Node n1 = site.getStartNode();
		Node n2 = site.getEndNode();
		
		DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
		nodes = db.shortestPath(n1, n2);
		db.close();
		
		return nodes;
	}
	
	private Response connectAdjacency(IHTTPSession session, Gson gson, AdjListSite site) {
		AdjacencyListConnectResponse c = new AdjacencyListConnectResponse();
		Map<String, String> parms = session.getParms();
		String beginString = parms.get("begin");
		String endString = parms.get("end");
		ArrayList<Node> nodes, allNodes;
		
		try {
			Integer.parseInt(beginString);
			Integer.parseInt(endString);
		} catch (NumberFormatException nfe) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "One of your inputs was not a number.");
		}
		site.setStartAndEndNodes(beginString, endString);
		
		// Check recents
		nodes = checkRecentConnections(site);
		
		if (nodes == null) {
			// Check DB for connection before algorithm
			nodes = checkDB(site);
			
			if (nodes == null) {
				nodes = Algorithm.processConnection(site);
				addRecentConnection(nodes);
				
				allNodes = new ArrayList<Node> (site.getAllNodes().values());
				
				InsertInDBThread t1 = new InsertInDBThread(allNodes);
				t1.start();
			}
		}
		
		c.setNodeCount(nodes.size());
		
		//for (Node n : nodes) {
			// TODO allow for adding a String value if possible
			//c.addNodeValue(n.getNodeID());
		//}
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			c.addEdge(new AdjacencyListEdge(i, i + 1));
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
			}
			
			for (int i = 0; i < subRecent.size(); i++) {
				builder.append(subRecent.get(i).getNodeID());
				if (i != subRecent.size() - 1) {
					builder.append("->");
				}
			}
			connections.add(builder.toString());
		}
		
		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(connections));
	}
}

class InsertInDBThread extends Thread {
	private ArrayList<Node> nodes;
	
	InsertInDBThread(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void run() {
		try {
			DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
			// Add all nodes
			ArrayList<Node> connections;
			
			db.addVertices(nodes);
			for (Node n1 : nodes) {
				connections = n1.getConnections();
				for (Node n2 : connections) {
					db.addConnection(n1, n2);
				}
			}
			
			db.close();
		} catch (Exception e) {
			System.err.println("Error: Could not add nodes to database");
		}
	}
	
}
