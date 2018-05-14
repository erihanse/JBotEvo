package erihanse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.robot.ODNetworkRobot;
import simulation.Simulator;
import simulation.Stoppable;
import simulation.Updatable;
import simulation.util.Arguments;

/**
 * ResultWriter
 *  Writes how long the experiment took to file indicated by arguments["filename"],
 *  or "outputfile.txt" if none specified.
 */
public class ResultWriter implements Updatable, Stoppable {
    String filename;
    public ResultWriter(Arguments arguments) {
        filename = arguments.getArgumentAsStringOrSetDefault("filename", "outputfile.txt");
    }

	@Override
	public synchronized void terminate(Simulator simulator) {
        // TODO: Write travelled distance
        // ArrayList<ODNetworkRobot> odRobots = ((EAHSimpleArenaEnvironment) simulator.getEnvironment()).getODRobots();
        // HashMap<String, Arguments> args = simulator.getArguments();
        EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
        try {
            FileWriter fw = new FileWriter(new File(filename), true);
            fw.write(String.format("%s %s\n", simulator.getTime(), eahenv.getTotalDistanceTravelled()));
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void update(Simulator simulator) { }

}