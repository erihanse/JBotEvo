package erihanse.commoninterface.evaluationfunctions;

import commoninterface.RobotCI;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.utils.CIArguments;
import erihanse.robot.ODNetworkRobot;

public class CIConnectivityEvaluationFunction extends CIODNEATEvaluationFunction {

	public CIConnectivityEvaluationFunction(CIArguments args) {
		super(args);
	}

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs) {

	}

	@Override
	//needs: inputs in [0,1], outputs in [0,1]
	public double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs) {
		ODNetworkRobot odrobot = (ODNetworkRobot) robot;
		int id = odrobot.getId();

		/**
		 * assuming that
		 * Sensor1=(
			classname=commoninterface.evolution.RealThymioIRCISensor,
			id=1,
		),
		Sensor2=(
			classname=commoninterface.evolution.RealThymioInNestSensor,
			id=2,
		),
		Sensor3=(
			classname=commoninterface.evolution.RealThymioDistanceNestSensor,
			id=3,
		)
		 */
		//to prevent potential rounding errors
		boolean inNest = Math.abs(inputs[inputs.length - 2] - 1.0) < 0.00000001;
		double nestReading = inputs[inputs.length - 1];

		if(inNest)
			return 1.0;
		else {
			return nestReading;
		}
		//return (inNest) ? 1.0 : 0.0;
	}

	@Override
	public double getDefaultEnergyValue() {
		return this.initialEnergyFactor * this.maxEnergyValue;
	}
}
