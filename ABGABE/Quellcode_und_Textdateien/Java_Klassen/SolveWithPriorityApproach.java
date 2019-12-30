package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author FelixGreuling
 *
 */
public class SolveWithPriorityApproach {
	/**
	 * number targets
	 */
	private int n;
	/**
	 * pursuer start coordinate - start and end of the tour
	 */
	private int origin;
	/**
	 * axis of start position
	 * getKey() -> Index 1 ; getKey() -> Index 2
	 */
	private boolean pursuerAxis;
	/**
	 * pursuer velocity
	 */
	private int pursuerVel;
	/**
	 * unsorted target list input
	 */
	private ArrayList<Target> targetList = new ArrayList<>();
	/**
	 * target list in intercepting order
	 */
	private ArrayList<Target> OUTPUT = new ArrayList<>();
	/**
	 * weights to calc. the priority
	 */
	private int velWeight, posWeight,distanceWeight;

	/**
	 * Constructor
	 * @param inputFile 22OA_<x>.txt
	 * @throws IOException invalid input file
	 */
	public SolveWithPriorityApproach(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile)); 
		this.n = Integer.valueOf(in.readLine());
		String[] pursuerStart = in.readLine().split(" ");
		this.origin = Integer.valueOf(pursuerStart[0]);
		this.pursuerAxis = Integer.valueOf(pursuerStart[1]) == 0 ? true : false;
		this.pursuerVel = Integer.valueOf(in.readLine());
		String[] linePos = in.readLine().split(" ");
		String[] lineVel = in.readLine().split(" ");
		// 0: left-right-axis, 1: top-bottom-axis
		String[] lineAxis = in.readLine().split(" ");
		in.close();

		for (int i = 0; i < n; i++) {
			targetList.add(new Target(Integer.valueOf(linePos[i]), Integer.valueOf(lineVel[i]),
					Integer.valueOf(lineAxis[i]) == 0 ? true : false));
		}
	}
 
	/**
	 * start calc. with prio approach
	 * @return target list in intercepting order
	 */
	public ArrayList<Target> prioApproach() {
		if (OUTPUT.isEmpty()) {
			mainAlgorithm();
		}
		Collections.sort(OUTPUT, Target.compTime);

		return OUTPUT;
	}

	/**
	 * calc tour with priority approach 
	 */
	private void mainAlgorithm() {

		// pursuer position at a target
		// init with origin
		Target current = new Target(this.origin, 0, this.pursuerAxis);
		current.setInterceptingTime(0);
		// to return to the origin at the end of the tour
		Target finalTarget = new Target(this.origin, 0, this.pursuerAxis);
		OUTPUT.add(current);

		// PriorityQueue by Java does not update, if the comperable value
		// (t_i.getPrio()) changes
		ArrayList<Target> prioQ = new ArrayList<>();

		for (int i = 0; i < this.targetList.size(); i++) {
			targetList.get(i).setInterceptingTime(Double.MAX_VALUE);
			targetList.get(i).setPrio(calcPrio(targetList.get(i), this.origin, pursuerAxis));
			prioQ.add(this.targetList.get(i));
		}
		
		Collections.sort(prioQ, Target.compPrio); 
		
		double timestamp = 0;
		Target prev = current;

		@SuppressWarnings("unused")
		int cnt = 0;
		
		// loop - main part of the algorithm
		while (!prioQ.isEmpty()) {
			cnt++;
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("Iteration: " + cnt);
			}
			
			prev = current;
			// if all remaining targets in PrioQ are located on one side of the
			// intersection, once the pursuer reached the intersection from current then
			// calc time to reach intereception
			ArrayList<Target> targetsOnOneSide = interceptAllTargetsOfOneSide(current.getUpdatedPos(), timestamp,
					prioQ);
			if (!targetsOnOneSide.isEmpty()) {
				OUTPUT.addAll(targetsOnOneSide);
				if(Constants.PRINT_TARGETS_AND_STEPS) {
					System.out.println("Targets on one side: " + targetsOnOneSide);
					System.out.println("OUTPUT: " + OUTPUT + "\n");
				}
				break;
			}

			for (Target t : prioQ) {
				t.setPrio(calcPrio(t, prev.getUpdatedPos(), prev.getLeftRightAxis()));
			}

			Collections.sort(prioQ, Target.compPrio);
			// prioQ.poll()
			current = prioQ.get(0);
			prioQ.remove(0);
			
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("Current: " + current);
			}

			double time = timeToInterceptTarget(current.getUpdatedPos(), current.getVel(), prev.getUpdatedPos(),
					current.getLeftRightAxis(), prev.getLeftRightAxis());

			// update current
			current.setInterceptingTime(timestamp + time);
			current.setUpdatePos(calcPositionAfterTime(current, time));

			OUTPUT.add(current);

			ArrayList<Target> interceptedTargets = calcTargetsWithinPositions(prev.getUpdatedPos(),
					current.getUpdatedPos(), timestamp, prioQ, prev.getLeftRightAxis(), current.getLeftRightAxis());

			// delete interceptedTargets in PrioQ
			for (Target t : interceptedTargets) {

				prioQ.remove(t);
				// update interceptedTargets
				double tmp = timeToInterceptTarget(t.getUpdatedPos(), t.getVel(), prev.getUpdatedPos(),
						t.getLeftRightAxis(), prev.getLeftRightAxis());
				t.setInterceptingTime(timestamp + tmp);
				t.setUpdatePos(calcPositionAfterTime(t, tmp));
				OUTPUT.add(t);

			}
			timestamp += time;
			// update target´s positions in prioQ
			for (Target t : prioQ) {
				t.setUpdatePos(calcPositionAfterTime(t, time));
			}
			if(Constants.PRINT_TARGETS_AND_STEPS) {
				System.out.println("interceptedTargets: " + interceptedTargets);
				System.out.println("OUTPUT: " + OUTPUT + "\n");
			}
		}

		Collections.sort(OUTPUT, Target.compTime);

		prev = OUTPUT.get(OUTPUT.size() - 1);
		timestamp = prev.getInterceptingTime();
		timestamp += timeToInterceptTarget(this.origin, 0, prev.getUpdatedPos(), this.pursuerAxis, prev.getLeftRightAxis());
		finalTarget.setInterceptingTime(timestamp);
		OUTPUT.add(finalTarget);
	}

	/**
	 * check, if all targets are on one side of the origin
	 * @param pos current position
	 * @param timestamp current timestamp
	 * @param prioQ remaining targets in prioQ
	 * @return return null <- condition is false, else target list in intercepting order
	 */
	private ArrayList<Target> interceptAllTargetsOfOneSide(double pos, double timestamp, ArrayList<Target> prioQ) {
		// time to reach the intersection from the pursuer´s position
		double timeToIntersection = pathTimeEquation(0, 0, pos);
		double[] tmpPositions = new double[prioQ.size()];
		boolean sameSide = true;
		for (int i = 0; i < prioQ.size(); i++) {
			tmpPositions[i] = calcPositionAfterTime(prioQ.get(i), timeToIntersection);
		}
		for (int i = 0; i < prioQ.size() - 1; i++) {
			if (!(((tmpPositions[i] < 0 && tmpPositions[i + 1] < 0) || (tmpPositions[i] > 0 && tmpPositions[i + 1] > 0)) &&
					prioQ.get(i).getLeftRightAxis() == prioQ.get(i + 1).getLeftRightAxis())) {
				sameSide = false;
				break;
			}
		}
		ArrayList<Target> targets = new ArrayList<>();
		if (sameSide) {
			for (int i = 0; i < prioQ.size(); i++) {
				targets.add(prioQ.get(i));
				// update interceptingTime and position
				double time = pathTimeEquation(tmpPositions[i], prioQ.get(i).getVel(), 0) + timeToIntersection;
				targets.get(i).setUpdatePos(calcPositionAfterTime(targets.get(i), time));
				time += timestamp;
				targets.get(i).setInterceptingTime(time);
			}
		}
		return targets;
	}

	/**
	 * calc the position of target after time
	 * @param t target
	 * @param time
	 * @return new position
	 */
	private double calcPositionAfterTime(Target t, double time) {
		return t.getUpdatedPos() + t.getVel() * time;
	}

	/**
	 * if coordinates of the current position and the target are the same, but they are on different axis, 
	 * swap signs (pos,vel) of the target
	 * @param tPos position of the target
	 * @param tVel velocity of the target
	 * @param pursuerPos current position
	 * @param axis1 current axis
	 * @param axis2 target axis
	 * @return time to intercept target t
	 */
	private double timeToInterceptTarget(double tPos, int tVel, double pursuerPos, boolean axis1, boolean axis2) {
		// same position with other axis
		if ((tPos < 0 && pursuerPos < 0 || tPos > 0 && pursuerPos > 0) && (axis1 != axis2)) {
			// swap signs
			return pathTimeEquation(-tPos, -tVel, pursuerPos);
		} else
			return pathTimeEquation(tPos, tVel, pursuerPos);
	}

	/**
	 * calc. the time to intercept target t
	 * @param tPos position of the target
	 * @param tVel velocity of the target
	 * @param pursuerPos current position
	 * @return time to intercept target t
	 */
	private double pathTimeEquation(double tPos, int tVel, double pursuerPos) {
		// pursuer needs to move left, if the target is left of the pursuer
		int vpur = tPos < pursuerPos ? this.pursuerVel * -1 : this.pursuerVel;
		return Math.abs((pursuerPos - tPos) / (vpur - tVel));
	}

	/**
	 * calc. intercepted targets within posA and posB
	 * @param posA start
	 * @param posB end
	 * @param timestamp timestamp at posA
	 * @param targets remaining targets
	 * @param axis1 axis posA
	 * @param axis2 axis posB
	 * @return list of intercepted targets 
	 */
	private ArrayList<Target> calcTargetsWithinPositions(double posA, double posB, double timestamp,
			ArrayList<Target> targets, boolean axis1, boolean axis2) {

		ArrayList<Target> interceptedTargets = new ArrayList<>();

		for (Target t : targets) {

			double posTA = t.getUpdatedPos();

			// condition to be intercepted: posTA and posTB within posA & posB or
			// posTA >= (<=) posB and posTB within posA & posB when moving right (left)

			// same axis
			if ((axis1 == t.getLeftRightAxis() && axis2 == t.getLeftRightAxis())
					|| (posA == 0 && axis2 == t.getLeftRightAxis())) {

				double timeToPos = pathTimeEquation(posB, 0, posA);
				double posTB = calcPositionAfterTime(t, timeToPos);
				boolean rightDirection = posA < posB ? true : false;

				if (rightDirection && (posTA >= posA && posTB <= posB)) { // right
					interceptedTargets.add(t);
					continue;
				}
				if (!rightDirection && (posTA <= posA && posTB >= posB)) { // left
					interceptedTargets.add(t);
					continue;
				}

			} else if((axis1 == t.getLeftRightAxis() || axis2 == t.getLeftRightAxis())
					&& (posA != 0 && axis2 == t.getLeftRightAxis())) {
				// calc to intersection
				double timeToPos = pathTimeEquation(0, 0, posA);
				double posTB = calcPositionAfterTime(t, timestamp + timeToPos);
				boolean rightDirection = posA < posB ? true : false;

				// posB is intersection -> 0
				if (axis1 == t.getLeftRightAxis()) {
					if (rightDirection && (posTA >= posA && posTB <= 0)) { // right
						interceptedTargets.add(t);
						continue;
					}
					if (!rightDirection && (posTA <= posA && posTB >= 0)) { // left
						interceptedTargets.add(t);
						continue;
					}
				}

				// calc from intersection to posB
				posTA = posTB;
				timeToPos = pathTimeEquation(posB, 0, 0);
				posTB = calcPositionAfterTime(t, timeToPos);
				rightDirection = posTA < posTB ? true : false;

				// posA is intersection -> 0
				if (axis2 == t.getLeftRightAxis()) {
					if (rightDirection && (posTA >= 0 && posTB <= posB)) { // right
						interceptedTargets.add(t);
						continue;
					}
					if (!rightDirection && (posTA <= 0 && posTB >= posB)) { // left
						interceptedTargets.add(t);
						continue;
					}
				}
			}
		}
		return interceptedTargets;
	}

	/**
	 * calc the prio of a target
	 * the prio is a combination of different factors which are based on the time to intercept the target
	 * @param t considered target
	 * @param posPur current position
	 * @param axisPur current axis
	 * @return priority
	 */
	private double calcPrio(Target t, double posPur, boolean axisPur) {
		
		// very influential: !!!, little influential: !
		double prio = 0;
		boolean movingTowardsIntersection = ((t.getUpdatedPos() > 0 && t.getVel() < 0)
				|| (t.getUpdatedPos() < 0 && t.getVel() > 0));

		// velocity !!!
		// fast -> prio+
		double proportionVel = (double) Math.abs(t.getVel()) / this.pursuerVel * this.velWeight;

		// distance towards or away !
		// far away from the intersection -> prio+		
		double proportionPos = (double) Math.abs(t.getUpdatedPos()) / this.pursuerVel * (movingTowardsIntersection ? -1 : 1) * this.posWeight;
	
		// distance position - target !
		// near targets -> prio+ (substract time to target from prio)
		double proportionDistance = (double) timeToInterceptTarget(t.getUpdatedPos(), t.getVel(), posPur, t.getLeftRightAxis(), axisPur) * distanceWeight;
		
		prio = proportionVel + proportionPos - proportionDistance;
		return prio;
	}

	/**
	 * set weights for the prio approach
	 * @param velWeight
	 * @param posWeight
	 * @param distanceWeight
	 */
	public void setPriorityWeights(int velWeight, int posWeight, int distanceWeight) {
		this.velWeight = velWeight;
		this.posWeight = posWeight;
		this.distanceWeight = distanceWeight;
	}

}
