package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.parser.declarations.AnnoDeclaration;

import java.util.Iterator;

abstract public class DecorationParser {
    static void parseNonSpecialAnnosAndTags(
            AnnoDeclaration[] annoDeclarations,
            IModelElement vPModel,
            DecoratedElement decoratedElement,
            Entity entity) throws Exception {

        Iterator stereotypes = vPModel.stereotypeModelIterator();

        while (stereotypes.hasNext()) {
            IStereotype stereotype = (IStereotype) stereotypes.next();

            if (stereotype.getName().startsWith("@")) // annotation
            {
                AnnoDeclaration annoDeclaration = getAnnoDeclarationByName(
                        stereotype.getName().substring(1),
                        annoDeclarations);

                if (annoDeclaration != null) {
                    Anno newAnno = annoDeclaration.createAnno(vPModel, stereotype, entity.getParentModel());
                    if (newAnno != null) {
                        decoratedElement.addAnno(newAnno);
                    }
                }
            } else if (stereotype.getName().startsWith("#")) // tags
            {
                decoratedElement.addTag(TagParser.parseTag(vPModel, stereotype, entity));
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
}
