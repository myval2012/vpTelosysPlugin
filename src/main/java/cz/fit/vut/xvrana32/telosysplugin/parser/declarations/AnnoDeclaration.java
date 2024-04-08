package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;

/**
 * Base for annotation declarations.
 * Used for creating annotations that are represented by stereotype in VP project with at max one tagged value.
 */
public abstract class AnnoDeclaration {

    public final String name;
    public final Anno.AnnoType annoType;
    public final ParamDeclaration[] params;

    /**
     * @param _name     Name of the annotation without the '@' as in the Visual Paradigm stereotypes.
     * @param _annoType Name of the annotation without the '@' as in the Telosys DSL.
     * @param _params   Declarations of each parameter if annotation has parameters, otherwise empty array.
     */
    public AnnoDeclaration(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params) {
        name = _name;
        annoType = _annoType;
        params = _params;
    }

    /**
     * @param vPElement    VP element that this stereotype is in.
     * @param vPStereotype Stereotype that this annotation is based on.
     * @param model        Compiled model.
     * @return Created annotation.
     * @throws Exception With explanation for user if syntactic or semantic checks fail.
     */
    public abstract Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception;

    /**
     * Checks that the tagged value follows the conditions described below:
     * <ul>
     *     <li>The tagged value is from the vpStereotype.</li>
     *     <li>Type of the tagged value is same as the type in paramDeclaration.</li>
     * </ul>
     *
     * @param vPStereotype     Object for condition.
     * @param vPTaggedValue    Tagged value to check.
     * @param paramDeclaration Object for condition.
     * @return true if conditions are met, false otherwise.
     */
    protected boolean checkTaggedValue(IStereotype vPStereotype,
                                       ITaggedValue vPTaggedValue,
                                       ParamDeclaration paramDeclaration) {
        return vPTaggedValue.getTagDefinitionStereotype() != null &&
                vPTaggedValue.getTagDefinitionStereotype().equals(vPStereotype) &&
                vPTaggedValue.getType() == paramDeclaration.paramType;
    }

    /**
     * Finds the tagged value associated with given stereotype.
     * @param vPTaggedValueContainer Container containing the searched tagged value.
     * @param vPStereotype Stereotype associated with tagged value.
     * @param paramDeclaration Additional information to check.
     * @return Tagged value if found, null otherwise
     */
    protected ITaggedValue getValidTaggedValue(ITaggedValueContainer vPTaggedValueContainer,
                                               IStereotype vPStereotype,
                                               ParamDeclaration paramDeclaration) {
        if (vPTaggedValueContainer == null) {
            return null;
        }

        for (ITaggedValue vPTaggedValue : vPTaggedValueContainer.toTaggedValueArray()) {
            if (vPTaggedValue.getName().equals(paramDeclaration.name) &&
                    checkTaggedValue(vPStereotype, vPTaggedValue, paramDeclaration)
            ) {
                return vPTaggedValue;
            }
        }
        return null;
    }
}