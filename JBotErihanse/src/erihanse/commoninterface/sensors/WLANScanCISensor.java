package erihanse.commoninterface.sensors;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class WLANScanCISensor extends CISensor {
	private ThymioCI thymio;
	private List<Long> readings;
	private int nRobots;

	public WLANScanCISensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfSensors() {
		return nRobots;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(double time, Object[] entities) {
		// readings = thymio.

	}

}
