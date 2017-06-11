import java.util.ArrayList;
import java.util.Random;


public class Organism {

	//This class has 4 variables. The array of cells and the 3 probabilities used to determine how the cells evolve.
	private Cell[][] cells;
	private double p1, p2, p3;

	private static Random rand = new Random();

	public Organism(Cell[][] cells, double p1, double p2, double p3){
		this.cells = cells;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	//basic getter and setter methods.
	public Cell[][] getCells(){
		return cells;
	}
	public void setCells(Cell[][] cells){
		this.cells = cells;
	}
	//as periodic boundary conditions are required, this ensures that no ArrayOutOfBound exceptions
	//are raised when doing so.
	public String getState(int i, int j){
		int n = getCells().length;
		i = (i+n)%n;
		j = (j+n)%n;
		return getCells()[i][j].getState();
	}
	public void setState(int i, int j, String state){
		getCells()[i][j].setState(state);
	}

	public double getP1(){
		return p1;
	}
	public void setP1(double p1){
		this.p1 = p1;
	}
	public double getP2(){
		return p2;
	}
	public void setP2(double p2){
		this.p2 = p2;
	}
	public double getP3(){
		return p3;
	}
	public void setP3(double p3){
		this.p3 = p3;
	}

	//Method for determining if a cell's nearest neighbour is infected or not.
	public boolean infectedNeighbour(int i, int j){

		if(getState(i, j).equals("S")){
			String[] states = new String[8];

			states[0] = getState(i-1, j);
			states[1] = getState(i-1, j+1);
			states[2] = getState(i, j+1);
			states[3] = getState(i+1, j+1);
			states[4] = getState(i+1, j);
			states[5] = getState(i+1, j-1);
			states[6] = getState(i, j-1);
			states[7] = getState(i-1, j-1);

			for(int k = 0; k < states.length; k++){
				if(states[k].equals("I")) return true;
			}
			return false;
		}
		return false;
	}

	public boolean infectedNeighbourSimplified(int i, int j){

		if(getState(i, j).equals("S")){
			String[] states = new String[4];

			states[0] = getState(i-1, j);
			states[1] = getState(i, j+1);
			states[2] = getState(i+1, j);
			states[3] = getState(i, j-1);

			for(int k = 0; k < states.length; k++){
				if(states[k].equals("I")) return true;
			}

		}
		return false;

	}

	//Method for updating the lattice. A point is selected at random and its state is then updated according
	//to the rules of the system.
	public void spreadDisease(){

		int iRand = rand.nextInt(getCells().length);
		int jRand = rand.nextInt(getCells().length);
		boolean infectedNeighbour = infectedNeighbour(iRand, jRand);
		//boolean infectedNeighbour = infectedNeighbourSimplified(iRand, jRand);

		getCells()[iRand][jRand].evolve(infectedNeighbour, getP1(), getP2(), getP3());
	}

	//Method for updating the system for a certain number of iterations.
	public void spreadDiseaseRepeated(int nReps){

		for(int i = 0; i < nReps; i++){
			spreadDisease();
		}
	}

	//method for determining the number of infected cells in the system.
	//each cell is iterated over and the number of "I" states is counted.
	public double nInfected(){

		double runningTotal = 0.;
		int n = getCells().length;

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(getState(i, j).equals("I")) runningTotal += 1;
			}
		}
		return runningTotal;
	}

	//method for creating a contour plot of the infected fraction of the lattice as a function of p1 and p3.
	public static void infectedFractionPlot(){

		int systemSize = 128;
		int N = systemSize*systemSize;
		int equibThresh = 100*N;
		int postEquibThresh = N;
		int nReps = 100;
		String filename = "infectedContourPlotSimplified";

		double p1Start = 0.0;
		double p2 = 0.5;
		double p3Start = 0.0;
		double increment = 0.01;

		ArrayList<Double> xData = new ArrayList<Double>();
		ArrayList<Double> yData = new ArrayList<Double>();
		ArrayList<Double> zData = new ArrayList<Double>();

		for(double p1 = p1Start; p1 <= 1.0; p1 += increment){

			xData.add(p1);

			for(double p3 = p3Start; p3 <= 1.0; p3 += increment){

				yData.add(p3);
				double nInfected = 0.0;
				Organism organism = Organism.equalRandomOrganism(systemSize, p1, p2, p3);
				organism.spreadDiseaseRepeated(equibThresh);

				innerloop:
					for(int i = 0; i < nReps; i++){
						double currentInfected = organism.nInfected();

						//if the system has reached an absorbing state, then there is no point in continuing,
						//so the code exits from this particular configuration here.
						if((int)currentInfected == 0) break innerloop;

						nInfected += currentInfected;
						organism.spreadDiseaseRepeated(postEquibThresh);
					}

				double avgInfected = nInfected/nReps;
				zData.add(avgInfected/N);
			}
			System.out.println(p1);
		}
		//this prints the data to a file for graphing purposes.
		Toolbox.printDataToFile(xData, yData, zData, filename);
	}

	//method for making a contour plot of the variance of the infected fraction.
	public static void infectedVariancePlot(){

		int systemSize = 128;
		int N = systemSize*systemSize;
		int equibThresh = 10*N;
		int postEquibThresh = N;
		int nReps = 100;
		String filename = "varianceContour2";

		double p1Start = 0.0;
		double p2 = 0.5;
		double p3Start = 0.0;
		double increment = 0.01;

		ArrayList<Double> xData = new ArrayList<Double>();
		ArrayList<Double> yData = new ArrayList<Double>();
		ArrayList<Double> zData = new ArrayList<Double>();

		for(double p1 = p1Start; p1 <= 1.0; p1 += increment){

			xData.add(p1);

			for(double p3 = p3Start; p3 <= 1.0; p3 += increment){
				yData.add(p3);
				double nInfected = 0.0;
				double nInfectedSq = 0.0;
				Organism organism = Organism.equalRandomOrganism(systemSize, p1, p2, p3);
				organism.spreadDiseaseRepeated(equibThresh);

				innerloop:
					for(int i = 0; i < nReps; i++){

						double infectedPop = organism.nInfected();

						if((int)infectedPop == 0) break innerloop;

						nInfected += infectedPop;
						nInfectedSq += infectedPop*infectedPop;
						organism.spreadDiseaseRepeated(postEquibThresh);
					}

				double nInfectedSqAvg = nInfectedSq/nReps;
				double nInfectedAvgSq = (nInfected/nReps)*(nInfected/nReps);
				double varianceFraction = (nInfectedSqAvg - nInfectedAvgSq)/N;
				zData.add(varianceFraction);

			}
			System.out.println(p1);
		}
		Toolbox.printDataToFile(xData, yData, zData, filename);
	}

	//method for finding the recurrence time of the infection as a function of sweeps
	public static void wavesOverTimePlot(){

		int systemSize = 128;
		int N = systemSize*systemSize;
		int equibThresh = 64*N;
		int postEquibThresh = N;
		int nPoints = 1000;
		String filename = "wavesOverTime2";

		double p1 = 0.8;
		double p2 = 0.1;
		double p3 = 0.0055;

		Organism organism = Organism.equalRandomOrganism(systemSize, p1, p2, p3);
		ArrayList<Double> xData = new ArrayList<Double>();
		ArrayList<Double> yData = new ArrayList<Double>();

		organism.spreadDiseaseRepeated(equibThresh);

		for(int i = 0; i < nPoints; i++){
			xData.add((double)i);
			yData.add(organism.nInfected()/systemSize);
			organism.spreadDiseaseRepeated(postEquibThresh);
			if(i%100 == 0) System.out.println(i); 
		}
		Toolbox.printDataToFile(xData, yData, filename);
	}

	//method for calculating the minimum fraction of permanently immune Cells in order to stop the 
	//spread of infection as a function of p1.
	public static void minimumImmunity(){
		int systemSize = 128;
		int N = systemSize*systemSize;
		int equibThresh = 100*N;
		String filename = "minImmunity";

		double p1 = 0.0;
		double p2 = 0.5;
		double p3 = 0.5;
		double increment = 0.01;
		double fractionImmune = 0.0;
		double fractionPlaceholder = 0.0;

		ArrayList<Double> xData = new ArrayList<Double>();
		ArrayList<Double> yData = new ArrayList<Double>();

		for(p1 = 0.; p1 <= 1.; p1+=increment){
			xData.add(p1);
			innerloop:
				for(fractionImmune = fractionPlaceholder; fractionImmune <= 1.; fractionImmune+=increment){
					Organism organism = Organism.fractionImmuneOrganism(systemSize, fractionImmune, p1, p2, p3);
					organism.spreadDiseaseRepeated(equibThresh);
					if((int)organism.nInfected() == 0) break innerloop;

				}
		yData.add(fractionImmune);
		fractionPlaceholder = fractionImmune/1.2;
		//fractionImmune /= 1.2;
		System.out.println(p1);

		}

		Toolbox.printDataToFile(xData, yData, filename);	
	}


	//method to create a new organism with the S, I & R states all equally distributed.
	public static Organism equalRandomOrganism(int N, double p1, double p2, double p3){
		Cell[][] cells = Cell.equallyRandomInfections(N);

		return new Organism(cells, p1, p2, p3);
	}

	//method to create a new organism with a fraction of the Cells in the RR state.
	public static Organism fractionImmuneOrganism(int N, double fractionImmune, double p1, double p2, double p3){

		Cell[][] cells = Cell.fractionImmune(N, fractionImmune);

		return new Organism(cells, p1, p2, p3);
	}

}
