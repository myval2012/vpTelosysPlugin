/**
 *
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cz.fit.vut.xvrana32.telosysplugin.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.parser.ProjectParser;
import cz.fit.vut.xvrana32.telosysplugin.utils.Config;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import org.telosys.tools.api.TelosysProject;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

/**
 * Compiles all models and creates Telosys project with compiled models. If project already exists in location given in
 * {@link Config}, only update existing models / create new.
 */
public class VPGenerateActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        Thread thread = new Thread(() -> performActionHelper(vpAction));
        thread.start();
    }

    @Override
    public void update(VPAction vpAction) {

    }

    private void performActionHelper(VPAction vpAction){
        Logger.resetStats();

        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        ProjectParser projectParser = new ProjectParser();
        List<Model> models;

        try {
            models = projectParser.parse(project);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                Logger.logE(String.format("Unhandled error occurred: %s", e));
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    Logger.logE(stackTraceElement.toString());
                }
            } else {
                Logger.logE(e.getMessage());
            }
            return;
        }

        try {
            TelosysProject telosysProject = new TelosysProject(Config.getTelosysProjectFolder().getAbsolutePath());
            telosysProject.initProject();

            for (Model model : models) {
                String modelName = model.getName();

                if (telosysProject.modelFolderExists(modelName)) {
                    Logger.logD("Model already exists... updating model");
                    for (Iterator<Entity> it = model.getEntitiesIterator(); it.hasNext(); ) {
                        Entity entity = it.next();
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
                    for (Iterator<Entity> it = model.getEntitiesIterator(); it.hasNext(); ) {
                        Entity entity = it.next();
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

        Logger.logI("Generating Telosys models completed.");
        Logger.logStats();
    }
}
