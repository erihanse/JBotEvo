package commoninterface.evolution;

import java.io.Serializable;

public class TemplateSynapse extends Synapse implements Serializable{

	private String macroId;
	private boolean isInput;
	
	public TemplateSynapse(long innovationNumber, double weight, long from,
			long to, String macroId, boolean isInput) {
		super(innovationNumber, weight, from, to);
		this.macroId = macroId;
		this.isInput = isInput;
	}
	
	public String getMacroId(){
		return this.macroId;
	}
	
	public boolean isInput(){
		return this.isInput;
	}

}
