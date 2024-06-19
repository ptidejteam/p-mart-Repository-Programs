/*
 * @(#) $Id: EquipmentModifier.java,v 1.1 2006/02/20 23:54:35 vauchers Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Definition and games rules for an equipment modifier.
 *
 * @version $Revision: 1.1 $
 */
public class EquipmentModifier extends PObject
{
	private ArrayList specialAbilities = new ArrayList();
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

	public final static int VISIBLE_NO = 0;
	public final static int VISIBLE_YES = 1;
	public final static int VISIBLE_QUALIFIED = 2;

	private final static int NAMINGOPTION_NORMAL = 0;
	private final static int NAMINGOPTION_NONAME = 1;
	private final static int NAMINGOPTION_NOLIST = 2;

	/**
	 * Clone an EquipmentModifier
	 * @return a clone of the EquipmentModifier
	 */
	public Object clone()
	{
		EquipmentModifier aObj = (EquipmentModifier)super.clone();
		aObj.itemType = (ArrayList)itemType.clone();
		aObj.specialAbilities = (ArrayList)specialAbilities.clone();
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
		StringBuffer aString = new StringBuffer(getName().length());
		if (namingOption != NAMINGOPTION_NONAME)
		{
			aString.append(getName());
		}
		if ((namingOption != NAMINGOPTION_NOLIST) && (associatedList.size() > 0))
		{
			if (namingOption != NAMINGOPTION_NONAME)
			{
				aString.append(" (");
			}
			boolean bFirst = true;
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				if (!bFirst)
				{
					aString.append(", ");
				}
				aString.append((String)e.next());
				bFirst = false;
			}
			if (namingOption != NAMINGOPTION_NONAME)
			{
				aString.append(")");
			}
		}
		return aString.toString();
	}

	public void setIgnores(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ",", false);
		ignores.clear();
		while (aTok.hasMoreTokens())
		{
			final String aReplace = (String)aTok.nextToken();
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
			final String aReplace = (String)aTok.nextToken();
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
			final String aType = (String)aTok.nextToken();
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
			final int idx = aTypes.indexOf((String)aTok.nextToken());
			if (idx >= 0)
			{
				if (aTok.hasMoreTokens())
				{
					final String newArmorType = (String)aTok.nextToken();
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
		else
		{
			this.namingOption = NAMINGOPTION_NORMAL;
		}
	}

	public ArrayList getSpecialAbilities()
	{
		return specialAbilities;
	}

	public void setSpecialAbilties(String aAbilities)
	{
		final StringTokenizer aTok = new StringTokenizer(aAbilities, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (!specialAbilities.contains(aString))
			{
				specialAbilities.add(aString);
			}
		}
	}


	public int getChoice(int pool, Equipment parent)
	{
		if (choiceString.length() < 1)
		{
			return 1;
		}

		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);

		ArrayList availableList = new ArrayList();	// available list of choices
		ArrayList selectedList = new ArrayList();		// selected list of choices
		final String choiceType = aTok.nextToken();

		final Chooser chooser = new Chooser();
		chooser.setPoolFlag(false);
		chooser.setAllowsDups(false);
		chooser.setVisible(false);
		chooser.setPool(pool);
		selectedList = (ArrayList)associatedList.clone();

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (aString.startsWith("TYPE="))
			{
				if (pool > 0)
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
							availableList.addAll(sibling.getAssociatedList());
						}
					}
				}
				else if (choiceType.equalsIgnoreCase("SKILL"))
				{
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						final Skill aSkill = (Skill)e.next();
						if (aSkill.isType(aString) && !availableList.contains(aString))
						{
							availableList.add(aSkill.getName());
						}
					}
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

		chooser.setTitle("Select " + choiceType + " (" + getName() + ")");
		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);
		chooser.show();

		getAssociatedList().clear();
		for (int i = 0; i < chooser.getSelectedList().size(); i++)
		{
			final String aString = (String)chooser.getSelectedList().get(i);
			if (!associatedList.contains(aString))
			{
				associatedList.add(aString);
			}
		}
		return associatedList.size();
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
		if (associatedList.size() != 0)
		{
			for (int i = 0; i < myBonusList.size(); i++)
			{
				String aString = (String)myBonusList.get(i);
				int idx = aString.indexOf("%CHOICE");
				if (idx >= 0)
				{
					aString = aString.substring(0, idx) + (String)associatedList.get(0) + aString.substring(idx + 7);
					myBonusList.set(i, aString);
				}
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
