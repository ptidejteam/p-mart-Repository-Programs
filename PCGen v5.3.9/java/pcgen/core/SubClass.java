/*
 * SubClass.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 19, 2002, 10:29 PM
 *
 * $Id: SubClass.java,v 1.1 2006/02/21 01:16:13 vauchers Exp $
 */

package pcgen.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.Logging;

/**
 * <code>SubClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class SubClass extends PCClass
{
	private int cost = 0;
	private String choice = null;
	private List levelArray = null;

	public SubClass()
	{
		numSpellsFromSpecialty = 0;
		spellBaseStat = null;
	}

	public void setCost(int arg)
	{
		cost = arg;
	}

	public int getCost()
	{
		return cost;
	}

	public void setChoice(String arg)
	{
		choice = arg;
	}

	public String getChoice()
	{
		if (choice == null)
		{
			return "";
		}
		return choice;
	}

	public void addToLevelArray(String arg)
	{
		if (levelArray == null)
		{
			levelArray = new ArrayList();
		}
		levelArray.add(arg);
	}

	public void applyLevelArrayModsTo(PCClass aClass)
	{
		if (levelArray == null)
		{
			return;
		}
		try
		{
			URL aURL = new URL(aClass.getSourceFile());
			for (Iterator i = levelArray.iterator(); i.hasNext();)
			{
				String aLine = (String) i.next();
				PCClassLoader.parseLine(aClass, aLine, aURL, -1);
			}
		}
		catch (MalformedURLException exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
		catch (PersistenceLayerException exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
	}
}
