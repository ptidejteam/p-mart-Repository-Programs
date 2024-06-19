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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:19:07 $
 *
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.FeatMultipleChoice;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.LevelAbility;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCSpell;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SystemCollections;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;

/**
 * <code>PCGVer2Parser</code>
 * Parses a line oriented format.
 * Each line should adhere to the following grammar:<br>
 *
 * <i>line</i> := EMPTY | <i>comment</i> | <i>taglist</i>
 * <i>comment</i> := '#' STRING
 * <i>taglist</i> := tag ('|' tag)*
 * <i>tag</i> := simpletag | nestedtag
 * <i>nestedtag</i> := TAGNAME ':' '[' taglist ']'
 * <i>simpletag</i> := TAGNAME ':' TAGVALUE
 *
 *
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */

final class PCGVer2Parser implements PCGParser, IOConstants
{
	/**
	 * DO NOT CHANGE line separator.
	 * Need to keep the Unix line separator to ensure cross-platform portability.
	 *
	 * author: Thomas Behr 2002-11-13
	 */
	private static final String LINE_SEP = "\n";

	private final List warnings = new ArrayList();
	private PlayerCharacter aPC;
	private Cache cache;

	private final Set seenStats = new HashSet();
	private final List weaponprofs = new ArrayList();
	//
	// MAJOR.MINOR.REVISION
	//
	private int[] pcgenVersion = {0, 0, 0};

	/**
	 * Constructor
	 */
	PCGVer2Parser(PlayerCharacter aPC)
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
	 * <br>author: Thomas Behr 20-07-02
	 *
	 * @param lines   the String to parse
	 */
	public void parsePCG(String[] lines) throws PCGParseException
	{
		initCache(lines.length);
		for (int i = 0; i < lines.length; ++i)
		{
			if ((lines[i].trim().length() > 0) && !isComment(lines[i]))
			{
				cacheLine(lines[i].trim());
			}
		}
		parseCachedLines();
		aPC.setDirty(false);
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
		cache.put(s.substring(0, s.indexOf(':')), s);
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
		* CAMPAIGN:CMP - Monkey Book I - Book For Monkeys
		* CAMPAIGN:CMP - Monkey Book II - Book By Monkeys
		* ...
		*
		* first thing to do is checking campaigns - no matter what!
		*/
		if (cache.containsKey(TAG_CAMPAIGN))
		{
			parseCampaignLines(cache.get(TAG_CAMPAIGN));
		}

		Iterator it;
		/*
		 * VERSION:x.x.x
		 */
		if (cache.containsKey(TAG_VERSION))
		{
			parseVersionLine((String) cache.get(TAG_VERSION).get(0));
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
			for (it = cache.get(TAG_STAT).iterator(); it.hasNext();)
			{
				parseStatLine((String) it.next());
			}

			checkStats();
		}
		if (cache.containsKey(TAG_ALIGNMENT))
		{
			parseAlignmentLine((String) cache.get(TAG_ALIGNMENT).get(0));
		}
		if (cache.containsKey(TAG_RACE))
		{
			parseRaceLine((String) cache.get(TAG_RACE).get(0));
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
			parsePoolPointsLine((String) cache.get(TAG_POOLPOINTS).get(0));
		}
		if (cache.containsKey(TAG_AUTOSPELLS))
		{
			parseAutoSpellsLine((String) cache.get(TAG_AUTOSPELLS).get(0));
		}
		if (cache.containsKey(TAG_LOADCOMPANIONS))
		{
			parseLoadCompanionLine((String) cache.get(TAG_LOADCOMPANIONS).get(0));
		}
		if (cache.containsKey(TAG_USETEMPMODS))
		{
			parseUseTempModsLine((String) cache.get(TAG_USETEMPMODS).get(0));
		}
		if (cache.containsKey(TAG_HTMLOUTPUTSHEET))
		{
			parseHTMLOutputSheetLine((String) cache.get(TAG_HTMLOUTPUTSHEET).get(0));
		}
		if (cache.containsKey(TAG_PDFOUTPUTSHEET))
		{
			parsePDFOutputSheetLine((String) cache.get(TAG_PDFOUTPUTSHEET).get(0));
		}
		if (cache.containsKey(TAG_AUTOSORTGEAR))
		{
			parseAutoSortGearLine((String) cache.get(TAG_AUTOSORTGEAR).get(0));
		}
		if (cache.containsKey(TAG_AUTOSORTSKILLS))
		{
			parseAutoSortSkillsLine((String) cache.get(TAG_AUTOSORTSKILLS).get(0));
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
			for (it = cache.get(TAG_CLASS).iterator(); it.hasNext();)
			{
				parseClassLine((String) it.next());
			}

			checkSkillPools();
		}
		if (cache.containsKey(TAG_CLASSABILITIESLEVEL))
		{
			for (it = cache.get(TAG_CLASSABILITIESLEVEL).iterator(); it.hasNext();)
			{
				parseClassAbilitiesLevelLine((String) it.next());
			}
		}

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 */
		if (cache.containsKey(TAG_EXPERIENCE))
		{
			parseExperienceLine((String) cache.get(TAG_EXPERIENCE).get(0));
		}

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		if (cache.containsKey(TAG_TEMPLATESAPPLIED))
		{
			for (it = cache.get(TAG_TEMPLATESAPPLIED).iterator(); it.hasNext();)
			{
				parseTemplateLine((String) it.next());
			}
		}

		if (cache.containsKey(TAG_REGION))
		{
			for (it = cache.get(TAG_REGION).iterator(); it.hasNext();)
			{
				parseRegionLine((String) it.next());
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
			for (it = cache.get(TAG_SKILL).iterator(); it.hasNext();)
			{
				parseSkillLine((String) it.next());
			}
		}

		/*
		 * #Character Languages
								 * LANGUAGE:Chondathan|LANGUAGE:Common|LANGUAGE:Literacy
		 */
		if (cache.containsKey(TAG_LANGUAGE))
		{
			for (it = cache.get(TAG_LANGUAGE).iterator(); it.hasNext();)
			{
				parseLanguageLine((String) it.next());
			}
		}

		/*
		 * Anything that is already Pipe Delimited should be in
		 * parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
		 * FEATPOOL:>number of remaining feats<
		 */
		if (cache.containsKey(TAG_FEAT))
		{
			for (it = cache.get(TAG_FEAT).iterator(); it.hasNext();)
			{
				parseFeatLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_VFEAT))
		{
			for (it = cache.get(TAG_VFEAT).iterator(); it.hasNext();)
			{
				parseVFeatLine((String) it.next());
			}
		}

		if (cache.containsKey(TAG_FEATPOOL))
		{
			for (it = cache.get(TAG_FEATPOOL).iterator(); it.hasNext();)
			{
				parseFeatPoolLine((String) it.next());
			}
		}


		/*
		 * Contains information about PC's equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Longsword|OUTPUTORDER:1|COST:5|WT:5|NOTE:It's very sharp!|>other info<
		 * EQUIPNAME:Backpack|OUTPUTORDER:-1|COST:5|WT:5|NOTE:on my back
		 * EQUIPNAME:Rope (Silk)|OUTPUTORDER:3|COST:5|WT:5
		 */
		if (cache.containsKey(TAG_MONEY))
		{
			for (it = cache.get(TAG_MONEY).iterator(); it.hasNext();)
			{
				parseMoneyLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_EQUIPNAME))
		{
			for (it = cache.get(TAG_EQUIPNAME).iterator(); it.hasNext();)
			{
				parseEquipmentLine((String) it.next());
			}

		}
		if (cache.containsKey(TAG_EQUIPSET))
		{
			/*
			 * strangely enough this works even if we create a
			 * EquipSet for content whose container EquipSet
			 * has not been created yet
			 * author: Thomas Behr 10-09-02
			 *
			 * Comment from EquipSet author:
			 * It only works because I've already sorted on output
			 * in PCGVer2Creator
			 * author: Jayme Cox 01-16-03
			 *
			 */
			//Collections.sort(cache.get(TAG_EQUIPSET), new EquipSetLineComparator());

			for (it = cache.get(TAG_EQUIPSET).iterator(); it.hasNext();)
			{
				parseEquipmentSetLine((String) it.next());
			}
		}
		/**
		 * CALCEQUIPSET line contains the "working" equipment list
		 **/
		if (cache.containsKey(TAG_CALCEQUIPSET))
		{
			for (it = cache.get(TAG_CALCEQUIPSET).iterator(); it.hasNext();)
			{
				parseCalcEquipSet((String) it.next());
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
			for (it = cache.get(TAG_DEITY).iterator(); it.hasNext();)
			{
				parseDeityLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_DOMAIN))
		{
			for (it = cache.get(TAG_DOMAIN).iterator(); it.hasNext();)
			{
				parseDomainLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_DOMAINSPELLS))
		{
			for (it = cache.get(TAG_DOMAINSPELLS).iterator(); it.hasNext();)
			{
				parseDomainSpellsLine((String) it.next());
			}
		}

		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
		 */
		if (cache.containsKey(TAG_SPELLLIST))
		{
			for (it = cache.get(TAG_SPELLLIST).iterator(); it.hasNext();)
			{
				parseSpellListLines((String) it.next());
			}
		}

		if (cache.containsKey(TAG_SPELLNAME))
		{
			for (it = cache.get(TAG_SPELLNAME).iterator(); it.hasNext();)
			{
				parseSpellLine((String) it.next());
			}

			sortCharacterSpells();
		}

		/*
		 * #Character Description/Bio/Historys
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		if (cache.containsKey(TAG_CHARACTERBIO))
		{
			parseCharacterBioLine((String) cache.get(TAG_CHARACTERBIO).get(0));
		}
		if (cache.containsKey(TAG_CHARACTERDESC))
		{
			parseCharacterDescLine((String) cache.get(TAG_CHARACTERDESC).get(0));
		}
		if (cache.containsKey(TAG_CHARACTERCOMP))
		{
			for (it = cache.get(TAG_CHARACTERCOMP).iterator(); it.hasNext();)
			{
				parseCharacterCompLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_CHARACTERASSET))
		{
			for (it = cache.get(TAG_CHARACTERASSET).iterator(); it.hasNext();)
			{
				parseCharacterAssetLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_CHARACTERMAGIC))
		{
			for (it = cache.get(TAG_CHARACTERMAGIC).iterator(); it.hasNext();)
			{
				parseCharacterMagicLine((String) it.next());
			}
		}

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		if (cache.containsKey(TAG_MASTER))
		{
			for (it = cache.get(TAG_MASTER).iterator(); it.hasNext();)
			{
				parseMasterLine((String) it.next());
			}
		}
		if (cache.containsKey(TAG_FOLLOWER))
		{
			for (it = cache.get(TAG_FOLLOWER).iterator(); it.hasNext();)
			{
				parseFollowerLine((String) it.next());
			}
		}

		/*
		 * #Character Notes Tab
		 */
		if (cache.containsKey(TAG_NOTE))
		{
			for (it = cache.get(TAG_NOTE).iterator(); it.hasNext();)
			{
				parseNoteLine((String) it.next());
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
		 * CITY:text
		 * PERSONALITYTRAIT1:text
		 * PERSONALITYTRAIT2:text
		 * SPEECHPATTERN:text
		 * PHOBIAS:text
		 * INTERESTS:text
		 * CATCHPHRASE:text
		 */
		if (cache.containsKey(TAG_CHARACTERNAME))
		{
			parseCharacterNameLine((String) cache.get(TAG_CHARACTERNAME).get(0));
		}
		if (cache.containsKey(TAG_TABNAME))
		{
			parseTabNameLine((String) cache.get(TAG_TABNAME).get(0));
		}
		if (cache.containsKey(TAG_PLAYERNAME))
		{
			parsePlayerNameLine((String) cache.get(TAG_PLAYERNAME).get(0));
		}
		if (cache.containsKey(TAG_HEIGHT))
		{
			parseHeightLine((String) cache.get(TAG_HEIGHT).get(0));
		}
		if (cache.containsKey(TAG_WEIGHT))
		{
			parseWeightLine((String) cache.get(TAG_WEIGHT).get(0));
		}
		if (cache.containsKey(TAG_AGE))
		{
			parseAgeLine((String) cache.get(TAG_AGE).get(0));
		}
		if (cache.containsKey(TAG_GENDER))
		{
			parseGenderLine((String) cache.get(TAG_GENDER).get(0));
		}
		if (cache.containsKey(TAG_HANDED))
		{
			parseHandedLine((String) cache.get(TAG_HANDED).get(0));
		}
		if (cache.containsKey(TAG_SKINCOLOR))
		{
			parseSkinColorLine((String) cache.get(TAG_SKINCOLOR).get(0));
		}
		if (cache.containsKey(TAG_EYECOLOR))
		{
			parseEyeColorLine((String) cache.get(TAG_EYECOLOR).get(0));
		}
		if (cache.containsKey(TAG_HAIRCOLOR))
		{
			parseHairColorLine((String) cache.get(TAG_HAIRCOLOR).get(0));
		}
		if (cache.containsKey(TAG_HAIRSTYLE))
		{
			parseHairStyleLine((String) cache.get(TAG_HAIRSTYLE).get(0));
		}
		if (cache.containsKey(TAG_LOCATION))
		{
			parseLocationLine((String) cache.get(TAG_LOCATION).get(0));
		}
		//this tag is obsolete, but left in for backward-compatibility, replaced by TAG_CITY
		if (cache.containsKey(TAG_RESIDENCE))
		{
			parseResidenceLine((String) cache.get(TAG_RESIDENCE).get(0));
		}
		if (cache.containsKey(TAG_CITY))
		{
			parseCityLine((String) cache.get(TAG_CITY).get(0));
		}
		if (cache.containsKey(TAG_BIRTHPLACE))
		{
			parseBirthplaceLine((String) cache.get(TAG_BIRTHPLACE).get(0));
		}
		if (cache.containsKey(TAG_PERSONALITYTRAIT1))
		{
			for (it = cache.get(TAG_PERSONALITYTRAIT1).iterator(); it.hasNext();)
			{
				parsePersonalityTrait1Line((String) it.next());
			}
		}
		if (cache.containsKey(TAG_PERSONALITYTRAIT2))
		{
			for (it = cache.get(TAG_PERSONALITYTRAIT2).iterator(); it.hasNext();)
			{
				parsePersonalityTrait2Line((String) it.next());
			}
		}
		if (cache.containsKey(TAG_SPEECHPATTERN))
		{
			parseSpeechPatternLine((String) cache.get(TAG_SPEECHPATTERN).get(0));
		}
		if (cache.containsKey(TAG_PHOBIAS))
		{
			parsePhobiasLine((String) cache.get(TAG_PHOBIAS).get(0));
		}
		if (cache.containsKey(TAG_INTERESTS))
		{
			parseInterestsLine((String) cache.get(TAG_INTERESTS).get(0));
		}
		if (cache.containsKey(TAG_CATCHPHRASE))
		{
			parseCatchPhraseLine((String) cache.get(TAG_CATCHPHRASE).get(0));
		}
		if (cache.containsKey(TAG_PORTRAIT))
		{
			parsePortraitLine((String) cache.get(TAG_PORTRAIT).get(0));
		}

		/*
		 * #Character Weapon proficiencies
		 */
		if (cache.containsKey(TAG_WEAPONPROF))
		{
			for (it = cache.get(TAG_WEAPONPROF).iterator(); it.hasNext();)
			{
				parseWeaponProficienciesLine((String) it.next());
			}

			checkWeaponProficiencies();
		}

		/*
		 * # Kits
		 */
		if (cache.containsKey(TAG_KIT))
		{
			for (it = cache.get(TAG_KIT).iterator(); it.hasNext();)
			{
				parseKitLine((String) it.next());
			}
		}

		if (cache.containsKey(TAG_ARMORPROF))
		{
			for (it = cache.get(TAG_ARMORPROF).iterator(); it.hasNext();)
			{
				parseArmorProfLine((String) it.next());
			}
		}

		/*
		 * # Temporary Bonuses
		 */
		if (cache.containsKey(TAG_TEMPBONUS))
		{
			for (it = cache.get(TAG_TEMPBONUS).iterator(); it.hasNext();)
			{
				parseTempBonusLine((String) it.next());
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
		if (SettingsHandler.isLoadCampaignsWithPC())
		{
			for (Iterator it = lines.iterator(); it.hasNext();)
			{
				line = (String) it.next();
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
				for (Iterator it2 = tokens.getElements().iterator(); it2.hasNext();)
				{
					aCampaign = Globals.getCampaignNamed(((PCGElement) it2.next()).getText());
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
				if (Globals.getUseGUI())
				{
					pcgen.gui.PCGen_Frame1.getInst().getMainSource().updateLoadedCampaignsUI();
				}
			}
		}

		if (!Globals.displayListsHappy())
		{
			throw new PCGParseException("parseCampaignLines", "N/A",
				"Insufficient campaign information to load character file.");
		}
	}

	private void parseVersionLine(String line) throws PCGParseException
	{
		int[] version = {0, 0, 0};
		int idx = 0;
		//
		// 4.2.4-autobuild-200212200400
		//
		final StringTokenizer aTok = new StringTokenizer(line.substring(TAG_VERSION.length() + 1), ".-", true);
		try
		{
			while ((idx < 3) && aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();
				if (aString.equals("-"))
				{
					break;
				}
				else if (!aString.equals("."))
				{
					version[idx++] = Integer.parseInt(aString);
				}
			}
			pcgenVersion = version;
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseVersionLine", "N/A",
				"Invalid PCGen version.");
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

	/**
	 * # Auto known spells
	 **/
	private void parseAutoSpellsLine(String line)
	{
		aPC.setAutoSpells(line.endsWith("Y"));
	}

	/**
	 * # Load companions with master?
	 **/
	private void parseLoadCompanionLine(String line)
	{
		aPC.setLoadCompanion(line.endsWith("Y"));
	}

	/**
	 * # Use temporary mods/bonuses?
	 **/
	private void parseUseTempModsLine(String line)
	{
		aPC.setUseTempMods(line.endsWith("Y"));
	}

	/**
	 * # HTML Output Sheet location
	 **/
	private void parseHTMLOutputSheetLine(String line)
	{
		String aFileName = EntityEncoder.decode(line.substring(TAG_HTMLOUTPUTSHEET.length() + 1));
		if (aFileName.length() <= 0)
		{
			aFileName = SettingsHandler.getSelectedCharacterHTMLOutputSheet();
		}
		aPC.setSelectedCharacterHTMLOutputSheet(aFileName);
	}

	/**
	 * # PDF Output Sheet location
	 **/
	private void parsePDFOutputSheetLine(String line)
	{
		String aFileName = EntityEncoder.decode(line.substring(TAG_PDFOUTPUTSHEET.length() + 1));
		if (aFileName.length() <= 0)
		{
			aFileName = SettingsHandler.getSelectedCharacterPDFOutputSheet();
		}
		aPC.setSelectedCharacterPDFOutputSheet(aFileName);
	}

	/**
	 * # Auto sort gear
	 **/
	private void parseAutoSortGearLine(String line)
	{
		aPC.setAutoSortGear(line.endsWith("Y"));
	}

	/**
	 * # Auto sort skills
	 **/
	private void parseAutoSortSkillsLine(String line)
	{
		aPC.setAutoSortSkills(line.endsWith("Y"));
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

	//this method is obsolete, but left in for backward-compatibility, replaced by parseCityLine()
	private void parseResidenceLine(String line)
	{
		aPC.setResidence(EntityEncoder.decode(line.substring(TAG_RESIDENCE.length() + 1)));
		aPC.setDirty(true); // trigger a save prompt so that the PCG will be updated
	}

	private void parseCityLine(String line)
	{
		aPC.setResidence(EntityEncoder.decode(line.substring(TAG_CITY.length() + 1)));
	}

	private void parseBirthplaceLine(String line)
	{
		aPC.setBirthplace(EntityEncoder.decode(line.substring(TAG_BIRTHPLACE.length() + 1)));
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

		final Iterator it = tokens.getElements().iterator();

		PCGElement element;
		final String statName;

		if (it.hasNext())
		{
			element = (PCGElement) it.next();
			statName = element.getText();

			final int index = Globals.getStatFromAbbrev(statName);

			if ((index > -1) && seenStats.add(statName.toUpperCase()) && (it.hasNext()))
			{
				element = (PCGElement) it.next();
				try
				{
					((PCStat) aPC.getStatList().getStats().get(index)).setBaseScore(
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
		if (seenStats.size() != Globals.s_ATTRIBSHORT.length)
		{
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
		final int i = SystemCollections.getIndexOfAlignment(alignment);
		if (i >= 0)
		{
			aPC.setAlignment(i, true);
			return;
		}

		final String message = "Invalid alignment specification.";
		warnings.add(message);
	}

	private void parseRaceLine(String line) throws PCGParseException
	{
		final StringTokenizer sTok = new StringTokenizer(line.substring(TAG_RACE.length() + 1), "|", false);
		final String race_name = EntityEncoder.decode(sTok.nextToken());
		final Race aRace = Globals.getRaceKeyed(race_name);

		HashMap hitPointMap = new HashMap();

		if (aRace != null)
		{
			aPC.setRace(aRace);
			final int hitDice = aRace.hitDice();

			if (sTok.hasMoreTokens())
			{
				String aString = sTok.nextToken();
				if (aString.startsWith(TAG_HITPOINTS))
				{
					final StringTokenizer aTok = new StringTokenizer(aString.substring(TAG_HITPOINTS.length()), ":", false);
					int i = 0;
					if (hitDice > 0)
					{
						while (aTok.hasMoreTokens())
						{
							if (i >= hitDice)
							{
								warnings.add("Saved race (" + race_name + ") now has fewer HITDICE.");
								break;
							}

							try
							{
								hitPointMap.put(Integer.toString(i++), new Integer(aTok.nextToken()));
							}
							catch (NumberFormatException ex)
							{
								throw new PCGParseException("parseRaceLine", aString, ex.getMessage());
							}
						}
						if (i < hitDice)
						{
							warnings.add("Saved race (" + race_name + ") now has more HITDICE.");
						}
						aPC.getRace().setHitPointMap(hitPointMap);
					}
					else
					{
						String warning = "Saved race (" + race_name + ") no longer has a HITDICE tag";
						if (!SettingsHandler.isMonsterDefault())
						{
							warning += " or," + Constants.s_LINE_SEP + "was saved with \"Use Default Monsters\" on";
						}
						warnings.add(warning + ".");
					}
				}
				else
				{
					warnings.add("Ignoring unknown race info: " + aString);
				}
			}
		}
		else
		{
			final String message =
				"Race not found: " + race_name + "." + Constants.s_LINE_SEP +
				PCGParser.s_CHECKLOADEDCAMPAIGNS;
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

		final Iterator it = tokens.getElements().iterator();

		// the first element defines the class key name!!!
		if (it.hasNext())
		{

			element = (PCGElement) it.next();

			aPCClass = Globals.getClassKeyed(EntityEncoder.decode(element.getText()));
			if (aPCClass != null)
			{
				// Icky: Need to redesign the way classes work!
				// Icky: Having to clone the class here is UGLY!
				aPCClass = (PCClass) aPCClass.clone();
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
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_SUBCLASS.equals(tag))
			{
				aPCClass.setSubClassName(EntityEncoder.decode(element.getText()));
			}
			if (TAG_LEVEL.equals(tag))
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
			else if (TAG_SKILLPOOL.equals(tag))
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
			else if (TAG_CANCASTPERDAY.equals(tag))
			{
				// TODO
			}
			else if (TAG_SPELLBASE.equals(tag))
			{
				final String spellBase = EntityEncoder.decode(element.getText());
				if ((Globals.getStatFromAbbrev(spellBase.toUpperCase()) > -1) ||
					Constants.s_NONE.equalsIgnoreCase(spellBase) ||
					"Any".equalsIgnoreCase(spellBase) ||
					"SPELL".equalsIgnoreCase(spellBase))
				{
					aPCClass.setSpellBaseStat(spellBase);
				}
			}
			else if (TAG_PROHIBITED.equals(tag))
			{
				aPCClass.setProhibitedString(EntityEncoder.decode(element.getText()));
			}
		}

		if (level > -1)
		{
			aPC.getClassList().add(aPCClass);
			for (int i = 0; i < level; ++i)
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

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			skillPoints += ((PCClass) it.next()).getSkillPool().intValue();
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
			final String message =
				"Illegal Class abilities line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		int level = -1;
		PCClass aPCClass = null;
		String tag;
		PCGElement element;
		PCLevelInfo pcl = null;

		final Iterator it = tokens.getElements().iterator();

		// the first element defines the class key name and level
		// eg: Cleric=4
		if (it.hasNext())
		{
			element = (PCGElement) it.next();
			final int index = element.getText().indexOf('=');

			if (index < 0)
			{
				final String message = "Invalid class/level specification: " + element.getText();
				warnings.add(message);
				return;
			}

			final String classKeyName = EntityEncoder.decode(element.getText().substring(0, index));
			aPCClass = aPC.getClassKeyed(classKeyName);

			if (aPCClass == null)
			{
				final String message = "Could not find class: " + classKeyName;
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
			pcl = aPC.saveLevelInfo(classKeyName);
			pcl.setLevel(level);
		}

		String specialAbilityName;
		SpecialAbility specialAbility;

		while (it.hasNext())
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_HITPOINTS.equals(tag))
			{
				try
				{
					aPCClass.setHitPoint(level - 1, new Integer(element.getText()));

				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid hitpoint specification: " + tag + ":" + element.getText();
					warnings.add(message);
				}

			}
			else if (TAG_SAVES.equals(tag))
			{
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					final String dString = EntityEncoder.decode(((PCGElement) it2.next()).getText());
					if (dString.startsWith("BONUS|"))
					{
						aPCClass.addBonusList(dString.substring(6), aPCClass);
					}
					aPCClass.addSave(dString);
				}
			}
			else if (TAG_SPECIALTIES.equals(tag))
			{
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					aPCClass.getSpecialtyList().add(EntityEncoder.decode(((PCGElement) it2.next()).getText()));
				}
			}
			else if (TAG_SPECIALABILITIES.equals(tag))
			{
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					String saSource = "";
					specialAbilityName = EntityEncoder.decode(((PCGElement) it2.next()).getText());
					specialAbility = new SpecialAbility(specialAbilityName);
					if (specialAbilityName.endsWith(":-1"))
					{
						specialAbilityName = specialAbilityName.substring(0, specialAbilityName.length()-3);
						specialAbility = new SpecialAbility(specialAbilityName);
						specialAbility.setSASource("PCClass|"+ aPCClass.getName() + '|' + 0);
					}
					else
					{
						specialAbility = new SpecialAbility(specialAbilityName);
						specialAbility.setSASource("PCCLASS|" + aPCClass.getName() + '|' + level);
					}

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
			else if (tag.equals(TAG_LEVELABILITY))
			{
				Iterator it2 = element.getChildren().iterator();
				String dString;
				if (it2.hasNext())
				{
					dString = EntityEncoder.decode(((PCGElement) it2.next()).getText());
					LevelAbility la = aPCClass.addAddList(level, dString);
					List choiceList = new ArrayList();
					if (la != null)
					{
						la.process(choiceList);
						choiceList.clear();
						while (it2.hasNext())
						{
							dString = EntityEncoder.decode(((PCGElement) it2.next()).getText());
							choiceList.add(dString);
						}
						la.processChoice(null, choiceList, "");
					}
				}
			}
			//
			// abbrev=score
			//
			else if (tag.equals(TAG_PRESTAT) || tag.equals(TAG_POSTSTAT))
			{
				boolean isPre = false;
				if (tag.equals(TAG_PRESTAT))
				{
					isPre = true;
				}
				final int idx = element.getText().indexOf('=');
				if (idx > 0)
				{
					final int idxStat = Globals.getStatFromAbbrev(element.getText().substring(0, idx));
					if (idxStat >= 0)
					{
						try
						{
							aPC.saveStatIncrease(element.getText().substring(0, idx), Integer.parseInt(element.getText().substring(idx + 1)), isPre);
						}
						catch (NumberFormatException nfe)
						{
							warnings.add("Invalid stat modification: " + tag + ":" + element.getText());
						}
					}
					else
					{
						warnings.add("Unknown stat: " + tag + ":" + element.getText());
					}
				}
				else
				{
					warnings.add("Missing = in tag:" + tag + ":" + element.getText());
				}
			}
			else if (pcl != null && TAG_SKILLPOINTSGAINED.equals(tag))
			{
				pcl.setSkillPointsGained(Integer.parseInt(element.getText()));
			}
			else if (pcl != null && TAG_SKILLPOINTSREMAINING.equals(tag))
			{
				pcl.setSkillPointsRemaining(Integer.parseInt(element.getText()), false);
			}
			else if (TAG_DATA.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else
			{
				final String message = "Unknown tag: " + tag + ":" + element.getText();
				warnings.add(message);
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
		final StringTokenizer stok = new StringTokenizer(line.substring(TAG_EXPERIENCE.length() + 1), ":", false);
		try
		{
			aPC.setXP(Integer.parseInt(stok.nextToken()));
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
		if (line.charAt(TAG_TEMPLATESAPPLIED.length() + 1) == '[')
		{
			final PCGTokenizer tokens;
	
			try
			{
				tokens = new PCGTokenizer(line);
			}
			catch (PCGParseException pcgpex)
			{
				final String message =
					"Illegal Template line ignored: " + line + Constants.s_LINE_SEP +
					"Error: " + pcgpex.getMessage();
				warnings.add(message);
				return;
			}
	
			PCGElement element;
			String tag;
			PCTemplate aPCTemplate = null;
	
			Iterator it = tokens.getElements().iterator();
			if (it.hasNext())
			{
				element = (PCGElement) it.next();
				tag = element.getName();
	
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					element = (PCGElement) it2.next();
					tag = element.getName();
					if (TAG_NAME.equals(tag))
					{
						aPCTemplate = addNamedTemplate(element.getText());
						if (aPCTemplate == null)
						{
							break;
						}
					}
					else if (TAG_CHOSENFEAT.equals(tag))
					{
						String mapKey = null;
						String mapValue = null;
						for (Iterator it3 = element.getChildren().iterator(); it3.hasNext();)
						{
							element = (PCGElement) it3.next();
							tag = element.getName();
							if (TAG_MAPKEY.equals(tag))
							{
								mapKey = element.getText();
							}
							else if (TAG_MAPVALUE.equals(tag))
							{
								mapValue = element.getText();
							}
						}
						if ((mapKey != null) && (mapValue != null))
						{
							aPCTemplate.addChosenFeat(EntityEncoder.decode(mapKey), EntityEncoder.decode(mapValue));
						}
					}
				}
			}
		}
		else
		{
			addNamedTemplate(line.substring(TAG_TEMPLATESAPPLIED.length() + 1));
		}
	}

	private PCTemplate addNamedTemplate(final String templateName)
	{
		PCTemplate aPCTemplate = Globals.getTemplateNamed(EntityEncoder.decode(templateName));
		if (aPCTemplate != null)
		{
			final int preXP = aPC.getXP();
			aPCTemplate = aPC.addTemplate(aPCTemplate);
			//
			// XP written to file contains leveladjustment XP. If template modifies XP, then
			// it will have already been added into total. Need to make sure it is not doubled.
			//
			if (aPC.getXP() != preXP)
			{
				aPC.setXP(preXP);
			}
		}
		return aPCTemplate;
	}
	
	/*
	 * ###############################################################
				 * Character Region methods
	 * ###############################################################
	 */

	private void parseRegionLine(String line)
	{
		final String r =
			EntityEncoder.decode(line.substring(TAG_REGION.length() + 1));
		aPC.setRegion(r);
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
			final String message =
				"Illegal Skill line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		Skill aSkill = null;
		String tag;
		PCGElement element;

		final Iterator it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{
			element = (PCGElement) it.next();
			final String skillKey = EntityEncoder.decode(element.getText());
			aSkill = aPC.getSkillKeyed(skillKey);
			if (aSkill == null)
			{
				aSkill = Globals.getSkillKeyed(skillKey);

				if (aSkill != null)
				{
					// Icky: Need to redesign the way skills work!
					// Icky: Having to clone the skill here is UGLY!
					aSkill = (Skill) aSkill.clone();
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
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_SYNERGY.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else if (TAG_OUTPUTORDER.equals(tag))
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
			else if (TAG_CLASSBOUGHT.equals(tag))
			{
				PCGElement childClass = null;
				PCGElement childRanks = null;

				PCGElement child;
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					child = (PCGElement) it2.next();

					if (TAG_CLASS.equals(child.getName()))
					{
						childClass = child;
					}
					else if (TAG_RANKS.equals(child.getName()))
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
			else if (TAG_ASSOCIATEDDATA.equals(tag))
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
			final String message =
				"Illegal Language line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		PCGElement element;
		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
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
			final String message =
				"Illegal Feat line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		boolean added = false;
		Feat aFeat = null;
		PCGElement element;

		final Iterator it = tokens.getElements().iterator();

		// the first element defines the Feat key name
		if (it.hasNext())
		{
			element = (PCGElement) it.next();
			final String featKey = EntityEncoder.decode(element.getText());
			// First, check to see if the PC already has this Feat
			// If so, then we just need to mod it, otherwise
			// we need to create a new one and add it

			aFeat = aPC.getFeatKeyed(featKey);
			if (aFeat != null)
			{
				added = parseFeatsHandleAppliedToAndSaveTags(it, aFeat, line);
			}
			else
			{
				// PC does not have the feat
				aFeat = Globals.getFeatKeyed(featKey);
				if (aFeat != null)
				{
					// Clone the new feat
					aFeat = (Feat) aFeat.clone();
					// parse all the tags for this Feat
					added = parseFeatsHandleAppliedToAndSaveTags(it, aFeat, line);
					if (!added)
					{
						// add it to the list
						aPC.addFeat(aFeat);
					}
				}
				else
				{
					final String message = "Could not add feat: " + featKey;
					warnings.add(message);
					return;
				}
			}
		}
	}

	private void parseVFeatLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
				"Illegal VFeat line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		Feat aFeat = null;
		PCGElement element;

		final Iterator it = tokens.getElements().iterator();

		// the first element defines the Feat key name
		if (it.hasNext())
		{
			element = (PCGElement) it.next();
			final String featKey = EntityEncoder.decode(element.getText());
			aFeat = Globals.getFeatKeyed(featKey);
			if (aFeat == null)
			{
				final String message = "Could not add vfeat: " + featKey;
				warnings.add(message);
				return;
			}
			aPC.addVirtualFeat(featKey, aPC.vFeatList());
			aFeat = PlayerCharacter.getFeatNamedInList(aPC.vFeatList(), featKey);
			aFeat.setNeedsSaving(true);
		}

		parseFeatsHandleAppliedToAndSaveTags(it, aFeat, line);

		// TODO
		// process all additional information
	}

	private boolean parseFeatsHandleAppliedToAndSaveTags(final Iterator it, Feat aFeat, String line)
	{
		boolean added = false;
		PCGElement element;
		String tag;
		while (it.hasNext())
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_APPLIEDTO.equals(tag))
			{
				String appliedToKey = EntityEncoder.decode(element.getText());
				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					aPC.addWeaponProf(updateProficiencyName(appliedToKey, false));
					// addWeaponProf adds the feat to this
					// PC's list, so don't add it again!
					added = true;
				}
				if (appliedToKey.startsWith(TAG_MULTISELECT))
				{
					//
					// Should be in the form:
					// MULTISELECCT:maxcount:#chosen:choice1:choice2:...:choicen
					//
					final StringTokenizer sTok = new StringTokenizer(appliedToKey, ":", false);
					if (sTok.countTokens() > 2)
					{
						sTok.nextToken();		// should be TAG_MULTISELECT
						final int maxChoices = Integer.parseInt(sTok.nextToken());
						sTok.nextToken();		// toss this--number of choices made
						FeatMultipleChoice fmc = new FeatMultipleChoice();
						fmc.setMaxChoices(maxChoices);
						while (sTok.hasMoreTokens())
						{
							fmc.addChoice(sTok.nextToken());
						}
						aFeat.addAssociated(fmc);
					}
					else
					{
						final String message = "Illegal Feat line ignored: " + line;
						warnings.add(message);
					}
				}
				else if ((aFeat.isMultiples() && aFeat.isStacks()) ||
					!aFeat.containsAssociated(appliedToKey))
				{
					aFeat.addAssociated(appliedToKey);
				}
			}
			else if (TAG_SAVE.equals(tag))
			{

				final String saveKey = EntityEncoder.decode(element.getText());
				if (saveKey.startsWith("BONUS") && (saveKey.length() > 6))
				{
					aFeat.addBonusList(saveKey.substring(6), aFeat);
				}

				aFeat.addSave(saveKey);
			}
		}
		return added;
	}

	private void parseFeatPoolLine(String line)
	{
		try
		{
			aPC.setFeats(Double.parseDouble(line.substring(TAG_FEATPOOL.length() + 1)));
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
			final String message =
				"Illegal Weapon proficiencies line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		PObject source = null;
		PCGElement element;
		String tag;

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();

			if (TAG_SOURCE.equals(element.getName()))
			{
				String type = "";
				String name = "";

				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{
					element = (PCGElement) it2.next();
					tag = element.getName();

					if (TAG_TYPE.equals(tag))
					{
						type = element.getText().toUpperCase();
					}
					else if (TAG_NAME.equals(tag))
					{
						name = element.getText();
					}
				}

				if ("".equals(type) || "".equals(name))
				{
					final String message = "Illegal Weapon proficiencies line ignored: " + line;
					warnings.add(message);
					return;
				}

				if ("RACE".equals(type))
				{
					source = aPC.getRace();
				}
				else if ("PCCLASS".equals(type))
				{
					source = aPC.getClassNamed(name);
				}
				else if ("DOMAIN".equals(type))
				{
					source = aPC.getCharacterDomainNamed(name);
				}
				else if ("FEAT".equals(type))
				{
					source = aPC.getFeatNamed(name);
				}

				if (source == null)
				{
					final String message = "Invalid source specification: " + line;
					warnings.add(message);
				}

				break;
			}
		}

		element = (PCGElement) tokens.getElements().get(0);

		if (source == null)
		{
			for (Iterator it = element.getChildren().iterator(); it.hasNext();)
			{
				weaponprofs.add(updateProficiencyName(((PCGElement) it.next()).getText(), true));
			}
		}
		else
		{
			for (Iterator it = element.getChildren().iterator(); it.hasNext();)
			{
				source.addSelectedWeaponProfBonus(updateProficiencyName(((PCGElement) it.next()).getText(), true));
			}
		}
	}

	private static String updateProficiencyName(String aString, boolean decode)
	{
		if (decode)
		{
			aString = EntityEncoder.decode(aString);
		}
		if (Globals.getWeaponProfNamed(aString) == null)
		{
			int idx = aString.indexOf("1-H");
			if (idx >= 0)
			{
				aString = aString.substring(0, idx) + "Exotic" + aString.substring(idx + 3);
			}
			else
			{
				idx = aString.indexOf("2-H");
				if (idx >= 0)
				{
					aString = aString.substring(0, idx) + "Martial" + aString.substring(idx + 3);
				}
			}
		}
		return aString;
	}

	private void checkWeaponProficiencies()
	{
		aPC.setAutomaticFeatsStable(false);
		aPC.featAutoList();		// populate profs array with automatic profs

		for (Iterator it = weaponprofs.iterator(); it.hasNext();)
		{
			if (aPC.hasWeaponProfNamed((String) it.next()))
			{
				it.remove();
			}
		}

		//
		// For some reason, character had a proficiency that they should not have. Inform
		// the user that they no longer have the proficiency.
		//
		if (weaponprofs.size() > 0)
		{
			String s = weaponprofs.toString();
			s = s.substring(1, s.length() - 1);
			final String message = "No longer proficient with following weapon(s):" + Constants.s_LINE_SEP + s;
			warnings.add(message);
		}
	}

	/*
	 * ###############################################################
				 * Character Equipment methods
	 * ###############################################################
	 */

	private void parseMoneyLine(String line)
	{
		aPC.setGold(line.substring(TAG_MONEY.length() + 1));
	}

	private void parseEquipmentLine(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
				"Illegal Equipment line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final String itemKey;
		Equipment aEquip;

		PCGElement element;
		String tag;

		// the first element defines the item key name!!!
		element = (PCGElement) tokens.getElements().get(0);
		itemKey = EntityEncoder.decode(element.getText());

		// might be dynamically created container
		aEquip = aPC.getEquipmentNamed(itemKey);

		if (aEquip == null)
		{
			// Must load custom equipment from the .pcg file
			// before we check the Global list (which may get
			// loaded from customeEquipment.lst) as equipment
			// in the PC's .pcg may contain additional info
			// such as Charges on a wand, etc
			//
			if (line.indexOf(TAG_CUSTOMIZATION) >= 0)
			{
				// might be customized item
				for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
				{
					element = (PCGElement) it.next();

					if (TAG_CUSTOMIZATION.equals(element.getName()))
					{
						String baseItemKey = "";
						String customProperties = "";

						for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
						{
							element = (PCGElement) it2.next();
							tag = element.getName();

							if (TAG_BASEITEM.equals(tag))
							{
								baseItemKey = EntityEncoder.decode(element.getText());
							}
							else if (TAG_DATA.equals(tag))
							{
								customProperties = EntityEncoder.decode(element.getText());
							}
						}

						final Equipment aEquip2 = Globals.getEquipmentKeyed(baseItemKey);
						if (aEquip2 != null)
						{
							aEquip = (Equipment) aEquip2.clone();
							aEquip.load(customProperties, "$", "=");
							aEquip.setOutputName("");
							Globals.addEquipment((Equipment) aEquip.clone());
						}

						break;
					}
				}

			}
			else
			{
				aEquip = Globals.getEquipmentKeyed(itemKey);
				if (aEquip != null)
				{
					// standard item
					aEquip = (Equipment) aEquip.clone();
				}
			}
			if (aEquip == null)
			{
				final String message = "Could not add equipment: " + itemKey + Constants.s_LINE_SEP + PCGParser.s_CHECKLOADEDCAMPAIGNS;
				warnings.add(message);
				return;
			}

			aPC.addEquipment(aEquip);
		}

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_QUANTITY.equals(tag))
			{
				aEquip.setQty(element.getText());
			}
			else if (TAG_OUTPUTORDER.equals(tag))
			{
				int index = 0;
				try
				{
					index = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can or have to do about this
				}
				aEquip.setOutputIndex(index);
			}
			else if (TAG_COST.equals(tag))
			{
			}
			else if (TAG_WT.equals(tag))
			{
			}
		}
	}

	/**
	 * ###############################################################
	 * Character EquipSet Stuff
	 * ###############################################################
	 **/
	private void parseCalcEquipSet(String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * EquipSet is not critical for characters,
			 * no need to stop the load process
			 */
			final String message =
				"Illegal Calc EquipSet line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final String calcEQId = EntityEncoder.decode(((PCGElement) tokens.getElements().get(0)).getText());

		if (calcEQId != null)
		{
			aPC.setCalcEquipSetId(calcEQId);
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
			final String message =
				"Illegal EquipSet line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		String setName = null;
		String setID = null;
		String itemKey = null;
		String setNote = null;
		Float itemQuantity = null;
		boolean useTempMods = false;

		PCGElement element;
		String tag;

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_EQUIPSET.equals(tag))
			{
				setName = EntityEncoder.decode(element.getText());
			}
			else if (TAG_ID.equals(tag))
			{
				setID = element.getText();
			}
			else if (TAG_VALUE.equals(tag))
			{
				itemKey = EntityEncoder.decode(element.getText());
			}
			else if (TAG_QUANTITY.equals(tag))
			{
				try
				{
					itemQuantity = new Float(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					itemQuantity = new Float(0.0f);
				}
			}
			else if (TAG_NOTE.equals(tag))
			{
				setNote = EntityEncoder.decode(element.getText());
			}
			else if (TAG_USETEMPMODS.equals(tag))
			{
				useTempMods = element.getText().endsWith("Y");
			}
		}

		if ((setName == null) || "".equals(setName) ||
			(setID == null) || "".equals(setID))
		{
			final String message = "Illegal EquipSet line ignored: " + line;
			warnings.add(message);
			return;
		}

		final EquipSet aEquipSet;
		Equipment aEquip;
		Equipment eqI;

		aEquipSet = new EquipSet(setID, setName);

		if (setNote != null)
		{
			aEquipSet.setNote(setNote);
		}

		if (itemKey != null)
		{
			aEquipSet.setValue(itemKey);
			eqI = aPC.getEquipmentNamed(itemKey);

			if (eqI == null)
			{
				eqI = Globals.getEquipmentNamed(itemKey);
			}

			if (eqI == null)
			{
				final String message = "Could not find equipment: " + itemKey;
				warnings.add(message);
				return;
			}
			else
			{
				aEquip = (Equipment) eqI.clone();

				if (itemQuantity != null)
				{
					aEquipSet.setQty(itemQuantity);
					aEquip.setQty(itemQuantity);
					aEquip.setNumberCarried(itemQuantity);
				}

				// if the idPath is longer than 3
				// it's inside a container
				if ((new StringTokenizer(setID, ".")).countTokens() > 3)
				{
					// get parent EquipSet

					final EquipSet aEquipSet2 = aPC.getEquipSetByIdPath(aEquipSet.getParentIdPath());

					// get the container
					Equipment aEquip2 = null;
					if (aEquipSet2 != null)
					{
						aEquip2 = aEquipSet2.getItem();
					}

					// add the child to container
					if (aEquip2 != null)
					{
						aEquip2.insertChild(aEquip);
						aEquip.setParent(aEquip2);
					}
				}

				aEquipSet.setItem(aEquip);
			}
		}

		aEquipSet.setUseTempMods(useTempMods);

		aPC.addEquipSet(aEquipSet);
	}

	/**
	 * ###############################################################
	 * Temporary Bonuses
	 * ###############################################################
	 **/
	private void parseTempBonusLine(String line)
	{
		PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
				"Illegal TempBonus line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		String cTag = null;
		String tName = null;
		String bonus;
		String cType;
		String cName;

		PCGElement element;
		String tag;

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_TEMPBONUS.equals(tag))
			{
				cTag = EntityEncoder.decode(element.getText());
			}
			else if (TAG_TEMPBONUSTARGET.equals(tag))
			{
				tName = EntityEncoder.decode(element.getText());
			}
		}
		if ((cTag == null) || (tName == null))
		{
			warnings.add("Illegal TempBonus line ignored: " + line);
			return;
		}

		BonusObj newB = null;
		Feat aFeat;
		Spell aSpell;
		Equipment aEquip;
		PCClass aClass;
		PCTemplate aTemplate;

		StringTokenizer aTok = new StringTokenizer(cTag, "=", false);
		cType = aTok.nextToken();
		cName = aTok.nextToken();

		Equipment aEq = null;

		if (!tName.equals("PC"))
		{
			// bonus is applied to an equipment item
			// so create a new one and add to PC
			Equipment eq = aPC.getEquipmentNamed(tName);
			if (eq == null)
			{
				return;
			}
			aEq = (Equipment) eq.clone();
			//aEq.setWeight("0");
			aEq.resetTempBonusList();
		}

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_TEMPBONUSBONUS.equals(tag))
			{
				bonus = EntityEncoder.decode(element.getText());
			}
			else
			{
				continue;
			}
			if ((bonus == null) || (bonus.length() <= 0))
			{
				continue;
			}

			// Check the Creator type so we know what
			// type of object to set as the creator
			if (cType.equals("FEAT"))
			{
				aFeat = Globals.getFeatKeyed(cName);
				newB = Bonus.newBonus(bonus);
				newB.setCreatorObject(aFeat);
			}
			else if (cType.equals("SPELL"))
			{
				aSpell = Globals.getSpellNamed(cName);
				newB = Bonus.newBonus(bonus);
				newB.setCreatorObject(aSpell);
			}
			else if (cType.equals("EQUIPMENT"))
			{
				aEquip = aPC.getEquipmentNamed(cName);
				newB = Bonus.newBonus(bonus);
				newB.setCreatorObject(aEquip);
			}
			else if (cType.equals("CLASS"))
			{
				aClass = aPC.getClassNamed(cName);
				int idx = bonus.indexOf('|');
				newB = Bonus.newBonus(bonus.substring(idx + 1));
				newB.setCreatorObject(aClass);
			}
			else if (cType.equals("TEMPLATE"))
			{
				aTemplate = aPC.getTemplateNamed(cName);
				newB = Bonus.newBonus(bonus);
				newB.setCreatorObject(aTemplate);
			}

			if (newB == null)
			{
				return;
			}

			// Check to see if the target was the PC or an Item
			if (tName.equals("PC"))
			{
				newB.setApplied(true);
				newB.setTargetObject(aPC);
				aPC.addTempBonus(newB);
			}
			else
			{
				newB.setApplied(true);
				newB.setTargetObject(aEq);
				aEq.addTempBonus(newB);
				aPC.addTempBonus(newB);
			}
		}

		if (aEq != null)
		{
			aEq.setAppliedName(cName);
			aPC.addTempBonusItemList(aEq);
		}
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
			final String message =
				"Illegal Deity line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final String deityName = EntityEncoder.decode(((PCGElement) tokens.getElements().get(0)).getText());
		final Deity aDeity = Globals.getDeityNamed(deityName);

		if (aDeity != null)
		{
			aPC.setDeity(aDeity);
		}
		else if (!Constants.s_NONE.equals(deityName))
		{

			// TODO
			// create Deity object from information contained in pcg
			// for now issue a warning

			final String message =
				"Deity not found: " + deityName + "." + Constants.s_LINE_SEP +
				PCGParser.s_CHECKLOADEDCAMPAIGNS;
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
			final String message =
				"Illegal Domain line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		PCGElement element;
		String tag;

		final Iterator it = tokens.getElements().iterator();

		if (it.hasNext())
		{
			element = (PCGElement) it.next();

			// the first element defines the domain name
			final String domainName = EntityEncoder.decode(element.getText());
			Domain aDomain = Globals.getDomainKeyed(domainName);
			if ((aDomain == null) && (!Constants.s_NONE.equals(domainName)))
			{
				// TODO
				// create Domain object from
				// information contained in pcg
				// But for now just issue a warning

				final String message =
					"Global domain not found: " + domainName + "." + Constants.s_LINE_SEP + PCGParser.s_CHECKLOADEDCAMPAIGNS;
				warnings.add(message);
			}
			else if ((aPC.getCharacterDomainNamed(domainName) == null) && (!Constants.s_NONE.equals(domainName)))
			{
				// PC doesn't have the domain, so create a new
				// one and add it to the PC domain list
				CharacterDomain aCharacterDomain = new CharacterDomain();
				aDomain = (Domain) aDomain.clone();
				aCharacterDomain.setDomain(aDomain);

				while (it.hasNext())
				{
					element = (PCGElement) it.next();
					tag = element.getName();

					if (TAG_SOURCE.equals(tag))
					{
						aCharacterDomain.setDomainSource(sourceElementToString(element));
					}
					else if (TAG_ASSOCIATEDDATA.equals(tag))
					{
						aCharacterDomain.getDomain().addAssociated(EntityEncoder.decode(element.getText()));
					}
				}

				aPC.addCharacterDomain(aCharacterDomain);
				aDomain.setIsLocked(true);

				// TODO
				// set associated list
			}
			else
			{
				// PC already has this domain
				Logging.errorPrint("Duplicate domain found: " + domainName);
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
			final String message =
				"Illegal Spell line ignored: " + line + Constants.s_LINE_SEP +
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
		Object obj = null;

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_SPELLNAME.equals(tag))
			{
				final String spellName = EntityEncoder.decode(element.getText());
				// either NULL (no spell), a Spell instance, or ArrayList of Spells (with same name) returned
				obj = Globals.getSpellMap().get(spellName);
				if (obj instanceof Spell)
				{
					aSpell = (Spell) obj;
					//				aSpell = Globals.getSpellNamed(spellName);
				}
				if (obj == null)
				{
					final String message = "Could not find spell named: " + spellName;
					warnings.add(message);
					return;
				}
			}
			else if (TAG_TIMES.equals(tag))
			{
				try
				{
					times = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_CLASS.equals(tag))
			{
				final String className = EntityEncoder.decode(element.getText());
				aPCClass = aPC.getClassNamed(className);
				if (aPCClass == null)
				{
					final String message = "Invalid class specification: " + className;
					warnings.add(message);
					return;
				}
			}
			else if (TAG_SPELLBOOK.equals(tag))
			{
				spellBook = EntityEncoder.decode(element.getText());
			}
			else if (TAG_SPELLLEVEL.equals(tag))
			{
				try
				{
					spellLevel = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_SOURCE.equals(tag))
			{
				String typeName = "";
				String objectName = "";

				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{

					element = (PCGElement) it2.next();
					tag = element.getName();

					if (TAG_TYPE.equals(tag))
					{
						typeName = element.getText().toUpperCase();
					}
					else if (TAG_NAME.equals(tag))
					{
						objectName = element.getText();
					}
				}

				if ("DOMAIN".equals(typeName))
				{
					source = aPC.getCharacterDomainNamed(objectName);
					if (source == null)
					{
						final String message = "Could not find domain: " + objectName;
						warnings.add(message);
						return;
					}
				}
				else
				{
					// it's either the class, sub-class or a cast-as class
					// first see if it's the class
					if (aPCClass != null && objectName.equals(aPCClass.getName()) ||
						aPCClass.getSpellKey().indexOf(typeName + '|' + objectName) >= 0)
					{
						source = aPCClass;
					}
					else
					{
						source = aPC.getClassNamed(objectName); // see if PC has the class
					}
				}
			}
			else if (TAG_FEATLIST.equals(tag))
			{

				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();)
				{

					aFeat = Globals.getFeatNamed(
						EntityEncoder.decode(((PCGElement) it2.next()).getText()));
					if (aFeat != null)
					{
						metaFeats.add(aFeat);
					}
				}
			}
		}

		if ((obj == null) || (aPCClass == null) || (spellBook == null))
		{
			final String message = "Illegal Spell line ignored: " + line;
			warnings.add(message);
			return;
		}

		/*
		 * this can only happen if the source type was NOT DOMAIN!
		 */
		if (source == null)
		{
			source = aPCClass;
		}

		if (obj instanceof ArrayList)
		{
			// find the instance of Spell in this class
			// best suited to this spell
			for (Iterator eo = ((ArrayList) obj).iterator(); eo.hasNext();)
			{
				aSpell = (Spell) eo.next();
				// valid spell has a non-negative spell level
				if ((aSpell != null) && (aSpell.levelForKey(source.getSpellKey())[0] >= 0))
				{
					break;
				}
			}
		}

		final int[] spellLevels = aSpell.levelForKey(source.getSpellKey());
		for (int sindex = 0; sindex < spellLevels.length; ++sindex)
		{
			final int level = spellLevels[sindex];

			if (level < 0)
			{
				boolean found = false;
				List aList = source.getSpellList();
				if (aList == null)
				{
					continue;
				}
				for (Iterator ri = aList.iterator(); ri.hasNext();)
				{
					PCSpell pcSpell = (PCSpell) ri.next();
					if (pcSpell == null)
					{
						continue;
					}
					found = (aSpell.getName().equals(pcSpell.getName()) && pcSpell.getSpellbook().equals(spellBook));
					if (found)
					{
						break;
					}
				}
				if (!found)
				{
					final String message = "Could not find spell " + aSpell.getName() +
						" in " + shortClassName(source) + " " + source.getName();
					warnings.add(message);
				}
				continue;
			}

			// do not load auto knownspells into default spellbook
			if (spellBook.equals(Globals.getDefaultSpellBook()) &&
				aPCClass.isAutoKnownSpell(aSpell.getKeyName(), level) &&
				aPC.getAutoSpells())
			{
				continue;
			}

			CharacterSpell aCharacterSpell = aPCClass.getCharacterSpellForSpell(aSpell, aPCClass);

			if (aCharacterSpell == null)
			{
				aCharacterSpell = new CharacterSpell(source, aSpell);
				if (!(source instanceof Domain))
				{
					aCharacterSpell.addInfo(level, 1, Globals.getDefaultSpellBook());
				}
				aPCClass.addCharacterSpell(aCharacterSpell);
			}

			SpellInfo aSpellInfo = null;
			if (source.getKeyName().equals(aPCClass.getKeyName()) ||
				!spellBook.equals(Globals.getDefaultSpellBook()))
			{
				aSpellInfo = aCharacterSpell.getSpellInfoFor(spellBook, spellLevel, -1);
				if ((aSpellInfo == null) || !metaFeats.isEmpty())
				{
					aSpellInfo = aCharacterSpell.addInfo(spellLevel, times, spellBook);
				}
			}
			if ((aSpellInfo != null) && !metaFeats.isEmpty())
			{
				aSpellInfo.addFeatsToList(metaFeats);
			}
		} // end sindex for loop

		// just to make sure the spellbook is present
		aPC.addSpellBook(spellBook);
	}

	private void sortCharacterSpells()
	{
		// now sort each classes spell list
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			((PCClass) it.next()).sortCharacterSpellList();
		}
	}

	private void parseKitLine(String line)
	{
		final StringTokenizer stok = new StringTokenizer(line.substring(TAG_KIT.length() + 1), "|", false);
		if (stok.countTokens() != 2)
		{
		}
		/** final String region = */ stok.nextToken(); //TODO: Is this intended to be thrown away? The value is never used.
		/** final String kit = stok.nextToken(); */ //TODO: Is this intended to be thrown away? The value is never used.
		final Kit aKit = Globals.getKitKeyed(line.substring(TAG_KIT.length() + 1));
		if (aKit == null)
		{
			warnings.add("Kit not found: " + line);
			return;
		}
		aPC.addKit(aKit);
	}

	private void parseArmorProfLine(String line)
	{
		final StringTokenizer stok = new StringTokenizer(line.substring(TAG_ARMORPROF.length() + 1), ":", false);
		// should be in the form ARMORPROF:objectype=name:prof:prof:prof:prof:etc.
		String objecttype = stok.nextToken();
		String objectname = objecttype.substring(objecttype.indexOf('=') + 1);
		List aList = new ArrayList();
		while (stok.hasMoreTokens())
		{
			aList.add(stok.nextToken());
		}

		if (objecttype.startsWith(TAG_DEITY))
		{
			if (aPC.getDeity() != null)
			{
				aPC.getDeity().addSelectedArmorProfs(aList);
			}
		}
		else if (objecttype.startsWith(TAG_CLASS))
		{
			PCClass aClass = aPC.getClassNamed(objectname);
			if (aClass != null)
			{
				aClass.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find class " + objectname);
			}
		}
		else if (objecttype.startsWith(TAG_FEAT))
		{
			Feat aFeat = aPC.getFeatNamed(objectname);
			if (aFeat != null)
			{
				aFeat.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find feat " + objectname);
			}
		}
		else if (objecttype.startsWith(TAG_SKILL))
		{
			Skill aSkill = aPC.getSkillNamed(objectname);
			if (aSkill != null)
			{
				aSkill.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find skill " + objectname);
			}
		}
		else if (objecttype.startsWith(TAG_DOMAIN))
		{
			Domain aDomain = aPC.getCharacterDomainNamed(objectname);
			if (aDomain != null)
			{
				aDomain.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find domain " + objectname);
			}
		}
		else if (objecttype.startsWith(TAG_EQUIPMENT))
		{
			Equipment eq = aPC.getEquipmentNamed(objectname);
			if (eq != null)
			{
				eq.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find equipment named " + objectname);
			}
		}
		else if (objecttype.startsWith(TAG_TEMPLATE))
		{
			PCTemplate aTemplate = aPC.getTemplateNamed(objectname);
			if (aTemplate != null)
			{
				aTemplate.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrint("Error in line:" + line + "\n Could not find template " + objectname);
			}
		}
		else
		{
			Logging.errorPrint("Error loading line :" + line);
			return; // no object type found
		}

	}
	/*
	 * ###############################################################
			 * Spell List Information methods
	 * ###############################################################
	 */

	/*
	 * #Spell List Information
	 * SPELLLIST:sourceclassname|spelllistentry|spelllistentry
	 */
	private void parseSpellListLines(String line)
	{
		final String subLine = line.substring(TAG_SPELLLIST.length() + 1);
		final StringTokenizer stok = new StringTokenizer(subLine, "|", false);
		try
		{
			final String className = stok.nextToken();
			final PCClass aClass = aPC.getClassNamed(className);
			while (aClass != null && stok.hasMoreTokens())
			{
				final String tok = stok.nextToken();
				aClass.addClassSpellList(tok);
			}
		}
		catch (NumberFormatException exc)
		{
			//TODO: Should this really be ignored???
		}
	}

	private static String shortClassName(Object o)
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

	private void parseCharacterCompLine(String line)
	{
		aPC.getMiscList().set(1, EntityEncoder.decode(line.substring(TAG_CHARACTERCOMP.length() + 1)));
	}

	private void parseCharacterAssetLine(String line)
	{
		aPC.getMiscList().set(0, EntityEncoder.decode(line.substring(TAG_CHARACTERASSET.length() + 1)));
	}

	private void parseCharacterMagicLine(String line)
	{
		aPC.getMiscList().set(2, EntityEncoder.decode(line.substring(TAG_CHARACTERMAGIC.length() + 1)));
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
			final String message =
				"Illegal Follower line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final Follower aFollower = new Follower("", "", "");

		PCGElement element;
		String tag;
		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_FOLLOWER.equals(tag))
			{
				aFollower.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_TYPE.equals(tag))
			{
				aFollower.setType(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_RACE.equals(tag))
			{
				aFollower.setRace(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_HITDICE.equals(tag))
			{
				try
				{
					aFollower.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_FILE.equals(tag))
			{
				aFollower.setRelativeFileName(EntityEncoder.decode(element.getText()));
			}
		}

		if (!"".equals(aFollower.getFileName()) &&
			!"".equals(aFollower.getName()) &&
			!"".equals(aFollower.getType()))
		{
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
			final String message =
				"Illegal Master line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final Follower aMaster = new Follower("", "", "");

		PCGElement element;
		String tag;
		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_MASTER.equals(tag))
			{
				aMaster.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_TYPE.equals(tag))
			{
				aMaster.setType(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_HITDICE.equals(tag))
			{
				try
				{
					aMaster.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_FILE.equals(tag))
			{
				/*
				 * quick and dirty way to handle ':'
				 * need to come up with a clean solution before releasing
				 *
				 * author: Thomas Behr 09-09-02
				 */
				aMaster.setRelativeFileName(EntityEncoder.decode(element.getText()));
			}
		}

		if (!"".equals(aMaster.getFileName()) &&
			!"".equals(aMaster.getName()) &&
			!"".equals(aMaster.getType()))
		{
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
			final String message =
				"Illegal Notes line ignored: " + line + Constants.s_LINE_SEP +
				"Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}

		final NoteItem ni = new NoteItem(-1, -1, "", "");

		PCGElement element;
		String tag;

		for (Iterator it = tokens.getElements().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_NOTE.equals(tag))
			{
				ni.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_ID.equals(tag))
			{
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
			else if (TAG_PARENTID.equals(tag))
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
			else if (TAG_VALUE.equals(tag))
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
	private static boolean isComment(String line)
	{
		return line.trim().startsWith("#");
	}

	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static String sourceElementToString(PCGElement source)
	{
		String type = "";
		String name = "";
		String level = "";
		String defined = "";

		PCGElement element;
		String tag;
		for (Iterator it = source.getChildren().iterator(); it.hasNext();)
		{
			element = (PCGElement) it.next();
			tag = element.getName();

			if (TAG_TYPE.equals(tag))
			{
				type = element.getText();
			}
			else if (TAG_NAME.equals(tag))
			{
				name = element.getText();
			}
			else if (TAG_LEVEL.equals(tag))
			{
				level = element.getText();
			}
			else if (TAG_DEFINED.equals(tag))
			{
				defined = element.getText().toUpperCase();
			}
		}

		//TODO:gorm - guestimate good starting buffer size
		final StringBuffer buffer = new StringBuffer(1000);
		buffer.append(type);
		buffer.append(("Y".equals(defined)) ? '=' : '|');
		buffer.append(name);
		if (!"".equals(level))
		{
			buffer.append('|');
			buffer.append(level);
		}

		return buffer.toString();
	}

	/*
	 * ###############################################################
				 * Inner classes
	 * ###############################################################
	 */

	private static final class PCGElement
	{
		private final String name;

		private String text;
		private List children;

		private PCGElement(String name)
		{
			this.name = name;
		}

		private void addContent(PCGElement child)
		{
			if (children == null)
			{
				this.children = new ArrayList(0);
			}
			children.add(child);
		}

		private void addContent(String argText)
		{
			text = argText;
		}

		private List getChildren()
		{
			if (children == null)
			{
				this.children = new ArrayList(0);
			}
			return children;
		}

		private String getName()
		{
			return name;
		}

		private String getText()
		{
			return (text != null) ? text : "";
		}

		public String toString()
		{
			//TODO:gorm - optimize stringbuffer size
			final StringBuffer buffer = new StringBuffer(1000);
			buffer.append('<').append(getName()).append('>').append(LINE_SEP);
			buffer.append("<text>").append(getText()).append("</text>").append(LINE_SEP);
			for (Iterator it = getChildren().iterator(); it.hasNext();)
			{
				buffer.append(it.next().toString()).append(LINE_SEP);
			}
			buffer.append("</").append(getName()).append('>');

			return buffer.toString();
		}

	}

	private static final class PCGTokenizer
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
		private PCGTokenizer(String line) throws PCGParseException
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
		private PCGTokenizer(String line,
			String delimiters) throws PCGParseException
		{
			final char[] dels = delimiters.toCharArray();

			this.innerDelimiter = String.valueOf(dels[0]);
			this.outerDelimiter = String.valueOf(dels[1]);
			this.nestedStartDelimiter = String.valueOf(dels[2]);
			this.nestedStopDelimiter = String.valueOf(dels[3]);

			this.nestedStartDelimiterChar = nestedStartDelimiter.charAt(0);
			this.nestedStopDelimiterChar = nestedStopDelimiter.charAt(0);

			this.elements = new ArrayList(0);

			tokenizeLine(line);
		}

		private List getElements()
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
			String token;
			String tag = null;
			final StringBuffer buffer = new StringBuffer(1000);

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
							if (index >= 0)
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
						if ((index >= 0 ) && (index == token.length() - 1))
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

					++nestedDepth;
				}
				else if (token.equals(nestedStopDelimiter))
				{

					--nestedDepth;

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
				final int index = token.indexOf(innerDelimiter);
				if (index >= 0)
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
			for (int i = 0; i < chars.length; ++i)
			{
				if (chars[i] == nestedStartDelimiterChar)
				{
					++delimCount;
				}
				else if (chars[i] == nestedStopDelimiterChar)
				{
					--delimCount;
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
	}
}
