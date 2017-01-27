package environment;

import java.util.ArrayList;
import java.util.Random;

import mathutils.Vector2d;
import robot.Thymio;
import simulation.Simulator;
import simulation.physicalobjects.Nest;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class SimpleArenaWithNestEnvironment extends SimpleArenaEnvironment {

	private Nest nest;
	protected double nestRadius;
	protected boolean randomWalk = true;
	protected int randomWalkSteps = 50;
	protected int currentSteps = 0;
	private Simulator simulator;

	private int environmentSteps = 0;
	protected double lastLeft, lastRight;
	protected boolean randomise;
	public SimpleArenaWithNestEnvironment(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.simulator = simulator;
		nestRadius = args.getArgumentAsDoubleOrSetDefault("nestradius", 0.10);
		randomise = args.getArgumentAsIntOrSetDefault("randomise", 0) == 1;
	}

	public void setup(Simulator simulator){
		super.setup(simulator);
		if(randomise){
			Vector2d pos = generateRandomPosition(simulator, 
					this.width - (wallThickness + nestRadius), 
					this.height - (wallThickness + nestRadius));
			nest = new Nest(simulator, "Nest", pos.x, pos.y, nestRadius);
		}
		else
			nest = new Nest(simulator, "Nest", 0.0, 0.0, nestRadius);
		addObject(nest);
	}

	protected void placeRobots(Simulator simulator) {
		Random random = simulator.getRandom();
		// place robots in a random position, with a random orientation, at the beginning of the simulation
		ArrayList<Robot> robots = this.robots;
		for(int i = 0; i < robots.size(); i++){
			Robot r = robots.get(i);
			Vector2d randomPosition = generateRandomPosition(simulator, 
					this.width - (wallThickness + r.getRadius()), 
					this.height - (wallThickness + r.getRadius()));
			r.teleportTo(randomPosition);
			//robots current orientation +- a given offset
			double orientation = r.getOrientation() + (random.nextDouble()*2-1) * this.randomizeOrientationValue;
			r.setOrientation(orientation);
		}
	}


	@Override
	public void update(double time) {
		//move to a new random location
		environmentSteps++;
		if(environmentSteps % 300 == 0){
			Vector2d pos = generateRandomPosition(simulator, 
					this.width - (wallThickness + nestRadius), 
					this.height - (wallThickness + nestRadius));
			nest.teleportTo(pos);
		}
		
		//do here to override actuator update
		if(randomWalk && currentSteps < randomWalkSteps){
			DifferentialDriveRobot robot = (DifferentialDriveRobot) this.simulator.getRobots().get(0);

			//forward
			if(currentSteps < randomWalkSteps/2){
				robot.setWheelSpeed(1.0, 1.0);
			}
			//change every second, range 0 to 1
			else if(currentSteps % 10 == 0){
				int direction = simulator.getRandom().nextInt(4);
				switch(direction){
				//forward
				case 0:
					lastLeft = 1.0;
					lastRight = 1.0;
					break;

					//backward
				case 1:
					lastLeft = -1.0;
					lastRight = -1.0;
					break;
					//left
				case 2:
					lastLeft = 0.5;
					lastRight = 1.0;
					break;
					//right
				case 3:
					lastLeft = 1.0;
					lastRight = 0.5;
					break;
				}
			}	
			robot.setWheelSpeed(lastLeft, lastRight);

		}
		currentSteps++;

	}

}
