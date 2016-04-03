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
		
		ArrayList<Object> RIDs = db.addVertices(AdjListNodes);
				
		for (int i = 0; i < RIDs.size() - 1; i++) {
			db.connect("Connection", RIDs.get(i), RIDs.get(i + 1));
		}
		
		//db.removeAllConnections();
		//Object node = db.getVerticesByFields("Node", new String[]{"name"}, new Object[] {"Tom"});
		
		//db.getConnectedNeighbors(node);
		
		System.out.println(db);
		
		db.close();
	}
}
