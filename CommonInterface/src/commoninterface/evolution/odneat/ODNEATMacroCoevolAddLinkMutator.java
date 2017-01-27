package commoninterface.evolution.odneat;

import java.util.ArrayList;

import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;

public class ODNEATMacroCoevolAddLinkMutator extends Mutator<ODNEATMacroNodeGene>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int MAX_LINK_ATTEMPTS = 30;

	protected double connectionWeightRange;

	protected ODNEATInnovationManager dib;

	public ODNEATMacroCoevolAddLinkMutator(double weightRange, ODNEATInnovationManager dib){
		this.connectionWeightRange = weightRange;
		this.dib = dib;
	}

	@Override
	public void applyMutation(ODNEATMacroNodeGene macronode) {

		if(! (macronode.getBehaviourGene() instanceof PreprogrammedGene) 
				&& !(macronode.getBehaviourGene() instanceof MacroBehaviourGene)){
			try {
				if(macronode.getBehaviourGene() instanceof EvolvedANNBehaviourGene && random.nextDouble() < this.mutationProb){
					EvolvedANNBehaviourGene bGene = (EvolvedANNBehaviourGene) macronode.getBehaviourGene();
					mutateEvolvedANNGene(bGene);
				}
				else {
					System.out.println(macronode.getClass());
					throw new Exception("Unsupported type");
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void mutateEvolvedANNGene(EvolvedANNBehaviourGene net) {
		ArrayList<ODNEATNodeGene> fromNodes = net.getNodesList();
		ArrayList<ODNEATNodeGene> toNodes = net.getNodesList();

		ArrayList<ODNEATLinkGene> links = (ArrayList<ODNEATLinkGene>) net.getLinkGenes(true);
		// find a new available link
		int tries = 0;
		ODNEATLinkGene newLink = null;
		while (newLink == null && tries < MAX_LINK_ATTEMPTS) {
			//try some recurrent
			long toNode = toNodes.get(random.nextInt(toNodes.size())).getInnovationNumber();
			long fromNode = fromNodes.get(random.nextInt(fromNodes.size())).getInnovationNumber();
			if(!existsLinks(fromNode, toNode, links)){
				newLink = dib.submitLinkInnovation(fromNode, toNode);
				newLink.setWeight(generateRandomWeight());
				net.insertSingleLinkGene(newLink);
			}
			tries++;
		}
	}
	
	protected double generateRandomWeight() {
		return random.nextDouble() * connectionWeightRange * 2 - connectionWeightRange;
	}

	protected boolean existsLinks(long fromNode, long toNode, ArrayList<ODNEATLinkGene> links) {
		for(ODNEATLinkGene g : links){
			if(g.getFromId() == fromNode && g.getToId() == toNode)
				return true;
		}
		return false;
	}



}
