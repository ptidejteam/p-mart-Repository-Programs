/*
 * EditorBasePanel.java
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
 * Created on October 31, 2002, 4:36 PM
 *
 * @(#) $Id: EditorBasePanel.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

/**
 * <code>EditorBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import pcgen.util.PropertyFactory;

final class EditorBasePanel extends JPanel
{

	private JPanel pnlProductIdentity;
	private BasePanel jPanel1;
	private JLabel lblSource;
	private JTextField txtSource;
	private JPanel pnlDeityName;
	private JPanel pnlSource;
	private JTextField txtDeityName;
	private JLabel lblName;
	private JCheckBox chkProductIdentity;

	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form EditorBasePanel */
	EditorBasePanel(int argEditType)
	{
		editType = argEditType;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

//setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.green,java.awt.Color.green));

		pnlDeityName = new JPanel();
		lblName = new JLabel();
		txtDeityName = new JTextField();
		String nameID = "";
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				jPanel1 = new DeityBasePanel();
				nameID = "DeityName";
				break;

			case EditorConstants.EDIT_SKILL:
				nameID = "SkillName";
				jPanel1 = new SkillBasePanel();
				break;

			default:
				jPanel1 = new BasePanel();
				break;
		}
		pnlProductIdentity = new JPanel();
		chkProductIdentity = new JCheckBox();
		pnlSource = new JPanel();
		lblSource = new JLabel();
		txtSource = new JTextField();

		setLayout(new GridBagLayout());

		pnlDeityName.setLayout(new GridBagLayout());

		lblName.setText(PropertyFactory.getString("in_dem" + nameID));
		lblName.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_" + nameID, 0));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityName.add(lblName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityName.add(txtDeityName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlDeityName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jPanel1, gridBagConstraints);

		pnlProductIdentity.setLayout(new FlowLayout(FlowLayout.RIGHT));

		chkProductIdentity.setText(PropertyFactory.getString("in_demProIden"));
		chkProductIdentity.setMnemonic(PropertyFactory.getMnemonic("in_mn_demProIden", 0));
		chkProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlProductIdentity.add(chkProductIdentity);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(pnlProductIdentity, gridBagConstraints);

		pnlSource.setLayout(new GridBagLayout());

		lblSource.setLabelFor(txtSource);
		lblSource.setText(PropertyFactory.getString("in_sourceLabel"));
		lblSource.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_sourceLabel", 0));
		lblSource.setPreferredSize(new Dimension(70, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSource.add(lblSource, gridBagConstraints);

		txtSource.setPreferredSize(new Dimension(280, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSource.add(txtSource, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(pnlSource, gridBagConstraints);
	}

	public void setNameText(String aString)
	{
		txtDeityName.setText(aString);
	}

	public String getNameText()
	{
		return txtDeityName.getText().trim();
	}

	public void setSourceText(String aString)
	{
		txtSource.setText(aString);
	}

	public String getSourceText()
	{
		return txtSource.getText().trim();
	}

	public void setProductIdentity(boolean isPI)
	{
		chkProductIdentity.setSelected(isPI);
	}

	public boolean getProductIdentity()
	{
		return chkProductIdentity.isSelected();
	}

	public void setHolyItemText(final String aString)
	{
		jPanel1.setHolyItemText(aString);
	}

	public String getHolyItemText()
	{
		return jPanel1.getHolyItemText();
	}

	public void setDescriptionText(final String aString)
	{
		jPanel1.setDescriptionText(aString);
	}

	public String getDescriptionText()
	{
		return jPanel1.getDescriptionText();
	}

	public void setDeityAlignment(final String aString)
	{
		jPanel1.setDeityAlignment(aString);
	}

	public String getDeityAlignment()
	{
		return jPanel1.getDeityAlignment();
	}

	public void setFavoredWeaponsAvailableList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setFavoredWeaponsAvailableList(aList, sort);
	}

	public Object[] getFavoredWeaponsAvailableList()
	{
		return jPanel1.getFavoredWeaponsAvailableList();
	}

	public void setFavoredWeaponsSelectedList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setFavoredWeaponsSelectedList(aList, sort);
	}

	public Object[] getFavoredWeaponsSelectedList()
	{
		return jPanel1.getFavoredWeaponsSelectedList();
	}

	public void setTypesAvailableList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setTypesAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setTypesSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return jPanel1.getTypesSelectedList();
	}

	public void setIsUntrained(final boolean isUntrained)
	{
		jPanel1.setIsUntrained(isUntrained);
	}

	public boolean getIsUntrained()
	{
		return jPanel1.getIsUntrained();
	}

	public void setIsExclusive(final boolean isExclusive)
	{
		jPanel1.setIsExclusive(isExclusive);
	}

	public boolean getIsExclusive()
	{
		return jPanel1.getIsExclusive();
	}

	public void setKeyStat(final String aString)
	{
		jPanel1.setKeyStat(aString);
	}

	public String getKeyStat()
	{
		return jPanel1.getKeyStat();
	}

	public void setArmorCheck(final int aCheck)
	{
		jPanel1.setArmorCheck(aCheck);
	}

	public int getArmorCheck()
	{
		return jPanel1.getArmorCheck();
	}
}
