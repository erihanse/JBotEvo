package erihanse.main;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import evolutionaryrobotics.JBotEvolver;
import gui.CombinedGui;
import gui.Gui;
import gui.configuration.ConfigurationGui;
import gui.evolution.EvolutionGui;
import simulation.util.Arguments;

/**
 * EvoMain
 *  This file only runs the gui for the Evolution and Results, not the configuration.
 */
public class EvoMain {
    public static void main(String[] args) throws Exception {
        // System.out.println(usageToString());
        for (String s : args) {
            System.out.println(s);
        }

        try {
            JFrame frame = new JFrame();
            JBotEvolver jbotSim = new JBotEvolver(args);
            EvolutionGui evoPanel = new EvolutionGui(jbotSim, new Arguments(""));

            frame.add(evoPanel);
            frame.setSize(800, 600);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            evoPanel.init(jbotSim);
            evoPanel.executeEvolution();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String usageToString() {
        return "Usage: \n"
                + "  If nothing is provided on the commandline, default setting will be used for everything.\n" + "\n"
                + "  java JBotEvolver [--gui, --experiment, --environment, --robot, --controller, --population, --evaluation]\n"
                + "       --gui           name=..,arg1=..   use the gui [name] and pass it args\n"
                + "       --experiment    name=..,arg1=..   use the experiment [name] and  pass it args\n"
                + "       --environment   name=..,arg1=..   use the environment [name] and  pass it args\n"
                + "       --robot         name=..,arg1=..   use the robot model [name] and  pass it args\n"
                + "       --controller    name=..,arg1=..   use  the controller [name] and  pass it args\n" + "\n"
                + "  To run evolutions, the following settings can be used:\n"
                + "       --population    name=..,arg1=..   enable evolution and use the popolation [name] and  pass it args\n"
                + "       --evaluation    name=..,arg1=..   use the evaluation function [name] and pass it args\n"
                + "       --random-seed   #                 use the random seed specified in #\n"
                + "       --output        [directory]       use [directory] for saving populuations and logs\n" + "\n"
                + "  If a given factory has been nicely documented, you should be able to run it with a 'help' in\n"
                + "  the args. to get a list of the available implementation. For instance, to learn which experiments\""
                + "  are available, run \"java JBotEvolver --population help\". And to learn which implementation specific\"\n"
                + "  arguments that the mulambda population takes, run: \"java JBotEvolver --population name=mulambda,help\n"
                + "\n"
                + "  Arguments can also be stored in a file. If the first argument does not start with \"--\", the argument\n"
                + "  is interpreted as a file name and the arguments are read and process from that file before any other\n"
                + "  arguments on the commandline are processed. Example:\n" + " \n"
                + "    java JBotEvolver myarguments.txt --gui name=...,... --...\n" + " \n"
                + "  will cause JBotEvolver to first read myarguments.txt (remove all white spaces and add white spaces in front\n"
                + "  of \"--\", so that you can create more human readable configuration/argument files) and processed, before\n"
                + "  the --gui argument and subsequent arguments are read. Arguments on the commandline can overwrite the\n"
                + "  arguments in the file.\n" + "\n" + "  Parallel:\n"
                + "       --slave [port #]                 		  start-up a slave on port #\n"
                + "       --master [slaves.txt]            		  start-up as a server with the slaves specified in a file with\n"
                + "                                        		  one [IP port] per line.\n"
                + "       --client [server=server.name] [port=#]    start-up as a client to server server.name on port #"
                + "       --paralleler-client [server=server.name] [port=#]    start-up as a client fot the parallerer infrastrucure, connects to server.name on port #";
    }
}