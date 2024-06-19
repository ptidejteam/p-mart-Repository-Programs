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
	private String followerAlignments = "";
	private String description = Constants.s_NONE;
	private String holyItem = Constants.s_NONE;
	private String favoredWeapon = "";
	private ArrayList domainList = new ArrayList();
	private ArrayList pantheonList = new ArrayList();
	private ArrayList raceList = new ArrayList();
	private String deityAlignment = "TN";
	private boolean d_allDomains = false;

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Object clone()
	{
		Deity d = (Deity)super.clone();

		d.domainList = new ArrayList(domainList);
		d.pantheonList = new ArrayList(pantheonList);
		d.raceList = new ArrayList(raceList);
		return d;
	}

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
	 * @return a comma-separated string of the domains this deity has
	 */
	public String domainListString()
	{
		return Utility.unSplit(domainList, ",");
	}

	/**
	 * @return a comma-separated string of the PI-formatted domains this deity has
	 */
	public String domainListPIString()
	{
		Iterator iter = domainList.iterator();
		StringBuffer piString = new StringBuffer(100);

		while (iter.hasNext())
		{
			String domainName = (String)iter.next();
			final Domain aDomain = Globals.getDomainNamed(domainName);
			if (aDomain != null)
			{
				domainName = aDomain.piSubString();
			}
			else if (domainName.equals("ALL") || domainName.equals("ANY"))
			{
			}
			else
			{
				continue;
			}

			if (piString.length() != 0)
			{
				piString.append(',');
			}
			piString.append(domainName);
		}

		return "<html>" + piString.toString() + "</html>";
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
			d_allDomains |= nextTok.equals("ALL") | nextTok.equals("ANY");
		}
	}

	/**
	 * @return a comma-separated String of the alignments this deity can accept
	 */
	public String getFollowerAlignments()
	{
		return followerAlignments;
	}

	/**
	 * @return this deity's alignment
	 */
	public String getDeityAlignment()
	{
		return deityAlignment;
	}

	public void setFollowerAlignments(String aString)
	{
		followerAlignments = aString;
	}

	public void setDeityAlignment(String aString)
	{
		deityAlignment = aString;
	}

	/**
	 * @return true if this deity allows worshippers of the passed in alignment
	 * @param index An integer representation of an alignment
	 */
	public boolean allowsAlignment(int index)
	{
		//[VARDEFINED=SuneLG=0]369
		String aligns = followerAlignments;
		for (; ;)
		{
			int idxStart = aligns.indexOf('[');
			if (idxStart < 0)
			{
				break;
			}
			int idxEnd = aligns.indexOf(']', idxStart);
			if (idxEnd < 0)
			{
				break;
			}

			final String subPre = aligns.substring(idxStart + 1, idxEnd);
			final StringTokenizer pTok = new StringTokenizer(subPre, "=", false);
			if (pTok.countTokens() != 3)
			{
				break;
			}
			final String cond = pTok.nextToken();
			final String vName = pTok.nextToken();
			final String condAlignment = pTok.nextToken();
			boolean hasCond = false;
			if (cond.equals("VARDEFINED"))
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				if ((aPC != null) && aPC.hasVariable(vName))
				{
					hasCond = true;
				}
			}

			if (hasCond)
			{
				aligns = aligns.substring(0, idxStart) + condAlignment + aligns.substring(idxEnd + 1);
			}
			else
			{
				aligns = aligns.substring(0, idxStart) + aligns.substring(idxEnd + 1);
			}
		}

		final boolean flag = aligns.lastIndexOf(String.valueOf(index)) > -1;
		Globals.debugPrint("AL=", flag);
		return flag;
	}

	private final boolean acceptableClass(Iterator classList)
	{
		boolean flag = (!classList.hasNext());
		while (classList.hasNext() && !flag)
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
		Globals.debugPrint("CLASS=", flag);
		return flag;
	}

	private final boolean acceptablePreReqs()
	{
		return passesPreReqTests();
	}

	/**
	 * Check whether this deity can be selected by a character with the
	 * given classes, alignment, race and gender.
	 *
	 * @param classList an arraylist of PCClass objects.
	 * @param anAlignment 0 through 8 inclusive
	 * @return <code>true</code> means the deity can be a selected by a
	 * character with the given properties; <code>false</code> means the
	 * character cannot.
	 */
	public boolean canBeSelectedBy(ArrayList classList, int anAlignment)
	{
		boolean result = false;
		Globals.debugPrint("DEITY=", name);
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
		while (aTok.hasMoreTokens())
		{
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
		while (aTok.hasMoreTokens())
		{
			tmp = aTok.nextToken().trim();
			pantheonList.add(tmp);
			Globals.getPantheons().add(tmp);
		}
	}

	public String preReqHTMLStrings(boolean includeHeader)
	{
		ArrayList prereqs = new ArrayList();
		addPreReqTo(prereqs);

		boolean bsubPreReq = false;
		StringBuffer preReqString = new StringBuffer(20);
		final StringTokenizer aTok = new StringTokenizer(followerAlignments, "[]", true);
		while (aTok.hasMoreTokens())
		{
			String preReq = aTok.nextToken();
			if (preReq.equals("["))
			{
				bsubPreReq = true;
			}
			else if (preReq.equals("]"))
			{
				bsubPreReq = false;
				preReqString.append(preReq);
			}
			else
			{
				if (preReqString.length() != 0)
				{
					preReqString.append(',');
				}
				if (bsubPreReq)
				{
					preReqString.append('[').append(preReq);
				}
				else
				{
					preReqString.append(Utility.commaDelimit(preReq));
				}
			}
		}

		prereqs.add("PREALIGN:" + preReqString.toString());
		return preReqHTMLStringsForList(null, prereqs, includeHeader);
	}

	public void setSpecialAbilityList(String aString, int anInt)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		if (specialAbilityList == null)
			specialAbilityList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			if (bString.equals(".CLEAR"))
			{
				specialAbilityList.clear();
				continue;
			}
			final StringTokenizer bTok = new StringTokenizer(bString, ":", false);
			String dString = bTok.nextToken(); //Throw away token
			if (bTok.hasMoreTokens())
				dString = bTok.nextToken();
			SpecialAbility sa = new SpecialAbility(bString, "DEITY=" + name + "|" + dString);
			addSpecialAbilityToList(sa);
		}
	}

	public ArrayList addSpecialAbilitiesToList(ArrayList aList)
	{
		if (specialAbilityList == null || specialAbilityList.isEmpty())
		{
			return aList;
		}
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			final SpecialAbility sa = (SpecialAbility)i.next();
			if (sa.PCQualifiesFor(Globals.getCurrentPC()))
			{
				aList.add(sa);
			}
		}
		return aList;
	}

	public String getPCCText()
	{
		StringBuffer txt = new StringBuffer(200);
		txt.append(getName());
		if (domainList.size() != 0)
		{
			txt.append("\tDOMAINS:").append(Utility.unSplit(domainList, ","));
		}
		if (followerAlignments.length() != 0)
		{
			txt.append("\tFOLLOWERALIGN:").append(followerAlignments);
		}
		if (description.length() != 0)
		{
			txt.append("\tDESC:").append(description);
		}
		if (holyItem.length() != 0)
		{
			txt.append("\tSYMBOL:").append(holyItem);
		}
		if (favoredWeapon.length() != 0)
		{
			txt.append("\tDEITYWEAP:").append(favoredWeapon);
		}
		txt.append("\tALIGN:").append(deityAlignment);
		if (pantheonList.size() != 0)
		{
			txt.append("\tPANTHEON:").append(Utility.unSplit(pantheonList, "|"));
		}
		if (raceList.size() != 0)
		{
			txt.append("\tRACE:").append(Utility.unSplit(raceList, "|"));
		}
		txt.append(super.getPCCText(false));
		return txt.toString();
	}
}

