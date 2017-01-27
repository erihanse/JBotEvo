package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.HashMap;

import commoninterface.evolution.ODNEATLinkGene;

public class MoveForwardBehaviourGene implements MacroBehaviourGene, PreprogrammedGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap<String, Double> parameters;
	protected long innovationNumber;
	
	protected static final String MOVEMENT_KEY = "u";
	protected final double MOVEMENT_VALUE = 1.0;
	
	public MoveForwardBehaviourGene(long innovationNumber){
		this.innovationNumber = innovationNumber;
		this.parameters = new HashMap<String, Double>();
		parameters.put(MOVEMENT_KEY, MOVEMENT_VALUE);
	}
	
	@Override
	public long getInnovationNumber() {
		return this.innovationNumber;
	}

	@Override
	public MacroBehaviourGene copy() {
		MoveForwardBehaviourGene copy = new MoveForwardBehaviourGene(this.innovationNumber);
		copy.parameters.clear();
		for(String key: this.parameters.keySet()){
			String copyKey = new String(key);
			double value = this.parameters.get(key);
			copy.parameters.put(copyKey, value);
		}
		return copy;
	}

	@Override
	public boolean hasParameters() {
		return true;
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return this.parameters;
	}
	
	public void setParameterValue(String key, double value){
		if(this.parameters.containsKey(key))
			this.parameters.put(key, value);
	}

	public double getMovementParameter() {
		return this.parameters.get(MOVEMENT_KEY);
	}

	@Override
	public int getNumberOfBehaviourGenes() {
		return 1;
	}
	
	@Override
	public long adjustInnovationNumbers(long nextInnovation, long[] inputsIds) {
		return nextInnovation;
	}
	
	@Override
	public ArrayList<Long> getOrderedListInputNodeGenes() {
		return null;
	}

	@Override
	public ArrayList<Long> getOrderedListOutputNodeGenes() {
		return null;
	}
	
	@Override
	public double normaliseParameterValue(String key, double value) {
		return value;
	}
	
	@Override
	public void replaceAllInnovations(HashMap<Long, Long> oldToNew,
			ODNEATInnovationManager dib) {
		if(!oldToNew.containsKey(this.innovationNumber)){
			oldToNew.put(this.innovationNumber, dib.nextInnovationNumber());
		}
		this.innovationNumber = oldToNew.get(this.innovationNumber);
	}
	
	@Override
	public boolean containsSubNodeWithId(long id) {
		return id == this.innovationNumber;
	}
	
	@Override
	public ArrayList<ODNEATLinkGene> getConnectionsList() {
		return new ArrayList<ODNEATLinkGene>();
	}
}
