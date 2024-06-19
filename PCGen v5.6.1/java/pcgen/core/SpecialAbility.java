/*
 * SpecialAbility.java
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
 */

package pcgen.core;

// Migration to XML resources
//import pcgen.xml.FindXML;
//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeIterator;

import java.io.Serializable;
import java.util.StringTokenizer;
import pcgen.util.Logging;

/**
 * <code>SpecialAbility</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class SpecialAbility extends PObject implements Serializable, Comparable
{
//	private String name = "";
	private String SAsource = "";
	private String SAdesc = "";

	public SpecialAbility()
	{
	}

	public SpecialAbility(String argName)
	{
		name = argName;
	}

	public SpecialAbility(String argName, String argSource)
	{
		name = argName;
		SAsource=argSource;
	}

	public SpecialAbility(String argName, String argSource, String argDesc)
	{
		name = argName;
		SAsource = argSource;
		SAdesc = argDesc;
	}

	public int compareTo(Object obj)
	{

		if (obj instanceof SpecialAbility)
		{
			if (name.equals(obj.toString()))
			{
				return SAsource.compareToIgnoreCase(((SpecialAbility) obj).SAsource);
			}
		}
		return this.name.compareToIgnoreCase(obj.toString());
	}

	public String toString()
	{
		return name;
	}

	String getSADesc()
	{
		return SAdesc;
	}

	public void setSADesc(String aString)
	{
		SAdesc = aString;
	}

	public String getSASource()
	{
		return SAsource;
	}

	public void setSASource(String aString)
	{
		SAsource = aString;
	}

	boolean pcQualifiesFor(PlayerCharacter aPC)
	{
		if ("".equals(SAsource) || SAsource.endsWith("|0"))
		{
			return true;
		}
		if (!passesPreReqToGain())
		{
			return false;
		}
		// currently source is either empty or
		// PCCLASS|classname|classlevel (means it's a chosen special ability)
		// PCCLASS=classname|classlevel (means it's a defined special ability)
		// DEITY=deityname|totallevels
		final StringTokenizer aTok = new StringTokenizer(SAsource, "|=", false);
		final String aString = aTok.nextToken();
		final String aName = aTok.nextToken();
		PCClass aClass;
		final int anInt;
		try
		{
			anInt = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("pcQualifiesFor:" + SAsource, exc);
			return false;
		}

		if ("PCCLASS".equals(aString))
		{
			aClass = aPC.getClassNamed(aName);
			if (aClass == null)
			{
				return false;
			}
			return (aClass.getLevel() >= anInt);
		}
		return aPC.getTotalLevels() >= anInt;
	}

	/** Set the class that is used to determine if a character should have this special
	 * ability
	 * @param oldClass The name of the original class
	 * @param newClass The name of the new class that is
	 * to be used for this special ability
	 */
	public void setQualificationClass(String oldClass, String newClass)
	{
		if ("".equals(SAsource))
		{
			return;
		}

		try
		{
			final StringTokenizer aTok = new StringTokenizer(SAsource, "|=", false);
			final String typeString = aTok.nextToken();
			final String className = aTok.nextToken();
			final String levelString = aTok.nextToken();

			if (className.equals(oldClass))
			{
				Logging.errorPrint("Source class changed from " + oldClass + " to " + newClass + " for " + name);

				setSASource(typeString + "=" + newClass + "|" + levelString);
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint("setQualificationClass:" + SAsource, exc);
		}
	}
}
