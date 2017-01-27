package commoninterface.evolution.odneat.eval;

import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class CIOnlineNavigationEvaluationFunction extends CIODNEATEvaluationFunction {

	public CIOnlineNavigationEvaluationFunction(CIArguments args) {
		super(args);
	}

	@Override
	public void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs) {
		
	}

	@Override
	//needs: inputs in [0,1], outputs in [0,1]
	public double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs) {
		
		//change outputs from [0,1] to [-1, 1]
		for(int i = 0; i < outputs.length; i++)
			outputs[i] = outputs[i] * 2 -1;
		
		double maxReading = 0;
		for(int i = 0; i < inputs.length; i++){
			maxReading = maxReading > inputs[i] ? maxReading : inputs[i];
		}
		/**
		 * actuation values.
		 */
		/*ThymioCI c = (ThymioCI) robot;
		double maxSpeed = c.getMaxSpeedPerSec();
		double wheelDiameter = c.getWheelDiameter();*/
		//function 1
		double ts = this.computeTransaxialSpeed2(outputs[0], outputs[1]);
		double rs = this.computeRotationalSpeed2(outputs[0], outputs[1]);

		//[0:1] fitness
		double variation = ts * rs * (1 - maxReading);

		
		//standard odneat
		//map [0:1] to [-1:1]
		/*variation = variation * 2 - 1;

		currentEnergy += variation;

		return currentEnergy;*/
		return variation;
	}


	//motion
	protected double computeRotationalSpeed(double leftWheelSpeed, double rightWheelSpeed){
		/*double pi = Math.PI;
		double maxRotationalSpeed = (wheelDiameter*pi);*/
		double left = leftWheelSpeed;///(wheelDiameter * pi);
		double right = rightWheelSpeed;///(wheelDiameter * pi);

		//left /= maxRotationalSpeed;
		//right /= maxRotationalSpeed;
		left /= 2;
		right /= 2;

		return Math.abs(left) + Math.abs(right);
	}

	protected double computeTransaxialSpeed(double leftWheelSpeed,	double rightWheelSpeed){
		//[-0.5 to 0.5] speed
		double left = leftWheelSpeed/2;
		double right = rightWheelSpeed/2;

		//[0 : 1]
		left += 0.5;
		right += 0.5;

		double diff = 1 - Math.sqrt(Math.abs(left - right));

		return diff;	
	}

	@Override
	public double getDefaultEnergyValue() {
		return this.initialEnergyFactor * this.maxEnergyValue;
	}
	
	//motion
	protected double computeRotationalSpeed2(double leftWheelSpeed, double rightWheelSpeed){
		//double pi = Math.PI;
		//double maxRotationalSpeed = maxWheelSpeed/(wheelDiameter*pi);
		double left = leftWheelSpeed;///(wheelDiameter * pi);
		double right = rightWheelSpeed;///(wheelDiameter * pi);

		//left /= maxRotationalSpeed;
		//right /= maxRotationalSpeed;
		left /= 2;
		right /= 2;

		return Math.abs(left) + Math.abs(right);
	}

	protected double computeTransaxialSpeed2(double leftWheelSpeed,	double rightWheelSpeed){
		//[-0.5 to 0.5] speed
		double left = leftWheelSpeed/2;
		double right = rightWheelSpeed/2;

		//[0 : 1]
		left += 0.5;
		right += 0.5;

		double diff = 1 - Math.sqrt(Math.abs(left - right));

		return diff;	
	}

}
