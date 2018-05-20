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
 * ResultWriter Writes how long the experiment took to file indicated by
 * arguments["filename"], or "outputfile.txt" if none specified.
 */
public class ResultWriter implements Updatable, Stoppable {
    String filename;
    Arguments arguments;

    public ResultWriter(Arguments arguments) {
        filename = arguments.getArgumentAsStringOrSetDefault("filename", "outputfile.txt");
        this.arguments = arguments;
    }

    @Override
    public synchronized void terminate(Simulator simulator) {
        EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
        try {
            String outputFolder = simulator.getArguments().get("--output").getCompleteArgumentString();
            FileWriter fw = new FileWriter(new File(outputFolder + "/../../" + filename), true);
            fw.write(String.format("%s %s\n", simulator.getTime(), eahenv.getTotalDistanceTravelled()));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Simulator simulator) {
    }

}