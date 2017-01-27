package commoninterface.evolution;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import commoninterface.evolution.odneat.EvolvedANNBehaviourGene;
import commoninterface.evolution.odneat.MacroGenome;
import commoninterface.evolution.odneat.ODNEATActionLinkGene;
import commoninterface.evolution.odneat.ODNEATMacroNodeGene;
import commoninterface.evolution.odneat.ODNEATPriorityLinkGene;
import commoninterface.evolution.odneat.ODNEATTemplateLinkGene;
import commoninterface.evolution.odneat.PreprogrammedGene;
import commoninterface.utils.CIArguments;


public class ExtendedMacroANNCodec extends GPMapping<MacroGenome, MacroNetwork> {

	private double weightRange;
	protected final ActivationFunction function = new ActivationSteepenedSigmoid();

	public ExtendedMacroANNCodec(double weightRange) {
		super(null);

		this.weightRange = weightRange;
	}



	@Override
	public MacroNetwork decode(MacroGenome genome) {
		//first, the enabled links maintained at genome level.
		ArrayList<ODNEATLinkGene> links = new ArrayList<ODNEATLinkGene>();
		links.addAll(genome.getLinkGenes(true));
		//now loop through the macro-neurons to get the other links
		HashMap<Long, ODNEATMacroNodeGene> macros = genome.getMacroNodeGenes();

		for(Long l : macros.keySet()){
			ODNEATMacroNodeGene node = macros.get(l);
			links.addAll(node.getTemplateInputs());
			links.addAll(node.getBehaviourGene().getConnectionsList());
		}
		//create synapses objects
		ArrayList<Synapse> synapses = this.createSynapses(links);
		//now get all (non-decomposable) nodes
		ArrayList<ODNEATNodeGene> nodes = getNodes(genome);
		ArrayList<Neuron> neurons = this.createNeurons(nodes);

		HashMap<Long, Neuron> idToNeuron = createNeuronsMap(neurons);
		//assign connections to neurons
		this.assignConnectionsToNeurons(synapses, idToNeuron);
		//assign neuron depth
		this.assignNeuronDepth(this.getOutputNeurons(neurons), 0);

		ArrayList<Integer> layerIds = getLayersIds(neurons);
		//sort into an ascending order
		Collections.sort(layerIds);

		NeuralNetLayer[] layers = computeLayers(neurons, layerIds);

		MacroNetwork net = new MacroNetwork(layers);

		for(Long l : macros.keySet()){
			net.registerMacroNeuron(l, macros.get(l).getBehaviourGene().getOrderedListOutputNodeGenes(), macros.get(l));
		}

		return net;
	}



	public NeuralNetLayer[] computeLayers(ArrayList<Neuron> neurons, ArrayList<Integer> layerIds){
		NeuralNetLayer[] layers = new NeuralNetLayer[layerIds.size()];
		for(int i = 0; i < layerIds.size(); i++){
			int idIndex = layerIds.size() - 1 - i;
			int currentLayerId = layerIds.get(idIndex);
			ArrayList<Neuron> layerNeurons = getNeuronsWithDepth(currentLayerId, neurons);
			Collections.sort(layerNeurons);
			layers[i] = new NeuralNetLayer(currentLayerId, layerNeurons);
		}
		return layers;
	}

	protected ArrayList<Neuron> getNeuronsWithDepth(int depth, ArrayList<Neuron> allNeurons) {
		ArrayList<Neuron> layerNeurons = new ArrayList<Neuron>();
		for(Neuron n : allNeurons){
			if(n.getNeuronDepth() == depth)
				layerNeurons.add(n);
		}
		return layerNeurons;
	}


	public ArrayList<Integer> getLayersIds(ArrayList<Neuron> neurons) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(Neuron n : neurons){
			int depth = n.getNeuronDepth();
			//because of the macro-neurons "remove structure" operator, some neurons may not have output connections
			//if the neuron does not have input connections, assign them depth max_value (input depth) -1 , otherwise
			if(depth == -1){
				if(n.getType() == Neuron.INPUT_NEURON){
					depth = Integer.MAX_VALUE;
				}
				/*try {
					throw new Exception("invalid depth for " + n.getInnovationNumber() + "; " + n.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				else if(n.getIncomingConnections().isEmpty()){
					depth = Integer.MAX_VALUE - 1;
				}
				else {
					//max depth of the input connections - 1
					for(Neuron input : n.getIncomingNeurons()){
						depth = Math.max(depth, input.getNeuronDepth());
					}
				}
				n.setNeuronDepth(depth);
			}
			if(!ids.contains(depth))
				ids.add(depth);
		}
		return ids;
	}


	public ArrayList<Neuron> getOutputNeurons(ArrayList<Neuron> neurons) {
		ArrayList<Neuron> outputNeurons = new ArrayList<Neuron>();
		for(Neuron n : neurons){
			if(n.getType() == Neuron.OUTPUT_NEURON){
				outputNeurons.add(n);
			}
		}
		return outputNeurons;
	}



	public void assignNeuronDepth(ArrayList<Neuron> neurons, int depth) {

		for (int i = 0; i < neurons.size(); i++) {
			Neuron neuron = neurons.get(i);
			if (neuron.getType() == Neuron.OUTPUT_NEURON) {
				if (neuron.getNeuronDepth() == -1) {
					neuron.setNeuronDepth(0);
					this.assignNeuronDepth(neuron.getIncomingNeurons(), depth + 1);
				}
			} else if (neuron.getType() == Neuron.HIDDEN_NEURON ){
				//					|| neuron.getType() == Neuron.PROGRAMMED_MACRO_NEURON){
				if (neuron.getNeuronDepth() == -1) {
					neuron.setNeuronDepth(depth);
					this.assignNeuronDepth(neuron.getIncomingNeurons(), depth + 1);				
				}
			} else if (neuron.getType() == Neuron.INPUT_NEURON) {
				neuron.setNeuronDepth(Integer.MAX_VALUE);
			}
		}
	}

	private HashMap<Long, Neuron> createNeuronsMap(ArrayList<Neuron> neurons) {
		HashMap<Long,Neuron> map = new HashMap<Long, Neuron>();

		for(Neuron n : neurons){
			map.put(n.getInnovationNumber(), n);
		}

		return map;
	}


	private void assignConnectionsToNeurons(ArrayList<Synapse> synapses,
			HashMap<Long, Neuron> idToNeuron) {
		for(Synapse s : synapses){

			long to = s.getToNeuron();
			Neuron toNeuron = idToNeuron.get(to);

			if(!toNeuron.containsIncomingSynapse(s.getFromNeuron(), to)){
				toNeuron.addIncomingSynapse(s);
				Neuron n = idToNeuron.get(s.getFromNeuron());

				toNeuron.addIncomingNeuron(n);
			}
		}

		for(long key : idToNeuron.keySet()){
			idToNeuron.get(key).sortIncomingConnections();
		}
	}


	/**
	 * note: ann-based evolved macro-neurons are no longer present because they have already been unfolded,
	 * as well as the hierarchical macro-neuron.
	 * So: standard neurons, arbitrator neurons, and preprogrammed neurons.
	 */
	private ArrayList<Neuron> createNeurons(ArrayList<ODNEATNodeGene> nodes) {
		ArrayList<Neuron> standardNeurons = new ArrayList<Neuron>();
		for(ODNEATNodeGene node : nodes){
			Neuron n = null;
			switch(node.getType()){
			case ODNEATNodeGene.INPUT:
				//no bias in the input neurons.
				n = new StandardNeuron(node.getInnovationNumber(), Neuron.INPUT_NEURON, null, 0.0);
				break;
			case ODNEATNodeGene.HIDDEN:
				n = new StandardNeuron(node.getInnovationNumber(), Neuron.HIDDEN_NEURON, this.function.clone(), node.getBias());
				break;
				//for simplicity, all output neurons are created as output neurons with priorities
			case ODNEATNodeGene.OUTPUT:
				n = new OutputNeuronWithPriorities(node.getInnovationNumber(), this.function.clone(), node.getBias());
				break;
				/*case ODNEATNodeGene.ARBITRATOR_MACRO_NODE:
				ArbitratorMacroNodeGene arb = (ArbitratorMacroNodeGene) node;
				n = new ArbitratorMacroNeuron(arb.getInnovationNumber(), arb.getFunction());
				break;*/
			default:
				//has to be preprogrammed.
				//ODNEATMacroNodeGene preprogrammedNode = (ODNEATMacroNodeGene) node;
				//n = new BehaviourDecoder().decodePreprogrammed(preprogrammedNode, preprogrammedNode.getBehaviourGene());
				break;
			}
			standardNeurons.add(n);
		}
		return standardNeurons;
	}


	private ArrayList<ODNEATNodeGene> getNodes(MacroGenome genome) {
		ArrayList<ODNEATNodeGene> nodes = new ArrayList<ODNEATNodeGene>();
		//first loop through the macro-neurons
		HashMap<Long, ODNEATMacroNodeGene> macros = genome.getMacroNodeGenes();
		for(long key : macros.keySet()){
			ODNEATMacroNodeGene macro = macros.get(key);
			//preprogrammed macro-node, just add the node
			if(macro.getBehaviourGene() instanceof PreprogrammedGene){
				decodePreprogrammedGene(nodes,  macro);
			}
			else if(macro.getBehaviourGene() instanceof EvolvedANNBehaviourGene){
				decodeEvolvedANNGene(nodes, (EvolvedANNBehaviourGene) macro.getBehaviourGene());
			}
			/*else if(macro.getBehaviourGene() instanceof HierarchicalMacroBehaviourGene){
				HierarchicalMacroBehaviourGene hierarchicalNode = (HierarchicalMacroBehaviourGene) macro.getBehaviourGene();
				decodeHierarchicalGene(nodes, hierarchicalNode);
			}*/
		}
		//now add the standard neurons
		nodes.addAll(genome.getStandardNodeGenes());

		return nodes;
	}


	/*private void decodeHierarchicalGene(ArrayList<ODNEATNodeGene> nodes,
			HierarchicalMacroBehaviourGene hierarchicalNode) {
		for(MacroTreeNode node : hierarchicalNode.getTree().getAllNodes()){
			ODNEATMacroNodeGene macro = node.getNodeMacroGene();
			if(macro instanceof ArbitratorMacroNodeGene){
				nodes.add(node.getNodeMacroGene());
			}
			else if(macro.getBehaviourGene() instanceof PreprogrammedGene){
				decodePreprogrammedGene(nodes, macro);
			}
			else if(macro.getBehaviourGene() instanceof EvolvedANNBehaviourGene){
				decodeEvolvedANNGene(nodes, (EvolvedANNBehaviourGene) macro.getBehaviourGene());
			}
			else {
				try {
					throw new Exception("Invalid type");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}*/


	private void decodeEvolvedANNGene(ArrayList<ODNEATNodeGene> nodes,
			EvolvedANNBehaviourGene evolvedANN) {
		ArrayList<ODNEATNodeGene> annNodes = evolvedANN.getNodesList();
		//little trick to make the decoding easier.
		for(ODNEATNodeGene node : annNodes){
			ODNEATNodeGene copy = node.copy();
			if(copy.getType() == ODNEATNodeGene.INPUT || copy.getType() == ODNEATNodeGene.OUTPUT){
				copy.setType(ODNEATNodeGene.HIDDEN);
			}
			nodes.add(copy);
		}
	}


	private void decodePreprogrammedGene(ArrayList<ODNEATNodeGene> nodes, ODNEATMacroNodeGene macro) {
		nodes.add(macro);
	}


	public ArrayList<Synapse> createSynapses(ArrayList<ODNEATLinkGene> links) {
		ArrayList<Synapse> synapses = new ArrayList<Synapse>(links.size());
		Synapse s = null;
		for(ODNEATLinkGene link : links){
			if(link instanceof ODNEATPriorityLinkGene){
				ODNEATPriorityLinkGene pLink = (ODNEATPriorityLinkGene) link;
				s = new PrioritySynapse(pLink.getInnovationNumber(), pLink.getWeight(), 
						pLink.getFromId(), pLink.getToId(), pLink.getMacroId());
			}
			else if(link instanceof ODNEATActionLinkGene){
				ODNEATActionLinkGene aLink = (ODNEATActionLinkGene) link;
				s = new ActionSynapse(aLink.getInnovationNumber(), aLink.getWeight(), 
						aLink.getFromId(), aLink.getToId(), aLink.getMacroId());
			}
			else if(link instanceof ODNEATTemplateLinkGene){
				ODNEATTemplateLinkGene tLink = (ODNEATTemplateLinkGene) link;
				s = new TemplateSynapse(link.getInnovationNumber(), link.getWeight(), link.getFromId(), link.getToId(), tLink.getMacroId(), tLink.isInput());
			}
			//regular odneat link gene
			else {
				s = new Synapse(link.getInnovationNumber(), link.getWeight(), link.getFromId(), link.getToId());
			}

			//ensure that the connection weights are within the predefined range.
			/*if(s.getWeight() > this.maxConnectionWeight){
				s.setWeight(maxConnectionWeight);
			}
			else if(s.getWeight() < this.minConnectionWeight){
				s.setWeight(minConnectionWeight);
			}*/

			synapses.add(s);
		}
		return synapses;
	}

	@Override
	public MacroGenome encode(MacroNetwork p) {
		try {
			throw new Exception("Encoding of a network is not supported by " + this.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}