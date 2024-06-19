/*
 * EquipmentModifier.java
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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:39 $
 *
 */

package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Definition and games rules for an equipment modifier.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class EquipmentModifier extends PObject
{
	private List specialProperties = new ArrayList();
	private List itemType = new ArrayList();
	private List replaces = new ArrayList();
	private List ignores = new ArrayList();
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

	private static final String s_CHARGES = "CHARGES";

	private List vFeatList = null;		// virtual feat list

	public int getMinCharges()
	{
		return minCharges;
	}

	public int getMaxCharges()
	{
		return maxCharges;
	}

	protected int getSR()
	{
		if (SR == null)
		{
			return 0;
		}
		if ("%CHOICE".equals(SR) && (getAssociatedCount() > 0))
		{
			return Delta.parseInt(associatedList.get(0).toString());
		}
		return super.getSR();
	}

	int getUsedCharges()
	{
		return maxCharges - getRemainingCharges();
	}

	int getRemainingCharges()
	{
		if (getAssociatedCount() > 0)
		{
			return getSpellCharges(getAssociated(0));
		}
		return 0;
	}

	void setRemainingCharges(int remainingCharges)
	{
		if (getAssociatedCount() > 0)
		{
			String listEntry = getAssociated(0);
			String chargeInfo = getSpellInfoString(listEntry, s_CHARGES);
			if (chargeInfo.length() != 0)
			{
				chargeInfo = s_CHARGES + '[' + chargeInfo + ']';
				final int idx = listEntry.indexOf(chargeInfo);
				listEntry = listEntry.substring(0, idx) + listEntry.substring(idx + chargeInfo.length());
				listEntry += s_CHARGES + '[' + Integer.toString(remainingCharges) + ']';
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
			Logging.errorPrint("Invalid " + s_CHARGES + " tag value: " + aString);
		}
	}

	public void setSpellInfo(PObject spellCastingClass, Spell theSpell, String spellVariant, String spellType, int spellLevel, int spellCasterLevel, Object[] spellMetamagicFeats, int charges)
	{
		final StringBuffer spellInfo = new StringBuffer(100);
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
				final Feat aFeat = (Feat) spellMetamagicFeats[i];
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

	private static int getSpellLevel(String listEntry)
	{
		return getSpellInfo(listEntry, "SPELLLEVEL");
	}

	private int getSpellCharges(String listEntry)
	{
		return getSpellInfo(listEntry, s_CHARGES);
	}

	private static int getSpellCasterLevel(String listEntry)
	{
		return getSpellInfo(listEntry, "CASTERLEVEL");
	}

	private static String getSpellName(String listEntry)
	{
		return getSpellInfoString(listEntry, "SPELLNAME");
	}

	private static String getSpellVariant(String listEntry)
	{
		return getSpellInfoString(listEntry, "VARIANT");
	}

	private static String getSpellCaster(String listEntry)
	{
		return getSpellInfoString(listEntry, "CASTER");
	}

	private static List getSpellMetafeats(String listEntry)
	{
		final String metaFeat = getSpellInfoString(listEntry, "METAFEATS");
		return Utility.split(metaFeat, ',');
	}

	private static int getSpellInfo(String listEntry, String desiredInfo)
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
				//TODO: Should this really be ignored?
			}
		}
		return modValue;
	}

	private static String getSpellInfoString(String listEntry, String desiredInfo)
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
		EquipmentModifier aObj = null;
		try
		{
			aObj = (EquipmentModifier) super.clone();
			aObj.itemType = (List) ((ArrayList) itemType).clone();
			aObj.specialProperties = (List) ((ArrayList) specialProperties).clone();
			aObj.replaces = (List) ((ArrayList) replaces).clone();
			aObj.ignores = (List) ((ArrayList) ignores).clone();
			aObj.armorType = armorType;
			aObj.cost = cost;
			aObj.preCost = preCost;
			aObj.proficiency = proficiency;
			aObj.equipmentVisible = equipmentVisible;
			aObj.plus = plus;
			aObj.assignToAll = assignToAll;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
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

		final StringBuffer aString = new StringBuffer(getName().length());
		if (namingOption == NAMINGOPTION_SPELL)
		{
			if (getAssociatedCount() > 0)
			{
				final String listEntry = getAssociated(0);

				final List metaFeats = getSpellMetafeats(listEntry);
				String spellName = getSpellName(listEntry);
				if (SettingsHandler.guiUsesOutputName())
				{
					Spell aSpell = Globals.getSpellNamed(spellName);
					if (aSpell != null)
					{
						spellName = aSpell.getOutputName();
					}
				}
				aString.append(spellName);
				final String info = getSpellVariant(listEntry);
				if (info.length() != 0)
				{
					aString.append(" (").append(info).append(')');
				}
				if (metaFeats.size() != 0)
				{
					aString.append('/').append(Utility.join(metaFeats, "/"));
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
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ",");
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

	boolean willIgnore(String aString)
	{
		return ignores.contains(aString.toUpperCase().trim());
	}

	public void setReplacement(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ",");
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

	boolean willReplace(String aString)
	{
		return replaces.contains(aString.toUpperCase().trim());
	}

	public void setItemType(String aString)
	{
		final String typeString = aString.toUpperCase().trim();
		StringTokenizer aTok = new StringTokenizer(typeString, ".");
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

	String replaceArmorType(List aTypes)
	{
		StringTokenizer aTok = new StringTokenizer(armorType, "|");
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

	public List getItemType()
	{
		return itemType;
	}

	public void setVisible(String aString)
	{
		if (aString.length() > 0 && aString.charAt(0) == 'Y')
		{
			equipmentVisible = VISIBLE_YES;
		}
		else if (aString.length() > 0 && aString.charAt(0) == 'Q')
		{
			equipmentVisible = VISIBLE_QUALIFIED;
		}
		else
		{
			equipmentVisible = VISIBLE_NO;
		}
	}

	public void setAssignment(String aString)
	{
		assignToAll = aString.length() > 0 && aString.charAt(0) == 'Y';
	}

	public boolean getAssignToAll()
	{
		return assignToAll;
	}

	int getVisible()
	{
		return equipmentVisible;
	}

	public String getCost()
	{
		return cost;
	}

	String getCost(int eqIdx)
	{
		final String listEntry = getAssociated(eqIdx);
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
				final int iCasterLevel = getSpellCasterLevel(listEntry);
				modChoice = Integer.toString(iCasterLevel);

				//
				// Tack on the item creation multiplier, if there is one
				//
				final String castClassName = getSpellCaster(listEntry);
				if (castClassName.length() != 0)
				{
					final PCClass castClass = Globals.getClassNamed(castClassName);
					if (castClass != null)
					{
						StringBuffer multiple = new StringBuffer(200);
						String aString = castClass.getItemCreationMultiplier();
						if (aString.length() != 0)
						{
							StringTokenizer aTok = new StringTokenizer(aString, "+-*/()", true);
							//
							// This is to support older versions of the ITEMCREATE tag
							// that allowed 0.5, because it used to be just a multiple
							//
							if (aTok.countTokens() == 1)
							{
								multiple.append(iCasterLevel).append('*').append(aString);
							}
							else
							{
								while (aTok.hasMoreTokens())
								{
									aString = aTok.nextToken();
									if (aString.equals("CL"))
									{
										multiple.append(iCasterLevel);
									}
									else
									{
										multiple.append(aString);
									}
								}
							}
							modChoice = multiple.toString();
						}
					}
				}
			}
			costFormula = costFormula.substring(0, idx) + "(" + modChoice + ")" + costFormula.substring(idx + 12);
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
					//TODO: Should this really be ignored?
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

	public void setNamingOption(String argNamingOption)
	{
		if ("NOLIST".equalsIgnoreCase(argNamingOption))
		{
			namingOption = NAMINGOPTION_NOLIST;
		}
		else if ("NONAME".equalsIgnoreCase(argNamingOption))
		{
			namingOption = NAMINGOPTION_NONAME;
		}
		else if ("NOTHING".equalsIgnoreCase(argNamingOption))
		{
			namingOption = NAMINGOPTION_NOTHING;
		}
		else if ("SPELL".equalsIgnoreCase(argNamingOption))
		{
			namingOption = NAMINGOPTION_SPELL;
		}
		else
		{
			namingOption = NAMINGOPTION_NORMAL;
		}
	}

	public void setCostDouble(boolean costDoubles)
	{
		costDouble = costDoubles ? 1 : 0;
	}

	boolean getCostDouble()
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
			for (int i = 0; i < getPreReqCount(); ++i)
			{
				final String preReq = getPreReq(i);
				if (preReq.startsWith("PRETYPE:") && (preReq.indexOf("EQMODTYPE=MagicalEnhancement") >= 0 || preReq.indexOf("EQMODTYPE.MagicalEnhancement") >= 0))
				{
					return true;
				}
			}
		}
		return costDouble == 1;
	}

	public List getSpecialProperties()
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

	int getChoice(int pool, Equipment parent, boolean bAdd)
	{
		if (choiceString.length() == 0)
		{
			return 1;
		}

		boolean forEqBuilder = choiceString.startsWith("EQBUILDER.");

		if (bAdd && forEqBuilder)
		{
			return 1;
		}

		final StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);

		final List availableList = new ArrayList();	// available list of choices
		List selectedList = new ArrayList();			// selected list of choices
		String choiceType = aTok.nextToken();

		final pcgen.gui.utils.ChooserInterface chooser = pcgen.gui.utils.ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);
		chooser.setVisible(false);
		chooser.setPool(pool);
		addAssociatedTo(selectedList);

		boolean allowDuplicates = false;
		boolean noSign = false;

		int minValue = 0;
		int maxValue = 0;
		int incValue = 1;
		int maxSelect = 0;
		String title = null;

		if (choiceType.startsWith("COUNT="))
		{
			if (choiceType.substring(6).equalsIgnoreCase("ALL"))
			{
				maxSelect = Integer.MAX_VALUE;
			}
			else
			{
				try
				{
					maxSelect = Integer.parseInt(choiceType.substring(6));
				}
				catch (NumberFormatException e)
				{
					//TODO: Should this really be ignored?
				}
			}
			choiceType = aTok.nextToken();
		}

		while (!forEqBuilder && aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if ((maxSelect > 0) && (maxSelect != Integer.MAX_VALUE))
			{
				if (pool > 0)
				{
					chooser.setPool(maxSelect - selectedList.size());
				}
			}
			if (aString.startsWith("TITLE="))
			{
				title = aString.substring(6);
			}
			else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				if ((pool > 0) && (maxSelect == 0))
				{
					chooser.setPool(pool - selectedList.size());
				}
				aString = aString.substring(5);

				boolean bAll = false;
				if (aString.equalsIgnoreCase("ALL"))
				{
					bAll = true;
				}
				if (aString.startsWith("LASTCHOICE"))
				{
					for (Iterator e = parent.getEqModifierList(true).iterator(); e.hasNext();)
					{
						final EquipmentModifier sibling = (EquipmentModifier) e.next();
						if (!(sibling.equals(this)) && sibling.getChoiceString().startsWith(choiceType))
						{
							sibling.addAssociatedTo(availableList);
						}
					}
				}
				else if ("SKILL".equalsIgnoreCase(choiceType))
				{
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						final Skill aSkill = (Skill) e.next();
						if ((bAll || aSkill.isType(aString)) && !availableList.contains(aSkill.getName()))
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				else if ("EQUIPMENT".equalsIgnoreCase(choiceType))
				{
					for (Iterator e = Globals.getEquipmentList().iterator(); e.hasNext();)
					{
						final Equipment aEquip = (Equipment) e.next();
						if (aEquip.isType(aString) && !availableList.contains(aEquip.getName()))
						{
							availableList.add(aEquip.getName());
						}
					}
				}
				else if ("FEAT".equalsIgnoreCase(choiceType))
				{
					for (Iterator e = Globals.getFeatList().iterator(); e.hasNext();)
					{
						final Feat aFeat = (Feat) e.next();
						if ((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) && !availableList.contains(aFeat.getName()))
						{
							//
							// TODO: Allow multiples
							//
							if ((bAll || aFeat.isType(aString)) && (aFeat.getChoiceString().length() == 0))
							{
								availableList.add(aFeat.getName());
							}
						}
					}
				}
				//
				// Used by internal equipment modifier "Add Type"
				// see LstSystemLoader.java
				//
				else if ("EQTYPES".equalsIgnoreCase(aString))
				{
					availableList.addAll(Equipment.getEquipmentTypes());
				}
				else
				{
					Logging.errorPrint("Unknown option in CHOOSE '" + aString + "'");
				}
			}
			else if ("STAT".equals(aString))
			{
				for (int x = 0; x < Globals.s_ATTRIBSHORT.length; x++)
				{
					availableList.add(Globals.s_ATTRIBSHORT[x]);
				}
			}
			else if ("SKILL".equals(aString))
			{
				for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
				{
					final Skill aSkill = (Skill) e.next();
					availableList.add(aSkill.getName());
				}
			}
			else if ("MULTIPLE".equals(aString))
			{
				allowDuplicates = true;
			}
			else if ("NOSIGN".equals(aString))
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
					//TODO: Should this really be ignored?
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
					//TODO: Should this really be ignored?
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
					//TODO: Should this really be ignored?
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

		if (maxSelect == Integer.MAX_VALUE)
		{
			chooser.setPool(availableList.size() - selectedList.size());
			bAdd = true;
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
		if (title == null)
		{
			title = choiceType;
		}
		chooser.setAllowsDups(allowDuplicates);
		chooser.setSelectedListTerminator("|");
		chooser.setTitle("Select " + title + " (" + getName() + ")");
		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);
		chooser.show();

		clearAssociated();
		selectedList = chooser.getSelectedList();
		for (int i = 0; i < selectedList.size(); i++)
		{
			String aString = (String) selectedList.get(i);
			if (minValue < maxValue)
			{
				int idx = aString.indexOf('|');
				if (idx < 0)
				{
					List secondaryChoice = new ArrayList();
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
					aString += '|' + (String) chooser.getSelectedList().get(0);
				}
			}
/*			else if ("FEAT".equalsIgnoreCase(choiceType))
			{
				Feat aFeat = Globals.getFeatNamed(aString);
				if (aFeat != null)
				{
					if (aFeat.getChoiceString().length() != 0)
					{
						aFeat = (Feat) aFeat.clone();
						ArrayList aList = new ArrayList();
						ArrayList sList = new ArrayList();
						//
						// TODO: At some point, getCurrentPC() needs to be removed
						// from modChoices, so that choices are not dependent upon
						// current character
						//
						Utility.modChoices(aFeat, aList, sList, true);
						ArrayList x = new ArrayList();
						aFeat.addAssociatedTo(x);
					}
				}
			}
*/
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

	String getProficiency()
	{
		return proficiency;
	}

	/**
	 * Should use this instead of the current getBonusList()
	 * but have to find everywhere an EquipmentModifier is added
	 * from and call this function. JSC 08/20/03
	 **/
	public void calcBonuses()
	{
		List addList = new LinkedList();
		List delList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			String aString = aBonus.toString();
			final int idx = aString.indexOf("%CHOICE");
			if (idx >= 0)
			{
				delList.add(aBonus);
				// Add an entry for each of the
				// associated list entries
				for (int j = 0; j < getAssociatedCount(); j++)
				{
					final BonusObj newBonus = Bonus.newBonus(aString.substring(0, idx) + getAssociated(j) + aString.substring(idx + 7));
					newBonus.setCreatorObject(this);
					addList.add(newBonus);
				}
			}
		}
		if (delList.size() > 0)
		{
			for (Iterator ab = delList.iterator(); ab.hasNext();)
			{
				removeBonusList((BonusObj) ab.next());
			}
			for (Iterator ab = addList.iterator(); ab.hasNext();)
			{
				addBonusList((BonusObj) ab.next());
			}
		}
	}

	public List getBonusList()
	{
		final List myBonusList = new ArrayList(super.getBonusList());
		for (int i = myBonusList.size() - 1; i > -1; i--)
		{
			BonusObj aBonus = (BonusObj) myBonusList.get(i);
			String aString = aBonus.toString();
			final int idx = aString.indexOf("%CHOICE");
			if (idx >= 0)
			{
				//
				// Add an entry for each of the associated list entries
				//
				for (int j = 0; j < getAssociatedCount(); j++)
				{
					final BonusObj newBonus = Bonus.newBonus(aString.substring(0, idx) + getAssociated(j) + aString.substring(idx + 7));
					newBonus.setCreatorObject(this);
					myBonusList.add(newBonus);
				}
				myBonusList.remove(aBonus);
			}
		}

		return myBonusList;
	}

	public boolean getBonusListString(String aString)
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (aBonus.getBonusInfo().equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	public double bonusTo(String aType, String aName, Object obj)
	{
		return super.bonusTo(aType, aName, obj, getBonusList());
	}

	public List getBonusListOfType(String aType, String aName)
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if ((aBonus.getTypeOfBonus().indexOf(aType) >= 0) &&
				(aBonus.getBonusInfo().indexOf(aName) >= 0) &&
				(!willIgnore(getKeyName())))
			{
				aList.add(aBonus);
			}
		}
		return aList;
	}

	/**
	 * returns all BonusObj's that are "active"
	 **/
	public List getActiveBonuses()
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (!willIgnore(getKeyName()) && passesPreReqToUse())
			{
				aBonus.setApplied(true);
				aList.add(aBonus);
			}
		}
		return aList;
	}

	/**
	 * Returns the list of virtual feats this item bestows upon its weilder
	 *
	 * @return	List of Feat objects
	 **/
	List getVFeatList()
	{
		if (vFeatList != null)
		{
			if (choiceString.startsWith("FEAT") || (choiceString.indexOf("|FEAT") >= 0))
			{
				List vFeats = new ArrayList();
				for (Iterator e = vFeatList.iterator(); e.hasNext();)
				{
					final String aString = (String) e.next();
					if (aString.equals("%CHOICE"))
					{
						for (int i = 0; i < getAssociatedCount(); i++)
						{
							vFeats.add(getAssociated(i));
						}
					}
					else
					{
						vFeats.add(aString);
					}
				}
				return vFeats;
			}
		}
		return vFeatList;
	}

	public boolean hasVFeats()
	{
		return (vFeatList != null) && (vFeatList.size() > 0);
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder
	 * @param vList a | delimited list of feats to add to the list
	 **/
	public void addVFeatList(String vList)
	{
		final StringTokenizer aTok = new StringTokenizer(vList, "|", false);
		while (aTok.hasMoreTokens())
		{
			if (vFeatList == null)
			{
				vFeatList = new ArrayList();
			}
			vFeatList.add(aTok.nextToken());
		}
	}

}
