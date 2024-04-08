package cz.fit.vut.xvrana32.telosysplugin.elements;

/**
 * Inner Representation of Telosys DSL Link.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
public class Link extends DecoratedElement {
    private final Entity associationEntity; // Inner representation of association class.
    private final String vPAssociationId; // ID of associations

    private final boolean isCollection; // multiplicity

    private final Entity linkTo;
    private Entity parentEntity = null; // Back ref to owning entity.

    /**
     * Creates a link with no annotations or tags.
     *
     * @param _vpId              ID of the {@link com.vp.plugin.model.IModelElement} representing
     *                           link in VP project (the attribute).
     * @param _name              Name of the link.
     * @param _linkTo            Entity that this link points to.
     * @param _vPAssociationId   ID of the {@link com.vp.plugin.model.IAssociationClass association class}
     *                           in VP project.
     * @param _associationEntity Entity (that represents the association class) if relationship
     *                           has an association class, null otherwise.
     */
    public Link(String _vpId, String _name, Entity _linkTo, String _vPAssociationId,
                Entity _associationEntity, boolean _isCollection) {
        super(_vpId, _name);
        linkTo = _linkTo;
        vPAssociationId = _vPAssociationId;
        associationEntity = _associationEntity;
        isCollection = _isCollection;
    }

    public boolean isCollection() {
        return isCollection;
    }

    /**
     * @return Entity that this link points to.
     */
    public Entity getToEntity() {
        return linkTo;
    }

    /**
     * @return Entity that owns this link.
     */
    public Entity getParentEntity() {
        return parentEntity;
    }

    /**
     * Sets a reference to owning entity.
     *
     * @param parentEntity Entity that owns this link.
     */
    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    /**
     * @return Link in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(name);
        stringBuilder.append(" : ");
        stringBuilder.append(linkTo.getName());

        if (isCollection) {
            stringBuilder.append("[]");
        }

        if (annos.size() > 0 || tags.size() > 0) {
            stringBuilder.append(" { ");
            stringBuilder.append(decorationsToString(" "));
            stringBuilder.append("}");
        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String getVPAssociationId() {
        return vPAssociationId;
    }

    public Entity getAssociationEntity() {
        return associationEntity;
    }
}
