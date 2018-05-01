package erihanse.commoninterface.sensors;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;
import erihanse.commoninterface.WLANNetworkCIRobot;

public class WLANScanCISensor extends CISensor {
	private List<Long> readings;
	private int nRobots;
	protected WLANNetworkCIRobot networkRobot;

	public WLANScanCISensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		networkRobot = (WLANNetworkCIRobot) robot;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfSensors() {
		return 5;
	}

	@Override
	/**
	 * readings[0]: n neighbours
	 * readings[1-4]: signal strength of 4 nearest neighbours
	 */
	public double getSensorReading(int sensorNumber) {
		// TODO Auto-generated method stub
		return readings.get(sensorNumber);
	}

	@Override
	public void update(double time, Object[] entities) {
		// readings = thymio.
	}

}
