package erihanse.robot;

import java.util.ArrayList;
import java.util.LinkedList;

import erihanse.commoninterface.WLANNetworkCIRobot;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.mathutils.MyMathUtils;
import erihanse.network.NetworkNode;
import erihanse.physicalobjects.HomeNest;
import erihanse.physicalobjects.TargetNest;
import robot.Thymio;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

// TODO: Update classname
public class ODNetworkRobot extends Thymio implements NetworkNode, WLANNetworkCIRobot {
	/**
	 * Simulated network range of this robot.
	 */
	@ArgumentsAnnotation(name = "range", defaultValue = "1", help = "Simulated signal range of the attached networking device of the robot")
	public double range = 1;
	private int sourceHops = -1;
	private int destinationHops = -1;

	private LinkedList<NetworkNode> homeRoute = new LinkedList<NetworkNode>();
	private LinkedList<NetworkNode> targetRoute = new LinkedList<NetworkNode>();

	protected Simulator simulator;

	public ODNetworkRobot(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.simulator = simulator;
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
		homeRoute.clear();
		LinkedList<NetworkNode> shortestRoute = new LinkedList<>();
		EAHSimpleArenaEnvironment se = (EAHSimpleArenaEnvironment) env;
		ArrayList<Robot> robots = env.getRobots();

		HomeNest homeNest = se.getHomeNest();

		// If in range of home
		if (inRangeOfHome()) {
			homeRoute.clear();
			homeRoute.add(se.getHomeNest());
			return;
		}

		NetworkNode neighbourNode;
		// We can have several routes home
		ArrayList<LinkedList<NetworkNode>> paths = new ArrayList<>();
		// int shortestPathSize = Integer.MAX_VALUE;
		LinkedList<NetworkNode> potentialRoute;
		int shortestRouteSize = Integer.MAX_VALUE;

		// Retrieve neighbouring nodes' routes
		for (Robot neighbourRobot : robotsInRange()) {
			ODNetworkRobot mr = (ODNetworkRobot) neighbourRobot;

			// Skip ourself
			if (neighbourRobot == this) {
				continue;
			}

			neighbourNode = (NetworkNode) neighbourRobot;
			LinkedList<NetworkNode> neighbourRoute = neighbourNode.getHomeRoute();

			// If our neighbour doesn't know about a way home, skip it
			if (neighbourRoute.isEmpty()) {
				continue;
			}

			// If we are part of the neighbour's solution, skip it
			if (neighbourRoute.contains(this)) {
				continue;
			}

			// We have found a valid route from a neighbour
			potentialRoute = (LinkedList<NetworkNode>) neighbourRoute.clone();
			potentialRoute.add(neighbourNode);
			if (potentialRoute.size() < shortestRouteSize) {
				shortestRoute = potentialRoute;
				shortestRouteSize = shortestRoute.size();
			}
		}
		homeRoute = shortestRoute;

		return;
	}

	public void calculateTargetRoute() {
		targetRoute.clear();
		LinkedList<NetworkNode> shortestRoute = new LinkedList<>();
		EAHSimpleArenaEnvironment se = (EAHSimpleArenaEnvironment) env;
		ArrayList<Robot> robots = env.getRobots();

		TargetNest targetNest = se.getTargetNest();

		// If in range of target
		if (inRangeOfTarget()) {
			targetRoute.clear();
			targetRoute.add(se.getTargetNest());
			return;
		}

		NetworkNode neighbourNode;
		// We can have several routes to target
		ArrayList<LinkedList<NetworkNode>> paths = new ArrayList<>();
		// int shortestPathSize = Integer.MAX_VALUE;
		LinkedList<NetworkNode> potentialRoute;
		int shortestRouteSize = Integer.MAX_VALUE;

		// Retrieve neighbouring nodes' routes
		for (Robot neighbourRobot : robotsInRange()) {
			ODNetworkRobot mr = (ODNetworkRobot) neighbourRobot;

			// Skip ourself
			if (neighbourRobot == this) {
				continue;
			}

			neighbourNode = (NetworkNode) neighbourRobot;
			LinkedList<NetworkNode> neighbourRoute = neighbourNode.getTargetRoute();

			// If our neighbour doesn't know about a way to target, skip it
			if (neighbourRoute.isEmpty()) {
				continue;
			}

			// If we are part of the neighbour's solution, skip it
			if (neighbourRoute.contains(this)) {
				continue;
			}

			// We have found a valid route from a neighbour
			potentialRoute = (LinkedList<NetworkNode>) neighbourRoute.clone();
			potentialRoute.add(neighbourNode);
			if (potentialRoute.size() < shortestRouteSize) {
				shortestRoute = potentialRoute;
				shortestRouteSize = shortestRoute.size();
			}
		}
		targetRoute = shortestRoute;

		return;
	}

	private boolean inRangeOfHome() {
		EAHSimpleArenaEnvironment se = (EAHSimpleArenaEnvironment) env;
		HomeNest homeNest = se.getHomeNest();
		return MyMathUtils.inRange(this, homeNest, range);
	}

	private boolean inRangeOfTarget() {
		EAHSimpleArenaEnvironment se = (EAHSimpleArenaEnvironment) env;
		TargetNest targetNest = se.getTargetNest();
		return MyMathUtils.inRange(this, targetNest, range);
	}

	@Override
	public int getNumberOfNeighbours() {
		ArrayList<String> jalla = new ArrayList<>();
		jalla.add("C");
		jalla.add("B");
		jalla.add("A");
		jalla.sort(null);
		System.out.println(jalla);
		return 0;
	}

	@Override
	public double[] getNeighboursSignalStrength() {
		// TODO Auto-generated method stub
		return null;
	}
}