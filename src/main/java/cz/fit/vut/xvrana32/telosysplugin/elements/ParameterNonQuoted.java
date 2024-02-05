package cz.fit.vut.xvrana32.telosysplugin.elements;

public class ParameterNonQuoted extends ParameterBase {
    public ParameterNonQuoted(Object _value) {
        super(_value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
