/*
 * PCGVer1Creator.java
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
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.Spell;

/**
 * <code>PCGVer1Creator</code><br>
 * @author Thomas Behr 19-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer1Creator implements IOConstants
{
        private PlayerCharacter aPC;

        /**
         * Constructor
         */
        public PCGVer1Creator(PlayerCharacter aPC) 
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
		StringBuffer buffer = new StringBuffer();

                /*
                 * #System Information
                 * CAMPAIGNS:>:-delimited list<
                 * VERSION:x.x.x
                 * ROLLMETHOD:xxx
                 * PURCHASEPOINTS:Y or N|TYPE:>living City, Living greyhawk, etc<
                 * UNLIMITEDPOOLCHECKED:Y or N
                 * POOLPOINTS:>numeric value 0-?<
                 * GAME MODE:DnD
                 */
                appendComment("System Information", buffer);
		appendCampaignLine(buffer);
		appendVersionLine(buffer);
		appendRollMethodLine(buffer);
		appendPurchasePointsLine(buffer);
		appendUnlimitedPoolCheckedLine(buffer);
		appendPoolPointsLine(buffer);
		appendGameModeLine(buffer);

                /*
                 * #Character Bio
                 * CHARACTERNAME:Code Monkey
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
                 * REGION:text
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
		appendRegionLine(buffer);
		appendPersonalityTrait1Line(buffer);
		appendPersonalityTrait2Line(buffer);
		appendSpeechPatternLine(buffer);
		appendPhobiasLine(buffer);
		appendInterestsLine(buffer);
		appendCatchPhraseLine(buffer);

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
                 */
                appendNewline(buffer);
                appendComment("Character Class(es)", buffer);
		appendClassLines(buffer);

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
                 * hmmm, better have
                 * SKILL:Alchemy|CLASS:N|COST:2|RANK:7
                 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK7
                 */
                appendNewline(buffer);
                appendComment("Character Skills", buffer);
		appendSkillLines(buffer);

                /*
                 * Anything that is already Pipe Delimited should be in parenthesis to avoid confusion on PCGen's part
                 *
                 * #Character Feats
                 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
                 */
                appendNewline(buffer);
                appendComment("Character Feats", buffer);
		appendFeatLines(buffer);

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

                /*
                 * #Character Deity/Domain
                 * DEITY:Yondalla|DEITYDOMAINS:Good,Law,Protection|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
                 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
                 * DOMAINSPELLS:GOOD(>list of level by level spells)
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
                 * #Character Notes Tab - not implemented yet
                 */
                appendNewline(buffer);
                appendComment("Character Notes Tab", buffer);
                
                return buffer.toString();
        }
        

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */

	private void appendCampaignLine(StringBuffer buffer)
	{
		buffer.append(TAG_CAMPAIGNS).append(":");

		Campaign aCamp = null;
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign)it.next();
			if (aCamp.isLoaded())
			{
				buffer.append(aCamp.getName()).append("|");
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
			d_properties = ResourceBundle.getBundle("pcgen/gui/PCGenProp");
			buffer.append(d_properties.getString("VersionNumber"));
		}
		catch (java.util.MissingResourceException mre)
		{
			d_properties = null;
		}

                buffer.append("\n");
	}

        private void appendRollMethodLine(StringBuffer buffer) 
        {
                buffer.append(TAG_ROLLMETHOD).append(":");
                buffer.append(Integer.toString(Globals.getRollMethod()));
                buffer.append("|");
                buffer.append(Globals.getRollMethodExpression(Globals.getRollMethod()));
                buffer.append("\n");
        }
        
        private void appendPurchasePointsLine(StringBuffer buffer) 
        {
                buffer.append(TAG_PURCHASEPOINTS).append(":");
                buffer.append((Globals.isPurchaseStatMode()) ? "Y" : "N");
                buffer.append("|");
                buffer.append(TAG_TYPE).append(":");
                // TODO
                buffer.append("\n");
        }
        
        private void appendUnlimitedPoolCheckedLine(StringBuffer buffer) 
        {
                buffer.append(TAG_UNLIMITEDPOOLCHECKED).append(":");
                buffer.append((Globals.isStatPoolUnlimited()) ? "Y" : "N");
                buffer.append("\n");
        }
        
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

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */

        private void appendCharacterNameLine(StringBuffer buffer)
        {
                buffer.append(TAG_CHARACTERNAME).append(":");
                buffer.append(aPC.getName());
                buffer.append("\n");
        }

        private void appendPlayerNameLine(StringBuffer buffer)
        {
                buffer.append(TAG_PLAYERNAME).append(":");
                buffer.append(aPC.getPlayersName());
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
                buffer.append(aPC.getGender());
                buffer.append("\n");
        }

        private void appendHandedLine(StringBuffer buffer)
        {
                buffer.append(TAG_HANDED).append(":");
                buffer.append(aPC.getHanded());
                buffer.append("\n");
        }

        private void appendSkinColorLine(StringBuffer buffer)
        {
                buffer.append(TAG_SKINCOLOR).append(":");
                buffer.append(aPC.getSkinColor());
                buffer.append("\n");
        }

        private void appendEyeColorLine(StringBuffer buffer)
        {
                buffer.append(TAG_EYECOLOR).append(":");
                buffer.append(aPC.getEyeColor());
                buffer.append("\n");
        }

        private void appendHairColorLine(StringBuffer buffer)
        {
                buffer.append(TAG_HAIRCOLOR).append(":");
                buffer.append(aPC.getHairColor());
                buffer.append("\n");
        }

        private void appendHairStyleLine(StringBuffer buffer)
        {
                buffer.append(TAG_HAIRSTYLE).append(":");
                buffer.append(encodeChars(aPC.getHairStyle()));
                buffer.append("\n");
        }

        private void appendLocationLine(StringBuffer buffer)
        {
                buffer.append(TAG_LOCATION).append(":");
                buffer.append(aPC.getLocation());
                buffer.append("\n");
        }

        private void appendRegionLine(StringBuffer buffer)
        {
                buffer.append(TAG_REGION).append(":");
                buffer.append(aPC.getRegion());
                if (aPC.getSubRegion().length() > 0) {
                        buffer.append(" (").append(aPC.getSubRegion()).append(")");
                }
                buffer.append("\n");
        }

        private void appendPersonalityTrait1Line(StringBuffer buffer)
        {
                buffer.append(TAG_PERSONALITYTRAIT1).append(":");
                buffer.append(encodeChars(aPC.getTrait1()));
                buffer.append("\n");
        }

        private void appendPersonalityTrait2Line(StringBuffer buffer)
        {
                buffer.append(TAG_PERSONALITYTRAIT2).append(":");
                buffer.append(encodeChars(aPC.getTrait2()));
                buffer.append("\n");
        }

        private void appendSpeechPatternLine(StringBuffer buffer)
        {
                buffer.append(TAG_SPEECHPATTERN).append(":");
                buffer.append(encodeChars(aPC.getSpeechTendency()));
                buffer.append("\n");
        }

        private void appendPhobiasLine(StringBuffer buffer)
        {
                buffer.append(TAG_PHOBIAS).append(":");
                buffer.append(encodeChars(aPC.getPhobias()));
                buffer.append("\n");
        }

        private void appendInterestsLine(StringBuffer buffer)
        {
                buffer.append(TAG_INTERESTS).append(":");
                buffer.append(encodeChars(aPC.getInterests()));
                buffer.append("\n");
        }

        private void appendCatchPhraseLine(StringBuffer buffer)
        {
                buffer.append(TAG_CATCHPHRASE).append(":");
                buffer.append(encodeChars(aPC.getCatchPhrase()));
                buffer.append("\n");
        }

	/*
	 * ###############################################################
         * Character Attributes methods
	 * ###############################################################
	 */

        private void appendStatLines(StringBuffer buffer)
        {
                for (int i = 0; i < aPC.getStats().length; i++) {

                        buffer.append(TAG_STAT).append(":");
                        buffer.append(Globals.s_ATTRIBSHORT[i]);
                        buffer.append("=");
                        buffer.append(Integer.toString(aPC.getStat(i)));
                        buffer.append("\n");
                }
        }
        
        private void appendAlignmentLine(StringBuffer buffer)
        {
                buffer.append(TAG_ALIGNMENT).append(":");
                buffer.append(Globals.s_ALIGNSHORT[aPC.getAlignment()]);
                buffer.append("\n");
        }

        private void appendRaceLine(StringBuffer buffer)
        {
                buffer.append(TAG_RACE).append(":");
                buffer.append(aPC.getRace().getKeyName());
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
                PCClass aClass;
                for (Iterator it = aPC.getClassList().iterator(); it.hasNext();) {
                        aClass = (PCClass)it.next();

                        buffer.append(TAG_CLASS).append(":");
                        buffer.append(aClass.getKeyName());
                        buffer.append("|");
                        buffer.append(TAG_LEVEL).append(":");
                        buffer.append(aClass.getLevel().toString());
                        buffer.append("\n");
                        
                        for (int i = 0; i < aClass.getLevel().intValue(); i++) {
                                buffer.append(TAG_CLASSABILITIESLEVEL).append(":");
                                buffer.append(aClass.getKeyName()).append("=").append(Integer.toString(i+1));
                                buffer.append("(");
                                // TODO
                                buffer.append(")");
                                buffer.append("\n");
                        }
                }
        }
        
	/*
	 * ###############################################################
         * Character Templates methods
	 * ###############################################################
	 */

        private void appendTemplateLines(StringBuffer buffer)
        {
                PCTemplate aTemplate;
                for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();) {
                        aTemplate = (PCTemplate)it.next();
                        buffer.append(TAG_TEMPLATESAPPLIED).append(":");
                        buffer.append(aTemplate.getKeyName());
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
                String del;
                Skill aSkill;
                for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();) {
                        aSkill = (Skill)it.next();
                        
                        buffer.append(TAG_SKILL).append(":");
                        buffer.append(aSkill.getKeyName());
                        buffer.append("|");
                        buffer.append(TAG_CLASS).append(":");
                        buffer.append((aSkill.isClassSkill(aPC.getClassList())) ? "Y" : "N");
                        buffer.append("|");
                        buffer.append(TAG_COST).append(":");
                        buffer.append(aSkill.costForPCClassList(aPC.getClassList()).toString());
                        buffer.append("|");
                        buffer.append(TAG_SYNERGY).append(":");
                        del = "";
                        for (Iterator it2 = aSkill.synergyList().iterator(); it2.hasNext();) {
                                buffer.append(del).append((String)it2.next());
                                del = ",";
                        }
                        buffer.append("|");
                        buffer.append(TAG_RANK).append(":");
                        buffer.append(aSkill.getRank().toString());
                        buffer.append("\n");
                }

                // TODO
                // have to add CLASSBOUGHT tag
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
                        if (aFeat.isMultiples()) {
                                for (Iterator it2 = aFeat.getAssociatedList().iterator(); it2.hasNext();) {
                                        buffer.append(TAG_FEAT).append(":");
                                        buffer.append(aFeat.getKeyName());
                                        buffer.append(" (");
                                        buffer.append((String)it2.next());
                                        buffer.append(")");
                                        buffer.append("|");
                                        buffer.append(TAG_TYPE).append(":");
                                        buffer.append(aFeat.getType());
                                        buffer.append("|");
                                        buffer.append(TAG_DESC).append(":");
                                        buffer.append(aFeat.getDescription());
                                        buffer.append("|");
                                        buffer.append(TAG_DATA).append(":");
                                        buffer.append("(");
                                        // TODO
                                        buffer.append(")");
                                        buffer.append("\n");
                                }
                        } else {
                                buffer.append(TAG_FEAT).append(":");
                                buffer.append(aFeat.getKeyName());
                                buffer.append("|");
                                buffer.append(TAG_TYPE).append(":");
                                buffer.append(aFeat.getType());
                                buffer.append("|");
                                buffer.append(TAG_DESC).append(":");
                                buffer.append(aFeat.getDescription());
                                buffer.append("|");
                                buffer.append(TAG_DATA).append(":");
                                buffer.append("(");
                                // TODO
                                buffer.append(")");
                                buffer.append("\n");
                        }
                }
                
                // TODO
                // what is the saveList for???
        }
        
	/*
	 * ###############################################################
         * Character Equipment methods
	 * ###############################################################
	 */

        private void appendEquipmentLines(StringBuffer buffer)
        {
                String del;
		Equipment aEquip;
                Equipment aEquip2;
		for (Iterator it = aPC.getEquipmentList().values().iterator(); it.hasNext();)
		{
                        aEquip = (Equipment)it.next();

                        buffer.append(TAG_EQUIPNAME).append(":");
                        buffer.append(aEquip.getKeyName());
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
                        buffer.append(aEquip.getLocation());
                        buffer.append("|");
                        if (aEquip.isContainer()) {
                                /*
                                 * this needs to be updated as soon as it is possible
                                 * to store multiple instances of the same item at
                                 * different locations.
                                 *
                                 * author: Thomas Behr 27-03-02
                                 */
                                buffer.append(TAG_CONTAINS).append(":");
                                buffer.append("(");
                                del = "";
                                for (Iterator it2 = aEquip.getContents().iterator(); it2.hasNext();) {
                                        aEquip2 = (Equipment)it2.next();
                                        buffer.append(del).append(aEquip2.getKeyName());
                                        del = ", ";
                                }
//                                  buffer.append(aEquip.getContainerContentsString());
                                buffer.append(")");
                                buffer.append("|");
                                buffer.append(TAG_TOTALWT).append(":");
                                buffer.append(aEquip.getContainedWeight(true).toString());
                                buffer.append("|");
                        }
                        buffer.append(TAG_DATA).append(":");
                        // TODO
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
                if (aPC.getDeity() != null) {
                        Deity aDeity = aPC.getDeity();

                        buffer.append(TAG_DEITY).append(":");
                        buffer.append(aDeity.getKeyName());
                        buffer.append("|");
                        buffer.append(TAG_DEITYDOMAINS).append(":");
                        String del = "";
                        for (Iterator it = aDeity.getDomainList().iterator(); it.hasNext();) {
                                buffer.append(del).append((String)it.next());
                                del = ",";
                        }
                        buffer.append("|");
                        buffer.append(TAG_ALIGNALLOW).append(":");
                        buffer.append(aDeity.getAlignments());
                        buffer.append("|");
                        buffer.append(TAG_DESC).append(":");
                        buffer.append(aDeity.getDescription());
                        buffer.append("|");
                        buffer.append(TAG_HOLYITEM).append(":");
                        buffer.append(aDeity.getHolyItem());
                        buffer.append("|");
                        buffer.append(TAG_SYMBOL).append(":");
                        // TODO
                        buffer.append(Globals.s_NONE);
                        buffer.append("|");
                        buffer.append(TAG_DEITYFAVWEAP).append(":");
                        buffer.append(aDeity.getFavoredWeapon());
                        buffer.append("|");
                        buffer.append(TAG_DEITYALIGN).append(":");
                        buffer.append(aDeity.getAlignment());
                        buffer.append("\n");
                }
        }
        
        private void appendDomainLines(StringBuffer buffer)
        {
                String del;
                String levelString;
                StringTokenizer tokens;

                PCClass aClass;

                Domain aDomain;
                CharacterDomain aCharDomain;

                Spell aSpell;
                
                List domainSpells = new ArrayList();
                for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();) {
                        aCharDomain = (CharacterDomain)it.next();
                        aDomain = aCharDomain.getDomain();

                        // TODO :
                        // improve here - performance and concept!!!!
                        domainSpells.clear();
                        for (Iterator it2 = Globals.getSpellMap().values().iterator(); it2.hasNext();) {
                                aSpell = (Spell)it2.next();

                                levelString = aSpell.levelForClass(aDomain.getName());
                                if ((levelString.length() > 0) &&
                                    (levelString.indexOf("-1") < 0)) {
                                        tokens = new StringTokenizer(levelString, ",");
                                        while (tokens.hasMoreTokens()) {
                                                if (tokens.nextToken().equals(aDomain.getName())) {
                                                        break;
                                                }
                                        }
                                        domainSpells.add(((tokens.hasMoreTokens()) ? tokens.nextToken() + " " : "") +
                                                         aSpell.getName());
                                }
                        }

                        buffer.append(TAG_DOMAIN).append(":");
                        buffer.append(aDomain.getKeyName());
                        buffer.append("|");
                        buffer.append(TAG_DOMAINGRANTS).append(":");
                        buffer.append(aDomain.getGrantedPower());
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
                        buffer.append(TAG_DOMAINSPELLS).append(":");
                        buffer.append(aDomain.getKeyName());
                        buffer.append("(");
                        del = "";
                        java.util.Collections.sort(domainSpells);
                        for (Iterator it2 = domainSpells.iterator(); it2.hasNext();) {
                                buffer.append(del).append((String)it2.next());
                                del = ", ";
                        }
                        buffer.append(")");
                        buffer.append("\n");
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
         */
        private void appendSpellLines(StringBuffer buffer)
        {
                PCClass aClass;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
                {
                        aClass = (PCClass)it.next();

                        // determine if this class can cast spells
                        boolean isCaster = false;
                        for (Iterator it2 = aClass.getCastList().iterator(); it2.hasNext();) {
                                if (!"0".equals((String)it2.next())) {
                                        isCaster = true;
                                        break;
                                }
                        }
                        
                        boolean isPsionic = (aClass.getKnownList().size() > 0) && !isCaster;
                        
                        if (isCaster || isPsionic) {
                                buffer.append(TAG_CLASS).append(":");
                                buffer.append(aClass.getKeyName());
                                buffer.append("|");
                                buffer.append(TAG_CANCASTPERDAY).append(":");
                                buffer.append((String)aClass.getCastList().get(aClass.getLevel().intValue()-1));
                                buffer.append("\n");

//                                  if (!aClass.getSpellType().toUpperCase().equals("DIVINE")) {
                                        Spell aSpell;
                                        for (Iterator it2 = aClass.spellList().iterator(); it2.hasNext();) {
                                                aSpell = (Spell)it2.next();
                                                
                                                buffer.append((isCaster) ? TAG_SPELLNAME : TAG_POWERNAME).append(":");
                                                buffer.append(aSpell.getKeyName());
                                                buffer.append("|");
                                                buffer.append(TAG_SCHOOL).append(":");
                                                buffer.append(aSpell.getSchool());
                                                buffer.append("|");
                                                buffer.append(TAG_SUBSCHOOL).append(":");
                                                buffer.append(aSpell.getSubschool());
                                                buffer.append("|");
                                                buffer.append(TAG_COMP).append(":");
                                                buffer.append(aSpell.getComponentList());
                                                buffer.append("|");
                                                buffer.append(TAG_CT).append(":");
                                                buffer.append(aSpell.getCastingTime());
                                                buffer.append("|");
                                                buffer.append(TAG_DURATION).append(":");
                                                buffer.append(aSpell.getDuration());
                                                buffer.append("|");
                                                buffer.append(TAG_DESCRIPTOR).append(":");
                                                buffer.append(aSpell.getDescriptorList());
                                                buffer.append("|");
                                                buffer.append(TAG_RANGE).append(":");
                                                buffer.append(aSpell.getRange());
                                                buffer.append("|");
                                                buffer.append(TAG_EFFECT).append(":");
                                                buffer.append(aSpell.getEffect());
                                                buffer.append("|");
                                                buffer.append(TAG_EFFECTTYPE).append(":");
                                                buffer.append(aSpell.getEffectType());
                                                buffer.append("|");
                                                buffer.append(TAG_SAVE).append(":");
                                                buffer.append(aSpell.getSaveInfo());
                                                buffer.append("|");
                                                buffer.append(TAG_SR).append(":");
                                                buffer.append(aSpell.getSR());
                                                buffer.append("\n");
                                        }
//                                  }
                        }
                }
        }
        
	/*
	 * ###############################################################
         * Character Description/Bio/History methods
	 * ###############################################################
	 */

        private void appendCharacterBioLine(StringBuffer buffer)
        {
                buffer.append(TAG_CHARACTERBIO).append(":");
                buffer.append(encodeChars(aPC.getBio()));
                buffer.append("\n");
        }
        
        private void appendCharacterDescLine(StringBuffer buffer)
        {
                buffer.append(TAG_CHARACTERDESC).append(":");
                buffer.append(encodeChars(aPC.getDescription()));
                buffer.append("\n");
        }
        
	/*
	 * ###############################################################
         * Character Notes Tab methods
	 * ###############################################################
	 */

	/*
	 * ###############################################################
         * Miscellaneous methods
	 * ###############################################################
	 */

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
                while (tokens.hasMoreTokens()) {
                        buffer.append(tokens.nextToken());
                }
                work = buffer.toString();


                buffer = new StringBuffer();
                tokens = new StringTokenizer(work, "\r\n");
                while (tokens.hasMoreTokens()) {
                        buffer.append("# ").append(tokens.nextToken()).append("\n");
                }

                return buffer.toString();
        }

        /**
         * encode characters
         * "\\" -> "\\\\"
         * "\n" -> "\\n"
         * "\r" -> "\\r"
         * "\f" -> "\\f"
         *
         * <br>author: Thomas Behr 19-03-02
         *
         * @param s   the String to encode
         * @return the encoded String
         */
        private String encodeChars(String s) 
        {
                String del;
                StringBuffer buffer;
                StringTokenizer tokens;

                String work = new String(s);
                
                String[] oldStuff = {
                        "\\",
                        "\n",
                        "\r",
                        "\f"
                };
                
                String[] newStuff = {
                        "\\\\",
                        "\\n",
                        "\\r",
                        "\\f"
                };

                for (int i = 0; i < oldStuff.length; i++) {
                        
                        del = "";
                        buffer = new StringBuffer();
                        tokens = new StringTokenizer(work, oldStuff[i]);
                        while (tokens.hasMoreTokens()) {
                                buffer.append(del).append(tokens.nextToken());
                                del = newStuff[i];
                        }
                        work = buffer.toString();
                }

                return work;
        }
}
