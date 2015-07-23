package TemporalCommunities;
import java.util.HashSet;
import java.util.Set;

public class Clusting {
	public Set<Node> cluster;
	private double clustertime;
	public Clusting() {
		cluster = new HashSet<Node>();
	}
	public boolean Checkconnectingto(Node n){
		for(Node n1 : cluster)
			if(n1.ReturnConnection(n))
				return true;
		return false;
	}
	public void SetClustertime(double time){
		this.clustertime = time;
	}
	public double ReturnClustertime(){
		return clustertime;
	}
	public int ReturnInsideLink(){
		int outsidelink = 0;
		int ldegree = LocalDegree();
		for(Node n0 : cluster){
			for(Node n1 : n0.ReturnConnect()){
				if(!cluster.contains(n1))
					outsidelink++;
			}
		}
		return (ldegree-outsidelink);
	}
	public Set<Node> Returnconnect(){
		return this.cluster;
	}
	public void AddNoed(Node n){
		int[] a = new int[2];
		//a[-1]=0;
		cluster.add(n);
	}
	public void DeleteNode(Node n){
		cluster.remove(n);
	}
	public int LocalDegree(){
		int temp = 0;
		for(Node n1 : cluster)
			temp = temp + n1.ReturnDegree();
		return temp;
	}
	public int ReturnSize(){
		int size = 0;
		for(Node n1 : cluster)
			size++;
		return size;
	}
	public boolean ReturnConnetcionTo(Clusting c){
		
		return false;
	}
}
