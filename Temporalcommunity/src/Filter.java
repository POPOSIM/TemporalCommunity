import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Filter {
	public static void Result(String str){
		String filepath = "Haggle4-Cam-Imote-dirconn.txt";
		FileWriter output = null;
		try {
			output = new FileWriter(str+"filter.txt");
			try {
				FileReader fr = new FileReader(filepath);
				BufferedReader br = new BufferedReader(fr);
				String s;
				double arrivetime;
				int from,to;
				while ((s = br.readLine()) != null) {
					String[] connectionstring = s.split(" ");
					arrivetime = Double.parseDouble(connectionstring[0]);
					from = Integer.parseInt(connectionstring[2]);
					to = Integer.parseInt(connectionstring[3]);
					if(from == Integer.parseInt(str) || to == Integer.parseInt(str)){
						output.write(s+"\n");
					}
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
