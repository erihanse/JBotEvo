package commoninterface.evolution.odneat;

public interface Species <E>{

	public E getSpeciesRepresentative();
	
	public E produceOffspring(Selector<E> selector, Crossover<E> crossover, Mutator<E> mutator, 
			String offspringId);
	
	public boolean containsMember(E e);
	
	public void addNewSpeciesMember(E e);
	
	public int getSpeciesSize();
	public int getSpeciesId();
}
