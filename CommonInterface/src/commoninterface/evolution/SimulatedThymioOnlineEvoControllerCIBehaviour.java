package commoninterface.evolution;


import java.util.ArrayList;

import commoninterface.RobotCI;
import commoninterface.evolution.odneat.MacroGenome;
import commoninterface.evolution.odneat.ODNEAT;
import commoninterface.evolution.odneat.ODNEATPopulation;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.neuralnetwork.CINeuralNetwork;
import commoninterface.utils.CIArguments;

public class SimulatedThymioOnlineEvoControllerCIBehaviour extends ThymioOnlineEvoControllerCIBehaviour{

	public SimulatedThymioOnlineEvoControllerCIBehaviour(CIArguments args,
			RobotCI robot) {
		super(args, robot);
		//System.out.println("construtor");
	}
	
	@Override
	public void step(double timestep) {
		super.step(timestep);
	}
	
	@Override
	/**
	 * Make any initial configurations.
	 */
	public synchronized void start() {
		ALGDescriptor descriptor = new ALGDescriptor("../CommonInterface/config/odneat_conf.properties");

	//	CIArguments sensorArgs = new CIArguments(args.getArgumentAsString("sensors"));
	//	initSensors(sensorArgs);

		CINeuralNetwork net = CINeuralNetwork.getNeuralNetwork( robot, 
				new CIArguments(args.getArgumentAsString("network")));

		this.network = (MacroNetwork) net;

		map = new ExtendedMacroANNCodec(descriptor.weightRange);
	//	System.out.println("map");

		//TODO:
		CIODNEATEvaluationFunction func = loadCIODNEATEvaluationFunction(new CIArguments(args.getArgumentAsString("task")));

		CIArguments eval = new CIArguments(args.getArgumentAsString("evaluation"));
		boolean useMaturation = true;
		int evalSteps = -1;
		this.noEvalPeriod = eval.getArgumentAsIntOrSetDefault("noevalperiod", 0);
		if(eval != null && eval.getArgumentAsString("policy").equalsIgnoreCase("fixed")){
			useMaturation = false;
			evalSteps = eval.getArgumentAsInt("evalsteps");
		}
		CIArguments macroArgs = new CIArguments(args.getArgumentAsStringOrSetDefault(
				"macroneurons", ""));
		macroArgs.setArgument("macroreuse", args.getArgumentAsDoubleOrSetDefault("macroreuse", 0.0));
		if(!args.getArgumentIsDefined("loadpop")){
			ArrayList<ODNEATGene> genes;
			if(args.getArgumentIsDefined("weights")) {
				String netStr = args.getArgumentAsString("weights");
				genes = SerialisationHelper.deserialize(netStr);
				System.out.println("network loaded...");
			}
			else
				genes = new ArrayList<ODNEATGene>();
			this.instance = new ODNEAT(descriptor, this.thymio, func, this.network.getNumberOfInputNeurons(), 
					this.network.getNumberOfOutputNeurons(), maturationperiod, useMaturation, 
					evalSteps, genes, macroArgs, noEvalPeriod);
		}
		else {
			System.out.println("LOADING POPULATION...");
			String file = args.getArgumentAsString("loadpop") + "_" + this.thymio.getRobotId();
			ArrayList<ODNEATGenome> genomes = PopulationUtils.loadPopulationFromFile(file,
					this.network.getNumberOfInputNeurons(), this.network.getNumberOfOutputNeurons());
			this.instance = new ODNEAT(descriptor, this.thymio, func, this.network.getNumberOfInputNeurons(), 
					this.network.getNumberOfOutputNeurons(), maturationperiod, useMaturation, 
					evalSteps, new ArrayList<ODNEATGene>(), macroArgs, noEvalPeriod);

			ODNEATPopulation pop = (ODNEATPopulation) this.instance.getPopulation();
			for(ODNEATGenome g : genomes){
				pop.addODNEATGenome(g, false);
			}
			pop.respeciate();
			//just for clearly distinguishing the two phases
			this.instance.setControllersGenerated(1000);
			this.instance.reproduce();
		}


		this.updateStructure(this.instance.getActiveGenome());
		//System.out.println("start completed");
	}
	
	@Override
	protected void initSensors(CIArguments args) {
		
	}
	
}