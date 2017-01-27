package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.HashMap;

import commoninterface.evolution.ODNEATGene;
import commoninterface.evolution.ODNEATLinkGene;


public interface MacroBehaviourGene extends ODNEATGene {
	
	public MacroBehaviourGene copy();

	public long getInnovationNumber();
	
	@Override
	public boolean equals(Object o);
	
	public boolean hasParameters();
	
	public HashMap<String, Double> getParameters();
	
	public double normaliseParameterValue(String key, double value);
	
	public int getNumberOfBehaviourGenes();

	public long adjustInnovationNumbers(long nextInnovation, long[] inputsIds);

	public ArrayList<Long> getOrderedListInputNodeGenes();

	public ArrayList<Long> getOrderedListOutputNodeGenes();

	public void replaceAllInnovations(HashMap<Long, Long> oldToNew,
			ODNEATInnovationManager dib);

	public boolean containsSubNodeWithId(long id);

	public ArrayList<ODNEATLinkGene> getConnectionsList();
}
