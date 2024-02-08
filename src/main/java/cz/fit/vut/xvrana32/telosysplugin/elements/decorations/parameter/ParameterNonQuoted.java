package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

/**
 * Parameter whose value should not be printed in quotes.
 */
public class ParameterNonQuoted extends ParameterBase {
    public ParameterNonQuoted(Object _value) {
        super(_value);
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
