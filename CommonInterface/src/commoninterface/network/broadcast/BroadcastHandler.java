package commoninterface.network.broadcast;

import java.util.ArrayList;
import java.util.Iterator;

import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.entities.Entity;
import commoninterface.entities.RobotLocation;
import commoninterface.entities.SharedDroneLocation;
import commoninterface.entities.ThymioSharedEntity;
import commoninterface.evolution.ODNEATGenome;

public abstract class BroadcastHandler {

	public static int PORT = 8888;
	protected static boolean DEBUG = false;
	protected static long CLEAN_ENTITIES_TIME = 100; //100 timesteps == 10 seconds
	protected ArrayList<BroadcastMessage> broadcastMessages;

	protected RobotCI robot;

	public BroadcastHandler(RobotCI drone, ArrayList<BroadcastMessage> broadcastMessages) {
		this.robot = drone;
		this.broadcastMessages = broadcastMessages;
	}

	public abstract void sendMessage(String message);

	public void messageReceived(String address, String message) {
		if(address.equals(robot.getNetworkAddress()))
			return;
		String[] spl = message.split(BroadcastMessage.MESSAGE_SEPARATOR);
		String identifier = spl[0];
		//System.out.println("identifier: " + identifier);

		switch(identifier) {
		case HeartbeatBroadcastMessage.IDENTIFIER:
			//do nothing :D
			break;
		case PositionBroadcastMessage.IDENTIFIER:

			RobotLocation dl = PositionBroadcastMessage.decode(message);
			dl.setTimestepReceived((long)(robot.getTimeSinceStart() * 10));

			robot.replaceEntity(dl);

			if(DEBUG)
				System.out.println("Added DroneLocation "+dl);
			break;
		case VirtualPositionBroadcastMessage.IDENTIFIER:
			VirtualPositionBroadcastMessage.decode(address, message, (ThymioCI)robot);
			break;
		case SharedDroneBroadcastMessage.IDENTIFIER:

			SharedDroneLocation location = SharedDroneBroadcastMessage.decode(address, message);
			location.setTimestepReceived((long)(robot.getTimeSinceStart() * 10));

			robot.replaceEntity(location);
			if(DEBUG)
				System.out.println("Added SharedDroneLocation "+location);

			break;
		case SharedThymioBroadcastMessage.IDENTIFIER:
			ThymioSharedEntity tse = SharedThymioBroadcastMessage.decode(message);
			tse.setTimestepReceived((long)(robot.getTimeSinceStart() * 10));
			robot.replaceEntity(tse);
			if(DEBUG)
				System.out.println("Added ThymioSharedEntity "+tse);

			break;

		case ODNEATGenomeBroadcastMessage.IDENTIFIER:
			//System.out.println("broadcast handler got one");
			ODNEATGenomeBroadcastMessage.decode(spl, robot);
			break;
		case InAreaBroadcastMessage.IDENTIFIER:
			InAreaBroadcastMessage.decode(spl, robot);
			break;
		}
	}

	public void update(double timestep) {
		cleanupEntities(timestep);
	}

	protected void cleanupEntities(double timestep) {
		synchronized(robot.getEntities()) {
			Iterator<Entity> i = robot.getEntities().iterator();

			while(i.hasNext()) {
				Entity e = i.next();
				if(e instanceof RobotLocation || e instanceof SharedDroneLocation) {
					if(timestep - e.getTimestepReceived() > CLEAN_ENTITIES_TIME) {
						i.remove();
						if(DEBUG)
							System.out.println("Removed Entity during cleanup");
					}
				}
			}
		}
	}

	public RobotCI getDrone() {
		return robot;
	}
}