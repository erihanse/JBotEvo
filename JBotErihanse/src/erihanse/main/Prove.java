package erihanse.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import src.Controller;
import evolutionaryrobotics.JBotEvolver;
import evolutionaryrobotics.NEATPostEvaluation;
import evolutionaryrobotics.PostEvaluation;
import evolutionaryrobotics.neuralnetworks.Chromosome;
import simulation.util.Arguments;
import src.Main;
import taskexecutor.TaskExecutor;

public class Prove extends Thread {

    protected Controller controller;
    protected HashMap<String, Arguments> args;
    protected String defaultArgs;
    protected Main main;
    protected String outputFolder;
    protected int nTries = 3;

    public Prove(Main main, Controller controller, String arguments) {
        this.controller = controller;
        this.defaultArgs = arguments;
        this.main = main;
    }

    @Override
    public void run() {

        String runsVariable = main.getGlobalVariable("%runs");
        int nRuns = 1;
        if (runsVariable != null)
            nRuns = Integer.parseInt(runsVariable);

        Worker[] workers = new Worker[nRuns];

        for (int i = 0; i < nRuns; i++) {
            workers[i] = new Worker(i + 1);
            workers[i].start();
        }

        for (Worker w : workers)
            try {
                w.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        main.evolutionFinished(controller.getName());
    }

    private void createConfigFile(String folderName, String configName) throws Exception {
        createFile(folderName, configName, Arguments.beautifyString(controller.getCompleteConfiguration()));
    }

    protected void createFile(String folderName, String fileName, String contents) throws Exception {
        File f = new File(folderName);

        if (!f.exists())
            f.mkdir();

        f = new File(folderName + "/" + fileName);

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(contents);
        bw.flush();
        bw.close();
    }

    protected double getAverage(double[] values) {

        double avg = 0;

        for (double i : values)
            avg += i;

        return avg / values.length;
    }

    protected double getStdDeviation(double[] values, double avg) {

        double stdDeviation = 0;

        for (double d : values)
            stdDeviation += Math.pow(d - avg, 2);

        return Math.sqrt(stdDeviation / values.length);
    }

    protected double getOverallAverage(double[][] values, int runs) {
        double[] avgs = new double[runs];

        for (int i = 0; i < avgs.length; i++)
            avgs[i] = getAverage(values[i]);

        return getAverage(avgs);
    }

    protected double getOverallStdDeviation(double[][] values, int runs) {
        double[] avgs = new double[runs];
        double[] stds = new double[runs];

        for (int i = 0; i < avgs.length; i++) {
            avgs[i] = getAverage(values[i]);
            stds[i] = getStdDeviation(values[i], avgs[i]);
        }

        double overallAverage = getAverage(avgs);

        return getStdDeviation(avgs, overallAverage);
    }

    class Worker extends Thread {

        private int run;

        public Worker(int run) {
            this.run = run;
        }

        @Override
        public void run() {
            try {
                JBotEvolver jBotEvolver;

                String[] originalArgs = Arguments.readOptionsFromFile("C:\\JBot\\thymios\\JBotErihanse\\confs\\output-Stop-N-neighbours-autom.conf");
                for (int i = 0; i < originalArgs.length; i++) {
                    originalArgs[i] = originalArgs[i].replaceAll("\\%run", this.run + "");
                }

                // String[] extraArgs = ("--output " + outputFolder + "/" + run + " --random-seed " + seed).split(" ");
                String[] extraArgs = ("--output " + outputFolder + "/" + run + " --random-seed ").split(" ");

                String[] fullArgs = new String[originalArgs.length + extraArgs.length];

                int index;
                for (index = 0; index < originalArgs.length; index++)
                    fullArgs[index] = originalArgs[index];

                for (int i = 0; i < extraArgs.length; i++)
                    fullArgs[index++] = extraArgs[i];

                jBotEvolver = new JBotEvolver(fullArgs);

                TaskExecutor taskExecutor;

                if (controller.getArguments("--executor") != null) {
                    taskExecutor = TaskExecutor.getTaskExecutor(jBotEvolver, controller.getArguments("--executor"));
                } else {
                    taskExecutor = TaskExecutor.getTaskExecutor(jBotEvolver,
                            jBotEvolver.getArguments().get("--executor"));
                }
                taskExecutor.start();
                evolutionaryrobotics.evolution.Evolution a = evolutionaryrobotics.evolution.Evolution
                    .getEvolution(jBotEvolver, taskExecutor, jBotEvolver.getArguments().get("--evolution"));
                a.executeEvolution();
                taskExecutor.stopTasks();

                main.decrementEvolutions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}