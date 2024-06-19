/*
 *  NotesPlugin.java - plugin handler for the "Notes" plugin for GMGen
 *  Copyright (C) 2003 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */

package plugin.notes;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import plugin.notes.gui.NotesView;

/**
 *  The <code>NotesPlugin</code> controls the various classes that are involved
 *  in the functionality of the Notes System. This <code>class
 * </code> is a plugin for the <code>GMGenSystem</code>, is called by the <code>PluginLoader</code>
 *  and will create a model and a view for this plugin.
 *
 *@author     Expires 2003
 *@created    August 27, 2003
 *@version    2.10
 */
public class NotesPlugin extends GMBPlugin {
	/**  The plugin menu item in the tools menu. */
	private JMenuItem notesToolsItem = new JMenuItem();

	/**  The user interface for the encounter generator. */
	private NotesView theView;

	/**  The English name of the plugin. */
	private String name = "Notes";

	/**  The version number of the plugin. */
	private String version = "01.00.99.01.00";

	/**  The Log Name for the Logging system */
	public static final String LOG_NAME = "Notes";


	/**  Constructor for the NotesPlugin object */
	public NotesPlugin() {
		theView = new NotesView(getDataDir(), this);
	}


	/**
	 *  Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	public void start() {
		GMBus.send(new TabAddMessage(this, name, getView()));
		initMenus();
	}


	/**
	 *  Accessor for name
	 *
	 *@return     The name value
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Accessor for version
	 *
	 *@return     The version value
	 */
	public String getVersion() {
		return version;
	}


	/**  Initializes the Menus on the menu bar */
	private void initMenus() {
		notesToolsItem.setMnemonic('o');
		notesToolsItem.setText("Notes");
		notesToolsItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					toolMenuItem(evt);
				}
			});
		GMBus.send(new ToolMenuItemAddMessage(this, notesToolsItem));
	}


	/**
	 *  Changes to the notes plugin as the active tab
	 *
	 *@param  evt  Action Event of a click on the tool menu item
	 */
	public void toolMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for (int i = 0; i < tp.getTabCount(); i++) {
			if (tp.getComponentAt(i) instanceof NotesView) {
				tp.setSelectedIndex(i);
			}
		}
	}


	/**
	 *  Gets the <code>JPanel</code> view for the notes plugin
	 *
	 *@return    the view.
	 */
	public JPanel getView() {
		return theView;
	}

	public boolean isRecognizedFileType(File launch) {
		if(launch.toString().endsWith(".pcg") || launch.toString().endsWith(".pcp")) {
			return true;
		}
		return false;
	}

	public void loadRecognizedFileType(File launch) {
		GMBus.send(new OpenPCGRequestMessage(this, launch, false));
	}

	/**
	 *  listens to messages from the GMGen system, and handles them as needed
	 *
	 *@param  message  GMBus Message
	 *@see             gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if (message instanceof StateChangedMessage) {
			handleStateChangedMessage((StateChangedMessage) message);
		} else if (message instanceof WindowClosedMessage) {
			handleWindowClosedMessage((WindowClosedMessage) message);
		} else if (message instanceof FileOpenMessage) {
			handleFileOpenMessage((FileOpenMessage) message);
		}
	}


	/**
	 *  Handles the FileOpenMessage
	 *
	 *@param  message  GMBus FileOpenMessage
	 */
	private void handleFileOpenMessage(FileOpenMessage message) {
		if (GMGenSystemView.getTabPane().getSelectedComponent() instanceof NotesView) {
			theView.handleOpen();
		}
	}


	/**
	 *  Handles the StateChangedMessage
	 *
	 *@param  message  GMBus StateChangedMessage
	 */
	private void handleStateChangedMessage(StateChangedMessage message) {
		StateChangedMessage smessage = message;
		if (GMGenSystemView.getTabPane().getSelectedComponent() instanceof NotesView) {
			notesToolsItem.setEnabled(false);
			JMenu editMenu = smessage.getEditMenu();
			theView.initEditMenu(editMenu);
			theView.refreshTree();
			GMGenSystem.inst.openFileItem.setEnabled(true);
		} else {
			notesToolsItem.setEnabled(true);
		}
	}


	/**
	 *  Handles the WindowClosedMessage
	 *
	 *@param  message  GMBus WindowClosedMessage
	 */
	private void handleWindowClosedMessage(WindowClosedMessage message) {
		theView.windowClosed();
	}
}
