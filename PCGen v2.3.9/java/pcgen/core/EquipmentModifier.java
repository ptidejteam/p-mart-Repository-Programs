/*
 * @(#) $Id: EquipmentModifier.java,v 1.1 2006/02/20 23:47:11 vauchers Exp $
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
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
	private int plus = 0;
	private int equipmentVisible = VISIBLE_YES;
	private boolean assignToAll = false;

	public final static int VISIBLE_NO = 0;
	public final static int VISIBLE_YES = 1;
	public final static int VISIBLE_QUALIFIED = 2;

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
		StringBuffer aString = new StringBuffer(getName());
		if (associatedList.size() > 0)
		{
			aString.append(" (");
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
			aString.append(")");
		}
		return aString.toString();
	}

	private void setIgnores(String aString)
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

	private void setReplacement(String aString)
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

	private void setArmorType(String aString)
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

	private void setVisible(String aString)
	{
		if (aString.startsWith("Y"))
			equipmentVisible = VISIBLE_YES;
		else if (aString.startsWith("Q"))
			equipmentVisible = VISIBLE_QUALIFIED;
		else
			equipmentVisible = VISIBLE_NO;
	}

	private void setAssignment(String aString)
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

	private void setCost(String aString)
	{
		cost = aString;
	}

	public String getPreCost()
	{
		return preCost;
	}

	private void setPreCost(String aString)
	{
		preCost = aString;
	}

	private void setPlus(String aString)
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

	public ArrayList getSpecialAbilities()
	{
		return specialAbilities;
	}

	private void setSpecialAbilties(String aAbilities)
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


	public int getChoice(int pool)
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

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (!availableList.contains(aString))
			{
				availableList.add(aString);
			}
		}
		selectedList = (ArrayList)associatedList.clone();

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

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;

		String aCol = null;
		for (col = 0; col < colMax; col++)
		{
			aCol = colToken.nextToken();
			if (super.parseTag(aCol))
			{
				continue;
			}

			final int len = aCol.length();
			if (col == 0)
			{
				setName(aCol);
			}
			else if (aCol.startsWith("CHOOSE:"))
			{
				choiceString = aCol.substring(7);
			}
			else if (aCol.startsWith("ARMORTYPE:"))
			{
				setArmorType(aCol.substring(10));
			}
			else if (aCol.startsWith("ASSIGNTOALL:"))
			{
				setAssignment(aCol.substring(12));
			}
			else if (aCol.startsWith("BONUS:"))
			{
				addBonusList(aCol.substring(6));
			}
			else if (aCol.startsWith("ITYPE:"))
			{
				setItemType(aCol.substring(6));
			}
			else if (aCol.startsWith(Globals.s_TAG_TYPE))
			{
				setType(aCol.substring(Globals.s_TAG_TYPE.length()));
			}
			else if (aCol.startsWith("KEY:"))
			{
				setKeyName(aCol.substring(4));
			}
			else if (aCol.startsWith("PRE"))
			{
				preReqArrayList.add(aCol);
			}
			else if (aCol.startsWith("VISIBLE:"))
			{
				setVisible(aCol.substring(8));
			}
			else if (aCol.startsWith("COST:"))
			{
				setCost(aCol.substring(5));
			}
			else if (aCol.startsWith("COSTPRE:"))
			{
				setPreCost(aCol.substring(8));
			}
			else if (aCol.startsWith("REPLACES:"))
			{
				setReplacement(aCol.substring(9));
			}
			else if (aCol.startsWith("IGNORES:"))
			{
				setIgnores(aCol.substring(8));
			}
			else if (aCol.startsWith("PLUS:"))
			{
				setPlus(aCol.substring(5));
			}
			else if (aCol.startsWith("SA:"))
			{
				setSpecialAbilties(aCol.substring(3));
			}
			else
				JOptionPane.showMessageDialog
					(null, "Illegal equipment modifier info " +
					sourceFile.getName() + ":" + Integer.toString(lineNum) +
					" \"" + aCol + "\"", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}
}
