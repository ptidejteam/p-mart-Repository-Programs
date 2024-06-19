/*
 * PCGIOHandler.java
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
 * Created on March 11, 2002, 8:30 PM
 */

package pcgen.io;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.Spell;
import pcgen.core.Utility;

/**
 * <code>PCGIOHandler</code><br>
 * Reading and Writing PlayerCharacters in PCGen's own format (PCG).
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision: 1.1 $
 */

public class PCGIOHandler
{
        private PlayerCharacter aPC;
        
        /**
         * Writes the contents of the given PlayerCharacter to a stream
         *
         * <br>author: Thomas Behr 11-03-02
         *
         * @param aPC   the PlayerCharacter to write
         * @param out   the stream to be written to
         */
        public void write( PlayerCharacter aPC, OutputStream out ) 
        {
                this.aPC = aPC;

                String pcgString = createPCGString();
                try {
                        // maybe wrap this in a BufferedWriter for performance???
                        out.write(pcgString.getBytes(),0,pcgString.length());
                        aPC.setDirty(false);
                }
                catch(IOException ioe) {
                        ioe.printStackTrace();
                }
        }


        /**
         * Reads the contents of the given PlayerCharacter from a stream
         *
         * <br>author: Thomas Behr 11-03-02
         * 
         * @param aPC   the PlayerCharacter to store the read data
         * @param in    the stream to be read from
         */
        public void read( PlayerCharacter aPC, InputStream in ) 
        {
                // todo
        }
        
  
        /** 
         * Returns the PCG file format string, i.e. "PCG Format"
         *
         * <br>author: Thomas Behr 11-03-02
         *
         * @return "PCG Format"
         */
        public String getFileFormatString()
        {
                return "PCG Format";
        }
  

        /** 
         * Returns the PCG file name extension, i.e. "pcg"
         *
         * <br>author: Thomas Behr 11-03-02
         *
         * @return "pcg"
         */
        public String getFileNameExtension() 
        {
                return "pcg";
        }


        /*
         * ###############################################################
         * private helper methods
         * ###############################################################
         */

        private String createPCGString() 
        {
                StringBuffer buffer = new StringBuffer();
                
                appendCampaignLine(buffer);
		appendVersionLine(buffer);
		appendNameLine(buffer);
		appendStatsLine(buffer);
		appendClassesLine(buffer);
		appendFeatsLine(buffer);
		appendSkillsLine(buffer);
		appendDeityLine(buffer);
		appendRaceLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassSpellLine((PCClass)it.next(), buffer);
		}
		appendLanguagesLine(buffer);
		appendWeaponProfsLine(buffer);
		appendUnusedPointsLine(buffer);
		appendMiscLine(buffer);
		appendEquipmentLine(buffer);
		appendGoldBioDescriptionLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassesSkillLine((PCClass)it.next(), buffer);
		}
		appendExperienceAndMiscListLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassSpecialtyAndSaveLine((PCClass)it.next(), buffer);
		}
		appendTemplateLine(buffer);

                return buffer.toString();
        }

	private void appendCampaignLine(StringBuffer buffer)
	{
		buffer.append("CAMPAIGNS:");

		Campaign aCamp = null;
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign)it.next();
			if (aCamp.isLoaded())
			{
				buffer.append(aCamp.getName()).append(":");
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
		ResourceBundle d_properties;
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/PCGenProp");
			buffer.append("VERSION:");
			buffer.append(d_properties.getString("VersionNumber"));
			buffer.append("\n");
		}
		catch (java.util.MissingResourceException mre)
		{
			d_properties = null;
		}
	}

	private void appendNameLine(StringBuffer buffer)
	{
		buffer.append(Utility.escapeColons2(aPC.getName())).append(":");
                buffer.append(Utility.escapeColons2(aPC.getPlayersName()));
                buffer.append("\n");

	}

	private void appendStatsLine(StringBuffer buffer)
	{
		if (Globals.s_ATTRIBLONG.length != 6)
		{
			buffer.append("STATS:");
                        buffer.append(Integer.toString(Globals.s_ATTRIBLONG.length)).append(":");
		}
		for (int i = 0; i < Globals.s_ATTRIBLONG.length; i++)
		{
			buffer.append(Integer.toString(aPC.getStat(i))).append(":");
		}
		buffer.append(Integer.toString(aPC.getPoolAmount())).append(":");
                buffer.append(Integer.toString(aPC.getCostPool()));
                buffer.append("\n");

	}

	private void appendClassesLine(StringBuffer buffer)
	{
                PCClass aClass;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass)it.next();
			buffer.append(aClass.getKeyName()).append(":");
                        buffer.append(aClass.getSubClassName()).append(" :");
                        buffer.append(aClass.getProhibitedString()).append(" :");
			buffer.append(aClass.getLevel().toString()).append(":");
			for (int j = 0; j < aClass.getLevel().intValue(); j++)
			{
				buffer.append(aClass.getHitPointList(j).toString()).append(":");
			}
			buffer.append(aClass.skillPool().toString()).append(":");
			buffer.append(aClass.getSpellBaseStat()).append(":");
		}
                buffer.append("\n");

	}

	private void appendClassesSkillLine(PCClass aClass, StringBuffer buffer)
	{
                buffer.append(aClass.getKeyName()).append(":");
		for (Iterator it = aClass.skillList().iterator(); it.hasNext();)
		{
			buffer.append((String)it.next()).append(" :");
		}
                buffer.append("\n");

	}

	private void appendClassSpecialtyAndSaveLine(PCClass aClass, StringBuffer buffer)
	{
                buffer.append(aClass.getKeyName()).append(":");
		for (Iterator it = aClass.getSpecialtyList().iterator(); it.hasNext();) {
			buffer.append("SPECIAL").append((String)it.next()).append(":");
                }
		for (Iterator it = aClass.getSaveList().iterator();  it.hasNext();)
		{
			buffer.append((String)it.next()).append(":");
		}

                buffer.append("\n");
	}

	private void appendClassSpellLine(PCClass aClass, StringBuffer buffer)
	{
		Spell aSpell = null;
                List spellTimesList = null;
		for (Iterator it = aClass.spellList().iterator(); it.hasNext();)
		{
			aSpell = (Spell)it.next();
                        spellTimesList = aSpell.getTimes();
			buffer.append(aSpell.getKeyName());
                        int i = 0;
			for (Iterator it2 = aSpell.getSpellBooks().iterator(); it2.hasNext(); i++) {
				buffer.append("|").append(it2.next().toString());
                                buffer.append("|").append(spellTimesList.get(i).toString());
                        }
			buffer.append(":");
		}
                buffer.append("\n");

	}

	private void appendExperienceAndMiscListLine(StringBuffer buffer)
	{
                buffer.append(aPC.getExperience().toString()).append(":");
		for (int i = 0; i < 3; i++)
		{
			buffer.append(Utility.escapeColons(aPC.getMiscList().get(i).toString())).append(" :");
		}
		buffer.append(aPC.getFPoints()).append(":");
                buffer.append(aPC.getDPoints()).append(":");
                buffer.append("\n");

	}

	private void appendGoldBioDescriptionLine(StringBuffer buffer)
	{
                buffer.append(aPC.getGold().toString()).append(":");
                buffer.append(Utility.escapeColons(aPC.getBio())).append(":");
                buffer.append(Utility.escapeColons(aPC.getDescription())).append(":");
                buffer.append("\n");

	}

	private void appendEquipmentLine(StringBuffer buffer)
	{
                String eqMod1;
                String eqMod2;
                String eqSize;
                String sProp;
                Equipment eq;
		for (Iterator it = aPC.getEquipmentList().values().iterator(); it.hasNext();)
		{
			eq = (Equipment)it.next();
			if (!eq.getHasHeaderParent())
			{

				eqMod1 = eq.getEqModifierString(true);			// key1.key2|assoc1|assoc2.key3.key4
				eqMod2 = eq.getEqModifierString(false);			// key1.key2|assoc1|assoc2.key3.key4
				sProp = eq.getRawSpecialProperties();

				boolean bSameName = eq.getName().equals(eq.getItemNameFromModifiers());

				if (bSameName && eq.getKeyName().equals(eq.getBaseItemName()))
				{
					sProp = "";
				}

				if (!bSameName ||
                                    !eq.getSize().equals(eq.getBaseSize()) ||
                                    (eqMod1.length() != 0) ||
                                    (eqMod2.length() != 0) ||
                                    (sProp.length() != 0))
				{
					// Save customized name if differs from generated name
					if ((!bSameName) || (sProp.length() != 0))
					{
						buffer.append(eq.getName()).append(";");
					}

					eqSize = eq.getSize();
					if (eqSize.length() == 0)
					{
						eqSize = Constants.s_SIZESHORT[Constants.SIZE_M];
					}
					buffer.append(eq.getBaseItemName() + ";" + eqSize).append(";");
					if (eqMod1.length() != 0)
					{
						buffer.append(eqMod1);
					}
					else
					{
						buffer.append(Constants.s_NONE);
					}
					buffer.append(";");

					if (eqMod2.length() != 0)
					{
						buffer.append(eqMod2);
					}
					else
					{
						buffer.append(Constants.s_NONE);
					}
					if (sProp.length() != 0)
					{
						buffer.append(";").append(sProp);
					}
				}
				else
				{
					buffer.append(eq.getBaseItemName());
				}
				buffer.append(" :");

                                buffer.append(eq.qty().toString()).append(":");

				if (!eq.isHeaderParent())
				{
					if (eq.isEquipped()) {
						buffer.append("Y:");
                                        } else {
						buffer.append("N:");
                                        }
				}
				else
				{
					Equipment aHChild = null;
					for (Iterator it2 = eq.getHeaderChildren().iterator(); it2.hasNext();)
					{
						aHChild = (Equipment)it2.next();

						if (aHChild.isEquipped()) {
							buffer.append("Y|");
						} else {
							buffer.append("N|");
                                                }                                                

						if (aHChild.getCarried().compareTo(new Float(0)) > 0)
						{
							if (aHChild.getParent() != null) {
								buffer.append(aHChild.getCarried()).append("@");
                                                                buffer.append(((Equipment)aHChild.getParent()).getKeyName());
							} else {
								buffer.append(aHChild.getCarried().toString());
                                                        }
						}
						else
							buffer.append("N");

						if (it2.hasNext()) {
							buffer.append("|");
						} else {
							buffer.append(":");
                                                }
					}

				}
				if (eq.getCarried().compareTo(new Float(0)) > 0)
				{
					if (eq.getParent() != null) {
						buffer.append(eq.getCarried()).append("@");
                                                buffer.append(((Equipment)eq.getParent()).getKeyName()).append(":");
					} else {
						buffer.append(eq.getCarried()).append(":");
                                        }
				}
				else
					buffer.append("N:");

				buffer.append(Equipment.getHandName(eq.getHand())).append(":");

				if (eq.getHand() == Equipment.TWOWEAPON_HANDS) {
					buffer.append(eq.getNumberEquipped()).append(":");
                                }
			}
		}
                buffer.append("\n");

	}

	private void appendMiscLine(StringBuffer buffer)
	{
		buffer.append(Utility.escapeColons2(aPC.getEyeColor())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getSkinColor())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getHairColor())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getHairStyle())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getSpeechTendency())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getPhobias())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getInterests())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getTrait1())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getTrait2())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getCatchPhrase())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getLocation())).append(" :");
                buffer.append(Utility.escapeColons2(aPC.getResidence())).append(" :");
                buffer.append("\n");

	}

	private void appendUnusedPointsLine(StringBuffer buffer)
	{
		buffer.append(String.valueOf(aPC.getSkillPoints())).append(":");
		buffer.append(String.valueOf(aPC.getFeats()));
                buffer.append("\n");

	}

	private void appendWeaponProfsLine(StringBuffer buffer)
	{
		for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext();)
		{
			buffer.append(it.next().toString()).append(":");
		}
                buffer.append("\n");

	}

	private void appendLanguagesLine(StringBuffer buffer)
	{
		for (Iterator it = aPC.getLanguagesList().iterator(); it.hasNext();)
		{
			buffer.append(it.next().toString()).append(":");
		}
                buffer.append("\n");

	}

	private void appendRaceLine(StringBuffer buffer)
	{
		buffer.append(aPC.getRace().getKeyName()).append(":");
		buffer.append(Integer.toString(aPC.getAlignment())).append(":");
		buffer.append(Integer.toString(aPC.getHeight())).append(":");
		buffer.append(Integer.toString(aPC.getWeight())).append(":");
		buffer.append(Integer.toString(aPC.getAge())).append(":");
		buffer.append(aPC.getGender()).append(":");
		buffer.append(aPC.getHanded());
		if (aPC.getRace().hitDice() != 0) {
			for (int j = 0; j < aPC.getRace().hitDice(); j++)
			{
				buffer.append(":").append(aPC.getRace().getHitPointList(j).toString());
			}
                }
                buffer.append("\n");

	}

	private void appendDeityLine(StringBuffer buffer)
	{
                String aName = (aPC.getDeity() != null) ? aPC.getDeity().getKeyName() : Constants.s_NONE;
		buffer.append(aName).append(":");

                Domain aDomain;
                CharacterDomain aCharDomain;
		for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();)
		{
			aCharDomain = (CharacterDomain)it.next();

			aName = Constants.s_NONE;
			if (aCharDomain != null)
			{
				aDomain = aCharDomain.getDomain();
				if (aDomain != null)
				{
					aName = aDomain.getKeyName() + "=" + aCharDomain.getDomainSource();
				}
			}
			buffer.append(aName).append(":");
		}
                buffer.append("\n");

	}

	private void appendSkillsLine(StringBuffer buffer)
	{
                Float aRank = null;
		Skill aSkill = null;
                List aRankList = null;
		for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
		{
			aSkill = (Skill)it.next();

			// Only save skills with a Rank
			aRank = aSkill.getRank();
			if (aRank.doubleValue() != 0.0)
			{
				buffer.append(aSkill.getKeyName()).append(":");
                                buffer.append(aSkill.getRank().toString()).append(":");

				aRankList = aSkill.getRankList();
				buffer.append(aRankList.size()).append(":");
				for (Iterator it2 = aRankList.iterator(); it2.hasNext();)
				{
					buffer.append((String)it2.next()).append(":");
				}

				for (Iterator it2 = aSkill.getAssociatedList().iterator(); it2.hasNext();)
                                {
					buffer.append(it2.next().toString()).append(":");
                                }
			}
		}
                buffer.append("\n");
	}

	/*
         * Iterate through a characters feat list and save the associated choices with it.
         * featList = ArrayList of feats a character has chosen
         * virtualFeatList = ArrayList of feats a character has virtually
         * automaticFeatList = ArrayList of feats a character has automatically via FEATAUTO: tags
         * aggregatedFeatList = ArrayList which combines all 3 of the above lists.
         * we only want to save choices here, since when we load a character we assume anything
         * listed is a chosen feat.  The virtual and automatic feats will get re-applied as the
         * character is recreated on import, so those don't need to be saved. The obvious flaw
         * in this is how to save choices due to a virtual or automatic feat, but that can be
         * avoided for now if we simply say that we don't support multiple-feats in a virtual or
         * automatic fashion. This will need to be addressed at some point, but is low priority.
         *
         * <br>author: merton_monk@yahoo.com (Bryan McRoberts) 2-5-2002
         */
	private void appendFeatsLine(StringBuffer buffer)
	{
                Feat aFeat;
		for (Iterator it = aPC.getFeatList().iterator(); it.hasNext();)
		{
			aFeat = (Feat)it.next();
			buffer.append(aFeat.toString());
			for (Iterator it2 = aFeat.getSaveList().iterator(); it2.hasNext();)
                        {
				buffer.append("[").append(it2.next().toString()).append("]");
                        }
                        
			buffer.append(":");
                        buffer.append(Integer.toString(aFeat.getAssociatedList().size())).append(":");

                        for (Iterator it2 = aFeat.getAssociatedList().iterator(); it2.hasNext();)
                        {
				buffer.append(it2.next().toString()).append(":");
                        }
		}
                buffer.append("\n");

	}

	private void appendTemplateLine(StringBuffer buffer)
	{
		buffer.append("TEMPLATE:");
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
                {        
			buffer.append(((PCTemplate)it.next()).getName()).append(":");
                        // Very bad results if we don't write the invisible templates to the pcg file. :)
//                          aTemplate = (PCTemplate)it.next();
//                          if (aTemplate.isVisible()) {
//                                  buffer.append(aTemplate.getName()).append(":");
//                          }
                }
                buffer.append("\n");
	}

}

