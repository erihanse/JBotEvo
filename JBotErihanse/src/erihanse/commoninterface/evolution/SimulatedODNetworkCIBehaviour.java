package erihanse.commoninterface.evolution;

import java.awt.Color;

import commoninterface.RobotCI;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineCenterAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineHomingEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineNavigationEvaluationFunction;
import commoninterface.utils.CIArguments;
import erihanse.commoninterface.evaluationfunctions.CIConnectivityEvaluationFunction;
import erihanse.robot.ODNetworkRobot;

/**
 * SimulatedODNetworkCIBehaviour
 */
public class SimulatedODNetworkCIBehaviour extends SimulatedThymioOnlineEvoControllerCIBehaviour {
    public SimulatedODNetworkCIBehaviour(CIArguments args, RobotCI robot) {
        super(args, robot);
        maturationperiod = Integer.parseInt(new CIArguments(args.getArgumentAsString("evaluation")).getArgumentAsStringOrSetDefault("maturationperiod", "500"));
    }

    @Override
    protected CIODNEATEvaluationFunction loadCIODNEATEvaluationFunction(CIArguments ciArguments) {
        if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("connectivity"))
            return new CIConnectivityEvaluationFunction(ciArguments);
        else return super.loadCIODNEATEvaluationFunction(ciArguments);
    }

    public void updateStructure(Object object) {
        super.updateStructure(object);
        ODNetworkRobot odRobot = (ODNetworkRobot) robot;
        // This gives a flashing behaviour when the robot changes controller
        odRobot.setBodyColor(Color.MAGENTA);
	}
}