package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import commoninterface.evolution.ODNEATGenome;


public class ODNEATPopulation implements Serializable, RobotPopulation<ODNEATGenome>{

	private static final long serialVersionUID = 1L;

	protected ArrayList<ODNEATSpecies> speciesList;

	protected Random random;

	protected int speciesCount, robotId;

	protected double sumAverageSpeciesFitness;

	protected int maxSize;

	protected String currentId;

	protected ODNEATInnovationManager inManager;	
	protected CompatibilityCalculator calc;

	public ODNEATPopulation(Random random, CompatibilityCalculator calc, int maxSize){
		speciesList = new ArrayList<ODNEATSpecies>();
		this.speciesCount = 0;
		this.random = random;
		this.calc = calc;
		this.maxSize = maxSize;
	}

	public void setCurrentId(String currentId){
		this.currentId = currentId;
	}

	public ODNEATSpecies containsChromosome(ODNEATGenome current) {
		for(ODNEATSpecies s : this.speciesList){
			if(s.containsMember(current))
				return s;
		}

		return null;
	}

	public ODNEATGenome reproduce(String newId, 
			Selector<ODNEATGenome> selector, Crossover<ODNEATGenome> crossover,
			Mutator<ODNEATGenome> mutator) {
		//make room for the new one if necessary.
		//the addition method verifies the size.
		/*if(this.computeSize() > this.maxSize)
			this.removeWorstAdjustedFitness(currentId, tabuList);*/
		ODNEATGenome newChromosome;
		/*if(this.random.nextBoolean()){
			newChromosome = reuseBest();
			newChromosome.setId(newId);
		}
		else {*/
		ODNEATSpecies species = chooseParentSpecies();
		newChromosome = species.produceOffspring(selector, crossover, mutator, 
				newId);
		//}
		this.setCurrentId(String.valueOf(newId));

		this.addODNEATGenome(newChromosome, true);		
		return newChromosome;
	}

	private ODNEATGenome reuseBest() {
		System.out.println("rb");
		ODNEATGenome best = null;
		for(ODNEATSpecies s : this.speciesList){
			for(ODNEATGenome g : s.getGenomes()){
				if(best == null || g.getFitness() > best.getFitness()){
					best = g.copy();
				}
			}
		}
		best.setAge(0);
		best.setSpeciesId(0);
		System.out.println("rb: " + best.getId());
		return best;
	}

	protected ODNEATSpecies chooseParentSpecies() {
		if(speciesList.size() == 1)
			return this.speciesList.get(0);

		double[] probs = getAllSpeciesSpawnProbabilities();
		double value = random.nextDouble();
		ODNEATSpecies selected = null;
		for(int i = 0; i < probs.length; i++){
			value -= probs[i];
			if(value <= 0){
				selected = this.speciesList.get(i);
				i = probs.length;
			}
		}

		return selected;
	}

	/*public double getSpawnProbability(int specieId){
		boolean found = false;
		double value = 0;
		for(int i = 0; !found && i < this.speciesList.size(); i++){
			ODNEATSpecies s = this.speciesList.get(i);
			if(s.getSpeciesId() == specieId){
				found = true;
				value = s.getSpeciesAvgAdjustedFitness() / this.sumAverageSpeciesFitness;
			}
		}

		return value;
	}*/

	public double[] getAllSpeciesSpawnProbabilities(){
		double[] spawnValues = new double[this.speciesList.size()];
		for(int i = 0; i < speciesList.size(); i++){
			spawnValues[i] = this.speciesList.get(i).getSpeciesAvgAdjustedFitness()
					/ this.sumAverageSpeciesFitness;
		}
		return spawnValues;
	}

	protected void updateGenome(ODNEATGenome genome, double currentEnergyLevel){
		boolean found = false;
		for(int i = 0; !found && i < this.speciesList.size(); i++){
			ODNEATSpecies species = this.speciesList.get(i);
			if(species.containsMember(genome)){
				found = true;
				//System.out.println("==========\n found =========\n");
				//TODO:
				species.updateGenomeEnergyAndAge(genome.getId(), currentEnergyLevel, genome.getGenomeAge());
				//species.updateEnergyLevel(genome.getId(), currentEnergyLevel);
				//species.updateAge(genome.getId(), genome.getGenomeAge());
				this.computeSumAdjustedFitness();
			}
		}

		if(!found){
			System.out.println("not found.");

			System.out.println("searching for: " + genome.getId());
			for(ODNEATSpecies s : this.speciesList){
				for(ODNEATGenome g : s.getGenomes()){
					System.out.print(g.getId() + " ; " + (g.getId().equalsIgnoreCase(genome.getId())) + "\n");
				}
			}
			try{
				throw new Exception("");
			}
			catch(Exception e){
				e.printStackTrace();
			}
			System.exit(0);
		}
	}


	//restriction based insertion.
	//depends on how good it is for the population
	public void addConditionallyODNEATGenome(ODNEATGenome possibleAddition) {
		//here we can just add the chromosome because: 1
		/**
		 * 1 - if pop.size < max size, then it will be added without any problem
		 * 2 - if pop.size >= max size, then it will be added and the one with the worst adjusted fitness will be removed.
		 * 2.1 - if the new addition is the worst, it will be removed after being added, no harms done.
		 */
		this.addODNEATGenome(possibleAddition, true);

	}

	public void addODNEATGenome(ODNEATGenome c, boolean currentIdMatters){


		boolean foundSpecies = false;
		for(int i = 0; !foundSpecies && i < speciesList.size(); i++){
			if(isChromosomeCompatible(c, speciesList.get(i))){
				speciesList.get(i).addNewSpeciesMember(c);
				foundSpecies = true;
			}
		}
		if(!foundSpecies){
			assignToNewSpecies(c);
		}

		computeSumAdjustedFitness();

		//check if we are over or equal the population size
		int size = computeSize();
		if(size > this.maxSize){
			this.removeWorstAdjustedFitness(currentIdMatters);
		}
	}

	private boolean isChromosomeCompatible(ODNEATGenome applicant,
			ODNEATSpecies species) {
		ODNEATGenome representative = species.getSpeciesRepresentative();

		double compatScore = calc.computeCompatibilityScore(applicant, representative);
		return compatScore < calc.getCompatibilityThreshold();
	}

	public int computeSize() {
		int size = 0;
		for(ODNEATSpecies s : this.speciesList){
			size += s.getSpeciesSize();
		}
		return size;
	}

	protected void computeSumAdjustedFitness() {
		this.sumAverageSpeciesFitness = 0;
		for(ODNEATSpecies s : this.speciesList){
			this.sumAverageSpeciesFitness += s.getSpeciesAvgAdjustedFitness();
		}

		//TODO: FS MARKthis.sumAverageSpeciesFitness /= this.speciesList.size();
	}

	protected void assignToNewSpecies(ODNEATGenome c) {
		speciesCount++;
		ODNEATSpecies species = new ODNEATSpecies(random, speciesCount);
		species.addNewSpeciesMember(c);
		this.speciesList.add(species);

	}

	//remove the worst adjusted excluding the current
	public void removeWorstAdjustedFitness(boolean currentIdMatters){
		if(currentIdMatters){
			this.removeWorstAdjustedWithRestriction();
		}
		else
			removeWorstAdjustedWithoutRestriction();

		eliminateExtinctSpecies();

		computeSumAdjustedFitness();
	}

	public void eliminateExtinctSpecies() {
		Iterator<ODNEATSpecies> it = this.speciesList.iterator();
		while(it.hasNext()){
			ODNEATSpecies current = it.next();
			if(current.getSpeciesSize() == 0)
				it.remove();
		}
	}

	protected void removeWorstAdjustedWithoutRestriction() {
		double worstAdjustedFitness = Double.MAX_VALUE;
		ODNEATSpecies worstSpecies = null;
		int worstAdjustedIndex = -1;
		for(ODNEATSpecies s : speciesList){
			for(int i = 0; i < s.getSpeciesSize(); i++){
				ODNEATGenome current = s.getChromosomeAt(i);
				if(current.getAdjustedFitness() < worstAdjustedFitness){
					worstAdjustedFitness = current.getAdjustedFitness();
					worstSpecies = s;
					worstAdjustedIndex = i;
				}
			}
		}

		if(worstAdjustedIndex != -1){
			//tabuList.addElement(worstSpecies.getChromosomeAt(worstAdjustedIndex));
			worstSpecies.removeAtIndex(worstAdjustedIndex);
		}
	}

	protected void removeWorstAdjustedWithRestriction() {
		double worstAdjustedFitness = Double.MAX_VALUE;
		ODNEATSpecies worstSpecies = null;
		int worstAdjustedIndex = -1;
		for(ODNEATSpecies s : speciesList){
			for(int i = 0; i < s.getSpeciesSize(); i++){
				ODNEATGenome current = s.getChromosomeAt(i);
				if(!current.getId().equals(currentId) && current.getAdjustedFitness() < worstAdjustedFitness){
					worstAdjustedFitness = current.getAdjustedFitness();
					worstSpecies = s;
					worstAdjustedIndex = i;
				}
			}
		}
		//try{
		if(worstAdjustedIndex != -1){
			//tabuList.addElement(worstSpecies.getChromosomeAt(worstAdjustedIndex));
			worstSpecies.removeAtIndex(worstAdjustedIndex);
		}
		/*}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println(worstAdjustedIndex);
			System.exit(0);
		}*/

	}

	public double getSumAverageSpeciesFitness(){
		return this.sumAverageSpeciesFitness;
	}

	public double getProportionateAdjustedFitness(int speciesId) {

		for(ODNEATSpecies s : this.speciesList){
			if(s.getSpeciesId() == speciesId)
				return s.getSpeciesAvgAdjustedFitness() / this.getSumAverageSpeciesFitness();
		}
		return 0;
	}

	public ArrayList<ODNEATSpecies> getSpeciesList() {
		return this.speciesList;
	}

	@Override
	public int getCurrentPopulationSize() {
		return this.computeSize();
	}

	@Override
	public int getMaxPopulationSize() {
		return this.maxSize;
	}

	@Override
	//we use a different "reproduce" method (more parameters).
	public ODNEATGenome reproduce() {
		return null;
	}

	@Override
	public ODNEATGenome getFittestIndividual() {
		double maxFitness = Double.MIN_VALUE;
		ODNEATGenome best = null;

		for(ODNEATSpecies species : this.speciesList){
			for(ODNEATGenome current : species.getGenomes()){
				if(current.getFitness() > maxFitness){
					maxFitness = current.getFitness();
					best = current;
				}
			}
		}
		return best;
	}

	public CompatibilityCalculator getCompatibilityScoreCalculator() {
		return this.calc;
	}

	public void respeciate() {
		ArrayList<ODNEATGenome> allGenomes = new ArrayList<ODNEATGenome>();
		Iterator<ODNEATSpecies> specIt = this.speciesList.iterator();
		while(specIt.hasNext()){
			ODNEATSpecies spec = specIt.next();
			Iterator<ODNEATGenome> genIt = spec.getGenomes().iterator();
			while(genIt.hasNext()){
				ODNEATGenome genome = genIt.next();
				allGenomes.add(genome);
				genIt.remove();
			}
			specIt.remove();
		}

		for(ODNEATGenome genome : allGenomes){
			this.addODNEATGenome(genome, false);
		}
	}

	public ODNEATGenome reproduceWithMacroSpecialisation(String newGenomeId,
			Selector<ODNEATGenome> selector, Crossover<ODNEATGenome> crossover,
			Mutator<ODNEATGenome> mutator, MacroNeuronsPopulation macroPop) {
		ODNEATSpecies species = chooseParentSpecies();
		ODNEATGenome newChromosome = species.produceOffspring(selector, crossover, mutator, newGenomeId);
		this.setCurrentId(String.valueOf(newGenomeId));

		//macro selection
		MacroGenome g = (MacroGenome) newChromosome;
		HashMap<Long, ODNEATMacroNodeGene> map = g.getMacroNodeGenes();
		for(Long key : map.keySet()){
			map.put(key, macroPop.selectNext(key));
		}
		
		this.addODNEATGenome(g, true);		
		return newChromosome;
	}

}
