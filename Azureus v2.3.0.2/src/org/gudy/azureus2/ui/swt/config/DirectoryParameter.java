/*
 * Created on 08-Jun-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.ui.swt.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.ui.swt.ImageRepository;

/**
 * @author parg
 *
 */

public class 
DirectoryParameter
	extends Parameter
{
	Control[] controls;
	  
	public 
	DirectoryParameter(
		final Composite pluginGroup,
		String			name,
		String			defaultValue )
	{  
	  	controls = new Control[2];
	           	    
	    final org.gudy.azureus2.ui.swt.config.StringParameter sp =
	    	new org.gudy.azureus2.ui.swt.config.StringParameter(
	    	    pluginGroup,
	    	    name,
				defaultValue );
	    
	    controls[0] = sp.getControl();
	    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	    controls[0].setLayoutData(gridData);
	    
	    Button browse = new Button(pluginGroup, SWT.PUSH);
	    Image imgOpenFolder = ImageRepository.getImage("openFolderButton");
	    browse.setImage(imgOpenFolder);
	    imgOpenFolder.setBackground(browse.getBackground());
	    browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));

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
	    controls[1] = browse;
	  }
	  
	public void 
	setLayoutData(
		Object layoutData)
	{
	}
	
	public Control
	getControl()
	{
		return( controls[0]);
	}
	
	public Control[] 
	getControls()
	{
	    return controls;
	}
}
