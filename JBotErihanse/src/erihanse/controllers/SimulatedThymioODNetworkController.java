package erihanse.controllers;

import commoninterface.ThymioCI;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;
import commoninterface.utils.CIArguments;
import controllers.Controller;
import erihanse.commoninterface.evolution.SimulatedODNetworkCIBehaviour;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class SimulatedThymioODNetworkController extends Controller {

	private SimulatedODNetworkCIBehaviour controller;

	public SimulatedThymioODNetworkController(Simulator simulator, Robot robot, Arguments args) {
		super(simulator, robot, args);
		controller = new SimulatedODNetworkCIBehaviour(
				new CIArguments(args.getCompleteArgumentString()), (ThymioCI) robot);
		// controller.start();
		((ThymioCI) this.robot).startBehavior(controller);
		// controller.start();
	}

	@Override
	public void controlStep(double time) {
		controller.step(time);
	}

	public SimulatedODNetworkCIBehaviour getCIController() {
		return controller;
	}

}
