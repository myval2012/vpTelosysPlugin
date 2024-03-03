package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;

import java.util.ArrayList;
import java.util.List;

/**
 *  Common annotation declaration. Used if VP stereotype (and its tagged values) are in 1:1 relationship
 *  with the Telosys DSL annotation (and its parameters).
 */
public class AnnoCommon extends AnnoDeclarationMultiple {

    public AnnoCommon(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        Anno newAnno = new Anno(annoType);

        List<IParameter> parameters = new ArrayList<>(params.length);
        findTaggedValues(vPElement, vPStereotype, model, parameters);

        // check mandatory values, assume all values are mandatory
        for (IParameter parameter : parameters){
            if (parameter.getValue() == null){
                throw new Exception("One of the mandatory tagged values does not have a value.");
            }
        }

        newAnno.addParameters(parameters);

        return newAnno;
    }
}
