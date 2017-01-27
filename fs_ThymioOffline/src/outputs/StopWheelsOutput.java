package outputs;

import evolutionaryrobotics.neuralnetworks.outputs.NNOutput;
import simulation.robot.actuators.Actuator;
import simulation.util.Arguments;
import actuator.StopActuator;

public class StopWheelsOutput extends NNOutput {
	private StopActuator actuator;
	protected double value;

	public StopWheelsOutput(Actuator actuator, Arguments args) {
		super(actuator,args);
		this.actuator  = (StopActuator)actuator;
	}

	@Override
	public int getNumberOfOutputValues() {
		return 1;
	}

	@Override
	public void setValue(int output, double value) {
		this.value = value;
	}

	@Override
	public void apply() {
		actuator.setStatus(value);
	}
}