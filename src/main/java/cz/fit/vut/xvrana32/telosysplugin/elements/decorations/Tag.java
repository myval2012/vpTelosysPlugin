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
package cz.fit.vut.xvrana32.telosysplugin.elements.decorations;

import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter.IParameter;

/**
 * Inner representation of Telosys DSL tag.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 * Each tag contains tag type (name of the tag) and can also contain {@link IParameter}.
 */
public class Tag {
    private final String name;
    private final IParameter parameter;

    /**
     * @param _name Name of the tag.
     * @param _parameter Parameter if tag has one, null otherwise.
     */
    public Tag(String _name, IParameter _parameter) {
        name = _name;
        parameter = _parameter;
    }

    public String getName() {
        return name;
    }

    public IParameter getParameter() {
        return parameter;
    }

    /**
     * @return Tag in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        stringBuilder.append(name);

        if (parameter == null) {
            return stringBuilder.toString();
        }
        stringBuilder.append("(");
        stringBuilder.append(parameter);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
