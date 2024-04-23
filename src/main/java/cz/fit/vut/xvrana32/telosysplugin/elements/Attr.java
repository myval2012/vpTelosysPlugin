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

/**
 * Inner representation of Telosys DSL attribute.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 */
public class Attr extends DecoratedElement {
    private Entity parentEntity = null;

    /**
     * Enum of all Telosys DSL data types.
     */
    public enum AttrType {
        BINARY,
        BOOLEAN,
        BYTE,
        DATE,
        DECIMAL,
        DOUBLE,
        FLOAT,
        INT,
        LONG,
        SHORT,
        STRING,
        TIME,
        TIMESTAMP;

        /**
         * @return Data type in Telosys DSL format.
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private final AttrType attrType;

    /**
     * Creates an attribute with no annotations or tags.
     * @param _vpId ID of the {@link com.vp.plugin.model.IModelElement} representing attribute in VP project.
     * @param _name Name of the attribute.
     * @param _attrType Data type of this attribute.
     */
    public Attr(String _vpId, String _name, AttrType _attrType) {
        super(_vpId, _name);
        attrType = _attrType;
    }

    /**
     * @return Entity which owns this attribute.
     */
    public Entity getParentEntity() {
        return parentEntity;
    }

    /**
     * Sets a reference to owning entity.
     * @param parentEntity Entity that owns this attribute.
     */
    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    public AttrType getAttrType() {
        return attrType;
    }

    /**
     * @return Attribute in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(name);
        stringBuilder.append(" : ");
        stringBuilder.append(attrType.toString());

        if (annos.size() > 0 || tags.size() > 0 ){
            stringBuilder.append(" { ");
            stringBuilder.append(decorationsToString(" "));
            stringBuilder.append("}");
        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }
}
