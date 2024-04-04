package cz.fit.vut.xvrana32.telosysplugin;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.parser.ProjectParser;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import org.telosys.tools.api.TelosysProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VPGenerateActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        Logger.resetStats();

        try {
            Logger.logFile = new FileWriter(Logger.LOG_FILE_PATH);
        } catch (IOException e) {
            Logger.logE(String.format("Something went wrong when opening log file: %s", e.getMessage()));
            return;
        }

        ViewManager viewManager = ApplicationManager.instance().getViewManager();

        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        ProjectParser projectParser = new ProjectParser();
        List<Model> models;

        try {
            models = projectParser.parse(project);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                Logger.logE(String.format("Unhandled error occurred: %s", e));
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    Logger.log(stackTraceElement.toString());
                }
            } else {
                Logger.logE(e.getMessage());
            }
            return;
        }

//        // faster creation for debugging and testing
//        Logger.log("Creating log file...");
//        try {
//            for (Model model : models) {
//                Logger.logFile.write(model.toString());
//            }
//            Logger.log("Log file generated successfully");
//            Logger.logFile.close();
//        } catch (Exception e) {
//            Logger.log(String.format("Unable to write to log file: %s", e.getMessage()));
//        }

        try {
            TelosysProject telosysProject = new TelosysProject(Logger.TELOSYS_TEST_FOLDER);
            telosysProject.initProject();

            for (Model model : models) {
                String modelName = model.getName();

                if (telosysProject.modelFolderExists(modelName)) {
                    Logger.logD("Model already exists... updating model");
                    for (Entity entity : model.getEntities()){
                        File entityF = telosysProject.getDslEntityFile(modelName, entity.getName());
                        if (!entityF.exists()){
                            entityF = telosysProject.createNewDslEntity(modelName, entity.getName());
                        }
                        FileWriter entityFW = new FileWriter(entityF);
                        entityFW.write(entity.toString());
                        entityFW.close();
                    }
                } else {
                    File modelF = telosysProject.createNewDslModel(modelName);
                    for (Entity entity : model.getEntities()) {
                        File entityF = telosysProject.createNewDslEntity(modelName, entity.getName());
                        FileWriter entityFW = new FileWriter(entityF);
                        entityFW.write(entity.toString());
                        entityFW.close();
                    }
                }

            }
        } catch (Exception e) {
            Logger.logE(e.getMessage());
        }

        // get all root models and their model types
//        Iterator iter = project.modelElementIterator();
//        while (iter.hasNext()) {
//            IModelElement modelElement = (IModelElement) iter.next();
//            viewManager.showMessage("Found model: " + modelElement.getName() + " of type: " +
//                    modelElement.getModelType(), "Telosys plugin");
//        }

        // get all models of modelType "Model" print their names and also warn against parsing the helper model
//        IModelElement[] models = project.toModelElementArray("Model");
//        for (IModelElement model : models) {
//            viewManager.showMessage(
//                    "found model named: " + model.getName() +
//                            (model.getName().equals(Constants.DSLConstants.DSL_HELPER_MODEL_NAME) ?
//                                    " !!! This model should be skipped during parsing !!!" : ""),
//                    Constants.PluginConstants.MESSAGE_TAG);
//        }

        // get all model types in my focused project
//        String[] modelTypes = ApplicationManager
//                .instance()
//                .getProjectManager()
//                .getAllModelTypes(project, true, false);
//
//        for (String modelType: modelTypes) {
//            viewManager.showMessage("Found model type: " + modelType, "Telosys plugin");
//        }
        Logger.log("Generating Telosys models completed.");
        Logger.logStats();
    }

    @Override
    public void update(VPAction vpAction) {

    }
}
