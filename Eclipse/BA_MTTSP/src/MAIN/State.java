package MAIN;

public class State implements Comparable<State> {

	private int skIndex;
	private int sfIndex;
	private int stateIndex;
	private Target sk;
	private Target sf;
	private boolean isStart = false;
	private boolean isEmpty = false;
	private boolean isLeft;
	private Target furthestAway = null;
	// shortest time in which this state can be achieved
	private double shortestTime = Double.MAX_VALUE;
	
	public State(Target sk, Target sf, int stateIndex, int skIndex, int sfIndex, boolean isLeft) {
		this.sk = sk;
		this.sf = sf;
		this.skIndex = skIndex;
		this.sfIndex = sfIndex;
		this.stateIndex = stateIndex;
		this.isLeft = isLeft;
	}
	
	/**
	 * determines, if sk is on the left or right side of the origin
	 * @return true: left, false: right
	 */
	public boolean getIsLeft() {
		return isLeft;
	}

	/**
	 * Constructor for A_0 and A_final
	 * @param isStart true: A_0, false: A_final
	 */
	public State(boolean isStart, int stateIndex) {
		this.isStart = isStart;
		this.isEmpty = true;
		this.stateIndex = stateIndex;
	}
	
	public int getSkIndex() {
		return skIndex;
	}

	public int getSfIndex() {
		return sfIndex;
	}

	public int getStateIndex() {
		return stateIndex;
	}

	public void setShortestTime(double t) {
		this.shortestTime = t;
	}
	
	public double getShortestTime() {
		return this.shortestTime;
	}
	
	/**
	 * getter for isStart, true: A_0, false: anything else
	 * @return isStart A_0
	 */
	public boolean isStart() {
		return isStart;
	}

	/**
	 * getter Sk
	 * @return current target
	 */
	public Target getSk() {
		return sk;
	}

	/**
	 * getter Sf
	 * @return next target
	 */
	public Target getSf() {
		return sf;
	}
	
	public Target getFurthestAway() {
		return furthestAway;
	}

	public void setFurthestAway(Target furthestAway) {
		this.furthestAway = furthestAway;
	}
	
	@Override
	public int compareTo(State otherState) {
		return this.skIndex + this.sfIndex < otherState.getSkIndex() + otherState.getSfIndex() ? -1 : (this.skIndex + this.sfIndex >  otherState.getSkIndex() + otherState.getSfIndex() ? 1 : 0);
	}
	
	@Override
    public String toString() {
		String tmp = " - shortest time: ";
		tmp += shortestTime == -1? "Not calculated yet" : String.valueOf(shortestTime);
		if(!isEmpty) {
			return "{(" + this.sk.getPos() + ", " + this.sk.getVel() + "), (" + this.sf.getPos() + ", " + this.sf.getVel() + "), StateIndex: " + 
		           this.stateIndex + ", s_kIndex: " + this.skIndex + ", s_fIndex: " + this.sfIndex + ", SkIsLeft: " + this.isLeft + "}"  + tmp;
		}
		else if(isStart) {
			return "A_0, StateIndex: 0" + tmp;
		} 
		else {
			return "A_final, StateIndex: " + this.stateIndex + tmp;
		}
		
	}
	
}
