/*
 * InfoCompanions.java
 *
 * Copyright (C) 2001 Thomas G. W. Epperly
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
 * Created on June 2, 2001, 1:34 PM
 */

package pcgen.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * This is a bare bones companions text editor.
 *
 * @author  Tom Epperly <tepperly@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class InfoCompanions extends JPanel
{

	private boolean d_shown = false;
	private JLabel d_companionLabel;
	private JScrollPane d_companionArea;
	private JTextArea d_companionText;

	/** Creates new form InfoCompanions */
	public InfoCompanions()
	{
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		d_companionLabel = new JLabel();
		d_companionArea = new JScrollPane();
		d_companionText = new JTextArea();

		setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraints1;

		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				initializeContent(evt);
			}

			public void componentHidden(java.awt.event.ComponentEvent evt)
			{
				storeContent(evt);
			}
		});

		d_companionLabel.setText("Companions");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		add(d_companionLabel, gridBagConstraints1);

		d_companionText.setLineWrap(true);
		d_companionText.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				updatePC(evt);
			}
		});

		d_companionArea.setViewportView(d_companionText);

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		add(d_companionArea, gridBagConstraints1);

	}

	private void storeContent(java.awt.event.ComponentEvent evt)
	{
		if (Globals.getCurrentPC() != null && d_shown)
		{
			java.util.ArrayList ml = Globals.getCurrentPC().getMiscList();
			String currentCore = (String)ml.get(1);
			String currentDisplay = d_companionText.getText();
			if (!currentDisplay.equals(currentCore))
			{
				ml.set(1, currentDisplay);
				Globals.getCurrentPC().setDirty(true);
			}
		}
	}

	private void initializeContent(java.awt.event.ComponentEvent evt)
	{
		if (Globals.getCurrentPC() != null)
		{
			String currentCore = (String)Globals.getCurrentPC().getMiscList().get(1);
			String currentDisplay = d_companionText.getText();
			if (!currentDisplay.equals(currentCore))
			{
				d_companionText.setText(currentCore);
			}
		}
		else
		{
			d_companionText.setText("");
		}
		requestDefaultFocus();
		d_shown = true;
	}

	private void updatePC(java.awt.event.FocusEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String origText = (String)Globals.getCurrentPC().getMiscList().get(1);
		String newText = d_companionText.getText();
		if ((aPC != null) &&
			(Globals.getCurrentPC().getMiscList().size() > 1) &&
			!newText.equals(origText)
		)
		{
			Globals.getCurrentPC().getMiscList().set(1,
				newText);
			Globals.getCurrentPC().setDirty(true);
		}
	}

}
