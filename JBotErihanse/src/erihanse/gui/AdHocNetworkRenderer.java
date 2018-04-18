package erihanse.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

import erihanse.environment.EAHSimpleArenaEnvironment;
import gui.renderer.TwoDRendererDebug;
import mathutils.Point2d;
import mathutils.Vector2d;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.Wall;
import simulation.physicalobjects.collisionhandling.knotsandbolts.PolygonShape;
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
        super.drawRobot(graphics, robot);
        drawCircle(robot.getPosition(), 1);
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
        //     new Polygon(new int[] { 0, 1, 2, 3}, new int[] {1,2,3,4}, 4)
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
                if(po instanceof Wall) {
                    System.out.println(po);
                }
            }

            repaint();

            // graphics.drawPolyline(xpoints.stream().mapToInt(Integer::valueOf).toArray(),
            //         ypoints.stream().mapToInt(Integer::valueOf).toArray(), xpoints.size());
            // drawMarker(graphics, new Marker(simulator, "name", 1, -1, -1, 0, 1, Color.cyan));
        }
    }
}