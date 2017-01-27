package commoninterface.evolution;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class RealThymioInNestSensor extends CISensor {
	//public static boolean DEBUG = false;
	private ThymioCI thymio;
	private List<Short> readings;

	private double inNest;
	protected final double minValue = 100;
	
	public RealThymioInNestSensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		thymio = (ThymioCI)robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 1;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return inNest;
	}
	
	/*********
	 * prox.ground.ambiant : ambient light intensity at the ground, varies between 0 (no light) and 1023 (maximum light)
	prox.ground.reflected : amount of light received when the sensor emits infrared, varies between 0 (no reflected light) and 1023 (maximum reflected light)
	prox.ground.delta : difference between reflected light and ambient light, linked to the distance and to the ground colour.
	 */

	@Override
	public void update(double time, Object[] entities) {
		readings = thymio.getGroundReflectedLightReadings();
		double mean = (readings.get(0) + readings.get(1))/2;
		if(mean <= minValue){
			inNest = 1.0;
		}
		else {
			inNest = 0.0;
		}
		//System.out.println("innest says: " + readings.get(0) + " ; " + readings.get(1) + ";" + inNest);
	}

}
