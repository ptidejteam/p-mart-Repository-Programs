/*
 * InfoInventory.java
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
 * Created on September 16, 2002, 3:30 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:13:32 $
 *
 */

package pcgen.gui.tabs;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pcgen.core.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.PToolBar;
import pcgen.gui.filter.Filterable;
import pcgen.util.PropertyFactory;

/**
 * <code>InfoInventory</code><br>
 * @author Thomas Behr 16-09-02
 * @version $Revision: 1.1 $
 */

public final class InfoInventory extends JTabbedPane implements Filterable
{
	static final long serialVersionUID = -4186874622211290063L;
	private static final int GEAR_INDEX = 0;
	private static final int EQUIPMENT_INDEX = 1;
	private static final int RESOURCES_INDEX = 2;
	private static final int TEMPMOD_INDEX = 3;
	private static final int NATURAL_INDEX = 4;

	private InfoGear gear = new InfoGear();
	private InfoEquipping equipment = new InfoEquipping();
	private InfoResources resources = new InfoResources();
	private InfoTempMod tempmod = new InfoTempMod();
	private InfoNaturalWeapons naturalWeapons;

	public InfoInventory()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Constants.tabNames[Constants.TAB_INVENTORY]);

		initComponents();
		initActionListeners();

		InfoGear.setNeedsUpdate(true);
	}

	public void setNeedsUpdate(boolean b)
	{
		InfoGear.setNeedsUpdate(b);
		equipment.setNeedsUpdate(b);
		resources.setNeedsUpdate(b);
		tempmod.setNeedsUpdate(b);
		if (SettingsHandler.showNaturalWeaponTab())
		{
			naturalWeapons.setNeedsUpdate(b);
		}
	}

	private void updateCharacterInfo()
	{
		gear.updateCharacterInfo();
		equipment.updateCharacterInfo();
		resources.updateCharacterInfo();
		tempmod.updateCharacterInfo();
		if (SettingsHandler.showNaturalWeaponTab())
		{
			naturalWeapons.updateCharacterInfo();
		}
	}

	private void initComponents()
	{
		add(gear, GEAR_INDEX);
		setTitleAt(GEAR_INDEX, PropertyFactory.getString("in_Info" + gear.getName()));
		add(equipment, EQUIPMENT_INDEX);
		setTitleAt(EQUIPMENT_INDEX, PropertyFactory.getString("in_Info" + equipment.getName()));
		add(resources, RESOURCES_INDEX);
		setTitleAt(RESOURCES_INDEX, PropertyFactory.getString("in_Info" + resources.getName()));
		add(tempmod, TEMPMOD_INDEX);
		setTitleAt(TEMPMOD_INDEX, PropertyFactory.getString("in_Info" + tempmod.getName()));
		if (SettingsHandler.showNaturalWeaponTab())
		{
			naturalWeapons = new InfoNaturalWeapons();
			add(naturalWeapons, NATURAL_INDEX);
			setTitleAt(NATURAL_INDEX, PropertyFactory.getString("in_Info" + naturalWeapons.getName()));
		}
	}

	private void initActionListeners()
	{
		gear.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		equipment.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		resources.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		tempmod.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				updateCharacterInfo();
			}
		});

		addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (getSelectedIndex() == GEAR_INDEX)
				{
					gear.updateCharacterInfo();
				}
				else if (getSelectedIndex() == EQUIPMENT_INDEX)
				{
					equipment.updateCharacterInfo();
				}
				else if (getSelectedIndex() == RESOURCES_INDEX)
				{
					resources.updateCharacterInfo();
				}
				else if (getSelectedIndex() == TEMPMOD_INDEX)
				{
					tempmod.updateCharacterInfo();
				}
			}
		});
	}

	/**
	 * Selector
	 **/
	public final InfoGear getInfoGear()
	{
		return gear;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public List getAvailableFilters()
	{
		return gear.getAvailableFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public List getRemovedFilters()
	{
		return gear.getRemovedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public List getSelectedFilters()
	{
		return gear.getSelectedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public void initializeFilters()
	{
		gear.initializeFilters();

		gear.setKitFilter("GEAR");
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public void refreshFiltering()
	{
		gear.refreshFiltering();
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public boolean isMatchAnyEnabled()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.isMatchAnyEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public boolean isNegateEnabled()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.isNegateEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public int getSelectionMode()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.getSelectionMode();
		}

		return DISABLED_MODE;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public int getFilterMode()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.getFilterMode();
		}

		return MATCH_ALL;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public void setFilterMode(int mode)
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			gear.setFilterMode(mode);
		}
	}
}
