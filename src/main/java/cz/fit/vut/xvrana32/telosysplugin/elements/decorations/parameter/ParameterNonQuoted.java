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

/**
 * Parameter whose value should not be printed in quotes.
 */
public class ParameterNonQuoted extends ParameterBase {
    public ParameterNonQuoted(Object _value) {
        super(_value);
    }

    /**
     * @return Value of parameter in Telosys DSL format.
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
