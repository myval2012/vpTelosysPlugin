package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

/**
 *  Common annotation declaration. Used if VP stereotype (and its tagged values) are in 1:1 relationship
 *  with the Telosys DSL annotation (and its parameters).
 */
public class AnnoCommon extends AnnoDeclaration {

    public AnnoCommon(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();

        Anno newAnno = new Anno(annoType);

        for (ParamDeclaration paramDeclaration : params) {
            ITaggedValue vPTaggedValue = vPTaggedValueContainer.getTaggedValueByName(paramDeclaration.name);
            if (vPTaggedValue == null
                    || !vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype)
                    || vPTaggedValue.getType() != paramDeclaration.paramType
            ) {
//                Logger.log("The proper tagged value for the stereotype was not found.");
                return null;
            }

            newAnno.addParameter(ParameterFactory.CreateParameter(vPTaggedValue, model, paramDeclaration.textQuoted));
//            Logger.log(String.format("Parameter %s added to the Annotation with value: %s",
//                    vPTaggedValue.getName(),
//                    vPTaggedValue.getValueAsText()));
        }
        return newAnno;
    }
}
