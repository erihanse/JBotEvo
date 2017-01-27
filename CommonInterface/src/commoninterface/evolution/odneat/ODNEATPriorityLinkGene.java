package commoninterface.evolution.odneat;


public class ODNEATPriorityLinkGene extends ODNEATTemplateLinkGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final double DEFAULT_CONNECTION_WEIGHT = 1.0;
	
	public ODNEATPriorityLinkGene(long innovationNumber, boolean enabled,
			long fromId, long toId, String macroId) {
		//by default, the connection weight of priority links is set at 1.0
		super(innovationNumber, enabled, fromId, toId, DEFAULT_CONNECTION_WEIGHT, macroId, false);
	}
	
	
	@Override
	public boolean equals(Object o){
		if((! (o instanceof ODNEATPriorityLinkGene)) || o == null)
			return false;
		ODNEATPriorityLinkGene other = (ODNEATPriorityLinkGene) o;
		
		return this.innovationNumber == other.innovationNumber &&
				this.enabled == other.enabled &&
				this.fromId == other.fromId &&
				this.toId == other.toId &&
				this.macroId.equalsIgnoreCase(other.macroId) &&
				this.isInput == other.isInput;
		}

	public ODNEATPriorityLinkGene copy() {
		ODNEATPriorityLinkGene copy = new ODNEATPriorityLinkGene(this.innovationNumber, this.enabled,
				this.fromId, this.toId, new String(this.macroId));
		copy.weight = this.weight;
		copy.isInput = this.isInput;
		
		return copy;
	}
}
