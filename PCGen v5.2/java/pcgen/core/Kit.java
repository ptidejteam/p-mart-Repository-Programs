/*
 * Kit.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 1:49 PM
 */

package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.character.CharacterSpell;
import pcgen.core.kit.KitFeat;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitSchool;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSkillAdd;
import pcgen.core.kit.KitSpells;
import pcgen.core.kit.KitWrapper;
import pcgen.core.spell.Spell;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.Logging;

/**
 * <code>Kit</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class Kit extends PObject implements Comparable
{
	private String region = "";
	private String sellRate = null;
	private String buyRate = null;
	private List gearList = null;
	private List featList = null;
	private List spellsList = null;
	private List skillList = null;
	private List profList = null;
	private KitSchool school = null;
	private PlayerCharacter aPC = null;

	/**
	 * Constructor for Kit
	 * @param argRegion String
	 */
	public Kit(String argRegion)
	{
		super();
		region = argRegion;
	}

	/**
	 * Used to compare Kits.
	 * @param o Object
	 * @return int
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object o)
	{
		//this should throw a ClassCastException for non-Kit, like the Comparable interface calls for
		final Kit oKit = (Kit) o;
		int retVal = region.compareTo(oKit.getRegion());
		if (retVal == 0)
		{
			retVal = getName().compareTo(oKit.getName());
		}
		return retVal;
	}

	/**
	 * Returns the region of the kit.
	 * @return String
	 */
	public String getRegion()
	{
		return region;
	}

	/**
	 * Sets the name of the kit.
	 * @param argName String
	 */
	public void setName(String argName)
	{
		if (!argName.endsWith(".MOD"))
		{
			name = argName;
			keyName = region + "|" + argName;
		}
	}

	private void setMyType()
	{
		if (super.getMyTypeCount() == 0)
		{
			if (gearList != null)
			{
				addMyType("GEAR");
			}
			if (featList != null)
			{
				addMyType("FEAT");
			}
			if (spellsList != null)
			{
				addMyType("SPELL");
			}
			if (skillList != null)
			{
				addMyType("SKILL");
			}
			if (school != null)
			{
				addMyType("SCHOOL");
			}
		}
	}

	/**
	 * Gets the type of the kit.
	 * @return String
	 */
	public String getType()
	{
		setMyType();
		return super.getType();
	}

	/**
	 * Gets one type of the kit.
	 * @param i int
	 * @return String
	 */
	public String getMyType(int i)
	{
		setMyType();
		return super.getMyType(i);
	}

	/**
	 * Returns the number of types the kit has.
	 * @return int
	 */
	public int getMyTypeCount()
	{
		setMyType();
		return super.getMyTypeCount();
	}

	/**
	 * Returns whether the kit has a certain type.
	 * @param aType String
	 * @return boolean
	 */
	public boolean isType(String aType)
	{
		setMyType();
		return super.isType(aType);
	}

	/**
	 * Sets the sell rate of the kit.
	 * @param argRate String
	 */
	public void setSellRate(String argRate)
	{
		sellRate = argRate;
	}

	/**
	 * Gets the buy rate of the kit.
	 * @return String
	 */
	private String getBuyRate()
	{
		return buyRate;
	}

	/**
	 * Sets the buy rate of the kit.
	 * @param argRate String
	 */
	public void setBuyRate(String argRate)
	{
		buyRate = argRate;
	}

	/**
	 * Gets the feats in the kit.
	 * @return List
	 */
	public List getFeats()
	{
		return featList;
	}

	/**
	 * Adds a feat to the kit.
	 * @param kFeat KitFeat
	 */
	public void addFeat(KitFeat kFeat)
	{
		if (kFeat != null)
		{
			if (featList == null)
			{
				featList = new ArrayList();
			}
			featList.add(kFeat);
		}
	}

	/**
	 * Gets the skill list of the kit.
	 * @return List
	 */
	public List getSkill()
	{
		return skillList;
	}

	/**
	 * Adds a skill to the kit.
	 * @param kSkill KitSkill
	 */
	public void addSkill(KitSkill kSkill)
	{
		if (kSkill != null)
		{
			if (skillList == null)
			{
				skillList = new ArrayList();
			}
			skillList.add(kSkill);
		}
	}

	/**
	 * Gets a list of the gear of the kit.
	 * @return List
	 */
	public List getGear()
	{
		return gearList;
	}

	/**
	 * Adds a gear to the kit.
	 * @param kGear KitGear
	 */
	public void addGear(KitGear kGear)
	{
		if (kGear != null)
		{
			if (gearList == null)
			{
				gearList = new ArrayList();
			}
			gearList.add(kGear);
		}
	}

	/**
	 * Gets the spells of the kit.
	 * @return List
	 */
	public List getSpells()
	{
		return spellsList;
	}

	/**
	 * Adds a spell to the kit.
	 * @param kSpells KitSpells
	 */
	public void addSpells(KitSpells kSpells)
	{
		if (kSpells != null)
		{
			if (spellsList == null)
			{
				spellsList = new ArrayList();
			}
			spellsList.add(kSpells);
		}
	}

	/**
	 * Method getProfs
	 * @return List
	 */
	public List getProfs()
	{
		return profList;
	}

	/**
	 * Method addProf
	 * @param kProf KitProf
	 */
	public void addProf(KitProf kProf)
	{
		if (kProf != null)
		{
			if (profList == null)
			{
				profList = new ArrayList();
			}
			profList.add(kProf);
		}
	}

	/**
	 * Method setSchool
	 * @param kSchool KitSchool
	 */
	public void setSchool(KitSchool kSchool)
	{
		school = kSchool;
	}

	/**
	 * Add the skills
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void addKitSkills(List thingsToAdd, List warnings)
	{
		List aList = getSkill();
		if ((aList != null) && (aList.size() != 0))
		{
			PCClass pcClass = getPCClass(false);
			if (pcClass == null)
			{
				warnings.add("SKILL: No owning class found. Kit needs PRECLASS");
				return;
			}

			//
			// get a list of skills that meet prereqs
			//
			List aSkillList = new ArrayList();
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitSkill kSkill = (KitSkill) aList.get(i);
				if (!passesPreReqTestsForList(aPC, this, kSkill.getPrereqs()))
				{
					continue;
				}
				String skillName = itemPassesPrereqs(kSkill.getSkillName());
				if (skillName == null)
				{
					continue;
				}

				Skill aSkill;
				if (skillName.startsWith("TYPE="))
				{
					final String skillType = skillName.substring(5);
					List skillsOfType = new ArrayList();
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						aSkill = (Skill) e.next();
						if (aSkill.isType(skillType))
						{
							skillsOfType.add(aSkill.getName());
						}
					}
					if (skillsOfType.size() == 0)
					{
						continue;
					}
					else if (skillsOfType.size() == 1)
					{
						skillName = (String) skillsOfType.get(0);
					}
					else
					{
						//
						// No choice means they don't want to assign any skill points to it
						//
						skillName = Globals.chooseFromList("Select " + skillType + " skill", skillsOfType, new ArrayList(), 1);
						if (skillName == null)
						{
							continue;
						}
					}
				}

				aSkill = Globals.getSkillNamed(skillName);
				if (aSkill == null)
				{
					warnings.add("SKILL: Non-existant skill \"" + skillName + "\"");
					continue;
				}
				KitSkillAdd sta = new KitSkillAdd(aSkill, Math.min(kSkill.getRank(), aPC.getMaxRank(skillName, pcClass).doubleValue()), aSkill.costForPCClass(pcClass).intValue(), kSkill.isFree());
				aSkillList.add(sta);
			}
			int skillPool = pcClass.skillPool().intValue();
			if ((aSkillList.size() == 0) || (skillPool == 0))
			{
				return;
			}

			//
			// Add 1 point to each skill until we run out of skill points for this class or
			// all the skills are at their max.
			//
			int skillsUsed = 0;
			final int x = aSkillList.size();
			boolean bModified;
			while (skillsUsed < skillPool)
			{
				bModified = false;
				for (int i = 0; i < x; ++i)
				{
					final KitSkillAdd sta = (KitSkillAdd) aSkillList.get(i);
					if (sta.addRank())
					{
						bModified = true;
						if (++skillsUsed == skillPool)
						{
							break;
						}
					}
				}
				//
				// If went through entire list without adding, then exit (all skills at max)
				//
				if (!bModified)
				{
					break;
				}
			}

			for (int i = 0; i < x; ++i)
			{
				final KitSkillAdd sta = (KitSkillAdd) aSkillList.get(i);
				if (sta.wasModified())
				{
					KitWrapper tta = new KitWrapper(sta, false);
					tta.setPObject(pcClass);
					thingsToAdd.add(tta);
				}
			}
		}
	}

	/**
	 * Add the Feats
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void addKitFeats(List thingsToAdd, List warnings)
	{
		List aList = getFeats();
		aPC = Globals.getCurrentPC();
		if ((aList != null) && (aList.size() != 0))
		{
			boolean tooManyFeats = false;
			int featsChosen = 0;
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitFeat kFeat = (KitFeat) aList.get(i);
				//
				// Can't have it/them if don't qualify
				//
				if (!passesPreReqTestsForList(aPC, this, kFeat.getPrereqs()))
				{
					continue;
				}

				//
				// Only ask for feats that exist
				//
				// TODO: Allow feats with multiple choices like Martial Weapon Proficiency
				//
				List aFeatList = new ArrayList();
				for (Iterator e = kFeat.getFeatList().iterator(); e.hasNext();)
				{
					final String featName = itemPassesPrereqs((String) e.next());
					if (featName == null)
					{
						continue;
					}

					final Feat aFeat = Globals.getFeatNamed(featName);
					if (aFeat != null)
					{
						aFeatList.add(featName);
					}
					else
					{
						warnings.add("FEAT: Non-existant feat \"" + featName + "\"");
					}
				}
				int choiceCount = kFeat.getChoiceCount();
				//
				// Can't choose more entries than there are...
				//
				if (choiceCount > aFeatList.size())
				{
					choiceCount = aFeatList.size();
				}

				//
				// Don't allow choosing of more than allotted number of feats
				//
				if (choiceCount > ((int) aPC.getFeats() - featsChosen))
				{
					choiceCount = (int) aPC.getFeats() - featsChosen;
					tooManyFeats = true;
				}
				if (choiceCount == 0)
				{
					continue;
				}

				List xs;
				if (choiceCount == aFeatList.size())
				{
					xs = aFeatList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose feat(s)", aFeatList, new ArrayList(), choiceCount);
						if (xs.size() != 0)
						{
							break;
						}
					}
				}
				//
				// Add to list of things to add to the character
				//
				for (Iterator e = xs.iterator(); e.hasNext();)
				{
					final String featName = (String) e.next();
					final Feat aFeat = Globals.getFeatNamed(featName);
					if (aFeat != null)
					{
						KitWrapper tta = new KitWrapper(featName, false);
						tta.setPObject(aFeat);
						thingsToAdd.add(tta);
						++featsChosen;
					}
					else
					{
						warnings.add("FEAT: Non-existant feat \"" + featName + "\"");
					}
				}
			}
			if (tooManyFeats)
			{
				warnings.add("FEAT: Some feats were not granted--not enough remaining feats");
			}
		}
	}

	/**
	 * Add the proficiencies
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void addKitProfs(List thingsToAdd, List warnings)
	{
		aPC = Globals.getCurrentPC();
		List aList = getProfs();

		if ((aList != null) && (aList.size() != 0))
		{
			PObject aPObject;
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitProf kProf = (KitProf) aList.get(i);
				if (!passesPreReqTestsForList(aPC, this, kProf.getPrereqs()))
				{
					continue;
				}

				//
				// Get list of weapon profs that selection should come from
				//
				// TODO: If racial or class optional proficiency is same as what we want
				// to set, don't error
				//
				List aBonusList;
				PCClass pcClass;

				if (kProf.isRacial())
				{
					final Race pcRace = aPC.getRace();
					if (pcRace == null)
					{
						warnings.add("PROF: PC has no race");
						continue;
					}
					if (pcRace.getSelectedWeaponProfBonusCount() != 0)
					{
						warnings.add("PROF: Race has already selected bonus weapon proficiency");
						continue;
					}
					aPObject = pcRace;
					aBonusList = pcRace.getWeaponProfBonus();
				}
				else
				{
					pcClass = getPCClass(false);
					if (pcClass == null)
					{
						warnings.add("PROF: No owning class found. Kit needs PRECLASS");
						continue;
					}
					if (pcClass.getSelectedWeaponProfBonusCount() != 0)
					{
						warnings.add("PROF: Class has already selected bonus weapon proficiency");
						continue;
					}
					aPObject = pcClass;
					aBonusList = pcClass.getWeaponProfBonus();
				}
				if ((aBonusList == null) || (aBonusList.size() == 0))
				{
					warnings.add("PROF: No optional weapon proficiencies");
					continue;
				}

				List aProfList = new ArrayList();
				for (Iterator e = kProf.getProfList().iterator(); e.hasNext();)
				{
					final String profName = itemPassesPrereqs((String) e.next());
					if (profName == null)
					{
						continue;
					}

					if (!aBonusList.contains(profName))
					{
						warnings.add("PROF: Weapon proficiency \"" + profName + "\" is not in list of choices");
					}

					final WeaponProf aProf = Globals.getWeaponProfNamed(profName);
					if (aProf != null)
					{
						aProfList.add(profName);
					}
					else
					{
						warnings.add("PROF: Non-existant proficiency \"" + profName + "\"");
					}
				}
				int choiceCount = kProf.getChoiceCount();
				//
				// Can't choose more entries than there are...
				//
				if (choiceCount > aProfList.size())
				{
					choiceCount = aProfList.size();
				}

				if (choiceCount == 0)
				{
					continue;
				}

				List xs;
				if (choiceCount == aProfList.size())
				{
					xs = aProfList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose Proficiencies", aProfList, new ArrayList(), choiceCount);
						if (xs.size() != 0)
						{
							break;
						}
					}
				}
				//
				// Add to list of things to add to the character
				//
				for (Iterator e = xs.iterator(); e.hasNext();)
				{
					final String profName = (String) e.next();
					final WeaponProf aProf = Globals.getWeaponProfNamed(profName);
					if (aProf != null)
					{
						KitWrapper tta = new KitWrapper(aProf, false);
						tta.setPObject(aPObject);
						thingsToAdd.add(tta);
					}
					else
					{
						warnings.add("PROF: Non-existant proficiency \"" + profName + "\"");
					}
				}
			}
		}
	}

	/**
	 * Add the equipment
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void addKitGear(List thingsToAdd, List warnings)
	{
		aPC = Globals.getCurrentPC();
		boolean outOfFunds = false;
		List aList = getGear();
		if ((aList != null) && (aList.size() != 0))
		{
			//
			// get the purchase rate
			//
			int aBuyRate = SettingsHandler.getGearTab_BuyRate();
			final String purchaseFormula = getBuyRate();
			if ((purchaseFormula != null) && (purchaseFormula.length() != 0))
			{
				String costFormula = getCostFromFormula(purchaseFormula);
				if (costFormula != null)
				{
					aBuyRate = aPC.getVariableValue(costFormula, "").intValue();
				}
			}
			BigDecimal pcGold = aPC.getGold();
			BigDecimal goldSpent = new BigDecimal("0");

			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitGear kGear = (KitGear) aList.get(i);
				if (!passesPreReqTestsForList(aPC, this, kGear.getPrereqs()))
				{
					continue;
				}

				//
				// Find the item
				//
				String eqName = itemPassesPrereqs(kGear.getName());
				if (eqName == null)
				{
					continue;
				}
				if (eqName.startsWith("TYPE="))
				{
					List eqList = Globals.getEquipmentOfType(Globals.getEquipmentList(), eqName.substring(5), "");
					//
					// Remove any that are too expensive
					//
					int maxCost = kGear.getMaxCost();
					if (maxCost != 0)
					{
						BigDecimal bdMaxCost = new BigDecimal(Integer.toString(maxCost));
						for (int idx = eqList.size() - 1; idx >= 0; --idx)
						{
							if (((Equipment) eqList.get(idx)).getCost().compareTo(bdMaxCost) > 0)
							{
								eqList.remove(idx);
							}
						}
						eqName = Globals.chooseFromList("Choose equipment", eqList, new ArrayList(), 1);
						//
						// TODO: Check to see if the user has selected an item that requires modification (MOD:R)
						//
					}
				}

				Equipment eq = Globals.getEquipmentNamed(eqName);
				if (eq == null)
				{
					warnings.add("GEAR: Non-existant gear \"" + eqName + "\"");
					continue;
				}

				//
				// Find and add any equipment modifiers
				//
				final List eqMods = kGear.getEqMods();
				final List eqTypeList = eq.typeList();
				eq = (Equipment) eq.clone();

				//
				// Resize item for character--never resize weapons or ammo, unless it's a natural (weapon)
				//
				boolean tryResize = false;
				if (eq.isType("Natural") || (!eq.isWeapon() && !eq.isAmmunition()))
				{
					tryResize = Globals.canResizeHaveEffect(eq, null);
				}

				if (tryResize)
				{
					eq.resizeItem(Globals.getSizeAdjustmentAtIndex(aPC.sizeInt()).getAbbreviation());
				}
				if (eqMods != null)
				{
					for (int j = 0; j < eqMods.size(); ++j)
					{
						final String eqModName = (String) eqMods.get(j);
						int idxPipe = eqModName.indexOf('|');
						EquipmentModifier eqMod;
						if (idxPipe < 0)
						{
							eqMod = Globals.getQualifiedModifierNamed(eqModName, eqTypeList);
						}
						else
						{
							eqMod = Globals.getQualifiedModifierNamed(eqModName.substring(0, idxPipe), eqTypeList);
						}
						if (eqMod == null)
						{
						warnings.add("GEAR: " + eqName + ": non-existant equipment modifier \"" + eqModName + "\"");
						}
						else
						{
							if (idxPipe > 0)
							{
								eq.addEqModifier(eqMod.getKeyName() + eqModName.substring(idxPipe), true);		// stick on primary head
							}
							else
							{
								eq.addEqModifier(eqMod, true);		// stick on primary head
							}
						}
					}
				}
				if (tryResize || (eqMods != null))
				{
					eq.nameItemFromModifiers();
				}

				int qty = kGear.getQty();
				final BigDecimal eqCost = eq.getCost();
				BigDecimal extendedCost = new BigDecimal("0");
				if (aBuyRate != 0)
				{
					final BigDecimal bdBuyRate = new BigDecimal(Integer.toString(aBuyRate)).multiply(new BigDecimal("0.01"));
					//
					// Check to see if can afford to buy equipment. If cannot, then decrement
					// the quantity and try again.
					//
					extendedCost = eqCost.multiply(new BigDecimal(Integer.toString(qty))).multiply(bdBuyRate);
					while (qty > 0)
					{
						if (extendedCost.add(goldSpent).compareTo(pcGold) <= 0)			// PC has enough?
						{
							break;
						}

						extendedCost = eqCost.multiply(new BigDecimal(Integer.toString(--qty))).multiply(bdBuyRate);
					}
					goldSpent = goldSpent.add(extendedCost);
				}

				if (qty != kGear.getQty())
				{
					outOfFunds = true;
				}

				//
				// Can't buy none
				//
				if (qty == 0)
				{
					continue;
				}
				KitWrapper tta = new KitWrapper(eq, qty);
				tta.setCost(extendedCost);
				thingsToAdd.add(tta);
			}
		}
		if (outOfFunds)
		{
			warnings.add("GEAR: Some or all equipment not added--not enough funds.");
		}
	}

	/**
	 * Add the spells
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void addKitSpells(List thingsToAdd, List warnings)
	{
		aPC = Globals.getCurrentPC();
		List aList = getSpells();
		if ((aList != null) && (aList.size() != 0))
		{
			PCClass pcClass = getPCClass(true);
			if (pcClass == null)
			{
				warnings.add("SPELLS: No owning class found. Kit needs PRECLASS");
				return;
			}

			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitSpells kSpells = (KitSpells) aList.get(i);
				if (!passesPreReqTestsForList(aPC, this, kSpells.getPrereqs()))
				{
					continue;
				}

				List aSpellList = new ArrayList();
				for (Iterator e = kSpells.getSpellList().iterator(); e.hasNext();)
				{
					final String spellName = itemPassesPrereqs((String) e.next());
					if (spellName == null)
					{
						continue;
					}

					if (spellName.startsWith("LEVEL="))
					{
						aSpellList = Globals.getSpellsIn(Integer.parseInt(spellName.substring(6)), pcClass.getName(), "");
					}
					else
					{
						final Spell aSpell = Globals.getSpellNamed(spellName);
						if (aSpell != null)
						{
							aSpellList.add(aSpell);
						}
						else
						{
							warnings.add("SPELLS: Non-existant spell \"" + spellName + "\"");
						}
					}
				}
				//
				// no limit to number--get them all
				//
				final String choiceFormula = kSpells.getCountFormula();
				int choiceCount;
				if (choiceFormula.length() == 0)
				{
					choiceCount = aSpellList.size();
				}
				else
				{
					choiceCount = aPC.getVariableValue(choiceFormula, "").intValue();
				}

				//
				// Can't choose more entries than there are...
				//
				if (choiceCount > aSpellList.size())
				{
					choiceCount = aSpellList.size();
				}

				if (choiceCount == 0)
				{
					continue;
				}

				List xs;
				if (choiceCount == aSpellList.size())
				{
					xs = aSpellList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose spell(s)", aSpellList, new ArrayList(), choiceCount);
						if (xs.size() != 0)
						{
							break;
						}
					}
				}

				//
				// Add to list of things to add to the character
				//
				for (Iterator e = xs.iterator(); e.hasNext();)
				{
					final Object obj = e.next();
					Spell aSpell;
					if (obj instanceof Spell)
					{
						aSpell = (Spell) obj;
					}
					else
					{
						aSpell = Globals.getSpellNamed((String) obj);
					}
					if (aSpell != null)
					{
						KitWrapper tta = new KitWrapper(aSpell, false);
						tta.setPObject(pcClass);
						thingsToAdd.add(tta);
					}
					else
					{
						warnings.add("SPELLS: Non-existant spell \"" + obj.toString() + "\"");
					}
				}
			}
		}
	}

	/**
	 * Method processKit
	 * @param thingsToAdd List
	 */
	public void processKit(List thingsToAdd)
	{
		processKit(thingsToAdd, -1);
	}

	/**
	 * Method processKit
	 * @param thingsToAdd List
	 * @param kitNo int
	 */
	public void processKit(List thingsToAdd, int kitNo)
	{
		BigDecimal goldSpent = new BigDecimal("0");
		for (Iterator e = thingsToAdd.iterator(); e.hasNext();)
		{
			final KitWrapper tta = (KitWrapper) e.next();
			final Object obj = tta.getObject();

			if (obj instanceof String)
			{
				if (tta.getPObject() instanceof Feat)
				{
					aPC.modFeat((String) obj, true, false);
					if (tta.isFree())
					{
						aPC.setFeats(aPC.getFeats() + 1);
					}
				}

				PCGen_Frame1.forceUpdate_InfoFeats();
			}
			else if (obj instanceof Equipment)
			{
				final Equipment existing = aPC.getEquipmentNamed(((Equipment) obj).getName());
				if (existing == null)
				{
					((Equipment) obj).setQty(new Float(tta.getQty()));

					aPC.addEquipment((Equipment) obj);
					Globals.addEquipment((Equipment) obj);
				}
				else
				{
					existing.setQty(existing.qty() + tta.getQty());
				}
				if (((Equipment) obj).getMemberOfKit() == -1)
				{
					((Equipment) obj).setMemberOfKit(kitNo);
				}
				goldSpent = goldSpent.add(tta.getCost());
				PCGen_Frame1.forceUpdate_InfoInventory();
			}
			else if (obj instanceof Spell)
			{
				updatePCSpells((Spell) obj, (PCClass) tta.getPObject());
			}
			else if (obj instanceof KitSkillAdd)
			{
				updatePCSkills((KitSkillAdd) obj, (PCClass) tta.getPObject());
			}
			else if (obj instanceof WeaponProf)
			{
				PObject pobj = tta.getPObject();
				pobj.addSelectedWeaponProfBonus(((WeaponProf) obj).getName());
			}
			else
			{
				Logging.errorPrint("Unknown object type: " + obj.getClass().getName());
			}
		}
		aPC.setGold(aPC.getGold().subtract(goldSpent).toString());
		aPC.addKit(this);
	}

	/**
	 * itemname[PRE1|PRE2|...|PREn]
	 * @param aString String
	 * @return String
	 */
	private String itemPassesPrereqs(String aString)
	{
		final int idxStart = aString.indexOf('[');
		if ((idxStart < 0) || !aString.endsWith("]"))
		{
			return aString;
		}

		final String itemName = aString.substring(0, idxStart);
		aString = aString.substring(idxStart + 1, aString.length() - 1);
		List prereqList = pcgen.core.utils.Utility.split(aString, '|');

		if (passesPreReqTestsForList(Globals.getCurrentPC(), this, prereqList))
		{
			return itemName;
		}
		return null;
	}

	/**
	 * Get the 1st class in the kit the qualified it.
	 * @param spells boolean
	 * @return PCClass
	 */
	private PCClass getPCClass(boolean spells)
	{
		final List prereqList = getPreReqList();
		if (prereqList != null)
		{
			List preR = new ArrayList();
			for (int i = 0, x = prereqList.size(); i < x; ++i)
			{
				String prereq = (String) prereqList.get(i);
				if (prereq.startsWith("PRECLASS:"))
				{
					final int j = prereq.lastIndexOf('=');
					if (j > 0)
					{
						final String reqLevel = prereq.substring(j);
						final StringTokenizer aTok = new StringTokenizer(prereq.substring(9, j), ",", false);
						while (aTok.hasMoreTokens())
						{
							final String className = aTok.nextToken();
							//
							// Character must have levels in class
							//
							PCClass aClass = aPC.getClassNamed(className);
							if (aClass != null)
							{
								//
								// Must be able to cast spells
								//
								final String baseStat = aClass.getSpellBaseStat();
								if (!spells || ((baseStat != null) && (baseStat.length() > 0) && !baseStat.equalsIgnoreCase(Constants.s_NONE)))
								{
									preR.clear();
									preR.add("PRECLASS:" + className + reqLevel);
									if (passesPreReqTestsForList(aPC, this, preR))
									{
										return aClass;
									}
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Method getCostFromFormula
	 * @param formula String
	 * @return String
	 */
	private String getCostFromFormula(String formula)
	{
		final StringTokenizer aTok = new StringTokenizer(formula, "[]", true);
		String costFormula = null;
		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();
			if ("]".equals(tok))
			{
				costFormula = null;
			}
			else
			{
				if (costFormula == null)
				{
					costFormula = tok;
				}
				else
				{
					List al = new ArrayList();
					al.add(tok);
					if (!passesPreReqTestsForList(aPC, null, al))
					{
						costFormula = null;
						continue;
					}
					break;
				}
			}
		}
		return costFormula;
	}

	/**
	 * Method updatePCSpells
	 * @param obj Spell
	 * @param pcClass PCClass
	 */
	private void updatePCSpells(Spell obj, PCClass pcClass)
	{
		final int spLevel = obj.getFirstLevelForKey(pcClass.getSpellKey());
		if (spLevel < 0)
		{
			Logging.errorPrint("SPELLS: " + pcClass.getName() + " cannot cast spell \"" + obj.getName() + "\"");
			return;
		}
		CharacterSpell cs = new CharacterSpell(pcClass, obj);
		cs.addInfo(spLevel, 1, null);
		final String aString = aPC.addSpell(cs, null, pcClass.getName(), Globals.getDefaultSpellBook(), spLevel, spLevel);
		if (aString.length() != 0)
		{
			return;
		}
		PCGen_Frame1.forceUpdate_InfoSpells();
	}

	/**
	 * Method updatePCSkills
	 * @param obj KitSkillAdd
	 * @param pcClass PCClass
	 */
	private void updatePCSkills(KitSkillAdd obj, PCClass pcClass)
	{
		Skill aSkill = aPC.addSkill(obj.getSkill());

		final String aString = aSkill.modRanks(obj.getDelta(), pcClass);

		if (aString.length() > 0)
		{
			Logging.errorPrint("SKILL: " + aString);
		}
		PCGen_Frame1.forceUpdate_InfoSkills();
	}

}
