package commoninterface.network.messages;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import commoninterface.network.messages.Message;

public class JavaCameraCaptureMessage extends Message {

	private static final long serialVersionUID = -2683957128513511562L;
	private byte[] frame;

	public JavaCameraCaptureMessage(byte[] frame, String senderHostname) {
		super(senderHostname);
		this.frame = frame;
	}

	public byte[] getFrameBytes() {
		byte[] byteArray = null;
		try{
			Mat mat = new Mat(480, 640, CvType.CV_8UC3);			
			mat.put(0, 0, frame);
			//System.out.println("frame size (in bytes): " + frame.length);
			//System.out.println(mat.total()  + ";" + mat.channels());
			//long pDelay = System.currentTimeMillis();
			Core.flip(mat, mat, -1);
			//to byte array
			MatOfByte mob = new MatOfByte();
			Highgui.imencode(".png", mat ,mob); 
			byteArray = mob.toArray();

			//pDelay = (System.currentTimeMillis() - pDelay);
			//double d = ((double)pDelay/1000);
			//System.out.println("pc delay: " + d);
		}
		catch(Exception e){
			return null;
		}
		return byteArray;
		//BEFORE (WORKS ON RPI)
		/*long pDelay = System.currentTimeMillis();
		Core.flip(frame, frame, -1);
		//to byte array
		MatOfByte mob = new MatOfByte();
		Highgui.imencode(".png", frame ,mob); 
		byte[] byteArray = mob.toArray();

		pDelay = (System.currentTimeMillis() - pDelay);
		double d = ((double)pDelay/1000);
		System.out.println("delay: " + d);
		return byteArray;*/
	}

	@Override
	public Message getCopy() {
		return new JavaCameraCaptureMessage(frame, getSenderHostname());
	}

}
