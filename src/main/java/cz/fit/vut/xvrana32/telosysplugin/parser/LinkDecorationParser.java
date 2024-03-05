package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Attr;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.Link;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.*;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

public class LinkDecorationParser {
    public static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("Embedded", Anno.AnnoType.EMBEDDED, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeEager", Anno.AnnoType.FETCH_TYPE_EAGER, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeLazy", Anno.AnnoType.FETCH_TYPE_LAZY, new ParamDeclaration[]{}),
            new AnnoCommon("Insertable", Anno.AnnoType.INSERTABLE, new ParamDeclaration[]
                    {new ParamDeclaration("insertable", ITaggedValueDefinition.TYPE_BOOLEAN)}),
            new AnnoLinkByAttr("LinkByAttr", Anno.AnnoType.LINK_BY_ATTR, new ParamDeclaration[]
                    {new ParamDeclaration("linkByAttrClass", ITaggedValueDefinition.TYPE_MODEL_ELEMENT)}),
            new AnnoCommon("LinkByFK", Anno.AnnoType.LINK_BY_FK, new ParamDeclaration[]
                    {new ParamDeclaration("fkName", false)}),
            new AnnoCommon("OrphanRemoval", Anno.AnnoType.ORPHAN_REMOVAL, new ParamDeclaration[]{}),
            new AnnoCommon("Updatable", Anno.AnnoType.UPDATABLE, new ParamDeclaration[]
                    {new ParamDeclaration("updatable", ITaggedValueDefinition.TYPE_BOOLEAN)}),

            new AnnoCascade("Cascade", Anno.AnnoType.CASCADE, new ParamDeclaration[]
                    {new ParamDeclaration("cascade", ITaggedValueDefinition.TYPE_MODEL_ELEMENT)}),

            new AnnoCommon("Optional", Anno.AnnoType.OPTIONAL, new ParamDeclaration[]{}),
            new AnnoCommon("MappedBy", Anno.AnnoType.MAPPED_BY, new ParamDeclaration[]{}),
    };

    public static void parse(IProject vPProject, Link link) throws Exception {
        IClass vPClass = (IClass) vPProject.getModelElementById(link.getParentEntity().getVpId());
        IAssociation vPAssociation = (IAssociation) vPProject.getModelElementById(link.getVPAssociationId());
        IAttribute vPAttr = (IAttribute) vPClass.getChildById(link.getVpId());

        // special annotations
        // direction == 0 if direction is from RelationshipEndFrom to RelationshipEndTo
        // direction == 1 if direction is not from RelationshipEndFrom to RelationshipEndTo
        String multiplicityFrom = ((IAssociationEnd) vPAssociation.getFromEnd()).getMultiplicity();
        String multiplicityTo = ((IAssociationEnd) vPAssociation.getToEnd()).getMultiplicity();

        // multiplicity has to be either 0..1 or 0..* other are not allowed --> error
        if ((!multiplicityFrom.equals("0..1") && !multiplicityFrom.equals("0..*")) ||
                !multiplicityTo.equals("0..1") && !multiplicityTo.equals("0..*")) {
            throw new Exception("The multiplicity of association ends has to be set to '0..1' or '0..*'.");
        }

        boolean isOnToSide = vPAssociation.getFrom().getId().equals(link.getParentEntity().getVpId());
        boolean isOnFromSide = !isOnToSide;
        boolean isFromSideOwning = vPAssociation.getDirection() == 1;
        boolean isToSideOwning = !isFromSideOwning;

        // !!! use only these in the upcoming conditions
//        String multiplicityThisSide = isOnFromSide ? multiplicityFrom : multiplicityTo;
        String multiplicityOtherSide = isOnFromSide ? multiplicityTo : multiplicityFrom;
        boolean isCollection = link.getIsCollection(); // this link has multiplicity ToMany
        boolean isOtherSideCollection = multiplicityOtherSide.endsWith("*"); // opposite link has multiplicity ToMany
        boolean isManyToMany = isCollection && isOtherSideCollection;
        boolean isManyToOne = !isCollection && isOtherSideCollection;
        boolean isOneToMany = isCollection && !isOtherSideCollection;
        boolean isOneToOne = !isCollection && !isOtherSideCollection;
        boolean isOwningSide = (isOnFromSide && isFromSideOwning) || (isOnToSide && isToSideOwning);
        boolean isInverseSide = !isOwningSide;
        IAttribute owningVPAttr = isFromSideOwning ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();
        IAttribute inverseVPAttr = isToSideOwning ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();

//        Logger.log(String.format("This link is on %s side", isOnFromSide ? "from" : "to"));
//        Logger.log(String.format("The owning side is %s", isFromSideOwning ? "from" : "to"));
//        Logger.log(String.format("Multiplicity of this side is %s", multiplicityThisSide));
//        Logger.log(String.format("Which means that this side %s a collection", isCollection ? "is" : "isn't"));
//        Logger.log(String.format("This relationship %s ManyToMany", isManyToMany ? "is" : "isn't"));
//        Logger.log(String.format("This is the %s side", isOwningSide ? "owning" : "inverse"));
//        if (owningVPAttr != null) {
//            Logger.log(String.format("There is an owning attr: %s", owningVPAttr.getName()));
//        }
//        if (inverseVPAttr != null) {
//            Logger.log(String.format("There is an inverse attr: %s", inverseVPAttr.getName()));
//        }

        // preconditions:
        // * in case of a non-ManyToMany relationship, the many side cannot be owning side.
        if (!isManyToMany && isOwningSide && isCollection) {
            Logger.logE(String.format(
                    "Link %s in class %s has the inverse side set to Many side on non-ManyToMany relationship.",
                    link.getName(),
                    link.getParentEntity().getName()));
            return;
        }

        // JoinEntity
        // LinkByJoinEntity
        // ManyToMany
        if (isManyToMany) {
            // it's many-to-many relationship
            link.addAnno(new Anno(Anno.AnnoType.MANY_TO_MANY));
            Entity joinEntity = link.getAssociationEntity();

            if (joinEntity != null) {
//                joinEntity.addAnno(new Anno(Anno.AnnoType.JOIN_ENTITY));

                // if this is the owning side add LinkByJoinEntity
//                if (isOwningSide) {
                    Anno newAnno = new Anno(Anno.AnnoType.LINK_BY_JOIN_ENTITY);
                    newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_ENTITY, joinEntity));
                    link.addAnno(newAnno);
//                }
            }
        }
//        else if (link.getAssociationEntity() != null && isOwningSide) {
            // this is not Many-To-Many relationship and if:
            // * there is an association class and
            // * this is the owning side of the link
            // then its attributes should be treated as FK attributes.

//            Entity associationEntity = link.getAssociationEntity();

            // check association attributes do not already have FK annotation.
//            boolean hasFK = false;
//            for (Attr attr : associationEntity.getAttrs()) {
//                if (attr.containsAnnoType(Anno.AnnoType.F_K)) {
//                    hasFK = true;
//                    break;
//                }
//            }

//            if (!hasFK) {
                // create a foreign key annotation, name of FK is the name of the association entity.
//                Anno fKAnno = new Anno(Anno.AnnoType.F_K);
//                fKAnno.addParameter(ParameterFactory.CreateParameter(
//                        ParameterFactory.ValueType.STRING,
//                        associationEntity.getName()));
//                fKAnno.addParameter(ParameterFactory.CreateParameter(
//                        ParameterFactory.ValueType.LINK_ENTITY,
//                        link.getToEntity()));

//                // add linkByFK to this link
//                Anno linkByFKAnno = new Anno(Anno.AnnoType.LINK_BY_FK);
//                link.addAnno(linkByFKAnno);
//
//                // add FK annotation to all FK attributes.
//                for (Attr attr : associationEntity.getAttrs()) {
//                    attr.addAnno(fKAnno);
//                }

                // add association entity attributes to this entity
//                Entity thisEntity = link.getParentEntity();
//                for (Attr associationClassAttr : associationEntity.getAttrs()) {
//                    Attr classAttr = link.getParentEntity().getAttrByName(associationClassAttr.getName());
//                    if ( classAttr == null){
//                        thisEntity.addAttr(associationClassAttr);
//                    }
//                    else{
//                        // merge
//                        mergeAnnos(classAttr, associationClassAttr);
//                    }
//                }
//            }
//        }

        // MappedBy
        // if this is the inverse side of the relationship and there is a link attribute on the owning side...
        if (isInverseSide && owningVPAttr != null) {
            Logger.logD("Generating MappedBy");
            Anno newAnno = new Anno(Anno.AnnoType.MAPPED_BY);
            for (Link owningLink : link.getToEntity().getLinks()) {

                if (owningLink.getVpId().equals(owningVPAttr.getId())) {
                    newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_LINK, owningLink));
                    link.addAnno(newAnno);
                    break;
                }
            }
        }

        // OneToOne
        if (isOneToOne) {
            link.addAnno(new Anno(Anno.AnnoType.ONE_TO_ONE));
        }

        DecorationParser.checkTaggedValuesStereotype(vPAttr.getTaggedValues());
        DecorationParser.parseNonSpecialAnnosAndTags(annoDeclarations, vPAttr, link, link.getParentEntity());
    }

    private static void mergeAnnos(Attr target, Attr source){
        for (Anno anno : source.getAnnos()){
            if (!target.addAnno(anno)){
                Logger.logW(String.format(
                        "Class: %s, Attribute: %s already contains annotation %s, annotation skipped.",
                        target.getParentEntity().getName(),
                        target.getName(),
                        anno.getAnnoType().toString()));
            }
        }
    }
}