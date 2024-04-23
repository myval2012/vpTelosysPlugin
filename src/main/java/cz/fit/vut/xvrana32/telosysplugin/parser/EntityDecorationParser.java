/**
 *
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
package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoCommon;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ConstraintDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ParamDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;


public class EntityDecorationParser {
    public static final AnnoDeclaration[] annoDeclarations = {
            new AnnoCommon("Context", Anno.AnnoType.CONTEXT, new ParamDeclaration[]
                    {new ParamDeclaration("context", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbCatalog", Anno.AnnoType.DB_CATALOG, new ParamDeclaration[]
                    {new ParamDeclaration("dbCatalog", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbComment", Anno.AnnoType.DB_COMMENT, new ParamDeclaration[]
                    {new ParamDeclaration("dbComment", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbSchema", Anno.AnnoType.DB_SCHEMA, new ParamDeclaration[]
                    {new ParamDeclaration("dbSchema", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbTable", Anno.AnnoType.DB_TABLE, new ParamDeclaration[]
                    {new ParamDeclaration("dbTable", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbTableSpace", Anno.AnnoType.DB_TABLESPACE, new ParamDeclaration[]
                    {new ParamDeclaration("dbTableSpace", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("DbView", Anno.AnnoType.DB_VIEW, new ParamDeclaration[]{}),
            new AnnoCommon("Domain", Anno.AnnoType.DOMAIN, new ParamDeclaration[]
                    {new ParamDeclaration("domain", ITaggedValueDefinition.TYPE_TEXT,
                            true, false)}),
            new AnnoCommon("InMemoryRepository", Anno.AnnoType.IN_MEMORY_REPOSITORY, new ParamDeclaration[]{}),
            new AnnoCommon("ReadOnly", Anno.AnnoType.READ_ONLY, new ParamDeclaration[]{}),
    };

    private static final ConstraintDeclaration[] constraints = {
            new ConstraintDeclaration("InMemoryRepository", Anno.AnnoType.IN_MEMORY_REPOSITORY),
            new ConstraintDeclaration("ReadOnly", Anno.AnnoType.READ_ONLY),
    };

    public static void parse(IProject vPProject, Entity entity) {
        IClass vPClass = (IClass) vPProject.getModelElementById(entity.getVpId());

        if (vPClass.isAbstract()) {
            entity.addAnno(new Anno(Anno.AnnoType.ABSTRACT));
        }

        // check for generalization
        if (vPClass.toRelationshipCount() > 0) {
            ISimpleRelationship[] vPRels = vPClass.toToRelationshipArray();
            for (ISimpleRelationship vPRel : vPRels) {
                if (vPRel.getModelType().equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
                    Anno newAnno = new Anno(Anno.AnnoType.EXTENDS);

                    try {
                        newAnno.addParameter(ParameterFactory.CreateParameter(
                                entity.getParentModel().getEntityByVpId(vPRel.getFrom().getId()),
                                ParameterFactory.ValueType.LINK,
                                false,
                                false
                        ));
                        entity.addAnno(newAnno);
                    }
                    catch (Exception e){
                        Logger.logE(String.format(
                                "While creating @Extends for class %s: %s",
                                entity.getName(),
                                e.getMessage()));
                    }

                }
            }
        }


        DecorationParser.checkTaggedValuesStereotype(vPClass.getTaggedValues());
        DecorationParser.parseNonSpecialAnnosAndTags(annoDeclarations, vPClass, entity, entity);

        // constraints
        DecorationParser.parseConstraints(constraints, entity, vPClass);
    }
}
