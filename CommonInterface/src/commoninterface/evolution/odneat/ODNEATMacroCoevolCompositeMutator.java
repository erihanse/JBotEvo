package commoninterface.evolution.odneat;

import java.util.ArrayList;


public class ODNEATMacroCoevolCompositeMutator extends Mutator<ODNEATMacroNodeGene>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<Mutator<ODNEATMacroNodeGene>> mutators;
	
	public ODNEATMacroCoevolCompositeMutator(){
		this.mutators = new ArrayList<Mutator<ODNEATMacroNodeGene>>();
	}
	
	public void registerMutator(Mutator<ODNEATMacroNodeGene> newMutator){
		this.mutators.add(newMutator);
	}
	
	@Override
	public void applyMutation(ODNEATMacroNodeGene mutatee) {
		for(Mutator<ODNEATMacroNodeGene> mutator : mutators){
			mutator.applyMutation(mutatee);
		}
	}

}
