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
package cz.fit.vut.xvrana32.telosysplugin.elements.decorations.parameter;

import cz.fit.vut.xvrana32.telosysplugin.elements.Attr;
import cz.fit.vut.xvrana32.telosysplugin.elements.DecoratedElement;

/**
 * Parameter pointing to a {@link DecoratedElement}.
 */
public class ParameterLink extends ParameterBase {
    private final boolean isAbsolute; // is absolute can only be set true for attribute value

    public ParameterLink(Object _value, boolean _isAbsolute) {
        super(_value);
        isAbsolute = _isAbsolute;
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        String str = "";
        if (isAbsolute && value instanceof Attr) {
            str = ((Attr) value).getParentEntity().getName() + ".";
        }
        return str + ((DecoratedElement) value).getName();
    }
}
