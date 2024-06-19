/*
 * AlignmentPanel.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 13, 2002, 5:00 PM
 */
package pcgen.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.Filterable;

/**
 * <code>AlignmentPanel</code><br>
 * <code>JPanel</code> with <code>JLabel</code> and
 * <code>JComboBox</code> for alignment selection.<br>
 * This class is self-synchronizing with respect to instances
 * that display alignment for a common
 * {@link pcgen.core.PlayerCharacter} instance.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public final class AlignmentPanel extends JPanel
{
	private static final Hashtable MODELS = new Hashtable();
	private static final Hashtable FILTERABLES = new Hashtable();

	private JLabel alignmentLabel;
	private JComboBox alignmentBox;

	private List filterables;

	/**
	 * Constructor
	 *
	 * <br>author: Thomas Behr 13-03-02
	 */
	public AlignmentPanel()
	{
		super();
		initComponents();
		filterables = getFilterables();
	}

	/**
	 * implementation of {@link java.awt.event.ActionListener}
	 * interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param e
	 */
	private void alignmentActionPerformed(ActionEvent e)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int newAlignment = alignmentBox.getSelectedIndex();
		final int oldAlignment = aPC.getAlignment();
		if (newAlignment == oldAlignment)
		{
			return;
		}

// Get a list of classes that will become unqualified
		StringBuffer unqualified = new StringBuffer();
		List classList = aPC.getClassList();
		ArrayList exclassList = new ArrayList();
		PCClass aClass;
		for (Iterator it = classList.iterator(); it.hasNext();)
		{
			aClass = (PCClass)it.next();

			aPC.setAlignment(oldAlignment, false, true);
			if (aClass.isQualified())
			{
				aPC.setAlignment(newAlignment, false, true);
				if (!aClass.isQualified())
				{
					if (unqualified.length() > 0)
					{
						unqualified.append(", ");
					}
					unqualified.append(aClass.getName());
					exclassList.add(aClass);
				}
			}
		}

// Give the user a chance to bail
		if (unqualified.length() > 0)
		{
			if (JOptionPane.showConfirmDialog(null,
				"This will change the following class(es) to ex-class(es):\n" +
				unqualified.toString(),
				"PCGen",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				aPC.setAlignment(oldAlignment, false, true);
				alignmentBox.setSelectedIndex(oldAlignment);
				return;
			}
		}

// Convert the class(es)
		for (Iterator it = exclassList.iterator(); it.hasNext();)
		{
			aPC.makeIntoExClass((PCClass)it.next());
		}
		aPC.setAlignment(newAlignment, false, true);
	}

	/**
	 * initialize gui components
	 *
	 * <br>author: Thomas Behr
	 */
	private void initComponents()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		alignmentLabel = new JLabel("Alignment : ");
		alignmentBox = new JComboBox(getModel());
		Utility.setDescription(alignmentBox, "You must select an alignment.");
		alignmentBox.setSelectedIndex(Math.max(0, Globals.getCurrentPC().getAlignment()));
		alignmentBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				alignmentActionPerformed(e);
				for (Iterator it = filterables.iterator(); it.hasNext();)
				{
					((Filterable)it.next()).refreshFiltering();
				}
			}
		});

		/*
		 * this ugly, but I am too lazy to define an
		 * appropriate add-method for two components
		 *
		 * author: Thomas Behr 14-03-02
		 */
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(alignmentLabel, gbc);
		add(alignmentLabel);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(alignmentBox, gbc);
		add(alignmentBox);

	}

	/**
	 *
	 * <br>author: Thomas Behr 13-03-02
	 *
	 * @return a list of shared <code>Filterable</code> instances
	 */
	private static List getFilterables()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();

		List filterables = (List)FILTERABLES.get(aPC);
		if (filterables == null)
		{
			filterables = new ArrayList();
			FILTERABLES.put(aPC, filterables);
		}

		return filterables;
	}

	/**
	 *
	 * <br>author: Thomas Behr 13-03-02
	 *
	 * @return a shared <code>ComboBoxModel</code> instance
	 */
	private ComboBoxModel getModel()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();

		ComboBoxModel model = (ComboBoxModel)MODELS.get(aPC);
		if (model == null)
		{
			model = new DefaultComboBoxModel(Constants.s_ALIGNLONG);
			MODELS.put(aPC, model);
		}

		return model;
	}


	/**
	 * Register a Filterable instance to be updated on
	 * changes to the alignment.<br>
	 * The <code>Filterable</code>
	 * instance will be shared appropriatly.<br>
	 * This class does not check for duplicates!
	 *
	 * <br>author: Thomas Behr 13-03-02
	 *
	 * @param filterable   a Filterable instance to
	 *                     refresh on any alignment change
	 */
	public static void registerFilterable(Filterable filterable)
	{
		getFilterables().add(filterable);
	}

	/**
	 * Register a list of Filterable instances to be updated on
	 * changes to the alignment.<br>
	 * The <code>Filterable</code>
	 * instances will be shared appropriatly.<br>
	 * This class does not check for duplicates!
	 *
	 * <br>author: Thomas Behr 13-03-02
	 *
	 * @param filterables   a list of Filterable instances to
	 *                      refresh on any alignment change
	 */
	public static void registerFilterables(List filterables)
	{
		getFilterables().addAll(filterables);
	}

	/**
	 * disposes of the shared <code>ComboBoxModel</code> instance
	 * and all shared <code>Filterable</code> instances.<br>
	 * BEWARE:<br>
	 * After calling this method, instances of <code>AlignmentPanel</code>
	 * that share this instance's <code>ComboBoxModel</code> and
	 * <code>Filterable</code> instances will not work properly
	 * anymore!
	 *
	 * <br>author: Thomas Behr 13-03-02
	 */
	public void dispose()
	{
		final ComboBoxModel model = alignmentBox.getModel();

		for (Iterator it = MODELS.keySet().iterator(); it.hasNext();)
		{
			if (MODELS.get(it.next()).equals(model))
			{
				it.remove();
				break;
			}
		}

		for (Iterator it = FILTERABLES.keySet().iterator(); it.hasNext();)
		{
			if (FILTERABLES.get(it.next()).equals(filterables))
			{
				it.remove();
				break;
			}
		}

		filterables = null;
	}
}


