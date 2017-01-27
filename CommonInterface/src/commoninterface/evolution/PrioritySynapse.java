package commoninterface.evolution;

import java.io.Serializable;

public class PrioritySynapse extends Synapse implements Serializable {

	protected String macroId;
	
	public PrioritySynapse(long innovationNumber, double weight, long from,
			long to, String macroId) {
		super(innovationNumber, weight, from, to);
		this.macroId = macroId;
	}
	
	public String getMacroId(){
		return this.macroId;
	}
	
	public void setMacroId(String newMacroId){
		this.macroId = newMacroId;
	}

}
