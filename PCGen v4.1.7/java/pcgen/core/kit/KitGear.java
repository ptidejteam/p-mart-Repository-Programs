/*
 * KitGear.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 8:58 PM
 *
 * $Id: KitGear.java,v 1.1 2006/02/21 00:57:51 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <code>KitGear</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class KitGear implements Serializable
{
	private String name = "";
	private int qty = 1;
	private int maxCost = 0;
	private ArrayList eqMods = null;
	private ArrayList prereqs = null;

	private static final long serialVersionUID = 1;

	public KitGear(String gearName)
	{
		name = gearName;
	}

	public String toString()
	{
		int maxSize = 0;
		if (eqMods != null)
			maxSize = eqMods.size();
		final StringBuffer info = new StringBuffer(maxSize * 5);
		if (qty != 1)
		{
			info.append(qty).append('x');
		}
		info.append(name);
		if (maxSize > 0)
		{
			info.append(" (");
			for (int i = 0; i < maxSize; ++i)
			{
				if (i != 0)
				{
					info.append('/');
				}
				info.append((String) eqMods.get(i));
			}
			info.append(')');
		}
		return info.toString();
	}

	private static void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

	public String getName()
	{
		return name;
	}

	public void setQty(String argQty)
	{
		try
		{
			qty = Integer.parseInt(argQty);
		}
		catch (Exception exc)
		{
			System.err.println("Invalid qty \"" + argQty + "\" in KitGear.setQty");
		}
	}

	public int getQty()
	{
		return qty;
	}

	public int getMaxCost()
	{
		return maxCost;
	}

	public void setMaxCost(String argMaxCost)
	{
		try
		{
			maxCost = Integer.parseInt(argMaxCost);
		}
		catch (Exception exc)
		{
			System.err.println("Invalid max cost \"" + argMaxCost + "\" in KitGear.setMaxCost");
		}
	}

	public void addEqMod(String argEqMod)
	{
		if (eqMods == null)
		{
			eqMods = new ArrayList();
		}
		eqMods.add(argEqMod);
	}

	public ArrayList getEqMods()
	{
		return eqMods;
	}

	public void addPrereq(String argPrereq)
	{
		if (prereqs == null)
		{
			prereqs = new ArrayList();
		}
		prereqs.add(argPrereq);
	}

	public ArrayList getPrereqs()
	{
		return prereqs;
	}
}
