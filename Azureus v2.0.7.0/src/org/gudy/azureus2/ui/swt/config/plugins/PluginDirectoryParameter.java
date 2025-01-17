/*
 * File    : PluginStringParameter.java
 * Created : 15 d�c. 2003}
 * By      : Olivier
 *
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.gudy.azureus2.ui.swt.config.plugins;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.gudy.azureus2.pluginsimpl.ui.config.DirectoryParameter;
import org.gudy.azureus2.ui.swt.Messages;

/**
 * @author Olivier
 *
 */
public class PluginDirectoryParameter implements PluginParameterImpl {
  
  Control[] controls;
  
  public PluginDirectoryParameter(final Group pluginGroup,DirectoryParameter parameter) {
    controls = new Control[3];
           
    controls[0] = new Label(pluginGroup,SWT.NULL);
    Messages.setLanguageText(controls[0],parameter.getLabel());
    
    final org.gudy.azureus2.ui.swt.config.StringParameter sp =
    	new org.gudy.azureus2.ui.swt.config.StringParameter(
    	    pluginGroup,
    	    parameter.getKey(),
					parameter.getDefaultValue());
    controls[1] = sp.getControl();
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    controls[1].setLayoutData(gridData);
    
    Button browse = new Button(pluginGroup,SWT.PUSH);
    Messages.setLanguageText(browse, "ConfigView.button.browse"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 100;
    browse.setLayoutData(gridData);
    
    browse.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        DirectoryDialog dialog = new DirectoryDialog(pluginGroup.getShell(), SWT.APPLICATION_MODAL);
        dialog.setFilterPath(sp.getValue());        
        String path = dialog.open();
        if (path != null) {
          sp.setValue(path);
        }
      }
    });
    controls[2] = browse;
  }
  
  public Control[] getControls(){
    return controls;
  }

}
