package environment;

import java.util.Random;

import mathutils.Vector2d;

import simulation.Simulator;
import simulation.physicalobjects.Nest;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class TurnSideEnvironment extends SimpleArenaEnvironment {

	protected final double RATIO = 1.5;
	protected final double SHORTEST_SIDE = 0.2*RATIO, LONGEST_SIDE = 1.00*RATIO;
	protected final short NUMBER_ORIENTATIONS = 4;
	protected int mazeOrientation;
	protected double initialX, initialY, centerX, centerY;

	protected double leftX, leftY, rightX, rightY;

	public static final int TURN_SIDE_LEFT = 0;
	public static final int TURN_SIDE_RIGHT = 1;

	protected double distanceToWall = SHORTEST_SIDE/2;//0.15;


	public TurnSideEnvironment(Simulator simulator, Arguments args) {
		super(simulator, args);
	}

	@Override
	public void setup(Simulator simulator){
		//
		this.width = LONGEST_SIDE;
		this.height = LONGEST_SIDE;

		Random random = simulator.getRandom();
		mazeOrientation = random.nextInt(this.NUMBER_ORIENTATIONS);
		super.setup(simulator);
		//this.setupWalls(simulator);
		this.placeRobots(simulator);
	}

	protected void placeRobots(Simulator simulator){
		Robot r = simulator.getEnvironment().getRobots().get(0);
		Vector2d position = null;
		double orientation = 0;
		switch(mazeOrientation){
		case 0:
			position = new Vector2d(0, -height/2 + distanceToWall);
			orientation = Math.toRadians(90);
			centerX = 0;
			centerY = height/2 - distanceToWall;
			break;
		case 1:
			position = new Vector2d(width/2 - distanceToWall, 0);
			orientation = Math.toRadians(180);
			centerX = -width/2 + distanceToWall;
			centerY = 0;
			break;
		case 2:
			position = new Vector2d(0, height/2 - distanceToWall);
			orientation = Math.toRadians(270);
			centerX = 0;
			centerY = -height/2 + distanceToWall;
			break;
		case 3:
			position = new Vector2d(-width/2 + distanceToWall, 0);
			orientation = Math.toRadians(0);
			centerX = width/2 - distanceToWall;
			centerY = 0;
			break;
		}
		r.moveTo(position);
		r.setOrientation(orientation);
		this.initialX = position.getX();
		this.initialY = position.getY();
		setupCorners();
		this.addObject(new Nest(simulator, "nest", centerX, centerY, 0.1));
		this.addObject(new Nest(simulator, "nest", leftX, leftY, 0.1));
		this.addObject(new Nest(simulator, "nest", rightX, rightY, 0.1));

	}

	protected void setupCorners() {
		switch(mazeOrientation){
		case 0:
			this.leftX = centerX - this.width/4;
			this.rightX = centerX + width/4;
			this.leftY = rightY = centerY;
			break;
		case 1:
			this.leftX = rightX = centerX;
			this.leftY = centerY - height/4;
			this.rightY = centerY + height/4;
			break;
		case 2:
			this.leftX = centerX + this.width/4;
			this.rightX = centerX - width/4;
			this.leftY = rightY = centerY;
			break;
		case 3:
			this.leftX = rightX = centerX;
			this.leftY = centerY + height/4;
			this.rightY = centerY - height/4;
			break;
		}
	}

	protected void setupWalls(Simulator simulator) {
		switch(mazeOrientation){
		case 0:
			createTMazeEnvironment(simulator);
			break;
		case 1:
			createLeftRotatedTMazeEnvironment(simulator);
			break;
		case 2:
			createBackRotatedTMazeEnvironment(simulator);
			break;
		case 3:
			createRightRotatedTMazeEnvironment(simulator);
			break;
		}
	}

	protected void createTMazeEnvironment(Simulator simulator) {
		//bottom.
		this.createWall(simulator, 0, -this.height/2 - this.wallThickness/2, this.SHORTEST_SIDE*2, this.wallThickness);
		//bottom left.
		this.createWall(simulator, -SHORTEST_SIDE/2 - this.wallThickness/2, 0 - SHORTEST_SIDE/2, this.wallThickness, this.LONGEST_SIDE - this.SHORTEST_SIDE);
		//bottom right
		this.createWall(simulator, SHORTEST_SIDE/2 + this.wallThickness/2, 0 - SHORTEST_SIDE/2, this.wallThickness, this.LONGEST_SIDE - this.SHORTEST_SIDE);

		//top wall
		createWall(simulator, 0, this.height/2 + wallThickness/2, width + wallThickness, wallThickness);
		//left corner
		createWall(simulator, - this.width/2 - this.wallThickness/2, this.LONGEST_SIDE/2 - this.SHORTEST_SIDE/2,
				this.wallThickness, this.SHORTEST_SIDE);
		createWall(simulator, -SHORTEST_SIDE/2 - this.wallThickness/2 - SHORTEST_SIDE, this.height/2 - SHORTEST_SIDE - this.wallThickness/2, 
				SHORTEST_SIDE*2, this.wallThickness);
		//right corner
		createWall(simulator, this.width/2 + this.wallThickness/2, this.LONGEST_SIDE/2 - this.SHORTEST_SIDE/2,
				this.wallThickness, this.SHORTEST_SIDE);
		createWall(simulator, SHORTEST_SIDE/2 + this.wallThickness/2 + SHORTEST_SIDE, this.height/2 - SHORTEST_SIDE - this.wallThickness/2, 
				SHORTEST_SIDE*2, this.wallThickness);
	}

	private void createRightRotatedTMazeEnvironment(Simulator simulator) {
		//left
		this.createWall(simulator, -this.width/2 - this.wallThickness/2, 0, this.wallThickness, this.SHORTEST_SIDE*2);
		//corridor wall at left
		this.createWall(simulator, 0 - SHORTEST_SIDE/2, -SHORTEST_SIDE/2 - this.wallThickness/2,
				this.LONGEST_SIDE - this.SHORTEST_SIDE, this.wallThickness);
		//corridor wall at right
		this.createWall(simulator, 0 - SHORTEST_SIDE/2, SHORTEST_SIDE/2 + this.wallThickness/2, 
				this.LONGEST_SIDE - this.SHORTEST_SIDE, this.wallThickness);
		//RIGHT WALL
		createWall(simulator, this.width/2 + this.wallThickness/2, 0, this.wallThickness, height + wallThickness);

		//beginning of corners 
		createWall(simulator, this.width/2 - SHORTEST_SIDE - wallThickness/2, this.width/2 - this.SHORTEST_SIDE,
				this.wallThickness, this.SHORTEST_SIDE*2);
		createWall(simulator, this.width/2 - SHORTEST_SIDE - wallThickness/2, -this.width/2 + this.SHORTEST_SIDE,
				this.wallThickness, this.SHORTEST_SIDE*2);
		//TOP corner

		createWall(simulator, this.width/2 - SHORTEST_SIDE/2, this.height/2 + this.wallThickness/2, 
				SHORTEST_SIDE, this.wallThickness);

		createWall(simulator, this.width/2 - SHORTEST_SIDE/2, -this.height/2 - this.wallThickness/2, 
				SHORTEST_SIDE, this.wallThickness);

	}

	private void createBackRotatedTMazeEnvironment(Simulator simulator) {
		//bottom.
		this.createWall(simulator, 0, -this.height/2 - this.wallThickness/2, width + wallThickness, this.wallThickness);
		//bottom left.
		this.createWall(simulator, -SHORTEST_SIDE/2 - this.wallThickness/2, 0 + SHORTEST_SIDE/2, this.wallThickness, this.LONGEST_SIDE - this.SHORTEST_SIDE);
		//bottom right
		this.createWall(simulator, SHORTEST_SIDE/2 + this.wallThickness/2, 0 + SHORTEST_SIDE/2, this.wallThickness, this.LONGEST_SIDE - this.SHORTEST_SIDE);
		//top wall
		createWall(simulator, 0, this.height/2 + wallThickness/2, SHORTEST_SIDE*2, wallThickness);
		//left corner
		createWall(simulator, - this.width/2 - this.wallThickness/2, -this.LONGEST_SIDE/2 + this.SHORTEST_SIDE/2,
				this.wallThickness, this.SHORTEST_SIDE);
		createWall(simulator, -this.width/2 + SHORTEST_SIDE, -this.height/2 + SHORTEST_SIDE + this.wallThickness/2, 
				SHORTEST_SIDE*2, this.wallThickness);
		//right corner
		createWall(simulator, this.width/2 + this.wallThickness/2, -this.LONGEST_SIDE/2 + this.SHORTEST_SIDE/2,
				this.wallThickness, this.SHORTEST_SIDE);
		createWall(simulator, this.width/2 - SHORTEST_SIDE, -this.height/2 + SHORTEST_SIDE + this.wallThickness/2, 
				SHORTEST_SIDE*2, this.wallThickness);
	}

	private void createLeftRotatedTMazeEnvironment(Simulator simulator) {
		//right
		this.createWall(simulator, this.width/2 + this.wallThickness/2, 0, this.wallThickness, this.SHORTEST_SIDE*2);
		//corridor wall at left
		this.createWall(simulator, SHORTEST_SIDE/2, -SHORTEST_SIDE/2 - this.wallThickness/2,
				this.LONGEST_SIDE - this.SHORTEST_SIDE, this.wallThickness);
		//corridor wall at right
		this.createWall(simulator, SHORTEST_SIDE/2, SHORTEST_SIDE/2 + this.wallThickness/2, 
				this.LONGEST_SIDE - this.SHORTEST_SIDE, this.wallThickness);
		//left WALL
		createWall(simulator, -this.width/2 - this.wallThickness/2, 0, this.wallThickness, height + wallThickness);

		//beginning of corners 
		createWall(simulator, -this.width/2 + SHORTEST_SIDE + wallThickness/2, this.width/2 - this.SHORTEST_SIDE,
				this.wallThickness, this.SHORTEST_SIDE*2);
		createWall(simulator, -this.width/2 + SHORTEST_SIDE + wallThickness/2, -this.width/2 + this.SHORTEST_SIDE,
				this.wallThickness, this.SHORTEST_SIDE*2);


		createWall(simulator, -this.width/2 + SHORTEST_SIDE/2, this.height/2 + this.wallThickness/2, 
				SHORTEST_SIDE, this.wallThickness);

		createWall(simulator, -this.width/2 + SHORTEST_SIDE/2, -this.height/2 - this.wallThickness/2, 
				SHORTEST_SIDE, this.wallThickness);
	}

	public int robotHasTurned(Vector2d robotPosition) {
		switch(mazeOrientation){
		case 0:
			//still in the corridor
			if(robotPosition.getY() < centerY )
				return -1;
			//not in the corridor, see in which direction the robot is turning
			//left??
			else if(robotPosition.getX() <= centerX)
				return TURN_SIDE_LEFT;
			//right
			else
				return TURN_SIDE_RIGHT;
		case 2:
			//still in the corridor
			if(robotPosition.getY() > centerY )
				return -1;
			//not in the corridor, see in which direction the robot is turning
			//left??
			else if(robotPosition.getX() >= centerX)
				return TURN_SIDE_LEFT;
			//right
			else
				return TURN_SIDE_RIGHT;
		case 1:
			//still in the corridor
			if(robotPosition.getX() > centerX)
				return -1;
			//left??
			else if(robotPosition.getY() <= centerY)
				return TURN_SIDE_LEFT;
			else
				return TURN_SIDE_RIGHT;
		case 3:
			//still in the corridor
			if(robotPosition.getX() < centerX )
				return -1;
			//left??
			else if(robotPosition.getY() >= centerY)
				return TURN_SIDE_LEFT;
			else
				return TURN_SIDE_RIGHT;
		}
		return -1;
	}

	public double getTotalDistance() {
		//the two corners are at the same distance of the center, so just use one of them
		Vector2d initial = new Vector2d(initialX, initialY), 
				center = new Vector2d(centerX, centerY), corner = new Vector2d(leftX, leftY);
		
		return initial.distanceTo(center) + center.distanceTo(corner);
	}

	public double computeTraveledDistance(Vector2d robotPosition) {
		int status = this.robotHasTurned(robotPosition);
		//not has turned
		if(status == -1){
			return robotPosition.distanceTo(new Vector2d(initialX, initialY));
		}
		double distanceTraveled = new Vector2d(initialX, initialY).distanceTo(new Vector2d(centerX, centerY));
		//has turned
		if(status == TURN_SIDE_LEFT){
			if(mazeOrientation == 0 || mazeOrientation == 2)
				return distanceTraveled + Math.abs(robotPosition.getX() - centerX);
			else
				return distanceTraveled + Math.abs(robotPosition.getY() - centerY);
		}
		else if(status == TURN_SIDE_RIGHT){
			if(mazeOrientation == 0 || mazeOrientation == 2)
				return distanceTraveled + Math.abs(robotPosition.getX() - centerX);
			else
				return distanceTraveled + Math.abs(robotPosition.getY() - centerY);
		}
		return distanceTraveled;
	}
}
