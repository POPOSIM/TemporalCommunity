package TemporalCommunities;
import java.util.HashSet;
import java.util.Set;

public class Node {
	private String Id;
	private int Degree;
	private Set<Node> ConnectingTo;
	public Node(){
		this.ConnectingTo = new HashSet<Node>();
	}
	public void SetId(String id){
		this.Id = id;
	}
	public String ReturnId(){
		return Id;
	}
	public int ReturnDegree(){
		return this.Degree;
	}
	public void SetConnection(Node conn){
		this.ConnectingTo.add(conn);
		int number = 0;
		for (Node n1 : ConnectingTo) {
			number++;
		}
		this.Degree = number;
	}
	public boolean ReturnConnection(Node conn){
		return ConnectingTo.contains(conn);
	}
	public Set<Node> ReturnConnect(){
		return this.ConnectingTo;
	}
}
