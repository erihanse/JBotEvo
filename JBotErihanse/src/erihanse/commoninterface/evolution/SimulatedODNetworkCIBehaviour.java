package erihanse.commoninterface.evolution;

import commoninterface.RobotCI;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineCenterAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineHomingEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineNavigationEvaluationFunction;
import commoninterface.utils.CIArguments;
import erihanse.commoninterface.evaluationfunctions.CIConnectivityEvaluationFunction;
import erihanse.commoninterface.evaluationfunctions.CISourceHopsEvaluationFunction;
import erihanse.evaluationfunction.SourceHopsEvaluationFunction;

/**
 * SimulatedODNetworkCIBehaviour
 */
public class SimulatedODNetworkCIBehaviour extends SimulatedThymioOnlineEvoControllerCIBehaviour {
    public SimulatedODNetworkCIBehaviour(CIArguments args, RobotCI robot) {
        super(args, robot);
    }

    @Override
    protected CIODNEATEvaluationFunction loadCIODNEATEvaluationFunction(CIArguments ciArguments) {
        //	System.out.println("task: " + ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("navigation-obstacle"));
        if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("navigation-obstacle"))
            return new CIOnlineNavigationEvaluationFunction(ciArguments);
        else if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("homing"))
            return new CIOnlineHomingEvaluationFunction(ciArguments);
        else if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("aggregation"))
            return new CIOnlineAggregationEvaluationFunction(ciArguments);
        else if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("centeraggregation"))
            return new CIOnlineCenterAggregationEvaluationFunction(ciArguments);
        else if (ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("connectivity"))
            return new CIConnectivityEvaluationFunction(ciArguments);
        return null;
    }
}