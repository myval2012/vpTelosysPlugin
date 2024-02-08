package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;

/**
 * Parameter pointing to a {@link DecoratedElement}.
 */
public class ParameterLink extends ParameterBase{
    public ParameterLink(Object _value) {
        super(_value);
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        return ((DecoratedElement) value).getName();
    }
}
