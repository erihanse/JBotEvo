package erihanse.robot;

import java.util.ArrayList;
import java.util.LinkedList;

import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.mathutils.MyMathUtils;
import erihanse.network.NetworkNode;
import erihanse.physicalobjects.HomeNest;
import robot.Thymio;
import simulation.Simulator;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

// TODO: Update classname
public class MyDifferentialDriveRobot extends Thymio implements NetworkNode {
	/**
	 * Simulated network range of this robot.
	 */
	@ArgumentsAnnotation(name = "range", defaultValue = "1", help = "Simulated signal range of the attached networking device of the robot")
	public double range = 1;
	private int sourceHops = -1;
	private int destinationHops = -1;

	private LinkedList<NetworkNode> homeRoute = new LinkedList<NetworkNode>();
	private LinkedList<NetworkNode> targetRoute = new LinkedList<NetworkNode>();

	public MyDifferentialDriveRobot(Simulator simulator, Arguments args) {
		super(simulator, args);
	}

	/**
	 * @return Number of hops away from home
	 */
	public int getSourceHops() {
		return sourceHops;
	}

	/**
	 * @param sourceHops the sourceHops to set
	 */
	public void setSourceHops(int sourceHops) {
		this.sourceHops = sourceHops;
	}

	/**
	 * @return the destinationHops
	 */
	public int getDestinationHops() {
		return destinationHops;
	}

	/**
	 * @param destinationHops the destinationHops to set
	 */
	public void setDestinationHops(int destinationHops) {
		this.destinationHops = destinationHops;
	}

	@Override
	/**
	 * Will force a recalculation of the home route for this robot. This will
	 * also force a recalculation on neighbour robots' home route.
	 */
	public LinkedList<NetworkNode> getHomeRoute() {
		return homeRoute;
	}

	@Override
	public LinkedList<NetworkNode> getTargetRoute() {
		return targetRoute;
	}

	public ArrayList<Robot> robotsInRange() {
		ArrayList<Robot> inRangeRobots = new ArrayList<>();
		for (Robot r : env.getRobots()) {
			if (MyMathUtils.inRange(this, r, range)) {
				inRangeRobots.add(r);
			}
		}
		inRangeRobots.remove(this);
		return inRangeRobots;
	}

	public void calculateHomeRoute() {
		LinkedList<NetworkNode> shortestRoute = new LinkedList<>();
		EAHSimpleArenaEnvironment se = (EAHSimpleArenaEnvironment) env;
		ArrayList<Robot> robots = env.getRobots();

		// Sometimes, se.homeNest is not set
		HomeNest homeNest = se.getHomeNest();

		// Avoid threading issue
		if (homeNest == null) {
			// System.out.println("Home nest null " + this);
			return;
		}

		// If in range of home
		if (MyMathUtils.inRange(MyDifferentialDriveRobot.this, homeNest, range)) {
			homeRoute.clear();
			homeRoute.add(se.getHomeNest());
			return;
		}

		NetworkNode node;
		// We can have several routes home
		ArrayList<LinkedList<NetworkNode>> paths = new ArrayList<>();
		// int shortestPathSize = Integer.MAX_VALUE;
		LinkedList<NetworkNode> potentialRoute;
		int shortestRouteSize = Integer.MAX_VALUE;

		// Retrieve neighbouring nodes' routes
		for (Robot robot : robotsInRange()) {
			MyDifferentialDriveRobot mr = (MyDifferentialDriveRobot) robot;

			// Skip ourself
			if (robot == this) {
				continue;
			}

			node = (NetworkNode) robot;
			LinkedList<NetworkNode> neighbourRoute = node.getHomeRoute();

			// If our neighbour doesn't know about a way home, skip it
			if (neighbourRoute.isEmpty()) {
				continue;
			}

			// If we are part of the neighbour's solution, skip it
			if (neighbourRoute.contains(this)) {
				continue;
			}

			if (neighbourRoute.contains(node)) {
				continue;
			}

			// We have found a valid route from a neighbour
			potentialRoute = neighbourRoute;
			potentialRoute.add(node);
			if (potentialRoute.size() < shortestRouteSize) {
				shortestRoute = potentialRoute;
				shortestRouteSize = shortestRoute.size();
			}
		}
		homeRoute = shortestRoute;
		return;
		// return shortestRoute;
	}
}