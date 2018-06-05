
package erihanse.neuralnetworks;

import commoninterface.CISensor;
import commoninterface.neuralnetwork.inputs.CINNInput;
import commoninterface.utils.CIArguments;

/**
 * CINNInput for number of neighbours.
 */
public class NeighboursCINNInput extends CINNInput {

	private double cutoff = 4500, limit = 1.0;


	public NeighboursCINNInput(CISensor s, CIArguments args) {
		super(s, args);
	}

	@Override
	public int getNumberOfInputValues() {
		return sensor.getNumberOfSensors();
	}

	@Override
	public double getValue(int index) {

		if(index >= getNumberOfInputValues())
			throw new RuntimeException("[NeighboursCINNInput] Invalid number of input index!");

		return sensorToInput(sensor.getSensorReading(index), index);
	}

	double sensorToInput(double value, int sensorNumber) {
		//normalise
		double normalised  = value / cutoff;
		if(normalised > limit)
			normalised = limit;

		return normalised;
	}
}
