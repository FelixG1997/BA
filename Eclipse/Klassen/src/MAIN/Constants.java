package MAIN;

/**
 * 
 * @author FelixGreuling
 *
 */
public final class Constants {

	/**
	 * Determines 1D or 2OA case
	 */
	public static final boolean IS_1D = true;
	/**
	 * True: Generate new Input, False: use Input with specific File
	 */
	public static final boolean GENERATE_NEW_INPUT = false;
	/**
	 * Specific input file for 1D-cases, if generateNewInput=false
	 */
	public static String INPUTFILE_1D = "1D_0.txt";
	/**
	 * Specific input file for 2OA-cases, if generateNewInput=false
	 */
	public static String INPUTFILE_2OA = "2OA_0.txt";
	/**
	 * True: Calc optimal tour with Brute Force
	 */
	public static boolean USE_BRUTE_FORCE = false;
	/**
	 * True: Print respective Steps and Targets of the different approaches
	 */
	public static final boolean PRINT_TARGETS_AND_STEPS = false;
	/**
	 * Number of moving targets
	 */
	public static final int N = 10;
	/**
	 * Pursuer start / origin
	 */
	public static final int PURSUER_POS = 0;
	/**
	 * Value of max pursuer speed
	 */
	public static final int V_PURSUER = 40;
	/**
	 * Maximum speed of each target
	 */
	public static final int V_I_MAX = 20;
	/**
	 * Limit of the x coordinate
	 */
	public static final int LIMIT_COORD = 10000;
	/**
	 * Axis start of the pursuer
	 * true: left-right, false: top-bot
	 */
	public static final boolean PURSUER_STARTS_ON_LEFTRIGHT_AXIS = true;
	/**
	 * weights for prioApproach
	 */
	public static final int[] WEIGHTS = {87,34,31}; // 9,3,8 |48,2,4 |75,9,17|90,30,80|87,34,31
	/**
	 * sets velocities
	 */
	public static final boolean CONSTANT_VELOCITIES = false;
	
	// ---------------------------------------------------------
	// --------------------TESTING Variables--------------------
	// ---------------------------------------------------------
	
	/**
	 * Enables testing of the 2OA method in combination with Brute-Force
	 */
	public static boolean TESTING_ENABLED = false;
	/**
	 * True: Use a Seq_x.txt Input to calculate a specific sequence
	 */
	public static final boolean CALC_SPECIFIC_SEQUENCE = false;
	/**
	 * Enables testing of multiple 2OA Instances without Brute-Force to calculate suitable weights
	 */
	public static boolean TEST_PRIO_WITH_MULTIPLE_INSTANCES = true;
	/**
	 * Enables testing of multiple 2OA Instances with Brute-Force to determine approximations
	 */
	public static boolean TEST_BF_WITH_MULTIPLE_INSTANCES = false;
	/**
	 * Test random weights and check, if they create an optimal solution for a given inputFile
	 */
	public static final boolean TEST_WEIGHTS = false;
	/**
	 * Calculate the quality of the Prio-Alg with prio(I)/opt(I)
	 */
	public static boolean TEST_PRIO_QUALITY = false;
	/**
	 * number of test iterations
	 */
	public static int TEST_ITERATIONS = 1000;
	/**
	 * number of test instances
	 */
	public static int TEST_INSTANCES = 100;
	/**
	 * maximum value of each random weight
	 */
	public static int LIMIT_RANDOM_WEIGHTS = 100;
}