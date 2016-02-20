
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import API.Comment;

import com.google.gson.Gson;

import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;

public class WebApp extends SimpleWebServer {
	
	public static final String MIME_JSON = "application/json";
	
	private ArrayList<Comment> comments;

	public WebApp() throws IOException {
		super("cs307team4.cs.purdue.edu", 80, new File("client/"), false);
		comments = new ArrayList<Comment>();
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
			case "/api/comments": {
				Random rand = new Random();
				comments.add(new Comment(rand.nextInt(), "Tom", "I have commented " + (comments.size() + 1) + " times"));
				
				r = newFixedLengthResponse(Response.Status.OK, MIME_JSON, gson.toJson(comments));
				break;
			}
			default: {
				r = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_JSON, "{}");
			}
		}
		
		return r;
	}
}
