/*
 * QualifiedAvailableSelectedPanel.java
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
 * Created on January 19, 2003, 10:46 PM
 *
 * @(#) $Id: QualifiedAvailableSelectedPanel.java,v 1.1 2006/02/21 01:28:20 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

/**
 * <code>AvailableSelectedPanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class QualifiedAvailableSelectedPanel extends AvailableSelectedPanel
{
	private JLabel lblQualifier;
	private JLabel lblVariable;
	private JComboBoxEx cmbQualifier = new JComboBoxEx();
	private JComboBoxEx cmbVariable;

	//
	// Qualifier
	//

	void setQualifierModel(final DefaultComboBoxModel model)
	{
		cmbQualifier.setModel(model);
	}

	int getQualifierItemCount()
	{
		return cmbQualifier.getItemCount();
	}

	Object getQualifierItemAt(final int idx)
	{
		return cmbQualifier.getItemAt(idx);
	}

	int getQualifierSelectedIndex()
	{
		return cmbQualifier.getSelectedIndex();
	}

	void setQualifierSelectedIndex(final int idx)
	{
		cmbQualifier.setSelectedIndex(idx);
	}

	Object getQualifierSelectedItem()
	{
		return cmbQualifier.getSelectedItem();
	}

	//
	// Variable
	//

	void setVariableModel(final DefaultComboBoxModel model)
	{
		if (cmbVariable != null)
		{
			cmbVariable.setModel(model);
		}
	}

	int getVariableSelectedIndex()
	{
		if (cmbVariable != null)
		{
			return cmbVariable.getSelectedIndex();
		}
		return -1;
	}

	Object getVariableSelectedItem()
	{
		if (cmbVariable != null)
		{
			return cmbVariable.getSelectedItem();
		}
		return null;
	}

	void setVariableSelectedIndex(final int idx)
	{
		if (cmbVariable != null)
		{
			cmbVariable.setSelectedIndex(idx);
		}
	}

	void setVariableEnabled(final boolean isEnabled)
	{
		if (cmbVariable != null)
		{
			cmbVariable.setEnabled(isEnabled);
			lblVariable.setEnabled(isEnabled);
		}
	}

	/** Creates new form QualifiedAvailableSelectedPanel */
	QualifiedAvailableSelectedPanel(final String qtext, final String vtext, final EditorAddFilter filter, final ItemListener itml)
	{
		super(false);
		if (qtext == null)
		{
			return;
		}

		setExtraLayout(new GridBagLayout());

		lblQualifier = new JLabel(PropertyFactory.getString(qtext));
		lblQualifier.setLabelFor(cmbQualifier);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		addExtra(lblQualifier, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		addExtra(cmbQualifier, gbc);

		if (itml != null)
		{
			cmbQualifier.addItemListener(itml);
		}

		if (vtext != null)
		{
			cmbVariable = new JComboBoxEx();
			lblVariable = new JLabel(PropertyFactory.getString(vtext));
			lblVariable.setLabelFor(cmbVariable);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 5, 2, 5);
			gbc.anchor = GridBagConstraints.WEST;
			addExtra(lblVariable, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 5, 2, 5);
			gbc.anchor = GridBagConstraints.EAST;
			addExtra(cmbVariable, gbc);

			//
			// Disable the variable portion if there is a listener defined
			//
			if (itml != null)
			{
				lblVariable.setEnabled(false);
				cmbVariable.setEnabled(false);
			}

		}

		if (filter != null)
		{
			setAddFilter(filter);
		}

	}
}
