package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;

public class ConstraintDeclaration {
    public final String name;
    public final Anno.AnnoType annoType;

    public ConstraintDeclaration(String _name, Anno.AnnoType _annoType) {
        name = _name;
        annoType = _annoType;
    }
}