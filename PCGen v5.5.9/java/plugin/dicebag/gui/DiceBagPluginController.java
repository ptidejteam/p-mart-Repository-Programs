/*
 *  gmgen.plugin.dicebag.gui - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 RossLodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  DiceBagPluginController.java
 *
 *  Created on Oct 17, 2003, 2:54:59 PM
 */
package plugin.dicebag.gui;

import gmgen.GMGenSystem;
import gmgen.io.SimpleFileFilter;
import java.awt.Component;
import java.io.File;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import pcgen.core.SettingsHandler;
import plugin.dicebag.DiceBagPlugin;

/**
 * <p>The controler class for DiceBag plugin.  Should handle all interface
 * actions.</p>
 *
 * @author Ross M. Lodge
 */
public class DiceBagPluginController {
	/** The view */
	private DiceBagPluginView theView = null;
	/** The model */
	private DiceBagPluginModel theModel = null;

	/**
	 * <p>Primary constructor for the DiceBagController object.</p>
	 *
	 * @param container
	 */
	public DiceBagPluginController() {
		theModel = new DiceBagPluginModel();
		theView = new DiceBagPluginView(theModel);
		openInitialBags();
	}

	/**
	 * <p>Returns the root component of the view.</p>
	 * @see DiceBagView#getMainComponent()
	 */
	public Component getComponent() {
		return theView.getMainComponent();
	}

	/**
	 * <p>Displays a file-open dialog box and processes the selected values.</p>
	 *
	 * @return <code>boolean</code> indicating success/failure of operation.
	 */
	public boolean fileOpen() {
		boolean returnValue = false;
		String sFile = SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", System.getProperty("user.dir"));
		JFileChooser open = new JFileChooser();
		if(sFile != null) {
			File defaultFile = new File(sFile);
			if(defaultFile.exists()) {
				open.setCurrentDirectory(defaultFile);
			}
		}
		String[] fileExt = new String[] {"dbg"};
		SimpleFileFilter ff = new SimpleFileFilter(fileExt, "GMGen Dice Bag");
		open.addChoosableFileFilter(ff);
		open.setFileFilter(ff);
		if (open.showOpenDialog(GMGenSystem.inst) == JFileChooser.APPROVE_OPTION) {
			SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", open.getSelectedFile().getParent());
			theModel.loadDiceBag(open.getSelectedFile());
			returnValue = true;
		}
		return returnValue;
	}

	/**
	 * <p>Requests the model to open a new dice bag.</p>
	 */
	public void fileNew() {
		theModel.addNewDicebag();
	}

	/**
	 * <p>Saves the currently active bag (if it exists), using
	 * <code>chooseSaveFile()</code>.</p>
	 */
	public void fileSave() {
		if (theModel.getActiveBag() != null) {
			final File saveFile = chooseSaveFile(theModel.getActiveBag());
			if (saveFile != null) {
				if (saveFile.exists()) {
					int choice = JOptionPane.showConfirmDialog(getComponent(), "File Exists, Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (choice == JOptionPane.NO_OPTION) {
						return;
					}
				}
				theModel.saveDiceBag(theModel.getActiveBag(), saveFile);
			}
		}
	}

	/**
	 * <p>This static method opens a file save chooser
	 * and returns the chosen file.</p>
	 *
	 * @param bag Bag to save
	 * @return Returns the file to save to
	 */
	public static File chooseSaveFile(DiceBagModel bag) {
		File returnValue = null;
		JFileChooser save = new JFileChooser();
		String[] fileExt = new String[] {"dbg"};
		SimpleFileFilter ff = new SimpleFileFilter(fileExt, "GMGen Dice Bag");
		save.addChoosableFileFilter(ff);
		save.setFileFilter(ff);
		if (bag.getFilePath() != null) {
			save.setSelectedFile(new File(bag.getFilePath()));
		}
		else {
			String sFile = SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", System.getProperty("user.dir"));
			save.setCurrentDirectory(new File(sFile));
		}
		if (save.showSaveDialog(GMGenSystem.inst) == JFileChooser.APPROVE_OPTION) {
			SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + ".LastFile", save.getSelectedFile().getParent());
			String fileName = save.getSelectedFile().getName();
			String dirName = save.getSelectedFile().getParent();
			String ext = "";
			if (fileName.indexOf(".dbg") < 0) {
				ext = ".dbg";
			}
			returnValue = new File(dirName + File.separator + fileName + ext);
		}
		return returnValue;
	}

	//opens bags that were open when the plugins last closed.
	protected void openInitialBags() {
		String lastFiles = SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + "closeFiles", "");
		StringTokenizer tok = new StringTokenizer(lastFiles,"|");
		boolean noLoads = true;
		for (int i = 0; tok.hasMoreTokens(); i++) {
			String fileName = tok.nextToken();
			File file = new File(fileName);
			if(file.exists() && fileName.endsWith(".dbg")) {
				try {
					theModel.loadDiceBag(file);
					noLoads = false;
				}
				catch(Exception e) {
				}
			}
		}

		if(noLoads) {
			fileNew();
		}
	}

	/**
	 * <p>Instructs the view to close all windows.</p>
	 */
	public void windowClosed() {
		theView.closeAll();
	}
}
