/*
 * InfoNaturalWeapons.java
 * Copyright 2003 (C) Greg Bingleman
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
 * Created on February 10, 2003, 11:45 PM
 *
 * @(#) $Id: InfoNaturalWeapons.java,v 1.1 2006/02/21 01:18:57 vauchers Exp $
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.utils.JTableEx;
import pcgen.util.Delta;

/**
 * ???
 * @author  Greg Bingleman (byngl@hotmail.com)
 * @version $Revision: 1.1 $
 */
public class InfoNaturalWeapons extends FilterAdapterPanel
{
	static final long serialVersionUID = 7796493138427983908L;
	private JTableEx tblNaturalAttacks;
	private JScrollPane scpNaturalAttacks;

	private static boolean needsUpdate = true;
	private static PlayerCharacter aPC = null;

	/** Creates new form NaturalEquipPane */
	public InfoNaturalWeapons()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Constants.tabNames[Constants.TAB_NATWEAPONS]);

		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		scpNaturalAttacks = new JScrollPane();
		tblNaturalAttacks = new JTableEx();

		setLayout(new BorderLayout());

		tblNaturalAttacks.setBackground(getBackground());
		tblNaturalAttacks.setModel(new DefaultTableModel(
			new String[]{
				"Attack", "#", "To-Hit Modifier", "Damage/Attack", "Excludes Weapons", "Iterative BAB", "Enabled"
			}, 0
		)
		{
			Class[] types = new Class[]
			{
				String.class,
				Integer.class,
				String.class,
				String.class,
				Boolean.class,
				Boolean.class,
				Boolean.class
			};
			boolean[] canEdit = new boolean[]
			{
				false,
				false,
				false,
				false,
				true,
				true,
				true
			};

			public Class getColumnClass(int columnIndex)
			{
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return canEdit[columnIndex];
			}
		});
		scpNaturalAttacks.setViewportView(tblNaturalAttacks);

		add(scpNaturalAttacks, BorderLayout.CENTER);
	}

	public void setNeedsUpdate(boolean bNeedsUpdate)
	{
		needsUpdate = bNeedsUpdate;
	}

	public void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if ((bPC == null) || (!needsUpdate && (aPC == bPC)))
		{
//			return;
		}
		aPC = bPC;

//System.err.println("here");

//System.err.println("NATURALWEAPONS:" + aPC.getRace().getNaturalWeapons());

		//
		// Get a list of natural weapons only
		//
		Equipment eq;
		List naturalWeapons = new ArrayList();
		for (Iterator e = aPC.getEquipmentMasterList().iterator(); e.hasNext();)
		{
			eq = (Equipment) e.next();
			if (!eq.isWeapon() || !eq.isType("Natural"))
			{
				continue;
			}
			naturalWeapons.add(eq);
		}

		DefaultTableModel tblModel = (DefaultTableModel) tblNaturalAttacks.getModel();
		//
		// Clear out existing contents of table
		//
		for (int i = tblModel.getRowCount() - 1; i >= 0; --i)
		{
			tblModel.removeRow(i);
		}

		final int weaponCount = naturalWeapons.size();
		if (weaponCount == 0)
		{
			return;
		}

		if (weaponCount >= 1)
		{
//System.err.println("before: " + naturalWeapons);
			Collections.sort(naturalWeapons, new Comparator()
			{
				public final int compare(final Object o1, final Object o2)
				{
					final int mem1 = ((Equipment) o1).getMemberOfKit();
					final int mem2 = ((Equipment) o2).getMemberOfKit();
					if (mem1 < mem2)
					{
						return -1;
					}
					else if (mem1 > mem2)
					{
						return 1;
					}
					if (((Equipment) o1).isType("Primary"))
					{
						return -1;
					}
					return ((Equipment) o1).getName().compareToIgnoreCase(((Equipment) o2).getName());
				}
			});
//System.err.println("after: " + naturalWeapons);
		}

		final int meleeBonus = (int) aPC.getStatBonusTo("TOHIT", "TYPE=MELEE");
		final int rangeBonus = (int) aPC.getStatBonusTo("TOHIT", "TYPE=RANGED");
		final int damagBonus = (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");
		final int BAB = aPC.baseAttackBonus();
		final int sizeAdj = (int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");

//System.err.println("meleeBonus=" + meleeBonus);
//System.err.println("rangeBonus=" + rangeBonus);
//System.err.println("damagBonus=" + damagBonus);
//System.err.println("BAB=" + BAB);
//System.err.println("sizeAdj=" + sizeAdj);


//System.err.println(aPC.getKitInfo());

		int prevKitNo = -9;
		int iWeaponCount = 0;
		for (int i = 0; i < weaponCount; ++i)
		{
			eq = (Equipment) naturalWeapons.get(i);
			final int wpBonus = (int) aPC.getTotalBonusTo("WEAPONPROF=" + eq.profName(), "TOHIT");

			if (prevKitNo != eq.getMemberOfKit())
			{
				iWeaponCount = 0;
			}

//System.err.println("wpBonus=" + wpBonus);

			Object[] newRow = new Object[7];
			newRow[0] = eq.getName();				// Attack
			newRow[1] = new Integer((int) eq.qty());		// #
			//
			// MM p.7
			// Primary attack is at BAB + modifiers
			// All other attacks have a -5 modifier, unless has Multiattack feat, in which
			// case, all other attacks get a -2 modifier
			//
			// In the case where there is only 1 secondary attack, this attack does not get
			// a modifier
			//
			StringBuffer tohitStr = new StringBuffer(12);
			int toHit = BAB + sizeAdj;
			if (!eq.isType("Primary"))
			{
				toHit -= 5 - (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY");
			}
			else
			{
				//
				// Do this in case we have a need to futz with the primary to hit without
				// affecting the secondary to hit
				//
				toHit += (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY");
			}
			if (eq.isMelee())
			{
				tohitStr.append(Delta.toString(toHit + meleeBonus + wpBonus));
				tohitStr.append(" melee");
			}
			else if (eq.isRanged())
			{
				tohitStr.append(Delta.toString(toHit + rangeBonus));
				tohitStr.append(" ranged");
			}
			newRow[2] = tohitStr.toString();				// To-Hit Modifier

			int damBonus = damagBonus;
			if (damBonus > 0)
			{
				//
				// MM p.7
				// Primary attack gets a 1.5 multiplier if it is the only attack
				// Secondary attack get a 0.5 multiplier
				//
				// TODO: This bit of code will not return the correct value if the creature
				// has a choice of one of two primary attacks. eg. 1 Slam OR 1 Trample
				//
				if (eq.isType("Primary") && (naturalWeapons.size() == 1) && ((int) eq.qty() == 1))
				{
					if (!eq.isRanged())
					{
						damBonus = (damBonus * 3) / 2;
					}
				}
				else if (eq.isType("Secondary"))
				{
					damBonus /= 2;
				}
			}
			StringBuffer damage = new StringBuffer(12);
			damage.append(eq.getDamage());
			if (damBonus != 0)
			{
				damage.append(Delta.toString(damBonus));
			}
			newRow[3] = damage.toString();			// Damage

			newRow[4] = new Boolean(false);			// Excludes Weapons
			newRow[5] = new Boolean(false);			// Iterative BAB
			newRow[6] = new Boolean(true);			// Enabled
			tblModel.addRow(newRow);
		}

	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllEquipmentFilters(this);

		setKitFilter("NATURALWEAPON");
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		setNeedsUpdate(true);
		updateCharacterInfo();
	}

	/**
	 * specifies whether the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}
}
