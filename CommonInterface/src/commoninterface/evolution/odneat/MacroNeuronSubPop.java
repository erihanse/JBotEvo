package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class MacroNeuronSubPop implements RobotPopulation<ODNEATMacroNodeGene> {

	private String macroName;

	private ArrayList<ODNEATMacroSpecies> speciesList = new ArrayList<ODNEATMacroSpecies>();
	protected CompatibilityCalculator calc;
	private Random random;
	private double sumAverageSpeciesFitness;
	protected int maxSize = 40, speciesCount = 0;

	private int nodeId;
	private int robotId;
	
	private String currentId;
	
	public MacroNeuronSubPop(Random random, String macroName, CompatibilityCalculator calc, int robotId) {
		this.macroName = macroName;
		this.calc = calc;
		this.random = random;
		this.robotId = robotId;
		nodeId = 1;
	}

	public void insertMacroNeuronInPop(ODNEATMacroNodeGene macro, boolean currentIdMatters) {
		boolean foundSpecies = false;

		/*if(macro.fitness == 0){
			System.out.println("ADDING WITH FIT = 0");
			macro.fitness = 0.00001;
		}*/
		for(int i = 0; !foundSpecies && i < speciesList.size(); i++){
			if(isCompatible(macro, speciesList.get(i))){
				speciesList.get(i).addNewSpeciesMember(macro);
				foundSpecies = true;
			}
		}
		if(!foundSpecies){
			assignToNewSpecies(macro);
		}
		
		computeSumAdjustedFitness();

		//check if we are over or equal the population size
		int size = computeSize();
//		System.out.println("SIZE" + size + "; MAX SIZE: " + maxSize);
		if(size > this.maxSize){
			this.removeWorstAdjustedFitness(currentIdMatters);
		}
	}
	
	public ODNEATMacroNodeGene reproduce(
			Selector<ODNEATMacroNodeGene> selector, Crossover<ODNEATMacroNodeGene> crossover,
			Mutator<ODNEATMacroNodeGene> mutator) {
		
		String newId = this.macroName + "." + this.robotId + "." + (this.nodeId++);
		ODNEATMacroSpecies species = chooseParentSpecies();
		
		ODNEATMacroNodeGene macro = species.produceOffspring(selector, crossover, mutator, newId);
		this.setCurrentId(String.valueOf(newId));

		this.insertMacroNeuronInPop(macro, true);
		
		return macro;
	}

	private void setCurrentId(String id) {
		this.currentId = id;
	}

	private void assignToNewSpecies(ODNEATMacroNodeGene macro) {
		speciesCount++;
		ODNEATMacroSpecies species = new ODNEATMacroSpecies(random, speciesCount);
		species.addNewSpeciesMember(macro);
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
	
	protected void removeWorstAdjustedWithoutRestriction() {
		double worstAdjustedFitness = Double.MAX_VALUE;
		ODNEATMacroSpecies worstSpecies = null;
		int worstAdjustedIndex = -1;
		for(ODNEATMacroSpecies s : speciesList){
			for(int i = 0; i < s.getSpeciesSize(); i++){
				ODNEATMacroNodeGene current = s.getGeneAtIndex(i);
				if(current.getAdjustedFitness() < worstAdjustedFitness){
					worstAdjustedFitness = current.getAdjustedFitness();
					worstSpecies = s;
					worstAdjustedIndex = i;
				}
			}
		}

		if(worstAdjustedIndex != -1){
			worstSpecies.removeAtIndex(worstAdjustedIndex);
		}
	}

	protected void removeWorstAdjustedWithRestriction() {
		double worstAdjustedFitness = Double.MAX_VALUE;
		ODNEATMacroSpecies worstSpecies = null;
		int worstAdjustedIndex = -1;
		for(ODNEATMacroSpecies s : speciesList){
			for(int i = 0; i < s.getSpeciesSize(); i++){
				ODNEATMacroNodeGene current = s.getGeneAtIndex(i);
				if(!current.getEvolutionId().equals(currentId) && current.getAdjustedFitness() < worstAdjustedFitness){
					worstAdjustedFitness = current.getAdjustedFitness();
					worstSpecies = s;
					worstAdjustedIndex = i;
				}
			}
		}
		if(worstAdjustedIndex != -1){
			worstSpecies.removeAtIndex(worstAdjustedIndex);
		}
	}
	
	public void eliminateExtinctSpecies() {
		Iterator<ODNEATMacroSpecies> it = this.speciesList.iterator();
		while(it.hasNext()){
			ODNEATMacroSpecies current = it.next();
			if(current.getSpeciesSize() == 0)
				it.remove();
		}
	}

	public int computeSize() {
		int size = 0;
		for(ODNEATMacroSpecies s : this.speciesList){
			size += s.getSpeciesSize();
		}
		return size;
	}

	private boolean isCompatible(ODNEATMacroNodeGene applicant,
			ODNEATMacroSpecies species) {
		if(calc.getCompatibilityThreshold() < 0)
			return false;
		ODNEATMacroNodeGene representative = species.getSpeciesRepresentative();

		double compatScore = calc.computeCompatibilityScore(applicant, representative);
		return compatScore < calc.getCompatibilityThreshold();
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
	public ODNEATMacroNodeGene reproduce() {
		return null;
	}

	@Override
	public ODNEATMacroNodeGene getFittestIndividual() {
		double maxFitness = Double.MIN_VALUE;
		ODNEATMacroNodeGene best = null;

		for(ODNEATMacroSpecies species : this.speciesList){
			for(ODNEATMacroNodeGene current : species.getGenomes()){
				if(current.getFitness() > maxFitness){
					maxFitness = current.getFitness();
					best = current;
				}
			}
		}
		return best;
	}

	protected ODNEATMacroSpecies chooseParentSpecies() {
		//
		this.computeSumAdjustedFitness();

		double[] probs = getAllSpeciesSpawnProbabilities();
		double value = random.nextDouble();
		ODNEATMacroSpecies selected = null;
		for(int i = 0; i < probs.length; i++){
			value -= probs[i];
			if(value <= 0){
				selected = this.speciesList.get(i);
				i = probs.length;
			}
		}
		//System.out.println("PROBS: " + Arrays.toString(probs) + ";" + this.sumAverageSpeciesFitness);
		//System.out.println("SPECIES in the subpop: " + this.speciesList.size());
		//System.out.println("new: " + this.sumAverageSpeciesFitness);
		/*for(int i = 0; i < this.speciesList.size(); i++){
			System.out.println(this.speciesList.get(i).getGenomes().size() + ";" + this.speciesList.get(i).getSpeciesAvgAdjustedFitness());
		}*/
		return selected;
	}

	public double[] getAllSpeciesSpawnProbabilities(){
		if(speciesList.size() == 1)
			return new double[]{1.0};
	//	if(this.sumAverageSpeciesFitness == 0)
	//		this.sumAverageSpeciesFitness = 0.00000001;
	//	System.out.println("SPECIES: " + this.speciesList.size());
		double[] spawnValues = new double[this.speciesList.size()];
		for(int i = 0; i < speciesList.size(); i++){
			spawnValues[i] = this.speciesList.get(i).getSpeciesAvgAdjustedFitness() 
					/ this.sumAverageSpeciesFitness;
		}
		return spawnValues;
	}

	protected void computeSumAdjustedFitness() {
		this.sumAverageSpeciesFitness = 0;
		for(ODNEATMacroSpecies s : this.speciesList){
			this.sumAverageSpeciesFitness += s.getSpeciesAvgAdjustedFitness();
		}
	}
	
	public ArrayList<ODNEATMacroSpecies> getSpeciesList(){
		return this.speciesList;
	}
}
