package commoninterface.evolution.odneat;

import java.io.Serializable;

public abstract class Crossover <E> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double pXOver;
	
	public abstract E applyCrossover(E[] parents, String childId);
	
	public void setCrossoverProbability(double crossoverProbability){
		this.pXOver = crossoverProbability;
	}
	
	public double getCrossoverProbability(){
		return this.pXOver;
	}
}
