package databaseInterfacing;

import java.util.ArrayList;

import siteClasses.AdjListNode;
import siteClasses.Node;

public class DBInterfaceTest {

	public static void main(String[] args) {
		DBInterfacer db = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
		
		ArrayList<Node> AdjListNodes = new ArrayList<Node>();
		AdjListNodes.add(new AdjListNode("Tom"));
		AdjListNodes.add(new AdjListNode("Steph"));
		
		db.addVertices(AdjListNodes);
		db.addConnections(AdjListNodes);
		
		//db.removeAllConnections();		
		//db.getConnectedNeighbors(node);
		
		System.out.println(db);
		
		db.close();
	}
}
