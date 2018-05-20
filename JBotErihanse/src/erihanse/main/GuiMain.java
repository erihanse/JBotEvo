package erihanse.main;

import gui.CombinedGui;

class GuiMain {
    public static void main(String[] args) throws InterruptedException {
        new CombinedGui(
                // new String[] { "--gui",
                // "classname=ResultViewerGui,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer))"
                // });
                new String[] { "--gui",
                        "classname=AdHocResultViewerGui,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer,wallray=1))" });
        // new String[] { "--gui",
        // "classname=CIResultViewerGui,enabledebugoptions=1,renderer=(classname=AdHocNetworkRenderer))"
        // });
    }
}