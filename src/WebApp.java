
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import siteClasses.AdjListSite;
import siteClasses.Node;
import API.AdjacencyListConnectResponse;
import API.AdjacencyListEdge;
import algorithm.Algorithm;
import databaseInterfacing.DBInterfacer;

import com.google.gson.Gson;

import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {
	
	public static final String MIME_JSON = "application/json";
	

	public WebApp() throws IOException {
		super("cs307team4.cs.purdue.edu", 80, new File("client/"), false);
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
			default: {
				r = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_JSON, "{}");
			}
		}
		
		return r;
	}
	
	private Response connectAdjacency(IHTTPSession session, Gson gson, AdjListSite site) {
		AdjacencyListConnectResponse c = new AdjacencyListConnectResponse();
		Map<String, String> parms = session.getParms();
		String beginString = parms.get("begin");
		String endString = parms.get("end");
		
		try {
			Integer.parseInt(beginString);
			Integer.parseInt(endString);
		} catch (NumberFormatException nfe) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "One of your inputs was not a number.");
		}
		site.setStartAndEndNodes(beginString, endString);
		ArrayList<Node> nodes = Algorithm.processConnection(site);
		
		InsertInDBThread thread = new InsertInDBThread(nodes);
		thread.start();
		
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
}

class InsertInDBThread extends Thread {
	private ArrayList<Node> nodes;
	
	InsertInDBThread(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void run() {
		DBInterfacer db;
		try {
			db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
			
			db.addVertices(nodes);
			db.addConnections(nodes);
			
			db.close();
		} catch (Exception e) {
			System.err.println("Error: Could not add nodes to database");
		}
	}
	
}
