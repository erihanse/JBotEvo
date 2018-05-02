package erihanse.gui;

import evolutionaryrobotics.JBotEvolver;
import gui.ResultViewerGui;
import gui.util.GraphPlotter;
import simulation.JBotSim;
import simulation.Simulator;
import simulation.util.Arguments;

public class CIResultViewerGui extends ResultViewerGui{

	public CIResultViewerGui(JBotSim jBotEvolver, Arguments args) {
		super(jBotEvolver, args);
	}

	protected void launchGraphPlotter(JBotEvolver jbot, Simulator sim) {
		new CIGraphPlotter(jbot,sim);
	}

}
