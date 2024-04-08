package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

import cz.fit.vut.xvrana32.telosysplugin.elements.Attr;
import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;

/**
 * Parameter pointing to a {@link DecoratedElement}.
 */
public class ParameterLink extends ParameterBase {
    private final boolean isAbsolute; // is absolute can only be set true for attribute value

    public ParameterLink(Object _value, boolean _isAbsolute) {
        super(_value);
        isAbsolute = _isAbsolute;
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        String str = "";
        if (isAbsolute && value instanceof Attr) {
            str = ((Attr) value).getParentEntity().getName() + ".";
        }
        return str + ((DecoratedElement) value).getName();
    }
}
