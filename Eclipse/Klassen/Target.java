package MAIN;

import java.util.Comparator;

public class Target {

	private int pos;
	private int vel;
	private boolean isLeft;
	private double interceptingTime = -1;
	private boolean leftRightAxis = true;
	private double prio = 0;
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

	public void setInterceptingTime(double t) {
		this.interceptingTime = t;
	}

	public double getInterceptingTime() {
		return this.interceptingTime;
	}

	public int getPos() {
		return this.pos;
	}

	public int getVel() {
		return this.vel;
	}

	public boolean isLeft() {
		return this.isLeft;
	}

	public boolean getLeftRightAxis() {
		return this.leftRightAxis;
	}

	public double getPrio() {
		return prio;
	}

	public void setPrio(double prio) {
		this.prio = prio;
	}

	public double getUpdatedPos() {
		return this.updatedPos;
	}

	public void setUpdatePos(double pos) {
		this.updatedPos = pos;
	}

	public boolean sameSide(Target t2) {
		if (((this.updatedPos < 0 && t2.getUpdatedPos() < 0) || (this.updatedPos > 0 && t2.getUpdatedPos() > 0))
				&& this.leftRightAxis == t2.getLeftRightAxis()) {
			return true;
		}
		return false;
	}

	public static Comparator<Target> compVel = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			if (t1.isLeft)
				return t1.getVel() < t2.getVel() ? -1 : (t1.getVel() > t2.getVel() ? 1 : 0);
			else 
				return t1.getVel() > t2.getVel() ? -1 : (t1.getVel() < t2.getVel() ? 1 : 0);
	}};
	
	public static Comparator<Target> compTime = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			return t1.getInterceptingTime() < t2.getInterceptingTime() ? -1 : (t1.getInterceptingTime() > t2.getInterceptingTime() ? 1 : 0);

	}};
	
	public static Comparator<Target> compPrio = new Comparator<Target>() {
		public int compare(Target t1, Target t2) {
			return t1.getPrio() > t2.getPrio() ? -1 : (t1.getPrio() < t2.getPrio() ? 1 : 0);
	}};
	
	@Override
	public String toString() {
		String tmp = !Constants.is1D ? "L-R-Axis: " + this.leftRightAxis + ", " : "";
		tmp += interceptingTime == Double.MAX_VALUE ? "Not calculated yet" : String.valueOf(interceptingTime);
		return "(" + this.pos + ", " + this.vel + ") - Intercepting time: " + tmp;
	}

	

}