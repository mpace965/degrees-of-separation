package siteClasses;

import java.io.IOException;
import java.net.*;
import java.io.*;
import com.google.gson.*;


// TODO: Where is this being used? 
// Can we get rid of it? Or could we
// use the "getInfo()" method in the LastfmSite
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
}
