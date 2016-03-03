
import java.io.File;
import java.io.IOException;

import API.AdjacencyListConnectResponse;
import API.AdjacencyListEdge;

import com.google.gson.Gson;

import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {
	
	public static final String MIME_JSON = "application/json";
	

	public WebApp() throws IOException {
		super("localhost", 8000, new File("client/"), false);
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
			case "/api/connect": {
				AdjacencyListConnectResponse c = new AdjacencyListConnectResponse();
				c.setNodeCount(13);
				c.addEdge(new AdjacencyListEdge(0, 1));
				c.addEdge(new AdjacencyListEdge(1, 2));
				c.addEdge(new AdjacencyListEdge(2, 0));
				c.addEdge(new AdjacencyListEdge(1, 3));
				c.addEdge(new AdjacencyListEdge(3, 2));
				c.addEdge(new AdjacencyListEdge(3, 4));
				c.addEdge(new AdjacencyListEdge(4, 5));
				c.addEdge(new AdjacencyListEdge(5, 6));
				c.addEdge(new AdjacencyListEdge(5, 7));
				c.addEdge(new AdjacencyListEdge(6, 7));
				c.addEdge(new AdjacencyListEdge(6, 8));
				c.addEdge(new AdjacencyListEdge(7, 8));
				c.addEdge(new AdjacencyListEdge(9, 4));
				c.addEdge(new AdjacencyListEdge(9, 11));
				c.addEdge(new AdjacencyListEdge(9, 10));
				c.addEdge(new AdjacencyListEdge(10, 11));
				c.addEdge(new AdjacencyListEdge(11, 12));
				c.addEdge(new AdjacencyListEdge(12, 10));
				
				r = newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(c));
				break;
			}
			default: {
				r = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_JSON, "{}");
			}
		}
		
		return r;
	}
}
