package databaseInterfacing;

public class DBInterfaceTest {

	public static void main(String[] args) {
		DBInterfacer interfacer = new DBInterfacer("remote:localhost/Connections", "root", "team4", 100, 0.2);
//		String[] props = {"name"};
//		Object[] values1 = {"PUT NODE1 NAME HERE"};
//		Object[] values2 = {"PUT NODE2 NAME HERE"};
//		Object node1 = interfacer.addVertex("Node", props, values1);
//		Object node2 = interfacer.addVertex("Node", props, values2);
//		interfacer.addNewConnection("Connection", node1, node2);
		
		String[] props = {"name", "access_time"};
		Object[] values1 = {"sam", "2016-03-03 08:50:31"};
		Object[] values2 = {"Tom", "2016-03-03 08:50:32"};
		
		Object node1 = interfacer.addVertex("Node", props, values1);
		Object node2 = interfacer.addVertex("Node", props, values2);
		Object node3 = interfacer.addVertex("Node", props, values1);
		
		interfacer.addNewConnection("Connection", node1, node2);
		interfacer.addNewConnection("Connection", node2, node3);
		
		//interfacer.removeAllConnections();
		//Object node = interfacer.getVerticesByFields("Node", new String[]{"name"}, new Object[] {"Tom"});
		
		//interfacer.getConnectedNeighbors(node);
		
		System.out.println(interfacer);
		
		interfacer.close();
	}
}
