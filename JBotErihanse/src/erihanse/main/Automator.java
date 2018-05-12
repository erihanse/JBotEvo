package erihanse.main;

import src.Controller;
import src.Evolution;
import src.Main;

/**
 * Automator for launching experiments, see
 * {@link https://github.com/BioMachinesLab/jbotevolver/wiki/Evolution-Automator}.
 */
public class Automator {
    /**
     * @param args
     * [0]: configuration file to use.
     */
    public static void main(String[] args) {
        Main main = new Main(args);

        main.execute();
    }
}