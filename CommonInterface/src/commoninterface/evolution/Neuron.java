package commoninterface.evolution;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Neuron implements Comparable<Neuron>, Serializable {

	protected long innovationNumber;
	protected ArrayList<Neuron> incomingNeurons;
	protected ArrayList<Synapse> incomingSynapses;
	protected int depth = -1;
	protected int type;
	protected double BIAS = 1.0;

	public static final int INPUT_NEURON = 1;
	public static final int HIDDEN_NEURON = 3;
	public static final int OUTPUT_NEURON = 2;
	//public static final int ARBITRATOR_MACRO_NEURON = 4;
	//public static final int PROGRAMMED_MACRO_NEURON = 5;
	/*public static final int MACRO_NEURON = 4;
	public static final int EVOLVED_MACRO_NEURON = 5;
	public static final int TEACHER_EXECUTOR_MACRO_NEURON = 6;
	public static final int TEACHER_ONLY_MACRO_NEURON = 7;*/

	public abstract void step();
	public abstract double getActivationValue();
	public abstract void reset();
	public abstract double getBias();

	public int getType(){
		return type;
	}

	public void setNeuronDepth(int depth){
		this.depth = depth;
	}

	public int getNeuronDepth(){
		return this.depth;
	}

	public void addIncomingNeuron(Neuron n){
		this.incomingNeurons.add(n);
	}

	public void addIncomingSynapse(Synapse s){
		this.incomingSynapses.add(s);
	}

	public long getInnovationNumber() {
		return this.innovationNumber;
	}

	public abstract void sortIncomingConnections();

	public ArrayList<Neuron> getIncomingNeurons() {
		return this.incomingNeurons;
	}

	public int compareTo(Neuron o) {
		return Long.compare(this.getInnovationNumber(), o.getInnovationNumber());
	}

	public ArrayList<Synapse> getIncomingConnections() {
		return this.incomingSynapses;
	}

	public Synapse getIncomingConnection(Neuron from) {
		for(Synapse s : this.incomingSynapses){
			if(s.getFromNeuron() == from.getInnovationNumber()){
				return s;
			}
		}
		return null;
	}
	
	public boolean containsIncomingSynapse(long fromNeuron, long toNeuron) {
		for(Synapse s : this.incomingSynapses){
			if(s.getFromNeuron() == fromNeuron && s.getToNeuron() == toNeuron)
				return true;
		}
		return false;
	}
	
	public boolean containsIncomingNeuron(long fromNeuron) {
		for(Neuron n : this.incomingNeurons){
			if(n.getInnovationNumber() == fromNeuron){
				return true;
			}
		}
		return false;
	}
}
