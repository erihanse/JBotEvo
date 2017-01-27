package commoninterface.network.messages;

import java.util.Arrays;


public class ThymioExecutingMessage extends Message {

	private static final long serialVersionUID = -6251588481500969630L;
	private int robotId;

	private String controller;
	private double globalStep;
	private int age;
	private double fitness;
	private double energy;
	private double[] inputs;
	private double[] outputs;

	public ThymioExecutingMessage(int robotId, String controller, double globalExecutionStep, int age, double fitness, double energy, 
			double[] controllerInputs, double[] controllerOutputs, String senderHostname) {
		super(senderHostname);
		this.robotId = robotId;
		this.controller = new String(controller);
		this.globalStep = globalExecutionStep;
		this.age = age;
		this.fitness = fitness;
		this.energy = energy;
		this.inputs = controllerInputs;
		this.outputs = controllerOutputs;
	}
	
	@Override
	public Message getCopy() {
		return new ThymioExecutingMessage(robotId, new String(controller), globalStep, age, fitness, energy, inputs.clone(), outputs.clone(), senderHostname);
	}

	@Override
	public String toString(){
		return ("C-EX" + ";" + robotId + ";"  + controller + ";" + globalStep + ";" + age + ";" + fitness + ";" + energy + ";" + 
				Arrays.toString(inputs) + ";" + Arrays.toString(outputs));
	}
	
	/**
	 * @return the robotId
	 */
	public int getRobotId() {
		return robotId;
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

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * @return the energy
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * @return the inputs
	 */
	public double[] getInputs() {
		return inputs;
	}

	/**
	 * @return the outputs
	 */
	public double[] getOutputs() {
		return outputs;
	}
	
}

