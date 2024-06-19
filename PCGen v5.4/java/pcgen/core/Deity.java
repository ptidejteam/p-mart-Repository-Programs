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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:39 $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;

/**
 * <code>Deity</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Deity extends PObject
{
	private String followerAlignments = "";
	private String holyItem = Constants.s_NONE;
	private String favoredWeapon = "";
	private String alignment = ""; // was TN
	private List domainList = null;
	private String domainListString = null;
	private String domainListPIString = null;

	private List pantheonList = new ArrayList();
	//private String pantheonListString = null;

	private List raceList = new ArrayList();
	private boolean d_allDomains = false;

	/**
	 * Deity Constructor.
	 */
	public Deity()
	{
		super();
		buildDomainList();
	}

	/**
	 * Clones a Deity object
	 *
	 * @return A clone of the Deity object.
	 */
	public Object clone()
	{
		Deity d = null;
		try
		{
			d = (Deity) super.clone();
			d.domainList = new ArrayList(getDomainList());
			d.pantheonList = new ArrayList(pantheonList);
			d.raceList = new ArrayList(raceList);
			d.domainListString = domainListString;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return d;
	}

	/**
	 * @return a List of the domains this deity has
	 */
	public List getDomainList()
	{
		return domainList;
	}

	/**
	 * This method builds the contents of the domain list from the
	 * domain list String.
	 */
	private void buildDomainList()
	{
		if ((domainList == null) || (domainList.size() == 0))
		{

			if ((domainListString == null) || (domainListString.length() < 1))
			{
				domainList = new ArrayList();
				return;
			}

			// test for "all domains"
			if (domainListString.indexOf("ALL") + domainListString.indexOf("ANY") > -2)
			{
				d_allDomains = true;
				domainList = new ArrayList(Globals.getDomainList());
			}
			else
			{
				// create the domain list
				if (domainList == null)
				{
					domainList = new ArrayList();
				}

				final StringTokenizer colToken = new StringTokenizer(domainListString, ",", false);

				while (colToken.hasMoreTokens())
				{
					final String nextTok = colToken.nextToken().trim();
					if (nextTok.length() > 0)
					{
						Domain aDomain = Globals.getDomainKeyed(nextTok);
						if (aDomain != null)
						{
							domainList.add(aDomain);
						}
					}
				} // end while( more tokens )
			} // end else
		} // end null or empty check
	}

	/**
	 * @return a comma-separated string of the PI-formatted domains this deity has
	 */
	public String getDomainListPIString()
	{
		if (domainListPIString == null)
		{
			// In order to be lazy, we need to make sure we're
			// safely creating only one instance of the string.
			// This requires synchronization.
			//TODO: Are you sure this works? See http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
			synchronized (this)
			{
				if (domainListPIString == null)
				{

					final StringBuffer piString = new StringBuffer(100);
					piString.append("<html>");

					// Shortcut for all domains
					if (d_allDomains)
					{
						piString.append("ALL");
					}
					// Build string of domains separated by commas
					else
					{
						final Iterator iter = getDomainList().iterator();
						boolean started = false;
						while (iter.hasNext())
						{
							final Domain aDomain = (Domain) iter.next();
							if (aDomain != null)
							{
								if (started)
								{
									piString.append(',');
								}
								else
								{
									started = piString.length() > 0;
								}
								piString.append(aDomain.piSubString());
							}
						}
					}
					piString.append("</html>");

					domainListPIString = piString.toString();

				} // end of double-locking
			} // end of synchronized block
		} // end of null-check
		return domainListPIString;
	}

	/**
	 * @param domainName
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomainNamed(String domainName)
	{
		Domain testDomain = Globals.getDomainNamed(domainName);
		return hasDomain(testDomain);
	}

	/**
	 * @param aDomain
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomain(Domain aDomain)
	{
		return d_allDomains || getDomainList().contains(aDomain);
	}

	/**
	 * This method is called from I/O routines to pass the deity
	 * a delimited string of domain names that this deity has.
	 * This method should ONLY be called from I/O!
	 * @param aDomainList String list of domains delimited by
	 * commas
	 */
	public void setDomainListString(String aDomainList)
	{
		domainListString = aDomainList;
		domainListPIString = null;
		d_allDomains = false;
		if (domainList != null)
		{
			domainList.clear();
		}
		buildDomainList();
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
	public String getAlignment()
	{
		return alignment;
	}

	/**
	 * This method sets the string containing the numeric
	 * alignments accepted by this deity.
	 * @param alignmentString String containing the numeric alignments (with no spaces
	 * or other delimiters, i.e. 3678).
	 */
	public void setFollowerAlignments(String alignmentString)
	{
		followerAlignments = alignmentString;
	}

	/**
	 * This method sets the deity's alignment
	 * @param aString String containing the short abbreviation for the
	 * deity's alignment
	 */
	public void setAlignment(String aString)
	{
		alignment = aString;
	}

	/**
	 * @param index An integer representation of an alignment
	 * @return true if this deity allows worshippers of the passed in alignment
	 */
	private boolean allowsAlignment(int index)
	{
		//[VARDEFINED=SuneLG=0]367
		String aligns = followerAlignments;
		for (; ;)
		{
			final int idxStart = aligns.indexOf('[');
			if (idxStart < 0)
			{
				break;
			}
			final int idxEnd = aligns.indexOf(']', idxStart);
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
			if ("VARDEFINED".equals(cond))
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
		return flag;
	}

	/**
	 * This method determines whether any of the classes that a character
	 * has is acceptable to this deity.
	 * @param classList Iterator pointing to the Collection of classes the
	 * character has
	 * @return boolean
	 */
	private boolean acceptableClass(Iterator classList)
	{
		boolean flag = (!classList.hasNext());
		while (classList.hasNext() && !flag)
		{
			final PCClass aClass = (PCClass) classList.next();
			final String deityString = aClass.getDeityString();
			if (deityString != null && deityString.length() > 0)
			{
				if ("ANY".equals(deityString) || "ALL".equals(deityString))
				{
					flag = true;
				}
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
		return flag;
	}

	/**
	 * Check whether this deity can be selected by a character with the
	 * given classes, alignment, race and gender.
	 *
	 * @param classList a list of PCClass objects.
	 * @param anAlignment 0 through 8 inclusive
	 * @return <code>true</code> means the deity can be a selected by a
	 * character with the given properties; <code>false</code> means the
	 * character cannot.
	 */
	public boolean canBeSelectedBy(List classList, int anAlignment)
	{
		boolean result;
		try
		{
			result = acceptableClass(classList.iterator()) && allowsAlignment(anAlignment) && passesPreReqToGain();
		}
		catch (NumberFormatException nfe)
		{
			result = false;
		}
		return result;
	}

	/**
	 * @return the name of the holy item of this deity
	 */
	public String getHolyItem()
	{
		return holyItem;
	}

	/**
	 * This method sets the holy weapon of this deity.
	 * @param aString String name of the holy weapon of this deity.
	 */
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

	/**
	 * This method sets the favored weapon of this deity.
	 * @param aString String favored weapon of this deity.
	 */
	public void setFavoredWeapon(String aString)
	{
		favoredWeapon = aString;
	}

	/**
	 * This method gets the list of races (names) that are acceptable
	 * to this deity.
	 * @return List of String names of races
	 */
	public List getRaceList()
	{
		return raceList;
	}

	/**
	 * This method sets the list of races that this deity accepts.
	 * @param aString String of race names delimited by pipe characters.
	 */
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
	 * This method returns the list of pantheons this deity belongs to
	 * @return List containing the names of the pantheons this deity belongs to
	 */
	public List getPantheonList()
	{
		return pantheonList;
	}

	/**
	 * This method adds a single pantheon to the pantheons that this deity
	 * belongs to and also ensures that it is present in the global list
	 * of pantheon names.
	 * @param aString String name of a pantheon
	 */
	public void addPantheonList(String aString)
	{
		aString = aString.trim();
		pantheonList.add(aString);
		Globals.getPantheons().add(aString);
	}

	/**
	 * This method sets the list of pantheons that this deity belongs to.
	 * @param aString String of pantheon names delimited by pipe characters.
	 */
	public void setPantheonList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		pantheonList.clear();           // remove previous contents
		while (aTok.hasMoreTokens())
		{
			addPantheonList(aTok.nextToken());
		}
	}

	/**
	 * This method gets the comma-delimited list of pre-requisites for selecting
	 * this deity in HTML format.
	 * @param includeHeader boolean true if the &lt;html&gt; and &lt;/html&gt; tags
	 * should be included
	 * @return the list of prerequisites
	 */
	public String preReqHTMLStrings(boolean includeHeader)
	{
		final List prereqs = new ArrayList();
		addPreReqTo(prereqs);

		if (followerAlignments.length() != 0)
		{
			boolean bsubPreReq = false;
			final StringBuffer preReqString = new StringBuffer(20);
			final StringTokenizer aTok = new StringTokenizer(followerAlignments, "[]", true);
			while (aTok.hasMoreTokens())
			{
				String preReq = aTok.nextToken();
				if ("[".equals(preReq))
				{
					bsubPreReq = true;
				}
				else if ("]".equals(preReq))
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
		}
		//NoSuchElementException could be thrown on this return
		//TODO - define default behaviour when this occurs
		return preReqHTMLStringsForList(null, prereqs, includeHeader);
	}

	/**
	 * This method sets the special abilities granted by this deity.
	 * For efficiency, avoid calling this method except from I/O routines.
	 * @param aString String of special abilities delimited by pipes
	 * @param level int level at which the ability is granted
	 */
	public void setSpecialAbilityList(String aString, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		List specialAbilityList = getSpecialAbilityList();

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				specialAbilityList.clear();
				continue;
			}
			final StringTokenizer bTok = new StringTokenizer(bString, ":", false);
			String dString = bTok.nextToken(); //Throw away token
			if (bTok.hasMoreTokens())
			{
				dString = bTok.nextToken();
			}
			SpecialAbility sa = new SpecialAbility(bString, "DEITY=" + name + "|" + dString);
			addSpecialAbilityToList(sa);
		}
	}

	/**
	 * This method adds a group of abilities to the list of special
	 * abilities granted by thid deity.
	 * @param aList List of SpecialAbility objects granted by
	 * this deity.
	 * @return List
	 */
	protected List addSpecialAbilitiesToList(List aList)
	{
		List specialAbilityList = getSpecialAbilityList();
		if (specialAbilityList == null || specialAbilityList.isEmpty())
		{
			return aList;
		}
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			final SpecialAbility sa = (SpecialAbility) i.next();
			if (sa.pcQualifiesFor(Globals.getCurrentPC()))
			{
				aList.add(sa);
			}
		}
		return aList;
	}

	/**
	 * This method gets the text used in outputting source files (.pcc files)
	 * Made public on 10 Dec 2002 by sage_sam to match PObject method
	 * @return String containing properly formatted pcc text for this deity.
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());
		if ((domainListString != null) && (domainListString.length() != 0))
		{
			txt.append("\tDOMAINS:").append(domainListString);
		}
		if (followerAlignments.length() != 0)
		{
			txt.append("\tFOLLOWERALIGN:").append(followerAlignments);
		}
		//if (description.length() != 0)
		//{
		//	txt.append("\tDESC:").append(pcgen.io.EntityEncoder.encode(description));
		//}
		if (holyItem.length() != 0)
		{
			txt.append("\tSYMBOL:").append(holyItem);
		}
		if (favoredWeapon.length() != 0)
		{
			txt.append("\tDEITYWEAP:").append(favoredWeapon);
		}
		txt.append("\tALIGN:").append(alignment);
		if (pantheonList.size() != 0)
		{
			txt.append("\tPANTHEON:").append(Utility.join(pantheonList, "|"));
		}
		if (raceList.size() != 0)
		{
			txt.append("\tRACE:").append(Utility.join(raceList, "|"));
		}
		txt.append(super.getPCCText(false));
		return txt.toString();
	}
}

