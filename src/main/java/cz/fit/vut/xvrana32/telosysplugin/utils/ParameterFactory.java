package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;

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
        ENUM_ORDINAL,
        INT,
        FLOAT,
        BOOLEAN,
    }

    public static IParameter CreateParameter(ITaggedValue vPTaggedValue, Model model, boolean textQuoted) {
        switch (vPTaggedValue.getType()) {
            case ITaggedValueDefinition.TYPE_TEXT:
                if (textQuoted) {
                    return new ParameterQuoted(vPTaggedValue.getValueAsText());
                }
                return new ParameterNonQuoted(vPTaggedValue.getValueAsText());
            case ITaggedValueDefinition.TYPE_MODEL_ELEMENT:
                return new ParameterLink(model.getEntityByVpId(vPTaggedValue.getValueAsModel().getId()));
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
            case ENUM_ORDINAL:
            case INT:
            case FLOAT:
            case BOOLEAN:
                return new ParameterNonQuoted(_value);
            case LINK_ENTITY:
            case LINK_ATTRIBUTE:
            case LINK_LINK:
                return new ParameterLink(_value);
            default:
                return null;
        }
    }
}
