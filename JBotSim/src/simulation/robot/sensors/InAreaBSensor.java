package simulation.robot.sensors;

import java.awt.Color;

import simulation.Simulator;
import simulation.physicalobjects.GroundColoredBand;
import simulation.physicalobjects.GroundColoredBand.Edge;
import simulation.physicalobjects.PhysicalObject;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class InAreaBSensor extends InNestSensor {

	protected GroundColoredBand band = null;

	public InAreaBSensor(Simulator simulator, int id, Robot robot,
			Arguments args) {
		super(simulator, id, robot, args);
	//	System.out.println("B ID: " + id);

	}

	public boolean isInNest() {


		//first call
		if(band == null) {
			for(PhysicalObject p : simulator.getEnvironment().getAllObjects()) {
				if(p instanceof GroundColoredBand && p.getName().equalsIgnoreCase("B"))
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
