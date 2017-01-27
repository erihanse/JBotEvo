package commoninterface.messageproviders;

import commoninterface.ThymioCI;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.network.messages.InformationRequest;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.MessageProvider;

public class ThymioControllerGenerationMessageProvider implements MessageProvider {

	private ThymioCI thymio;


	public ThymioControllerGenerationMessageProvider(ThymioCI thymio) {
		this.thymio = thymio;
	}

	@Override
	public Message getMessage(Message request) {
		if (request instanceof InformationRequest
				&& ((InformationRequest) request).getMessageTypeQuery().equals(
						InformationRequest.MessageType.CONTROLLER_GENERATION)) {
			if(thymio.getActiveBehavior() instanceof ThymioOnlineEvoControllerCIBehaviour){
				ThymioOnlineEvoControllerCIBehaviour c = (ThymioOnlineEvoControllerCIBehaviour) thymio.getActiveBehavior();
				if(c.hasControllerGenerationMessage())
					return c.getControllerGenerationMessage();
			}
		}
		return null;
	}

}
