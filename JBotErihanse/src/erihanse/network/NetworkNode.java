package erihanse.network;

import java.util.LinkedList;

public interface NetworkNode {
    /**
     * Retrieves all connected neighbours' home route, by invoking their
     * getHomeRoute. This will be the shortest path to home.
     * @return route to HomeNest for this Network Node
     */
    LinkedList<NetworkNode> getHomeRoute();

    /**
     * Retrieves all connected neighbours' target route, by invoking their
     * getHomeRoute. This will be the shortest path to destination.
     * @return route to TargetNest for this Network Node
     */
    LinkedList<NetworkNode> getTargetRoute();

    void calculateHomeRoute();

}