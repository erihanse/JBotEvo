package commoninterface.evolution;

import java.io.Serializable;

public class ActionSynapse extends Synapse implements Serializable{

	protected String macroId;
	
	public ActionSynapse(long innovationNumber, double weight, long from,
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
