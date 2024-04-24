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
import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ConstraintDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

import java.util.Iterator;

/**
 * Common parser methods.
 */
abstract public class DecorationParser {
    /**
     * Parses all stereotypes and creates annotations and tags.
     * @param annoDeclarations Array of accepted stereotype annotation declarations.
     * @param vPModel Model of currently parsed model element for context.
     * @param decoratedElement Inner representation of currently parsed element.
     * @param entity Entity to which the element belongs to, if decoratedElement is itself the entity,
     *               pass the decorated element here
     */
    static void parseNonSpecialAnnosAndTags(
            AnnoDeclaration[] annoDeclarations,
            IModelElement vPModel,
            DecoratedElement decoratedElement,
            Entity entity) {

        Iterator stereotypes = vPModel.stereotypeModelIterator();
        String msgCommonHeader = vPModel.getModelType().equals(IModelElementFactory.MODEL_TYPE_ATTRIBUTE) ?
                String.format("In class %s, attribute %s,", vPModel.getParent().getName(), vPModel.getName()) :
                String.format("In class %s,", vPModel.getName());

        while (stereotypes.hasNext()) {
            IStereotype vPStereotype = (IStereotype) stereotypes.next();

            if (vPStereotype.getName().startsWith("@")) {
                // annotation
                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(
                        vPStereotype.getName().substring(1),
                        annoDeclarations);

                if (annoDeclaration == null) {
                    Logger.logE(String.format("%s stereotype %s: Unknown annotation, stereotype skipped.",
                            msgCommonHeader,
                            vPStereotype.getName()));
                    continue;
                }

                try {
                    Anno newAnno = annoDeclaration.createAnno(vPModel, vPStereotype, entity.getParentModel());
                    decoratedElement.addAnno(newAnno);
                } catch (Exception e) {
                    Logger.logE(String.format("%s stereotype %s: %s",
                            msgCommonHeader,
                            vPStereotype.getName(),
                            e.getMessage()));
                }

            } else if (vPStereotype.getName().startsWith("#")) {
                // tags
                try {
                    decoratedElement.addTag(TagParser.parseTag(vPModel, vPStereotype, entity));
                } catch (Exception e) {
                    Logger.logW(String.format("%s stereotype %s: %s",
                            msgCommonHeader,
                            vPStereotype.getName(),
                            e.getMessage()));
                }
            } else {
                String msg = "All stereotypes have to start with @ or # to have an impact. Stereotype skipped.";
                Logger.logW(String.format("%s stereotype %s: %s",
                        msgCommonHeader,
                        vPStereotype.getName(),
                        msg));
            }
        }
    }

    /**
     * Finds the appropriate annotation declaration by given name.
     * @param name Name of searched annotation.
     * @param annoDeclarations Array of annotation declarations in which to search.
     * @return Declaration of annotation if exists, null otherwise
     */
    static AnnoDeclaration getAnnoDeclarationByName(String name, AnnoDeclaration[] annoDeclarations) {
        for (AnnoDeclaration annoDeclaration : annoDeclarations) {
            if (annoDeclaration.name.equals(name)) {
                return annoDeclaration;
            }
        }
        return null;
    }

    /**
     * Checks that all tagged values have a stereotype, if not warning message is printed.
     *
     * @param vPTaggedValueContainer Container for tagged value to check.
     */
    static void checkTaggedValuesStereotype(ITaggedValueContainer vPTaggedValueContainer) {
        if (vPTaggedValueContainer == null) {
            return;
        }

        for (ITaggedValue vPTaggedValue : vPTaggedValueContainer.toTaggedValueArray()) {
            if (vPTaggedValue.getTagDefinitionStereotype() == null) {
                Logger.logW(String.format(
                        "Tagged value %s does not have a stereotype. This tagged value has no effect on compilation",
                        vPTaggedValue.getName()));
            }
        }
    }

    /**
     * Parses all constraints and creates their annotations.
     * @param constraintList Array of all accepted constraints.
     * @param decoratedElement Inner representation of currently parsed element.
     * @param vPModelElement Currently parsed model element.
     */
    static void parseConstraints(
            ConstraintDeclaration[] constraintList,
            DecoratedElement decoratedElement,
            IModelElement vPModelElement) {
        Iterator iterator = vPModelElement.constraintsIterator();
        while (iterator.hasNext()) {
            IModelElement vPConstraint = (IModelElement) iterator.next();
            boolean constraintFound = false;
            for (ConstraintDeclaration constraintDeclaration : constraintList) {
                if (constraintDeclaration.name.equals(vPConstraint.getName())) {
                    constraintFound = true;
                    if (!decoratedElement.addAnno(new Anno(constraintDeclaration.annoType))) {
                        Logger.logW(String.format(
                                "%s already contains annotation %s",
                                decoratedElement.getName(), "@" + constraintDeclaration.annoType.toString()
                        ));
                    }
                    break;
                }
            }

            if (!constraintFound) {
                Logger.logW(String.format(
                        "Constraint %s doesn't exist or can't be assigned to %s",
                        "@" + vPConstraint.getName(),
                        decoratedElement.getName()));
            }
        }
    }
}
