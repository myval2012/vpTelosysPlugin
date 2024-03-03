package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.utils.ParameterFactory;

import java.util.ArrayList;
import java.util.List;

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
public class AnnoGeneratedValue extends AnnoDeclarationMultiple {

    public AnnoGeneratedValue(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        Anno newAnno = new Anno(annoType);

        // add the fist parameter
        IParameter firstParameter;
        if (name.endsWith("AUTO")) {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "AUTO");
        } else if (name.endsWith("IDENTITY")) {
            firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "IDENTITY");
        } else {
            if (name.endsWith("SEQUENCE")) {
                firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "SEQUENCE");
            } else {
                firstParameter = ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING, "TABLE");
            }
            newAnno.addParameter(firstParameter);

            List<IParameter> parameters = new ArrayList<>(params.length);
            findTaggedValues(vPElement, vPStereotype, model, parameters);

            // check mandatory values, for GeneratedValue if first parameter from list has value,
            // then all except last have to have value too.
            boolean mustHaveValues = parameters.get(0).getValue() != null;
            for (int i = 0; i < parameters.size() - 1; i++) {
                IParameter parameter = parameters.get(i);
                if ((parameter.getValue() == null && mustHaveValues) ||
                        (parameter.getValue() != null && !mustHaveValues)){
                    throw new Exception(String.format("Tagged value %s does not have a value.",
                            params[i].name));
                }
            }

//            for (IParameter parameter : parameters) {
//                if ((parameter.getValue() == null && mustHaveValues)
//                        || (parameter.getValue() != null && !mustHaveValues)) {
//                    throw new Exception(String.format("Tagged value %s does not have a value.",
//                            params[parameters.indexOf(parameter)]));
//                }
//            }

            newAnno.addParameters(parameters);
            return newAnno;
        }

        newAnno.addParameter(firstParameter);

//        List<IParameter> parameters = new ArrayList<>(params.length);
//        findTaggedValues(vPElement, vPStereotype, model, parameters);

        // check mandatory values, for generated value all values are mandatory
//        for (IParameter parameter : parameters) {
//            if (parameter.getValue() == null) {
//                throw new Exception("One of the mandatory tagged values does not have a value.");
//            }
//        }
//
//        newAnno.addParameters(parameters);

        return newAnno;
    }
}
