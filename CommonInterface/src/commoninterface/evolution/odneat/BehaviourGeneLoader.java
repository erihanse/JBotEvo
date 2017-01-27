package commoninterface.evolution.odneat;



public class BehaviourGeneLoader {

	public static MacroBehaviourGene load(String classname, long innovationNumber){
		switch(classname){
		case "MoveForwardBehaviourGene":
			return new MoveForwardBehaviourGene(innovationNumber);
		case "TurnLeftBehaviourGene":
			return new TurnLeftBehaviourGene(innovationNumber);
		case "TurnRightBehaviourGene":
			return new TurnRightBehaviourGene(innovationNumber);
		case "DoNotMoveBehaviourGene":
			return new DoNotMoveBehaviourGene(innovationNumber);
		default:
			return null;
		}
	}
}
