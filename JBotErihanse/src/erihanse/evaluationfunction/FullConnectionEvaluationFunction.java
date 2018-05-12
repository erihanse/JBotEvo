
package erihanse.evaluationfunction;

import java.util.Comparator;
import java.util.stream.Stream;

import commoninterface.mathutils.Vector2d;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import simulation.Simulator;
import simulation.physicalobjects.PhysicalObject;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 * Class MyEvaluationfunction (Global fitness function)
 * Returns the nearest robot from sink that is part of the longest home route.
 */
public class FullConnectionEvaluationFunction extends EvaluationFunction {

	public FullConnectionEvaluationFunction(Arguments args) {
		super(args);
		fitness = 0;
	}

	@Override
	public void update(Simulator simulator) {
		EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
		NetworkNode closestRobot = getClosestNetworkNodeFromSink(simulator, eahenv);
	}

	private NetworkNode getClosestNetworkNodeFromSink(Simulator simulator, EAHSimpleArenaEnvironment eahenv) {
		for (Robot r : simulator.getRobots()) {
			NetworkNode node = (NetworkNode) r;
			if (node.getHomeRoute().size() > 0 && node.getSinkRoute().size() > 0) {
				System.out.println("Full connectivity");
			}
		}

		NetworkNode closestNetworkRobot = eahenv.getLongestRouteFromHome()
			.stream()
			.min(new Comparator<NetworkNode>() {
				@Override
				public int compare(NetworkNode o1, NetworkNode o2) {
					return ((PhysicalObject) o1).getPosition()
						.distanceTo(eahenv.getSinkNest().getPosition()) < ((PhysicalObject) o2).getPosition()
							.distanceTo(eahenv.getSinkNest().getPosition()) ? -1 : 1;
				}
			})
			.get();
		fitness = (closestNetworkRobot.getPosition().distanceTo(eahenv.getSinkNest().getPosition()));
		return closestNetworkRobot;
	}
}
