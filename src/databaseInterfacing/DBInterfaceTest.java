package databaseInterfacing;

public class DBInterfaceTest {

	public static void main(String[] args) {
		DBInterfacer interfacer = new DBInterfacer("remote:localhost/Connections", "root", "team4", 10);
		
		String[] props = {"name", "time"};
		Object[] values1 = {"sam", "4:30"};
		Object[] values2 = {"Tom", "3:46"};
		
		Object node1 = interfacer.addVertex("Node", props, values1);
		Object node2 = interfacer.addVertex("Node", props, values2);
		
		interfacer.addNewConnection("Connection", node1, node2);
		
		System.out.println(interfacer);
		
		interfacer.close();
	}
}
