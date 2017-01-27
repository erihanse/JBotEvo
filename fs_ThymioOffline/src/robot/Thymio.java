package robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import network.SimulatedBroadcastHandler;

import sensors.ThymioCameraSensor;
import sensors.ThymioIRSensor;
import sensors.ThymioNestSensor;
import simulation.Simulator;
import simulation.Stoppable;
import simulation.physicalobjects.PhysicalObject;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.actuators.TwoWheelActuator;
import simulation.robot.sensors.GroupAreaASensor;
import simulation.robot.sensors.GroupAreaBSensor;
import simulation.robot.sensors.InAreaASensor;
import simulation.robot.sensors.InAreaBSensor;
import simulation.robot.sensors.InNestSensor;
import simulation.util.Arguments;

import actuator.StopActuator;
import actuator.ThymioTwoWheelActuator;

import commoninterface.CIBehavior;
import commoninterface.CISensor;
import commoninterface.ThymioCI;
import commoninterface.entities.Entity;
import commoninterface.evolution.SimulatedThymioOnlineEvoControllerCIBehaviour;
import commoninterface.evolution.ThymioOnlineEvoControllerCIBehaviour;
import commoninterface.mathutils.Vector2d;
import commoninterface.messageproviders.BehaviorMessageProvider;
import commoninterface.messageproviders.EntitiesMessageProvider;
import commoninterface.messageproviders.EntityMessageProvider;
import commoninterface.messageproviders.LogMessageProvider;
import commoninterface.messageproviders.NeuralActivationsMessageProvider;
import commoninterface.messageproviders.ThymioControllerGenerationMessageProvider;
import commoninterface.messageproviders.ThymioExecutingMessageProvider;
import commoninterface.messageproviders.ThymioPopulationStatusMessageProvider;
import commoninterface.network.ConnectionHandler;
import commoninterface.network.broadcast.BroadcastHandler;
import commoninterface.network.broadcast.BroadcastMessage;
import commoninterface.network.broadcast.HeartbeatBroadcastMessage;
import commoninterface.network.broadcast.InAreaBroadcastMessage;
import commoninterface.network.broadcast.ODNEATGenomeBroadcastMessage;
import commoninterface.network.messages.Message;
import commoninterface.network.messages.MessageProvider;
import commoninterface.utils.CIArguments;
import commoninterface.utils.RobotLogger;
import commoninterface.utils.logger.LogCodex;
import commoninterface.utils.logger.LogCodex.LogType;

public class Thymio extends DifferentialDriveRobot implements ThymioCI, Stoppable {

	private HashMap<String, Double> runtimeParameters = new HashMap<String, Double>();
	private Simulator simulator;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<CISensor> cisensors = new ArrayList<CISensor>();
	private SimulatedThymioBroadcastHandler broadcastHandler;

	private ThymioIRSensor irSensor;
	private ThymioCameraSensor cameraSensor;
	private ThymioTwoWheelActuator wheels;
	private ThymioNestSensor nestSensor;

	private InAreaASensor areaASensor;
	private InAreaBSensor areaBSensor;
	
	private Vector2d virtualPosition;
	private Double virtualOrientation;

	private ArrayList<MessageProvider> messageProviders;
	private ArrayList<CIBehavior> alwaysActiveBehaviors;
	private CIBehavior activeBehavior;

	private RobotLogger logger;

	private double leftPercentage = 0;
	private double rightPercentage = 0;
	private boolean useNestSensor;
	private boolean useMultiNestSensor;



	public Thymio(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.simulator = simulator;

		ArrayList<BroadcastMessage> broadcastMessages = new ArrayList<BroadcastMessage>();
		broadcastMessages.add(new HeartbeatBroadcastMessage(this));
		broadcastMessages.add(new ODNEATGenomeBroadcastMessage(this));
		
		broadcastMessages.add(new InAreaBroadcastMessage(this));

		//broadcastMessages.add(new SharedThymioBroadcastMessage(this));

		broadcastHandler = new SimulatedThymioBroadcastHandler(this, broadcastMessages);
		alwaysActiveBehaviors = new ArrayList<CIBehavior>();

		distanceBetweenWheels = 0.10;

		if(getRadius() != 0.08)
			throw new RuntimeException("Radius lower than 0.08m");

		//	boolean ci = args.getFlagIsTrue("ci");
		boolean irSenseRobots = args.getFlagIsTrue("irsenserobot");
		boolean activeCamera = args.getFlagIsTrue("camerasensor");
		int naiveSensor = args.getArgumentAsInt("naivesensor");
		int noiseEnabled = args.getArgumentAsInt("noiseenabled");
		this.useNestSensor = args.getFlagIsTrue("nestsensor");
		boolean stopActuator = args.getFlagIsTrue("stopactuator");

		Arguments irSensorsArgs = new Arguments("naivesensor=" + (naiveSensor) +
				",cutoffangle=45,noiseenabled=" + noiseEnabled + ",numberofrays=7,senserobot=" + (irSenseRobots ? 1 : 0));

		sensors.add(new ThymioIRSensor(simulator, sensors.size()+1, this, irSensorsArgs));

		if(activeCamera){
			Arguments cameraSensorArgs = new Arguments("numbersensors=2,range=0.2,eyes=1,share=1");
			sensors.add(new ThymioCameraSensor(simulator, sensors.size()+1, this, cameraSensorArgs));
		}
		if(useNestSensor){
			Arguments nestSensorArgs = new Arguments("nestradius=0.05,naivesensor=" + naiveSensor + ",noisenabled=" + noiseEnabled);
			sensors.add(new InNestSensor(simulator, sensors.size() + 1, this, nestSensorArgs));
			sensors.add(new ThymioNestSensor(simulator, sensors.size() + 1, this, nestSensorArgs));
		}
		this.useMultiNestSensor = args.getFlagIsTrue("multinestsensor");
		if(useMultiNestSensor){
			Arguments nestSensorArgs = new Arguments("nestradius=0.05,naivesensor=" + naiveSensor + ",noisenabled=" + noiseEnabled);
			sensors.add(new InAreaASensor(simulator, sensors.size() + 1, this, nestSensorArgs));
			sensors.add(new InAreaBSensor(simulator, sensors.size() + 1, this, nestSensorArgs));
			sensors.add(new GroupAreaASensor(simulator, sensors.size() + 1, this, nestSensorArgs));
			sensors.add(new GroupAreaBSensor(simulator, sensors.size() + 1, this, nestSensorArgs));
		}

		String maxSpeed = (args.getArgumentIsDefined("maxspeed") ? ("maxspeed=" + args.getArgumentAsDouble("maxspeed")) : "");
		Arguments twoWheelsArgs = new Arguments(maxSpeed);
		actuators.add(new ThymioTwoWheelActuator(simulator, actuators.size()+1, twoWheelsArgs));
		if(stopActuator)
			actuators.add(new StopActuator(simulator, actuators.size() + 1, null));

		this.startLogger(args.getArgumentAsStringOrSetDefault("out", ""));
		//setStatus("Running!\n");
		if(logger != null)
			logger.logMessage(LogCodex.encodeLog(LogType.MESSAGE, "Running!\n"));

	}

	@Override
	public void shutdown(){
		logger.logMessage(LogCodex.encodeLog(LogType.MESSAGE, "Shutting down Controller..."));

		if (logger != null)
			logger.stopLogging();

		System.out.println("# Finished Controller cleanup!");
	} 

	@Override
	public void updateSensors(double simulationStep, ArrayList<PhysicalObject> teleported) {
		super.updateSensors(simulationStep, teleported);

		for(CIBehavior b : alwaysActiveBehaviors)
			b.step(simulationStep);

		//SimulatedThymioOnlineEvoControllerCIBehaviour updated when the simulation updates the controllers.
		if(activeBehavior != null && (!(activeBehavior instanceof SimulatedThymioOnlineEvoControllerCIBehaviour))) {
			activeBehavior.step(simulationStep);
		}
	}

	@Override
	public void updateActuators(Double time, double timeDelta) {
		super.updateActuators(time, timeDelta);
		this.broadcastHandler.update(time);
	}

	@Override
	public void setMotorSpeeds(double leftMotor, double rightMotor) {
		if(wheels == null)
			wheels = (ThymioTwoWheelActuator) getActuatorByType(ThymioTwoWheelActuator.class);

		//TODO: (OLD)
		//[-1,1] to [0,1] -- why?
		//double leftSpeed = leftMotor/2 + 0.5;
		//double rightSpeed = rightMotor/2 + 0.5;

		//still in [0,1]
		double leftSpeed = leftMotor, rightSpeed = rightMotor;

		double signedLeft = leftMotor * 2 - 1;
		double signedRight = rightMotor * 2 - 1;

		this.setSigned(signedLeft, signedRight);
		//leftPercentage = leftSpeed;
		//rightPercentage = rightSpeed;

		wheels.setLeftWheelSpeed(leftSpeed);
		wheels.setRightWheelSpeed(rightSpeed);
	}


	protected double signedLeft, signedRight;
	private void setSigned(double signedLeft, double signedRight) {
		this.signedLeft = signedLeft;
		this.signedRight = signedRight;
	}

	public double getSignedLeft(){
		return signedLeft;
	}

	public double getSignedRight(){
		return signedRight;
	}

	@Override
	public List<Short> getInfraredSensorsReadings() {
		List<Short> readings = new LinkedList<Short>();
		if(irSensor == null)
			irSensor = (ThymioIRSensor)getSensorByType(ThymioIRSensor.class);

		readings.add((short)irSensor.getSensorReading(0));
		readings.add((short)irSensor.getSensorReading(1));
		readings.add((short)irSensor.getSensorReading(2));
		readings.add((short)irSensor.getSensorReading(3));
		readings.add((short)irSensor.getSensorReading(4));
		readings.add((short)irSensor.getSensorReading(5));
		readings.add((short)irSensor.getSensorReading(6));

		return readings;
	}       

	@Override
	public double[] getCameraReadings() {
		double[] readings = new double[2];

		if(cameraSensor == null)
			cameraSensor = (ThymioCameraSensor)getSensorByType(ThymioCameraSensor.class);

		readings[0] = cameraSensor.getSensorReading(0);
		readings[1] = cameraSensor.getSensorReading(1);

		return readings;
	}

	@Override
	public double getTimeSinceStart() {
		return simulator.getTime()*10;
	}

	@Override
	public void begin(HashMap<String, CIArguments> args) { }

	@Override
	public ArrayList<Entity> getEntities() {
		return entities;
	}

	@Override
	public ArrayList<CISensor> getCISensors() {
		return cisensors;
	}

	@Override
	public String getNetworkAddress() {
		return getId()+":"+getId()+":"+getId()+":"+getId();
	}

	@Override
	public BroadcastHandler getBroadcastHandler() {
		return broadcastHandler;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	@Override
	public Vector2d getVirtualPosition() {
		return virtualPosition;
	}

	@Override
	public void setVirtualPosition(double x, double y) {
		if(virtualPosition == null)
			virtualPosition = new Vector2d(x, y);
		else
			virtualPosition.set(x, y);
	}

	@Override
	public Double getVirtualOrientation() {
		return virtualOrientation;
	}

	@Override
	public void setVirtualOrientation(double orientation) {
		virtualOrientation = orientation;
	}

	@Override
	public double getThymioRadius() {
		return getRadius();
	}

	@Override
	public String getInitMessages() {
		return "Simulated thymio with ID "+getId();
	}

	@Override
	public void processInformationRequest(Message request, ConnectionHandler conn) {
		Message response = null;

		for (MessageProvider p : getMessageProviders()) {
			response = p.getMessage(request);

			if (response != null)
				break;
		}

		if(conn != null && response != null) {
			conn.sendData(response);
		}
	}

	@Override
	public void reset() {
		leftWheelSpeed = 0;
		rightWheelSpeed = 0;
	}

	@Override
	public RobotLogger getLogger() {
		return logger;
	}

	@Override
	public List<MessageProvider> getMessageProviders() {
		//We only do this here because messageProviders might not be necessary
		//most of the times, and it saves simulation time
		if(messageProviders == null) {
			initMessageProviders();
		}

		return messageProviders;
	}

	private void initMessageProviders() {
		messageProviders = new ArrayList<MessageProvider>();

		messageProviders.add(new EntityMessageProvider(this));
		messageProviders.add(new EntitiesMessageProvider(this));
		messageProviders.add(new BehaviorMessageProvider(this));
		messageProviders.add(new NeuralActivationsMessageProvider(this));
		messageProviders.add(new LogMessageProvider(this));

		messageProviders.add(new ThymioControllerGenerationMessageProvider(this));
		messageProviders.add(new ThymioExecutingMessageProvider(this));
		messageProviders.add(new ThymioPopulationStatusMessageProvider(this));
	}

	@Override
	public String getStatus() {
		if(getActiveBehavior() != null)
			return "Running behavior "+getActiveBehavior().getClass().getSimpleName();
		return "Idle";
	}

	@Override
	public void startBehavior(CIBehavior b) {
		//OLD
		/*stopActiveBehavior();
		activeBehavior = b;
		activeBehavior.start();
		log("Starting CIBehavior "+b.getClass().getSimpleName());*/

		//NEW
		stopActiveBehavior();
		activeBehavior = b;
		//		startBehavior = true;
		//		behaviorTimeStep = 0;
		//before sleeping, log
		//TODO: Improve
		if(logger != null && activeBehavior instanceof ThymioOnlineEvoControllerCIBehaviour){
			ThymioOnlineEvoControllerCIBehaviour c = (ThymioOnlineEvoControllerCIBehaviour) activeBehavior;
			c.setLogger(this.logger);
		}


		activeBehavior.start();

		String str= "Starting CIBehavior "+activeBehavior.toString();
		//System.out.println(str);
		log(LogCodex.encodeLog(LogType.MESSAGE, str));
	}

	@Override
	public void stopActiveBehavior() {
		if (activeBehavior != null) {
			activeBehavior.cleanUp();
			log("Stopping CIBehavior "+activeBehavior.getClass().getSimpleName());
			activeBehavior = null;
			setMotorSpeeds(0, 0);
		}
	}

	@Override
	public CIBehavior getActiveBehavior() {
		return activeBehavior;
	}

	private void log(String msg) {
		if(logger != null)
			logger.logMessage(msg);
	}

	public void startLogger(String outputFolder) {
		//System.out.println("LOGGER: ON");
		SimulatedThymioFileLogger fileLogger = new SimulatedThymioFileLogger(this);
		fileLogger.setOutputFolder(outputFolder);
		//System.out.println("OUT: " + outputFolder);
		fileLogger.start();
		this.logger = fileLogger;
	}



	@Override
	public double getLeftMotorSpeed() {
		return leftPercentage;
	}

	@Override
	public double getRightMotorSpeed() {
		return rightPercentage;
	}

	@Override
	public void replaceEntity(Entity e) {
		synchronized(entities){
			entities.remove(e);
			entities.add(e);
		}
	}

	@Override
	public void setProperty(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getMaxSpeedPerSec() {
		return 0.20;
	}

	@Override
	public void setParameter(String name, double value) {
		this.runtimeParameters.put(name, value);
	}


	@Override
	public double getParameter(String name){
		return this.runtimeParameters.get(name);
	}


	@Override
	public void setRobotId(int id) {
		this.id = id;
	}

	@Override
	public int getRobotId(){
		return id;
	}

	/**
	 * 	prox.ground.reflected : amount of light received when the sensor emits infrared, 
	 * varies between 0 (no reflected light) and 1023 (maximum reflected light)
	 */
	@Override
	public List<Short> getGroundReflectedLightReadings() {
		List<Short> readings = new LinkedList<Short>();
		if(this.useNestSensor){
			if(nestSensor == null)
				nestSensor = (ThymioNestSensor)getSensorByType(ThymioNestSensor.class);

			double reading = nestSensor.getSensorReading(0)*1023;
			readings.add((short) reading);
		}
		else if(this.useMultiNestSensor){
			if(areaASensor == null)
				areaASensor = (InAreaASensor)getSensorByType(InAreaASensor.class);
			
			if(areaBSensor == null)
				areaBSensor = (InAreaBSensor)getSensorByType(InAreaBSensor.class);
			
			if(areaASensor.isInNest())
				readings.add((short) 0);
			else if(areaBSensor.isInNest())
				readings.add((short) 1023);
			else
				readings.add((short) 500);
		}
		return readings;
	}


	protected HashMap<Integer, boolean[]> positions = new HashMap<Integer, boolean[]>();

	@Override
	public void updateRobotsPositioning(int otherRobot, boolean areaA,
			boolean areaB) {
		if(otherRobot != this.id)
			this.positions.put(otherRobot, new boolean[]{areaA, areaB});		
	}

	@Override
	public HashMap<Integer, boolean[]> getRobotsPositioning() {
		return this.positions;
	}

	@Override
	public void update(Simulator simulator) {}

	@Override
	public void terminate(Simulator simulator) {
		this.shutdown();
	}
}