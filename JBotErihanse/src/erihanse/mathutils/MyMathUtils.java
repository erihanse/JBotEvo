package erihanse.mathutils;

import simulation.physicalobjects.PhysicalObject;

/**
 * MyMathUtils
 */
public class MyMathUtils {
    public static boolean inRange(PhysicalObject o1, PhysicalObject o2, double range) {
        return o1.getPosition().distanceTo(o2.getPosition()) <= range;
    }
}