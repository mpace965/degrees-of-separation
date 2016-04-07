import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import siteClasses.AdjListNode;
import siteClasses.AdjListSite;
import siteClasses.LastfmSite;
import siteClasses.LastfmNode;
import siteClasses.Node;
import siteClasses.Site;
import siteClasses.ThesaurusNode;
import API.AdjacencyListConnectResponse;
import API.Edge;
import API.LastfmArtist;
import API.LastfmConnectResponse;
import API.LastfmTag;
import algorithm.Algorithm;
import databaseInterfacing.DBInterfacer;

import com.google.gson.Gson;

import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {

	public static final String MIME_JSON = "application/json";
	public static ArrayList<ArrayList<Node>> recentConnections;
	public static int maxRecentConnections;

	public WebApp() throws IOException {
		super("localhost", 8000, new File("client/"), false);

		recentConnections = new ArrayList<ArrayList<Node>>();
		maxRecentConnections = 50;
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

	private Response connectLastfm(IHTTPSession session, Gson gson, LastfmSite lastfmSite) {
		LastfmConnectResponse c = new LastfmConnectResponse();
		Map<String, String> parms = session.getParms();
		String beginString = parms.get("begin");
		String endString = parms.get("end");

		lastfmSite.setStartAndEndNodes(beginString, endString);
		// ArrayList<Node> nodes = Algorithm.processConnection(lastfmSite);
		//
		// c.setNodeCount(nodes.size());
		//
		// for (Node n : nodes) {
		// //Create a LastfmArtist with json from n, supply c.addNodeValue with
		// it
		// }
		//
		// for (int i = 0; i < nodes.size() - 1; i++) {
		// c.addEdge(new Edge(i, i + 1));
		// }
		c.setNodeCount(5);

		LastfmArtist cher = new LastfmArtist();
		cher.setName("Cher");
		cher.setImage("http://img2-ak.lst.fm/i/u/174s/09c2f4f6fd9d508077bb4cb769214388.png");
		cher.setListeners(969040);
		cher.setPlaycount(12774053);
		cher.addTag(new LastfmTag("pop", "http://www.last.fm/tag/pop"));
		cher.addTag(new LastfmTag("female vocalists", "http://www.last.fm/tag/female+vocalists"));
		cher.addTag(new LastfmTag("80s", "http://www.last.fm/tag/80s"));
		cher.addTag(new LastfmTag("dance", "http://www.last.fm/tag/dance"));
		cher.addTag(new LastfmTag("rock", "http://www.last.fm/tag/rock"));
		cher.setBio("Cher (born Cherilyn Sarkisian; May 20, 1946) is an Oscar - and Grammy- winning American singer and actress. A major figure for over five decades in the world of popular culture, she is often referred to as the Goddess of Pop for having first brought the sense of female autonomy and self-actualization into the entertainment industry.\n\nShe is known for her distinctive contralto and for having worked extensively across media, as well as for continuously reinventing both her music and image, the latter of which has been known to induce controversy. <a href=\"http://www.last.fm/music/Cher\">Read more on Last.fm</a>");

		LastfmArtist jason = new LastfmArtist();
		jason.setName("Jason Mraz");
		jason.setImage("http://img2-ak.lst.fm/i/u/174s/4e9c73020d1246588c9f0f0b83229ce1.png");
		jason.setListeners(2092932);
		jason.setPlaycount(57580483);
		jason.addTag(new LastfmTag("singer-songwriter", "http://www.last.fm/tag/singer-songwriter"));
		jason.addTag(new LastfmTag("acoustic", "http://www.last.fm/tag/acoustic"));
		jason.addTag(new LastfmTag("pop", "http://www.last.fm/tag/pop"));
		jason.addTag(new LastfmTag("alternative", "http://www.last.fm/tag/alternative"));
		jason.addTag(new LastfmTag("rock", "http://www.last.fm/tag/rock"));
		jason.setBio("Jason Mraz (born June 23, 1977 in Mechanicsville, Virginia) is a Grammy-winning American singer-songwriter. Mraz’s stylistic influences include reggae, pop, rock, folk, jazz, and hip hop.\n\nMraz released his debut album, Waiting for My Rocket to Come in 2002 but it was not until the release of his second album, Mr. A-Z that Mraz achieved commercial success. The album peaked at number five on the Billboard Hot 200 and sold over one hundred thousand copies in the US. <a href=\"http://www.last.fm/music/Jason+Mraz\">Read more on Last.fm</a>");

		c.addNodeValue(cher);
		c.addNodeValue(jason);
		c.addNodeValue(cher);
		c.addNodeValue(jason);
		c.addNodeValue(cher);

		for (int i = 0; i < 4; i++) {
			c.addEdge(new Edge(i, i + 1));
		}

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(c));
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
	
			DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 10000000, 0.2);
			nodes = db.shortestPath(n1, n2);
			db.close();
	
			return nodes;
		} catch (Exception e) {
			return null;
		}
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
				
				addRecentConnection(nodes);
				
				allNodes = new ArrayList<Node>(site.getAllNodes().values());

				InsertInDBThread t1 = new InsertInDBThread(allNodes);
				t1.start();
			}
		}
		
		InsertInDBThread t2 = new InsertInDBThread(null);
		t2.start();
		
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
				builder.append(subRecent.get(i).getNodeID());
				if (i != subRecent.size() - 1) {
					builder.append("->");
				}
			}
			connections.add(builder.toString());
		}

		return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(connections));
	}
	
	private Response getStatistics(IHTTPSession session, Gson gson) {
		try {
			ArrayList<String> stats = new ArrayList<String>();
			
			DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 10000000, 0.2);
			String connectionsMade = db.getStatistic("NumberOfConnections");
			
			if (connectionsMade == null) {
				db.setStatistic("NumberOfConnections", "0");
				connectionsMade = "0";
			}
			
			stats.add("Number of Connections Made: " + connectionsMade);
			db.close();
			
			return newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(stats));
		} catch (Exception e) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Could not get Statistics");
		}
	}
}

class InsertInDBThread extends Thread {
	private ArrayList<Node> nodes;

	InsertInDBThread(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public void run() {
		try {
			DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 10000000, 0.2);
			
			if (nodes == null) {
				// Update number of connections made in db
				String statStr = db.getStatistic("NumberOfConnections");
				if (statStr != null) {
					int statInt = Integer.parseInt(statStr);
					db.setStatistic("NumberOfConnections", Integer.toString(statInt + 1));
				} else {
					db.setStatistic("NumberOfConnections", "1");
				}
			} else {
			
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
			}

			db.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: Could not add nodes to database");
		}
	}

}
