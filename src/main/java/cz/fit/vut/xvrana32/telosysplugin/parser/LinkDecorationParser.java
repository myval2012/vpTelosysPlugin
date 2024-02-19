package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Attr;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.Link;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.*;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.Iterator;

public class LinkDecorationParser {
    private static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("Embedded", Anno.AnnoType.EMBEDDED, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeEager", Anno.AnnoType.FETCH_TYPE_EAGER, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeLazy", Anno.AnnoType.FETCH_TYPE_LAZY, new ParamDeclaration[]{}),
            new AnnoCommon("Insertable", Anno.AnnoType.INSERTABLE, new ParamDeclaration[]
                    {new ParamDeclaration("insertable", ITaggedValueDefinition.TYPE_BOOLEAN)}),
            new AnnoLinkByAttr("LinkByAttr", Anno.AnnoType.LINK_BY_ATTR, new ParamDeclaration[]
                    {new ParamDeclaration("linkByAttrClass", ITaggedValueDefinition.TYPE_MODEL_ELEMENT)}),
            new AnnoCommon("LinkByFK", Anno.AnnoType.LINK_BY_FK, new ParamDeclaration[]
                    {new ParamDeclaration("foreignKeyName", false)}),
            new AnnoCommon("OrphanRemoval", Anno.AnnoType.ORPHAN_REMOVAL, new ParamDeclaration[]{}),
            new AnnoCommon("Updatable", Anno.AnnoType.UPDATABLE, new ParamDeclaration[]
                    {new ParamDeclaration("updatable", ITaggedValueDefinition.TYPE_BOOLEAN)}),

            new AnnoCascade("Cascade", Anno.AnnoType.CASCADE, new ParamDeclaration[]
                    {new ParamDeclaration("cascade", ITaggedValueDefinition.TYPE_MODEL_ELEMENT)}),

            new AnnoCommon("Optional", Anno.AnnoType.OPTIONAL, new ParamDeclaration[]{}),
    };

    public static void parse(IProject vPProject, Link link) throws Exception {
        // TODO refactor conditions
//        Iterator stereotypes;
        IClass vPClass = (IClass) vPProject.getModelElementById(link.getParentEntity().getVpId());
        IAssociation vPAssociation = (IAssociation) vPProject.getModelElementById(link.getVPAssociationId());
        IAttribute vPAttr = (IAttribute) vPClass.getChildById(link.getVpId());

        // special annotations
        // direction == 0 if direction is from RelationshipEndFrom to RelationshipEndTo
        // direction == 1 if direction is not from RelationshipEndFrom to RelationshipEndTo
        String multiplicityFrom = ((IAssociationEnd) vPAssociation.getFromEnd()).getMultiplicity();
        String multiplicityTo = ((IAssociationEnd) vPAssociation.getToEnd()).getMultiplicity();
        boolean isOnFromSide = vPAssociation.getFrom().getId().equals(link.getParentEntity().getVpId());
        boolean isOnToSide = !isOnFromSide;

        boolean isFromSideOwning = vPAssociation.getDirection() == 0;
        boolean isToSideOwning = !isFromSideOwning;

        boolean isOwningSide = (isOnFromSide && isFromSideOwning) || (isOnToSide && isToSideOwning);
        boolean isInverseSide = !isOwningSide;
        IAttribute owningVPAttr = isFromSideOwning ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();
        IAttribute inverseVPAttr = isToSideOwning ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();

        // TODO test that owning side of relationship is not many side (only if it is not Many-To-Many)

//        IAttribute inverseVPAttr = ((IAssociationEnd) owningVPAttr.getReferencingAssociationEnd().getOppositeEnd())
//                .getRepresentativeAttribute();

//        Logger.log("JoinEntity, LinkByJoinEntity and ManyToMany...");
        // JoinEntity
        // LinkByJoinEntity
        // ManyToMany
        if (multiplicityFrom.endsWith("*") && multiplicityTo.endsWith("*")) {
            // it's many-to-many relationship
            link.addAnno(new Anno(Anno.AnnoType.MANY_TO_MANY));
            // add association class to standard entities if it is not already there
            Entity joinEntity = link.getAssociationEntity();

            if (joinEntity == null) {
                // TODO automatically generate JoinEntity, add it to associationEntities?
                // TODO how to create a unique name?
                return; // Join entity does not exist
            }
            joinEntity.addAnno(new Anno(Anno.AnnoType.JOIN_ENTITY));

            // if this is the owning side add LinkByJoinEntity
            if ((isOnFromSide && vPAssociation.getDirection() == 0) ||
                    (isOnToSide && vPAssociation.getDirection() == 1)) {
                Anno newAnno = new Anno(Anno.AnnoType.LINK_BY_JOIN_ENTITY);
                newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_ENTITY, joinEntity));
                link.addAnno(newAnno);
            }
        } else if (link.getAssociationEntity() != null && isOwningSide) {
//            Logger.log("Auto-generating FK");

            // this is not Many-To-Many relationship and if:
            // * there is an association class and
            // * this is the owning side of the link
            // then its attributes should be treated as FK attributes.

            Entity associationEntity = link.getAssociationEntity();

            // check association attributes do not already have FK annotation.
            boolean noFK = true;
            for (Attr attr : associationEntity.getAttrs()){
                if (attr.containsAnnoType(Anno.AnnoType.F_K)){
                    noFK = false;
                    break;
                }
            }

            // create a foreign key annotation, name of FK is the name of the association entity.
            Anno fKAnno = new Anno(Anno.AnnoType.F_K);
            fKAnno.addParameter(ParameterFactory.CreateParameter(
                    ParameterFactory.ValueType.STRING,
                    associationEntity.getName()));
            fKAnno.addParameter(ParameterFactory.CreateParameter(
                    ParameterFactory.ValueType.LINK_ENTITY,
                    link.getToEntity()));

            // add linkByFK to this link
            Anno linkByFKAnno = new Anno(Anno.AnnoType.LINK_BY_FK);
            if (!link.addAnno(linkByFKAnno)){
                // TODO error
                return; // this link already contains this annotation
            }

            // add FK annotation to all FK attributes.
            for (Attr attr:associationEntity.getAttrs()){
                attr.addAnno(fKAnno);
            }

            // add association entity attributes to this entity
            Entity thisEntity = link.getParentEntity();
            for (Attr attr: associationEntity.getAttrs()){
                thisEntity.addAttr(attr);
            }
        }


        // MappedBy
        // if this is the inverse side of the relationship and there is a link on the owning side add MappedBy
//        IAttribute owningAttribute = isOnFromSide ?
//                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
//                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();

//        Logger.log(String.format("The owning attribute is named %s with %s",
//                owningAttribute == null ? null : owningAttribute.getName(),
//                owningAttribute == null ? null : owningAttribute.getId()));

//        Logger.log("MappedBy...");

        if ((isOnFromSide && vPAssociation.getDirection() == 1 && owningVPAttr != null) ||
                (isOnToSide && vPAssociation.getDirection() == 0 && owningVPAttr != null)) {
            Anno newAnno = new Anno(Anno.AnnoType.MAPPED_BY);
            for (Link owningLink : link.getToEntity().getLinks()) {

                if (owningLink.getVpId().equals(owningVPAttr.getId())) {
                    newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_LINK, owningLink));
                    link.addAnno(newAnno);
                    break;
                }
            }
        }

//        Logger.log("OneToOne");
        // OneToOne
        if (multiplicityFrom.endsWith("1") && multiplicityTo.endsWith("1")) {
            link.addAnno(new Anno(Anno.AnnoType.ONE_TO_ONE));
        }

//        // Optional
//        if ((isOnFromSide
//                && multiplicityTo.startsWith("0")) ||
//                (isOnToSide
//                        && multiplicityFrom.startsWith("0")
//                )) {
//            link.addAnno(new Anno(Anno.AnnoType.OPTIONAL));
//        }

        // TODO constraints

        DecorationParser.parseNonSpecialAnnosAndTags(annoDeclarations, vPAttr, link, link.getParentEntity());

////        Logger.log("OtherAnnotations");
//        stereotypes = vPAttr.stereotypeModelIterator();
//        while (stereotypes.hasNext()) {
//            IStereotype stereotype = (IStereotype) stereotypes.next();
//            if (stereotype.getName().startsWith("@")) // annotation
//            {
////                Logger.log(String.format("Found an annotation in Link: %s, has name: %s",
////                        vPAttr.getName(),
////                        stereotype.getName()));
//
//                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(stereotype.getName().substring(1));
//                if (annoDeclaration != null) {
////                    Logger.log(String.format("Annotation declaration for %s was found",
////                            stereotype.getName()));
//                    Anno newAnno = annoDeclaration.createAnno(vPAttr,
//                            stereotype, link.getParentEntity().getParentModel());
//                    if (newAnno != null) {
//                        link.addAnno(newAnno);
//                    }
//                }
////                Anno newAnno = evaluateAnno(vPAttr, stereotype, attr);
////                if (newAnno != null){
////                    attr.addAnno(newAnno);
////                }
//            } else if (stereotype.getName().startsWith("#")) // tags
//            {
//                link.addTag(TagParser.parseTag(vPAttr, stereotype, link.getParentEntity()));
////                Logger.log(String.format("Found a tag in link: %s, has name: %s",
////                        vPAttr.getName(),
////                        stereotype.getName()));
//            }
//        }
    }
//
//    private static AnnoDeclaration getAnnoDeclarationByName(String name) {
//        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
//            if (annoDeclaration.name.equals(name)) {
//                return annoDeclaration;
//            }
//        }
//        return null;
//    }
}
