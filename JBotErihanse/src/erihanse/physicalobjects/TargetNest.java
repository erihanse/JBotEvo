package erihanse.physicalobjects;

import java.awt.Color;
import java.util.LinkedList;

import erihanse.network.NetworkNode;
import simulation.Simulator;
import simulation.physicalobjects.Nest;

/**
 *
 */
public class TargetNest extends Nest implements NetworkNode {
	LinkedList<NetworkNode> homeRoute = new LinkedList<NetworkNode>();
	LinkedList<NetworkNode> targetRoute = new LinkedList<NetworkNode>();

	public TargetNest(Simulator simulator, String name, double x, double y, double radius) {
		super(simulator, name, x, y, radius);
		setColor(Color.magenta);
	}

	@Override
	public LinkedList<NetworkNode> getHomeRoute() {
		return homeRoute;
	}

	@Override
	public LinkedList<NetworkNode> getSinkRoute() {
		return targetRoute;
	}

	@Override
	public void calculateHomeRoute() {
		// TODO: ???
	}
}