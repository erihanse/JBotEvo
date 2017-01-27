package evaluationfunctions;

import mathutils.Vector2d;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.actuators.Actuator;
import simulation.robot.actuators.TwoWheelActuator;
import simulation.util.Arguments;
import environment.TurnSideEnvironment;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;

public class TurnRightEvaluationFunction extends EvaluationFunction{

	protected TurnSideEnvironment env;
	protected int steps;

	public TurnRightEvaluationFunction(Arguments args) {
		super(args);
		steps = args.getArgumentAsIntOrSetDefault("steps", 150);
	}

	@Override
	public void update(Simulator simulator) {
		env = (TurnSideEnvironment) simulator.getEnvironment();
		Robot r = env.getRobots().get(0);
		Vector2d currentPosition = r.getPosition();

		int status = env.robotHasTurned(r.getPosition());
		TwoWheelActuator act = (TwoWheelActuator) r.getActuatorWithId(1);
		//robot turned to the wrong side.
		if(status == TurnSideEnvironment.TURN_SIDE_LEFT || r.isInvolvedInCollisonWall()
				|| act.getSpeed()[0] < 0 && act.getSpeed()[1] < 0){
			fitness = 0;
			simulator.stopSimulation();
		}
		else {
			double totalDistance = env.getTotalDistance();
			double currentDistance = env.computeTraveledDistance(currentPosition);
			double percentage = currentDistance/totalDistance;
			fitness = percentage;

			if(currentDistance >= totalDistance){
				fitness = 1 + ((steps-simulator.getTime())/steps);
				simulator.stopSimulation();
			}
		}
	}
}
