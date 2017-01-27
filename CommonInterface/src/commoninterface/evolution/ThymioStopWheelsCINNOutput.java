package commoninterface.evolution;

import commoninterface.RobotCI;
import commoninterface.neuralnetwork.outputs.CINNOutput;
import commoninterface.utils.CIArguments;

public class ThymioStopWheelsCINNOutput extends CINNOutput {

	private double value;
	
	public ThymioStopWheelsCINNOutput(RobotCI robot, CIArguments args) {
		super(robot,args);
	}
	
	@Override
	public int getNumberOfOutputValues() {
		return 1;
	}

	@Override
	public void setValue(int output, double value) {
		this.value = value;
	}

	@Override
	public void apply() {
		if(this.value > 0.5){
			robot.setMotorSpeeds(0, 0);
		}
	}

}

