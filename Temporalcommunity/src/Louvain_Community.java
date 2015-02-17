import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Louvain_Community {
	private Graph network;
	private Set<Clusting> clusters;
	private Set<Node> nodes;
	Map<Node, Clusting> mapping;
	private double Community_time;
	private double ModQ;

	public Louvain_Community(Graph g) {
		this.network = new Graph();
		this.clusters = new HashSet<Clusting>();
		this.network = g;
		this.Community_time = g.GetGraphtime();
		this.ModQ = 0.0;
		this.mapping = new HashMap<Node, Clusting>();
	}

	private void Build() {
		nodes = new HashSet<Node>();
		for (String s1 : network.GraphMap) {
			String[] connectionstring = s1.split(" ");
			if (nodes.isEmpty()) {
				Node n0 = new Node();
				n0.SetId(connectionstring[0]);
				Node n1 = new Node();
				n1.SetId(connectionstring[1]);
				n1.SetConnection(n0);
				n0.SetConnection(n1);
				nodes.add(n0);
				nodes.add(n1);
			} else {
				int find0 = 0, find1 = 0;
				Node n0 = null, n1 = null;
				for (Node n : nodes) {
					if (n.ReturnId().equals(connectionstring[0])) {
						find0 = 1;
						n0 = n;
					}
					if (n.ReturnId().equals(connectionstring[1])) {
						find1 = 1;
						n1 = n;
					}
				}
				if (find0 == 1 && find1 == 1) {
					n0.SetConnection(n1);
					n1.SetConnection(n0);
				} else if (find0 == 0 && find1 == 1) {
					n0 = new Node();
					n0.SetId(connectionstring[0]);
					n0.SetConnection(n1);
					n1.SetConnection(n0);
					nodes.add(n0);
				} else if (find0 == 1 && find1 == 0) {
					n1 = new Node();
					n1.SetId(connectionstring[1]);
					n1.SetConnection(n0);
					n0.SetConnection(n1);
					nodes.add(n1);
				} else if (find0 == 0 && find1 == 0) {
					n0 = new Node();
					n0.SetId(connectionstring[0]);
					n1 = new Node();
					n1.SetId(connectionstring[1]);
					n0.SetConnection(n1);
					n1.SetConnection(n0);
					nodes.add(n0);
					nodes.add(n1);
				}
			}
		}

	}

	private void onelevel() {
		double NewModQ = 0.0;
		double OldModQ = 0.0;
		double Oldgain = 0.0;
		double Newgain = 0.0;
		boolean stable = false;
		Node Gotn = null;
		Set<Node> temp;
		Set<Node> temp2 = new HashSet<Node>();
		for (Node n1 : nodes) {
			Clusting c1 = new Clusting();
			c1.AddNoed(n1);
			c1.SetClustertime(Community_time);
			clusters.add(c1);
			mapping.put(n1, c1);
		}
		//System.out.println("LevelOne Start");
		OldModQ = modularity();
		//System.out.println(OldModQ);
		while (!stable) {
			stable = true;
			clusters.clear();
			for (Node n1 : nodes) {
				temp2.clear();
				temp = n1.ReturnConnect();
				for (Node n : mapping.get(n1).cluster) {
					if (n != n1) {
						temp2.add(n);
					}
				}
				for(Node n : temp2){
					mapping.put(n, null);
				}
				
				mapping.put(n1, null);
				Clusting c1 = new Clusting();
				c1.SetClustertime(Community_time);
				c1.AddNoed(n1);
				mapping.put(n1, c1);
				
				if (!temp2.isEmpty()) {
					while (!temp2.isEmpty()) {
						Clusting c = new Clusting();
						c.SetClustertime(Community_time);
						int stop = 0;
						while (stop != 1 && !temp2.isEmpty()) {
							stop = 1;
							for (Node n : temp2) {
								if (c.cluster.isEmpty()) {
									c.AddNoed(n);
									temp2.remove(n);
									stop = 0;
									break;
								} else {
									if (c.Checkconnectingto(n)) {
										c.AddNoed(n);
										temp2.remove(n);
										stop = 0;
										break;
									}
								}
							}
						}
						for (Node n : c.cluster) {
							mapping.put(n, c);
						}
					}
				}
				for (Node n2 : temp) {
					Newgain = gain(mapping.get(n1), mapping.get(n2));
					if (Newgain > Oldgain) {
						Gotn = n2;
						Oldgain = Newgain;
					}
				}

				if (Oldgain > 0) {
					Clusting c;
					c = mapping.get(Gotn);
					c.AddNoed(n1);
					mapping.remove(n1);
					mapping.put(n1, c);
					Oldgain = 0.0;
				}
			}
			for (Node n : nodes) {
				Clusting c1 = mapping.get(n);
				clusters.add(c1);
			}
			NewModQ = modularity();
			//System.out.println(NewModQ);
			if (NewModQ > OldModQ) {
				OldModQ = NewModQ;
				stable = false;
			}
		}
		ModQ = modularity();
		//System.out.println(ModQ);
		//System.out.println("LevelOne Done");
	}

	private void twolevel() {
		double NewModQ = 0.0;
		double OldModQ = 0.0;
		double Oldgain = 0.0;
		double Newgain = 0.0;
		boolean stable = false;
		Set<Clusting> temp;
		//System.out.println("LevelTwo Start");
		OldModQ = modularity();
		//System.out.println(OldModQ);
		while(!stable){
			stable = true;
			temp = clusters;
			Clusting Gotc = null;
			for(Clusting c : clusters){
				Oldgain = 0.0;
				for(Clusting t : temp){
					if(c != t){
						Newgain = gain(c, t);
						if (Newgain > Oldgain) {
							Gotc = t;
							Oldgain = Newgain;
						}
					}
				}
				if (Oldgain > 0) {
					for(Node n : Gotc.cluster){
						c.AddNoed(n);
					}
					clusters.remove(Gotc);
					break;
				}
			}
			NewModQ = modularity();
			//System.out.println(NewModQ);
			if (NewModQ > OldModQ) {
				OldModQ = NewModQ;
				stable = false;
			}
		}
		ModQ = modularity();
		//System.out.println(ModQ);
		//System.out.println("LevelTwo Done");
	}

	private double modularity() {
		double newmod = 0;
		int globaldegree = 0;
		int localdegree = 0;
		int insidelink = 0;
		for (Node n : nodes) {
			globaldegree = globaldegree + n.ReturnDegree();
		}
		//System.out.println(globaldegree);
		for (Clusting c : clusters) {
			insidelink = c.ReturnInsideLink();
			localdegree = c.LocalDegree();
			newmod = newmod
					+ (((double) insidelink / (double) globaldegree) - (((double) localdegree * (double) localdegree) / ((double) globaldegree * (double) globaldegree)));
		}
		
		return newmod;
	}

	private double gain(Clusting c1, Clusting c2) {
		int dnc = 0;
		int c1degree, c2degree;
		int globaldegree = 0;
		double gain = 0.0;
		for(Node n1 : c1.cluster){
			for (Node n2 : c2.cluster) {
				if (n1.ReturnConnection(n2))
					dnc++;
			}
		}
		//System.out.println("dnc "+dnc);
		for (Node n : nodes) {
			globaldegree = globaldegree + n.ReturnDegree();
		}
		//System.out.println(globaldegree);
		c1degree = c1.LocalDegree();
		//System.out.println("c1degree "+c1degree);
		c2degree = c2.LocalDegree();
		//System.out.println("c2degree "+c2degree);
		gain = (((double) dnc) - (((double) c1degree * (double) c2degree) / (double) globaldegree));
		return gain;
	}
	public double ReturnLouvainTime(){
		return this.Community_time;
	}
	public double ReturnModQ(){
		return this.ModQ;
	}
	public Set<Clusting> Getclusting() {
		Build();
		onelevel();
		/*
		System.out.println("Time:"+this.Community_time);
		for (Clusting c : clusters) {
			System.out.println("cluster");
			for (Node n : c.cluster) {
				System.out.print(n.ReturnId() + " ");
			}
			System.out.println();
		}
		*/
		twolevel();
		/*
		System.out.println("Time:"+this.Community_time);
		for (Clusting c : clusters) {
			System.out.println("cluster");
			for (Node n : c.cluster) {
				System.out.print(n.ReturnId() + " ");
			}
			System.out.println();
		}
		*/
		return clusters;
	}
}
