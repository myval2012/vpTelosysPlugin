package cz.fit.vut.xvrana32.telosysplugin.elements;

import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

/**
 * Inner Representation of Telosys DSL Parameter of annotation or tag.
 */
public class Parameter {

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

    private final ValueType valueType;
    private final Object value;

    // TODO Create a Parameter factory, or something
    public Parameter(ITaggedValue vPTaggedValue, Model model, boolean textQuoted) throws Exception {
        switch (vPTaggedValue.getType()) {
            case ITaggedValueDefinition.TYPE_TEXT:
            case ITaggedValueDefinition.TYPE_INTEGER:
            case ITaggedValueDefinition.TYPE_FLOATING_POINT_NUMBER:
                valueType = textQuoted ? ValueType.STRING : ValueType.TEXT;
                value = vPTaggedValue.getValueAsText();
                break;
            case ITaggedValueDefinition.TYPE_MODEL_ELEMENT:
                valueType = ValueType.LINK_ENTITY;
                value = model.getEntityByVpId(vPTaggedValue.getValueAsModel().getId());
                break;
//            case ITaggedValueDefinition.TYPE_ENUMERATION:
//                valueType = ValueType.ENUM_ORDINAL;
//                value = null;
//                break;
            case ITaggedValueDefinition.TYPE_BOOLEAN:
                valueType = ValueType.BOOLEAN;
                value = vPTaggedValue.getValue();
                Logger.log(String.format("The class of the value is %s", value.getClass().toString()));
                break;
            default:
                value = null;
                throw new Exception("Parameter is of illegal valueType");
        }
    }

    /**
     * @param _valueType Type of stored value.
     * @param _value     Stored value.
     */
    public Parameter(ValueType _valueType, Object _value) {
        valueType = _valueType;
        value = _value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Object getValue() {
        return value;
    }

    /**
     * @return Parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        switch (valueType) {
            case STRING:
                return String.format("\"%s\"", value);
            case TEXT:
                return value.toString();
            case LINK_ENTITY:
            case LINK_ATTRIBUTE:
            case LINK_LINK:
                return value == null ? "NULL" : ((DecoratedElement) value).getName(); // TODO remove null check
            case BOOLEAN:
                return value.toString();
            case ENUM_ORDINAL:
                // TODO implement if necessary
                throw new RuntimeException("Not implemented.");
        }
        return "";
    }
}
