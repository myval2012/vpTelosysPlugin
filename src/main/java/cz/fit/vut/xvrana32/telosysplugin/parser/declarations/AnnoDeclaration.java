package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import cz.fit.vut.xvrana32.telosysplugin.elements.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;

/**
 * Base for annotation declaration.
 * Used for creating annotations that are represented by stereotype in VP project.
 */
public abstract class AnnoDeclaration {

    public final String name;
    public final Anno.AnnoType annoType;
    public final ParamDeclaration[] params;

    /**
     * @param _name Name of the annotation without the '@'.
     * @param _params Declarations of each parameter if annotation has parameters, otherwise empty array.
     */
    public AnnoDeclaration(String _name, Anno.AnnoType _annoType, ParamDeclaration[] _params){
        name = _name;
        annoType = _annoType;
        params = _params;
    }

    /**
     * @param vPElement VP element that this stereotype is in.
     * @param vPStereotype Stereotype that this annotation is based on.
     * @param model Compiled model.
     * @throws Exception With explanation for user if syntactic or semantic checks fail.
     * @return Created annotation.
     */
    public abstract Anno createAnno(IModelElement vPElement, IStereotype vPStereotype, Model model) throws Exception;
}