package databaseInterfacing;

import java.util.ArrayList;

import siteClasses.AdjListNode;
import siteClasses.Node;

public class DBInterfaceTest {

	public static void main(String[] args) {
		DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
		
		ArrayList<Node> AdjListNodes = new ArrayList<Node>();
		AdjListNodes.add(new AdjListNode("Tom"));
		AdjListNodes.add(new AdjListNode("Matt"));
		AdjListNodes.add(new AdjListNode("Ryan"));
		AdjListNodes.add(new AdjListNode("Evan"));
		AdjListNodes.add(new AdjListNode("Hasini"));
		AdjListNodes.add(new AdjListNode("John"));
		
		db.addVertices(AdjListNodes);
		
		for (int i = 0; i < AdjListNodes.size() - 1; i++) {
			db.addConnection(AdjListNodes.get(i), AdjListNodes.get(i + 1));
		}
		
		db.close();
		
		db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
		
		ArrayList<Node> shortestPath = db.shortestPath(AdjListNodes.get(0), AdjListNodes.get(5));
		
		for (Node n : shortestPath) {
			System.out.println(n.getNodeID());
		}
		
//		db.removeAllConnections();
		
		System.out.println(db);
		
//		db.setStatistic("NumberOfConnections", "20");
		System.out.println(db.getStatistic("NumberOfConnections"));
		
		db.close();
	}
}
