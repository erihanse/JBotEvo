package erihanse.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Label;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import evolutionaryrobotics.JBotEvolver;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import gui.ResultViewerGui;
import gui.util.Graph;
import simulation.Simulator;
import simulation.Updatable;

/**
 * FitnessMonitor
 */
public class FitnessMonitor extends JFrame implements Updatable {
    JBotEvolver jBotEvolver;
    EvaluationFunction evaluationFunction;

    JLabel fitnessLabel;
    Graph fitnessGraph;

    public FitnessMonitor(JBotEvolver jBotEvolver, EvaluationFunction evaluationFunction) {
        super();
        this.evaluationFunction = evaluationFunction;

        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(fitnessLabel = new JLabel());

        setLayout(new BorderLayout());

        fitnessGraph = new Graph(); // panel
        // fitnessGraph.setShowLast(jBotEvolver.getArguments("));
        int simulationSteps = jBotEvolver.getArguments().get("--environment").getArgumentAsInt("steps");
        fitnessGraph.setShowLast(simulationSteps);
        fitnessGraph.setxLabel("Steps");
        fitnessGraph.setyLabel("Fitness");

        JPanel graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBorder(BorderFactory.createTitledBorder("Fitness Graph"));
        graphPanel.add(fitnessGraph, BorderLayout.CENTER);
        add(graphPanel);

        setSize(880, 320);
        setVisible(true);
    }

    @Override
    public void update(Simulator simulator) {
        fitnessLabel.setText(String.valueOf(simulator.getTime()));
        fitnessGraph.addData(evaluationFunction.getFitness());

    }
}