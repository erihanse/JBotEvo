package commoninterface.evolution.odneat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import commoninterface.evolution.ODNEATLinkGene;

public class ODNEATMacroCoevolConnectionWeightMutator extends Mutator<ODNEATMacroNodeGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double pWeightReplaced;
	protected double pToggle;
	protected double perturbMagnitude;
	protected double connectionWeightRange;

	public ODNEATMacroCoevolConnectionWeightMutator(double pWeightReplaced, double pToggle, double perturbMagnitude, double weightRange){
		this.pWeightReplaced = pWeightReplaced;
		this.pToggle = pToggle;
		this.perturbMagnitude = perturbMagnitude;
		this.connectionWeightRange = weightRange;
	}

	@Override
	public void applyMutation(ODNEATMacroNodeGene macronode) {
		Collection<ODNEATLinkGene> links = new ArrayList<ODNEATLinkGene>();
		links.addAll(macronode.getTemplateInputs());
		links.addAll(macronode.getBehaviourGene().getConnectionsList());
		for(ODNEATLinkGene link : links){
			this.mutateLink(link);
		}
	}

	protected double generateRandomWeight() {
		return random.nextDouble() * connectionWeightRange * 2 - connectionWeightRange;
	}

	protected void mutateLink(ODNEATLinkGene linkGene){
		double perturbRandVal = random.nextDouble();
		double disableRandVal = random.nextDouble();
		double newWeight;

		//weight change
		if (perturbRandVal < this.mutationProb) {
			//weight replacement
			if (this.pWeightReplaced < random.nextDouble()) {
				newWeight = generateRandomWeight();
			} else {
				//modify weight
				//newWeight = linkGene.getWeight() * (1 + getWeightPerturbation());
				newWeight = linkGene.getWeight() + this.getWeightPerturbation();
				newWeight = clampWeight(newWeight);				
			}
			linkGene.setWeight(newWeight);
		}

		/*if (!linkGene.isEnabled() && disableRandVal < this.pToggle)
			linkGene.setEnabled(true);*/
	}

	protected double clampWeight(double newWeight) {
		if(newWeight < -this.connectionWeightRange)
			newWeight = -this.connectionWeightRange;
		else if(newWeight > this.connectionWeightRange)
			newWeight = this.connectionWeightRange;

		return newWeight;
	}

	private double getWeightPerturbation() {
		return random.nextDouble() * this.perturbMagnitude * 2 - this.perturbMagnitude;
	}
}
