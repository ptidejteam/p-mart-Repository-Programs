/*
 * TypePanel.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 24, 2003, 11:41 PM
 *
 * @(#) $Id: TypePanel.java,v 1.1 2006/02/21 01:07:44 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import pcgen.core.Globals;
import pcgen.util.PropertyFactory;

/**
 * <code>TypePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

class TypePanel extends AvailableSelectedPanel
{
	private JLabel lblNewType;
	private JTextField txtNewType = new JTextField();
	private JButton btnNewType;

	/** Creates new form QualifiedAvailableSelectedPanel */
	TypePanel(final String qtext)
	{
		this(qtext, PropertyFactory.getString("in_type"));
	}

	TypePanel(final String qtext, final String title)
	{
		super(false);
		if (qtext == null)
		{
			return;
		}

		setBorder(new javax.swing.border.TitledBorder(title));

		lblNewType = new JLabel(qtext);
		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnNewType = new JButton(new ImageIcon(Globals.getRootFrame().getClass().getResource("resource/Forward16.gif")));
		}
		catch (Exception exc)
		{
			btnNewType = new JButton(">");
		}

		setExtraLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5, 2, 5);
		addExtra(lblNewType, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.weightx = 1.0;
		addExtra(txtNewType, gbc);

		btnNewType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnNewTypeActionPerformed();
			}
		});

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 12, 2, 5);
		addExtra(btnNewType, gbc);

	}

	private void btnNewTypeActionPerformed()
	{
		final String aString = txtNewType.getText().trim().toUpperCase();
		if (aString.length() != 0)
		{
			addItemToSelected(aString);
		}
	}

}
