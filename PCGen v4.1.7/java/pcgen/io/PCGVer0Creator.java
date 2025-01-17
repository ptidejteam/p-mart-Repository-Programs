/*
 * PCGVer0Creator.java
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
 * Created on March 22, 2002, 4:15 PM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.Utility;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;

/**
 * <code>PCGVer0Creator</code>
 *
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */
final class PCGVer0Creator
{
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 */
	PCGVer0Creator(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * create PCG string for a given PlayerCharacter
	 *
	 * <br>author: Thomas Behr 18-03-02
	 *
	 * @return a String in PCG format, containing all information
	 *         PCGen associates with a given PlayerCharacter
	 */
	public String createPCGString()
	{
		final StringBuffer buffer = new StringBuffer();

		appendCampaignLine(buffer);
		appendVersionLine(buffer);
		appendNameLine(buffer);
		appendStatsLine(buffer);
		appendClassesLine(buffer);
		appendFeatsLine(buffer);
		appendSkillsLine(buffer);
		appendDeityLine(buffer);
		appendRaceLine(buffer);
		appendAutoSpellsLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassSpellLine((PCClass) it.next(), buffer);
		}
		appendLanguagesLine(buffer);
		appendWeaponProfsLine(buffer);
		appendUnusedPointsLine(buffer);
		appendMiscLine(buffer);
		appendEquipmentLine(buffer);
		appendPortraitLine(buffer);
		appendGoldBioDescriptionLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassesSkillLine((PCClass) it.next(), buffer);
		}
		appendExperienceAndMiscListLine(buffer);
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			appendClassSpecialtyAndSaveLine((PCClass) it.next(), buffer);
		}
		appendTemplateLine(buffer);
		appendEquipSetLine(buffer);
		appendFollowerLine(buffer);
		appendNotesLine(buffer);
		buffer.append(Constants.s_LINE_SEP);
		return buffer.toString();
	}

	private static void appendCampaignLine(StringBuffer buffer)
	{
		buffer.append("CAMPAIGNS:");

		Campaign aCamp = null;
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign) it.next();
			if (aCamp.isLoaded())
			{
				buffer.append(aCamp.getName()).append(":");
			}
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	/*
	* modified this function to output the version number as displayed in pcgenprop.properties
	* instead of a simple int. This will record the version more accurately.
	*
	* <br>author: merton_monk 10/24/01
	*/
	private static void appendVersionLine(StringBuffer buffer)
	{
		ResourceBundle d_properties;
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			buffer.append("VERSION:");
			buffer.append(d_properties.getString("VersionNumber"));
			buffer.append(Constants.s_LINE_SEP);
		}
		catch (MissingResourceException mre)
		{
			d_properties = null;
		}
	}

	private void appendNameLine(StringBuffer buffer)
	{
		buffer.append(Utility.escapeColons2(aPC.getName())).append(":");
		buffer.append(Utility.escapeColons2(aPC.getTabName())).append(":");
		buffer.append(Utility.escapeColons2(aPC.getPlayersName()));
		buffer.append(Constants.s_LINE_SEP);

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
			buffer.append(String.valueOf(aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[i]))).append(":");
		}
		buffer.append(Integer.toString(aPC.getPoolAmount())).append(":");
		buffer.append(Integer.toString(aPC.getCostPool()));
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendClassesLine(StringBuffer buffer)
	{
		PCClass aClass;
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();
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
		buffer.append(Constants.s_LINE_SEP);

	}

	private static void appendClassesSkillLine(PCClass aClass, StringBuffer buffer)
	{
		buffer.append(aClass.getKeyName()).append(":");
		for (Iterator it = aClass.skillList().iterator(); it.hasNext();)
		{
			buffer.append((String) it.next()).append(" :");
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	private static void appendClassSpecialtyAndSaveLine(PCClass aClass, StringBuffer buffer)
	{
		buffer.append(aClass.getKeyName()).append(":");
		for (Iterator it = aClass.getSpecialtyList().iterator(); it.hasNext();)
		{
			buffer.append("SPECIAL").append((String) it.next()).append(":");
		}
		for (int i = 0; i < aClass.getSaveCount(); i++)
		{
			final String aString = aClass.getSave(i);
			final SpecialAbility sa = aClass.getSpecialAbilityNamed(aString);
			if (sa != null)
				buffer.append(sa.getName()).append(":").append(sa.getSource());
			else
				buffer.append(aClass.getSave(i));
			buffer.append(":");
		}

		buffer.append(Constants.s_LINE_SEP);
	}

	private void appendClassSpellLine(PCClass aClass, StringBuffer buffer)
	{
		// SPELL:name:times:type:objectname:classname:book:level:feat:feat:feat
		final List spellList = aClass.getCharacterSpell(null, "", -1);
		for (Iterator it = spellList.iterator(); it.hasNext();)
		{
			CharacterSpell cs = (CharacterSpell) it.next();
			for (Iterator ci = cs.getInfoList().iterator(); ci.hasNext();)
			{
				SpellInfo si = (SpellInfo) ci.next();
				final String tString = cs.getOwner().getSpellKey();
				final int[] spellLevels = cs.getSpell().levelForKey(tString);
				for (int sindex = 0; sindex < spellLevels.length; sindex++)
				{
					final int spellLevel = spellLevels[sindex];
					if (si.getBook().equals(Globals.getDefaultSpellBook()) &&
						aClass.isAutoKnownSpell(cs.getSpell().getKeyName(), spellLevel) &&
						aPC.getAutoSpells())
						continue;
					buffer.append("SPELL:").append(cs.getSpell().getKeyName());
					buffer.append(":").append(si.getTimes());
					final String a = tString.substring(0, tString.indexOf("|")); // CLASS or DOMAIN
					final String b = tString.substring(a.length() + 1); // name of class or domain
					buffer.append(":").append(a).append(":").append(b);
					buffer.append(":").append(aClass.getName());
					buffer.append(":").append(si.getBook());
					buffer.append(":").append(si.getActualLevel());
					if (si.getFeatList() != null)
						for (Iterator fi = si.getFeatList().iterator(); fi.hasNext();)
						{
							buffer.append(":").append(((Feat) fi.next()).getName());
						}
					buffer.append(Constants.s_LINE_SEP);
				}
			}
		}
	}

	private void appendExperienceAndMiscListLine(StringBuffer buffer)
	{
		buffer.append(Integer.toString(aPC.getXP())).append(":");
		for (int i = 0; i < 3; i++)
		{
			buffer.append(Utility.escapeColons(aPC.getMiscList().get(i).toString())).append(" :");
		}
		buffer.append(aPC.getStrFPoints()).append(":");
		buffer.append(aPC.getDPoints()).append(":");
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendGoldBioDescriptionLine(StringBuffer buffer)
	{
		buffer.append(aPC.getGold().toString()).append(":");
		buffer.append(Utility.escapeColons(aPC.getBio())).append(":");
		buffer.append(Utility.escapeColons(aPC.getDescription())).append(":");
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendEquipmentLine(StringBuffer buffer)
	{
		for (Iterator it = aPC.getEquipmentList().iterator(); it.hasNext();)
		{
			Equipment eq = (Equipment) it.next();
			if (!eq.getHasHeaderParent())
			{
				buffer.append(eq.formatSaveLine(";", "="));

				buffer.append(" :");

				buffer.append(eq.qty()).append(":");
				buffer.append(String.valueOf(eq.getOutputIndex())).append(":");

				if (!eq.isHeaderParent())
				{
					if (eq.isEquipped())
					{
						buffer.append("Y:");
					}
					else
					{
						buffer.append("N:");
					}
				}
				else
				{
					Equipment aHChild = null;
					for (int it2 = 0; it2 < eq.getHeaderChildCount(); it2++)
					{
						aHChild = eq.getHeaderChild(it2);

						if (aHChild.isEquipped())
						{
							buffer.append("Y|");
						}
						else
						{
							buffer.append("N|");
						}

						if (aHChild.getCarried().compareTo(new Float(0)) > 0)
						{
							if (aHChild.getParent() != null)
							{
								buffer.append(aHChild.getCarried()).append("@");
								buffer.append(((Equipment) aHChild.getParent()).getKeyName());
							}
							else
							{
								buffer.append(aHChild.getCarried().toString());
							}
						}
						else
						{
							buffer.append("N");
						}

						if (it2 == eq.getHeaderChildCount() - 1)
						{
							buffer.append(":");
						}
						else
						{
							buffer.append("|");
						}
					}

				}
				if (eq.getCarried().compareTo(new Float(0)) > 0)
				{
					if (eq.getParent() != null)
					{
						buffer.append(eq.getCarried()).append("@");
						buffer.append(((Equipment) eq.getParent()).getKeyName()).append(":");
					}
					else
					{
						buffer.append(eq.getCarried()).append(":");
					}
				}
				else
					buffer.append("N:");

				buffer.append(Equipment.getHandName(eq.getHand())).append(":");

				if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
				{
					buffer.append(eq.getNumberEquipped()).append(":");
				}
			}
		}
		buffer.append(Constants.s_LINE_SEP);

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
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendUnusedPointsLine(StringBuffer buffer)
	{
		buffer.append(String.valueOf(aPC.getSkillPoints())).append(":");
		buffer.append(String.valueOf(aPC.getFeats()));
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendWeaponProfsLine(StringBuffer buffer)
	{
		for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext();)
		{
			buffer.append(it.next().toString()).append(':');
		}
		//
		// Save any selected racial bonus weapons
		//
		if (aPC.getRace() != null)
		{
			for (int i = 0; i < aPC.getRace().getSelectedWeaponProfBonusCount(); i++)
			{
				if (i == 0)
				{
					buffer.append("RACE=").append(aPC.getRace().getName()).append(':');
				}
				buffer.append(aPC.getRace().getSelectedWeaponProfBonus(i)).append(':');
			}
		}

		//
		// Save any selected class bonus weapons
		//
		for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			for (int i = 0; i < aClass.getSelectedWeaponProfBonusCount(); i++)
			{
				if (i == 0)
				{
					buffer.append("CLASS=").append(aClass.getName()).append(':');
				}
				buffer.append(aClass.getSelectedWeaponProfBonus(i)).append(':');
			}
		}

		//
		// Save any selected domain bonus weapons
		//
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				for (int i = 0; i < aDomain.getSelectedWeaponProfBonusCount(); i++)
				{
					if (i == 0)
					{
						buffer.append("DOMAIN=").append(aDomain.getName()).append(':');
					}
					buffer.append(aDomain.getSelectedWeaponProfBonus(i)).append(':');
				}
			}
		}

		//
		// Save any selected feat bonus weapons
		//
		for (Iterator e = aPC.getFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			for (int i = 0; i < aFeat.getSelectedWeaponProfBonusCount(); i++)
			{
				if (i == 0)
				{
					buffer.append("FEAT=").append(aFeat.getName()).append(':');
				}
				buffer.append(aFeat.getSelectedWeaponProfBonus(i)).append(':');
			}
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendAutoSpellsLine(StringBuffer buffer)
	{
		if (aPC.getAutoSpells())
			buffer.append("AUTOSPELLS:YES");
		else
			buffer.append("AUTOSPELLS:NO");
		buffer.append(Constants.s_LINE_SEP);
	}

	private void appendLanguagesLine(StringBuffer buffer)
	{
		for (Iterator it = aPC.getLanguagesList().iterator(); it.hasNext();)
		{
			buffer.append(it.next().toString()).append(":");
		}
		buffer.append(Constants.s_LINE_SEP);

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
		if (aPC.getRace().hitDice() != 0)
		{
			for (int j = 0; j < aPC.getRace().hitDice(); j++)
			{
				buffer.append(":").append(aPC.getRace().getHitPointList(j).toString());
			}
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendDeityLine(StringBuffer buffer)
	{
		final String aName = (aPC.getDeity() != null) ? aPC.getDeity().getKeyName() : Constants.s_NONE;
		buffer.append(aName).append(':');

		Domain aDomain;
		CharacterDomain aCharDomain;
		for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();)
		{
			aCharDomain = (CharacterDomain) it.next();

			StringBuffer domainInfo = new StringBuffer(Constants.s_NONE);
			if (aCharDomain != null)
			{
				aDomain = aCharDomain.getDomain();
				if (aDomain != null)
				{
					domainInfo.setLength(0);
					domainInfo.append(aDomain.getKeyName());
					if (aDomain.getAssociatedCount() != 0)
					{
						// These are not my glory minutes - I am not happy that I am making a copy.
						ArrayList associatedList = new ArrayList(aDomain.getAssociatedCount());
						aDomain.addAssociatedTo(associatedList);
						domainInfo.append("=LIST|").append(Utility.unSplit(associatedList, "|"));
					}
					domainInfo.append('=').append(aCharDomain.getDomainSource());
				}
			}
			buffer.append(domainInfo.toString()).append(':');
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendSkillsLine(StringBuffer buffer)
	{
		Float aRank = null;
		Skill aSkill = null;
		List aRankList = null;
		for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
		{
			aSkill = (Skill) it.next();

			// Only save skills with a Rank
			aRank = aSkill.getRank();
			if (aRank.doubleValue() != 0.0 || aSkill.getOutputIndex() != 0)
			{
				buffer.append(aSkill.getKeyName()).append(":");
				buffer.append(aRank.toString()).append(":");
				buffer.append(String.valueOf(aSkill.getOutputIndex())).append(":");

				aRankList = aSkill.getRankList();
				buffer.append(aRankList.size()).append(":");
				for (Iterator it2 = aRankList.iterator(); it2.hasNext();)
				{
					buffer.append((String) it2.next()).append(":");
				}

				//
				// Append associated list
				//
				int it2;
				for (it2 = 0; it2 < aSkill.getAssociatedCount(); ++it2)
				{
					buffer.append(aSkill.getAssociated(it2)).append(":");
				}

				//
				// Pad with empty entries
				//
				final int iRank = aRank.intValue();
				if ((aSkill.getChoiceList() != null) && (aSkill.getChoiceList().size() > 0) && (iRank > 0))
				{
					for (; it2 < iRank; ++it2)
					{
						buffer.append(" :");
					}
				}

			}
		}
		buffer.append(Constants.s_LINE_SEP);
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
			aFeat = (Feat) it.next();
			buffer.append(aFeat.toString());
			for (int i2 = 0; i2 < aFeat.getSaveCount(); i2++)
			{
				buffer.append("[").append(aFeat.getSave(i2)).append("]");
			}

			buffer.append(":");
			buffer.append(Integer.toString(aFeat.getAssociatedCount())).append(":");

			for (int it2 = 0; it2 < aFeat.getAssociatedCount(); it2++)
			{
				buffer.append(aFeat.getAssociated(it2)).append(":");
			}
		}
		buffer.append(Constants.s_LINE_SEP);

	}

	private void appendTemplateLine(StringBuffer buffer)
	{
		buffer.append("TEMPLATE:");
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			buffer.append(((PCTemplate) it.next()).getName()).append(":");
		}
		buffer.append(Constants.s_LINE_SEP);
	}

	private void appendNotesLine(StringBuffer buffer)
	{
		for (Iterator i = aPC.getNotesList().iterator(); i.hasNext();)
		{
			NoteItem ni = (NoteItem) i.next();
			buffer.append("NOTES:").append(ni.getId()).append(":").append(ni.getParentId()).append(":").append(ni.getName());
			buffer.append(":").append(ni.getValue()).append(Constants.s_LINE_SEP);
		}
	}

	private void appendEquipSetLine(StringBuffer buffer)
	{
		// order is _very_ important, as the PCGVer0Parser depends on
		// root nodes and container nodes existing before it creates
		// the equipment to put into them

		// so we have to sort the EquipSetList before we save it
		final ArrayList eqSetList = aPC.getEquipSet();
		Collections.sort(eqSetList);

		for (Iterator e = eqSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getValue().length() > 0)
			{
				buffer.append("EQUIPSET:").append(es.getIdPath());
				buffer.append(":").append(es.getName());
				buffer.append(":").append(es.getValue());
				buffer.append(":").append(es.getQty());
				buffer.append(Constants.s_LINE_SEP);
			}
			else
			{
				buffer.append("EQUIPSET:").append(es.getIdPath());
				buffer.append(":").append(es.getName());
				buffer.append(Constants.s_LINE_SEP);
			}
		}
	}

	private void appendFollowerLine(StringBuffer buffer)
	{
		final Follower aM = aPC.getMaster();
		if (aM != null)
		{
			buffer.append("MASTER|").append(aM.getFileName());
			buffer.append("|").append(aM.getName());
			buffer.append("|").append(aM.getType());
			buffer.append("|").append(aM.getUsedHD());
			buffer.append(Constants.s_LINE_SEP);
		}

		final ArrayList fList = aPC.getFollowerList();

		if (fList.isEmpty())
			return;
		for (Iterator f = fList.iterator(); f.hasNext();)
		{
			Follower aF = (Follower) f.next();
			buffer.append("FOLLOWER|").append(aF.getFileName());
			buffer.append("|").append(aF.getName());
			buffer.append("|").append(aF.getType());
			buffer.append("|").append(aF.getUsedHD());
			buffer.append(Constants.s_LINE_SEP);
		}
	}

	private void appendPortraitLine(StringBuffer buffer)
	{
		buffer.append("PORTRAIT:");
		buffer.append(aPC.getPortraitPath());
		buffer.append(Constants.s_LINE_SEP);
	}
}

