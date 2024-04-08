package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Tag;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

public class TagParser {
    public static Tag parseTag(IModelElement vpElement, IStereotype vPStereotype, Entity entity) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vpElement.getTaggedValues();
        IParameter param = null;

        for (ITaggedValue vPTaggedValue : vPTaggedValueContainer.toTaggedValueArray()) {
            if (vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype)) {
                if (param != null) {
                    throw new Exception("Tag can have at most one tagged value. Stereotype skipped.");
                } else {
                    if (!(vPTaggedValue.getType() == ITaggedValue.TYPE_MODEL_ELEMENT
                            || vPTaggedValue.getType() == ITaggedValue.TYPE_TEXT)) {
                        throw new Exception("The only supported tagged value types for Telosys tags are model element and text. Stereotype skipped.");
                    }

                    param = ParameterFactory.CreateParameter(vPTaggedValue, entity.getParentModel(),
                            true, true); // always quoted
                }
            }
        }

        return new Tag(vPStereotype.getName().substring(1), param);
    }
}
