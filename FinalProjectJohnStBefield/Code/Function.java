/*
 * Ryan St. Pierre
 * John Ahn
 * Conor Belfield
 * 
 * This file implements five different functions to test them
 * on the PSO GA hybrid
 * 
 * 
 * 
 */
public class Function {

	private String functionType;
	private int dimensions;
	private double minInitPos;
	private double maxInitPos;
	private double minInitVel;
	private double maxInitVel;

	// personal best acceleration coefficient
	private double phi1;
	// global best acceleration coefficient
	private double phi2;

	// constriction factor calculated from phi and k
	private double phi;
	private double constrictionFactor;

	public Function(String functionType, int dimensions, 
			double minInitPos, double maxInitPos, double minInitVel,
			double maxInitVel,
			double phi1, double phi2, double k) {

		this.functionType = functionType;
		this.dimensions = dimensions;
		this.minInitPos = minInitPos;
		this.maxInitPos = maxInitPos;
		this.minInitVel = minInitVel;
		this.maxInitVel = maxInitVel;
		this.phi1 = phi1;
		this.phi2 = phi2;
		this.phi = phi1 + phi2;
		this.constrictionFactor = 
				(2 * k) / (this.phi - 2 + Math.sqrt(this.phi * (this.phi - 4)));

	}

	//Function that determines which valuation function is run
	public double eval(double[] values) {
		if ((this.functionType).equals("rok")) {
			return this.evalRosenbrock(values);
		}
		if ((this.functionType).equals("ack")) {
			return this.evalAckley(values);
		}
		if ((this.functionType).equals("ras")) {
			return this.evalRastrigin(values);
		}
		if ((this.functionType).equals("zak")) {
			return this.evalZakharov(values);
		}
		if ((this.functionType).equals("grk")) {
			return this.evalGriewank(values);
		}

		// note that the three functions have global mins of 0.
		// so if eval method returns a negative number, invalid function name.
		return -1.0;

	}

	// returns the value of the Rosenbrock Function with a array
	// of doubles representing a point with the number of dimensions
	// inputed. The minimum is 0.0, which occurs at (1.0,...,1.0)
	public double evalRosenbrock(double[] values) {
		double answer = 0.0;
		for (int i = 0; i < this.dimensions - 1; i++) {
			double y = values[i + 1];
			double x = values[i];
			answer += 100.0 * Math.pow(y - x * x, 2.0) + Math.pow(x - 1.0, 2.0);
		}
		return answer;
	}

	// returns the value of the Rastrigin Function with a array
	// of doubles representing a point with the number of dimensions
	// inputed. The minimum is 0.0, which occurs at (0.0,...,0.0)
	public double evalRastrigin(double[] values) {
		double answer = 0.0;
		for (int i = 0; i < this.dimensions; i++) {
			double x = values[i];
			answer += x * x - 10.0 * Math.cos(2.0 * Math.PI * x) + 10.0;
		}
		return answer;
	}

	// returns the value of the Ackley Function with a array
	// of doubles representing a point with the number of dimensions
	// inputed. The minimum is 0.0, which occurs at (0.0,...,0.0)
	public double evalAckley(double[] values) {
		double firstSum = 0.0;
		double secondSum = 0.0;
		for (int i = 0; i < this.dimensions; i++) {
			double x = values[i];
			firstSum = firstSum + (x * x);
			secondSum = Math.cos(2.0 * Math.PI * x);
		}
		return -20.0 * Math.exp(-0.2 * Math.sqrt(firstSum / this.dimensions)) - Math.exp(secondSum / this.dimensions)
				+ 20.0 + Math.E;
	}
	
	// returns the value of the Griewank Function with a array
	// of doubles representing a point with the number of dimensions
	// inputed. The minimum is 0.0, which occurs at (0.0,...,0.0)
	public double evalGriewank(double[] values) {
		double firstVal = values[0];
		double firstSum = firstVal * firstVal;
		double product = Math.cos( firstVal / Math.sqrt(1));
		for (int i = 1; i < this.dimensions; i++) {
			double x = values[i];
			firstSum += x * x;
			product = product * Math.cos( x/ (Math.sqrt(i+1)));
		}
		firstSum = firstSum / 4000;
		return firstSum - product + 1;
	}
	
	// returns the value of the Zakharov Function with a array
	// of doubles representing a point with the number of dimensions
	// inputed. The minimum is 0.0, which occurs at (0.0,...,0.0)
	public double evalZakharov(double[] values) {
		double firstSum = 0.0;
		double secondSum = 0.0;
		for (int i = 0; i < this.dimensions; i++) {
			double x = values[i];
			firstSum = firstSum + (x * x);
			secondSum = secondSum + (0.5 * (i+1) * x);
		}
		return firstSum + Math.pow(secondSum, 2.0) + Math.pow(secondSum, 4.0);
	}

	// Getters & Setters
	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public double getMinInitPos() {
		return minInitPos;
	}

	public void setMinInitPos(double minInitPos) {
		this.minInitPos = minInitPos;
	}

	public double getMaxInitPos() {
		return maxInitPos;
	}

	public void setMaxInitPos(double maxInitPos) {
		this.maxInitPos = maxInitPos;
	}

	public double getMinInitVel() {
		return minInitVel;
	}

	public void setMinInitVel(double minInitVel) {
		this.minInitVel = minInitVel;
	}

	public double getMaxInitVel() {
		return maxInitVel;
	}

	public void setMaxInitVel(double maxInitVel) {
		this.maxInitVel = maxInitVel;
	}
	
	public double getPhi1() {
		return phi1;
	}

	public void setPhi1(double phi1) {
		this.phi1 = phi1;
	}

	public double getPhi2() {
		return phi2;
	}

	public void setPhi2(double phi2) {
		this.phi2 = phi2;
	}

	public double getConstrictionFactor() {
		return constrictionFactor;
	}

	public void setConstrictionFactor(double constrictionFactor) {
		this.constrictionFactor = constrictionFactor;
	}

}
