package commoninterface.evolution.odneat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import commoninterface.RobotCI;
import commoninterface.ThymioCI;
import commoninterface.evolution.ALGDescriptor;
import commoninterface.evolution.MacroNetwork;
import commoninterface.evolution.ODNEATGene;
import commoninterface.evolution.ODNEATGenome;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.evolution.odneat.eval.CIODNEATEvaluationFunction;
import commoninterface.utils.CIArguments;



public class ODNEAT implements OnlineEA<ODNEATGenome>, Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * odneat components and parameters
	 */
	protected ODNEATPopulation population;
	protected ODNEATGenome current;
	protected ODNEATInnovationManager inManager;

	private int controllersGenerated = 0;
	//public final int MATURATION_PERIOD = 500;
	protected int MATURATION_PERIOD;

	protected boolean conductingEvolution = true;

	//age of the current controller/active genome (number of control cycles)
	protected int age = 0;
	//update every control cycle
	protected final double CONTROLLER_FREQUENCY_UPDATE = 1;

	public static final String ENERGY = "e";
	protected CIODNEATEvaluationFunction eval;
	/**
	 * evolutionary operators
	 */
	protected Mutator<ODNEATGenome> mutator;
	protected Crossover<ODNEATGenome> crossover;
	protected Selector<ODNEATGenome> selector;

	protected Random random;
	protected ArrayList<ODNEATGenome> receivedInformation;
	protected ThymioCI robot;
	private int robotId;
	//use maturation period or not
	private boolean useMaturation;
	//eval steps (when maturation period is not used)
	private int evalSteps;



	boolean useSpec, exchange;
	private boolean macroSpecialisation;
	private MacroNeuronsPopulation macroPop;
	private int noEvalPeriod;

	public ODNEAT(ALGDescriptor descriptor, ThymioCI r,
			CIODNEATEvaluationFunction eval, 
			int inputs, int outputs, int maturationperiod, 
			boolean useMaturation, int evalSteps, ArrayList<ODNEATGene> genes, CIArguments macroArgs, int noEvalPeriod){
		//System.out.println(args.toString());
		this.MATURATION_PERIOD = maturationperiod;
		this.useMaturation = useMaturation;
		this.evalSteps = evalSteps;

		//TODO: fix
		this.random = new Random();
		//we need to access the robot so that we can set the new controller when needed.
		this.robot = r;
		this.robotId = this.robot.getRobotId();
		//System.out.println("robot id: " + robotId + " (odNEAT)");
		this.eval = eval;

		/***
		 * for ablations
		 */
		this.exchange = descriptor.exchange();

		/**
		 * 
		 */
		this.noEvalPeriod = noEvalPeriod;
		
		CompatibilityCalculator calc = new CompatibilityCalculator(descriptor.getExcessCoeff(), descriptor.getDisjointCoeff(), 
				descriptor.getWeightCoeff(), descriptor.getThreshold());
		setupComponents(descriptor, calc);
		initialisePopulation(descriptor, calc);
		this.initializeFirstGenome(inputs, outputs, genes, macroArgs);

		//macro specialisation
		int evolvedMacros = macroArgs.getArgumentAsIntOrSetDefault("evolvedmacros", 0);
		if(evolvedMacros > 0)
			this.macroSpecialisation = macroArgs.getArgumentAsInt("macrospecialisation") == 1;
		System.out.println("EVOLVED MACROS: " + evolvedMacros);
		System.out.println("MACRO SPECIALISATION: " + macroSpecialisation);
		
		if(this.macroSpecialisation){
			macroPop = new MacroNeuronsPopulation(random, calc, descriptor,
					this.inManager, this.robotId, macroArgs.getArgumentAsDoubleOrSetDefault(
							"macroreuse",0.0));
			HashMap<Long, ODNEATMacroNodeGene> map = ((MacroGenome) current).getMacroNodeGenes();
			for(Long key : map.keySet()){
				ODNEATMacroNodeGene g = map.get(key);
				macroPop.initialisePop(key, g.getMacroName(), g.getMacroId(), this.robotId);
				macroPop.addMacroNeuronToPop(key, map.get(key).copy(), false);
			}
		}
	}

	public Random getRandom(){
		return this.random;
	}

	public void initialisePopulation(ALGDescriptor descriptor, CompatibilityCalculator calc) {

		this.population = new ODNEATPopulation(random, calc, descriptor.getPopulationSize());
	}

	public CIODNEATEvaluationFunction getODNEATEvaluationFunction(){
		return this.eval;
	}

	//in case we just want to see the robot's behaviour.
	public void setEvolutionStatus(boolean evolutionStatus){
		this.conductingEvolution = evolutionStatus;
	}

	protected void setupComponents(ALGDescriptor descriptor, CompatibilityCalculator calc) {
		inManager = new ODNEATInnovationManager(random, descriptor.getWeightRange());
		receivedInformation = new ArrayList<ODNEATGenome>();

		initialiseEvolutionaryOperators(descriptor);
	}

	public void initialiseEvolutionaryOperators(ALGDescriptor descriptor) {
		this.mutator = initialiseMutator(descriptor, this.random);
		this.selector = new ODNEATTournamentSelector();
		selector.setRandom(random);
		this.crossover = new StandardCrossover(random);
		crossover.setCrossoverProbability(descriptor.getPXover());		
	}

	protected ODNEATCompositeMutator initialiseMutator(ALGDescriptor descriptor, Random random) {

		ODNEATCompositeMutator compMut = new MutationOperatorsSetup().setup(descriptor, 
				random, this.inManager);

		return compMut;
	}

	public void initializeFirstGenome(int inputs, int outputs, ArrayList<ODNEATGene> genes, CIArguments macroArgs){
		String firstId = assignNewId();
		current = this.inManager.initialiseInnovation(firstId, inputs, outputs, genes, macroArgs);
		current.setEnergyLevel(eval.getDefaultEnergyValue());
		System.out.println("first genome energy: " + current.getEnergyLevel());
		//current.setEAInstance(eaInstance);
		robot.setParameter(ENERGY, new Double(current.getEnergyLevel()));
		//System.out.println("robot genome energy: " + robot.getParameter(ENERGY));

		population.setCurrentId(firstId);

		population.addODNEATGenome(current, false);
		//this.current = current.copy();

		this.resetAge();
	}

	@Override
	public boolean willBroadcastGenome() {
		
		if(!this.exchange)
			return false;
		double prob = this.random.nextDouble();
		//System.out.println("prob: " + prob + " ; pop: " + population.getProportionateAdjustedFitness(current.getSpeciesId()));
		//return true;
		return prob < population.getProportionateAdjustedFitness(current.getSpeciesId());
	}

	public void processGenomesReceived(Collection<ODNEATGenome> receivedInformation) {
		synchronized(this.receivedInformation){

			for(ODNEATGenome c : receivedInformation){
				//this way, we only have to search for it once.
				ODNEATSpecies species = population.containsChromosome(c);
				//population does not contain the current chromosome
				if(species == null){
					population.addConditionallyODNEATGenome(c);
					//add macro neurons to pop
					if(this.macroSpecialisation){
						MacroGenome g = (MacroGenome) c;
						//String controllerId = c.getId();
						//double fitness = c.getFitness();
						HashMap<Long, ODNEATMacroNodeGene> map = g.getMacroNodeGenes();
						for(Long key : map.keySet()){
							ODNEATMacroNodeGene node = map.get(key);
							//String evolutionId = node.getEvolutionId();
							this.macroPop.addMacroNeuronToPop(key, node,  true);
						}
					}
				}
				//not a new chromosome, update energy level and so forth
				else {
					updateGenome(c, c.getEnergyLevel());
				}
			}
		}

	}


	/*public void updateReceivedGenome(ODNEATGenome genome, double energyValue){
		population.updateGenome(genome, energyValue);

	}*/

	public void updateGenome(ODNEATGenome genome, double energyValue){
		population.updateGenome(genome, energyValue);
		if(this.macroSpecialisation){
			MacroGenome g = (MacroGenome) genome;
			String controllerId = genome.getId();
			double fitness = genome.getFitness();
			HashMap<Long, ODNEATMacroNodeGene> map = g.getMacroNodeGenes();
			for(Long key : map.keySet()){
				ODNEATMacroNodeGene node = map.get(key);
				String evolutionId = node.getEvolutionId();
				this.macroPop.updateMacroNodePerformance(controllerId, fitness, key, evolutionId, node);
			}
		}
	}

	@Override
	public ODNEATGenome reproduce() {	

		
		String newGenomeId = assignNewId();
		System.out.println("evolving new genome: " + newGenomeId);
		/*current = population.reproduce(newGenomeId, this.selector, this.crossover, 
				this.mutator);*/
		if(!this.macroSpecialisation)
			current = population.reproduce(newGenomeId, 
					this.selector, this.crossover, 
					this.mutator);
		else
			current = population.reproduceWithMacroSpecialisation(
					newGenomeId, 
					this.selector, this.crossover, 
					this.mutator, this.macroPop);
		current.setEnergyLevel(eval.getDefaultEnergyValue());
		this.robot.setParameter(ENERGY, current.getEnergyLevel());




		//current = temp.copy();

		this.age = 0;
		current.setAge(0);

		/**
		 * re-speciate the population
		 */
		//clear
		/*ArrayList<ODNEATGenome> genomes = new ArrayList<ODNEATGenome>();
		for(ODNEATSpecies s : this.population.getSpeciesList()){
			genomes.addAll(s.getGenomes());
			s.clear();
		}

		//remove the species without elements
		Iterator<ODNEATSpecies> it = this.population.getSpeciesList().iterator();
		while(it.hasNext()){
			ODNEATSpecies currentSpecies = it.next();
			if(currentSpecies.species.isEmpty())
				it.remove();
		}

		//add randomly
		while(!genomes.isEmpty()){
			ODNEATGenome g = genomes.remove(random.nextInt(genomes.size()));
			this.population.addODNEATGenome(g, tabuList, false);
		}*/

		return current;
	}

	private String assignNewId() {
		setControllersGenerated(getControllersGenerated() + 1);
		return this.robotId+ "." + getControllersGenerated();
	}


	public RobotPopulation<ODNEATGenome> getPopulation() {
		return this.population;
	}

	public int getControllersGenerated() {
		return this.controllersGenerated;
	}

	public void transmitGenome(ODNEATGenome e, OnlineEA<ODNEATGenome> otherInstance) {
		//NOTE: NOT USED IN THE CURRENT IMPLEMENTATION
	}

	public void receiveSingleGenome(ODNEATGenome g){
		//System.out.println("received genome: " + g.getId());
		synchronized(this.receivedInformation){
			if(!this.receivedInformation.contains(g)){
				this.receivedInformation.add(g);
			}
		}
	}

	@Override
	public ODNEATGenome getActiveGenome() {
		return current;
	}

	public void executeOnlineEvolution(double timestep,MacroNetwork network){
		if(this.conductingEvolution){
			double energy = robot.getParameter(ENERGY);

			this.processGenomesReceived(this.receivedInformation);
			this.receivedInformation.clear();
			//update the energy level.
			energy = updateEnergyLevel(energy, network);

			//we count the "age" of the controller
			this.increaseAge();
			this.current.setAge(this.age);
			analyseTaskPerformance(energy, timestep);
		}
		else{
			double energy = robot.getParameter(ENERGY);
			energy = this.updateEnergyLevel(energy, network);
			this.increaseAge();

			//just update the energy, there is no further evolution.
			energy = eval.limitEnergyLevel(energy);
			updateCurrentController(timestep, energy);
			this.robot.setParameter(ENERGY, energy);
		}
	}


	protected void increaseAge() {
		age++;
	}

	public void forceDeathCurrentController(double time) {
		this.generateNewController(time);
		//the default virtual energy level was already set the method "generate new controller".
		double energy = current.getEnergyLevel();
		updateCurrentController(time, energy);
		this.robot.setParameter(ENERGY, energy);
	}


	protected void analyseTaskPerformance(double energy, double time) {

		/***
		 * check if it is time to change to a new controller.
		 */
		//use maturation period
		//System.out.println("time: " + time + ";" + getAge());
		if(useMaturation && energy <= eval.getMinEnergyValue() && getAge() >= MATURATION_PERIOD){
			this.generateNewController(time);
			//default virtual energy level has already been set (generate new controller -> reproduce).
			energy = current.getEnergyLevel();
		}
		//no maturation
		else if(!this.useMaturation && getAge() >= this.evalSteps){
			this.generateNewController(time);
			//default virtual energy level has already been set (generate new controller -> reproduce).
			energy = current.getEnergyLevel();

		}
		//in range
		else {
			energy = eval.limitEnergyLevel(energy);
		}
		this.current.setEnergyLevel(energy);

		updateCurrentController(time, energy);
		this.robot.setParameter(ENERGY, energy);
	}

	protected void updateCurrentController(double currentTime, double energyValue) {
		if(currentTime % CONTROLLER_FREQUENCY_UPDATE == 0){
			this.updateGenome(this.current, energyValue);
		}
	}



	private void generateNewController(double time) {
		//System.out.println("TIME: " + time + ";" + this.evalSteps + ";" + this.getAge());


		current = this.reproduce();
		//current.setAge(0);
		this.updateGenome(current, current.getEnergyLevel());
		//@SuppressWarnings("unchecked")
		//OnlineController<ODNEATGenome> controller = (OnlineController<ODNEATGenome>) this.robot.getController();
		ThymioOnlineEvoControllerCIBehaviour behavior = (ThymioOnlineEvoControllerCIBehaviour) this.robot.getActiveBehavior();
		behavior.updateStructure(current);
		this.resetAge();
	}


	protected void resetAge() {
		this.setAge(0);
	}

	private void setAge(int newAge) {
		this.age = newAge;
	}

	public int getAge(){
		return age;
	}

	protected double updateEnergyLevel(double currentEnergy, MacroNetwork network) {
		return eval.updateEnergyLevel(currentEnergy, this.robot, network.getInputNeuronStates().clone(), network.getOutputNeuronStates().clone());
	}


	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("id: " + this.robotId + "\n");
		builder.append("pop-size: " + this.population.getCurrentPopulationSize() + "\n");

		builder.append("species: " + this.population.getSpeciesList().size() + "\n");
		ODNEATGenome g = this.getActiveGenome();
		builder.append("active-genome: ID: " + g.getId() + "; " +
				"E-LEVEL: " + g.getEnergyLevel() + "; " +
				"FIT: " + g.getFitness() + "; " + 
				"ADJ-FIT: " + g.getAdjustedFitness() + "\n");
		builder.append("total nodes: " + g.getNumberOfNodeGenes() + "\n");
		builder.append("total connections: " + g.getNumberOfLinkGenes(true) + "\n");
		builder.append("age: " + (this.age/10) + " secs \n");
		builder.append("===========================\n");
		return builder.toString();
	}

	@Override
	public ODNEATGenome resetParametersForEvaluation(Object activeGenome) {
		ODNEATGenome g = (ODNEATGenome) activeGenome;
		g.setEnergyLevel(this.eval.getDefaultEnergyValue());
		g.setFitness(0);
		g.setAdjustedFitness(0);
		g.setUpdatesCount(0);

		this.robot.setParameter(ENERGY, new Double(g.getEnergyLevel()));

		return g;
	}



	@Override
	public RobotCI getRobotInstance() {
		return this.robot;
	}

	public int getControllersGeneratedCurrentlyInRep() {
		String prefix = this.robotId+ ".";
		int count = 0;
		for(ODNEATSpecies species : this.population.getSpeciesList()){
			for(ODNEATGenome g : species.getGenomes()){
				if(g.getId().startsWith(prefix)){
					count++;
				}
			}
		}
		return count;
	}

	public int getNumberOfSpecies() {
		return this.population.getSpeciesList().size();
	}

	public ArrayList<ODNEATSpecies> getSpecies(){
		return this.population.getSpeciesList();
	}

	public int getRobotId() {
		return this.robotId;
	}

	public void setActiveGenome(ODNEATGenome ind) {
		this.current = ind;
	}

	public void setControllersGenerated(int controllersGenerated) {
		this.controllersGenerated = controllersGenerated;
	}
}
