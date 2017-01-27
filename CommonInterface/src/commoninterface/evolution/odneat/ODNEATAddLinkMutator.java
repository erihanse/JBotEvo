package commoninterface.evolution.odneat;

import java.util.ArrayList;
import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;


public class ODNEATAddLinkMutator extends Mutator<ODNEATGenome>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int MAX_LINK_ATTEMPTS = 30;

	protected double connectionWeightRange;

	protected ODNEATInnovationManager dib;

	public ODNEATAddLinkMutator(double weightRange, ODNEATInnovationManager dib){
		this.connectionWeightRange = weightRange;
		this.dib = dib;
	}

	@Override
	public void applyMutation(ODNEATGenome mutatee) {
		double linkRandVal = random.nextDouble();
		ODNEATLinkGene newLink = null;
		/**
		 * network-level operations
		 */
		if (linkRandVal < this.mutationProb) {
			ArrayList<ODNEATNodeGene> fromNodes = selectSourceNodes(mutatee);
			ArrayList<ODNEATNodeGene> toNodes = selectDestinationNodes(mutatee);
			ArrayList<ODNEATLinkGene> links = (ArrayList<ODNEATLinkGene>) mutatee.getLinkGenes(true);
			// find a new available link
			int tries = 0;
			while (newLink == null && tries < MAX_LINK_ATTEMPTS) {
				//try some recurrent
				long toNode = toNodes.get(random.nextInt(toNodes.size())).getInnovationNumber();
				long fromNode = fromNodes.get(random.nextInt(fromNodes.size())).getInnovationNumber();;
				if(!existsLinks(fromNode, toNode, links)){
					newLink = dib.submitLinkInnovation(fromNode, toNode);
					newLink.setWeight(generateRandomWeight());
					mutatee.insertSingleLinkGene(newLink);
				}
				tries++;
			}
		}
	
		mutatee.sortGenes();
	}

	/**
	 * all except the macro neurons.
	 */
	protected ArrayList<ODNEATNodeGene> selectSourceNodes(
			ODNEATGenome mutatee) {

		return (ArrayList<ODNEATNodeGene>) mutatee.getNodeGenes();
	}

	/**
	 * all except the inputs and the macro nodes.
	 */
	protected ArrayList<ODNEATNodeGene> selectDestinationNodes(ODNEATGenome mutatee) {
		ArrayList<ODNEATNodeGene> destinationNodes = new ArrayList<ODNEATNodeGene>();
		ArrayList<ODNEATNodeGene> nodesToProcess;
		nodesToProcess = (ArrayList<ODNEATNodeGene>) mutatee.getNodeGenes();
		
		
		for(ODNEATNodeGene g : nodesToProcess){
			if(g.getType() != ODNEATNodeGene.INPUT)
				destinationNodes.add(g);
		}
		
		return destinationNodes;
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
