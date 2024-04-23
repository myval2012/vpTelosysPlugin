package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.utils.Config;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ModelParser {
    Model model;
    List<String> associationVPIds = new ArrayList<>();

    /**
     * Parses given Visual Paradigm model.
     *
     * @param vPModel The parsed model.
     * @return Inner representation of Telosys DSL model.
     */
    public Model parse(IModelElement vPModel) {
        model = new Model(vPModel.getName(), vPModel.getId());
        LoopThroughModel(vPModel, "");

        if (model.getEntitiesCount() == 0) {
            Logger.logW(String.format("No class found in model %s, created Telosys model will be empty",
                    model.getName()));
        }

        // loop through all classes and create their annotations, tags, attributes and links
        phase1(vPModel.getProject());

        // loop through all classes and add annotations and tags to attributes
        phase2(vPModel.getProject());

        // loop through all classes and add annotations and tags to links
        phase3(vPModel.getProject());

        return model;
    }

    /**
     * Recursive method looking for classes in model hierarchy.
     *
     * @param vPModel        Model element to search.
     * @param currentPackage Concatenated names of all on
     *                       the way encountered packages.
     */
    private void LoopThroughModel(IModelElement vPModel, String currentPackage) {
        IModelElement[] vPChildren = vPModel.toChildArray();
        for (IModelElement vPChild : vPChildren) {

            String modelType = vPChild.getModelType();
            if (modelType.equals(IModelElementFactory.MODEL_TYPE_PACKAGE)) {

                String nextPackage = currentPackage;
                if (vPChild.getName().isEmpty()) {
                    Logger.logW("Name of the package cannot be empty. " +
                            "Package name skipped in concatenation.");
                } else {
                    nextPackage = currentPackage.isEmpty() ? vPChild.getName() :
                            currentPackage + Config.getSeparator() + vPChild.getName();
                }

                LoopThroughModel(vPChild, nextPackage);
            } else if (modelType.equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
                sortClass(vPChild, currentPackage);
                addSupportEntities((IClass) vPChild);
            }
        }
    }

    /**
     * Add all classes in class as support entities.
     *
     * @param vPClass Given class.
     */
    private void addSupportEntities(IClass vPClass) {
        IModelElement[] vPElements = vPClass.toChildArray(IModelElementFactory.MODEL_TYPE_CLASS);
        for (IModelElement vPElement : vPElements) {
            if (vPElement.stereotypeCount() > 0) {
                Logger.logW(String.format(
                        "Support class %s: stereotypes on support classes are ignored.",
                        vPElement.getName()));
            }
            // class inside another class = support class
            model.addSupportEntity(new Entity(vPElement.getName(), vPElement.getId()));
        }
    }

    /**
     * Find out if given class is entity class or association class. Add it to corresponding list.
     *
     * @param cls            Class to determine.
     * @param currentPackage Concatenated names of all on the way encountered packages.
     */
    private void sortClass(IModelElement cls, String currentPackage) {
        IAssociationClass vPAssociationClass = getAssociationClass(cls);

        Entity entity;
        if (vPAssociationClass != null) {
            Logger.logD(String.format("Association class: %s", cls.getName()));
            IAssociation vPAssociation;
            if (vPAssociationClass.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
                vPAssociation = (IAssociation) vPAssociationClass.getFrom();
            } else {
                vPAssociation = (IAssociation) vPAssociationClass.getTo();
            }

            entity = new AssociationEntity(cls.getName(), cls.getId(), vPAssociation.getDirection(),
                    vPAssociation.getFromEnd().getEndRelationship());
            model.addAssociationEntity((AssociationEntity) entity);
            associationVPIds.add(vPAssociation.getId());
        } else {
            entity = new Entity(cls.getName(), cls.getId());
            model.addEntity(entity);
        }

        if (!currentPackage.isEmpty()) {
            Anno newAnno = new Anno(Anno.AnnoType.PACKAGE);
            newAnno.addParameter(ParameterFactory.CreateParameter(
                    currentPackage,
                    ParameterFactory.ValueType.STRING,
                    true,
                    false));
            entity.addAnno(newAnno);
        }
    }

    /**
     * Finds association class of given class.
     *
     * @param cls Given class
     * @return Association class if found, null otherwise.
     */
    private IAssociationClass getAssociationClass(IModelElement cls) {
        if (cls.fromRelationshipCount() > 0) {
            IAssociationClass vPAssociation = getAssociationClassHelper(cls.toFromRelationshipArray());
            if (vPAssociation != null) {
                return vPAssociation;
            }
        }
        return getAssociationClassHelper(cls.toToRelationshipArray());
    }

    private IAssociationClass getAssociationClassHelper(ISimpleRelationship[] rels) {
        for (ISimpleRelationship rel : rels) {
            if (rel.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
                return (IAssociationClass) rel;
            }
        }
        return null;
    }

    /**
     * Loop through all classes and create their annotations, tags, attributes and links.
     *
     * @param vPProject Context.
     */
    private void phase1(IProject vPProject) {
        Logger.logD("Phase1");

        // phase one for entities
        for (Iterator<Entity> it = model.getEntitiesIterator(); it.hasNext(); ) {
            Entity entity = it.next();
            // create annotations and tags of the class
            Logger.logD(String.format("phase 1 entity: %s", entity.getName()));
            EntityDecorationParser.parse(vPProject, entity);

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);

        }

        // phase one for association entities
        for (Iterator<AssociationEntity> it = model.getAssociationEntitiesIterator(); it.hasNext(); ) {
            Entity entity = it.next();
            // create annotations and tags of the class
            Logger.logD(String.format("phase 1 entity: %s", entity.getName()));
            EntityDecorationParser.parse(vPProject, entity);

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);
        }

//        // phase one for support entities
//        for (Entity entity : model.getSupportEntities()) {
//            // create annotations and tags of the class
//            Logger.logD(String.format("phase 1 entity: %s", entity.getName()));
//            EntityDecorationParser.parse(vPProject, entity);
//
//            // create attributes and links
//            createAttrsLinksOfEntity(entity, vPProject);
//        }
    }

    /**
     * Creates Attributes and links for given entity.
     *
     * @param entity    Given entity.
     * @param vPProject Current project.
     */
    private void createAttrsLinksOfEntity(Entity entity, IProject vPProject) {
        // list of all vPLinks Ids, to determine if attribute is a part of a link or just an attribute
        IModelElement vPClass = vPProject.getModelElementById(entity.getVpId());
        List<String> vPLinksIds = new ArrayList<>();
        List<IRelationshipEnd> vPRelEnds = new ArrayList<>();
        Collections.addAll(vPRelEnds, vPClass.toFromRelationshipEndArray());
        Collections.addAll(vPRelEnds, vPClass.toToRelationshipEndArray());
        for (IRelationshipEnd vpRelEnd : vPRelEnds) {
            if (vpRelEnd.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END)) {
                IAttribute vPLink = ((IAssociationEnd) vpRelEnd.getOppositeEnd()).getRepresentativeAttribute();

                if (!(((IAssociationEnd) vpRelEnd).getMultiplicity().equals("0..*") ||
                        ((IAssociationEnd) vpRelEnd).getMultiplicity().equals("0..1"))) {
                    Logger.logE(String.format("Entity: %s: The multiplicity of association ends has to be set to '0..1' or '0..*'.",
                            entity.getName()));
                    vPLinksIds.add(null);
                } else if (vPLink == null) {
                    vPLinksIds.add(null);
                } else {
                    vPLinksIds.add(vPLink.getId());
                }

//                if (vPLink == null) {
//                    vPLinksIds.add(null);
//                } else if (vPLink.getMultiplicity().equals("0..*") || vPLink.getMultiplicity().equals("0..1")) {
//                    vPLinksIds.add(vPLink.getId());
//                } else {
//                    Logger.logE("The multiplicity of association ends has to be set to '0..1' or '0..*'.");
//                    vPLinksIds.add(null);
//                }
            }
        }

        IModelElement[] vPAttrs = vPClass.toChildArray(IModelElementFactory.MODEL_TYPE_ATTRIBUTE);
        for (IModelElement vPAttr : vPAttrs) {
            if (vPLinksIds.contains(vPAttr.getId())) {
                // this attribute is a link
                String vPAssociationId =
                        vPRelEnds.get(vPLinksIds.indexOf(vPAttr.getId())).getEndRelationship().getId();
                int associationEntityIndex = associationVPIds.indexOf(vPAssociationId);

                Iterator<AssociationEntity> entities = model.getAssociationEntitiesIterator();
                AssociationEntity associationEntity = null;
                if (associationEntityIndex != -1){
                    associationEntity = entities.next();
                    for (int i = 0; i != associationEntityIndex; associationEntity = entities.next(), i++)
                        ;
                }

//                Entity associationEntity = associationEntityIndex == -1
//                        ? null : model.getAssociationEntities().get(associationEntityIndex);

                boolean isArray = ((IAttribute) vPAttr).getMultiplicity().endsWith("*");
                try {
                    entity.addLink(new Link(
                            vPAttr.getId(),
                            vPAttr.getName(),
                            entity.getParentModel().getEntityByVpId(((IAttribute) vPAttr).getTypeAsModel().getId()),
                            vPAssociationId,
                            associationEntity,
                            isArray
                    ));
                } catch (Exception e) {
                    // should not be a problem here
                    Logger.logE(String.format(
                            "In entity %s, while creating link %s: %s",
                            entity.getName(),
                            vPAttr.getName(),
                            e.getMessage()));
                }
            } else {
                // this attribute is just an attribute
//                if (((IAttribute) vPAttr).getTypeAsString().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
//                    // Attributes of type Cascade Option will be parsed later
//                    continue;
//                }

                try {
                    entity.addAttr(new Attr(vPAttr.getId(), vPAttr.getName(),
                            Attr.AttrType.valueOf(((IAttribute) vPAttr).getTypeAsString().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    Logger.logE(String.format(
                            "In entity %s attribute %s: %s is not a valid data type.",
                            entity.getName(),
                            vPAttr.getName(),
                            ((IAttribute) vPAttr).getTypeAsString()));
                }
            }
        }
    }

    /**
     * Loop through all classes and add annotations and tags to attributes.
     *
     * @param vPProject Current project.
     */
    private void phase2(IProject vPProject) {
        // loop through all classes and add annotations and tags to attributes
        for (Iterator<Entity> it = model.getEntitiesIterator(); it.hasNext(); ) {
            Entity entity = it.next();
            Logger.logD(String.format("phase 2 entity: %s", entity.getName()));
            for (Iterator<Attr> iter = entity.getAttrsIterator(); iter.hasNext(); ) {
                Attr attr = iter.next();
                AttrDecorationParser.parse(vPProject, attr);
            }
        }

        for (Iterator<AssociationEntity> it = model.getAssociationEntitiesIterator(); it.hasNext(); ) {
            Entity entity = it.next();
            Logger.logD(String.format("phase 2 entity: %s", entity.getName()));
            for (Iterator<Attr> iter = entity.getAttrsIterator(); iter.hasNext(); ) {
                Attr attr = iter.next();
                AttrDecorationParser.parse(vPProject, attr);
            }
        }

//        for (Entity entity : model.getSupportEntities()) {
//            Logger.logD(String.format("phase 2 entity: %s", entity.getName()));
//            for (Attr attr : entity.getAttrs()) {
//                try {
//                    AttrDecorationParser.parse(vPProject, attr);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
    }

    /**
     * Loop through all classes and add annotations and tags to links.
     *
     * @param vPProject Current project.
     */
    private void phase3(IProject vPProject) {
        // loop through all classes and add annotations and tags to links
        for (Iterator<Entity> it = model.getEntitiesIterator(); it.hasNext(); ) {
            Entity entity = it.next();
            Logger.logD(String.format("phase 3 entity: %s", entity.getName()));
            for (Iterator<Link> iter = entity.getLinksIterator(); iter.hasNext(); ) {
                Link link = iter.next();
                Logger.logD(String.format("phase 3 link: %s", link.getName()));

                try {
                    LinkDecorationParser.parse(vPProject, link);
                } catch (Exception e) {
                    Logger.logE(String.format(
                            "In Entity %s, link %s: %s",
                            entity.getName(),
                            link.getName(),
                            e.getMessage()));
                }
            }
        }

        // add association entities that are marked with @JoinEntity (should be printed as well) to standard entities
        for (Iterator<AssociationEntity> it = model.getAssociationEntitiesIterator(); it.hasNext(); ) {
            AssociationEntity associationEntity = it.next();

            IEndRelationship iEndRelationship = associationEntity.iEndRelationship;
            String vPClassFromId = iEndRelationship.getFrom().getId();
            String vPClassToId = iEndRelationship.getTo().getId();

            if (associationEntity.multiplicityFrom.equals("0..*") &&
                    associationEntity.multiplicityTo.equals("0..*")) {
                // ManyToMany --> add JOIN_ENTITY
                Anno newAnno = new Anno(Anno.AnnoType.JOIN_ENTITY);
                associationEntity.addAnno(newAnno);
                model.addEntity(associationEntity);
            } else {
                // not ManyToMany --> merge with the right direction
                try {
                    Entity mergedEntity = associationEntity.direction == 0 ?
                            associationEntity.getParentModel().getEntityByVpId(vPClassFromId) :
                            associationEntity.getParentModel().getEntityByVpId(vPClassToId);

                    Logger.logD(String.format("Association entity %s will be merged into entity %s",
                            associationEntity.getName(),
                            mergedEntity.getName()));

                    // merge
                    for (Iterator<Attr> iter = associationEntity.getAttrsIterator(); iter.hasNext(); ) {
                        Attr attr = iter.next();
                        if (!mergedEntity.addAttr(attr)) {
                            mergedEntity.mergeAttr(attr);
                        }
                    }
                } catch (Exception e) {
                    Logger.logE(
                            String.format("While merging Association Entity %s: %s",
                                    associationEntity.getName(),
                                    e.getMessage()));
                }
            }
        }
    }
}
