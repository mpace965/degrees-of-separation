package algorithm;

import java.util.ArrayList;

import siteClasses.*;

public class AlgorithmTest {
	public static void main(String[] args) {
		// TODO account for unix system
		Site site = new Site("C:\\Users\\Ryan\\Downloads\\facebook_combined.txt");
		Node.setSite(site);
		site.setStart(0);
		site.setEnd(4038);
		
		long time1 = System.currentTimeMillis();
		ArrayList<Node> connection = Algorithm.processConnection(site); 
		long time2 = System.currentTimeMillis();
		
		for (int i = 0; i < connection.size() - 1; i++)
			System.out.print(connection.get(i).toString() + " --> ");
		System.out.println(connection.get(connection.size() - 1));
		System.out.printf("Process took : %.3f seconds\n", (double) (time2 - time1) / 1000d);
		System.out.printf("Process had %d file accesses\n", site.fileAccesses);
	}
}
