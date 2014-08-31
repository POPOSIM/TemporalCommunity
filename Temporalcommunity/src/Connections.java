import java.util.HashSet;

public class Connections {
	protected HashSet<String> checkupdown;

	public Connections() {
		checkupdown = new HashSet<String>();
	}

	public void AddConnection(String conn) {
		this.checkupdown.add(conn);
	}

	public void RemoveConnection(String conn) {
		this.checkupdown.remove(conn);
	}
}
