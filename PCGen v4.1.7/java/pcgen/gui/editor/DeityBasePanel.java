/*
 * DeityBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 1, 2002, 9:27 AM
 *
 * @(#) $Id: DeityBasePanel.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

/**
 * <code>DeityBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.util.PropertyFactory;

final class DeityBasePanel extends BasePanel
{
	private AvailableSelectedPanel pnlFavoredWeapons;
	private JScrollPane scpDescription;
	private JPanel pnlDescriptionText;
	private JPanel pnlDescription;
	private JComboBox cmbDeityAlignment;
	private JTextArea txtDescription;
	private JLabel lblDeityAlignment;
	private JLabel lblDescription;
	private JPanel pnlDeityAlignment;
	private JLabel lblHolyItem;
	private JTextField txtHolyItem;
	private JPanel pnlHolyItem;

	/** Creates new form DeityBasePanel */
	DeityBasePanel()
	{
		initComponents();
		intComponentContents();
	}

	private void intComponentContents()
	{
		//
		// Initialize the contents of the deity's alignment combo
		//
		ArrayList availableList = new ArrayList();
		for (Iterator e = Globals.getAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment anAlignment = (PCAlignment) e.next();
			if (anAlignment.isValidForDeity())
			{
				availableList.add(anAlignment.getName());
			}
		}
		cmbDeityAlignment.setModel(new DefaultComboBoxModel(availableList.toArray()));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

//setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.red,java.awt.Color.red));

		pnlHolyItem = new JPanel();
		lblHolyItem = new JLabel();
		txtHolyItem = new JTextField();
		pnlDeityAlignment = new JPanel();
		lblDeityAlignment = new JLabel();
		cmbDeityAlignment = new JComboBox();
		pnlDescription = new JPanel();
		lblDescription = new JLabel();
		pnlDescriptionText = new JPanel();
		scpDescription = new JScrollPane();
		txtDescription = new JTextArea();
		pnlFavoredWeapons = new AvailableSelectedPanel();

		setLayout(new GridBagLayout());

		pnlHolyItem.setLayout(new GridBagLayout());

		lblHolyItem.setLabelFor(txtHolyItem);
		lblHolyItem.setText(PropertyFactory.getString("in_demHolyItem"));
		lblHolyItem.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_demHolyItem", 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlHolyItem.add(lblHolyItem, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 1.0;
		pnlHolyItem.add(txtHolyItem, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlHolyItem, gridBagConstraints);

		pnlDeityAlignment.setLayout(new GridBagLayout());

		lblDeityAlignment.setLabelFor(cmbDeityAlignment);
		lblDeityAlignment.setText(PropertyFactory.getString("in_demDeityAlign"));
		lblDeityAlignment.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_demDeityAlign", 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityAlignment.add(lblDeityAlignment, gridBagConstraints);

		cmbDeityAlignment.setPreferredSize(new Dimension(180, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityAlignment.add(cmbDeityAlignment, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlDeityAlignment, gridBagConstraints);

		pnlDescription.setLayout(new GridBagLayout());

		lblDescription.setLabelFor(txtDescription);
		lblDescription.setText(PropertyFactory.getString("in_descrip"));
		lblDescription.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_descrip", 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		pnlDescription.add(lblDescription, gridBagConstraints);

		pnlDescriptionText.setLayout(new BorderLayout());

		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		scpDescription.setViewportView(txtDescription);

		pnlDescriptionText.add(scpDescription, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.8;
		pnlDescription.add(pnlDescriptionText, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlDescription, gridBagConstraints);

		pnlFavoredWeapons.setHeader(PropertyFactory.getString("in_demFavWea"));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlFavoredWeapons, gridBagConstraints);
	}

	public void setHolyItemText(String aString)
	{
		txtHolyItem.setText(aString);
	}

	public String getHolyItemText()
	{
		return txtHolyItem.getText().trim();
	}

	public void setDescriptionText(String aString)
	{
		txtDescription.setText(aString);
	}

	public String getDescriptionText()
	{
		return txtDescription.getText().trim();
	}

	public void setDeityAlignment(String aString)
	{
		for (Iterator e = Globals.getAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment anAlignment = (PCAlignment) e.next();
			if (anAlignment.isValidForDeity())
			{
				if (anAlignment.getKeyName().equals(aString))
				{
					cmbDeityAlignment.setSelectedItem(anAlignment.getName());
				}
			}
		}
	}

	public String getDeityAlignment()
	{
		String aString = (String) cmbDeityAlignment.getSelectedItem();
		if (aString != null)
		{
			final int dix = Globals.getIndexOfAlignment(aString);
			if (dix >= 0)
			{
				return Globals.getShortAlignmentAtIndex(dix);
			}
		}
		return null;
	}

	public void setFavoredWeaponsAvailableList(ArrayList aList, boolean sort)
	{
		pnlFavoredWeapons.setAvailableList(aList, sort);
	}

	public Object[] getFavoredWeaponsAvailableList()
	{
		return pnlFavoredWeapons.getAvailableList();
	}

	public void setFavoredWeaponsSelectedList(ArrayList aList, boolean sort)
	{
		pnlFavoredWeapons.setSelectedList(aList, sort);
	}

	public Object[] getFavoredWeaponsSelectedList()
	{
		return pnlFavoredWeapons.getSelectedList();
	}

}
