/*
 * PCGVer1Parser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 22, 2002, 12:15 AM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.core.*;

/**
 * <code>PCGVer1Parser</code><br>
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer1Parser implements IOConstants
{
	private List warnings = new ArrayList();
	private PlayerCharacter aPC;
	private Cache cache;

	/**
	 * Constructor
	 */
	public PCGVer1Parser(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @return a list of warning messages
	 */
	public List getWarnings()
	{
		return warnings;
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @param s   the String to parse
	 */
	public void parsePCG(String s) throws PCGParseException
	{
		final StringTokenizer tokens = new StringTokenizer(s, "\n");
		initCache(tokens.countTokens());
		while (tokens.hasMoreTokens())
		{
			cacheLine(tokens.nextToken().trim());
		}
		parseCachedLines();
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 20-07-02
	 *
	 * @param lines   the String to parse
	 */
	public void parsePCG(String[] lines) throws PCGParseException
	{
		initCache(lines.length);
		for (int i = 0; i < lines.length; i++)
		{
			if ((lines[i].trim().length() > 0) && !isComment(lines[i]))
			{
				cacheLine(lines[i].trim());
			}
		}
		parseCachedLines();
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 20-07-02
	 *
	 * @param lines   the String to parse
	 */
	public void parsePCG(List lines) throws PCGParseException
	{
		String line;
		initCache(lines.size());
		for (Iterator it = lines.iterator(); it.hasNext();)
		{
			line = ((String)it.next()).trim();
			if ((line.length() > 0) && !isComment(line))
			{
				cacheLine(line);
			}
		}
		parseCachedLines();
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

	private void initCache(int capacity)
	{
		cache = new Cache(capacity * 4 / 3);
	}

	private void cacheLine(String s)
	{
		cache.put(s.substring(0, s.indexOf(":")), s);
	}

	/**
	 * Does the actual work:<br>
	 * Retrieves cached lines and parses each line.
	 *
	 * Note: May have to change parse order!
	 *
	 * <br>author: Thomas Behr 31-07-02
	 *
	 * @throws PCGParseException
	 */
	private void parseCachedLines() throws PCGParseException
	{
		/*
		 * #Character Attributes
		 * STAT:STR=18
		 * STAT:DEX=18
		 * STAT:CON=18
		 * STAT:INT=18
		 * STAT:WIS=18
		 * STAT:CHA=18
		 * ALIGN:LG
		 * RACE:Human
		 */
		if (cache.containsKey(TAG_STAT))
		{
			parseStatLines(cache.get(TAG_STAT));
		}
		if (cache.containsKey(TAG_ALIGNMENT))
		{
			for (Iterator it = cache.get(TAG_ALIGNMENT).iterator(); it.hasNext();)
			{
				parseAlignmentLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_RACE))
		{
			for (Iterator it = cache.get(TAG_RACE).iterator(); it.hasNext();)
			{
				parseRaceLine((String)it.next());
			}
		}

		/*
		 * #System Information
		 * CAMPAIGNS:>:-delimited list<
		 * VERSION:x.x.x
		 * ROLLMETHOD:xxx
		 * PURCHASEPOINTS:Y or N|TYPE:>living City, Living greyhawk, etc<
		 * UNLIMITEDPOOLCHECKED:Y or N
		 * POOLPOINTS:>numeric value 0-?<
		 * GAMEMODE:DnD
		 * TABLABEL:0
		 */
		if (cache.containsKey(TAG_POOLPOINTS))
		{
			for (Iterator it = cache.get(TAG_POOLPOINTS).iterator(); it.hasNext();)
			{
				parsePoolPointsLine((String)it.next());
			}
		}

		/*
		 * #Character Class(es)
		 * CLASS:Fighter|LEVEL=3
		 * CLASSABILITIESLEVEL:Fighter=1(>This would only display up to the level the character has already,)
		 * CLASSABILITIESLEVEL:Fighter=2(>with any special abilities not covered by other areas,)
		 * CLASSABILITIESLEVEL:Fighter=3(>such as skills, feats, etc., but would list SA's, and the like<)
		 * CLASS:Wizard|LEVEL=1
		 * CLASSABILITIESLEVEL:Wizard=1(SA's, MEMORIZE:Y, etc)
		 */
		if (cache.containsKey(TAG_CLASS))
		{
			for (Iterator it = cache.get(TAG_CLASS).iterator(); it.hasNext();)
			{
				parseClassLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_CLASSABILITIESLEVEL))
		{
			for (Iterator it = cache.get(TAG_CLASSABILITIESLEVEL).iterator(); it.hasNext();)
			{
				parseClassAbilitiesLevelLine((String)it.next());
			}
		}

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 */
		if (cache.containsKey(TAG_EXPERIENCE))
		{
			for (Iterator it = cache.get(TAG_EXPERIENCE).iterator(); it.hasNext();)
			{
				parseExperienceLine((String)it.next());
			}
		}

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		if (cache.containsKey(TAG_TEMPLATESAPPLIED))
		{
			for (Iterator it = cache.get(TAG_TEMPLATESAPPLIED).iterator(); it.hasNext();)
			{
				parseTemplateLine((String)it.next());
			}
		}

		/*
		 * ###############################################################
		 * Character Skills methods
		 * ###############################################################
		 */
		/*
		 * #Character Skills
		 * CLASSBOUGHT:Fighter
		 * SKILL:Alchemy|CLASS:N|COST:2|RANK:7  (Should be Obvious what each of these does, I hope ;p)
		 * SKILL:Survival|CLASS:Y|COST:1|SYNERGY:Wilderness Lore=5=2|RANK:10
		 * CLASSBOUGHT:Wizard
		 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK:7
                 *
                 * CLASSBOUGHT not supported
		 */
		if (cache.containsKey(TAG_SKILL))
		{
			for (Iterator it = cache.get(TAG_SKILL).iterator(); it.hasNext();)
			{
				parseSkillLine((String)it.next());
			}
		}

		/*
		 * Anything that is already Pipe Delimited should be in parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
		 */
		if (cache.containsKey(TAG_FEAT))
		{
			for (Iterator it = cache.get(TAG_FEAT).iterator(); it.hasNext();)
			{
				parseFeatLine((String)it.next());
			}
		}

		/*
		 * This is the REALLY ugly part for all characters as it should contain ALL the information for the equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Sword (Long)|COST:5|WT:5|>other info<|CARRIED:Y|EQUIPPED:Y|LOCATION:Primary
		 * EQUIPNAME:Backpack|COST:5|WT:5|CONTAINS:(blah string)|CARRIED:Y|EQUIPPED:Y|TOTALWT:10
		 * EQUIPNAME:Rope (Silk)|COST:5|WT:5|CARRIED:Y|EQUIPPED:N|LOCATION:Backpack
		 */
		if (cache.containsKey(TAG_EQUIPNAME))
		{
			for (Iterator it = cache.get(TAG_EQUIPNAME).iterator(); it.hasNext();)
			{
				parseEquipmentLine((String)it.next());
			}
		}

		/*
		 * #Character Deity/Domain
		 * DEITY:Yondalla|DEITYDOMAINS:Good,Law,Protection|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD(>list of level by level spells)
		 */
		if (cache.containsKey(TAG_DEITY))
		{
			for (Iterator it = cache.get(TAG_DEITY).iterator(); it.hasNext();)
			{
				parseDeityLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_DOMAIN))
		{
			for (Iterator it = cache.get(TAG_DOMAIN).iterator(); it.hasNext();)
			{
				parseDomainLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_DOMAINSPELLS))
		{
			for (Iterator it = cache.get(TAG_DOMAINSPELLS).iterator(); it.hasNext();)
			{
				parseDomainSpellsLine((String)it.next());
			}
		}

		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
		 */
		if (cache.containsKey(TAG_SPELLNAME))
		{
			for (Iterator it = cache.get(TAG_SPELLNAME).iterator(); it.hasNext();)
			{
				parseSpellLine((String)it.next());
			}
		}

		/*
		 * #Character Description/Bio/History
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		if (cache.containsKey(TAG_CHARACTERBIO))
		{
			for (Iterator it = cache.get(TAG_CHARACTERBIO).iterator(); it.hasNext();)
			{
				parseCharacterBioLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_CHARACTERDESC))
		{
			for (Iterator it = cache.get(TAG_CHARACTERDESC).iterator(); it.hasNext();)
			{
				parseCharacterDescLine((String)it.next());
			}
		}

		/*
		 * #Character Notes Tab
		 */
		if (cache.containsKey(TAG_NOTE))
		{
			for (Iterator it = cache.get(TAG_NOTE).iterator(); it.hasNext();)
			{
				parseNoteLine((String)it.next());
			}
		}

		/*
		 * #Character Bio
		 * CHARACTERNAME:Code Monkey
		 * TABNAME:Code Monkey the Best Ever No Really!
		 * PLAYERNAME:Jason Monkey
		 * HEIGHT:75
		 * WEIGHT:198
		 * AGE:17
		 * GENDER:text
		 * HANDED:text
		 * SKIN:text
		 * EYECOLOR:text
		 * HAIRCOLOR:text
		 * HAIRSTYLE:text
		 * LOCATION:text
		 * RESIDENCE:text
		 * PERSONALITYTRAIT1:text
		 * PERSONALITYTRAIT2:text
		 * SPEECHPATTERN:text
		 * PHOBIAS:text
		 * INTERESTS:text
		 * CATCHPHRASE:text
		 */
		if (cache.containsKey(TAG_CHARACTERNAME))
		{
			for (Iterator it = cache.get(TAG_CHARACTERNAME).iterator(); it.hasNext();)
			{
				parseCharacterNameLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_TABNAME))
		{
			for (Iterator it = cache.get(TAG_TABNAME).iterator(); it.hasNext();)
			{
				parseTabNameLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_PLAYERNAME))
		{
			for (Iterator it = cache.get(TAG_PLAYERNAME).iterator(); it.hasNext();)
			{
				parsePlayerNameLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_HEIGHT))
		{
			for (Iterator it = cache.get(TAG_HEIGHT).iterator(); it.hasNext();)
			{
				parseHeightLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_WEIGHT))
		{
			for (Iterator it = cache.get(TAG_WEIGHT).iterator(); it.hasNext();)
			{
				parseWeightLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_AGE))
		{
			for (Iterator it = cache.get(TAG_AGE).iterator(); it.hasNext();)
			{
				parseAgeLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_GENDER))
		{
			for (Iterator it = cache.get(TAG_GENDER).iterator(); it.hasNext();)
			{
				parseGenderLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_HANDED))
		{
			for (Iterator it = cache.get(TAG_HANDED).iterator(); it.hasNext();)
			{
				parseHandedLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_SKINCOLOR))
		{
			for (Iterator it = cache.get(TAG_SKINCOLOR).iterator(); it.hasNext();)
			{
				parseSkinColorLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_EYECOLOR))
		{
			for (Iterator it = cache.get(TAG_EYECOLOR).iterator(); it.hasNext();)
			{
				parseEyeColorLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_HAIRCOLOR))
		{
			for (Iterator it = cache.get(TAG_HAIRCOLOR).iterator(); it.hasNext();)
			{
				parseHairColorLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_HAIRSTYLE))
		{
			for (Iterator it = cache.get(TAG_HAIRSTYLE).iterator(); it.hasNext();)
			{
				parseHairStyleLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_LOCATION))
		{
			for (Iterator it = cache.get(TAG_LOCATION).iterator(); it.hasNext();)
			{
				parseLocationLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_RESIDENCE))
		{
			for (Iterator it = cache.get(TAG_RESIDENCE).iterator(); it.hasNext();)
			{
				parseResidenceLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_PERSONALITYTRAIT1))
		{
			for (Iterator it = cache.get(TAG_PERSONALITYTRAIT1).iterator(); it.hasNext();)
			{
				parsePersonalityTrait1Line((String)it.next());
			}
		}
		if (cache.containsKey(TAG_PERSONALITYTRAIT2))
		{
			for (Iterator it = cache.get(TAG_PERSONALITYTRAIT2).iterator(); it.hasNext();)
			{
				parsePersonalityTrait2Line((String)it.next());
			}
		}
		if (cache.containsKey(TAG_SPEECHPATTERN))
		{
			for (Iterator it = cache.get(TAG_SPEECHPATTERN).iterator(); it.hasNext();)
			{
				parseSpeechPatternLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_PHOBIAS))
		{
			for (Iterator it = cache.get(TAG_PHOBIAS).iterator(); it.hasNext();)
			{
				parsePhobiasLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_INTERESTS))
		{
			for (Iterator it = cache.get(TAG_INTERESTS).iterator(); it.hasNext();)
			{
				parseInterestsLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_CATCHPHRASE))
		{
			for (Iterator it = cache.get(TAG_CATCHPHRASE).iterator(); it.hasNext();)
			{
				parseCatchPhraseLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_PORTRAIT))
		{
			for (Iterator it = cache.get(TAG_PORTRAIT).iterator(); it.hasNext();)
			{
				parsePortraitLine((String)it.next());
			}
		}
	}

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */

	private void parsePoolPointsLine(String line)
	{
		try
		{
			aPC.setPoolAmount(Integer.parseInt(line.substring(TAG_POOLPOINTS.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Pool Points line ignored: " + line;
			warnings.add(message);
		}
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */

	private void parseCharacterNameLine(String line)
	{
		aPC.setName(line.substring(TAG_CHARACTERNAME.length() + 1));
	}

	private void parseTabNameLine(String line)
	{
		aPC.setTabName(line.substring(TAG_TABNAME.length() + 1));
	}

	private void parsePlayerNameLine(String line)
	{
		aPC.setPlayersName(line.substring(TAG_PLAYERNAME.length() + 1));
	}

	private void parseHeightLine(String line)
	{
		try
		{
			aPC.setHeight(Integer.parseInt(line.substring(TAG_HEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Height line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseWeightLine(String line)
	{
		try
		{
			aPC.setWeight(Integer.parseInt(line.substring(TAG_WEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Weight line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseAgeLine(String line)
	{
		try
		{
			aPC.setAge(Integer.parseInt(line.substring(TAG_AGE.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Age line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseGenderLine(String line)
	{
		aPC.setGender(line.substring(TAG_GENDER.length() + 1));
	}

	private void parseHandedLine(String line)
	{
		aPC.setHanded(line.substring(TAG_HANDED.length() + 1));
	}

	private void parseSkinColorLine(String line)
	{
		aPC.setSkinColor(line.substring(TAG_SKINCOLOR.length() + 1));
	}

	private void parseEyeColorLine(String line)
	{
		aPC.setEyeColor(line.substring(TAG_EYECOLOR.length() + 1));
	}

	private void parseHairColorLine(String line)
	{
		aPC.setHairColor(line.substring(TAG_HAIRCOLOR.length() + 1));
	}

	private void parseHairStyleLine(String line)
	{
		aPC.setHairStyle(decodeChars(line.substring(TAG_HAIRSTYLE.length() + 1)));
	}

	private void parseLocationLine(String line)
	{
		aPC.setLocation(line.substring(TAG_LOCATION.length() + 1));
	}

	private void parseResidenceLine(String line)
	{
		aPC.setLocation(line.substring(TAG_RESIDENCE.length() + 1));
	}

	private void parsePersonalityTrait1Line(String line)
	{
		aPC.setTrait1(decodeChars(line.substring(TAG_PERSONALITYTRAIT1.length() + 1)));
	}

	private void parsePersonalityTrait2Line(String line)
	{
		aPC.setTrait2(decodeChars(line.substring(TAG_PERSONALITYTRAIT2.length() + 1)));
	}

	private void parseSpeechPatternLine(String line)
	{
		aPC.setSpeechTendency(decodeChars(line.substring(TAG_SPEECHPATTERN.length() + 1)));
	}

	private void parsePhobiasLine(String line)
	{
		aPC.setPhobias(decodeChars(line.substring(TAG_PHOBIAS.length() + 1)));
	}

	private void parseInterestsLine(String line)
	{
		aPC.setInterests(decodeChars(line.substring(TAG_INTERESTS.length() + 1)));
	}

	private void parseCatchPhraseLine(String line)
	{
		aPC.setCatchPhrase(decodeChars(line.substring(TAG_CATCHPHRASE.length() + 1)));
	}

	private void parsePortraitLine(String line)
	{
		aPC.setPortraitPath(line.substring(TAG_PORTRAIT.length() + 1));
	}

	/*
	 * ###############################################################
         * Character Attributes methods
	 * ###############################################################
	 */

	private void parseStatLines(List lines) throws PCGParseException
	{
		if (lines.size() == Globals.s_ATTRIBSHORT.length)
		{
			final Set seenStats = new HashSet();

			String line;
			for (Iterator it = lines.iterator(); it.hasNext();)
			{
				line = (String)it.next();

				int index = line.indexOf("=");
				if ((index > -1) && seenStats.add(line.substring(0, index).toUpperCase()))
				{
					parseStatLine(line);
				}
				else
				{
					final String message =
					  "Invalid attribute specification. " +
					  "Cannot load character.";
					throw new PCGParseException("parseStatLines", line, message);
				}
			}
		}
		else
		{
			final String message =
			  "Number of attributes for character is " + lines.size() + ". " +
			  "PCGen is currently using " + Globals.s_ATTRIBSHORT.length + ". " +
			  "Cannot load character.";
			throw new PCGParseException("parseStatLines", "N/A", message);
		}
	}

	private void parseStatLine(String line) throws PCGParseException
	{
		final StringTokenizer tokens = new StringTokenizer(line.substring(TAG_STAT.length() + 1), "=");

		if (tokens.countTokens() == 2)
		{
			int index = Globals.getStatFromAbbrev(tokens.nextToken());

			if (index > -1)
			{
				try
				{
					((PCStat)aPC.getStatList().getStats().get(index)).setBaseScore(Integer.parseInt(tokens.nextToken()));
				}
				catch (NumberFormatException nfe)
				{
					throw new PCGParseException("parseStatLine", line, nfe.getMessage());
				}
			}
			else
			{
				final String message =
				  "Invalid attribute specification. " +
				  "Cannot load character.";
				throw new PCGParseException("parseStatLine", line, message);
			}
		}
		else
		{
			final String message =
			  "Invalid attribute specification. " +
			  "Cannot load character.";
			throw new PCGParseException("parseStatLine", line, message);
		}
	}

	private void parseAlignmentLine(String line)
	{
		final String alignment = line.substring(TAG_ALIGNMENT.length() + 1);

		for (int i = 0; i < Constants.s_ALIGNSHORT.length; i++)
		{
			if (Constants.s_ALIGNSHORT[i].equalsIgnoreCase(alignment))
			{
				aPC.setAlignment(i, true);
				return;
			}
		}

		final String message = "Invalid alignment specification.";
		warnings.add(message);
	}

	private void parseRaceLine(String line) throws PCGParseException
	{
		final String race_name = line.substring(TAG_RACE.length() + 1);
		final Race aRace = Globals.getRaceKeyed(race_name);

		if (aRace != null)
		{
			aPC.setRace(aRace);
		}
		else
		{
			final String message =
			  "Race not found: " + race_name + ".\n" +
			  PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
			throw new PCGParseException("parseRaceLine", line, message);
		}

		// TODO
		// adjust for more information according to PCGVer1Creator.appendRaceLine
	}

	/*
	 * ###############################################################
         * Character Class(es) methods
	 * ###############################################################
	 */

	private void parseClassLine(String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Classes are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 14-08-02
			 */
			throw new PCGParseException("parseClassLine", line, pcgpex.getMessage());
		}

		PCClass aPCClass = null;
		String elementName;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the class key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();

			aPCClass = Globals.getClassKeyed(element.getText());
			if (aPCClass != null)
			{
				// Icky: Need to redesign the way classes work!
				// Icky: Having to clone the class here is UGLY!
				aPCClass = (PCClass)aPCClass.clone();
			}
			else
			{
				final String message = "Could not add class: " + element.getText();
				warnings.add(message);
				return;
			}
		}

		int level = -1;
		int skillPool = -1;

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			elementName = element.getName();

			if (elementName.equals(TAG_LEVEL))
			{
				try
				{
					level = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid level specification: " + element.getText();
					warnings.add(message);
				}
			}
			else if (elementName.equals(TAG_SKILLPOOL))
			{
				try
				{
					skillPool = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid skill pool specification: " + element.getText();
					warnings.add(message);
				}
			}
			else if (elementName.equals(TAG_CANCASTPERDAY))
			{
				// TODO
			}
		}

		if (level > -1)
		{
			aPC.getClassList().add(aPCClass);
			for (int i = 0; i < level; i++)
			{
				aPCClass.addLevel(false);
			}
		}
		if (skillPool > -1)
		{
			aPCClass.setSkillPool(skillPool);
		}

		// TODO:
		// add support for prohibitedString
		// add support for subClassName
	}

	private void parseClassAbilitiesLevelLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Class abilities are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 14-08-02
			 */
			final String message =
			  "Illegal Class abilities line ignored: " + line + "\n" +
			  "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		int level = -1;
		PCClass aPCClass = null;
		String elementName;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the class key name and level!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();
			int index = element.getText().indexOf("=");

			if (index < 0)
			{
				final String message = "Invalid class/level specification: " + element.getText();
				warnings.add(message);
				return;
			}

			String className = element.getText().substring(0, index);
			aPCClass = aPC.getClassKeyed(className);

			if (aPCClass == null)
			{
				final String message = "Could not find class: " + className;
				warnings.add(message);
				return;
			}

			try
			{
				level = Integer.parseInt(element.getText().substring(index + 1));
			}
			catch (NumberFormatException nfe)
			{
				final String message = "Invalid level specification: " + element.getText();
				warnings.add(message);
				return;
			}

			if (level < 1)
			{
				final String message = "Invalid level specification: " + element.getText();
				warnings.add(message);
				return;
			}
		}

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			elementName = element.getName();

			if (elementName.equals(TAG_HITPOINTS))
			{
				try
				{
					//aPCClass.hitPointList()[level - 1] = new Integer(element.getText());
					aPCClass.setHitPoint(level - 1, new Integer(element.getText()));

				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid hitpoint specification: " +
					  elementName + ":" + element.getText();
					warnings.add(message);
				}

			}
			else if (elementName.equals(TAG_DATA))
			{
				// TODO
				// for now it's ok to ignore it!
			}
		}

		// TODO:
		// process data
		//
		// need to add some consistency checks here to avoid
		// - duplicate entries for one and the same class/level pair
		// - missing entries for a given class/level pair
	}

	/*
	 * ###############################################################
         * Character Experience methods
	 * ###############################################################
	 */

	private void parseExperienceLine(String line) throws PCGParseException
	{
		try
		{
			aPC.setXP(Integer.parseInt(line.substring(TAG_EXPERIENCE.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			throw new PCGParseException("parseExperienceLine", line, nfe.getMessage());
		}
	}

	/*
	 * ###############################################################
         * Character Templates methods
	 * ###############################################################
	 */

	private void parseTemplateLine(String line)
	{
		final PCTemplate aPCTemplate = Globals.getTemplateNamed(line.substring(TAG_TEMPLATESAPPLIED.length() + 1));
		if (aPCTemplate != null)
		{
			aPC.addTemplate(aPCTemplate, false);
		}
	}

	/*
	 * ###############################################################
         * Character Skills methods
	 * ###############################################################
	 */

	private void parseSkillLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Skills are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 10-08-02
			 */
			final String message =
			  "Illegal Skill line ignored: " + line + "\n" +
			  "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		Skill aSkill = null;
		String elementName;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();

			aSkill = aPC.getSkillKeyed(element.getText());
			if (aSkill == null)
			{

				aSkill = Globals.getSkillKeyed(element.getText());

				if (aSkill != null)
				{
					// Icky: Need to redesign the way skills work!
					// Icky: Having to clone the skill here is UGLY!
					aSkill = (Skill)aSkill.clone();
					aPC.getSkillList().add(aSkill);
				}
				else
				{
					final String message = "Could not add skill: " + element.getText();
					warnings.add(message);
					return;
				}
			}
		}

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			elementName = element.getName();

			if (elementName.equals(TAG_SYNERGY))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else if (elementName.equals(TAG_OUTPUTORDER))
			{
				int outputindex = 0;
				try
				{
					outputindex = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// This is not critical.
					// Maybe warn the user?
				}
				aSkill.setOutputIndex(outputindex);
			}
			else if (elementName.equals(TAG_CLASSBOUGHT))
			{

				PCGElement childClass = null;
				PCGElement childRanks = null;

				PCGElement child;
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					child = (PCGElement)it2.next();

					if (child.getName().equals(TAG_CLASS))
					{
						childClass = child;
					}
					else if (child.getName().equals(TAG_RANKS))
					{
						childRanks = child;
					}
				}

				if (childClass == null)
				{
					final String message = "Invalid class/ranks specification: " + line;
					warnings.add(message);
					continue;
				}

				if (childRanks == null)
				{
					final String message = "Invalid class/ranks specification: " + line;
					warnings.add(message);
					continue;
				}

				PCClass aPCClass = aPC.getClassKeyed(childClass.getText());
				if (aPCClass == null)
				{
					final String message = "Could not find class: " + childClass.getText();
					warnings.add(message);
					continue;
				}

				try
				{
					double ranks = Double.parseDouble(childRanks.getText());
					aSkill.modRanks(ranks, aPCClass, true);
				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid ranks specification: " + childRanks.getText();
					warnings.add(message);
					continue;
				}
			}
			else if (elementName.equals(TAG_ASSOCIATEDDATA))
			{
				aSkill.addAssociated(element.getText());
			}
		}

		// TODO:
		// somehow the skill points value in PlayerCharacter is wrong
		// need to fix that ASAP
	}

	/*
	 * ###############################################################
         * Character Feats methods
	 * ###############################################################
	 */

	private void parseFeatLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Feats are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 10-08-02
			 */
			final String message =
			  "Illegal Feat line ignored: " + line + "\n" +
			  "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		Feat aFeat = null;
		String elementName;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();

			aFeat = Globals.getFeatKeyed(element.getText());
			if (aFeat != null)
			{

				aFeat = (Feat)aFeat.clone();
				aPC.modFeat(aFeat.getKeyName(), true, !aFeat.isMultiples());
				if (aFeat.isMultiples() &&
				  (aFeat.getAssociatedCount() == 0) &&
				  (aPC.getFeatKeyed(aFeat.getKeyName()) == null))
				{
					aPC.addFeat(aFeat);
				}

				// is this line really necessary ???
				aFeat = aPC.getFeatKeyed(aFeat.getKeyName());
			}
			else
			{
				final String message = "Could not add feat: " + element.getText();
				warnings.add(message);
				return;
			}
		}

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			elementName = element.getName();

			if (elementName.equals(TAG_APPLIEDTO))
			{

				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					aPC.addWeaponProf(element.getText());
				}
				else if ((aFeat.isMultiples() && aFeat.isStacks()) ||
				  !aFeat.containsAssociated(element.getText()))
				{
					aFeat.addAssociated(element.getText());
				}
			}
			else if (elementName.equals(TAG_SAVE))
			{

				if (element.getText().startsWith("BONUS") &&
				  (element.getText().length() > 6))
				{
					aFeat.addBonusList(element.getText().substring(6));
				}

				aFeat.addSave(element.getText());
			}
		}

		// TODO
		// process all additional information
	}

	/*
	 * ###############################################################
         * Character Equipment methods
	 * ###############################################################
	 */

	private void parseEquipmentLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Deity/Domain methods
	 * ###############################################################
	 */

	private void parseDeityLine(String line)
	{
		// TODO
	}

	private void parseDomainLine(String line)
	{
		// TODO
	}

	private void parseDomainSpellsLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Spells Information methods
	 * ###############################################################
	 */

	private void parseSpellLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Description/Bio/History methods
	 * ###############################################################
	 */

	private void parseCharacterBioLine(String line)
	{
		aPC.setBio(decodeChars(line.substring(TAG_CHARACTERBIO.length() + 1)));
	}

	private void parseCharacterDescLine(String line)
	{
		aPC.setDescription(decodeChars(line.substring(TAG_CHARACTERDESC.length() + 1)));
	}

	/*
	 * ###############################################################
         * Character Notes Tab methods
	 * ###############################################################
	 */

	private void parseNoteLine(String line)
	{
		final NoteItem ni = new NoteItem(-1, -1, "", "");
		final StringTokenizer tokens = new StringTokenizer(line, "|");

		String tag;
		String data;
		String cToken;
		while (tokens.hasMoreTokens())
		{
			cToken = tokens.nextToken();

			int index = cToken.indexOf(":");
			if (index < 0)
			{
				final String message = "Illegal Notes line ignored: " + line;
				warnings.add(message);

				break;
			}

			tag = cToken.substring(0, index);
			data = cToken.substring(index + 1);

			if (tag.equals(TAG_NOTE))
			{
				ni.setName(data);
			}
			else if (tag.equals(TAG_ID))
			{
				try
				{
					ni.setIdValue(Integer.parseInt(data));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String message = "Illegal Notes line ignored: " + line;
					warnings.add(message);

					break;
				}
			}
			else if (tag.equals(TAG_PARENTID))
			{
				try
				{
					ni.setParentId(Integer.parseInt(data));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String message = "Illegal Notes line ignored: " + line;
					warnings.add(message);

					break;
				}
			}
			else if (tag.equals(TAG_VALUE))
			{
				ni.setValue(decodeChars(data));
			}
			else
			{
			}
		}

		if (ni.getId() > -1)
		{
			aPC.addNotesItem(ni);
		}
	}

	/*
	 * ###############################################################
         * Miscellaneous methods
	 * ###############################################################
	 */

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 28-04-02
	 *
	 * @param line
	 */
	private boolean isComment(String line)
	{
		return line.trim().startsWith("#");
	}

	/**
	 * decode characters
	 * "\\" <- "\\\\"
	 * "\n" <- "\\n"
	 * "\r" <- "\\r"
	 * "\f" <- "\\f"
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param s   the String to decode
	 * @return the decoded String
	 */
	private String decodeChars(String s)
	{
		StringBuffer buffer = new StringBuffer();
		char[] chars = s.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if ((chars[i] == '\\') && ((i + 1) < chars.length))
			{
				switch (chars[i + 1])
				{
					case '\\':
						buffer.append('\\');
						i++;
						break;
					case 'n':
						buffer.append('\n');
						i++;
						break;
					case 'r':
						buffer.append('\r');
						i++;
						break;
					case 'f':
						buffer.append('\f');
						i++;
						break;
					default:
						buffer.append(chars[i]);
						break;
				}
			}
			else
			{
				buffer.append(chars[i]);
			}
		}

		return buffer.toString();
	}

	/*
	 * ###############################################################
         * Inner classes
	 * ###############################################################
	 */

	private class Cache
	{
		private Hashtable hashtable;

		public Cache()
		{
			hashtable = new Hashtable();
		}

		public Cache(int initialCapacity)
		{
			hashtable = new Hashtable(initialCapacity);
		}

		public void put(String key, String value)
		{
			if (hashtable.containsKey(key))
			{
				((List)hashtable.get(key)).add(value);
			}
			else
			{
				List values = new ArrayList();
				values.add(value);
				hashtable.put(key, values);
			}
		}

		public List get(String key)
		{
			return (List)hashtable.get(key);
		}

		public boolean containsKey(String key)
		{
			return hashtable.containsKey(key);
		}
	}

	private class PCGElement
	{
		private final String name;

		private String text;
		private List children;

		public PCGElement(String name)
		{
			this.name = name;
		}

		public void addContent(PCGElement child)
		{
			if (children == null)
			{
				this.children = new ArrayList(0);
			}
			children.add(child);
		}

		public void addContent(String text)
		{
			this.text = text;
		}

		public List getChildren()
		{
			if (children == null)
			{
				this.children = new ArrayList(0);
			}
			return children;
		}

		public List getChildren(String name)
		{
			List list = new ArrayList(0);

			PCGElement element;
			for (Iterator it = getChildren().iterator(); it.hasNext();)
			{
				element = (PCGElement)it.next();
				if (element.getName().equals(name))
				{
					list.add(element);
				}
			}

			return list;
		}

		public String getName()
		{
			return name;
		}

		public String getText()
		{
			return (text != null) ? text : "";
		}

		public String toString()
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("<").append(getName()).append(">\n");
			buffer.append("<text>").append(getText()).append("</text>\n");
			for (Iterator it = getChildren().iterator(); it.hasNext();)
			{
				buffer.append("").append(it.next().toString()).append("\n");
			}
			buffer.append("</").append(getName()).append(">");

			return buffer.toString();
		}

	}

	private class PCGTokenizer
	{
		private final List elements;

		private final String innerDelimiter;
		private final String outerDelimiter;
		private final String nestedStartDelimiter;
		private final String nestedStopDelimiter;

		private final char nestedStartDelimiterChar;
		private final char nestedStopDelimiterChar;

		/**
		 * Constructor
		 */
		public PCGTokenizer(String line) throws PCGParseException
		{
			this(line, ":|[]");
		}

		/**
		 * Constructor
		 * <br>
		 * @param line           a String to tokenize
		 * @param delimiters     a FOUR-character String specifying the four needed delimiters:
		 *                       <ol>
		 *                           <li>the inner delimiter for a PCGElement</li>
		 *                           <li>the outer delimiter for a PCGElement</li>
		 *                           <li>the start delimiter for nested PCGElements</li>
		 *                           <li>the stop delimiter for nested PCGElement</li>
		 *                       </ol>
		 */
		public PCGTokenizer(String line,
		  String delimiters) throws PCGParseException
		{
			char[] dels = delimiters.toCharArray();

			this.innerDelimiter = String.valueOf(dels[0]);
			this.outerDelimiter = String.valueOf(dels[1]);
			this.nestedStartDelimiter = String.valueOf(dels[2]);
			this.nestedStopDelimiter = String.valueOf(dels[3]);

			this.nestedStartDelimiterChar = nestedStartDelimiter.charAt(0);
			this.nestedStopDelimiterChar = nestedStopDelimiter.charAt(0);

			this.elements = new ArrayList(0);

			tokenizeLine(line);
		}

		public List getElements()
		{
			return elements;
		}

		private void tokenizeLine(String line) throws PCGParseException
		{
			checkSyntax(line);

			final PCGElement root = new PCGElement("root");
			tokenizeLine(root, line);
			elements.addAll(root.getChildren());
		}

		private void tokenizeLine(PCGElement parent, String line) throws PCGParseException
		{
			final String dels =
			  outerDelimiter +
			  nestedStartDelimiter +
			  nestedStopDelimiter;
			final StringTokenizer tokens = new StringTokenizer(line, dels, true);

			int nestedDepth = 0;
			String token = null;
			String elementName = null;
			final StringBuffer buffer = new StringBuffer();

			PCGElement element;

			while (tokens.hasMoreTokens())
			{
				token = tokens.nextToken();

				if (token.equals(outerDelimiter))
				{

					if (nestedDepth == 0)
					{
						token = buffer.toString();
						int index = token.indexOf(innerDelimiter);
						if (index > -1)
						{
							buffer.delete(0, buffer.length());

							element = new PCGElement(token.substring(0, index));
							element.addContent(token.substring(index + 1));
							parent.addContent(element);
						}
						else
						{
							final String message = "Malformed PCG element: " + token;
							throw new PCGParseException("PCGTokenizer::tokenizeLine",
							  line, message);
						}
					}
					else
					{
						buffer.append(token);
					}
				}
				else if (token.equals(nestedStartDelimiter))
				{

					if (nestedDepth == 0)
					{
						token = buffer.toString();
						int index = token.indexOf(innerDelimiter);
						if (index == token.length() - 1)
						{
							buffer.delete(0, buffer.length());

							elementName = token.substring(0, index);
						}
						else
						{
							final String message = "Malformed PCG element: " + token;
							throw new PCGParseException("PCGTokenizer::tokenizeLine",
							  line, message);
						}
					}
					else
					{
						buffer.append(token);
					}

					nestedDepth++;
				}
				else if (token.equals(nestedStopDelimiter))
				{

					nestedDepth--;

					if (nestedDepth == 0)
					{
						element = new PCGElement(elementName);
						tokenizeLine(element, buffer.toString());
						parent.addContent(element);
						buffer.delete(0, buffer.length());
					}
					else
					{
						buffer.append(token);
					}
				}
				else
				{
					buffer.append(token);
				}
			}

			if (buffer.length() > 0)
			{
				token = buffer.toString();
				int index = token.indexOf(innerDelimiter);
				if (index > -1)
				{
					buffer.delete(0, buffer.length());

					element = new PCGElement(token.substring(0, index));
					element.addContent(token.substring(index + 1));
					parent.addContent(element);
				}
				else
				{
					final String message = "Malformed PCG element: " + token;
					throw new PCGParseException("PCGTokenizer::tokenizeLine",
					  line, message);
				}
			}
		}

		private void checkSyntax(String line) throws PCGParseException
		{
			final char[] chars = line.toCharArray();

			int delimCount = 0;
			for (int i = 0; i < chars.length; i++)
			{
				if (chars[i] == nestedStartDelimiterChar)
				{
					delimCount++;
				}
				else if (chars[i] == nestedStopDelimiterChar)
				{
					delimCount--;
				}
			}

			if (delimCount < 0)
			{
				final String message = "Missing " + nestedStartDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax",
				  line, message);
			}
			else if (delimCount > 0)
			{
				final String message = "Missing " + nestedStopDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax",
				  line, message);
			}
		}

		private String getFirstTag(String line)
		{
			return line.substring(0, line.indexOf(innerDelimiter));
		}
	}
}
