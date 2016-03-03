package databaseInterfacing;

public class DBInterfaceTest {

	public static void main(String[] args) {
		DBInterfacer interfacer = new DBInterfacer("remote:localhost/Connections", "root", "team4");
		
		String[] props = {"name", "John"};
		interfacer.setVertexProperties(interfacer.addVertex(), props);
		
		interfacer.addNewConnection("Tom", "Matt");
	}
}
