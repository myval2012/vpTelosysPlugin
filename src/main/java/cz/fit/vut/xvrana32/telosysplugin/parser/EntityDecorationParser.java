package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoCommon;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ParamDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.Iterator;

public class EntityDecorationParser {
    private static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("Context", Anno.AnnoType.CONTEXT, new ParamDeclaration[]
                    {new ParamDeclaration("context", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbCatalog", Anno.AnnoType.DB_CATALOG, new ParamDeclaration[]
                    {new ParamDeclaration("dbCatalog", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbComment", Anno.AnnoType.DB_COMMENT, new ParamDeclaration[]
                    {new ParamDeclaration("dbComment", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbSchema", Anno.AnnoType.DB_SCHEMA, new ParamDeclaration[]
                    {new ParamDeclaration("dbSchema", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbTable", Anno.AnnoType.DB_TABLE, new ParamDeclaration[]
                    {new ParamDeclaration("dbTable", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbTableSpace", Anno.AnnoType.DB_TABLESPACE, new ParamDeclaration[]
                    {new ParamDeclaration("dbTableSpace", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("DbView", Anno.AnnoType.DB_VIEW, new ParamDeclaration[]{}),
            new AnnoCommon("Domain", Anno.AnnoType.DOMAIN, new ParamDeclaration[]
                    {new ParamDeclaration("domain", ITaggedValueDefinition.TYPE_TEXT)}),
            new AnnoCommon("InMemoryRepository", Anno.AnnoType.IN_MEMORY_REPOSITORY, new ParamDeclaration[]{}),
            new AnnoCommon("ReadOnly", Anno.AnnoType.READ_ONLY, new ParamDeclaration[]{}),
    };

//    private static final String[] constraints = {
//            "@InMemoryRepository",
//            "@ReadOnly"
//    };

    public static void parse(IProject vPProject, Entity entity) throws Exception {
        IClass vPClass = (IClass) vPProject.getModelElementById(entity.getVpId());

        if (vPClass.isAbstract()) {
            entity.addAnno(new Anno(Anno.AnnoType.ABSTRACT));
        }

        // check for generalization
        if (vPClass.toRelationshipCount() > 0) {
            ISimpleRelationship[] vPRels = vPClass.toToRelationshipArray();
            for (ISimpleRelationship vPRel : vPRels) {
                if (vPRel.getModelType().equals("Generalization")) {
                    Anno newAnno = new Anno(Anno.AnnoType.EXTENDS);
                    newAnno.addParameter(ParameterFactory.CreateParameter(
                            ParameterFactory.ValueType.LINK_ENTITY,
                            entity.getParentModel().getEntityByVpId(vPRel.getFrom().getId())
                    ));
                    entity.addAnno(newAnno);
                }
            }
        }

        // TODO constraints

        // TODO annotations and tags
        // TODO common implementation
        Iterator stereotypes = vPClass.stereotypeModelIterator();
        while (stereotypes.hasNext()) {
            IStereotype stereotype = (IStereotype) stereotypes.next();
            if (stereotype.getName().startsWith("@")) // annotation
            {
//                Logger.log(String.format("Found an annotation in class: %s, has name: %s",
//                        vPClass.getName(),
//                        stereotype.getName()));
                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(stereotype.getName().substring(1));
                if (annoDeclaration != null) {
                    Anno newAnno = annoDeclaration.createAnno(vPClass,
                            stereotype, entity.getParentModel());
                    if (newAnno != null){
                        entity.addAnno(newAnno);
                    }
                }
//                entity.addAnno(evaluateAnno(vPClass, stereotype, entity));
            } else if (stereotype.getName().startsWith("#")) // tags
            {
                entity.addTag(TagParser.parseTag(vPClass, stereotype, entity));
//                Logger.log(String.format("Found a tag in class: %s, has name: %s",
//                        vPClass.getName(),
//                        stereotype.getName()));
            }
        }
    }

    // TODO common implementation
    private static AnnoDeclaration getAnnoDeclarationByName(String name) {
        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
            if (annoDeclaration.name.equals(name)) {
                return annoDeclaration;
            }
        }
        return null;
    }
}
