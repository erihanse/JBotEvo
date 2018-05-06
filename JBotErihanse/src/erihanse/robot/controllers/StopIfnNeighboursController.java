package erihanse.robot.controllers;

import java.awt.Color;

import controllers.Controller;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import erihanse.robot.sensors.HomeRouteSensor;
import simulation.Simulator;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.LedState;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class StopIfnNeighboursController extends Controller {

    private double maxSpeed = 0.1;
    private double timeToStartAgain;

    double lastLeft = 0;
    double lastRight = 0;

    @ArgumentsAnnotation(name = "N neighbours to stop on", defaultValue = "4")
    private int nNeighboursToStop;
    @ArgumentsAnnotation(name = "Frames to sleep when stopping", defaultValue = "0")
	private double stopTime;

    Simulator simulator;
    HomeRouteSensor homeSensor;
    EAHSimpleArenaEnvironment eahenv;



    public StopIfnNeighboursController(Simulator simulator, Robot robot, Arguments args) {
        super(simulator, robot, args);
        if (args.getArgumentIsDefined("actuators")) {
            Arguments actuators = new Arguments(args.getArgumentAsString("actuators"));
            maxSpeed = actuators.getArgumentAsDoubleOrSetDefault("maxspeed", 0.1);
        }
        nNeighboursToStop = args.getArgumentAsIntOrSetDefault("nneighbourstostopon", 4);
        stopTime = args.getArgumentAsDoubleOrSetDefault("stoptime", 0);

        this.simulator = simulator;
        this.eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

        homeSensor = (HomeRouteSensor) robot.getSensorByType(HomeRouteSensor.class);

    }

    @Override
    public void controlStep(double time) {
        // Stay stopped if previously stopped until timer runs out
        // robot.setLedState(LedState.ON);
        // robot.setLedColor(Color.cyan);
        if (time < timeToStartAgain) {
            return;
        }

        // Don't move if part of longest solution
        if (((ODNetworkRobot) robot).getNumberOfNeighbours() == nNeighboursToStop && partOfLongestHomeRoute()) {
            timeToStartAgain = time + stopTime;
            robot.stop();
            robot.setBodyColor(1, 0, 0);
            return;
        }

        if (eahenv.getLongestRouteFromHome().contains(robot)) {
            robot.setLedColor(Color.cyan);
        } else {
            robot.setLedColor(Color.black);
        }

        int direction = simulator.getRandom().nextInt(4);

        robot.setBodyColor(0, 0, 0);
        if (time % 100 == 0) {

            switch (direction) {
            // forward
            case 0:
                lastLeft = 1.0;
                lastRight = 1.0;
                break;

            // backward
            case 1:
                lastLeft = -1.0;
                lastRight = -1.0;
                break;
            // left
            case 2:
                lastLeft = 0.5;
                lastRight = 1.0;
                break;
            // right
            case 3:
                lastLeft = 1.0;
                lastRight = 0.5;
                break;
            }
        }

        ((DifferentialDriveRobot) robot).setWheelSpeed(lastLeft * maxSpeed, lastRight * maxSpeed);

        int sensorReading = (int) homeSensor.getSensorReading(999);
        ((ODNetworkRobot) robot).setSourceHops(sensorReading);
    }

	private boolean partOfLongestHomeRoute() {
        return (eahenv.getLongestRouteFromHome().contains((NetworkNode) robot));
	}
}