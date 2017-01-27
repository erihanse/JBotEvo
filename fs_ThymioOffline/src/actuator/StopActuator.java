package actuator;

import simulation.Simulator;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.Robot;
import simulation.robot.actuators.Actuator;
import simulation.util.Arguments;

public class StopActuator extends Actuator {

	private double value;

	public StopActuator(Simulator simulator, int id, Arguments args) {
		super(simulator, id, args);
	}

	@Override
	public void apply(Robot robot, double timeDelta) {
		if(value > 0.5)
			((DifferentialDriveRobot) robot).setWheelSpeed(0.0, 0.0);
	}
	
	public void setStatus(double value) {
		this.value = value;
	}

	public boolean isStopped() {
		return value > 0.5;
	}
}
