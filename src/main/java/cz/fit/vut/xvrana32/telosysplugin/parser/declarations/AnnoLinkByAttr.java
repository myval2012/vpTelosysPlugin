package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.*;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.parser.AttrDecorationParser;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.ParameterFactory;

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
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        // check if the tagged value is there and read the ID of the model
        ITaggedValue vPTaggedValue = getValidTaggedValue(vPElement.getTaggedValues(), vPStereotype, params[0]);

        if (vPTaggedValue == null) {
            throw new Exception(String.format(
                    "Tagged value %s is missing or is not associated with %s stereotype.",
                    params[0].name,
                    vPStereotype.getName()));
        }

        if (vPTaggedValue.getValueAsModel() == null) {
            throw new Exception(String.format("Mandatory tagged value %s does not have a value.", params[0].name));
        }

        String supportClassId = vPTaggedValue.getValueAsModel().getId();

        // find the support entity
        Entity supportEntity = model.getSupportEntityByVpId(supportClassId);
        if (supportEntity == null) {
            throw new Exception(String.format("Support class %s was not found inside the supported class",
                    vPTaggedValue.getValueAsModel().getName()));
        }

        IModelElement[] vPAttributes = vPTaggedValue.getValueAsModel().toChildArray(IModelElementFactory.MODEL_TYPE_ATTRIBUTE);
        for (IModelElement vPAttribute : vPAttributes) {
            Attr newAttr = new Attr(
                    vPAttribute.getId(),
                    vPAttribute.getName(),
                    Attr.AttrType.valueOf(((IAttribute) vPAttribute).getTypeAsString().toUpperCase()));
            supportEntity.addAttr(newAttr);
            AttrDecorationParser.parse(vPElement.getProject(), newAttr);
        }

        // check mandatory values, for link by attr at least one parameter in supp class
        if (supportEntity.getAttrs().size() == 0) {
            throw new Exception(String.format(
                    "There has to be at least one attribute in the %s support class.",
                    supportEntity.getName()));
        }

        // add all attributes of the support entity to both the ParentEntity and the Annotation
        Entity parentEntity = model.getEntityByVpId(vPElement.getParent().getId());
        Anno newAnno = new Anno(annoType);
        for (Attr attr : supportEntity.getAttrs()) {
            if (!parentEntity.addAttr(attr)){
                parentEntity.mergeAttr(attr);
            }

            newAnno.addParameter(ParameterFactory.CreateParameter(
                    attr,
                    ParameterFactory.ValueType.LINK,
                    false,
                    false));
        }

        return newAnno;
    }
}
