package commoninterface.evolution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface ODNEATGenome extends Genome, Serializable {
	
	public double getEnergyLevel();
	public double getAdjustedFitness();
	
	public void setEnergyLevel(double energyLevel);
	
	public void setAdjustedFitness(double adjustedFitness);
	
	public void updateEnergyLevel(double newEnergyLevel);

	public Collection<ODNEATLinkGene> getLinkGenes(boolean onlyEnabled);
	public Collection<ODNEATNodeGene> getNodeGenes();
	
	public int getNumberOfNodeGenes();
	public int getNumberOfLinkGenes(boolean onlyEnabled);
	
	public void insertLinkGenes(Collection<ODNEATLinkGene> newLinks);
	public void insertNodeGenes(Collection<ODNEATNodeGene> newNodes);
	public void insertSingleLinkGene(ODNEATLinkGene newLink);
	public void insertSingleNodeGene(ODNEATNodeGene newNode);
	
	public ODNEATLinkGene getLinkGeneWithInnovationNumber(long in);
	public ODNEATNodeGene getNodeGeneWithInnovationNumber(long in);
	
	public int getSpeciesId();
	public void setSpeciesId(int speciesId);
	
	public ODNEATGenome copy();
	public void setUpdatesCount(int numberOfUpdates);
	public int getUpdatesCount();
	
	public void sortGenes();
	public ArrayList<Long> getNodeGenesByType(int input);
	public long getInnovationRange();
	public void setEAInstance(String eaInstance);	
	
	public int getGenomeAge();
	public void setAge(int age);
	public String getEAInstance();
	public void setSAGene(double value);
	public double getSAGene();
	public void setBDMap(HashMap<Integer, Integer> map);
	public HashMap<Integer, Integer> getBDMap();
	public String toString(String separator);
}
