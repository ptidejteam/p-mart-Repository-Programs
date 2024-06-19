/*
 * InfoGear.java
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
 */

package pcgen.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pcgen.gui.filter.Filterable;

/**
 * <code>InfoGear</code><br>
 * @author Thomas Behr 16-09-02
 * @version $Revision: 1.1 $
 */

final class InfoGear extends JTabbedPane implements Filterable
{
	private static final int INVENTORY_INDEX = 0;
	private static final int EQUIPMENT_INDEX = 1;
	private static final int RESOURCES_INDEX = 2;

	private InfoInventory inventory = new InfoInventory();
	private InfoEquipping equipment = new InfoEquipping();
	private InfoResources resources = new InfoResources();

	InfoGear()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Gear");

		initComponents();
		initActionListeners();

		inventory.setNeedsUpdate(true);
	}

	public void setNeedsUpdate(boolean b)
	{
		inventory.setNeedsUpdate(b);
		equipment.setNeedsUpdate(b);
		resources.setNeedsUpdate(b);
	}

	private void updateCharacterInfo()
	{
		inventory.updateCharacterInfo();
		equipment.updateCharacterInfo();
		resources.updateCharacterInfo();
	}

	private void initComponents()
	{
		add(inventory, INVENTORY_INDEX);
		setTitleAt(INVENTORY_INDEX, getName());
		add(equipment, EQUIPMENT_INDEX);
		setTitleAt(EQUIPMENT_INDEX, equipment.getName());
		add(resources, RESOURCES_INDEX);
		setTitleAt(RESOURCES_INDEX, resources.getName());
	}

	private void initActionListeners()
	{
		inventory.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		equipment.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		resources.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());

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
				if (getSelectedIndex() == INVENTORY_INDEX)
				{
					inventory.updateCharacterInfo();
				}
				else if (getSelectedIndex() == EQUIPMENT_INDEX)
				{
					equipment.setNeedsUpdate(true);
					equipment.display();
				}
				else if (getSelectedIndex() == RESOURCES_INDEX)
				{
					resources.setNeedsUpdate(true);
					resources.display();
				}
			}
		});
	}

	/**
	 * Selector
	 */
      final InfoInventory getInfoInventory()
	{
		return inventory;
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public List getAvailableFilters()
	{
		return inventory.getAvailableFilters();
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public List getRemovedFilters()
	{
		return inventory.getRemovedFilters();
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public List getSelectedFilters()
	{
		return inventory.getSelectedFilters();
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public void initializeFilters()
	{
		inventory.initializeFilters();

		inventory.setKitFilter("GEAR");
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public void refreshFiltering()
	{
		inventory.refreshFiltering();
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public boolean isMatchAnyEnabled()
	{
		if (getSelectedIndex() == INVENTORY_INDEX)
		{
			return inventory.isMatchAnyEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public boolean isNegateEnabled()
	{
		if (getSelectedIndex() == INVENTORY_INDEX)
		{
			return inventory.isNegateEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public int getSelectionMode()
	{
		if (getSelectedIndex() == INVENTORY_INDEX)
		{
			return inventory.getSelectionMode();
		}

		return DISABLED_MODE;
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public int getFilterMode()
	{
		if (getSelectedIndex() == INVENTORY_INDEX)
		{
			return inventory.getFilterMode();
		}

		return MATCH_ALL;
	}

	/**
	 * delegates filter related stuff to inventory tab
	 */
	public void setFilterMode(int mode)
	{
		if (getSelectedIndex() == INVENTORY_INDEX)
		{
			inventory.setFilterMode(mode);
		}
	}
}