
package erihanse.evaluationfunction;

import java.util.stream.Stream;

import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 * Class MyEvaluationfunction
 * 	Global
 */
public class FullConnectionEvaluationFunction extends EvaluationFunction {
	private double srcFactor = 0.1;
	private double dstFactor = 0.3;
	private double hopsFactor = 0.3;

	public FullConnectionEvaluationFunction(Arguments args) {
		super(args);
		fitness = 0;
	}


	@Override
	public void update(Simulator simulator) {
        // TODO: Must implement route to destination first.
		EAHSimpleArenaEnvironment sa = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
		int highestFitness = 0;
		for (Robot r : sa.getRobots()) {
			NetworkNode node = (NetworkNode) r;
			int size = node.getHomeRoute().size();

			if(size > highestFitness) {
				highestFitness = size;
			}
		}
		fitness = highestFitness;
	}
}
