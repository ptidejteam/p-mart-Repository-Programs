/*
 * VisionPanel.java
 * Copyright 2003 (C) Richard Askham <raskham@users.sourceforge.net>
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
 * Created on January 22, 2003, 10:50 PM
 *
 * @(#) $Id: VisionPanel.java,v 1.1 2006/02/21 01:33:33 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import pcgen.core.Constants;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

/**
 * <code>VisionPanel</code>
 *
 * The VisionPanel class provides a user interface allowing the user to select
 * the types of vision supported by a race.
 *
 * @author  Richard Askham <raskham@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
final class VisionPanel extends JPanel
{
	static final long serialVersionUID = 7340632645513314167L;
	private JComboBoxEx cmbVisionType;
	private JTextField txtVisionDistance;
	private JButton btnAdd;
	private JButton btnRemove;
	private JLabel lblSelected;
	private JList lstSelected;
	private JPanel pnlAvailable;
	private JPanel pnlAddRemove;
	private JPanel pnlSelected;
	private JScrollPane scpSelected;

	/**
	 * Creates a new VisionPanel
	 */
	VisionPanel()
	{
		super();
		initComponents();
		initComponentContents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel lblTemp;

		cmbVisionType = new JComboBoxEx(new String[]{"Normal", "Low-light", "Keen Low-light", "Darkvision", "Blindsight", "Keen Scent", "Tremorsense"});
		cmbVisionType.setEditable(true);

		txtVisionDistance = new JTextField();
		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAdd = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
			btnRemove = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		}
		catch (Exception exc)
		{
			btnAdd = new JButton(">");
			btnRemove = new JButton("<");
		}

		lblSelected = new JLabel(PropertyFactory.getString("in_selected"));
		lstSelected = new JList(new JListModel(new ArrayList(), true));
		pnlAvailable = new JPanel();
		pnlAddRemove = new JPanel();
		pnlSelected = new JPanel();
		scpSelected = new JScrollPane();

		// Layout the available panel
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_demTag"));
		title1.setTitleJustification(TitledBorder.LEFT);
		pnlAvailable.setBorder(title1);
		pnlAvailable.setLayout(new GridBagLayout());

		lblTemp = new JLabel(PropertyFactory.getString("in_demVisionType"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(cmbVisionType, gridBagConstraints);

		lblTemp = new JLabel(PropertyFactory.getString("in_demDistance"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(txtVisionDistance, gridBagConstraints);

		// Layout the add/remove panel
		pnlAddRemove.setLayout(new GridBagLayout());

		btnAdd.setEnabled(true);
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddActionPerformed();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAddRemove.add(btnAdd, gridBagConstraints);

		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveActionPerformed();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAddRemove.add(btnRemove, gridBagConstraints);

		// Layout the selected panel
		pnlSelected.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		lstSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstSelectedMouseClicked(evt);
			}
		});
		scpSelected.setPreferredSize(new Dimension(90, 20));
		scpSelected.setViewportView(lstSelected);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

		this.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.4;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAddRemove, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlSelected, gridBagConstraints);
	}

	private void initComponentContents()
	{
	}

	/**
	 * Used to detect when an item in the selected list double-clicked  If the
	 * item in the selected list is double- clicked, it will be removed.
	 */
	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (evt.getSource().equals(lstSelected))
		{
			final String aString = (String) lstSelected.getSelectedValue();

			final StringTokenizer visionString = new StringTokenizer(aString, ",", false);

			// Set vision type to selected item
			cmbVisionType.setSelectedItem(visionString.nextToken());

			// Set the vision distance to the selected item
			txtVisionDistance.setText(visionString.nextToken());

			if (EditUtil.isDoubleClick(evt, lstSelected, btnRemove))
			{
				btnRemoveActionPerformed();
			}
		}
	}

	/**
	 * Removes the selected vision string from the selected list.
	 */
	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);

		final JListModel lms = (JListModel) lstSelected.getModel();
		final Object[] x = lstSelected.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			lms.removeElement(x[i]);
		}
	}

	/**
	 * Adds the specified vision string to the selected list.
	 */
	private void btnAddActionPerformed()
	{
		String visionType = (String) cmbVisionType.getSelectedItem();

		if (visionType == null || visionType.length() == 0)
		{
			GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_demVisionInfoMissing"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		String newEntry = (String) cmbVisionType.getSelectedItem() + ",";
		if (txtVisionDistance.getText().trim().length() == 0)
		{
			newEntry += "0";
		}
		else
		{
			newEntry += txtVisionDistance.getText().trim();
		}

		addToSelectedLst(newEntry);
	}

	/**
	 * Sets the selected vision values to the supplied values.
	 *
	 * @param argSelected A list of string vision values.
	 */
	void setSelectedList(List argSelected)
	{
		for (int i = 0, x = argSelected.size(); i < x; ++i)
		{
			addToSelectedLst((String) argSelected.get(i));

		}
	}

	/**
	 * Returns the selected vision data as an array of vision specification.
	 * Each vision spec is a string.
	 *
	 * @return Object[] The selected vision values.
	 */
	Object[] getSelectedList()
	{
		return ((JListModel) lstSelected.getModel()).getElements();
	}

	/**
	 * Adds the specified vision type to the selected list. This will replace
	 * any vision values of the same type.
	 *
	 * @param visionValue A string containing the comma delimited parameters of
	 * the vision (type & distance)
	 */
	private void addToSelectedLst(String visionValue)
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();

		// Remove all matching vision types from the selected list
		for (int i = 0, x = lmd.getSize(); i < x; ++i)
		{
			Object obj = lmd.getElementAt(i);
			if (((String) obj).startsWith(visionValue.substring(0, visionValue.indexOf(","))))
			{
				lmd.removeElement(obj);
				x--;
			}
		}

		// Add in new value
		lmd.addElement(visionValue);

	}

	/**
	 * Returns the selected vision data as a hashmap
	 *
	 * @return HashMap The vision table.
	 */
	Map getVision()
	{

		Map vision = new HashMap();
		//vision.put("Normal", "0");

		Object[] selected = getSelectedList();
		for (int index = 0; index < selected.length; index++)
		{
			final String moveString = (String) selected[index];
			final int idx = moveString.indexOf(",");
			vision.put(moveString.substring(0, idx), moveString.substring(idx + 1));
		}
		return vision;

	}

}
