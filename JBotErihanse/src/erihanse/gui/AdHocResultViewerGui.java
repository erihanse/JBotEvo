package erihanse.gui;

import gui.ResultViewerGui;
import simulation.JBotSim;
import simulation.util.Arguments;
import updatables.BlenderExport;

/**
 * AdHocResultViewer
 */
public class AdHocResultViewerGui extends ResultViewerGui {
    public AdHocResultViewerGui (JBotSim jBotEvolver, Arguments args) {
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
}