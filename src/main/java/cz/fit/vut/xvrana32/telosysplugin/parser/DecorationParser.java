package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.ConstraintDeclaration;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

import java.util.Iterator;
import java.util.List;

abstract public class DecorationParser {
    static void parseNonSpecialAnnosAndTags(
            AnnoDeclaration[] annoDeclarations,
            IModelElement vPModel,
            DecoratedElement decoratedElement,
            Entity entity) {

        Iterator stereotypes = vPModel.stereotypeModelIterator();
        String msgCommonHeader = vPModel.getModelType().equals("Attribute") ?
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
//                    if (newAnno != null) {
                    decoratedElement.addAnno(newAnno);
//                    }
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
