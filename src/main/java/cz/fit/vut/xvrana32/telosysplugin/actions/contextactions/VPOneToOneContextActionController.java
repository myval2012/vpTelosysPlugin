package cz.fit.vut.xvrana32.telosysplugin.actions.contextactions;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import java.awt.event.ActionEvent;

public class VPOneToOneContextActionController implements com.vp.plugin.action.VPContextActionController {
    // make sure there is a constructor without any parameters
    public void performAction(
            com.vp.plugin.action.VPAction action,
            com.vp.plugin.action.VPContext context,
            ActionEvent e) {
        // called when the button is clicked
        IAssociation vPAssociation = (IAssociation) context.getModelElement();
        ((IAssociationEnd) vPAssociation.getFromEnd()).setMultiplicity(IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE);
        ((IAssociationEnd) vPAssociation.getToEnd()).setMultiplicity(IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE);
    }

    public void update(
            com.vp.plugin.action.VPAction action,
            com.vp.plugin.action.VPContext context) {
        // when the popup menu is selected, this will be called,
        // developer can set the properties of the action before it is shown (e.g. enable/disable the menu item)
    }
}