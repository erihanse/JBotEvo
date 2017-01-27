package commoninterface.sensors;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class ThymioCIInAreaBSensor extends CISensor {

	private ThymioCI thymio;
	private double inAreaB = 0.0;

	public ThymioCIInAreaBSensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		thymio = (ThymioCI)robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 1;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return inAreaB;
	}

	@Override
	public void update(double time, Object[] entities) {
		List<Short>	readings = thymio.getGroundReflectedLightReadings();
		inAreaB = readings.get(0) >= 1000 ? 1.0 : 0.0;
	}

}
