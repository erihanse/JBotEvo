package erihanse.evaluationfunction;

import java.util.ArrayList;

import erihanse.environment.EAHSimpleArenaEnvironment;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import mathutils.Vector2d;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 * DistanceFromTargetEvaluationFunction
 * The evaluation is set by the distance between the target and the closest robot.
 */
public class DistanceFromTargetEvaluationFunction extends EvaluationFunction {
    public DistanceFromTargetEvaluationFunction(Arguments args) {
        super(args);
    }

    @Override
    public void update(Simulator simulator) {
        ArrayList<Robot> robots = simulator.getEnvironment().getRobots();
        double distances[] = new double[robots.size()];
        int i = 0;
        double min = Double.MAX_VALUE;
        Robot closestRobot = null;
        Vector2d targetPosition = ((EAHSimpleArenaEnvironment)simulator.getEnvironment()).getSinkNest().getPosition();

		for (Robot r : robots) {
            distances[i] = r.getPosition().distanceTo(targetPosition);
            if (distances[i] < min) {
                closestRobot = r;
                min = distances[i];
            }
            i++;
        }

        fitness = min * 100;
    }

}