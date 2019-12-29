package MAIN;

/**
 * 
 * @author FelixGreuling
 *
 */
public class State implements Comparable<State> {

	/**
	 * sk index
	 */
	private int skIndex;
	/**
	 * sf index
	 */
	private int sfIndex;
	/**
	 * state index = skIndex + sfIndex
	 */
	private int stateIndex;
	/**
	 * sk target
	 */
	private Target sk;
	/**
	 * sf target
	 */
	private Target sf;
	/**
	 * true: A_0, false: A_final
	 */
	private boolean isStart = false;
	/**
	 * true: A_0 or A_final, else usual state
	 */
	private boolean isEmpty = false;
	/**
	 * sk starts on the left side of the origin
	 */
	private boolean isLeft;
	/**
	 * target, which is the furthest away from the current target
	 * important for tau_final
	 */
	private Target furthestAway = null;
	/**
	 * shortest time in which this state can be achieved
	 */
	private double shortestTime = Double.MAX_VALUE;
	
	/**
	 * usual Constructor
	 * @param sk
	 * @param sf
	 * @param stateIndex skIndex+sfIndex
	 * @param skIndex
	 * @param sfIndex
	 * @param isLeft sk starts on the left side of the origin
	 */
	public State(Target sk, Target sf, int stateIndex, int skIndex, int sfIndex, boolean isLeft) {
		this.sk = sk;
		this.sf = sf;
		this.skIndex = skIndex;
		this.sfIndex = sfIndex;
		this.stateIndex = stateIndex;
		this.isLeft = isLeft;
	}
	/**
	 * Constructor for A_0 and A_final
	 * @param isStart true: A_0, false: A_final
	 */
	public State(boolean isStart, int stateIndex) {
		this.isStart = isStart;
		this.isEmpty = true;
		this.stateIndex = stateIndex;
		this.sk = null;
		this.sf = null;
	}
	
	// --------------------------Getter & Setter---------------------------
	/**
	 * determines, if sk is on the left or right side of the origin
	 * @return true: left, false: right
	 */
	public boolean getIsLeft() {
		return isLeft;
	}
	/**
	 * getter skIndex
	 * @return skIndex
	 */
	public int getSkIndex() {
		return skIndex;
	}
	/**
	 * getter sfIndex
	 * @return sfIndex
	 */
	public int getSfIndex() {
		return sfIndex;
	}
	/**
	 * getter stateIndex
	 * @return stateIndex
	 */
	public int getStateIndex() {
		return stateIndex;
	}
	/**
	 * setter shortest time 
	 * @param t time
	 */
	public void setShortestTime(double t) {
		this.shortestTime = t;
	}
	/**
	 * getter shortest time
	 * @return shortestTime
	 */
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
	/**
	 * getter for the target, which is the most furthest away from the current target
	 * @return furthestAway
	 */
	public Target getFurthestAway() {
		return furthestAway;
	}
	/**
	 * setter for the target, which is the most furthest away from the current target
	 * @param furthestAway
	 */
	public void setFurthestAway(Target furthestAway) {
		this.furthestAway = furthestAway;
	}
	// ------------------------------------------------------------------------------
	
	@Override
	/**
	 * Comperator for state indices
	 */
	public int compareTo(State otherState) {
		return this.stateIndex < otherState.getStateIndex() ? -1 : (this.stateIndex > otherState.getStateIndex() ? 1 : 0);
	}
	
	@Override
	/**
	 * overwritten toString method
	 */
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
