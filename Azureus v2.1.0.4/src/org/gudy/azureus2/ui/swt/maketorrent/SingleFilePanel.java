/*
 * File : SingleFilePanel.java Created : 30 sept. 2003 02:50:19 By : Olivier
 * 
 * Azureus - a Java Bittorrent client
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details ( see the LICENSE file ).
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.gudy.azureus2.ui.swt.maketorrent;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;

/**
 * @author Olivier
 *  
 */
public class SingleFilePanel extends AbstractWizardPanel {
  private Text file;

  public SingleFilePanel(NewTorrentWizard wizard, AbstractWizardPanel previous) {
    super(wizard, previous);
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.ui.swt.maketorrent.IWizardPanel#show()
	 */
  public void show() {
    wizard.setTitle(MessageText.getString("wizard.singlefile"));
    wizard.setCurrentInfo(MessageText.getString("wizard.choosefile"));
    Composite panel = wizard.getPanel();
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    panel.setLayout(layout);
    Label label = new Label(panel, SWT.NULL);
    Messages.setLanguageText(label, "wizard.file");
    file = new Text(panel, SWT.BORDER);

    file.addModifyListener(new ModifyListener() {
      /*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
      public void modifyText(ModifyEvent arg0) {
        String fName = file.getText();
        ((NewTorrentWizard) wizard).singlePath = fName;
        String error = "";
        if (!fName.equals("")) {
          File f = new File(file.getText());
          if (!f.exists() || f.isDirectory()) {
            error = MessageText.getString("wizard.invalidfile");
          }
        }
        wizard.setErrorMessage(error);
        wizard.setNextEnabled(!((NewTorrentWizard) wizard).singlePath.equals("") && error.equals(""));
      }
    });
    file.setText(((NewTorrentWizard) wizard).singlePath);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    file.setLayoutData(gridData);
    Button browse = new Button(panel, SWT.PUSH);
    browse.addListener(SWT.Selection, new Listener() {
      /*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
      public void handleEvent(Event arg0) {
        FileDialog fd = new FileDialog(wizard.getWizardWindow());
        if (wizard.getErrorMessage().equals("") && !((NewTorrentWizard) wizard).singlePath.equals("")) {
          fd.setFileName(((NewTorrentWizard) wizard).singlePath);
        }
        String f = fd.open();
        if (f != null)
          file.setText(f);

      }
    });
    Messages.setLanguageText(browse, "wizard.browse");

    label = new Label(panel, SWT.NULL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    label.setLayoutData(gridData);
    label.setText("\n");

    label = new Label(panel, SWT.NULL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    label.setLayoutData(gridData);
    label.setForeground(Colors.blue);
    Messages.setLanguageText(label, "wizard.hint.file");
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.ui.swt.maketorrent.IWizardPanel#getNextPanel()
	 */
  public IWizardPanel getNextPanel() {
    // TODO Auto-generated method stub
    return new SavePathPanel(((NewTorrentWizard) wizard), this);
  }

  public void setFilename(String filename) {
    file.setText(filename);
  }
 }
