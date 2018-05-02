package erihanse.commoninterface.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;
import erihanse.commoninterface.WLANNetworkCIRobot;

public class WLANScanCISensor extends CISensor {
	private double[] readings;

	protected WLANNetworkCIRobot networkRobot;

	public WLANScanCISensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		networkRobot = (WLANNetworkCIRobot) robot;
		readings = new double[getNumberOfSensors()];
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfSensors() {
		return 5;
	}

	@Override
	/**
	 * readings[0]: n neighbours
	 *
	 * readings[1-4]: signal strength of 4 nearest neighbours
	 */
	public double getSensorReading(int sensorNumber) {
		// TODO Auto-generated method stub
		return readings[sensorNumber];
	}

	@Override
	public void update(double time, Object[] entities) {
		// readings[0]: nNeighbours
		readings[0] = (long) networkRobot.getNumberOfNeighbours();
		// readings[1-4]: signal strength of neighbours.
		// In this case, the distance between the two robots
		double[] neighbourSignalStrengths = networkRobot.getNeighboursSignalStrength();
		for (int i = 1; i < getNumberOfSensors(); i++) {
			readings[i] = neighbourSignalStrengths[i];
		}
	}
}
