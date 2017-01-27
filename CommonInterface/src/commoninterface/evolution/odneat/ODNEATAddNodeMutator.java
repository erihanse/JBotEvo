package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.HashMap;

import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;


public class ODNEATAddNodeMutator extends Mutator<ODNEATGenome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ODNEATInnovationManager dib;

	public ODNEATAddNodeMutator(ODNEATInnovationManager dib) {
		this.dib = dib;
	}

	@Override
	public void applyMutation(ODNEATGenome mutatee) {
		double nodeRandVal = random.nextDouble();
		ArrayList<ODNEATLinkGene> enabledLinks;
		// ArrayList nodes;
		ODNEATLinkGene chosen, newUpper;
		ODNEATNodeGene newNode;
		// System.out.println(pAddNode);
		if (nodeRandVal < this.mutationProb && ((enabledLinks = (ArrayList<ODNEATLinkGene>) mutatee.getLinkGenes(true)).size() > 0)) {
			chosen = enabledLinks.get(random.nextInt(enabledLinks.size()));
			// disable old link
			//chosen.setEnabled(false);
			newNode = dib.submitNodeInnovation();
			//requires that these are **outputs** of the macro-neurons (the inputs are maintained by the macro-neurons themselves)
			//newLower = dib.submitLinkInnovation(chosen.getFromId(), newNode.getInnovationNumber());
			newUpper = dib.submitLinkInnovation(newNode.getInnovationNumber(), chosen.getToId());
			//newLower.setWeight(1);
			newUpper.setWeight(chosen.getWeight());
			chosen.setWeight(1.0);
			chosen.setToId(newNode.getInnovationNumber());
			//removed from the genome so that it is not re-enabled
			/*if(verifyTypeOfNewConnection(newLower, chosen)){
				mutatee.getLinkGenes(true).remove(chosen);
			}*/			
			// now update the chromosome with new node and 2 new links
			mutatee.insertSingleNodeGene(newNode);
			//mutatee.insertSingleLinkGene(newLower);
			mutatee.insertSingleLinkGene(newUpper);
		}

	//mutatee.sortGenes();
	}


}
