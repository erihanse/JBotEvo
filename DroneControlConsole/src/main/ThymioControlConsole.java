package main;

import org.opencv.core.Core;

import commoninterface.network.messages.InformationRequest.MessageType;

import gui.RobotGUI;
import gui.ThymioGUI;
import network.ThymioConsoleMessageHandler;
import network.broadcast.ConsoleBroadcastHandler;
import threads.BehaviorMessageThread;
import threads.ConnectionThread;
import threads.OnlineEventUpdateThread;
import threads.MotorUpdateThread;
import threads.NetworkActivationsUpdateThread;
import threads.UpdateThread;
import dataObjects.ConsoleMotorSpeeds;

public class ThymioControlConsole extends RobotControlConsole {

	private ConsoleBroadcastHandler consoleBroadcastHandler;
	
	public ThymioControlConsole() {
		
		try {
			
			motorSpeeds = new ConsoleMotorSpeeds();
			
			gui = setupGUI();
			setupGamepad();
			
			consoleBroadcastHandler = new ConsoleBroadcastHandler(this);
			messageHandler = new ThymioConsoleMessageHandler(this);
			messageHandler.start();
			
			//Special case which does not depend on a connection and should only be started once
			ConnectionThread t = new ConnectionThread(this, gui.getConnectionPanel());
			t.start();
			
			addShutdownHooks();

			
			gui.setVisible(true);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void addShutdownHooks() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				disconnect();

				if (consoleBroadcastHandler != null) {
					consoleBroadcastHandler.closeConnections();
					consoleBroadcastHandler = null;
				}
			}
		});
	}

	@Override
	public void createUpdateThreads() {
		
		for(UpdateThread t : updateThreads)
			t.stopExecuting();
		
		updateThreads.clear();
		
		updateThreads.add(new UpdateThread(this, gui.getMessagesPanel(), MessageType.SYSTEM_STATUS));
		updateThreads.add(new BehaviorMessageThread(this, gui.getCommandPanel()));
		
		updateThreads.add(new UpdateThread(this, ((ThymioGUI)gui).getReadingsPanel(), MessageType.THYMIO_READINGS));
		updateThreads.add(new UpdateThread(this, ((ThymioGUI)gui).getCapturePanel(), MessageType.CAMERA_CAPTURE));
		//THYMIO-MARK
		//updateThreads.add(new UpdateThread(this, ((ThymioGUI)gui).getVirtualPositionPanel(), MessageType.THYMIO_VIRTUAL_POSITION));
		updateThreads.add(new NetworkActivationsUpdateThread(this, ((ThymioGUI)gui).getNeuralActivationsPanel(), MessageType.NEURAL_ACTIVATIONS));
		updateThreads.add(new MotorUpdateThread(this, gui.getMotorsPanel()));
		
		//fs logging stuff
		updateThreads.add(new OnlineEventUpdateThread(this, MessageType.CONTROLLER_EXECUTING, 5));
		updateThreads.add(new OnlineEventUpdateThread(this, MessageType.CONTROLLER_GENERATION, 10));
		updateThreads.add(new OnlineEventUpdateThread(this, MessageType.POPULATION_STATUS, 10));

		for(UpdateThread t : updateThreads)
			t.start();
		
	}

	@Override
	protected RobotGUI setupGUI() {
		return new ThymioGUI(this);
	}
	
	public static void main(String[] args) {
		 // Load the native library.
		 // Load the native library.
		System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.NATIVE_LIBRARY_NAME.toString());
	
		new ThymioControlConsole();
	}
	
	public void logStatus(String s){
		((ThymioGUI)gui).getLogsPanel().getBroadcastLogger().log(s);
	}
	
	@Override
	public void log(String s) {
		((ThymioGUI)gui).getLogsPanel().getIncidentLogger().log(s);
	}

	public ConsoleBroadcastHandler getConsoleBroadcastHandler() {
		return consoleBroadcastHandler;
	}
	
}
