package commoninterface.evolution;

public class ODNEATNodeGene implements ODNEATGene, Comparable<ODNEATNodeGene>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected long innovationNumber;
	protected int type;
	public static final int ARBITRATOR_MACRO_NODE = 6;
	public static final int HIERARCHICAL_MACRO_NODE = 5;
	//code of the node gene type.
	public static final int MACRO_NODE = 4;
	public static final int HIDDEN = 3;
	public static final int OUTPUT = 2;
	public static final int INPUT = 1;
	
	protected double bias;
	
	public ODNEATNodeGene(long innovationNumber, int type) {
		this.innovationNumber = innovationNumber;
		this.type = type;
		this.bias = 1.0;
	}
	
	public int getType() {
		return type;
	}

	public long getInnovationNumber() {
		return this.innovationNumber;
	}

	@Override
	public boolean equals(Object o){
		if((! (o instanceof ODNEATNodeGene)) || o == null)
			return false;

		ODNEATNodeGene other = (ODNEATNodeGene) o;
		return other.getInnovationNumber() == this.getInnovationNumber() &&
				other.type == this.type;
	}

	public ODNEATNodeGene copy(){
		ODNEATNodeGene clone = new ODNEATNodeGene(innovationNumber, type);
		return clone;
	}
	
	public int compareTo(ODNEATNodeGene o) {
		return Long.compare(this.getInnovationNumber(), o.getInnovationNumber());
	}

	public void setBias(double newBias) {
		this.bias = newBias;
	}

	public void setInnovationNumber(long newId) {
		this.innovationNumber = newId;
	}

	public double getBias() {
		return bias;
	}

	public void setType(int newType) {
		this.type = newType;
	}
	
}
