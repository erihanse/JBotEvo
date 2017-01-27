package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;

public class EvolvedANNBehaviourGene implements MacroBehaviourGene{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ANN_INSTANCE = "ANN";

	private boolean hasBias;
	private long biasId = -1;

	protected ArrayList<ODNEATLinkGene> links;
	protected ArrayList<ODNEATNodeGene> nodes;

	private String macroId;

	private long innovationNumber;

	public EvolvedANNBehaviourGene(String macroId,
			long innovationNumber) {
		this.macroId = new String(macroId);
		links = new ArrayList<ODNEATLinkGene>();
		this.nodes = new ArrayList<ODNEATNodeGene>();
		this.innovationNumber = innovationNumber;
	}

	@Override
	public long getInnovationNumber() {
		return this.innovationNumber;
	}

	public String getMacroId(){
		return this.macroId;
	}

	public int countNodesWithType(int type){
		int count = 0;
		for(ODNEATNodeGene node : this.nodes){
			if(node.getType() == type)
				count++;
		}
		return count;
	}

	public ArrayList<ODNEATLinkGene> getLinkGenes(boolean onlyEnabled) {
		if(onlyEnabled){
			ArrayList<ODNEATLinkGene> genes = new ArrayList<ODNEATLinkGene>();
			for(ODNEATLinkGene gene : this.links)
				if(gene.isEnabled())
					genes.add(gene);

			return genes;
		}

		return this.links;
	}

	public ODNEATNodeGene getNodeWithId(long id){
		for(ODNEATNodeGene node : this.nodes){
			if(node.getInnovationNumber() == id)
				return node;
		}

		return null;
	}

	public void insertSingleNodeGene(ODNEATNodeGene newNode) {
		if(!this.nodes.contains(newNode))
			this.nodes.add(newNode);
	}

	public void insertSingleLinkGene(ODNEATLinkGene newLink) {
		if(!this.links.contains(newLink))
			this.links.add(newLink);
	}

	public ArrayList<ODNEATLinkGene> getConnectionsList() {
		return this.links;
	}

	public ArrayList<ODNEATNodeGene> getNodesList(){
		return this.nodes;
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof EvolvedANNBehaviourGene) || o == null){
			return false;
		}

		EvolvedANNBehaviourGene other = (EvolvedANNBehaviourGene) o;

		if(!other.macroId.equalsIgnoreCase(this.macroId) || other.innovationNumber != this.innovationNumber
				|| other.isHasBias() != this.isHasBias() || other.getBiasId() != this.getBiasId() 
				|| other.links.size() != this.links.size() || other.nodes.size() != this.nodes.size())
			return false;

		if(!other.links.containsAll(this.links) || !other.nodes.containsAll(this.nodes)){
			return false;
		}

		return true;
	}

	@Override
	public EvolvedANNBehaviourGene copy() {
		EvolvedANNBehaviourGene copy = new EvolvedANNBehaviourGene(this.macroId, this.innovationNumber);
		copy.setHasBias(this.isHasBias());
		copy.setBiasId(this.getBiasId());

		for(ODNEATNodeGene node : this.nodes){
			copy.insertSingleNodeGene(node.copy());
		}

		for(ODNEATLinkGene link : this.links){
			copy.insertSingleLinkGene(link.copy());
		}

		return copy;
	}

	@Override
	public boolean hasParameters() {
		return false;
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return null;
	}

	@Override
	public int getNumberOfBehaviourGenes() {
		return 1;
	}

	@Override
	public long adjustInnovationNumbers(long nextInnovation, long[] inputsIds) {
		HashMap<Long, Long> innovations = new HashMap<Long, Long>();
		ArrayList<Long> oldInnovations = new ArrayList<Long>();
		int index = 0;
		for(ODNEATNodeGene node : this.nodes){
			//first, compatibilise the inputs
			if(node.getType() == ODNEATNodeGene.INPUT){
				innovations.put(node.getInnovationNumber(), inputsIds[index]);
				index++;
			}
			else {
				oldInnovations.add(node.getInnovationNumber());
			}
		}
		//sort to preserve the order
		Collections.sort(oldInnovations);
		for(long d : oldInnovations){
			innovations.put(d, nextInnovation++);
		}
		//replacement
		for(ODNEATNodeGene node : this.nodes){
			node.setInnovationNumber(innovations.get(node.getInnovationNumber()));
		}

		for(ODNEATLinkGene link : this.links){
			link.setFromId(innovations.get(link.getFromId()));
			link.setToId(innovations.get(link.getToId()));
			link.setInnovationNumber(nextInnovation++);
		}

		return nextInnovation;
	}

	@Override
	public ArrayList<Long> getOrderedListInputNodeGenes() {
		ArrayList<Long> ids = new ArrayList<Long>();
		for(ODNEATNodeGene node : this.nodes){
			if(node.getType() == ODNEATNodeGene.INPUT){
				ids.add(node.getInnovationNumber());
			}
		}
		Collections.sort(ids);
		return ids;
	}

	@Override
	public ArrayList<Long> getOrderedListOutputNodeGenes() {
		ArrayList<Long> ids = new ArrayList<Long>();
		for(ODNEATNodeGene node : this.nodes){
			if(node.getType() == ODNEATNodeGene.OUTPUT){
				ids.add(node.getInnovationNumber());
			}
		}
		Collections.sort(ids);
		return ids;
	}

	@Override
	//not used by this gene.
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

		for(ODNEATNodeGene node : this.nodes){
			if(!oldToNew.containsKey(node.getInnovationNumber())){
				oldToNew.put(node.getInnovationNumber(), dib.nextInnovationNumber());
			}
			node.setInnovationNumber(oldToNew.get(node.getInnovationNumber()));
		}
		for(ODNEATLinkGene link : this.links){
			if(!oldToNew.containsKey(link.getFromId())){
				oldToNew.put(link.getFromId(), dib.nextInnovationNumber());
			}
			if(!oldToNew.containsKey(link.getToId())){
				oldToNew.put(link.getToId(), dib.nextInnovationNumber());
			}
			if(!oldToNew.containsKey(link.getInnovationNumber())){
				oldToNew.put(link.getInnovationNumber(), dib.nextInnovationNumber());
			}
			link.setToId(oldToNew.get(link.getToId()));	
			link.setFromId(oldToNew.get(link.getFromId()));
			link.setInnovationNumber(oldToNew.get(link.getInnovationNumber()));
		}
	}

	@Override
	public boolean containsSubNodeWithId(long id) {

		if(id == this.innovationNumber){
			return true;
		}
		for(ODNEATNodeGene node : this.nodes){
			if(node.getInnovationNumber() == id)
				return true;
		}
		return false;
	}

	public void removeConnection(long fromId, long toId) {
		int indexToRemove = -1;
		for(int i = 0; i < this.links.size(); i++){
			ODNEATLinkGene link = this.links.get(i);
			if(link.getFromId() == fromId && link.getToId() == toId){
				indexToRemove = i;
				i = this.links.size();
			}
		}
		if(indexToRemove != -1){
			this.links.remove(indexToRemove);
		}
	}

	public boolean isHasBias() {
		return hasBias;
	}

	public void setHasBias(boolean hasBias) {
		this.hasBias = hasBias;
	}

	public long getBiasId() {
		return biasId;
	}

	public void setBiasId(long biasId) {
		this.biasId = biasId;
	}

}
