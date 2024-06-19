/*
 * BasePanel.java
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
 * Created on November 5, 2002, 4:29 PM
 *
 * @(#) $Id: BasePanel.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

/**
 * <code>BasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui.editor;

import java.util.ArrayList;
import javax.swing.JPanel;

class BasePanel extends JPanel
{

	public void setTypesAvailableList(final ArrayList aList, final boolean sort)
	{
	}

	public void setHolyItemText(final String aString)
	{
	}

	public String getHolyItemText()
	{
		return null;
	}

	public void setDescriptionText(final String aString)
	{
	}

	public String getDescriptionText()
	{
		return null;
	}

	public void setDeityAlignment(final String aString)
	{
	}

	public String getDeityAlignment()
	{
		return null;
	}

	public void setFavoredWeaponsAvailableList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getFavoredWeaponsAvailableList()
	{
		return null;
	}

	public void setFavoredWeaponsSelectedList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getFavoredWeaponsSelectedList()
	{
		return null;
	}

	public void setTypesSelectedList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getTypesSelectedList()
	{
		return null;
	}

	public void setIsUntrained(final boolean isUntrained)
	{
	}

	public boolean getIsUntrained()
	{
		return false;
	}

	public void setIsExclusive(final boolean isExclusive)
	{
	}

	public boolean getIsExclusive()
	{
		return false;
	}

	public void setKeyStat(final String aString)
	{
	}

	public String getKeyStat()
	{
		return null;
	}

	public void setArmorCheck(final int aCheck)
	{
	}

	public int getArmorCheck()
	{
		return pcgen.core.Skill.ACHECK_NONE;
	}
}
