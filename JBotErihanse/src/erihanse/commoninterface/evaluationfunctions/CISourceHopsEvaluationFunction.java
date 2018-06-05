package erihanse.commoninterface.evaluationfunctions;

import commoninterface.RobotCI;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.utils.CIArguments;
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
public class CISourceHopsEvaluationFunction extends CIODNEATEvaluationFunction {
	private double srcFactor = 0.1;
	private double dstFactor = 0.3;
	private double hopsFactor = 0.3;

	public CISourceHopsEvaluationFunction(CIArguments args) {
		super(args);
	}

	// @Override
	// public void update(Simulator simulator) {
	// 	EAHSimpleArenaEnvironment sa = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
	// 	int highestFitness = 0;
	// 	for (Robot r : sa.getRobots()) {
	// 		NetworkNode node = (NetworkNode) r;
	// 		int size = node.getHomeRoute().size();

	// 		if(size > highestFitness) {
	// 			highestFitness = size;
	// 		}
	// 	}
	// 	// fitness = highestFitness;
	// }

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs) {

	}

	@Override
	public double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs) {
		// TODO
		return 0;
	}

	@Override
	public double getDefaultEnergyValue() {
		return 0;
	}
}
