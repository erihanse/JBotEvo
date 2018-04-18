package erihanse.robot.controllers;

import java.util.Random;

import controllers.Controller;
import erihanse.robot.sensors.HomeRouteSensor;
import erihanse.robot.MyDifferentialDriveRobot;
import simulation.Simulator;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class RandomWalkController extends Controller {

    private double maxSpeed = 0.1;
    private double leftSpeed;
    private double rightSpeed;

    // TODO: cheating
    Simulator simulator;

    public RandomWalkController(Simulator simulator, Robot robot, Arguments args) {
        super(simulator, robot, args);
        if (args.getArgumentIsDefined("actuators")) {
            Arguments actuators = new Arguments(args.getArgumentAsString("actuators"));
            maxSpeed = actuators.getArgumentAsDoubleOrSetDefault("maxspeed", 0.1);
        }
        this.simulator = simulator;
    }

    @Override
    public void controlStep(double time) {
        if (time == 450) {
            System.out.println();
        }
        double multFactor = 10;
        if (time % 100 == 0) {
            Random random = simulator.getRandom();

            rightSpeed = (random.nextDouble() - 0.5) * 2;
            leftSpeed = (random.nextDouble() - 0.5) * 2;
        }

        ((DifferentialDriveRobot) robot).setWheelSpeed(leftSpeed * maxSpeed, rightSpeed * maxSpeed);
        if (leftSpeed == 0.0 && rightSpeed == 0.0)
            ((DifferentialDriveRobot) robot).setWheelSpeed(maxSpeed, maxSpeed * Math.min(.98, 1 - multFactor));
        int sensorReading = (int) robot.getSensorByType(HomeRouteSensor.class).getSensorReading(999);
        ((MyDifferentialDriveRobot) robot).setSourceHops(sensorReading);
        // ((MyDifferentialDriveRobot) robot)
        //         .setDestinationHops((int) robot.getSensorByType(DestinationHopsSensor.class).getSensorReading(999));
    }
}