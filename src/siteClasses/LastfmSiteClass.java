package siteClasses;

import java.io.IOException;
import java.net.*;
import java.io.*;
import com.google.gson.*;


// THIS CURRENTLY DOES NOTHING, HOLDS CODE WE MIGHT USE



@Deprecated
public class LastfmSiteClass {

	// This is our API key, never change this...
	final String apiKey = "c6c45e68f6b2a663da996fc504cf9f8b";

	// When passed the name of an Artist will return similar artists from the
	// site last.fm
	// Throws an IO exception if the artist passed was no valid
	public JsonObject getSimilar(String artistName) throws IOException {

		// Builds the strings used for the API requests
		String urlStart = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&format=json";
		String artist = "&artist=" + artistName;
		String key = "&api_key=" + apiKey;
		String url = urlStart + artist + key;

		// Builds a buffered reader to interpret input received from the API
		// request
		URL lastfmGetSimilar = new URL(url);
		URLConnection lfmSim = lastfmGetSimilar.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				lfmSim.getInputStream()));

		// Reads in a string received from the API requests
		StringBuilder builder = new StringBuilder();
		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
		}

		// Closes the buffered reader
		in.close();

		// Converts the string to a Json object
		JsonParser parser = new JsonParser();
		JsonObject simArt = parser.parse(builder.toString()).getAsJsonObject();

		return simArt;
	}

	// When passed the name of an Artist will return additional information on that artist from the
	// site last.fm
	// Throws an IO exception if the artist passed was no valid
	public JsonObject getInfo(String artistName) throws IOException {

		// Builds the strings used for the API requests
		String urlStart = "http://ws.audioscrobbler.com/2.0/?method=artist.getInfo&format=json";
		String artist = "&artist=" + artistName;
		String key = "&api_key=" + apiKey;
		String url = urlStart + artist + key;

		// Builds a buffered reader to interpret input received from the API
		// request
		URL lastfmGetSimilar = new URL(url);
		URLConnection lfmSim = lastfmGetSimilar.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				lfmSim.getInputStream()));

		// Reads in a string received from the API requests
		StringBuilder builder = new StringBuilder();
		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
		}

		// Closes the buffered reader
		in.close();

		// Converts the string to a Json object
		JsonParser parser = new JsonParser();
		JsonObject simArt = parser.parse(builder.toString()).getAsJsonObject();

		return simArt;
	}
	
	
	public static void main(String[] args) {
		LastfmSiteClass lf = new LastfmSiteClass();
		try {
			JsonObject z = lf.getSimilar("cher");
			int i = 0;
			String parts[] = new String [1024];
			JsonObject similar = z.getAsJsonObject("similarartists");
			for (JsonElement x : similar.getAsJsonArray("artist")) {
				parts[i] = x.getAsJsonObject().get("name").toString();
				parts[i] = parts[i].substring(1, parts[i].length()-1);
				System.out.println(parts[i]);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
