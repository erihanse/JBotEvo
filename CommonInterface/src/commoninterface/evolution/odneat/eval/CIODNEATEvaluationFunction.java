package commoninterface.evolution.odneat.eval;

import commoninterface.RobotCI;
import commoninterface.utils.CIArguments;

public abstract class CIODNEATEvaluationFunction {

	protected double maxEnergyValue;
	protected double initialEnergyFactor;
	protected double minEnergyValue;

	public CIODNEATEvaluationFunction(CIArguments args) {
		this.maxEnergyValue = args.getArgumentAsDoubleOrSetDefault("maxenergy", 100.0);
		this.initialEnergyFactor = args.getArgumentAsDoubleOrSetDefault("defaultfactor", 0.5);
		this.minEnergyValue = args.getArgumentAsDoubleOrSetDefault("minenergy", 0.0);
	}

	public abstract void updateFunctionSettings(RobotCI robot, double[] inputs, double[] outputs);

	public double limitEnergyLevel(double currentEnergy) {
		if (currentEnergy >= maxEnergyValue)
			currentEnergy = maxEnergyValue;
		else if (currentEnergy <= minEnergyValue)
			currentEnergy = minEnergyValue;
		return currentEnergy;
	}

	public double getMaxEnergyValue() {
		return this.maxEnergyValue;
	}

	public double getInitialEnergyFactor() {
		return this.initialEnergyFactor;
	}

	public abstract double updateEnergyLevel(double currentEnergy, RobotCI robot, double[] inputs, double[] outputs);

	public abstract double getDefaultEnergyValue();

	public double getMinEnergyValue() {
		return minEnergyValue;
	}

}
