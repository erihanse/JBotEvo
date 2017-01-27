package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Random;

import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;

public class ODNEATMacroCoevolCrossover extends Crossover<ODNEATMacroNodeGene>{

	protected Random random;

	public ODNEATMacroCoevolCrossover(Random random){
		this.random = random;
	}

	@Override
	/**
	 * NOTE: method receives a copy of the parents.
	 */
	public ODNEATMacroNodeGene applyCrossover(ODNEATMacroNodeGene[] parentsCopy, String childId) {
		ODNEATMacroNodeGene best, worst;
		//get the best
		if(parentsCopy[0].getFitness() == parentsCopy[1].getFitness()){
			best = parentsCopy[0].getBehaviourGene().getConnectionsList().size() > parentsCopy[1].getBehaviourGene().getConnectionsList().size() ? parentsCopy[1] : parentsCopy[0];
		}
		else {
			best = parentsCopy[0].getFitness() > parentsCopy[1].getFitness() ? parentsCopy[0] : parentsCopy[1];
		}
		//the worst is the other one.
		worst = best == parentsCopy[0] ? parentsCopy[1] : parentsCopy[0];
		
		//extract evolved anns
		EvolvedANNBehaviourGene b = (EvolvedANNBehaviourGene) best.getBehaviourGene(),
				w = (EvolvedANNBehaviourGene) worst.getBehaviourGene();

		//get the **active** link genes
		ArrayList <ODNEATLinkGene> bestGenes = (ArrayList<ODNEATLinkGene>) b.getLinkGenes(false), 
				worstGenes = (ArrayList<ODNEATLinkGene>) w.getLinkGenes(false);

		ArrayList<ODNEATLinkGene>[] matchingGenes = getMatchingGenes(bestGenes, worstGenes);
		ArrayList<ODNEATLinkGene> nonMatchingGenes = getNonMatchingGenes(bestGenes, worstGenes);

		//list of node genes, link genes of child
		ArrayList<ODNEATLinkGene> childLinks = new ArrayList<ODNEATLinkGene>();
		ArrayList<ODNEATNodeGene> childNodes = new ArrayList<ODNEATNodeGene>();
		ArrayList<Long> nodesAdded = new ArrayList<Long>();
		addStructure(childLinks, childNodes, nodesAdded, nonMatchingGenes, b);
		addStructure(childLinks, childNodes, nodesAdded, matchingGenes[0], b);
		addStructure(childLinks, childNodes, nodesAdded, matchingGenes[1], w);

		EvolvedANNBehaviourGene offspring = b.copy();
		b.getConnectionsList().clear();
		b.getNodesList().clear();
		
		for(ODNEATLinkGene link : childLinks)
			b.insertSingleLinkGene(link);
		for(ODNEATNodeGene node : childNodes)
			b.insertSingleNodeGene(node);
		
		ODNEATMacroNodeGene o = best.copy();
		o.setBehaviourGene(offspring);
		
		return o;
	}

	private void addStructure(ArrayList<ODNEATLinkGene> childLinks,
			ArrayList<ODNEATNodeGene> childNodes, ArrayList<Long> nodesAdded,
			ArrayList<ODNEATLinkGene> linksToAdd, EvolvedANNBehaviourGene b) {
		for(ODNEATLinkGene link : linksToAdd){
			ODNEATNodeGene[] connectionNodes = null;
			try {
				connectionNodes = getConnectionNodes(link, b);

			} catch (Exception e) {
				System.out.println("this should not happen...");
				e.printStackTrace();
			}
			ODNEATNodeGene from = connectionNodes[0], to = connectionNodes[1];
			//because of the macro-neurons removal of structure....
			if(from != null && to != null){
				if(!nodesAdded.contains(from.getInnovationNumber())){
					nodesAdded.add(from.getInnovationNumber());
					childNodes.add(from);
				}
				if(!nodesAdded.contains(to.getInnovationNumber())){
					nodesAdded.add(to.getInnovationNumber());
					childNodes.add(to);
				}
				childLinks.add(link);
			}
		}	
	}

	private ODNEATNodeGene[] getConnectionNodes(ODNEATLinkGene link,
			EvolvedANNBehaviourGene b) throws Exception {
		ODNEATNodeGene from = null, to = null;

		for(ODNEATNodeGene n : b.getNodesList()){
			if(n.getInnovationNumber() == link.getFromId())
				from = n;
			if(n.getInnovationNumber() == link.getToId())
				to = n;
		}
		return new ODNEATNodeGene[]{from, to};
	}

	private ArrayList<ODNEATLinkGene> getNonMatchingGenes(
			ArrayList<ODNEATLinkGene> bestGenes,
			ArrayList<ODNEATLinkGene> worstGenes) {
		ArrayList<ODNEATLinkGene> nonMatchingGenes = new ArrayList<ODNEATLinkGene>();
		for(ODNEATLinkGene bestLink : bestGenes){
			if(!worstGenes.contains(bestLink)){
				nonMatchingGenes.add(bestLink);
			}
		}
		return nonMatchingGenes;
	}

	//matching genes from any parent randomly
	private ArrayList<ODNEATLinkGene>[] getMatchingGenes(
			ArrayList<ODNEATLinkGene> bestGenes,
			ArrayList<ODNEATLinkGene> worstGenes) {
		//matching genes taken from the best parent
		ArrayList<ODNEATLinkGene> bestMatching = new ArrayList<ODNEATLinkGene>(), worstMatching = new ArrayList<ODNEATLinkGene>();
		for(ODNEATLinkGene bestLink : bestGenes){
			//matching link gene
			if(worstGenes.contains(bestLink)){
				if(random.nextBoolean()){
					bestMatching.add(bestLink);
					if(bestMatching.get(bestMatching.size() - 1) == null){
						System.out.println("matched best null");
						System.exit(0);
					}
				}
				else {
					int index = worstGenes.indexOf(bestLink);
					worstMatching.add(worstGenes.get(index));
					if(worstMatching.get(worstMatching.size() - 1) == null){
						System.out.println("matched worst null");
						System.exit(0);
					}
				}
			}
		}

		return new ArrayList[]{bestMatching, worstMatching};
	}

}
