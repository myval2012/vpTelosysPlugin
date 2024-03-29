package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterLink;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterNonQuoted;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterQuoted;

public class ParameterFactory {

    /**
     * Type of stored value.
     */
    public enum ValueType {
        STRING, // quoted
        TEXT, // not quoted
        LINK_ENTITY,
        LINK_ATTRIBUTE,
        LINK_LINK,
        ENUM_CASCADE,
        INT,
        FLOAT,
        BOOLEAN,
    }

    public static IParameter CreateParameter(ITaggedValue vPTaggedValue, Model model, boolean textQuoted) throws Exception {
        switch (vPTaggedValue.getType()) {
            case ITaggedValueDefinition.TYPE_TEXT:
                if (textQuoted) {
                    return new ParameterQuoted(vPTaggedValue.getValueAsText());
                }
                return new ParameterNonQuoted(vPTaggedValue.getValueAsText());
            case ITaggedValueDefinition.TYPE_MODEL_ELEMENT:
                if (vPTaggedValue.getValueAsModel() == null){
                    return new ParameterLink(null, false);
                }
                boolean isAbsolute = vPTaggedValue.getValueAsModel().getModelType().equals("Attribute");
                return new ParameterLink(model.getEntityByVpId(vPTaggedValue.getValueAsModel().getId()), isAbsolute);
            case ITaggedValueDefinition.TYPE_INTEGER:
            case ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER:
            case ITaggedValueDefinition.TYPE_BOOLEAN:
//                Logger.log(String.format("Value is of type %s",
//                        vPTaggedValue.getValue() == null ? null : vPTaggedValue.getValue().getClass().toString()));
                return new ParameterNonQuoted(vPTaggedValue.getValue());
            default:
                return null;
        }
    }

    /**
     * @param _valueType Type of stored value.
     * @param _value     Stored value.
     */
    public static IParameter CreateParameter(ValueType _valueType, Object _value) {
        switch (_valueType) {
            case STRING:
                return new ParameterQuoted(_value);
            case TEXT:
            case ENUM_CASCADE:
            case INT:
            case FLOAT:
            case BOOLEAN:
                return new ParameterNonQuoted(_value);
            case LINK_ENTITY:
            case LINK_ATTRIBUTE:
            case LINK_LINK:
                return new ParameterLink(_value, false);
            default:
                return null;
        }
    }
}
