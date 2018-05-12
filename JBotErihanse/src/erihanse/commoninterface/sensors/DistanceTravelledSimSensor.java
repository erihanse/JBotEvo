package erihanse.commoninterface.sensors;

import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;
import erihanse.robot.ODNetworkRobot;

/**
 * DistanceTravelledSimSensor
 */
public class DistanceTravelledSimSensor extends DistanceTravelledCISensor {
    ODNetworkRobot odRobot;
    public DistanceTravelledSimSensor(int id, RobotCI robot, CIArguments args) {
        super(id, robot, args);
        odRobot = (ODNetworkRobot) robot;
    }

    @Override
    public void update(double time, Object[] entities) {
        distanceTravelled += odRobot.getPosition().distanceTo(odRobot.getPreviousPosition());
    }
}