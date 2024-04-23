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