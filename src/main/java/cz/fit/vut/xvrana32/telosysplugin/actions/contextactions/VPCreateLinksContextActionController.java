package cz.fit.vut.xvrana32.telosysplugin.actions.contextactions;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.factory.IModelElementFactory;
import cz.fit.vut.xvrana32.telosysplugin.elements.decorations.Anno;
import cz.fit.vut.xvrana32.telosysplugin.utils.Logger;

import java.awt.event.ActionEvent;

public class VPCreateLinksContextActionController implements com.vp.plugin.action.VPContextActionController {
    // make sure there is a constructor without any parameters
    public void performAction(
            com.vp.plugin.action.VPAction action,
            com.vp.plugin.action.VPContext context,
            ActionEvent e) {
        // called when the button is clicked
        IAssociation vPAssociation = (IAssociation) context.getModelElement();
        IAssociationEnd vPAssociationFromEnd = (IAssociationEnd) vPAssociation.getFromEnd();
        IAssociationEnd vPAssociationToEnd = (IAssociationEnd) vPAssociation.getToEnd();
        IAttribute vPAttrFrom = ContextActionUtils.getOrCreateRepresentativeAttribute(vPAssociationFromEnd);
        IAttribute vPAttrTo = ContextActionUtils.getOrCreateRepresentativeAttribute(vPAssociationToEnd);
    }

    public void update(
            com.vp.plugin.action.VPAction action,
            com.vp.plugin.action.VPContext context) {
        // when the popup menu is selected, this will be called,
        // developer can set the properties of the action before it is shown (e.g. enable/disable the menu item)
    }
}