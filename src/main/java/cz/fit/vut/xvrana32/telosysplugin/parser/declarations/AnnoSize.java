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
 * Special annotation declaration for parsing @Size
 * <br> <br>
 * The parameter size is split into 2:
 * <ul>
 *     <li>precision</li>
 *     <li>scale</li>
 * </ul>
 */
public class AnnoSize extends AnnoDeclarationMultiple {

    public AnnoSize(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        Anno newAnno = new Anno(annoType);
        List<IParameter> splitParameters = new ArrayList<>(params.length);
        findTaggedValues(vPElement, vPStereotype, model,splitParameters);

        // TODO check mandatory values, for size "precision" is mandatory
        if (splitParameters.get(0).getValue() == null){
            throw new Exception("The tagged value precision is mandatory for stereotype @Size.");
        }

        newAnno.addParameter(
                ParameterFactory.CreateParameter(ParameterFactory.ValueType.STRING,
                splitParameters.get(0).getValue().toString() +
                        (splitParameters.get(1).getValue() == null ? "" :
                                "," + splitParameters.get(1).getValue().toString())));

        return newAnno;
    }
}
