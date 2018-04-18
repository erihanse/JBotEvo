package erihanse.physicalobjects;

import java.awt.Color;
import java.util.LinkedList;

import erihanse.network.NetworkNode;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.Nest;

/**
 * HomeNest
 *  This is the original distribution point for the robots, and will be one of of the
 *  key connection points part of the ad hoc network.
 */
public class HomeNest extends Nest implements NetworkNode {
    private LinkedList<NetworkNode> homeRoute = new LinkedList<NetworkNode>();
    private LinkedList<NetworkNode> targetRoute = new LinkedList<NetworkNode>();
    Environment env;

    public HomeNest(Simulator simulator, String name, double x, double y, double radius) {
        super(simulator, name, x, y, radius);
        setColor(Color.CYAN);
        env = simulator.getEnvironment();
        homeRoute.add(this);
    }

    @Override
    public LinkedList<NetworkNode> getHomeRoute() {
        return homeRoute;
    }

    @Override
    public LinkedList<NetworkNode> getTargetRoute() {
        return targetRoute;
    }

	@Override
	public void calculateHomeRoute() {
        // TODO: ???
        return;
	}
}