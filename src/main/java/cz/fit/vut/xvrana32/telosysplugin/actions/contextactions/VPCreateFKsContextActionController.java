package cz.fit.vut.xvrana32.telosysplugin.actions.contextactions;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;

import java.awt.event.ActionEvent;

public class VPCreateFKsContextActionController implements com.vp.plugin.action.VPContextActionController {
    // make sure there is a constructor without any parameters
    public void performAction(
            com.vp.plugin.action.VPAction action,
            com.vp.plugin.action.VPContext context,
            ActionEvent e) {
        IAssociation vPAssociation = (IAssociation) context.getModelElement();
        IAssociationUIModel vPAssociationShape = (IAssociationUIModel) context.getDiagramElement();
        IClassDiagramUIModel vPDiagram = (IClassDiagramUIModel) context.getDiagram();

        // find association class
        IClass vPAssociationClass = ContextActionUtils.getAssociationClass(vPAssociation);
        // if it doesn't exist create it
        if (vPAssociationClass == null){
            ContextActionUtils.createAssociationClassWithFKs(
                    vPAssociation,
                    vPDiagram,
                    vPAssociationShape);
        }
    }

    public void update(com.vp.plugin.action.VPAction action, com.vp.plugin.action.VPContext context) {
        // when the popup menu is selected, this will be called,
        // developer can set the properties of the action before it is shown (e.g. enable/disable the menu item)
    }
}