package erihanse.main;

import gui.CombinedGui;

class ConfigMain {
    public static void main(String[] args) {
        String classpath = System.getProperty("java.class.path");
        // new CombinedGui(new String[]{"--gui","classname=BlenderRenderer,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer))"});
        // new CombinedGui(new String[]{"--gui","classname=ResultViewerGui,renderer=(classname=TraceRenderer))"});
        new CombinedGui(
                new String[] { "--gui", "classname=ResultViewerGui,renderer=(classname=AdHocNetworkRenderer))" });
    }
}