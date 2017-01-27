package commoninterface.messageproviders;

import commoninterface.ThymioCI;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.network.messages.InformationRequest;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.MessageProvider;

public class ThymioPopulationStatusMessageProvider implements MessageProvider {

	private ThymioCI thymio;

	public ThymioPopulationStatusMessageProvider(ThymioCI thymio) {
		this.thymio = thymio;
	}

	@Override
	public Message getMessage(Message request) {
		if (request instanceof InformationRequest
				&& ((InformationRequest) request).getMessageTypeQuery().equals(
						InformationRequest.MessageType.POPULATION_STATUS)) {
			if(thymio.getActiveBehavior() instanceof ThymioOnlineEvoControllerCIBehaviour){
				ThymioOnlineEvoControllerCIBehaviour c = (ThymioOnlineEvoControllerCIBehaviour) thymio.getActiveBehavior();
				if(c.hasPopulationStatusMessage())
					return c.getPopulationStatusMessage();
			}
		}
		return null;
	}

}
