import java.util.*;

public class Graph {
	public HashSet<String> GraphMap;
	private double graphtime;
	public Graph() {
		GraphMap = new HashSet<String>();
		graphtime = 0.0;
	}
	public void BuildGraph(String conn) {
		GraphMap.add(conn);
	}
	public void SetGraphtime(double time) {
		graphtime = time;
	}
	public double GetGraphtime() {
		return graphtime;
	}
}
