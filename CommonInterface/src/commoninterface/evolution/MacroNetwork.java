package commoninterface.evolution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import commoninterface.evolution.odneat.ODNEATMacroNodeGene;
import commoninterface.neuralnetwork.CINeuralNetwork;
import commoninterface.neuralnetwork.inputs.CINNInput;
import commoninterface.neuralnetwork.outputs.CINNOutput;
import commoninterface.utils.CIArguments;

/**
 * a network with macro-neurons
 * @author fernando
 *
 */

public class MacroNetwork extends CINeuralNetwork implements Serializable{

	private static final long serialVersionUID = 1L;
	private NeuralNetLayer[] layers;
	//macro neuron to list of neurons that compose it
	private HashMap<Long, ArrayList<Neuron>> macroMap = new HashMap<Long, ArrayList<Neuron>>();
	
	//TODO: improve
	//(macro-neurons composing) neuron to id to previous activation value
	private HashMap<Long, Double> previousActivations = new HashMap<Long, Double>();	
	private double[] previousOutputs = new double[2];
	
	//macro-neuron to variations (of the macro-neuron, of the net)
	private HashMap<Long,ODNEATMacroNodeGene> idToGene = new HashMap<Long,ODNEATMacroNodeGene>();
	
	public MacroNetwork(Vector<CINNInput> inputs, Vector<CINNOutput> outputs, CIArguments arguments){
		this.create(inputs, outputs);
	}

	public MacroNetwork(NeuralNetLayer[] layers) {
		this.layers = layers;
	}

	@Override
	public void create(Vector<CINNInput> inputs, Vector<CINNOutput> outputs) {
		super.create(inputs, outputs);
	}

	@Override
	protected double[] propagateInputs(double[] inputValues) {
		return this.processInputs(inputValues);
	}

	public double[] processInputs(double[] input) {
		//System.out.println("I: " + Arrays.toString(input));
		ArrayList<Neuron> inputNeurons = layers[0].getNeurons();
		for(int i = 0; i < input.length; i++){
			((StandardNeuron) inputNeurons.get(i)).setActivationValue(input[i]);
		}
		for(NeuralNetLayer layer : layers)
			layer.step();

		//we already have the output values
		double[] outputValues = layers[layers.length - 1].getNeuronActivations();
		double outputVariation = (Math.abs(outputValues[0] - previousOutputs[0]) + Math.abs(outputValues[1] - previousOutputs[1]))/2.0;
		for(Long macroNeuronId : macroMap.keySet()){
			ArrayList<Neuron> innerNeurons = macroMap.get(macroNeuronId);
			double variation = 0.0;
			for(Neuron inner : innerNeurons){
				double currentActivation = inner.getActivationValue();
				double magnitudeChange = Math.abs(currentActivation - previousActivations.get(inner.getInnovationNumber()));
				variation += magnitudeChange;
			}
			variation /= innerNeurons.size();
			
			idToGene.get(macroNeuronId).registerVariation(new double[]{variation,outputVariation});
		}
		previousOutputs = outputValues.clone();
		return outputValues;
	}



	@Override
	public void reset() {
		for(NeuralNetLayer layer : layers){
			layer.reset();
		}
	}


	public void controlStep(double time) {
		super.controlStep(time);
	}

	public void updateStructure(MacroNetwork newStructure) {
		this.layers = newStructure.layers;
		
		//maps
		this.macroMap = newStructure.macroMap;
		this.previousActivations = newStructure.previousActivations;
		this.previousOutputs = newStructure.previousOutputs;
		this.idToGene = newStructure.idToGene;
	}
	
	public HashMap<Long, Neuron> getNeuronMap(){
		HashMap<Long,Neuron> map = new HashMap<Long,Neuron>();
		for(NeuralNetLayer l : this.layers){
			ArrayList<Neuron> neurons = l.getNeurons();
			for(Neuron n : neurons){
				map.put(n.getInnovationNumber(), n);
			}
		}
		
		return map;
	}

	public int getTotalNumberOfNeurons() {
		int count = 0;
		for(NeuralNetLayer l : this.layers){
			ArrayList<Neuron> neurons = l.getNeurons();
			count += neurons.size();
		}
		return count;
	}

	public int getTotalNumberOfConnections() {
		ArrayList<Long> connectionIds = new ArrayList<Long>();
		for(NeuralNetLayer l : this.layers){
			ArrayList<Neuron> neurons = l.getNeurons();
			for(Neuron n : neurons){
				ArrayList<Synapse> connections = n.getIncomingConnections();
				for(Synapse s : connections){
					if(!connectionIds.contains(s.getInnovationNumber()))
						connectionIds.add(s.getInnovationNumber());
				}
			}
		}
		return connectionIds.size();
	}

	public double[] getInputReadings() {
		return this.inputNeuronStates.clone();
	}

	public double[] getOutputReadings() {
		return this.getOutputNeuronStates().clone();
	}

	public ArrayList<Synapse> getAllLinks() {
		ArrayList<Synapse> links = new ArrayList<Synapse>();
		for(int i = 0; i < this.layers.length; i++){
			NeuralNetLayer layer = layers[i];
			for(Neuron n : layer.getNeurons()){
				ArrayList<Synapse> incoming = n.getIncomingConnections();
				for(Synapse s : incoming){
					if(!links.contains(s)){
						links.add(s);
					}
				}
			}
		}
		return links;
	}

	public void registerMacroNeuron(Long macroId,
			ArrayList<Long> macroOutputs, ODNEATMacroNodeGene macroGene) {
		ArrayList<Neuron> macroOut = new ArrayList<Neuron>();
		HashMap<Long, Neuron> neuronMap = this.getNeuronMap();
		for(Long l : macroOutputs){
			macroOut.add(neuronMap.get(l));
			previousActivations.put(neuronMap.get(l).getInnovationNumber(),0.0);
		}
		this.idToGene.put(macroId, macroGene);

		
		this.macroMap.put(macroId, macroOut);
	}

}
