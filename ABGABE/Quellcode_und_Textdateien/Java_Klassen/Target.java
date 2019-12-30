package MAIN;

import java.util.Comparator;

/**
 * 
 * @author FelixGreuling
 *
 */
public class Target {

	/**
	 * start coord of the target
	 */
	private int pos;
	/**
	 * velocity of the target
	 */
	private int vel;
	/**
	 * is on the left side of the origin
	 * false - right
	 */
	private boolean isLeft;
	/**
	 * intercepting time in the tour
	 */
	private double interceptingTime = -1;
	/**
	 * for 2D cases
	 * true - horizontal axis
	 * false - vertical axis
	 */
	private boolean leftRightAxis = true;
	/**
	 * for 2D cases
	 * priority value of the target
	 */
	private double prio = 0;
	/**
	 * for the prio approach
	 * position for a spec. timestamp
	 */
	private double updatedPos;

	/**
	 * Constructor for the 1D-case
	 * 
	 * @param pos initial Position
	 * @param vel initial velocitiy - neg. left, pos. right
	 */
	public Target(int pos, int vel) {
		this.pos = pos;
		this.vel = vel;
		isLeft = pos < 0 ? true : false;
	}

	/**
	 * Constructor for the two-ortogonal-case
	 * 
	 * @param pos           initial Position
	 * @param vel           initial velocitiy - neg. bottom, pos. top
	 * @param leftRightAxis target moves on the left-right-axis
	 */
	public Target(int pos, int vel, boolean leftRightAxis) {
		this.pos = pos;
		this.updatedPos = (double) pos;
		this.vel = vel;
		this.leftRightAxis = leftRightAxis;
	}

	// -----------------------------GETTER & SETTER------------------------------------
	
	/**
	 * setter intercepting time
	 * @param t time
	 */
	public void setInterceptingTime(double t) {
		this.interceptingTime = t;
	}
	/**
	 * getter intercepting time
	 * @return interccepting time
	 */
	public double getInterceptingTime() {
		return this.interceptingTime;
	}
	/**
	 * getter position
	 * @return position
	 */
	public int getPos() {
		return this.pos;
	}
	/**
	 * getter velocity
	 * @return velocity
	 */
	public int getVel() {
		return this.vel;
	}
	/**
	 * getter boolean left side of the origin
	 * @return isLeft
	 */
	public boolean isLeft() {
		return this.isLeft;
	}
	/**
	 * getter axis
	 * @return leftRightAxis
	 */
	public boolean getLeftRightAxis() {
		return this.leftRightAxis;
	}
	/**
	 * getter priority value
	 * @return prio
	 */
	public double getPrio() {
		return prio;
	}
	/**
	 * setter priority value
	 * @param prio new priority
	 */
	public void setPrio(double prio) {
		this.prio = prio;
	}
	/**
	 * getter updated position
	 * @return updatedPos
	 */
	public double getUpdatedPos() {
		return this.updatedPos;
	}
	/**
	 * setter for the new position
	 * @param pos new position
	 */
	public void setUpdatePos(double pos) {
		this.updatedPos = pos;
	}
	// --------------------------------------------------------------------------------
	
	/**
	 * checks, if two targets are on the same side of the intersection 
	 * @param t2 second target
	 * @return true: same side, false: other side
	 */
	public boolean sameSide(Target t2) {
		if (((this.updatedPos < 0 && t2.getUpdatedPos() < 0) || (this.updatedPos > 0 && t2.getUpdatedPos() > 0))
				&& this.leftRightAxis == t2.getLeftRightAxis()) {
			return true;
		}
		return false;
	}
	
	/**
	 * comperator in order of velocities
	 */
	public static Comparator<Target> compVel = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			if (t1.isLeft)
				return t1.getVel() < t2.getVel() ? -1 : (t1.getVel() > t2.getVel() ? 1 : 0);
			else 
				return t1.getVel() > t2.getVel() ? -1 : (t1.getVel() < t2.getVel() ? 1 : 0);
	}};
	/**
	 * comperator in order of intercepting times
	 */
	public static Comparator<Target> compTime = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			return t1.getInterceptingTime() < t2.getInterceptingTime() ? -1 : (t1.getInterceptingTime() > t2.getInterceptingTime() ? 1 : 0);

	}};
	/**
	 * comperator in order of priorities
	 */
	public static Comparator<Target> compPrio = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			return t1.getPrio() > t2.getPrio() ? -1 : (t1.getPrio() < t2.getPrio() ? 1 : 0);
	}};
	
	/**
	 * overwritten toString method
	 */
	@Override
	public String toString() {
		if(Constants.IS_1D) {
			return "(" + this.pos + ", " + this.vel + "), Intercepting time: " + (interceptingTime == -1 ? "Not calculated yet" : String.valueOf(interceptingTime));
		}
		return "(" + this.pos + ", " + this.vel + ", LR-Axis: " + this.leftRightAxis + "), Intercepting time: " + (interceptingTime == -1 ? "Not calculated yet" : String.valueOf(interceptingTime));
	}

	

}