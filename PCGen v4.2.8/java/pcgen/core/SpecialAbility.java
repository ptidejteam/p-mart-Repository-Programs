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

/**
 * <code>SpecialAbility</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class SpecialAbility extends Object implements Serializable, Comparable
{
	private String name = "";
	private String source = "";
	private String desc = "";

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
		source = argSource;
	}

	public SpecialAbility(String argName, String argSource, String argDesc)
	{
		name = argName;
		source = argSource;
		desc = argDesc;
	}

	public int compareTo(Object obj)
	{

		if (obj instanceof SpecialAbility)
		{
			if (name.equals(obj.toString()))
			{
				return source.compareTo(((SpecialAbility) obj).source);
			}
		}
		return this.name.toUpperCase().compareTo(obj.toString().toUpperCase());
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
	}

	String getDesc()
	{
		return desc;
	}

	public void setDesc(String aString)
	{
		desc = aString;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String aString)
	{
		source = aString;
	}

	boolean pcQualifiesFor(PlayerCharacter aPC)
	{
		if ("".equals(source) || source.endsWith("|0"))
		{
			return true;
		}
		// currently source is either empty or
		// PCCLASS|classname|classlevel (means it's a chosen special ability)
		// PCCLASS=classname|classlevel (means it's a defined special ability)
		// DEITY=deityname|totallevels
		final StringTokenizer aTok = new StringTokenizer(source, "|=", false);
		final String aString = aTok.nextToken();
		final String aName = aTok.nextToken();
		PCClass aClass;
		final int anInt;
		try
		{
			anInt = Integer.parseInt(aTok.nextToken());
		}
		catch (Exception exc)
		{
			Globals.errorPrint("pcQualifiesFor:" + source, exc);
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
		if ("".equals(source))
		{
			return;
		}

		try
		{
			final StringTokenizer aTok = new StringTokenizer(source, "|=", false);
			final String typeString = aTok.nextToken();
			final String className = aTok.nextToken();
			final String levelString = aTok.nextToken();

			if (className.equals(oldClass))
			{
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("Source class changed from " + oldClass + " to " + newClass + " for " + name);
				}
				setSource(typeString + "=" + newClass + "|" + levelString);
			}
		}
		catch (Exception exc)
		{
			Globals.errorPrint("setQualificationClass:" + source, exc);
		}
	}
}
