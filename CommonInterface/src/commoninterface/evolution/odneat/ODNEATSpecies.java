package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import commoninterface.evolution.ODNEATGenome;


//one species
public class ODNEATSpecies implements Serializable, Species<ODNEATGenome>{

	private static final long serialVersionUID = 1L;
	protected int speciesId;
	protected ArrayList<ODNEATGenome> species;
	protected Random random;

	protected double averageAdjustedFitness;

	public ODNEATSpecies(Random random, int speciesId) {
		this.speciesId = speciesId;
		species = new ArrayList<ODNEATGenome>();
		this.random = random;
	}

	public ArrayList<ODNEATGenome> getGenomes(){
		return this.species;
	}

	/*public boolean isChromosomeCompatible(ODNEATGenome applicant){
		double compatScore = Double.MAX_VALUE;
		//random representative
		ODNEATGenome representative = getRandomSpeciesElement();

		compatScore = new ODNEATSpecieHandler().compatibilityScore(applicant, representative, 
					this.excessCoeff, this.disjointCoeff, this.weightCoeff);

		return compatScore < this.compatibilityThreshold;
	}*/

	protected ODNEATGenome getRandomSpeciesElement() {
		int index = random.nextInt(species.size());
		return species.get(index);
	}

	@Override
	public ODNEATGenome getSpeciesRepresentative() {
		return this.getRandomSpeciesElement();
	}

	public void addNewSpeciesMember(ODNEATGenome specieMember) {
		this.species.add(specieMember);
		specieMember.setSpeciesId(this.speciesId);
		//we must adjust the fitness of each element after each addition or removal
		adjustFitness();
	}


	public void adjustFitness() {
		int newSize = this.species.size();

		averageAdjustedFitness = 0;

		for(ODNEATGenome element : species){
			double fitness = element.getFitness();
			fitness /= newSize;
			element.setAdjustedFitness(fitness);
			averageAdjustedFitness += fitness;
		}

		averageAdjustedFitness /= newSize;
	}

	//ODNEAT only generates one offspring at a time
	//the controller id is only used for statistics
	public ODNEATGenome produceOffspring(Selector<ODNEATGenome> selector, 
			Crossover<ODNEATGenome> xOver, Mutator<ODNEATGenome> mut,
			String newControllerId){
		ODNEATGenome offspring;

		//return a copy of the parent selected.
		ODNEATGenome[] parents = selector.selectParents(species);
		String pId;
		//System.out.println("cprob: " + xOver.getCrossoverProbability());
		if(parents.length == 1 || random.nextDouble() >= xOver.getCrossoverProbability()){
			//System.out.println("cross: " + xOver.getCrossoverProbability());
			pId = new String(parents[0].getId());
			offspring = parents[0];
			mut.applyMutation(offspring);
			offspring.setId(newControllerId);
		}
		else {
			//System.out.println("crossover! \\o/");
			offspring = xOver.applyCrossover(parents, newControllerId);
			mut.applyMutation(offspring);
		}

		return offspring;
	}

	public void removeWorstAdjustedFitness() {
		double worstAdjustedFitness = species.get(0).getAdjustedFitness();
		int index = 0;
		for(int i = 1; i < species.size(); i++){
			if(species.get(i).getAdjustedFitness() < worstAdjustedFitness){
				index = i;
				worstAdjustedFitness = species.get(i).getAdjustedFitness();
			}
		}
		species.remove(index);
		this.adjustFitness();
	}

	public boolean containsMember(ODNEATGenome member) {
		return species.contains(member);
	}

	public ODNEATGenome getChromosomeAt(int i) {
		return species.get(i);
	}

	public int getSpeciesSize(){
		return this.species.size();
	}

	public void removeAtIndex(int index){
		species.remove(index);
		adjustFitness();
	}

	public double getSpeciesAvgAdjustedFitness() {
		return this.averageAdjustedFitness;
	}

	public int getSpeciesId() {
		return this.speciesId;
	}


	public void updateEnergyLevel(String id, double currentEnergyLevel) {
		for(int i = 0; i < this.species.size(); i++){
			if(species.get(i).getId().equals(id)){
				species.get(i).updateEnergyLevel(currentEnergyLevel);
				this.adjustFitness();
				i = this.species.size();
			}
		}
	}

	public void clear() {
		this.species.clear();
	}

	/*public void updateAge(String id, int genomeAge) {
		for(int i = 0; i < this.species.size(); i++){
			if(species.get(i).getId().equals(id)){
				species.get(i).setAge(genomeAge);
				//this.adjustFitness();
				i = this.species.size();
			}
		}
	}*/

	public void updateGenomeEnergyAndAge(String id, double currentEnergyLevel, int genomeAge){
		for(int i = 0; i < this.species.size(); i++){
			if(species.get(i).getId().equals(id)){
				species.get(i).updateEnergyLevel(currentEnergyLevel);
				if(genomeAge < species.get(i).getGenomeAge()){
					/*try {
						throw new Exception("invalid age setting: " + genomeAge + "; " + species.get(i).getGenomeAge());
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}*/
				}
				species.get(i).setAge(genomeAge);
				this.adjustFitness();
				i = this.species.size();
			}
		}
	}
}
