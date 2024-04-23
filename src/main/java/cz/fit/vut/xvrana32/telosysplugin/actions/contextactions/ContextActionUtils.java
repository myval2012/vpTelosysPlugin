package cz.fit.vut.xvrana32.telosysplugin.actions.contextactions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.RelationshipRetriever;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

import java.util.Iterator;

public class ContextActionUtils {
    public static IAttribute getOrCreateRepresentativeAttribute(IAssociationEnd vPAssociationEnd) {
        IAttribute vPAttribute = vPAssociationEnd.getRepresentativeAttribute();
        if (vPAttribute == null) {
            IClass vPClass = (IClass) vPAssociationEnd.getOppositeEnd().getModelElement();
            String multiplicity = vPAssociationEnd.getMultiplicity();
            vPAttribute = IModelElementFactory.instance().createAttribute();
            vPAttribute.setMultiplicity(multiplicity);

            vPClass.addChild(vPAttribute);
            vPAttribute.setType(vPClass);
            vPAttribute.setVisibility(IAttribute.VISIBILITY_UNSPECIFIED);
            vPAssociationEnd.setRepresentativeAttribute(vPAttribute);
        }
        return vPAttribute;
    }

    public static IClass getAssociationClass(IAssociation vPAssociation) {
        IProject vPProject = vPAssociation.getProject();
        IClass vPAssociationClass = null;

        vPAssociationClass = getAssociationClassHelper(vPAssociation.toFromRelationshipArray());
        if (vPAssociationClass != null) {
            return vPAssociationClass;
        }
        return getAssociationClassHelper(vPAssociation.toToRelationshipArray());
    }

    private static IClass getAssociationClassHelper(ISimpleRelationship[] relationshipEnds) {
        for (ISimpleRelationship relationship : relationshipEnds) {
            if (relationship.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
                if (relationship.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
                    return (IClass) relationship.getFrom();
                }
                return (IClass) relationship.getTo();
            }
        }
        return null;
    }

    public static void createAttributesAndFKs(IClass vPAssociationClass, IAssociation vPAssociation) {
        if (((IAssociationEnd) vPAssociation.getToEnd()).getMultiplicity().equals(IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY) &&
                ((IAssociationEnd) vPAssociation.getFromEnd()).getMultiplicity().equals(IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY)) {
            // this is Many to Many --> create attributes from both sides
            createAttributesAndFKsHelper((IClass) vPAssociation.getFrom(), vPAssociationClass);
            createAttributesAndFKsHelper((IClass) vPAssociation.getTo(), vPAssociationClass);

            // set id to true foreach attr in association class
            for (Iterator it = vPAssociationClass.attributeIterator(); it.hasNext(); ) {
                IAttribute vPAttribute = (IAttribute) it.next();
                vPAttribute.setIsID(true);
            }
        } else {
            boolean isFromSideOwning = vPAssociation.getDirection() == 1;
            IClass inverseClass = (IClass) (isFromSideOwning ? vPAssociation.getFrom() : vPAssociation.getTo());
            createAttributesAndFKsHelper(inverseClass, vPAssociationClass);
        }
    }

    private static void createAttributesAndFKsHelper(IClass vPReferencedClass, IClass vPAssociationClass) {
        for (Iterator it = vPReferencedClass.attributeIterator(); it.hasNext(); ) {
            IAttribute vPAttribute = (IAttribute) it.next();
            if (vPAttribute.isID()) {
                IAttribute fkPart = IModelElementFactory.instance().createAttribute();
                vPAssociationClass.addChild(fkPart);

                fkPart.setName(vPReferencedClass.getName() + vPAttribute.getName());
                fkPart.setType(vPAttribute.getTypeAsModel());

                // create FK
                fkPart.addStereotype("@" + Anno.AnnoType.F_K.toString());
                ITaggedValueContainer taggedValues = fkPart.getTaggedValues();
                ITaggedValue taggedValue = taggedValues.getTaggedValueByName("referenced");
                taggedValue.setValue(vPAttribute);
            }
        }
    }

    public static void createAssociationClassWithFKs(
            IAssociation vPAssociation,
            IClassDiagramUIModel vPDiagram,
            IAssociationUIModel vPAssociationShape) {
        DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

        IClass vPAssociationClass = IModelElementFactory.instance().createClass();
        vPAssociation.getFrom().getParent().addChild(vPAssociationClass);

        IClassUIModel vPAssociationClassShape = (IClassUIModel)
                diagramManager.createDiagramElement(vPDiagram, vPAssociationClass);
        ContextActionUtils.createAttributesAndFKs(vPAssociationClass, vPAssociation);

        vPAssociationClassShape.setBounds(
                vPAssociationShape.getX() + vPAssociationShape.getWidth() / 4,
                vPAssociationShape.getY() - 50,
                100,
                100);
        vPAssociationClassShape.fitSize();

        // create the connector
        IAssociationClass vPAssociationClassConnector = IModelElementFactory.instance().createAssociationClass();
        vPAssociationClassConnector.setFrom(vPAssociationClass);
        vPAssociationClassConnector.setTo(vPAssociation);
        IAssociationClassUIModel vPAssociationClaasConnectorShape = (IAssociationClassUIModel)
                diagramManager.createConnector(
                        vPDiagram,
                        vPAssociationClassConnector,
                        vPAssociationClassShape,
                        vPAssociationShape,
                        null);
    }
}
