/*
 * Utility.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Februrary 4th, 2002.
 */
package pcgen.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;

/**
 * Convenience methods from various sources.
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 */
public final class Utility
{
	/**
	 * Put the description everywhere it belongs.
	 *
	 * @param component JComponent the component
	 * @param description String tool tip, etc.
	 */
	public static void setDescription(JComponent component, String description)
	{
		if (description != null)
		{
			component.setToolTipText(description);
			component.getAccessibleContext().setAccessibleDescription(description);
		}
	}

	/**
	 * Add an icon to a menu item if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param button AbstractButton the item
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(AbstractButton button, String iconName)
	{
		if (iconName != null)
		{
			URL imageLocation = Utility.class.getResource("resource/" + iconName);
			if (imageLocation != null)
			{
				button.setIcon(new ImageIcon(imageLocation));
				return true;
			}
		}

		return false;
	}

	/**
	 * Add an icon to a frame if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param frame Frame the frame
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(Frame frame, String iconName)
	{
		if (iconName != null)
		{
			URL imageLocation = Utility.class.getResource("resource/" + iconName);
			if (imageLocation != null)
			{
				frame.setIconImage(new ImageIcon(imageLocation).getImage());
				return true;
			}
		}

		return false;
	}

	/**
	 * Create a new menu with all the contaminant expectation
	 * fulfilled.
	 *
	 * @param label String what to display?
	 * @param mnemonic int menu shortcut key, <code>0</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean menu enabled?
	 *
	 * @return JMenu the new menu
	 */
	public final static JMenu createMenu(String label, char mnemonic, String description, String iconName, boolean enable)
	{
		JMenu menu = new JMenu(label);

		if (mnemonic != 0)
			menu.setMnemonic(mnemonic);
		setDescription(menu, description);
		maybeSetIcon(menu, iconName);
		menu.setEnabled(enable);

		return menu;
	}

	/**
	 * Create a new menu item with all the contaminant expectations
	 * fulfilled.
	 *
	 * @param label String what to display?
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param mnemonic char menu shortcut key, <code>0</code> for none
	 * @param accelerator String keyboard shortcut key, <code>0</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JMenuItem the new menu item
	 */
	public final static JMenuItem createMenuItem(String label, ActionListener listener, String command, char mnemonic, String accelerator, String description, String iconName, boolean enable)
	{
		JMenuItem item = new JMenuItem(label);

		if (listener != null)
			item.addActionListener(listener);
		if (command != null)
			item.setActionCommand(command);
		if (mnemonic != 0)
			item.setMnemonic(mnemonic);
		if (accelerator != null)
			item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		setDescription(item, description);
		maybeSetIcon(item, iconName);
		item.setEnabled(enable);

		return item;
	}

	/**
	 * Create a new button menu item with all the contaminant
	 * expectations fulfilled.
	 *
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JButtonMenuItem the new  button menu item
	 */
	public final static JButton createButton(ActionListener listener, String command, String description, String iconName, boolean enable)
	{
		JButton button = new JButton();
		// Work around old JDK bug on Windows
		button.setMargin(new Insets(0, 0, 0, 0));

		if (listener != null)
			button.addActionListener(listener);
		if (command != null)
			button.setActionCommand(command);
		setDescription(button, description);
		maybeSetIcon(button, iconName);

		button.setEnabled(enable);

		return button;
	}

	/**
	 * Create a new radio button menu item with all the contaminant
	 * expectations fulfilled.  You need to fiddle with
	 * <code>ButtonGroup</code> yourself.
	 *
	 * @param group ButtonGroup what button group, <code>null</code> for none
	 * @param label String what to display?
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param mnemonic char menu shortcut key, <code>0</code> for none
	 * @param accelerator String keyboard shortcut key, <code>null</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JRadioButtonMenuItemMenuItem the new radio button menu item
	 */
	public final static JRadioButtonMenuItem createRadioButtonMenuItem(ButtonGroup group, String label, ActionListener listener, String command, char mnemonic, String accelerator, String description, String iconName, boolean enable)
	{
		JRadioButtonMenuItem button = new JRadioButtonMenuItem(label);

		if (group != null)
			group.add(button);
		if (listener != null)
			button.addActionListener(listener);
		if (command != null)
			button.setActionCommand(command);
		if (mnemonic != 0)
			button.setMnemonic(mnemonic);
		if (accelerator != null)
			button.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		setDescription(button, description);
		maybeSetIcon(button, iconName);

		button.setEnabled(enable);

		return button;
	}

	/**
	 * Centers a <code>JFrame</code> to the screen.
	 *
	 * @param frame JFrame frame to center
	 * @param isPopup boolean is the frame a popup dialog?
	 */
	public final static void centerFrame(JFrame frame, boolean isPopup)
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (isPopup)
		{
			frame.setSize(screenSize.width / 2, screenSize.height / 2);
		}

		final Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}

		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	public final static Container getParentNamed(Container aObj, String parentName)
	{
		while (aObj != null)
		{
			if (aObj.getClass().getName().equals(parentName))
			{
				break;
			}
			aObj = aObj.getParent();
		}
		return aObj;
	}
}
