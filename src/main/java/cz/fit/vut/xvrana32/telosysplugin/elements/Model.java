package cz.fit.vut.xvrana32.telosysplugin.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Inner Representation of Telosys DSL Model.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
public class Model extends Element {
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> associationEntities = new ArrayList<>();

    private List<Entity> supportEntities = new ArrayList<>();

    /**
     * Creates an empty model.
     *
     * @param _vpId ID of the {@link com.vp.plugin.model.IModelElement} representing model in VP project.
     * @param _name Name of the model.
     */
    public Model(String _name, String _vpId) {
        super(_vpId, _name);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.setParentModel(this);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Replaces internal list of entities with given list.
     */
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
        for (Entity entity : entities) {
            entity.setParentModel(this);
        }
    }

    /**
     * Replaces internal list of association entities with given list.
     */
    public void setAssociationEntities(List<Entity> associationEntities) {
        this.associationEntities = associationEntities;
        for (Entity entity : this.associationEntities) {
            entity.setParentModel(this);
        }
    }

    public Entity getEntityByVpId(String vpId) throws Exception {
        return getEntityFromSetByVpId(entities, vpId);
    }

    public Entity getSupportEntityByVpId(String vpId) {
        for (Entity entity : supportEntities) {
            if (entity.getVpId().equals(vpId)) {
                return entity;
            }
        }
        return null;
    }

    public Entity getAssociationEntityByVpId(String vpId) throws Exception {
        return getEntityFromSetByVpId(associationEntities, vpId);
    }

    private Entity getEntityFromSetByVpId(List<Entity> entities, String vpId) throws Exception {
        for (Entity entity : entities) {
            if (entity.getVpId().equals(vpId)) {
                return entity;
            }
        }
        throw new Exception(String.format(
                "Entity does not exist in the current model %s",
                name));
    }

    /**
     * For development use only.
     *
     * @return All entities in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(String.format("MODEL %s: \n", name));
        for (Entity entity : entities) {
            str.append(entity.toString());
            str.append("\n");
        }
        str.append(String.format("MODEL %s END\n", name));
        return str.toString();
    }

    public List<Entity> getAssociationEntities() {
        return associationEntities;
    }

    public List<Entity> getSupportEntities() {
        return supportEntities;
    }

    /**
     * Replaces internal list of support entities with given list.
     */
    public void setSupportEntities(List<Entity> supportEntities) {
        this.supportEntities = supportEntities;
        for (Entity entity : this.supportEntities) {
            entity.setParentModel(this);
        }
    }
}
