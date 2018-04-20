
package erihanse.robot.sensors;

import erihanse.robot.ODNetworkRobot;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.robot.Robot;
import simulation.robot.sensors.NestSensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class HomeRouteSensor extends NestSensor {

    @ArgumentsAnnotation(name = "range", defaultValue = "" + DEFAULT_RANGE)
    private double range;
    private Environment env;

    @ArgumentsAnnotation(name = "turnoff", help = "Only works once.", values = { "0", "1" })
    private boolean turnOff = true;
    private boolean seenRobot = false;
    private double previousValue = 0;

    public HomeRouteSensor(Simulator simulator, int id, Robot robot, Arguments args) {
        super(simulator, id, robot, args);
        range = args.getArgumentAsDoubleOrSetDefault("range", DEFAULT_RANGE);
        //default it as true to enable backwards compatibility
        turnOff = args.getArgumentAsIntOrSetDefault("turnoff", 1) == 1;
        this.env = simulator.getEnvironment();
    }

    @Override
    public double getSensorReading(int sensorNumber) {
        // ArrayList<Robot> robots = env.getRobots();
        // int nHops = Integer.MAX_VALUE;

        // SimpleArenaEnvironment se = (SimpleArenaEnvironment) env;

        // if (MyMathUtils.inRange(this.robot, se.getHomeNest(), range)) {
        //     return 1;
        // }

        // for (Robot r : robots) {
        //     // If in range
        //     if (r.getPosition().distanceTo(this.robot.getPosition()) < range && this.robot != r) {
        //         ArrayList<Sensor> sensors = r.getSensors();
        //         for (Sensor s : sensors) {
        //             if (s instanceof HomeRouteSensor) {
        //                 double otherRobotReading = ((MyDifferentialDriveRobot) r).getSourceHops();
        //                 if (otherRobotReading < nHops) {
        //                     nHops = (int) otherRobotReading + 1;
        //                 }
        //             }
        //         }
        //     }
        // }
        // if (nHops == Integer.MAX_VALUE) {
        //     return -1;
        // }
        // return nHops;
        return ((ODNetworkRobot) robot).getHomeRoute().size();
    }
}