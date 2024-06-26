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

import java.util.ArrayList;
import java.util.List;

/**
 * Inner representation of Telosys DSL annotation.
 * <br> <br>
 * Used mostly as a data class for storing and accessing information during the project compilation.
 * Each annotation contains {@link AnnoType annotation type} to determine what annotation it is
 * and a list of {@link IParameter parameters}.
 */
public class Anno {

    /**
     * Enum of all annotations.
     * <br> <br>
     * Annotations of other types can not be created.
     */
    public enum AnnoType {
        ABSTRACT,
        AGGREGATE_ROOT,
        AUTO_INCREMENTED,
        CASCADE,
        CONTEXT,
        DB_CATALOG,
        DB_COMMENT,
        DB_DEFAULT_VALUE,
        DB_NAME,
        DB_SCHEMA,
        DB_SIZE,
        DB_TABLE,
        DB_TABLESPACE,
        DB_TYPE,
        DB_VIEW,
        DEFAULT_VALUE,
        DOMAIN,
        EMBEDDED,
        EXTENDS,
        FETCH_TYPE_EAGER,
        FETCH_TYPE_LAZY,
        F_K,
        FUTURE,
        GENERATED_VALUE,
        ID,
        INITIAL_VALUE,
        IN_MEMORY_REPOSITORY,
        INPUT_TYPE,
        INSERTABLE,
        JOIN_ENTITY,
        LABEL,
        LINK_BY_ATTR,
        LINK_BY_F_K,
        LINK_BY_JOIN_ENTITY,
        LONG_TEXT,
        MANY_TO_MANY,
        MAPPED_BY,
        MAX,
        MAX_LEN,
        MIN,
        MIN_LEN,
        NOT_BLANK,
        NOT_EMPTY,
        NOT_NULL,
        OBJECT_TYPE,
        ONE_TO_ONE,
        OPTIONAL,
        ORPHAN_REMOVAL,
        PACKAGE,
        PAST,
        PATTERN,
        PRIMITIVE_TYPE,
        READ_ONLY,
        SIZE,
        SIZE_MAX,
        SIZE_MIN,
        TRANSIENT,
        UNIQUE,
        UNSIGNED_TYPE,
        UPDATABLE;

        /**
         * @return Name of annotation in Telosys DSL format without '@'.
         */
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(name().toLowerCase());
            stringBuilder.replace(0, 1, Character.toString(Character.toUpperCase(stringBuilder.charAt(0))));
            boolean toUpper = false;
            for (int i = 1; i < stringBuilder.length(); i++) {
                if (toUpper) {
                    stringBuilder.replace(i, i + 1,
                            Character.toString(Character.toUpperCase(stringBuilder.charAt(i))));
                    toUpper = false;
                }
                if (Character.toString(stringBuilder.charAt(i)).equals("_")) {
                    toUpper = true;
                }
            }
            return stringBuilder.toString().replace("_", "");
        }
    }

    private final AnnoType annotationType;
    private final List<IParameter> parameters = new ArrayList<>();

    /**
     * Creates an annotation of given {@link AnnoType} with no parameters.
     *
     * @param _annotationType Annotation that should be created.
     */
    public Anno(AnnoType _annotationType) {
        annotationType = _annotationType;
    }

    public AnnoType getAnnoType() {
        return annotationType;
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public void addParameter(IParameter parameter) {
        parameters.add(parameter);
    }

    /**
     * Adds all {@link IParameter parameters} into the list.
     *
     * @param ps Parameters to add.
     */
    public void addParameters(List<IParameter> ps) {
        parameters.addAll(ps);
    }

    /**
     * @return Annotation in Telosys DSL format.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@");
        stringBuilder.append(annotationType.toString());

        if (parameters.size() == 0) {
            return stringBuilder.toString();
        }

        stringBuilder.append("(");
        boolean oneParamPrinted = false; // print comma after the first printed parameter
        for (IParameter parameter : parameters) {
            if (parameter.getValue() != null) { // parameters with null as value should be ignored
                if (oneParamPrinted) {
                    stringBuilder.append(", ");
                }
                oneParamPrinted = true;
                stringBuilder.append(parameter);
            }

        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
