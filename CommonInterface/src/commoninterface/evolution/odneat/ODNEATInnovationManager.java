package commoninterface.evolution.odneat;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Random;


import commoninterface.evolution.ODNEATGene;
import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ODNEATLinkGene;
import commoninterface.evolution.ODNEATNodeGene;
import commoninterface.utils.CIArguments;

/**
 * 
 * @author Fernando
 * 
 */
public class ODNEATInnovationManager implements Serializable {

	private static final long serialVersionUID = 1L;
	protected long START_TIME = System.nanoTime();


	private transient GenomeBuilder builder;

	public ODNEATInnovationManager(Random random, double connectionWeightRange) {
		this.builder = new GenomeBuilder(random, connectionWeightRange);
	}

	public long nextInnovationNumber() {
		long result = System.nanoTime() - START_TIME;
		return result;
	}

	public ODNEATNodeGene submitNodeInnovation() {
		long innovationNumber = this.nextInnovationNumber();

		ODNEATNodeGene gene = new ODNEATNodeGene(innovationNumber,	ODNEATNodeGene.HIDDEN);
		return gene;
	}
	
	
	public ODNEATLinkGene submitLinkInnovation(long from, long to) {
		long innovationNumber = this.nextInnovationNumber();
		// the 0 weight is a place holder
		ODNEATLinkGene gene = new ODNEATLinkGene(innovationNumber, true, from,
				to, 0);
		return gene;
	}

	public ODNEATGenome initialiseInnovation(String newGenomeId, int inputs,
			int outputs, ArrayList<ODNEATGene> genes, CIArguments macroArgs) {
			String gName = macroArgs.getArgumentAsStringOrSetDefault("encoding",
					"").toLowerCase();
			if(gName.equalsIgnoreCase("macrogenome"))
					return builder.createMacroGenome(newGenomeId, inputs, outputs, macroArgs);
			if(genes.isEmpty())
				return builder.createStandardGenome(newGenomeId, inputs, outputs);
			else
				return builder.createSeededGenome(newGenomeId, inputs, outputs, genes);
	}

}
