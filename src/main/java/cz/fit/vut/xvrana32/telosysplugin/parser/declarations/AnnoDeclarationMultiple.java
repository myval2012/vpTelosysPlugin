package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.List;

public abstract class AnnoDeclarationMultiple extends AnnoDeclaration {
    public AnnoDeclarationMultiple(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    protected void findTaggedValues(IModelElement vPElement,
                                    IStereotype vPStereotype,
                                    Model model,
                                    List<IParameter> parameters) throws Exception {

        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();
        for (ParamDeclaration paramDeclaration : params) {

            ITaggedValue vPTaggedValue = getValidTaggedValue(vPTaggedValueContainer, vPStereotype, paramDeclaration);

            if (vPTaggedValue != null) {
                parameters.add(ParameterFactory.CreateParameter(vPTaggedValue, model, paramDeclaration.textQuoted));
            } else {
                throw new Exception(String.format(
                        "Tagged value %s is missing or is not associated with %s stereotype.",
                        paramDeclaration.name,
                        vPStereotype.getName()));
            }
        }
    }
}
