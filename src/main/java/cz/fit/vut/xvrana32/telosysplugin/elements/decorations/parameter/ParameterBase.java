package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

/**
 * Shared implementation of parameters.
 */
abstract public class ParameterBase implements IParameter {
    protected Object value;

    public ParameterBase(Object _value){
        value = _value;
    }

    @Override
    public Object getValue(){
        return value;
    }
}
