package erihanse.physicalobjects;

import java.awt.Color;
import java.util.LinkedList;

import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.Nest;
import simulation.robot.Robot;

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

    /**
     * TODO. Moved this to eahsimplearenaenvironment..?
     */
    public LinkedList<NetworkNode> getLongestRouteFromHome() {

		LinkedList<NetworkNode> longestRoute = new LinkedList<>();

		for (ODNetworkRobot robot : ((EAHSimpleArenaEnvironment) env).getODRobots()) {
			if (robot.getHomeRoute().size() > longestRoute.size()) {
				longestRoute = robot.getHomeRoute();
			}
		}
		return longestRoute;
	}

	@Override
	public void calculateHomeRoute() {
        // TODO: ???
        return;
	}
}