package erihanse.network;

import java.util.LinkedList;

public interface NetworkNode {
    /**
     * @return route to Home connection point for this Network Node
     */
    LinkedList<NetworkNode> getHomeRoute();

    /**
     * @return route to Target connection point for this Network Node
     */
    LinkedList<NetworkNode> getTargetRoute();

    void calculateHomeRoute();

    public int getId();

}