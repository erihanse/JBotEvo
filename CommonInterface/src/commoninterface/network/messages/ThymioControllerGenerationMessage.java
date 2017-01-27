package commoninterface.network.messages;

import java.util.Arrays;


public class ThymioControllerGenerationMessage extends Message {

	private static final long serialVersionUID = -6251588481500969630L;
	private int robotId;
	//link-based net description (from, to, weight)
	private double[] netDescription;
	private String controller;
	private double globalStep;

	public ThymioControllerGenerationMessage(int robotId, 
			String controller, double globalExecutionStep, 
			double[] netDescription, String senderHostname) {
		super(senderHostname);
		this.robotId = robotId;
		this.controller = new String(controller);
		this.netDescription = netDescription;
		this.globalStep = globalExecutionStep;
	}
	
	@Override
	public Message getCopy() {
		return new ThymioControllerGenerationMessage(robotId, new String(controller), globalStep, netDescription.clone(), senderHostname);
	}
	
	@Override
	public String toString(){
		return ("C-GE;" + robotId + ";" + controller + ";" + 
	globalStep + ";" + Arrays.toString(netDescription));
	}

	/**
	 * @return the robotId
	 */
	public int getRobotId() {
		return robotId;
	}

	/**
	 * @return the netDescription
	 */
	public double[] getNetDescription() {
		return netDescription;
	}

	/**
	 * @return the controller
	 */
	public String getController() {
		return controller;
	}

	/**
	 * @return the globalStep
	 */
	public double getGlobalStep() {
		return globalStep;
	}

}
