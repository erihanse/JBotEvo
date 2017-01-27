package utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class ConvertOpenCVBytes {

	public byte[] convertToStdBytes(byte[] opencvBytes){
		Mat mat = new Mat();
        mat.put(0, 0, opencvBytes);
        
        MatOfByte mob = new MatOfByte();
        Highgui.imencode(".png", mat ,mob);
        
        byte[] byteArray = mob.toArray();
        return byteArray;
	}
}
