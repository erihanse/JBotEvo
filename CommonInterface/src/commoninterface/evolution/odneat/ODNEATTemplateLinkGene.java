package commoninterface.evolution.odneat;

import commoninterface.evolution.ODNEATLinkGene;

/**
 * connection link genes that make the connection between the ANN and the macro-neurons
 * can be either input or output
 * @author fernando
 *
 */
public class ODNEATTemplateLinkGene extends ODNEATLinkGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String macroId;//, hierarchyId;
	protected boolean isInput;
	//protected boolean belongsToHierarchy = false;
	
	public ODNEATTemplateLinkGene(long innovationNumber, boolean enabled,
			long fromId, long toId, double weight, String macroId, boolean isInput) {
		super(innovationNumber, enabled, fromId, toId, weight);
		this.macroId = macroId;
		this.isInput = isInput;
	}
	
	public boolean belongsToMacro(){
		return true;
	}
	
	public String getMacroId(){
		return macroId;
	}
	
	/*public boolean belongsToHierarchy(){
		return this.belongsToHierarchy;
	}
	
	public void setBelongsToHierarchy(boolean status){
		this.belongsToHierarchy = status;
	}
	
	public void setHierarchyId(String id){
		this.hierarchyId = new String(id);
	}
	
	public String getHierarchyId(){
		return hierarchyId;
	}*/
	
	@Override
	public boolean equals(Object o){
		if((! (o instanceof ODNEATTemplateLinkGene)) || o == null)
			return false;

		ODNEATTemplateLinkGene other = (ODNEATTemplateLinkGene) o;
		return other.getMacroId().equalsIgnoreCase(this.macroId) 
				&& other.getInnovationNumber() == this.getInnovationNumber() &&
				other.fromId == this.fromId && other.toId == this.toId && 
				//&&other.weight == this.weight
				other.selfRecurrent == this.selfRecurrent
				&& other.recurrent == this.recurrent;
	}

	public ODNEATTemplateLinkGene copy() {
		ODNEATTemplateLinkGene clone = new ODNEATTemplateLinkGene(innovationNumber, 
				enabled, fromId, toId, weight, macroId, isInput);
		clone.setSelfRecurrent(this.selfRecurrent);
		clone.setRecurrent(recurrent);
		
		return clone;
	}
	
	public boolean isInput(){
		return this.isInput;
	}
	
	//either serves as input to the macro-neurons or as output
	public boolean isOutput(){
		return !this.isInput;
	}

}
