/*
 * BioSet.java
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
 * Created on September 27, 2002, 5:30 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:00:27 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <code>BioSet</code>.
 *
 * @author Bryan McRoberts
 * @version $Revision: 1.1 $
 */
public final class BioSet
{
	private static PlayerCharacter aPC = null;
	/** key = Dwarf.BASEAGE or Dwarf%.BASEAGE, value = tagged value. */
	private static HashMap raceMap = new HashMap();
	/** for user overrides/additions, check this before raceMap. */
	private static HashMap userMap = new HashMap();
	/** key = region.age, value = bonus adjustments. */
	private static HashMap ageMap = new HashMap();
	private static String raceName = "";
	private static String regionName = Constants.s_NONE;
	private static String otherRaceName=""; // raceName+%

	/**
	 * Constructor
	 * <br>author: Bryan McRoberts 09-27-02
	 */
	public BioSet()
	{
	}

	public static void clearUserMap()
	{
		userMap.clear();
	}

	/**
	 * @param region
	 * @param race
	 * @param tag
	 */
	public static void addToRaceMap(String region, String race, String tag)
	{
		int x = tag.indexOf(":");
		if (x < 0)
		{
			Globals.errorPrint("Invalid value sent to map: " + tag + " (for " + race + ")");
			return; // invalid tag
		}
		final String key = region + "." + race + "." + tag.substring(0, x);
		final String value = tag.substring(x + 1);
		String r = (String) raceMap.get(key);
		if (r == null)
		{
			raceMap.put(key, value);
		}
		else
		{
			raceMap.put(key, r + "\t" + value);
		}
	}

	public static void addToUserMap(String region, String race, String tag)
	{
		int x = tag.indexOf(":");
		if (x < 0)
		{
			Globals.errorPrint("Invalid value sent to map: " + tag + " (for " + race + ")");
			return; // invalid tag
		}
		final String key = region + "." + race + "." + tag.substring(0, x);
		final String value = tag.substring(x + 1);
		String r = (String) userMap.get(key);
		if (r == null)
		{
			userMap.put(key, value);
		}
		else
		{
			userMap.put(key, r + "\t" + value);
		}
	}

	/**
	 * @param region = region (e.g. None)
	 * @param line = 0|Adult\tBONUS:STAT|MUS|-1
	 */
	public static void addToAgeMap(String region, String line)
	{
		final int x = line.indexOf("|");
		if (x >= 0)
		{
			ageMap.put(region + "." + line.substring(0, x), line.substring(x + 1));
		}
		else
		{
			ageMap.put(region + "." + line, null);
		}
	}

	/**
	 * @param region
	 * @param index
	 * @return
	 */
	private static String getAgeMapIndex(String region, int index)
	{
		String retVal = (String) ageMap.get(region + "." + String.valueOf(index));
		if ((retVal == null) || retVal.indexOf("BONUS:") == -1)
		{
			retVal = (String) ageMap.get("None." + String.valueOf(index));
		}
		return retVal;
	}

	static double getBonusTo(PlayerCharacter pc, String aType, String aName)
	{
		aPC = pc;
		regionName = aPC.getRegion()+".";
		raceName = aPC.getRace().getName().trim();
		// turn Dwarf (Moonbeam) into Dwarf% in case Dwarf (Moonbeam) can't be found
		if (raceName.indexOf("(") >= 0)
		{
			otherRaceName = raceName.substring(0, raceName.indexOf("(")).trim() + '%';
		}
		else
		{
			otherRaceName = raceName + '%';
		}

		int ageSet = 0;
		final String value = getValueInMaps(".BASEAGE");
		if (value == null)
		{
			return 0;
		}
		final StringTokenizer aTok = new StringTokenizer(value, "\t", false);
		while (aTok.hasMoreTokens())
		{
			final int age = Integer.parseInt(aTok.nextToken());
			if (age >= pc.getAge())
			{
				if (ageSet > 0)
				{
					--ageSet;
				}
				break;
			}
			++ageSet;
		}
		String ageSetLine = getAgeMapIndex(pc.getRegion(), ageSet);
		if (ageSetLine == null)
		{
			return 0;
		}
		final StringTokenizer tok = new StringTokenizer(ageSetLine, "\t", false);
		tok.nextToken(); // name of ageSet e.g. Middle Aged
		PObject p = new PObject();
		while (tok.hasMoreTokens())
		{
			final String b = tok.nextToken();
			if (b.startsWith("BONUS:"))
			{
				p.addBonusList(b.substring(6));
			}
		}
		return p.bonusTo(aType, aName);
	}

	/**
	 * Randomizes the values of the passed in attributes.
	 *
	 * @param randomize .-delimited list of attributes to randomize. (AGE.HT.WT.EYES.HAIR.SKIN are the possible values.)
	 */
	public static void randomize(String randomize)
	{
		aPC = Globals.getCurrentPC();
		if (aPC == null || aPC.getRace() == null)
		{
			return;
		}
		raceName = aPC.getRace().getName();

		if (raceName.indexOf("(") >= 0)
		{
			otherRaceName = raceName.substring(0, raceName.indexOf("(")).trim() + '%';
		}
		else
		{
			otherRaceName = raceName + '%';
		}
		regionName = aPC.getRegion() + '.';

		final ArrayList ranList = new ArrayList();
		StringTokenizer lineTok = new StringTokenizer(randomize, ".", false);
		while (lineTok.hasMoreTokens())
		{
			ranList.add(lineTok.nextToken());
		}

		if (ranList.contains("AGE"))
		{
			generateAge();
		}
		if (ranList.contains("HT") || ranList.contains("WT"))
		{
			generateHeightWeight();
		}
		if (ranList.contains("EYES"))
		{
			generateEyes();
		}
		if (ranList.contains("HAIR"))
		{
			generateHair();
		}
		if (ranList.contains("SKIN"))
		{
			generateSkin();
		}
		return;
	}

	private static String replaceString(String argInput, String replacement, int value)
	{
		String input = argInput;
		final int x = input.indexOf(replacement);
		if (x >= 0)
		{
			final String output = input.substring(0, x);
			final String appendage = input.substring(x + replacement.length());
			input = output + value + appendage;
		}
		return input;
	}

	private static String getValueInMaps(String addKey)
	{
		String r = (String) userMap.get(regionName + raceName + addKey);
		if (r != null)
		{
			return r;
		}
		r = (String) userMap.get(regionName + otherRaceName + addKey);
		if (r != null)
		{
			return r;
		}
		r = (String) raceMap.get(regionName + raceName + addKey);
		if (r != null)
		{
			return r;
		}
		r = (String) raceMap.get(regionName + otherRaceName + addKey);
		return r;
	}

	private static String getTokenNumberInMaps(String addKey, int tokenNum)
	{
		String r = getValueInMaps(addKey);
		if (r == null)
		{
			return r;
		}
		final StringTokenizer aTok = new StringTokenizer(r, "\t", false);
		for (; tokenNum >= 0; --tokenNum)
		{
			r = aTok.nextToken();
		}
		return r;
	}

	private static void generateAge()
	{
		int baseAge;
		int ageAdd = 0;
		String age = getTokenNumberInMaps(".BASEAGE", 0);
		if (age == null)
		{
			return;
		}
		String aClass = getTokenNumberInMaps(".CLASS", 0);
		if (aClass == null)
		{
			return;
		}

		baseAge = Integer.parseInt(age);
		final StringTokenizer bTok = new StringTokenizer(aClass, "[]|", false);
		while (bTok.hasMoreTokens())
		{
			final StringTokenizer cTok = new StringTokenizer(bTok.nextToken(), ",", false);
			String nextString = bTok.nextToken(); // should be BASEAGEADD:xdy
			if (nextString.startsWith("BASEAGEADD:"))
			{
				nextString = nextString.substring(11);
			}
			boolean foundIt = false;
			while (cTok.hasMoreTokens() && !foundIt)
			{
				if (aPC.getClassNamed(cTok.nextToken()) != null)
				{
					foundIt = true;
				}
			}
			if (foundIt)
			{
				ageAdd = RollingMethods.roll(nextString);
				break;
			}
		}
		if ((ageAdd > 0) && (baseAge > 0))
		{
			aPC.setAge(baseAge + ageAdd);
		}
	}

	private static void generateHeightWeight()
	{
		int baseHeight = 0;
		int baseWeight = 0;
		int htAdd = 0;
		int wtAdd = 0;
		String totalWeight = null;
		String htwt = getTokenNumberInMaps(".SEX", 0);
		if (htwt == null)
		{
			return;
		}
		final StringTokenizer genderTok = new StringTokenizer(htwt, "[]", false);
		while (genderTok.hasMoreTokens())
		{
			if (genderTok.nextToken().equals(aPC.getGender()))
			{
				final String htWtLine = genderTok.nextToken();
				final StringTokenizer htwtTok = new StringTokenizer(htWtLine, "|", false);
				while (htwtTok.hasMoreTokens())
				{
					final String tag = htwtTok.nextToken();
					if (tag.startsWith("BASEHT:"))
					{
						baseHeight = Integer.parseInt(tag.substring(7));
					}
					else if (tag.startsWith("BASEWT:"))
					{
						baseWeight = Integer.parseInt(tag.substring(7));
					}
					else if (tag.startsWith("HTDIEROLL:"))
					{
						htAdd = RollingMethods.roll(tag.substring(10));
					}
					else if (tag.startsWith("WTDIEROLL:"))
					{
						wtAdd = RollingMethods.roll(tag.substring(10));
					}
					else if (tag.startsWith("TOTALWT:"))
					{
						totalWeight = tag.substring(8);
					}
				}
				if (baseHeight != 0 && htAdd != 0)
				{
					aPC.setHeight(baseHeight + htAdd);
				}
				if (totalWeight != null && baseWeight != 0 && wtAdd != 0)
				{
					totalWeight = replaceString(totalWeight, "HTDIEROLL", htAdd);
					totalWeight = replaceString(totalWeight, "BASEWT", baseWeight);
					totalWeight = replaceString(totalWeight, "WTDIEROLL", wtAdd);
					aPC.setWeight(aPC.getVariableValue(totalWeight, "").intValue());
				}
				break;
			}
			else
			{
				genderTok.nextToken(); // burn next token
			}
		}
	}

	private static void generateSkin()
	{
		final String line = getTokenNumberInMaps(".SKINTONE", 0);
		if (line == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(line, "|", false);
		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		final int roll = RollingMethods.roll(1, aList.size()) - 1; // needs to be 0-offset
		aPC.setSkinColor((String) aList.get(roll));
	}

	private static void generateEyes()
	{
		final String line = getTokenNumberInMaps(".EYES", 0);
		if (line == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(line, "|", false);
		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		final int roll = RollingMethods.roll(1, aList.size()) - 1; // needs to be 0-offset
		aPC.setEyeColor((String) aList.get(roll));
	}

	private static void generateHair()
	{
		final String line = getTokenNumberInMaps(".HAIR", 0);
		if (line == null)
		{
			return;
		}
		ArrayList aList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(line, "|", false);
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		final int roll = RollingMethods.roll(1, aList.size()) - 1; // needs to be 0-offset
		aPC.setHairColor((String) aList.get(roll));
	}

	public String toString()
	{

		StringBuffer sb = new StringBuffer(100);
		sb.append("race name: ").append(raceName);
		sb.append(", regionName: ");
		sb.append(regionName);
		return sb.toString();
	}

}
