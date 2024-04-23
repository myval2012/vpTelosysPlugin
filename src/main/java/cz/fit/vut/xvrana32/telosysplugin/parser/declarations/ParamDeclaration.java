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
package cz.fit.vut.xvrana32.telosysplugin.parser.declarations;

import com.vp.plugin.model.ITaggedValueDefinition;

/**
 * Declaration of Parameter for {@link AnnoDeclaration}.
 */
public class ParamDeclaration {

    public final String name;
    public final int paramType;

    /**
     * Determines if the value should be surrounded in quotes when printed to Telosys DSL format.
     */
    public final boolean textQuoted;
    public final boolean isAbsolute;

//    /**
//     * {@link ParamDeclaration#paramType} is set to true.
//     * @param _name Parameter name that matches the auto-created parameters in VP.
//     * @param _paramType Data type of parameter.
//     */
//    public ParamDeclaration(String _name, int _paramType){
//        name = _name;
//        paramType = _paramType;
//        textQuoted = true;
//    }

    /**
     * Create a ParamDeclaration with {@link ParamDeclaration#paramType} of value ITaggedValueDefinition.TYPE_TEXT.
     *
     * @param _name       Parameter name that matches the auto-created parameters in VP.
     * @param _paramType  Data type of parameter.
     * @param _textQuoted Flag, if parameter is of text-types then
     *                    this flag determines if the value should be surrounded by quotes when printing in Telosys
     *                    DSL format.
     * @param _isAbsolute Flag, if parameter is of model-type then
     *                    this flag determines if the model name should be printed as absolute when printing in
     *                    Telosys DSL format.
     */
    public ParamDeclaration(String _name, int _paramType, boolean _textQuoted, boolean _isAbsolute) {
        name = _name;
        paramType = _paramType;
        textQuoted = _textQuoted;
        isAbsolute = _isAbsolute;
    }
}
