package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Collection;

import commoninterface.evolution.ODNEATGenome;

public class ODNEATTournamentSelector extends Selector<ODNEATGenome> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ODNEATGenome performATournament(ODNEATGenome pOne, ODNEATGenome pTwo) {
		ODNEATGenome winner = pOne.getFitness() > pTwo.getFitness() ? pOne : pTwo;
		return winner;
	}

	//selection returns a copy of the chosen parents.
	public ODNEATGenome[] selectParents(Collection<ODNEATGenome> individuals) {
		
		ArrayList<ODNEATGenome> members = (ArrayList<ODNEATGenome>) individuals;
		ODNEATGenome[] parents;
		if(members.size() == 1){
			parents = new ODNEATGenome[1];
			parents[0] = (ODNEATGenome) members.get(0).copy();
			return parents;
		}
		else {
			parents = new ODNEATGenome[2];
			for(int i = 0; i < 2; i++){
				ODNEATGenome pOne = members.get(random.nextInt(members.size()));
				ODNEATGenome pTwo = members.get(random.nextInt(members.size()));
				parents[i] = (ODNEATGenome) this.performATournament(pOne, pTwo).copy();
			}
		}
		return parents;
	}
}