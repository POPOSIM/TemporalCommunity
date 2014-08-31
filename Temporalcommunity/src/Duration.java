import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Duration {
	private static int cut = 86400;
	public static void Result(String str){
		String filepath = str+"filter.txt";
		FileWriter output = null;
		int [] stime = new int[100];
		int [] dtime = new int[100];
		try {
			output = new FileWriter(str+"duration.txt");
			output.write("Node "+str+"'s connection interval");
			try {
				FileReader fr = new FileReader(filepath);
				BufferedReader br = new BufferedReader(fr);
				String s;
				int arrivetime;
				int from,to;
				while ((s = br.readLine()) != null) {
					String[] connectionstring = s.split(" ");
					arrivetime = Integer.parseInt(connectionstring[0]);
					from = Integer.parseInt(connectionstring[2]);
					to = Integer.parseInt(connectionstring[3]);
					if(arrivetime < cut){
						if(from != Integer.parseInt(str)){
							if(connectionstring[4].equals("up")){
								stime[from] = arrivetime;
							}
							else if(connectionstring[4].equals("down")){
								dtime[from] = dtime[from] + arrivetime - stime[from];
								stime[from] = 0;
							}
						}
						else if(to != Integer.parseInt(str)){
							if(connectionstring[4].equals("up")){
								stime[to] = arrivetime;
							}
							else if(connectionstring[4].equals("down")){
								dtime[to] = dtime[to] + arrivetime - stime[to];
								stime[to] = 0;
							}
						}
					}
					else{
						output.write("\nTIME\t"+cut+"\n");
						for(int i = 0;i<100;i++){
							if(dtime[i] > 0)
								output.write(i+"\t"+dtime[i]+"\n");
						}
						for(int i = 0;i<100;i++){
							dtime[i] = 0;
						}
						cut = cut +86400;
						
						if(from != Integer.parseInt(str)){
							if(connectionstring[4].equals("up")){
								stime[from] = arrivetime;
							}
							else if(connectionstring[4].equals("down")){
								dtime[from] = dtime[from] + arrivetime - stime[from];
								stime[from] = 0;
							}
						}
						else if(to != Integer.parseInt(str)){
							if(connectionstring[4].equals("up")){
								stime[to] = arrivetime;
							}
							else if(connectionstring[4].equals("down")){
								dtime[to] = dtime[to] + arrivetime - stime[to];
								stime[to] = 0;
							}
						}
					}
				}
				output.write("\nTIME\t"+cut+"\n");
				for(int i = 0;i<100;i++){
					if(dtime[i] != 0)
						output.write(i+"\t"+dtime[i]+"\n");
				}
				fr.close();
				br.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
			}
			output.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
