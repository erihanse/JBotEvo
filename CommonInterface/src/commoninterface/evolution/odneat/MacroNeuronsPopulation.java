package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import commoninterface.evolution.ALGDescriptor;

public class MacroNeuronsPopulation implements Serializable {

	private CompatibilityCalculator calc;
	protected HashMap<Long,MacroNeuronSubPop> subpops = new HashMap<Long,MacroNeuronSubPop>();
	private Random random;
	private final double REUSE_PROB;
	private ODNEATMacroCoevolCompositeMutator mutator;
	private ODNEATMacroCoevolTournamentSelector selector;
	private ODNEATMacroCoevolCrossover crossover;


	public MacroNeuronsPopulation(Random random, CompatibilityCalculator calc, ALGDescriptor descriptor, 
			ODNEATInnovationManager inManager, int robotId, double reuseProb){
		this.calc = calc;
		this.random = random;
		this.REUSE_PROB = reuseProb;
		//this.REUSE_PROB = 0.0;
		//	System.out.println("REUSE PROB: " + REUSE_PROB);
		initialiseEvolutionaryOperators(descriptor, inManager, robotId);
	}

	public void initialiseEvolutionaryOperators(ALGDescriptor descriptor, 
			ODNEATInnovationManager inManager, int robotId) {
		this.mutator = initialiseMutator(descriptor, this.random, inManager, robotId);
		this.selector = new ODNEATMacroCoevolTournamentSelector();
		selector.setRandom(random);
		this.crossover = new ODNEATMacroCoevolCrossover(random);
		crossover.setCrossoverProbability(descriptor.getPXover());		
	}

	protected ODNEATMacroCoevolCompositeMutator initialiseMutator(ALGDescriptor descriptor, 
			Random random, ODNEATInnovationManager inManager, int robotId) {

		ODNEATMacroCoevolCompositeMutator compMut = new ODNEATMacroCoevolCompositeMutator();

		//add link mutator
		setupAddLinkMutator(compMut, descriptor, random, inManager);

		//add node mutator
		setupAddNodeMutator(compMut, descriptor, random, inManager);

		//connection weight mutator
		setupConnectionWeightMutator(compMut, descriptor, random);

		compMut.setMutationProbability(descriptor.getPMutation());
		compMut.setRandom(random);

		return compMut;
	}

	private void setupConnectionWeightMutator(ODNEATMacroCoevolCompositeMutator compMut,
			ALGDescriptor descriptor, Random random) {
		//double pWeightReplaced, double pToggle, double perturbMagnitude, double weightRange
		ODNEATMacroCoevolConnectionWeightMutator weightMut = new ODNEATMacroCoevolConnectionWeightMutator(descriptor.getPWeightReplaced(),
				descriptor.getPToggleLink(), descriptor.getMaxPerturb(), 
				descriptor.getWeightRange());
		weightMut.setMutationProbability(descriptor.getPWeightMutation());
		weightMut.setRandom(random);
		compMut.registerMutator(weightMut);

	}

	private void setupAddNodeMutator(ODNEATMacroCoevolCompositeMutator compMut,
			ALGDescriptor descriptor, Random random,
			ODNEATInnovationManager inManager) {
		ODNEATMacroCoeevolAddNodeMutator nodeMut = new ODNEATMacroCoeevolAddNodeMutator(inManager);
		nodeMut.setMutationProbability(descriptor.getPAddNode());
		nodeMut.setRandom(random);

		compMut.registerMutator(nodeMut);
	}

	private void setupAddLinkMutator(ODNEATMacroCoevolCompositeMutator compMut,
			ALGDescriptor descriptor, Random random,
			ODNEATInnovationManager inManager) {
		ODNEATMacroCoevolAddLinkMutator linkMut = new ODNEATMacroCoevolAddLinkMutator(descriptor.getWeightRange(), inManager);
		linkMut.setMutationProbability(descriptor.getPAddLink());
		linkMut.setRandom(random);
		compMut.registerMutator(linkMut);
	}

	public void initialisePop(Long key, String macroName, int macroId, int robotId) {
		//	System.out.println("INITIALISED POP: " + macroName + ";" + key);
		MacroNeuronSubPop subPop = new MacroNeuronSubPop(random, macroName, calc, robotId);
		subpops.put(key, subPop);
	}

	public void addMacroNeuronToPop(Long key, ODNEATMacroNodeGene macro, boolean idMatters){
		//	System.out.println("ADD MACRO TO POP: " + key);
		subpops.get(key).insertMacroNeuronInPop(macro, idMatters);
		//	System.out.println("SIZE AFTER INSERTION: " + subpops.get(key).computeSize());
	}

	public ODNEATMacroNodeGene selectNext(Long key) {
		try{
			//	System.out.println("SELECTING NEXT: " + key);
			MacroNeuronSubPop pop = subpops.get(key);
			if(random.nextDouble() < this.REUSE_PROB){
				//	System.out.println("REUSING");
				return pop.getFittestIndividual();
			}
			else {
				//	System.out.println("REPRODUCING");
				return pop.reproduce(selector, crossover, mutator);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	public void updateMacroNodePerformance(String controllerId, double fitness,
			Long key, String evolutionId, ODNEATMacroNodeGene original) {
		MacroNeuronSubPop pop = subpops.get(key);
		for(ODNEATMacroSpecies spec : pop.getSpeciesList()){
			for(ODNEATMacroNodeGene g : spec.getGenomes()){
				if(g.getEvolutionId().equalsIgnoreCase(evolutionId)){
					//update individual fitness
					g.update(controllerId, fitness, original);
					//update adjusted fitness
					spec.adjustFitness();
					break;
				}
			}
		}

	}

}
