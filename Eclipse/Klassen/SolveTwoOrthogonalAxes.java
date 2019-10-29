package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SolveTwoOrthogonalAxes {
	private int n;
	private int origin;
	// getKey() -> Index 1 ; getKey() -> Index 2
	private Integer pursuerAxis;
	private int pursuerVel;
	private ArrayList<Target> targetList = new ArrayList<>();
	private ArrayList<Target> OUTPUT = new ArrayList<>();
	private int velWeight, distanceOriginWeight, distancePosIntersectionWeight, distancePosOriginWeight;

	public SolveTwoOrthogonalAxes(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		this.n = Integer.valueOf(in.readLine());
		String[] pursuerStart = in.readLine().split(" ");
		this.origin = Integer.valueOf(pursuerStart[0]);
		this.pursuerAxis = Integer.valueOf(pursuerStart[1]);
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

	public ArrayList<Target> prioApproach() {

		preprocessing();
		if (OUTPUT.isEmpty()) {
			mainAlgorithm();
			postprocessing();
		}
		Collections.sort(OUTPUT, Target.compTime);

		return OUTPUT;
	}

	private void preprocessing() {

		ArrayList<Target> tmp = interceptAllTargetsOfOneSide(0, 0, targetList);
		if (!tmp.isEmpty()) {
			OUTPUT.addAll(tmp);

		}

		// TODO: Eliminations neccessary?

	}

	private void mainAlgorithm() {

		// double[] time = new double[this.targetList.size()];
		// pursuer position at a target
		// init with origin
		Target current = new Target(this.origin, 0, this.pursuerAxis == 0 ? true : false);
		current.setInterceptingTime(0);
		// to return to the origin at the end of the tour
		Target finalTarget = new Target(this.origin, 0, this.pursuerAxis == 0 ? true : false);
		OUTPUT.add(current);

		// PriorityQueue by Java does not update, if the comperable value
		// (t_i.getPrio()) changes
		ArrayList<Target> prioQ = new ArrayList<>();

		for (int i = 0; i < this.targetList.size(); i++) {
			targetList.get(i).setInterceptingTime(Double.MAX_VALUE);
			prioQ.add(this.targetList.get(i));
		}

		double timestamp = 0;
		Target prev = current;

		int cnt = 0;
		while (!prioQ.isEmpty()) {
			System.out.println(cnt);

			prev = current;
			// if all remaining targets in PrioQ are located on one side of the
			// intersection,
			// once the pursuer reached the intersection from current then
			// calc time to reach intereception
			ArrayList<Target> targetsOnOneSide = interceptAllTargetsOfOneSide(current.getUpdatedPos(), timestamp,
					prioQ);
			if (!targetsOnOneSide.isEmpty()) {
				OUTPUT.addAll(targetsOnOneSide);
				System.out.println("Targets on one side: " + targetsOnOneSide);
				break;
			}

			// TODO: calc prio of each target in prio
			for (Target t : prioQ) {
				t.setPrio(calcPrio(t, prev.getUpdatedPos()));
			}

			Collections.sort(prioQ, Target.compPrio);
			// prioQ.poll()
			current = prioQ.get(0);
			prioQ.remove(0);
			System.out.println(current);

			double time = timeToInterceptTarget(current.getUpdatedPos(), current.getVel(), prev.getUpdatedPos(),
					current.getLeftRightAxis(), prev.getLeftRightAxis());
			System.out.println(time);

			// update current
			current.setInterceptingTime(timestamp + time);
			current.setUpdatePos(calcPositionAfterTime(current, time));

			OUTPUT.add(current);

			ArrayList<Target> interceptedTargets = calcTargetsWithinPositions(prev.getUpdatedPos(),
					current.getUpdatedPos(), timestamp, time, prioQ, prev.getLeftRightAxis(),
					current.getLeftRightAxis());

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

			// update target´s positions in prioQ
			for (Target t : prioQ) {
				t.setUpdatePos(calcPositionAfterTime(t, timestamp + time));
			}

			timestamp += time;

//			Collections.sort(prioQ, Target.compPrio);

			cnt++;
			System.out.println("----------");
		}

		System.out.println("-------");

		Collections.sort(OUTPUT, Target.compTime);

		prev = OUTPUT.get(OUTPUT.size() - 1);
		timestamp = prev.getInterceptingTime();
		timestamp += pathTimeEquation(0, 0, prev.getUpdatedPos());
		System.out.println(timestamp);
		finalTarget.setInterceptingTime(timestamp);
		OUTPUT.add(finalTarget);
	}

	private void postprocessing() {

		// TODO: add eliminated targets

	}

	// return null, condition is false
	private ArrayList<Target> interceptAllTargetsOfOneSide(double pos, double timestamp, ArrayList<Target> prioQ) {
		// time to reach the intersection from the pursuer´s position
		double timeToIntersection = pathTimeEquation(0, 0, pos);
		double[] tmpPositions = new double[prioQ.size()];
		boolean sameSide = true;
		for (int i = 0; i < prioQ.size(); i++) {
			tmpPositions[i] = calcPositionAfterTime(prioQ.get(i), timeToIntersection);
		}
		for (int i = 0; i < prioQ.size() - 1; i++) {
			if (!((tmpPositions[i] < 0 && tmpPositions[i + 1] < 0) || (tmpPositions[i] > 0 && tmpPositions[i + 1] > 0)
					&& prioQ.get(i).getLeftRightAxis() == prioQ.get(i + 1).getLeftRightAxis())) {
				sameSide = false;
				break;
			}
		}
		ArrayList<Target> targets = new ArrayList<>();	
		if (sameSide) {
			for (int i = 0; i < prioQ.size(); i++) {
				targets.add(prioQ.get(i));
				double time = pathTimeEquation(tmpPositions[i], prioQ.get(i).getVel(), 0) + timestamp
						+ timeToIntersection;
				targets.get(i).setInterceptingTime(time);
			}
		}
		return targets;

	}

	private double calcPositionAfterTime(Target t, double time) {
		return t.getUpdatedPos() + t.getVel() * time;
	}

	// TODO: del. method?
	// tpos tvel
	private double timeToInterceptTarget(double tPos, int tVel, double pursuerPos, boolean axis1, boolean axis2) {
		// same position with other axis
		if ((tPos < 0 && pursuerPos < 0 || tPos > 0 && pursuerPos > 0) && (axis1 != axis2)) {
			// swap signs
			return pathTimeEquation(-tPos, -tVel, pursuerPos);
		} else
			return pathTimeEquation(tPos, tVel, pursuerPos);
	}

	// Weg-Zeit-Gesetz-Gleichung
	private double pathTimeEquation(double tPos, int tVel, double pursuerPos) {
		// pursuer needs to move left, if the target is left of the pursuer
		int vpur = tPos < 0 ? this.pursuerVel * -1 : this.pursuerVel;
		return Math.abs((pursuerPos - tPos) / (vpur - tVel));
	}

	private double calcPrio(Target t, double posPur) {
		// very influential: t_vel, t_updatedPos
		// little influential: distance pos-origin, Origin pos
		double prio = 0;
		
		boolean movingTowardsOrigin = ((t.getUpdatedPos() > 0 && t.getVel() < 0) || (t.getUpdatedPos() < 0 && t.getVel() > 0));
		
		double proportionVel = t.getVel()/Math.abs(this.pursuerVel) * this.velWeight;
		double proportionPos = (movingTowardsOrigin? 1:-1) * (t.getUpdatedPos()/this.pursuerVel) * this.distancePosOriginWeight;		
		double proportionIntersection = pathTimeEquation(0, 0, t.getPos()) * this.distancePosIntersectionWeight;
		double proportionOrigin = pathTimeEquation(this.origin, 0, t.getPos()) * this.distancePosOriginWeight;
		
		// TODO: distance pursuer - target
		
		prio = proportionVel - proportionPos + proportionIntersection + proportionOrigin;
		return prio;
	}
	
	private ArrayList<Target> calcTargetsWithinPositions(double posA, double posB, double timestamp, double time,
			ArrayList<Target> targets, boolean axis1, boolean axis2) {

		ArrayList<Target> interceptedTargets = new ArrayList<>();

		for (Target t : targets) {
			
			double posTA = t.getUpdatedPos();
			
			// condition to be intercepted: posTA and posTB within posA & posB or
			// posTA >= (<=) posB and posTB within posA & posB when moving right (left)

			// same axis
			if ((axis1 == t.getLeftRightAxis() && axis2 == t.getLeftRightAxis()) || (posA == 0 && axis2 == t.getLeftRightAxis())) { 
				
				double timeToPos = pathTimeEquation(posB, 0, posA);
				double posTB = calcPositionAfterTime(t, timeToPos);
				boolean rightDirection = posA < posB ? true : false;
				
				if (rightDirection && (posTA > posA && posTB < posB)) { // right
					interceptedTargets.add(t);
				}
				if (!rightDirection && (posTA < posA && posTB > posB)) { // left
					interceptedTargets.add(t);
				}

			} else {
				// calc to intersection
				double timeToPos = pathTimeEquation(0, 0, posA);
				double posTB = calcPositionAfterTime(t, timeToPos);
				boolean rightDirection = posA < posB ? true : false;

				// posB is intersection -> 0
				if (axis1 == t.getLeftRightAxis()) {
					if (rightDirection && (posTA > posA && posTB < posB)) { // right
						interceptedTargets.add(t);
					}
					if (!rightDirection && (posTA < posA && posTB > posB)) { // left
						interceptedTargets.add(t);
					}
				}

				// calc from intersection to posB
				posTA = posTB;
				timeToPos = pathTimeEquation(posB, 0, 0); // TODO: check if this statement is correct
				posTB = calcPositionAfterTime(t, timeToPos);
				rightDirection = posTA < posTB ? true : false;

				// posA is intersection -> 0
				posA = 0;
				if (axis1 == t.getLeftRightAxis()) {
					if (rightDirection && (posTA > posA && posTB < posB)) { // right
						interceptedTargets.add(t);
					}
					if (!rightDirection && (posTA < posA && posTB > posB)) { // left
						interceptedTargets.add(t);
					}
				}
			}
		}

		return interceptedTargets;
	}

	public void setPriorityWeights(int velWeight, int distanceOriginWeight, int distancePosIntersectionWeight, int distancePosOriginWeight) {
		this.velWeight = velWeight;
		this.distanceOriginWeight = distanceOriginWeight;
		this.distancePosIntersectionWeight = distancePosIntersectionWeight;
		this.distancePosOriginWeight = distancePosOriginWeight;
	}
	
}
