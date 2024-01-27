package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.ApplicationManager;

import java.io.FileWriter;

public class Logger {
    public static String LOG_FILE_PATH =
            "C:\\Users\\mrmar\\Documents\\_Folder\\Dokumenty\\BP\\telosys xample projects vpp\\log\\telosysLogFile.txt";
    public static String TELOSYS_TEST_FOLDER =
            "C:\\Users\\mrmar\\Documents\\_Folder\\Dokumenty\\BP\\telosys xample projects vpp\\log";
    public static FileWriter logFile;

    /**
     * Prints given text to VP message with {@link Constants.PluginConstants#MESSAGE_TAG}.
     * @param msg message to print.
     */
    public static void log(String msg){
        ApplicationManager.instance().getViewManager().showMessage(msg, Constants.PluginConstants.MESSAGE_TAG);
    }
}
