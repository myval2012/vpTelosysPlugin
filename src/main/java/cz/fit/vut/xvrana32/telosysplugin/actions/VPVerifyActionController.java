package cz.fit.vut.xvrana32.telosysplugin.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.parser.ProjectParser;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

public class VPVerifyActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                performActionHelper(vpAction);
            }
        };
        thread.start();
    }

    @Override
    public void update(VPAction vpAction) {

    }

    private void performActionHelper(VPAction vpAction){
        Logger.resetStats();

        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        ProjectParser projectParser = new ProjectParser();

        try {
            projectParser.parse(project);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                Logger.logE(String.format("Unhandled error occurred: %s", e));
                for (StackTraceElement stackTraceElement : e.getStackTrace()){
                    Logger.logE(stackTraceElement.toString());
                }
            } else {
                Logger.logE(e.getMessage());
            }
        }

        Logger.logI("Verification completed");
        Logger.logStats();
    }
}
