package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public abstract class Selector<E> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Random random;
	
	public void setRandom(Random random){
		this.random = random;
	}
	
	public abstract E[] selectParents(Collection<E> individuals);
	
}
