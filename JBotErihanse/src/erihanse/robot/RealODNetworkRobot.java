package erihanse.robot;

import java.util.ArrayList;

import commoninterfaceimpl.RealThymioCI;
import erihanse.commoninterface.WLANNetworkCIRobot;

/**
 * Future work
 */
public class RealODNetworkRobot extends RealThymioCI implements WLANNetworkCIRobot {

	public RealODNetworkRobot() {

	}

	@Override
	public int getNumberOfNeighbours() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getNeighboursSignalStrength() {
		// TODO Auto-generated method stub
		return null;
	}
}
