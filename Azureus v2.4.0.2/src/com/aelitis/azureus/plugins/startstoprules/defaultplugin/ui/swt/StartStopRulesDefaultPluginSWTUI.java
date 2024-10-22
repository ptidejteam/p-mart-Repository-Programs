/*
 * Created on 11-Sep-2005
 * Created by Paul Gardner
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
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
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.gudy.azureus2.core3.util.TimerEvent;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ui.menus.MenuItem;
import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
import org.gudy.azureus2.plugins.ui.tables.TableManager;
import org.gudy.azureus2.plugins.ui.tables.TableRow;

import com.aelitis.azureus.plugins.startstoprules.defaultplugin.DefaultRankCalculator;
import com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin;

public class StartStopRulesDefaultPluginSWTUI {
	public StartStopRulesDefaultPluginSWTUI(PluginInterface plugin_interface) {
		plugin_interface.addConfigSection(new ConfigSectionQueue());
		plugin_interface.addConfigSection(new ConfigSectionSeeding());
		plugin_interface.addConfigSection(new ConfigSectionSeedingAutoStarting());
		plugin_interface.addConfigSection(new ConfigSectionSeedingFirstPriority());
		plugin_interface.addConfigSection(new ConfigSectionSeedingIgnore());
	}

	public static void openDebugWindow(final DefaultRankCalculator dlData) {
		final Shell shell = new Shell(Display.getCurrent(), SWT.ON_TOP
				| SWT.SHELL_TRIM | SWT.TOOL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		GridData gd;
		shell.setLayout(layout);

		shell.setText("Debug for " + dlData.getDownloadObject().getName());

		final Text txtFP = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		txtFP.setLayoutData(gd);

		final Button btnAutoRefresh = new Button(shell, SWT.CHECK);
		btnAutoRefresh.setText("Auto-Refresh");
		btnAutoRefresh.setLayoutData(new GridData());

		final Button btnRefresh = new Button(shell, SWT.NONE);
		btnRefresh.setLayoutData(new GridData());
		btnRefresh.setText("Refresh");

		final Label lbl = new Label(shell, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		lbl.setLayoutData(gd);

		final TimerTask task = new TimerTask() {
			String lastText = "";

			public String formatString() {
				return "FP:\n" + dlData.sExplainFP + "\n" + "SR:" + dlData.sExplainSR
						+ "\n" + "TRACE:\n" + dlData.sTrace;
			}

			public void setText(final String s) {
				lastText = s;

				txtFP.setText(s);
			}

			public void run() {
				if (shell.isDisposed())
					return;

				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						String s = formatString();
						if (s.compareTo(lastText) != 0) {
							if (lastText.length() == 0 || btnAutoRefresh.getSelection()
									|| btnRefresh.getData("Pressing") != null)
								setText(s);
							else
								lbl.setText("Information is outdated.  Press refresh.");
						} else {
							lbl.setText("");
						}
					}
				});
			}
		};
		btnAutoRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (btnAutoRefresh.getSelection())
					lbl.setText("");
				task.run();
			}
		});

		btnRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				btnRefresh.setData("Pressing", "1");
				task.run();
				btnRefresh.setData("Pressing", null);
			}
		});

		shell.setSize(550, 350);
		shell.open();

		Timer timer = new Timer(true);
		timer.schedule(task, 0, 2000);
	}
}
