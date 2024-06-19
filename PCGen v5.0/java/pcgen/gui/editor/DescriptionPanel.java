/*
 * DescriptionPanel.java
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
 * Created on January 20, 2003, 4:39 PM
 *
 * @(#) $Id: DescriptionPanel.java,v 1.1 2006/02/21 01:07:44 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import pcgen.util.PropertyFactory;

/**
 * <code>DescriptionPanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class DescriptionPanel extends JPanel
{
	private JScrollPane scpDescription;
	private JPanel pnlDescriptionText;
	private JTextArea txtDescription;
	private JCheckBox chkDescProductIdentity;
	private JLabel lblDescription;

	DescriptionPanel()
	{
		super();
		initComponents();
	}

	private void initComponents()
	{
		lblDescription = new JLabel();
		pnlDescriptionText = new JPanel();
		scpDescription = new JScrollPane();
		txtDescription = new JTextArea();
		chkDescProductIdentity = new JCheckBox();

		setLayout(new GridBagLayout());

		GridBagConstraints gbc;

		lblDescription.setLabelFor(txtDescription);
		lblDescription.setText(PropertyFactory.getString("in_descrip"));
		lblDescription.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_descrip"));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		add(lblDescription, gbc);

		pnlDescriptionText.setLayout(new BorderLayout());

		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		scpDescription.setViewportView(txtDescription);

		pnlDescriptionText.add(scpDescription, BorderLayout.CENTER);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.8;
		add(pnlDescriptionText, gbc);

		chkDescProductIdentity.setText(PropertyFactory.getString("in_demDescProIden"));
		chkDescProductIdentity.setMnemonic(PropertyFactory.getMnemonic("in_mn_demDescProIden"));
		chkDescProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		add(chkDescProductIdentity, gbc);
	}

	final void setText(final String aString)
	{
		txtDescription.setText(aString);
	}

	final String getText()
	{
		return txtDescription.getText().trim();
	}

	final void setDescIsPI(final boolean descIsPI)
	{
		chkDescProductIdentity.setSelected(descIsPI);
	}

	final boolean getDescIsPI()
	{
		return chkDescProductIdentity.isSelected();
	}
}
