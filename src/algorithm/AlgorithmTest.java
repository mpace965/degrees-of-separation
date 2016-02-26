package algorithm;

import java.util.ArrayList;

import siteClasses.*;

public class AlgorithmTest {
	public static void main(String[] args) {
		// TODO account for unix system
		Site site = new Site("C:\\Users\\Ryan\\Downloads\\facebook_combined.txt");
//		ConnectionFinder find = new ConnectionFinder(site);
//		ArrayList<Node> connection = find.getChain(0, 500); 
		
		Node start = new Node(0);
		site.setConnections(start);
		
		System.out.println();
	}
}
