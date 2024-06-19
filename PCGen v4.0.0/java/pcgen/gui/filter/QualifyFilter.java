/*
 * QualifyFilter.java
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;

/**
 * <code>QualifyFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

class QualifyFilter extends AbstractPObjectFilter
{
	public QualifyFilter()
	{
		super("Miscellaneous", "Qualify");
	}

	public String getDescription()
	{
		return "Accept object if " + Globals.getCurrentPC().getName() + " qualifies for object.";
	}

	public boolean accept(PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			return Globals.getCurrentPC().canSelectDeity((Deity)pObject);
		}
		else if (pObject instanceof Domain)
		{
			return ((Domain)pObject).qualifiesForDomain();
		}
		else if (pObject instanceof Equipment)
		{
			final Equipment equip = (Equipment)pObject;
			boolean accept = equip.passesPreReqTests();
			if (accept && (equip.isType("Shield") || equip.isType("Weapon") || equip.isType("Armor")))
				return Globals.getCurrentPC().isProficientWith(equip);
			return accept;
		}
		else if (pObject instanceof Feat)
		{
			return ((Feat)pObject).passesPreReqTests();
		}
		else if (pObject instanceof PCClass)
		{
			return ((PCClass)pObject).isQualified();
		}
		else if (pObject instanceof PCTemplate)
		{
			return ((PCTemplate)pObject).isQualified();
		}
		else if (pObject instanceof Race)
		{
			return ((Race)pObject).passesPreReqTests();
		}

		return true;
	}
}
