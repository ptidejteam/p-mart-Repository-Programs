/*
 * EQFrame.java
 * Copyright 2001 (C) Greg Bingleman
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;

/**
 * Popup frame with export options
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version    $Revision: 1.1 $
 */
class EQFrame extends JFrame
{
	private static String in_itemCustomizer;
	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle eqFrameProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			eqFrameProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_itemCustomizer = eqFrameProperties.getString("in_itemCustomizer");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			eqFrameProperties = null;
		}
	}

	EqBuilder mainEq = null;

	//
	// Overridden so we can handle exit on System Close
	// by calling <code>handleQuit</code>.
	//
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit(true);
		}
	}

	/**
	 * Closes the program by calling <code>handleQuit</code>
	 */
	void exitItem_actionPerformed(boolean bCancelled)
	{
		handleQuit(bCancelled);
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 */
	void handleQuit(boolean bCancelled)
	{
		if (!bCancelled)
		{
			SettingsHandler.setCustomizerLeftUpperCorner(getLocationOnScreen());
			SettingsHandler.setCustomizerDimension(getSize());
			SettingsHandler.writeOptionsProperties();
		}
		this.dispose();
	}

	public EQFrame()
	{
		super(in_itemCustomizer);
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainEq = new EqBuilder();

		Container contentPane = getContentPane();
		contentPane.add(mainEq);

		pack();

		final Dimension customizerDim = SettingsHandler.getCustomizerDimension();
		final Point customizerLoc = SettingsHandler.getCustomizerLeftUpperCorner();
		int x = -11;
		int y = -11;
		if (customizerLoc != null)
		{
			x = (int)customizerLoc.getX();
			y = (int)customizerLoc.getY();
		}

		if ((x < -10) || (y < -10) || (customizerDim == null) || (customizerDim.height == 0) || (customizerDim.width == 0))
		{
			Utility.centerFrame(this, true);
		}
		else
		{
			setLocation(customizerLoc);
			setSize(customizerDim);
		}
	}


	public boolean setEquipment(Equipment aEq)
	{
		if (mainEq != null)
		{
			return mainEq.setEquipment(aEq);
		}
		return false;
	}

	public void toFront()
	{
		super.toFront();
		if (mainEq != null)
		{
			mainEq.toFront();
		}
	}

}//end EQFrame
