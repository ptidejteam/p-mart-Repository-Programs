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

import java.io.File;
import java.applet.Applet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Control;

import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.ConfigSectionSWT;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.*;
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
    	
      BooleanParameter est = new BooleanParameter(cDisplay, "Enable System Tray", true, "ConfigView.section.interface.enabletray");

      BooleanParameter ctt = new BooleanParameter(cDisplay, "Close To Tray", true, "ConfigView.label.closetotray");
      BooleanParameter mtt = new BooleanParameter(cDisplay, "Minimize To Tray", false, "ConfigView.label.minimizetotray");
      
      est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( ctt.getControls()));
      est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( mtt.getControls()));

    }
    
    new BooleanParameter(cDisplay, "Send Version Info",true, "ConfigView.label.allowSendVersion");
    

    Composite cArea = new Composite(cDisplay, SWT.NULL);
    layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.numColumns = 4;
    cArea.setLayout(layout);
    cArea.setLayoutData(new GridData());
    
    //Option disabled on OS X, as impossible to make it work correctly
    if(! Constants.isOSX) {      
    	
    	BooleanParameter play_sound = new BooleanParameter(cArea, "Play Download Finished",false, "ConfigView.label.playdownloadfinished");
        
    	Image imgOpenFolder = ImageRepository.getImage("openFolderButton");
	    
	    gridData = new GridData();
	    
	    gridData.widthHint = 150;
	    
	    final StringParameter pathParameter = new StringParameter(cArea, "Play Download Finished File", "");
	    
	    if ( pathParameter.getValue().length() == 0 ){
	    	
	    	pathParameter.setValue("<default>");
	    }
	    
	    pathParameter.setLayoutData(gridData);
	    

	    Button browse = new Button(cArea, SWT.PUSH);
	    
	    browse.setImage(imgOpenFolder);
	    
	    imgOpenFolder.setBackground(browse.getBackground());
	    
	    browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
	    
	    browse.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	        FileDialog dialog = new FileDialog(parent.getShell(), SWT.APPLICATION_MODAL);
	        dialog.setFilterExtensions(new String[] { "*.wav" });
	        dialog.setFilterNames(new String[] { "*.wav" }); 
	      
	        dialog.setText(MessageText.getString("ConfigView.section.interface.wavlocation"));
	      
	        final String path = dialog.open();
	      
	        if (path != null){
	        	
	        	pathParameter.setValue(path);
	        	
	        	new AEThread("SoundTest")
				{
	        		public void
					runSupport()
	        		{
	        			try{
	        				Applet.newAudioClip( new File( path ).toURL()).play();
	        			
	        				Thread.sleep(2500);
	        				
	        			}catch( Throwable e ){
	        				
	        			}
	        		}
	        	}.start();
	        }
	      }
	    });
	    
	  Label sound_info = new Label(cArea, SWT.NULL);
	  Messages.setLanguageText(sound_info, "ConfigView.section.interface.wavlocation.info");

	  play_sound.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( pathParameter.getControls()));
	  play_sound.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( new Control[]{browse,sound_info }));

	  BooleanParameter	confirm = new BooleanParameter(cArea, "confirmationOnExit",false, "ConfigView.section.style.confirmationOnExit");
	  gridData = new GridData();
	  gridData.horizontalSpan	= 4;
	  confirm.setLayoutData( gridData );
    }
    
    BooleanParameter confirm_removal = new BooleanParameter(cArea, "confirm_torrent_removal", "ConfigView.section.interface.confirm_torrent_removal" );
    gridData = new GridData();
    gridData.horizontalSpan = 4;
    confirm_removal.setLayoutData( gridData );
    
    
    // password
    
    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.password");

    gridData = new GridData();
    gridData.widthHint = 150;
    PasswordParameter pw1 = new PasswordParameter(cArea, "Password");
    pw1.setLayoutData(gridData);
    Text t1 = (Text)pw1.getControl();
    
    label = new Label(cArea, SWT.NULL);
    label = new Label(cArea, SWT.NULL);
    
    //password confirm

    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordconfirm");
    gridData = new GridData();
    gridData.widthHint = 150;
    PasswordParameter pw2 = new PasswordParameter(cArea, "Password Confirm");
    pw2.setLayoutData(gridData);
    Text t2 = (Text)pw2.getControl();
    label = new Label(cArea, SWT.NULL);
    label = new Label(cArea, SWT.NULL);
   
    // password activated
    
    label = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordmatch");
    passwordMatch = new Label(cArea, SWT.NULL);
    gridData = new GridData();
    gridData.widthHint = 150;
    passwordMatch.setLayoutData(gridData);
    refreshPWLabel();
    label = new Label(cArea, SWT.NULL);
    label = new Label(cArea, SWT.NULL);

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

    // drag-drop
    
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
    
    label = new Label(cArea, SWT.NULL);
    label = new Label(cArea, SWT.NULL);

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

		    reset.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		      	
		      	try{
		      		platform.registerApplication();
		      		
		      	}catch( PlatformManagerException e ){
		      	
		      		LGLogger.logUnrepeatableAlert("Failed to register application", e );
		      	}
		      }
		    });
		    
		    new BooleanParameter(cArea, "config.interface.checkassoc",true, "ConfigView.section.interface.checkassoc");
		    
		    label = new Label(cArea, SWT.NULL);
		    label = new Label(cArea, SWT.NULL);
		
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
