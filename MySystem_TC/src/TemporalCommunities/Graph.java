package TemporalCommunities;
import java.util.*;

public class Graph {
	public HashSet<String> EdgesSet;
	private double graphtime;
	public Graph() {
		EdgesSet = new HashSet<String>();
		graphtime = 0.0;
	}
	public void BuildGraph(String conn) {
		EdgesSet.add(conn);
	}
	public void SetGraphtime(double time) {
		graphtime = time;
	}
	public double GetGraphtime() {
		return graphtime;
	}
}
