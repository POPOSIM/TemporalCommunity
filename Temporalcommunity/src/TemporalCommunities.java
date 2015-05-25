import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TemporalCommunities {
	// constrain = 1 - each com's similarity minimum requirements
	private LinkedList<Graph> EachTemporalGraphs;
	private LinkedList<Community> EachCommnities;
	private LinkedList<Cover> EachCommnitiesCover;
	private Connections Nowconnections;
	public static final double cutdown = 600;
	private static final double SimilarityConstrain = 0.3;
	public static final int ConsecutiveConstrain = 4;
	private double CurrentTime;
	private int totalclusters;
	private int totalCommnities;

	public TemporalCommunities() {
		EachTemporalGraphs = new LinkedList<Graph>();
		EachCommnities = new LinkedList<Community>();
		EachCommnitiesCover = new LinkedList<Cover>();
		Nowconnections = new Connections();
		CurrentTime = 0;
		totalclusters = 0;
		totalCommnities = 0;
	}

	private void readdata(List<String> input) {
		double arrivetime;
		Graph p;
		for (String s : input) {
			String[] connectionstring = s.split(" ");
			arrivetime = Double.parseDouble(connectionstring[0]);
			while (CurrentTime + cutdown < arrivetime) {
				p = new Graph();
				for (String s1 : Nowconnections.checkupdown) {
					p.BuildGraph(s1);
				}
				if (!p.GraphMap.isEmpty()) {
					p.SetGraphtime(CurrentTime);
					EachTemporalGraphs.addLast(p);
				}
				CurrentTime = CurrentTime + cutdown;
			}
			if (connectionstring[4].equals("up")) {
				Nowconnections.AddConnection(connectionstring[2] + " "
						+ connectionstring[3]);
			} else if (connectionstring[4].equals("down")) {
				Nowconnections.RemoveConnection(connectionstring[2] + " "
						+ connectionstring[3]);
			}
		}
		p = new Graph();
		for (String s1 : Nowconnections.checkupdown) {
			p.BuildGraph(s1);
			p.SetGraphtime(CurrentTime);
			// System.out.println(s1);
		}
		if (!p.GraphMap.isEmpty())
			EachTemporalGraphs.addLast(p);
	}

	public void communities(List<String> input) {
		System.out.println("Reading current network states to produce static graph...");
		readdata(input);
		System.out.println("Cluster Detection...");
		/*
		FileWriter fr;
		try {
			fr = new FileWriter("01BeforeAggregation.txt");
			for (Graph p : EachTemporalGraphs) {
				Set<Clusting> c; //
				Louvain_Community L = new Louvain_Community(p);
				c = L.Getclusting();
				// Initialization : Place each cluster in its own temporal community.
				for (Clusting cc : c) {
					Community com = new Community();
					com.AddCluster(cc);
					EachCommnities.add(com);
				}
				// Output
				fr.write("Time:" + L.ReturnLouvainTime() + "\n");
				Double d = L.ReturnModQ();
				String str = String.valueOf(d);
				if (str.length() < 4)
					fr.write("\t\tModularity Q:"
							+ str.substring(0, str.indexOf(".") + 2) + "\n");
				else
					fr.write("\t\tModularity Q:"
							+ str.substring(0, str.indexOf(".") + 3) + "\n");
				for (Clusting c1 : c) {
					 fr.write("Time:" + c1.ReturnClustertime());
					fr.write("\t\tcluster ");
					totalclusters++;
					// System.out.println("cluster");
					for (Node n : c1.cluster) {
						fr.write(n.ReturnId() + " ");
						// System.out.print(n.ReturnId() + " ");
					}
					fr.write("\n");
					// System.out.println();
				}
			}
			fr.write("totalclusters " + totalclusters);
			fr.close();
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		for (Graph p : EachTemporalGraphs) {
			Set<Clusting> c;
			Louvain_Community L = new Louvain_Community(p);
			c = L.Getclusting();
			// Initialization : Place each cluster in its own temporal
			// community.
			for (Clusting cc : c) {
				Community com = new Community();
				com.AddCluster(cc);
				EachCommnities.add(com);
			}
			// Output
			for (Clusting c1 : c) {
				totalclusters++;
			}
		}
		totalCommnities = totalclusters;
		
		System.out
				.println("Clusters Aggregation for Communities...AfterAggregation.txt");
		Jaccard();
		// Show results
		AfterAggregation();
		System.out
				.println("Exclude all communities that continues sessions are shorter than k...AfterExclude.txt");
		Exclude();
		AfterExclude();
		// Slimming(str1);
		 System.out.println("The union of all clusters in a temporal community as the cover of the temporal community...Cover.txt");
		ToCover();
		ShowCover();
	}
	
	public boolean Seek(String str1) {
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					if (n.ReturnId().equals(str1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void Slimming(String str2) {
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			boolean bo = false;
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					if (n.ReturnId().equals(str2))
						bo = true;
				}
			}
			if (!bo) {
				i.remove();
				totalCommnities--;
			}
		}
	}

	private void ToCover() {
		List<Clusting> list;
		for (Community com : EachCommnities) {
			Cover co = new Cover();
			list = com.ReturnClusting();
			for (Clusting c : list) {
				for (Node n : c.cluster) {
					co.AddNode(n.ReturnId());
				}
			}
			EachCommnitiesCover.add(co);
		}
	}

	private void ShowCover() {
		FileWriter output;
		try {
			output = new FileWriter("04Cover.txt");
			for (Cover co : EachCommnitiesCover) {
				output.write("Community : ");
				output.write("{ ");
				for (String s : co.Returncoverage()) {
					output.write(s + " ");
				}
				output.write("} ");
				output.write("\n");
			}
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Exclude() {
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			if (!com.Remain()) {
				i.remove();
				totalCommnities--;
			}
		}
	}

	private void AfterExclude() {
		FileWriter output;
		try {
			output = new FileWriter("03AfterExclude.txt");
			for (Community com : EachCommnities) {
				output.write("Community : \n");
				int time = 86400;
				for (Clusting c : com.ReturnClusting()) {
					if (time < c.ReturnClustertime()) {
						output.write("\n");
						while (time < c.ReturnClustertime())
							time = time + 86400;
					}
					output.write("\t\t");
					output.write("Time:" + c.ReturnClustertime() + " ");
					output.write("{ ");
					int[] temp = new int[5000];
					for (Node n : c.cluster) {
						temp[Integer.parseInt(n.ReturnId())] = 1;
					}
					for (int i = 0; i < 5000; i++) {
						if (temp[i] == 1)
							output.write(i + " ");
					}
					output.write("}\n");
				}
				output.write("\n");
			}
			output.write("totalCommnities " + totalCommnities);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void AfterAggregation() {
		FileWriter output;
		try {
			output = new FileWriter("02AfterAggregation.txt");
			for (Community com : EachCommnities) {
				output.write("Community : \n");
				for (Clusting c : com.ReturnClusting()) {
					output.write("\t\t");
					output.write("Time:" + c.ReturnClustertime() + " ");
					output.write("{ ");
					for (Node n : c.cluster) {
						output.write(n.ReturnId() + " ");
					}
					output.write("}\n");
				}
				output.write("\n");
			}
			output.write("totalCommnities " + totalCommnities);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Jaccard() {
		double closest = 1.0;
		double closestTemp = 0.0;
		boolean stable = false;
		boolean roundDone = false;
		Community Ref1 = null;
		Community Ref2 = null;
		while (!stable) {
			stable = true;
			for (Community com1 : this.EachCommnities) {
				for (Community com2 : this.EachCommnities) {
					if (!roundDone) {
						if (com1.equals(com2)) {
							;
						} else {
							closestTemp = 1.0 - SimilarCalculation(com1, com2);
							if (closestTemp < closest
									&& closestTemp <= SimilarityConstrain) {
								closest = closestTemp;
								Ref1 = com1;
								Ref2 = com2;
								stable = false;
								/*if (closest <= 0.000001) {
									roundDone = true;
									;
								}*/
							}
						}
					}
				}
			}
			if (!stable) {
				merge(Ref1, Ref2);
				Ref1 = null;
				Ref2 = null;
				roundDone = false;
				;
				closest = 1.0;
			}
		}

	}

	private void merge(Community com1, Community com2) {
		for (Clusting c : com2.ReturnClusting()) {
			com1.AddCluster(c);
		}
		totalCommnities--;
		EachCommnities.remove(com2);
		// System.out.println("totalCommnities : "+totalCommnities);
	}

	private double SimilarCalculation(Community com1, Community com2) {
		int union = 0;
		int intersection = 0;
		double temp = 0.0;
		int number = 0;
		for (Clusting c1 : com1.ReturnClusting()) {
			for (Clusting c2 : com2.ReturnClusting()) {
				if (c1.ReturnClustertime() == c2.ReturnClustertime()) {
					return 0.0;
				} else {
					number++;
					for (Node n1 : c1.Returnconnect()) {
						intersection++;
					}
					for (Node n2 : c2.Returnconnect()) {
						intersection++;
					}
					for (Node n1 : c1.Returnconnect()) {
						for (Node n2 : c2.Returnconnect()) {
							if (n1.ReturnId().equals(n2.ReturnId())) {
								union++;
							}
						}
					}

					temp = temp
							+ ((double) union / (double) (intersection - union));
					intersection = 0;
					union = 0;
				}
			}
		}
		return temp / number;
	}
	public boolean Find_destination(String message_Des, String message_In){
		LinkedList<Community> EachLocalCommnities =  new LinkedList<Community>();
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			EachLocalCommnities.add(com);
		}
		for (Iterator<Community> i = EachLocalCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			boolean bo = false;
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					if (n.ReturnId().equals(message_Des))
						bo = true;
				}
			}
			if (!bo) {
				i.remove();
				totalCommnities--;
			}
		}
		for (Iterator<Community> i = EachLocalCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					if (n.ReturnId().equals(message_In)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public Set<String> getLocalCommunity(String message_Des) {
		Set<String> s = new HashSet<String>();
		LinkedList<Community> EachLocalCommnities =  new LinkedList<Community>();
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			EachLocalCommnities.add(com);
		}
		for (Iterator<Community> i = EachLocalCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			boolean bo = false;
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					if (n.ReturnId().equals(message_Des))
						bo = true;
				}
			}
			if (!bo) {
				i.remove();
				totalCommnities--;
			}
		}
		for (Iterator<Community> i = EachLocalCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			for (Clusting c : com.ReturnClusting()) {
				for (Node n : c.Returnconnect()) {
					s.add(n.ReturnId());
				}
			}
		}
		return s;
	}
}
