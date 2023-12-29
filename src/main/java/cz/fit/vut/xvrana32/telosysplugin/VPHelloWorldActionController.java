package cz.fit.vut.xvrana32.telosysplugin;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class VPHelloWorldActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        viewManager.showMessage("Hello World", "Telosys plugin");
    }

    @Override
    public void update(VPAction vpAction) {

    }
}
