package erihanse.robot;

import java.util.ArrayList;
import java.util.LinkedList;
import commoninterface.CISensor;
import commoninterface.neat.utils.MathUtils;
import erihanse.commoninterface.WLANNetworkCIRobot;
import erihanse.commoninterface.sensors.DistanceTravelledSimSensor;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.mathutils.MyMathUtils;
import erihanse.network.NetworkNode;
import erihanse.physicalobjects.HomeNest;
import erihanse.physicalobjects.TargetNest;
import robot.Thymio;
import sensors.CISensorWrapper;
import simulation.Simulator;
import simulation.physicalobjects.PhysicalObject;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class ODNetworkRobot extends Thymio implements NetworkNode, WLANNetworkCIRobot {
	/**
	 * Simulated network range of this robot.
	 */
	@ArgumentsAnnotation(name = "range", defaultValue = "1", help = "Simulated signal range of the attached networking device of the robot")
	private double range = 1;
	private int sourceHops = -1;
	private int destinationHops = -1;

	protected LinkedList<NetworkNode> homeRoute = new LinkedList<NetworkNode>();
	protected LinkedList<NetworkNode> targetRoute = new LinkedList<NetworkNode>();
	protected EAHSimpleArenaEnvironment eahenv;
	protected Simulator simulator;

	public ODNetworkRobot(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.simulator = simulator;
		this.eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
	}

	public double getRange() {
		return range;
	}

	/**
	 * @return Number of hops away from home
	 */
	public int getSourceHops() {
		return sourceHops;
	}

	/**
	 * @param sourceHops
	 *                       the sourceHops to set
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
	 * @param destinationHops
	 *                            the destinationHops to set
	 */
	public void setDestinationHops(int destinationHops) {
		this.destinationHops = destinationHops;
	}

	@Override
	public LinkedList<NetworkNode> getHomeRoute() {
		return homeRoute;
	}

	@Override
	public LinkedList<NetworkNode> getSinkRoute() {
		return targetRoute;
	}

	public ArrayList<ODNetworkRobot> robotsInRange() {
		ArrayList<ODNetworkRobot> inRangeRobots = new ArrayList<>();
		for (Robot r : env.getRobots()) {
			if (MyMathUtils.inRange(this, r, range)) {
				inRangeRobots.add((ODNetworkRobot) r);
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

		TargetNest targetNest = se.getSinkNest();

		// If in range of target
		if (inRangeOfTarget()) {
			targetRoute.clear();
			targetRoute.add(se.getSinkNest());
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
			LinkedList<NetworkNode> neighbourRoute = neighbourNode.getSinkRoute();

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
		TargetNest targetNest = se.getSinkNest();
		return MyMathUtils.inRange(this, targetNest, range);
	}

	@Override
	public int getNumberOfNeighbours() {
		return robotsInRange().size();
	}

	@Override
	public double[] getNeighboursSignalStrength() {
		return robotsInRange().stream()
			.mapToDouble(n -> this.getPosition().distanceTo(n.getPosition()))
			.toArray();
	}

	@Override
	public int getId() {
		return getRobotId();
	}

	@Override
	public void shutdown() {

	}

	public CISensor getCISensorByType(Class sensorClass) {
		return sensors
			.stream()
			.filter(CISensorWrapper.class::isInstance)
			.map(CISensorWrapper.class::cast)
			.map(s -> s.getCisensor())
			.filter(sensorClass::isInstance)
			.findFirst()
			.orElse(null);
	}
}