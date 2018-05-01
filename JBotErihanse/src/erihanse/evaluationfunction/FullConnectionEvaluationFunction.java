
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
		EAHSimpleArenaEnvironment sa = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

		int highestHomeHops = 0;
		int highestDestHops = 0;
		for (Robot r : simulator.getRobots()) {
			NetworkNode node = (NetworkNode) r;
			int homeRouteHops = node.getHomeRoute().size();
			int targetRouteHops = node.getTargetRoute().size();
			highestHomeHops = Math.max(homeRouteHops, highestHomeHops);
			highestDestHops = Math.max(targetRouteHops, highestDestHops);

			if (homeRouteHops > 0 && targetRouteHops > 0) {
				System.out.println("Full connectivity");
			}
		}
		// TODO: Set fitness to something meaningful
		fitness = highestHomeHops;
		// Durp
		// if (highestHomeHops > 0 ^ highestDestHops > 0) {
		// 	fitness = Math.max(highestHomeHops, highestDestHops);
		// } else if (highestHomeHops > 0 && highestDestHops > 0) {
		// 	fitness = Integer.MAX_VALUE;
		// }
	}
}
