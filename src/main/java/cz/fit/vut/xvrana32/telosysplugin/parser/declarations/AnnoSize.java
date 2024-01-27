package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.elements.Parameter;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

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
public class AnnoSize extends AnnoDeclaration {

    public AnnoSize(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        super(_name, _annoType, _params);
    }

    @Override
    public Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception {
        ITaggedValueContainer vPTaggedValueContainer = vPElement.getTaggedValues();

        Anno newAnno = new Anno(annoType);
        List<Parameter> splitParameters = new ArrayList<>(params.length);

        for (ParamDeclaration paramDeclaration : params) {
            ITaggedValue vPTaggedValue = vPTaggedValueContainer.getTaggedValueByName(paramDeclaration.name);
            if (vPTaggedValue == null
                    || !vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype)
                    || vPTaggedValue.getType() != paramDeclaration.paramType
            ) {
                Logger.log("The proper tagged value for the stereotype was not found.");
                return null;
            }

            splitParameters.add(new Parameter(vPTaggedValue, model, paramDeclaration.textQuoted));
            Logger.log(String.format("Parameter %s added to the Annotation with value: %s",
                    vPTaggedValue.getName(),
                    vPTaggedValue.getValueAsText()));
        }

        newAnno.addParameter(new Parameter(Parameter.ValueType.STRING, splitParameters.get(0).getValue().toString() +
                (splitParameters.get(1).getValue() == null ? "" : "," + splitParameters.get(1).getValue().toString())));

        return newAnno;
    }
}
