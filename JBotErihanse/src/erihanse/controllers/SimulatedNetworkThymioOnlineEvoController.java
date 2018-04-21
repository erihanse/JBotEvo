package erihanse.controllers;

import commoninterface.ThymioCI;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;
import commoninterface.utils.CIArguments;
import controllers.Controller;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class SimulatedNetworkThymioOnlineEvoController extends Controller{

	private SimulatedThymioOnlineEvoControllerCIBehaviour controller;
	public SimulatedNetworkThymioOnlineEvoController(Simulator simulator, Robot robot, Arguments args) {
		super(simulator, robot, args);
		controller = new SimulatedThymioOnlineEvoControllerCIBehaviour(new CIArguments(args.getCompleteArgumentString()), (ThymioCI)robot);
		//controller.start();
		((ThymioCI) this.robot).startBehavior(controller);
		//controller.start();
	}

	@Override
	public void controlStep(double time) {
		controller.step(time);
	}

	public SimulatedThymioOnlineEvoControllerCIBehaviour getCIController(){
		return controller;
	}

}
