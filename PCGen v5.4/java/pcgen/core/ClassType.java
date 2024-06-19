/*
 * Campaign.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:39 $
 */

package pcgen.core;

import pcgen.gui.utils.GuiFacade;

/**
 * <code>Campaign</code>.
 *
 * @author Felipe Diniz <fdiniz@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class ClassType extends PObject
{
	private boolean isMonster = false;
//	private boolean isPrestige = false;
	private String CRFormula = "";
	private boolean XPPenalty = true;

	public Object clone()
	{
		ClassType newClassType = null;
		try
		{
			newClassType = (ClassType) super.clone();
			newClassType.isMonster = isMonster;
//			newClassType.isPrestige = isPrestige;
			newClassType.CRFormula = new String(CRFormula);
			newClassType.XPPenalty = XPPenalty;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return newClassType;
	}

	public boolean isMonster()
	{
		return isMonster;
	}

	public void setIsMonster(boolean monster)
	{
		isMonster = monster;
	}

//	public boolean isPrestige()
//	{
//		return isPrestige;
//	}

//	public void setIsPrestige(boolean prestige)
//	{
//		isPrestige = prestige;
//	}

	public String getCRFormula()
	{
		return CRFormula;
	}

	public void setCRFormula(String argCRFormula)
	{
		CRFormula = argCRFormula;
	}

	public boolean getXPPenalty()
	{
		return XPPenalty;
	}

	public void setXPPenalty(boolean argXPPenalty)
	{
		XPPenalty = argXPPenalty;
	}
}
