import java.util.ArrayList;
import java.util.List;

public class Community {
	private List<Clusting> SimilarCluster;
	public Community(){
		SimilarCluster = new ArrayList<Clusting>();
	}
	public void AddCluster(Clusting c){
		if(SimilarCluster.isEmpty()){
			SimilarCluster.add(c);
		}
		else{
			int index = 0;
			for(Clusting c1:SimilarCluster){
				if(c1.ReturnClustertime() > c.ReturnClustertime()){
					break;
				}
				index++;
			}
			SimilarCluster.add(index, c);
		}
	}
	public List<Clusting> ReturnClusting(){
		return this.SimilarCluster;
	}
	public boolean Remain(){
		int temp = 1;
		Clusting now = null;
		for(Clusting c : this.SimilarCluster){
			if(now == null){
				now = c;
			}
			else{
				if(c.ReturnClustertime() == now.ReturnClustertime()+ TemporalCommunities.cutdown){
					temp++;
					now = c;
				}
				else{
					temp = 1;
					now = c;
				}
			}
			if(temp >= TemporalCommunities.ConsecutiveConstrain)
				return true;
				
		}
		return false;
	}
}
