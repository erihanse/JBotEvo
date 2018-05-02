package erihanse.commoninterface;

import java.util.ArrayList;

import erihanse.commoninterface.WLANNetworkCIRobot;

/**
 * @author eriha
 *
 */
public interface WLANNetworkCIRobot {
	/**
	 * Retrieves the number of neighbours
	 * @return
	 */
	public int getNumberOfNeighbours();
	public double[] getNeighboursSignalStrength();
}
