package commoninterface.evolution;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ALGDescriptor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double pXover;
	private double pAddLink;
	private double pAddNode;
	private double pToggleLink;
	private double pMutation;
	private double pMutateBias;
	private double pWeightReplaced;

	private double disjointCoeff;
	private double excessCoeff;
	private double weightCoeff;
	private double threshold;

	private double maxPerturb;
	private double maxBiasPerturb;
	protected double weightRange;

	private int popSize;
	private double weightChange;
	private boolean exchange;


	public ALGDescriptor(String parametersFile){
		Properties p = new Properties();
		try {
			//System.out.println("FILE: " + parametersFile);
			p.load(new FileInputStream(parametersFile));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialize(p);
	}

	private void initialize(Properties p) {
		initialiseLinkAndNodeValues(p);
		initialiseSpeciationCoefficients(p);
		this.popSize = Integer.valueOf(p.getProperty("POP.SIZE", "30"));	
		
		this.exchange = Integer.valueOf(p.getProperty("EXCHANGE")) == 1;
	}




	private void initialiseSpeciationCoefficients(Properties p) {
		disjointCoeff = Double.valueOf(p.getProperty("DISJOINT.COEFFICIENT", "1.0"));
		excessCoeff = Double.valueOf(p.getProperty("EXCESS.COEFFICIENT", "1.0"));
		weightCoeff = Double.valueOf(p.getProperty("WEIGHT.COEFFICIENT", "0.4"));
		threshold = Double.valueOf(p.getProperty("COMPATIBILITY.THRESHOLD", "3.0"));

	}

	private void initialiseLinkAndNodeValues(Properties p) {
		pXover = Double.valueOf(p.getProperty("PROBABILITY.CROSSOVER", "0.75"));
		pAddLink = Double.valueOf(p.getProperty("PROBABILITY.ADDLINK", "0.05"));
		pAddNode = Double.valueOf(p.getProperty("PROBABILITY.ADDNODE", "0.03"));
		pToggleLink = Double.valueOf(p.getProperty("PROBABILITY.TOGGLELINK", "0.25"));
		pMutation = Double.valueOf(p.getProperty("PROBABILITY.MUTATION", "0.8"));
		this.weightChange = Double.valueOf(p.getProperty("PROBABILITY.WEIGHT.CHANGE", "0.8"));
		pMutateBias = Double.valueOf(p.getProperty("PROBABILITY.MUTATEBIAS", "0.3"));
		pWeightReplaced = Double.valueOf(p.getProperty("PROBABILITY.WEIGHT.REPLACED", "0.1"));
		this.weightRange = Double.valueOf(p.getProperty("WEIGHT.RANGE", "5"));
		maxPerturb = Double.valueOf(p.getProperty("MAX.PERTURB", "0.5"));
		maxBiasPerturb = Double.valueOf(p.getProperty("MAX.BIAS.PERTURB", "0.1"));
	}



	/**
	 * @return Returns the pWeightReplaced.
	 */
	public double getPWeightReplaced() {
		return pWeightReplaced;
	}

	public double getProbCrossover(){
		return this.pXover;
	}

	/**
	 * @param weightReplaced The pWeightReplaced to set.
	 */
	public void setPWeightReplaced(double weightReplaced) {
		pWeightReplaced = weightReplaced;
	}


	/**
	 * @return Returns the disjointCoeff.
	 */
	public double getDisjointCoeff() {
		return disjointCoeff;
	}
	/**
	 * @param disjointCoeff The disjointCoeff to set.
	 */
	public void setDisjointCoeff(double disjointCoeff) {
		this.disjointCoeff = disjointCoeff;
	}
	/**
	 * @return Returns the excessCoeff.
	 */
	public double getExcessCoeff() {
		return excessCoeff;
	}
	/**
	 * @param excessCoeff The excessCoeff to set.
	 */
	public void setExcessCoeff(double excessCoeff) {
		this.excessCoeff = excessCoeff;
	}

	/**
	 * @return Returns the threshold.
	 */
	public double getThreshold() {
		return threshold;
	}
	/**
	 * @param threshold The threshold to set.
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	/**
	 * @return Returns the weightCoeff.
	 */
	public double getWeightCoeff() {
		return weightCoeff;
	}
	/**
	 * @param weightCoeff The weightCoeff to set.
	 */
	public void setWeightCoeff(double weightCoeff) {
		this.weightCoeff = weightCoeff;
	}
	/**
	 * @return Returns the pAddLink.
	 */
	public double getPAddLink() {
		return pAddLink;
	}
	/**
	 * @param addLink The pAddLink to set.
	 */
	public void setPAddLink(double addLink) {
		pAddLink = addLink;
	}
	/**
	 * @return Returns the pAddNode.
	 */
	public double getPAddNode() {
		return pAddNode;
	}
	/**
	 * @param addNode The pAddNode to set.
	 */
	public void setPAddNode(double addNode) {
		pAddNode = addNode;
	}
	/**
	 * @param disableLink The pDisableLink to set.
	 */
	public void setPToggleLink(double toggleLink) {
		pToggleLink = toggleLink;
	}
	/**
	 * @return Returns the pMutation.
	 */
	public double getPMutation() {
		return pMutation;
	}
	/**
	 * @param mutation The pMutation to set.
	 */
	public void setPMutation(double mutation) {
		pMutation = mutation;
	}
	/**
	 * @return Returns the pXover.
	 */
	public double getPXover() {
		return pXover;
	}
	/**
	 * @param xover The pXover to set.
	 */
	public void setProbCrossover(double xover) {
		pXover = xover;
	}


	/**
	 * @return Returns the pToggleLink.
	 */
	public double getPToggleLink() {
		return pToggleLink;
	}

	public double getPMutateBias() {
		return pMutateBias;
	}
	public void setPMutateBias(double mutateBias) {
		pMutateBias = mutateBias;
	}

	public double getMaxBiasPerturb() {
		return maxBiasPerturb;
	}
	public void setMaxBiasPerturb(double maxBiasPerturb) {
		this.maxBiasPerturb = maxBiasPerturb;
	}
	public double getMaxPerturb() {
		return maxPerturb;
	}
	public void setMaxPerturb(double maxPerturb) {
		this.maxPerturb = maxPerturb;
	}

	public int getPopulationSize() {
		return popSize;
	}

	public double getWeightRange() {
		return this.weightRange;
	}

	public double getPWeightMutation() {
		return this.weightChange;
	}


	public boolean exchange() {
		return this.exchange;
	}


}
