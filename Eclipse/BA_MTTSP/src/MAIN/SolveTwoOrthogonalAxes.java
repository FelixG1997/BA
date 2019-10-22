package MAIN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SolveTwoOrthogonalAxes {
	private int n;
	private int pursuerPos;
	private Integer pursuerAxis;
	private int pursuerVel;
	// getKey() -> Index 1 ; getKey() -> Index 2
	private ArrayList<Target> targetList = new ArrayList<>();
	
	public SolveTwoOrthogonalAxes(String inputFile) throws IOException {
		// Read input file
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		this.n = Integer.valueOf(in.readLine());
		String[] pursuerStart = in.readLine().split(" ");
		this.pursuerPos = Integer.valueOf(pursuerStart[0]);
		this.pursuerAxis = Integer.valueOf(pursuerStart[1]);
		this.pursuerVel = Integer.valueOf(in.readLine());
		String[] linePos = in.readLine().split(" ");
		String[] lineVel = in.readLine().split(" ");
		// 0: left-right-axis, 1: top-bottom-axis
		String[] lineAxis = in.readLine().split(" ");
		in.close();

		for (int i = 0; i < n; i++) {
			targetList.add(new Target(Integer.valueOf(linePos[i]), Integer.valueOf(lineVel[i]), Integer.valueOf(lineAxis[i])==0 ? true:false));
		}
	}
	
	public String prioApproach() {
		
		preprocessing();
		
		ArrayList<Target> OUTPUT = mainAlgorithm();
		
		OUTPUT = postprocessing(OUTPUT);
		
		String result = "-------------\nsolved";
		
		return result;
	}

	private void preprocessing() {
		
		// created, if further needed 
		
	}
	
	private ArrayList<Target> mainAlgorithm() {
		
		double[] time = new double[this.targetList.size()];
		// pursuer position at a target
		// init with origin
		Target current = new Target(this.pursuerPos, 0, this.pursuerAxis == 0? true:false);
		// to return to the origin at the end of the tour
		Target finalTarget = new Target(this.pursuerPos, 0, this.pursuerAxis == 0? true:false);
		ArrayList<Target> OUTPUT = new ArrayList<>();
		OUTPUT.add(current);
		
		// PriorityQueue by Java does not update, if the comperable value (t_i.getPrio()) changes
		ArrayList<Target> prioQ = new ArrayList<>();
		
		for(int i=0; i<this.targetList.size(); i++) {
			time[i] = Double.MAX_VALUE;
			prioQ.add(this.targetList.get(i));
		}
		
		double timestamp = 0;
		Target prev;
		
		while(!prioQ.isEmpty()) {
			// if all remaining targets in PrioQ are located on one side of the intersection,
			// once the pursuer reached the intersection from current then
			// calc time to reach intereception
			ArrayList<Target> tmp = interceptAllTargetsOfOneSide(current.getUpdatedPos(), timestamp, prioQ);
			if(!tmp.isEmpty()) {
				OUTPUT.addAll(tmp);
				System.out.println(OUTPUT);
				break;
			} 
			
			// TODO: calc prio of each target in prio
			
			
			Collections.sort(prioQ, Target.compPrio);
			prev = current;
			// prioQ.poll()
			current = prioQ.get(0);
			prioQ.remove(0);
			
			// TODO: calc time from prev to current
			
			// TODO: calc new position of each target
			
			// TODO: calc targets between prev and current
			
						
			// sort in order of nondecreasing interception times
			Collections.sort(prioQ, Target.compTime);
			
//			System.out.println(prioQ);
			
		}
		
		// TODO: calc time from last target to finalTarget
		
		
		return OUTPUT;		
	}
	
	private ArrayList<Target> postprocessing(ArrayList<Target> OUTPUT) {
		// TODO Auto-generated method stub
		return OUTPUT;
	}
	
	// return null, condition is false
	private ArrayList<Target> interceptAllTargetsOfOneSide(double pos, double timestamp, ArrayList<Target> prioQ){
		
		double timeToIntersection = calcTimeToIntersection(pos);
		double[] tmpPositions = new double [prioQ.size()];			
		boolean sameSide = true;
		for(int i=0; i<prioQ.size(); i++) {
			tmpPositions[i] = calcPositionAfterTime(prioQ.get(i), timeToIntersection);
			if(i!=0 && !((tmpPositions[i-1] < 0 && tmpPositions[i] < 0) || (tmpPositions[i-1] > 0 && tmpPositions[i] > 0)) && 
					prioQ.get(i-1).getLeftRightAxis() == prioQ.get(i).getLeftRightAxis()) {
				sameSide = false;
				break;
			}	
		}
		ArrayList<Target> targets = new ArrayList<>();
		if(sameSide) {
			for(int i=0; i<prioQ.size(); i++) {
				targets.add(prioQ.get(i));
				double time = timeToInterceptTarget(tmpPositions[i], prioQ.get(i).getVel(), 0) + timestamp + timeToIntersection;
				System.out.println(time);
				targets.get(i).setInterceptingTime(time);
			}
		}
		return targets;
		
		
	}
	
	private double calcTimeToIntersection(double pos) {
		
		// pursuer needs to move left, if the target is left of the pursuer
		int vpur = pos < 0 ? this.pursuerVel*-1:this.pursuerVel; 
		double time = Math.abs(pos / vpur);

		return time;
	}
	
	private double calcPositionAfterTime(Target t, double time) {
		
		double newPos = t.getUpdatedPos() + t.getVel()*time;
		
		return newPos;
	}
	
	// tpos tvel 
	private double timeToInterceptTarget(double tPos, double tVel, double pursuerPos) {
		
		double time;
		
		if(pursuerPos==0) {
			int vpur = tPos < 0 ? this.pursuerVel*-1:this.pursuerVel; 
			time = Math.abs((tPos) / (vpur - tVel));
		} else {
			// TODO!
			time = -1;
		}
		
		return time;
	}
	
	private void updatePrio(double currentPursuerPosition, boolean pursuerOnLeftRightAxis, double timeStamp) {
		
	}
	
}
