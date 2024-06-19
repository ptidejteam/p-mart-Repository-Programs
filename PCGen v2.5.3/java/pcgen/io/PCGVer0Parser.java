/*
 * PCGVer0Parser.java
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
 * Created on March 15, 2002, 4:30 PM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.Spell;
import pcgen.core.Utility;

/**
 * <code>PCGVer0Parser</code>
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer0Parser
{
        private List warnings = new ArrayList();
        private PlayerCharacter aPC;

	/*
         * Version of file being read
         * 0:
         * oh oh
         * 1:
         * hit points are no longer written with the CON modifier.
         * 2:
         * skills are saved by class
         */
	private int pcgVersion;

	/*
         * Used only for legacy (pre 2.3.0) Domain class pcg files
         */
        private int ignoreDomainClassLine = 0;

        /**
         * Constructor
         */
        public PCGVer0Parser(PlayerCharacter aPC)
        {
                this.aPC = aPC;
        }

        /**
         * Selector
         *
         * <br>author: Thomas Behr 15-03-02
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
	 * <br>author: Thomas Behr 15-03-02
	 *
	 * @param s   the String to parse
	 */
	public void parsePCG(String s) throws PCGParseException
	{
		StringTokenizer tokens = new StringTokenizer(s, "\n", true);
		String[] lines = new String[tokens.countTokens()];		// overkill, but easy
		for (int i = 0; tokens.hasMoreTokens();)
		{
			String line = tokens.nextToken();
			if (line.equals("\n"))
			{
				line = "";
			}
			else
			{
				if (tokens.hasMoreTokens())
				{
					String endLine = tokens.nextToken();
				}
			}
			lines[i++] = line;
		}

		int i = 0;
		if (checkCampaignLine(lines[i]))
		{
			i++;
		}

		pcgVersion = parseVersionLine(lines[i]);

		/*
		 * pcgVersion < 0:
		 *   no version tag
		 * pcgVersion > -1:
		 *   ignore second line (version line)
		 */
		if (pcgVersion > -1)
		{
			i++;
		}

		parseNameLine(lines[i++]);
		parseStatsLine(lines[i++]);
		parseClassesLine(lines[i++]);
		parseFeatsLine(lines[i++]);

		//Note, the following order is neccessary, for historical reasons...
		parseRaceLine(lines[i+2]);
		parseSkillsLine(lines[i++]);
		parseDeityLine(lines[i++]);
		i++;

		int dx = 0;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			if (++dx == ignoreDomainClassLine)
			{
				i++;
			}
			else
			{
				parseClassSpellLine((PCClass)it.next(), lines[i++]);
			}
		}

		if (++dx == ignoreDomainClassLine)
		{
			i++;
		}

		parseLanguagesLine(lines[i++]);
		int weaponProfLine = i++;
//		parseWeaponProfLine(lines[i++]);
		parseUnusedPointsLine(lines[i++]);
		parseMiscLine(lines[i++]);
		parseEquipmentLine(lines[i++]);
		i = parseGoldBioDescriptionLine(lines, i);

		dx = 0;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext(); it.next())
		{
			if (++dx == ignoreDomainClassLine)
			{
				i++;
			}
			parseClassesSkillLine(lines[i++]);
		}
		if (++dx == ignoreDomainClassLine)
		{
			i++;
		}

		i = parseExperienceAndMiscLine(lines, i);
		i = parseClassSpecialtyAndSaveLines(lines, i);
		parseTemplateLine(lines[i++]);
		i = parseNoteLine(lines, i);

		//
		// This needs to be called after the templates are loaded
		//
		parseWeaponProfLine(lines[weaponProfLine]);

		// Need to adjust for older versions of PCG files here
		if (pcgVersion < 1)
		{
			pcgAdjustHpRolls(-aPC.calcStatMod(Globals.CONSTITUTION));
		}
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

	private void pcgAdjustHpRolls(int increment)
	{
		if (aPC.getRace().hitDice() != 0) {
			aPC.getRace().PCG_adjustHpRolls(increment);
                }

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			((PCClass)it.next()).PCG_adjustHpRolls(increment);
		}
	}

        private boolean checkCampaignLine(String line)
        {
                return line.startsWith("CAMPAIGNS:");
        }

	/**
         * parseVersionLine should return 220 if string is 2.2.0 - this assumes a decimal release (no characters).
         */
	private int parseVersionLine(String line) throws PCGParseException
	{
		int version = -1;
                StringTokenizer aTok = new StringTokenizer(line, ":");
                String tag = (aTok.hasMoreTokens()) ? aTok.nextToken() : "";
                String ver = (aTok.hasMoreTokens()) ? aTok.nextToken() : "";

                // if the pcg file starts with VERSION data then lets process it
                try {
                        if (tag.equals("VERSION"))
                        {
                                aTok = new StringTokenizer(ver, ".");
                                version = 0;
                                while (aTok.hasMoreTokens()) {
                                        version = version * 10 + Integer.parseInt(aTok.nextToken());
                                }
                        }
                } catch (Exception ex) {
                        throw new PCGParseException("parseVersionLine", line, ex.getMessage());
                }

		return version;
	}

	private void parseNameLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		if (aTok.hasMoreTokens()) {
                        aPC.setName(Utility.unEscapeColons2(aTok.nextToken()));
                } else {
                        throw new PCGParseException("parseNameLine", line, "No character name found.");
                }

		if (aTok.hasMoreTokens()) {
			aPC.setPlayersName(Utility.unEscapeColons2(aTok.nextToken()));
                }
	}

	private void parseStatsLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		// Check for different STAT counts
		int statCount = 6;
		if (line.startsWith("STATS:"))
		{
			aTok.nextToken();			// ignore "STATS:"
			statCount = Integer.parseInt(aTok.nextToken());
		}
		if (statCount != Globals.s_ATTRIBLONG.length)
		{
                        final String message =
                                "Number of Stats for character is " + statCount + ". " +
				"PCGen is currently using " + Globals.s_ATTRIBLONG.length + ". " +
                                "Cannot load character.";
                        throw new PCGParseException("parseStatsLine", line, message);
		}

                try {
                        for (int i = 0; aTok.hasMoreTokens() && (i < Globals.s_ATTRIBLONG.length); i++) {
                                aPC.setStat(i, Integer.parseInt(aTok.nextToken()));
                        }
                        if (aTok.hasMoreTokens()) {
                                aPC.setPoolAmount(Integer.parseInt(aTok.nextToken()));
                        }
                        if (aTok.hasMoreTokens()) {
                                aPC.setCostPool(Integer.parseInt(aTok.nextToken()));
                        }
                } catch (Exception ex) {
                        throw new PCGParseException("parseStatsLine", line, ex.getMessage());
                }
	}

	private void parseClassesLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		String aName = null;
		boolean getNext = true;
		String aString = "";
		int x = 0;
		while (aTok.hasMoreTokens())
		{
			if (getNext) {
				x++;
				aName = aTok.nextToken();
			}
			else {
				aName = aString;
                        }

			getNext = true;
			if (!aTok.hasMoreTokens()) {
				break;
                        }

			boolean needCopy = true;
			PCClass aClass = aPC.getClassKeyed(aName);
			if (aClass == null)
			{
				aClass = Globals.getClassKeyed(aName);
			}
			else
			{
				needCopy = false;
			}

			if ((aClass == null) && aName.equalsIgnoreCase("Domain"))
			{
				System.out.println("Domain class found and ignored. " +
                                                   "Please check character to verify conversion is successful.");
				ignoreDomainClassLine = x;
			}
			else if (aClass == null)
			{
                                final String message =
                                        "Class not found: " + aName + ".\n" +
                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                throw new PCGParseException("parseClassesLine", line, message);
			}

			// ClassName:SubClassName:ProhibitedString:Level:[hp1:[hp2:...[hpn:]]]skillPool:SpellBaseStat:

			// If the class wasn't found we will parse through the data anyway, but just toss it
			String subClassName = aTok.nextToken().trim();
			String prohibitedString = aTok.nextToken().trim();
			int k = Integer.parseInt(aTok.nextToken());
			if (aClass != null)
			{
				if (needCopy)
				{
					aClass = (PCClass)aClass.clone();
					aPC.getClassList().add(aClass);
				}
				aClass.setSubClassName(subClassName);
				aClass.setProhibitedString(prohibitedString);
			}

			//
			// NOTE: race is not yet set here, so skillpool calculated in addLevel will be out by
			// racial intelligence adjustment and BonusSkillsPerLevel, but we're just going to trash
			// the calculated value in the next step anyway
			//
			for (int i = 0; i < k; i++)
			{
				int iHp = Integer.parseInt(aTok.nextToken());
				if (aClass != null)
				{
					aClass.addLevel(false);
					aClass.hitPointList()[i] = new Integer(iHp);
				}
			}
			Integer skillPool = new Integer(aTok.nextToken());
			if (aClass != null)
			{
				aClass.setSkillPool(skillPool);
			}

			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if ((Globals.getStatFromAbbrev(aString.toUpperCase()) > -1) ||
                                    aString.equalsIgnoreCase(Globals.s_NONE) ||
                                    aString.equalsIgnoreCase("Any") ||
                                    aString.equalsIgnoreCase("SPELL"))
				{
					if (aClass != null)
					{
						aClass.setSpellBaseStat(aString);
					}
				}
				else
				{
					getNext = false;
				}
			}
		}
		aPC.setCurrentHP(aPC.hitPoints());
	}

	private void parseFeatsLine(String line) throws PCGParseException
	{
		try
		{
			String aName = null;
			String aString = null;
			final StringTokenizer aTok = new StringTokenizer(line, ":");
			while (aTok.hasMoreTokens())
			{
				aName = aTok.nextToken().trim();
				if (aName.length() == 0)
				{
					continue;
				}
				final int k = Integer.parseInt(aTok.nextToken());
				final StringTokenizer bTok = new StringTokenizer(aName, "[]");

				aName = bTok.nextToken();
				Feat aFeat = Globals.getFeatKeyed(aName);
				if ((aFeat != null) && !aPC.hasFeatAutomatic(aName))
				{
					aFeat = (Feat)aFeat.clone();
					aPC.modFeat(aFeat.getKeyName(), true, !aFeat.isMultiples());
					if (aFeat.isMultiples() &&
						(aFeat.getAssociatedList().size() == 0) &&
						(aPC.getFeatKeyed(aFeat.getKeyName()) == null))
					{
						aPC.addFeat(aFeat);
					}

					aFeat = aPC.getFeatKeyed(aFeat.getKeyName());
					while (bTok.hasMoreTokens())
					{
						aString = bTok.nextToken();
						if (aString.startsWith("BONUS") && (aString.length() > 6))
						{
							aFeat.getBonusList().add(aString.substring(6));
						}
						aFeat.getSaveList().add(aString);
					}
				}
				else
				{
					aFeat = new Feat();
				}

				for (int j = 0; j < k; j++)
				{
					aString = aTok.nextToken();
					if (aName.endsWith("Weapon Proficiency"))
					{
						aPC.addWeaponProf(aString);
					}
					else if ((aFeat.isMultiples() && aFeat.isStacks()) || !aFeat.getAssociatedList().contains(aString))
					{
						aFeat.getAssociatedList().add(aString);
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new PCGParseException("parseFeatsLine", line, ex.getMessage());
		}
	}

	private void parseRaceLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		int x = 0;
		Integer[] hitPointList = null;
		Race aRace = null;
		String raceName = "";
                String token;
		for (int i = 0; aTok.hasMoreElements(); i++)
		{
			token = aTok.nextToken();
			switch (i)
			{
				case 0:
					aRace = Globals.getRaceKeyed(token);
					raceName = token;
					if (aRace != null)
					{
						aPC.setRace(aRace);
						if (aRace.hitDice() != 0)
						{
							hitPointList = new Integer[aRace.hitDice()];
						}
					}
					else
					{
                                                final String message =
                                                        "Race not found: " + token + ".\n" +
                                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                                throw new PCGParseException("parseRaceLine", line, message);
					}
					break;
				case 1:
                                        try {
                                                aPC.setAlignment(Integer.parseInt(token), true);
                                        } catch (Exception ex) {
                                                throw new PCGParseException("parseRaceLine", line, ex.getMessage());
                                        }
					break;
				case 2:
                                        try {
                                                aPC.setHeight(Integer.parseInt(token));
                                        } catch (Exception ex) {
                                                throw new PCGParseException("parseRaceLine", line, ex.getMessage());
                                        }
					break;
				case 3:
                                        try {
                                                aPC.setWeight(Integer.parseInt(token));
                                        } catch (Exception ex) {
                                                throw new PCGParseException("parseRaceLine", line, ex.getMessage());
                                        }
					break;
				case 4:
                                        try {
                                                aPC.setAge(Integer.parseInt(token));
                                        } catch (Exception ex) {
                                                throw new PCGParseException("parseRaceLine", line, ex.getMessage());
                                        }
					break;
				case 5:
					aPC.setGender(token);
					break;
				case 6:
					aPC.setHanded(token);
					break;
				default:
					if (hitPointList != null)
					{
                                                try {
                                                        hitPointList[x++] = new Integer(Integer.parseInt(token));
                                                } catch (Exception ex) {
                                                        throw new PCGParseException("parseRaceLine", line, ex.getMessage());
                                                }
						if (aRace != null)
						{
							if (x == aRace.hitDice())
							{
								aPC.getRace().setHitPointList(hitPointList);
								return;
							}
						}
					}
					else
					{
						if (x == 0)
						{
                                                        final String message =
                                                                "Saved race (" + raceName + ") no longer " +
                                                                "has a HITDICE tag.";
                                                        warnings.add(message);
						}
					}
			}
		}
	}

	private void parseSkillsLine(String line) throws PCGParseException
	{
		final StringTokenizer skillTokenizer = new StringTokenizer(line, ":");
		String skillName;
		List aRankList;
		while (skillTokenizer.hasMoreElements())
		{
			skillName = skillTokenizer.nextToken();
			if (!skillTokenizer.hasMoreTokens()) {
				return;
                        }

			final Float aFloat = new Float(skillTokenizer.nextToken());

			//
			// If newer version, then we can determine which skill belongs to which class as it
			// is saved in the PCG file
			//
			aRankList = new ArrayList();
			if (pcgVersion >= 2)
			{
				final Integer iCount = new Integer(skillTokenizer.nextToken());
				for (int i = 0; i < iCount.intValue(); i++)
				{
					aRankList.add(skillTokenizer.nextToken() + ":" + skillTokenizer.nextToken());
				}
			}


			// Locate the skill in question, add to list if not already there
			Skill aSkill = aPC.getSkillKeyed(skillName);
			if (aSkill == null)
			{
				for (int i = 0; i < Globals.getSkillList().size(); i++)
					if (skillName.equals(Globals.getSkillList().get(i).toString()))
					{
						aSkill = (Skill)Globals.getSkillList().get(i);
						aSkill = (Skill)aSkill.clone();
						aPC.getSkillList().add(aSkill);
						break;
					}
			}

			if (aSkill != null)
			{
				for (int i = 0; i < aRankList.size(); i++)
				{
					String bRank = (String)aRankList.get(i);
					int iOffs = bRank.indexOf(':');
					Float fRank = new Float(bRank.substring(iOffs + 1));
					PCClass aClass = aPC.getClassKeyed(bRank.substring(0, iOffs));
					if ((aClass != null) || bRank.substring(0, iOffs).equals(Globals.s_NONE))
					{
						bRank = aSkill.modRanks(fRank.doubleValue(), aClass, true);
						if (bRank.length() != 0) {
							System.out.println("loadSkillsLine: " + bRank);
                                                }
					}
					else
					{
						System.out.println("Class not found: " + bRank.substring(0, iOffs));
					}
				}

				if (pcgVersion < 2)
				{
					final String bRank = aSkill.modRanks(aFloat.doubleValue(), null, true);
					if (bRank.length() != 0) {
						System.out.println("loadSkillsLine: " + bRank);
                                        }
				}

				if (aSkill.choiceList().size() > 0 && aFloat.intValue() > 0)
				{
					for (int i = 0; i < aFloat.intValue(); i++) {
						aSkill.getAssociatedList().add(skillTokenizer.nextToken());
                                        }
				}
			}
			else
			{
				System.out.println("Skill not found: " + skillName);
				if (aFloat.doubleValue() != 0.0)
				{
                                        final String message =
                                                "Ranked skill not found: " + skillName + "(" + aFloat + ").\n" +
                                                PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                        warnings.add(message);
				}
			}
		}
	}

	private void parseDeityLine(String line) throws PCGParseException
	{
		final StringTokenizer deityTokenizer = new StringTokenizer(line, ":");

		String token;
		for (int i = 0; deityTokenizer.hasMoreElements(); i++)
		{
			token = deityTokenizer.nextToken();
			switch (i)
			{
				case 0:
                                        boolean deityFound = false;
                                        Deity aDeity;
                                        for (Iterator it = Globals.getDeityList().iterator(); it.hasNext();)
                                        {
                                                aDeity = (Deity)it.next();
                                                if (aDeity.toString().equals(token))
                                                {
                                                        aPC.setDeity(aDeity);
                                                        deityFound = true;
                                                        break;
                                                }
                                        }
                                        if (!deityFound && !token.equals(Globals.s_NONE))
                                        {
                                                final String message =
                                                        "Deity not found: " + token + ".\n" +
                                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                                warnings.add(message);
                                        }
					break;
				default:
					int j = aPC.getFirstEmptyCharacterDomain();
					if (j == -1)
					{
						CharacterDomain aCD = new CharacterDomain();
						aPC.getCharacterDomainList().add(aCD);
						j = aPC.getCharacterDomainList().size() - 1;
					}
					if (j >= 0)
					{
						final StringTokenizer cdTok = new StringTokenizer(token, "=", false);
						final String domainName = cdTok.nextToken();
						CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(j);
						Domain aDomain = Globals.getDomainKeyed(domainName);
						if (aDomain != null)
						{
							aDomain = (Domain)aDomain.clone();
							aCD.setDomain(aDomain);
							if (cdTok.hasMoreTokens())
							{
								String sSource = cdTok.nextToken();
								aCD.setDomainSource(sSource);
							}
							aDomain.setIsLocked(true);
						}
						else
						{
							if (!domainName.equals(Globals.s_NONE))
							{
                                                                final String message =
                                                                        "Domain not found: " + token + ".\n" +
                                                                        PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                                                warnings.add(message);
							}
						}
					}
			}
		}
	}

	private void parseClassSpellLine(PCClass aClass, String line)
	{
		final StringTokenizer classSpellTokenizer = new StringTokenizer(line, ":");
		String spellName;
		String spellLine = null;
		StringTokenizer bTok = null;
		Spell aSpell = null;
		while (classSpellTokenizer.hasMoreTokens())
		{
			spellLine = classSpellTokenizer.nextToken();
			bTok = new StringTokenizer(spellLine, "|", false);
			spellName = bTok.nextToken();
			aSpell = aClass.getSpellNamed(spellName);
			if (aSpell == null)
			{
				aSpell = (Spell)Globals.getSpellNamed(spellName);
				if (aSpell != null)
				{
					aSpell = (Spell)aSpell.clone();
					String className = new String(aClass.getKeyName());
					if (aClass.getCastAs().length() > 0) {
						className = aClass.getCastAs();
                                        }
					String levelString = aSpell.levelForClass(className, aClass.getName());
					aSpell.setClassLevels(levelString);
					aClass.spellList().add(aSpell);
				}
			}
			if (aSpell != null && bTok.countTokens() == 0)
			{
				aSpell.addToSpellBook(Globals.getDefaultSpellBook(), false);
				aPC.addSpellBook(Globals.getDefaultSpellBook());
			}
			if (aSpell != null)
			{
				String bookName = null;
				while (bTok.hasMoreTokens())
				{
					bookName = bTok.nextToken();
					aPC.addSpellBook(bookName);
					if (aSpell.getSpellBooks().contains(bookName))
					{
						if (bTok.hasMoreTokens()) {
							bTok.nextToken(); // this book already exists, so burn the token
                                                }
					}
					else
					{
						aSpell.getSpellBooks().add(bookName);
						if (bTok.hasMoreTokens()) {
							aSpell.getTimes().add(new Integer(bTok.nextToken()));
                                                }
						else {
							aSpell.getTimes().add(new Integer(1));
                                                }
					}
				}
			}
		}
	}

	private void parseLanguagesLine(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		while (aTok.hasMoreTokens())
		{
			aPC.addLanguage(aTok.nextToken());
		}
	}

	private void parseWeaponProfLine(String line)
	{
//		final StringTokenizer aTok = new StringTokenizer(line, ":");
//		while (aTok.hasMoreTokens())
//		{
//			aPC.addWeaponProf(aTok.nextToken());
//		}
		int iState = 0;
		StringTokenizer aTok = new StringTokenizer(line, ":", false);
		Race aRace = null;
		PCClass aClass = null;
		Domain aDomain = null;
		Feat aFeat = null;

		ArrayList myProfs = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (aString.startsWith("RACE="))
			{
				iState = 1;
				aRace = aPC.getRace();
				continue;
			}
			else if (aString.startsWith("CLASS="))
			{
				iState = 2;
				aString = aString.substring(6);
				aClass = aPC.getClassNamed(aString);
				continue;
			}
			else if (aString.startsWith("DOMAIN="))
			{
				iState = 3;
				aString = aString.substring(7);
				aDomain = aPC.getCharacterDomainNamed(aString);
				continue;
			}
			else if (aString.startsWith("FEAT="))
			{
				iState = 4;
				aString = aString.substring(5);
				aFeat = aPC.getFeatNamed(aString);
				continue;
			}

			switch (iState)
			{
				case 1:
					if (aRace != null)
					{
						aRace.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				case 2:
					if (aClass != null)
					{
						aClass.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				case 3:
					if (aDomain != null)
					{
						aDomain.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				case 4:
					if (aFeat != null)
					{
						aFeat.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				default:
					myProfs.add(aString);
					break;
			}
		}


		aPC.setAutomaticFeatsStable(false);
		aPC.featAutoList();		// populate profs array with automatic profs
		ArrayList nonproficient = new ArrayList();
		for (Iterator e = myProfs.iterator(); e.hasNext();)
		{
			final String aString = (String)e.next();
			if (!aPC.hasWeaponProfNamed(aString))
			{
				nonproficient.add(aString);
			}
		}


		//
		// For some reason, character had a proficiency that they should not have. Inform
		// the user that they no longer have the proficiency.
		//
		if (nonproficient.size() != 0)
		{
			String s = nonproficient.toString();
			s = s.substring(1, s.length() - 1);
			final String message = "No longer proficient with following weapon(s):\n" + s;
			warnings.add(message);
		}
	}

	private void parseUnusedPointsLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
                try {
                        aPC.setSkillPoints(Integer.parseInt(aTok.nextToken()));
                        aPC.setFeats(Integer.parseInt(aTok.nextToken()));
                } catch (Exception ex) {
                        throw new PCGParseException("parseUnusedPointsLine", line, ex.getMessage());
                }
	}

	private void parseMiscLine(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		String token;
		for (int i = 0; aTok.hasMoreTokens(); i++)
		{
			token = Utility.unEscapeColons2(aTok.nextToken().trim());
			switch (i)
			{
				case 0:
					aPC.setEyeColor(token);
					break;
				case 1:
					aPC.setSkinColor(token);
					break;
				case 2:
					aPC.setHairColor(token);
					break;
				case 3:
					aPC.setHairStyle(token);
					break;
				case 4:
					aPC.setSpeechTendency(token);
					break;
				case 5:
					aPC.setPhobias(token);
					break;
				case 6:
					aPC.setInterests(token);
					break;
				case 7:
					aPC.setTrait1(token);
					break;
				case 8:
					aPC.setTrait2(token);
					break;
				case 9:
					aPC.setCatchPhrase(token);
					break;
				case 10:
					aPC.setLocation(token);
					break;
				case 11:
					aPC.setResidence(token);
					break;
			}
		}
	}

	private Float parseCarried(Float qty, String aName)
	{
		float carried = 0.0F;
		if (aName.equals("Y"))
		{
			carried = qty.floatValue();
		}
		else if (aName.equals("N"))
		{
			carried = 0.0F;
		}
		else
		{
			try
			{
				carried = Float.parseFloat(aName);
			}
			catch (Exception e)
			{
				carried = 0.0F;
			}
		}
		return new Float(carried);
	}

	private void parseEquipmentLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		String aName;
		Equipment eq = null;
		HashMap containers = new HashMap();
		boolean bFound;
		HashMap headerChildren = null;
		while (aTok.hasMoreTokens())
		{
			headerChildren = new HashMap();
			aName = aTok.nextToken().trim();

			final StringTokenizer anTok = new StringTokenizer(aName, ";");
			String sized = "";
			String head1 = "";
			String head2 = "";
			String customName = "";
			String customProp = "";
			int tokenCount = anTok.countTokens();
			if ((tokenCount >= 4) && (tokenCount <= 6))
			{
				//
				// baseName;size;head1;head2
				// name;baseName;size;head1;head2
				// name;baseName;size;head1;head2;sprop
				//
				if (tokenCount >= 5)
				{
					customName = anTok.nextToken();
				}
				String baseName = anTok.nextToken();
				sized = anTok.nextToken();
				head1 = anTok.nextToken();
				head2 = anTok.nextToken();
				aName = baseName;
				if (tokenCount == 6)
				{
					customProp = anTok.nextToken();
				}
			}

			eq = (Equipment)Globals.getEquipmentKeyed(aName);
			if (eq == null)
			{
				eq = Globals.getEquipmentFromName(aName);		// Try to strip the modifiers off the item
			}

			bFound = true;
			if (eq == null)
			{
				eq = new Equipment();		// dummy container to stuff equipment info into
				bFound = false;
			}
			else
			{
				eq = (Equipment)eq.clone();
			}

			if (customProp.length() != 0)
			{
				eq.setSpecialProperties(customProp);
			}

			eq.addEqModifiers(head1, true);
			eq.addEqModifiers(head2, false);
			if (((sized.length() != 0) && !eq.getSize().equals(sized)) || (eq.getEqModifierList(true).size() + eq.getEqModifierList(false).size() != 0) || (customProp.length() != 0))
			{
				if (sized.length() == 0)
				{
					sized = eq.getSize();
				}
				eq.resizeItem(sized);
				eq.nameItemFromModifiers();
			}
			//
			// If item doesn't exist, add it to the equipment list
			//
			if (bFound)
			{
				if (customName.length() > 0)
				{
					eq.setName(customName);
				}
				Globals.addEquipment((Equipment)eq.clone());
			}


			eq.setQty(aTok.nextToken());
			if (eq.qty().floatValue() > 1 && eq.acceptsChildren())  //hack to see if it is a headerParent instead of a normal container
			{
				final int origQty = eq.qty().intValue();
				eq.setIsHeaderParent(true);
				final StringTokenizer cTok = new StringTokenizer(aTok.nextToken(), "|", false);
				if (cTok.countTokens() == 1)
				{
					Globals.debugPrint("Correct Path " + cTok.countTokens());

					final boolean firstEquipped = cTok.nextToken().equals("Y");
					int numberCarried = (parseCarried(new Float(origQty), aTok.nextToken())).intValue();

					for (int i = 0; i < origQty; i++)
					{
						final Equipment aHChild = eq.createHeaderParent();
						aHChild.clearHeaderChildren();
						if (i == 0)
							aHChild.setIsEquipped(firstEquipped);
						if (numberCarried-- > 0)
							aHChild.setCarried(new Float(0));
						headerChildren.put(aHChild.getKeyName(), aHChild);
					}
				}
				else
				{
					for (int i = 0; i < origQty; i++)
					{
						final Equipment aHChild = eq.createHeaderParent();
						aHChild.clearHeaderChildren();
						aHChild.setIsEquipped(cTok.nextToken().equals("Y"));
						final StringTokenizer bTok = new StringTokenizer(cTok.nextToken(), "@", false);
						aHChild.setCarried(parseCarried(aHChild.qty(), bTok.nextToken()));
						if (bTok.hasMoreTokens())
							containers.put(aHChild.getKeyName(), (String)bTok.nextToken());
						headerChildren.put(aHChild.getKeyName(), aHChild);
					}
					aTok.nextToken();
				}
			}
			else
			{
				eq.setIsEquipped(aTok.nextToken().equals("Y"));
				final StringTokenizer bTok = new StringTokenizer(aTok.nextToken(), "@", false);
				eq.setCarried(parseCarried(eq.qty(), bTok.nextToken()));
				if (bTok.hasMoreTokens())
					containers.put(eq.getKeyName(), (String)bTok.nextToken());
			}

			eq.setHand(Equipment.getHandNum(aTok.nextToken()));
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
			{
				eq.setNumberEquipped(Integer.parseInt(aTok.nextToken()));
			}

			if (bFound)
			{
				aPC.getEquipmentList().put(eq.getKeyName(), eq);
				aPC.getEquipmentList().putAll(headerChildren);
			}
			else
			{
				//
				// Only show message if not natural weapon
				//
				if (customName.indexOf("Natural/") < 0)
				{
                                        final String message =
                                                "Equipment not found: " + aName + " (" + eq.qty() + ").\n" +
                                                PCGIOHandler.s_CHECKLOADEDCAMPAIGNS;
                                        warnings.add(message);
				}
			}
		}
		//now insert parent/child relationships
		Equipment aParent = null;
		for (Iterator it = containers.keySet().iterator(); it.hasNext();)
		{
			aName = (String)it.next();
			eq = (Equipment)aPC.getEquipmentList().get(aName);
			if (eq != null)
			{
				aParent = (Equipment)aPC.getEquipmentList().get((String)containers.get(aName));
				aParent.insertChild(eq);
			}
		}
	}

	private int parseGoldBioDescriptionLine(String[] lines, int start) throws PCGParseException
	{
		int current = start;
		int i = 0;

		try
		{
			boolean nextLine = true;
			String line = "";
			String cString = "";
			while (i < 3)
			{
				if (nextLine)
				{
				    line = lines[current++];
				}

				int k = line.indexOf(':');
				while ((k > 0) && line.charAt(k - 1) == '\\')
				{
					k = line.indexOf(':', k + 1);
				}

				if ((k < 0) || ((k > 0) && (line.charAt(k - 1) == '\\')))
				{
					k = -1;
				}

				if (k == -1)
				{
					cString = cString.concat(line);
					cString = cString.concat("\r\n");
					nextLine = true;
				}
				else
				{
					k = line.indexOf(':');
					while ((k > 0) && (line.charAt(k - 1) == '\\'))
					{
						k = line.indexOf(':', k + 1);
					}
					cString = cString.concat(line.substring(0, k));
					Globals.debugPrint("Line " + i + ": " + cString);
					String tempStr = "";
					for (int j = 0; j < cString.length(); j++)
					{
						if (cString.charAt(j) != '\\')
						{
							tempStr += cString.charAt(j);
						}
						else
						{
							if (j + 1 < cString.length() && cString.charAt(j + 1) != ':')
							{
								tempStr += "\\";
							}
						}
					}
					switch (i)
					{
						case 0:
							aPC.setGold(tempStr);
							break;
						case 1:
							aPC.setBio(tempStr);
							break;
						case 2:
							aPC.setDescription(tempStr);
							break;
					}
					if (i < 3)
					{
						line = line.substring(k + 1);
					}
					cString = "";
					nextLine = false;
					i++;
				}
			}
		}
		catch (Exception ex)
		{
			throw new PCGParseException("parseGoldBioDescriptionLine", Integer.toString(i) + ":" + lines[current], ex.getMessage());
		}

		return current;
	}

	private void parseClassesSkillLine(String line) throws PCGParseException
	{
		StringTokenizer aTok = new StringTokenizer(line, ":");
		String token = aTok.nextToken();
		PCClass aClass = (PCClass)aPC.getClassKeyed(token);
		if (aClass == null)
		{
                        return; //Is this right? Shouldn't an exception be thrown instead?
		}
		while (aTok.hasMoreTokens())
		{
			if (aClass != null)
			{
				aClass.skillList().add(aTok.nextToken().trim());
			}
		}
	}

	private int parseExperienceAndMiscLine(String[] lines, int start) throws PCGParseException
	{
                int current = start;

                try {

                        int i = 0;
                        boolean nextLine = true;
                        String line = "";
                        String cString = "";
                        while (i < 6)
                        {
                                if (nextLine) {
                                        line = lines[current++];
                                }

                                int k = line.indexOf(':');
                                while (k > 0 && line.charAt(k - 1) == '\\') {
                                        k = line.indexOf(':', k + 1);
                                }

                                if ((k < 0 ) || (line.charAt(k - 1) == '\\')) {
                                        k = -1;
                                }

                                if (k == -1)
                                {
                                        cString = cString.concat(line);
                                        cString = cString.concat("\r\n");
                                        nextLine = true;
                                        //EOL so don't try 4 or 5, it'll break old PCG files
                                        if (i > 3) {
                                                break;
                                        }
                                }
                                else
                                {
                                        k = line.indexOf(':');
                                        while (line.charAt(k - 1) == '\\') {
                                                k = line.indexOf(':', k + 1);
                                        }

                                        cString = cString.concat(line.substring(0, k));
                                        Globals.debugPrint("Line " + i + ": " + cString);
                                        switch (i)
                                        {
					case 0:
						aPC.setExperience(new Integer(cString));
						break;
					case 1:
					case 2:
					case 3:
						String tempStr = "";
						for (int j = 0; j < cString.length(); j++)
						{
							if (cString.charAt(j) != '\\') {
								tempStr += cString.charAt(j);
                                                        }
							else
							{
								if ((j + 1 < cString.length()) &&
                                                                    (cString.charAt(j + 1) != ':')) {
									tempStr += "\\";
                                                                }
                                                        }
						}
						aPC.getMiscList().set(i - 1, tempStr.trim());
						break;
					case 4:
						aPC.setFPoints(cString);
						break;
					case 5:
						aPC.setDPoints(cString);
						break;
                                        }
                                        if (i < 6) {
                                                line = line.substring(k + 1);
                                        }
                                        cString = "";
                                        nextLine = false;
                                        i++;
                                }
                        }
                } catch (Exception ex) {
                        throw new PCGParseException("parseExperienceAndMiscLine", lines[current], ex.getMessage());
                }

                return current;
	}

	private int parseClassSpecialtyAndSaveLines(String[] lines, int start) throws PCGParseException
	{
                int current = start;

                try {
                        String line;
                        String token;
                        StringTokenizer aTok;
                        for (int i = 0; i < aPC.getClassList().size(); i++)
                        {
                                line = lines[current++];
                                if (line == null) {
                                        return current;
                                }

                                aTok = new StringTokenizer(line, ":", false);

                                String bString = aTok.nextToken();
                                PCClass aClass = aPC.getClassKeyed(bString);
                                if (aClass == null || aClass.getKeyName().equals("Domain")) {
                                        continue;
                                }

                                while (aTok.hasMoreTokens())
                                {
                                        token = aTok.nextToken();
                                        if (token.startsWith("SPECIAL")) {
                                                aClass.getSpecialtyList().add(token.substring(7));
                                        }
                                        else
                                        {
                                                /**
                                                 * This no longer needs to be saved in the PCG file.
                                                 * Need to strip it from older versions of
                                                 * save files. Gets handled differently in class
                                                 */
                                                if (token.equals("Smite Evil")) {
                                                        continue;
                                                }

                                                if (token.startsWith("BONUS"))
                                                {
                                                        aClass.getBonusList().add(token.substring(6));
                                                        if (token.lastIndexOf("|PCLEVEL|") > -1)
                                                        {
                                                                String tmp = token.substring(token.lastIndexOf("PCLEVEL"));
                                                                StringTokenizer cTok = new StringTokenizer(tmp, "|");
                                                                cTok.nextToken(); // should be PCLEVEL
                                                                if (cTok.hasMoreTokens())
                                                                {
                                                                        String sa = "Bonus Caster Level for " +
                                                                                cTok.nextToken();
                                                                        aPC.getSpecialAbilityList().add(sa);
                                                                }
                                                        }
                                                }
                                                else if (!aPC.getSpecialAbilityList().contains(token)) {
                                                        aPC.getSpecialAbilityList().add(token);
                                                }
                                                if (!aClass.getSaveList().contains(token) || token.startsWith("BONUS")) {
                                                        aClass.getSaveList().add(token);
                                                }
                                        }
                                }
                        }
                } catch (Exception ex) {
                        throw new PCGParseException("parseClassSpecialtyAndSaveLines", lines[current], ex.getMessage());
                }

                return current;
	}

	private void parseTemplateLine(String line)
	{
		if (line == null)
		{
			return;
		}
		else
		{
                        String work = new String(line);
			if (work.startsWith("TEMPLATE:"))
			{
				work = work.substring(9);
			}
			final StringTokenizer tokens = new StringTokenizer(work, ":");
                        PCTemplate aTemplate;
			while (tokens.hasMoreTokens())
			{
				aTemplate = Globals.getTemplateNamed(tokens.nextToken());
				/**
				 * bug fix:
				 * do not add (additional) gold on character load
				 *
				 * this is just a quick fix;
				 * actually I do not know how to do this properly
				 * oh well, seems to work --- for now
				 *
				 * author: Thomas Behr 06-01-02
				 */
				if (aTemplate != null)
				{
					aPC.addTemplate(aTemplate, false);
				}
			}
		}
	}

	private int parseNoteLine(String[] lines, int i)
	{
		if (i>= lines.length)
			return i;
		String lastLineParsed = lines[i++];
		boolean flag = (lastLineParsed != null && lastLineParsed.startsWith("NOTES:"));
		NoteItem anItem = null;
		while (flag == true)
		{
			if (lastLineParsed.startsWith("NOTES:"))
			{
				final StringTokenizer aTok = new StringTokenizer(lastLineParsed.substring(6), ":", false);
				final int id_value = Integer.parseInt(aTok.nextToken());
				final int id_parent = Integer.parseInt(aTok.nextToken());
				final String id_name = aTok.nextToken();
				final String id_text = aTok.nextToken();
				anItem = new NoteItem(id_value, id_parent, id_name, id_text);
				aPC.addNotesItem(anItem);
			}
			else
                        {
				anItem.setValue(anItem.getValue() + System.getProperty("line.separator") + lastLineParsed);
                        }

			lastLineParsed = (i<lines.length-1 ? lines[i++] : null);
			flag = (lastLineParsed != null && !lastLineParsed.equals(":ENDNOTES:"));
		}
		return i;
	}
}
