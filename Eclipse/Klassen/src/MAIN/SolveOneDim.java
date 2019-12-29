package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 
 * @author FelixGreuling
 *
 */
public class SolveOneDim {

	/**
	 * number targets
	 */
	private int n;
	/**
	 * pursuer Velocity
	 */
	private int pursuerVel;
	/**
	 * pursuer start position
	 */
	private int pursuerPos;
	/**
	 * unsorted target list
	 */
	private ArrayList<Target> unsortedTargetList = new ArrayList<>();
	/**
	 * targets with startcoords. on the left side of the origin
	 */
	private ArrayList<Target> leftTargets = new ArrayList<>();
	/**
	 * targets with startcoords. on the right side of the origin
	 */
	private ArrayList<Target> rightTargets = new ArrayList<>();

	/**
	 * Constructor
	 * @param inputFile 1D_<x>.txt
	 * @throws IOException invalid input file
	 */
	public SolveOneDim(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));

		this.n = Integer.valueOf(in.readLine());
		this.pursuerPos = Integer.valueOf(in.readLine().split(" ")[0]);
		this.pursuerVel = Integer.valueOf(in.readLine());
		String[] linePos = in.readLine().split(" ");
		String[] lineVel = in.readLine().split(" ");
		in.close();

		unsortedTargetList = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			unsortedTargetList.add(new Target(Integer.valueOf(linePos[i]), Integer.valueOf(lineVel[i])));
		}
	}

	/**
	 * Solving MTT-TSP with Helvig et al. approach 1D-case
	 * @return Target list with concatenated sorted list in intercepting order of targets
	 */
	public ArrayList<Target> helvigApproach() {
		
		Preprocessing();
		
		ArrayList<State> stateOutput = new ArrayList<>();
		
		// If all remaining targets are on one side of the origin
		// calc the time required to intercept all remaining targets
		if (leftTargets.isEmpty() || rightTargets.isEmpty()) {
		
			// add A_0
			stateOutput.add(new State(true, 0));
			// add A_final
			stateOutput.add(new State(false, 1));
			
			double maxInterceptingTime = - 1;
			// Index of last target on the other side 
			int furthestAway = 0;
			stateOutput.get(0).setShortestTime(0);
			
			if(leftTargets.isEmpty() || rightTargets.isEmpty()) {
				int size;
				ArrayList<Target> pairs;
				if(leftTargets.isEmpty()) {
					size = rightTargets.size();
					pairs = rightTargets;
				}
				else { // rightTargets.isEmpty()
					size = leftTargets.size();
					pairs = leftTargets;
				}
				
				for(int i=0; i<size; i++) {
					// weight from the origin to the point leftPairs.get(i)
					double tmp = calcTransWeight(0, pairs.get(i).getPos(), 0, pairs.get(i).getVel(), 0);
					// and return to the origin
					tmp += calcTransWeight(pairs.get(i).getPos(), 0, pairs.get(i).getVel(), 0, tmp);
					if(tmp > maxInterceptingTime) {
						maxInterceptingTime = tmp;
						furthestAway = i;
					}
				}
				// update tau_final
				stateOutput.get(1).setShortestTime(maxInterceptingTime);
				stateOutput.get(1).setFurthestAway(pairs.get(furthestAway));
			}
			
			
		} else {
			stateOutput = mainAlgorithm();
			Collections.reverse(stateOutput);
		}
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("List of states from A_0 back to A_final:");
			for (State s : stateOutput){
			    System.out.println(s.toString());		    
			}
		}
		
		ArrayList<Target> targetsInInterceptionOrder = postprocessing(stateOutput);
		
		return targetsInInterceptionOrder;
	}

	/**
	 * Preprocessing Step of Helvig approach
	 * sorts and eliminates targets in Left and Right
	 */
	private void Preprocessing() {
		
		for(Target t : unsortedTargetList) {
			if(t.getPos() < pursuerPos) {
				leftTargets.add(t);
			} else
				rightTargets.add(t);
		}

		// sort pairlists in order of nonincreasing speeds
		Collections.sort(leftTargets, Target.compVel);
		Collections.sort(rightTargets, Target.compVel);

		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("LeftPairs without eliminations:");
			for(Target t : leftTargets) {
				System.out.println(t.toString());
			}
			System.out.println("");
			
			System.out.println("RightPairs without eliminations:");
			for(Target t : rightTargets) {
				System.out.println(t.toString());
			}
			System.out.println("");
		}
		// eliminate those pairs, which are closer to the origin and slower than at
		// least another pair
		leftTargets = eliminatePairs(leftTargets, true);
		rightTargets = eliminatePairs(rightTargets, false);

		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("LeftPairs with eliminations:");
			for(Target t : leftTargets) {
				System.out.println(t.toString());
			}
			System.out.println("");
			
			System.out.println("RightPairs with eliminations:");
			for(Target t : rightTargets) {
				System.out.println(t.toString());
			}
			System.out.println("");
		}
	}

	/**
	 * Main algorithm of Helvig approach
	 * @return reverse list of states from A_final back to A_0
	 */
	private ArrayList<State> mainAlgorithm() {
		
		ArrayList<State> states = generateStates();
		
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println("States:");
			for (State s : states) {
				System.out.println(s.toString());
			}
			System.out.println("");
		}
		
		// size of states in STATE
		int n = states.size();
		int lpSize = leftTargets.size();
		int rpSize = rightTargets.size();

		// time needed to reach a state
		double t[] = new double[n];
		Arrays.fill(t, Double.MAX_VALUE);
		t[0] = 0;

		// indices of the parent-states
		int parents[] = new int[n];
		Arrays.fill(parents, -1);

		// iterator through all states
		int current = 0;

		while (current < n-1) { // no need to calculate A_final transitions, because there are non
 
			// the following condition belongs to the cond. above
			// for more efficiant implementation the boolean validState insures skipping the State A
			
			
			// A is the state that we consider in this iteration step
			State A = states.get(current);
			
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("");
				System.out.println("Iteration: " + current);
				System.out.println("A = " + A.toString());
				System.out.println(Arrays.toString(t));
				System.out.println("Parents: " + Arrays.toString(parents));
				System.out.println("t[n-1]="+t[n-1]+ ", parent: "+parents[n-1]);
			}
			
			// If there are no transitions into A then
			// Increment current and jump back to the beginning of the while loop
			if (t[current] == Double.MAX_VALUE) {
				current++;
				continue;
			}
			
			// If for state A, all remaining targets are on one side of the origin then
			
			double maxInterceptingTime = - 1;
			// Index of last target on the other side 
			int furthestAway = 0;
			
			// Left- or Rightside left over
			if(((!A.getIsLeft() && A.getSkIndex() == rpSize-1) || A.getIsLeft() && A.getSkIndex() == lpSize-1) && current!=0) {
				int size;
				ArrayList<Target> pairs;
				if(!A.getIsLeft() && A.getSkIndex() == rpSize-1) {
					size = lpSize;
					pairs = leftTargets;
				}
				else { // (A.getIsLeft() && A.getSkIndex() == lpSize-1)
					size = rpSize;
					pairs = rightTargets;
				}
				for(int i=A.getSfIndex(); i<size; i++) {
					// weight from A to the point leftPairs.get(i)
					double tmp = calcTransWeight(A.getSk().getPos(), pairs.get(i).getPos(), A.getSk().getVel(), pairs.get(i).getVel(), t[current]);
					// and return to the origin
					tmp += calcTransWeight(pairs.get(i).getPos(), 0, pairs.get(i).getVel(), 0, t[current] + tmp);
					if(tmp > maxInterceptingTime) {
						maxInterceptingTime = tmp;
						furthestAway = i;
					}
				}
				// update tau_final
				if(t[current] + maxInterceptingTime < t[n-1] && maxInterceptingTime != -1) {
					t[n-1] = t[current] + maxInterceptingTime;
					parents[n-1] = current; 
					states.get(n-1).setFurthestAway(pairs.get(furthestAway));
				}
				current++;
				continue;				
			}
			
			// determine States A_left and A_right
			State ALeft = getNextState(states, A, true, t[current]);
			State ARight = getNextState(states, A, false, t[current]);
			
			// Calculate the two transitions left and right from state A using lists Left
			// and Right
			double tauLeft, tauRight;
			if(A.isStart()) {
				tauLeft = calcTransWeight(0, ALeft.getSk().getPos(), 0, ALeft.getSk().getVel(), 0);
				tauRight = calcTransWeight(0, ARight.getSk().getPos(), 0, ARight.getSk().getVel(), 0);
			} else {
				tauLeft = calcTransWeight(A.getSk().getPos(), ALeft.getSk().getPos(), A.getSk().getVel(), ALeft.getSk().getVel(), t[current]);
				tauRight = calcTransWeight(A.getSk().getPos(), ARight.getSk().getPos(), A.getSk().getVel(), ARight.getSk().getVel(), t[current]);
			}
			
			// update timestamps
			if (t[current] + tauLeft < t[ALeft.getStateIndex()]) {
				t[ALeft.getStateIndex()] = t[current] + tauLeft;
				parents[ALeft.getStateIndex()] = current;
				
			}
			if (t[current] + tauRight < t[ARight.getStateIndex()]) {
				t[ARight.getStateIndex()] = t[current] + tauRight;
				parents[ARight.getStateIndex()] = current;
			}			
			current++;
		}
		
		if(Constants.PRINT_TARGETS_AND_STEPS) {
			System.out.println(""+"\n"+"Results: ");
			System.out.println(Arrays.toString(t));
			System.out.println("Parents: " + Arrays.toString(parents));
			System.out.println("t[n-1]="+t[n-1]+ ", parent: "+parents[n-1]);
			System.out.println("");
		}
		
		ArrayList<State> OUTPUT = new ArrayList<State>();
		
		// write states in output array
		OUTPUT.add(states.get(n-1)); // A_final
		int tmp = parents[n-1];
		states.get(n-1).setShortestTime(t[n-1]);
		while(tmp != -1) {
			OUTPUT.add(states.get(tmp));
			states.get(tmp).setShortestTime(t[tmp]);
			tmp = parents[tmp];
		}

		return OUTPUT;
	}

	/**
	 * Postprocessing Step of Helvig approach
	 * @param turningPoints State list / turning points
	 * @return Target list with concatenated sorted list in intercepting order of targets
	 */
	private ArrayList<Target> postprocessing(ArrayList<State> turningPoints) {
		
		double timestampA = 0;
		double timestampB = 0;
		State a, b;
		
		ArrayList<Target> targetsInInterceptionOrder = new ArrayList<>();
		Target start = new Target(0, 0);
		start.setInterceptingTime(0);
		targetsInInterceptionOrder.add(start);
		
		double lastTimestamp = 0;
		int lastS = 0;
		int lastV = 0;
		
		for(int i=0; i<turningPoints.size()-1; i++) {
			// For each pair of consecutive states in OUTPUT
			a = turningPoints.get(i);
			b = turningPoints.get(i+1);
			
			int s1 = 0, s2 = 0, v1 = 0, v2 = 0;
			double posA = 0, posB = 0;
						
			// calc positions and velocities of turning points
			if(i!=0) {
				s1 = a.getSk().getPos();
				v1 = a.getSk().getVel();
			} 
			
			if(i==turningPoints.size()-2) { // A_final
				s2 = b.getFurthestAway().getPos();
				v2 = b.getFurthestAway().getVel();	
			} else { 
				s2 = b.getSk().getPos();
				v2 = b.getSk().getVel();
			}
			
			// timestamp of a reaching b
			timestampB = timestampA + calcTransWeight(s1, s2, v1, v2, timestampA);
			
			posA = posAtTimestamp(s1, v1, timestampA);
			posB = posAtTimestamp(s2, v2, timestampB);
			
			//Calculate which targets are intercepted between the state pair
			ArrayList<Target> interceptedTargets = calcTargetsWithinPositions(posA, posB, timestampA, timestampB, unsortedTargetList);
			for(Target t:interceptedTargets) {
				double interceptedTime = timestampA + calcTransWeight(s1, t.getPos(), v1, t.getVel(), timestampA);
				t.setInterceptingTime(interceptedTime);
				if(interceptedTime > lastTimestamp) {
					lastTimestamp = interceptedTime;
					lastS = t.getPos();
					lastV = t.getVel();
				}
			}
			this.unsortedTargetList.removeAll(interceptedTargets);
			targetsInInterceptionOrder.addAll(interceptedTargets);
		
			// add returning to the origin
			if(i==turningPoints.size()-2) {
				Collections.sort(targetsInInterceptionOrder, Target.compTime);
				Target end = new Target(0, 0);
				end.setInterceptingTime(lastTimestamp + calcTransWeight(lastS, 0, lastV, 0, lastTimestamp));
				targetsInInterceptionOrder.add(end);
			}
			// set new timestamp
			timestampA = timestampB;	
		}		
		return targetsInInterceptionOrder;
	}
	
	/**
	 * method to determine targets within positions
	 * @param posA start
	 * @param posB end
	 * @param timestampA start
	 * @param timestampB end
	 * @param targets remaining targets
	 * @return intercepted targets within posA and posB
	 */
	private ArrayList<Target> calcTargetsWithinPositions(double posA, double posB, double timestampA, double timestampB, ArrayList<Target> targets) {

		ArrayList<Target> interceptedTargets = new ArrayList<>();

		for (Target t : targets) {

			double posTA = posAtTimestamp(t.getPos(), t.getVel(), timestampA);;
			double posTB = posAtTimestamp(t.getPos(), t.getVel(), timestampB);
			boolean rightDirection = posA < posB ? true : false;

			// condition to be intercepted: posTA and posTB within posA & posB or
			// posTA >= (<=) posB and posTB within posA & posB when moving right (left)
			if (rightDirection && (posTA >= posA && posTB <= posB)) { // right
				interceptedTargets.add(t);
				continue;
			}
			if (!rightDirection && (posTA <= posA && posTB >= posB)) { // left
				interceptedTargets.add(t);
				continue;
			}
		}
		return interceptedTargets;
	}
	
	/**
	 * method to remove those pairs, which are closer to the origin and slower than
	 * at least another pair
	 * 
	 * @param pairList List to be reduced
	 * @param isLeft   boolean to consider left-side- and right-side-case
	 * @return pairList list with eliminated pairs
	 */
	private ArrayList<Target> eliminatePairs(ArrayList<Target> pairList,
			boolean isLeft) {
		
		ArrayList<Target> pairsToBeEliminated = new ArrayList<Target>();
		for (Target t1 : pairList)
			for (Target t2 : pairList)
				if (Math.abs(t1.getPos()) < Math.abs(t2.getPos())
						&& (isLeft ? t1.getVel() >= t2.getVel() : t1.getVel() <= t2.getVel()))
					pairsToBeEliminated.add(t1);

		pairList.removeAll(pairsToBeEliminated);
		return pairList;
		
	}

	/**
	 * method to generate states with left & right Pairs
	 * also adds A_0 and A_final
	 * 
	 * @return State ArrayList
	 */
	private ArrayList<State> generateStates() {

		// states list
		ArrayList<State> states = new ArrayList<State>();

		// add A_0
		states.add(new State(true, 0));

		// generate all other states
		int stateCnt = 1;
		for (int i = 0; i < leftTargets.size(); i++) {
			for (int j = 0; j < rightTargets.size(); j++) {
				states.add(new State(leftTargets.get(i), rightTargets.get(j), stateCnt, i, j, true));
				stateCnt++;
				states.add(new State(rightTargets.get(j), leftTargets.get(i), stateCnt, j, i, false));
				stateCnt++;
			}
		}
		// sort in order of nondecreasing sum of the indices
		Collections.sort(states);

		// add A_final
		states.add(new State(false, stateCnt));

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
	private State getNextState(ArrayList<State> states, State A, boolean leftTrans, double timestamp) {

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
			// check if state is valid
//			double posT1 = posAtTimestamp(A.getSk().getPos(), A.getSk().getVel(), timestamp);
//			double posT2 = posAtTimestamp(nextState.getSk().getPos(), nextState.getSk().getVel(), timestamp);
//			// A_left
//			if (leftTrans) {
//				if(posT1<posT2) {
//					
//				}
//			}
//			// A_right
//			else {
//				if(posT1>posT2) {
//					
//				}
//			}
			
			
			
			
		} else { // A_0 hard coded
			nextState = leftTrans? states.get(1):states.get(2);
		}

		return nextState;
	}

	/**
	 * method to calculate the transition-weight between two states
	 * calc with physics equations: t=(s2-s1)/(v1-v2)
	 * @param s1 position pursuer at a specific turning point 1
	 * @param s2 position target 2
	 * @param v1 velocity target 1
	 * @param v2 velocity target 2
	 * @param timestamp current time
	 * @return tau transition-weight
	 */
	private double calcTransWeight(int s1, int s2, int v1, int v2, double timestamp) {
				
		// positions at specific timestamp
		double posA = posAtTimestamp(s1, v1, timestamp);
		double posB = posAtTimestamp(s2, v2, timestamp);
		
		// pursuer needs to move left, if the target is left of the pursuer
		int vpur = posB<posA?this.pursuerVel*-1:this.pursuerVel; 
		double tau = Math.abs((posB - posA) / (vpur - v2));

		return tau;
	}
	
	/**
	 * calc. the position for a target at a spec. timestamp
	 * @param init start coordinate
	 * @param v target's velocity
	 * @param timestamp current timestamp
	 * @return new position
	 */
	public double posAtTimestamp(double init, int v, double timestamp) {
		return init + (v*timestamp);
	}
}
