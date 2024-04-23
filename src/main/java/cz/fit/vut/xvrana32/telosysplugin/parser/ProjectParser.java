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
package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.CascadeOptions;
import cz.fit.vut.xvrana32.telosysplugin.utils.Config;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;

import java.util.*;

public class ProjectParser {
    // TODO rename supp model to metamodel
    List<Model> models = new ArrayList<>();

    public List<Model> parse(IProject project) throws Exception {
        List<IModelElement> vPModels = new ArrayList<>(Arrays.asList(project.toModelElementArray(IModelElementFactory.MODEL_TYPE_MODEL)));
//        IModelElement[] vPModels = project.toModelElementArray("Model");

//        // find support model and check it
//        boolean isGTTValid = false;
//        for (IModelElement vPModel : vPModels) {
//            if (vPModel.getName().equals(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME)) {
//                if (!checkGTTSuppModel(vPModel)) {
//                    throw new Exception(String.format(
//                            "The supporting model %s or one of its definitions is invalid / not found.",
//                            Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
//                }
//                isGTTValid = true;
//                break;
//            }
//        }
//        if (!isGTTValid) {
//            throw new Exception(String.format(
//                    "The supporting model %s was not found in the project root. Please initialize the project before compilation.",
//                    Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
//        }

        // find and validate the metamodel
        IModelElement suppModel = getSuppModel(vPModels);
        if (suppModel == null) {
            throw new Exception(String.format(
                    "The supporting model %s was not found in the project root. Please initialize the project before compilation.",
                    Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
        }
        if (!checkGTTSuppModel(suppModel)) {
            throw new Exception(String.format(
                    "The supporting model %s or one of its definitions is invalid / not found.",
                    Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME));
        }
        vPModels.remove(suppModel);


        // parse the given models
        for (IModelElement vPModel : vPModels) {
//            if (vPModel.getName().equals(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME)) {
//                continue; // skip the support model
//            }

            ModelParser modelParser = new ModelParser();
            models.add(modelParser.parse(vPModel));
        }

        return models;
    }

    private IModelElement getSuppModel(List<IModelElement> vPModels) {
        // find support model and check it
        for (IModelElement vPModel : vPModels) {
            if (vPModel.getName().equals(Constants.GTTSuppModelConstants.GTT_SUPP_MODEL_NAME)) {
                return vPModel;
            }
        }
        return null;
    }

    private boolean checkGTTSuppModel(IModelElement vPModel) throws Exception {
        IModelElement[] vPConstraints = vPModel.toChildArray(IModelElementFactory.MODEL_TYPE_CONSTRAINT_ELEMENT);

        // check the cascade options class
        // there must be only one stereotype <<enumeration>>
        // The name of the class must be GTT_CASCADE_OPTIONS_CLASS_NAME
//        if (vPCascadeOptionsClass.length != 1 ||
//                vPCascadeOptionsClass[0].stereotypeCount() != 1 ||
//                !vPCascadeOptionsClass[0].toStereotypeModelArray()[0].getName().equals("enumeration") ||
//                !vPCascadeOptionsClass[0].getName().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
//            return false;
//        }

        // check the cascade options class
        // there must be only one stereotype <<enumeration>>
        // The name of the class must be GTT_CASCADE_OPTIONS_CLASS_NAME
        if (!checkCascadeOptionsClass(
                (IClass) vPModel.getChildByName(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME))) {
            return false;
        }
//        IModelElement vPCascadeOptionsClass =
//                vPModel.getChildByName(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME);
//        if (vPCascadeOptionsClass == null ||
//                vPCascadeOptionsClass.stereotypeCount() != 1 ||
//                !vPCascadeOptionsClass.toStereotypeModelArray()[0].getName().equals("enumeration") ||
//                !vPCascadeOptionsClass.getName().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
//            return false;
//        }
//
//
//        // check each enumeration literal in cascadeClass
//        int literalsDefinedCount = 0;
//        for (IModelElement vpEnumLiteral : vPCascadeOptionsClass.toChildArray()) {
//            // the enumeration literal must be of model type EnumerationLiteral
//            if (!vpEnumLiteral.getModelType().equals("EnumerationLiteral")) {
//                return false;
//            }
//
//            // the enumeration elements must be from the CascadeOptions
//            boolean isLiteralValid = false;
//            for (CascadeOptions option : CascadeOptions.values()) {
//                if (option.toString().equals(vpEnumLiteral.getName())) {
//                    isLiteralValid = true;
//                    break;
//                }
//            }
//
//            if (!isLiteralValid) {
//                return false;
//            }
//            literalsDefinedCount++;
//        }
//
//        // all literals must be defined
//        if (literalsDefinedCount != CascadeOptions.values().length) {
//            return false;
//        }

        // check constraints (there must be only constraints defined in the GTT_CONSTRAINT_NAMES)
        int constraintsDefinedCount = 0;
        for (IModelElement vPConstraint : vPConstraints) {
            if (!Constants.GTTSuppModelConstants.GTT_CONSTRAINT_NAMES.contains(vPConstraint.getName())) {
                return false;
            }
            constraintsDefinedCount++;
        }

        // check config and the validity of file path
        IModelElement configClass = vPModel.getChildByName(Config.CONFIG_CLASS_NAME);
        if (configClass == null) {
            return false;
        }
        Config.loadConfig((IClass) configClass);

        return constraintsDefinedCount == Constants.GTTSuppModelConstants.GTT_CONSTRAINT_NAMES.size();
    }

    private boolean checkCascadeOptionsClass(IClass vPCascadeOptionsClass) {
        if (vPCascadeOptionsClass == null ||
                vPCascadeOptionsClass.stereotypeCount() != 1 ||
                !vPCascadeOptionsClass.toStereotypeModelArray()[0].getName().equals("enumeration") ||
                !vPCascadeOptionsClass.getName().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
            return false;
        }


        // check each enumeration literal in cascadeClass
        int literalsDefinedCount = 0;
        for (IModelElement vpEnumLiteral : vPCascadeOptionsClass.toChildArray()) {
            // the enumeration literal must be of model type EnumerationLiteral
            if (!vpEnumLiteral.getModelType().equals(IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL)) {
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
        return literalsDefinedCount == CascadeOptions.values().length;
    }
}
