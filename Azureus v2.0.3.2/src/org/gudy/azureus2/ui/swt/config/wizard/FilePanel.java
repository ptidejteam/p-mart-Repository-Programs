/*
 * File    : FilePanel.java
 * Created : 13 oct. 2003 01:31:52
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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.gudy.azureus2.core.MessageText;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;

/**
 * @author Olivier
 * 
 */
public class FilePanel extends AbstractWizardPanel {

  public FilePanel(ConfigureWizard wizard, IWizardPanel previous) {
    super(wizard, previous);
  }
  
  
  public void show() {
    
    wizard.setTitle(MessageText.getString("configureWizard.file.title"));
    //wizard.setCurrentInfo(MessageText.getString("configureWizard.nat.hint"));
    Composite rootPanel = wizard.getPanel();
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    rootPanel.setLayout(layout);

    Composite panel = new Composite(rootPanel, SWT.NULL);
    GridData gridData = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
    panel.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 3;
    panel.setLayout(layout);

    Label label = new Label(panel, SWT.WRAP);
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    gridData.widthHint = 380;
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "configureWizard.file.message1");
    
    label = new Label(panel,SWT.NULL);
    Messages.setLanguageText(label, "configureWizard.file.path");
    
    final Text textPath = new Text(panel,SWT.BORDER);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    textPath.setLayoutData(gridData);
    textPath.setText(((ConfigureWizard)wizard).torrentPath);
    
    Button browse = new Button(panel,SWT.PUSH);
    Messages.setLanguageText(browse, "configureWizard.file.browse");
    browse.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event arg0) {
        DirectoryDialog dd = new DirectoryDialog(wizard.getWizardWindow());
        dd.setFilterPath(textPath.getText());
        String path = dd.open();
        if(path != null) {
          textPath.setText(path);
        }     
      }
    });
    
    textPath.addListener(SWT.Modify, new Listener() {
      public void handleEvent(Event event) {
        String path = textPath.getText();
        ((ConfigureWizard)wizard).torrentPath = path;
        try {
          File f = new File(path);
          if(f.exists() && f.isDirectory()) {
            wizard.setErrorMessage("");
            wizard.setFinishEnabled(true);
          } else {
            wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
            wizard.setFinishEnabled(false);
          }            
        } catch(Exception e) {
          wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
          wizard.setFinishEnabled(false);
        }
      }
    });
    
    textPath.setText(((ConfigureWizard)wizard).torrentPath);
    
    label = new Label(panel, SWT.WRAP);
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    gridData.widthHint = 380;
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "configureWizard.file.message2");
    
    final Button fastResume = new Button(panel,SWT.CHECK);
    fastResume.setSelection(((ConfigureWizard)wizard).fastResume);
    fastResume.addListener(SWT.Selection,new Listener() {
      public void handleEvent(Event arg0) {
        ((ConfigureWizard)wizard).fastResume = fastResume.getSelection();
      }
    });
    label = new Label(panel,SWT.NULL);
    Messages.setLanguageText(label, "configureWizard.file.fastResume");

  }
  
  public IWizardPanel getFinishPanel() {
    return new FinishPanel(((ConfigureWizard)wizard),this);
  }

}
