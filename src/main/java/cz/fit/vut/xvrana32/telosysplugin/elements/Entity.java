package cz.fit.vut.xvrana32.telosysplugin.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Inner Representation of Telosys DSL Entity.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
public class Entity extends DecoratedElement {
    private Model parentModel = null; // Back ref to owning model.
    final List<Attr> attrs = new ArrayList<>();
    final List<Link> links = new ArrayList<>();

    /**
     * Creates an entity with no annotations or tags.
     * @param _vpId ID of the {@link com.vp.plugin.model.IModelElement} representing entity in VP project.
     * @param _name Name of the entity.
     */
    public Entity(String _name, String _vpId) {
        super(_vpId, _name);
    }

    // TODO if attr with same name is already in the entity, merge the two decorations
    public void addAttr(Attr attr) {
        attrs.add(attr);
        attr.setParentEntity(this);
    }

    public void addLink(Link link) {
        links.add(link);
        link.setParentEntity(this);
    }

    public List<Attr> getAttrs() {
        return attrs;
    }

    public List<Link> getLinks(){
        return links;
    }

    public Attr getAttrByName(String name){
        for (Attr attr: attrs){
            if (attr.name.equals(name)){
                return attr;
            }
        }
        return null;
    }

    /**
     * Sets a reference to owning model.
     * @param parentModel Model that owns this entity.
     */
    public void setParentModel(Model parentModel) {
        this.parentModel = parentModel;
    }

    /**
     * @return Model which owns this entity.
     */
    public Model getParentModel() {
        return parentModel;
    }

    public Link getLinkByName(String name){
        for (Link link: links){
            if (link.name.equals(name)){
                return link;
            }
        }
        return null;
    }

    /**
     * @return Entity in Telosys DSL format. Ends with newline.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(decorationsToString("\n"));
        stringBuilder.append(String.format("%s\n{\n", name));
        for (Attr attr: attrs){
            stringBuilder.append("\t");
            stringBuilder.append(attr.toString());
            stringBuilder.append("\n");
        }

        for (Link link: links){
            stringBuilder.append("\t");
            stringBuilder.append(link.toString());
            stringBuilder.append("\n");
        }

        stringBuilder.append("}\n");

        return stringBuilder.toString();
    }
}
