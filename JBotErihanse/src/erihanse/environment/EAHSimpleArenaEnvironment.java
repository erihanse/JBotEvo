package erihanse.environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

import erihanse.commoninterface.sensors.DistanceTravelledCISensor;
import erihanse.network.NetworkNode;
import erihanse.physicalobjects.HomeNest;
import erihanse.physicalobjects.TargetNest;
import erihanse.robot.ODNetworkRobot;
import mathutils.Vector2d;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.Nest;
import simulation.physicalobjects.PhysicalObjectType;
import simulation.physicalobjects.Wall;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class EAHSimpleArenaEnvironment extends Environment {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected double wallThickness = 0.1;
	protected double randomizeOrientationValue = 6.28;
	protected TargetNest targetNest;
	protected HomeNest homeNest;
	protected double totalDistanceTravelled;
	protected boolean distanceSensor;

	public EAHSimpleArenaEnvironment(Simulator simulator, Arguments args) {
		super(simulator, args);
		// setupWalls(simulator, args);
	}

	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		this.setupWalls(simulator);

		targetNest = new TargetNest(simulator, "Source", getWidth() / 2, getHeight() / 2, 1);
		homeNest = new HomeNest(simulator, "Source", -getWidth() / 2, -getHeight() / 2, 1);
		placeHomeNest(simulator);
		placeTargetNest(simulator);
		placeRobots(simulator);
		// robots.get(0).teleportTo(new Vector2d(-1.5, -1.5));
		// robots.get(1).teleportTo(new Vector2d(-1,-1));
		// robots.get(2).teleportTo(new Vector2d(-0.5,-0.5));
		// ((MyDifferentialDriveRobot) robots.get(0)).getHomeRoute();
	}

	private void placeTargetNest(Simulator simulator) {
		this.addObject(new TargetNest(simulator, "Source", getWidth() / 2, getHeight() / 2, 1));
	}

	private void placeHomeNest(Simulator simulator) {
		this.addObject(new HomeNest(simulator, "Source", -getWidth() / 2, -getHeight() / 2, 1));
	}

	public HomeNest getHomeNest() {
		return homeNest;
	}

	public TargetNest getSinkNest() {
		return targetNest;
	}

	protected void setupWalls(Simulator simulator) {
		createHorizontalWalls(simulator);
		createVerticalWalls(simulator);
	}

	/**
	 * Places the robots close to the original distribution area.
	 */
	protected void placeRobots(Simulator simulator) {
		Random random = simulator.getRandom();
		// place robots in a random position, with a random orientation, at the
		// beginning of the simulation
		ArrayList<Robot> robots = this.robots;
		double maxDisperseValue = (homeNest.getRadius() - wallThickness) / Math.sqrt(2);
		for (int i = 0; i < robots.size(); i++) {
			Robot r = robots.get(i);
			Vector2d distributionPoint = homeNest.getPosition();

			double x = distributionPoint.x + (random.nextDouble() * maxDisperseValue + wallThickness + 0.5);
			double y = distributionPoint.y + (random.nextDouble() * maxDisperseValue + wallThickness + 0.5);

			r.teleportTo(new Vector2d(x, y));
			// robots current orientation +- a given offset
			double orientation = r.getOrientation() + (random.nextDouble() * 2 - 1) * this.randomizeOrientationValue;
			r.setOrientation(orientation);
		}
	}

	@Override
	public void update(double time) {
		for (Robot r : getRobots()) {
			ODNetworkRobot mr = (ODNetworkRobot) r;
			mr.calculateHomeRoute();
			mr.calculateTargetRoute();
		}
	}

	protected Wall createWall(Simulator simulator, double x, double y, double width, double height) {
		Wall w = new Wall(simulator, "wall", x, y, Math.PI, 1, 1, 0, width, height, PhysicalObjectType.WALL);
		this.addObject(w);

		return w;
	}

	private void createHorizontalWalls(Simulator simulator) {
		createWall(simulator, 0, this.height / 2 + wallThickness / 2, width + wallThickness, wallThickness);
		createWall(simulator, 0, -this.height / 2 - wallThickness / 2, width + wallThickness, wallThickness);
	}

	private void createVerticalWalls(Simulator simulator) {
		createWall(simulator, -this.width / 2 - wallThickness / 2, 0, wallThickness, height + wallThickness);
		createWall(simulator, width / 2 + wallThickness / 2, 0, wallThickness, height + wallThickness);
	}

	public double getWallThickness() {
		return wallThickness;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ODNetworkRobot> getODRobots() {

		ArrayList<? extends Robot> robots = getRobots();
		ArrayList<ODNetworkRobot> odrobots;

		odrobots = (ArrayList<ODNetworkRobot>) robots;

		return odrobots;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<NetworkNode> getLongestRouteFromHome() {

		LinkedList<NetworkNode> longestRoute = new LinkedList<>();
		longestRoute.add(homeNest);

		for (ODNetworkRobot robot : getODRobots()) {
			if (robot.getHomeRoute().size() + 1 > longestRoute.size()) {
				longestRoute = (LinkedList<NetworkNode>) robot.getHomeRoute().clone();
				longestRoute.add(robot);
			}
		}
		return longestRoute;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<NetworkNode> getLongestRouteFromSink() {
		LinkedList<NetworkNode> longestRoute = new LinkedList<>();
		longestRoute.add(targetNest);

		for (ODNetworkRobot robot : getODRobots()) {
			if (robot.getSinkRoute().size() > longestRoute.size()) {
				longestRoute = (LinkedList<NetworkNode>) robot.getSinkRoute().clone();
				longestRoute.add(robot);
			}
		}
		return longestRoute;
	}

	public double getTotalDistanceTravelled() {
		// If all robots have the distance travelled sensor
		if (getODRobots().stream().allMatch(s -> s.getCISensorByType(DistanceTravelledCISensor.class) != null)) {
			// Sum the sensor readings of the distances travelled
			return getODRobots()
				.stream()
				.map(s -> s.getCISensorByType(DistanceTravelledCISensor.class).getSensorReading(0))
				.collect(Collectors.summingDouble(Double::doubleValue));
		} else {
			return 0;
		}
	}
}
