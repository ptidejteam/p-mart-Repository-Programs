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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.Filterable;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;

/**
 * <code>AlignmentPanel</code><br>
 * <code>JPanel</code> with <code>JLabel</code> and
 * <code>JComboBoxEx</code> for alignment selection.<br>
 * This class is self-synchronizing with respect to instances
 * that display alignment for a common
 * {@link pcgen.core.PlayerCharacter} instance.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

final class AlignmentPanel extends JPanel
{
	static final long serialVersionUID = 6524333911930050584L;
	private static final Map MODELS = new HashMap();
	private static final Map FILTERABLES = new HashMap();

	private JLabel alignmentLabel;
	private JComboBoxEx alignmentBox;

	private List filterables;

	/**
	 * Constructor
	 *
	 * <br>author: Thomas Behr 13-03-02
	 */
	AlignmentPanel()
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
	 */
	private void alignmentActionPerformed()
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
		List exclassList = new ArrayList();
		PCClass aClass;
		for (Iterator it = classList.iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();

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
			if (GuiFacade.showConfirmDialog(null, PropertyFactory.getString("in_alPaConfirm") + ":" + Constants.s_LINE_SEP + //				PropertyFactory.getString("in_alPaConfirm") + ":" + "\n" +
				unqualified.toString(), "PCGen", GuiFacade.OK_CANCEL_OPTION, GuiFacade.QUESTION_MESSAGE) == GuiFacade.CANCEL_OPTION)
			{
				aPC.setAlignment(oldAlignment, false, true);
				alignmentBox.setSelectedIndex(oldAlignment);
				return;
			}
		}

		// Convert the class(es)
		for (Iterator it = exclassList.iterator(); it.hasNext();)
		{
			aPC.makeIntoExClass((PCClass) it.next());
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

		alignmentLabel = new JLabel(PropertyFactory.getString("in_alignLabel") + " : ");
		alignmentBox = new JComboBoxEx(getModel());
		Utility.setDescription(alignmentBox, PropertyFactory.getString("in_alPaTip1") + ".");
		alignmentBox.setSelectedIndex(Math.max(0, Globals.getCurrentPC().getAlignment()));
		alignmentBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				alignmentActionPerformed();
				for (Iterator it = filterables.iterator(); it.hasNext();)
				{
					((Filterable) it.next()).refreshFiltering();
				}
			}
		});

		Utility.buildConstraints(gbc, 0, 0, 1, 1, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(alignmentLabel, gbc);
		add(alignmentLabel);

		Utility.buildConstraints(gbc, 1, 0, 1, 1, 0, 0);
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

		List filterables = (List) FILTERABLES.get(aPC);
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
	private static ComboBoxModel getModel()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();

		ComboBoxModel model = (ComboBoxModel) MODELS.get(aPC);
		if (model == null)
		{
			MODELS.put(aPC, model);
		}

		return model;
	}

}


