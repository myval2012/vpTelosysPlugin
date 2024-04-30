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
package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

import java.io.File;

/**
 * Class containing the current project configuration.
 */
public class Config {
    public final static String CONFIG_CLASS_NAME = "config";
    private static String separator;
    public final static String SEPARATOR_TAG_NAME = "Package Separator";

    private static File telosysProjectFolder;
    public final static String TELOSYS_PROJECT_FOLDER_TAG_NAME = "Telosys project directory";

    /**
     * Loads configuration variables from tagged values of configuration class.
     * @param vPConfigClass configuration class from metamodel.
     * @throws Exception Expected tagged value is missing.
     */
    public static void loadConfig(IClass vPConfigClass) throws Exception {
        ITaggedValueContainer vPTaggedValues = vPConfigClass.getTaggedValues();

        separator = loadValue(vPTaggedValues, "Package Separator");
//        telosysProjectFolder = new File(loadValue(vPTaggedValues, "Telosys project directory"));
        telosysProjectFolder = new File(loadValue(vPTaggedValues, "Telosys project directory"));
//        Logger.logD(telosysProjectFolder.getAbsolutePath());
        if (!telosysProjectFolder.isAbsolute()){
            // change the path so that it is relative to the current Visual Paradigm project folder
            File newFolder = vPConfigClass.getProject().getProjectFile();
            if (newFolder == null){
                throw new Exception(String.format(
                        "The specified path is relative but the project is not saved. Save the project " +
                        "or use absolute path for %s.'%s'", CONFIG_CLASS_NAME, TELOSYS_PROJECT_FOLDER_TAG_NAME));
            }
            telosysProjectFolder = new File(newFolder.getParentFile().getAbsolutePath(),telosysProjectFolder.getPath());
//            Logger.logD(telosysProjectFolder.getAbsolutePath());
        }


        if (!(telosysProjectFolder.exists() && telosysProjectFolder.isDirectory())){
            throw new Exception("Specified folder in config.'Telosys project directory' is not a directory.");
        }
    }

    /**
     * Find the configuration variable value in given container.
     * @param vPTaggedValues Container with searched tagged value.
     * @param valueName Name of the searched tagged value.
     * @return Value of the searched tagged value.
     * @throws Exception Searched tagged value is not found.
     */
    private static String loadValue(ITaggedValueContainer vPTaggedValues, String valueName) throws Exception {
        ITaggedValue vPTaggedValue = vPTaggedValues.getTaggedValueByName(valueName);
        if (vPTaggedValue == null) {
            throw new Exception(String.format("config.'%s' wasn't found.", valueName));
        }

        String value = vPTaggedValue.getValueAsString();
        if (value == null){
            throw new Exception(String.format("config.'%s' can't be empty.", valueName));
        }
        return value;
    }

    public static String getSeparator() {
        return separator;
    }
    public static File getTelosysProjectFolder() {
        return telosysProjectFolder;
    }
}
