package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelParser {
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

        // TODO loop through all classes and create their annotations, tags, attributes and links
        phase1(vPModel.getProject());

        // TODO loop through all classes and add annotations and tags to attributes
        phase2(vPModel.getProject());

        // TODO loop through all classes and add annotations and tags to links
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
                LoopThroughModel(vPChild,
                        currentPackage.isEmpty() ? vPChild.getName() : currentPackage + "." + vPChild.getName());
            } else if (modelType.equals("Class")) {
                sortClass(vPChild, currentPackage);
                addSupportEntities((IClass) vPChild);
            }
        }
    }

    private void addSupportEntities(IClass vPClass) {
        IModelElement[] vPElements = vPClass.toChildArray("Class");
        for (IModelElement vPElement : vPElements) {
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

        // decide if this is a association class or standard
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
//                IAssociationClass vPAssociationClass = (IAssociationClass) rel;
//                if (vPAssociationClass.getFrom().getModelType().equals("Association")){
//                    return (IAssociation) vPAssociationClass.getFrom();
//                }
//                return (IAssociation) vPAssociationClass.getTo();
            }
        }
        return null;
    }

    private void phase1(IProject vPProject) {
        // loop through all classes and create their annotations, tags, attributes and links
        for (Entity entity : standardEntities) {
//            Logger.log(String.format("Parsing Class: %s", vPClass.getName()));

            // create annotations and tags of the class
            try {
                EntityDecorationParser.parse(vPProject, entity);
            } catch (Exception e) {
                Logger.log(String.format("There was some error: %s", e.getMessage()));
            }

            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);

            // list of all vPLinks Ids, to determine if attribute is a part of a link or just an attribute
//            IModelElement vPClass = vPProject.getModelElementById(entity.getVpId());
//            List<String> vPLinksIds = new ArrayList<>();
//            List<IRelationshipEnd> vPRelEnds = new ArrayList<>();
//            Collections.addAll(vPRelEnds, vPClass.toFromRelationshipEndArray());
//            Collections.addAll(vPRelEnds, vPClass.toToRelationshipEndArray());
//            for (IRelationshipEnd vpRelEnd : vPRelEnds) {
//                if (vpRelEnd.getModelType().equals("AssociationEnd")) {
//                    IAttribute vPLink = ((IAssociationEnd) vpRelEnd.getOppositeEnd()).getRepresentativeAttribute();
//                    vPLinksIds.add(vPLink == null ? null : vPLink.getId());
//                }
//            }

//            IModelElement[] vPAttrs = vPClass.toChildArray("Attribute");
//            for (IModelElement vPAttr : vPAttrs) {
//                if (vPLinksIds.contains(vPAttr.getId())) {
//                    // this attribute is a link
//                    Logger.log(String.format("%s is a link", vPAttr.getName()));
//                    String vPAssociationId =
//                            vPRelEnds.get(vPLinksIds.indexOf(vPAttr.getId())).getEndRelationship().getId();
//                    Logger.log(String.format("The association ID is %s", vPAssociationId));
//                    int associationEntityIndex = associationVPIds.indexOf(vPAssociationId);
//                    Entity associationEntity = associationEntityIndex == -1
//                            ? null : associationEntities.get(associationEntityIndex);
//
//                    Logger.log(String.format("Association class of this association is named %s",
//                            associationEntity == null ? null : associationEntity.getName()));
//
//                    entity.addLink(new Link(
//                            vPAttr.getId(),
//                            vPAttr.getName(),
//                            entity.getParentModel().getEntityByVpId(((IAttribute) vPAttr).getTypeAsModel().getId()),
//                            vPAssociationId,
//                            associationEntity
//                    ));
//                } else {
//                    // this attribute is just an attribute
////                    Logger.log(String.format("%s is an attribute", vPAttr.getName()));
////                    Logger.log(String.format("The attrType has a name: %s",
////                            ((IAttribute) vPAttr).getTypeAsModel().getName()));
//
//                    entity.addAttr(new Attr(vPAttr.getId(), vPAttr.getName(),
//                            Attr.AttrType.valueOf(((IAttribute) vPAttr).getTypeAsModel().getName().toUpperCase())));
//                }
//            }
        }

        for (Entity entity : associationEntities) {
            // create annotations and tags of the class
            Logger.log(String.format("phase 1 entity: %s", entity.getName()));
            try {
                EntityDecorationParser.parse(vPProject, entity);
            } catch (Exception e) {
                Logger.log(String.format("There was some error: %s", e.getMessage()));
            }

            Logger.log("creating attributes and links...");
            // create attributes and links
            createAttrsLinksOfEntity(entity, vPProject);
        }

        for (Entity entity : supportEntities) {
            // create annotations and tags of the class
            Logger.log(String.format("phase 1 entity: %s", entity.getName()));
            try {
                EntityDecorationParser.parse(vPProject, entity);
            } catch (Exception e) {
                Logger.log(String.format("There was some error: %s", e.getMessage()));
            }

            Logger.log("creating attributes and links...");
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

                entity.addLink(new Link(
                        vPAttr.getId(),
                        vPAttr.getName(),
                        entity.getParentModel().getEntityByVpId(((IAttribute) vPAttr).getTypeAsModel().getId()),
                        vPAssociationId,
                        associationEntity,
                        isArray
                ));
            } else {
                // this attribute is just an attribute
//                Logger.log(String.format("%s is an attribute", vPAttr.getName()));
//                    Logger.log(String.format("The attrType has a name: %s",
//                            ((IAttribute) vPAttr).getTypeAsModel().getName()));
                try {
                    entity.addAttr(new Attr(vPAttr.getId(), vPAttr.getName(),
                            Attr.AttrType.valueOf(((IAttribute) vPAttr).getTypeAsModel().getName().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    Logger.log(String.format("Error occurred while parsing attribute: %s", e.getMessage()));
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
//        for (Entity entity : associationEntities) {
//            for (Attr attr : entity.getAttrs()) {
//                Logger.log(String.format("phase 2 entity: %s", entity.getName()));
////                Logger.log(String.format("Parsing Attribute: %s", attr.getName()));
//
//                try {
//                    AttrDecorationParser.parse(vPProject, attr);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }

//        for (Entity entity : supportEntities) {
//            for (Attr attr : entity.getAttrs()) {
//                Logger.log(String.format("phase 2 entity: %s", entity.getName()));
////                Logger.log(String.format("Parsing Attribute: %s", attr.getName()));
//
//                try {
//                    AttrDecorationParser.parse(vPProject, attr);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }

        // add association entities that are marked with @JoinEntity (should be printed as well) to standard entities
        for (Entity entity : associationEntities) {
            if (entity.containsAnnoType(Anno.AnnoType.JOIN_ENTITY)) {
                standardEntities.add(entity);
            }
        }
//        Logger.log("Let's continue");

    }
}
