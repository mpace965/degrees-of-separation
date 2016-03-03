package algorithm;

import java.util.ArrayList;

import siteClasses.*;

public class AlgorithmTest {
	public static void main(String[] args) {
		String fileSeparator = System.getProperty("file.separator");
		Site site = new Site("docs" + fileSeparator + "facebook_combined.txt");
		Node.setSite(site);
		site.setStart(0);
		site.setEnd(4040);

		long time1 = System.currentTimeMillis();
		ArrayList<Node> connection = Algorithm.processConnection(site); 
		long time2 = System.currentTimeMillis();

		if (connection == null) {
			System.out.println("no connection found");
		}
		else {
			for (int i = 0; i < connection.size() - 1; i++)
				System.out.print(connection.get(i).toString() + " --> ");
			System.out.println(connection.get(connection.size() - 1));
		}
		System.out.printf("Process took : %.3f seconds\n", (double) (time2 - time1) / 1000d);
		System.out.printf("Process had %d file accesses\n", site.fileAccesses);
	}
}
