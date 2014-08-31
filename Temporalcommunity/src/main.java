import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TemporalCommunities t = new TemporalCommunities();
		
		List<String> mylist = new LinkedList<String>();

		try {
			String filepath = "contact.txt";
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				mylist.add(s);
			}
			fr.close();
			br.close();
		} catch (Exception e) {
			System.out.println("Parser Error:");
			e.printStackTrace();
		}
		
		//mylist.add("0.0 conn 0 3 up");
		//mylist.add("0.0 conn 1 2 up");
		//mylist.add("2000.0 conn 0 3 down");
		//mylist.add("2000.0 conn 1 2 down");
		//t.communities(Message ID, Host contact history);
		t.communities(mylist);
		//t.Seek(Device ID) return if in t.communities
		//System.out.println(t.Find_destination("0", "0"));
		//System.out.println(t.Find_destination("0", "3"));
		//System.out.println(t.Find_destination("2", "1"));
		//System.out.println(t.Find_destination("2", "4"));
		//t.getLocalCommunity() return community set
		//System.out.println(t.getLocalCommunity("0"));
		//System.out.println(t.getLocalCommunity("2"));
		//Filter.Result("17");
		//Duration.Result("17");
	}
}
