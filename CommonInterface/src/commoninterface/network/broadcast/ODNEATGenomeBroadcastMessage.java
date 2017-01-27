package commoninterface.network.broadcast;

import java.util.ArrayList;

import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;
import commoninterface.evolution.StandardGenome;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.evolution.odneat.DoNotMoveBehaviourGene;
import commoninterface.evolution.odneat.EvolvedANNBehaviourGene;
import commoninterface.evolution.odneat.MacroBehaviourGene;
import commoninterface.evolution.odneat.MacroGenome;
import commoninterface.evolution.odneat.ODNEAT;
import commoninterface.evolution.odneat.ODNEATMacroNodeGene;
import commoninterface.evolution.odneat.ODNEATTemplateLinkGene;

public class ODNEATGenomeBroadcastMessage extends BroadcastMessage {

	public static final String IDENTIFIER = "GENOME";
	private final static int UPDATE_TIME = 1 * 1000; // 1 sec
	private ThymioCI thymio;

	public ODNEATGenomeBroadcastMessage(RobotCI robot) {
		super(UPDATE_TIME, IDENTIFIER);
		this.thymio = (ThymioCI) robot;
	}

	@Override
	public String getMessage() {
		ThymioOnlineEvoControllerCIBehaviour controller = (ThymioOnlineEvoControllerCIBehaviour) this.thymio.getActiveBehavior();
		if(controller == null)
			return null;
		ODNEAT odneat = controller.getODNEATInstance();
		if(odneat != null && odneat.willBroadcastGenome()){
			//System.out.println("will broadcast genome");
			return odneat.getActiveGenome().toString(MESSAGE_SEPARATOR);
		}
		return null;
	}

	@Override
	public String[] encode() {
		String[] messages = new String[1];

		String msg = getMessage();
		if(msg != null) {
			messages[0] = identifier+MESSAGE_SEPARATOR+msg;
		}

		if(messages[0] != null)
			return messages;

		return null;
	}

	public static void decode(String[] message, RobotCI robot) {
		try{
			if(message[1] == null)
				return;
			//		System.out.println("decoding...");
			ThymioCI receiver = (ThymioCI) robot;
			ThymioOnlineEvoControllerCIBehaviour controller = (ThymioOnlineEvoControllerCIBehaviour) receiver.getActiveBehavior();
			ODNEAT odneat = controller.getODNEATInstance();
			//skip identifier
			int index = 1;
			int links = Integer.valueOf(message[index++]);
			ArrayList<ODNEATLinkGene> linkGenes = new ArrayList<ODNEATLinkGene>();
			for(int i = 0; i < links; i++){
				int from = Integer.valueOf(message[index++]);
				int to = Integer.valueOf(message[index++]);
				double weight = Double.valueOf(message[index++]);
				long innovation = Long.valueOf(message[index++]);
				boolean enabled = Integer.valueOf(message[index++]) == 1;
				boolean selfRecurrent = Integer.valueOf(message[index++]) == 1;
				boolean recurrent = Integer.valueOf(message[index++]) == 1;
				ODNEATLinkGene link = new ODNEATLinkGene(innovation, enabled, from, to, weight);
				link.setRecurrent(recurrent);
				link.setSelfRecurrent(selfRecurrent);
				linkGenes.add(link);
			}

			int nodes = Integer.valueOf(message[index++]);
			ArrayList<ODNEATNodeGene> nodeGenes = new ArrayList<ODNEATNodeGene>();
			for(int i = 0; i < nodes; i++){
				long innovation = Long.valueOf(message[index++]);
				int type = Integer.valueOf(message[index++]);
				double bias = Double.valueOf(message[index++]);
				ODNEATNodeGene node = new ODNEATNodeGene(innovation, type);
				node.setBias(bias);

				nodeGenes.add(node);
			}

			String id = message[index++];
			double fitness = Double.valueOf(message[index++]);
			double adjustedFitness = Double.valueOf(message[index++]);
			double energyLevel = Double.valueOf(message[index++]);

			int speciesId = Integer.valueOf(message[index++]);
			int updatesCount = Integer.valueOf(message[index++]);
			String eaInstance = message[index++];
			int age = Integer.valueOf(message[index++]);
			double satime = Double.valueOf(message[index++]);

			ODNEATGenome g = new StandardGenome(id, linkGenes, nodeGenes);
			g.setFitness(fitness);
			g.setAdjustedFitness(adjustedFitness);
			g.setEnergyLevel(energyLevel);
			g.setUpdatesCount(updatesCount);
			g.setSpeciesId(speciesId);
			g.setEAInstance(eaInstance);
			g.setAge(age);
			g.setSAGene(satime);

			if(index == message.length)
				odneat.receiveSingleGenome(g);
			else {
				MacroGenome genome = new MacroGenome((StandardGenome) g);
				int macronodes = Integer.valueOf(message[index++]);
				for(int i = 0; i < macronodes; i++){
					//ends up not being used because the key in the inner map
					//in the innovation number of the macro neuron
					Long key = Long.valueOf(message[index++]);
					String macroName = String.valueOf(message[index++]);
					int macroId = Integer.valueOf(message[index++]);
					Long innovationNumber = Long.valueOf(message[index++]);
					int type = Integer.valueOf(message[index++]);

					int templateLinkGenes = Integer.valueOf(message[index++]);
					ArrayList<ODNEATTemplateLinkGene> templates = new ArrayList<ODNEATTemplateLinkGene>();
					for(int j = 0; j < templateLinkGenes; j++){
						long in = Long.valueOf(message[index++]);
						boolean enabled = Boolean.valueOf(message[index++]);
						long fromId = Long.valueOf(message[index++]);
						long toId = Long.valueOf(message[index++]);
						double weight = Double.valueOf(message[index++]);
						String m = String.valueOf(message[index++]);
						boolean isInput = Boolean.valueOf(message[index++]);
						ODNEATTemplateLinkGene l = new ODNEATTemplateLinkGene(in, enabled,
								fromId, toId, weight, m, isInput);
						templates.add(l);
					}
					String nodeType = String.valueOf(message[index++]);
					MacroBehaviourGene innernode = null;
					if(nodeType.equalsIgnoreCase("1")){
						long in = Long.valueOf(message[index++]);
						innernode = new DoNotMoveBehaviourGene(in);
					}
					else if(nodeType.equalsIgnoreCase("2")){
						String mid = String.valueOf(message[index++]);
						long in = Long.valueOf(message[index++]);
						boolean hasBias = Boolean.valueOf(message[index++]);
						long biasId = Long.valueOf(message[index++]);
						EvolvedANNBehaviourGene bg = new EvolvedANNBehaviourGene(mid, in);
						bg.setHasBias(hasBias);
						bg.setBiasId(biasId);

						int numLinks = Integer.valueOf(message[index++]);
						//ArrayList<ODNEATLinkGene> innerLinks = new ArrayList<ODNEATLinkGene>();
						for(int k = 0; k < numLinks; k++){
							int from = Integer.valueOf(message[index++]);
							int to = Integer.valueOf(message[index++]);
							double weight = Double.valueOf(message[index++]);
							long innovation = Long.valueOf(message[index++]);
							boolean enabled = Integer.valueOf(message[index++]) == 1;
							boolean selfRecurrent = Integer.valueOf(message[index++]) == 1;
							boolean recurrent = Integer.valueOf(message[index++]) == 1;
							ODNEATLinkGene link = new ODNEATLinkGene(innovation, enabled, from, to, weight);
							link.setRecurrent(recurrent);
							link.setSelfRecurrent(selfRecurrent);
							
							bg.insertSingleLinkGene(link);
						}

						int numNodes = Integer.valueOf(message[index++]);
						//ArrayList<ODNEATNodeGene> innerNodes = new ArrayList<ODNEATNodeGene>();
						for(int k = 0; k < numNodes; k++){
							long innovation = Long.valueOf(message[index++]);
							int t = Integer.valueOf(message[index++]);
							double bias = Double.valueOf(message[index++]);
							ODNEATNodeGene node = new ODNEATNodeGene(innovation, t);
							node.setBias(bias);

							bg.insertSingleNodeGene(node);
						}
						
						innernode = bg;
					}
					
					ODNEATMacroNodeGene m = new ODNEATMacroNodeGene(macroName, macroId,
							innovationNumber, type, innernode);
					for(ODNEATTemplateLinkGene template : templates){
						m.addTemplateInput(template);
					}
					genome.insertSingleNodeGene(m);
					
				}//end of individual macro node processing
				odneat.receiveSingleGenome(genome);
			}
		}
		catch(Exception e){
			return;
		}
	}

}
