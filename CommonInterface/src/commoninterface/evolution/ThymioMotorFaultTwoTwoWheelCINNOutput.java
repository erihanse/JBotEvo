package commoninterface.evolution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.neuralnetwork.outputs.CINNOutput;
import commoninterface.utils.CIArguments;

public class ThymioMotorFaultTwoTwoWheelCINNOutput extends CINNOutput {

	private double leftSpeed;
	private double rightSpeed;

	private boolean hasFaultLeft = false, hasFaultRight = false;
	private double faultRatio = 1.0;
	public ThymioMotorFaultTwoTwoWheelCINNOutput(RobotCI robot, CIArguments args) {
		super(robot,args);
		int robotId = ((ThymioCI) robot).getRobotId();
		//fault one motor experiments, robot id = 11
		if(robotId == 11 || robotId == 12){
			Random random = new Random();
			//left wheel
			if(random.nextBoolean())
				hasFaultLeft = true;
			//right wheel
			else
				hasFaultRight = true;

			//from 0.5 to 0.75
			faultRatio = random.nextDouble() * 0.25 + 0.5;

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("motor_faults_one_" + 
						robotId + ".csv", true));
				writer.write(hasFaultLeft + ";" + hasFaultRight + ";" + faultRatio);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getNumberOfOutputValues() {
		return 2;
	}

	@Override
	public void setValue(int output, double value) {

		if (output == 0)
			leftSpeed = value*2 - 1;
		else
			rightSpeed = value*2 - 1;
	}

	@Override
	public void apply() {
		if(hasFaultLeft){
			if(leftSpeed > faultRatio)
				leftSpeed = faultRatio;
			else if(leftSpeed < -faultRatio)
				leftSpeed = -faultRatio;
		}
		else if(hasFaultRight){
			if(rightSpeed > faultRatio)
				rightSpeed = faultRatio;
			else if(rightSpeed < -faultRatio)
				rightSpeed = -faultRatio;
		}
		robot.setMotorSpeeds(leftSpeed, rightSpeed);
	}

}
