/*
 * PCGVer2Creator.java
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
 * Created on March 19, 2002, 4:15 PM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
//  import pcgen.core.spell.Spell;

/**
 * <code>PCGVer2Creator</code><br>
 * @author Thomas Behr 19-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer2Creator implements IOConstants
{
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 */
	public PCGVer2Creator(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * create PCG string for a given PlayerCharacter
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @return a String in PCG format, containing all information
	 *         PCGen associates with a given PlayerCharacter
	 */
	public String createPCGString()
	{
		final StringBuffer buffer = new StringBuffer();

		appendPCGVersionLine(buffer);

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
                 *
                 * hmmm, better have
		 * CAMPAIGNS:>campaign_name<|CAMPAIGNS:>campaign_name<|...
		 */
		appendNewline(buffer);
		appendComment("System Information", buffer);
		//appendCampaignLineOldFormat(buffer);
		appendCampaignLine(buffer);
		appendVersionLine(buffer);
		appendRollMethodLine(buffer);
		appendPurchasePointsLine(buffer);
		//appendUnlimitedPoolCheckedLine(buffer);
		appendPoolPointsLine(buffer);
		appendGameModeLine(buffer);
		appendTabLabelLine(buffer);
                appendAutoSpellsLine(buffer);

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
		appendNewline(buffer);
		appendComment("Character Bio", buffer);
		appendCharacterNameLine(buffer);
		appendTabNameLine(buffer);
		appendPlayerNameLine(buffer);
		appendHeightLine(buffer);
		appendWeightLine(buffer);
		appendAgeLine(buffer);
		appendGenderLine(buffer);
		appendHandedLine(buffer);
		appendSkinColorLine(buffer);
		appendEyeColorLine(buffer);
		appendHairColorLine(buffer);
		appendHairStyleLine(buffer);
		appendLocationLine(buffer);
		appendResidenceLine(buffer);
		appendPersonalityTrait1Line(buffer);
		appendPersonalityTrait2Line(buffer);
		appendSpeechPatternLine(buffer);
		appendPhobiasLine(buffer);
		appendInterestsLine(buffer);
		appendCatchPhraseLine(buffer);
		appendPortraitLine(buffer);

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
                 *
                 * hmmm better have
		 * STAT:STR|SCORE:18
		 */
		appendNewline(buffer);
		appendComment("Character Attributes", buffer);
		appendStatLines(buffer);
		appendAlignmentLine(buffer);
		appendRaceLine(buffer);

		/*
		 * #Character Class(es)
		 * CLASS:Fighter|LEVEL=3
		 * CLASSABILITIESLEVEL:Fighter=1(>This would only display up to the level the character has already,)
		 * CLASSABILITIESLEVEL:Fighter=2(>with any special abilities not covered by other areas,)
		 * CLASSABILITIESLEVEL:Fighter=3(>such as skills, feats, etc., but would list SA's, and the like<)
		 * CLASS:Wizard|LEVEL=1
		 * CLASSABILITIESLEVEL:Wizard=1(SA's, MEMORIZE:Y, etc)
                 *
		 * hmmm, better have
		 * CLASS:Fighter|LEVEL:3|SKILLPOOL:0
		 * CLASS:Wizard|LEVEL:1|SKILLPOOL:0|CANCASTPERDAY:1,1
		 */
		appendNewline(buffer);
		appendComment("Character Class(es)", buffer);
		appendClassLines(buffer);

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
                 */
		appendNewline(buffer);
		appendComment("Character Experience", buffer);
		appendExperienceLine(buffer);

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		appendNewline(buffer);
		appendComment("Character Templates", buffer);
		appendTemplateLines(buffer);

		/*
		 * #Character Skills
		 * CLASSBOUGHT:Fighter
		 * SKILL:Alchemy|CROSSCLASS:Y|COST:2|RANK:7  (Should be Obvious what each of these does, I hope ;p)
		 * SKILL:Survival|CLASS:Y|COST:1|SYNERGY:Wilderness Lore=5=2|RANK:10
		 * CLASSBOUGHT:Wizard
		 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK7
		 *
		 *
		 * hmmm, better have
		 * SKILL:Alchemy|SYNERGY:....|OUTPUTORDER:1|CLASSBOUGHT:[CLASS:FIGHTER|RANKS:7|COST:2|CLASSSKILL:N]
		 * SKILL:Spellcraft|SYNERGY:....|OUTPUTORDER:1|CLASSBOUGHT:[CLASS:WIZARD|RANKS:7|COST:1|CLASSSKILL:Y]
		 */
		appendNewline(buffer);
		appendComment("Character Skills", buffer);
		appendSkillLines(buffer);

		/*
		 * #Character Languages
		 */
		appendNewline(buffer);
		appendComment("Character Languages", buffer);
		appendLanguageLine(buffer);

		/*
		 * Anything that is already Pipe Delimited should be in parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
                 *
                 * hmmm, better have colons and pipes encoded as entities
		 * FEAT:Alertness|TYPE:General|SAVE:BONUS&colon;SKILL&pipe;Listen,Spot&pipe;2|DESC:+2 on Listen and Spot checks
		 */
		appendNewline(buffer);
		appendComment("Character Feats", buffer);
		appendFeatLines(buffer);

		/*
		 * #Character Weapon proficiencies
		 */
		appendNewline(buffer);
		appendComment("Character Weapon proficiencies", buffer);
		appendWeaponProficiencyLines(buffer);

		/*
		 * This is the REALLY ugly part for all characters as it should contain ALL the information for the equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Sword (Long)|COST:5|WT:5|>other info<|CARRIED:Y|EQUIPPED:Y|LOCATION:Primary
		 * EQUIPNAME:Backpack|COST:5|WT:5|CONTAINS:(blah string)|CARRIED:Y|EQUIPPED:Y|TOTALWT:10
		 * EQUIPNAME:Rope (Silk)|COST:5|WT:5|CARRIED:Y|EQUIPPED:N|LOCATION:Backpack
		 */
		appendNewline(buffer);
		appendComment("Character Equipment", buffer);
		appendEquipmentLines(buffer);
		appendEquipmentSetLines(buffer);

		/*
		 * #Character Deity/Domain
		 * DEITY:Yondalla|DEITYDOMAINS:Good,Law,Protection|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD(>list of level by level spells)
                 *
		 * hmmm, better have
                 * DEITY:Yondalla|DEITYDOMAINS:[DOMAIN:Good|DOMAIN:Law|DOMAIN:Protection]|...
		 * DOMAINSPELLS:GOOD|SPELLLIST:(>list of level by level spells)
		 */
		appendNewline(buffer);
		appendComment("Character Deity/Domain", buffer);
		appendDeityLine(buffer);
		appendDomainLines(buffer);

		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
                 *
                 * hmmm, moved CANCASTPERDAY to standard class line
		 */
		appendNewline(buffer);
		appendComment("Character Spells Information", buffer);
		appendSpellLines(buffer);

		/*
		 * #Character Description/Bio/History
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		appendNewline(buffer);
		appendComment("Character Description/Bio/History", buffer);
		appendCharacterBioLine(buffer);
		appendCharacterDescLine(buffer);

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		appendNewline(buffer);
		appendComment("Character Master/Follower", buffer);
		appendFollowerLines(buffer);

		/*
		 * #Character Notes Tab
		 */
		appendNewline(buffer);
		appendComment("Character Notes Tab", buffer);
		appendNotesLines(buffer);

		return buffer.toString();
	}


	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

	private void appendPCGVersionLine(StringBuffer buffer)
	{
                buffer.append(TAG_PCGVERSION).append(":");
                buffer.append("2.0");
                buffer.append("\n");
        }
        
	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */

	private void appendCampaignLineOldFormat(StringBuffer buffer)
	{
		buffer.append(TAG_CAMPAIGNS).append(":");

		String del = "";
		Campaign aCamp = null;
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign)it.next();
			if (aCamp.isLoaded())
			{
				buffer.append(del).append(aCamp.getName());
				del = ":";
			}
		}
		buffer.append("\n");

	}

	private void appendCampaignLine(StringBuffer buffer)
	{
		String del = "";
		Campaign aCamp = null;
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign)it.next();
			if (aCamp.isLoaded())
			{
				buffer.append(del);
				buffer.append(TAG_CAMPAIGN).append(":");
				buffer.append(aCamp.getName());
				del = "|";
			}
		}
		buffer.append("\n");
	}

	/*
         * modified this function to output the version number as displayed in pcgenprop.properties
	 * instead of a simple int. This will record the version more accurately.
         *
         * <br>author: merton_monk 10/24/01
	 */
	private void appendVersionLine(StringBuffer buffer)
	{
		buffer.append(TAG_VERSION).append(":");

		ResourceBundle d_properties;
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			buffer.append(d_properties.getString("VersionNumber"));
		}
		catch (MissingResourceException mre)
		{
			d_properties = null;
		}

		buffer.append("\n");
	}

	private void appendRollMethodLine(StringBuffer buffer)
	{
		buffer.append(TAG_ROLLMETHOD).append(":");
		buffer.append(Integer.toString(SettingsHandler.getRollMethod()));
		buffer.append("|");
		buffer.append(TAG_EXPRESSION).append(":");
		buffer.append(SettingsHandler.getRollMethodExpression(SettingsHandler.getRollMethod()));
		buffer.append("\n");
	}

	private void appendPurchasePointsLine(StringBuffer buffer)
	{
		buffer.append(TAG_PURCHASEPOINTS).append(":");
		buffer.append((SettingsHandler.isPurchaseStatMode()) ? "Y" : "N");
		buffer.append("|");
		buffer.append(TAG_TYPE).append(":");
		// TODO
		buffer.append("\n");
	}

	//private void appendUnlimitedPoolCheckedLine(StringBuffer buffer)
	//{
		//buffer.append(TAG_UNLIMITEDPOOLCHECKED).append(":");
		//buffer.append((SettingsHandler.isStatPoolUnlimited()) ? "Y" : "N");
		//buffer.append("\n");
	//}

	private void appendPoolPointsLine(StringBuffer buffer)
	{
		buffer.append(TAG_POOLPOINTS).append(":");
		buffer.append(Integer.toString(aPC.getPoolAmount()));
		buffer.append("\n");
	}

	private void appendGameModeLine(StringBuffer buffer)
	{
		buffer.append(TAG_GAMEMODE).append(":");
		buffer.append(Globals.getGameMode());
		buffer.append("\n");
	}

	private void appendTabLabelLine(StringBuffer buffer)
	{
		buffer.append(TAG_TABLABEL).append(":");
		buffer.append(Integer.toString(SettingsHandler.getNameDisplayStyle()));
		buffer.append("\n");
	}

	private void appendAutoSpellsLine(StringBuffer buffer)
	{
                buffer.append(TAG_AUTOSPELLS).append(":");
                buffer.append(aPC.getAutoSpells() ? "Y" : "N");
		buffer.append("\n");
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */

	private void appendCharacterNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERNAME).append(":");
		buffer.append(EntityEncoder.encode(aPC.getName()));
		buffer.append("\n");
	}

	private void appendTabNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_TABNAME).append(":");
		buffer.append(EntityEncoder.encode(aPC.getTabName()));
		buffer.append("\n");
	}

	private void appendPlayerNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_PLAYERNAME).append(":");
		buffer.append(EntityEncoder.encode(aPC.getPlayersName()));
		buffer.append("\n");
	}

	private void appendHeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_HEIGHT).append(":");
		buffer.append(Integer.toString(aPC.getHeight()));
		buffer.append("\n");
	}

	private void appendWeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_WEIGHT).append(":");
		buffer.append(Integer.toString(aPC.getWeight()));
		buffer.append("\n");
	}

	private void appendAgeLine(StringBuffer buffer)
	{
		buffer.append(TAG_AGE).append(":");
		buffer.append(Integer.toString(aPC.getAge()));
		buffer.append("\n");
	}

	private void appendGenderLine(StringBuffer buffer)
	{
		buffer.append(TAG_GENDER).append(":");
		buffer.append(EntityEncoder.encode(aPC.getGender()));
		buffer.append("\n");
	}

	private void appendHandedLine(StringBuffer buffer)
	{
		buffer.append(TAG_HANDED).append(":");
		buffer.append(EntityEncoder.encode(aPC.getHanded()));
		buffer.append("\n");
	}

	private void appendSkinColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_SKINCOLOR).append(":");
		buffer.append(EntityEncoder.encode(aPC.getSkinColor()));
		buffer.append("\n");
	}

	private void appendEyeColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_EYECOLOR).append(":");
		buffer.append(EntityEncoder.encode(aPC.getEyeColor()));
		buffer.append("\n");
	}

	private void appendHairColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRCOLOR).append(":");
		buffer.append(EntityEncoder.encode(aPC.getHairColor()));
		buffer.append("\n");
	}

	private void appendHairStyleLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRSTYLE).append(":");
		buffer.append(EntityEncoder.encode(aPC.getHairStyle()));
		buffer.append("\n");
	}

	private void appendLocationLine(StringBuffer buffer)
	{
		buffer.append(TAG_LOCATION).append(":");
		buffer.append(EntityEncoder.encode(aPC.getLocation()));
		buffer.append("\n");
	}

	private void appendResidenceLine(StringBuffer buffer)
	{
		buffer.append(TAG_RESIDENCE).append(":");
		buffer.append(EntityEncoder.encode(aPC.getResidence()));
		buffer.append("\n");
	}

	private void appendPersonalityTrait1Line(StringBuffer buffer)
	{
		buffer.append(TAG_PERSONALITYTRAIT1).append(":");
		buffer.append(EntityEncoder.encode(aPC.getTrait1()));
		buffer.append("\n");
	}

	private void appendPersonalityTrait2Line(StringBuffer buffer)
	{
		buffer.append(TAG_PERSONALITYTRAIT2).append(":");
		buffer.append(EntityEncoder.encode(aPC.getTrait2()));
		buffer.append("\n");
	}

	private void appendSpeechPatternLine(StringBuffer buffer)
	{
		buffer.append(TAG_SPEECHPATTERN).append(":");
		buffer.append(EntityEncoder.encode(aPC.getSpeechTendency()));
		buffer.append("\n");
	}

	private void appendPhobiasLine(StringBuffer buffer)
	{
		buffer.append(TAG_PHOBIAS).append(":");
		buffer.append(EntityEncoder.encode(aPC.getPhobias()));
		buffer.append("\n");
	}

	private void appendInterestsLine(StringBuffer buffer)
	{
		buffer.append(TAG_INTERESTS).append(":");
		buffer.append(EntityEncoder.encode(aPC.getInterests()));
		buffer.append("\n");
	}

	private void appendCatchPhraseLine(StringBuffer buffer)
	{
		buffer.append(TAG_CATCHPHRASE).append(":");
		buffer.append(EntityEncoder.encode(aPC.getCatchPhrase()));
		buffer.append("\n");
	}

	private void appendPortraitLine(StringBuffer buffer)
	{
		buffer.append(TAG_PORTRAIT).append(":");
		buffer.append(EntityEncoder.encode(aPC.getPortraitPath()));
		buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Attributes methods
	 * ###############################################################
	 */

	private void appendStatLines(StringBuffer buffer)
	{
		for (Iterator i = aPC.getStatList().getStats().iterator(); i.hasNext();)
		{
			final PCStat aStat = (PCStat)i.next();
			buffer.append(TAG_STAT).append(":");
			buffer.append(aStat.getAbb());
			buffer.append("|");
			buffer.append(TAG_SCORE).append(":");
			buffer.append(Integer.toString(aStat.getBaseScore()));
			buffer.append("\n");
		}
	}

	private void appendAlignmentLine(StringBuffer buffer)
	{
		buffer.append(TAG_ALIGNMENT).append(":");
		buffer.append(Constants.s_ALIGNSHORT[aPC.getAlignment()]);
		buffer.append("\n");
	}

	private void appendRaceLine(StringBuffer buffer)
	{
		buffer.append(TAG_RACE).append(":");
		buffer.append(EntityEncoder.encode(aPC.getRace().getKeyName()));
		buffer.append("\n");

		// TODO
		// don't we want to save more info here?
	}

	/*
	 * ###############################################################
         * Character Class(es) methods
	 * ###############################################################
	 */

	private void appendClassLines(StringBuffer buffer)
	{
                Cache specials;

                String aSave;
                List saves;
                List specialties;
                String aSource;
                SpecialAbility aSpecialAbility;
                List specialAbilities;
                
                String subClassName;
                String prohibited;
                
		PCClass aClass;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass)it.next();

                        int classLevel = aClass.getLevel().intValue();
                        
			buffer.append(TAG_CLASS).append(":");
			buffer.append(EntityEncoder.encode(aClass.getKeyName()));

                        subClassName = aClass.getSubClassName();
                        if (!subClassName.equals("")) {
                                buffer.append("|");
                                buffer.append(TAG_SUBCLASS).append(":");
                                buffer.append(EntityEncoder.encode(subClassName));
                        }

			buffer.append("|");
			buffer.append(TAG_LEVEL).append(":");
			buffer.append(Integer.toString(classLevel));
			buffer.append("|");
			buffer.append(TAG_SKILLPOOL).append(":");
			buffer.append(aClass.skillPool().toString());

			// determine if this class can cast spells
			boolean isCaster = false;
			for (Iterator it2 = aClass.getCastList().iterator(); it2.hasNext();)
			{
				if (!"0".equals((String)it2.next()))
				{
					isCaster = true;
					break;
				}
			}

			boolean isPsionic = (aClass.getKnownList().size() > 0) && !isCaster;

			if (isCaster || isPsionic)
			{
				buffer.append("|");
				buffer.append(TAG_SPELLBASE).append(":");
                                buffer.append(EntityEncoder.encode(aClass.getSpellBaseStat()));
				buffer.append("|");
				buffer.append(TAG_CANCASTPERDAY).append(":");
				buffer.append((String)aClass.getCastList().get(classLevel - 1));
			}

                        prohibited = aClass.getProhibitedString();
                        if (!prohibited.equals("")) {
                                buffer.append("|");
                                buffer.append(TAG_PROHIBITED).append(":");
                                buffer.append(EntityEncoder.encode(prohibited));
                        }

			buffer.append("\n");

                        specials = new Cache();

                        String key;
                        key = TAG_SPECIALTY + "0";
                        for (Iterator it2 = aClass.getSpecialtyList().iterator(); it2.hasNext();)
                        {
                                specials.put(key, (String)it2.next());
                        }
                        
                        key = TAG_SAVE + "0";
                        for (int i = 0; i < aClass.getSaveCount(); i++)
                        {
                                aSave = aClass.getSave(i);
                                aSpecialAbility = aClass.getSpecialAbilityNamed(aSave);
                                if (aSpecialAbility != null)
                                {
                                        int relevantLevel = 1;
                                        aSource = aSpecialAbility.getSource();
                                        try {
                                                relevantLevel = Integer.parseInt(
                                                        aSource.substring(aSource.lastIndexOf("|") + 1));
                                        }
                                        catch (NumberFormatException nfe) {
                                                // nothing we can do about it
                                        }
                                        specials.put(TAG_SA + (relevantLevel - 1), aSpecialAbility.getName());
                                }
                                else
                                {
                                        specials.put(key, aSave);
                                }
                        }

			for (int i = 0; i < classLevel; i++)
			{
				buffer.append(TAG_CLASSABILITIESLEVEL).append(":");
				buffer.append(EntityEncoder.encode(aClass.getKeyName())).append("=").append(Integer.toString(i + 1));
				buffer.append("|");
				buffer.append(TAG_HITPOINTS).append(":");
				buffer.append(aClass.getHitPointList(i).toString());

                                appendSpecials(buffer, specials.get(TAG_SAVE + i), TAG_SAVES, TAG_SAVE);
                                appendSpecials(buffer, specials.get(TAG_SPECIALTY + i), TAG_SPECIALTIES, TAG_SPECIALTY);
                                appendSpecials(buffer, specials.get(TAG_SA + i), TAG_SPECIALABILITIES, TAG_SA);

//  				buffer.append("|");
//  				buffer.append(TAG_DATA).append(":");
//  				buffer.append("(");
//  				// TODO
//  				buffer.append(")");
				buffer.append("\n");
			}
		}
	}
        private void appendSpecials(StringBuffer buffer,
                                    List specials, String tag_group, String tag_item) 
        {
                if ((specials != null) && (!specials.isEmpty())) {
                        buffer.append("|");
                        buffer.append(tag_group).append(":");
                        buffer.append("[");
                        String del = "";
                        for (Iterator it2 = specials.iterator(); it2.hasNext();) {
                                buffer.append(del);
                                buffer.append(tag_item).append(":");
                                buffer.append(EntityEncoder.encode((String)it2.next()));
                                del = "|";
                        }
                        buffer.append("]");
                }
        }
        
	/*
	 * ###############################################################
         * Character Experience methods
	 * ###############################################################
	 */

	private void appendExperienceLine(StringBuffer buffer)
	{
		buffer.append(TAG_EXPERIENCE).append(":");
		buffer.append(Integer.toString(aPC.getXP()));
		buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Templates methods
	 * ###############################################################
	 */

	private void appendTemplateLines(StringBuffer buffer)
	{
		PCTemplate aTemplate;
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			aTemplate = (PCTemplate)it.next();
			buffer.append(TAG_TEMPLATESAPPLIED).append(":");
			buffer.append(EntityEncoder.encode(aTemplate.getKeyName()));
			buffer.append("\n");
		}

		// TODO
		// don't we want to save more info here?
	}

	/*
	 * ###############################################################
         * Character Skills methods
	 * ###############################################################
	 */

	private void appendSkillLines(StringBuffer buffer)
	{
		String del = "";
		Skill aSkill;
		for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
		{
			aSkill = (Skill)it.next();

			if ((aSkill.getRank().doubleValue() > 0) ||
			  (aSkill.getOutputIndex() != 0))
			{

				buffer.append(TAG_SKILL).append(":");
				buffer.append(EntityEncoder.encode(aSkill.getKeyName()));
				del = "|" + TAG_SYNERGY + ":";
				for (Iterator it2 = aSkill.getSynergyList().iterator(); it2.hasNext();)
				{
					buffer.append(del).append((String)it2.next());
					del = ",";
				}
				buffer.append("|");
				buffer.append(TAG_OUTPUTORDER).append(":");
				buffer.append(Integer.toString(aSkill.getOutputIndex()));
				buffer.append("|");
				String ranks;
				String className;
				String classRanks;
				PCClass aPCClass;
				for (Iterator it2 = aSkill.getRankList().iterator(); it2.hasNext();)
				{
					classRanks = (String)it2.next();

					int index = classRanks.indexOf(":");
					className = classRanks.substring(0, index);
					ranks = classRanks.substring(index + 1);

					aPCClass = aPC.getClassKeyed(className);

					buffer.append(TAG_CLASSBOUGHT).append(":");
					buffer.append("[");
					buffer.append(TAG_CLASS).append(":");
					buffer.append(EntityEncoder.encode(className));
					buffer.append("|");
					buffer.append(TAG_RANKS).append(":");
					buffer.append(ranks);
					buffer.append("|");
					buffer.append(TAG_COST).append(":");
					buffer.append(aSkill.costForPCClass(aPCClass).toString());
					buffer.append("|");
					buffer.append(TAG_CLASSSKILL).append(":");
					buffer.append((aSkill.isClassSkill(aPCClass)) ? "Y" : "N");
					buffer.append("]");
				}
				for (int i = 0; i < aSkill.getAssociatedCount(); i++)
				{
					buffer.append("|");
					buffer.append(TAG_ASSOCIATEDDATA).append(":");
					buffer.append(EntityEncoder.encode(aSkill.getAssociated(i)));
				}
				buffer.append("\n");
			}
		}
	}

	/*
	 * ###############################################################
         * Character Language methods
	 * ###############################################################
	 */

	private void appendLanguageLine(StringBuffer buffer)
	{
		String del = "";
		for (Iterator it = aPC.getLanguagesList().iterator(); it.hasNext();)
		{
			buffer.append(del);
			buffer.append(TAG_LANGUAGE).append(":");
			buffer.append(EntityEncoder.encode(it.next().toString()));
			del = "|";
		}
		buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Feats methods
	 * ###############################################################
	 */

	private void appendFeatLines(StringBuffer buffer)
	{
		Feat aFeat;
		for (Iterator it = aPC.getFeatList().iterator(); it.hasNext();)
		{
			aFeat = (Feat)it.next();
			if (aFeat.isMultiples())
			{
				for (int it2 = 0; it2 < aFeat.getAssociatedCount(); it2++)
				{
					buffer.append(TAG_FEAT).append(":");
					buffer.append(EntityEncoder.encode(aFeat.getKeyName()));
					buffer.append("|");
					buffer.append(TAG_APPLIEDTO).append(":");
					buffer.append(EntityEncoder.encode(aFeat.getAssociated(it2)));
					buffer.append("|");
					buffer.append(TAG_TYPE).append(":");
					buffer.append(EntityEncoder.encode(aFeat.getType()));
					for (int it3 = 0; it3 < aFeat.getSaveCount(); it3++)
					{
						buffer.append("|");
						buffer.append(TAG_SAVE).append(":");
						buffer.append(EntityEncoder.encode(aFeat.getSave(it3)));
					}
					buffer.append("|");
					buffer.append(TAG_DESC).append(":");
					buffer.append(EntityEncoder.encode(aFeat.getDescription()));
//  					buffer.append("|");
//  					buffer.append(TAG_DATA).append(":");
//  					buffer.append("(");
//  					// TODO
//  					buffer.append(")");
					buffer.append("\n");
				}
			}
			else
			{
				buffer.append(TAG_FEAT).append(":");
				buffer.append(EntityEncoder.encode(aFeat.getKeyName()));
				buffer.append("|");
				buffer.append(TAG_TYPE).append(":");
				buffer.append(EntityEncoder.encode(aFeat.getType()));
				for (int it3 = 0; it3 < aFeat.getSaveCount(); it3++)
				{
					buffer.append("|");
					buffer.append(TAG_SAVE).append(":");
					buffer.append(EntityEncoder.encode(aFeat.getSave(it3)));
				}
				buffer.append("|");
				buffer.append(TAG_DESC).append(":");
				buffer.append(EntityEncoder.encode(aFeat.getDescription()));
//  				buffer.append("|");
//  				buffer.append(TAG_DATA).append(":");
//  				buffer.append("(");
//  				// TODO
//  				buffer.append(")");
				buffer.append("\n");
			}
		}

                buffer.append(TAG_FEATPOOL).append(":");
                buffer.append(Integer.toString(aPC.getFeats()));
                buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Weapon proficiencies methods
	 * ###############################################################
	 */

	private void appendWeaponProficiencyLines(StringBuffer buffer)
	{
                int size = aPC.getWeaponProfList().size();
                if (size > 0)
                {
                        /*
                         * since aPC.getWeaponProfList() returns a TreeSet,
                         * we have to put them into an array first.
                         * we do not use TreeSet's toArray()-method since it
                         * makes no guarantees on element order.
                         *
                         * author: Thomas Behr 08-09-02
                         */
                        final String[] weaponProficiencies = new String[size];

                        int j = 0;
                        for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext(); j++)
                        {
                                weaponProficiencies[j] = it.next().toString();
                        }
                        
                        // as per Mynex's request do not write more than 10 weapons per line
                        final int step = 10;
                        final int times = size / step + ((size % step > 0) ? 1 : 0);
                        for (int k = 0; k < times; k++)
                        {
                                buffer.append(TAG_WEAPONPROF).append(":");
                                buffer.append("[");

                                String del = "";
                                int start = k * step;
                                int stop  = Math.min(size, k * step + 10);                                
                                for (int i = k * step; i < stop; i++)
                                {
                                        buffer.append(del);
                                        buffer.append(TAG_WEAPON).append(":");
                                        buffer.append(EntityEncoder.encode(weaponProficiencies[i]));
                                        del = "|";
                                }

                                buffer.append("]");
                                buffer.append("\n");
                        }
                }
                
//                  buffer.append(TAG_WEAPONPROF).append(":");
//                  buffer.append("[");
//                  String del = "";
//  		for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext();)
//  		{
//                          buffer.append(del);
//                          buffer.append(TAG_WEAPON).append(":");
//  			buffer.append(it.next().toString());
//                          del = "|";
//  		}
//                  buffer.append("]");
//                  buffer.append("\n");
                
		//
		// Save any selected racial bonus weapons
		//
                appendWeaponProficiencyLines(buffer, aPC.getRace());

		//
		// Save any selected class bonus weapons
		//
		for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
		{
                        appendWeaponProficiencyLines(buffer, (PObject)e.next());
                }
                
		//
		// Save any selected domain bonus weapons
		//
                CharacterDomain aCharacterDomain;
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
                        appendWeaponProficiencyLines(buffer, ((CharacterDomain)e.next()).getDomain());
                }       
                
		//
		// Save any selected feat bonus weapons
		//
		for (Iterator e = aPC.getFeatList().iterator(); e.hasNext();)
		{
                        appendWeaponProficiencyLines(buffer, (PObject)e.next());
                }
        }

        private void appendWeaponProficiencyLines(StringBuffer buffer, PObject source)
        {
                int size = (source != null) ? source.getSelectedWeaponProfBonusCount() : 0;

                if (size > 0)
                {
                        // as per Mynex's request do not write more than 10 weapons per line
                        final int step = 10;
                        final int times = size / step + 1;
                        for (int k = 0; k < times; k++)
                        {        
                                buffer.append(TAG_WEAPONPROF).append(":");
                                buffer.append("[");

                                String del = "";
                                int start = k * step;
                                int stop  = Math.min(size, k * step + 10);                                
                                for (int i = k * step; i < stop; i++)
                                {
                                        buffer.append(del);
                                        buffer.append(TAG_WEAPON).append(":");
                                        buffer.append(EntityEncoder.encode(source.getSelectedWeaponProfBonus(i)));
                                        del = "|";
                                }

                                buffer.append("]");
                                buffer.append("|");
                                appendSourceInTaggedFormat(buffer, source);
                                buffer.append("\n");
                        }
                }
        }

	/*
	 * ###############################################################
         * Character Equipment methods
	 * ###############################################################
	 */

	private void appendEquipmentLines(StringBuffer buffer)
	{
		String del = "";
		Equipment aEquip;
		Equipment aEquip2;
		for (Iterator it = aPC.getEquipmentList().iterator(); it.hasNext();)
		{
                        
			aEquip = (Equipment)it.next();

			buffer.append(TAG_EQUIPNAME).append(":");
			buffer.append(EntityEncoder.encode(aEquip.getKeyName()));
			buffer.append("|");
			buffer.append(TAG_OUTPUTORDER).append(":");
			buffer.append(Integer.toString(aEquip.getOutputIndex()));
			buffer.append("|");
			buffer.append(TAG_COST).append(":");
			buffer.append(aEquip.getCost().toString());
			buffer.append("|");
			buffer.append(TAG_WT).append(":");
			buffer.append(aEquip.getWeight().toString());
			buffer.append("|");
			buffer.append(TAG_QUANTITY).append(":");
			buffer.append(aEquip.qty().toString());
			buffer.append("|");
			buffer.append(TAG_CARRIED).append(":");
			buffer.append(aEquip.getCarried().toString());
			buffer.append("|");
			buffer.append(TAG_EQUIPPED).append(":");
			buffer.append((aEquip.isEquipped()) ? "Y" : "N");
			buffer.append("|");
			buffer.append(TAG_LOCATION).append(":");
			buffer.append(EntityEncoder.encode(aEquip.getLocation()));

			if (aEquip.isContainer())
			{
				/*
				 * this needs to be updated as soon as it is possible
				 * to store multiple instances of the same item at
				 * different locations.
				 *
				 * author: Thomas Behr 27-03-02
				 */
                                buffer.append("|");
				buffer.append(TAG_CONTAINS).append(":");
//  				buffer.append("(");
				buffer.append("[");
				del = "";
				for (Iterator it2 = aEquip.getContents().iterator(); it2.hasNext();)
				{
					aEquip2 = (Equipment)it2.next();
//  					buffer.append(del).append(aEquip2.getKeyName());
//  					del = ", ";
                                        buffer.append(del);
                                        buffer.append(TAG_EQUIPNAME).append(":");
                                        buffer.append(EntityEncoder.encode(aEquip2.getKeyName()));
					del = "|";
                                        
				}
//                                  buffer.append(aEquip.getContainerContentsString());
//  				buffer.append(")");
				buffer.append("]");
				buffer.append("|");
				buffer.append(TAG_TOTALWT).append(":");
				buffer.append(aEquip.getContainedWeight(true).toString());
			}

                        final String customization = aEquip.formatSaveLine("|", ":").trim();
                        if ((customization.length() > 0) && (customization.indexOf("|") > -1))
                        {
				buffer.append("|");
                                buffer.append(TAG_CUSTOMIZATION).append(":");
				buffer.append("[");
                                buffer.append(TAG_BASEITEM).append(":");
                                buffer.append(customization);
				buffer.append("]");
                        }
                        
//                          buffer.append("|");
//  			buffer.append(TAG_DATA).append(":");
//  			buffer.append("(");
//  			// TODO
//  			buffer.append(")");
			buffer.append("\n");
		}
	}

	private void appendEquipmentSetLines(StringBuffer buffer)
	{
		final ArrayList eqSetList = aPC.getEquipSet();
		Collections.sort(eqSetList);

                EquipSet aEquipSet;
		for (Iterator it = eqSetList.iterator(); it.hasNext();)
		{
			aEquipSet = (EquipSet)it.next();

                        buffer.append(TAG_EQUIPSET).append(":");
                        buffer.append(EntityEncoder.encode(aEquipSet.getName()));
                        buffer.append("|");
                        buffer.append(TAG_ID).append(":");
                        buffer.append(aEquipSet.getIdPath());

			if (aEquipSet.getValue().length() > 0)
			{
				buffer.append("|");
				buffer.append(TAG_VALUE).append(":");
				buffer.append(EntityEncoder.encode(aEquipSet.getValue()));
				buffer.append("|");
				buffer.append(TAG_QUANTITY).append(":");
				buffer.append(aEquipSet.getQty());
			}

                        buffer.append("\n");
		}

        }
        
	/*
	 * ###############################################################
         * Character Deity/Domain methods
	 * ###############################################################
	 */

	private void appendDeityLine(StringBuffer buffer)
	{
		if (aPC.getDeity() != null)
		{
			Deity aDeity = aPC.getDeity();

			buffer.append(TAG_DEITY).append(":");
			buffer.append(EntityEncoder.encode(aDeity.getKeyName()));

                        /*
                         * currently unused information
                         *
                         * author: Thomas Behr 09-09-02
                         */
			buffer.append("|");
			buffer.append(TAG_DEITYDOMAINS).append(":");
			buffer.append("[");
			String del = "";
			for (Iterator it = aDeity.getDomainList().iterator(); it.hasNext();)
			{
				buffer.append(del);
                                buffer.append(TAG_DOMAIN).append(":");
                                buffer.append(EntityEncoder.encode((String)it.next()));
				del = "|";
			}
			buffer.append("]");

			buffer.append("|");
			buffer.append(TAG_ALIGNALLOW).append(":");
			buffer.append(aDeity.getFollowerAlignments());
			buffer.append("|");
			buffer.append(TAG_DESC).append(":");
			buffer.append(EntityEncoder.encode(aDeity.getDescription()));
			buffer.append("|");
			buffer.append(TAG_HOLYITEM).append(":");
			buffer.append(EntityEncoder.encode(aDeity.getHolyItem()));
//  			buffer.append("|");
//  			buffer.append(TAG_SYMBOL).append(":");
//  			// TODO
//  			buffer.append(Constants.s_NONE);

			buffer.append("|");
			buffer.append(TAG_DEITYFAVWEAP).append(":");
                        buffer.append("[");
                        final StringTokenizer tokens =
                                new StringTokenizer(aDeity.getFavoredWeapon(), "|");
                        del = "";
                        while (tokens.hasMoreTokens()) {
                                buffer.append(del);
                                buffer.append(TAG_WEAPON).append(":");
                                buffer.append(EntityEncoder.encode(tokens.nextToken()));
                                del = "|";
                        }
                        buffer.append("]");

			buffer.append("|");
			buffer.append(TAG_DEITYALIGN).append(":");
			buffer.append(aDeity.getDeityAlignment());
			buffer.append("\n");
		}
	}

	private void appendDomainLines(StringBuffer buffer)
	{
		String del = "";
		String levelString = "";
		StringTokenizer tokens;

		Domain aDomain;
		CharacterDomain aCharDomain;

//  		Spell aSpell;

//  		List domainSpells = new ArrayList();
		for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();)
		{
			aCharDomain = (CharacterDomain)it.next();
			aDomain = aCharDomain.getDomain();

			// TODO :
//  			// improve here - performance and concept!!!!
//  			domainSpells.clear();
//  			for (Iterator it2 = Globals.getSpellMap().values().iterator(); it2.hasNext();)
//  			{
//  				aSpell = (Spell)it2.next();

//  //                                levelString = aSpell.levelForClass(aDomain.getName());
//  				if ((levelString.length() > 0) &&
//  				  (levelString.indexOf("-1") < 0))
//  				{
//  					tokens = new StringTokenizer(levelString, ",");
//  					while (tokens.hasMoreTokens())
//  					{
//  						if (tokens.nextToken().equals(aDomain.getName()))
//  						{
//  							break;
//  						}
//  					}
//  					domainSpells.add(((tokens.hasMoreTokens()) ? tokens.nextToken() + " " : "") +
//  					  aSpell.getName());
//  				}
//  			}

			buffer.append(TAG_DOMAIN).append(":");
			buffer.append(EntityEncoder.encode(aDomain.getKeyName()));
                        for (int i = 0; i < aDomain.getAssociatedCount(); i++)
                        {
                                buffer.append("|");
                                buffer.append(TAG_ASSOCIATEDDATA).append(":");
                                buffer.append(EntityEncoder.encode(aDomain.getAssociated(i)));
                        }
			buffer.append("|");
			buffer.append(TAG_DOMAINGRANTS).append(":");
			buffer.append(EntityEncoder.encode(aDomain.getGrantedPower()));
                        buffer.append("|");
                        appendSourceInTaggedFormat(buffer, aCharDomain.getDomainSource());
                        
//                          buffer.append("|");
//                          buffer.append(TAG_DOMAINFEATS).append(":");
//                          buffer.append(aDomain.getFeatList());
//                          buffer.append("|");
//                          buffer.append(TAG_DOMAINSKILLS).append(":");
//                          buffer.append(aDomain.getSkillList());
//                          buffer.append("|");
//                          buffer.append(TAG_DOMAINSPECIALS).append(":");
//                          buffer.append(aDomain.getSpecialAbility());
//                          buffer.append("|");
//                          buffer.append(TAG_DOMAINSPELLS).append(":");
//                          buffer.append(aDomain.getSpellList());
			buffer.append("\n");

                        /*
                         * not working yet anyways
                         *
                         * author: Thomas Behr 09-09-02
                         */ 
//  			buffer.append(TAG_DOMAINSPELLS).append(":");
//  			buffer.append(EntityEncoder.encode(aDomain.getKeyName()));
//  			buffer.append("|");
//  			buffer.append(TAG_SPELLLIST).append(":");
//                          buffer.append("[");
//  			del = "";
//  			Collections.sort(domainSpells);
//  			for (Iterator it2 = domainSpells.iterator(); it2.hasNext();)
//  			{
//  				buffer.append(del);
//  				buffer.append(TAG_SPELL).append(":");
//                                  buffer.append(EntityEncoder.encode((String)it2.next()));
//  				del = "|";
//  			}
//                          buffer.append("]");
//  			buffer.append("\n");
		}
	}

	/*
	 * ###############################################################
         * Character Spells Information methods
	 * ###############################################################
	 */

	/*
	 * #Character Spells Information
	 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
	 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
         *
         * completely changed due to new Spell API
	 */
	private void appendSpellLines(StringBuffer buffer)
	{
		PCClass aClass;
                CharacterSpell aCharacterSpell;
                SpellInfo aSpellInfo;
                String spellKey;
                List metaFeats;
                String del;
                
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass)it.next();

                        for (Iterator it2 = aClass.getCharacterSpell(null, "", -1).iterator(); it2.hasNext();)
                        {
                                aCharacterSpell = (CharacterSpell)it2.next();

                                for (Iterator it3 = aCharacterSpell.getInfoList().iterator(); it3.hasNext();)
                                {
                                        aSpellInfo = (SpellInfo)it3.next();
                                        spellKey = aCharacterSpell.getOwner().getSpellKey();

                                        if (aSpellInfo.getBook().equals(Globals.getDefaultSpellBook()) &&
                                            aClass.isAutoKnownSpell(aCharacterSpell.getSpell().getKeyName(),
                                                                    aCharacterSpell.getSpell().levelForKey(spellKey)) &&
                                            aPC.getAutoSpells()) {
                                                continue;
                                        }

                                        buffer.append(TAG_SPELLNAME).append(":");
                                        buffer.append(EntityEncoder.encode(aCharacterSpell.getSpell().getKeyName()));
                                        buffer.append("|");
                                        buffer.append(TAG_TIMES).append(":");
                                        buffer.append(Integer.toString(aSpellInfo.getTimes()));
                                        buffer.append("|");
                                        buffer.append(TAG_CLASS).append(":");
                                        buffer.append(EntityEncoder.encode(aClass.getName()));
                                        buffer.append("|");
                                        buffer.append(TAG_SPELLBOOK).append(":");
                                        buffer.append(EntityEncoder.encode(aSpellInfo.getBook()));
                                        buffer.append("|");
                                        buffer.append(TAG_SPELLLEVEL).append(":");
                                        buffer.append(Integer.toString(aSpellInfo.getActualLevel()));

                                        metaFeats = aSpellInfo.getFeatList();
                                        if ((metaFeats != null) && (!metaFeats.isEmpty())) {
                                                buffer.append("|");
                                                buffer.append(TAG_FEATLIST).append(":");
                                                buffer.append("[");
                                                del = "";
                                                for (Iterator it4 = metaFeats.iterator(); it4.hasNext();) {
                                                        buffer.append(del);
                                                        buffer.append(TAG_FEAT).append(":");
                                                        buffer.append(EntityEncoder.encode(((Feat)it4.next()).getName()));
                                                        del = "|";
                                                }
                                                buffer.append("]");
                                        }

                                        buffer.append("|");
                                        appendSourceInTaggedFormat(buffer, spellKey);
                                        buffer.append("\n");
                                }
                        }
                }
                
//                                                  buffer.append((isCaster) ? TAG_SPELLNAME : TAG_POWERNAME).append(":");
//                                                  buffer.append(aSpell.getKeyName());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_SCHOOL).append(":");
//                                                  buffer.append(aSpell.getSchool());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_SUBSCHOOL).append(":");
//                                                  buffer.append(aSpell.getSubschool());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_COMP).append(":");
//                                                  buffer.append(aSpell.getComponentList());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_CT).append(":");
//                                                  buffer.append(aSpell.getCastingTime());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_DURATION).append(":");
//                                                  buffer.append(aSpell.getDuration());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_DESCRIPTOR).append(":");
//                                                  buffer.append(aSpell.getDescriptorList());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_RANGE).append(":");
//                                                  buffer.append(aSpell.getRange());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_EFFECT).append(":");
//                                                  buffer.append(aSpell.getEffect());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_EFFECTTYPE).append(":");
//                                                  buffer.append(aSpell.getEffectType());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_SAVE).append(":");
//                                                  buffer.append(aSpell.getSaveInfo());
//                                                  buffer.append("|");
//                                                  buffer.append(TAG_SR).append(":");
//                                                  buffer.append(aSpell.getSR());
//                                                  buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Description/Bio/History methods
	 * ###############################################################
	 */

	private void appendCharacterBioLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERBIO).append(":");
		buffer.append(EntityEncoder.encode(aPC.getBio()));
		buffer.append("\n");
	}

	private void appendCharacterDescLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERDESC).append(":");
		buffer.append(EntityEncoder.encode(aPC.getDescription()));
		buffer.append("\n");
	}

	/*
	 * ###############################################################
         * Character Follower methods
	 * ###############################################################
	 */

	private void appendFollowerLines(StringBuffer buffer)
	{
		final Follower aMaster = aPC.getMaster();
		if (aMaster != null)
		{
			buffer.append(TAG_MASTER).append(":");
                        buffer.append(EntityEncoder.encode(aMaster.getName()));
			buffer.append("|");
			buffer.append(TAG_TYPE).append(":");
			buffer.append(EntityEncoder.encode(aMaster.getType()));
			buffer.append("|");
			buffer.append(TAG_HITDICE).append(":");
			buffer.append(aMaster.getUsedHD());
			buffer.append("|");
			buffer.append(TAG_FILE).append(":");
                        buffer.append(EntityEncoder.encode(aMaster.getFileName()));
			buffer.append("\n");
		}
 
		final ArrayList followers = aPC.getFollowerList();
		if (!followers.isEmpty()) {
                        
                        Follower aFollower;
                        for (Iterator it = followers.iterator(); it.hasNext();)
                        {
                                aFollower = (Follower)it.next();
                                buffer.append(TAG_FOLLOWER).append(":");
                                buffer.append(EntityEncoder.encode(aFollower.getName()));
                                buffer.append("|");
                                buffer.append(TAG_TYPE).append(":");
                                buffer.append(EntityEncoder.encode(aFollower.getType()));
                                buffer.append("|");
                                buffer.append(TAG_HITDICE).append(":");
                                buffer.append(aFollower.getUsedHD());
                                buffer.append("|");
                                buffer.append(TAG_FILE).append(":");
                                buffer.append(EntityEncoder.encode(aFollower.getFileName()));
                                buffer.append("\n");
                        }
                }
	}

	/*
	 * ###############################################################
         * Character Notes Tab methods
	 * ###############################################################
	 */

	private void appendNotesLines(StringBuffer buffer)
	{
		for (Iterator i = aPC.getNotesList().iterator(); i.hasNext();)
		{
			NoteItem ni = (NoteItem)i.next();
			buffer.append(TAG_NOTE).append(":");
			buffer.append(EntityEncoder.encode(ni.getName()));
			buffer.append("|");
			buffer.append(TAG_ID).append(":");
			buffer.append(Integer.toString(ni.getId()));
			buffer.append("|");
			buffer.append(TAG_PARENTID).append(":");
			buffer.append(Integer.toString(ni.getParentId()));
			buffer.append("|");
			buffer.append(TAG_VALUE).append(":");
			buffer.append(EntityEncoder.encode(ni.getValue()));
			buffer.append("\n");
		}
	}

	/*
	 * ###############################################################
         * Miscellaneous methods
	 * ###############################################################
	 */

        /*
         * currently source is either empty or
         * PCCLASS|classname|classlevel (means it's a chosen special ability)
         * PCCLASS=classname|classlevel (means it's a defined special ability)
         * DEITY=deityname|totallevels
         */
        private void appendSourceInTaggedFormat(StringBuffer buffer, String source) 
        {
                final StringTokenizer tokens = new StringTokenizer(source, "|=");
                buffer.append(TAG_SOURCE).append(":");
                buffer.append("[");
                buffer.append(TAG_TYPE).append(":");
                buffer.append(tokens.nextToken());
                buffer.append("|");
                buffer.append(TAG_NAME).append(":");
                buffer.append(tokens.nextToken());
                if (tokens.hasMoreTokens())
                {
                        buffer.append("|");
                        buffer.append(TAG_LEVEL).append(":");
                        buffer.append(tokens.nextToken());
                }
                if (source.indexOf("=") > -1)
                {
                        buffer.append("|");
                        buffer.append(TAG_DEFINED).append(":");
                        buffer.append("Y");
                }
                buffer.append("]");
        }
        
        /*
         * currently source is either empty or
         * PCCLASS|classname|classlevel (means it's a chosen special ability)
         * PCCLASS=classname|classlevel (means it's a defined special ability)
         * DEITY=deityname|totallevels
         */
        private void appendSourceInTaggedFormat(StringBuffer buffer, PObject source) 
        {
                buffer.append(TAG_SOURCE).append(":");
                buffer.append("[");
                buffer.append(TAG_TYPE).append(":");

                // I love reflection :-)
                final Class srcClass = source.getClass();
                final String pckName = srcClass.getPackage().getName();
                final String srcName = srcClass.getName().substring(pckName.length() + 1);

                buffer.append(srcName.toUpperCase());
                buffer.append("|");
                buffer.append(TAG_NAME).append(":");
                buffer.append(source.getName());
                buffer.append("]");
        }        

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param buffer
	 */
	private void appendNewline(StringBuffer buffer)
	{
		buffer.append("\n");
	}

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param comment
	 * @param buffer
	 */
	private void appendComment(String comment, StringBuffer buffer)
	{
		buffer.append(createComment(comment));
	}

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param s   the String which will be converted into a comment;
	 *            i.e. '#','\r' will be removed,
	 *                 '\t','\f' will be replaced with ' ',
	 *            and each line will start with "# "
	 * @return the newly created comment
	 */
	private String createComment(String s)
	{
		StringBuffer buffer;
		StringTokenizer tokens;

		String work = new String(s + "\n");
		work = work.replace('\t', ' ');
		work = work.replace('\f', ' ');

		buffer = new StringBuffer();
		tokens = new StringTokenizer(work, "#");
		while (tokens.hasMoreTokens())
		{
			buffer.append(tokens.nextToken());
		}
		work = buffer.toString();

		buffer = new StringBuffer();
		tokens = new StringTokenizer(work, "\r\n");
		while (tokens.hasMoreTokens())
		{
			buffer.append("# ").append(tokens.nextToken()).append("\n");
		}

		return buffer.toString();
	}
}





