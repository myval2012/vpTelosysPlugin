package cz.fit.vut.xvrana32.telosysplugin.elements;

/**
 * Inner representation of Telosys DSL tag.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 * Each tag contains tag type (name of the tag) and can also contain {@link Parameter}.
 */
public class Tag {
    private final String tagType; // name
    private final Parameter parameter;

    /**
     * @param _tagType Name of the tag.
     * @param _parameter Parameter if tag has one, null otherwise.
     */
    public Tag(String _tagType, Parameter _parameter) {
        tagType = _tagType;
        parameter = _parameter;
    }

    public String getTagType() {
        return tagType;
    }

    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @return Tag in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        stringBuilder.append(tagType);

        if (parameter == null) {
            return stringBuilder.toString();
        }
        stringBuilder.append("(");
        stringBuilder.append(parameter);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
