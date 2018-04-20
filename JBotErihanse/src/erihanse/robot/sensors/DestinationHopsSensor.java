package erihanse.robot.sensors;

import java.util.ArrayList;

import erihanse.robot.ODNetworkRobot;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.robot.Robot;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class DestinationHopsSensor extends Sensor {

    @ArgumentsAnnotation(name = "range", defaultValue = "" + DEFAULT_RANGE)
    private double range;
    private Environment env;

    @ArgumentsAnnotation(name = "turnoff", help = "Only works once.", values = { "0", "1" })
    private boolean turnOff = true;
    private boolean seenRobot = false;
    private double previousValue = 0;

    public DestinationHopsSensor(Simulator simulator, int id, Robot robot, Arguments args) {
        super(simulator, id, robot, args);
        range = args.getArgumentAsDoubleOrSetDefault("range", DEFAULT_RANGE);
        //default it as true to enable backwards compatibility
        turnOff = args.getArgumentAsIntOrSetDefault("turnoff", 1) == 1;
        this.env = simulator.getEnvironment();
    }

    @Override
    public double getSensorReading(int sensorNumber) {
        // TODO:
        ArrayList<Robot> robots = env.getRobots();
        int nHops = Integer.MAX_VALUE;

        if (this.robot.getSensorByType(DestinationHopsSensor.class).getSensorReading(999) == 1) {
            return 1;
        }

        for (Robot r : robots) {
            if (r.getPosition().distanceTo(this.robot.getPosition()) < range && this.robot.getId() != r.getId()) {
                ArrayList<Sensor> sensors = r.getSensors();
                for (Sensor s : sensors) {
                    if (s instanceof DestinationHopsSensor) {
                        double reading = ((ODNetworkRobot) r).getDestinationHops();
                        if (reading < nHops) {
                            nHops = (int) reading + 1;
                        }
                    }
                }
            }
        }
        return nHops;
    }

}
