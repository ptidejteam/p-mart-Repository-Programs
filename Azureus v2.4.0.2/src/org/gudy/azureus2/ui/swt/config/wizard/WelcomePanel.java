/*
 * File    : WelcomePanel.java
 * Created : 12 oct. 2003 16:05:59
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
 
package org.gudy.azureus2.ui.swt.config.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.mainwindow.Cursors;
import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;

/**
 * @author Olivier
 * 
 */
public class WelcomePanel extends AbstractWizardPanel {


  public WelcomePanel(ConfigureWizard wizard,IWizardPanel previous) {
    super(wizard,previous);
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.wizard.IWizardPanel#show()
   */
  public void show() {
    wizard.setTitle(MessageText.getString("configureWizard.welcome.title"));
    
    String initsMode = "";
    final String[] text = {""};
    int userMode = COConfigurationManager.getIntParameter("User Mode");
    final String[] messTexts = {"ConfigView.section.mode.beginner.wiki.definitions",
    		"ConfigView.section.mode.intermediate.wiki.host",
    		"ConfigView.section.mode.advanced.wiki.main",
    		"ConfigView.section.mode.intermediate.wiki.publish"
    };
    final String[] links = {"http://azureus.aelitis.com/wiki/index.php/This_funny_word",
    		"http://azureus.aelitis.com/wiki/index.php/HostingFiles",
    		"http://azureus.aelitis.com/wiki/index.php/Main_Page",
    		"http://azureus.aelitis.com/wiki/index.php/PublishingFiles"
    };
    
    Composite rootPanel = wizard.getPanel();
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    rootPanel.setLayout(layout);
  
    Composite panel = new Composite(rootPanel, SWT.NULL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    panel.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 1;
    panel.setLayout(layout);     

    Label label0 = new Label(panel,SWT.WRAP);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    label0.setLayoutData(gridData);
    Messages.setLanguageText(label0,"configureWizard.welcome.message");
    
    label0 = new Label(panel, SWT.NULL);
    
    Label label1 = new Label(panel,SWT.WRAP);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    label1.setLayoutData(gridData);
    Messages.setLanguageText(label1,"configureWizard.welcome.usermodes");
    
    //// USER MODE GROUP ////
    gridData = new GridData();
    final Group gRadio = new Group(panel, SWT.WRAP);
    Messages.setLanguageText(gRadio, "ConfigView.section.mode.title");
    gRadio.setLayoutData(gridData);
    gRadio.setLayout(new RowLayout(SWT.HORIZONTAL));

    Button button0 = new Button (gRadio, SWT.RADIO);
    Messages.setLanguageText(button0, "ConfigView.section.mode.beginner");
    button0.setData("iMode", "0");
    button0.setData("sMode", "beginner.text");
    
    Button button1 = new Button (gRadio, SWT.RADIO);
    Messages.setLanguageText(button1, "ConfigView.section.mode.intermediate");
    button1.setData("iMode", "1");
    button1.setData("sMode", "intermediate.text");
    
    Button button2 = new Button (gRadio, SWT.RADIO);
    Messages.setLanguageText(button2, "ConfigView.section.mode.advanced");
    button2.setData("iMode", "2");
    button2.setData("sMode", "advanced.text");
    
    if ( userMode == 0) {
    	initsMode = "beginner.text";
    	button0.setSelection(true);
    } else if ( userMode == 1) {
    	initsMode = "intermediate.text";
    	button1.setSelection(true);
    } else {
    	initsMode = "advanced.text";
    	button2.setSelection(true);
    }
    
    final Label labl = new Label(panel, SWT.WRAP);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.widthHint = 380;
    gridData.heightHint = 50;
    labl.setLayoutData(gridData);
	text[0] = MessageText.getString("ConfigView.section.mode." + initsMode);
	labl.setText(text[0]);
	labl.addListener (SWT.Selection, new Listener () {
		public void handleEvent(Event event) {
			Program.launch(event.text);
		}
	});
	
    Group gWiki = new Group(panel, SWT.WRAP);
    gridData = new GridData();
    gridData.widthHint = 350;
    gWiki.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 1;
    gWiki.setLayout(layout);
    
    gWiki.setText(MessageText.getString("Utils.link.visit"));

	    final Label linkLabel = new Label(gWiki, SWT.NULL);
	    linkLabel.setText( MessageText.getString( messTexts[userMode] ) );
	    linkLabel.setData( links[userMode] );
	    linkLabel.setCursor(Cursors.handCursor);
	    linkLabel.setForeground(Colors.blue);
	    gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalIndent = 10;
	    linkLabel.setLayoutData( gridData );
	    linkLabel.addMouseListener(new MouseAdapter() {
	      public void mouseDoubleClick(MouseEvent arg0) {
	        Program.launch((String) ((Label) arg0.widget).getData());
	      }
	      public void mouseUp(MouseEvent arg0) {
	        Program.launch((String) ((Label) arg0.widget).getData());
	      }
	    });
	    
	    final Label linkLabel1 = new Label(gWiki, SWT.NULL);
	    linkLabel1.setText( (userMode == 1)?MessageText.getString(messTexts[3]):"");
	    linkLabel1.setData( links[3] );
	    linkLabel1.setCursor(Cursors.handCursor);
	    linkLabel1.setForeground(Colors.blue);
	    gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalIndent = 10;
	    linkLabel1.setLayoutData( gridData );
	    linkLabel1.addMouseListener(new MouseAdapter() {
	      public void mouseDoubleClick(MouseEvent arg0) {
	        Program.launch((String) ((Label) arg0.widget).getData());
	      }
	      public void mouseUp(MouseEvent arg0) {
	        Program.launch((String) ((Label) arg0.widget).getData());
	      }
	    });

    
    Listener radioGroup = new Listener () {
    	public void handleEvent (Event event) {
    		
    		Control [] children = gRadio.getChildren ();
    		
    		for (int j=0; j<children.length; j++) {
    			 Control child = children [j];
    			 if (child instanceof Button) {
    				 Button button = (Button) child;
    				 if ((button.getStyle () & SWT.RADIO) != 0) button.setSelection (false);
    			 }
    		}
    		
		    Button button = (Button) event.widget;
		    button.setSelection (true);
		    int mode = Integer.parseInt((String)button.getData("iMode"));
		    text[0] = MessageText.getString("ConfigView.section.mode." + (String)button.getData("sMode"));
		    labl.setText(text[0]);
		    
		    linkLabel.setText( MessageText.getString(messTexts[mode]) );
		    linkLabel.setData( links[mode] );
		    if(mode == 1){
			    linkLabel1.setText( MessageText.getString(messTexts[3]) );
			    linkLabel1.setData( links[3] );
		    } else{
			    linkLabel1.setText( "" );
			    linkLabel1.setData( "" );
		    }
		    COConfigurationManager.setParameter("User Mode", Integer.parseInt((String)button.getData("iMode")));
		    }
    };
    
    button0.addListener (SWT.Selection, radioGroup);
    button1.addListener (SWT.Selection, radioGroup);
    button2.addListener (SWT.Selection, radioGroup);
    
  }
  
  
  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel#isNextEnabled()
   */
  public boolean isNextEnabled() {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel#getNextPanel()
   */
  public IWizardPanel getNextPanel() {
    return new TransferPanel(((ConfigureWizard)wizard),this);
  }

}
