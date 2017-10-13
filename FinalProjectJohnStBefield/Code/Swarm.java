/*
 * This file implements the swarm in the PSO
 * it provides the functions to creates neighborhoods
 * and divide particles into them, to iterates
 * through the neighborhoods and update their best
 * values global wise and on the neighborhood level
 * 
 */

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Swarm {

	private int numParticles;
	public Particle[] swarm;
	private Function function;
	private String nbhdType; // i.e. "gl", "ra"
	private int numNbhds; // determined by numParticles and neighborhood type
	public Particle[][] nbhds; // nbhds are arrays of particles

	// index of each array in nbhdBests corresponds to same index array in
	// nbhds
	public double[][] nbhdBestPositions; // each array contains the solution for that nbhd
	public double[] nbhdBestValues; // each index is the value of the solution. indices correspond
	
	public double globalBestValue = 2147199999; 
	public double[] globalBestPosition; // array of the solution

	private int VN_DIST = 1; // von Neumann distance for vN neighborhoods
	private int RING_NBHD_SZ = 3; // rings of size 3
	private int RAND_NBHD_SZ = 5; // random nbhds of size 5
	// prob that in an iteration, a particle recreates its nbhd (nbhdType =
	// "ra")
	private double PROB_NEW_RAND_NBHD = 0.2;

	private Random rand = new Random();

	//New constructor so we can define the swarm
	public Swarm(int numParticles, Function function, Particle[] swarm) {
		this.function = function;
		this.numParticles = numParticles;
		this.swarm = swarm;
	}
	
	public Swarm(int numParticles, Function function) {
		this.function = function;
		this.numParticles = numParticles;
		this.swarm = new Particle[numParticles];
		for (int i = 0; i < numParticles; i++) {
			this.swarm[i] = new Particle(function);
		}

	}

	public void createNbhds(String nbhd) {
		if (nbhd.equals("gl")) {
			this.createGlNbhd();
		}
		if (nbhd.equals("ra")) {
			this.createRaNbhds();
		}
		if (nbhd.equals("ri")) {
			this.createRiNbhds();
		}
		if (nbhd.equals("vn")) {
			this.createVnNbhds(this.createParticleGrid());
		}
		this.nbhdBestValues = new double[this.numNbhds];
		this.nbhdBestPositions = new double[this.numNbhds][this.function.getDimensions()];

		// updates neighborhood and global bests
		this.preIterationSetup();
	}

	// updates nhbd and global bests
	public void preIterationSetup() {
		// global nbhd ignores nbhd update method
		if (!(this.nbhdType).equals("gl")) {
			this.updateNbhdBests();
		}
		this.updateGlobalBest();
	}

	// Global Topology
	public void createGlNbhd() {
		this.numNbhds = 1;
		this.nbhdType = "gl";

		this.nbhds = new Particle[this.numNbhds][this.numParticles];
		this.nbhds[0] = this.swarm;

	}

	// random
	public void createRaNbhds() {

		this.numNbhds = this.numParticles;
		this.nbhdType = "ra";

		this.nbhds = new Particle[this.numNbhds][RAND_NBHD_SZ];

		// create random neighborhoods with probability 1
		this.randomizeNbhds(1.0);
	}

	// randomize neighborhoods for particles
	public void randomizeNbhds(double probNewRandNbhd) {
		for (int i = 0; i < this.numParticles; i++) {
			if (rand.nextDouble() < probNewRandNbhd) {
				this.createRaNbhdForSingleParticle(i);
			}
		}
	}

	public void createRaNbhdForSingleParticle(int indexOfParticle) {

		// keep track of indices to ensure duplicate particles are not added to
		// nbhd
		ArrayList<Integer> indicesInCurrNbhd = new ArrayList<Integer>();

		// add seed particle to its own nbhd
		indicesInCurrNbhd.add(indexOfParticle);
		this.nbhds[indexOfParticle][0] = swarm[indexOfParticle];

		for (int i = 1; i < RAND_NBHD_SZ; i++) {
			int possibleIndex = rand.nextInt(this.numParticles);

			// generate new int index if it is already in the nbhd
			while (indicesInCurrNbhd.contains(possibleIndex)) {
				possibleIndex = rand.nextInt(this.numParticles);
			}

			// if unique int index, add the particle to nbhd
			indicesInCurrNbhd.add(possibleIndex);
			this.nbhds[indexOfParticle][i] = swarm[possibleIndex];
		}

	}

	//finds most fit particle and returns its index
	public int findBestIndex(Particle[] particles) {
		int interimBestIndex = 0;
		double interimBestValue = particles[0].getValueOfIndivBest();
		
		for (int i = 1; i < particles.length; i++) {
			if (particles[i].getValueOfIndivBest() < interimBestValue) {
				interimBestIndex = i;
			}
		}
		return interimBestIndex;

	}
	
	//loops through the values and checks if there is a new global best
	public boolean updateGlobalBest() {

		int iterationBestIndex = this.findBestIndex(this.swarm);

		double iterationBestValue = this.swarm[iterationBestIndex].getValueOfIndivBest();
		if (iterationBestValue < this.globalBestValue) {
			this.globalBestValue = iterationBestValue;
			this.globalBestPosition = this.swarm[iterationBestIndex].getIndivBestPos();
			return true;
		}
		return false;
	}

	//checks if there was a neww neighborhood best
	public void updateNbhdBests() {
		for (int i = 0; i < this.numNbhds; i++) {
			int nbhdBestIndex = this.findBestIndex(this.nbhds[i]);
			this.nbhdBestValues[i] = this.nbhds[i][nbhdBestIndex].getValueOfIndivBest();
			this.nbhdBestPositions[i] = this.nbhds[i][nbhdBestIndex].getIndivBestPos();
		}
	}

	//iterates once depending on neighborhood type
	public boolean singleIteration() {
		if ((this.nbhdType).equals("gl")) {
			return singleIterationGL();
		}
		if ((this.nbhdType).equals("ra")) {
			return singleIterationRA();
		}
		if ((this.nbhdType).equals("ri")) {
			return singleIterationRI();
		}
		if ((this.nbhdType).equals("vn")) {
			return singleIterationRI();
		}
		return false; // should never get here
	}
	
	//global neighborhood iteration
	public boolean singleIterationGL() {
		
		// update particles
		for (int i = 0; i < this.numParticles; i++) {
			this.swarm[i].singleIteration(this.globalBestPosition);
		}
		return this.updateGlobalBest(); // true if new best found
	}
	
	// iteration for random neighborhood. returns true if the iteration produces
	// new gBest
	public boolean singleIterationRA() {
		this.randomizeNbhds(PROB_NEW_RAND_NBHD);
		return this.singleIterationRI();
	}

	// iteration for topologies that are neither global nor random
	public boolean singleIterationRI() {
		// update particles
		for (int i = 0; i < this.numParticles; i++) {
			// nbhd best of the nbhd that particle is the master of
			this.swarm[i].singleIteration(this.nbhdBestPositions[i]);
		}
		this.updateNbhdBests();
		return this.updateGlobalBest(); // true if new best found
	}

	public String toString() {
		String string1 = "numParticles: " + this.numParticles + "; Nbhd Type: " + this.nbhdType;
		String string2 = "\nnumNbhds: " + this.nbhds.length + "; Nbhd size: " + this.nbhds[0].length;

		String string = string1 + string2;
		return string;
	}
	
	//creates Ring Topology
		public void createRiNbhds() {
			this.numNbhds = this.numParticles;
			this.nbhdType = "ri";

			this.nbhds = new Particle[this.numNbhds][RING_NBHD_SZ];

			for (int i = 0; i < this.numParticles - (RING_NBHD_SZ - 1); i++) {
				nbhds[i] = Arrays.copyOfRange(this.swarm, i, i + RING_NBHD_SZ);
			}
			for (int i = this.numParticles - (RING_NBHD_SZ - 1); i < this.numParticles; i++) {
				for (int j = 0; j < RING_NBHD_SZ; j++) {
					nbhds[i][j] = swarm[(i + j) % this.numParticles];
				}
			}

		}
		
		// auxiliary method for von Neumann. creates the grid of particles
		public Particle[][] createParticleGrid() {

			// initialize grid dimensions: (n, n+1)
			// Math.ceil returns upper bound on grid column size
			int numCols = (int) Math.ceil(Math.sqrt(this.numParticles));
			int numRows = numCols - 1;
			
			// check to see if grid row size is insufficient. if so, turn that baby
			// into a square
			if (this.numParticles > numCols * numRows) {
				numRows += 1;
			}

			// loop to create grid
			Particle[][] grid = new Particle[numRows][numCols];
			int indexOfCurrParticle = 0;
			for (int r = 0; r < numRows; r++) {
				
				// fill by column because possibly more columns than rows
				// i.e. filling by column ensures the grid is as square as possible
				for (int c = 0; c < numCols; c++) {
					if (indexOfCurrParticle < this.numParticles) {
						grid[r][c] = swarm[indexOfCurrParticle];
						indexOfCurrParticle++;
					}
				}
			}
			return grid;

		}

		// creates von neumann neighborhood for a single particle
		public void createVnNbhdForSingleParticle(Particle[][] grid, int indexOfParticle) {

			// find coordinates of particle in the grid
			int r = indexOfParticle / grid[0].length;
			int c = indexOfParticle % grid[0].length;

			// put the seed particle in its own neighborhood
			nbhds[indexOfParticle][0] = grid[r][c];
			int currNbhdSize = 1;

			// fill the neighborhood w.r.t. von Neumann for each vnDist
			// i.e. all particles that are of taxicab distance of vnDist away
			for (int vnDist = 1; vnDist <= VN_DIST; vnDist++) {
				

				// inc1 starts at max vnDist, inc2 at 0. can think of these two
				// distances
				// as vertical and horizontal distance. they must always add up to
				// vnDist
				int inc1 = vnDist;
				int inc2 = 0;

				// check each combo of vertical and horizontal distance
				// each loop will increment inc1 by -1 and inc2 by 1, and then check
				// all 4 quadrants
				// around the seed particle (i.e. 4 particles are added to the nbhd
				// per loop)
				while (inc2 < vnDist) {

					// essentially, given a point in quadrant 1, we are finding its
					// mirror images
					// of 90 degree rotations around the seed point.

					// quadrant 1
					nbhds[indexOfParticle][currNbhdSize] = 
							grid[(inc1 + grid.length + r) % grid.length][(inc2 + grid[0].length + c)
					               % grid[0].length];

					// quadrant 2
					nbhds[indexOfParticle][currNbhdSize + 1] = 
							grid[(-inc1 + grid.length + r) % grid.length][(-inc2 + grid[0].length + c)
									% grid[0].length];

					// quadrant 3
					nbhds[indexOfParticle][currNbhdSize + 2] = 
							grid[(inc2 + grid.length + r) % grid.length][(-inc1 + grid[0].length + c)
									% grid[0].length];

					// quadrant 4
					nbhds[indexOfParticle][currNbhdSize + 3] = 
							grid[(-inc2 + grid.length + r) % grid.length][(inc1 + grid[0].length + c)
									% grid[0].length];

					// we added 4 particles to the nbhd
					currNbhdSize += 4;

					// change increments to check the next combo of
					// vertical/horizontal distance
					inc1--;
					inc2++;
				}
			}

		}

		// von Neumann neighborhoods. this only works if numParticles is the product
		// of
		// consecutive integers, or the square of an integer
		public void createVnNbhds(Particle[][] grid) {

			this.numNbhds = this.numParticles;
			this.nbhdType = "vn";

			// equation below is # of particles in a VN nbhd given VN dist
			int vnNbhdSize = 1 + 2 * VN_DIST * (VN_DIST + 1);
			this.nbhds = new Particle[this.numNbhds][vnNbhdSize];

			// loop through each "seed" particle in swarm to create nbhds.
			for (int i = 0; i < this.numParticles; i++) {
				this.createVnNbhdForSingleParticle(grid, i);
			}
		}
	

	// Getters & Setters
	public Particle[] getSwarm() {
		return swarm;
	}

	public void setSwarm(Particle[] swarm) {
		this.swarm = swarm;
	}

	public Particle[][] getNbhds() {
		return nbhds;
	}

	public void setNbhds(Particle[][] nbhds) {
		this.nbhds = nbhds;
	}

	public double[] getNbhdBestValues() {
		return nbhdBestValues;
	}

	public void setNbhdBestValues(double[] nbhdBestValues) {
		this.nbhdBestValues = nbhdBestValues;
	}

	public int getNumParticles() {
		return numParticles;
	}

	public void setNumParticles(int numParticles) {
		this.numParticles = numParticles;
	}

	public String getNbhdType() {
		return nbhdType;
	}

	public void setNbhdType(String nbhdType) {
		this.nbhdType = nbhdType;
	}

	public int getNumNbhds() {
		return numNbhds;
	}

	public void setNumNbhds(int numNbhds) {
		this.numNbhds = numNbhds;
	}

	public double[][] getNbhdBestPositions() {
		return nbhdBestPositions;
	}

	public void setNbhdBestPositions(double[][] nbhdBestPositions) {
		this.nbhdBestPositions = nbhdBestPositions;
	}

	public double getGlobalBestValue() {
		return globalBestValue;
	}

	public double[] getGlobalBestPosition() {
		return globalBestPosition;
	}

	public void setGlobalBestPosition(double[] globalBestPosition) {
		this.globalBestPosition = globalBestPosition;
	}

	public void setGlobalBestValue(double globalBestValue) {
		this.globalBestValue = globalBestValue;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}
	
	

}