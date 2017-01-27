package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.Collection;

import commoninterface.RobotCI;
import commoninterface.evolution.Genome;
import commoninterface.evolution.MacroNetwork;
import commoninterface.neuralnetwork.CINeuralNetwork;

public interface OnlineEA<E extends Genome> extends Serializable {
	
	public boolean willBroadcastGenome();
	
	public void transmitGenome(E e, OnlineEA<E> otherInstance);
	
	public void receiveSingleGenome(E e);

	public void processGenomesReceived(Collection<E> received);
	
	public E getActiveGenome();
	
	public E reproduce();
	
	public RobotPopulation<E> getPopulation();
	
	public void executeOnlineEvolution(double time, MacroNetwork net);

	public String toString();
	
	public void setEvolutionStatus(boolean evolutionsStatus);

	public E resetParametersForEvaluation(Object activeGenome);

	//public void setRobot(RobotCI robot);

	public RobotCI getRobotInstance();
}
