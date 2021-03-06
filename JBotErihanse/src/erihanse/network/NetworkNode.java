package erihanse.network;

import java.util.ArrayList;
import java.util.LinkedList;

import erihanse.robot.ODNetworkRobot;
import mathutils.Vector2d;
import simulation.physicalobjects.PhysicalObject;

    public interface NetworkNode {
    /**
     * @return route to Home connection point for this Network Node
     */
    LinkedList<NetworkNode> getHomeRoute();

    /**
     * @return route to Target connection point for this Network Node
     */
    LinkedList<NetworkNode> getSinkRoute();

    void calculateHomeRoute();

    public int getId();

    public default ArrayList<ODNetworkRobot> getNeighbourRobots() {
        return null;
    };

    public Vector2d getPosition();
}