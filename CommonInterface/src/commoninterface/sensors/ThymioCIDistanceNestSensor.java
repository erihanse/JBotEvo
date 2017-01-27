package commoninterface.sensors;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class ThymioCIDistanceNestSensor extends CISensor {

	private ThymioCI thymio;
	private double reading;

	public ThymioCIDistanceNestSensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		thymio = (ThymioCI)robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 1;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return reading;
	}

	@Override
	public void update(double time, Object[] entities) {
		List<Short> readings = thymio.getInfraredSensorsReadings();
		reading = readings.get(0)/1023;
	}

}
