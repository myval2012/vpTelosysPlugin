package cz.fit.vut.xvrana32.telosysplugin.elements;

public class ParameterLink extends ParameterBase{
    public ParameterLink(Object _value) {
        super(_value);
    }

    @Override
    public String toString() {
        return ((Element) value).getName();
    }
}
