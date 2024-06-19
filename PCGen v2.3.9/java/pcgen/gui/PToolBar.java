/*
 * PToolBar.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class PToolBar extends JToolBar
{
	static Filter aFilter = null;

	public PToolBar()
	{
		addButtons(this);
	}

	protected void addButtons(JToolBar toolBar)
	{
		JButton button = null;

		//first button
		button = new JButton(new ImageIcon(getClass().getResource("filter.gif")));
		button.setToolTipText("Select filter.");
		button.setPreferredSize(new Dimension(20, 20));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (aFilter == null)
					aFilter = new Filter();
				aFilter.show();
			}
		});
		toolBar.add(button);


	}

}
