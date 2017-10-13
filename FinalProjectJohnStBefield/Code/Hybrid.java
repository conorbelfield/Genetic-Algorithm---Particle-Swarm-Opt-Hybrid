import java.util.ArrayList;
import java.util.Arrays;

public class Hybrid {

	
	// initialization values: [minInitPos, maxInitPos, minInitVel, maxInitVel]
	private static double[] ROK_INIT_RANGES = new double[]{15.0, 30.0, -2.0, 2.0};
	private static double[] ACK_INIT_RANGES = new double[]{16.0, 32.0, -2.0, 4.0};
	private static double[] RAS_INIT_RANGES = new double[]{2.56, 5.12, -2.0, 4.0};
	private static double[] GRK_INIT_RANGES = new double[]{300.0, 600.0, -10.0, 10.0};
	private static double[] ZAK_INIT_RANGES = new double[]{15.0, 30.0, -2.0, 4.0};
	
	private static ArrayList<String> FUNCTIONS = 
			new ArrayList<String>(Arrays.asList("rok", "ack", "ras", "grk", "zak"));
	private static double[][] INIT_RANGES = new double[][]{ROK_INIT_RANGES, 
		ACK_INIT_RANGES, 
		RAS_INIT_RANGES, GRK_INIT_RANGES, ZAK_INIT_RANGES};
	
	// personal best acceleration coefficient
	private static double PHI_1 = 2.05;
	// global best acceleration coefficient
	private static double PHI_2 = 2.05;
	private static double K = 1.0;
	

	private int dim;
	private int numParticles;
	private int maxIterations;
	private double mutProb;
	private double crossProb;
	private double cutRatio;
	private String nbhdType;
	private String crossMethod;
	private String sel;
	
	//initializes values in a hybrid
	public Hybrid(double cutRatio, double mutProb, double crossProb,
			int numParticles, int dim, int maxIterations,
			String nbhdType, String crossMethod, String sel) {
		this.cutRatio = cutRatio;
		this.dim = dim;
		this.numParticles = numParticles;
		this.maxIterations = maxIterations;
		this.mutProb = mutProb;
		this.crossProb = crossProb;
		this.nbhdType = nbhdType;
		this.crossMethod = crossMethod;
		this.sel = sel;
	}
	
	//initializes some values in the hybrid to default values if it was only
	//called with three parameters of cut ratio, mutation probability
	//and crossover probability
	public Hybrid(double cutRatio, double mutProb, double crossProb) {
		this(cutRatio, mutProb, crossProb, 30, 30, 10000, "vn", "1c", "none");
	}

	//initializes default values if initialized only woth cut ratio
	public Hybrid(double cutRatio) {
		this(cutRatio, .05, 1.0);
		
	}
	
	//runs one trial of the hybrid with the given parameters 
	//from the initializer and the given function
	//functionNum corresponds to, in order
	//("rok", "ack", "ras", "grk", "zak")
	public double[][] trial(int functionNum) {
		
		// functionNum, cutRatio, gBest value, gBest iteration, runTime
		double[][] trialStats = new double[2][maxIterations/500];
		String fType = FUNCTIONS.get(functionNum);
		double minP = INIT_RANGES[functionNum][0];
		double maxP = INIT_RANGES[functionNum][1];
		double minV = INIT_RANGES[functionNum][2];
		double maxV = INIT_RANGES[functionNum][3];
	
		//creates function
		Function function = new Function(fType, this.dim, minP, maxP, minV, maxV, PHI_1, PHI_2, K);
		double mutLB = function.getMinInitPos();
		double mutUB = function.getMaxInitPos();
		
		final long startTime = System.nanoTime();
		
		//creates swarm
		Swarm swarm = new Swarm(this.numParticles, function);
		swarm.createNbhds(this.nbhdType);
		
		int iterationNumOfGBest = 0;
		
		//iterates through iterating swarm and creating
		//a GA of the cut ratio size each time and 
		//running it with parts of the swarm
		for (int i = 0; i < maxIterations; i++) {
	
			if (swarm.singleIteration()) {
				iterationNumOfGBest = i;
			}
			GA pop = new GA(swarm, this.cutRatio, i, this.maxIterations);
			pop.evolve(this.crossMethod, this.crossProb, this.mutProb, mutLB, mutUB, sel);
			if(i%500 == 0) {
				trialStats[1][i/500] = swarm.getGlobalBestValue();
			}
		}
		
		final double runTime = (System.nanoTime() - startTime) / 1000000000.0;
		
		trialStats[0][0] = swarm.getGlobalBestValue();
		trialStats[0][1] = this.cutRatio;
		trialStats[0][2] = functionNum;
		trialStats[0][3] = iterationNumOfGBest;
		trialStats[0][4] = runTime;
		
		return trialStats;

	}
}
