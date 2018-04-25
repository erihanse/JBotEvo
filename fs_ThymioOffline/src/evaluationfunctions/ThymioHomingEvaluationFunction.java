package evaluationfunctions;

import actuator.StopActuator;
import sensors.ThymioNestSensor;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.sensors.InNestSensor;
import simulation.util.Arguments;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;

public class ThymioHomingEvaluationFunction extends EvaluationFunction {

	protected int steps;
	protected int noEvalPeriod = 0;

	public ThymioHomingEvaluationFunction(Arguments args) {
		super(args);
		this.fitness = 0;
		this.noEvalPeriod = args.getArgumentAsIntOrSetDefault("noevalperiod", 50);
	}

	@Override
	public void update(Simulator simulator) {
		steps++;
		if(steps >= noEvalPeriod){
			for (Robot robot : simulator.getRobots()) {
				InNestSensor s = (InNestSensor) robot.getSensorByType(InNestSensor.class);
				ThymioNestSensor nest = (ThymioNestSensor) robot.getSensorByType(ThymioNestSensor.class);
				if(s.isInNest()){
					fitness += 1.0;
					//System.out.println("IN NEST!");
				}
				else {
					double reading = nest.readingNest();

					fitness += reading;
				}
			}
		}
	}

	@Override
	public double getFitness() {
		double finalFit = fitness/(steps - noEvalPeriod);
		return finalFit;
	}
}