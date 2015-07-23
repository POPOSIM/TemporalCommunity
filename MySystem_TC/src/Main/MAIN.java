package Main;

import TemporalCommunities.TemporalCommunities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class MAIN {
/***************************************************************************************************
* Change the "BernoulliProcess" class if you changed the input trace file before this program begin. *
****************************************************************************************************/
	
	/* *
	 * parameter about input file .
	 * */
	// setting your file name of real trace in Trace/Real/'TraceName'/'TraceFile'
	
	private static String TraceFile = "infocom2005";
	
	
	/* *
	 * parameter about output file .
	 * */
	
	
	
	/* *
	 * the parameter will be increment from lower bound to upper bound.
	 * */
	//TimeStep
	
	private static int TimpeStep_LowerBound = 600;//3600;
	
	//SimilarityConstrain
	
	private static double SimilarityConstrain_LowerBound = 0.3;//0.7;
	
	//ConsecutiveConstrain
	
	private static int ConsecutiveConstrain_LowerBound = 4;//2;

	
	//File path (Fixed)
	private static String RealTracePath = "Trace/Real/"+TraceFile+".txt";
	

	
	public static void main(String[] args) throws Exception {
		
	
		
		int TimeStep = TimpeStep_LowerBound;
		double SimilarityConstrain = SimilarityConstrain_LowerBound;
		int ConsecutiveConstrain = ConsecutiveConstrain_LowerBound;
		List<String> RealTraceContactList=null;
		
		
		
		
		
		//Reading real trace into list.
		RealTraceContactList = new LinkedList<String>();
		try {
			FileReader fr = new FileReader(RealTracePath);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				RealTraceContactList.add(s);
			}
			fr.close();
			br.close();
		} catch (Exception e) {
			System.out.println("Parser Error:");
			e.printStackTrace();
		}
		
		//------------------loop begin-------------------------
		
	//	for (TimeStep=TimpeStep_LowerBound; TimeStep<=TimeStep_UpperBound; TimeStep=TimeStep+TimeStep_Increment){
		//	for (long Int_Inc=(long)(SimilarityConstrain_LowerBound*10); Int_Inc<=(long)(SimilarityConstrain_UpperBound*10); Int_Inc+=(long)(SimilarityConstrain_Increment*10)){
			//	SimilarityConstrain = Int_Inc*0.1;
				//for (ConsecutiveConstrain=ConsecutiveConstrain_LowerBound; ConsecutiveConstrain<=ConsecutiveConstrain_UpperBound; ConsecutiveConstrain=ConsecutiveConstrain+ConsecutiveConstrain_Increment){
		TemporalCommunities RealTraceTemporal = new TemporalCommunities(TimeStep,SimilarityConstrain,ConsecutiveConstrain,"Real");
		RealTraceTemporal.communities(RealTraceContactList);
					
					
					
					
					
			//	}//ConsecutiveConstrain
		//	}//SimilarityConstrain
	//	}//TimeStep
	}//Main
	
	
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //
				InputStream inStream = new FileInputStream(oldPath);//
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch(Exception e) {
			System.out.println("copy files error");
			e.printStackTrace();
		}
	}
}
