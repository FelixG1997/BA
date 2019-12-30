package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class just appears for testcases
 * @author FelixGreuling
 *
 */
public class CalcSequence {
	/**
	 * target list in a spec. order
	 */
	private ArrayList<Target> targetList = new ArrayList<>();
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
	 * Constructor
	 * @param inputFile 22OA_<x>.txt
	 * @throws IOException invalid input file
	 */
	public CalcSequence(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		int n = Integer.valueOf(in.readLine());
		String[] pursuerStart = in.readLine().split(" ");
		origin = Integer.valueOf(pursuerStart[0]);
		pursuerAxis = Integer.valueOf(pursuerStart[1]) == 0? true : false;
		pursuerVel = Integer.valueOf(in.readLine());
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
	 * Intercepts each Target in the specified order
	 * @return pathtime
	 */
	public ArrayList<Target> getPathTime(){
		
		Target start = new Target(origin, 0, pursuerAxis);
		Target end = new Target(origin, 0, pursuerAxis);
		
		double timestamp = 0;
		start.setInterceptingTime(timestamp);
		
		Target current;
		Target prev = start;
		
		ArrayList<Target> OUTPUT = new ArrayList<>();
		OUTPUT.add(start);
		
		while(!this.targetList.isEmpty()) {
			
			current = this.targetList.get(0);
			this.targetList.remove(0);
			
			double time = timeToInterceptTarget(current.getUpdatedPos(), current.getVel(), prev.getUpdatedPos(), prev.getLeftRightAxis(), current.getLeftRightAxis());
			
			for(Target t : this.targetList) {
				t.setUpdatePos(calcPositionAfterTime(t, time));
			}
			
			timestamp += time;
			
			current.setInterceptingTime(timestamp);
			current.setUpdatePos(calcPositionAfterTime(current, time));
			
			OUTPUT.add(current);
		
			prev = current;
		}
		
		timestamp += timeToInterceptTarget(end.getUpdatedPos(), 0, prev.getUpdatedPos(), prev.getLeftRightAxis(), end.getLeftRightAxis());
		end.setInterceptingTime(timestamp);
		OUTPUT.add(end);
		
		return OUTPUT;
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
}
