package cz.fit.vut.xvrana32.telosysplugin.elements;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IEndRelationship;
import com.vp.plugin.model.IModelElement;

public class AssociationEntity extends Entity{
//    private AssociationEnd associationEndOne;
//    private AssociationEnd associationEndTwo;
//    public AssociationEnd getAssociationEndOne() {
//        return associationEndOne;
//    }

    /**
     * 0 --> merge into from
     * 1 --> merge into to
     */
    private final int direction;
    private final IEndRelationship iEndRelationship;

    private final String multiplicityFrom;
    private final String multiplicityTo;


//    public AssociationEnd getAssociationEndTwo() {
//        return associationEndTwo;
//    }

    public int getDirection() {
        return direction;
    }

//    public class AssociationEnd {
//        private final String multiplicity;
//        private final Entity referencedEntity;
//        public AssociationEnd(String _multiplicity, Entity _referencedEntity){
//            multiplicity = _multiplicity;
//            referencedEntity = _referencedEntity;
//        }
//
//        public String getMultiplicity() {
//            return multiplicity;
//        }
//
//        public Entity getReferencedEntity() {
//            return referencedEntity;
//        }
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

    public IEndRelationship getiEndRelationship() {
        return iEndRelationship;
    }

    public String getMultiplicityFrom() {
        return multiplicityFrom;
    }

    public String getMultiplicityTo() {
        return multiplicityTo;
    }


//    public void setAssociationEndOne(AssociationEnd associationEnd){
//        associationEndOne = associationEnd;
//    }
//    public void setAssociationEndTwo(AssociationEnd associationEnd){
//        associationEndTwo = associationEnd;
//    }
}
