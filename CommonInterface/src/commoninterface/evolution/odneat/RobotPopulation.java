package commoninterface.evolution.odneat;

public interface RobotPopulation<E> {

	public int getCurrentPopulationSize();
	public int getMaxPopulationSize();
	
	public E reproduce();
	
	public E getFittestIndividual();
}
