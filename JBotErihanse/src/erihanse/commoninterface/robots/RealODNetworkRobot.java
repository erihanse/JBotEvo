package erihanse.commoninterface.robots;

import commoninterfaceimpl.RealThymioCI;
import erihanse.commoninterface.WLANNetworkCI;

public class RealODNetworkRobot extends RealThymioCI implements WLANNetworkCI {

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
