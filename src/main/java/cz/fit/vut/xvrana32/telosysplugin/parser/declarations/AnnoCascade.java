package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.CascadeOptions;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

/**
 * Special annotation declaration for parsing @LinkByAttr
 * <br> <br>
 * The arguments for this annotation are in support entity.
 */
public class AnnoCascade extends AnnoDeclaration {

    public AnnoCascade(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) {
        // check if the tagged value is there and read the ID of the model

        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();
        ITaggedValue vPTaggedValue = vPTaggedValueContainer.getTaggedValueByName(params[0].name);
        if (!checkTaggedValue(vPStereotype, vPTaggedValue, params[0])){
            return null; // TODO error
        }

        String supportClassId = vPTaggedValue.getValueAsModel().getId();

        // find the support entity
        Entity supportEntity = model.getSupportEntityByVpId(supportClassId);
        if (supportEntity == null){
            Logger.log("Support entity was not found.");
            return null;
        }

        // add all attributes of the support entity to both the ParentEntity and the Annotation
        Anno newAnno = new Anno(annoType);

        for(IModelElement vPAttr : vPTaggedValue.getValueAsModel().toChildArray("Attribute")){
            // TODO check if they are all of type "Cascade options"
           newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.ENUM_CASCADE,
                   CascadeOptions.valueOf(((IAttribute)vPAttr).getInitialValue())));
            Logger.log(String.format("********* Added param to anno %s with value %s", vPAttr.getName(),
                    ((IAttribute)vPAttr).getInitialValue()));
        }
        return newAnno;
    }
}
