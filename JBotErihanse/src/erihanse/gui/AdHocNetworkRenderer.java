package erihanse.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import gui.renderer.TwoDRendererDebug;
import mathutils.Point2d;
import mathutils.Vector2d;
import simulation.environment.Environment;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.Wall;
import simulation.physicalobjects.collisionhandling.knotsandbolts.PolygonShape;
import simulation.robot.LedState;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 * MyRenderer
 */
public class AdHocNetworkRenderer extends TwoDRendererDebug {

    Vector2d startWallPosition;
    Vector2d endWallPosition;

    public AdHocNetworkRenderer(Arguments arguments) {
        super(arguments);
    }

    @Override
    protected void drawRobot(Graphics graphics, Robot robot) {
        if (image.getWidth() != getWidth() || image.getHeight() != getHeight())
            createImage();

        int circleDiameter = bigRobots ? (int) Math.max(10, Math.round(robot.getDiameter() * scale))
                : (int) Math.round(robot.getDiameter() * scale);
        int x = transformX(robot.getPosition().getX()) - circleDiameter / 2;
        int y = transformY(robot.getPosition().getY()) - circleDiameter / 2;

        // if(robot.getId() == selectedRobot) {
        // graphics.setColor(Color.yellow);
        // graphics.fillOval(x-2, y-2, circleDiameter + 4, circleDiameter + 4);
        //
        // }
        graphics.setColor(robot.getBodyColor());
        graphics.fillOval(x, y, circleDiameter, circleDiameter);

        int avgColor = (robot.getBodyColor().getRed() + robot.getBodyColor().getGreen()
                + robot.getBodyColor().getBlue()) / 3;

        if (avgColor > 255 / 2) {
            graphics.setColor(Color.BLACK);
        } else {
            graphics.setColor(Color.WHITE);
        }

        double orientation = robot.getOrientation();
        Vector2d p0 = new Vector2d();
        Vector2d p1 = new Vector2d();
        Vector2d p2 = new Vector2d();
        p0.set(0, -robot.getRadius() / 3);
        p1.set(0, robot.getRadius() / 3);
        p2.set(6 * robot.getRadius() / 7, 0);

        p0.rotate(orientation);
        p1.rotate(orientation);
        p2.rotate(orientation);

        int[] xp = new int[3];
        int[] yp = new int[3];

        xp[0] = transformX(p0.getX() + robot.getPosition().getX());
        yp[0] = transformY(p0.getY() + robot.getPosition().getY());

        xp[1] = transformX(p1.getX() + robot.getPosition().getX());
        yp[1] = transformY(p1.getY() + robot.getPosition().getY());

        xp[2] = transformX(p2.getX() + robot.getPosition().getX());
        yp[2] = transformY(p2.getY() + robot.getPosition().getY());

        graphics.fillPolygon(xp, yp, 3);

        graphics.setColor(Color.BLACK);

        // System.out.println("Robot ID: "+robot.getId());
        // System.out.println("\n Actuators:");
        // for(Actuator act:robot.getActuators()){
        // System.out.println(act);
        // }
        // System.out.println("\n Sensors:");
        // for(Sensor sensor:robot.getSensors()){
        // System.out.println(sensor);
        // }
        // System.out.println("\n\n");

        double ledRadius = 0.015;

        p0.set(ledRadius * 3 / 2, 0);
        p0.rotate(orientation + Math.PI);

        int ledX = transformX(p0.getX() + robot.getPosition().getX() - ledRadius);
        int ledY = transformY(p0.getY() + robot.getPosition().getY() + ledRadius);

        int leadDiameter = (int) Math.round(ledRadius * 2 * scale);

        boolean paint = false;

        if (robot.getLedState() == LedState.BLINKING) {
            if (blink) {
                graphics.setColor(robot.getLedColor());
                paint = true;
                blink = false;
            } else {
                blink = true;
            }

        } else if (robot.getLedState() == LedState.ON) {
            graphics.setColor(robot.getLedColor());
            paint = true;
        }
        if (paint)
            graphics.fillOval(ledX, ledY, leadDiameter, leadDiameter);
        drawCircle(robot.getPosition(), 1);
        drawHomeConnection(graphics, robot);
        drawSinkConnection(graphics, robot);
    }

    /**
     * Draws connection between a robot and the neighbour that connects him to home.
     */
    private void drawHomeConnection(Graphics graphics, Robot robot) {
        graphics.setColor(Color.DARK_GRAY);
        ODNetworkRobot odRobot = (ODNetworkRobot) robot;
        EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

        if (eahenv.getLongestRouteFromHome().contains(odRobot)) {
            if (odRobot.getHomeRoute().size() > 0) {
                PhysicalObject homeRobot = (PhysicalObject) odRobot.getHomeRoute().getLast();
                Vector2d thisRobotPos = new Vector2d(transformX(odRobot.getPosition().getX()),
                        transformY(odRobot.getPosition().getY()));
                Vector2d wayHomeRobot = new Vector2d(transformX(homeRobot.getPosition().getX()),
                        transformY(homeRobot.getPosition().getY()));
                graphics.drawLine((int) thisRobotPos.x, (int) thisRobotPos.y, (int) wayHomeRobot.x,
                        (int) wayHomeRobot.y);
            }
        }
    }

    private void drawSinkConnection(Graphics graphics, Robot robot) {
        graphics.setColor(Color.orange);
        ODNetworkRobot odRobot = (ODNetworkRobot) robot;
        EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

        if (eahenv.getLongestRouteFromHome().contains(odRobot)) {
            if (odRobot.getSinkRoute().size() > 0) {
                PhysicalObject homeRobot = (PhysicalObject) odRobot.getSinkRoute().getLast();
                Vector2d thisRobotPos = new Vector2d(transformX(odRobot.getPosition().getX()),
                        transformY(odRobot.getPosition().getY()));
                Vector2d wayHomeRobot = new Vector2d(transformX(homeRobot.getPosition().getX()),
                        transformY(homeRobot.getPosition().getY()));
                graphics.drawLine((int) thisRobotPos.x, (int) thisRobotPos.y, (int) wayHomeRobot.x,
                        (int) wayHomeRobot.y);
            }
        }
    }

    @Override
    public void drawCircle(Point2d center, double radius) {
        int circleDiameter = (int) Math.round(0.5 + 2 * radius * scale);
        int x = transformX(center.getX()) - circleDiameter / 2;
        int y = transformY(center.getY()) - circleDiameter / 2;

        graphics.setColor(Color.LIGHT_GRAY);

        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
        g2d.drawOval(x, y, circleDiameter, circleDiameter);
        graphics.setColor(Color.BLACK);
    }

    @Override
    public void drawWall(Wall w) {
        graphics.setColor(Color.RED);
        PolygonShape s = (PolygonShape) w.shape;
        Polygon p = s.getPolygon();

        int[] xs = p.xpoints.clone();
        int[] ys = p.ypoints.clone();

        for (int i = 0; i < xs.length; i++) {
            double x = xs[i] / 10000.0;
            double y = ys[i] / 10000.0;
            xs[i] = transformX(x);
            ys[i] = transformY(y);
        }

        Polygon e2 = new Polygon(xs, ys, 4);
        Graphics2D g2 = (Graphics2D) graphics;

        g2.draw(e2);
        update(g2);
        // g2.draw(
        // new Polygon(new int[] { 0, 1, 2, 3}, new int[] {1,2,3,4}, 4)
        // );
    }

    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            startWallPosition = new Vector2d(e.getX(), e.getY());
        }
    }

    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            endWallPosition = new Vector2d(e.getX(), e.getY());

            EAHSimpleArenaEnvironment env = (EAHSimpleArenaEnvironment) simulator.getEnvironment();
            double wallThickness = env.getWallThickness();
            // Wall v = env.createWall(simulator, startWallPosition, endWallPosition, 50);

            System.out.println(env.getAllObjects().get(3));
            for (PhysicalObject po : env.getAllObjects()) {
                if (po instanceof Wall) {
                    System.out.println(po);
                }
            }

            repaint();

            // graphics.drawPolyline(xpoints.stream().mapToInt(Integer::valueOf).toArray(),
            // ypoints.stream().mapToInt(Integer::valueOf).toArray(), xpoints.size());
            // drawMarker(graphics, new Marker(simulator, "name", 1, -1, -1, 0, 1,
            // Color.cyan));
        }
    }
}