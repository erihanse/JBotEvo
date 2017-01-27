package commoninterface.evolution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PopulationUtils {

	
	public static ArrayList<ODNEATGenome> loadPopulationFromFile(String file, int inputs, int outputs) {
		HashMap<String,Long> innovations = new HashMap<String, Long>();
		long innovation = 0;
		ArrayList<ODNEATGenome> genomes = new ArrayList<ODNEATGenome>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			//file starts with a "controller" line
			String controller = new String(reader.readLine());
			ArrayList<ODNEATLinkGene> links = new ArrayList<ODNEATLinkGene>();
			while((line = reader.readLine()) != null){
				if(line.startsWith("#CONTROLLER")){
					genomes.add(buildGenomeFromLinks(controller,inputs,outputs, links));
					links.clear();
					controller = new String(line);
				}
				else {
					String[] info = line.split(",");
					long from = Long.valueOf(info[0].split("\\.")[0]), 
							to = Long.valueOf(info[1].split("\\.")[0]);
					double weight = Double.valueOf(info[2]);
					String key = from + "-" + to;
					if(innovations.containsKey(key)){
						links.add(new ODNEATLinkGene(innovations.get(key), true, from, to, weight));
					}
					else {
						links.add(new ODNEATLinkGene(innovation, true, from, to, weight));
						innovations.put(key, innovation);
						innovation++;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return genomes;
	}

	private static ODNEATGenome buildGenomeFromLinks(String controller,
			int inputs, int outputs, ArrayList<ODNEATLinkGene> links) {
		ArrayList<ODNEATNodeGene> nodes = new ArrayList<ODNEATNodeGene>();
		HashSet<Long> nodesIds = new HashSet<Long>();
		for(ODNEATLinkGene link : links){
			if(!nodesIds.contains(link.getFromId())){
				nodes.add(createNode(link.getFromId(), inputs, outputs));
				nodesIds.add(link.getFromId());
			}
			if(!nodesIds.contains(link.getToId())){
				nodes.add(createNode(link.getToId(), inputs, outputs));
				nodesIds.add(link.getToId());
			}
		}
		//ex: #CONTROLLER13.12,1.1362791694352157
		String[] info = controller.replace("#CONTROLLER", "").split(",");
		ODNEATGenome genome = new StandardGenome(info[0], links, nodes);
		genome.setAge(3000);
		genome.setFitness(Double.valueOf(info[1]));
		return genome;
	}

	private static ODNEATNodeGene createNode(long id, int inputs,
			int outputs) {
		int type;
		if(id < inputs)
			type = ODNEATNodeGene.INPUT;
		else if(id < (inputs + outputs))
			type = ODNEATNodeGene.OUTPUT;
		else
			type = ODNEATNodeGene.HIDDEN;
		return new ODNEATNodeGene(id, type);
	}

}
