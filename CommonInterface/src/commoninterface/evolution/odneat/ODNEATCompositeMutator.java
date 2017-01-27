package commoninterface.evolution.odneat;

import java.util.ArrayList;

import commoninterface.evolution.ODNEATGenome;


public class ODNEATCompositeMutator extends Mutator<ODNEATGenome>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<Mutator<ODNEATGenome>> mutators;
	
	public ODNEATCompositeMutator(){
		this.mutators = new ArrayList<Mutator<ODNEATGenome>>();
	}
	
	public void registerMutator(Mutator<ODNEATGenome> newMutator){
		this.mutators.add(newMutator);
	}
	
	
	/*public ODNEATCompositeMutator(ODNEATAddLinkMutator linkMutator, 
			ODNEATAddNodeMutator nodeMutator, ODNEATConnectionWeightMutator weightMutator){
		this.addLinkMutator = linkMutator;
		this.addNodeMutator = nodeMutator;
		this.weightMutator = weightMutator;
	}*/
	
	@Override
	public void applyMutation(ODNEATGenome mutatee) {
		for(Mutator<ODNEATGenome> mutator : mutators){
			mutator.applyMutation(mutatee);
		}
	}

}
