package commoninterface.evolution.odneat;

import java.util.ArrayList;

import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;

public class ODNEATMacroCoeevolAddNodeMutator extends Mutator<ODNEATMacroNodeGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ODNEATInnovationManager dib;

	public ODNEATMacroCoeevolAddNodeMutator(ODNEATInnovationManager dib) {
		this.dib = dib;
	}

	@Override
	public void applyMutation(ODNEATMacroNodeGene node) {
		if(random.nextDouble() < this.mutationProb){
			//split input nodes vs. split something in the inner part of the net, 50/50
			/*if(random.nextBoolean()){
				splitTemplateInputsOf(node, mutatee);
			}
			else {*/
			EvolvedANNBehaviourGene bGene = (EvolvedANNBehaviourGene) node.getBehaviourGene();
			mutateEvolvedANNGene(bGene);
			/*	}*/
		}
	}

	private void mutateEvolvedANNGene(EvolvedANNBehaviourGene bGene) {
		ArrayList<ODNEATLinkGene> enabledLinks = bGene.getLinkGenes(true);
		ODNEATLinkGene chosen = enabledLinks.get(random.nextInt(enabledLinks.size()));
		//chosen.setEnabled(false);
		ODNEATNodeGene newNode = dib.submitNodeInnovation();
		//ODNEATLinkGene newLower = dib.submitLinkInnovation(chosen.getFromId(), newNode.getInnovationNumber());
		ODNEATLinkGene newUpper = dib.submitLinkInnovation(newNode.getInnovationNumber(), chosen.getToId());
		//newLower.setWeight(1);
		newUpper.setWeight(chosen.getWeight());
		chosen.setWeight(1.0);
		chosen.setToId(newNode.getInnovationNumber());
		//add
		//bGene.insertSingleLinkGene(newLower);
		bGene.insertSingleLinkGene(newUpper);
		bGene.insertSingleNodeGene(newNode);
	}


}
