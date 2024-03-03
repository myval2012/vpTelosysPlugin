package cz.fit.vut.xvrana32.telosysplugin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
