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
 * Last Edited: $Date: 2006/02/21 01:16:12 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import pcgen.util.Logging;

/**
 * <code>BioSet</code>.
 *
 * @author Bryan McRoberts
 * @version $Revision: 1.1 $
 */
public final class BioSet extends PObject
{
	private PlayerCharacter aPC = null;
	/** key = Dwarf.BASEAGE or Dwarf%.BASEAGE, value = tagged value. */
	private Map raceMap = new HashMap();
	/** for user overrides/additions, check this before raceMap. */
	private Map userMap = new HashMap();
	/** key = region.age, value = bonus adjustments. */
	private Map ageMap = new HashMap();
	private String raceName = "";
	private String regionName = Constants.s_NONE;

	public void clearUserMap()
	{
		userMap.clear();
	}

	/**
	 * @param region
	 * @param race
	 * @param tag
	 */
	public void addToRaceMap(String region, String race, String tag)
	{
		final int x = tag.indexOf(':');
		if (x < 0)
		{
			Logging.errorPrint("Invalid value sent to map: " + tag + " (for " + race + ")");
			return; // invalid tag
		}
		final String key = region + "." + race + "." + tag.substring(0, x);
		final String value = tag.substring(x + 1);
		final String r = (String) raceMap.get(key);
		if (r == null)
		{
			raceMap.put(key, value);
		}
		else
		{
			raceMap.put(key, r + "\t" + value);
		}
	}

	public void addToUserMap(String region, String race, String tag)
	{
		final int x = tag.indexOf(':');
		if (x < 0)
		{
			Logging.errorPrint("Invalid value sent to map: " + tag + " (for " + race + ")");
			return; // invalid tag
		}
		final String key = region + "." + race + "." + tag.substring(0, x);
		final String value = tag.substring(x + 1);
		final String r = (String) userMap.get(key);
		if (r == null)
		{
			userMap.put(key, value);
		}
		else
		{
			userMap.put(key, r + "\t" + value);
		}
	}

	public void removeFromUserMap(String region, String race, String tag)
	{
		final String key;
		final int x = tag.indexOf(':');
		if (x < 0)
		{
			key = region + "." + race + "." + tag;
		}
		else
		{
			key = region + "." + race + "." + tag.substring(0, x);
		}
		userMap.remove(key);
	}

	/**
	 * @param region = region (e.g. None)
	 * @param line = 0|Adult\tBONUS:STAT|MUS|-1
	 */
	public void addToAgeMap(String region, String line)
	{
		final int x = line.indexOf('|');
		if (x >= 0)
		{
			ageMap.put(region + "." + line.substring(0, x), line.substring(x + 1));
		}
		else
		{
			ageMap.put(region + "." + line, null);
		}
	}

	public String getTagForRace(String region, String race, String tag)
	{
		return getValueInMaps(region, "." + race, "." + tag);
	}

	/**
	 * @param region
	 * @param index
	 * @return
	 */
	public String getAgeMapIndex(String region, int index)
	{
		String retVal = (String) ageMap.get(region + "." + String.valueOf(index));
		if ((retVal == null) || (retVal.indexOf("BONUS:") < 0))
		{
			retVal = (String) ageMap.get("None." + String.valueOf(index));
		}
		return retVal;
	}

	/**
	 * Left alone for a while, will remove in a month unless it is used.
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 * @param pc
	 * @param aType
	 * @param aName
	 * @return
	 */
	public double getBonusTo(PlayerCharacter pc, String aType, String aName)
	{
		aPC = pc;
		regionName = aPC.getRegion() + ".";
		raceName = aPC.getRace().getName().trim();

		final int ageSet = getPCAgeSet(pc);

		final String ageSetLine = getAgeMapIndex(pc.getRegion(), ageSet);
		if (ageSetLine == null)
		{
			return 0;
		}
		final StringTokenizer tok = new StringTokenizer(ageSetLine, "\t", false);
		tok.nextToken(); // name of ageSet e.g. Middle Aged
		final PObject p = new PObject();
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

	public String getAgeSetLine(PlayerCharacter pc)
	{
		aPC = pc;
		regionName = aPC.getRegion() + ".";
		raceName = aPC.getRace().getName().trim();

		final int ageSet = getPCAgeSet(pc);

		return getAgeMapIndex(pc.getRegion(), ageSet);
	}

	/**
	 * Randomizes the values of the passed in attributes.
	 *
	 * @param randomizeStr .-delimited list of attributes to randomize. (AGE.HT.WT.EYES.HAIR.SKIN are the possible values.)
	 */
	public void randomize(final String randomizeStr)
	{
		aPC = Globals.getCurrentPC();
		if (aPC == null || aPC.getRace() == null)
		{
			return;
		}
		raceName = aPC.getRace().getName();

		regionName = aPC.getRegion() + '.';

		final List ranList = new ArrayList();
		final StringTokenizer lineTok = new StringTokenizer(randomizeStr, ".", false);
		while (lineTok.hasMoreTokens())
		{
			final String aString = lineTok.nextToken();
			if (aString.startsWith("AGECAT"))
			{
				generateAge(Integer.parseInt(aString.substring(6)), false);
			}
			else
			{
				ranList.add(aString);
			}
		}

		if (ranList.contains("AGE"))
		{
			generateAge(0, true);
		}
		if (ranList.contains("HT") || ranList.contains("WT"))
		{
			generateHeightWeight();
		}
		if (ranList.contains("EYES"))
		{
			aPC.setEyeColor(generateBioValue(".EYES"));
		}
		if (ranList.contains("HAIR"))
		{
			aPC.setHairColor(generateBioValue(".HAIR"));
		}
		if (ranList.contains("SKIN"))
		{
			aPC.setSkinColor(generateBioValue(".SKINTONE"));
		}
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

	private String getValueInMaps(String addKey)
	{
		return getValueInMaps(regionName, raceName, addKey);
	}

	private String mapFind(Map m, final String argRegionName, final String argRaceName, final String addKey, final String altRaceName)
	{
		//
		// First check for region.racename.key
		//
		String r = (String) m.get(argRegionName + argRaceName + addKey);
		if (r != null)
		{
			return r;
		}

		//
		// If not found, try the race name without any parenthesis
		//
		final int altRaceLength = altRaceName.length();
		if (altRaceLength != 0)
		{
			r = (String) m.get(argRegionName + altRaceName + addKey);
			if (r != null)
			{
				return r;
			}
		}

		//
		// If still not found, try the same two searches again without a region
		//
		if (!argRegionName.equals(Constants.s_NONE))
		{
			r = (String) m.get(Constants.s_NONE + "." + argRaceName + addKey);
			if (r != null)
			{
				return r;
			}
			if (altRaceLength != 0)
			{
				r = (String) m.get(Constants.s_NONE + "." + altRaceName + addKey);
			}
		}
		return r;
	}

	private String getValueInMaps(String argRegionName, String argRaceName, String addKey)
	{
		String anotherRaceName;
		if (argRaceName.indexOf('(') >= 0)
		{
			anotherRaceName = argRaceName.substring(0, argRaceName.indexOf('(')).trim() + '%';
		}
		else
		{
			anotherRaceName = argRaceName + '%';
		}

		final String r = mapFind(userMap, argRegionName, argRaceName, addKey, anotherRaceName);
		if (r != null)
		{
			return r;
		}
		return mapFind(raceMap, argRegionName, argRaceName, addKey, anotherRaceName);
	}

	private String getTokenNumberInMaps(String addKey, int tokenNum)
	{
		String r = getValueInMaps(addKey);
		if (r == null)
		{
			return r;
		}
		final StringTokenizer aTok = new StringTokenizer(r, "\t", false);
		for (; tokenNum >= 0; --tokenNum)
		{
			if (!aTok.hasMoreTokens())
			{
				return null;
			}
			r = aTok.nextToken();
		}
		return r;
	}

	private void generateAge(final int ageCategory, final boolean useClassOnly)
	{
		//
		// Can't find a base age for the category, then there's nothing to do
		//
		final String age = getTokenNumberInMaps(".BASEAGE", ageCategory);
		if (age == null)
		{
			return;
		}



		//
		// First check for class age modification information
		//
		final int baseAge = Integer.parseInt(age);
		int ageAdd = -1;

		String aClass = getTokenNumberInMaps(".CLASS", ageCategory);
		if (aClass != null)
		{
			final StringTokenizer bTok = new StringTokenizer(aClass, "[]|", false);
			while (bTok.hasMoreTokens() && (ageAdd < 0))
			{
				final StringTokenizer cTok = new StringTokenizer(bTok.nextToken(), ",", false);
				String nextString = bTok.nextToken(); // should be BASEAGEADD:xdy
				if (nextString.startsWith("BASEAGEADD:"))
				{
					nextString = nextString.substring(11);
				}
				while (cTok.hasMoreTokens())
				{
					if (aPC.getClassNamed(cTok.nextToken()) != null)
					{
						ageAdd = RollingMethods.roll(nextString);
						break;
					}
				}
			}
		}

		//
		// If there was no class age modification, then generate a number based on the .LST
		//
		if ((ageAdd < 0) && !useClassOnly)
		{
			aClass = getTokenNumberInMaps(".AGEDIEROLL", ageCategory);
			if (aClass != null)
			{
				ageAdd = RollingMethods.roll(aClass);
			}
		}

		if ((ageAdd >= 0) && (baseAge > 0))
		{
			aPC.setAge(baseAge + ageAdd);
		}
	}

	private void generateHeightWeight()
	{
		int baseHeight = 0;
		int baseWeight = 0;
		int htAdd = 0;
		int wtAdd = 0;
		String totalWeight = null;
		final String htwt = getTokenNumberInMaps(".SEX", 0);
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

	private String generateBioValue(final String addKey)
	{
		final String line = getTokenNumberInMaps(addKey, 0);
		final String rv;
		if (line != null)
		{
			final StringTokenizer aTok = new StringTokenizer(line, "|", false);
			final List aList = new ArrayList();
			while (aTok.hasMoreTokens())
			{
				aList.add(aTok.nextToken());
			}
			final int roll = RollingMethods.roll(1, aList.size()) - 1; // needs to be 0-offset
			rv = (String) aList.get(roll);
		}
		else
		{
			rv = "";
		}
		return rv;
	}

	public String toString()
	{
		final StringBuffer sb = new StringBuffer(100);
		sb.append("race name: ").append(raceName);
		sb.append(", regionName: ");
		sb.append(regionName);
		return sb.toString();
	}

	/**
	 * Read in ages, setup a mapped structure for them.
	 * @param region
	 * @return
	 */
	private SortedMap setupAgeSet(String region)
	{
		final SortedMap ageSets = new TreeMap();
		for (Iterator it = ageMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();
			if (key.startsWith(region + "."))
			{
				final Integer setNum = new Integer(key.substring(region.length() + 1));
				final String value = (String) ageMap.get(key);
				final SortedMap races = new TreeMap();
				races.put("AGESET", value);
				ageSets.put(setNum, races);
			}
		}
		return ageSets;
	}

	/**
	 * Builds a string describing the bio settings for the specified race
	 * This string is formatted so that it can be read in by BioSetLoader
	 *
	 * @param region The region of the race to be output
	 * @param race   The name of the race to be output
	 * @return String A lst string describing the region's biosets.
	 */
	public String getRacePCCText(String region, String race)
	{
		final StringBuffer sb = new StringBuffer(1000);
		sb.append("REGION:").append(region).append("\n\n");

		final SortedMap ageSets = getRaceTagsByAge(region, race, false);

		return appendAgesetInfo(ageSets, sb);

	}

	private String appendAgesetInfo(final SortedMap ageSets, final StringBuffer sb)
	{
		// Iterate through ages, outputing the info
		for (Iterator it = ageSets.keySet().iterator(); it.hasNext();)
		{
			final Integer key = (Integer) it.next();
			final SortedMap races = (SortedMap) ageSets.get(key);

			sb.append("AGESET:").append(key).append("|");
			sb.append(races.get("AGESET")).append("\n");

			for (Iterator raceIt = races.keySet().iterator(); raceIt.hasNext();)
			{
				final String aRaceName = (String) raceIt.next();
				if (!"AGESET".equals(aRaceName))
				{
					final SortedMap tags = (SortedMap) races.get(aRaceName);
					for (Iterator tagIt = tags.keySet().iterator(); tagIt.hasNext();)
					{
						final String tagName = (String) tagIt.next();
						sb.append("RACENAME:").append(aRaceName).append("\t\t");
						sb.append(tagName).append(':').append(tags.get(tagName)).append("\n");
					}
				}
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Copies the bio data for one race to a new race.
	 *
	 * @param origRegion The region of the original race
	 * @param origRace   The name of the original race
	 * @param copyRegion The region of the target race
	 * @param copyRace   The name of the target race
	 */
	public void copyRaceTags(String origRegion, String origRace, String copyRegion, String copyRace)
	{
		// Retreive the original race's info
		final SortedMap ageSets = getRaceTagsByAge(origRegion, origRace, true);

		// Iterate through ages, adding the info for the new race
		for (Iterator it = ageSets.keySet().iterator(); it.hasNext();)
		{
			final Integer key = (Integer) it.next();
			final SortedMap races = (SortedMap) ageSets.get(key);

			for (Iterator raceIt = races.keySet().iterator(); raceIt.hasNext();)
			{
				final String aRaceName = (String) raceIt.next();
				if (!"AGESET".equals(aRaceName))
				{
					final SortedMap tags = (SortedMap) races.get(aRaceName);
					for (Iterator tagIt = tags.keySet().iterator(); tagIt.hasNext();)
					{
						final String tagName = (String) tagIt.next();
						final String value = (String) tags.get(tagName);
						addToUserMap(copyRegion, copyRace, tagName + ":" + value);
					}
				}
			}
		}
	}

	/**
	 * Retrieves a collection of the tags defined for a race grouped by
	 * the age brackets.
	 *
	 * @param region The region of the race
	 * @param race   The name of the race.
	 * @param includeGenericMatches Should generic race references such as Elf% be included
	 * @return SortedMap A map of the gae brackets. Within each age bracket is a
	 * sorted map of the races (one only) and wihtin this is the tags for that
	 * race and age.
	 */
	private SortedMap getRaceTagsByAge(String region, String race, boolean includeGenericMatches)
	{
		String otherRace = "";
		if (includeGenericMatches)
		{
			final int idx = race.indexOf('(');
			if (idx >= 0)
			{
				otherRace = race.substring(0, idx).trim() + '%';
			}
			else
			{
				otherRace = race + '%';
			}
		}
		// Read in ages, setup a mapped structure for them
		SortedMap ageSets = setupAgeSet(region);

		// Read in the base race settings, split where necessary and add to the appropriate age bracket
		for (Iterator it = raceMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();
			if (key.startsWith(region + "." + race + ".") || key.startsWith(region + "." + otherRace + "."))
			{
				final String value = (String) raceMap.get(key);
				addTagToAgeSet(ageSets, key, value);
			}
		}

		// Read in the user settings, split where necessary and add to the appropriate age bracket
		for (Iterator it = userMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();
			if (key.startsWith(region + "." + race + ".") || key.startsWith(region + "." + otherRace + "."))
			{
				final String value = (String) userMap.get(key);
				addTagToAgeSet(ageSets, key, value);
			}
		}
		return ageSets;
	}

	/**
	 * Adds the tag (key & value) to the supplied ageSets collection. It is
	 * assumed that the ageSet already has an entry for each age bracket and
	 * that this entry will be a SortedMap of races. Each race will contain a
	 * SortedMap of tags and their values.<br/>
	 * The key is assumed to be of the form region.race.tag
	 * eg "Custom.Human%.MAXAGE"
	 * The value is assumed to be either a tab delimited set of values or a
	 * single value, depending on the tag. eg "34	52	69	110" or "Blond|Brown"
	 * If a single value, it will be added to the first age set. Multiple values
	 * are split amoungst the age sets in order, with any values not matching an
	 * age set being ignored.
	 *
	 * @param ageSets The collection of age brackets.
	 * @param key The region.race.tag specifier.
	 * @param value The value of the tag.
	 */
	private void addTagToAgeSet(SortedMap ageSets, String key, String value)
	{
		StringTokenizer tok = new StringTokenizer(key, ".");
		if (tok.countTokens() >= 3)
		{
			tok.nextToken(); // ignore region name
			final String aRaceName = tok.nextToken();
			final String tagName = tok.nextToken();

			if (tagName.equals("BASEAGE") || tagName.equals("MAXAGE") || tagName.equals("AGEDIEROLL"))
			{
				// Need to split these amoungst the agesets
				// NB: There may be more values than age sets. It seems that there are
				// normally double currently. These extras are not used by this class,
				// so they will not be output, just ignored by this code.
				tok = new StringTokenizer(value, "\t");
				for (int ageBracket = 0; ageBracket < ageSets.size() && tok.hasMoreTokens(); ageBracket++)
				{
					final String tagValue = tok.nextToken();
					final SortedMap races = (SortedMap) ageSets.get(new Integer(ageBracket));
					SortedMap tags = (SortedMap) races.get(aRaceName);
					if (tags == null)
					{
						tags = new TreeMap();
						races.put(aRaceName, tags);
					}
					tags.put(tagName, tagValue);
				}
			}
			else
			{
				final SortedMap races = (SortedMap) ageSets.get(new Integer(0));
				SortedMap tags = (SortedMap) races.get(aRaceName);
				if (tags == null)
				{
					tags = new TreeMap();
					races.put(aRaceName, tags);
				}
				tags.put(tagName, value);
			}
		}
	}

	public int getPCAgeSet(PlayerCharacter pc)
	{
		final String value = getValueInMaps(".BASEAGE");
		if (value == null)
		{
			return 0;
		}
		final StringTokenizer aTok = new StringTokenizer(value, "\t", false);
		final int pcAge = pc.getAge();
		int ageSet = -1;
		while (aTok.hasMoreTokens())
		{
			final int setBaseAge = Integer.parseInt(aTok.nextToken());
			if (pcAge < setBaseAge)
			{
				break;
			}
			++ageSet;
		}
		//
		// Check to see if character is younger than earliest age group
		//
		if (ageSet < 0)
		{
			//Globals.errorPrint("Warning: character is younger than any age information available. Using adjustments for first age category.");
			ageSet = 0;
		}
		return ageSet;
	}

	public Map getAgeMap()
	{
		return ageMap;
	}

	public int getAgeSetNamed(final String ageCategory)
	{
		String aString;
		for (Iterator e = ageMap.entrySet().iterator(); e.hasNext();)
		{
			final Map.Entry entry = (Map.Entry) e.next();
			aString = entry.getValue().toString();
			if (aString.equals(ageCategory) || aString.startsWith(ageCategory + "\t"))
			{
				aString = entry.getKey().toString();
				final int idx = aString.indexOf('.');
				if (idx >= 0)
				{
					return Integer.parseInt(aString.substring(idx + 1));
				}
			}
		}
		return -1;
	}
}
