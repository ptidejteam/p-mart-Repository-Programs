/*
 * File    : ConfigPanel*.java
 * Created : 11 mar. 2004
 * By      : TuxPaper
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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
 *
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.ConfigSectionSWT;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.platform.*;

public class ConfigSectionInterface implements ConfigSectionSWT {
  Label passwordMatch;

  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_ROOT;
  }

	public String configSectionGetName() {
		return ConfigSection.SECTION_INTERFACE;
	}

  public void configSectionSave() {
  }

  public void configSectionDelete() {
  }
  

  public Composite configSectionCreate(final Composite parent) {
    GridData gridData;
    GridLayout layout;
    Label label;

    Composite cDisplay = new Composite(parent, SWT.NULL);

    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    cDisplay.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 1;
    cDisplay.setLayout(layout);

    new BooleanParameter(cDisplay, "Open Details", "ConfigView.label.opendetails");
    new BooleanParameter(cDisplay, "Open Bar", false, "ConfigView.label.openbar");

    if(!Constants.isOSX) {
      new BooleanParameter(cDisplay, "Close To Tray", true, "ConfigView.label.closetotray");
      new BooleanParameter(cDisplay, "Minimize To Tray", false, "ConfigView.label.minimizetotray");
    }
    
    new BooleanParameter(cDisplay, "Send Version Info",true, "ConfigView.label.allowSendVersion");
    new BooleanParameter(cDisplay, "confirmationOnExit",false, "ConfigView.section.style.confirmationOnExit");
    
    Composite cArea = new Composite(cDisplay, SWT.NULL);
    layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.numColumns = 2;
    cArea.setLayout(layout);
    cArea.setLayoutData(new GridData());
    
    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.password");

    gridData = new GridData();
    gridData.widthHint = 150;
    PasswordParameter pw1 = new PasswordParameter(cArea, "Password");
    pw1.setLayoutData(gridData);
    Text t1 = (Text)pw1.getControl();
    

    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordconfirm");
    gridData = new GridData();
    gridData.widthHint = 150;
    PasswordParameter pw2 = new PasswordParameter(cArea, "Password Confirm");
    pw2.setLayoutData(gridData);
    Text t2 = (Text)pw2.getControl();

    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordmatch");
    passwordMatch = new Label(cArea, SWT.NULL);
    gridData = new GridData();
    gridData.widthHint = 150;
    passwordMatch.setLayoutData(gridData);
    refreshPWLabel();


    t1.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        refreshPWLabel();
      }
    });
    t2.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        refreshPWLabel();
      }
    });

    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.style.dropdiraction");

    String[] drop_options = {
         "ConfigView.section.style.dropdiraction.opentorrents",
         "ConfigView.section.style.dropdiraction.sharefolder",
         "ConfigView.section.style.dropdiraction.sharefoldercontents",
         "ConfigView.section.style.dropdiraction.sharefoldercontentsrecursive",
     };

    String dropLabels[] = new String[drop_options.length];
    String dropValues[] = new String[drop_options.length];
    for (int i = 0; i < drop_options.length; i++) {

       dropLabels[i] = MessageText.getString( drop_options[i]);
       dropValues[i] = "" + i;
    }
    new StringListParameter(cArea, "config.style.dropdiraction", "", dropLabels, dropValues);
    
    
    	// reset associations
 
    try{
	    final PlatformManager	platform  = PlatformManagerFactory.getPlatformManager();
	    
	    if ( platform != null && platform.getPlatformType() == PlatformManager.PT_WINDOWS ){
	    	
		    Composite cResetAssoc = new Composite(cArea, SWT.NULL);
		    layout = new GridLayout();
		    layout.marginHeight = 0;
		    layout.marginWidth = 0;
		    layout.numColumns = 2;
		    cResetAssoc.setLayout(layout);
		    cResetAssoc.setLayoutData(new GridData());
		 
		    label = new Label(cResetAssoc, SWT.NULL);
		    Messages.setLanguageText(label, "ConfigView.section.interface.resetassoc");

		    Button reset = new Button(cResetAssoc, SWT.PUSH);
		    Messages.setLanguageText(reset, "ConfigView.section.interface.resetassocbutton"); //$NON-NLS-1$

		    //browse.setImage(imgOpenFolder);
		    //imgOpenFolder.setBackground(browse.getBackground());
		    //browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
		
		    reset.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		      	
		      	try{
		      		platform.registerApplication();
		      		
		      	}catch( PlatformManagerException e ){
		      	
		      		LGLogger.logAlert("Failed to register application", e );
		      	}
		      }
		    });
		    
		    new BooleanParameter(cArea, "config.interface.checkassoc",true, "ConfigView.section.interface.checkassoc");
	    }
    }catch( PlatformManagerException e ){
    	
    }
    
    return cDisplay;
  }
  
  private void refreshPWLabel() {

    if(passwordMatch == null || passwordMatch.isDisposed())
      return;
    byte[] password = COConfigurationManager.getByteParameter("Password", "".getBytes());
    COConfigurationManager.setParameter("Password enabled", false);
    if (password.length == 0) {
      passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchnone"));
    }
    else {
      byte[] confirm = COConfigurationManager.getByteParameter("Password Confirm", "".getBytes());
      if (confirm.length == 0) {
        passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
      }
      else {
        boolean same = true;
        for (int i = 0; i < password.length; i++) {
          if (password[i] != confirm[i])
            same = false;
        }
        if (same) {
          passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchyes"));
          COConfigurationManager.setParameter("Password enabled", true);
        }
        else {
          passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
        }
      }
    }    
  }

}
