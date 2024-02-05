package cz.fit.vut.xvrana32.telosysplugin.elements;

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
