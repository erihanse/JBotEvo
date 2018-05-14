package erihanse.commoninterface.sensors;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;
import erihanse.robot.ODNetworkRobot;

/**
 * DistanceTravelledCISensor
 */
public class DistanceTravelledCISensor extends CISensor {
	protected double distanceTravelled = 0;
	// TODO: This is wrong?
    ODNetworkRobot odRobot;

	public DistanceTravelledCISensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		odRobot = (ODNetworkRobot) robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 1;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return distanceTravelled;
	}

	@Override
    public void update(double time, Object[] entities) {
        distanceTravelled += odRobot.getPosition().distanceTo(odRobot.getPreviousPosition());
    }
}