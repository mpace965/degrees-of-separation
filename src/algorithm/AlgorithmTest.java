package algorithm;

import java.util.ArrayList;
import java.util.Scanner;

import siteClasses.*;

public class AlgorithmTest {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);

		String fileSeparator = System.getProperty("file.separator");
		AdjListSite site = new AdjListSite("docs" + fileSeparator + "facebook_combined.txt");

		System.out.println("enter start and end node");
		while (scan.hasNextLine()) {
			String start = scan.nextLine();
			String end = scan.nextLine();
			
			site.setStartAndEndNodes(start, end);

			long time1 = System.currentTimeMillis();
			ArrayList<Node> connection = null;
			try {
				site.resetAccessCount();
				// all processing of the algorithm
				connection = Algorithm.processConnection(site);
			}
			catch (Exception e) {
				System.err.println(e.getStackTrace().toString());
				System.out.println("no connection");
			}
			
			// statistics and printing below
			long time2 = System.currentTimeMillis();
			if (connection == null) {
				System.out.println("no connection");
			}
			else {
				for (int i = 0; i < connection.size() - 1; i++)
					System.out.print(connection.get(i).toString() + " --> ");
				System.out.println(connection.get(connection.size() - 1));
			}
			System.out.printf("Process took : %.3f seconds\n", (double) (time2 - time1) / 1000d);
			System.out.printf("Process had %d file accesses\n", site.getAccessCount());
		}	
		scan.close();
	}
}
