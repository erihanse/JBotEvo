package erihanse.robot.controllers;


import controllers.Controller;
import erihanse.robot.ODNetworkRobot;
import erihanse.robot.sensors.HomeRouteSensor;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class RandomWalkController extends Controller {

    private double maxSpeed = 0.2;
    private double lastLeft = 0;
    private double lastRight = 0;

    Simulator simulator;
    HomeRouteSensor homeSensor;

    public RandomWalkController(Simulator simulator, Robot robot, Arguments args) {
        super(simulator, robot, args);
        if (args.getArgumentIsDefined("actuators")) {
            Arguments actuators = new Arguments(args.getArgumentAsString("actuators"));
            maxSpeed = actuators.getArgumentAsDoubleOrSetDefault("maxspeed", 0.1);
        }
        this.simulator = simulator;
        homeSensor = (HomeRouteSensor) robot.getSensorByType(HomeRouteSensor.class);
    }

    @Override
    public void controlStep(double time) {
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
        ((ODNetworkRobot) robot).setWheelSpeed(lastLeft * maxSpeed, lastRight * maxSpeed);
    }
}