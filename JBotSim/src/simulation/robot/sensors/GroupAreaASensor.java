package simulation.robot.sensors;

import simulation.Simulator;
import simulation.physicalobjects.GroundColoredBand;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.GroundColoredBand.Edge;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class GroupAreaASensor extends Sensor {

	protected GroundColoredBand band = null;
	protected Simulator simulator;

	public GroupAreaASensor(Simulator simulator, int id, Robot robot,
			Arguments args) {
		super(simulator, id, robot, args);
		this.simulator = simulator;
	}

	public double proportionInNest() {
		//first call
		if(band == null) {
			for(PhysicalObject p : simulator.getEnvironment().getAllObjects()) {
				if(p instanceof GroundColoredBand && p.getName().equalsIgnoreCase("A"))
					band = (GroundColoredBand) p;
			}
		}
		//left right bottom top
		Edge[] edges = band.getEdges();
		double proportion = 0;
		for(Robot r : simulator.getRobots()){
			mathutils.Vector2d robotPos = r.getPosition();
			boolean inside = robotPos.x > edges[0].getP1().x && robotPos.x < edges[1].getP1().x
			&& robotPos.y > edges[2].getP1().y && robotPos.y < edges[3].getP1().y;
			if(inside)
				proportion++;
		}
		proportion /= simulator.getRobots().size();
		

		return proportion;
	}
	
	@Override
	public double getSensorReading(int sensorNumber) {
		return this.proportionInNest();
	}
}
