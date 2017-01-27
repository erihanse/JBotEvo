package commoninterface.evolution;

import java.util.List;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.utils.CIArguments;

public class RealThymioIRCISensor extends CISensor {
	//public static boolean DEBUG = false;
	private ThymioCI thymio;
	private List<Short> readings;

	public RealThymioIRCISensor(int id, RobotCI robot, CIArguments args) {
		super(id, robot, args);
		thymio = (ThymioCI)robot;
	}

	@Override
	public int getNumberOfSensors() {
		return 7;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		return readings.get(sensorNumber);
	}

	@Override
	public void update(double time, Object[] entities) {
		/*if(DEBUG){
			readings = new ArrayList<Short>(this.getNumberOfSensors());
			for(int i = 0; i < this.getNumberOfSensors(); i++)
				readings.add((short) (Math.random() * 4000));
		}
		else*/
		readings = thymio.getInfraredSensorsReadings();
	}

}
