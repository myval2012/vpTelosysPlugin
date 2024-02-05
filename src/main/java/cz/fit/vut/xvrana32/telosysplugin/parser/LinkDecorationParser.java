package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Anno;
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
                    {new ParamDeclaration("cascade", ITaggedValueDefinition.TYPE_MODEL_ELEMENT )})
    };

    public static void parse(IProject vPProject, Link link) throws Exception {
        Iterator stereotypes;
        IClass vPClass = (IClass) vPProject.getModelElementById(link.getParentEntity().getVpId());
//        if (vPClass == null){
//            Logger.log("Entity was not found");
//        }
//        else{
//            Logger.log(String.format("The entity %s was found ", vPClass.getName()));
//        }

        IAssociation vPAssociation = (IAssociation) vPProject.getModelElementById(link.getVPAssociationId());
//        if (vPAssociation == null){
//            Logger.log("Association was not found");
//        }
//        else{
//            Logger.log(String.format("The association %s was found ", vPAssociation.getName()));
//        }

        IAttribute vPAttr = (IAttribute) vPClass.getChildById(link.getVpId());
//        if (vPAttribute == null){
//            Logger.log("Link (attribute) was not found");
//        }
//        else{
//            Logger.log(String.format("The link (attribute) %s was found ", vPAttribute.getName()));
//        }

        // TODO special annotations
        String multiplicityFrom = ((IAssociationEnd) vPAssociation.getFromEnd()).getMultiplicity();
        String multiplicityTo = ((IAssociationEnd) vPAssociation.getToEnd()).getMultiplicity();
        boolean isOnFromSide = vPAssociation.getFrom().getId().equals(link.getParentEntity().getVpId());
        boolean isOnToSide = !isOnFromSide;

        // direction == 0 if direction is from RelationshipEndFrom to RelationshipEndTo
        // direction == 1 if direction is not from RelationshipEndFrom to RelationshipEndTo
//        Logger.log(String.format("The direction of this association is %d",
//                vPAssociation.getDirection()));
//        Logger.log(String.format("The multiplicity is from %s to %s",
//                ((IAssociationEnd) vPAssociation.getFromEnd()).getMultiplicity(),
//                ((IAssociationEnd) vPAssociation.getToEnd()).getMultiplicity()));

        // JoinEntity
        // LinkByJoinEntity
        // ManyToMany
        if (multiplicityFrom.endsWith("*") && multiplicityTo.endsWith("*")) {
            // it's many-to-many relationship
            link.addAnno(new Anno(Anno.AnnoType.MANY_TO_MANY));
//            Logger.log("Added annotation @ManyToMany");
            // add association class to standard entities if it is not already there
            Entity joinEntity = link.getAssociationEntity();

            if (joinEntity == null) {
//                Logger.log("Join entity does not exist.");
                return;
            }

//            if (!link.getParentEntity().getParentModel().getEntities().contains(joinEntity)) {
            joinEntity.addAnno(new Anno(Anno.AnnoType.JOIN_ENTITY));
//                link.getParentEntity().getParentModel().addEntity(joinEntity);
//            Logger.log("Added join entity");
//            }

            // if this is the owning side add LinkByJoinEntity
            if ((isOnFromSide && vPAssociation.getDirection() == 0) ||
                    (isOnToSide && vPAssociation.getDirection() == 1)) {
                Anno newAnno = new Anno(Anno.AnnoType.LINK_BY_JOIN_ENTITY);
                newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_ENTITY, joinEntity));
                link.addAnno(newAnno);
//                Logger.log("Added annotation @LinkByJoinEntity");

            }
        }


//        Logger.log("Checking mappedBy");
        // MappedBy
        // if this is the inverse side of the relationship and there is a link on the owning side add MappedBy
        IAttribute owningAttribute = isOnFromSide ?
                ((IAssociationEnd) vPAssociation.getFromEnd()).getRepresentativeAttribute() :
                ((IAssociationEnd) vPAssociation.getToEnd()).getRepresentativeAttribute();

//        Logger.log(String.format("The owning attribute is named %s with %s",
//                owningAttribute == null ? null : owningAttribute.getName(),
//                owningAttribute == null ? null : owningAttribute.getId()));

        if ((isOnFromSide && vPAssociation.getDirection() == 1 && owningAttribute != null) ||
                (isOnToSide && vPAssociation.getDirection() == 0 && owningAttribute != null)) {
            Anno newAnno = new Anno(Anno.AnnoType.MAPPED_BY);
//            Logger.log("Trying to add MappedBy...");
            for (Link owningLink : link.getToEntity().getLinks()) {

//                Logger.log(String.format("Trying Link %s with id %s", owningLink.getName(), owningLink.getVpId()));
                if (owningLink.getVpId().equals(owningAttribute.getId())) {
                    newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_LINK, owningLink));
                    link.addAnno(newAnno);
//                    Logger.log("Added annotation @MappedBy");
                    break;
                }
            }
        }

//        Logger.log("Checking OneToOne");
        // OneToOne
        if (multiplicityFrom.contains("1") && multiplicityTo.contains("1")) {
            link.addAnno(new Anno(Anno.AnnoType.ONE_TO_ONE));
//            Logger.log("Added annotation @OneToOne");
        }

//        Logger.log("Checking optional");
        // Optional
        if ((isOnFromSide
                && multiplicityTo.startsWith("0")) ||
                (isOnToSide
                        && multiplicityFrom.startsWith("0")
                )) {
            link.addAnno(new Anno(Anno.AnnoType.OPTIONAL));
//            Logger.log("Added annotation @Optional");
        }
//        Logger.log("Checking ended");

//        IClass vPClass = (IClass) vPProject.getModelElementById(link.getParentEntity().getVpId());
//        Logger.log(vPClass == null ? "vPClass not found" : "Found vPClass");
//        IAttribute vPAttr = (IAttribute) vPClass.getChildById(link.getVpId());
//        Logger.log(vPAttr == null ? "VP Attribute not found" : "Found VP Attribute");

//        // check for special annotation
//        if (vPAttr.isID()) {
//            link.addAnno(new Anno(Anno.AnnoType.ID));
////            Logger.log(String.format("Added ID annotation to Parameter: %s", attr.getName()));
//        }
//
//        if (vPAttr.getInitialValue() != null && !vPAttr.getInitialValue().isEmpty()) {
////            Logger.log(String.format("Added INITIAL_VALUE annotation to Parameter: %s", attr.getName()));
//            Anno newAnno = new Anno(Anno.AnnoType.INITIAL_VALUE);
//            newAnno.addParameter(new Parameter(Parameter.ValueType.STRING, vPAttr.getInitialValue()));
//            link.addAnno(newAnno);
//        }

        // TODO constraints

        // TODO annotations and tags
        stereotypes = vPAttr.stereotypeModelIterator();
        while (stereotypes.hasNext()) {
            IStereotype stereotype = (IStereotype) stereotypes.next();
            if (stereotype.getName().startsWith("@")) // annotation
            {
//                Logger.log(String.format("Found a annotation in Link: %s, has name: %s",
//                        vPAttr.getName(),
//                        stereotype.getName()));

                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(stereotype.getName().substring(1));
                if (annoDeclaration != null) {
//                    Logger.log(String.format("Annotation declaration for %s was found",
//                            stereotype.getName()));
                    Anno newAnno = annoDeclaration.createAnno(vPAttr,
                            stereotype, link.getParentEntity().getParentModel());
                    if (newAnno != null) {
                        link.addAnno(newAnno);
                    }
                }
//                Anno newAnno = evaluateAnno(vPAttr, stereotype, attr);
//                if (newAnno != null){
//                    attr.addAnno(newAnno);
//                }
            } else if (stereotype.getName().startsWith("#")) // tags
            {
                link.addTag(TagParser.parseTag(vPAttr, stereotype, link.getParentEntity()));
//                Logger.log(String.format("Found a tag in link: %s, has name: %s",
//                        vPAttr.getName(),
//                        stereotype.getName()));
            }
        }
    }

    private static AnnoDeclaration getAnnoDeclarationByName(String name) {
        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
            if (annoDeclaration.name.equals(name)) {
                return annoDeclaration;
            }
        }
        return null;
    }
}
