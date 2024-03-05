package cz.fit.vut.xvrana32.telosysplugin.utils;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

import java.io.File;

public class Config {
    private static String separator;
    private static File telosysProjectFolder;

    public static void loadConfig(IClass vPConfigClass) throws Exception {
        ITaggedValueContainer vPTaggedValues = vPConfigClass.getTaggedValues();

        separator = loadValue(vPTaggedValues, "Package Separator");
        telosysProjectFolder = new File(loadValue(vPTaggedValues, "Telosys project directory"));

        if (!(telosysProjectFolder.exists() && telosysProjectFolder.isDirectory())){
            throw new Exception("Specified folder in config.'Telosys project directory' is not a directory.");
        }
    }

    private static String loadValue(ITaggedValueContainer vPTaggedValues, String valueName) throws Exception {
        ITaggedValue vPTaggedValue = vPTaggedValues.getTaggedValueByName(valueName);
        if (vPTaggedValue == null) {
            throw new Exception(String.format("config.'%s' was not found.", valueName));
        }
        return vPTaggedValue.getValueAsString();
    }
}
