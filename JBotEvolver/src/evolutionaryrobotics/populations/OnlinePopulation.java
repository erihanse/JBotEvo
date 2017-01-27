package evolutionaryrobotics.populations;

import evolutionaryrobotics.neuralnetworks.Chromosome;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class OnlinePopulation extends Population{

	private int size;

	public OnlinePopulation(Arguments arguments) {
		super(arguments);
		size = arguments.getArgumentAsIntOrSetDefault("size", size);
	}

	@Override
	public void createRandomPopulation() {
		randomNumberGenerator.setSeed(generationRandomSeed);
		//chromosomes = new Chromosome[getPopulationSize()];
		//bestChromosome = null;
		setGenerationRandomSeed(randomNumberGenerator.nextInt());
	}

	public void evolve() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean evolutionDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNumberOfCurrentGeneration() {
		return 0;
	}

	@Override
	public int getPopulationSize() {
		return size;
	}

	@Override
	public int getNumberOfChromosomesEvaluated() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Chromosome getNextChromosomeToEvaluate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEvaluationResult(Chromosome chromosome, double fitness) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEvaluationResultForId(int pos, double fitness) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createNextGeneration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getLowestFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getHighestFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Chromosome getBestChromosome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chromosome[] getTopChromosome(int number) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chromosome getChromosome(int chromosomeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupIndividual(Robot r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Chromosome[] getChromosomes() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
