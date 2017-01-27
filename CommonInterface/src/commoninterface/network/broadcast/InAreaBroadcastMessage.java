package commoninterface.network.broadcast;

import java.util.ArrayList;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;

public class InAreaBroadcastMessage extends BroadcastMessage {

	public static final String IDENTIFIER = "AREA";
	private final static int UPDATE_TIME = 1 * 100; // 0.1 sec
	private ThymioCI thymio;

	public InAreaBroadcastMessage(RobotCI robot) {
		super(UPDATE_TIME, IDENTIFIER);
		this.thymio = (ThymioCI) robot;
	}

	@Override
	public String getMessage() {
		ThymioOnlineEvoControllerCIBehaviour controller = 
				(ThymioOnlineEvoControllerCIBehaviour) this.thymio.getActiveBehavior();
		if(controller == null)
			return null;
		ArrayList<CISensor> sensors = this.thymio.getCISensors();
		if(sensors == null)
			return null;
		/**
		 * make sure they match
		 * Sensor2=(
			classname=commoninterface.evolution.RealThymioInAreaASensor,
			id=2,
		),
		Sensor3=(
			classname=commoninterface.evolution.RealThymioInAreaBSensor,
			id=3,
		),
		 */
		boolean inAreaA = sensors.get(1).getSensorReading(0) > 0.999, 
				inAreaB = sensors.get(2).getSensorReading(0) > 0.999;
		
		return thymio.getRobotId() + MESSAGE_SEPARATOR + inAreaA + MESSAGE_SEPARATOR + inAreaB;
	}

	@Override
	public String[] encode() {

		String[] messages = new String[1];

		String msg = getMessage();
		if(msg != null) {
			messages[0] = identifier+MESSAGE_SEPARATOR+msg;
		}

		if(messages[0] != null)
			return messages;

		return null;
	}

	public static void decode(String[] message, RobotCI robot) {
		int robotId = Integer.valueOf(message[1]);
		boolean areaA = Boolean.valueOf(message[2]);
		boolean areaB = Boolean.valueOf(message[3]);
		ThymioCI r = (ThymioCI) robot;
		r.updateRobotsPositioning(robotId, areaA, areaB);
	}

}