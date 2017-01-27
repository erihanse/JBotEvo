package commoninterface.evolution;

import commoninterface.CISensor;
import commoninterface.neuralnetwork.inputs.CINNInput;
import commoninterface.utils.CIArguments;

public class RealThymioGeneralOneNNInputs  extends CINNInput {
	
	public RealThymioGeneralOneNNInputs(CISensor s, CIArguments args) {
		super(s, args);
	}

	@Override
	public int getNumberOfInputValues() {
		return sensor.getNumberOfSensors();
	}

	@Override
	public double getValue(int index) {
		return sensor.getSensorReading(index);
	}
	
}
