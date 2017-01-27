package commoninterface.evolution.odneat;

import java.util.ArrayList;

import commoninterface.evolution.ODNEATLinkGene;

public class CompatibilityAux {

	public double[] computeCompatibilityScore(ArrayList<ODNEATLinkGene> current, ArrayList<ODNEATLinkGene> other, long commonRangeInnovation){
		double numDisjoint = 0, numExcess = 0, numMatched = 0, weightDifference = 0;

		ArrayList<ODNEATLinkGene> allLinks = new ArrayList<ODNEATLinkGene>();
		allLinks.addAll(current);
		allLinks.addAll(other);
		ArrayList<Long> innovationsProcessed = new ArrayList<Long>();
		for(ODNEATLinkGene link : allLinks){
			if(!innovationsProcessed.contains(link.getInnovationNumber())){
				innovationsProcessed.add(link.getInnovationNumber());
				//matching.
				if(current.contains(link) && other.contains(link)){
					//taking advantage of "equals".
					double weight1 = current.get(current.indexOf(link)).getWeight();
					double weight2 = other.get(other.indexOf(link)).getWeight();
					weightDifference += Math.abs(weight1 - weight2);
					numMatched++;
				}
				//either disjoint or excess
				else {
					if(link.getInnovationNumber() < commonRangeInnovation)
						numDisjoint++;
					else
						numExcess++;
				}
			}
		}
		
		return new double[]{numMatched, numExcess, numDisjoint, weightDifference};
	}
}
