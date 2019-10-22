package MAIN;

public final class Constants {

	/**
	 * Determines 1D or 2OA case
	 */
	public static final boolean is1D = false;
	/**
	 * True: Generate new Input, False: use Input with specific File
	 */
	public static final boolean generateNewInput = false;
	/**
	 * Number of moving targets
	 */
	public static final int n = 5;

	/**
	 * Pursuer start / origin
	 */
	public static final int purPos = 0;

	/**
	 * Value of max pursuer speed
	 */
	public static final int vMax = 18;

	/**
	 * Limit of the x coordinate
	 */
	public static final int limitCoord = 50;

	/**
	 * Number of corridors
	 */
	public static final int corridors = 2;
	
	/**
	 * Axis start of the pursuer
	 * true: left-right, false: top-bot
	 */
	public static final boolean pursuerStartsOnLeftRightAxis = true;
	
}