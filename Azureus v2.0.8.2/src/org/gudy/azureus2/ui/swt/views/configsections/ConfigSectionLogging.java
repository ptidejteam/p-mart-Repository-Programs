/*
 * File    : ConfigPanel*.java
 * Created : 11 mar. 2004
 * By      : TuxPaper
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

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;

import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.ConfigSectionSWT;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.Messages;

public class ConfigSectionLogging implements ConfigSectionSWT {
  private static final int logFileSizes[] =
     {
       1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50, 75, 100, 200, 300, 500
     };

  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_ROOT;
  }

	public String configSectionGetName() {
		return "logging";
	}

  public void configSectionSave() {
  }

  public void configSectionDelete() {
  }
  

  public Composite configSectionCreate(final Composite parent) {
  	int[] components = { 0, 1, 2, 4 };
    Image imgOpenFolder = ImageRepository.getImage("openFolderButton");
    GridData gridData;
    GridLayout layout;
    Label label;

    Composite gLogging = new Composite(parent, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gLogging.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gLogging.setLayout(layout);

    // row

    label = new Label(gLogging, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.logging.enable"); //$NON-NLS-1$
    BooleanParameter enableLogging = new BooleanParameter(gLogging, "Logging Enable"); //$NON-NLS-1$

    Composite cArea = new Composite(gLogging, SWT.NULL);
    layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.numColumns = 3;
    cArea.setLayout(layout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    cArea.setLayoutData(gridData);


    // row

    Label lStatsPath = new Label(cArea, SWT.NULL);
    Messages.setLanguageText(lStatsPath, "ConfigView.section.logging.logdir"); //$NON-NLS-1$

    gridData = new GridData();
    gridData.widthHint = 150;
    final StringParameter pathParameter = new StringParameter(cArea, "Logging Dir"); //$NON-NLS-1$ //$NON-NLS-2$
    pathParameter.setLayoutData(gridData);
    Button browse = new Button(cArea, SWT.PUSH);
    browse.setImage(imgOpenFolder);
    imgOpenFolder.setBackground(browse.getBackground());
    browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
    browse.addListener(SWT.Selection, new Listener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      public void handleEvent(Event event) {
      DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.APPLICATION_MODAL);
        dialog.setFilterPath(pathParameter.getValue());
        dialog.setText(MessageText.getString("ConfigView.section.logging.choosedefaultsavepath")); //$NON-NLS-1$
        String path = dialog.open();
        if (path != null) {
        pathParameter.setValue(path);
        }
      }
    });

    Label lMaxLog = new Label(cArea, SWT.NULL);

    Messages.setLanguageText(lMaxLog, "ConfigView.section.logging.maxsize");
    final String lmLabels[] = new String[logFileSizes.length];
    final int lmValues[] = new int[logFileSizes.length];
    for (int i = 0; i < logFileSizes.length; i++) {
      int  num = logFileSizes[i];
      lmLabels[i] = " " + num + " MB";
      lmValues[i] = num;
    }

    IntListParameter paramMaxSize = new IntListParameter(cArea, "Logging Max Size", lmLabels, lmValues);
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    paramMaxSize.setLayoutData(gridData);

    
    Composite cLogTypes = new Composite(gLogging, SWT.NULL);
    layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.numColumns = 3;
    layout.makeColumnsEqualWidth = true;
    cLogTypes.setLayout(layout);
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    cLogTypes.setLayoutData(gridData);

		for (int i = 0; i < components.length; i++) {
      Group gLogType = new Group(cLogTypes, SWT.NULL);
      layout = new GridLayout();
      layout.numColumns = 2;
      gLogType.setLayout(layout);
      gLogType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
      Messages.setLanguageText(gLogType, "ConfigView.section.logging.log" + components[i] + "component");
      
      for (int j = 0; j <= 3; j++) {
        label = new Label(gLogType, SWT.NULL);
        Messages.setLanguageText(label, "ConfigView.section.logging.log" + j + "type");
        new BooleanParameter(gLogType, "bLog" + components[i] + "-" + j);
      }
    }
    
    Control[] controls = { cArea, cLogTypes };
    enableLogging.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controls));
    return gLogging;
  }
}
