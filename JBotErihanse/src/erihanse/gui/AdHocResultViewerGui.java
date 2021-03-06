package erihanse.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import erihanse.controllers.SimulatedThymioODNetworkController;
import erihanse.environment.EAHSimpleArenaEnvironment;
import erihanse.network.NetworkNode;
import erihanse.robot.ODNetworkRobot;
import gui.ResultViewerGui;
import simulation.JBotSim;
import simulation.Simulator;
import simulation.util.Arguments;
import updatables.BlenderExport;

/**
 * AdHocResultViewer
 */
public class AdHocResultViewerGui extends ResultViewerGui {
	protected JTextField homeHopsTextField;
	protected JTextField SinkHopsTextField;

	/**
	 * Opens up a new monitor for showing the fitness over time when performing live
	 * simulations
	 */
	protected JCheckBox monitorFitnessCheckBox;
	protected boolean showFitnessMonitor;
	protected JTextField longestHomeRoute;
	protected JTextField longestSinkRoute;
	protected JTextField totalDistanceTravelled;

	protected CINeuralNetworkTopologyGraphViz graphViz2;

	public AdHocResultViewerGui(JBotSim jBotEvolver, Arguments args) {
		super(jBotEvolver, args);
	}

	@Override
	protected void loadCurrentFile() {
		String filename = currentFileTextField.getText();
		try {
			if (validFile(filename)) {

				String extra = "";
				if (System.getProperty("os.name").contains("Windows")) {
					extra = extraArguments.getText();
					extra = extra.replaceAll("\r", "");
				} else {
					extra = extraArguments.getText();
				}

				jBotEvolver.loadFile(filename, extra);
				simulator = loadSimulator();
				if (showFitnessMonitor)
					simulator.addCallback(new FitnessMonitor(jBotEvolver, evaluationFunction));
				if (exportToBlender != null && exportToBlender.isSelected())
					simulator.addCallback(new BlenderExport());
				if (simulateUntil == 0)
					playPosition.setValue(0);

				launchSimulation();

				lastCycleTime = System.currentTimeMillis();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected JPanel initRightWrapperPanel() {

		int panelWidth = 300;

		JPanel sideTopPanel = new JPanel();
		sideTopPanel.setLayout(new BoxLayout(sideTopPanel, BoxLayout.Y_AXIS));

		pauseButton = new JButton("Start/Pause");
		plotButton = new JButton("Plot Neural Activations");

		pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		sideTopPanel.add(pauseButton);
		plotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		sideTopPanel.add(plotButton);

		sideTopPanel.add(new JLabel(" "));

		JLabel sleep = new JLabel("Sleep between control steps (ms)");
		sideTopPanel.add(sleep);
		sleep.setAlignmentX(Component.CENTER_ALIGNMENT);

		sleepSlider.setMajorTickSpacing(20);
		sleepSlider.setMinorTickSpacing(5);
		sleepSlider.setPaintTicks(true);
		sleepSlider.setPaintLabels(true);
		sleepSlider.setValue(10);

		sleepSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
		sideTopPanel.add(sleepSlider);

		sideTopPanel.add(new JLabel("Play position (%)"));
		playPosition.setMajorTickSpacing(20);
		playPosition.setMinorTickSpacing(5);
		playPosition.setPaintTicks(true);
		playPosition.setPaintLabels(true);
		playPosition.setValue(0);
		playPosition.setAlignmentX(Component.CENTER_ALIGNMENT);
		sideTopPanel.add(playPosition);

		sideTopPanel.add(new JLabel(" "));
		monitorFitnessCheckBox = new JCheckBox("Show fitness monitor");
		monitorFitnessCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		sideTopPanel.add(monitorFitnessCheckBox);

		if (enableDebugOptions) {

			neuralNetworkCheckbox = new JCheckBox("Show Neural Network");
			neuralNetworkCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
			sideTopPanel.add(neuralNetworkCheckbox);
			/*
			 * neuralNetworkViewerCheckbox = new JCheckBox( "Show Neural Network #2");
			 * neuralNetworkViewerCheckbox.setAlignmentX(Component. CENTER_ALIGNMENT);
			 * sideTopPanel.add(neuralNetworkViewerCheckbox);
			 */
			exportToBlender = new JCheckBox("Export to Blender");
			exportToBlender.setAlignmentX(Component.CENTER_ALIGNMENT);
			sideTopPanel.add(exportToBlender);

			sideTopPanel.add(new JLabel(" "));
		}

		// Status panel
		JPanel statusPanel = new JPanel(new GridLayout(7, 2));
		statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		statusPanel.add(new JLabel("Control step: "));
		controlStepTextField = new JTextField("N/A");
		controlStepTextField.setHorizontalAlignment(JTextField.CENTER);
		statusPanel.add(controlStepTextField);

		statusPanel.add(new JLabel("Fitness: "));
		fitnessTextField = new JTextField("N/A");
		fitnessTextField.setHorizontalAlignment(JTextField.CENTER);
		statusPanel.add(fitnessTextField);

		statusPanel.add(new JLabel("Max Home hops: "));
		homeHopsTextField = new JTextField("N/A");
		homeHopsTextField.setHorizontalAlignment(JTextField.CENTER);
		statusPanel.add(homeHopsTextField);

		statusPanel.add(new JLabel("Max Dst Hops: "));
		SinkHopsTextField = new JTextField("N/A");
		SinkHopsTextField.setHorizontalAlignment(JTextField.CENTER);
		statusPanel.add(SinkHopsTextField);

		statusPanel.add(new JLabel("Longest home route:"));
		longestHomeRoute = new JTextField("N/A");
		statusPanel.add(longestHomeRoute);

		statusPanel.add(new JLabel("Longest sink route:"));
		longestSinkRoute = new JTextField("N/A");
		statusPanel.add(longestSinkRoute);

		statusPanel.add(new JLabel("Total distance travelled:"));
		totalDistanceTravelled = new JTextField("N/A");
		statusPanel.add(totalDistanceTravelled);

		sideTopPanel.add(statusPanel);
		// statusPanel.setPreferredSize(new Dimension(panelWidth, 200));
		statusPanel.setPreferredSize(null);

		JPanel sideWrapperPanel = new JPanel(new BorderLayout());
		sideWrapperPanel.add(sideTopPanel, BorderLayout.NORTH);
		sideWrapperPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

		pauseButton.setPreferredSize(new Dimension(panelWidth, 50));
		plotButton.setPreferredSize(new Dimension(panelWidth, 50));

		return sideWrapperPanel;
	}

	@Override
	public synchronized void update(Simulator simulator) {
		super.update(simulator);
		EAHSimpleArenaEnvironment eahenv = (EAHSimpleArenaEnvironment) simulator.getEnvironment();

		// Retrieve longest routes from home and sink
		LinkedList<? extends NetworkNode> longestHomeRoute = eahenv.getLongestRouteFromHome();
		LinkedList<NetworkNode> longestSinkRoute = eahenv.getLongestRouteFromSink();
		homeHopsTextField.setText(String.valueOf(longestHomeRoute.size()));
		SinkHopsTextField.setText(String.valueOf(longestSinkRoute.size()));

		// Display longest home route
		String longestHomeRouteText = longestHomeRoute.stream()
			.map(s -> String.valueOf(s.getId()))
			.collect(Collectors.joining("->"));
		this.longestHomeRoute.setText(longestHomeRouteText);
		this.longestHomeRoute.setToolTipText(longestHomeRouteText);

		// Display longest sink route
		String longestSinkRouteText = longestSinkRoute.stream()
			.map(s -> String.valueOf(s.getId()))
			.collect(Collectors.joining("->"));
		this.longestSinkRoute.setText(longestSinkRouteText);
		this.longestSinkRoute.setToolTipText(longestSinkRouteText);

		// Display the total distance the robots have travelled in total:
		double distanceTravelled = eahenv.getTotalDistanceTravelled();
		this.totalDistanceTravelled.setText(String.valueOf(distanceTravelled));
	}

	@Override
	protected void initListeners() {
		super.initListeners();
		monitorFitnessCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JCheckBox check = (JCheckBox) arg0.getSource();
				showFitnessMonitor = check.isSelected();
			}
		});
	}

	@Override
	protected void displayNeuralNetwork() {
		if (showNeuralNetwork && graphViz2 == null) {
			ODNetworkRobot odRobot = (ODNetworkRobot) simulator.getEnvironment().getRobots().get(0);
			SimulatedThymioODNetworkController cont = (SimulatedThymioODNetworkController) odRobot.getController();
			graphViz2 = new CINeuralNetworkTopologyGraphViz(cont.getCIController().getNetwork());
		}
		if (showNeuralNetwork)
			graphViz2.show();
	}

	@Override
	protected void updateNeuralNetworkDisplay() {
		/**
		 * Unfortunately, the way it is now, the neural network is buffered to disk,
		 * meaning the simulation will lag quite a bit. A workaround is to not display
		 * the neural networks for each time frame. That will however make the activations on
		 * each node quickly become obsolete.
		 */
		if (simulator.getTime() % 100 == 0) {
			if (showNeuralNetwork) {
				ODNetworkRobot odRobot = (ODNetworkRobot) simulator.getEnvironment().getRobots().get(0);
				SimulatedThymioODNetworkController cont = (SimulatedThymioODNetworkController) odRobot.getController();
				if (graphViz2 != null) {
					graphViz2.changeNeuralNetwork(cont.getCIController().getNetwork());

				} else {
					graphViz2 = new CINeuralNetworkTopologyGraphViz(cont.getCIController().getNetwork());
				}
			}
		}
	}
}