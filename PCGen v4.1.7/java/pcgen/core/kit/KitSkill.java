/*
 * KitSkill.java
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
 * Created on September 23, 2002, 10:28 PM
 *
 * $Id: KitSkill.java,v 1.1 2006/02/21 00:57:51 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <code>KitSkill</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class KitSkill implements Serializable
{
	private String skillName = "";
	private double rank = 1.0;
	private ArrayList prereqs = null;
	private boolean free = false;
	private static final long serialVersionUID = 1;

	public KitSkill(String argSkill)
	{
		skillName = argSkill;
	}

	private static void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

	public String toString()
	{
		final StringBuffer info = new StringBuffer(100);
		info.append(skillName).append(" (").append(rank);

		if (info.toString().endsWith(".0"))
		{
			info.setLength(info.length() - 2);
		}

		if (free)
		{
			info.append("/free");
		}
		info.append(')');

		return info.toString();
	}

	public String getSkillName()
	{
		return skillName;
	}

	public double getRank()
	{
		return rank;
	}

	public void setRank(String argRank)
	{
		try
		{
			rank = Double.parseDouble(argRank);
		}
		catch (Exception exc)
		{
			System.err.println("Invalid rank \"" + argRank + "\" in KitSkill.setRank");
		}
	}

	public boolean isFree()
	{
		return free;
	}

	public void setFree(boolean argFree)
	{
		free = argFree;
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
