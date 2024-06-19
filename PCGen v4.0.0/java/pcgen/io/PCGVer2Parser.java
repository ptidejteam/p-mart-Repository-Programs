/*
 * PCGVer2Parser.java
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;

/**
 * <code>PCGVer2Parser</code>
 * 
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer2Parser implements PCGParser, IOConstants
{
	private List warnings = new ArrayList();
	private PlayerCharacter aPC;
	private Cache cache;

        private final Map containers = new HashMap();
        private final Set seenStats = new HashSet();

        
	/**
	 * Constructor
	 */
	public PCGVer2Parser(PlayerCharacter aPC)
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
		 * #System Information
		 * CAMPAIGN:WotC - D&D Core Rules I - Players Hand Book
		 * CAMPAIGN:WotC - DnD Core Rules II - Dungeon Masters Guide
                 * ...                
                 *
                 * first thing to do is checking campaigns - no matter what!
                 */
                if (cache.containsKey(TAG_CAMPAIGN))
		{
			parseCampaignLines(cache.get(TAG_CAMPAIGN));
		}

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
			for (Iterator it = cache.get(TAG_STAT).iterator(); it.hasNext();)
			{
				parseStatLine((String)it.next());
			}

                        checkStats();
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
		 * AUTOSPELLS:Y or N
		 */
		if (cache.containsKey(TAG_POOLPOINTS))
		{
			for (Iterator it = cache.get(TAG_POOLPOINTS).iterator(); it.hasNext();)
			{
				parsePoolPointsLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_AUTOSPELLS))
		{
			for (Iterator it = cache.get(TAG_AUTOSPELLS).iterator(); it.hasNext();)
			{
				parseAutoSpellsLine((String)it.next());
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

                        checkSkillPools();
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
		 * #Character Languages
                 * LANGUAGE:Chondathan|LANGUAGE:Common|LANGUAGE:Literacy
		 */
		if (cache.containsKey(TAG_LANGUAGE))
		{
			for (Iterator it = cache.get(TAG_LANGUAGE).iterator(); it.hasNext();)
			{
				parseLanguageLine((String)it.next());
			}
		}

		/*
		 * Anything that is already Pipe Delimited should be in parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
                 * FEATPOOL:>number of remaining feats<
		 */
		if (cache.containsKey(TAG_FEAT))
		{
			for (Iterator it = cache.get(TAG_FEAT).iterator(); it.hasNext();)
			{
				parseFeatLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_FEATPOOL))
		{
			for (Iterator it = cache.get(TAG_FEATPOOL).iterator(); it.hasNext();)
			{
				parseFeatPoolLine((String)it.next());
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

                        putEquipmentInAppropriateContainer();
		}
		if (cache.containsKey(TAG_EQUIPSET))
		{
                        /*
                         * strangely enough this works even if we create a EquipSet
                         * for content whose container EquipSet has not been created yet
                         *
                         * oh well, we'll just leave the sort code as comments for now -
                         * you never know
                         *
                         * author: Thomas Behr 10-09-02
                         */
//                          Collections.sort(cache.get(TAG_EQUIPSET), new EquipSetLineComparator());
                        
			for (Iterator it = cache.get(TAG_EQUIPSET).iterator(); it.hasNext();)
			{
                                parseEquipmentSetLine((String)it.next());
			}
		}

		/*
		 * #Character Deity/Domain
                 * DEITY:Yondalla|DEITYDOMAINS:[DOMAIN:Good|DOMAIN:Law|DOMAIN:Protection]|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD|SPELLLIST:[SPELL:bla|SPELL:blubber|...]
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

                        sortCharacterSpells();
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
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		if (cache.containsKey(TAG_MASTER))
		{
			for (Iterator it = cache.get(TAG_MASTER).iterator(); it.hasNext();)
			{
				parseMasterLine((String)it.next());
			}
		}
		if (cache.containsKey(TAG_FOLLOWER))
		{
			for (Iterator it = cache.get(TAG_FOLLOWER).iterator(); it.hasNext();)
			{
				parseFollowerLine((String)it.next());
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

		/*
		 * #Character Weapon proficiencies
		 */
		if (cache.containsKey(TAG_WEAPONPROF))
		{
			for (Iterator it = cache.get(TAG_WEAPONPROF).iterator(); it.hasNext();)
			{
				parseWeaponProficienciesLine((String)it.next());
			}
		}
	}

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */

	private void parseCampaignLines(List lines) throws PCGParseException
	{
                final List campaigns = new ArrayList();

                String line;
                PCGTokenizer tokens;
                for (Iterator it = lines.iterator(); it.hasNext();)
                {
                        line = (String)it.next();
                        try
                        {
                                tokens = new PCGTokenizer(line);
                        }
                        catch (PCGParseException pcgpex)
                        {
                                /*
                                 * Campaigns are critical for characters,
                                 * need to stop the load process
                                 *
                                 * Thomas Behr 14-08-02
                                 */
                                throw new PCGParseException("parseCampaignLines", line, pcgpex.getMessage());
                        }

                        Campaign aCampaign;
                        for (Iterator it2 = tokens.getElements().iterator(); it2.hasNext();) {
                                aCampaign = Globals.getCampaignNamed(((PCGElement)it2.next()).getText());
                                if (aCampaign != null)
                                {
                                        if (!aCampaign.isLoaded())
                                        {
                                                campaigns.add(aCampaign);
                                        }
                                }
                        }
                }

                if (campaigns.size() > 0)
                {
                        try
                        {
                                PersistenceManager.loadCampaigns(campaigns);
                        }
                        catch (PersistenceLayerException e)
                        {
                                throw new PCGParseException("parseCampaignLines", "N/A", e.getMessage());
                        }
                        if (Globals.getUseGUI()) {
                                Globals.getRootFrame().getMainSource().updateLoadedCampaignsUI();
                        }
                }

                if (!Globals.displayListsHappy())
                {
                        throw new PCGParseException("parseCampaignLines", "N/A",
                                                    "Insufficient campaign information to load character file.");
                }
        }
  
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

	private void parseAutoSpellsLine(String line)
	{
                aPC.setAutoSpells(line.endsWith("Y"));
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */

	private void parseCharacterNameLine(String line)
	{
		aPC.setName(EntityEncoder.decode(line.substring(TAG_CHARACTERNAME.length() + 1)));
	}

	private void parseTabNameLine(String line)
	{
		aPC.setTabName(EntityEncoder.decode(line.substring(TAG_TABNAME.length() + 1)));
	}

	private void parsePlayerNameLine(String line)
	{
		aPC.setPlayersName(EntityEncoder.decode(line.substring(TAG_PLAYERNAME.length() + 1)));
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
		aPC.setGender(EntityEncoder.decode(line.substring(TAG_GENDER.length() + 1)));
	}

	private void parseHandedLine(String line)
	{
		aPC.setHanded(EntityEncoder.decode(line.substring(TAG_HANDED.length() + 1)));
	}

	private void parseSkinColorLine(String line)
	{
		aPC.setSkinColor(EntityEncoder.decode(line.substring(TAG_SKINCOLOR.length() + 1)));
	}

	private void parseEyeColorLine(String line)
	{
		aPC.setEyeColor(EntityEncoder.decode(line.substring(TAG_EYECOLOR.length() + 1)));
	}

	private void parseHairColorLine(String line)
	{
		aPC.setHairColor(EntityEncoder.decode(line.substring(TAG_HAIRCOLOR.length() + 1)));
	}

	private void parseHairStyleLine(String line)
	{
		aPC.setHairStyle(EntityEncoder.decode(line.substring(TAG_HAIRSTYLE.length() + 1)));
	}

	private void parseLocationLine(String line)
	{
		aPC.setLocation(EntityEncoder.decode(line.substring(TAG_LOCATION.length() + 1)));
	}

	private void parseResidenceLine(String line)
	{
		aPC.setLocation(EntityEncoder.decode(line.substring(TAG_RESIDENCE.length() + 1)));
	}

	private void parsePersonalityTrait1Line(String line)
	{
		aPC.setTrait1(EntityEncoder.decode(line.substring(TAG_PERSONALITYTRAIT1.length() + 1)));
	}

	private void parsePersonalityTrait2Line(String line)
	{
		aPC.setTrait2(EntityEncoder.decode(line.substring(TAG_PERSONALITYTRAIT2.length() + 1)));
	}

	private void parseSpeechPatternLine(String line)
	{
		aPC.setSpeechTendency(EntityEncoder.decode(line.substring(TAG_SPEECHPATTERN.length() + 1)));
	}

	private void parsePhobiasLine(String line)
	{
		aPC.setPhobias(EntityEncoder.decode(line.substring(TAG_PHOBIAS.length() + 1)));
	}

	private void parseInterestsLine(String line)
	{
		aPC.setInterests(EntityEncoder.decode(line.substring(TAG_INTERESTS.length() + 1)));
	}

	private void parseCatchPhraseLine(String line)
	{
		aPC.setCatchPhrase(EntityEncoder.decode(line.substring(TAG_CATCHPHRASE.length() + 1)));
	}

	private void parsePortraitLine(String line)
	{
		aPC.setPortraitPath(EntityEncoder.decode(line.substring(TAG_PORTRAIT.length() + 1)));
	}

	/*
	 * ###############################################################
         * Character Attributes methods
	 * ###############################################################
	 */

	private void parseStatLine(String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Ability scores are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			throw new PCGParseException("parseStatLine", line, pcgpex.getMessage());
		}

                
                Iterator it = tokens.getElements().iterator();

                PCGElement element;
                String statName;
                
		if (it.hasNext())
		{
                        element = (PCGElement)it.next();
                        statName = element.getText();
                        
			int index = Globals.getStatFromAbbrev(statName);

			if ((index > -1) && seenStats.add(statName.toUpperCase()) && (it.hasNext()))
			{
                                element = (PCGElement)it.next();
				try
				{
					((PCStat)aPC.getStatList().getStats().get(index)).setBaseScore(
                                                Integer.parseInt(element.getText()));
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

        private void checkStats() throws PCGParseException
        {
                if (seenStats.size() != Globals.s_ATTRIBSHORT.length) {
			final String message =
			  "Number of attributes for character is " + seenStats.size() + ". " +
			  "PCGen is currently using " + Globals.s_ATTRIBSHORT.length + ". " +
			  "Cannot load character.";
			throw new PCGParseException("parseStatLines", "N/A", message);
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
		final String race_name = EntityEncoder.decode(line.substring(TAG_RACE.length() + 1));
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
		String tag;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the class key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();

			aPCClass = Globals.getClassKeyed(EntityEncoder.decode(element.getText()));
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
			tag = element.getName();

			if (tag.equals(TAG_SUBCLASS))
                        {
				aPCClass.setSubClassName(EntityEncoder.decode(element.getText()));
                        }
                        if (tag.equals(TAG_LEVEL))
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
			else if (tag.equals(TAG_SKILLPOOL))
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
			else if (tag.equals(TAG_CANCASTPERDAY))
			{
				// TODO
			}
			else if (tag.equals(TAG_SPELLBASE))
			{
                                final String spellBase = EntityEncoder.decode(element.getText());
				if ((Globals.getStatFromAbbrev(spellBase.toUpperCase()) > -1) ||
                                    spellBase.equalsIgnoreCase(Constants.s_NONE) ||
                                    spellBase.equalsIgnoreCase("Any") ||
                                    spellBase.equalsIgnoreCase("SPELL"))
				{
                                        aPCClass.setSpellBaseStat(spellBase);
				}
			}
			else if (tag.equals(TAG_PROHIBITED))
			{
				aPCClass.setProhibitedString(EntityEncoder.decode(element.getText()));
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
	}

        private void checkSkillPools() 
        {
                int skillPoints = 0;
                
                for (Iterator it = aPC.getClassList().iterator(); it.hasNext();) {
                        skillPoints += ((PCClass)it.next()).getSkillPool().intValue();
                }

                aPC.setSkillPoints(skillPoints);
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
		String tag;
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

			String className = EntityEncoder.decode(element.getText().substring(0, index));
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

                String specialAbilityName;
                SpecialAbility specialAbility;
                
		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			tag = element.getName();

			if (tag.equals(TAG_HITPOINTS))
			{
				try
				{
					//aPCClass.hitPointList()[level - 1] = new Integer(element.getText());
					aPCClass.setHitPoint(level - 1, new Integer(element.getText()));

				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid hitpoint specification: " +
					  tag + ":" + element.getText();
					warnings.add(message);
				}

			}
			else if (tag.equals(TAG_SAVES))
                        {
                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        aPCClass.addSave(EntityEncoder.decode(((PCGElement)it2.next()).getText()));
                                }
                        }
			else if (tag.equals(TAG_SPECIALTIES))
                        {
                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        aPCClass.getSpecialtyList().add(
                                                EntityEncoder.decode(((PCGElement)it2.next()).getText()));
                                }
                        }
			else if (tag.equals(TAG_SPECIALABILITIES))
                        {
                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        specialAbilityName = EntityEncoder.decode(((PCGElement)it2.next()).getText());
                                        specialAbility = new SpecialAbility(specialAbilityName);
                                        specialAbility.setSource("PCCLASS|" + aPCClass.getName() + "|" + level);

                                        if (!aPC.hasSpecialAbility(specialAbilityName))
                                        {
                                                aPCClass.addSpecialAbilityToList(specialAbility);
                                        }
                                        if (!aPCClass.containsSave(specialAbilityName))
                                        {
                                                aPCClass.addSave(specialAbilityName);
                                        }
                                }
                        }
			else if (tag.equals(TAG_DATA))
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
		final PCTemplate aPCTemplate = Globals.getTemplateNamed(
                        EntityEncoder.decode(line.substring(TAG_TEMPLATESAPPLIED.length() + 1)));
		if (aPCTemplate != null)
		{
			aPC.addTemplate(aPCTemplate);
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
		String tag;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();
                        final String skillKey = EntityEncoder.decode(element.getText());
                        
			aSkill = aPC.getSkillKeyed(skillKey);
			if (aSkill == null)
			{

				aSkill = Globals.getSkillKeyed(skillKey);

				if (aSkill != null)
				{
					// Icky: Need to redesign the way skills work!
					// Icky: Having to clone the skill here is UGLY!
					aSkill = (Skill)aSkill.clone();
					aPC.getSkillList().add(aSkill);
				}
				else
				{
					final String message = "Could not add skill: " + skillKey;
					warnings.add(message);
					return;
				}
			}
		}

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			tag = element.getName();

			if (tag.equals(TAG_SYNERGY))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else if (tag.equals(TAG_OUTPUTORDER))
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
			else if (tag.equals(TAG_CLASSBOUGHT))
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

                                final String childClassKey = EntityEncoder.decode(childClass.getText());
				PCClass aPCClass = aPC.getClassKeyed(childClassKey);
				if (aPCClass == null)
				{
					final String message = "Could not find class: " + childClassKey;
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
			else if (tag.equals(TAG_ASSOCIATEDDATA))
			{
				aSkill.addAssociated(EntityEncoder.decode(element.getText()));
			}
		}
	}

	/*
	 * ###############################################################
         * Character Languages methods
	 * ###############################################################
	 */

	private void parseLanguageLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Languages are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 06-099-02
			 */
			final String message =
			  "Illegal Language line ignored: " + line + "\n" +
			  "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                PCGElement element;
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        aPC.addLanguage(EntityEncoder.decode(element.getText()));
                }
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
		String tag;
		PCGElement element;

		Iterator it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{

			element = (PCGElement)it.next();
                        final String featKey = EntityEncoder.decode(element.getText());
                        
			aFeat = Globals.getFeatKeyed(featKey);
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
				final String message = "Could not add feat: " + featKey;
				warnings.add(message);
				return;
			}
		}

		while (it.hasNext())
		{

			element = (PCGElement)it.next();
			tag = element.getName();

			if (tag.equals(TAG_APPLIEDTO))
			{

                                final String appliedToKey = EntityEncoder.decode(element.getText());
				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					aPC.addWeaponProf(appliedToKey);
				}
				else if ((aFeat.isMultiples() && aFeat.isStacks()) ||
                                         !aFeat.containsAssociated(appliedToKey))
				{
					aFeat.addAssociated(appliedToKey);
				}
			}
			else if (tag.equals(TAG_SAVE))
			{

                                final String saveKey = EntityEncoder.decode(element.getText());
				if (saveKey.startsWith("BONUS") && (saveKey.length() > 6))
				{
					aFeat.addBonusList(saveKey.substring(6));
				}

				aFeat.addSave(saveKey);
			}
		}

		// TODO
		// process all additional information
	}

        private void parseFeatPoolLine(String line) 
        {
		try
		{
                        aPC.setFeats(Integer.parseInt(line.substring(TAG_FEATPOOL.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Feat Pool line ignored: " + line;
			warnings.add(message);
		}
        }

	/*
	 * ###############################################################
         * Character Weapon proficiencies methods
	 * ###############################################################
	 */

	private void parseWeaponProficienciesLine(String line) 
        {
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Weapon proficiencies not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 07-09-02
			 */
			final String message =
                                "Illegal Weapon proficiencies line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                PObject source = null;
                PCGElement element;
                String tag;
                
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {

                        element = (PCGElement)it.next();

                        if (element.getName().equals(TAG_SOURCE)) {

                                String type = "";
                                String name = "";
                                
                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        
                                        element = (PCGElement)it2.next();
                                        tag = element.getName();

                                        if (tag.equals(TAG_TYPE)) {
                                                type = element.getText().toUpperCase();
                                        }
                                        else if (tag.equals(TAG_NAME)) {
                                                name = element.getText();
                                        }
                                }

                                if (type.equals("") || name.equals("")) {
                                        final String message =
                                                "Illegal Weapon proficiencies line ignored: " + line;
                                        warnings.add(line);
                                        return;
                                }

                                if (type.equals("RACE")) {
                                        source = aPC.getRace();
                                }
                                else if (type.equals("PCCLASS")) {
                                        source = aPC.getClassNamed(name);
                                }
                                else if (type.equals("DOMAIN")) {
                                        source = aPC.getCharacterDomainNamed(name);
                                }
                                else if (type.equals("FEAT")) {
                                        source = aPC.getFeatNamed(name);
                                }

                                if (source == null) {
					final String message = "Invalid source specification: " + line;
					warnings.add(message);
                                }

                                break;
                        }
                }

                element = (PCGElement)tokens.getElements().get(0);

                if (source == null) {

                        aPC.setAutomaticFeatsStable(false);
                        aPC.featAutoList();		// populate profs array with automatic profs

                        List nonproficient = new ArrayList();
                        
                        String proficiency;
                        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
                                proficiency = EntityEncoder.decode(((PCGElement)it.next()).getText());

                                if (!aPC.hasWeaponProfNamed(proficiency))
                                {
                                        nonproficient.add(proficiency);
                                }
                        }


                        //
                        // For some reason, character had a proficiency that they should not have. Inform
                        // the user that they no longer have the proficiency.
                        //
                        if (nonproficient.size() > 0)
                        {
                                String s = nonproficient.toString();
                                s = s.substring(1, s.length() - 1);
                                final String message = "No longer proficient with following weapon(s):\n" + s;
                                warnings.add(message);
                        }

                }
                else {
                        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
                                source.addSelectedWeaponProfBonus(EntityEncoder.decode(((PCGElement)it.next()).getText()));
                        }
                }
        }
        
	/*
	 * ###############################################################
         * Character Equipment methods
	 * ###############################################################
	 */

	private void parseEquipmentLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Equipment is not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 07-09-02
			 */
			final String message =
                                "Illegal Equipment line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                String itemKey;
                Equipment aEquip = null;
                
                PCGElement element;
                String tag;

                String del = "";

                
                // the first element defines the item key name!!!
                element = (PCGElement)tokens.getElements().get(0);
                itemKey = EntityEncoder.decode(element.getText());
                aEquip = Globals.getEquipmentKeyed(itemKey);

                if (aEquip != null) {
                        aEquip = (Equipment)aEquip.clone();
                }
                else {
                
                        for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                                element = (PCGElement)it.next();
                                
                                if (element.getName().equals(TAG_CUSTOMIZATION)) {
                                        String baseItemKey = "";
                                        StringBuffer customProperties = new StringBuffer();
                                        
                                        del = "";
                                        for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                                element = (PCGElement)it2.next();
                                                tag = element.getName();
                                                
                                                if (tag.equals(TAG_BASEITEM)) {
                                                        baseItemKey = element.getText();
                                                }
                                                else {
                                                        customProperties.append(del);
                                                        customProperties.append(tag);
                                                        customProperties.append(":");
                                                        customProperties.append(element.getText());
                                                        del = "|";
                                                }
                                        }
                                        
                                        final Equipment aEquip2 = Globals.getEquipmentKeyed(baseItemKey);
                                        if (aEquip2 != null)
                                        {
                                                aEquip = (Equipment)aEquip2.clone();
                                                aEquip.load(customProperties.toString(), "|", ":");
                                                Globals.addEquipment((Equipment)aEquip.clone());
                                        }
                                        break;
                                }
                        }

                        if (aEquip == null) {
                                final String message = "Could not add equipment: " + itemKey + "\n" +
                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                warnings.add(message);
                                return;
                        }
                }
                
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();

                        if (tag.equals(TAG_QUANTITY)) {
                                aEquip.setQty(element.getText());
                        }
                        else if (tag.equals(TAG_OUTPUTORDER)) {
                                int index = 0;
                                try {
                                        index = Integer.parseInt(element.getText());
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can or have to do about this
                                }
                                aEquip.setOutputIndex(index);
                        }
                        else if (tag.equals(TAG_COST)) {
                        }
                        else if (tag.equals(TAG_WT)) {
                        }
                        else if (tag.equals(TAG_CARRIED)) {
                                Float carried = new Float(0.0f);
                                try {
                                        carried = new Float(element.getText());
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can do about it
                                }

                                aEquip.setCarried(carried);
                        }
                        else if (tag.equals(TAG_EQUIPPED)) {
				aEquip.setIsEquipped(element.getText().equalsIgnoreCase("Y"));
                        }
                        else if (tag.equals(TAG_LOCATION)) {
                                final String location = EntityEncoder.decode(element.getText());
                                if (location.startsWith("Hand")) {
                                        aEquip.setHand(Equipment.getHandNum(location.substring(6, location.length()-1)));
                                }
                        }
                        else if (tag.equals(TAG_CONTAINS)) {
                                containers.put(aEquip, element);
                        }    
                }
                
                aPC.getEquipmentList().add(aEquip);


		// TODO
                // take EquipSets into account
	}

        private void putEquipmentInAppropriateContainer() 
        {
                String equipmentKey;
                Equipment container;
                Equipment equipment;
                PCGElement containedEquipment;
                
                for (Iterator it = containers.keySet().iterator(); it.hasNext();) {
                        container = (Equipment)it.next();
                        containedEquipment = (PCGElement)containers.get(container);

                        for (Iterator it2 = containedEquipment.getChildren().iterator(); it2.hasNext();) {
                                equipmentKey = EntityEncoder.decode(((PCGElement)it2.next()).getText());
                                equipment = aPC.getEquipmentNamed(equipmentKey);
                                if (equipment != null) {
                                        container.insertChild(equipment);
                                }
                                else {
                                        final String message =
                                                "Could not add equipment " + equipmentKey +
                                                " to container " + container.getName();
                                        warnings.add(message);
                                }
                        }
                }
        }

	private void parseEquipmentSetLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * EquipSets are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 07-09-02
			 */
			final String message =
                                "Illegal EquipSet line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                String setName = null;
                String setID = null;
                String itemKey = null;
                Float itemQuantity = null;
                
                PCGElement element;
                String tag;
                
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();
                        
                        if (tag.equals(TAG_EQUIPSET)) {
                                setName = EntityEncoder.decode(element.getText());
                        }
                        else if (tag.equals(TAG_ID)) {
                                setID = element.getText();
                        }
                        else if (tag.equals(TAG_VALUE)) {
                                itemKey = EntityEncoder.decode(element.getText());
                        }
                        else if (tag.equals(TAG_QUANTITY)) {
                                try {
                                        itemQuantity = new Float(element.getText());
                                }
                                catch (NumberFormatException nfe) {
                                        itemQuantity = new Float(0.0f);
                                }
                        }
                }

                if ((setName == null) || setName.equals("") ||
                    (setID == null) || setID.equals("")) {
                        final String message = "Illegal EquipSet line ignored: " + line;
                        warnings.add(message);
                        return;
                }

                EquipSet aEquipSet;
                Equipment aEquip;

                aEquipSet = new EquipSet(setID, setName);
                
                if (itemKey != null) {

                        aEquipSet.setValue(itemKey);

//                          aEquip = aPC.getEquipmentNamed(itemKey);
                        aEquip = Globals.getEquipmentNamed(itemKey);
                        if (aEquip == null) {
                                final String message = "Could not find equipment: " + itemKey;
                                warnings.add(message);
                        }
                        else {
                                aEquip = (Equipment)aEquip.clone();
                                
                                if (itemQuantity != null) {
                                        aEquipSet.setQty(itemQuantity);
                                        aEquip.setQty(itemQuantity);
                                        aEquip.setNumberCarried(itemQuantity);
                                }

                                // if the idPath is longer than 3
                                // it's inside a container
                                if ((new StringTokenizer(setID, ".")).countTokens() >3) {
                                        // get parent EquipSet

                                        EquipSet aEquipSet2 = aPC.getEquipSetByIdPath(aEquipSet.getParentIdPath());

                                        // get the container
                                        Equipment aEquip2 = null;
                                        if (aEquipSet2 != null) {
                                                aEquip2 = aEquipSet2.getItem();
                                        }

                                        // add the child to container
                                        if (aEquip2 != null) {
                                                aEquip2.insertChild(aEquip);
                                                aEquip.setParent(aEquip2);
                                        }
                                }

                                aEquipSet.setItem(aEquip);                                
                        }
                }
                
                aPC.addEquipSet(aEquipSet);
	}

	/*
	 * ###############################################################
         * Character Deity/Domain methods
	 * ###############################################################
	 */

	private void parseDeityLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Deities are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 06-09-02
			 */
			final String message =
                                "Illegal Deity line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                final String deityName = EntityEncoder.decode(((PCGElement)tokens.getElements().get(0)).getText());
                final Deity aDeity = Globals.getDeityNamed(deityName);

                if (aDeity != null) {
                        aPC.setDeity(aDeity);
                } else if (!deityName.equals(Constants.s_NONE)) {
                        
                        // TODO
                        // create Deity object from information contained in pcg
                        // for now issue a warning

                        final String message =
                                "Deity not found: " + deityName + ".\n" +
                                PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                        warnings.add(message);
                }
	}

	private void parseDomainLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Domains are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 07-09-02
			 */
			final String message =
                                "Illegal Deity line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		PCGElement element;
                String tag;
                
		Iterator it = tokens.getElements().iterator();

		if (it.hasNext())
		{

                        element = (PCGElement)it.next();
                        
                        // the first element defines the skill key name!!!
                        final String domainName = EntityEncoder.decode(element.getText());
                        final Domain aDomain = Globals.getDomainKeyed(domainName);

                        if (aDomain != null) {
                                
                                final CharacterDomain aCharacterDomain;
                                int j = aPC.getFirstEmptyCharacterDomain();
                                if (j < 0)
                                {
                                        aCharacterDomain = new CharacterDomain();
                                        aPC.getCharacterDomainList().add(aCharacterDomain);
                                        j = aPC.getCharacterDomainList().size() - 1;
                                }
                                else {
                                        aCharacterDomain = (CharacterDomain)aPC.getCharacterDomainList().get(j);
                                }
                                aCharacterDomain.setDomain(aDomain).setIsLocked(true);

                                while (it.hasNext()) {
                                        
                                        element = (PCGElement)it.next();
                                        tag = element.getName();
                                        
                                        if (tag.equals(TAG_SOURCE)) {
                                                aCharacterDomain.setDomainSource(sourceElementToString(element));
                                        }
                                        else if (tag.equals(TAG_ASSOCIATEDDATA)) {
                                                aCharacterDomain.getDomain().addAssociated(
                                                        EntityEncoder.decode(element.getText()));
                                        }
                                }

                                // TODO
                                // set associated list
                        }
                        else if (!domainName.equals(Constants.s_NONE)) {
                                // TODO
                                // create Domain object from information contained in pcg
                                // for now issue a warning
                                
                                final String message =
                                        "Domain not found: " + domainName + ".\n" +
                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                warnings.add(message);
                        }
                }
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
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Spells are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			final String message =
                                "Illegal Spell line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                Spell aSpell = null;
                PCClass aPCClass = null;
                PObject source = null;
                
                String spellBook = null;
                
                int times = 0;
                int spellLevel = 0;

                Feat aFeat;
                final List metaFeats = new ArrayList();

                PCGElement element;
                String tag;
                
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();

                        if (tag.equals(TAG_SPELLNAME)) {
                                final String spellName = EntityEncoder.decode(element.getText());
                                aSpell = Globals.getSpellNamed(spellName);
                                if (aSpell == null) {
                                        final String message = "Could not find spell named: " + spellName;
                                        warnings.add(message);
                                        return;
                                }
                        }
                        else if (tag.equals(TAG_TIMES)) {
                                try {
                                        times = Integer.parseInt(element.getText());
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can do about it
                                }
                        }
                        else if (tag.equals(TAG_CLASS)) {
                                final String className = EntityEncoder.decode(element.getText());
                                aPCClass = aPC.getClassNamed(className);
                                if (aPCClass == null) {
                                        final String message = "Invalid class specification: " + className;
                                        warnings.add(message);
                                        return;
                                }
                        }
                        else if (tag.equals(TAG_SPELLBOOK)) {
                                spellBook = EntityEncoder.decode(element.getText());
                        }
                        else if (tag.equals(TAG_SPELLLEVEL)) {
                                try {
                                        spellLevel = Integer.parseInt(element.getText());
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can do about it
                                }
                        }
                        else if (tag.equals(TAG_SOURCE)) {
                                String typeName = "";
                                String objectName = "";

                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        
                                        element = (PCGElement)it2.next();
                                        tag = element.getName();

                                        if (tag.equals(TAG_TYPE)) {
                                                typeName = element.getText().toUpperCase();
                                        }
                                        else if (tag.equals(TAG_NAME)) {
                                                objectName = element.getText();
                                        }
                                }

                                if (typeName.equals("DOMAIN")) {
                                        source = aPC.getCharacterDomainNamed(objectName);
                                        if (source == null) {
                                                final String message = "Could not find domain: " + objectName;
                                                warnings.add(message);
                                                return;
                                        }
                                }
                                else {
                                        // it's either the class, sub-class or a cast-as class
                                        // first see if it's the class
                                        source = aPC.getClassNamed(objectName);
                                }
                        }
                        else if (tag.equals(TAG_FEATLIST)) {

                                for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
                                        
                                        aFeat = Globals.getFeatNamed(
                                                EntityEncoder.decode(((PCGElement)it2.next()).getText()));
                                        if (aFeat != null) {
                                                metaFeats.add(aFeat);
                                        }
                                }
                        }
                }

                if ((aSpell == null) || (aPCClass == null) || (spellBook == null)) {
                        final String message = "Illegal Spell line ignored: " + line;
                        warnings.add(message);
                        return;
                }

                /*
                 * this can only happen if the source type was NOT DOMAIN!
                 */
                if (source == null) {
                        source = aPCClass;
                }
                
                int level = aSpell.levelForKey(source.getSpellKey());

                if (level < 0) {
                        final String message = "Could not find spell " + aSpell.getName() +
                                " in " + shortClassName(source) + " " + source.getName();
                        warnings.add(message);
                        return;
                }

                // do not load auto knownspells into default spellbook
                if (spellBook.equals(Globals.getDefaultSpellBook()) &&
                    aPCClass.isAutoKnownSpell(aSpell.getKeyName(), level) &&
                    aPC.getAutoSpells())
                {
                        return;
                }

                CharacterSpell aCharacterSpell = aPCClass.getCharacterSpellForSpell(aSpell);

                if (aCharacterSpell == null) {
                        aCharacterSpell = new CharacterSpell(aPCClass, aSpell);
                        if (!(source instanceof Domain)) {
                                aCharacterSpell.addInfo(level, 1, Globals.getDefaultSpellBook());
                        }
                        aPCClass.addCharacterSpell(aCharacterSpell);
                }

                SpellInfo aSpellInfo = null;
                if (source.getKeyName().equals(aPCClass.getKeyName()) ||
                    !spellBook.equals(Globals.getDefaultSpellBook())) {
                        aSpellInfo = aCharacterSpell.getSpellInfoFor(spellBook, spellLevel, -1);
                        if ((aSpellInfo == null) || !metaFeats.isEmpty()) {
                                aSpellInfo = aCharacterSpell.addInfo(spellLevel, times, spellBook);
                        }
                }
                if ((aSpellInfo != null) && !metaFeats.isEmpty()) {
                        aSpellInfo.addFeatsToList(metaFeats);
                }
                
                // just to make sure the spellbook is present
                aPC.addSpellBook(spellBook);        
        }
        
        private void sortCharacterSpells() 
        {
		// now sort each classes spell list
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			((PCClass)it.next()).sortCharacterSpellList();
		}
	}

        private String shortClassName(Object o) 
        {
                final Class objClass = o.getClass();
                final String pckName = objClass.getPackage().getName();
                return objClass.getName().substring(pckName.length() + 1);
        }        

	/*
	 * ###############################################################
         * Character Description/Bio/History methods
	 * ###############################################################
	 */

	private void parseCharacterBioLine(String line)
	{
		aPC.setBio(EntityEncoder.decode(line.substring(TAG_CHARACTERBIO.length() + 1)));
	}

	private void parseCharacterDescLine(String line)
	{
		aPC.setDescription(EntityEncoder.decode(line.substring(TAG_CHARACTERDESC.length() + 1)));
	}

	/*
	 * ###############################################################
         * Character Follower methods
	 * ###############################################################
	 */

	private void parseFollowerLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Followers are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			final String message =
                                "Illegal Follower line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                final Follower aFollower = new Follower("", "", "");
                
                PCGElement element;
                String tag;
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();
                        
                        if (tag.equals(TAG_FOLLOWER)) {
                                aFollower.setName(EntityEncoder.decode(element.getText()));
                        }
                        else if (tag.equals(TAG_TYPE)) {
                                aFollower.setType(EntityEncoder.decode(element.getText()));
                        }
                        else if (tag.equals(TAG_HITDICE)) {
                                int usedHD = 0;
                                try {
                                        aFollower.setUsedHD(Integer.parseInt(element.getText()));
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can do about it
                                }
                        }
                        else if (tag.equals(TAG_FILE)) {
                                aFollower.setFileName(EntityEncoder.decode(element.getText()));
                        }
                }

                if (!aFollower.getFileName().equals("") &&
                    !aFollower.getName().equals("") &&
                    !aFollower.getType().equals("")) {
                        aPC.addFollower(aFollower);
                }
	}

	private void parseMasterLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Master is not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			final String message =
                                "Illegal Master line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

                final Follower aMaster = new Follower("", "", "");
                
                PCGElement element;
                String tag;
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();
                        
                        if (tag.equals(TAG_MASTER)) {
                                aMaster.setName(EntityEncoder.decode(element.getText()));
                        }
                        else if (tag.equals(TAG_TYPE)) {
                                aMaster.setType(EntityEncoder.decode(element.getText()));
                        }
                        else if (tag.equals(TAG_HITDICE)) {
                                int usedHD = 0;
                                try {
                                        aMaster.setUsedHD(Integer.parseInt(element.getText()));
                                }
                                catch (NumberFormatException nfe) {
                                        // nothing we can do about it
                                }
                        }
                        else if (tag.equals(TAG_FILE)) {
                                /*
                                 * quick and dirty way to handle ':'
                                 * need to come up with a clean solution before releasing
                                 * 
                                 * author: Thomas Behr 09-09-02
                                 */
                                aMaster.setFileName(EntityEncoder.decode(element.getText()));
                        }
                }

                if (!aMaster.getFileName().equals("") &&
                    !aMaster.getName().equals("") &&
                    !aMaster.getType().equals("")) {
                        aPC.setMaster(aMaster);
                }
	}

	/*
	 * ###############################################################
         * Character Notes Tab methods
	 * ###############################################################
	 */

	private void parseNoteLine(String line)
        {
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Notes are not critical for characters,
			 * no need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			final String message =
                                "Illegal Notes line ignored: " + line + "\n" +
                                "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final NoteItem ni = new NoteItem(-1, -1, "", "");

                PCGElement element;
                String tag;
                
                for (Iterator it = tokens.getElements().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();
                        
			if (tag.equals(TAG_NOTE))
			{
				ni.setName(EntityEncoder.decode(element.getText()));
			}
			else if (tag.equals(TAG_ID)) {
				try
				{
					ni.setIdValue(Integer.parseInt(element.getText()));
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
					ni.setParentId(Integer.parseInt(element.getText()));
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
				ni.setValue(EntityEncoder.decode(element.getText()));
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

        /*
         * currently source is either empty or
         * PCCLASS|classname|classlevel (means it's a chosen special ability)
         * PCCLASS=classname|classlevel (means it's a defined special ability)
         * DEITY=deityname|totallevels
         */
        private String sourceElementToString(PCGElement source) 
        {
                String type = "";
                String name = "";
                String level = "";
                String defined = "";

                PCGElement element;
                String tag;
                for (Iterator it = source.getChildren().iterator(); it.hasNext();) {
                        element = (PCGElement)it.next();
                        tag = element.getName();
                        
                        if (tag.equals(TAG_TYPE)) {
                                type = element.getText();
                        }
                        else if (tag.equals(TAG_NAME)) {
                                name = element.getText();
                        }
                        else if (tag.equals(TAG_LEVEL)) {
                                level = element.getText();
                        }
                        else if (tag.equals(TAG_DEFINED)) {
                                defined = element.getText().toUpperCase();
                        }
                }
                
                final StringBuffer buffer = new StringBuffer();
                buffer.append(type);
                buffer.append((defined.equals("Y")) ? "=" : "|");
                buffer.append(name);
                if (!level.equals("")) {
                        buffer.append("|");
                        buffer.append(level);
                }
                
                return buffer.toString();
        }

	/*
	 * ###############################################################
         * Inner classes
	 * ###############################################################
	 */

        /*
         * Sorts EquipSet lines according to their ID
         *
         * It seems like EquipSet intances need NOT be created
         * according to their ID induced order, so this class
         * is currently not needed. We'll keep it just in case.
         *
         * @author Thomas Behr 10-09-02
         */
        private class EquipSetLineComparator implements Comparator 
        {
                public int compare(Object o1, Object o2) 
                {
                        final StringTokenizer st1 = new StringTokenizer(o1.toString(), "|");
                        final StringTokenizer st2 = new StringTokenizer(o2.toString(), "|");

                        String o1ID = "ID:-1";
                        String o2ID = "ID:-1";

                        String cToken;

                        while (st1.hasMoreTokens()) {
                                cToken = st1.nextToken();
                                if (cToken.startsWith(TAG_ID)) {
                                        o1ID = cToken;
                                        break;
                                }
                        }

                        while (st2.hasMoreTokens()) {
                                cToken = st2.nextToken();
                                if (cToken.startsWith(TAG_ID)) {
                                        o2ID = cToken;
                                        break;
                                }
                        }

                        return o1ID.compareTo(o2ID);
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
			String tag = null;
			final StringBuffer buffer = new StringBuffer();

			PCGElement element;

			while (tokens.hasMoreTokens())
			{
				token = tokens.nextToken().trim();

				if (token.equals(outerDelimiter))
				{

					if (nestedDepth == 0)
					{
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
                                                        
                                                        tag = token.substring(0, index);
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
						element = new PCGElement(tag);
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
