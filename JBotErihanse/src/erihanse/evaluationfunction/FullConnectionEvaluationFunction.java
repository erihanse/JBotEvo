
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
 * Class MyEvaluationfunction Global
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
		EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

		NetworkNode closestRobot = getClosestNetworkNodeFromSink(simulator, eahenv);
		// fitness =
		// eahenv.getTargetNest().getPosition().distanceTo(closestRobot.getPosition());

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
