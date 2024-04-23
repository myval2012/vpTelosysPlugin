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
package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;

public class ParameterFactory {

    /**
     * Type of stored value.
     */
    public enum ValueType {
        STRING,
        LINK,
        ENUM_CASCADE
    }

    public static IParameter CreateParameter(
            ITaggedValue vPTaggedValue,
            Model model,
            boolean textQuoted,
            boolean isAbsolute) throws Exception {
        switch (vPTaggedValue.getType()) {
            case ITaggedValueDefinition.TYPE_INTEGER:
            case ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER:
            case ITaggedValueDefinition.TYPE_BOOLEAN:
            case ITaggedValueDefinition.TYPE_TEXT:
                return textQuoted ?
                        new ParameterQuoted(vPTaggedValue.getValueAsText()) :
                        new ParameterNonQuoted(vPTaggedValue.getValueAsText());
            case ITaggedValueDefinition.TYPE_MODEL_ELEMENT:
                // TODO find out if possible
                if (vPTaggedValue.getValueAsModel() == null) {
                    return new ParameterLink(null, false);
                }

                if (vPTaggedValue.getValueAsModel().getModelType().equals(IModelElementFactory.MODEL_TYPE_ATTRIBUTE)){
                    Entity entity = model.getEntityByVpId(vPTaggedValue.getValueAsModel().getParent().getId());
                    return new ParameterLink(entity.getAttrByName(vPTaggedValue.getValueAsModel().getName()), isAbsolute);
                }
                return new ParameterLink(model.getEntityByVpId(vPTaggedValue.getValueAsModel().getId()), isAbsolute);
            default:
                throw new Exception("Parameter Factory doesn't recognise this type of tagged value");
        }
    }

//    public static IParameter CreateParameter(ITaggedValue vPTaggedValue, Model model, boolean textQuoted) throws Exception {
//        switch (vPTaggedValue.getType()) {
//            case ITaggedValueDefinition.TYPE_TEXT:
//                if (textQuoted) {
//                    return new ParameterQuoted(vPTaggedValue.getValueAsText());
//                }
//                return new ParameterNonQuoted(vPTaggedValue.getValueAsText());
//            case ITaggedValueDefinition.TYPE_MODEL_ELEMENT:
//                if (vPTaggedValue.getValueAsModel() == null) {
//                    return new ParameterLink(null, false);
//                }
//                boolean isAbsolute = vPTaggedValue.getValueAsModel().getModelType().equals("Attribute");
//                return new ParameterLink(model.getEntityByVpId(vPTaggedValue.getValueAsModel().getId()), isAbsolute);
//            case ITaggedValueDefinition.TYPE_INTEGER:
//            case ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER:
//            case ITaggedValueDefinition.TYPE_BOOLEAN:
////                Logger.log(String.format("Value is of type %s",
////                        vPTaggedValue.getValue() == null ? null : vPTaggedValue.getValue().getClass().toString()));
//                return new ParameterNonQuoted(vPTaggedValue.getValue());
//            default:
//                return null;
//        }
//    }

    public static IParameter CreateParameter(Object value, ValueType valueType, boolean textQuoted, boolean isAbsolute) {
        switch (valueType) {
            case STRING:
                return textQuoted ? new ParameterQuoted(value) : new ParameterNonQuoted(value);
            case LINK:
                return new ParameterLink(value, isAbsolute);
            case ENUM_CASCADE:
                return new ParameterNonQuoted(value);
        }
        return null;
    }

//    /**
//     * @param _valueType Type of stored value.
//     * @param _value     Stored value.
//     */
//    public static IParameter CreateParameter(ValueType _valueType, Object _value) {
//        switch (_valueType) {
//            case STRING:
//                return new ParameterQuoted(_value);
//            case TEXT:
//            case ENUM_CASCADE:
//            case INT:
//            case FLOAT:
//            case BOOLEAN:
//                return new ParameterNonQuoted(_value);
//            case LINK_ENTITY:
//            case LINK_ATTRIBUTE:
//            case LINK_LINK:
//                return new ParameterLink(_value, false);
//            default:
//                return null;
//        }
//    }
}
