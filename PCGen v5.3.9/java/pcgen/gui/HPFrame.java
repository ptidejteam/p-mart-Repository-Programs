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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import pcgen.core.Globals;
import pcgen.gui.utils.IconUtilitities;
import pcgen.util.PropertyFactory;

/**
 * Popup frame with export options
 *
 * @author  @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class HPFrame extends JFrame
{
	private MainHP mainHP = null;

	private static String myGetTitle()
	{
		String title = PropertyFactory.getString("in_adjustHP");
		final int idx = title.indexOf("%s");
		if (idx >= 0)
		{
			title = title.substring(0, idx) + Globals.getGameModeHitPointText() + title.substring(idx + 2);
		}
		return title;
	}

	HPFrame()
	{
		super();
		setTitle(myGetTitle());
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource(IconUtilitities.RESOURCE_URL + "PcgenIcon.gif"));
		this.setIconImage(img);
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth >> 1, screenHeight >> 1);
		setLocation(screenWidth >> 2, screenHeight >> 2);

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
