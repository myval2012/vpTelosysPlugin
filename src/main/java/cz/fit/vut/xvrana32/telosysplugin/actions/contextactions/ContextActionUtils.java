/**
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cz.fit.vut.xvrana32.telosysplugin.actions.contextactions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;

import java.util.Iterator;

/**
 * Common methods used by other XXXContextActionController controllers.
 */
public class ContextActionUtils {

    /**
     * Retrieves a representative attribute of given association end, if representative attribute does not exist create
     * new representative attribute. The created attribute is assigned as representative attribute of given
     * association end.
     * @param vPAssociationEnd association end of wanted representative attribute.
     * @return Representative attribute of given association end, new attribute if null.
     */
    public static IAttribute getOrCreateRepresentativeAttribute(IAssociationEnd vPAssociationEnd) {
        IAttribute vPAttribute = vPAssociationEnd.getRepresentativeAttribute();
        // create new representative attribute
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

    /**
     * Finds an association class that is in relationship with given association.
     * @param vPAssociation Association linked with wanted association class.
     * @return Association class of given association, null if association class does not exist.
     */
    public static IClass getAssociationClass(IAssociation vPAssociation) {
        IClass vPAssociationClass;

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

    /**
     * Create FK attributes within given Association class and adds @FK stereotype with "referenced"
     * tagged value set appropriately.
     * @param vPAssociationClass Association class within which to create the attributes.
     * @param vPAssociation Association on which to base FK attributes and referenced class on.
     */
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
                fkPart.addStereotype("@" + Anno.AnnoType.F_K);
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
