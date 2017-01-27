package commoninterface.evolution;

import java.io.Serializable;
import java.util.ArrayList;

public class OutputNeuronWithPriorities extends Neuron implements Serializable{

	protected ActivationFunction activationFunction;
	protected double activation = 0;
	protected final double PRIORITY_THRESHOLD = 0.0;

	public OutputNeuronWithPriorities(long innovationNumber,
			ActivationFunction function, double bias) {
		this.innovationNumber = innovationNumber;
		this.type = Neuron.OUTPUT_NEURON;
		this.activationFunction = function;
		this.incomingNeurons = new ArrayList<Neuron>();
		this.incomingSynapses = new ArrayList<Synapse>();
		this.activationFunction = function;
		this.BIAS = bias;
	}

	@Override
	public void step() {
		double currentActivation = 0;/*, subnetActivation = 0;
		double maxPriority = this.PRIORITY_THRESHOLD, actionValue = 0;*/
		for (int i = 0; i < this.incomingSynapses.size(); i++) {
			Synapse synapse = this.incomingSynapses.get(i);
			// check if it is a priority synapse
			/*if (synapse instanceof PrioritySynapse) {
				PrioritySynapse pSynapse = (PrioritySynapse) synapse;
				ProgrammedMacroNeuron macro = (ProgrammedMacroNeuron) this.incomingNeurons.get(i);
				double currentPriority = pSynapse.getWeight()
			 * macro.getPriorityValue(pSynapse.getInnovationNumber());
				i++;
				// the synapse that carries the action value
				ActionSynapse s = (ActionSynapse) this.incomingSynapses.get(i);
				double currentActionValue = macro.getActivationValue(s
						.getInnovationNumber()) * s.getWeight();
				// a behaviour with highest priority
				if (currentPriority > maxPriority) {
					// System.out.println("HIGHEST PRIORITY: " +
					// s.getMacroId());
					maxPriority = currentPriority;
					actionValue = currentActionValue;
				}
			} else {*/
			/*if(this.incomingNeurons.get(i) instanceof ANNEvolvedMacroNeuron){
					//System.out.println("=============================================");
					ANNEvolvedMacroNeuron neuron = (ANNEvolvedMacroNeuron) this.incomingNeurons.get(i);
					//subnetActivation = Math.max(synapse.getWeight() * neuron.getActivationValue(synapse.getInnovationNumber()),subnetActivation);
					//subnetActivation += synapse.getWeight() * neuron.getActivationValue(synapse.getInnovationNumber());
					//maxPriority = 0.1;
					//System.out.println("ASKING TO: " + neuron.getMacroId());
					currentActivation += synapse.getWeight() * neuron.getActivationValue(synapse.getInnovationNumber());
				}
				else {*/
			currentActivation += synapse.getWeight() * this.incomingNeurons.get(i).getActivationValue();
			//}
			//}
		}

		// execute the action specified by a behaviour.
		/*if (maxPriority > this.PRIORITY_THRESHOLD) {
			if(actionValue > 0)
				currentActivation = actionValue;
			else if(subnetActivation > 0)
				currentActivation = subnetActivation;
		}*/
		// System.out.println("no actv of behaviour");
		currentActivation += this.BIAS;
		double[] actValue = new double[] { currentActivation };
		activationFunction.activationFunction(actValue, 0, 1);
		this.activation = actValue[0];
	}

	@Override
	public double getActivationValue() {
		return activation;
	}

	@Override
	public void sortIncomingConnections() {
		/*ArrayList<Synapse> tempSynapses = new ArrayList<Synapse>(
				this.incomingSynapses.size());
		tempSynapses.addAll(incomingSynapses);
		this.incomingSynapses.clear();
		ArrayList<Neuron> tempNeurons = new ArrayList<Neuron>();
		tempNeurons.addAll(incomingNeurons);
		this.incomingNeurons.clear();

		ArrayList<PrioritySynapse> priorities = this.getPriorityLinks(tempSynapses);
		int numberOfPriorities = priorities.size();
		for (int i = 0; i < numberOfPriorities; i++) {
			String macroId = priorities.get(i).getMacroId();
			// add the priority synapse
			this.incomingSynapses.add(priorities.get(i));
			this.incomingNeurons.add(findNeuronWithId(priorities.get(i).getFromNeuron(), tempNeurons));
			// search for and add the corresponding action link.
			ActionSynapse action = this.getActionPriorityWithMacroId(macroId, tempSynapses);
			this.incomingSynapses.add(action);
			this.incomingNeurons.add(findNeuronWithId(action.getFromNeuron(), tempNeurons));
		}
		// now add all the others.
		for (int i = 0; i < tempSynapses.size(); i++) {
			Synapse s = tempSynapses.get(i);
			if (!(s instanceof ActionSynapse) && !(s instanceof PrioritySynapse)){
				this.incomingSynapses.add(s);
				this.incomingNeurons.add(findNeuronWithId(s.getFromNeuron(), tempNeurons));
			}
		}*/
	}

	/*private Neuron findNeuronWithId(long id, ArrayList<Neuron> neurons) {
		for(Neuron n : neurons){
			if(n.getInnovationNumber() == id){
				return n;
			}
		}

		return null;
	}*/

	/*protected ActionSynapse getActionPriorityWithMacroId(String macroId,
			ArrayList<Synapse> list) {
		for (Synapse s : list) {
			if (s instanceof ActionSynapse) {
				ActionSynapse action = (ActionSynapse) s;
				if (action.getMacroId().equalsIgnoreCase(macroId))
					return action;
			}
		}
		return null;
	}*/

	/*protected ArrayList<PrioritySynapse> getPriorityLinks(
			ArrayList<Synapse> list) {
		ArrayList<PrioritySynapse> priorities = new ArrayList<PrioritySynapse>();

		for (Synapse s : list) {
			if (s instanceof PrioritySynapse)
				priorities.add((PrioritySynapse) s);
		}
		return priorities;
	}*/

	@Override
	public void reset() {
		this.activation = 0;
	}

	@Override
	public double getBias() {
		return 0;
	}
}
