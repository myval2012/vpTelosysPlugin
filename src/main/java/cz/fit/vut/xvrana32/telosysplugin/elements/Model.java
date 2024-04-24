/**
 *
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cz.fit.vut.xvrana32.telosysplugin.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Inner Representation of Telosys DSL Model.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
    public class Model extends Element {
    private final List<Entity> entities = new ArrayList<>();
    private final List<AssociationEntity> associationEntities = new ArrayList<>();
    private final List<Entity> supportEntities = new ArrayList<>();

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

    public void addSupportEntity(Entity entity) {
        supportEntities.add(entity);
        entity.setParentModel(this);
    }

    public void addAssociationEntity(AssociationEntity entity) {
        associationEntities.add(entity);
        entity.setParentModel(this);
    }

    public Iterator<Entity> getEntitiesIterator() {
        return entities.iterator();
    }

    public int getEntitiesCount() {return entities.size();}

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

    public Iterator<AssociationEntity> getAssociationEntitiesIterator() {
        return associationEntities.iterator();
    }
}
