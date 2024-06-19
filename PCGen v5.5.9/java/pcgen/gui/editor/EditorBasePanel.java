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
 * @(#) $Id: EditorBasePanel.java,v 1.1 2006/02/21 01:28:20 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import pcgen.core.PObject;
import pcgen.util.PropertyFactory;

/**
 * <code>EditorBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
final class EditorBasePanel extends JPanel
{

	private JPanel pnlProductIdentity;
	private BasePanel jPanel1;
	private JLabel lblSource;
	private JTextField txtSource;
	private JPanel pnlName;
	private JPanel pnlSource;
	private JTextField txtName;
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

		pnlName = new JPanel();
		lblName = new JLabel();
		txtName = new JTextField();
		String nameID = "";
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				nameID = "ClassName";
				jPanel1 = new ClassBasePanel();
				break;

			case EditorConstants.EDIT_DEITY:
				nameID = "DeityName";
				jPanel1 = new DeityBasePanel();
				break;

			case EditorConstants.EDIT_DOMAIN:
				nameID = "DomainName";
				jPanel1 = new DomainBasePanel();
				break;

			case EditorConstants.EDIT_FEAT:
				nameID = "FeatName";
				jPanel1 = new FeatBasePanel();
				break;

			case EditorConstants.EDIT_LANGUAGE:
				nameID = "LanguageName";
				jPanel1 = new LanguageBasePanel();
				break;

			case EditorConstants.EDIT_RACE:
				nameID = "RaceName";
				jPanel1 = new RaceBasePanel();
				break;

			case EditorConstants.EDIT_SKILL:
				nameID = "SkillName";
				jPanel1 = new SkillBasePanel();
				break;

			case EditorConstants.EDIT_SPELL:
				nameID = "SpellName";
				jPanel1 = new SpellBasePanel();
				break;

			case EditorConstants.EDIT_TEMPLATE:
				nameID = "TemplateName";
				jPanel1 = new TemplateBasePanel();
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				nameID = "CampaignName";
				jPanel1 = new SourceBasePanel();
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

		pnlName.setLayout(new GridBagLayout());

		lblName.setText(PropertyFactory.getString("in_dem" + nameID));
		lblName.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_" + nameID));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(lblName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(txtName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jPanel1, gridBagConstraints);

		pnlProductIdentity.setLayout(new FlowLayout(FlowLayout.RIGHT));

		chkProductIdentity.setText(PropertyFactory.getString("in_demProIden"));
		chkProductIdentity.setMnemonic(PropertyFactory.getMnemonic("in_mn_demProIden"));
		chkProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlProductIdentity.add(chkProductIdentity);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(pnlProductIdentity, gridBagConstraints);

		pnlSource.setLayout(new GridBagLayout());

		lblSource.setLabelFor(txtSource);
		lblSource.setText(PropertyFactory.getString("in_sourceLabel"));
		lblSource.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_sourceLabel"));
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
		txtName.setText(aString);
	}

	public String getNameText()
	{
		return txtName.getText().trim();
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

	void updateView(PObject thisPObject)
	{
		jPanel1.updateView(thisPObject);
	}

	void updateData(PObject thisPObject)
	{
		jPanel1.updateData(thisPObject);
	}

}
