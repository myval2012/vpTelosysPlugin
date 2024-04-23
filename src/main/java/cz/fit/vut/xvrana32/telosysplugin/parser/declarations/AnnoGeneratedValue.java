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

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Special annotation declaration for parsing @GeneratedValue
 * <br> <br>
 * GeneratedValue is represented by 4 stereotypes:
 * <ul>
 *     <li>GeneratedValueAUTO</li> - Create additional first parameter "AUTO"
 *     <li>GeneratedValueIDENTITY</li> - Create additional first parameter "IDENTITY"
 *     <li>GeneratedValueSEQUENCE</li> - Create additional first parameter "SEQUENCE"
 *     <li>GeneratedValueTABLE</li> - Create additional first parameter "TABLE"
 * </ul>
 */
public class AnnoGeneratedValue extends AnnoDeclarationMultiple {

    public AnnoGeneratedValue(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        Anno newAnno = new Anno(annoType);

        // add the fist parameter
        IParameter firstParameter;
        if (name.endsWith("AUTO")) {
            firstParameter = ParameterFactory.CreateParameter(
                    "AUTO",
                    ParameterFactory.ValueType.STRING,
                    false,
                    false);
        } else if (name.endsWith("IDENTITY")) {
            firstParameter = ParameterFactory.CreateParameter(
                    "IDENTITY",
                    ParameterFactory.ValueType.STRING,
                    false,
                    false);
        } else {
            List<IParameter> parameters = new ArrayList<>(params.length);
            findTaggedValues(vPElement, vPStereotype, model, parameters);

            if (name.endsWith("SEQUENCE")) {
                firstParameter = ParameterFactory.CreateParameter(
                        "SEQUENCE",
                        ParameterFactory.ValueType.STRING,
                        false,
                        false);
                if (!hasMandatoryValuesSEQUENCE(parameters)) {
                    throw new Exception(String.format("One of Tagged values of %s does not have a value.", name));
                }
            } else {
                firstParameter = ParameterFactory.CreateParameter(
                        "TABLE",
                        ParameterFactory.ValueType.STRING,
                        false,
                        false);
                if (hasMandatoryValuesTABLE(parameters)){
                    throw new Exception(String.format("One of Tagged values of %s does not have a value.", name));
                }
            }
            newAnno.addParameter(firstParameter);

//            List<IParameter> parameters = new ArrayList<>(params.length);
//            findTaggedValues(vPElement, vPStereotype, model, parameters);
//
//            // check mandatory values, for GeneratedValue if first parameter from list has value,
//            // then all except last have to have value too.
//            boolean mustHaveValues = parameters.get(0).getValue() != null;
//            for (int i = 0; i < parameters.size() - 1; i++) {
//                IParameter parameter = parameters.get(i);
//                if ((parameter.getValue() == null && mustHaveValues) ||
//                        (parameter.getValue() != null && !mustHaveValues)) {
//                    throw new Exception(String.format("Tagged value %s does not have a value.",
//                            params[i].name));
//                }
//            }

            newAnno.addParameters(parameters);
            return newAnno;
        }

        newAnno.addParameter(firstParameter);

        return newAnno;
    }

    private boolean hasMandatoryValuesSEQUENCE(List<IParameter> parameters) {

        if (parameters.get(0).getValue() != null) {
            // if first has a value then a second needs to have value too
            return parameters.get(1).getValue() != null;
        }
        // if first doesn't have a value then all can't have a value
        return parameters.get(1).getValue() == null && parameters.get(2).getValue() == null;
    }

    private boolean hasMandatoryValuesTABLE(List<IParameter> parameters) {
        boolean[] hasValue = new boolean[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            hasValue[i] = parameters.get(i).getValue() != null;
        }

        // if 1st has a value, 2nd has to have a value
        if (hasValue[0]) {
            if (!hasValue[1]) {
                return false;
            }
        }
        // if 2nd has a value, 3rd and 4th has to have value
        if (hasValue[2]) {
            return hasValue[3] && hasValue[4];
        }

        // if 1st doesn't have a value, nothing can have a value
        return !hasValue[0] &&
                !hasValue[1] &&
                !hasValue[3] &&
                !hasValue[4] &&
                !hasValue[5];
    }
}
