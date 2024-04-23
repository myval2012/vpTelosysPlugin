package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.ApplicationManager;

import java.io.FileWriter;

public class Logger {
//    public static String LOG_FILE_PATH =
//            "C:\\Users\\mrmar\\Documents\\_Folder\\Dokumenty\\BP\\telosys xample projects vpp\\log\\telosysLogFile.txt";
//    public static String TELOSYS_TEST_FOLDER =
//            "C:\\Users\\mrmar\\Documents\\_Folder\\Dokumenty\\BP\\telosys xample projects vpp\\log";
//    public static FileWriter logFile;

    private static int warningCount;
    private static int errorCount;

    /**
     * Prints given text to VP message with {@link Constants.PluginConstants#MESSAGE_TAG}.
     *
     * @param msg Message to print.
     */
    private static void log(String msg) {
        ApplicationManager.instance().getViewManager().showMessage(msg, Constants.PluginConstants.MESSAGE_TAG);
    }

    /**
     * Logging function for debugging purposes.
     * @param msg Message to print.
     */
    public static void logD(String msg){
//        log(msg);
    }

    /**
     * Prints a user message of severity level WARNING.
     * Adds calling to stats
     * @param msg Message to print.
     */
    public static void logW(String msg) {
        warningCount++;
        log("WARNING: " + msg);
    }

    /**
     * Prints a user message of severity level ERROR.
     * Adds calling to stats.
     * @param msg Message to print.
     */
    public static void logE(String msg) {
        errorCount++;
        log("ERROR: " + msg);
    }

    /**
     * Prints a user message of severity level INFO.
     *
     * @param msg Message to print.
     */
    public static void logI(String msg) {
        log("INFO: " + msg);
    }

    public static void logStats() {
        log("**********************************");
        log(String.format("* Total errors: %d", errorCount));
        log(String.format("* Total warnings: %d", warningCount));
        if (errorCount == 0 && warningCount == 0){
            log("**********************************");
            log("* SUCCESS                        *");
        }
        log("**********************************");
    }

    public static void resetStats() {
        errorCount = 0;
        warningCount = 0;
    }
}
