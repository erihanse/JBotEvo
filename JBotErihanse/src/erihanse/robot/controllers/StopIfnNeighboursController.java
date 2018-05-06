package erihanse.robot.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import controllers.Controller;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import erihanse.robot.sensors.HomeRouteSensor;
import mathutils.Vector2d;
import simulation.Simulator;
import simulation.physicalobjects.PhysicalObject;
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

    ODNetworkRobot odRobot;

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
        this.odRobot = (ODNetworkRobot) robot;

        homeSensor = (HomeRouteSensor) robot.getSensorByType(HomeRouteSensor.class);

    }

    @Override
    public void controlStep(double time) {
        if (partOfLongestHomeRoute() && partOfLongestSinkRoute()) {
            robot.setBodyColor(Color.GREEN);
            return;
        }

        // Stay stopped if previously stopped until timer runs out
        // robot.setLedState(LedState.ON);
        // robot.setLedColor(Color.cyan);
        if (!partOfLongestHomeRoute()) {
            robot.setLedState(LedState.OFF);
        }
        if (time < timeToStartAgain) {
            return;
        }

        // Don't move if part of longest solution TODO: Buuut maybe move if you're close
        // to the others in the route.
        if (partOfLongestHomeRoute()) {
            if (inOptimalRange()) {
                robot.stop();
                robot.setBodyColor(1, 0, 0);
                return;
            } else {
                robot.setLedColor(Color.cyan);
                robot.setLedState(LedState.ON);
            }
            // return;
        }

        // if (((ODNetworkRobot) robot).getNumberOfNeighbours() == nNeighboursToStop) {
        //     timeToStartAgain = time + stopTime;
        //     robot.stop();
        //     robot.setBodyColor(1, 0, 0);
        //     return;
        // }

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

    private boolean inOptimalRange() {
        Vector2d robpos = odRobot.getPosition();
        return eahenv.getLongestRouteFromHome()
            .stream()
            // Filter out those not in range
            .filter(homeRouteRobot -> odRobot.robotsInRange().contains(homeRouteRobot))
            // Get the positions of neighbouring robots
            .map(homeRouteRobot -> ((ODNetworkRobot) homeRouteRobot).getPosition())
            // Distances must be within interval
            .allMatch(s -> robpos.distanceTo(s) > 0.7 && robpos.distanceTo(s) < 0.9);
    }

    private boolean partOfLongestHomeRoute() {
        return (eahenv.getLongestRouteFromHome().contains((NetworkNode) robot));
    }

    private boolean partOfLongestSinkRoute() {
        return (eahenv.getLongestRouteFromSink().contains((NetworkNode) robot));
    }
}