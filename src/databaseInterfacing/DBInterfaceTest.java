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
		db.addConnections(AdjListNodes);
		db.close();
		
		db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
		
		db.shortestPath(AdjListNodes.get(0), AdjListNodes.get(5));
		
//		db.removeAllConnections();
		
		System.out.println(db);
		
		db.close();
	}
}
