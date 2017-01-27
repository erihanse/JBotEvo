package network;

import gui.ThymioGUI;
import main.ThymioControlConsole;

import commoninterface.network.messages.CameraCaptureMessage;
import commoninterface.network.messages.JavaCameraCaptureMessage;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.ThymioControllerGenerationMessage;
import commoninterface.network.messages.ThymioExecutingMessage;
import commoninterface.network.messages.ThymioPopulationStatusMessage;
import commoninterface.network.messages.ThymioReadingsMessage;
import commoninterface.network.messages.ThymioVirtualPositionMessage;

public class ThymioConsoleMessageHandler extends ControlConsoleMessageHandler {

	private ThymioControlConsole thymioConsole;

	public ThymioConsoleMessageHandler(ThymioControlConsole console) {
		super(console);
		this.thymioConsole = console;
	}

	@Override
	protected Message processMessage(Message message) {

		Message m = super.processMessage(message);

		if(m != null) {
			return null;
		} else if(message instanceof ThymioControllerGenerationMessage ||
				message instanceof ThymioPopulationStatusMessage 
				|| message instanceof ThymioExecutingMessage){
			String str = message.toString();
			thymioConsole.logStatus(str);
			if(message instanceof ThymioExecutingMessage){
				ThymioExecutingMessage tem = (ThymioExecutingMessage) message;
				double fit = tem.getFitness();
				String controller = tem.getController();
				int age = tem.getAge();
				System.out.println("STATUS: " + controller + ", fit " + fit + " , age " + age);
			}
		}
		else if (message instanceof ThymioReadingsMessage) {
			((ThymioGUI)console.getGUI()).getReadingsPanel().displayData((ThymioReadingsMessage) message);
		} else if (message instanceof JavaCameraCaptureMessage) {
			((ThymioGUI)console.getGUI()).getCapturePanel().displayData((JavaCameraCaptureMessage) message);
			/*} else if (message instanceof ThymioVirtualPositionMessage) {
			((ThymioGUI)console.getGUI()).getVirtualPositionPanel().displayData((ThymioVirtualPositionMessage) message);*/
		} else {
			System.out.println("Received non recognized message type: " + message.getClass().toString());
		}

		return null;
	}
}