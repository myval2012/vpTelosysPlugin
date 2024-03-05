package cz.fit.vut.xvrana32.telosysplugin;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.parser.ProjectParser;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

public class VPVerifyActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        ProjectParser projectParser = new ProjectParser();

        try {
            projectParser.parse(project);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                Logger.logE(String.format("Unhandled error occurred: %s", e));
                for (StackTraceElement stackTraceElement : e.getStackTrace()){
                    Logger.log(stackTraceElement.toString());
                }
            } else {
                Logger.logE(e.getMessage());
            }
        }
    }

    @Override
    public void update(VPAction vpAction) {

    }
}
