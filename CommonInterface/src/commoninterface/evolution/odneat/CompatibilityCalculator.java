package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.ArrayList;

import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ODNEATLinkGene;


public class CompatibilityCalculator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double excessCoef, disjointCoef, weightCoef, compatibilityThreshold;


	protected MacroCompatibilityCalculator macroComp;

	public CompatibilityCalculator(double excessCoef, double disjointCoef, double weightCoef, double compThreshold){
		this.excessCoef = excessCoef;
		this.disjointCoef = disjointCoef;
		this.weightCoef = weightCoef;
		this.compatibilityThreshold = compThreshold;
		//use same parameters
		this.initialiseMacroCompatibilityCalculator(weightCoef,
				disjointCoef, excessCoef, weightCoef);
	}

	public void initialiseMacroCompatibilityCalculator(double macroProgrammedParameterCoef, double macroDisjointCoef, 
			double macroExcessCoef, double macroMatchingWeightCoef){
		this.macroComp = new MacroCompatibilityCalculator(macroProgrammedParameterCoef, 
				macroDisjointCoef, macroExcessCoef, macroMatchingWeightCoef);
	}

	public double getExcessCoeff(){
		return this.excessCoef;
	}

	public double getDisjointCoeff(){
		return this.disjointCoef;
	}

	public double getWeightCoeff(){
		return this.weightCoef;
	}

	public double getThreshold(){
		return this.compatibilityThreshold;
	}

	public double getCompatibilityThreshold(){
		return this.compatibilityThreshold;
	}

	public double computeCompatibilityScore(ODNEATGenome e2, ODNEATGenome e1){
		if(e1 instanceof MacroGenome){
			//System.out.println("macs");
			return this.compatibilityScoreWithMacroNeurons(e2, e1, excessCoef, disjointCoef, weightCoef);
		}
		return this.compatibilityScore(e2, e1, this.excessCoef, this.disjointCoef, this.weightCoef);
	}

	private double compatibilityScoreWithMacroNeurons(ODNEATGenome e2,
			ODNEATGenome e1, double excessCoef, double disjointCoef,
			double weightCoef) {
		double compatibilityScore = this.compatibilityScore(e2, e1, excessCoef, disjointCoef, weightCoef);
		MacroGenome g1 = (MacroGenome) e1;
		MacroGenome g2 = (MacroGenome) e2;
		compatibilityScore += this.macroComp.computeCompatibility(g1, g2);
		return compatibilityScore;
	}

	private double compatibilityScore(ODNEATGenome e2, 
			ODNEATGenome e1, double excessCoeff, 
			double disjointCoeff, double weightCoeff) {
		ArrayList<ODNEATLinkGene> current = (ArrayList<ODNEATLinkGene>) e1.getLinkGenes(true), 
				other = (ArrayList<ODNEATLinkGene>) e2.getLinkGenes(true);
		long commonRangeInnovation = Math.min(e1.getInnovationRange(), e2.getInnovationRange());

		double[] values = new CompatibilityAux().computeCompatibilityScore(current, other, commonRangeInnovation);
		double numMatched = values[0], numExcess = values[1], numDisjoint = values[2], weightDifference = values[3];
		//new double[]{numMatched, numExcess, numDisjoint, weightDifference};

		int longest = other.size() > current.size() ? other.size() : current.size();
		double score = (excessCoeff * numExcess / longest)
				+ (disjointCoeff * numDisjoint / longest);
		double weightDiff = (weightCoeff * weightDifference / numMatched);

		score += weightDiff;
		return score;
	}

	public double computeCompatibilityScore(ODNEATMacroNodeGene applicant,
			ODNEATMacroNodeGene representative) {
		//System.out.println("compat: " + this.macroComp == null);
		return this.macroComp.computeCompatibilityBetweenIndividualMacroNodeGenes(applicant, representative);
	}

	/*public double compatibilityScore(ODNEATGenome e2, 
			ODNEATGenome e1, double excessCoeff, 
			double disjointCoeff, double weightCoeff) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;
		ArrayList<ODNEATLinkGene> current = (ArrayList<ODNEATLinkGene>) e1.getLinkGenes(true);
		ArrayList<ODNEATLinkGene> other = (ArrayList<ODNEATLinkGene>) e2.getLinkGenes(true);

		int g1 = 0, g2 = 0;

		while(g1 < current.size() - 1 || g2 < current.size() - 1) {

			if (g1 == current.size() - 1) {
				g2++;
				numExcess++;
				continue;
			}

			if (g2 == other.size() - 1) {
				g1++;
				numExcess++;
				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = ((ODNEATLinkGene) current.get(g1)).getInnovationNumber();
			final long id2 = ((ODNEATLinkGene) other.get(g2)).getInnovationNumber();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math.abs(((ODNEATLinkGene) current.get(g1)).getWeight()
						- ((ODNEATLinkGene) other.get(g2)).getWeight());
			}

			// innovation numbers are different so increment the disjoint score
			if (id1 < id2) {
				numDisjoint++;
				g1++;
			}

			if (id1 > id2) {
				++numDisjoint;
				++g2;
			}

		}

		int longest = other.size() > current.size() ? other.size() : current.size();

		double score = (excessCoeff * numExcess / longest)
				+ (disjointCoeff * numDisjoint / longest);

		//double score = excessCoeff * numExcess + disjointCoeff * numDisjoint;

		double weightDiff = (weightCoeff * weightDifference / numMatched);

		score += weightDiff;
		return score;
	}*/


	/*public double[] getCompatibilityComponents(ODNEATGenome e1,
			ODNEATGenome e2) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;
		ArrayList<ODNEATLinkGene> current = (ArrayList<ODNEATLinkGene>) e1.getLinkGenes(false);
		ArrayList<ODNEATLinkGene> other = (ArrayList<ODNEATLinkGene>) e2.getLinkGenes(false);

		int g1 = 0, g2 = 0;

		while(g1 < current.size() - 1 || g2 < current.size() - 1) {

			if (g1 == current.size() - 1) {
				g2++;
				numExcess++;
				continue;
			}

			if (g2 == other.size() - 1) {
				g1++;
				numExcess++;
				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = ((ODNEATLinkGene) current.get(g1)).getInnovationNumber();
			final long id2 = ((ODNEATLinkGene) other.get(g2)).getInnovationNumber();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math.abs(((ODNEATLinkGene) current.get(g1)).getWeight()
						- ((ODNEATLinkGene) other.get(g2)).getWeight());
			}

			// innovation numbers are different so increment the disjoint score
			if (id1 < id2) {
				numDisjoint++;
				g1++;
			}

			if (id1 > id2) {
				++numDisjoint;
				++g2;
			}

		}		
		return new double[]{numMatched, numExcess + numDisjoint, weightDifference/numMatched};		
	}*/
}
