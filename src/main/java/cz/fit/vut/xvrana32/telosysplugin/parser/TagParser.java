package cz.fit.vut.xvrana32.telosysplugin.parser;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.Entity;
import cz.fit.vut.xvrana32.telosysplugin.elements.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.Tag;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

public class TagParser {
    public static Tag parseTag(IModelElement vpElement, IStereotype vPStereotype, Entity entity) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vpElement.getTaggedValues();
        IParameter param = null;

        for (ITaggedValue vPTaggedValue: vPTaggedValueContainer.toTaggedValueArray()){
            if (vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype)){
                if (param != null){
//                    Logger.log(String.format("It looks like this tag %s has more than one parameter!!!",
//                            vPStereotype.getName()));
                    return null;
                }
                else
                {
//                    Logger.log("Adding a parameter to this tag.");
                    param = ParameterFactory.CreateParameter(vPTaggedValue, entity.getParentModel(), true); // always quoted
                }
            }
        }

//        Logger.log(String.format("Creating tag with name: %s", vPStereotype.getName()));
        return new Tag(vPStereotype.getName().substring(1), param);
    }
}
