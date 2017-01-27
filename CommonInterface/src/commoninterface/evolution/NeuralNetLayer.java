package commoninterface.evolution;

import java.io.Serializable;
import java.util.ArrayList;

public class NeuralNetLayer implements Serializable{

	protected int id;
	protected ArrayList<Neuron> layerNeurons;
	
	public NeuralNetLayer(int id, ArrayList<Neuron> layerNeurons) {
		this.id = id;
		this.layerNeurons = layerNeurons;
	}
	
	public int getLayerId(){
		return this.id;
	}
	
	public void step(){
		for(Neuron n : layerNeurons){
			n.step();
		}
	}
	
	public double[] getNeuronActivations(){
		double[] values = new double[layerNeurons.size()];
		for(int i = 0; i < values.length; i++){
			values[i] = layerNeurons.get(i).getActivationValue();
		}
		return values;
	}

	public void reset() {
		for(Neuron n : this.layerNeurons)
			n.reset();
	}

	public ArrayList<Neuron> getNeurons() {
		return this.layerNeurons;
	}

	/*public double getActivationOfNeuron(long innovation) {
		for(Neuron n : this.layerNeurons)
			if(n.getInnovationNumber() == innovation)
				return n.getActivationValue();
		
		return -1;
	}*/

}
