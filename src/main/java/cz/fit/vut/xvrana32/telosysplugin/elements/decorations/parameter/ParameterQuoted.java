package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;


/**
 * Value of this parameter should be printed in quotes.
 */
public class ParameterQuoted extends ParameterBase {
    public ParameterQuoted(Object _value) {
        super(_value);
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        return String.format("\"%s\"", value.toString());
    }
}
