package evaluationfunctions;

import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.sensors.InAreaASensor;
import simulation.util.Arguments;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;

public class ThymioAggregation extends EvaluationFunction {

	protected double steps, robots;
	protected int noEvalPeriod;
	public ThymioAggregation(Arguments args) {
		super(args);
		this.fitness = 0;
		this.noEvalPeriod = args.getArgumentAsIntOrSetDefault("noevalperiod", 50);
	}

	@Override
	public void update(Simulator simulator) {
		steps++;
		if(steps >= noEvalPeriod){
			this.robots = simulator.getRobots().size();
			double contribution = 0;
			for(Robot r : simulator.getRobots()){
				InAreaASensor sa = (InAreaASensor) r.getSensorByType(InAreaASensor.class);
				InAreaASensor sb = (InAreaASensor) r.getSensorByType(InAreaASensor.class);
				if(sa.isInNest() || sb.isInNest()){
					contribution += 1;
					for(Robot other : simulator.getRobots()){
						if(r != other){
							InAreaASensor otherSa = (InAreaASensor) other.getSensorByType(InAreaASensor.class);
							InAreaASensor otherSb = (InAreaASensor) other.getSensorByType(InAreaASensor.class);
							//there is another robot in the same nest
							if((sa.isInNest() && otherSa.isInNest())
									|| (sb.isInNest() && otherSb.isInNest()))
								contribution += 1;
						}
					}
				}
			}
			contribution /= simulator.getRobots().size();
			fitness += contribution;
		}
	}

	@Override
	public double getFitness() {
		double finalFit = fitness/(steps);
		return finalFit;
	}

}
