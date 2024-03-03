package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelParser {
    // TODO refactoring
    List<Entity> standardEntities = new ArrayList<>();
    List<Entity> supportEntities = new ArrayList<>();
    List<Entity> associationEntities = new ArrayList<>();
    List<String> associationVPIds = new ArrayList<>();

    public Model parse(IModelElement vPModel) {
        Model model = new Model(vPModel.getName(), vPModel.getId());
        LoopThroughModel(vPModel, "");
        model.setEntities(standardEntities);
        model.setAssociationEntities(associationEntities);
        model.setSupportEntities(supportEntities);

        if (standardEntities.size() == 0) {
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

    private void LoopThroughModel(IModelElement vPModel, String currentPackage) {
//        Logger.log(String.format("In package: %s", currentPackage));
        IModelElement[] vPChildren = vPModel.toChildArray();
        for (IModelElement vPChild : vPChildren) {
//            Logger.Log("Found modelElement: " + vPChild.getName() + ", of type " + vPChild.getModelType() +
//                    ", whose parent is : " + vPChild.getParent().getName() + ", id is: " + vPChild.getId());
            String modelType = vPChild.getModelType();
            if (modelType.equals("Package")) {

                String nextPackage = currentPackage;
                if (vPChild.getName().isEmpty()) {
                    Logger.logW("Name of the package cannot be empty. Package name skipped in concatenation.");
                } else {
                    nextPackage = currentPackage.isEmpty() ? vPChild.getName() : currentPackage + "." + vPChild.getName();
                }

                LoopThroughModel(vPChild,
//                        currentPackage.isEmpty() ? vPChild.getName() : currentPackage + "." + vPChild.getName()
                        nextPackage
                );
            } else if (modelType.equals("Class")) {
                sortClass(vPChild, currentPackage);
                addSupportEntities((IClass) vPChild);
            }
        }
    }

    private void addSupportEntities(IClass vPClass) {
        IModelElement[] vPElements = vPClass.toChildArray("Class");
        for (IModelElement vPElement : vPElements) {
            if (vPElement.stereotypeCount() > 0) {
                Logger.logW(String.format(
                        "Support class %s: stereotypes on support classes are ignored.",
                        vPElement.getName()));
            }
            // class inside another class = support class
            supportEntities.add(new Entity(vPElement.getName(), vPElement.getId()));
        }
    }

    private void sortClass(IModelElement cls, String currentPackage) {
        Entity entity = new Entity(cls.getName(), cls.getId());
        if (!currentPackage.isEmpty()) {
            Anno newAnno = new Anno(Anno.AnnoType.PACKAGE);
            newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, currentPackage));
            entity.addAnno(newAnno);
        }

        // decide if this is an association class or standard
        IAssociationClass vPAssociationClass = getAssociationClass(cls);
        if (vPAssociationClass != null) {
            IAssociation vPAssociation;
//            Logger.Log(String.format("Association class: %s", cls.getName()));
            associationEntities.add(entity);
            if (vPAssociationClass.getFrom().getModelType().equals("Association")) {
                vPAssociation = (IAssociation) vPAssociationClass.getFrom();
            } else {
                vPAssociation = (IAssociation) vPAssociationClass.getTo();
            }
            associationVPIds.add(vPAssociation.getId());
        } else {
//            Logger.Log(String.format("Class: %s", cls.getName()));
            standardEntities.add(entity);
        }
    }

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
            if (rel.getModelType().equals("AssociationClass")) {
                return (IAssociationClass) rel;
            }
        }
        return null;
    }

    private void phase1(IProject vPProject) {
        // loop through all classes and create their annotations, tags, attributes and links
        Logger.log("Phase1");

        for (Entity entity : standardEntities) {
            // create annotations and tags of the class
            Logger.log(String.format("phase 1 entity: %s", entity.getName()));
            EntityDecorationParser.parse(vPProject, entity);

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);

        }

        for (Entity entity : associationEntities) {
            // create annotations and tags of the class
            Logger.log(String.format("phase 1 entity: %s", entity.getName()));
            EntityDecorationParser.parse(vPProject, entity);

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);
        }

        for (Entity entity : supportEntities) {
            // create annotations and tags of the class
            Logger.log(String.format("phase 1 entity: %s", entity.getName()));
            EntityDecorationParser.parse(vPProject, entity);

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);
        }
    }

    private void createAttrsLinksOfEntity(Entity entity, IProject vPProject) {
        // list of all vPLinks Ids, to determine if attribute is a part of a link or just an attribute
        IModelElement vPClass = vPProject.getModelElementById(entity.getVpId());
        List<String> vPLinksIds = new ArrayList<>();
        List<IRelationshipEnd> vPRelEnds = new ArrayList<>();
        Collections.addAll(vPRelEnds, vPClass.toFromRelationshipEndArray());
        Collections.addAll(vPRelEnds, vPClass.toToRelationshipEndArray());
        for (IRelationshipEnd vpRelEnd : vPRelEnds) {
            if (vpRelEnd.getModelType().equals("AssociationEnd")) {
                IAttribute vPLink = ((IAssociationEnd) vpRelEnd.getOppositeEnd()).getRepresentativeAttribute();
                vPLinksIds.add(vPLink == null ? null : vPLink.getId());
            }
        }

        IModelElement[] vPAttrs = vPClass.toChildArray("Attribute");
        for (IModelElement vPAttr : vPAttrs) {
            if (vPLinksIds.contains(vPAttr.getId())) {
                // this attribute is a link
//                Logger.log(String.format("%s is a link", vPAttr.getName()));
                String vPAssociationId =
                        vPRelEnds.get(vPLinksIds.indexOf(vPAttr.getId())).getEndRelationship().getId();
//                Logger.log(String.format("The association ID is %s", vPAssociationId));
                int associationEntityIndex = associationVPIds.indexOf(vPAssociationId);
                Entity associationEntity = associationEntityIndex == -1
                        ? null : associationEntities.get(associationEntityIndex);

//                Logger.log(String.format("Association class of this association is named %s",
//                        associationEntity == null ? null : associationEntity.getName()));

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
//                Logger.log(String.format("%s is an attribute", vPAttr.getName()));
//                    Logger.log(String.format("The attrType has a name: %s",
//                            ((IAttribute) vPAttr).getTypeAsModel().getName()));

                if (((IAttribute) vPAttr).getTypeAsString().equals(Constants.GTTSuppModelConstants.GTT_CASCADE_OPTIONS_CLASS_NAME)) {
                    // Attributes of type Cascade Option will be parsed later
                    continue;
                }

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

    private void phase2(IProject vPProject) {
        for (Entity entity : standardEntities) {
            Logger.log(String.format("phase 2 entity: %s", entity.getName()));
            for (Attr attr : entity.getAttrs()) {
//                Logger.log(String.format("Parsing Attribute: %s", attr.getName()));

                try {
                    AttrDecorationParser.parse(vPProject, attr);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (Entity entity : associationEntities) {
            Logger.log(String.format("phase 2 entity: %s", entity.getName()));
            for (Attr attr : entity.getAttrs()) {
//                Logger.log(String.format("Parsing Attribute: %s", attr.getName()));

                try {
                    AttrDecorationParser.parse(vPProject, attr);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (Entity entity : supportEntities) {
            Logger.log(String.format("phase 2 entity: %s", entity.getName()));
            for (Attr attr : entity.getAttrs()) {
//                Logger.log(String.format("Parsing Attribute: %s", attr.getName()));

                try {
                    AttrDecorationParser.parse(vPProject, attr);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void phase3(IProject vPProject) {
        for (Entity entity : standardEntities) {
            Logger.log(String.format("phase 3 entity: %s", entity.getName()));
            for (Link link : entity.getLinks()) {
                Logger.log(String.format("phase 3 link: %s", link.getName()));
                try {
                    LinkDecorationParser.parse(vPProject, link);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // add association entities that are marked with @JoinEntity (should be printed as well) to standard entities
        for (Entity entity : associationEntities) {
            if (entity.containsAnnoType(Anno.AnnoType.JOIN_ENTITY)) {
                standardEntities.add(entity);
            }
        }
    }
}
