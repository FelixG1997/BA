package MAIN;

import javafx.util.Pair;

public class State implements Comparable<State> {

	private int skIndex;
	private int sfIndex;
	private int totalIndex;
	private Pair<Integer, Integer> s_k;
	private Pair<Integer, Integer> s_f;
	private int mintime = Integer.MAX_VALUE;
	private boolean isStart = false;
	private boolean isEmpty = false;
	private boolean isLeft;
	
	public State(Pair<Integer, Integer> sk, Pair<Integer, Integer> sf, int skIndex, int sfIndex, boolean isLeft) {
		this.s_k = sk;
		this.s_f = sf;
		this.skIndex = skIndex;
		this.sfIndex = sfIndex;
		this.totalIndex = this.skIndex + this.sfIndex;
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
	public State(boolean isStart) {
		this.isStart = isStart;
		this.isEmpty = true;
	}
	
	public int getSkIndex() {
		return skIndex;
	}

	public int getSfIndex() {
		return sfIndex;
	}

	public int getTotalIndex() {
		return totalIndex;
	}

	/**
	 * getter for isStart, true: A_0, false: anything else
	 * @return isStart A_0
	 */
	public boolean isStart() {
		return isStart;
	}

	/**
	 * getter S_k
	 * @return current target
	 */
	public Pair<Integer, Integer> getSk() {
		return s_k;
	}

	/**
	 * getter S_f
	 * @return next target
	 */
	public Pair<Integer, Integer> getSf() {
		return s_f;
	}
	
	public int getMintime() {
		return mintime;
	}

	public void setMintime(int mintime) {
		this.mintime = mintime;
	}
	
	@Override
	public int compareTo(State otherState) {
		return this.totalIndex < otherState.getTotalIndex() ? -1 : (this.totalIndex >  otherState.getTotalIndex()? 1 : 0);
	}
	
	@Override
    public String toString() {
		if(!isEmpty) {
			return "{(" + this.s_k.getKey() + ", " + this.s_k.getValue() + "), (" + this.s_f.getKey() + ", " + this.s_f.getValue() + "), TotalIndex: " + 
		           this.totalIndex + ", s_kIndex: " + this.skIndex + ", s_fIndex: " + this.sfIndex + ", SkIsLeft: " + this.isLeft + "}" ;
		}
		else if(isStart) {
			return "A_0" ;
		} 
		else {
			return "A_final";
		}
		
	}
	
}
