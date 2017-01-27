package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;


public class ODNEATMacroNodeGene extends ODNEATNodeGene {

	private static final long serialVersionUID = 1L;

	protected String macroName;
	protected MacroBehaviourGene behaviourGene;

	private ArrayList<ODNEATTemplateLinkGene> inputs;

	private int macroId;


	public ODNEATMacroNodeGene(String macroName, int macroId, long innovationNumber, 
			int type, MacroBehaviourGene gene) {
		super(innovationNumber, type);
		this.macroName = new String(macroName);
		this.behaviourGene = gene.copy();
		this.inputs = new ArrayList<ODNEATTemplateLinkGene>();
		this.macroId = macroId;
	}

	public int getMacroId(){
		return this.macroId;
	}


	public ArrayList<ODNEATTemplateLinkGene> getTemplateInputs(){
		return this.inputs;
	}

	public void addTemplateInput(ODNEATTemplateLinkGene input){
		if(!this.inputs.contains(input))
			this.inputs.add(input);
	}

	public void removeTemplateInput(ODNEATTemplateLinkGene input){
		this.inputs.remove(input);
	}


	public String getMacroName(){
		return macroName;
	}

	public void setMacroId(String newId){
		this.macroName = newId;
	}

	public MacroBehaviourGene getBehaviourGene(){
		return this.behaviourGene;
	}


	public HashMap<Long, Long> replaceAllInnovations(ODNEATInnovationManager dib) {
		HashMap<Long, Long> oldToNew = new HashMap<Long, Long>();
		oldToNew.put(this.innovationNumber, dib.nextInnovationNumber());
		this.innovationNumber = oldToNew.get(this.innovationNumber);

		for(ODNEATLinkGene link : this.inputs){
			if(!oldToNew.containsKey(link.getToId())){
				oldToNew.put(link.getToId(), dib.nextInnovationNumber());
			}
			if(!oldToNew.containsKey(link.getInnovationNumber())){
				oldToNew.put(link.getInnovationNumber(), dib.nextInnovationNumber());
			}
			link.setToId(oldToNew.get(link.getToId()));				
			link.setInnovationNumber(oldToNew.get(link.getInnovationNumber()));
		}
		behaviourGene.replaceAllInnovations(oldToNew, dib);
		return oldToNew;
	}

	/************
	 * COEVOLUTION MATERIAL
	 */

	@Override
	public boolean equals(Object o){
		if((! (o instanceof ODNEATMacroNodeGene)) || o == null)
			return false;

		ODNEATMacroNodeGene other = (ODNEATMacroNodeGene) o;
		boolean basicMatch = other.getMacroName().equalsIgnoreCase(this.macroName)
				&& other.getInnovationNumber() == this.getInnovationNumber() &&
				other.type == this.type
				&& other.behaviourGene.equals(this.behaviourGene)
				&& this.inputs.size() == other.inputs.size()
				&& this.macroId == other.macroId
				&& this.fitness == other.fitness 
				&& this.adjustedFitness == other.adjustedFitness
				&& this.evolutionId.equalsIgnoreCase(other.evolutionId);


		if(!basicMatch || !other.inputs.containsAll(this.inputs))
			return false;

		return true;
	}

	public ODNEATMacroNodeGene copy(){
		ODNEATMacroNodeGene copy = new ODNEATMacroNodeGene(new String(this.macroName), macroId, 
				innovationNumber, type, this.behaviourGene.copy());
		copy.fitness = fitness;
		copy.adjustedFitness = adjustedFitness;
		copy.speciesId = speciesId;
		copy.evolutionId = new String(evolutionId);

		for(ODNEATTemplateLinkGene link : this.inputs){
			copy.addTemplateInput(link.copy());
		}

		copy.parentFitness = parentFitness;

		for(double[] vec: this.variationMap){
			copy.variationMap.add(vec.clone());
		}

		return copy;
	}


	protected double fitness = 0;
	protected double adjustedFitness = 0;
	private int speciesId = -1;
	private String evolutionId = "";

	public double getFitness(){
		return fitness;
	}

	public double getAdjustedFitness(){
		return adjustedFitness;
	}

	public void setAdjustedFitness(double newAdjustedFitness){
		this.adjustedFitness = newAdjustedFitness;
	}

	public void setSpeciesId(int speciesId){
		this.speciesId = speciesId;
	}

	public int getSpeciesId(){
		return this.speciesId;
	}

	public String getEvolutionId() {
		return this.evolutionId;
	}

	public void setId(String id){
		this.evolutionId = new String(id);
	}

	public void setBehaviourGene(EvolvedANNBehaviourGene newGene) {
		this.behaviourGene = newGene;
	}


	private HashMap<String, Double> evalMap = new HashMap<String, Double>();
	public void update(String controllerId, double currentFitness, ODNEATMacroNodeGene original) {
		//update variation map
		if(original.variationMap.size() > this.variationMap.size()){
			this.variationMap.clear();
			for(double [] vec : original.variationMap){
				this.variationMap.add(vec.clone());
			}
		}
		evalMap.put(controllerId, currentFitness);
		//update fitness
		fitness = 0;
		for(double d : evalMap.values())
			fitness += d;

		fitness /= evalMap.size();	

		//double absolute = 0;
		//macro-coevolution is being useg
		/*if(speciesId != -1){
			if(this.variationMap.size() >= 2){
				double[] v1 = new double[this.variationMap.size()], v2 = new double[this.variationMap.size()];
				for(int i = 0; i < variationMap.size(); i++){
					v1[i] = variationMap.get(i)[0];
					v2[i] = variationMap.get(i)[1];
				}
				double absolute = Math.abs(new SpearmansCorrelation().correlation(v1,v2));
				fitness = fitness * absolute * this.variationMap.size();
			}
			else
				fitness = 0.00001;
		}*/
	}

	protected final int MIN_SIZE_VARIATION_MAP = 15;
	protected double parentFitness;

	private ArrayList<double[]> variationMap = new ArrayList<double[]>();
	public void registerParentsFitness(double parentFitness) {
		this.parentFitness = parentFitness;
	}

	public void registerVariation(double[] variation) {
		this.variationMap.add(variation);
	}

	public ArrayList<double[]> getVariationMap() {
		return variationMap;
	}

}
