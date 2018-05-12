package erihanse.evaluationfunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import erihanse.environment.EAHSimpleArenaEnvironment;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import simulation.Simulator;
import simulation.util.Arguments;

/**
 * Class MyEvaluationfunction This is a global fitness function to measure the
 * time it takes before we have established a connection between both endpoints.
 */
public class FullConnectionTimerEvaluationFunction extends EvaluationFunction {
    private String outputfilename = "fitnessvalues.txt";

    public FullConnectionTimerEvaluationFunction(Arguments args) {
        super(args);
        fitness = 0;
    }

    @Override
    public void update(Simulator simulator) {
        fitness = simulator.getTime();
        if (fullConnectivity(simulator)) {
            simulationDone(simulator);
        }

        // If done with simulation
        if (simulator.getTime() == simulator.getEnvironment().getSteps()-1) {
            simulationDone(simulator);
        }
    }

    private synchronized void simulationDone(Simulator simulator) {
        simulator.stopSimulation();
        System.out.println("FITNESS: " + fitness + "!!!");

        try {
            FileWriter fw = new FileWriter(new File(outputfilename), true);
            fw.write(fitness + "\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if any of the robots have full connectivity, i.e. at least one of
     * the deployed robots have both a route home, and a route to sink.
     *
     * @return If any of the robots are connected to both endpoints.
     */
    private boolean fullConnectivity(Simulator simulator) {
        EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

        return eahenv.getODRobots()
            .stream()
            .anyMatch(s -> s.getHomeRoute().size() != 0 & s.getSinkRoute().size() != 0);
    }
}
