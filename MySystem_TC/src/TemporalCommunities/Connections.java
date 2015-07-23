package TemporalCommunities;
import java.util.HashSet;

public class Connections {
	protected HashSet<String> ConnUpEdges;

	public Connections() {
		ConnUpEdges = new HashSet<String>();
	}

	public void AddConnection(String conn) {
		this.ConnUpEdges.add(conn);
	}

	public void RemoveConnection(String conn) {
		this.ConnUpEdges.remove(conn);
	}
	public void RemoveAllConnection() {
		this.ConnUpEdges.clear();
	}
}
