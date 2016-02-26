package algorithm;

import java.util.ArrayList;

import siteClasses.*;

public class AlgorithmTest {
	public static void main(String[] args) {
		// TODO account for unix system
		Site site = new Site("C:\\Users\\Ryan\\Downloads\\facebook_combined.txt");
		Site.setStart(0);
		Site.setEnd(500);
		ArrayList<Node> connection = Algorithm.processConnection(site); 
		
		
		System.out.println(connection.toString());
	}
}
