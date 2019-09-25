package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javafx.util.Pair;

public class SolveOneDim {

	private int n;
	private int pursuerVel;
	private int pursuerPos;
	// getKey() -> Index 1 ; getKey() -> Index 2
	private ArrayList<Pair<Integer, Integer>> unsortedPairList;

	public SolveOneDim(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));

		this.n = Integer.valueOf(in.readLine());
		this.pursuerPos = Integer.valueOf(in.readLine());
		this.pursuerVel = Integer.valueOf(in.readLine());
		String[] linePos = in.readLine().split(" ");
		String[] lineVel = in.readLine().split(" ");
		in.close();

		// TODO: origin can be different

		unsortedPairList = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			unsortedPairList.add(new Pair<Integer, Integer>(Integer.valueOf(linePos[i]), Integer.valueOf(lineVel[i])));
		}
	}

	/**
	 * Solving MTT-TSP with Helvig et al. approach 1D-case
	 * 
	 * @return String with concatenated sorted lists of targets
	 */
	public String helvigApproach() {

		ArrayList<Pair<Integer, Integer>> leftPairs = new ArrayList<>();
		ArrayList<Pair<Integer, Integer>> rightPairs = new ArrayList<>();

		for (Pair p : unsortedPairList) {
			if ((int) p.getKey() < pursuerPos) {
				leftPairs.add(p);
			} else
				rightPairs.add(p);
		}

		// sort pairlists in order of nonincreasing speeds
		leftPairs = sortPairList(leftPairs);
		rightPairs = sortPairList(rightPairs);

//		System.out.println("LeftPairs without eliminations:");
//		for(Pair<Integer, Integer> p : leftPairs) {
//			System.out.println(p);
//		}
//		System.out.println("");
//		
//		System.out.println("RightPairs without eliminations:");
//		for(Pair<Integer, Integer> p : rightPairs) {
//			System.out.println(p);
//		}
//		System.out.println("");

		// eliminate those pairs, which are closer to the origin and slower than at
		// least another pair
		leftPairs = eliminatePairs(leftPairs, true);
		rightPairs = eliminatePairs(rightPairs, false);

		System.out.println("LeftPairs with eliminations:");
		for(Pair<Integer, Integer> p : leftPairs) {
			System.out.println(p);
		}
		System.out.println("");
		
		System.out.println("RightPairs with eliminations:");
		for(Pair<Integer, Integer> p : rightPairs) {
			System.out.println(p);
		}
		System.out.println("");

		if (leftPairs.isEmpty() || rightPairs.isEmpty()) {
			// TODO: implement!
			// calc the time required to intercept all remaining targets
			// -> postprocessing
		}

		// TODO: Main algorithm
		ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> mainOutput = mainAlgorithm(leftPairs,
				rightPairs);

		// TODO: Postprocessing

		return "------ \n" + "Solved";
	}

	/**
	 * Main algorithm part of Helvig approach
	 * 
	 * @param leftPairs
	 * @param rightPairs
	 * @return reverse list of states from A_final back to A_0
	 */
	private ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> mainAlgorithm(
			ArrayList<Pair<Integer, Integer>> leftPairs, ArrayList<Pair<Integer, Integer>> rightPairs) {

		ArrayList<State> states = generateStates(leftPairs, rightPairs);

		System.out.println("States:");
		for (State s : states) {
			System.out.println(s.toString());
		}
		System.out.println("");

		// size of states in STATE
		int n = states.size();
		int lpSize = leftPairs.size();
		int rpSize = rightPairs.size();

		// time needed to reach a state
		double t[] = new double[n];
		Arrays.fill(t, Double.MAX_VALUE);
		t[0] = 0;

		// indices of the parent-states
		int parents[] = new int[n];
		Arrays.fill(parents, -1);

		// iterator through all states
		int current = 0;

		while (current < n - 1) { // no need to calculate A_final transitions, because there are non
 
			System.out.println("Iteration: " + current);
			System.out.println("t[n-1]="+t[n-1]+ ", parent: "+parents[n-1]);
			
			// A is the state that we consider in this iteration step
			State A = states.get(current);

			// If there are no transitions into A then
			// Increment current and jump back to the beginning of the while loop
			if (t[current] == Double.MAX_VALUE) {
				current++;
				continue;
			}

			// If for state A, all remaining targets are on one side of the origin then
			double maxInterceptingTime = - 1;
			
			// Leftside left over
			if (!A.getIsLeft() && A.getSkIndex() == rpSize-1) {
				for(int i=A.getSfIndex(); i<lpSize-1; i++) {
					double tmp = calcTransWeight(A.getSk().getKey(), leftPairs.get(i).getKey(), leftPairs.get(i).getValue(), t[current]);
					if(tmp > maxInterceptingTime) {
						maxInterceptingTime = tmp;
					}
				}
				if(t[current] + maxInterceptingTime < t[states.size()-1] && maxInterceptingTime != -1) {
					t[n-1] = t[current] + maxInterceptingTime;
					parents[n-1] = current; 
				}
				current++;
				continue;
				
			// Rightside left over
			}  
			if(A.getIsLeft() && A.getSkIndex() == lpSize-1) {
				for(int i=A.getSfIndex(); i<rpSize-1; i++) {
					double tmp = calcTransWeight(A.getSk().getKey(), rightPairs.get(i).getKey(), rightPairs.get(i).getValue(), t[current]);
					if(tmp > maxInterceptingTime) {
						maxInterceptingTime = tmp;
					}
				}
				if(t[current] + maxInterceptingTime < t[states.size()-1] && maxInterceptingTime != -1) {
					t[n-1] = t[current] + maxInterceptingTime;
					parents[n-1] = current; 
				}
				current++;
				continue;
			}
			
			// determine States A_left and A_right
			State ALeft = getNextState(states, A, true);
			State ARight = getNextState(states, A, false);

			System.out.println(A);
			System.out.println(ALeft.toString());
			System.out.println(ARight.toString());
			System.out.println("---");
			
			// Calculate the two transitions left and right from state A using lists Left
			// and Right
			double tauLeft, tauRight;
			if(A.isStart()) {
				tauLeft = calcTransWeight(0, ALeft.getSf().getKey(), ALeft.getSf().getValue(), 0);
				tauRight = calcTransWeight(0, ARight.getSf().getKey(), ARight.getSf().getValue(), 0);
			} else {
				tauLeft = calcTransWeight(A.getSk().getKey(), ALeft.getSf().getKey(), ALeft.getSf().getValue(), t[current]);
				tauRight = calcTransWeight(A.getSk().getKey(), ARight.getSf().getKey(), ARight.getSf().getValue(), t[current]);
			
			}

//				System.out.println(tauLeft+", "+tauRight);

			// update timestamps
			if (t[current] + tauLeft < t[states.indexOf(ALeft)]) {
				t[states.indexOf(ALeft)] = t[current] + tauLeft;
				parents[states.indexOf(ALeft)] = current;
			}
			if (t[current] + tauRight < t[states.indexOf(ARight)]) {
				t[states.indexOf(ARight)] = t[current] + tauRight;
				parents[states.indexOf(ARight)] = current;
			}

			current++;
		}
		
		System.out.println("-----"+"\n"+"Results: ");
		System.out.println(Arrays.toString(t));
		System.out.println(Arrays.toString(parents));
		System.out.println(t[n-1]);
		
		ArrayList<State> OUTPUT = new ArrayList<State>();

		return null;
	}

	/**
	 * method to sort a pairlist in order of nonincreasing speeds
	 * 
	 * @param pairList List to be sorted
	 * @return pairList sorted list
	 */
	private ArrayList<Pair<Integer, Integer>> sortPairList(ArrayList<Pair<Integer, Integer>> pairList) {

		Collections.sort(pairList, new Comparator<Pair<Integer, Integer>>() {
			@Override
			public int compare(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
				// leftPairs need another sorting
				boolean isLeft = a.getKey() < 0 ? true : false;
				if (isLeft) {
					return a.getValue() < b.getValue() ? -1 : (a.getValue() > b.getValue() ? 1 : 0);
				} else {
					return a.getValue() > b.getValue() ? -1 : (a.getValue() < b.getValue() ? 1 : 0);
				}

			}
		});
		return pairList;
	}

	/**
	 * method to remove those pairs, which are closer to the origin and slower than
	 * at least another pair
	 * 
	 * @param pairList List to be reduced
	 * @param isLeft   boolean to consider left-side- and right-side-case
	 * @return pairList list with eliminated pairs
	 */
	private ArrayList<Pair<Integer, Integer>> eliminatePairs(ArrayList<Pair<Integer, Integer>> pairList,
			boolean isLeft) {

		ArrayList<Pair<Integer, Integer>> pairsToBeEliminated = new ArrayList<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> p1 : pairList)
			for (Pair<Integer, Integer> p2 : pairList)
				if (Math.abs(p1.getKey()) < Math.abs(p2.getKey())
						&& (isLeft ? p1.getValue() >= p2.getValue() : p1.getValue() <= p2.getValue()))
					pairsToBeEliminated.add(p1);

		pairList.removeAll(pairsToBeEliminated);
		return pairList;
	}

	/**
	 * method to generate states with the origin and left & right Pairs
	 * 
	 * @param leftPairs
	 * @param rightPairs
	 * @return State ArrayList
	 */
	private ArrayList<State> generateStates(ArrayList<Pair<Integer, Integer>> leftPairs,
			ArrayList<Pair<Integer, Integer>> rightPairs) {

		// states list
		ArrayList<State> states = new ArrayList<State>();

		// add A_0
		states.add(new State(true));

		// generate all other states
		for (int i = 0; i < leftPairs.size(); i++) {
			for (int j = 0; j < rightPairs.size(); j++) {
				states.add(new State(leftPairs.get(i), rightPairs.get(j), i, j, true));
				states.add(new State(rightPairs.get(j), leftPairs.get(i), j, i, false));
			}
		}
		// sort in order of nondecreasing sum of the indices
		Collections.sort(states);

		// add A_end
		states.add(new State(false));

		return states;

	}

	/**
	 * determines the new State after transition A_left or A_right
	 * 
	 * @param states    List of states
	 * @param A         current State
	 * @param leftTrans true: determine A_left, false: determine A_right
	 * @return nextState A_left or A_right, depends on boolean leftTrans
	 */
	// TODO! FIX and rework!
	private State getNextState(ArrayList<State> states, State A, boolean leftTrans) {

		int skIndex = A.getSkIndex();
		int sfIndex = A.getSfIndex();
		State nextState = null;
		
		if (!A.isStart()) {

			// A_left
			if (leftTrans) {
				if (A.getIsLeft()) { // sk is left
					skIndex++;
				} else {
					skIndex = sfIndex;
					sfIndex = A.getSkIndex() + 1;
				}
			}
			// A_right
			else {
				if (!A.getIsLeft()) { // sk is right
					skIndex++;
				} else {
					skIndex = sfIndex;
					sfIndex = A.getSkIndex() + 1;
				}
			}

			// find new State in list states
			for (State s : states) {
				if (s.getIsLeft() == leftTrans && s.getSkIndex() == skIndex && s.getSfIndex() == sfIndex) {
					nextState = s;
					break;
				}
			}

		} else { // A_0 hard coded
			nextState = leftTrans? states.get(1):states.get(2);
		}

		return nextState;
	}

	/**
	 * method to calculate the transition-weight between two states
	 * calc with physics equations: t=(s2-s1)/(v1-v2)
	 * @param s1 position pursuer at a specific turning point
	 * @param s2 position target
	 * @param v2 velocity target
	 * @param timestamp current time
	 * @return tau transition-weight
	 */
	// TODO: Calc tau correctly
	private double calcTransWeight(int s1, int s2, int v2, double timestamp) {
		double tau;
		// positions at specific timestamp
		double posA;
		double posB;
		int v1 = this.pursuerVel;
		
		posA = s1 + v1 * timestamp;
		posB = s2 + v2 * timestamp;
		
		tau = Math.abs((posB - posA) / (this.pursuerVel - v2));
//		System.out.println(tau);
		return tau;
	}
}
