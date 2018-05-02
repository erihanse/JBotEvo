package erihanse.commoninterface.robots;

import java.util.ArrayList;

import commoninterfaceimpl.RealThymioCI;
import erihanse.commoninterface.WLANNetworkCIRobot;

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
