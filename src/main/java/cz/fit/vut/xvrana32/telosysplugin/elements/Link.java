package cz.fit.vut.xvrana32.telosysplugin.elements;

/**
 * Inner Representation of Telosys DSL Link.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
public class Link extends DecoratedElement {
    private final Entity associationEntity;
    private final String vPAssociationId;
    private final boolean isArray;

    private final Entity linkTo;
    private Entity parentEntity = null;

    /**
     * Creates a link with no annotations or tags.
     *
     * @param _vpId                ID of the {@link com.vp.plugin.model.IModelElement} representing
     *                             link in VP project (the attribute).
     * @param _name                Name of the link.
     * @param _linkTo              Entity that this link points to.
     * @param _vPAssociationId     ID of the {@link com.vp.plugin.model.IAssociationClass association class}
     *                             in VP project.
     * @param _associationEntity Entity (that represents the association class) if relationship
     *                             has an association class, null otherwise.
     */
    public Link(String _vpId, String _name, Entity _linkTo, String _vPAssociationId,
                Entity _associationEntity, boolean _isArray) {
        super(_vpId, _name);
        linkTo = _linkTo;
        vPAssociationId = _vPAssociationId;
        associationEntity = _associationEntity;
        isArray = _isArray;
    }

    /**
     * @return Entity that this link points to.
     */
    public Entity getToEntity() {
        return linkTo;
    }

    public Entity getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(name);
        stringBuilder.append(" : ");
        stringBuilder.append(linkTo.getName());

        if (isArray){
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
