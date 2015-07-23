package TemporalCommunities;
import java.util.HashSet;
import java.util.Set;
public class Cover {
	private Set<String> coverage;
	public Cover(){
		coverage = new HashSet<String>();
	}
	public void AddNode(String s){
		if(!coverage.contains(s))
			coverage.add(s);
	}
	public Set<String> Returncoverage(){
		return coverage;
	}
}
