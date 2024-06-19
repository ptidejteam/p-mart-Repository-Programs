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
	private ArrayList gearList = null;
	private ArrayList featList = null;
	private ArrayList spellsList = null;
	private ArrayList skillList = null;
	private ArrayList profList = null;
	private KitSchool school = null;
	private PlayerCharacter aPC = null;

	public Kit(String argRegion)
	{
		super();
		region = argRegion;
	}

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

	public String getRegion()
	{
		return region;
	}

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

	public String getType()
	{
		setMyType();
		return super.getType();
	}

	public String getMyType(int i)
	{
		setMyType();
		return super.getMyType(i);
	}

	public int getMyTypeCount()
	{
		setMyType();
		return super.getMyTypeCount();
	}

	public boolean isType(String aType)
	{
		setMyType();
		return super.isType(aType);
	}

	public void setSellRate(String argRate)
	{
		sellRate = argRate;
	}

	private String getBuyRate()
	{
		return buyRate;
	}

	public void setBuyRate(String argRate)
	{
		buyRate = argRate;
	}

	public ArrayList getFeats()
	{
		return featList;
	}

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

	public ArrayList getSkill()
	{
		return skillList;
	}

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

	public ArrayList getGear()
	{
		return gearList;
	}

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

	public ArrayList getSpells()
	{
		return spellsList;
	}

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

	public ArrayList getProfs()
	{
		return profList;
	}

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

	public void setSchool(KitSchool kSchool)
	{
		school = kSchool;
	}

	//  Add the skills
	public void addKitSkills(ArrayList thingsToAdd, ArrayList warnings)
	{
		ArrayList aList = getSkill();
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
			ArrayList skillList = new ArrayList();
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
					ArrayList skillsOfType = new ArrayList();
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
				skillList.add(sta);
			}
			int skillPool = pcClass.skillPool().intValue();
			if ((skillList.size() == 0) || (skillPool == 0))
			{
				return;
			}

			//
			// Add 1 point to each skill until we run out of skill points for this class or
			// all the skills are at their max.
			//
			int skillsUsed = 0;
			final int x = skillList.size();
			boolean bModified;
			while (skillsUsed < skillPool)
			{
				bModified = false;
				for (int i = 0; i < x; ++i)
				{
					final KitSkillAdd sta = (KitSkillAdd) skillList.get(i);
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
				final KitSkillAdd sta = (KitSkillAdd) skillList.get(i);
				if (sta.wasModified())
				{
					KitWrapper tta = new KitWrapper(sta, false);
					tta.setPObject(pcClass);
					thingsToAdd.add(tta);
				}
			}
		}
	}

	//
	// Add the Feats
	//
	public void addKitFeats(ArrayList thingsToAdd, ArrayList warnings)
	{
		ArrayList aList = getFeats();
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
				ArrayList featList = new ArrayList();
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
						featList.add(featName);
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
				if (choiceCount > featList.size())
				{
					choiceCount = featList.size();
				}

				//
				// Don't allow choosing of more than allotted number of feats
				//
				if (choiceCount > (aPC.getFeats() - featsChosen))
				{
					choiceCount = aPC.getFeats() - featsChosen;
					tooManyFeats = true;
				}
				if (choiceCount == 0)
				{
					continue;
				}

				ArrayList xs;
				if (choiceCount == featList.size())
				{
					xs = featList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose feat(s)", featList, new ArrayList(), choiceCount);
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

	//
	// Add the proficiencies
	//
	public void addKitProfs(ArrayList thingsToAdd, ArrayList warnings)
	{
		aPC = Globals.getCurrentPC();
		ArrayList aList = getProfs();

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
				ArrayList bonusList;
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
					bonusList = pcRace.getWeaponProfBonus();
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
					bonusList = pcClass.getWeaponProfBonus();
				}
				if ((bonusList == null) || (bonusList.size() == 0))
				{
					warnings.add("PROF: No optional weapon proficiencies");
					continue;
				}

				ArrayList profList = new ArrayList();
				for (Iterator e = kProf.getProfList().iterator(); e.hasNext();)
				{
					final String profName = itemPassesPrereqs((String) e.next());
					if (profName == null)
					{
						continue;
					}

					if (!bonusList.contains(profName))
					{
						warnings.add("PROF: Weapon proficiency \"" + profName + "\" is not in list of choices");
					}

					final WeaponProf aProf = Globals.getWeaponProfNamed(profName);
					if (aProf != null)
					{
						profList.add(profName);
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
				if (choiceCount > profList.size())
				{
					choiceCount = profList.size();
				}

				if (choiceCount == 0)
				{
					continue;
				}

				ArrayList xs;
				if (choiceCount == profList.size())
				{
					xs = profList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose Proficiencies", profList, new ArrayList(), choiceCount);
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

	//
	// Add the equipment
	//
	public void addKitGear(ArrayList thingsToAdd, ArrayList warnings)
	{
		aPC = Globals.getCurrentPC();
		boolean outOfFunds = false;
		ArrayList aList = getGear();
		if ((aList != null) && (aList.size() != 0))
		{
			//
			// get the purchase rate
			//
			int buyRate = SettingsHandler.getInventoryTab_BuyRate();
			final String purchaseFormula = getBuyRate();
			if ((purchaseFormula != null) && (purchaseFormula.length() != 0))
			{
				String costFormula = getCostFromFormula(purchaseFormula);
				if (costFormula != null)
				{
					buyRate = aPC.getVariableValue(costFormula, "").intValue();
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
					ArrayList eqList = Globals.getEquipmentOfType(Globals.getEquipmentList(), eqName.substring(5), "");
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
				final ArrayList eqMods = kGear.getEqMods();
				final ArrayList eqTypeList = eq.typeList();
				eq = (Equipment) eq.clone();

				//
				// Resize item for character--never resize weapons or ammo
				//
				boolean tryResize = false;
				if (!eq.isWeapon() && !eq.isAmmunition())
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
						final EquipmentModifier eqMod = Globals.getQualifiedModifierNamed(eqModName, eqTypeList);
						if (eqMod == null)
						{
							warnings.add("GEAR: " + eqName + ": non-existant equipment modifier \"" + eqModName + "\"");
						}
						else
						{
							eq.addEqModifier(eqMod, true);		// stick on primary head
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
				if (buyRate != 0)
				{
					final BigDecimal bdBuyRate = new BigDecimal(Integer.toString(buyRate)).multiply(new BigDecimal("0.01"));
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

	//
	// Add the spells
	//
	public void addKitSpells(ArrayList thingsToAdd, ArrayList warnings)
	{
		aPC = Globals.getCurrentPC();
		ArrayList aList = getSpells();
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

				ArrayList spellList = new ArrayList();
				for (Iterator e = kSpells.getSpellList().iterator(); e.hasNext();)
				{
					final String spellName = itemPassesPrereqs((String) e.next());
					if (spellName == null)
					{
						continue;
					}

					if (spellName.startsWith("LEVEL="))
					{
						spellList = Globals.getSpellsIn(Integer.parseInt(spellName.substring(6)), pcClass.getName(), "");
					}
					else
					{
						final Spell aSpell = Globals.getSpellNamed(spellName);
						if (aSpell != null)
						{
							spellList.add(aSpell);
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
					choiceCount = spellList.size();
				}
				else
				{
					choiceCount = aPC.getVariableValue(choiceFormula, "").intValue();
				}

				//
				// Can't choose more entries than there are...
				//
				if (choiceCount > spellList.size())
				{
					choiceCount = spellList.size();
				}

				if (choiceCount == 0)
				{
					continue;
				}

				ArrayList xs;
				if (choiceCount == spellList.size())
				{
					xs = spellList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					for (; ;)
					{
						xs = Globals.getChoiceFromList("Choose spell(s)", spellList, new ArrayList(), choiceCount);
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

	public void processKit(ArrayList thingsToAdd)
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
				goldSpent = goldSpent.add(tta.getCost());
				PCGen_Frame1.forceUpdate_InfoGear();
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
				Globals.errorPrint("Unknown object type: " + obj.getClass().getName());
			}
		}
		aPC.setGold(aPC.getGold().subtract(goldSpent).toString());
		aPC.addKit(this);
	}

	//
	// itemname[PRE1|PRE2|...|PREn]
	//
	private String itemPassesPrereqs(String aString)
	{
		final int idxStart = aString.indexOf('[');
		if ((idxStart < 0) || !aString.endsWith("]"))
		{
			return aString;
		}

		final String itemName = aString.substring(0, idxStart);
		aString = aString.substring(idxStart + 1, aString.length() - 1);
		ArrayList prereqList = pcgen.core.Utility.split(aString, '|');

		if (passesPreReqTestsForList(Globals.getCurrentPC(), this, prereqList))
		{
			return itemName;
		}
		return null;
	}

	//
	// Get the 1st class in the kit the qualified it
	//
	private PCClass getPCClass(boolean spells)
	{
		final ArrayList prereqList = getPreReqList();
		if (prereqList != null)
		{
			ArrayList preR = new ArrayList();
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

	private String getCostFromFormula(String formula)
	{
		final StringTokenizer aTok = new StringTokenizer(formula, "[]", true);
		String costFormula = null;
		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();
			if ("[".equals(tok))
			{
				//TODO What is this?
			}
			else if ("]".equals(tok))
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
					ArrayList al = new ArrayList();
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

	private void updatePCSpells(Spell obj, PCClass pcClass)
	{
		final int spLevel = obj.getFirstLevelForKey(pcClass.getSpellKey());
		if (spLevel < 0)
		{
			Globals.errorPrint("SPELLS: " + pcClass.getName() + " cannot cast spell \"" + obj.getName() + "\"");
			return;
		}
		CharacterSpell cs = new CharacterSpell(pcClass, obj);
		cs.addInfo(spLevel, 1, null);
		final String aString = aPC.addSpell(cs, null, pcClass.getName(), Globals.getDefaultSpellBook(), spLevel, spLevel);
		if (aString.length() != 0)
		{
			//System.err.println("SPELLS: " + aString);
			return;
		}
		PCGen_Frame1.forceUpdate_InfoSpells();
	}

	private void updatePCSkills(KitSkillAdd obj, PCClass pcClass)
	{
		Skill aSkill = aPC.addSkill(obj.getSkill());

		final String aString = aSkill.modRanks(obj.getDelta(), pcClass);

		if (aString.length() > 0)
		{
			Globals.errorPrint("SKILL: " + aString);
		}
		PCGen_Frame1.forceUpdate_InfoSkills();
	}

}
