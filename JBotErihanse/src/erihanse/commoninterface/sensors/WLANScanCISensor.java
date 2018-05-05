package erihanse.commoninterface.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;
import erihanse.commoninterface.WLANNetworkCIRobot;

public class WLANScanCISensor extends CISensor {
	final private int numberOfNeighboursToCompare = 5;
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
		return numberOfNeighboursToCompare;
	}

	@Override
	/**
	 * readings[0]: n neighbours
	 *
	 * readings[1-4]: signal strength of 4 nearest neighbours
	 */
	public double getSensorReading(int sensorNumber) {
		return readings[sensorNumber];
	}

	@Override
	public void update(double time, Object[] entities) {
		// Zero out readings
		readings = new double[getNumberOfSensors()];
		// readings[0]: nNeighbours
		readings[0] = (long) networkRobot.getNumberOfNeighbours();
		// readings[1-4]: signal strength of neighbours.
		// In this case, the distance between the two robots
		double[] neighbourSignalStrengths = networkRobot.getNeighboursSignalStrength();
		for (int i = 1; i < getNumberOfSensors() && i < neighbourSignalStrengths.length - 1; i++) {
			readings[i] = neighbourSignalStrengths[i-1];
		}
	}
}
