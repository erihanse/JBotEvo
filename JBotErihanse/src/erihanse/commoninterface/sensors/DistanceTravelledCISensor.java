package erihanse.commoninterface.sensors;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;

/**
 * DistanceTravelledCISensor
 */
public abstract class DistanceTravelledCISensor extends CISensor {
    protected double distanceTravelled = 0;

	public DistanceTravelledCISensor(int id, RobotCI robot, CIArguments args) {
        super(id, robot, args);
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
	public abstract void update(double time, Object[] entities);


}