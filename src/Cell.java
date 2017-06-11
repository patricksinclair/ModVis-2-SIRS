import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//This is the Cell class. The simulation takes a 2D array of Cells and updates their state according to the rules of 
//the simulation.

//A Cell has 4 states:
//S - susceptible to infection.
//I - infected.
//R - immune to the infection.
//RR - permanently immune to the infection.
public class Cell {

	private String state;
	private static Random rand = new Random();

	public Cell(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}
	public void setState(String state){
		this.state = state;
	}

	//This method determined what state the Cell will move to in it's next iteration.
	//A random number is generated, and depending on what state the Cell is currently in, if the random number is
	//less than the probability of the Cell evolving, the cell will progress to its next state.
	//A cell can only become infected if one of its 8 nearest neighbours is also infected.
	public void evolve(boolean infectedNeighbour, double p1, double p2, double p3){

		String state = getState();
		double r = rand.nextDouble();

		if((state.equals("S") && infectedNeighbour) && (r <= p1)) setState("I");
		else if(state.equals("I") && r <= p2) setState("R");
		else if(state.equals("R") && r <= p3) setState("S");
	}

	//method for creating a 2D array of cells with an equal distribution of states. This lattice does not contain the 
	//RR state.
	public static Cell[][] equallyRandomInfections(int N){

		Cell[][] cells = new Cell[N][N];

		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				double r = rand.nextDouble();

				if(r <= 1./3.) cells[i][j] = new Cell("S");
				else if(r > 1./3. && r <= 2./3.) cells[i][j] = new Cell("I");
				else cells[i][j] = new Cell("R");
			}
		}
		return cells;
	}


	//method for creating a 2D array with a fraction (number between 0 - 1) of the Cells in the RR state.
	public static Cell[][] fractionImmune(int N, double fractionImmune){

		Cell[][] cells = new Cell[N][N];

		double remainder = 1. - fractionImmune;
		
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				double r = rand.nextDouble();
				
				if(r <= remainder/3.) cells[i][j] = new Cell("S");
				else if(r > remainder/3. && r <= 2*remainder/3.) cells[i][j] = new Cell("I");
				else if(r > 2*remainder/3. && r <= remainder) cells[i][j] = new Cell("R");
				else cells[i][j] = new Cell("RR");
			}
		}

		return cells;
	}
	
}
