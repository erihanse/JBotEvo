package threads;

import commoninterface.network.NetworkUtils;
import commoninterface.network.messages.InformationRequest;
import commoninterface.network.messages.InformationRequest.MessageType;
//import gui.panels.UpdatePanel;
import main.RobotControlConsole;

public class OnlineEventUpdateThread extends UpdateThread {
	private String myHostname = "";
	//time in millis
	private long sleepTime;

	public OnlineEventUpdateThread(RobotControlConsole console, MessageType type, long period) {
		super(console, null, type);
		updateHostname();
		//secs to mils
		sleepTime = period * 1000;
	}

	@Override
	public void run() {

		//panel.threadWait();

		while (keepGoing) {
			console.sendData(new InformationRequest(type, myHostname));
			long timeAfterSending = System.currentTimeMillis();
			//panel.threadWait();
			calculateSleep(timeAfterSending);
		}
	}
	
	protected void calculateSleep(long timeAfterSending) {
		long timeElapsed = System.currentTimeMillis() - timeAfterSending;

		long necessarySleep = sleepTime - timeElapsed;
		try {
			if (necessarySleep > 0)
				Thread.sleep(necessarySleep);
		} catch (InterruptedException e) {
		}
	}

	private void updateHostname() {
		myHostname = NetworkUtils.getHostname();
	}
}
