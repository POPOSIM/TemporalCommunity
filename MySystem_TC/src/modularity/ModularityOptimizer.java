package modularity;


/**
 * ModularityOptimizer
 *
 * @author Ludo Waltman
 * @author Nees Jan van Eck
 * @version 1.2.0, 05/14/14
 * 
 * 
 * 
Using command-line arguments

The Modularity Optimizer can also be run using command-line arguments. The following syntax must be used:

java -jar ModularityOptimizer.jar input_file output_file modularity_function resolution_parameter optimization_algorithm n_random_starts n_iterations random_seed print_output
The command-line arguments are defined as follows:
1.input_file	Name of the input file
2.output_file	Name of the output file
3.modularity_function	Modularity function (1 = standard; 2 = alternative)
4.resolution_parameter	Value of the resolution parameter
5.optimization_algorithm	Algorithm for modularity optimization (1 = original Louvain algorithm; 2 = Louvain algorithm with multilevel refinement; 3 = SLM algorithm)
6.n_random_starts	Number of random starts
7.n_iterations	Number of iterations per random start
8.random_seed	Seed of the random number generator
9.print_output	Whether or not to print output to the console (0 = no; 1 = yes)
As an example, the Modularity Optimizer may be run as follows:
                                       1            2         3  4  5  6  7 8 9
java -jar ModularityOptimizer.jar network.txt communities.txt 1 1.0 2 10 10 0 0
This will cause the Modularity Optimizer to read a network from the network.txt input file, to carry out standard modularity-based community detection
(i.e., standard modularity function with resolution parameter equal to 1.0) 
by performing 10 runs of 10 iterations of the SLM algorithm, and to write the resulting community structure to the communities.txt output file. 
The random number generator will be initialized with a seed of 0, and no output will be printed to the console.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ModularityOptimizer
{
	
	private static boolean printOutput, update;
	 private static Console console;
     private static double modularity, maxModularity, resolution, resolution2;
     private static int algorithm, i, j, modularityFunction, nClusters, nIterations, nRandomStarts;
     private static int[] cluster;
     private static long beginTime, endTime, randomSeed;
    private static Network network;
    private static Random random;
    private static String inputFileName;
    private static String outputFileName;
     
	public ModularityOptimizer(String inputFileName,String outputFileName,int modularityFunction,double resolution,int algorithm,int nRandomStarts,int nIterations,long randomSeed,int printOutput ) throws IOException{
		this.inputFileName = inputFileName;
		 this.outputFileName = outputFileName;
         this.modularityFunction = modularityFunction;
         this.resolution = resolution;
         this.algorithm = algorithm;
         this.nRandomStarts = nRandomStarts;
         this.nIterations = nIterations;
         this.randomSeed = randomSeed;
         this.printOutput = (printOutput > 0);
         MAIN();
	}
	
	public void MAIN() throws IOException{
		 network = readInputFile(inputFileName, modularityFunction);

	        if (printOutput)
	        {
	            System.out.format("Number of nodes: %d%n", network.getNNodes());
	            System.out.format("Number of edges: %d%n", network.getNEdges() / 2);
	            System.out.println();
	            System.out.println("Running " + ((algorithm == 1) ? "Louvain algorithm" : ((algorithm == 2) ? "Louvain algorithm with multilevel refinement" : "smart local moving algorithm")) + "...");
	            System.out.println();
	        }

	        resolution2 = ((modularityFunction == 1) ? (resolution / network.getTotalEdgeWeight()) : resolution);

	        beginTime = System.currentTimeMillis();
	        cluster = null;
	        nClusters = -1;
	        maxModularity = Double.NEGATIVE_INFINITY;
	        random = new Random(randomSeed);
	        for (i = 0; i < nRandomStarts; i++)
	        {
	            if (printOutput && (nRandomStarts > 1))
	                System.out.format("Random start: %d%n", i + 1);

	            network.initSingletonClusters();

	            j = 0;
	            update = true;
	            do
	            {
	                if (printOutput && (nIterations > 1))
	                    System.out.format("Iteration: %d%n", j + 1);

	                if (algorithm == 1)
	                    update = network.runLouvainAlgorithm(resolution2, random);
	                else if (algorithm == 2)
	                    update = network.runLouvainAlgorithmWithMultilevelRefinement(resolution2, random);
	                else if (algorithm == 3)
	                    network.runSmartLocalMovingAlgorithm(resolution2, random);
	                j++;

	                modularity = network.calcQualityFunction(resolution2);

	                if (printOutput && (nIterations > 1))
	                    System.out.format("Modularity: %.4f%n", modularity);
	            }
	            while ((j < nIterations) && update);

	            if (modularity > maxModularity)
	            {
	                network.orderClustersByNNodes();
	                cluster = network.getClusters();
	                nClusters = network.getNClusters();
	                maxModularity = modularity;
	            }

	            if (printOutput && (nRandomStarts > 1))
	            {
	                if (nIterations == 1)
	                    System.out.format("Modularity: %.4f%n", modularity);
	                System.out.println();
	            }
	        }
	        endTime = System.currentTimeMillis();

	        if (printOutput)
	        {
	            if (nRandomStarts == 1)
	            {
	                if (nIterations > 1)
	                    System.out.println();
	                System.out.format("Modularity: %.4f%n", maxModularity);
	            }
	            else
	                System.out.format("Maximum modularity in %d random starts: %.4f%n", nRandomStarts, maxModularity);
	            System.out.format("Number of communities: %d%n", nClusters);
	            System.out.format("Elapsed time: %d seconds%n", Math.round((endTime - beginTime) / 1000.0));
	            System.out.println();
	            System.out.println("Writing output file...");
	            System.out.println();
	        }

	        writeOutputFile(outputFileName, cluster);
	}
	
   

    private static Network readInputFile(String fileName, int modularityFunction) throws IOException
    {
        BufferedReader bufferedReader;
        double[] edgeWeight1, edgeWeight2, nodeWeight;
        int i, j, nEdges, nLines, nNodes;
        int[] firstNeighborIndex, neighbor, nNeighbors, node1, node2;
        Network network;
        String[] splittedLine;

        bufferedReader = new BufferedReader(new FileReader(fileName));

        nLines = 0;
        while (bufferedReader.readLine() != null)
            nLines++;

        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(fileName));

        node1 = new int[nLines];
        node2 = new int[nLines];
        edgeWeight1 = new double[nLines];
        i = -1;
        for (j = 0; j < nLines; j++)
        {
            //splittedLine = bufferedReader.readLine().split("\t");
        	splittedLine = bufferedReader.readLine().split(" ");
            node1[j] = Integer.parseInt(splittedLine[0]);
            if (node1[j] > i)
                i = node1[j];
            node2[j] = Integer.parseInt(splittedLine[1]);
            if (node2[j] > i)
                i = node2[j];
            
            if (node1[j]>node2[j] ){
            	int tmp = node1[j];
            	node1[j]=node2[j];
            	node2[j]=tmp; 
            }
            
            edgeWeight1[j] = (splittedLine.length > 2) ? Double.parseDouble(splittedLine[2]) : 1;
        }
        nNodes = i + 1;

        bufferedReader.close();

        nNeighbors = new int[nNodes];
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                nNeighbors[node1[i]]++;
                nNeighbors[node2[i]]++;
            }

        firstNeighborIndex = new int[nNodes + 1];
        nEdges = 0;
        for (i = 0; i < nNodes; i++)
        {
            firstNeighborIndex[i] = nEdges;
            nEdges += nNeighbors[i];
        }
        firstNeighborIndex[nNodes] = nEdges;

        neighbor = new int[nEdges];
        edgeWeight2 = new double[nEdges];
        Arrays.fill(nNeighbors, 0);
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                j = firstNeighborIndex[node1[i]] + nNeighbors[node1[i]];
                neighbor[j] = node2[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node1[i]]++;
                j = firstNeighborIndex[node2[i]] + nNeighbors[node2[i]];
                neighbor[j] = node1[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node2[i]]++;
            }

        if (modularityFunction == 1)
        {
            nodeWeight = new double[nNodes];
            for (i = 0; i < nEdges; i++)
                nodeWeight[neighbor[i]] += edgeWeight2[i];
            network = new Network(nNodes, firstNeighborIndex, neighbor, edgeWeight2, nodeWeight);
        }
        else
            network = new Network(nNodes, firstNeighborIndex, neighbor, edgeWeight2);

        return network;
    }

    private static void writeOutputFile(String fileName, int[] cluster) throws IOException
    {
        BufferedWriter bufferedWriter;
        int i;

        bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        try{
	        for (i = 0; i < cluster.length; i++)
	        {
	            bufferedWriter.write(Integer.toString(cluster[i]));
	            bufferedWriter.newLine();
	        }
        }catch(NullPointerException e){
        	//System.out.println(cluster);
        	bufferedWriter.close();
        }
        bufferedWriter.close();
    }
}
