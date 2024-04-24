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
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.CascadeOptions;
import cz.fit.vut.xvrana32.telosysplugin.parser.AttrDecorationParser;
import cz.fit.vut.xvrana32.telosysplugin.parser.EntityDecorationParser;
import cz.fit.vut.xvrana32.telosysplugin.parser.LinkDecorationParser;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ParamDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.utils.Config;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

/**
 * Create metamodel inside the Visual Paradigm project. If metamodel exists add non-existing definitions to it.
 */
public class VPInitActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        Thread thread = new Thread(() -> performActionHelper(vpAction));
        thread.start();
    }

    @Override
    public void update(VPAction vpAction) {

    }

    /**
     * Creates configuration class with project variables inside given metamodel.
     * @param telosysMetamodel metamodel in which to create config.
     */
    private void createConfig(IModel telosysMetamodel) {
        // code below is inspired by https://forums.visual-paradigm.com/t/how-to-stor-config-info-for-plugin/11772/4
        // original code by: peter.wong
        // the original code:

        /*
        // SAVE
        IGenericModel lLoginInfo = IModelElementFactory.createGenericModel();
        lLoginInfo.setGenericModelType("LoginInformation"); // I categorize this GenericModel is a "Login Information"

        ITaggedValueContainer lTaggedValues = IModelElementFactory.createTaggedValueContainer();
        lLoginInfo.setTaggedValues(lTaggedValues);

        ITaggedValue lConnectionString = lTaggedValues.createTaggedValue(); // create a tagged value to store Connection String
        lConnectionString.setName("Connection String");
        lConnectionString.setValue(...);

        ITaggedValue lRepositoryName = lTaggedValues.createTaggedValue(); // create another tagged value to store RepositoryName
        lRepositoryName.setName("Repository Name");
        lRepositoryName.setValue(...);

        // LOAD
        IProject lProject = ...;
        IModelElement[] lElements = lProject.toModelElementArray(IModelElementFactory.MODEL_TYPE_GENERIC_MODEL);
        for (IModelElement lElement : lElements) {
            // find out the LoginInformation from project.
            if ("LoginInformation".equals(((IGenericModel) lElement).getGenericModelType())) {
                IGenericModel lLoginInformation = (IGenericModel) lElement;
                String lConnectionString = lLoginInformation.getTaggedValues().getTaggedValueByName("Connection String").getValue();
                String lRepositoryName = lLoginInformation.getTaggedValues().getTaggedValueByName("Repository Name").getValue();

            }
        }
        */

        // create the config model
        IClass configModel = (IClass) telosysMetamodel.createChild(IModelElementFactory.MODEL_TYPE_CLASS);
        configModel.setName(Config.CONFIG_CLASS_NAME);

        // create the tagged values, for each key value pair in config
        ITaggedValueContainer taggedValueContainer = IModelElementFactory.instance().createTaggedValueContainer();
        configModel.setTaggedValues(taggedValueContainer);

        ITaggedValue packageSeparator = taggedValueContainer.createTaggedValue();
        packageSeparator.setName(Config.SEPARATOR_TAG_NAME);
        packageSeparator.setValue(".");

        ITaggedValue telosysProjectDir = taggedValueContainer.createTaggedValue();
        telosysProjectDir.setName(Config.TELOSYS_PROJECT_FOLDER_TAG_NAME);

        if (ApplicationManager.instance().getProjectManager().getProject().getProjectFile() == null) {
            Logger.logW(String.format("Your project is not saved, therefore config.'%s' was not initialized.",
                    Config.TELOSYS_PROJECT_FOLDER_TAG_NAME));
        } else {
            telosysProjectDir.setValue(
                    ApplicationManager.instance().getProjectManager().getProject().getProjectFile().getParent());
        }
    }

    private IClass findCascadeOptionClass(IModel telosysMetamodel) {
        return (IClass) findModelElementHelper(telosysMetamodel.toChildArray(IModelElementFactory.MODEL_TYPE_CLASS),
                Constants.TelosysMetamodelConstants.CASCADE_OPTIONS_CLASS_NAME);
    }

    private IModel findTelosysMetamodel() {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();

        return (IModel) findModelElementHelper(project.toModelElementArray(IModelElementFactory.MODEL_TYPE_MODEL),
                Constants.TelosysMetamodelConstants.METAMODEL_NAME);
    }

    /**
     * Finds model element with given name inside given array.
     * @param vpModelElements Array to search in.
     * @param searchedModel model element name to search for.
     * @return Model element if found, null otherwise
     */
    private IModelElement findModelElementHelper(IModelElement[] vpModelElements, String searchedModel) {
        for (IModelElement vPModelElement : vpModelElements) {
            if (vPModelElement.getName().equals(searchedModel)) {
                return vPModelElement;
            }
        }
        return null;
    }

    /**
     * Create stereotypes from given array for given model type. If stereotypes is already defined skip it.
     * @param modelType Model type of created stereotypes.
     * @param annoDeclarations Array of stereotype declarations used as base for creating stereotypes.
     */
    private void createStereotypes(String modelType, AnnoDeclaration[] annoDeclarations) {
        IModelElement[] vPStereotypes = ApplicationManager.instance().getProjectManager().getSelectableStereotypesForModelType(
                modelType,
                ApplicationManager.instance().getProjectManager().getProject(),
                false);

        // check if attribute annotation stereotype
        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
            boolean isDefined = false;
            for (IModelElement vPStereotype : vPStereotypes) {
                if (annoDeclaration.name.equals(vPStereotype.getName().substring(1))) {
                    isDefined = true;
                    break;
                }
            }

            if (!isDefined) {
                // create the stereotype
                IStereotype newStereotype = IModelElementFactory.instance().createStereotype();
                newStereotype.setBaseType(modelType);
                newStereotype.setName("@" + annoDeclaration.name);

                // create it's tagged values
                ITaggedValueDefinitionContainer newContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
                newStereotype.setTaggedValueDefinitions(newContainer);

                for (ParamDeclaration paramDeclaration : annoDeclaration.params) {
                    ITaggedValueDefinition taggedDef = newContainer.createTaggedValueDefinition();
                    taggedDef.setName(paramDeclaration.name);
                    taggedDef.setType(paramDeclaration.paramType);
                }
            }
        }
    }

    private void performActionHelper(VPAction vpAction){
        Logger.resetStats();

        // if GTTSuppModel is not defined, create it
        IModel gTTSuppModel = findTelosysMetamodel();
        if (gTTSuppModel == null) {
            gTTSuppModel = IModelElementFactory.instance().createModel();
            gTTSuppModel.setName(Constants.TelosysMetamodelConstants.METAMODEL_NAME);
        }

        // if Cascade class is not defined, create it
        IClass gTTCascadeOptionClass = findCascadeOptionClass(gTTSuppModel);
        if (gTTCascadeOptionClass == null) {
            gTTCascadeOptionClass = (IClass) gTTSuppModel.createChild(IModelElementFactory.MODEL_TYPE_CLASS);
            gTTCascadeOptionClass.setName(Constants.TelosysMetamodelConstants.CASCADE_OPTIONS_CLASS_NAME);

            // define the enum literals etc.
            gTTCascadeOptionClass.addStereotype("enumeration");
            for (CascadeOptions cascadeOption : CascadeOptions.values()) {
                IEnumerationLiteral demoEnumLit = (IEnumerationLiteral)
                        gTTCascadeOptionClass.createChild(IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL);
                demoEnumLit.setName(cascadeOption.toString());
            }
        }

        // add constraints that are missing
        IModelElement[] gTTConstraints = gTTSuppModel.toChildArray(IModelElementFactory.MODEL_TYPE_CONSTRAINT_ELEMENT);
        for (String gTTConstraintName : Constants.TelosysMetamodelConstants.CONSTRAINT_NAMES) {
            boolean found = false;
            for (IModelElement gTTConstraint : gTTConstraints) {
                if (gTTConstraint.getName().equals(gTTConstraintName)) {
                    found = true;
                }
            }

            if (!found) {
                IConstraintElement gTTNewConstraint = (IConstraintElement)
                        gTTSuppModel.createChild(IModelElementFactory.MODEL_TYPE_CONSTRAINT_ELEMENT);
                gTTNewConstraint.setName(gTTConstraintName);
            }
        }

        // add stereotypes, if they do not exist
        createStereotypes(IModelElementFactory.MODEL_TYPE_ATTRIBUTE, AttrDecorationParser.annoDeclarations);
        createStereotypes(IModelElementFactory.MODEL_TYPE_ATTRIBUTE, LinkDecorationParser.annoDeclarations);
        createStereotypes(IModelElementFactory.MODEL_TYPE_CLASS, EntityDecorationParser.annoDeclarations);

        // create a generic model config

        IClass vPConfigClass = (IClass) gTTSuppModel.getChildByName(Config.CONFIG_CLASS_NAME);
        if (vPConfigClass == null) {
            createConfig(gTTSuppModel);
        }

        Logger.logI("Project was initialized.");
        Logger.logStats();
    }
}
