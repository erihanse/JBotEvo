package main;

import java.util.HashMap;
import java.util.Scanner;

import org.opencv.core.Core;
import commoninterface.utils.CIArguments;
import commoninterfaceimpl.RealThymioCI;

public class MainThymio {

	public static void main(String[] args) {
		
		 // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.NATIVE_LIBRARY_NAME.toString());
	        
		RealThymioCI thymio = new RealThymioCI();
	
		
		try {
		
			HashMap<String,CIArguments> arg = CIArguments.parseArgs(new String[]{"config/thymio.conf"});
			
			thymio.begin(arg);
			thymio.start();
		
			Scanner s = new Scanner(System.in);
			while(s.hasNextLine()) {
				String line = s.nextLine();
				if(line.equals("q")) {
					thymio.shutdown();
					s.close();
					System.exit(0);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
