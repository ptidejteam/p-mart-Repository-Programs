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
 *  DiceBagPluginView.java
 *
 *  Created on Oct 17, 2003, 2:54:51 PM
 */
package plugin.dicebag.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import pcgen.core.SettingsHandler;
import plugin.dicebag.DiceBagPlugin;

/**
 *
 * <p>The view class for the DiceBag plugin.  Should manage and initialize
 * all GUI components.  Should delegate all user actions to the controller class.</p>
 * @author Ross M. Lodge
 */
public class DiceBagPluginView implements Observer {
	/**
	 * <p>Listener for events on the internal frame children of this view.</p>
	 *
	 * @author Ross M. Lodge
	 */
	private class ChildListener extends InternalFrameAdapter {
		/* (non-Javadoc)
		 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
		 */
		public void internalFrameActivated(InternalFrameEvent e) {
			DiceBagPluginView.this.internalFrameActivated(e);
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
		 */
		public void internalFrameClosed(InternalFrameEvent e) {
			DiceBagPluginView.this.internalFrameClosed(e);
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
		 */
		public void internalFrameClosing(InternalFrameEvent e) {
			DiceBagPluginView.this.internalFrameClosing(e);
		}

	}

	/** The model */
	private DiceBagPluginModel m_model;
	/** The desktop pane */
	private JDesktopPane theDesktop = null;
	/** Listener for internal frame events */
	private InternalFrameAdapter listener = new ChildListener();
	/** Coords for a new bag */
	private int newX = 0;
	private int newY = 0;

	/**
	 * <p>Default (and only) constructor.  Initializes the components.</p>
	 *
	 * @param o The observable object.
	 */
	public DiceBagPluginView(DiceBagPluginModel o) {
		super();
		o.addObserver(this);
		m_model = o;
		initComponents();
	}

	/**
	 * <p>Handles closing events -- calls the model <code>closeDiceBag()</code>
	 * code.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		if (e.getInternalFrame() != null && e.getInternalFrame() instanceof DiceBagView) {
			m_model.closeDiceBag(((DiceBagView)e.getInternalFrame()).getBag());
		}
	}

	/**
	 * <p>Handles the frame closing event; alows the user to choose
	 * whether or not to save or cancel, and vetoes the close if cancel.</p>
	 *
	 * @param e The event which fired this handler.
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		if (e.getInternalFrame() != null && e.getInternalFrame() instanceof DiceBagView) {
			final int answer = askSaveBag(((DiceBagView)e.getInternalFrame()).getBag(), JOptionPane.YES_NO_CANCEL_OPTION);
			if (answer == JOptionPane.CANCEL_OPTION) {
				e.getInternalFrame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
			}
			else if (answer == JOptionPane.NO_OPTION && answer == JOptionPane.YES_OPTION) {
				e.getInternalFrame().setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			}
		}
	}

	/**
	 * <p>Displays an option dialog with the specified <code>option</code>
	 * value and either saves the dice bag or not based on the response.
	 * If the cancel option is chosen or the user aborts the save dialog,
	 * <code>JOptionPane.CANCEL_OPTION</code> is returned instead
	 * of yes or no.  If the bag has not been changed since creation or
	 * loading, <code>JOptionPane.NO_OPTION</code> is returned.</p>
	 *
	 * @param bag The bag that needs saving.
	 * @param option One of the JOptionPane constants (like <code>YES_NO_OPTION</code>
	 * for display in the option pane.
	 * @return The selection option
	 */
	private int askSaveBag(DiceBagModel bag, int option) {
		int returnValue = JOptionPane.CANCEL_OPTION;
		if (bag.isChanged()) {
			returnValue =
				JOptionPane.showConfirmDialog(
					getMainComponent(),
					"Do you want to save your changes to dicebag " + bag.getName() + "?",
					"Save?",
					option);
			if (returnValue == JOptionPane.YES_OPTION) {
				if (bag.getFilePath() != null && bag.getFilePath().length() > 0) {
					m_model.saveDiceBag(bag);
				}
				else {
					final File saveFile = DiceBagPluginController.chooseSaveFile(bag);
					if (saveFile != null) {
						m_model.saveDiceBag(bag,saveFile);
					}
					else {
						//Use cancel here because the user chose to abort the save dialog
						returnValue = JOptionPane.CANCEL_OPTION;
					}
				}
			}
		}
		else {
			returnValue = JOptionPane.NO_OPTION;
		}
		return returnValue;
	}

	/**
	 * <p>Handler for frame activation -- manages the currently active bag
	 * information.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		if (e.getInternalFrame() != null && e.getInternalFrame() instanceof DiceBagView) {
			m_model.setActiveBag(((DiceBagView)e.getInternalFrame()).getBag());
		}
	}

	/**
	 * <p>Initializes all the components of the view.</p>
	 */
	private void initComponents() {
		theDesktop = new JDesktopPane();
		theDesktop.setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * <p>Returns the root component, the one that will
	 * be placed in the main tab pane.</p>
	 *
	 * @return The main or root component for this view.
	 */
	public Component getMainComponent() {
		return theDesktop;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 *
	 * Forwards update messages on to the appropriate handlers.
	 */
	public void update(Observable o, Object arg) {
		if (o != null && o instanceof DiceBagPluginModel && arg != null && arg instanceof DiceBagMessage) {
			DiceBagMessage msg = (DiceBagMessage)arg;
			switch (msg.getType()) {
				case DiceBagMessage.ALL_DICE_BAGS_REMOVED:
					allDiceBagsRemoved();
					break;
				case DiceBagMessage.DICE_BAG_ADDED:
					diceBagAdded(msg.getDiceBag());
					break;
				case DiceBagMessage.DICE_BAG_REMOVED:
					diceBagRemoved(msg.getDiceBag());
					break;
				case DiceBagMessage.DICE_BAG_SAVED:
					diceBagSaved(msg.getDiceBag());
					break;
				case DiceBagMessage.MODEL_INITIALIZED:
					modelInitialized();
					break;
				default:
					break;
			}
		}
	}

	/**
	 * <p>Handles the all dice bags removed message.</p>
	 */
	private void allDiceBagsRemoved() {
		Component[] frames = theDesktop.getComponents();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof DiceBagView) {
				((DiceBagView)frames[i]).hide();
			}
		}
	}

	/**
	 * <p>Handles the dice bag added message; instantiates a new
	 * internal frame.</p>
	 *
	 * @param model
	 */
	private void diceBagAdded(DiceBagModel model) {
		DiceBagView view = new DiceBagView(model);
		view.addInternalFrameListener(listener);
		theDesktop.add(view);
		view.setLocation(newX, newY);
		newX += 20;
		newY += 20;
		if(!theDesktop.getBounds().contains(newX + 40, newY + 40)) {
			newX = 0;
			newY = 0;
		}
		view.setVisible(true);
	}

	/**
	 * <p>Handles the dice bag removed message; removes
	 * the frame if its open.</p>
	 *
	 * @param model
	 */
	private void diceBagRemoved(DiceBagModel model) {
		Component[] frames = theDesktop.getComponents();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof DiceBagView) {
				if (((DiceBagView)frames[i]).getBag() == model) {
					((DiceBagView)frames[i]).hide();
				}
			}
		}
	}

	/**
	 * <p>Does nothing.</p>
	 *
	 * @param model
	 */
	private void diceBagSaved(DiceBagModel model) {
		// Do nothing . . .
	}

	/**
	 * <p>Does nothing.</p>
	 */
	private void modelInitialized() {
		// Do nothing . . .
	}

	/**
	 * <p>Handles the close all message; requests save for all bags.</p>
	 */
	public void closeAll() {
		Component[] frames = theDesktop.getComponents();
		StringBuffer files = new StringBuffer();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof DiceBagView) {
				DiceBagModel bag = ((DiceBagView)frames[i]).getBag();
				askSaveBag(bag, JOptionPane.YES_NO_OPTION);
				if(!bag.isChanged() && !bag.isBagEmpty()) {
					files.append(bag.getFilePath() + "|");
				}
			}
		}
		SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + "closeFiles", files.toString());
	}
}
