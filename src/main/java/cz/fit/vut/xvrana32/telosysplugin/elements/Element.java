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
 * Base for Telosys elements that have representation in VP project.
 */
public abstract class Element {
    protected final String vpId;
    protected final String name;

    /**
     * @param _vpId ID of {@link com.vp.plugin.model.IModelElement} representing this element in VP project.
     * @param _name Name of the element.
     */
    public Element(String _vpId, String _name) {
        vpId = _vpId;
        name = _name;
    }

    /**
     * @return Id of {@link com.vp.plugin.model.IModelElement} representing this element in VP project.
     */
    public String getVpId() {
        return vpId;
    }

    public String getName(){
        return name;
    }
}
