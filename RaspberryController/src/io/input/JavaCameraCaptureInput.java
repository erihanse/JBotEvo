package io.input;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import commoninterface.RobotCI;
import commoninterface.network.messages.CameraCaptureMessage;
import commoninterface.network.messages.InformationRequest;
import commoninterface.network.messages.JavaCameraCaptureMessage;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.MessageProvider;

public class JavaCameraCaptureInput implements ControllerInput, MessageProvider {


	protected boolean available = false;

	private RobotCI robotCI;

	//opencv stuff
	private VideoCapture camera;

	private Mat frame = new Mat();



	public JavaCameraCaptureInput(RobotCI robotCI){


		this.robotCI = robotCI;
		available = true;

		camera = new VideoCapture(0);
		//width and height
		System.out.println("camera settings: " + camera.get(3) + "x" + camera.get(4));
	}

	@Override
	public Message getMessage(Message request) {
		if (request instanceof InformationRequest
				&& ((InformationRequest) request).getMessageTypeQuery().equals(
						InformationRequest.MessageType.CAMERA_CAPTURE)) {
			return new JavaCameraCaptureMessage((byte[]) this.getReadings(),
					robotCI.getNetworkAddress());
		}
		return null;
	}

	@Override
	public Object getReadings() {
		if (camera != null){
			//read

			camera.read(frame);
			//long delay = System.currentTimeMillis();
			byte[] return_buff = new byte[(int) (frame.total() * 
					frame.channels())];
			frame.get(0, 0, return_buff);
			/*delay = System.currentTimeMillis() - delay;
			System.out.println("delay: " + delay);
			System.out.println("bytes: " + return_buff.length + " ; " + frame.total() + " ; " + frame.channels());*/
			return return_buff;
		}
		else
			return null;
	}

	/*public byte[] getSpace(Mat mat) {
		byte[] dat = null;
		BufferedImage img = null;

		int w = mat.cols(), h = mat.rows();
		//if (dat == null || dat.length != w * h * 3)
		dat = new byte[w * h * 3];
		img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		mat.get(0, 0, dat);
		img.getRaster().setDataElements(0, 0, 
		mat.cols(), mat.rows(), dat);

		return img.
	}*/

	@Override
	public boolean isAvailable() {
		return available;
	}

	public void release() {
		this.camera.release();
	}


}
