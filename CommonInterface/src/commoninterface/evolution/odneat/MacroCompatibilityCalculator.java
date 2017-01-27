package commoninterface.evolution.odneat;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;

import commoninterface.evolution.ODNEATLinkGene;

public class MacroCompatibilityCalculator implements Serializable{

	//coefficient for matching programmed macro neurons (with parameters).
	private double macroParameterCoef;
	//evolved disjoint genes, evolved excess genes, and weight coef of matching genes.
	private double macroDisjointCoeff, macroExcessCoef, macroWeightCoef;

	public MacroCompatibilityCalculator(double macroProgrammedParameterCoef,
			double macroDisjointCoeff, double macroExcessCoef,
			double macroWeightCoef) {
		this.macroParameterCoef = macroProgrammedParameterCoef;
		this.macroDisjointCoeff = macroDisjointCoeff;
		this.macroExcessCoef = macroExcessCoef;
		this.macroWeightCoef = macroWeightCoef;
	}

	/**
	 * 
	 */
	public double computeCompatibility(MacroGenome g1, MacroGenome g2) {
		//innovation number to macMacroNeuron	
		HashMap<Long, ODNEATMacroNodeGene> mn1 = g1.getMacroNodeGenes();
		HashMap<Long, ODNEATMacroNodeGene> mn2 = g2.getMacroNodeGenes();
		
		//first, compute the average parameter difference between matching _preprogrammed macro-neurons_
		double compatScore = 0, averageParameterDiff = 0;
		int compatPreprogrammedMacroNeurons = 0;
		for(Long macroId : mn1.keySet()){
			ODNEATMacroNodeGene macro1 = mn1.get(macroId);
			ODNEATMacroNodeGene macro2 = mn2.get(macroId);
			if(macro2 != null 
					&& macro2.getBehaviourGene() instanceof PreprogrammedGene 
					&& macro1.getBehaviourGene() instanceof PreprogrammedGene){
				if(macro1.getBehaviourGene().hasParameters()){
				compatPreprogrammedMacroNeurons++;
				averageParameterDiff += computeAverageParameterDifference(macro1.getBehaviourGene().getParameters(), 
						macro2.getBehaviourGene().getParameters());
				}
			}
		}
		
		if(compatPreprogrammedMacroNeurons > 0){
			averageParameterDiff /= compatPreprogrammedMacroNeurons;
			compatScore += averageParameterDiff * this.macroParameterCoef;
		}
		
		//compatibility as in odneat, i.e., based on matching, disjoint, and excess innovation number.
		compatScore += computeCompatibilityBetweenMacroNeurons(mn1, mn2);
		
		return compatScore;
	}


	private double computeCompatibilityBetweenMacroNeurons(
			HashMap<Long, ODNEATMacroNodeGene> mn1,
			HashMap<Long, ODNEATMacroNodeGene> mn2) {
		ArrayList<ODNEATLinkGene> current = new ArrayList<ODNEATLinkGene>(),
				other = new ArrayList<ODNEATLinkGene>();
		//for macro-neurons viewed as ONE sub-network
		//get all enabled links
		/*getAllEnabledLinks(mn1, current);
		getAllEnabledLinks(mn2, other);
		//start the computation
		long commonInnovationRange = Math.min(computeInnovationRange(current), computeInnovationRange(other));
		double[] values = new CompatibilityAux().computeCompatibilityScore(current, other, commonInnovationRange);
		double numMatched = values[0], numExcess = values[1], numDisjoint = values[2], weightDifference = values[3];
		//new double[]{numMatched, numExcess, numDisjoint, weightDifference};
		
		int longest = other.size() > current.size() ? other.size() : current.size();
		double score = (this.macroExcessCoef * numExcess / longest)
				+ (this.macroDisjointCoeff * numDisjoint / longest);
		double weightDiff = (this.macroWeightCoef * weightDifference / numMatched);
	
		score += weightDiff;*/
		
		double combinedScore = 0;
		//combined score
		for(Long key : mn1.keySet()){
			ODNEATMacroNodeGene m1 = mn1.get(key), m2 = mn2.get(key);
			//get all enabled links
			getAllEnabledLinks(m1, current);
			getAllEnabledLinks(m2, other);
			//start the computation
			long commonInnovationRange = Math.min(computeInnovationRange(current), computeInnovationRange(other));
			double[] values = new CompatibilityAux().computeCompatibilityScore(current, other, commonInnovationRange);
			double numMatched = values[0], numExcess = values[1], numDisjoint = values[2], weightDifference = values[3];
			//new double[]{numMatched, numExcess, numDisjoint, weightDifference};
			
			int longest = other.size() > current.size() ? other.size() : current.size();
			double score = (this.macroExcessCoef * numExcess / longest)
					+ (this.macroDisjointCoeff * numDisjoint / longest);
			double weightDiff = (this.macroWeightCoef * weightDifference / numMatched);
		
			score += weightDiff;
			
			combinedScore += score;
		}
		return combinedScore;
	}

	/*private void getAllEnabledLinks(HashMap<Long, ODNEATMacroNodeGene> mn1,
			ArrayList<ODNEATLinkGene> current) {
		for(ODNEATMacroNodeGene gene : mn1.values()){
			current.addAll(gene.getTemplateInputs());
			current.addAll(gene.getBehaviourGene().getConnectionsList());
		}
	}*/

	private void getAllEnabledLinks(ODNEATMacroNodeGene gene,
			ArrayList<ODNEATLinkGene> current) {
		current.addAll(gene.getTemplateInputs());
		current.addAll(gene.getBehaviourGene().getConnectionsList());
	}

	private long computeInnovationRange(ArrayList<ODNEATLinkGene> list) {
		long maxRange = Long.MIN_VALUE;
		for(ODNEATLinkGene link : list){
			if(link.isEnabled()){
				maxRange = Math.max(link.getInnovationNumber(), maxRange);
			}
		}
		return maxRange;
	}

	private double computeAverageParameterDifference(
			HashMap<String, Double> p1, HashMap<String, Double> p2) {
		int parametersCounted = 0;	
		double diff = 0;
		for(String key : p1.keySet()){
			if(p2.containsKey(key)){
				double v1 = p1.get(key), v2 = p2.get(key);
				diff += Math.abs(v1 - v2);
				parametersCounted++;
			}
		}
		
		diff /= parametersCounted;
		
		return diff;
	}

	public double computeCompatibilityBetweenIndividualMacroNodeGenes(
			ODNEATMacroNodeGene m1, ODNEATMacroNodeGene m2) {
		ArrayList<ODNEATLinkGene> current = new ArrayList<ODNEATLinkGene>(),
				other = new ArrayList<ODNEATLinkGene>();
		getAllEnabledLinks(m1, current);
		getAllEnabledLinks(m2, other);
		//start the computation
		long commonInnovationRange = Math.min(computeInnovationRange(current), computeInnovationRange(other));
		double[] values = new CompatibilityAux().computeCompatibilityScore(current, other, commonInnovationRange);
		double numMatched = values[0], numExcess = values[1], numDisjoint = values[2], weightDifference = values[3];
		//new double[]{numMatched, numExcess, numDisjoint, weightDifference};
		
		int longest = other.size() > current.size() ? other.size() : current.size();
		double score = (this.macroExcessCoef * numExcess / longest)
				+ (this.macroDisjointCoeff * numDisjoint / longest);
		double weightDiff = (this.macroWeightCoef * weightDifference / numMatched);
	
		score += weightDiff;
		
		return score;
	}


}
