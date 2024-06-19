/*
 * SystemCollections.java
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
 * Created 2003-07-12 14:02
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:16:13 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import pcgen.core.character.EquipSlot;

/**
 * Contains lists of stuff loaded from system-wide lst files.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 **/

public class SystemCollections
{
	/** The following are loaded from system files.
	 * <ul>
	 * <li>alignmentList</li>
	 * <li>birthplaceList</li>
	 * <li>bonusStackList</li>
	 * <li>checkList</li>
	 * <li>cityList</li>
	 * <li>gameModeList</li>
	 * <li>hairStyleList</li>
	 * <li>helpContextFileList</li>
	 * <li>interestsList</li>
	 * <li>locationList</li>
	 * <li>paperInfoList</li>
	 * <li>phobiaList</li>
	 * <li>phraseList</li>
	 * <li>schoolsList</li>
	 * <li>sizeAdjustmentList</li>
	 * <li>eqSlotList</li>
	 * <li>specialsList</li>
	 * <li>speechList</li>
	 * <li>statList</li>
	 * <li>traitList</li>
	 * <li>bonusSpellMap</li>
	 * </ul>
	 */
	private static final List alignmentList = new ArrayList(15);
	private static final List birthplaceList = new ArrayList(10);
	private static final List bonusStackList = new ArrayList();
	private static final List checkList = new ArrayList();
	private static final List cityList = new ArrayList(10);
	private static final List gameModeList = new ArrayList();
	private static final List hairStyleList = new ArrayList(65);
	private static final List interestsList = new ArrayList(230);
	private static final List locationList = new ArrayList(30);
	private static final List paperInfoList = new ArrayList(15);
	private static final List phobiaList = new ArrayList(200);
	private static final List phraseList = new ArrayList(800);
	private static final List schoolsList = new ArrayList(20);
	private static final List sizeAdjustmentList = new ArrayList(9);
	private static final List eqSlotList = new ArrayList(20);
	private static final List specialsList = new ArrayList(20);
	private static final List speechList = new ArrayList(100);
	private static final List statList = new ArrayList();
	private static final List traitList = new ArrayList(550);
	private static final SizeAdjustment spareSize = new SizeAdjustment();

	/**
	 * Make sure it doesn't get instantiated.
	 */
	private SystemCollections()
	{
	}


	//ALIGNMENTLIST

	/**
	 * Add to the alignment list.
	 * @param alignment
	 */
	public static void addToAlignmentList(PCAlignment alignment)
	{
		alignmentList.add(alignment);
	}

	/**
	 * Clear out the alignment list.
	 */
	public static void clearAlignmentList()
	{
		alignmentList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the alignmentlist.
	 * @return
	 */
	public static List getUnmodifiableAlignmentList()
	{
		return Collections.unmodifiableList(alignmentList);
	}

	private static PCAlignment getAlignmentAtIndex(final int index)
	{
		PCAlignment align;
		if ((index < 0) || (index >= alignmentList.size()))
		{
			align = null;
		}
		else
		{
			align = (PCAlignment) alignmentList.get(index);
		}
		return align;
	}

	/**
	 * Return the short version of the alignment name found at the index. (E.g. LG)
	 * @param index
	 * @return
	 */
	public static String getShortAlignmentAtIndex(final int index)
	{
		final PCAlignment al = getAlignmentAtIndex(index);
		if (al == null)
		{
			return "";
		}
		return al.getKeyName();
	}

	/**
	 * Return the long version of the alignment name found at the index. (E.g. Lawful Good)
	 * @param index
	 * @return
	 */
	public static String getLongAlignmentAtIndex(final int index)
	{
		String alName;
		final PCAlignment al = getAlignmentAtIndex(index);
		if (al == null)
		{
			alName = "";
		}
		else
		{
			alName = al.getName();
		}
		return alName;
	}

	/**
	 * Return the index of the alignment name (handles both short and long names.)
	 * @param alignmentName
	 * @return
	 */
	public static int getIndexOfAlignment(final String alignmentName)
	{
		for (int i = 0; i < alignmentList.size(); ++i)
		{
			final PCAlignment alignment = (PCAlignment) alignmentList.get(i);
			// if long name or short name of alignment matches, return index
			if (alignment.getName().equalsIgnoreCase(alignmentName) || alignment.getKeyName().equalsIgnoreCase(alignmentName))
			{
				return i;
			}
		}
		return -1; // not found
	}

	/**
	 * Returns an array of alignment names.
	 * @param useLongForm True if the long names should be returned.
	 * @return
	 */
	public static String[] getAlignmentListStrings(boolean useLongForm)
	{
		final String[] al = new String[alignmentList.size()];
		int x = 0;
		final Iterator i = alignmentList.iterator();
		while (i.hasNext())
		{
			final PCAlignment alignment = (PCAlignment) i.next();
			if (useLongForm)
			{
				al[x++] = alignment.getName();
			}
			else
			{
				al[x++] = alignment.getKeyName();
			}
		}

		return al;
	}


	//BirthplaceList

	/**
	 * Add a birthplace name to the birthplace list.
	 * @param birthplace
	 */
	public static void addToBirthplaceList(String birthplace)
	{
		birthplaceList.add(birthplace);
	}

	/**
	 * Returns and <b>unmodifiable</b> birtplace list.
	 * @return
	 */
	public static List getUnmodifiableBirthplaceList()
	{
		return Collections.unmodifiableList(birthplaceList);
	}


	//BONUSSTACKLIST
	/**
	 * Add an item to the bonus stacking list.
	 * @param item
	 */
	public static void addToBonusStackList(String item)
	{
		bonusStackList.add(item.toUpperCase());
	}

	/**
	 * Return an <b>unmodifiable</b> version of the bonus stacking list.
	 * @return
	 */
	public static List getUnmodifiableBonusStackList()
	{
		return Collections.unmodifiableList(bonusStackList);
	}


	//CHECKLIST
	/**
	 * Add a check to the list of checks.
	 * @param obj
	 */
	public static void addToCheckList(PObject obj)
	{
		checkList.add(obj);
	}

	/**
	 * Empty the check list.
	 */
	public static void clearCheckList()
	{
		checkList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the check list.
	 * @return
	 */
	public static List getUnmodifiableCheckList()
	{
		return Collections.unmodifiableList(checkList);
	}

	static int getIndexOfCheck(final String check)
	{
		for (int i = 0; i < checkList.size(); ++i)
		{
			if (checkList.get(i).toString().equalsIgnoreCase(check))
			{
				return i;
			}
		}
		return -1; // not found
	}

	/**
	 * Return the requested object.
	 * @param name
	 * @return
	 */
	public static PObject getCheckNamed(final String name)
	{
		PObject check;
		final int index = getIndexOfCheck(name);
		if (index == -1)
		{
			check = null;
		}
		else
		{
			check = (PObject) checkList.get(index);
		}
		return check;
	}

	//CITYLIST
	/**
	 * Add to the city list.
	 * @param city
	 */
	public static void addToCityList(String city)
	{
		cityList.add(city);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the city list.
	 * @return
	 */
	public static List getUnmodifiableCityList()
	{
		return Collections.unmodifiableList(cityList);
	}


	//EQUIPSLOTLIST

	/**
	 * Add the equipment slot to the equipment slot list.
	 * @param equipmentSlot
	 */
	public static void addToEquipSlotsList(EquipSlot equipmentSlot)
	{
		eqSlotList.add(equipmentSlot);
	}

	/**
	 * Empty the equipment slots list.
	 */
	public static void clearEquipSlotsList()
	{
		eqSlotList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the equipment slots list.
	 * @return
	 */
	public static List getUnmodifiableEquipSlotList()
	{
		return Collections.unmodifiableList(eqSlotList);
	}


	//GAMEMODELIST
	/**
	 * Add the game mode to the list.
	 * @param mode
	 */
	public static void addToGameModeList(GameMode mode)
	{
		gameModeList.add(mode);
	}

	/**
	 * Empty the game mode list.
	 */
	public static void clearGameModeList()
	{
		gameModeList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return
	 */
	public static List getUnmodifiableGameModeList()
	{
		return Collections.unmodifiableList(gameModeList);
	}

	/**
	 * Sort the game mode list.
	 */
	public static void sortGameModeList()
	{
		Collections.sort(gameModeList);
	}

	/**
	 * Return a game mode matching the name.
	 * @param aString
	 * @return
	 */
	public static GameMode getGameModeNamed(String aString)
	{
		for (Iterator e = gameModeList.iterator(); e.hasNext();)
		{
			final GameMode gameMode = (GameMode) e.next();
			if (gameMode.getName().equalsIgnoreCase(aString))
			{
				return gameMode;
			}
		}
		return null;
	}


	//HAIRSTYLELIST
	/**
	 * Add the hairstyle to the list.
	 * @param hairStyle
	 */
	public static void addToHairStyleList(String hairStyle)
	{
		hairStyleList.add(hairStyle);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return
	 */
	public static List getUnmodifiableHairStyleList()
	{
		return Collections.unmodifiableList(hairStyleList);
	}


	//INTERESTLIST
	/**
	 * Add to the interests list.
	 * @param interest
	 */
	public static void addToInterestsList(String interest)
	{
		interestsList.add(interest);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the interests list.
	 * @return
	 */
	public static List getUnmodifiableInterestsList()
	{
		return Collections.unmodifiableList(interestsList);
	}


	//LOCATIONLIST
	/**
	 * Add to the location list.
	 * @param location
	 */
	public static void addToLocationList(String location)
	{
		locationList.add(location);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the location list.
	 * @return
	 */
	public static List getUnmodifiableLocationList()
	{
		return Collections.unmodifiableList(locationList);
	}


	//STATLIST

	/**
	 * Add to the stat list.
	 * @param stat
	 */
	public static void addToStatList(PCStat stat)
	{
		statList.add(stat);
	}

	/**
	 * Clear out the stat list.
	 */
	public static void clearStatList()
	{
		statList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the stat list.
	 * @return
	 */
	public static List getUnmodifiableStatList()
	{
		return Collections.unmodifiableList(statList);
	}


	//PAPERINFOLIST

	/**
	 * Add the paper info to the list.
	 * @param paper
	 */
	public static void addToPaperInfoList(PaperInfo paper)
	{
		paperInfoList.add(paper);
	}

	/**
	 * Empty the paper info list.
	 */
	public static void clearPaperInfoList()
	{
		paperInfoList.clear();
	}

	/**
	 * Return an <b>unmodifiable</b> version of the paper info list.
	 * @return
	 */
	public static List getUnmodifiablePaperInfo()
	{
		return Collections.unmodifiableList(paperInfoList);
	}

	//PHOBIALIST
	/**
	 * Add the phobia to the phobia list.
	 * @param phobia
	 */
	public static void addToPhobiaList(String phobia)
	{
		phobiaList.add(phobia);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phobia list.
	 * @return
	 */
	public static List getUnmodifiablePhobiaList()
	{
		return Collections.unmodifiableList(phobiaList);
	}

	//PHRASELIST

	/**
	 * Add the phrase to the phrase list.
	 * @param phrase
	 */
	public static void addToPhraseList(String phrase)
	{
		phraseList.add(phrase);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phrase list.
	 * @return
	 */
	public static List getUnmodifiablePhraseList()
	{
		return Collections.unmodifiableList(phraseList);
	}

	//SCHOOLSLIST
	/**
	 * Add the school to the list.
	 * @param school
	 */
	public static void addToSchoolList(String school)
	{
		schoolsList.add(school);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the schools list.
	 * @return
	 */
	public static List getUnmodifiableSchoolsList()
	{
		return Collections.unmodifiableList(schoolsList);
	}

	//SIZEADJUSTMENTLIST

	/**
	 * Clears the sizeAdjustmentList.
	 */
	public static void clearSizeAdjustmentList()
	{
		sizeAdjustmentList.clear();
	}

	/**
	 * Adds the item to the sizeAdjustmentList.
	 * @param item
	 */
	public static void addToSizeAdjustmentList(SizeAdjustment item)
	{
		sizeAdjustmentList.add(item);
	}

	static SizeAdjustment getDefaultSizeAdjustment()
	{
		for (Iterator i = sizeAdjustmentList.iterator(); i.hasNext();)
		{
			final SizeAdjustment s = (SizeAdjustment) i.next();
			if (s.isDefaultSize())
			{
				return s;
			}
		}
		return null;
	}

	/**
	 * Returns the requested SizeAdjustment item, or null if the index is inappropriate.
	 * @param index
	 * @return
	 */
	public static SizeAdjustment getSizeAdjustmentAtIndex(final int index)
	{
		SizeAdjustment sa = null;
		if ((index >= 0) && (index < sizeAdjustmentList.size()))
		{
			sa = (SizeAdjustment) sizeAdjustmentList.get(index);
		}
		return sa;
	}

	/**
	 * Returns the size of the sizeAdjustmentList.
	 * @return
	 */
	public static int getSizeAdjustmentListSize()
	{
		return sizeAdjustmentList.size();
	}

	/**
	 * Returns the requested size adjustment.
	 * @param name
	 * @return
	 */
	public static SizeAdjustment getSizeAdjustmentNamed(String name)
	{
		if (name.trim().length() == 0)
		{
			return spareSize;
		}

		for (Iterator i = sizeAdjustmentList.iterator(); i.hasNext();)
		{
			final SizeAdjustment s = (SizeAdjustment) i.next();
			if (s.getName().equalsIgnoreCase(name) || s.getAbbreviation().equalsIgnoreCase(name))
			{
				return s;
			}
		}
		return null;
	}

	//SPECIALSLIST

	/**
	 * Add to the specials list.
	 * @param sa
	 */
	public static void addToSpecialsList(SpecialAbility sa)
	{
		specialsList.add(sa);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the specials list.
	 * @return
	 */
	public static List getUnmodifiableSpecialsList()
	{
		return Collections.unmodifiableList(specialsList);
	}

	//SPEECHLIST
	/**
	 * Add to the speech list.
	 * @param speech
	 */
	public static void addToSpeechList(String speech)
	{
		speechList.add(speech);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the speech list.
	 * @return
	 */
	public static List getUnmodifiableSpeechList()
	{
		return Collections.unmodifiableList(speechList);
	}


	//TRAITLIST

	/**
	 * Add the trait to the trait list.
	 * @param trait
	 */
	public static void addToTraitList(String trait)
	{
		traitList.add(trait);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the trait list.
	 * @return
	 */
	public static List getUnmodifiableTraitList()
	{
		return Collections.unmodifiableList(traitList);
	}

}
