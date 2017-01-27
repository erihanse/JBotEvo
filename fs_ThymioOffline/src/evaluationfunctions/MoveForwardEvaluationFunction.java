package evaluationfunctions;

import mathutils.Vector2d;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.actuators.TwoWheelActuator;
import simulation.robot.sensors.WallRaySensor;
import simulation.util.Arguments;
import environment.MoveForwardEnvironment;
import environment.TurnSideEnvironment;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;

public class MoveForwardEvaluationFunction extends EvaluationFunction {

	protected int steps;
	protected MoveForwardEnvironment env;

	public MoveForwardEvaluationFunction(Arguments args) {
		super(args);
		steps = args.getArgumentAsIntOrSetDefault("steps", 100);
	}

	@Override
	public void update(Simulator simulator) {
		
		env = (MoveForwardEnvironment) simulator.getEnvironment();
		Robot r = env.getRobots().get(0);
		Vector2d currentPosition = r.getPosition();
	
		double totalDistance = env.getTotalDistance(0.25);
		double currentDistance = env.computeTraveledDistance(currentPosition);
		double percentage = currentDistance/totalDistance;
		fitness = percentage;
		TwoWheelActuator act = (TwoWheelActuator) r.getActuatorWithId(1);

		//System.out.println("F: " + fitness);
		if(act.getSpeed()[0] < 0 && act.getSpeed()[1] < 0){
			fitness = 0;
			simulator.stopSimulation();
		}
		if(currentDistance >= totalDistance){
			fitness = 1.0 + (steps-simulator.getTime())/steps;
			simulator.stopSimulation();
		}
	}

}
