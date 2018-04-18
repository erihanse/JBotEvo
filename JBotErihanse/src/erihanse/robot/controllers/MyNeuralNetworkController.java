package erihanse.robot.controllers;

import erihanse.robot.MyDifferentialDriveRobot;
import erihanse.robot.sensors.HomeRouteSensor;
import evolutionaryrobotics.neuralnetworks.NeuralNetworkController;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class MyNeuralNetworkController extends NeuralNetworkController {
    public MyNeuralNetworkController(Simulator simulator, Robot robot, Arguments args) {
        super(simulator, robot, args);
    }

    @Override
    public void controlStep(double time) {
        super.controlStep(time);
        ((MyDifferentialDriveRobot) robot)
                .setSourceHops((int) robot.getSensorByType(HomeRouteSensor.class).getSensorReading(999));
        ((MyDifferentialDriveRobot) robot)
                .setDestinationHops((int) robot.getSensorByType(HomeRouteSensor.class).getSensorReading(999));
    }
}