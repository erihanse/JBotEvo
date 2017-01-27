package simulation.robot.sensors;

import java.awt.Color;
import simulation.Simulator;
import simulation.physicalobjects.GroundColoredBand;
import simulation.physicalobjects.GroundColoredBand.Edge;
import simulation.physicalobjects.PhysicalObject;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class InAreaASensor extends InNestSensor {

	protected GroundColoredBand band = null;

	public InAreaASensor(Simulator simulator, int id, Robot robot,
			Arguments args) {
		super(simulator, id, robot, args);
	//	System.out.println("A ID: " + id);
	}

	public boolean isInNest() {


		/*//first call
		if(band == null) {
			for(PhysicalObject p : simulator.getEnvironment().getAllObjects()) {
				if(p instanceof GroundColoredBand && p.getName().equalsIgnoreCase("A"))
					band = (GroundColoredBand) p;
			}
		}
		PolygonShape ps = (PolygonShape) band.shape;
		return ps.checkCollisionWithShape(robot.shape);*/

		//first call
		if(band == null) {
			for(PhysicalObject p : simulator.getEnvironment().getAllObjects()) {
				if(p instanceof GroundColoredBand && p.getName().equalsIgnoreCase("A"))
					band = (GroundColoredBand) p;
			}
		}
		//left right bottom top
		Edge[] edges = band.getEdges();
		mathutils.Vector2d robotPos = robot.getPosition();
		boolean inside = robotPos.x > edges[0].getP1().x && robotPos.x < edges[1].getP1().x
				&& robotPos.y > edges[2].getP1().y && robotPos.y < edges[3].getP1().y;

		return inside;
	}
}
