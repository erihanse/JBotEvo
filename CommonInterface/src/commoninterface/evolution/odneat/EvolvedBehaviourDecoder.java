package commoninterface.evolution.odneat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import commoninterface.evolution.ODNEATGene;
import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;
import commoninterface.evolution.SerialisationHelper;

public class EvolvedBehaviourDecoder {

	protected final static String EVOLVED_FILE_EXTENSION = ".evo";
	protected final static String REPOSITORY_EVOLVED_BEHAVIOURS = "repository_evolved_behaviours/";
	protected final static String separator = ";";

	public static MacroBehaviourGene loadEvolvedBehaviour(
			final String behaviourToLoad, long macroInnovation, double disableConnectionProb){

		try {
			File behaviour = getBehaviourFile(behaviourToLoad);
			BufferedReader reader = new BufferedReader(new FileReader(behaviour));
			return loadGraphBasedANNGenome(reader, macroInnovation, behaviourToLoad, disableConnectionProb);
			
			/*String[] instance = reader.readLine().split(separator);

			switch(instance[1]){
			case "ANN":
			default:
				reader.close();
				return null;
			}*/	
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	protected static MacroBehaviourGene loadGraphBasedANNGenome(BufferedReader reader, 
			long macroInnovation, String macroId, double disableConnectionProb) throws IOException {
		EvolvedANNBehaviourGene gene = new EvolvedANNBehaviourGene(macroId, macroInnovation);
		String all = "", line = null;
		while((line = reader.readLine()) != null){
			all += new String(line);
		}
		reader.close();
		ArrayList<ODNEATGene> loaded = SerialisationHelper.deserialize(all);
		int links = 0, nodes = 0;
		for(ODNEATGene g : loaded){
			if(g instanceof ODNEATLinkGene){
				links++;
				ODNEATLinkGene link = (ODNEATLinkGene) g;
				gene.insertSingleLinkGene(link);
			}
			else if(g instanceof ODNEATNodeGene){
				nodes++;
				ODNEATNodeGene node = (ODNEATNodeGene) g;
				gene.insertSingleNodeGene(node);
			}
		}
		System.out.println("EBD TOTAL PARAMS (at loading), LINKS: " + links + ", NODES: " + nodes);
		//previous implementation (requires different file format)
		/*String[] biasInfo = reader.readLine().split(separator), inputsInfo = reader.readLine().split(separator),
				outputsInfo = reader.readLine().split(separator);
		boolean hasBias = biasInfo[1].equalsIgnoreCase("true");
		long biasId = Long.valueOf(biasInfo[2]);
		ArrayList<ODNEATLinkGene> connections = loadConnections(reader);
		ArrayList<Long> nodesProcessed = new ArrayList<Long>();
		for(int i = 0; i < connections.size(); i++){
			ODNEATLinkGene link = connections.get(i);
			if(!hasBias || (hasBias && link.getFromId() != biasId)){
				gene.insertSingleLinkGene(link);
				if(!nodesProcessed.contains(link.getFromId())){
					ODNEATNodeGene node = new ODNEATNodeGene(link.getFromId(), getRole(inputsInfo, outputsInfo, link.getFromId()));
					gene.insertSingleNodeGene(node);
					nodesProcessed.add(link.getFromId());
				}
				if(!nodesProcessed.contains(link.getToId())){
					ODNEATNodeGene node = new ODNEATNodeGene(link.getToId(), getRole(inputsInfo, outputsInfo, link.getToId()));
					gene.insertSingleNodeGene(node);
					nodesProcessed.add(link.getToId());
				}
			}
		}

		if(hasBias){
			//now that all nodes have been created, we can set their bias.
			for(int i = 0; i < connections.size(); i++){
				ODNEATLinkGene link = connections.get(i);
				if(link.getFromId() == biasId){
					long nodeToChange = link.getToId();
					double biasValue = link.getWeight();
					gene.getNodeWithId(nodeToChange).setBias(biasValue);
				}
			}
		}*/
		return gene;
	}

	/*private static int getRole(String[] inputsInfo, String[] outputsInfo, long id) {
		
		for(int i = 2; i < inputsInfo.length; i++){
			String s = inputsInfo[i];
			if(Long.valueOf(s) == id)
				return ODNEATNodeGene.INPUT;
		}
		for(int i = 2; i < outputsInfo.length; i++){
			String s = outputsInfo[i];
			if(Long.valueOf(s) == id)
				return ODNEATNodeGene.OUTPUT;
		}

		return ODNEATNodeGene.HIDDEN;
	}

	/*private static ArrayList<ODNEATLinkGene> loadConnections(BufferedReader reader) throws IOException {
		ArrayList<ODNEATLinkGene> connections = new ArrayList<ODNEATLinkGene>();
		String line;
		while((line = reader.readLine()) != null){
			//from, to, weight, innovation number
			String[] connection = line.split(separator);
			long from = Long.valueOf(connection[0]), to = Long.valueOf(connection[1]);
			double weight = Double.valueOf(connection[2]);
			long innovation = Long.valueOf(connection[3]);
			connections.add(new ODNEATLinkGene(innovation, true, from, to, weight));
		}

		reader.close();
		return connections;
	}*/

	/*private static double disableConnectionWeight(double disableConnectionProb, double weight) {
		if(Math.random() < disableConnectionProb)
			weight = 0;
getNumberOfLinkGenes
		return weight;
	}*/

	protected static File getBehaviourFile(final String behaviourToLoad) {
		File repository = new File(REPOSITORY_EVOLVED_BEHAVIOURS);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				return name.endsWith(EVOLVED_FILE_EXTENSION)
						&& name.contains(behaviourToLoad);
			}
		};

		File[] behaviours = repository.listFiles(filter);
		return behaviours[0];
	}
}
