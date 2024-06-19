/*
 * Deity.java
 * Copyright 2001 (C) Bryan McRoberts (mocha@mcs.net)
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * <code>Deity</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Deity extends PObject
{
	private ArrayList domainList = new ArrayList();
	private String alignments = "";
	private String description = "";
	private String holyItem = "";
	private String favoredWeapon = "ALL";
	private ArrayList pantheonList = new ArrayList();
	private ArrayList raceList = new ArrayList();
	private String alignment = "";
	private String specialAbility = "";
	private boolean d_allDomains = false;

	/**
	 * @return the name of the deity this object represents
	 */
	public String toString()
	{
		return name;
	}

	/**
	 * @return an ArrayList of the domains this deity has
	 */
	public ArrayList getDomainList()
	{
		return domainList;
	}

	/**
	 * Awfully similar to unSplit
	 * @return a comma-separated string of the domains this deity has
	 */
	public String domainListString()
	{
		StringBuffer aString = new StringBuffer(domainList.size() * 10);
		for (Iterator e = domainList.iterator(); e.hasNext();)
		{
			if (aString.length() > 0) aString.append(',');
			aString.append((String)e.next());
		}
		return aString.toString();
	}

	/**
	 * @return true if the deity has the passed-in domain
	 * @param aDomain
	 */
	public boolean hasDomainNamed(String aDomain)
	{
		return d_allDomains || getDomainList().contains(aDomain);
	}

	public void setDomainList(String aDomainList)
	{
		final String delim = ",";
		final ArrayList dList = getDomainList();
		final StringTokenizer colToken = new StringTokenizer(aDomainList, delim, false);
		dList.clear();             // remove all previous contents
		d_allDomains = false;
		dList.ensureCapacity(colToken.countTokens());
		while (colToken.hasMoreTokens())
		{
			final String nextTok = colToken.nextToken();
			dList.add(nextTok);
			d_allDomains = d_allDomains || nextTok.equals("ALL");
		}
	}

	/**
	 * @return a comma-separated String of the alignments this deity can accept
	 */
	public String getAlignments()
	{
		return alignments;
	}

	/**
	 * @return this deity's alignment
	 */
	public String getAlignment()
	{
		return alignment;
	}

	public void setAlignments(String aString)
	{
		alignments = aString;
	}

	public void setAlignment(String alignment)
	{
		this.alignment = alignment;
	}

	/**
	 * @return true if this deity allows worshippers of the passed in alignment
	 * @param index An integer representation of an alignment
	 */
	public boolean allowsAlignment(int index)
	{
		final boolean flag = alignments.lastIndexOf(String.valueOf(index)) > -1;
		Globals.debugPrint("AL=" + flag);
		return flag;
	}

	final public boolean acceptableClass(Iterator classList)
	{
		boolean flag = (!classList.hasNext());
		while (classList.hasNext() && flag == false)
		{
			final PCClass aClass = (PCClass)classList.next();
			final String deityString = aClass.getDeityString();
			if (deityString.length() > 0)
			{
				if (deityString.equals("ANY") || deityString.equals("ALL"))
					flag = true;
				final StringTokenizer aTok = new StringTokenizer(deityString, "|", false);
				while (aTok.hasMoreTokens())
				{
					if (getName().equals(aTok.nextToken()))
					{
						flag = true;
					}
				}
			}
		}
		Globals.debugPrint("CLASS=" + flag);
		return flag;
	}

	public final boolean acceptablePreReqs()
	{
		return passesPreReqTests();
	}

	/**

	 * Check whether this deity can be selected by a character with the

	 * given classes, alignment, race and gender.

	 *

	 * @param classList an arraylist of PCClass objects.

	 * @param anAlignment 0 through 8 inclusive

	 * @param raceName  the name of the characters race.

	 * @param gender    M or F.

	 * @return <code>true</code> means the deity can be a selected by a

	 * character with the given properties; <code>false</code> means the

	 * character cannot.

	 */
	public boolean canBeSelectedBy(ArrayList classList,
		int anAlignment,
		String raceName,
		String gender)
	{
		boolean result = false;
		Globals.debugPrint("DEITY=" + name);
		try
		{
			result =
				acceptableClass(classList.iterator()) &&
				allowsAlignment(anAlignment) &&
				acceptablePreReqs();
		}
		catch (NumberFormatException nfe)
		{
			result = false;
		}
		return result;
	}

	/**
	 * @return a description of this deity
	 */
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String aDescription)
	{
		description = aDescription;
	}

	/**
	 * @return the name of the holy item of this deity
	 */
	public String getHolyItem()
	{
		return holyItem;
	}

	public void setHolyItem(String aString)
	{
		holyItem = aString;
	}

	/**
	 * @return the name of the favored weapon of this deity
	 */
	public String getFavoredWeapon()
	{
		return favoredWeapon;
	}

	public void setFavoredWeapon(String aString)
	{
		favoredWeapon = aString;
	}

	public ArrayList getRaceList()
	{
		return raceList;
	}

	public void setRaceList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		raceList.clear();           // remove previous contents
                String tmp;
		while (aTok.hasMoreTokens()) {
			raceList.add(aTok.nextToken());
                }
	}

	/**
	 * @return the list of pantheons this deity belongs to
	 */
	public ArrayList getPantheonList()
	{
		return pantheonList;
	}

	public void setPantheonList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		pantheonList.clear();           // remove previous contents
                String tmp;
		while (aTok.hasMoreTokens()) {
                        tmp = aTok.nextToken().trim();
			pantheonList.add(tmp);
                        Globals.getPantheons().add(tmp);
                }
	}


	/**
	 * @return the special ability granted by this deity
	 */
	public final String getSpecialAbility()
	{
		return specialAbility;
	}

	public void setSpecialAbility(String specialAbility)
	{
		this.specialAbility = specialAbility;
	}

	public void addSpecialAbility(String specialAbility)
	{
		this.specialAbility += specialAbility;
	}

	public String preReqHTMLStrings(boolean includeHeader)
	{
		ArrayList prereqs = new ArrayList(preReqArrayList);
		prereqs.add("PREALIGN:" + Utility.commaDelimit(alignments));
		return preReqHTMLStringsForList(null, prereqs, includeHeader);
	}
}

