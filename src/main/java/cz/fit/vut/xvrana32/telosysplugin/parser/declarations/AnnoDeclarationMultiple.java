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
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

import java.util.List;

/**
 * Extends {@link AnnoDeclaration} with methods for handling multiple tagged values.
 */
public abstract class AnnoDeclarationMultiple extends AnnoDeclaration {
    public AnnoDeclarationMultiple(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    /**
     * Finds tagged values associated with given stereotype and adds them to list of  parameters.
     * @param vPElement ModelElement with searched tagged values.
     * @param vPStereotype Stereotype associated with searched tagged values.
     * @param model Context.
     * @param parameters List to add Parameters into.
     * @throws Exception If at least one tagged value is not found or is invalid.
     */
    protected void findTaggedValues(IModelElement vPElement,
                                    IStereotype vPStereotype,
                                    Model model,
                                    List<IParameter> parameters) throws Exception {

        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();
        for (ParamDeclaration paramDeclaration : params) {

            ITaggedValue vPTaggedValue = getValidTaggedValue(vPTaggedValueContainer, vPStereotype, paramDeclaration);

            if (vPTaggedValue != null) {
                parameters.add(ParameterFactory.CreateParameter(vPTaggedValue, model,
                        paramDeclaration.textQuoted, paramDeclaration.isAbsolute));
            } else {
                throw new Exception(String.format(
                        "Tagged value %s is missing or is not associated with %s stereotype.",
                        paramDeclaration.name,
                        vPStereotype.getName()));
            }
        }
    }
}
