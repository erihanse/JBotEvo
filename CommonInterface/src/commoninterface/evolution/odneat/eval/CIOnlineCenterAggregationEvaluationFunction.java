package commoninterface.evolution.odneat.eval;

import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;

public class CIOnlineCenterAggregationEvaluationFunction extends CIODNEATEvaluationFunction{

	private int robots;

	public CIOnlineCenterAggregationEvaluationFunction(CIArguments args) {
		super(args);
		this.robots = args.getArgumentAsInt("robots");
	}

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs,
			double[] outputs) {}

	/**
	 * Sensor1=(
			classname=commoninterface.evolution.RealThymioIRCISensor,
			id=1,			
		),
		Sensor2=(
			classname=commoninterface.evolution.RealThymioInAreaASensor,
			id=2,
		),
		Sensor3=(
			classname=commoninterface.evolution.RealThymioInAreaBSensor,
			id=3,
		),
		Sensor4=(
			classname=commoninterface.evolution.RealThymioGroupAreaASensor,
			id=4,robots=3
		),
		Sensor5=(
			classname=commoninterface.evolution.RealThymioGroupAreaBSensor,
			id=5,robots=3
		)
	 */
	@Override
	//needs: inputs in [0,1], outputs in [0,1]
	public double updateEnergyLevel(double currentEnergy, RobotCI robot,
			double[] inputs, double[] outputs) {
		double contribution = 0;
		boolean inAreaA = inputs[inputs.length -4] > 0.999, inAreaB = inputs[inputs.length - 3] > 0.999;
		double othersInA = inputs[inputs.length -2], othersInB = inputs[inputs.length - 1];
		/*if(inAreaA){
			contribution = 1 + (othersInA * (robots - 1));
		}
		else if(inAreaB){
			contribution = 1 + (othersInB * (robots - 1));
		}*/
		double othersInNone = 1 - othersInA - othersInB;
		if(!inAreaA && !inAreaB){
			contribution = 1 + (othersInNone * (robots - 1));
		}
		return contribution;
	}

	@Override
	public double getDefaultEnergyValue() {
		return this.initialEnergyFactor * this.maxEnergyValue;
	}



}
