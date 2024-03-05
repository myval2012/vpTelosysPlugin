package cz.fit.vut.xvrana32.telosysplugin;

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
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

public class VPInitActionController implements VPActionController {
    @Override
    public void performAction(VPAction vpAction) {
        Logger.resetStats();

        // if GTTSuppModel is not defined, create it
        IModel gTTSuppModel = findGTTSuppModel();
        if (gTTSuppModel == null) {
            gTTSuppModel = IModelElementFactory.instance().createModel();
            gTTSuppModel.setName(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME);
        }

        // if Cascade class is not defined, create it
        IClass gTTCascadeOptionClass = findGTTCascadeOptionClass(gTTSuppModel);
        if (gTTCascadeOptionClass == null) {
            gTTCascadeOptionClass = (IClass) gTTSuppModel.createChild(IModelElementFactory.MODEL_TYPE_CLASS);
            gTTCascadeOptionClass.setName(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME);

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
        for (String gTTConstraintName : Constants.GTTSuppModelConstants.GTT_CONSTRAINT_NAMES) {
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
        // code below is modified code from https://forums.visual-paradigm.com/t/how-to-stor-config-info-for-plugin/11772/4
        IModelElement[] vPElements = gTTSuppModel.toChildArray(IModelElementFactory.MODEL_TYPE_CLASS);
        for (IModelElement vPElement : vPElements) {
            // find out the LoginInformation from project.
            if (vPElement.getName().equals("config")) {
                return; // the initialization is complete
            }
//            if ("LoginInformation".equals(((IGenericModel) lElement).getGenericModelType())) {
//                IGenericModel lLoginInformation = (IGenericModel) lElement;
//                String lConnectionString = lLoginInformation.getTaggedValues().getTaggedValueByName("Connection String").getValue();
//                String lRepositoryName = lLoginInformation.getTaggedValues().getTaggedValueByName("Repository Name").getValue();
//
//            }
        }
        // config is not there create it
        createConfig(gTTSuppModel);
        Logger.log("Project was initialized.");
        Logger.logStats();
    }

    private void createConfig(IModel gTTSuppModel) {
        // create the config model
        IClass configModel = (IClass) gTTSuppModel.createChild(IModelElementFactory.MODEL_TYPE_CLASS);
        configModel.setName("config");

        // create the tagged values, for each key value pair in config
        ITaggedValueContainer taggedValueContainer = IModelElementFactory.instance().createTaggedValueContainer();
        configModel.setTaggedValues(taggedValueContainer);

        ITaggedValue packageSeparator = taggedValueContainer.createTaggedValue();
        packageSeparator.setName("Package Separator");
        packageSeparator.setValue(".");

        ITaggedValue telosysProjectDir = taggedValueContainer.createTaggedValue();
        telosysProjectDir.setName("Telosys project directory");
        telosysProjectDir.setValue(
                ApplicationManager.instance().getProjectManager().getProject().getProjectFile().getParent());
    }

    private IClass findGTTCascadeOptionClass(IModel gTTSuppModel) {
        return (IClass) findModelElementHelper(gTTSuppModel.toChildArray(IModelElementFactory.MODEL_TYPE_CLASS),
                Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME);
    }

    @Override
    public void update(VPAction vpAction) {

    }

    private IModel findGTTSuppModel() {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();

        return (IModel) findModelElementHelper(project.toModelElementArray(IModelElementFactory.MODEL_TYPE_MODEL),
                Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME);
    }

    private IModelElement findModelElementHelper(IModelElement[] vpModelElements, String searchedModel) {
        for (IModelElement vPModelElement : vpModelElements) {
            if (vPModelElement.getName().equals(searchedModel)) {
                return vPModelElement;
            }
        }
        return null;
    }

    private void createStereotypes(String modelType, AnnoDeclaration[] annoDeclarations) {
        IModelElement[] vPStereotypes = ApplicationManager.instance().getProjectManager().getSelectableStereotypesForModelType(
                modelType,
                ApplicationManager.instance().getProjectManager().getProject(),
                false);

        // check if attribute annotation stereotype
        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
//            Logger.log(String.format("Checking for stereotype %s in modelType %s", annoDeclaration.name, modelType));
            boolean isDefined = false;
            for (IModelElement vPStereotype : vPStereotypes) {
                if (annoDeclaration.name.equals(vPStereotype.getName().substring(1))) {
//                    Logger.log("I have found the stereotype!!!");
                    isDefined = true;
                    break;
                }
            }

            if (!isDefined) {
//                Logger.log("The stereotype was not found.");
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
//            Logger.log("continuing...");
        }
    }
}
