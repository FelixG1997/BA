package MAIN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * 
 * @author FelixGreuling
 *
 */
public class Main {	
	
	@SuppressWarnings("unused")
	/**
	 * Main method
	 * decides, if 1D or 2OA case needs to be calc or starts testcases
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String inputFile;
		double time1D = Double.MAX_VALUE;
		double timePrio = Double.MAX_VALUE;
		double timeBruteForce = Double.MAX_VALUE;
		Main main = new Main();
		if(Constants.V_I_MAX >= Constants.V_PURSUER || Constants.N <= 0) {
			System.out.println("Invalid Constants Config. Check v_iMax<vMax and n>0!");
		}	
		
		// 1D-case
		if (Constants.IS_1D) {
			if(!Constants.TESTING_ENABLED) {
				// generate 1D Input
				if (Constants.GENERATE_NEW_INPUT) {
					GenerateInput input = new GenerateInput();
					inputFile = input.generateFile();
				} else {
					inputFile = Constants.INPUT_FILE_1D;
				}
				time1D = main.calc1D(inputFile);
				if(Constants.USE_BRUTE_FORCE) {
					System.out.println("1D-Algorithm-time: " + time1D);
					System.out.println("Brute-Force-time: " + main.calcBruteForce(inputFile));
				} else
					System.out.println("1D-Algorithm-time: " + time1D);
			} else {
				main.test1DRuntime();
			}
			
		}

		// 2-orth.-axis-case
		else {
			String file = "";
			if(!Constants.TESTING_ENABLED) {
				if (Constants.GENERATE_NEW_INPUT) {
					GenerateInput input = new GenerateInput();
					inputFile = input.generateFile();
				} else {
					inputFile = Constants.INPUT_FILE_2OA;
				}
				// weights for calculating the priority of a target
				// velocity, distance towards or away, distance position - intersection, 
				// distance position - origin, distance position - target
				timePrio = main.calcPrio(inputFile, Constants.WEIGHTS[0], Constants.WEIGHTS[1],Constants.WEIGHTS[2]);
				System.out.println("Priority-Algorithm-time: " + timePrio);
				
				if (Constants.USE_BRUTE_FORCE) {
					timeBruteForce = main.calcBruteForce(inputFile);
					System.out.println("Optimal time: " + timeBruteForce);
	
					// print
					if((timePrio+0.1 <= timeBruteForce || timePrio-0.1 <= timeBruteForce)  && timePrio+0.2 > timeBruteForce) { // small rounding issues
						System.out.println("--> 2OA-algorithm found an optimal solution");
					} else if	(timePrio > timeBruteForce) {
						System.out.println("--> 2OA-algorithm didn't find an optimal solution");
					} else
						System.out.println("--> Something went wrong! - 2OA-algorithm-time < Brute-Force-time");
				}
			} else {
				System.out.println("Testing started..");
				if(Constants.TEST_MULTIPLE_PRIO_INSTANCES) {
					file = "Weights_0.txt";
					main.testPrioWithMultipleInstances(file);
				} else if(Constants.TEST_MULTIPLE_BF_INSTANCES) {
					file = "BF_Results.txt";
					main.testBFWithMultipleInstances(file);
				} else if(Constants.TEST_PRIO_QUALITY) {
					file = "Quality.txt";
					main.testPrioQuality(file);
				} else if(Constants.TEST_WEIGHTS) {
				// generate 2D Input
					if (Constants.GENERATE_NEW_INPUT) {
						GenerateInput input = new GenerateInput();
						inputFile = input.generateFile();
					} else {
						inputFile = Constants.INPUT_FILE_2OA;
					}
					// Testing different weights					
					if(Constants.USE_BRUTE_FORCE) {
						file = "Weights_1.txt";
						main.testWeightsWithBruteForce(inputFile, file);
						main.calcWeightAverage(file);
					} else {
						file = "Weights_2.txt";
						main.testWeightsWithoutBruteForce(inputFile, file);
						main.calcWeightAverage(file);
					}
				}
				// Consider a specific Sequence
				if (Constants.TEST_SPECIFIC_SEQUENCE) {
					inputFile = "Seq.txt";
					double timeSequence = main.calcSequence(inputFile);
					System.out.println(timeSequence);
				}
			}	
		}
		System.out.println("Finished!");
	}

	/**
	 * method to call SolveWithPriorityApproach class an print results
	 * @param inputFile
	 * @param a weight a
	 * @param b weight b
	 * @param c weight c
	 * @return time of the tour
	 * @throws IOException if inputFile is invalid
	 */
	private double calcPrio(String inputFile, int a, int b, int c) throws IOException {
		
		SolveWithPriorityApproach solvePrio = new SolveWithPriorityApproach(inputFile);
		solvePrio.setPriorityWeights(a, b, c);
		ArrayList<Target> OUTPUT = solvePrio.prioApproach();
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("Prio Approach Results:");
			for(Target t : OUTPUT) {
				System.out.println(t);
			}
		}
		return OUTPUT.get(OUTPUT.size() - 1).getInterceptingTime();
	}

	/**
	 * method to call SolveOneDim class an print results
	 * @param inputFile
	 * @return time of the tour
	 * @throws IOException if inputFile is invalid
	 */
	private double calc1D(String inputFile) throws IOException {
		SolveOneDim solve1D = new SolveOneDim(inputFile);
		ArrayList<Target> OUTPUT = solve1D.helvigApproach();
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("\n1D Results:");
			for(Target t : OUTPUT) {
				System.out.println(t);
			}
		}
		return OUTPUT.get(OUTPUT.size() - 1).getInterceptingTime();
	}

	private double calcBruteForce(String inputFile) throws IOException {
		SolveWithBruteForce bruteForce = new SolveWithBruteForce(inputFile);
		ArrayList<Target> OUTPUT = bruteForce.solveWithBF();
		if (!OUTPUT.isEmpty()) {
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("\nBrute Force Results:");
				for(Target t : OUTPUT) {
					System.out.println(t);
				}
			}
		}
		return OUTPUT.get(OUTPUT.size() - 1).getInterceptingTime();
	}

	/**
	 * method to call CalcSequence class an print results
	 * @param inputFile
	 * @return time of the tour
	 * @throws IOException if inputFile is invalid
	 */
	private double calcSequence(String inputFile) throws IOException {
		CalcSequence seq = new CalcSequence(inputFile);
		ArrayList<Target> OUTPUT = seq.getPathTime();
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("\nSpecific Sequence:");
			for(Target t : OUTPUT) {
				System.out.println(t);
			}
		}
		return OUTPUT.get(OUTPUT.size() - 1).getInterceptingTime();
	}

	// -------------------------------------------------------
	// --------------------TESTING AREA ----------------------
	// -------------------------------------------------------
	
	/**
	 * testmethod to test 1D runtime for a spec. number of testinstances
	 * @throws IOException if a file cant be created
	 */
	private void test1DRuntime() throws IOException {
		long totalTime = 0;
		for(int i=0; i<Constants.TEST_INSTANCES; i++) {
			
			// generate testinstance
			GenerateInput input = new GenerateInput();
			String inputFile = input.generateFile();
			
			long timeA = System.currentTimeMillis();
			SolveOneDim solve1D = new SolveOneDim(inputFile);
			solve1D.helvigApproach();
			long timeB = System.currentTimeMillis();
			totalTime += (timeB - timeA);
			
			// delete testinstance
			File file = new File(inputFile);
			file.delete();
		}
		double average = (double)totalTime/Constants.TEST_INSTANCES;
		System.out.println(average);
		
	}
	
	/**
	 * testmethod to test Prio-Alg with a spec. number of testinstances 
	 * method determines the 10 best weights
	 * @param weightFile Weights_0.txt
	 * @throws IOException if a file cant be created
	 */
	private void testPrioWithMultipleInstances(String weightFile) throws IOException {
		Main main = new Main();
		// generate weights
		ArrayList<double[]> randomWeights = new ArrayList<>();
		for(int i=0; i<Constants.TEST_ITERATIONS; i++) {
			// 4th index contains the tourtime, last index contains the rating
			Random rand = new Random();
			int a = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int b = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int c = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			double[] instance = {a,b,c,0,0};
			randomWeights.add(instance);
		}
		
		int testInstances = Constants.TEST_INSTANCES;
		for(int i=0; i<testInstances; i++) {
			if(i==testInstances/2) {
				System.out.println("50% done!");
			}
			// generate testinstance
			GenerateInput input = new GenerateInput();
			String inputFile = input.generateFile();
			
			for(int j=0; j<Constants.TEST_ITERATIONS; j++) {
				randomWeights.get(j)[3] = main.calcPrio(inputFile,(int) randomWeights.get(j)[0],(int) randomWeights.get(j)[1],(int) randomWeights.get(j)[2]);
			}
			
			// sort in ordner of the time (nonincreasing)
			Collections.sort(randomWeights, new Comparator<double[]>() {
		        @Override
		        public int compare(double[] w1, double[] w2) {
		            return w1[3] < w2[3]? 1 : (w1[3] > w2[3]? -1 : 0);
		        }
		    });
			
			// calc rating of each weights
			for(int j=0; j<Constants.TEST_ITERATIONS; j++) {
				randomWeights.get(randomWeights.indexOf(randomWeights.get(j)))[4] += j;
			}			
			
			// delete testinstance
			File file = new File(inputFile);
			file.delete();
		}
		
		// sort in ordner of the rating (nondecreasing)
		Collections.sort(randomWeights, new Comparator<double[]>() {
	        @Override
	        public int compare(double[] w1, double[] w2) {
	            return w1[4] < w2[4]? 1 : (w1[4] > w2[4]? -1 : 0);
	        }
	    });
		// write the best 3 weights in the file
		BufferedWriter writer = new BufferedWriter(new FileWriter(weightFile));
		for (int i = 0; i < 10; i++) {
			writer.write(randomWeights.get(i)[0] + " " + randomWeights.get(i)[1] + " " + randomWeights.get(i)[2]);
			writer.newLine();
		}
		writer.close();
	}
	
	/**
	 * testmethod to test BF-Alg with a spec. number of testinstances
	 * method writes BF vertices, cuts,.. into a file
	 * @param weightFile BF_Results.txt
	 * @throws IOException if a file cant be created
	 */
	@SuppressWarnings("static-access")
	private void testBFWithMultipleInstances(String file) throws IOException {
		
		long sumCuts = 0;
		long sumReachedRoots = 0;
		long sumConsideredVertices = 0;
		int fails = 0;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		int testInstances = Constants.TEST_INSTANCES;
		for(int i=0; i<testInstances; i++) {
			System.out.println("Brute Force Iteration: " + i + " of " + testInstances);
			writer.newLine(); writer.write("Instance Number: " + i);
			// generate testinstance
			GenerateInput input = new GenerateInput();
			String inputFile = input.generateFile();
			
			// calcBF;
			SolveWithBruteForce bf = new SolveWithBruteForce(inputFile);
			try {
				bf.solveWithBF();
				// write results into file
				writer.newLine(); writer.write("Total Vertices: " + bf.totalVertices);
				writer.newLine(); writer.write("Considered Vertices: " + bf.consideredVertices);	
				writer.newLine(); writer.write("Proportion Considered Vertices: " + 
							(double) bf.consideredVertices/bf.totalVertices);
				writer.newLine(); writer.write("Cuts: " + bf.cuts);
				writer.newLine(); writer.write("Reached Roots: " + bf.reachedRoots);			
				writer.newLine(); writer.write("tauMin: " + String.valueOf(bf.tauMin));
				sumCuts += bf.cuts;
				sumReachedRoots += bf.reachedRoots;
				sumConsideredVertices += bf.consideredVertices;
				writer.newLine();
			} catch (OutOfMemoryError e) {
				fails++;
			}
					
			// delete testinstance
			File f = new File(inputFile);
			f.delete();
		}
		int n = Constants.N;
		double devider = testInstances - fails;
		writer.newLine(); writer.write("Results:");
		double consVert = (double) sumConsideredVertices/devider;
		writer.newLine(); writer.write("Average Consisidered Vertices: " + consVert);
		writer.newLine(); writer.write("Average Proportion Vertices: " + 
					(double) consVert/SolveWithBruteForce.totalVertices(n));
		if(n==13) {
			writer.write("Calc the previous value manually with Average Consisidered Vertices/16926797485, "
					+ "the devider is out of int range!");
		}
		writer.newLine(); writer.write("Average Cuts: " + (double) sumCuts/devider);
		writer.newLine(); writer.write("Average Reached Roots: " + (double) sumReachedRoots/devider);
		writer.newLine(); writer.write("Fails: " + fails);
		
		writer.close();
	}
	
	/**
	 * testmethod to calc the quality of the the prio-alg
	 * compares with bf tourtime
	 * @param file Quality.txt
	 * @throws IOException
	 */
	private void testPrioQuality(String file) throws IOException {
		
		double minQuality = Double.MAX_VALUE;
		double maxQuality = 0;
		double totalQuality = 0;
		int optTours = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));	

		for(int i=0; i<Constants.TEST_INSTANCES; i++) {
			if(i==Constants.TEST_INSTANCES/2) {
				System.out.println("50% done!");
			}
			// generate testinstance
			GenerateInput input = new GenerateInput();
			String inputFile = input.generateFile();
			
			// prio
			SolveWithPriorityApproach prio = new SolveWithPriorityApproach(inputFile);
			prio.setPriorityWeights(Constants.WEIGHTS[0], Constants.WEIGHTS[1], Constants.WEIGHTS[2]);
			ArrayList<Target> OUTPUT1 = prio.prioApproach();
			double t1 = OUTPUT1.get(OUTPUT1.size() - 1).getInterceptingTime();
			
			// BF
			SolveWithBruteForce bf = new SolveWithBruteForce(inputFile);
			ArrayList<Target>OUTPUT2 = bf.solveWithBF();
			double t2 = OUTPUT2.get(OUTPUT2.size() - 1).getInterceptingTime();
			
			double quality;
			// small rounding issues
			if(t1-t2 > 1) {
				quality = t1/t2;
			} else {
				quality = 1;
				optTours++;
			}
			
			totalQuality += quality;
			if(quality < minQuality) {
				minQuality = quality;
			}
			if(quality > maxQuality) {
				maxQuality = quality;
			}
			
			// delete testinstance
			File f = new File(inputFile);
			f.delete();
		}
		double averageQuality = totalQuality/Constants.TEST_INSTANCES;
		
		writer.write("Results for n = " + Constants.N + ", v_iMax = " + Constants.V_I_MAX + " and v_Max = " + Constants.V_PURSUER);
		writer.newLine();
		writer.newLine(); writer.write("Min Quality: " + minQuality);
		writer.newLine(); writer.write("Max Quality: " + maxQuality);
		writer.newLine(); writer.write("Average Quality: " + averageQuality);
		writer.newLine(); writer.write("Optimal Tours: " + optTours);
		writer.close();
	}

	/**
	 * testmethod to write optimal weights into a file for a spec. file
	 * @param inputFile 2D_<x>.txt
	 * @param weightFile Weights_1.txt
	 * @throws IOException invalid input or weights file
	 */
	private void testWeightsWithBruteForce(String inputFile, String weightFile) throws IOException {
		boolean stop = false;
		int cnt = 0;
		
		Main m = new Main();
		double timeBruteForce = m.calcBruteForce(inputFile);
		System.out.println("Optimal time: " + timeBruteForce);
		// write all weights into txt-File
		BufferedWriter writer = new BufferedWriter(new FileWriter(weightFile));
		writer.write("Optimal time = " + timeBruteForce);
		writer.newLine();
		
		System.out.println("Testing " + Constants.TEST_ITERATIONS + " Iterations started.." );
		
		while (!stop) {
			Random rand = new Random();
			
			int a = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int b = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int c = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);

			double time2OA = m.calcPrio(inputFile, a, b, c);
			
			if (time2OA - 0.1 <= timeBruteForce) {
				writer.write(a + "," + b + "," + c + "," + time2OA);
				writer.newLine();
			}
			if (cnt > Constants.TEST_ITERATIONS) {
				stop = true;
				System.out.println("Stopped");
			}
			cnt++;
		}
		writer.write("end");
		writer.close();
	}

	/**
	 * method that gen. random weights for a given file and writes optimal weights into file
	 * @param inputFile inputFile 2D_<x>.txt
	 * @param weightFile Weights_2.txt
	 * @throws IOException invalid input or weights file
	 */
	private void testWeightsWithoutBruteForce(String inputFile, String weightFile) throws IOException {
		
		int counter = 0;
		Main m = new Main();
		
		int[] weights = Constants.WEIGHTS;
		double time = m.calcPrio(inputFile, weights[0], weights[1], weights[2]);
		BufferedWriter writer = new BufferedWriter(new FileWriter(weightFile));
		
		
		while (counter < Constants.TEST_ITERATIONS) {
			
			Random rand = new Random();
			
			int a = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int b = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);
			int c = rand.nextInt(Constants.LIMIT_RANDOM_WEIGHTS);

			double tmpTime = m.calcPrio(inputFile, a, b, c);
			
			if (tmpTime < time) {
				time = tmpTime;
				writer.write(a + "," + b + "," + c + "," + time);
				writer.newLine();
				counter = 0;
			}
			
			counter++;
		}
		writer.write("end");
		writer.close();
	}
	
	/**
	 * testmethod to determine average weights
	 * @param file Weights_1.txt or Weights_2.txt
	 * @throws IOException invalid input file
	 */
	private void calcWeightAverage(String file) throws IOException {
		System.out.println("Calculate Average Weights started..");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		// first line just contains the tourtime
		String line = reader.readLine();
		if(line.contains("Optimal")) {
			line = reader.readLine();
		} 
		
		int lines = 1;
		int[] weights = new int[3];
		
		// read weights
		while(!line.equals("end")) {
			if(line.contains("Nonoptimal")) {
				line = reader.readLine();
				continue;
			}
			String[] weightsString = line.split(",");
			int[] weightsLine = new int[3];
			for(int i=0; i<3; i++) {
				weightsLine[i] = Integer.valueOf(weightsString[i]);
			}
			weights[0] += weightsLine[0];
			weights[1] += weightsLine[1];
			weights[2] += weightsLine[2];

			lines++;
			line = reader.readLine();
		}
		reader.close();
		
		// determine the average
		weights[0] /= lines;
		weights[1] /= lines;
		weights[2] /= lines;
		
		System.out.println("Average Weights: " + Arrays.toString(weights));		
	}
	/**
	 * Constructor
	 */
	private Main() {
		
	}

}