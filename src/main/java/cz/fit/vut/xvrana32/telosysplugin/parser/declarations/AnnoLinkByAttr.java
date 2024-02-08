package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

/**
 * Special annotation declaration for parsing @LinkByAttr
 * <br> <br>
 * The arguments for this annotation are in support entity.
 */
public class AnnoLinkByAttr extends AnnoDeclaration {

    public AnnoLinkByAttr(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
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
//            Logger.log("Support entity was not found.");
            return null;
        }

        // add all attributes of the support entity to both the ParentEntity and the Annotation
        Entity parentEntity = model.getEntityByVpId(vPElement.getParent().getId());
        Anno newAnno = new Anno(annoType);

        for (Attr attr:supportEntity.getAttrs()){
            parentEntity.addAttr(attr);
            newAnno.addParameter(ParameterFactory.CreateParameter(ParameterFactory.ValueType.LINK_ATTRIBUTE, attr));
        }

        return newAnno;
    }
}
