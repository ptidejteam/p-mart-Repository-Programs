/*
 * @(#) $Id: EquipmentModifier.java,v 1.1 2006/02/21 00:02:22 vauchers Exp $
 *
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
 * Created on November 19, 2001, 4:28 PM
 */
package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.spell.Spell;
import pcgen.gui.Chooser;
import pcgen.util.Delta;

/**
 * Definition and games rules for an equipment modifier.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public class EquipmentModifier extends PObject
{
	private ArrayList specialProperties = new ArrayList();
	private ArrayList itemType = new ArrayList();
	private ArrayList replaces = new ArrayList();
	private ArrayList ignores = new ArrayList();
	private String armorType = "";
	private String cost = "0";
	private String preCost = "0";
	private String proficiency = "";
	private int plus = 0;
	private int equipmentVisible = VISIBLE_YES;
	private boolean assignToAll = false;
	private int namingOption = NAMINGOPTION_NORMAL;
	private int minCharges = 0;
	private int maxCharges = 0;

	private int costDouble = -1;

	protected static final int VISIBLE_NO = 0;
	protected static final int VISIBLE_YES = 1;
	protected static final int VISIBLE_QUALIFIED = 2;

	private static final int NAMINGOPTION_NORMAL = 0;
	private static final int NAMINGOPTION_NONAME = 1;
	private static final int NAMINGOPTION_NOLIST = 2;
	private static final int NAMINGOPTION_NOTHING = 3;
	private static final int NAMINGOPTION_SPELL = 4;

	private String s_CHARGES = "CHARGES";


	public int getMinCharges()
	{
		return minCharges;
	}

	public int getMaxCharges()
	{
		return maxCharges;
	}

	public int getSR()
	{
		if (SR == null)
			return 0;
		if (SR.equals("%CHOICE") && getAssociatedCount() > 0)
			return Delta.parseInt(associatedList.get(0).toString());
		return super.getSR();
	}

	public String getSpellName()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellName(getAssociated(0));
		}
		return "";
	}

	public String getSpellCaster()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellCaster(getAssociated(0));
		}
		return "";
	}

	public int getSpellLevel()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellLevel(getAssociated(0));
		}
		return -1;
	}

	public int getSpellCasterLevel()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellCasterLevel(getAssociated(0));
		}
		return 9999;
	}

	public int getUsedCharges()
	{
		return maxCharges - getRemainingCharges();
	}

	public int getRemainingCharges()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellCharges(getAssociated(0));
		}
		return 0;
	}

	public void setRemainingCharges(int remainingCharges)
	{
		if (getAssociatedCount() > 0)
		{
			String listEntry = getAssociated(0);
			String chargeInfo = getSpellInfoString(listEntry, s_CHARGES);
			if (chargeInfo.length() != 0)
			{
				chargeInfo = s_CHARGES + "[" + chargeInfo + "]";
				final int idx = listEntry.indexOf(chargeInfo);
				listEntry = listEntry.substring(0, idx) + listEntry.substring(idx + chargeInfo.length());
				listEntry = listEntry + s_CHARGES + "[" + Integer.toString(remainingCharges) + "]";
				setAssociated(0, listEntry);
			}
		}
	}

	public void setChargeInfo(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		try
		{
			minCharges = Integer.parseInt(aTok.nextToken());
			if (minCharges < 0)
			{
				minCharges = 0;
			}
			maxCharges = minCharges;
			if (aTok.hasMoreTokens())
			{
				maxCharges = Integer.parseInt(aTok.nextToken());
			}
			if (maxCharges < minCharges)
			{
				maxCharges = minCharges;
			}
		}
		catch (NumberFormatException exc)
		{
			Globals.errorPrint("Invalid " + s_CHARGES + " tag value: " + aString);
		}
	}

	public void setSpellInfo(PObject spellCastingClass, Spell theSpell, String spellVariant, String spellType, int spellLevel, int spellCasterLevel, Object[] spellMetamagicFeats, int charges)
	{
		StringBuffer spellInfo = new StringBuffer(100);
		spellInfo.append("SPELLNAME[").append(theSpell.getName()).append(']');
		spellInfo.append("CASTER[").append(spellCastingClass.getName()).append(']');
		if (spellVariant.length() != 0)
		{
			spellInfo.append("VARIANT[").append(spellVariant).append(']');
		}
		spellInfo.append("SPELLTYPE[").append(spellType).append(']');
		spellInfo.append("SPELLLEVEL[").append(spellLevel).append(']');
		spellInfo.append("CASTERLEVEL[").append(spellCasterLevel).append(']');
		if (charges > 0)
		{
			spellInfo.append(s_CHARGES).append('[').append(charges).append(']');
		}

		if ((spellMetamagicFeats != null) && (spellMetamagicFeats.length > 0))
		{
			spellInfo.append("METAFEATS[");
			for (int i = 0; i < spellMetamagicFeats.length; i++)
			{
				final Feat aFeat = (Feat)spellMetamagicFeats[i];
				if (i != 0)
				{
					spellInfo.append(",");
				}
				spellInfo.append(aFeat.getKeyName());
			}
			spellInfo.append(']');
		}
		addAssociated(spellInfo.toString());
	}

	private int getSpellLevel(String listEntry)
	{
		return getSpellInfo(listEntry, "SPELLLEVEL");
	}

	private int getSpellCharges(String listEntry)
	{
		return getSpellInfo(listEntry, s_CHARGES);
	}

	private int getSpellCasterLevel(String listEntry)
	{
		return getSpellInfo(listEntry, "CASTERLEVEL");
	}
/* JK 2002-07-24 This code is unused. Commenting it out. Will remove next time I come across it.
	private String getSpellType(String listEntry)
	{
		return getSpellInfoString(listEntry, "SPELLTYPE");
	}
	*/

	private String getSpellName(String listEntry)
	{
		return getSpellInfoString(listEntry, "SPELLNAME");
	}

	private String getSpellVariant(String listEntry)
	{
		return getSpellInfoString(listEntry, "VARIANT");
	}

	private String getSpellCaster(String listEntry)
	{
		return getSpellInfoString(listEntry, "CASTER");
	}

	private ArrayList getSpellMetafeats(String listEntry)
	{
		final String metaFeat = getSpellInfoString(listEntry, "METAFEATS");
		return Utility.split(metaFeat, ',');
	}

	private int getSpellInfo(String listEntry, String desiredInfo)
	{
		int modValue = 0;
		final String info = getSpellInfoString(listEntry, desiredInfo);
		if (info.length() > 0)
		{
			try
			{
				modValue = Delta.parseInt(info);
			}
			catch (NumberFormatException exc)
			{
			}
		}
		return modValue;
	}

	private String getSpellInfoString(String listEntry, String desiredInfo)
	{
		final int offs = listEntry.indexOf(desiredInfo + "[");
		final int offs2 = listEntry.indexOf(']', offs + 1);
		if ((offs >= 0) && (offs2 > offs))
		{
			return listEntry.substring(offs + desiredInfo.length() + 1, offs2);
		}
		return "";
	}


	/**
	 * Clone an EquipmentModifier
	 * @return a clone of the EquipmentModifier
	 */
	public Object clone()
	{
		EquipmentModifier aObj = (EquipmentModifier)super.clone();
		aObj.itemType = (ArrayList)itemType.clone();
		aObj.specialProperties = (ArrayList)specialProperties.clone();
		aObj.replaces = (ArrayList)replaces.clone();
		aObj.ignores = (ArrayList)ignores.clone();
		aObj.armorType = armorType;
		aObj.cost = cost;
		aObj.preCost = preCost;
		aObj.proficiency = proficiency;
		aObj.equipmentVisible = equipmentVisible;
		aObj.plus = plus;
		aObj.assignToAll = assignToAll;
		return aObj;
	}

	/**
	 * Return a string representation of the EquipmentModifier
	 * @return a String representation of the EquipmentModifier
	 */
	public String toString()
	{
		if (namingOption == NAMINGOPTION_NOTHING)
		{
			return "";
		}

		StringBuffer aString = new StringBuffer(getName().length());
		if (namingOption == NAMINGOPTION_SPELL)
		{
			if (getAssociatedCount() > 0)
			{
				final String listEntry = getAssociated(0);

				ArrayList metaFeats = getSpellMetafeats(listEntry);
				aString.append(getSpellName(listEntry));
				String info = getSpellVariant(listEntry);
				if (info.length() != 0)
				{
					aString.append(" (").append(info).append(')');
				}
				if (metaFeats.size() != 0)
				{
					aString.append('/').append(Utility.unSplit(metaFeats, "/"));
				}
				aString.append('/').append(getSpellCaster(listEntry));
				aString.append('/').append(Utility.ordinal(getSpellCasterLevel(listEntry)));
			}
		}
		else
		{
			if (namingOption != NAMINGOPTION_NONAME)
			{
				aString.append(getName());
			}
			if ((namingOption != NAMINGOPTION_NOLIST) && (getAssociatedCount() > 0))
			{
				if (namingOption != NAMINGOPTION_NONAME)
				{
					aString.append(" (");
				}
				boolean bFirst = true;
				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (!bFirst)
					{
						aString.append(", ");
					}
					aString.append(getAssociated(e));
					bFirst = false;
				}
				if (namingOption != NAMINGOPTION_NONAME)
				{
					aString.append(")");
				}
			}
		}
		return aString.toString().trim().replace('|', ' ');
	}

	public void setIgnores(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ",", false);
		ignores.clear();
		while (aTok.hasMoreTokens())
		{
			final String aReplace = aTok.nextToken();
			if (!ignores.contains(aReplace))
			{
				ignores.add(aReplace);
			}
		}
	}


	public boolean willIgnore(String aString)
	{
		return ignores.contains(aString.toUpperCase().trim());
	}

	public void setReplacement(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ",", false);
		replaces.clear();
		while (aTok.hasMoreTokens())
		{
			final String aReplace = aTok.nextToken();
			if (!replaces.contains(aReplace))
			{
				replaces.add(aReplace);
			}
		}
	}

	public boolean willReplace(String aString)
	{
		return replaces.contains(aString.toUpperCase().trim());
	}

	public void setItemType(String aString)
	{
		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		itemType.clear();
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if (!itemType.contains(aType))
			{
				itemType.add(aType);
			}
		}
	}

	public void setArmorType(String aString)
	{
		armorType = aString.toUpperCase().trim();
	}

	public String replaceArmorType(ArrayList aTypes)
	{
		final StringTokenizer aTok = new StringTokenizer(armorType, "|", false);
		if (aTok.hasMoreTokens())
		{
			final int idx = aTypes.indexOf(aTok.nextToken());
			if (idx >= 0)
			{
				if (aTok.hasMoreTokens())
				{
					final String newArmorType = aTok.nextToken();
					aTypes.set(idx, newArmorType);
					return newArmorType;
				}
				else
				{
					aTypes.remove(idx);
				}
			}
		}
		return null;
	}

	public ArrayList getItemType()
	{
		return itemType;
	}

	public void setVisible(String aString)
	{
		if (aString.startsWith("Y"))
			equipmentVisible = VISIBLE_YES;
		else if (aString.startsWith("Q"))
			equipmentVisible = VISIBLE_QUALIFIED;
		else
			equipmentVisible = VISIBLE_NO;
	}

	public void setAssignment(String aString)
	{
		assignToAll = aString.startsWith("Y");
	}

	public boolean getAssignToAll()
	{
		return assignToAll;
	}

	public int getVisible()
	{
		return equipmentVisible;
	}

	public String getCost()
	{
		return cost;
	}

	public String getCost(int eqIdx)
	{
		String listEntry = getAssociated(eqIdx);
		String costFormula = cost;
		String modChoice;
		int idx;
		for (modChoice = ""; ;)
		{
			idx = costFormula.indexOf("%SPELLLEVEL");
			if (idx < 0)
			{
				break;
			}
			if (modChoice.length() == 0)
			{
				final int iLevel = getSpellLevel(listEntry);
				if (iLevel == 0)
				{
					modChoice = "0.5";
				}
				else
				{
					modChoice = Integer.toString(iLevel);
				}
			}
			costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 11);
		}

		for (modChoice = ""; ;)
		{
			idx = costFormula.indexOf("%CASTERLEVEL");
			if (idx < 0)
			{
				break;
			}
			if (modChoice.length() == 0)
			{
				modChoice = Integer.toString(getSpellCasterLevel(listEntry));
			}
			costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 12);
		}

		for (modChoice = ""; ;)
		{
			idx = costFormula.indexOf("%" + s_CHARGES);
			if (idx < 0)
			{
				break;
			}
			if (modChoice.length() == 0)
			{
				modChoice = Integer.toString(getSpellCharges(listEntry));
			}
			costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 8);
		}

		for (modChoice = ""; ;)
		{
			idx = costFormula.indexOf("%SPELLCOST");
			if (idx < 0)
			{
				break;
			}
			if (modChoice.length() == 0)
			{
				final String spellName = getSpellName(listEntry);
				Spell aSpell = Globals.getSpellNamed(spellName);
				if (aSpell != null)
				{
					modChoice = aSpell.getCost().add(new BigDecimal(Integer.toString(aSpell.getXPCost() * 5))).toString();
				}
			}
			costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 10);
		}

		for (modChoice = ""; ;)
		{
			idx = costFormula.indexOf("%CHOICE");
			if (idx < 0)
			{
				break;
			}
			if (modChoice.length() == 0)
			{
				final int offs = listEntry.lastIndexOf('|');
				int modValue = 0;
				try
				{
					modValue = Delta.parseInt(listEntry.substring(offs + 1));
				}
				catch (NumberFormatException exc)
				{
				}
				modChoice = Integer.toString(modValue);
			}
			costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 7);
		}
		return costFormula;
	}

	public void setCost(String aString)
	{
		cost = aString;
	}

	public String getPreCost()
	{
		return preCost;
	}

	public void setPreCost(String aString)
	{
		preCost = aString;
	}

	public void setPlus(String aString)
	{
		try
		{
			plus = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			// Ignore
		}
	}

	public int getPlus()
	{
		return plus;
	}

	public void setNamingOption(String namingOption)
	{
		if (namingOption.equalsIgnoreCase("NOLIST"))
		{
			this.namingOption = NAMINGOPTION_NOLIST;
		}
		else if (namingOption.equalsIgnoreCase("NONAME"))
		{
			this.namingOption = NAMINGOPTION_NONAME;
		}
		else if (namingOption.equalsIgnoreCase("NOTHING"))
		{
			this.namingOption = NAMINGOPTION_NOTHING;
		}
		else if (namingOption.equalsIgnoreCase("SPELL"))
		{
			this.namingOption = NAMINGOPTION_SPELL;
		}
		else
		{
			this.namingOption = NAMINGOPTION_NORMAL;
		}
	}

	public void setCostDouble(boolean costDoubles)
	{
		costDouble = costDoubles ? 1 : 0;
	}

	public boolean getCostDouble()
	{
		//
		// Uninitialized?
		//
		if (costDouble < 0)
		{
			if (isType("MagicalEnhancement") || isType("BaseMaterial"))
			{
				return false;
			}
			if (itemType.contains("MAGIC"))
			{
				return true;
			}
			for (int i = 0; i < getPreReqCount(); i++)
			{
				final String preReq = getPreReq(i);
				if (preReq.startsWith("PRETYPE:") && (preReq.indexOf("EQMODTYPE=MagicalEnhancement") >= 0))
				{
					return true;
				}
			}
		}
		return costDouble == 1;
	}

	public ArrayList getSpecialProperties()
	{
		return specialProperties;
	}

	public void setSpecialProperties(String aAbilities)
	{
		final StringTokenizer aTok = new StringTokenizer(aAbilities, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (!specialProperties.contains(aString))
			{
				specialProperties.add(aString);
			}
		}
	}


	public int getChoice(int pool, Equipment parent, boolean bAdd)
	{
		if (choiceString.length() == 0)
		{
			return 1;
		}

		if (bAdd && choiceString.startsWith("EQBUILDER."))
		{
			return 1;
		}

		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);

		ArrayList availableList = new ArrayList();	// available list of choices
		ArrayList selectedList = new ArrayList();		// selected list of choices
		String choiceType = aTok.nextToken();

		final Chooser chooser = new Chooser();
		chooser.setPoolFlag(false);
		chooser.setVisible(false);
		chooser.setPool(pool);
		selectedList = new ArrayList();
		addAssociatedTo(selectedList);

		boolean allowDuplicates = false;
		boolean noSign = false;

		int minValue = 0;
		int maxValue = 0;
		int incValue = 1;
		int maxSelect = 0;

		if (choiceType.startsWith("COUNT="))
		{
			try
			{
				maxSelect = Integer.parseInt(choiceType.substring(6));
			}
			catch (NumberFormatException e)
			{
			}
			choiceType = aTok.nextToken();
		}

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (maxSelect > 0)
			{
				if (pool > 0)
				{
					chooser.setPool(maxSelect - selectedList.size());
				}
			}
			if (aString.startsWith("TYPE="))
			{
				if ((pool > 0) && (maxSelect == 0))
				{
					chooser.setPool(pool - selectedList.size());
				}
				aString = aString.substring(5);
				if (aString.startsWith("LASTCHOICE"))
				{
					for (Iterator e = parent.getEqModifierList(true).iterator(); e.hasNext();)
					{
						final EquipmentModifier sibling = (EquipmentModifier)e.next();
						if ((sibling != this) && sibling.getChoiceString().startsWith(choiceType))
						{
							sibling.addAssociatedTo(availableList);
						}
					}
				}
				else if (choiceType.equalsIgnoreCase("SKILL"))
				{
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						final Skill aSkill = (Skill)e.next();
						if (aSkill.isType(aString) && !availableList.contains(aSkill.getName()))
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				else if (choiceType.equalsIgnoreCase("EQUIPMENT"))
				{
					for (Iterator e = Globals.getEquipmentList().iterator(); e.hasNext();)
					{
						final Equipment aEquip = (Equipment)e.next();
						if (aEquip.isType(aString) && !availableList.contains(aEquip.getName()))
						{
							availableList.add(aEquip.getName());
						}
					}
				}
				else if (aString.equalsIgnoreCase("ALL"))
				{
					availableList.addAll(Equipment.getEquipmentTypes());
					chooser.setPool(Equipment.getEquipmentTypes().size() - selectedList.size());
					bAdd = true;
				}
			}
			else if (aString.equals("STAT"))
			{
				for (int x = 0; x < Globals.s_ATTRIBSHORT.length; x++)
				{
					availableList.add(Globals.s_ATTRIBSHORT[x]);
				}
			}
			else if (aString.equals("SKILL"))
			{
				for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
				{
					final Skill aSkill = (Skill)e.next();
					availableList.add(aSkill.getName());
				}
			}
			else if (aString.equals("MULTIPLE"))
			{
				allowDuplicates = true;
			}
			else if (aString.equals("NOSIGN"))
			{
				noSign = true;
			}
			else if (aString.startsWith("MIN="))
			{
				try
				{
					minValue = Delta.parseInt(aString.substring(4));
				}
				catch (NumberFormatException e)
				{
				}
			}
			else if (aString.startsWith("MAX="))
			{
				try
				{
					maxValue = Delta.parseInt(aString.substring(4));
				}
				catch (NumberFormatException e)
				{
				}
			}
			else if (aString.startsWith("INCREMENT="))
			{
				try
				{
					incValue = Delta.parseInt(aString.substring(10));
					if (incValue < 1)
					{
						incValue = 1;
					}
				}
				catch (NumberFormatException e)
				{
				}
			}

			else
			{
				if (!availableList.contains(aString))
				{
					availableList.add(aString);
				}
			}
		}

		if ((availableList.size() == 0) && (minValue < maxValue))
		{
			for (int j = minValue; j <= maxValue; j += incValue)
			{
				if (j != 0)
				{
					if (noSign && (j > 0))
					{
						availableList.add(Integer.toString(j));
					}
					else
					{
						availableList.add(Delta.toString(j));
					}
				}
			}
			minValue = maxValue;
		}

		if (!bAdd)
		{
			chooser.setPool(0);
		}
		chooser.setAllowsDups(allowDuplicates);
		chooser.setSelectedListTerminator("|");
		chooser.setTitle("Select " + choiceType + " (" + getName() + ")");
		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);
		chooser.show();


		clearAssociated();
		selectedList = chooser.getSelectedList();
		for (int i = 0; i < selectedList.size(); i++)
		{
			String aString = (String)selectedList.get(i);
			if (minValue < maxValue)
			{
				int idx = aString.indexOf('|');
				if (idx < 0)
				{
					ArrayList secondaryChoice = new ArrayList();
					for (int j = minValue; j <= maxValue; j += incValue)
					{
						if (j != 0)
						{
							secondaryChoice.add(Delta.toString(j));
						}
					}

					chooser.setTitle("Select modifier (" + aString + ")");
					chooser.setAvailableList(secondaryChoice);
					chooser.setSelectedList(new ArrayList());
					chooser.setPool(1);
					chooser.show();
					if (chooser.getSelectedList().size() == 0)
					{
						continue;
					}
					aString = aString + "|" + (String)chooser.getSelectedList().get(0);
				}
			}

			if (allowDuplicates || !containsAssociated(aString))
			{
				addAssociated(aString);
			}
		}
		return getAssociatedCount();
	}

	public void setProficiency(String prof)
	{
		proficiency = prof;
	}

	public String getProficiency()
	{
		return proficiency;
	}

	public ArrayList getBonusList()
	{
		ArrayList myBonusList = new ArrayList(super.getBonusList());
		for (int i = myBonusList.size() - 1; i > -1; i--)
		{
			String aString = (String)myBonusList.get(i);
			final int idx = aString.indexOf("%CHOICE");
			if (idx >= 0)
			{
				//
				// Add an entry for each of the associated list entries
				//
				for (int j = 0; j < getAssociatedCount(); j++)
				{
					final String aBonus = aString.substring(0, idx) + getAssociated(j) + aString.substring(idx + 7);
					myBonusList.add(aBonus);
				}
				myBonusList.remove(i);
			}
		}

		return myBonusList;
	}

	public String getBonusListString()
	{
		String s = getBonusList().toString();

		if (s.equals("[]"))
		{
			return "";
		}
		// Don't display the surrounding brackets.
		else
		{
			return s.substring(1, s.length() - 1);
		}
	}

	public int bonusTo(String aType, String aName, Object obj)
	{
		return super.bonusTo(aType, aName, obj, getBonusList());
	}
}
