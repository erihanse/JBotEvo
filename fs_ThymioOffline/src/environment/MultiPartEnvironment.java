package environment;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import mathutils.Vector2d;

import simulation.Simulator;
import simulation.physicalobjects.GroundColoredBand;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class MultiPartEnvironment extends SimpleArenaEnvironment {

	protected GroundColoredBand bandOne, bandTwo;
	protected Simulator simulator;
	protected boolean randomWalk = true;
	protected int randomWalkSteps = 50;
	protected int currentSteps = 0;
	protected double lastLeft, lastRight;

	public MultiPartEnvironment(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.simulator = simulator;
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
	public void setup(Simulator simulator){
		super.setup(simulator);
		this.bandOne = new GroundColoredBand(simulator, 
		//		"A", -this.width/4, -this.height/4, this.width/2, this.height/2, Color.LIGHT_GRAY);
				"A", -this.width/3, 0.0, this.width/3, this.height, Color.LIGHT_GRAY);
		this.bandTwo = new GroundColoredBand(simulator, 
				"B", +this.width/3, 0.0, this.width/3, this.height, Color.DARK_GRAY);
		//		"B", +this.width/4, +this.height/4, this.width/2, this.height/2, Color.DARK_GRAY);
		addObject(bandOne);
		addObject(bandTwo);
	}
	
	@Override
	public void update(double time) {
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
