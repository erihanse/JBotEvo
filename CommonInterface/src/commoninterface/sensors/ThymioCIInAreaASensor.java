package commoninterface.sensors;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class ThymioCIInAreaASensor extends CISensor {

	private ThymioCI thymio;
	private double inAreaA = 0.0;

	public ThymioCIInAreaASensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		thymio = (ThymioCI)robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 1;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return inAreaA;
	}

	@Override
	public void update(double time, Object[] entities) {
		List<Short>	readings = thymio.getGroundReflectedLightReadings();
		inAreaA = readings.get(0) <= 100 ? 1.0 : 0.0;

	}

}
