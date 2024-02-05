package cz.fit.vut.xvrana32.telosysplugin.elements;


/**
 * Value of this parameter should be printed in quotes.
 */
public class ParameterQuoted extends ParameterBase {
    public ParameterQuoted(Object _value) {
        super(_value);
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value.toString());
    }
}
