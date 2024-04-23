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
package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.CascadeOptions;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

/**
 * Special annotation declaration for parsing @LinkByAttr
 * <br> <br>
 * The arguments for this annotation are in support entity.
 */
public class AnnoCascade extends AnnoDeclaration {

    public AnnoCascade(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        // check if the tagged value is there and read the ID of the model

        ITaggedValue vPTaggedValue = getValidTaggedValue(vPElement.getTaggedValues(), vPStereotype, params[0]);
        if (vPTaggedValue == null) {
            throw new Exception(String.format(
                    "Tagged value %s is missing or is not associated with %s stereotype.",
                    params[0].name,
                    vPStereotype.getName()));
        }

        if (vPTaggedValue.getValueAsModel() == null) {
            throw new Exception(String.format("Mandatory tagged value %s does not have a value.", params[0].name));
        }

        String supportClassId = vPTaggedValue.getValueAsModel().getId();

        // find the support entity
        Entity supportEntity = model.getSupportEntityByVpId(supportClassId);
        if (supportEntity == null) {
            throw new Exception(String.format("Support class %s was not found inside the supported class",
                    vPTaggedValue.getValueAsModel().getName()));
        }

        // add all attributes of the support entity to both the ParentEntity and the Annotation
        Anno newAnno = new Anno(annoType);
        for (IModelElement vPAttrAsModel : vPTaggedValue.getValueAsModel().toChildArray(IModelElementFactory.MODEL_TYPE_ATTRIBUTE)) {
            // check if they are all type "Cascade options"
            IAttribute vPAttr = (IAttribute) vPAttrAsModel;
            if (vPAttr.stereotypeCount() > 0) {
                Logger.logW(String.format(
                        "Support class %s: Attributes in @Cascade support class can't have stereotypes. Stereotypes of attribute %s skipped.",
                        vPAttr.getParent().getName(),
                        vPAttr.getName()));
            }

            if (!vPAttr.getTypeAsString().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {

                Logger.logW(String.format(
                        "Support class %s for stereotype @Cascade only uses attribute of type %s, attribute %s of type %s ignored.",
                        vPAttr.getParent().getName(),
                        Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME,
                        vPAttr.getName(),
                        vPAttr.getTypeAsString()));
                continue;
            }
            if (vPAttr.getInitialValue() == null) {
                Logger.logW(String.format("Attributes in support class %s without initial value are ignored." +
                                " Set initial value to indicate what cascade option you want.",
                        vPAttr.getParent().getName()));
                continue;
            }

            newAnno.addParameter(ParameterFactory.CreateParameter(
                    CascadeOptions.valueOf(vPAttr.getInitialValue()),
                    ParameterFactory.ValueType.ENUM_CASCADE,
                    false,
                    false));
        }

        // check mandatory values, for cascade it is at least one cascade option
        if (newAnno.getParametersCount() == 0) {
            throw new Exception(String.format(
                    "There has to be at least one Cascade option with initial value in the %s support class.",
                    supportEntity.getName()));
        }

        return newAnno;
    }
}
