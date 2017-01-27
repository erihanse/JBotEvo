package commoninterface.evolution.odneat.eval;

public class TestFitnessFunction {

	public static void main(String[] args){
		double maxSpeed = 0.2;
		double wheelDiameter = 0.035;
		//function 1
		double left = 1, right = -1;
		double ts = computeTransaxialSpeed(left, right);
		double rs = computeRotationalSpeed(left, right);
		System.out.println("ts: " + ts + " ; rs: " + rs);
		double variation = (ts*rs) * 2 - 1;
		System.out.println("var: " + variation);
	}
	
	


	//motion
	protected static double computeRotationalSpeed(double leftWheelSpeed, double rightWheelSpeed){
		//double pi = Math.PI;
		//double maxRotationalSpeed = maxWheelSpeed/(wheelDiameter*pi);
		double left = leftWheelSpeed;///(wheelDiameter * pi);
		double right = rightWheelSpeed;///(wheelDiameter * pi);

		//left /= maxRotationalSpeed;
		//right /= maxRotationalSpeed;
		left /= 2;
		right /= 2;

		return Math.abs(left) + Math.abs(right);
	}

	protected static double computeTransaxialSpeed(double leftWheelSpeed,	double rightWheelSpeed){
		//[-0.5 to 0.5] speed
		double left = leftWheelSpeed/2;
		double right = rightWheelSpeed/2;

		//[0 : 1]
		left += 0.5;
		right += 0.5;

		double diff = 1 - Math.sqrt(Math.abs(left - right));

		return diff;	
	}


}