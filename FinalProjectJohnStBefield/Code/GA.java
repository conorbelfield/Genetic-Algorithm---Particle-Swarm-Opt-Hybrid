/*
 * Ryan St. Pierre
 * Conor Belfield
 * John Ahn
 * 
 * This class implements the Genetic Algorithm. It contains three different
 * methods of ranking individuals, which are Boltzman selction, tournament
 * selection and ranked selection. It also crosses over solutions when 
 * creating the next generation in either a one point crossover or
 * a uniform crossover. The individuals can then be mutated with a given
 * probabilty
 * 
 */

import java.util.ArrayList;
import java.util.Random;

public class GA {

	private static int TOURNAMENT_SIZE = 4;

	private int[] particleIndices;
	Swarm swarm;
	int numEliteParticles;
	int particleDim;
	int currIteration;
	int maxIterations;
	double iterDependFactor = 5.0;
	double[][] fitnessValues; 
	
	// create a population of particles to GA on
	public GA(Swarm swarm, double cutRatio, int currIteration, int maxIterations) {
		
		this.swarm = swarm;
		this.currIteration = currIteration;
		this.maxIterations = maxIterations;
		this.sortValues();
		
	}
	
	//takes the values in the swarm quicksort to sort the particles 
	//by fitness
	public void sortValues(){
		Particle[] swarmParticles = swarm.getSwarm();
		// auxiliary code to rank particles by fitness, providing indices
		//to access the values and a fitness value to sort by
		//in a 2D array
		fitnessValues = new double[2][swarmParticles.length];
		for (int i = 0; i < swarmParticles.length; i++) {
			fitnessValues[0][i] = swarmParticles[i].eval();
			fitnessValues[1][i] = i;
		}
		QuickSort.quickSort(fitnessValues);
		
		// create array of particles, ranked by fitness
		int[] rankedIndices = new int[swarmParticles.length];
		for (int i = 0; i < swarmParticles.length; i++) {
			rankedIndices[i] = (int) fitnessValues[1][i];
		}
		this.particleIndices = rankedIndices;
	}
	
	//function holds the loop that creates each generation of individuals for the
	//GA and calls the smaller functions to do each of the subtasks in the process
	//to create an optimal solution
	//nothing is returned but a solution is created in the class object
	public void evolve(String crossMethod, double crossProb, double mutateProb, 
			double mutUpperBound, double mutLowerBound,String sel) {	
	
			if(sel.equals("rs") || sel.equals("ts") || sel.equals("bs")){
				selection(sel);

			}
		
			//Crossover to create the new candidate solutions
			this.crossover(crossMethod, crossProb);
			//mutate the solutions
			this.mutate(mutateProb, mutUpperBound, mutLowerBound);
			
	}

	
	
	//implements 1 point crossover between two individuals
	//where a random point is chosen between the two and the halves of
	//the two swap
	public void onePointCrossover(int parent1, int parent2) {

        int crossoverIndex = (int) (particleDim * Math.random());
        for (int i = crossoverIndex; i < particleDim; i++) {
            double tempPos = swarm.swarm[parent1].getPosValue(i);
            swarm.swarm[parent1].setPos(i, swarm.swarm[parent2].getPosValue(i));
            swarm.swarm[parent2].setPos(i, tempPos);
            double tempVel = swarm.swarm[parent1].getVelValue(i);
            swarm.swarm[parent1].setVel(i, swarm.swarm[parent2].getVelValue(i));
            swarm.swarm[parent2].setVel(i, tempVel);
        }
	}

//	implements uniform crossover between two individuals where the individual
	//corresponding values in the two are swapped with some probability,
	//in this case 0.5
	public void uniformCrossover(int parent1, int parent2) {
		for (int i = 0; i < particleDim; i++) {
			if (Math.random() < 0.5) {
				double tempPos = swarm.swarm[parent1].getPosValue(i);
				swarm.swarm[parent1].setPos(i, swarm.swarm[parent2].getPosValue(i));
				swarm.swarm[parent2].setPos(i, tempPos);
	            double tempVel = swarm.swarm[parent1].getVelValue(i);
	            swarm.swarm[parent1].setVel(i, swarm.swarm[parent2].getVelValue(i));
	            swarm.swarm[parent2].setVel(i, tempVel);
			}
		}
	}
//
	// method that applies crossover to entire breeding pool
	// note: because breeding pool is twice the size of individual pool, it must
	// be even
	//It does not return anything but modifies the object
	public void crossover(String crossoverMethod, double crossoverProbability) {
		int iters = numEliteParticles;
		if(numEliteParticles % 2 == 1){
			iters = iters - 1;
		}

		//loops through the pairs of particles
		for (int i = 0; i < iters/2; i++) {

			// for every consecutive pair of particles
			// if crossover occurs
			if (Math.random() < crossoverProbability) {
				if (crossoverMethod.equals("1c")) {
					onePointCrossover(this.particleIndices[2*i], this.particleIndices[2*i+1]);
				}
				if (crossoverMethod.equals("uc")) {
					uniformCrossover(this.particleIndices[2*i], this.particleIndices[2*i+1]);
				}
			}
		}


	}
	
	//A method that determines which selection we will use
	public void selection(String selectionMethod) {
		if (selectionMethod.equals("rs")) {
			this.rankSelection();
		}
		if (selectionMethod.equals("ts")) {
			this.tournamentSelection();
		}
		if (selectionMethod.equals("bs")) {
			this.boltzmannSelection();
		}
	}
	
	//function performs one of three types of selection on the individuals of
		// the population, in this case rank selection where individuals have
		//a probability of being chosen proportional to their fitness rank in the
		//population
		public void rankSelection() {
		    int dim = particleDim;
		    Particle[] breedingPool = new Particle[swarm.swarm.length];
		    int sumOfRanks = (swarm.swarm.length)*(swarm.swarm.length + 1)/2;
		    for  (int i = 0; i < swarm.swarm.length; i++) {
		        double marker = sumOfRanks;
		        Random rand = new Random();
		        int random = (int)(rand.nextDouble() * (sumOfRanks + 1) + 1);
		        boolean cont = true;
		        
		        for (int y = swarm.swarm.length - 1; y > -1 && cont; y--) {
		            if (random + y + 1 > marker) {
		                breedingPool[i] = 
		                		this.swarm.swarm[(int)fitnessValues[1][swarm.swarm.length - 1 -y]];
		                cont = false;
		            }
		            else {
		                marker -= (y+1);
		            }
		        }
		    }
		    this.swarm.swarm = breedingPool;
		}
		
		//the function is an implementation of tournament selection where two individuals
		//at random are chosen from the population and the one with the higher fitness
		//is selected for the next generation
		public void tournamentSelection() {
			
			Particle[] newBreedingPool = new Particle[swarm.swarm.length];
			
			//Create swarm size new soultions
			for (int j = 0; j < swarm.swarm.length; j++) {
				//Choose two random individuals
				int ind1 = (int) (Math.random() * (swarm.swarm.length - 1));
				int ind2 = (int) (Math.random() * (swarm.swarm.length - 1));
				double fit1 = swarm.swarm[ind1].eval();
				double fit2 = swarm.swarm[ind2].eval();
				
				//Add the better one to the new breeding pool
				if (fit1 <= fit2) {
					newBreedingPool[j] = swarm.swarm[ind1];

				} else {
					newBreedingPool[j]= swarm.swarm[ind2];
				}

			}
			swarm.swarm = newBreedingPool;
		}

		//implements boltzman selection where the individuals are chosen with a
		// probability based upon e to the power their fitness. Chosen individuals
		//then advance to the next generation
		public void boltzmannSelection() {
			Particle[] newBreedingPool = new Particle[swarm.swarm.length];
			double sumFit = 0;
			double e = Math.E;
			
			//Sum all the boltzman equation fitnesses
			for (int x = 0; x < swarm.swarm.length; x++) {
				sumFit += Math.pow(e, fitnessValues[0][x]);
			}
			
			//Setting the probabilites of individuals
			double[] prob = new double[swarm.swarm.length];
			for (int x = 0; x < swarm.swarm.length; x++) {
				
				prob[(int)fitnessValues[1][x]] = Math.pow(e, fitnessValues[0][x]) / sumFit;
			}
			
			//This is creating the ranges that correspond to the probability of an individual
			//being chosen. All the probabilites sum to one, which is why we can split them into sections
			//from zero to one.
			double mark = 0;
			double[] ranges = new double[swarm.swarm.length];
			for (int x = 0; x < swarm.swarm.length; x++) {
				ranges[x] = mark;
				mark += prob[x];
			}
			
			//We now find swarm size random numbers between 0 and 1, and whichever
			//Individual owns the range that the random number falls in is selected. This is
			//very similar to rank selection.
			for (int x = 0; x < swarm.swarm.length; x++) {
				double location = Math.random();
				int y;
				for (y = swarm.swarm.length - 1;  location <  ranges[y]; y--) {
				}

				newBreedingPool[x] = swarm.swarm[y];
			}
			swarm.swarm = newBreedingPool;
		}
	
	// method that mutates each individual
	public void mutate(double mutProb, double UB, double LB) {
		for (int i = 0; i < numEliteParticles; i++) {
	        double iterationDependency = Math.pow((1 - this.currIteration/this.maxIterations), iterDependFactor);
	        for (int j = 0; j < this.swarm.swarm[this.particleIndices[i]].getDim(); j++){
	            Random rand = new Random();
	            if (rand.nextDouble() < mutProb){
	                //mutate to UB
	                if (rand.nextDouble() > 0.5){
	                	this.swarm.swarm[this.particleIndices[i]].setPos(j, this.swarm.swarm[this.particleIndices[i]].getPosValue(j)
	                			+ (UB - this.swarm.swarm[this.particleIndices[i]].getPosValue(j))
	                			* Math.pow(1 - rand.nextDouble(), iterationDependency));
	                }
	                
	                //mutate to LB
	                else{
	                	this.swarm.swarm[this.particleIndices[i]].setPos(j, this.swarm.swarm[this.particleIndices[i]].getPosValue(j) - 
	                			(this.swarm.swarm[this.particleIndices[i]].getPosValue(j) - LB) * 
	                			Math.pow((1 - rand.nextDouble()), iterationDependency));
	                }
	            }
	        }
		}
	}

}
