package erihanse.commoninterface.evaluationfunctions;

import java.awt.Color;

import commoninterface.RobotCI;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.utils.CIArguments;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.robot.ODNetworkRobot;
import mathutils.Vector2d;

public class CIConnectivityEvaluationFunction extends CIODNEATEvaluationFunction {

	private ODNetworkRobot odRobot;

	public CIConnectivityEvaluationFunction(CIArguments args) {
		super(args);
	}

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs) {

	}

	@Override
	// needs: inputs in [0,1], outputs in [0,1]
	public double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs) {
		if (odRobot == null) {

			this.odRobot = (ODNetworkRobot) robot;
		}

		// Full connectivity, stop simulation
		if (odRobot.getHomeRoute().size() > 0 && odRobot.getSinkRoute().size() > 0) {
			odRobot.getSimulator().stopSimulation();
		}

		EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) odRobot.getSimulator().getEnvironment();
		// if (inOptimalRangeOfClosestNeighbours() && odRobot.getNumberOfNeighbours() !=
		// 0 && !odRobot.getHomeRoute().isEmpty()) {
		if (inOptimalRangeOfClosestNeighbours() && eahenv.getLongestRouteFromHome().contains(odRobot)) {
			robot.setMotorSpeeds(0.5, 0.5); // Stand still
			odRobot.setBodyColor(Color.green);
			return 1;
		}
		// Punish if crashing
		if (odRobot.getCollidingObjects().size() > 0) {
			odRobot.setBodyColor(Color.YELLOW);
			return 0;
		} else {
			odRobot.setBodyColor(Color.black);
			return currentEnergy - 0.001;
		}
	}

	private boolean inOptimalRangeOfClosestNeighbours() {
		Vector2d robpos = odRobot.getPosition();
		EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) odRobot.getSimulator().getEnvironment();

		return eahenv.getLongestRouteFromHome()
			.stream()
			// Filter out those not in range
			.filter(homeRouteRobot -> odRobot.getNeighbourRobots().contains(homeRouteRobot))
			// Get the positions of neighbouring robots
			.map(homeRouteRobot -> ((ODNetworkRobot) homeRouteRobot).getPosition())
			// Distances must be within interval
			// .allMatch(s -> robpos.distanceTo(s) > 0.7 && robpos.distanceTo(s) < 0.9);
			.allMatch(s -> robpos.distanceTo(s) > 0.7);
	}

	@Override
	public double getDefaultEnergyValue() {
		return this.initialEnergyFactor * this.maxEnergyValue;
	}
}
