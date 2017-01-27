package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Collection;


public class ODNEATMacroCoevolTournamentSelector extends Selector<ODNEATMacroNodeGene> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ODNEATMacroNodeGene performATournament(ODNEATMacroNodeGene pOne, ODNEATMacroNodeGene pTwo) {
		ODNEATMacroNodeGene winner = pOne.getFitness() > pTwo.getFitness() ? pOne : pTwo;
		return winner;
	}

	//selection returns a copy of the chosen parents.
	public ODNEATMacroNodeGene[] selectParents(Collection<ODNEATMacroNodeGene> individuals) {
		
		ArrayList<ODNEATMacroNodeGene> members = (ArrayList<ODNEATMacroNodeGene>) individuals;
		ODNEATMacroNodeGene[] parents;
		if(members.size() == 1){
			parents = new ODNEATMacroNodeGene[1];
			parents[0] = (ODNEATMacroNodeGene) members.get(0).copy();
			return parents;
		}
		else {
			parents = new ODNEATMacroNodeGene[2];
			for(int i = 0; i < 2; i++){
				ODNEATMacroNodeGene pOne = members.get(random.nextInt(members.size()));
				ODNEATMacroNodeGene pTwo = members.get(random.nextInt(members.size()));
				parents[i] = (ODNEATMacroNodeGene) this.performATournament(pOne, pTwo).copy();
			}
		}
		return parents;
	}
}