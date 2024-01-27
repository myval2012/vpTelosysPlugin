package cz.fit.vut.xvrana32.telosysplugin.parser;


import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import cz.fit.vut.xvrana32.telosysplugin.elements.Model;
import cz.fit.vut.xvrana32.telosysplugin.utils.Constants;

import java.util.*;

public class ProjectParser {

    List<Model> models = new ArrayList<>();

    public List<Model> parse(IProject project) {
        IModelElement[] vPModels = project.toModelElementArray("Model");

        for (IModelElement vPModel : vPModels) {
            if (vPModel.getName().equals(Constants.DSLConstants.DSL_HELPER_MODEL_NAME)) {
                continue;
            }

            ModelParser modelParser = new ModelParser();
            models.add(modelParser.parse(vPModel));
        }
        return models;
    }
}
