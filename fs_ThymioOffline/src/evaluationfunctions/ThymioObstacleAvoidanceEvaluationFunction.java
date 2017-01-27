package evaluationfunctions;

import commoninterface.evolution.MacroNetwork;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;

import controllers.SimulatedThymioOnlineEvoController;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import evolutionaryrobotics.neuralnetworks.NeuralNetworkController;

public class ThymioObstacleAvoidanceEvaluationFunction extends EvaluationFunction {

	protected int steps = 0;
	public ThymioObstacleAvoidanceEvaluationFunction(Arguments args) {
		super(args);
		this.fitness = 0;
	}

	/*for(int i = 0; i < outputs.length; i++)
		outputs[i] = outputs[i] * 2 -1;

	double maxReading = 0;
	for(int i = 0; i < inputs.length; i++){
		maxReading = maxReading > inputs[i] ? maxReading : inputs[i];
	}
	double ts = this.computeTransaxialSpeed2(outputs[0], outputs[1]);
	double rs = this.computeRotationalSpeed2(outputs[0], outputs[1]);

	//[0:1] fitness
	double variation = ts * rs * (1 - maxReading);*/

	/*******
	 * 
	 */
	@Override
	public void update(Simulator simulator) {
		for (Robot robot : simulator.getRobots()) {
			double[] inputs = null, outputs = null;

			if(robot.getController() instanceof NeuralNetworkController){
				NeuralNetworkController controller = (NeuralNetworkController) robot.getController();
				inputs = controller.getNeuralNetwork().getInputNeuronStates();
				outputs = controller.getNeuralNetwork().getOutputNeuronStates().clone();
			}
			else if(robot.getController() instanceof SimulatedThymioOnlineEvoController){
				SimulatedThymioOnlineEvoController controller = (SimulatedThymioOnlineEvoController) robot.getController();
				MacroNetwork network = controller.getCIController().getNetwork();
			//	System.out.println("NET: " + (network == null));
				inputs = network.getInputNeuronStates();
				outputs = network.getOutputNeuronStates().clone();
			}
			for(int i = 0; i < outputs.length; i++)
				outputs[i] = outputs[i] * 2 -1;

			double maxReading = 0;
			for(int i = 0; i < inputs.length; i++){
				maxReading = maxReading > inputs[i] ? maxReading : inputs[i];
			}
			double ts = this.computeTransaxialSpeed2(outputs[0], outputs[1]);
			double rs = this.computeRotationalSpeed2(outputs[0], outputs[1]);

			//[0:1] fitness
			double variation = ts * rs * (1 - maxReading);

			fitness += variation;
			steps++;

		}
	}

	@Override
	public double getFitness() {
		double avg = fitness/steps;

		return avg;
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
