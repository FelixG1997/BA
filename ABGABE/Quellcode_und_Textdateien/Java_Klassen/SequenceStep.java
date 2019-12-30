package MAIN;

import java.util.ArrayList;

/**
 * 
 * @author FelixGreuling
 *
 */
public class SequenceStep {

	// 1. create tree
	// 2. follow each path, calc for each step the timestamp and if remaining
	// targets are between the prent and the current target
	// 2.1 stop calc tree below, if timestamp > SWBF.tauMin or a Target of
	// remainingTargets is between prev & current

	private int k;
	private double timestamp = 0;
	private boolean isRoot = false;
	private boolean isLeaf = false;
	private double newPos = 0;
	private Target current;
	private ArrayList<Target> remainingTargets = new ArrayList<>();
	private ArrayList<SequenceStep> childs = new ArrayList<>();

	/**
	 * Constructor
	 * @param k depth in the tree
	 * @param current target
	 * @param remainingTargets
	 */
	public SequenceStep(int k, Target current, ArrayList<Target> remainingTargets) {
		if (k == 0)
			this.isRoot = true;
		this.k = k;
		this.current = current;
		// create new Targets, so the targets don´t refer to the others DELETE
		this.remainingTargets.addAll(remainingTargets);
		this.remainingTargets.remove(current);
		// generate next tree-level
		if (this.remainingTargets.isEmpty()) {
			this.isLeaf = true;
		}
	}

	/**
	 * method to calc everything for the current vertice/target
	 * @param prevNewPos previous position
	 * @param prevTime previous time
	 * @param prevLeftRightAxis previous axis
	 */
	public void calcPathTime(double prevNewPos, double prevTime, boolean prevLeftRightAxis) {
		ArrayList<Target> interceptedTargets = new ArrayList<>();
		if (!isRoot) {	
			SolveWithBruteForce.currentIndices[k-1] = SolveWithBruteForce.targetList.indexOf(current);
			
			this.timestamp = prevTime;
			// Positions for each Target 	
			current.setUpdatePos(calcPositionAfterTime(current, this.timestamp));
			for(Target t : remainingTargets) {
				t.setUpdatePos(calcPositionAfterTime(t, this.timestamp));
			}
			double time = timeToInterceptTarget(current.getUpdatedPos(), current.getVel(), prevNewPos,
					current.getLeftRightAxis(), prevLeftRightAxis);
			
			this.newPos = calcPositionAfterTime(current, timestamp + time);
			
			interceptedTargets = calcTargetsWithinPositions(prevNewPos,
					this.newPos, this.timestamp, remainingTargets, prevLeftRightAxis,
					current.getLeftRightAxis());
			
			this.timestamp += time;
			SolveWithBruteForce.currentTimestamps[k-1] = timestamp;
			SolveWithBruteForce.consideredVertices++;
		} 
		// cut tree, if timestamp > SWBF.tauMin or 
		// a Target of remainingTargets is between prev & current
		// last condition can be disabled,  this can result in an nonoptimal tour caused by an unknown edgecase
		if (timestamp + 0.1 > SolveWithBruteForce.tauMin || !interceptedTargets.isEmpty()) { // 
			// stop Sequence!
			if(!interceptedTargets.isEmpty()) {
				SolveWithBruteForce.cuts++;
			}
		} else {		
			// follow treepath
			if(!isLeaf) {
				for (Target t : remainingTargets) {
					SequenceStep child = new SequenceStep(this.k + 1, t, this.remainingTargets);
					childs.add(child);
					child.calcPathTime(this.newPos, this.timestamp, this.current.getLeftRightAxis());
				}
			// last Target -> calc to origin and update minTau & seqMin, if timestamp < tauMin
			} else {
				SolveWithBruteForce.reachedRoots++;
				double time = timeToInterceptTarget(SolveWithBruteForce.origin, 0, this.newPos,
						SolveWithBruteForce.pursuerAxis, current.getLeftRightAxis());
				double finalTime = this.timestamp + time;
				SolveWithBruteForce.currentTimestamps[k] = finalTime;
				// update mintau and seqMin in SolveWithBruteForce-class
				if(finalTime < SolveWithBruteForce.tauMin) {
					SolveWithBruteForce.tauMin = finalTime;
					SolveWithBruteForce.setMinArrarys();
				}
			}	
		}
	}

	/**
	 * calc the position of target after time
	 * @param t target
	 * @param time
	 * @return new position
	 */
	private double calcPositionAfterTime(Target t, double time) {
		return t.getPos() + t.getVel() * time;
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
		int vpur = tPos < pursuerPos ? SolveWithBruteForce.pursuerVel * -1 : SolveWithBruteForce.pursuerVel;
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
				double posTB = calcPositionAfterTime(t, timestamp + timeToPos);
				boolean rightDirection = posA < posB ? true : false;

				if (rightDirection && (posTA > posA && posTB < posB)) { // right
					interceptedTargets.add(t);
					continue;
				}
				if (!rightDirection && (posTA < posA && posTB > posB)) { // left
					interceptedTargets.add(t);
					continue;
				}

			} else if ((axis1 == t.getLeftRightAxis() || axis2 == t.getLeftRightAxis())
					&& (posA == 0 && axis2 == t.getLeftRightAxis())) {
				// calc to intersection
				double timeToPos = pathTimeEquation(0, 0, posA);
				double posTB = calcPositionAfterTime(t, timeToPos);
				boolean rightDirection = posA < posB ? true : false;

				// posB is intersection -> 0
				if (axis1 == t.getLeftRightAxis()) {
					if (rightDirection && (posTA > posA && posTB < 0)) { // right
						interceptedTargets.add(t);
						continue;
					}
					if (!rightDirection && (posTA < posA && posTB > 0)) { // left
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
					if (rightDirection && (posTA > 0 && posTB < posB)) { // right
						interceptedTargets.add(t);
						continue;
					}
					if (!rightDirection && (posTA < 0 && posTB > posB)) { // left
						interceptedTargets.add(t);
						continue;
					}
				}
			}
		}
		return interceptedTargets;
	}
}
