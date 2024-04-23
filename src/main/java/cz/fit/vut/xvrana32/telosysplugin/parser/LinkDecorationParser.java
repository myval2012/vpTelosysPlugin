package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.Link;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.*;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

import java.util.Iterator;

public class LinkDecorationParser {
    public static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("Embedded", Anno.AnnoType.EMBEDDED, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeEager", Anno.AnnoType.FETCH_TYPE_EAGER, new ParamDeclaration[]{}),
            new AnnoCommon("FetchTypeLazy", Anno.AnnoType.FETCH_TYPE_LAZY, new ParamDeclaration[]{}),
            new AnnoCommon("Insertable", Anno.AnnoType.INSERTABLE, new ParamDeclaration[]
                    {new ParamDeclaration("insertable", ITaggedValueDefinition.TYPE_BOOLEAN,
                            false, false)}),
            new AnnoLinkByAttr("LinkByAttr", Anno.AnnoType.LINK_BY_ATTR, new ParamDeclaration[]
                    {new ParamDeclaration("linkByAttrClass", ITaggedValueDefinition.TYPE_MODEL_ELEMENT,
                            false, false)}),
            new AnnoCommon("LinkByFK", Anno.AnnoType.LINK_BY_F_K, new ParamDeclaration[]
                    {new ParamDeclaration("fkName", ITaggedValueDefinition.TYPE_TEXT,
                            false, false)}),
            new AnnoCommon("OrphanRemoval", Anno.AnnoType.ORPHAN_REMOVAL, new ParamDeclaration[]{}),
            new AnnoCommon("Updatable", Anno.AnnoType.UPDATABLE, new ParamDeclaration[]
                    {new ParamDeclaration("updatable", ITaggedValueDefinition.TYPE_BOOLEAN,
                            false, false)}),

            new AnnoCascade("Cascade", Anno.AnnoType.CASCADE, new ParamDeclaration[]
                    {new ParamDeclaration("cascade", ITaggedValueDefinition.TYPE_MODEL_ELEMENT,
                            false, false)}),

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
//        if ((!multiplicityFrom.equals("0..1") && !multiplicityFrom.equals("0..*")) ||
//                !multiplicityTo.equals("0..1") && !multiplicityTo.equals("0..*")) {
//            throw new Exception("The multiplicity of association ends has to be set to '0..1' or '0..*'.");
//        }

        boolean isOnToSide = vPAssociation.getFrom().getId().equals(link.getParentEntity().getVpId());
        boolean isOnFromSide = !isOnToSide;
        boolean isFromSideOwning = vPAssociation.getDirection() == 1;
        boolean isToSideOwning = !isFromSideOwning;

        // !!! use only these in the upcoming conditions
//        String multiplicityThisSide = isOnFromSide ? multiplicityFrom : multiplicityTo;
        String multiplicityOtherSide = isOnFromSide ? multiplicityTo : multiplicityFrom;
        boolean isCollection = link.isCollection(); // this link has multiplicity ToMany
        boolean isOtherSideCollection = multiplicityOtherSide.endsWith("*"); // opposite link has multiplicity ToMany
        boolean isManyToMany = isCollection && isOtherSideCollection;
//        boolean isManyToOne = !isCollection && isOtherSideCollection;
//        boolean isOneToMany = isCollection && !isOtherSideCollection;
        boolean isOneToOne = !isCollection && !isOtherSideCollection;
        boolean isOwningSide = (isOnFromSide && isFromSideOwning) || (isOnToSide && isToSideOwning);
        boolean isInverseSide = !isOwningSide;
        IAttribute owningVPAttr = isFromSideOwning ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();
//        IAttribute inverseVPAttr = isToSideOwning ?
//                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
//                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();

        // preconditions:
        // * in case of a non-ManyToMany relationship, the many side cannot be owning side.
        if (!isManyToMany && isOwningSide && isCollection) {
            Logger.logE(String.format(
                    "Link %s in class %s has the inverse side set to Many side on non-ManyToMany relationship.",
                    link.getName(),
                    link.getParentEntity().getName()));
            return;
        }

        // LinkByJoinEntity
        // ManyToMany
        if (isManyToMany) {
            // it's many-to-many relationship
            link.addAnno(new Anno(Anno.AnnoType.MANY_TO_MANY));
            Entity joinEntity = link.getAssociationEntity();


            if (joinEntity != null) {
                Anno newAnno = new Anno(Anno.AnnoType.LINK_BY_JOIN_ENTITY);
                newAnno.addParameter(ParameterFactory.CreateParameter(
                        joinEntity,
                        ParameterFactory.ValueType.LINK,
                        false,
                        false));
                link.addAnno(newAnno);
            }
        }

        // MappedBy
        // if this is the inverse side of the relationship and there is a link attribute on the owning side...
        if (isInverseSide && owningVPAttr != null) {
            Logger.logD("Generating MappedBy");
            Anno newAnno = new Anno(Anno.AnnoType.MAPPED_BY);
            for (Iterator<Link> it = link.getToEntity().getLinksIterator(); it.hasNext(); ) {
                Link owningLink = it.next();

                if (owningLink.getVpId().equals(owningVPAttr.getId())) {
                    newAnno.addParameter(ParameterFactory.CreateParameter(
                            owningLink,
                            ParameterFactory.ValueType.LINK,
                            false,
                            false));
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

//    private static void mergeAnnos(Attr target, Attr source) {
//        for (Anno anno : source.getAnnos()) {
//            if (!target.addAnno(anno)) {
//                Logger.logW(String.format(
//                        "Class: %s, Attribute: %s already contains annotation %s, annotation skipped.",
//                        target.getParentEntity().getName(),
//                        target.getName(),
//                        anno.getAnnoType().toString()));
//            }
//        }
//    }
}