package cz.fit.vut.xvrana32.telosysplugin.elements;

/**
 * Base for Telosys elements that have representation in VP project.
 */
public abstract class Element {
    protected final String vpId;
    protected final String name;

    /**
     * @param _vpId ID of {@link com.vp.plugin.model.IModelElement} representing this element in VP project.
     * @param _name Name of the element.
     */
    public Element(String _vpId, String _name) {
        vpId = _vpId;
        name = _name;
    }

    /**
     * @return Id of {@link com.vp.plugin.model.IModelElement} representing this element in VP project.
     */
    public String getVpId() {
        return vpId;
    }

    public String getName(){
        return name;
    }
}
