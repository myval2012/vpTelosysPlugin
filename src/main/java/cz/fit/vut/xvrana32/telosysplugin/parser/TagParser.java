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

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Tag;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

public class TagParser {
    public static Tag parseTag(IModelElement vpElement, IStereotype vPStereotype, Entity entity) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vpElement.getTaggedValues();
        IParameter param = null;

        for (ITaggedValue vPTaggedValue : vPTaggedValueContainer.toTaggedValueArray()) {
            if (vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype)) {
                if (param != null) {
                    throw new Exception("Tag can have at most one tagged value. Stereotype skipped.");
                } else {
                    if (!(vPTaggedValue.getType() == ITaggedValue.TYPE_MODEL_ELEMENT
                            || vPTaggedValue.getType() == ITaggedValue.TYPE_TEXT)) {
                        throw new Exception("The only supported tagged value types for Telosys tags are model element and text. Stereotype skipped.");
                    }

                    param = ParameterFactory.CreateParameter(vPTaggedValue, entity.getParentModel(),
                            false, true);
                }
            }
        }

        return new Tag(vPStereotype.getName().substring(1), param);
    }
}
