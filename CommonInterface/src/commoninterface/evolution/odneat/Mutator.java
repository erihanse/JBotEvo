package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.Random;

public abstract class Mutator <E> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double mutationProb;
	protected Random random;
	
	public void setMutationProbability(double mutationProb){
		this.mutationProb = mutationProb;
	}
	
	public double getMutationProbability(){
		return this.mutationProb;
	}
	
	public void setRandom(Random random){
		this.random = random;
	}
	
	public abstract void applyMutation(E e);
}
