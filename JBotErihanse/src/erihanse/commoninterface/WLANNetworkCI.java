package erihanse.commoninterface;

/**
 * @author eriha
 *
 */
public interface WLANNetworkCI {
	/**
	 * Retrieves the number of neighbours 
	 * @return
	 */
	public int getNumberOfNeighbours();
	public double[] getNeighboursSignalStrength();
}
