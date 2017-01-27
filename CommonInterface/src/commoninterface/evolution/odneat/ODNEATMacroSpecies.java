package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Random;


public class ODNEATMacroSpecies {

	private double averageAdjustedFitness;
	private ArrayList<ODNEATMacroNodeGene> species;
	private Random random;
	private int speciesId;

	public ODNEATMacroSpecies(Random random, int speciesId) {
		this.speciesId = speciesId;
		species = new ArrayList<ODNEATMacroNodeGene>();
		this.random = random;
	}

	public double getSpeciesAvgAdjustedFitness() {
		return this.averageAdjustedFitness;
	}
	
	public void adjustFitness() {
		int newSize = this.species.size();

		averageAdjustedFitness = 0;

		for(ODNEATMacroNodeGene element : species){
			double fitness = element.getFitness();
			fitness /= newSize;
			element.setAdjustedFitness(fitness);
			averageAdjustedFitness += fitness;
		}

		averageAdjustedFitness /= newSize;
	}

	public ODNEATMacroNodeGene produceOffspring(Selector<ODNEATMacroNodeGene> selector, 
			Crossover<ODNEATMacroNodeGene> xOver, Mutator<ODNEATMacroNodeGene> mut,
			String newControllerId){
		ODNEATMacroNodeGene offspring;

		//return a copy of the parent selected.
		ODNEATMacroNodeGene[] parents = selector.selectParents(species);
		if(parents.length == 1 || random.nextDouble() >= xOver.getCrossoverProbability()){
			offspring = parents[0];
			mut.applyMutation(offspring);
			offspring.setId(newControllerId);
		}
		else {
			offspring = xOver.applyCrossover(parents, newControllerId);
			mut.applyMutation(offspring);
		}
		
		offspring.registerParentsFitness(parents.length == 1 ? parents[0].fitness : (parents[0].fitness + parents[1].fitness)/2);

		return offspring;
	}

	public void addNewSpeciesMember(ODNEATMacroNodeGene specieMember) {
		this.species.add(specieMember);
		specieMember.setSpeciesId(this.speciesId);
		adjustFitness();
	}

	public int getSpeciesSize() {
		return this.species.size();
	}

	public ODNEATMacroNodeGene getGeneAtIndex(int i) {
		return this.species.get(i);
	}

	public ODNEATMacroNodeGene getSpeciesRepresentative() {
		return this.getRandomSpeciesElement();
	}

	private ODNEATMacroNodeGene getRandomSpeciesElement() {
		int index = random.nextInt(species.size());
		return species.get(index);
	}
	
	public ArrayList<ODNEATMacroNodeGene> getGenomes(){
		return this.species;
	}
	
	public void removeAtIndex(int index){
		species.remove(index);
		adjustFitness();
	}

}
