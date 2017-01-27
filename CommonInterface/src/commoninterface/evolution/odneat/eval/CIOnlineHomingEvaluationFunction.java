package commoninterface.evolution.odneat.eval;


import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;

public class CIOnlineHomingEvaluationFunction extends CIODNEATEvaluationFunction {

	public CIOnlineHomingEvaluationFunction(CIArguments args) {
		super(args);
	}

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs) {

	}

	@Override
	//needs: inputs in [0,1], outputs in [0,1]
	public double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs) {
		
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
