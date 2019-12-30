package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * @author FelixGreuling
 *
 */
public class SolveWithBruteForce {

	/**
	 * number targets
	 */
	public static int n;
	/**
	 * start position pursuer
	 */
	public static int origin;
	/**
	 * axis of start position
	 * getKey() -> Index 1 ; getKey() -> Index 2
	 */
	public static boolean pursuerAxis;
	/**
	 * pursuer velocity
	 */
	public static int pursuerVel;
	/**
	 * tau_min, tree cuts, if the interc. time > tau_min
	 */
	public static double tauMin = Double.MAX_VALUE;
	/**
	 * unsorted target list input
	 */
	public static ArrayList<Target> targetList;
	/**
	 * current indices of the SequenceStep iteration
	 */
	public static int[] currentIndices;
	/**
	 * current timestamps of the SequenceStep iteration
	 */
	public static double[] currentTimestamps;
	/**
	 * minimum indices for all SequenceSteps
	 */
	public static int[] minIndices;
	/**
	 * minimum timestamps for all SequenceSteps
	 */
	public static double[] minTimestamps;
	/**
	 * number of the cuts in the tree
	 */
	public static int cuts = 0;
	/**
	 * number of the reached roots in the tree
	 */
	public static int reachedRoots = 0;
	/**
	 * number of the considered vertices in the tree
	 */
	public static int consideredVertices = 0;
	/**
	 * number of the total vertices in the tree
	 */
	public static int totalVertices = 0;

	/**
	 * Constructor
	 * @param inputFile 22OA_<x>.txt
	 * @throws IOException invalid input file
	 */
	public SolveWithBruteForce(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		n = Integer.valueOf(in.readLine());
		String[] pursuerStart = in.readLine().split(" ");
		origin = Integer.valueOf(pursuerStart[0]);
		pursuerAxis = Integer.valueOf(pursuerStart[1]) == 0? true : false;
		pursuerVel = Integer.valueOf(in.readLine());
		String[] linePos = in.readLine().split(" ");
		String[] lineVel = in.readLine().split(" ");
		// 0: left-right-axis, 1: top-bottom-axis
		String[] lineAxis = in.readLine().split(" ");
		in.close();
		
		targetList = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			targetList.add(new Target(Integer.valueOf(linePos[i]), Integer.valueOf(lineVel[i]),
					Integer.valueOf(lineAxis[i]) == 0 ? true : false));
		}
	}

	/**
	 * start calc. with brute force
	 * @return target list in intercepting order
	 */
	public ArrayList<Target> solveWithBF() {

		// sort targetList in order of nondecreasing absolut velocties
		// Reduce some iterations
		Collections.sort(targetList, new Comparator<Target>() {
			public int compare(Target t1, Target t2) {
				int v1 = Math.abs(t1.getVel());
				int v2 = Math.abs(t2.getVel());
				return v1 < v2 ? -1 : (v1 > v2 ? 1 : 0);
		}
		});
		
		if (n > 14) {
			System.out.println("The Brute Force method just solves instances with n <= 13!");
			System.out.println("Note, that finding a solution could stil fail in worse-case-scenarios");
			return null;
		} else {
			// reset / init
			tauMin = Double.MAX_VALUE;
			cuts = 0;
			reachedRoots = 0;
			consideredVertices = 0;
			totalVertices = totalVertices(n);
			currentIndices = new int[n];
			minIndices = new int[n];
			currentTimestamps = new double[n+1];
			minTimestamps= new double[n+1];
			Arrays.fill(currentTimestamps, -1);
			
			Target start = new Target(origin, 0, pursuerAxis);
			start.setInterceptingTime(0);
			
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("Brute Force started..");
				System.out.println("Permutations: n! = " + n + "! = " + fac(n));
				// value for n==13 is out of Integer range
				if(n==13) {
					System.out.println("Total Vertices: = 16926797485");
				} 
				else if(n==14) {
					System.out.println("Total Vertices: = 236975164804");
				}
				else {
					System.out.println("Total Vertices: = " +  totalVertices);
				}
			}
			SequenceStep root = new SequenceStep(0, start, targetList);
			root.calcPathTime(0, 0, pursuerAxis);
			
			// for small Error handling
			boolean error = false;
			
			ArrayList<Target> OUTPUT = new ArrayList<>();
			OUTPUT.add(start);
			for(int i = 0; i < n+1; i++) {
				if(i==n) {
					// add origin
					Target finalTarget = new Target(origin, 0, pursuerAxis);
					finalTarget.setInterceptingTime(minTimestamps[i]);
					OUTPUT.add(finalTarget);
				} else {
					targetList.get(minIndices[i]).setInterceptingTime(minTimestamps[i]);
					OUTPUT.add(targetList.get(minIndices[i]));					
				}
				if(currentTimestamps[i] == -1) {
					error = true;
				}	
			}
			
			if(error) {
				System.out.println("Something went wrong! Check correct input like v_i < v_pursuer. ");
			}	
			
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("Cuts: " + cuts);
				System.out.println("Reached Roots: " + reachedRoots);
				System.out.println("Considered Vertices: " + consideredVertices);
				System.out.println("Brute Force finished..");
			}
			
			return OUTPUT;
		}
	}

	/**
	 * set minimums for SequenceStep objects
	 */
	public static void setMinArrarys() {
		for (int i = 0; i < n+1; i++) {
			if(i!=n) {
				minIndices[i] = currentIndices[i];
			}
			minTimestamps[i] = currentTimestamps[i];
		}
	}
	
	/**
	 * calc faculty
	 * @param n number targets
	 * @return faculty of n
	 */
	public static int fac(int n) {
		return n==1?1:n*fac(n-1); 
	}
	/**
	 * calc number vertices
	 * @param n number targets
	 * @return number vertices of n
	 */
	public static int totalVertices(int n) {
		return n==1?1:n+n*totalVertices(n-1); 
	}
}
