package cz.fit.vut.xvrana32.telosysplugin.elements;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IEndRelationship;
import com.vp.plugin.model.IModelElement;

/**
 * Represents association class and all values that are needed for proper merge / Join entity creation.
 */
public class AssociationEntity extends Entity{
    /**
     * Direction of association.
     * 0 --> merge into from
     * 1 --> merge into to
     */
    public final int direction;

    /**
     * Both association ends and elements are navigable from this element.
     */
    public final IEndRelationship iEndRelationship;

    public final String multiplicityFrom;
    public final String multiplicityTo;


//    public int getDirection() {
//        return direction;
//    }

    /**
     * Creates an entity with no annotations or tags.
     *
     * @param _name Name of the entity.
     * @param _vpId ID of the {@link IModelElement} representing entity in VP project.
     */
    public AssociationEntity(String _name, String _vpId, int _direction, IEndRelationship _iEndRelationship) {
        super(_name, _vpId);
        direction = _direction;
        iEndRelationship = _iEndRelationship;
        multiplicityFrom = ((IAssociationEnd)iEndRelationship.getFromEnd()).getMultiplicity();
        multiplicityTo = ((IAssociationEnd)iEndRelationship.getToEnd()).getMultiplicity();
    }

//    public IEndRelationship getiEndRelationship() {
//        return iEndRelationship;
//    }

//    public String getMultiplicityFrom() {
//        return multiplicityFrom;
//    }

//    public String getMultiplicityTo() {
//        return multiplicityTo;
//    }
}
