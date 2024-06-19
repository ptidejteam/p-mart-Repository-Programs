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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.WeakHashMap;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

/**
 * Convenience methods from various sources.
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 */
public final class Utility
{
	private static WeakHashMap toolTipIndex = new WeakHashMap();
	private static ResourceBundle properties = null;

	/**
	 * Flip the state of tooltips to that described by the global isToolTipTextShown.
	 */
	public static void handleToolTipShownStateChange()
	{
		if (Globals.isToolTipTextShown())
		{
			// Put all the descriptions in
			for (Iterator i = toolTipIndex.keySet().iterator(); i.hasNext();)
			{
				JComponent jComponent = (JComponent)i.next();
				String description = (String)toolTipIndex.get(jComponent);
				if (jComponent != null && description != null) setDescriptionOnComponent(jComponent, description);
			}
		}
		else
		{
			// Remove all descriptions
			for (Iterator i = toolTipIndex.keySet().iterator(); i.hasNext();)
			{
				JComponent jComponent = (JComponent)i.next();
				if (jComponent != null)
				{
					removeDescriptionOnComponent(jComponent);
				}
			}
		}
	}

	/**
	 * Private implementation description deleter
	 *
	 * @param jComponent JComponent the component
	 */
	private static void removeDescriptionOnComponent(JComponent jComponent)
	{
		if (!Globals.isToolTipTextShown())
		{
			jComponent.setToolTipText(null);
			jComponent.getAccessibleContext().setAccessibleDescription(null);
		}
	}

	/**
	 * Private implementation description placer
	 *
	 * @param component JComponent the component
	 * @param description String tool tip, etc.
	 */
	private static void setDescriptionOnComponent(JComponent component, String description)
	{
		if (description != null)
		{
			if (Globals.isToolTipTextShown())
			{
				component.setToolTipText(description);
				component.getAccessibleContext().setAccessibleDescription(description);
			}
		}
	}

	/**
	 * Put the description everywhere it belongs.
	 *
	 * @param component JComponent the component
	 * @param description String tool tip, etc.
	 */
	public static void setDescription(JComponent component, String description)
	{
		toolTipIndex.put(component, description);
		setDescriptionOnComponent(component, description);
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
	public static final JMenu createMenu(String label, char mnemonic, String description, String iconName, boolean enable)
	{
		JMenu menu = new JMenu(label);

		if (mnemonic != 0) menu.setMnemonic(mnemonic);
		if (Globals.isToolTipTextShown())
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
	public static final JMenuItem createMenuItem(String label, ActionListener listener, String command, char mnemonic, String accelerator, String description, String iconName, boolean enable)
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
		if (Globals.isToolTipTextShown())
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
	public static final JButton createButton(ActionListener listener, String command, String description, String iconName, boolean enable)
	{
		JButton button = new JButton();
		// Work around old JDK bug on Windows
		button.setMargin(new Insets(0, 0, 0, 0));

		if (listener != null)
			button.addActionListener(listener);
		if (command != null)
			button.setActionCommand(command);
		if (Globals.isToolTipTextShown())
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
	public static final JRadioButtonMenuItem createRadioButtonMenuItem(ButtonGroup group, String label, ActionListener listener, String command, char mnemonic, String accelerator, String description, String iconName, boolean enable)
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
		if (Globals.isToolTipTextShown())
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

	public static final void setGuiTextInfo(Object obj, String in_String)
	{
		if (properties == null)
		{
			Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
			try
			{
				properties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			}
			catch (MissingResourceException mrex)
			{
				mrex.printStackTrace();
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

		if (obj instanceof JLabel)
		{
			((JLabel)obj).setText(text);
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
			((JButton)obj).setText(text);
			if (mnemonic != '\0')
			{
				((JButton)obj).setMnemonic(mnemonic);
			}
		}
	}

	public static void selectDefaultBrowser(java.awt.Component parent)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");
		if (System.getProperty("os.name").startsWith("Mac OS"))
		{
			// On MacOS X, do not traverse file bundles
			fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
		}

		if (Globals.getBrowserPath() == null)
		{
			//No action, as we have no idea what a good default would be...
		}
		else
		{
			fc.setCurrentDirectory(Globals.getBrowserPath());
		}
		final int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final java.io.File file = fc.getSelectedFile();
			Globals.setBrowserPath(file);
		}
	}


	public static boolean chooseSpokenLanguage(PlayerCharacter aPC)
	{
		return chooseSpokenLanguage(aPC, "Speak Language");
	}

	public static boolean chooseSpokenLanguage(PlayerCharacter aPC, String skillName)
	{
		if (aPC != null)
		{
			final Skill speakLanguage = aPC.getSkillNamed(skillName);
			if (speakLanguage == null)
			{
				JOptionPane.showMessageDialog(null, "You do not have enough ranks in Speak Language", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return false;
			}

			SortedSet autoLangs = aPC.getAutoLanguages();
			SortedSet langs = aPC.getLanguagesList();

			int numLanguages = speakLanguage.getTotalRank().intValue();
			ArrayList selected = new ArrayList();
			speakLanguage.addAssociatedTo(selected);
			ArrayList excludedLangs = new ArrayList();

			ArrayList available = new ArrayList();
			for (Iterator e = Globals.getLanguageList().iterator(); e.hasNext();)
			{
				available.add(e.next().toString());
			}

			//
			// Do not give choice of automatic languages
			//
			for (Iterator e = autoLangs.iterator(); e.hasNext();)
			{
				final String lang = (String)e.next();
				available.remove(lang);
				excludedLangs.add(lang);
			}
			//
			// Do not give choice of selected bonus languages
			//
			for (Iterator e = langs.iterator(); e.hasNext();)
			{
				final String lang = (String)e.next();
				if (!selected.contains(lang))
				{
					available.remove(lang);
					excludedLangs.add(lang);
				}
			}

			Globals.sortChooserLists(available, selected);

			Chooser lc = new Chooser();
			lc.setVisible(false);
			lc.setAvailableList(available);
			lc.setSelectedList(selected);
			lc.setPool(numLanguages - selected.size());
			lc.setPoolFlag(false);
			lc.show();

			langs.clear();
			langs.addAll(lc.getSelectedList());
			langs.addAll(excludedLangs);				// Add in all choice-excluded languages

			speakLanguage.clearAssociated();
			speakLanguage.addAllToAssociated(lc.getSelectedList());
			aPC.setDirty(true);
			return true;
		}
		return false;
	}

	public static String trimZeros(String aString)
	{
		try
		{
			return trimBigDecimal(new BigDecimal(aString)).toString();
		}
		catch (Exception exc)
		{
		}
		return "";
	}

	public static String trimZeros(BigDecimal n)
	{
		return trimBigDecimal(n).toString();
	}

	//
	// trimBigDecimal ( (BigDecimal) a) to cut off all trailing zeros
	//
	public static BigDecimal trimBigDecimal(BigDecimal n)
	{
		try
		{
			for (; ;)
			{
				n = n.setScale(n.scale() - 1);
			}
		}
		catch (ArithmeticException e)
		{
			// Not "real" error: No more trailing zeroes -> Just exit
			// setScale() tries to eliminate a non-zero digit -> Out of the loop
			// Remember exceptions are not recommended for exiting loops, but this seems to be the best way...
		}
		return n;
	}

	// Sets [n] to [dp] decimal places
	public static BigDecimal formatBigDecimal(BigDecimal n, int dp)
	{
		return n.setScale(dp, BigDecimal.ROUND_HALF_UP);	// Sets scale and rounds up if most significant (cut off) number >= 5
	}
}
