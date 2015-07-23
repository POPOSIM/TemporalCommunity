package TemporalCommunities;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TemporalCommunities {
	// constrain = 1 - each com's similarity minimum requirements
	private LinkedList<Graph> EachTemporalGraphs=null;
	private LinkedList<Community> EachCommnities=null;
	private LinkedList<Cover> EachCommnitiesCover=null;
	private Connections Nowconnections=null;
	public static int TimeStep=0;   //= 1800;
	private static double SimilarityConstrain=0;  //= 0.7;
	public static int ConsecutiveConstrain=0;  //= 2;
	private static String CommunitiesType; //Real or Synthetic
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
	public TemporalCommunities(int TimeStep, double SimilarityConstrain, int ConsecutiveConstrain,String CommunitiesType) {
		if (EachTemporalGraphs==null)
			EachTemporalGraphs = new LinkedList<Graph>();
		else
			EachTemporalGraphs.clear();
		
		if (EachCommnities==null)
			EachCommnities = new LinkedList<Community>();
		else
			EachCommnities.clear();
		
		if (EachCommnitiesCover==null)
			EachCommnitiesCover = new LinkedList<Cover>();
		else
			EachCommnitiesCover.clear();
		
		if(Nowconnections==null)
			Nowconnections = new Connections();
		else
			Nowconnections.RemoveAllConnection();
		CurrentTime = 0;
		totalclusters = 0;
		totalCommnities = 0;
		this.TimeStep = TimeStep;
		this.SimilarityConstrain = SimilarityConstrain;
		this.ConsecutiveConstrain = ConsecutiveConstrain;
		this.CommunitiesType = CommunitiesType;
	}
	private void readdata1(List<String> input) {
		double arrivetime;
		Graph p;
		for (String s : input) {//Contact List
			String[] connectionstring = s.split(" ");//Real
			//String[] connectionstring = s.split("	");//Active Network
			arrivetime = Double.parseDouble(connectionstring[0]);
			//瘥�00蝘銝�撐graph,雿���鈕napshot甇斤���嗆connection up����dges
			//�冽迨銋��銝蝺�銝��亦���
			while (CurrentTime + TimeStep < arrivetime) {
				p = new Graph();
				for (String edge : Nowconnections.ConnUpEdges) {
					p.BuildGraph(edge);
				}
				if (!p.EdgesSet.isEmpty()) {
					p.SetGraphtime(CurrentTime);
					EachTemporalGraphs.addLast(p);
				}
				CurrentTime = CurrentTime + TimeStep;
			}
			if (connectionstring[4].equals("up")) {
				Nowconnections.AddConnection(connectionstring[2] + " "+ connectionstring[3]);
			} else if (connectionstring[4].equals("down")) {
				Nowconnections.RemoveConnection(connectionstring[2] + " "+ connectionstring[3]);
			}
		}
		p = new Graph();
		for (String s1 : Nowconnections.ConnUpEdges) {
			p.BuildGraph(s1);
			p.SetGraphtime(CurrentTime);
			// System.out.println(s1);
		}
		if (!p.EdgesSet.isEmpty())
			EachTemporalGraphs.addLast(p);
	}
	private void readdata2(List<String> input) {
		double arrivetime;
		Graph p;
		for (String s : input) {//Contact List
			//String[] connectionstring = s.split(" ");//Real
			String[] connectionstring = s.split("	");//Active Network
			arrivetime = Double.parseDouble(connectionstring[0]);
			//瘥�00蝘銝�撐graph,雿���鈕napshot甇斤���嗆connection up����dges
			//�冽迨銋��銝蝺�銝��亦���
			while (CurrentTime + TimeStep < arrivetime) {
				p = new Graph();
				for (String edge : Nowconnections.ConnUpEdges) {
					p.BuildGraph(edge);
				}
				if (!p.EdgesSet.isEmpty()) {
					p.SetGraphtime(CurrentTime);
					EachTemporalGraphs.addLast(p);
				}
				CurrentTime = CurrentTime + TimeStep;
			}
			if (connectionstring[4].equals("up")) {
				Nowconnections.AddConnection(connectionstring[2] + " "+ connectionstring[3]);
			} else if (connectionstring[4].equals("down")) {
				Nowconnections.RemoveConnection(connectionstring[2] + " "+ connectionstring[3]);
			}
		}
		p = new Graph();
		for (String s1 : Nowconnections.ConnUpEdges) {
			p.BuildGraph(s1);
			p.SetGraphtime(CurrentTime);
			// System.out.println(s1);
		}
		if (!p.EdgesSet.isEmpty())
			EachTemporalGraphs.addLast(p);
	}
	public void communities(List<String> input) throws IOException {
		System.out.println("Reading current network states to produce static graph...");
		try{
			readdata1(input);//Check Done OK! 
		}catch(NumberFormatException e){
			readdata2(input);//Check Done OK! 
		}
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
		for (Graph p : EachTemporalGraphs) {//�瘥�00蝘��甈∠���
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
		Aggregation_BeiAnn();
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
			output = new FileWriter("Temp/TemporalCommunitiesTemp/"+this.CommunitiesType+"/04Cover.txt");
			for (Cover co : EachCommnitiesCover) {
				//output.write("Community : ");
				//output.write("{ ");
				for (String s : co.Returncoverage()) {
					output.write(s + " ");
				}
				//output.write("} ");
				output.write("\n");
			}
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Exclude() {
		// �芯��cluster����箇�活��=ConsecutiveConstrain�ommunity
		for (Iterator<Community> i = EachCommnities.iterator(); i.hasNext();) {
			Community com = i.next();
			if (!com.isSession()) {
				i.remove();
				totalCommnities--;
			}
		}
	}

	private void AfterExclude() {
		FileWriter output;
		try {
			output = new FileWriter("Temp/TemporalCommunitiesTemp/"+this.CommunitiesType+"/03AfterExclude.txt");
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
			output = new FileWriter("Temp/TemporalCommunitiesTemp/"+this.CommunitiesType+"/02AfterAggregation.txt");
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

	private void Aggregation_original() {
		/*
		Community :            
		    Time:0.0 { 3 4 }

        Community :              <==community
		    Time:0.0 { 1 2 }             <==cluster
		    Time:600.0 { 2 1 }             <==cluster
		    Time:1800.0 { 2 1 }             <==cluster
		    Time:2400.0 { 1 2 }             <==cluster
		 * */
		// 1. 銝��憪��ommunity�折�芣��芸楛銝��Cluster
		// 2. 撠��ommunity (�桐�cluster), �遙�妾ommunity(�桐�cluster)閮�jaccard Avg.
		// 3. �曉�嗡葉 ��隡潛��拙�Community(�桐�cluster) , �蔥���ommunity(�拙�cluster)
		// ���琿�銴�,3甇仿� �游瘝��潛��蔥��,�誨銵牢ommunities�蝛拙����
		double closest = 1.0;
		double Jacc = 0.0;
		boolean stable = false;
		boolean roundDone = false;
		Community SimCom1 = null;
		Community SimCom2 = null;
		while (!stable) {
			stable = true;
			for (Community com1 : this.EachCommnities) {
				for (Community com2 : this.EachCommnities) {
					if (!roundDone) {
						if (!com1.equals(com2)) {
							Jacc = 1.0 - SimilarCalculation(com1, com2);
							if (Jacc < closest && Jacc <= SimilarityConstrain) {
								closest = Jacc;
								SimCom1 = com1;
								SimCom2 = com2;
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
				mergeCommunities_BeiAnn(SimCom1, SimCom2);
				SimCom1 = null;
				SimCom2 = null;
				roundDone = false;
				closest = 1.0;
			}
		}

	}
	
	private void mergeCommunities_original(Community com1, Community com2) {

	    for (Clusting c : com2.ReturnClusting()) {
			com1.AddCluster(c);
		}
		totalCommnities--;
		EachCommnities.remove(com2);
	
	}
	
	private void Aggregation_BeiAnn() {
		/*
		Community :            
		    Time:0.0 { 3 4 }

        Community :              <==community
		    Time:0.0 { 1 2 }             <==cluster
		    Time:600.0 { 2 1 }             <==cluster
		    Time:1800.0 { 2 1 }             <==cluster
		    Time:2400.0 { 1 2 }             <==cluster
		 * */
		//System.out.println(this.EachCommnities.size());
        ArrayList<Community> removelist = new ArrayList<Community>();
		double [][] table = new double [this.EachCommnities.size()][this.EachCommnities.size()];		
		double Jacc = 0.0;
		boolean stable = false;
		boolean instable = false;
		Community SimCom1 = null;
		Community SimCom2 = null;
		//System.out.println(this.EachCommnities.size());
		while (!stable) {
			stable = true;
			for (int i = 0; i<this.EachCommnities.size();i++) {
				for (int j = i + 1 ; j<this.EachCommnities.size();j++) {																	
					Jacc = 1.0 - SimilarCalculation(this.EachCommnities.get(i), this.EachCommnities.get(j));                   					
					table[i][j]= Jacc;					
				}					
			}
			instable = false;
			while(!instable){
				instable = true;
				double minValue = SimilarityConstrain;
				int i; 
				int j = 0;
				int ii=0;
				int jj=0;
				for (i = 0;i<this.EachCommnities.size();i++){
					for (j = i + 1 ; j<this.EachCommnities.size() ; j++){						
						if(table[i][j]<minValue){
							minValue = table[i][j];
							instable = false;
							ii = i;
							jj = j;
						}
					}
				}
			    if(minValue!=SimilarityConstrain&& ii != jj ){
			    stable = false;
				//System.out.println(ii+" "+jj);
				SimCom1 = this.EachCommnities.get(ii);
				SimCom2 = this.EachCommnities.get(jj);
				mergeCommunities_BeiAnn(SimCom1, SimCom2);
				removelist.add(SimCom1);					
				for (int column = 0 ; column<this.EachCommnities.size() ; column++){
					table[ii][column]=2;
					table[jj][column]=2;
					table[column][jj]=2;
					table[column][ii]=2;
				}
				}			   

			}
			for(int i = removelist.size()-1;i>=0;i--){
				Community g = removelist.get(i);
				//System.out.println(g);
				EachCommnities.remove(g);
				totalCommnities--;
			}	
			removelist.clear();
			//System.out.println("out");
		}
		
	}
	private void mergeCommunities_BeiAnn(Community com1, Community com2) {
		for (Clusting c : com1.ReturnClusting()) {
			com2.AddCluster(c);
		}		

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
