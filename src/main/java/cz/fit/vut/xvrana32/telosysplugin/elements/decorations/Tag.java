package cz.fit.vut.xvrana32.telosysplugin.elements.decorations;

import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;

/**
 * Inner representation of Telosys DSL tag.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 * Each tag contains tag type (name of the tag) and can also contain {@link IParameter}.
 */
public class Tag {
    private final String name;
    private final IParameter parameter;

    /**
     * @param _name Name of the tag.
     * @param _parameter Parameter if tag has one, null otherwise.
     */
    public Tag(String _name, IParameter _parameter) {
        name = _name;
        parameter = _parameter;
    }

    public String getName() {
        return name;
    }

    public IParameter getParameter() {
        return parameter;
    }

    /**
     * @return Tag in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        stringBuilder.append(name);

        if (parameter == null) {
            return stringBuilder.toString();
        }
        stringBuilder.append("(");
        stringBuilder.append(parameter);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
