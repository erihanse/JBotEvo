package sensors;

import java.util.ArrayList;
import java.util.Random;

import simulation.Simulator;
import simulation.physicalobjects.ClosePhysicalObjects;
import simulation.physicalobjects.ClosePhysicalObjects.CloseObjectIterator;
import simulation.physicalobjects.Nest;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.checkers.AllowNestChecker;
import simulation.robot.Robot;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;

public class ThymioNestSensor extends Sensor {

	//0.5 cm
	private static final double THYMIO_DIST_GROUNDSENSOR = 0.005;
	private ClosePhysicalObjects closeObjects;
	private Simulator simulator;
	protected double range;
	protected boolean noiseEnabled, naiveSensor;
	protected Random random;
	protected final double NOISESTDEV = 0.05f;

	static final double[] REAL_READINGS = {83.5625,
		107.45, 115.15, 127.35, 140.65, 157, 176, 196.65, 219.8, 248.3, 278.95, 329.05, 388.45,
		457.15, 510.25, 560.3, 616.25, 677.25, 756.5, 820.55, 865.75, 882.65, 895.75,
		909.85, 923.1, 935.35
	};

	static final double[] REAL_DEV = {9.2805800824,
		17.6052474235, 18.1057565722, 20.7371393246, 24.2623273063, 28.0769618989,
		30.6491177859, 33.6597135429, 36.8061779711, 42.1140056713, 46.3777112982,
		57.4533907203, 67.6636223496, 70.5335120052, 70.3209746355, 75.3602575702,
		86.3584177471, 101.5458802191, 105.8432306465, 98.9553195294, 84.8049619948,
		73.919070255, 63.1738411886, 52.9411540248, 42.1312485605, 32.5839401837
	};
	

	static final double[] DISTANCES = {0,
		1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};



	public ThymioNestSensor(Simulator simulator, int id, Robot robot, Arguments args) {
		super(simulator, id, robot, args);
		this.simulator = simulator;
		this.range = args.getArgumentAsDoubleOrSetDefault("range", 0.25);
		this.noiseEnabled = args.getArgumentAsInt("noise") == 1;
		this.naiveSensor = args.getArgumentAsInt("naivesensor") == 1;
		this.random = simulator.getRandom();
	}

	public double readingNest() {

		if(closeObjects == null) {
			//first call
			double radius = 0;
			for(PhysicalObject p : simulator.getEnvironment().getAllObjects()) {
				if(p instanceof Nest)
					radius = Math.max(radius, p.getRadius());
			}

			this.closeObjects = new ClosePhysicalObjects(simulator.getEnvironment(),
					radius,
					new AllowNestChecker());
		}

		CloseObjectIterator nestIterator = closeObjects.iterator();

		while (nestIterator.hasNext()) {
			PhysicalObject nest = nestIterator.next().getObject();
			//distancia entre centros
			double distance = nest.getPosition().distanceTo(robot.getPosition());
			distance = distance - (robot.getRadius() - THYMIO_DIST_GROUNDSENSOR) - nest.getRadius();
			if(distance > range){
				//System.out.println("distance; " + distance);
				return 0.0;
			}
			if(naiveSensor){
				//	System.out.println("cjecking");

				//double reading = distance/getRange();
				double reading = (range - distance)/range;
				if(this.noiseEnabled){
					reading += random.nextGaussian()*NOISESTDEV * reading;
				}
				if(reading > 1.0)
					reading = 1.0;
				else if(reading < 0.0)
					reading = 0.0;
				return reading;
			}
			else {
				distance *= 100;// 0.01 in the simulator is 1cm. We use the value 1 as 1cm
				if (distance <= DISTANCES[0])
					return 1.0;
				for (int i = 1; i < DISTANCES.length; i++) {
					if (distance <= DISTANCES[i]) {
						double distanceBefore = DISTANCES[i - 1];
						double distanceAfter = DISTANCES[i];

						double linearization = (distance - distanceBefore) / (distanceAfter - distanceBefore);

						double sensorReferenceAfter = REAL_READINGS[i];
						double sensorReferenceBefore = REAL_READINGS[i - 1];
						//porque mais perto = leituras mais baixas
						double diff = sensorReferenceAfter - sensorReferenceBefore;
						double currentSensorValue = diff * (linearization) + sensorReferenceBefore;

						//if (noiseEnabled)
						//	currentSensorValue += random.nextGaussian() * REAL_DEV[i];

						double minCutoff = REAL_READINGS[0], 
								maxCutoff = REAL_READINGS[REAL_READINGS.length - 1];
						//APPLY THRESHOLDS
						if(currentSensorValue < minCutoff)
							currentSensorValue = minCutoff;
						else if(currentSensorValue > maxCutoff)
							currentSensorValue = maxCutoff;
						
						//normalize
						double normalised = (currentSensorValue - minCutoff)/(maxCutoff-minCutoff);
						/**
						 * normalised == 0 equals in the nest
						 * normalised == 1, not in range
						 * 
						 *  so, flip value
						 */
						
						normalised = 1 - normalised;
						
						return normalised;
					}
				}

			}
		}
		return 0.0;

	}

	@Override
	public void update(double time, ArrayList<PhysicalObject> teleported) {
		if(closeObjects != null)
			closeObjects.update(time, teleported);
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return readingNest();
	}

	@Override
	public String toString() {
		return "ThymioNestSensor [" + readingNest() + "]";
	}
}