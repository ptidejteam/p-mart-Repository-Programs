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
package pcgen.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Convenience methods from various sources.
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 * @version $Revision: 1.1 $
 */
public final class Utility
{

	private static ResourceBundle properties = null;

	/**
	 * Flip the state of tooltips to that described by the global isToolTipTextShown.
	 */
	public static void handleToolTipShownStateChange()
	{
		/*
		 * replaced complex and mysterious code for toolTip handling
		 * with a simple call to ToolTipManager
		 *
		 * To binkley:
		 * Hey, if I broke something - I do apologize in advance.
		 * My IDE (Idea) said this method wasn't used very often
		 * so I thought I'd give the simpler code a try.
		 * Contact me at ravenlock@gmx.de if you want to discuss
		 * these changes
		 *
		 * author: Thomas Behr 03-10-02
		 */
		ToolTipManager.sharedInstance().setEnabled(SettingsHandler.isToolTipTextShown());
	}

	/**
	 * Put the description everywhere it belongs.
	 *
	 * @param component JComponent the component
	 * @param description String tool tip, etc.
	 */
	public static void setDescription(JComponent component, String description)
	{
		/*
		 * replaced complex and mysterious code for toolTip handling
		 * with a simple call to ToolTipManager
		 *
		 * To binkley:
		 * Hey, if I broke something - I do apologize in advance.
		 * My IDE (Idea) said this method wasn't used very often
		 * so I thought I'd give the simpler code a try.
		 * Contact me at ravenlock@gmx.de if you want to discuss
		 * these changes
		 *
		 * author: Thomas Behr 03-10-02
		 */
		component.setToolTipText(description);
	}

	/**
	 * Create a new menu with all the contaminant expectation
	 * fulfilled.
	 *
	 * @param prop String property to lookup in LanguageBundle
	 * @param iconName String icon name
	 * @param enable boolean menu enabled?
	 *
	 * @return JMenu the new menu
	 */
	public static JMenu createMenu(final String prop, String iconName, boolean enable)
	{
		final String label = PropertyFactory.getString("in_" + prop);
		final char mnemonic = PropertyFactory.getMnemonic("in_mn_" + prop);
		final String description = PropertyFactory.getString("in_" + prop + "Tip");
		return createMenu(label, mnemonic, description, iconName, enable);
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
	public static JMenu createMenu(String label, char mnemonic, String description, String iconName, boolean enable)
	{
		final JMenu menu = new JMenu(label);

		if (mnemonic != 0)
		{
			menu.setMnemonic(mnemonic);
		}
		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(menu, description);
		}
		IconUtilitities.maybeSetIcon(menu, iconName);
		menu.setEnabled(enable);

		return menu;
	}

	/**
	 * Create a new menu item with all the contaminant expectations
	 * fulfilled.
	 *
	 * @param prop String property to lookup in LanguageBundle
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param accelerator String keyboard shortcut key, <code>0</code> for none
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JMenuItem the new menu item
	 */
	public static JMenuItem createMenuItem(final String prop, final ActionListener listener, final String command, final String accelerator, final String iconName, final boolean enable)
	{
		String label = PropertyFactory.getString("in_" + prop);
		char mnemonic = PropertyFactory.getMnemonic("in_mn_" + prop);
		String description = PropertyFactory.getString("in_" + prop + "Tip");
		return createMenuItem(label, listener, command, mnemonic, accelerator, description, iconName, enable);
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
	public static JMenuItem createMenuItem(final String label, final ActionListener listener, final String command, final char mnemonic, final String accelerator, final String description, final String iconName, final boolean enable)
	{
		final JMenuItem item = new JMenuItem(label);

		if (listener != null)
		{
			item.addActionListener(listener);
		}
		if (command != null)
		{
			item.setActionCommand(command);
		}
		if (mnemonic != '\0')
		{
			item.setMnemonic(mnemonic);
		}
		if (accelerator != null)
		{
			item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		}
		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(item, description);
		}
		IconUtilitities.maybeSetIcon(item, iconName);
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
	public static final JButton createButton(ActionListener listener, String command, String description, String iconName, boolean enable)
	{
		final JButton button = new JButton();
		// Work around old JDK bug on Windows
		button.setMargin(new Insets(0, 0, 0, 0));

		if (listener != null)
		{
			button.addActionListener(listener);
		}
		if (command != null)
		{
			button.setActionCommand(command);
		}
		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(button, description);
		}
		IconUtilitities.maybeSetIcon(button, iconName);

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
	public static final JRadioButtonMenuItem createRadioButtonMenuItem(ButtonGroup group, String label, ActionListener listener, String command, char mnemonic, String accelerator, String description, String iconName, boolean enable)
	{
		final JRadioButtonMenuItem button = new JRadioButtonMenuItem(label);

		if (group != null)
		{
			group.add(button);
		}
		if (listener != null)
		{
			button.addActionListener(listener);
		}
		if (command != null)
		{
			button.setActionCommand(command);
		}
		if (mnemonic != 0)
		{
			button.setMnemonic(mnemonic);
		}
		if (accelerator != null)
		{
			button.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		}
		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(button, description);
		}
		IconUtilitities.maybeSetIcon(button, iconName);

		button.setEnabled(enable);

		return button;
	}

	/**
	 * Centers a <code>JFrame</code> to the screen.
	 *
	 * @param frame JFrame frame to center
	 * @param isPopup boolean is the frame a popup dialog?
	 */
	public static final void centerFrame(JFrame frame, boolean isPopup)
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

	/**
	 * Centers a <code>JDialog</code> to the screen.
	 *
	 * @param dialog JDialog dialog to center
	 */
	public static final void centerDialog(JDialog dialog)
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		final Dimension dialogSize = dialog.getSize();
		if (dialogSize.height > screenSize.height)
		{
			dialogSize.height = screenSize.height;
		}
		if (dialogSize.width > screenSize.width)
		{
			dialogSize.width = screenSize.width;
		}

		dialog.setLocation((screenSize.width - dialogSize.width) / 2, (screenSize.height - dialogSize.height) / 2);
	}

	/**
	 *
	 * @param aObj
	 * @param parentName
	 * @return
	 */
	public static final Container getParentNamed(Container aObj, String parentName)
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

	/**
	 *
	 * @param obj
	 * @param in_String
	 */
	public static final void setGuiTextInfo(Object obj, String in_String)
	{
		if (properties == null)
		{
			final Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
			try
			{
				properties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			}
			catch (MissingResourceException mrex)
			{
				Logging.errorPrint("Exception in Utility::setGuiTextInfo", mrex);
			}
		}

		String text;
		if (properties == null)
		{
			text = in_String;
		}
		else
		{
			try
			{
				text = properties.getString(in_String);
			}
			catch (Exception exc)
			{
				text = in_String;
			}
		}

		setTextAndMnemonic(obj, text);
	}

	/**
	 *
	 * @param obj
	 * @param text
	 */
	public static final void setTextAndMnemonic(Object obj, String text)
	{
		if (obj instanceof JLabel)
		{
			((JLabel) obj).setText(text);
			return;
		}

		int textLength = text.length();
		int idx = 0;
		char mnemonic = '\0';
		for (; ;)
		{
			idx = text.indexOf('&', idx);
			if (idx < 0)
			{
				break;
			}

			if (idx < (textLength - 1))
			{
				if (text.charAt(idx + 1) == '&')
				{
					idx += 1;
				}
				else
				{
					mnemonic = text.charAt(idx + 1);
				}
				text = text.substring(0, idx) + text.substring(idx + 1);
				textLength -= 1;
			}
		}
		if (obj instanceof JButton)
		{
			((JButton) obj).setText(text);
			if (mnemonic != '\0')
			{
				((JButton) obj).setMnemonic(mnemonic);
			}
		}
		else if (obj instanceof JMenuItem)
		{
			((JMenuItem) obj).setText(text);
			if (mnemonic != '\0')
			{
				((JMenuItem) obj).setMnemonic(mnemonic);
			}
		}
	}

	/**
	 * Sets the default browser.
	 * @param parent The component to show the dialog over.
	 */
	public static void selectDefaultBrowser(Component parent)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");
		if (System.getProperty("os.name").startsWith("Mac OS"))
		{
			// On MacOS X, do not traverse file bundles
			fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
		}

		if (SettingsHandler.getBrowserPath() == null)
		{
			//No action, as we have no idea what a good default would be...
		}
		else
		{
			fc.setCurrentDirectory(new File(SettingsHandler.getBrowserPath()));
		}
		final int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final File file = fc.getSelectedFile();
			SettingsHandler.setBrowserPath(file.getAbsolutePath());
		}
	}

	/**
	 * Strip everything between <> out.
	 * @param htmlIn The text to strip
	 * @return The stripped text.
	 */
	public static String stripHTML(String htmlIn)
	{
		String stringOut = htmlIn;
		while (stringOut.indexOf('<') >= 0)
		{
			stringOut = stringOut.substring(0, stringOut.indexOf('<')) + stringOut.substring(stringOut.indexOf('>') + 1, stringOut.length());
		}
		return stringOut;
	}

	/**
	 * Set up GridBag Constraints.
	 * @param gbc The gridbagconstraints to set up
	 * @param gx  cols from left (left-most col for multi-column cell)
	 * @param gy  rows from top (top-most row for multi-row cell)
	 * @param gw  cols wide
	 * @param gh  rows high
	 * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 */
	public static void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

}
