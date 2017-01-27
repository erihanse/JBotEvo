package commoninterface.evolution;

import commoninterface.CISensor;
import commoninterface.neuralnetwork.inputs.CINNInput;
import commoninterface.utils.CIArguments;

public class RealThymioNormalizedCIIRNNInputs extends CINNInput {
	
	private double cutoff = 4500, limit = 1.0;

	public RealThymioNormalizedCIIRNNInputs(CISensor s, CIArguments args) {
		super(s, args);
	}

	@Override
	public int getNumberOfInputValues() {
		return sensor.getNumberOfSensors();
	}

	@Override
	public double getValue(int index) {
		//get reading
		double normalised = sensor.getSensorReading(index);
		//normalise
		normalised /= cutoff;
		if(normalised > limit)
			normalised = limit;
		
		return normalised;
	}
	
}
