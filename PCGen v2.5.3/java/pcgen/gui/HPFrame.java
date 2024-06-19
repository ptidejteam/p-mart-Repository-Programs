/*
 * HPFrame.java
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

import java.awt.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import pcgen.core.Globals;

/**
 * Popup frame with export options
 */
//class HPFrame extends JDialog	//JFrame

class HPFrame extends JFrame
{
	private static String in_adjustHP;
	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle hpFrameProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			hpFrameProperties = ResourceBundle.getBundle("pcgen/gui/properities/LanguageBundle", currentLocale);
			in_adjustHP = hpFrameProperties.getString("in_adjustHP");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			hpFrameProperties = null;
		}
	}

	MainHP mainHP = null;

	public HPFrame()
	{
		super(in_adjustHP);
		//JFrame aFrame;// = new Frame(in_adjustHP);
		//super(aFrame = new JFrame(in_adjustHP), true);
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);
		//aFrame.setIconImage(img);
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		mainHP = new MainHP();
		Container contentPane = getContentPane();
		contentPane.add(mainHP);
		setVisible(true);
	}

	public void setPSize()
	{
		if (mainHP != null)
		{
			mainHP.setPSize();
		}
	}

}//end HPFrame
