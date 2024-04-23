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

import java.util.Arrays;
import java.util.List;

/**
 * class containing all constants
 */
public class Constants {
    public static class PluginConstants{
        /**
         * Tag for logging messages.
         */
        public static final String MESSAGE_TAG = "Telosys plugin";
    }

    public static class GTTSuppModelConstants {
        /**
         * Name of the auto-created model in VP.
         */
        public static final String GTT_SUPP_MODEL_NAME = "Telosys DSL defs";

        public static final String GTT_CASCADE_OPTIONS_CLASS_NAME = "Cascade options";

        public static final List<String> GTT_CONSTRAINT_NAMES = Arrays.asList(
                "Future",
                "InMemoryRepository",
                "NotBlank",
                "NotEmpty",
                "NotNull",
                "Past",
                "ReadOnly"
        );
    }
}
