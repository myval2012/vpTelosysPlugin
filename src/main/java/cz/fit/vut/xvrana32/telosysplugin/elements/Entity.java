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

import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Tag;

import java.util.ArrayList;
import java.util.Iterator;
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
     *
     * @param _vpId ID of the {@link com.vp.plugin.model.IModelElement} representing entity in VP project.
     * @param _name Name of the entity.
     */
    public Entity(String _name, String _vpId) {
        super(_vpId, _name);
    }

    /**
     * Adds attribute if the entity does not contain an attribute with the same name.
     * @param attr Attribute to add.
     * @return true if added, false otherwise.
     */
    public boolean addAttr(Attr attr) {
        Attr innerAttr = getAttrByName(attr.name);
        if (innerAttr != null){
            return false;
        }

        attr.setParentEntity(this);
        return attrs.add(attr);
    }

    /**
     * Merges decorations of attr and attribute in entity with the same name as attr.
     * If there is no attribute of same name, method doesn't do anything.
     * @param attr Attribute with annotations and tags.
     */
    public void mergeAttr(Attr attr) {
        Attr mergedAttr = getAttrByName(attr.name);
        if (mergedAttr == null) {
            return;
        }

        for (Anno anno : attr.annos) {
            mergedAttr.addAnno(anno);
        }

        for (Tag tag : attr.tags) {
            mergedAttr.addTag(tag);
        }
    }

    public void addLink(Link link) {
        links.add(link);
        link.setParentEntity(this);
    }

    public Iterator<Attr> getAttrsIterator() {
        return attrs.iterator();
    }

    public int getAttrsCount() {return attrs.size();}

    public Iterator<Link> getLinksIterator() {
        return links.iterator();
    }

    public Attr getAttrByName(String name) {
        for (Attr attr : attrs) {
            if (attr.name.equals(name)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Sets a reference to owning model.
     *
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

    /**
     * @return Entity in Telosys DSL format. Ends with newline.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(decorationsToString("\n"));
        stringBuilder.append(String.format("%s\n{\n", name));
        for (Attr attr : attrs) {
            stringBuilder.append("\t");
            stringBuilder.append(attr.toString());
            stringBuilder.append("\n");
        }

        for (Link link : links) {
            stringBuilder.append("\t");
            stringBuilder.append(link.toString());
            stringBuilder.append("\n");
        }

        stringBuilder.append("}\n");

        return stringBuilder.toString();
    }
}
