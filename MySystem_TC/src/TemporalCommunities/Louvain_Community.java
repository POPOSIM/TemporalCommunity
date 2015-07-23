package TemporalCommunities;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import modularity.ModularityOptimizer;

public class Louvain_Community {
	private Graph graph;
	private Set<Clusting> clusters;
	private Set<Node> nodes;
	Map<Node, Clusting> mapping;
	private double Community_time;
	private double ModQ;
	private int EllisonInt = 0;
	public Louvain_Community(Graph g) {
		this.graph = new Graph();
		this.clusters = new HashSet<Clusting>();
		this.graph = g;
		this.Community_time = g.GetGraphtime();
		this.ModQ = 0.0;
		this.mapping = new HashMap<Node, Clusting>();
	}

	private void Build() {
		nodes = new HashSet<Node>();
		int[] a = new int[2];

		for (String edge : graph.EdgesSet) {
			String[] edgestring = edge.split(" ");
			if (nodes.isEmpty()) {
				Node n0 = new Node();
				n0.SetId(edgestring[0]);
				Node n1 = new Node();
				n1.SetId(edgestring[1]);
				n1.SetConnection(n0);
				n0.SetConnection(n1);
				nodes.add(n0);
				nodes.add(n1);
			} else {
				int n0InLouvainGraph = 0, n1InLouvainGraph = 0;
				Node n0 = null, n1 = null;
				for (Node n : nodes) {
					if (n.ReturnId().equals(edgestring[0])) {
						n0InLouvainGraph = 1;
						n0 = n;
					}
					if (n.ReturnId().equals(edgestring[1])) {
						n1InLouvainGraph = 1;
						n1 = n;
					}
				}
				if (n0InLouvainGraph == 1 && n1InLouvainGraph == 1) {
					n0.SetConnection(n1);
					n1.SetConnection(n0);
				} else if (n0InLouvainGraph == 0 && n1InLouvainGraph == 1) {
					n0 = new Node();
					n0.SetId(edgestring[0]);
					n0.SetConnection(n1);
					n1.SetConnection(n0);
					nodes.add(n0);
				} else if (n0InLouvainGraph == 1 && n1InLouvainGraph == 0) {
					n1 = new Node();
					n1.SetId(edgestring[1]);
					n1.SetConnection(n0);
					n0.SetConnection(n1);
					nodes.add(n1);
				} else if (n0InLouvainGraph == 0 && n1InLouvainGraph == 0) {
					n0 = new Node();
					n0.SetId(edgestring[0]);
					n1 = new Node();
					n1.SetId(edgestring[1]);
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
		int[] a = new int[2];
		Node Gotn = null;
		Set<Node> temp;
		Set<Node> temp2 = new HashSet<Node>();
		for (Node n1 : nodes) {
			Clusting c1 = new Clusting();
			c1.AddNoed(n1);
			c1.SetClustertime(Community_time);
			//System.out.println(Community_time);
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
		int[] a = new int[2];
		for (Node n : nodes) {
			globaldegree = globaldegree + n.ReturnDegree();
		}
		//System.out.println(globaldegree);
		for (Clusting c : clusters) {
			insidelink = c.ReturnInsideLink();
			localdegree = c.LocalDegree();
			newmod = newmod
					+ 
					(
						( (double) insidelink / (double) globaldegree ) 
						- 
						(
							( (double) localdegree * (double) localdegree ) 
							/ 
							( (double) globaldegree * (double) globaldegree )
						)
					);
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
	public Set<Clusting> Getclusting() throws IOException {
		//Build(); //Check done OK!! , adding nodes and edges from graph to Louvain Community 
		//onelevel();
		//twolevel();

		String inputpath = "Temp/TemporalCommunitiesTemp/cluster/input.txt";
		String outputpath ="Temp/TemporalCommunitiesTemp/cluster/output.txt";
		writeEdges(inputpath);
		RunModularity(inputpath,outputpath);
		return clusters;
	}
	
	public void writeEdges(String FilePath) throws IOException{
		FileWriter fw = new FileWriter(FilePath);
		String NewEdges ="";

		for (String edge : graph.EdgesSet)
			NewEdges = NewEdges + edge +"\n";
		

		fw.write(NewEdges);
		fw.close();
		
	}
	
	public void RunModularity(String InputFilePath,String OutputFilePath) throws IOException{
		ModularityOptimizer MO = new ModularityOptimizer(InputFilePath, OutputFilePath, 1, 1.0, 2, 10, 10, 0, 0);
		TreeMap<String,String> cluster = new TreeMap<String,String>();
		FileReader fr = new FileReader(OutputFilePath);
		BufferedReader br = new BufferedReader(fr);
		String s;
		int n=0;
		while((s = br.readLine())!=null){
			String[] clu = s.split(" ");
			String node = String.valueOf(n);

			if (!cluster.containsKey(clu[0]))
				node = String.valueOf(n);
			else
				node = cluster.get(clu[0]) + " " + node;
			cluster.put(clu[0], node);
			n++;
		}
		

		clusters.clear();
		for (String key : cluster.keySet()){
			Clusting c1 = new Clusting();
			c1.SetClustertime(Community_time);
			String value = cluster.get(key);
			String[] tmp = value.split(" ");
			for (String node : tmp){
				Node n1 = new Node();
				n1.SetId(node);
				c1.AddNoed(n1);
			}
			if (c1.cluster.size()>=2)
				clusters.add(c1);
		}
		

		br.close();
		fr.close();
	}

}
