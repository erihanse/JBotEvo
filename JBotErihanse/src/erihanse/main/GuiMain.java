package erihanse.main;

import gui.CombinedGui;

class GuiMain {
    public static void main(String[] args) {
        // new CombinedGui(new String[]{"--gui","classname=BlenderRenderer,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer))"});
        // new CombinedGui(new String[]{"--gui","classname=ResultViewerGui,renderer=(classname=TraceRenderer))"});
        new CombinedGui(
                new String[] { "--gui", "classname=ResultViewerGui,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer))" });
    }
}