package commoninterface.evolution;

import java.io.Serializable;

public interface Genome extends Serializable {

	public void setId(String newId);
	public String getId();
	
	public double getFitness();
	public void setFitness(double fitness);
	
	public Genome copy();
}
