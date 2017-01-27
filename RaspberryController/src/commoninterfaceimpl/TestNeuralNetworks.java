package commoninterfaceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.evolution.RealThymioIRCISensor;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.neuralnetwork.CINeuralNetwork;
import commoninterface.utils.CIArguments;



public class TestNeuralNetworks {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
		String conf = "/home/fernando/Dropbox/real-robot-experiments/DroneControlConsole/controllers/";
		conf += "preset_online_evo.conf";
		
//		HashMap<String,CIArguments> a = CIArguments.parseArgs(new String[]{conf});

		String arg = "type=(ThymioOnlineEvoControllerCIBehaviour),sensors=(Sensor1=(" +
				"classname=commoninterface.evolution.RealThymioIRCISensor,"+
				"id=1,)),network=(classname=commoninterface.neuralnetwork.MacroNetwork,inputs=(IR=("+
				"classname=commoninterface.evolution.RealThymioNormalizedCIIRNNInputs,id=1))," +
				"outputs=(TwoWheel=(classname=commoninterface.evolution.ThymioTwoWheelCINNOutput," + 
				"id=1))),task=(taskname=navigation-obstacle)";
		
	
		CIArguments cia = new CIArguments(arg,true);
		
		RealThymioCI robot = new RealThymioCI();
		ThymioOnlineEvoControllerCIBehaviour controller = 
				new ThymioOnlineEvoControllerCIBehaviour(cia, robot);
		
		controller.start();
		
		CINeuralNetwork network = controller.getNetwork();
		System.out.println("inputs: " + network.getNumberOfInputNeurons());
		System.out.println("outputs: " + network.getNumberOfOutputNeurons());
		int steps = 1000000;
		Object[] entities = robot.getEntities().toArray();
		//RealThymioIRCISensor.DEBUG = true;
		for(int i = 0; i < steps; i++){
			/*double[] inputs = new double[network.getNumberOfInputNeurons()];
			for(int j = 0; j < inputs.length; j++)
				inputs[j] = Math.random();*/
			
			for(CISensor s : robot.getCISensors()) {
				s.update(i,entities);			
			}
			

			network.controlStep(i);
			System.out.println("readings: " + Arrays.toString(network.getInputNeuronStates()));

			System.out.println("controls: " + Arrays.toString(network.getOutputNeuronStates()));
		}
	}
}
