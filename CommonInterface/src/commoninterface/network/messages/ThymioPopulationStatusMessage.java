package commoninterface.network.messages;

import java.util.Arrays;

public class ThymioPopulationStatusMessage extends Message {

	private static final long serialVersionUID = -6251588481500969630L;
	private int robotId, popSize, numSpecies;
	private double globalExecutionStep;
	//size of each individual species
	private int[] speciesSize;
	private String[] genomeIds;
	private double[] genomeFitnessScores;
	
	public ThymioPopulationStatusMessage(String senderHostname, int robotId, 
			double globalStep, int popSize, int numSpecies,
			int[] speciesSize, String[] genomeIds, double[] genomeFitnessScores) {
		super(senderHostname);
		this.robotId = robotId;
		this.globalExecutionStep = globalStep;
		this.popSize = popSize;
		this.numSpecies = numSpecies;
		this.speciesSize = speciesSize.clone();
		this.genomeIds = genomeIds;
		this.genomeFitnessScores = genomeFitnessScores;
	}

	
	@Override
	public Message getCopy() {
		return new ThymioPopulationStatusMessage(senderHostname, this.robotId, this.globalExecutionStep, this.popSize,
				this.numSpecies, this.speciesSize.clone(), this.genomeIds.clone(), this.genomeFitnessScores.clone());
	}
	
	@Override
	public String toString(){
		return ("P-ST" + ";" + robotId + ";"  + globalExecutionStep + ";" + popSize + ";" + numSpecies + ";" + Arrays.toString(speciesSize) + ";" + 
				Arrays.toString(genomeIds) + ";" + Arrays.toString(genomeFitnessScores));
	}


	/**
	 * @return the robotId
	 */
	public int getRobotId() {
		return robotId;
	}


	/**
	 * @return the globalExecutionStep
	 */
	public double getGlobalExecutionStep() {
		return globalExecutionStep;
	}


	/**
	 * @return the popSize
	 */
	public int getPopSize() {
		return popSize;
	}


	/**
	 * @return the numSpecies
	 */
	public int getNumSpecies() {
		return numSpecies;
	}


	/**
	 * @return the speciesSize
	 */
	public int[] getSpeciesSize() {
		return speciesSize;
	}


	/**
	 * @return the genomeIds
	 */
	public String[] getGenomeIds() {
		return genomeIds;
	}


	/**
	 * @return the genomeFitnessScores
	 */
	public double[] getGenomeFitnessScores() {
		return genomeFitnessScores;
	}

}
