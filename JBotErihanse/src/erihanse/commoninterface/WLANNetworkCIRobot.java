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
	/**
	 * Retrieves signal strength of neighbour robots
	 */
	public double[] getNeighboursSignalStrength();
}
