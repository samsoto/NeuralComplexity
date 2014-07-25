package neuralcomplexity;

import JavaMI.MutualInformation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class NeuralComplexity {
  
    public static void main(String[] args) throws Exception {

        //Note: Make sure these numbers match with the data that
        //will be pulled from a text file.
        final int NUM_NODES = 18;
        final int NUM_TIME = 90;

        //Ever neuron is originally a continuous values from 0 -> 1
        //however these values will be descretized inorder to use JavaMI
        //Also note the base cannot go much higher without causing an
        //error to be throw later. This is because when comparing subsets
        //you must view the whole subset as a single number. Having
        //just a few neurons can result in very large numbers that JavaMI
        //will not except.
        int base = 3;

        
        //Array storing an agents brain activity over time
        int[][] neuronStateHistory = new int[NUM_NODES][NUM_TIME];

        //Read in, from a file, an agent brains activity over time
        BufferedReader br = new BufferedReader(new FileReader("brainActivity.txt"));
        for (int time = 0; time < NUM_TIME; time++) {
            char[] line = br.readLine().toCharArray();
            for (int node = 0; node < NUM_NODES; node++) {
                neuronStateHistory[node][time]= Integer.parseInt(""+line[node]);
            }
        }

        //Neural complexity requires the average mutual inforamtion of all
        //subsets of size k.
        List<List<Double>> miForSubsetsOfSizeK = new ArrayList<>();
        for (int i = 1; i <= NUM_NODES / 2; i++) {
            miForSubsetsOfSizeK.add(new ArrayList<Double>());
        }

        //go through all numbers up to 2^N inorder to figure out all
        //subsets k <= N/2
        for (int i = 1; i < Math.pow(2, NUM_NODES); i++) {

            int k = Integer.bitCount(i);

            if (k <= NUM_NODES / 2) {
                List<int[]> subSizeK = new ArrayList<>();
                List<int[]> subSizeKComp = new ArrayList<>();

                for (int j = 0; j < neuronStateHistory.length; j++) {
                    if (((int) Math.pow(2, j) & i) > 0) {
                        subSizeK.add(neuronStateHistory[j]);
                    } else {
                        subSizeKComp.add(neuronStateHistory[j]);
                    }
                }

                double[] subset = new double[NUM_TIME];
                for (int q = 0; q < NUM_TIME; q++) {
                    int value = 0;
                    for (int j = 0; j < subSizeK.size(); j++) {
                        value += (int) Math.pow(base, j) * subSizeK.get(j)[q];
                    }
                    subset[q] = value;
                }

                double[] subsetComp = new double[NUM_TIME];
                for (int q = 0; q < NUM_TIME; q++) {
                    int value = 0;
                    for (int j = 0; j < subSizeKComp.size(); j++) {
                        value += (int) Math.pow(base, j) * subSizeKComp.get(j)[q];
                    }
                    subsetComp[q] = value;
                }

                double mi = MutualInformation.calculateMutualInformation(subset, subsetComp);
                miForSubsetsOfSizeK.get(k - 1).add(mi);

            }
        }

        int k = 1;
        for (List<Double> kList : miForSubsetsOfSizeK) {
            double avg = 0;
            for (Double mi : kList) {
                avg += mi;
            }
            avg /= kList.size();
        }
        
        double neuralComplexity = 0;
        for (int i = 0; i < miForSubsetsOfSizeK.size(); i++) {
            double avg = 0;
            for (Double d : miForSubsetsOfSizeK.get(i)) {
                avg += d;
            }
            avg /= miForSubsetsOfSizeK.get(i).size();
            neuralComplexity += avg;
        }

        System.out.println("Neural Complexity: " + neuralComplexity);
    }

}
