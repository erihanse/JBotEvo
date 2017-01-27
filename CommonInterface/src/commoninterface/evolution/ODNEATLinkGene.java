package commoninterface.evolution;

public class ODNEATLinkGene implements ODNEATGene, Comparable<ODNEATLinkGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected long fromId, toId, innovationNumber;
	protected double weight;
	protected boolean selfRecurrent = false, recurrent = false, enabled = true;

	/**
	 * Creates the gene based on the params
	 * @param innovationNumber
	 * @param enabled
	 * @param fromId
	 * @param toId
	 * @param weight
	 */
	public ODNEATLinkGene(long innovationNumber, boolean enabled, long fromId, long toId, double weight) {
		this.innovationNumber = innovationNumber;
		this.enabled = true;
		this.fromId = fromId;
		this.toId = toId;
		this.weight = weight;
	}

	/**
	 * @return Returns the recurrent.
	 */
	public boolean isRecurrent() {
		return recurrent;
	}
	/**
	 * @param recurrent The recurrent to set.
	 */
	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}
	
	/**
	 * @return Returns the selfRecurrent.
	 */
	public boolean isSelfRecurrent() {
		return selfRecurrent;
	}
	/**
	 * @param selfRecurrent The selfRecurrent to set.
	 */
	public void setSelfRecurrent(boolean selfRecurrent) {
		this.selfRecurrent = selfRecurrent;
	}
	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**

	/**
	 * @param weight The weight to set.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @return Returns the fromId.
	 */
	public long getFromId() {
		return fromId;
	}
	/**
	 * @return Returns the innovationNumber.
	 */
	public long getInnovationNumber() {
		return innovationNumber;
	}
	/**
	 * @return Returns the toId.
	 */
	public long getToId() {
		return toId;
	}
	/**
	 * @return Returns the weight.
	 */
	public double getWeight() {
		return weight;
	}

	@Override
	/**
	 * equal link genes => weight does not matter (because of crossover and speciation).
	 */
	public boolean equals(Object o){
		if((! (o instanceof ODNEATLinkGene)) || o == null)
			return false;

		ODNEATLinkGene other = (ODNEATLinkGene) o;
		return other.getInnovationNumber() == this.getInnovationNumber() &&
				other.fromId == this.fromId && other.toId == this.toId;
	}

	public ODNEATLinkGene copy() {
		ODNEATLinkGene clone = new ODNEATLinkGene(innovationNumber, 
				enabled, fromId, toId, weight);
		clone.setSelfRecurrent(this.selfRecurrent);
		clone.setRecurrent(recurrent);
		
		return clone;
	}
	
	public int compareTo(ODNEATLinkGene o) {
		return Long.compare(this.getInnovationNumber(), o.getInnovationNumber());
	}

	public void setFromId(long from) {
		this.fromId = from;
	}
	
	public void setToId(long to){
		this.toId = to;
	}

	public void setInnovationNumber(long l) {
		this.innovationNumber = l;
	}

}
