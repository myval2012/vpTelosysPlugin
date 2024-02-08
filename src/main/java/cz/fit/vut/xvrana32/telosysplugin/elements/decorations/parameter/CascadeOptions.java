package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

/**
 * Enum of all possible @Cascade arguments.
 */
public enum CascadeOptions {
    ALL,
    MERGE,
    PERSIST,
    REFRESH,
    REMOVE;

    @Override
    public String toString(){
        return name();
    }
}
