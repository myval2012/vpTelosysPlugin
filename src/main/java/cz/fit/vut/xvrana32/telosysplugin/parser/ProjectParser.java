package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.CascadeOptions;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;

import java.util.*;

public class ProjectParser {

    List<Model> models = new ArrayList<>();

    public List<Model> parse(IProject project) throws Exception {
        boolean isGTTValid = false;
        IModelElement[] vPModels = project.toModelElementArray("Model");

        // find support model and check it
        for (IModelElement vPModel : vPModels) {
            if (vPModel.getName().equals(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME)) {
                if (!checkGTTSuppModel(vPModel)) {
                    throw new Exception(String.format(
                            "The supporting model %s or one of its definitions is invalid / not found.",
                            Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
                }
                isGTTValid = true;
                break;
            }
        }

        if (!isGTTValid) {
            throw new Exception(String.format(
                    "The supporting model %s was not found in the project root. Please initialize the project before compilation.",
                    Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
        }

        // parse the given models
        for (IModelElement vPModel : vPModels) {
            if (vPModel.getName().equals(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME)) {
                continue; // skip the support model
            }

            ModelParser modelParser = new ModelParser();
            models.add(modelParser.parse(vPModel));
        }

        return models;
    }

    private boolean checkGTTSuppModel(IModelElement vPModel) {
        IModelElement[] vPCascadeOptionsClass = vPModel.toChildArray("Class");
        IModelElement[] vPConstraints = vPModel.toChildArray("ConstraintElement");

        // check the cascade options class
        // there must be only one stereotype <<enumeration>>
        // The name of the class must be GTT_CASCADE_OPTIONS_CLASS_NAME
        if (vPCascadeOptionsClass.length != 1 ||
                vPCascadeOptionsClass[0].stereotypeCount() != 1 ||
                !vPCascadeOptionsClass[0].toStereotypeModelArray()[0].getName().equals("enumeration") ||
                !vPCascadeOptionsClass[0].getName().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
            return false;
        }

        // check each enumeration literal
        int literalsDefinedCount = 0;
        for (IModelElement vpEnumLiteral : vPCascadeOptionsClass[0].toChildArray()) {
            // the enumeration literal must be of model type EnumerationLiteral
            if (!vpEnumLiteral.getModelType().equals("EnumerationLiteral")) {
                return false;
            }

            // the enumeration elements must be from the CascadeOptions
            boolean isLiteralValid = false;
            for (CascadeOptions option : CascadeOptions.values()) {
                if (option.toString().equals(vpEnumLiteral.getName())) {
                    isLiteralValid = true;
                    break;
                }
            }

            if (!isLiteralValid) {
                return false;
            }
            literalsDefinedCount++;
        }

        // all literals must be defined
        if (literalsDefinedCount != CascadeOptions.values().length){
            return false;
        }

        // check constraints (there must be only constraints defined in the GTT_CONSTRAINT_NAMES)
        int constraintsDefinedCount = 0;
        for (IModelElement vPConstraint : vPConstraints){
            if (!Constants.GTTSuppModelConstants.GTT_CONSTRAINT_NAMES.contains(vPConstraint.getName())){
                return false;
            }
            constraintsDefinedCount++;
        }

        return constraintsDefinedCount == Constants.GTTSuppModelConstants.GTT_CONSTRAINT_NAMES.size();
    }
}
