package commoninterface.evolution;


import java.util.ArrayList;
import java.util.Collection;
import commoninterface.CIBehavior;
import commoninterface.CISensor;
import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.evolution.odneat.MacroGenome;
import commoninterface.evolution.odneat.ODNEAT;
import commoninterface.evolution.odneat.ODNEATPopulation;
import commoninterface.evolution.odneat.ODNEATSpecies;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineCenterAggregationEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineHomingEvaluationFunction;
import commoninterface.evolution.odneat.eval.CIOnlineNavigationEvaluationFunction;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.ThymioControllerGenerationMessage;
import commoninterface.network.messages.ThymioExecutingMessage;
import commoninterface.network.messages.ThymioPopulationStatusMessage;
import commoninterface.neuralnetwork.CINeuralNetwork;
import commoninterface.utils.CIArguments;
import commoninterface.utils.RobotLogger;

public class ThymioOnlineEvoControllerCIBehaviour extends CIBehavior {

	/*private ArrayList<String> inputsNames;
	private ArrayList<String> outputsNames;
	private ArrayList<Double[]> inputs;
	private ArrayList<Double[]> outputs;*/
	protected CIArguments args;
	/*private String description = "";
	private boolean logInputsOutputs = false;*/

	protected ThymioCI thymio;
	//private CINeuralNetwork network;

	protected GPMapping<MacroGenome, MacroNetwork> map;
	protected ODNEAT instance;
	protected MacroNetwork network;
	//500 behavior steps
	protected int maturationperiod = 500;

	//messages
	protected Message populationStatusMessage, controllerExecutingMessage, controllerGenerationMessage;


	private double timestep;

	private RobotLogger logger;

	protected int noEvalPeriod;

	private int noEvalStepsCount;

	public ThymioOnlineEvoControllerCIBehaviour(CIArguments args, RobotCI robot) {
		super(args, robot);
		this.thymio = (ThymioCI) robot;
		this.args = args;
	}

	@Override
	/**
	 * Make any initial configurations.
	 */
	public void start() {
		System.out.println("starting thymio online evo....");

		ALGDescriptor descriptor = new ALGDescriptor("../CommonInterface/config/odneat_conf.properties");

		CIArguments sensorArgs = new CIArguments(args.getArgumentAsString("sensors"));
		initSensors(sensorArgs);

		CINeuralNetwork net = CINeuralNetwork.getNeuralNetwork( robot, 
				new CIArguments(args.getArgumentAsString("network")));

		this.network = (MacroNetwork) net;

		map = new ExtendedMacroANNCodec(descriptor.weightRange);
		System.out.println("map");

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
					evalSteps, genes, macroArgs,noEvalPeriod);
		}
		else {
			System.out.println("LOADING POPULATION...");
			String file = args.getArgumentAsString("loadpop") + "_" + this.thymio.getRobotId();
			ArrayList<ODNEATGenome> genomes = PopulationUtils.loadPopulationFromFile(file,
					this.network.getNumberOfInputNeurons(), this.network.getNumberOfOutputNeurons());
			this.instance = new ODNEAT(descriptor, this.thymio, func, this.network.getNumberOfInputNeurons(), 
					this.network.getNumberOfOutputNeurons(), maturationperiod, useMaturation, 
					evalSteps, new ArrayList<ODNEATGene>(), macroArgs,noEvalPeriod);

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
		System.out.println("start completed");
	}

	protected CIODNEATEvaluationFunction loadCIODNEATEvaluationFunction(
			CIArguments ciArguments) {
		//	System.out.println("task: " + ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("navigation-obstacle"));
		if(ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("navigation-obstacle"))
			return new CIOnlineNavigationEvaluationFunction(ciArguments);
		else if(ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("homing"))
			return new CIOnlineHomingEvaluationFunction(ciArguments);
		else if(ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("aggregation"))
			return new CIOnlineAggregationEvaluationFunction(ciArguments);
		else if(ciArguments.getArgumentAsString("taskname").equalsIgnoreCase("centeraggregation"))
			return new CIOnlineCenterAggregationEvaluationFunction(ciArguments);
		else
			return null;
	}

	public void updateStructure(Object object) {
		MacroGenome g = (object instanceof MacroGenome) ? 
				((MacroGenome) object) : new MacroGenome((StandardGenome) object);
		this.network.updateStructure(map.decode(g));
		//create messages
		createNewEvolutionMessages();

		this.noEvalStepsCount = 0;
	}

	//for debug
	/*public ODNEATGenome createStandardGenome(String newGenomeId, int inputs,
			int outputs, long startInnovationId) {
		System.out.printf("creating genome: " + inputs + " inputs, " + outputs + " outputs");
		final boolean linkEnabled = true;
		ArrayList<ODNEATNodeGene> neuronsList = new ArrayList<ODNEATNodeGene>();
		ArrayList<ODNEATLinkGene> linksList = new ArrayList<ODNEATLinkGene>();

		long innovationID = startInnovationId;
		// first inputs
		for (int i = 0; i < inputs; i++) {
			ODNEATNodeGene gene = new ODNEATNodeGene(innovationID++, ODNEATNodeGene.INPUT);
			neuronsList.add(gene);
		}

		// then outputs
		for (int i = 0; i < outputs; i++) {
			ODNEATNodeGene gene = new ODNEATNodeGene(innovationID++, ODNEATNodeGene.OUTPUT);
			neuronsList.add(gene);
		}

		// and now links
		for (int i = 0; i < inputs; i++) {
			for (int j = 0; j < outputs; j++) {
				long fromID = neuronsList.get(i).getInnovationNumber();
				long toID = neuronsList.get(inputs + j).getInnovationNumber();
				double w = this.generateRandomWeight();
				ODNEATLinkGene gene = new ODNEATLinkGene(innovationID++, linkEnabled, fromID, toID, w);
				linksList.add(gene);
			}
		}

		return new StandardGenome(newGenomeId, linksList, neuronsList);
	}

	protected double generateRandomWeight() {
		return new Random().nextDouble() * 10 * 2 - 10;
	}

	/* END DEBUG*/


	private void createNewEvolutionMessages() {
		int robotId = ((ThymioCI) this.robot).getRobotId();
		String controller = this.instance.getActiveGenome().getId();
		double globalStep = this.timestep;
		//ODNEATGenome g = this.instance.getActiveGenome();
		Collection<ODNEATLinkGene> links = this.instance.getActiveGenome().getLinkGenes(true);
		double[] net = new double[links.size() * 3];
		int index = 0;
		for(ODNEATLinkGene link : links){
			net[index++] = link.getFromId();
			net[index++] = link.getToId();
			net[index++] = link.getWeight();
		}
		this.controllerGenerationMessage = 	new ThymioControllerGenerationMessage(
				robotId, controller, globalStep, net, (controller + ""));
		int popSize = this.instance.getPopulation().getCurrentPopulationSize();

		ArrayList<ODNEATSpecies> species = this.instance.getSpecies();
		int numSpecies = species.size();
		int[] speciesSize = new int[numSpecies];
		double[] scores = new double[popSize];
		String[] genomeIds = new String[popSize];
		int specIndex = 0;
		index = 0;
		for(ODNEATSpecies spec : species){
			speciesSize[specIndex++] = spec.getSpeciesSize();
			for(ODNEATGenome g : spec.getGenomes()){
				scores[index] = g.getFitness();
				genomeIds[index++] = g.getId();
			}
		}
		this.populationStatusMessage = new ThymioPopulationStatusMessage((controller + ""), 
				robotId, globalStep, popSize, numSpecies, speciesSize, genomeIds, 
				scores);

		//TODO: log
		if(logger != null){
			this.logger.logMessage(this.populationStatusMessage.toString());
			this.logger.logMessage(this.controllerGenerationMessage.toString());
		}
	}

	protected void initSensors(CIArguments args) {

		robot.getCISensors().clear();
		//robot.getCISensors().add(new RealThymioIRCISensor());
		for(int i = 0 ; i < args.getNumberOfArguments() ; i++) {
			CIArguments sensorArgs = new CIArguments(args.getArgumentAsString(args.getArgumentAt(i)));
			CISensor s = CISensor.getSensor(robot,sensorArgs.getArgumentAsString("classname"), sensorArgs);
			robot.getCISensors().add(s);
		}
	}


	int steps = -1;
	//every 10 steps
	int freq = 10;

	protected double lastLeft, lastRight;
	@Override
	public void step(double timestep) {
		if(this.timestep == timestep && timestep >2){
			new Exception().printStackTrace();
			System.exit(0);
		}
		this.timestep = timestep;
		if(network == null || robot.getCISensors().isEmpty())
			return;


		//System.out.println("regular execution");
		steps++;

		Object[] entities = robot.getEntities().toArray();

		//update sensor readings
		for(CISensor s : robot.getCISensors()) {
			//System.out.print(s.getClass().getName());
			try{
				s.update(timestep,entities);
			}
			catch(Exception e){
				System.out.println(e.getLocalizedMessage() + ";" + e.getMessage());
			}
		}
		//random walk/no eval behaviour
		if(this.noEvalStepsCount < this.noEvalPeriod){
			noEvalStepsCount++;
			if(noEvalStepsCount < noEvalPeriod/2){
				this.thymio.setMotorSpeeds(1.0, 1.0);
			}
			//change every second, range 0 to 1
			else if(noEvalStepsCount % 10 == 0){
				int direction = this.instance.getRandom().nextInt(4);
				switch(direction){
				//forward
				case 0:
					lastLeft = 1.0;
					lastRight = 1.0;
					break;

					//backward
				case 1:
					lastLeft = -1.0;
					lastRight = -1.0;
					break;
					//left
				case 2:
					lastLeft = 0.5;
					lastRight = 1.0;
					break;
					//right
				case 3:
					lastLeft = 1.0;
					lastRight = 0.5;
					break;
				}
			}	
			robot.setMotorSpeeds(lastLeft, lastRight);
		}
		else {
			network.controlStep(timestep);
			instance.executeOnlineEvolution(timestep, network);
		}
		/*if(!COLLECT){
		}
		else {
			System.out.println("sample");
			if(writer == null){
				try {
					writer = new BufferedWriter(new FileWriter("ground_readings.txt", true));
					writer.write("start");
					writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			reading++;
			if(reading % 2 == 0)
				this.thymio.setMotorSpeeds(0.5, 0.5);
			else{
				List<Short> list = this.thymio.getGroundReflectedLightReadings();
				this.thymio.setMotorSpeeds(0.0, 0.0);
				String toWrite =  reading + ";";
				for(Short s : list){
					toWrite += s + ";";
				}
				try {
					writer.write(toWrite);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}*/

		//every 10 cycles
		if(this.logger != null && steps % freq == 0){
			Message m = this.getControllerExecutingMessage();
			if(m != null)
				this.logger.logMessage(m.toString());
		}
		
	}


	public MacroNetwork getNetwork() {
		return this.network;
	}

	public boolean hasPopulationStatusMessage() {
		return this.populationStatusMessage != null;
	}

	public Message getPopulationStatusMessage() {
		return this.populationStatusMessage;
	}

	public boolean hasControllerExecutingMessage() {
		return true;
	}

	public Message getControllerExecutingMessage() {

		try{
			int robotId = ((ThymioCI) this.robot).getRobotId();
			String controller = this.instance.getActiveGenome().getId();
			double globalStep = this.timestep;
			int age = this.instance.getAge();
			double energy = this.instance.getActiveGenome().getEnergyLevel();
			double fit = this.instance.getActiveGenome().getFitness();
			double[] inputs = this.network.getInputReadings().clone();
			double[] outputs = this.network.getOutputReadings().clone();

			this.controllerExecutingMessage = new ThymioExecutingMessage(robotId, controller, globalStep,
					age, energy, fit, inputs, outputs, (robotId + ""));

			return this.controllerExecutingMessage;
		}
		catch(Exception e){
			return null;
		}
	}

	public boolean hasControllerGenerationMessage() {
		return this.controllerGenerationMessage != null;
	}

	public Message getControllerGenerationMessage() {
		return this.controllerGenerationMessage;
	}

	public void setLogger(RobotLogger logger) {
		this.logger = logger;
	}

	public ODNEAT getODNEATInstance(){
		return this.instance;
	}
}
