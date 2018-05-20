package commoninterface.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class StandardGenome implements ODNEATGenome {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	protected double fitness = 0;
	protected double adjustedFitness = 0;
	protected double energyLevel = 0;
	protected int speciesId = -1;
	protected int updatesCount = 0;

	protected ArrayList<ODNEATLinkGene> linkGenes;
	protected ArrayList<ODNEATNodeGene> nodeGenes;

	//odneat
	protected String eaInstance = "o";
	protected int age = 0;
	protected double saTime = 1;

	protected HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

	protected StandardGenome(){
		linkGenes = new ArrayList<ODNEATLinkGene>();
		nodeGenes = new ArrayList<ODNEATNodeGene>();
	}

	public StandardGenome(String id, Collection<ODNEATLinkGene> linkGenes, Collection<ODNEATNodeGene> nodeGenes){
		this.linkGenes = new ArrayList<ODNEATLinkGene>();
		this.nodeGenes = new ArrayList<ODNEATNodeGene>();

		this.id = id;
		this.linkGenes.addAll(linkGenes);
		this.nodeGenes.addAll(nodeGenes);
		/*for(ODNEATNodeGene node : nodeGenes)
			System.out.println("TYPE " + node.getType());*/
	}

	public StandardGenome(String id, ODNEATLinkGene[] linkGenes, ODNEATNodeGene[] nodeGenes){
		this.linkGenes = new ArrayList<ODNEATLinkGene>();
		this.nodeGenes = new ArrayList<ODNEATNodeGene>();

		this.id = id;

		for(ODNEATLinkGene link : linkGenes)
			this.linkGenes.add(link);

		for(ODNEATNodeGene node : nodeGenes)
			this.nodeGenes.add(node);
	}

	@Override
	public Collection<ODNEATLinkGene> getLinkGenes(boolean onlyEnabled) {
		if(onlyEnabled){
			ArrayList<ODNEATLinkGene> genes = new ArrayList<ODNEATLinkGene>();
			for(ODNEATLinkGene gene : this.linkGenes)
				if(gene.isEnabled())
					genes.add(gene);

			return genes;
		}
		return this.linkGenes;
	}

	@Override
	public ArrayList<ODNEATNodeGene> getNodeGenes() {
		return this.nodeGenes;
	}

	@Override
	public void insertLinkGenes(Collection<ODNEATLinkGene> newLinks) {
		for(ODNEATLinkGene link : newLinks){
			if(!this.linkGenes.contains(link)){
				this.linkGenes.add(link);
			}
		}
	}

	@Override
	public void insertNodeGenes(Collection<ODNEATNodeGene> newNodes) {
		for(ODNEATNodeGene node : newNodes){
			if(!this.nodeGenes.contains(node)){
				this.nodeGenes.add(node);
			}
		}
	}

	@Override
	public int getNumberOfNodeGenes(){
		return this.nodeGenes.size();
	}

	@Override
	public int getNumberOfLinkGenes(boolean onlyEnabled){
		return this.getLinkGenes(onlyEnabled).size();
	}

	public double getFitness(){
		return fitness;
	}

	public double getAdjustedFitness(){
		return adjustedFitness;
	}

	public double getEnergyLevel(){
		return energyLevel;
	}

	public void setAdjustedFitness(double newAdjustedFitness){
		this.adjustedFitness = newAdjustedFitness;
	}

	public void setFitness(double newFitness){
		this.fitness = newFitness;
	}

	public void setEnergyLevel(double newLevel){
		this.energyLevel = newLevel;
	}

	public String getId(){
		return id;
	}

	public void setId(String newId){
		this.id = newId;
	}

	/*
	 * EAH change
	*/
	public String toString(String separator){
		StringBuffer b = new StringBuffer();
		//links
		b.append(this.linkGenes.size());
		b.append(separator);
		/**
		 * 	protected long fromId, toId, innovationNumber;
	protected double weight;
	protected boolean selfRecurrent = false, recurrent = false, enabled = true;
		 */
		for(ODNEATLinkGene link : this.linkGenes){
			b.append("\nLink:\n");
			b.append(link.getFromId() + separator + link.getToId() + separator + link.getWeight() + separator + link.getInnovationNumber()
					+ separator + (link.enabled == true ? 1 : 0 ) + separator + (link.selfRecurrent == true ? 1 : 0) + separator + (link.recurrent == true ? 1 : 0) + separator);
		}
		//nodes
		b.append("\n");
		b.append(this.nodeGenes.size());
		b.append(separator);

		for(ODNEATNodeGene node : this.nodeGenes){
			b.append("\nGene:\n");
			b.append(node.innovationNumber + separator + node.type + separator + node.bias + separator);
		}

		b.append("\nStats:\n");
		b.append(id + separator + fitness + separator + adjustedFitness + separator + energyLevel + separator + speciesId + separator + updatesCount
				+ separator + eaInstance + separator + age + separator + saTime);

		return b.toString();
	}

	public StandardGenome copy(){
		StandardGenome copy = new StandardGenome();

		for(ODNEATLinkGene linkGene : this.linkGenes)
			copy.linkGenes.add(linkGene.copy());

		for(ODNEATNodeGene nodeGene : this.nodeGenes)
			copy.nodeGenes.add(nodeGene.copy());

		//copy of the fields.
		copy.id = new String(id);
		copy.fitness = fitness;
		copy.adjustedFitness = adjustedFitness;
		copy.energyLevel = energyLevel;
		copy.speciesId = speciesId;
		copy.updatesCount = updatesCount;
		copy.eaInstance = new String(eaInstance);
		copy.age = this.age;

		copy.saTime = this.saTime;

		if(this.map != null){
			copy.map = new HashMap<Integer, Integer>();
			for(Integer key : this.map.keySet()){
				copy.map.put(key, this.map.get(key));
			}
		}

		return copy;
	}

	@Override
	public boolean equals(Object o){
		if((! (o instanceof StandardGenome)) || o == null)
			return false;

		StandardGenome other = (StandardGenome) o;
		if(other.getId().equalsIgnoreCase(this.getId()))
			return true;

		if(this.linkGenes.size() != other.linkGenes.size() || this.nodeGenes.size() != other.nodeGenes.size())
			return false;

		for(ODNEATLinkGene link : this.linkGenes){
			if(!other.linkGenes.contains(link))
				return false;
		}

		for(ODNEATNodeGene node : this.nodeGenes){
			if(!other.nodeGenes.contains(node))
				return false;
		}

		return true;
	}

	public ODNEATNodeGene getNodeGeneWithInnovationNumber(long in) {
		for(ODNEATNodeGene g : this.nodeGenes){
			if(g.getInnovationNumber() == in)
				return g;
		}
		return null;
	}



	public ODNEATLinkGene getLinkGeneWithInnovationNumber(long in) {
		for(ODNEATLinkGene l : this.linkGenes){
			if(l.isEnabled() && l.getInnovationNumber() == in){
				return l;
			}
		}
		return null;
	}

	@Override
	public void insertSingleLinkGene(ODNEATLinkGene newLink) {
		if(!linkGenes.contains(newLink))
			this.linkGenes.add(newLink);
	}

	@Override
	public void insertSingleNodeGene(ODNEATNodeGene newNode) {
		if(!this.nodeGenes.contains(newNode))
			this.nodeGenes.add(newNode);
	}

	public void setSpeciesId(int speciesId){
		this.speciesId = speciesId;
	}

	public int getSpeciesId(){
		return this.speciesId;
	}

	@Override
	public void updateEnergyLevel(double newEnergyLevel) {
		this.energyLevel = newEnergyLevel;
		this.updatesCount++;
		this.fitness = this.fitness + (energyLevel - fitness)/(updatesCount);
		/*System.out.println("updating: " + fitness + "; " + energyLevel + "; " + this.updatesCount);
		if(updatesCount == 100)
			System.exit(0);*/
	}

	public int getNumberOfNodeGenesWithType(int type){
		int count = 0;

		for(ODNEATNodeGene nodeGene : this.nodeGenes){
			if(nodeGene.getType() == type)
				count++;
		}
		return count;
	}

	@Override
	public void setUpdatesCount(int numberOfUpdates) {
		this.updatesCount = numberOfUpdates;
	}

	public void sortGenes(){
		Collections.sort(this.linkGenes);
	}

	@Override
	public ArrayList<Long> getNodeGenesByType(int type) {
		ArrayList<Long> nodesId = new ArrayList<Long>();
		for(ODNEATNodeGene node : this.nodeGenes){
			if(node.getType() == type && !nodesId.contains(node.getInnovationNumber())){
				nodesId.add(node.getInnovationNumber());
			}
		}

		return nodesId;
	}

	@Override
	public long getInnovationRange() {
		long max = this.linkGenes.get(0).getInnovationNumber();
		for(int i = 1; i < linkGenes.size(); i++){
			max = Math.max(max, linkGenes.get(i).getInnovationNumber());
		}
		return max;
	}

	@Override
	public int getUpdatesCount() {
		return this.updatesCount;
	}

	@Override
	public void setEAInstance(String eaInstance) {
		this.eaInstance = eaInstance;
	}

	@Override
	public int getGenomeAge() {
		return this.age;
	}

	public void setAge(int newAge){
		this.age = newAge;
	}

	@Override
	public String getEAInstance() {
		return this.eaInstance;
	}

	@Override
	public void setSAGene(double value) {
		this.saTime = value;
	}

	@Override
	public double getSAGene() {
		return this.saTime;
	}

	@Override
	public void setBDMap(HashMap<Integer, Integer> map) {
		this.map = map;
	}

	@Override
	public HashMap<Integer, Integer> getBDMap() {
		return map;
	}


}
