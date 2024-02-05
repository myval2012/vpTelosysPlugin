package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

/**
 * Special annotation declaration for parsing @GeneratedValue
 * <br> <br>
 * GeneratedValue is represented by 4 stereotypes:
 * <ul>
 *     <li>GeneratedValueAUTO</li> - Create additional first parameter "AUTO"
 *     <li>GeneratedValueIDENTITY</li> - Create additional first parameter "IDENTITY"
 *     <li>GeneratedValueSEQUENCE</li> - Create additional first parameter "SEQUENCE"
 *     <li>GeneratedValueTABLE</li> - Create additional first parameter "TABLE"
 * </ul>
 */
public class AnnoGeneratedValue extends AnnoDeclaration {

    public AnnoGeneratedValue(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();

        Anno newAnno = new Anno(annoType);

        // add the fist parameter
        IParameter firstParameter;
        if (name.endsWith("AUTO")) {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "AUTO");
        } else if (name.endsWith("IDENTITY")) {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "IDENTITY");
        } else if (name.endsWith("SEQUENCE")) {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "SEQUENCE");
        } else {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "TABLE");
        }
        newAnno.addParameter(firstParameter);

        // proceed as in AnnoCommon
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
