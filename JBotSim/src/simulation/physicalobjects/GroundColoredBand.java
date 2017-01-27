package simulation.physicalobjects;

import java.awt.Color;
import java.io.Serializable;

import mathutils.MathUtils;
import mathutils.Vector2d;

import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
import simulation.physicalobjects.collisionhandling.knotsandbolts.PolygonShape;

public class GroundColoredBand extends PhysicalObject {

	private Edge left, right, top, bottom;
	private Edge[] edges;


	private Color color;
	private double width;
	private double height;

	public GroundColoredBand(Simulator simulator,  String name, double x, double y, double width, double height, Color color) {
		super(simulator, name, x, y, 0, 0, PhysicalObjectType.GROUND_COLORED_BAND);
		this.width = width;
		this.height = height;
		this.color = color;
		this.setPosition(new Vector2d(x,y));
		initializeEdges();
		edges = getEdges();
		//this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 2 * width, width);

		this.defineShape(simulator);
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		return color;
	}

	public Edge[] getEdges() {
		return new Edge[]{left, right, bottom, top};
	}

	protected void defineShape(Simulator simulator) {

		double[] xs = new double[4];
		xs[0] = edges[3].getP1().x;
		xs[1] = edges[1].getP1().x;
		xs[2] = edges[2].getP1().x;
		xs[3] = edges[0].getP1().x;

		double[] ys = new double[4];
		ys[0] = edges[3].getP1().y;
		ys[1] = edges[1].getP1().y;
		ys[2] = edges[2].getP1().y;
		ys[3] = edges[0].getP1().y;

		this.shape = new PolygonShape(simulator, name, this, 0, 0, 0, xs, ys);
	}

	protected void initializeEdges() {
		Vector2d topLeft = new Vector2d(getTopLeftX(), getTopLeftY()),
				topRight = new Vector2d(getTopLeftX() + getWidth(), getTopLeftY()),
				bottomLeft = new Vector2d(getTopLeftX(), getTopLeftY() - getHeight()),
				bottomRight = new Vector2d(getTopLeftX() + getWidth(), 
						getTopLeftY() - getHeight());
		top    = new Edge(topLeft, topRight);
		right  = new Edge(topRight, bottomRight);
		left   = new Edge(bottomLeft, topLeft);
		bottom = new Edge(bottomRight, bottomLeft);
	}

	public double getTopLeftX() {
		return getPosition().getX() - width/2;
	}

	public double getTopLeftY(){
		return getPosition().getY() + height/2;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/********
	 * EDGE
	 */

	public class Edge implements Serializable{

		private Vector2d p1, p2;
		private Vector2d normal;

		public Edge(Vector2d p1, Vector2d p2){
			this.p1 = p1;
			this.p2 = p2;
			this.normal = new Vector2d();		
			this.normal.x = -(p2.y-p1.y);
			this.normal.y = (p2.x-p1.x);
		}

		public Vector2d getP1() {
			return p1;
		}

		public Vector2d getP2(){
			return p2;
		}

		public Vector2d getNormal() {
			return normal;
		}
	}

	public Vector2d intersectsWithLineSegment(Vector2d p1, Vector2d p2) {
		Vector2d closestPoint      = null;
		Vector2d lineSegmentVector = new Vector2d(p2);
		lineSegmentVector.sub(p1);

		for (Edge e : edges) {
			double dot = e.getNormal().dot(lineSegmentVector);
			if (dot < 0) {
				Vector2d e1 = e.getP1();
				Vector2d e2 = e.getP2();
				closestPoint = MathUtils.intersectLines(p1, p2, e1, e2);
				if(closestPoint != null) {
					break;
				}
			}
		}
		return closestPoint;
	}

	public Vector2d intersectsWithLineSegment(Vector2d p1, Vector2d p2, double maxReflectionAngle) {
		Vector2d closestPoint      = null;
		Vector2d lineSegmentVector = new Vector2d(p2);
		lineSegmentVector.sub(p1);

		for (Edge e : edges) {
			//			double dot = e.getNormal().dot(lineSegmentVector);
			//			if (dot < 0) {
			Vector2d e1 = e.getP1();
			Vector2d e2 = e.getP2();
			Vector2d intersection = MathUtils.intersectLines(p1, p2, e1, e2);
			if(intersection != null) {

				//					double a = lineSegmentVector.angle(e.getNormal()) -Math.PI;

				//					if(Math.abs(a) >= maxReflectionAngle)
				//						intersection = null;

				if(closestPoint != null) {
					if(intersection.distanceTo(p1) < closestPoint.distanceTo(p1))
						closestPoint = intersection;
				} else {
					closestPoint = intersection;
				}
				//					break;
				//				}
			}
		}
		return closestPoint;
	}
}
