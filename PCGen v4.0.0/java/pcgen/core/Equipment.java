/*
 *  Equipment.java
 *  Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:47:08 $
 *
 */

package pcgen.core;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.io.FileAccess;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 *  <code>Equipment</code>.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * created    December 27, 2001
 * @version    $Revision: 1.1 $
 */
public class Equipment extends PObject
  implements Serializable, EquipmentCollection, Comparable
{
	/**
	 *  The item is held in neither hand
	 */
	public static final int NEITHER_HAND = 0;
	/**
	 *  The item is held in the primary hand
	 */
	public static final int PRIMARY_HAND = 1;
	/**
	 *  The item is held in the secondary hand
	 */
	public static final int SECONDARY_HAND = 2;
	/**
	 *  The item is held in both hands
	 */
	public static final int BOTH_HANDS = 3;
	/**
	 *  The item is either a double weapon or one of a pair of weapons
	 */
	public static final int TWOWEAPON_HANDS = 4;
	private static final String[] s_handNames = {
		"Neither",
		"Primary",
		"Off-hand",
		"Both",
		"Two-Weapons"
	};
	private static TreeSet s_equipmentTypes = new TreeSet();

	private BigDecimal cost = new BigDecimal("0");
	//private Integer acMod = new Integer(0);
	private Integer maxDex = new Integer(100);
	private Integer DefBon = new Integer(0);
	private Integer acCheck = new Integer(0);
	private Integer spellFailure = new Integer(0);
	private String moveString = "";
	private Float weight = new Float(0);
	private String modifiedName = "";
	// where extras can be placed
	private String size = Constants.s_SIZESHORT[Constants.SIZE_M];
	private String damage = "";
	private String critRange = "";
	private int critMult = 0;
	private String altDamage = "";
	private int altCritMult = 0;
	private Integer range = new Integer(0);
	private Float qty = new Float(0);
	private boolean equipped = false;
	private String longName = "";
	private Integer attacks = new Integer(1);
	private String profName = "";
	private Float carried = new Float(0);
	private int inHand = NEITHER_HAND;
	private int hands = 1;
	private String bonusType = null;
	private String specialProperties = "";
	private String indexedUnderType = "";
	private int numberEquipped = 0;
	private int reach = 0;
	private EquipmentCollection d_parent = null;
	private ArrayList d_containedEquipment = null;
	private ArrayList d_headerChildren = null;
	private boolean d_acceptsChildren = false;
	private HashMap d_acceptsTypes = null;
	private HashMap d_childTypes = null;
	private Float containerWeightCapacity = new Float(0);
	private String containerCapacityString = "";
	private String containerContentsString = "";
	private boolean containerConstantWeight = false;
	private Equipment headerParent = null;
	private boolean hasHeaderParent = false;
	private boolean isHeaderParent = false;
	private boolean isOnlyNaturalWeapon = false;
	/** if is true a BAB of 13 yields 13/8/3, if false, merely 13. */
	private boolean attacksProgress = true;
	private ArrayList eqModifierList = new ArrayList();
	private ArrayList altEqModifierList = new ArrayList();
	private ArrayList altTypeList = null;
	private BigDecimal baseCost = new BigDecimal("0");
	private String sizeBase = "";
	private String baseItem = "";
	private boolean modifiersAllowed = true;
	private boolean modifiersRequired = false;
	private BigDecimal costMod = new BigDecimal("0");
	private Float weightMod = new Float(0);
//attempt to add DR vales for Armor
	private Integer eDR = new Integer(-1);
	private int outputIndex = 0;

	private boolean calculatingCost = false;
	private boolean weightAlreadyUsed = false;

	/**
	 *  Used in callbacks from PObject.bonusTo() (getVariableValue(),
	 *  setBonusStackFor()) and PObject.passesPreReqTestsForList() (isPreType())
	 */
	private boolean bonusPrimary = true;
	private ArrayList vFeatList = new ArrayList(); // virtual feat list

	/**
	 *  if true a BAB of 13 yields 13/8/3, if false, merely 13
	 *
	 *@return    whether it gives several attacks
	 */
	public boolean isAttacksProgress()
	{
		return attacksProgress;
	}

	/**
	 *  Returns whether to give several attacks
	 *
	 *@param  argAttacksProgress  whether to give several attacks.
	 */
	public void setAttacksProgress(boolean argAttacksProgress)
	{
		attacksProgress = argAttacksProgress;
	}

	/**
	 *  Returns true if this is the only natural weapon.
	 *
	 *@return    true if this is the only natural weapon.
	 */
	public boolean isOnlyNaturalWeapon()
	{
		return isOnlyNaturalWeapon;
	}

	/**
	 *  Set to true if this is the only natural weapon.
	 *
	 *@param  onlyNaturalWeapon  set to true if this is the only natural weapon.
	 */
	public void setOnlyNaturalWeapon(boolean onlyNaturalWeapon)
	{
		isOnlyNaturalWeapon = onlyNaturalWeapon;
	}

	/**
	 *  DR for equipment
	 *
	 */
	public Integer eDR()
	{
		int check = eDR.intValue() + bonusTo("EQMARMOR", "EDR", true);
		if (check < 0)
		{
			check = 0;
		}
		return new Integer(check);
	}

	/**
	 *  Sets the acCheck attribute of the Equipment object
	 *
	 *@param  aString  The new acCheck value
	 */
	public void seteDR(String aString)
	{
		Globals.debugPrint("Entering seteDR.");
		Globals.debugPrint("Passed Value:", aString);
		try
		{
			eDR = new Integer(aString);
		}
		catch (NumberFormatException nfe)
		{
			eDR = new Integer(0);
			// ignore
		}
		Globals.debugPrint("Exiting seteDR.");
		Globals.debugPrint("Exit value of eDR:", eDR.toString());
	}

	/**
	 *  ???
	 *
	 *@param  aHeaderParent  ???
	 */
	public void setIsHeaderParent(boolean aHeaderParent)
	{
		isHeaderParent = aHeaderParent;
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public boolean isHeaderParent()
	{
		return isHeaderParent;
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public boolean getHasHeaderParent()
	{
		return hasHeaderParent;
	}

	/**
	 * setter
	 *
	 */
	public void addContainedEquipment(Equipment e)
	{
		if (d_containedEquipment == null)
			d_containedEquipment = new ArrayList();
		d_containedEquipment.add(e);
	}

	/**
	 * ???
	 *
	 */
	private void addContainedEquipmentTo(Collection c)
	{
		if (getContainedEquipmentCount() == 0) return;
		c.addAll(d_containedEquipment);
	}

	/**
	 *  contains
	 *
	 *@return true if containedEquipment contains the passed item
	 */
	public boolean containsContainedEquipment(Equipment e)
	{
		if (d_containedEquipment == null) return false;
		return d_containedEquipment.contains(e);
	}

	/**
	 *  count
	 *
	 *@return number of containedEquipment objects
	 */
	public int getContainedEquipmentCount()
	{
		if (d_containedEquipment == null) return 0;
		return d_containedEquipment.size();
	}

	/**
	 * accessor
	 *
	 *@return containedEquipment object
	 */
	public Equipment getContainedEquipment(int i)
	{
		return (Equipment)d_containedEquipment.get(i);
	}

	/**
	 * accessor
	 *
	 *@return index of containedEquipment object
	 */
	public int getContainedEquipmentIndexOf(Equipment e)
	{
		if (d_containedEquipment == null)
			return -1;
		return d_containedEquipment.indexOf(e);
	}

	/**
	 * remover
	 */
	private void removeContainedEquipment(int i)
	{
		d_containedEquipment.remove(i);
	}

	/**
	 *  Returns the number of hands required to use this item.
	 *
	 *@return    the number of hands required to use this item.
	 */
	public int getHands()
	{
		return hands;
	}

	public void setHands(int argHands)
	{
		hands = argHands;
	}

	/**
	 *  Returns the number of items of this type that are carried.
	 *
	 *@return    the number of items of this type that are carried.
	 */
	public Float getCarried()
	{
		return carried;
	}

	/**
	 *  Sets the number of items of this type that are carried.
	 *
	 *@param  argCarried  the number of items of this type that are carried.
	 */
	public void setCarried(Float argCarried)
	{
		carried = argCarried;
	}

	/**
	 *  Returns the alternate damage for this item.
	 *
	 *@return    the alternate damage for this item.
	 */
	public String getAltDamage()
	{
		return getDamage(false);
	}

	/**
	 *  Sets the alternate damage for this item.
	 *
	 *@param  aString  the alternate damage for this item.
	 */
	public void setAltDamage(String aString)
	{
		altDamage = aString;
	}

	/**
	 *  Returns the name of this hand
	 *
	 *@param  handNumber  the hand for which a name is wanted
	 *@return             the name of this hand
	 */
	public static String getHandName(int handNumber)
	{
		return s_handNames[handNumber];
	}

	/**
	 *  Returns the number of a hand
	 *
	 *@param  handDesc  The name of a hand one wants to know the number of
	 *@return           the number of a hand
	 */
	public static int getHandNum(String handDesc)
	{
		for (int i = 0; i < s_handNames.length; ++i)
		{
			if (s_handNames[i].equals(handDesc))
			{
				return i;
			}
		}
		try
		{
			return Integer.parseInt(handDesc);
		}
		catch (NumberFormatException nfe)
		{
			GuiFacade.showMessageDialog(null, "Unable to interpret hand setting: " + handDesc, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}

	/**
	 * Return the output index, which controls the order in
	 * which the equipment appears on a character sheet.
	 * Note: -1 means hidden and 0 means not set
	 *
	 * <br>author: James Dempsey 17-Jun-02
	 *
	 * @return the output index for this equipment item (-1=hidden, 0=not set)
	 */
	public int getOutputIndex()
	{
		return outputIndex;
	}

	/**
	 * Set this item's output index, which controls the order
	 * in which the equipment appears on a character sheet.
	 * Note: -1 means hidden and 0 means not set
	 *
	 * <br>author: James Dempsey 17-Jun-02
	 *
	 * @param newIndex the new output index for this equipment item (-1=hidden, 0=not set)
	 */
	public void setOutputIndex(int newIndex)
	{
		outputIndex = newIndex;
	}

	/**
	 *  Removes all items from this container.
	 */
	public void clearContainedEquipment()
	{
		d_containedEquipment = null;
	}

	/**
	 *  returns pointer to Equipment of first headerChild
	 *
	 *@return    pointer to Equipment of first headerChild
	 */
	public Equipment createHeaderParent()
	{
		//returns pointer to Equipment of first headerChild

		Equipment anEquip = (Equipment)clone();
		anEquip.setHeaderParent(this);

		//clear certain properties in the headerparent, which it is now;
		containerContentsString = "";

		//clear some the junk
		clearChildTypes();
		d_parent = null;

		addHeaderChild(anEquip);
		isHeaderParent = true;
		hasHeaderParent = false;

		anEquip.hasHeaderParent = true;
		anEquip.isHeaderParent = false;

		anEquip.setKeyName(anEquip.toString());

		//correct the qty of the (now) header child to 1;
		anEquip.qty = new Float(1);

		//move the pointers from any children to the new parent
		for (int e = 0; e < getContainedEquipmentCount(); e++)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = getContainedEquipment(e);
			aChild.setParent(anEquip);
		}

		clearContainedEquipment();
		//clear the last junk

		setIsEquipped(false);

		return anEquip;
	}

	/**
	 *  ???
	 */
	public void collapseHeaderParent()
	{
		Equipment lastHchild = getHeaderChild(0);
		hasHeaderParent = false;
		isHeaderParent = false;
		qty = new Float(1);
		clearChildTypes();
		if (lastHchild.getChildTypeCount() > 0)
			d_childTypes = new HashMap(lastHchild.d_childTypes);
		clearContainedEquipment();
		if (lastHchild.getContainedEquipmentCount() > 0)
		{
			d_containedEquipment = new ArrayList(lastHchild.getContainedEquipmentCount());
			lastHchild.addContainedEquipmentTo(d_containedEquipment);
		}
		setParent(lastHchild.getParent());

		for (int e = 0; e < getContainedEquipmentCount(); e++)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = getContainedEquipment(e);
			aChild.setParent(this);
		}

		clearHeaderChildren();

		setCarried(lastHchild.getCarried());
		setIsEquipped(lastHchild.isEquipped(), lastHchild.whatHand());
		setHand(lastHchild.whatHand());
	}

	/**
	 *  Removes all children funky hack, may have problems with container
	 */
	public void quickRemoveAllChildren()
	{
		for (int e = 0; e < getContainedEquipmentCount(); e++)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = getContainedEquipment(e);
			aChild.d_parent = null;
		}
		clearContainedEquipment();
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 */
	public void setHeaderParent(Object anObject)
	{
		headerParent = (Equipment)anObject;
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 */
	public boolean addHeaderChild(Equipment anObject)
	{
		if (d_headerChildren == null) d_headerChildren = new ArrayList();
		return d_headerChildren.add(anObject);
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 *@return           ???
	 */
	public int indexOfHeaderChild(Equipment anObject)
	{
		if (d_headerChildren == null) return -1;
		return d_headerChildren.indexOf(anObject);
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public int getHeaderChildCount()
	{
		if (d_headerChildren == null) return 0;
		return d_headerChildren.size();
	}

	/**
	 *  ???
	 *
	 *@param  i  ???
	 *@return    ???
	 */
	public Equipment getHeaderChild(int i)
	{
		return (Equipment)d_headerChildren.get(i);
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public Equipment getHeaderParent()
	{
		return headerParent;
	}

	/**
	 *  ???
	 *
	 *@param  anArray  ???
	 */
	public void setHeaderChildren(Collection anArray)
	{
		clearHeaderChildren();
		if (anArray == null || anArray.size() == 0) return;
		d_headerChildren = new ArrayList();
		d_headerChildren.addAll(anArray);
	}

	/**
	 *  Removes a header child.
	 *
	 *@param  anObject  the header child to remove
	 */
	public boolean removeHeaderChild(Object anObject)
	{
		if (d_headerChildren == null) return false;
		if (d_headerChildren.remove(anObject))
		{
			//
			// re-key
			for (Iterator e = d_headerChildren.iterator(); e.hasNext();)
			{
				final Equipment anEquip = (Equipment)e.next();
				anEquip.setKeyName(anEquip.toString());
			}
			if (d_headerChildren.size() == 0) clearHeaderChildren();
			return true;
		}
		return false;
	}

	/**
	 *  Removes all header children.
	 */
	public void clearHeaderChildren()
	{
		d_headerChildren = null;
	}

	public String getSource()
	{
		String aString = super.getSource();
		if (isType(Constants.s_CUSTOM))
		{
			aString = "Custom - " + aString;
		}
		return aString;
	}

	/**
	 * Get display information for all "interesting" properties.
	 *
	 * @return display string of bonuses and special properties
	 */
	public String getInterestingDisplayString()
	{
		StringBuffer s = new StringBuffer(getBonusListString());
		String t = getSpecialProperties();

		if (t == null)
		{
			t = "";
		}

		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			EquipmentModifier eqMod = (EquipmentModifier)e.next();
			for (Iterator modIterator = eqMod.getBonusList().iterator(); modIterator.hasNext();)
			{
				final String eqModBonus = (String)modIterator.next();
				if ((eqModBonus.length() != 0) && !eqModBonus.startsWith("EQM"))
				{
					if (s.length() != 0)
					{
						s.append(", ");
					}
					s.append(eqModBonus);
				}

			}
		}

		if (t.length() != 0)
		{
			if (s.length() != 0)
			{
				s.append('|');
			}
			s.append(t);
		}
		return s.toString();
	}

	public String getRawSpecialProperties()
	{
		return specialProperties;
	}

	/**
	 *  Returns special properties of an Equipment.
	 *
	 *@return    special properties of an Equipment.
	 */
	public String getSpecialProperties()
	{
		ArrayList list1 = new ArrayList(eqModifierList);
		ArrayList list2 = new ArrayList(altEqModifierList);
		ArrayList comn = new ArrayList();

		//
		// Get all the modifiers that apply to the entire item into a separate list
		//
		for (int i = list1.size() - 1; i >= 0; i--)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)list1.get(i);
			if (!eqMod.getAssignToAll())
			{
				continue;
			}
			comn.add(0, eqMod);
			list1.remove(i);
		}

		//
		// remove the common modifiers from the alternate list
		//
		for (int i = list2.size() - 1; i >= 0; i--)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)list2.get(i);
			if (!eqMod.getAssignToAll())
			{
				continue;
			}

			final int j = comn.indexOf(eqMod);
			if (j >= 0)
			{
				list2.remove(i);
			}
			else
			{
				Globals.debugPrint("SPROP: eqMod expected but not found: ", eqMod.getName());
			}
		}

		final String common = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(comn), true));
		final String saList1 = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list1), true));
		final String saList2 = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list2), false));
		StringBuffer sp = new StringBuffer(specialProperties.length() + common.length() + saList1.length() + saList2.length() + 5);

		sp.append(specialProperties);
		if (common.length() != 0)
		{
			if (sp.length() != 0)
			{
				sp.append(", ");
			}
			sp.append(common);
		}

		if (saList1.length() != 0)
		{
			if (sp.length() != 0)
			{
				sp.append(", ");
			}
			if (isDouble())
			{
				sp.append("Head1: ");
			}
			sp.append(saList1);
		}
		if (isDouble() && (saList2.length() != 0))
		{
			if (sp.length() != 0)
			{
				sp.append(", ");
			}
			sp.append("Head2: ").append(saList2);
		}

		return sp.toString();
	}

	/**
	 *  Sets special properties of an Equipment.
	 *
	 *@param  aProp  The properties to set
	 */
	public void setSpecialProperties(String aProp)
	{
		specialProperties = aProp;
	}

	/**
	 *  Returns the Equipment as a String
	 *
	 *@return    the Equipment as a String
	 */
	public String toString()
	{
		StringBuffer aString = new StringBuffer(name);
		if (hasHeaderParent)
		{
			aString.append(" -").append(headerParent.indexOfHeaderChild(this) + 1).append('-');
		}

		if (modifiedName.length() > 0)
		{
			aString.append(" (").append(modifiedName).append(")");
		}
		return aString.toString();
	}

	/**
	 *@param  aString
	 */
	public void setIndexedUnderType(String aString)
	{
		indexedUnderType = aString;
	}

	/**
	 *  Gets the indexedUnderType attribute of the Equipment object
	 *
	 *@return    The indexedUnderType value
	 */
	public String isIndexedUnderType()
	{
		return indexedUnderType;
	}

	/**
	 *  Gets the name attribute of the Equipment object
	 *
	 *@return    The name value
	 */
	public String getName()
	{
		return toString();
	}

	/**
	 *  Gets the baseItemName attribute of the Equipment object
	 *
	 *@return    The baseItemName value
	 */
	public String getBaseItemName()
	{
		if (baseItem.length() == 0)
		{
			return getKeyName();
		}
		return baseItem;
	}

	public void setBaseItem(String argBaseItem)
	{
		baseItem = argBaseItem;
	}

	/**
	 *  Gets the childCount attribute of the Equipment object
	 *
	 *@return    The childCount value
	 */
	public int getChildCount()
	{
		return getContainedEquipmentCount();
	}

	/**
	 *  Gets a child of the Equipment object
	 *
	 *@param  childIndex  The index of the child to get
	 *@return    The child value
	 */
	public Object getChild(int childIndex)
	{
		return getContainedEquipment(childIndex);
	}

	/**
	 *  Removes a child from the Equipment
	 *
	 *@param  child  The child to remove
	 */
	public void removeChild(Object child)
	{
		int i = indexOfChild(child);
		Equipment anEquip = (Equipment)child;
		Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", new Float(getChildType("Total").floatValue() - qtyRemoved.floatValue()));
		String aString = anEquip.isIndexedUnderType();
		setChildType(aString, new Float(getChildType(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		removeContainedEquipment(i);
		updateContainerContentsString();
		anEquip = this;
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			anEquip.updateContainerContentsString();
		}
	}

	/**
	 *  Removes a child from the Equipment
	 *
	 *@param  childIndex  The number of the child to remove
	 */
	public void removeChild(int childIndex)
	{
		Equipment anEquip = getContainedEquipment(childIndex);
		Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", new Float(getChildType("Total").floatValue() - qtyRemoved.floatValue()));
		String aString = anEquip.isIndexedUnderType();
		setChildType(aString, new Float(getChildType(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		removeContainedEquipment(childIndex);
		updateContainerContentsString();
		anEquip = this;
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			anEquip.updateContainerContentsString();
		}
	}

	/**
	 *  Gets the index of a child
	 *
	 *@param  child  The child
	 *@return        the index of the child
	 */
	public int indexOfChild(Object child)
	{
		if (!(child instanceof Equipment))
			return -1;
		return getContainedEquipmentIndexOf((Equipment)child);
	}

	/**
	 *  Sets the parent attribute of the Equipment object
	 *
	 *@param  parent  The new parent value
	 */
	public void setParent(EquipmentCollection parent)
	{
		d_parent = parent;
	}

	/**
	 *  Gets the uberParent attribute of the Equipment object
	 *
	 *@return    The uberParent value
	 */
	public Equipment getUberParent()
	{
		if (getParent() == null)
		{
			return this;
		}
		Equipment anEquip = (Equipment)getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
		}
		return anEquip;
	}

	/**
	 *  Returns how 'deep' in a structure an Equipment is.
	 *
	 *@return   how 'deep' in a structure an Equipment is.
	 */
	public int itemDepth()
	{
		if (getParent() == null)
		{
			return 0;
		}
		int i = 1;
		Equipment anEquip = (Equipment)getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			i++;
		}
		return i;
	}

	/**
	 *  Gets the parent of the Equipment object
	 *
	 *@return    The parent
	 */
	public EquipmentCollection getParent()
	{
		return d_parent;
	}

	/**
	 *  Gets the parentName of the Equipment object
	 *
	 *@return    The parentName
	 */
	public String getParentName()
	{
		Equipment anEquip = (Equipment)getParent();
		if (anEquip != null)
		{
			return anEquip.toString();
		}
		if (equipped)
		{
			return "Equipped";
		}
		if (numberCarried().intValue() > 0)
		{
			return "Carried";
		}
		return "";
	}

	/**
	 *  Adds a child to this Equipment
	 *
	 * Wonder why it accepts an Object?
	 *
	 *@param  child  The child to add
	 */
	public void insertChild(Object child)
	{
		if (child == null)
		{
			return;
		}

		Equipment anEquip = (Equipment)child;
		Float aFloat = anEquip.numberCarried();
		Float bFloat = aFloat;

		String aString = pickChildType(anEquip.eqTypeList(), aFloat);

		if (containsChildType(aString))
		{
			aFloat = new Float(getChildType(aString).floatValue() + aFloat.floatValue());
		}

		bFloat = new Float(getChildType("Total").floatValue() + bFloat.floatValue());
		setChildType(aString, aFloat);
		setChildType("Total", bFloat);
		addContainedEquipment(anEquip);
		anEquip.setIndexedUnderType(aString);
		anEquip.setParent(this);
		//hmm probably not needed; but as it currently isn't hurting anything...

		updateContainerContentsString();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			anEquip.updateContainerContentsString();
		}

	}

	/**
	 *  Returns true if the Equipment can take children.
	 *
	 *@return    true if the Equipment can take children.
	 */
	public boolean acceptsChildren()
	{
		return d_acceptsChildren;
	}

	/**
	 *  Returns 0 on object error, 1 on can fit, 2 on too heavy, 3 on properties problem (unimplemented), 4 on capacity error
	 *
	 *@param  obj  The equipment to check
	 *@return      0 on object error, 1 on can fit, 2 on too heavy, 3 on properties problem (unimplemented), 4 on capacity error
	 */
	public int canContain(Object obj)
	{
		if (obj instanceof Equipment)
		{

			Equipment anEquip = (Equipment)obj;

			if (checkChildWeight(new Float(anEquip.getWeight().floatValue() * anEquip.numberCarried().floatValue())))
			{
				// canHold(my HashMap()))  //quick hack since the properties hashmap doesn't exist

				if (checkContainerCapacity(anEquip.eqTypeList(), anEquip.numberCarried()))
				{
					//the qty value is a temporary hack - insert all or nothing.  should reset person to be a container, with capacity=capacity

					return 1;
				}
				else
				{
					return 4;
				}

			}
			else
			{
				return 2;
			}
		}
		return 0;
	}

	/**
	 *  Clears all child types
	 */
	public void clearChildTypes()
	{
		d_childTypes = null;
	}

	/**
	 *  Checks whether the child type is possessed
	 *
	 *@param  aString  Description of the Parameter
	 *@return          true if has child type
	 */
	private boolean containsChildType(String aString)
	{
		if (d_childTypes == null)
			return false;
		return d_childTypes.containsKey(aString);
	}

	/**
	 *  Gets the acceptsTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return          The acceptsTypes value
	 */
	private Float getChildType(String aString)
	{
		if (d_childTypes == null) return null;
		return (Float)d_childTypes.get(aString);
	}

	/**
	 *  Gets the number of child types
	 *
	 *@return  The number of distinct types
	 */
	private int getChildTypeCount()
	{
		if (d_childTypes == null) return 0;
		return d_childTypes.size();
	}

	/**
	 *  Sets the child type value
	 *
	 *@param  parameter  Description of the Parameter
	 *@param  childType child type
	 */
	public void setChildType(String parameter, Float childType)
	{
		if (d_childTypes == null)
			d_childTypes = new HashMap();
		d_childTypes.put(parameter, childType);
	}

	/**
	 *  Checks whether the proposed type is one that is accepted
	 *
	 *@param  aString  Description of the Parameter
	 *@return          The acceptsTypes value
	 */
	private boolean acceptsType(String aString)
	{
		if (d_acceptsTypes == null)
			return false;
		return d_acceptsTypes.containsKey(aString);
	}

	/**
	 *  Gets the number of accepted types
	 *
	 *@return  The number of distinct types
	 */
	private int getAcceptsTypeCount()
	{
		if (d_acceptsTypes == null) return 0;
		return d_acceptsTypes.size();
	}

	/**
	 *  Gets the acceptsTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return          The acceptsTypes value
	 */
	private Float getAcceptsType(String aString)
	{
		if (d_acceptsTypes == null) return null;
		return (Float)d_acceptsTypes.get(aString);
	}

	/**
	 *  Sets the acceptence of a type
	 *
	 *@param  parameter  Description of the Parameter
	 *@param  acceptsType Acceptance
	 */
	private void setAcceptsType(String parameter, Float acceptsType)
	{
		if (d_acceptsTypes == null)
			d_acceptsTypes = new HashMap();
		d_acceptsTypes.put(parameter, acceptsType);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aTypeList  Description of the Parameter
	 *@param  aQuant     Description of the Parameter
	 *@return            Description of the Return Value
	 */
	private String pickChildType(TreeSet aTypeList, Float aQuant)
	{
		String canContain = "";
		Float acceptsType = getAcceptsType("Total");
		//
		// Sanity check
		//
		if (acceptsType == null)
		{
			acceptsType = new Float(0);
		}
		if (getChildType("Total") == null)
		{
			setChildType("Total", new Float(0));
		}
		if ((getChildType("Total").floatValue() + aQuant.floatValue()) <= acceptsType.floatValue())
		{
			Iterator e = aTypeList.iterator();
			String aString = null;
			while (e.hasNext() && canContain.equals(""))
			{
				aString = (String)e.next();

				Globals.debugPrint("Testing ", aString);

				if (acceptsType(aString))
				{
					Globals.debugPrint("Accepts ", aString);

					if (containsChildType(aString))
					{
						if ((getChildType(aString).floatValue() + aQuant.floatValue()) <= getAcceptsType(aString).floatValue())
						{
							canContain = aString;
						}
					}
					else
					{
						if (aQuant.floatValue() <= getAcceptsType(aString).floatValue())
						{
							canContain = aString;
						}
					}
				}
			}

			if ((canContain.equals("")) && acceptsType("Any"))
			{
				if (!containsChildType("Any"))
				{
					setChildType("Any", new Float(0));
				}

				if ((getChildType("Any").floatValue() + aQuant.floatValue()) <= getAcceptsType("Any").floatValue())
				{
					canContain = "Any";
				}
			}

		}
		return canContain;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aTypeList  Description of the Parameter
	 *@param  aQuant     Description of the Parameter
	 *@return            Description of the Return Value
	 */
	private boolean checkContainerCapacity(TreeSet aTypeList, Float aQuant)
	{
		if (acceptsType("Any"))
		{
			if (getAcceptsType("Any").intValue() == -1)
			{
				return true;
			}
		}

		if (pickChildType(aTypeList, aQuant).equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aFloat  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	private boolean checkChildWeight(Float aFloat)
	{
		if ((aFloat.floatValue() + getContainedWeight().floatValue()) <= containerWeightCapacity.floatValue() || containerWeightCapacity.intValue() == -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  properties  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	public boolean canHold(HashMap properties)
	{
		return true;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  properties  Description of the Parameter
	 *@param  additive    Description of the Parameter
	 */
	public void updateProperties(HashMap properties, boolean additive)
	{
		// no action
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String longName()
	{
		if (longName.length() == 0)
		{
			return toString();
		}
		return longName;
	}

	/**
	 *  Sets the longName attribute of the Equipment object
	 *
	 *@param  aString  The new longName value
	 */
	public void setLongName(String aString)
	{
		longName = aString;
	}

	/**
	 *  Gets the baseCost attribute of the Equipment object
	 *
	 *@return    The baseCost value
	 */
	public BigDecimal getBaseCost()
	{
		return baseCost;
	}

	/**
	 *  Gets the cost attribute of the Equipment object
	 *
	 *@return    The cost value
	 */
	public BigDecimal getCost()
	{
		BigDecimal c = new BigDecimal("0");

		//
		// Do pre-sizing cost increment.
		// eg. in the case of adamantine armor, want to add the cost of the metal before the
		// armor gets resized.
		//
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(aEqMod.getPreCost(), "", "", true).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer.toString(getBaseQty() * iCount))));
		}
		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(aEqMod.getPreCost(), "", "", false).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer.toString(getBaseQty() * iCount))));
		}
		//
		// c has cost of the item's modifications at the item's original size
		//
		double mult = Globals.sizeAdjustmentCostMultiplier(getSize(), typeList()) / Globals.sizeAdjustmentCostMultiplier(getBaseSize(), typeList());
		c = c.multiply(new BigDecimal(mult));

		BigDecimal itemCost = cost.add(c);

		ArrayList modifierCosts = new ArrayList();

		BigDecimal nonDoubleCost = new BigDecimal("0");

		c = new BigDecimal("0");
		int iPlus = 0;
		int altPlus = 0;
		calculatingCost = true;
		weightAlreadyUsed = false;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}

			BigDecimal eqModCost;
			String costFormula = aEqMod.getCost();
			if ((aEqMod.getAssociatedCount() > 0) && !costFormula.equals(aEqMod.getCost(0)))
			{
				eqModCost = new BigDecimal("0");
				for (int idx = 0; idx < aEqMod.getAssociatedCount(); idx++)
				{
					costFormula = aEqMod.getCost(idx);
					BigDecimal thisModCost = new BigDecimal(getVariableValue(costFormula, "", "", true).toString());

					eqModCost = eqModCost.add(thisModCost);
					if (!aEqMod.getCostDouble())
					{
						nonDoubleCost = nonDoubleCost.add(thisModCost);
					}
					else
					{
						modifierCosts.add(thisModCost);
					}
				}
				iCount = 1;
			}
			else
			{
				eqModCost = new BigDecimal(getVariableValue(costFormula, "", "", true).toString());
				if (!aEqMod.getCostDouble())
				{
					nonDoubleCost = nonDoubleCost.add(eqModCost);
				}
				else
				{
					modifierCosts.add(eqModCost);
				}
			}
			c = c.add(eqModCost.multiply(new BigDecimal(Integer.toString(getBaseQty() * iCount))));
			iPlus += aEqMod.getPlus() * iCount;
		}
		//
		// Get costs from lowest to highest
		//
		if (modifierCosts.size() > 1)
		{
			Collections.sort(modifierCosts);
			Globals.debugPrint("" + modifierCosts);
		}

		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final String costFormula = aEqMod.getCost();
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(costFormula, "", "", false).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer.toString(getBaseQty() * iCount))));
			altPlus += aEqMod.getPlus() * iCount;
		}
		calculatingCost = false;

		//
		// Tack on the cost of the magical enhancement(s)
		//
		if ((iPlus != 0) || (altPlus != 0))
		{
			BigDecimal aPlus = new BigDecimal(Integer.toString(iPlus * iPlus));
			for (int i = 0; i < 2; i++)
			{
				if (aPlus.intValue() == 0)
				{
					continue;
				}

				if (isWeapon() || isAmmunition())
				{
					// Cost += iPlus * iPlus * 2000
					aPlus = aPlus.multiply(new BigDecimal("2000"));
					//
					// Ammo is made in batches of 50, so get the price for 1 unit
					//
					if (isAmmunition())
					{
						aPlus = aPlus.divide(new BigDecimal("50"), BigDecimal.ROUND_UNNECESSARY);
					}
					aPlus = aPlus.multiply(new BigDecimal(Integer.toString(getBaseQty())));
					c = c.add(aPlus);
				}
				//else if (isArmor() || isShield())
				else
				{
					// Cost += iPlus * iPlus * 1000
					aPlus = aPlus.multiply(new BigDecimal("1000"));
					c = c.add(aPlus);
				}
				aPlus = new BigDecimal(Integer.toString(altPlus * altPlus));
			}
		}

		//
		// Items with values less than 1 gp have their prices rounded up to 1 gp per item
		// eg. 20 Arrows cost 1 gp, or 5 cp each. 1 MW Arrow costs 7 gp.
		//
		// Masterwork and Magical ammo is made in batches of 50, so the MW cost per item
		// should be 6 gp. This would give a cost of 6.05 gp per arrow, 6.1 gp per bolt and 6.01 gp
		// per bullet.
		//
		if (c.compareTo(new BigDecimal("0")) != 0)
		{
			//
			// Convert to double and use math.ceil as ROUND_CEILING doesn't appear to work
			// on BigDecimal.divide
			final int baseQ = getBaseQty();
			itemCost = new BigDecimal(Math.ceil(itemCost.doubleValue() / baseQ) * baseQ);
		}

		if (!isAmmunition() && !isArmor() && !isShield() && !isWeapon())
		{
			//
			// If item doesn't occupy a fixed location, then double the cost of the modifications
			// DMG p.243
			//
			if (!isMagicLimitedType())
			{
				//
				// TODO: Multiple similar abilities. 100% of costliest, 75% of next, and 50% of rest
				//
				if (!ignoresCostDouble())
				{
					c = c.subtract(nonDoubleCost).multiply(new BigDecimal("2"));
					c = c.add(nonDoubleCost);
					//c = c.multiply(new BigDecimal("2"));
				}
			}
			else
			{
				//
				// Add in the cost of 2nd, 3rd, etc. modifiers again (gives times 2)
				//
				for (int i = modifierCosts.size() - 2; i > -1; i--)
				{
					c = c.add(((BigDecimal)modifierCosts.get(i)).movePointLeft(1));	// * 0.1
				}
			}
		}
		return c.add(itemCost).add(costMod);
	}

	/**
	 *  Sets the cost attribute of the Equipment object
	 *
	 *@param  aString  The new cost value
	 */
	public void setCost(String aString)
	{
		setCost(aString, false);
	}

	/**
	 *  Sets the cost attribute of the Equipment object
	 *
	 *@param  aString  The new cost value
	 *@param  bBase    The new cost value
	 */
	public void setCost(String aString, boolean bBase)
	{
		try
		{
			cost = new BigDecimal(aString);
			if (bBase)
			{
				baseCost = cost;
			}
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Gets the acMod attribute of the Equipment object
	 *
	 *@return    The acMod value
	 */
	public Integer getACMod()
	{
		int mod = bonusTo("EQMARMOR", "AC", true) + bonusTo("COMBAT", "AC", true);
		return new Integer(mod);
	}

	/**
	 *  Gets the maxDex attribute of the Equipment object
	 *
	 *@return    The maxDex value
	 */
	public Integer getMaxDex()
	{
		int mdex = maxDex.intValue() + bonusTo("EQMARMOR", "MAXDEX", true);
		if (mdex > 100)
		{
			mdex = 100;
		}
		if (mdex < 0)
		{
			mdex = 0;
		}
		return new Integer(mdex);
	}

	/**
	 *  Sets the maxDex attribute of the Equipment object
	 *
	 *@param  aString  The new maxDex value
	 */
	public void setMaxDex(String aString)
	{
		try
		{
			maxDex = Delta.decode(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Gets the DefBon attribute of the Equipment object
	 *
	 *@return    The DefBon value
	 */
	public Integer getDefBonus()
	{
		int dbon = DefBon.intValue() + bonusTo("EQMARMOR", "DEFBONUS", true);
		if (dbon > 100)
		{
			dbon = 100;
		}
		//if (dbon < 0)
		//{
		//	dbon = 0;
		//}
		return new Integer(dbon);
	}

	/**
	 *  Sets the DefBon attribute of the Equipment object
	 *
	 *@param  aString  The new DefBon value
	 */
	public void setDefBonus(String aString)
	{
		try
		{
			DefBon = Delta.decode(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Gets the containedWeight attribute of the Equipment object
	 *
	 *@return    The containedWeight value
	 */
	public Float getContainedWeight()
	{
		/*
 *  added by Emily Smirle (Syndaryl) 12.08.2001
 */
		return getContainedWeight(false);
	}

	/**
	 *  Gets the containedWeight attribute of the Equipment object
	 *
	 *@param  effective  Description of the Parameter
	 *@return            The containedWeight value
	 */
	public Float getContainedWeight(boolean effective)
	{
		/*
 *  modified by Emily Smirle (Syndaryl) 12.08.2001
 */
		Float total = new Float(0);

		if ((containerConstantWeight && !effective) || getChildCount() == 0)
		{
			return total;
		}
		else
		{
			for (int e = 0; e < getContainedEquipmentCount(); e++)
			{
				Equipment anEquip = getContainedEquipment(e);
				if (anEquip.getContainedEquipmentCount() > 0)
					total = new Float(total.floatValue() + anEquip.getWeight().floatValue() + anEquip.getContainedWeight().floatValue());
				else
					total = new Float(total.floatValue() + anEquip.getWeight().floatValue() * anEquip.getCarried().floatValue());
			}
		}

		return total;
	}

	/**
	 *  Sets the container attribute of the Equipment object
	 *
	 *@param  aString  The new container value
	 */
	public void setContainer(String aString)
	{
		//-1 means unlimited
		boolean limited = true;
		Float aFloat = new Float(0);
		d_acceptsChildren = true;
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if (bString.startsWith("*"))
			{
				containerConstantWeight = true;
				bString = bString.substring(1);
			}
			containerWeightCapacity = new Float(bString);
		}
		else
		{
			containerWeightCapacity = new Float(-1);
		}

		if (!aTok.hasMoreTokens())
		{
			limited = false;
			setAcceptsType("Any", new Float(-1));
		}
		String itemType = null;
		Float itemNumber = null;
		while (aTok.hasMoreTokens())
		{
			final StringTokenizer typeTok = new StringTokenizer(aTok.nextToken(), "=", false);
			itemType = typeTok.nextToken();
			itemNumber = new Float(0);
			if (typeTok.hasMoreTokens())
			{
				itemNumber = new Float(typeTok.nextToken());

				if (limited)
				{
					aFloat = new Float(aFloat.floatValue() + itemNumber.floatValue());
				}
			}
			else
			{
				limited = false;
				itemNumber = new Float(-1);
			}
			if (!itemType.equals("Any") && !itemType.equals("Total"))
				setAcceptsType(itemType.toUpperCase(), itemNumber);
			else
				setAcceptsType(itemType, itemNumber);
		}

		if (!acceptsType("Total"))
		{
			if (!limited)
			{
				aFloat = new Float(-1);
			}
			setAcceptsType("Total", aFloat);
		}

		updateContainerCapacityString();
		updateContainerContentsString();
	}

	/**
	 *  Description of the Method
	 */
	private void updateContainerCapacityString()
	{
		StringBuffer tempStringBuffer = new StringBuffer();
		boolean comma = false;

		if (containerWeightCapacity.intValue() != -1)
		{
			tempStringBuffer.append(containerWeightCapacity).append(" ").append(Globals.getWeightDisplay());
			comma = true;
		}

		if (getAcceptsTypeCount() > 0)
		{
			for (Iterator e = d_acceptsTypes.keySet().iterator(); e.hasNext();)
			{
				if (comma)
				{
					tempStringBuffer.append(", ");
					comma = false;
				}
				String aString = (String)e.next();

				if (getAcceptsType(aString).intValue() != -1)
				{
					tempStringBuffer.append(getAcceptsType(aString).floatValue()).append(" ");
					tempStringBuffer.append(aString);
					comma = true;
				}
				else if (!aString.equals("Total"))
				{
					comma = true;
					tempStringBuffer.append(aString);
				}
			}
		}
		containerCapacityString = tempStringBuffer.toString();
	}

	/**
	 *  Description of the Method
	 */
	public void updateContainerContentsString()
	{
		containerContentsString = "";
		StringBuffer tempStringBuffer = new StringBuffer(getChildCount() * 20);
		// Make sure there's no bug here.
		if (acceptsChildren())
		{
			tempStringBuffer.append(getContainedWeight(true)).append(" ").append(Globals.getWeightDisplay());
			/*
			*  Modified by Emily Smirle (Syndaryl)
			*/
		}

		for (int e = 0; e < getChildCount(); e++)
		{
			final Equipment anEquip = (Equipment)getChild(e);
			if (anEquip.getCarried().compareTo(new Float(0)) > 0)
			{
				tempStringBuffer.append(", ").append(anEquip.numberCarried()).append(" ").append(anEquip);
			}
		}
		containerContentsString = tempStringBuffer.toString();
	}

	/**
	 *  Gets the containerCapacityString attribute of the Equipment object
	 *
	 *@return    The containerCapacityString value
	 */
	public String getContainerCapacityString()
	{
		return containerCapacityString;
	}

	/**
	 *  Gets the containerContentsString attribute of the Equipment object
	 *
	 *@return    The containerContentsString value
	 */
	public String getContainerContentsString()
	{
		return containerContentsString;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Integer acCheck()
	{
		int check = acCheck.intValue() + bonusTo("EQMARMOR", "ACCHECK", true);
		if (check > 0)
		{
			check = 0;
		}
		return new Integer(check);
	}

	/**
	 *  Sets the acCheck attribute of the Equipment object
	 *
	 *@param  aString  The new acCheck value
	 */
	public void setACCheck(String aString)
	{
		try
		{
			acCheck = new Integer(aString);
		}
		catch (NumberFormatException nfe)
		{
			acCheck = new Integer(0);
			// ignore
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Integer spellFailure()
	{
		int fail = spellFailure.intValue() + bonusTo("EQMARMOR", "SPELLFAILURE", true);
		if (fail < 0)
		{
			fail = 0;
		}
		return new Integer(fail);
	}

	/**
	 *  Sets the spellFailure attribute of the Equipment object
	 *
	 *@param  aString  The new spellFailure value
	 */
	public void setSpellFailure(String aString)
	{
		try
		{
			spellFailure = Delta.decode(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String moveString()
	{
		if (moveString.length() > 0)
		{
			if (!Globals.isStarWarsMode())
			{
				int eqLoad;
				if (isHeavy())
				{
					eqLoad = Constants.HEAVY_LOAD;
				}
				else if (isMedium())
				{
					eqLoad = Constants.MEDIUM_LOAD;
				}
				else if (isLight())
				{
					eqLoad = Constants.LIGHT_LOAD;
				}
				else
				{
					eqLoad = Constants.OVER_LOAD;
				}

				//
				// This will generate a list for base moves 30,20 or 60,50,40 depending on how many tokens are
				// in the original tag
				//
				StringTokenizer aTok = new StringTokenizer(moveString, ",");
				int baseMove = -1;
				int tokenCount = aTok.countTokens();
				switch (tokenCount)
				{
					case 2:
						baseMove = 30;
						break;
					case 3:
						baseMove = 60;
						break;
					default:
						tokenCount = -1;
						break;
				}
				if (tokenCount > 0)
				{
					StringBuffer retString = new StringBuffer(moveString.length());
					for (int i = 0; i < tokenCount; i++)
					{
						if (i != 0)
						{
							retString.append(',');
						}
						retString.append(Integer.toString(Globals.calcEncumberedMove(eqLoad, baseMove, true)));
						baseMove -= 10;
					}
					return retString.toString();
				}
			}
		}
		return moveString;
	}

	/**
	 *  Sets the moveString attribute of the Equipment object
	 *
	 *@param  aString  The new moveString value
	 */
	public void setMoveString(String aString)
	{
		moveString = aString;
	}

	/**
	 *  Gets the weight attribute of the Equipment object
	 *
	 *@return    The weight value
	 */
	public Float getWeight()
	{
		int f = bonusTo("EQM", "WEIGHTMULT", true);
		if (f == 0)
		{
			f = 1;
		}
		double aWeight = weight.doubleValue() * f;

		f = bonusTo("EQM", "WEIGHTDIV", true);
		if (f == 0)
		{
			f = 1;
		}
		aWeight /= f;

		aWeight += bonusTo("EQM", "WEIGHTADD", true);
		aWeight += weightMod.doubleValue();

		return new Float(aWeight);
	}

	/**
	 *  Sets the weight attribute of the Equipment object
	 *
	 *@param  aString  The new weight value
	 */
	public void setWeight(String aString)
	{
		try
		{
			weight = new Float(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aString  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	public boolean typeStringContains(String aString)
	{
		return isType(aString);
	}

	/**
	 *  Sets the typeString attribute of the Equipment object
	 *
	 *@param  aString  The new typeString value
	 */
	public void setTypeString(String aString)
	{
		setType(aString);

		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase(), ".", false);
		while (aTok.hasMoreTokens())
		{
			final String type = aTok.nextToken();
			s_equipmentTypes.add(type);
		}
		if (isWeapon())
		{
			if (getCritRange().length() == 0)
			{
				setCritRange("1");
			}
			if (getCritMult().length() == 0)
			{
				setCritMult("x2");
			}
		}
	}

	/**
	 *  Sets the altTypeList attribute of the Equipment object
	 *
	 *@param  aString  The new altTypeList value
	 */
	public void setAltTypeList(String aString)
	{
		aString = aString.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String type = aTok.nextToken();
			addAltType(type);
		}
	}

	public int getAltTypeCount()
	{
		if (altTypeList == null) return 0;
		return altTypeList.size();
	}

	public void addAltType(String type)
	{
		if (altTypeList == null) altTypeList = new ArrayList(1);
		altTypeList.add(type);
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String modifiedName()
	{
		return modifiedName;
	}

	/**
	 *  Sets the modifiedName attribute of the Equipment object
	 *
	 *@param  nameString  The new modifiedName value
	 */
	public void setModifiedName(String nameString)
	{
		modifiedName = nameString;
	}

	/**
	 *  Gets the size attribute of the Equipment object
	 *
	 *@return    The size value
	 */
	public String getSize()
	{
		return size;
	}

	/**
	 *  Gets the baseSize attribute of the Equipment object
	 *
	 *@return    The baseSize value
	 */
	public String getBaseSize()
	{
		return sizeBase;
	}

	/**
	 *  Sets the size attribute of the Equipment object
	 *
	 *@param  sizeString  The new size value
	 */
	public void setSize(String sizeString)
	{
		setSize(sizeString, false);
	}

	/**
	 *  Sets the size attribute of the Equipment object
	 *
	 *@param  sizeString  The new size value
	 *@param  bBase       The new size value
	 */
	public void setSize(String sizeString, boolean bBase)
	{
		sizeString = sizeString.toUpperCase().substring(0, 1);
		size = sizeString;
		if (bBase)
		{
			sizeBase = sizeString;
		}
	}

	private String getWeaponInfo(String infoType, boolean bPrimary)
	{
		final String it = infoType + "|";
		final EquipmentModifier eqMod = getEqModifierKeyed("PCGENi_WEAPON", bPrimary);
		if (eqMod != null)
		{
			for (int i = 0; i < eqMod.getAssociatedCount(); i++)
			{
				final String aString = eqMod.getAssociated(i);
				if (aString.startsWith(it))
				{
					return aString.substring(it.length());
				}
			}
		}
		return "";
	}

	/**
	 *  Gets the damage attribute of the Equipment object
	 *
	 *@return    The damage value
	 */
	public String getDamage()
	{
		return getDamage(true);
	}

	public String getDamage(boolean bPrimary)
	{
		String baseDamage = bPrimary ? damage : altDamage;
		if (!isWeapon() || (!bPrimary && !isDouble()))
		{
			return baseDamage;
		}

		if (baseDamage.length() == 0)
		{
			baseDamage = getWeaponInfo("DAMAGE", bPrimary);
		}

		final int iSize = Globals.sizeInt(getSize());
		int iMod = iSize + bonusTo("EQMWEAPON", "DAMAGESIZE", bPrimary);
		if (iMod < Constants.SIZE_F)
		{
			iMod = Constants.SIZE_F;
		}
		else if (iMod > Constants.SIZE_C)
		{
			iMod = Constants.SIZE_C;
		}
		return adjustDamage(baseDamage, Constants.s_SIZESHORT[iMod]);
	}

	/**
	 *  Sets the damage attribute of the Equipment object
	 *
	 *@param  aString  The new damage value
	 */
	public void setDamage(String aString)
	{
		damage = aString;
	}

	/**
	 *  Gets the bonusToHit attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The bonusToHit value
	 */
	public int getBonusToHit(boolean bPrimary)
	{
		return bonusTo("WEAPON", "TOHIT", bPrimary);
	}

	/**
	 *  Gets the bonusToDamage attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The bonusToDamage value
	 */
	public int getBonusToDamage(boolean bPrimary)
	{
		return bonusTo("WEAPON", "DAMAGE", bPrimary);
	}

	/**
	 *  Gets the critRangeDouble attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The critRangeDouble value
	 */
	public int getCritRangeDouble(boolean bPrimary)
	{
		return bonusTo("EQMWEAPON", "CRITRANGEDOUBLE", bPrimary);
	}

	/**
	 *  Gets the critRangeAdd attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The critRangeAdd value
	 */
	public int getCritRangeAdd(boolean bPrimary)
	{
		return bonusTo("EQMWEAPON", "CRITRANGEADD", bPrimary);
	}

	/**
	 *  Gets the rawCritRange attribute of the Equipment object
	 *
	 *@return    The rawCritRange value
	 */
	public int getRawCritRange()
	{
		String cr = critRange;
		if (cr.length() == 0)
		{
			cr = getWeaponInfo("CRITRANGE", true);
		}
		if (cr.length() != 0)
		{
			try
			{
				return Integer.parseInt(cr);
			}
			catch (NumberFormatException nfe)
			{
				//ignore
			}
		}
		return 0;
	}

	/**
	 *  Gets the critRange attribute of the Equipment object
	 *
	 *@return    The critRange value
	 */
	public String getCritRange()
	{
		return getCritRange(true);
	}

	public String getCritRange(boolean bPrimary)
	{
		String cr = critRange;
		if (cr.length() == 0)
		{
			cr = getWeaponInfo("CRITRANGE", bPrimary);
		}
		if ((cr.length() == 0) || (!bPrimary && !isDouble()))
		{
			return "";
		}

		return Integer.toString(getRawCritRange() * (getCritRangeDouble(bPrimary) + 1) + getCritRangeAdd(bPrimary));
	}


	//
	// This can be different if one head is Keen and the other is not
	//
	/**
	 *  Gets the altCritRange attribute of the Equipment object
	 *
	 *@return    The altCritRange value
	 */
	public String getAltCritRange()
	{
		return getCritRange(false);
	}

	/**
	 *  Sets the critRange attribute of the Equipment object
	 *
	 *@param  aString  The new critRange value
	 */
	public void setCritRange(String aString)
	{
		critRange = aString;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  mult  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public String multAsString(int mult)
	{
		if (mult == 0)
		{
			return "";
		}
		else if (mult < 0)
		{
			return "-";
		}

		return "x" + Integer.toString(mult);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  mult  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public int multAsInt(int mult)
	{
		if (mult < 0)
		{
			return 0;
		}
		return mult;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aString  Description of the Parameter
	 *@return          Description of the Return Value
	 */
	private int parseCritMult(String aString)
	{
		if (aString.startsWith("x"))
		{
			try
			{
				return Integer.parseInt(aString.substring(1));
			}
			catch (NumberFormatException nfe)
			{
				Globals.debugPrint("parseCritMult:" + getName() + ":" + aString);
			}
		}
		return -1;
	}

	private int getCritMultiplier(boolean bPrimary)
	{
		int mult = bPrimary ? critMult : altCritMult;
		if (mult == 0)
		{
			final String cm = getWeaponInfo("CRITMULT", bPrimary);
			if (cm.length() != 0)
			{
				mult = Integer.parseInt(cm);
			}
		}
		return mult;
	}

	/**
	 *  Gets the critMult attribute of the Equipment object
	 *
	 *@return    The critMult value
	 */
	public String getCritMult()
	{
		return multAsString(getCritMultiplier(true));
	}

	/**
	 *  Gets the critMultiplier attribute of the Equipment object
	 *
	 *@return    The critMultiplier value
	 */
	public int getCritMultiplier()
	{
		return multAsInt(getCritMultiplier(true));
	}

	/**
	 *  Sets the critMult attribute of the Equipment object
	 *
	 *@param  aString  The new critMult value
	 */
	public void setCritMult(String aString)
	{
		critMult = parseCritMult(aString);
	}

	/**
	 *  Gets the altCritMult attribute of the Equipment object
	 *
	 *@return    The altCritMult value
	 */
	public String getAltCritMult()
	{
		//
		// Use primary if none defined
		//
		if (altCritMult == 0)
		{
			return getCritMult();
		}
		return multAsString(getCritMultiplier(false));
	}

	/**
	 *  Gets the altCritMultiplier attribute of the Equipment object
	 *
	 *@return    The altCritMultiplier value
	 */
	public int getAltCritMultiplier()
	{
		//
		// Use primary if none defined
		//
		if (altCritMult == 0)
		{
			return getCritMultiplier();
		}
		return multAsInt(getCritMultiplier(false));
	}

	/**
	 *  Sets the altCrit attribute of the Equipment object
	 *
	 *@param  aString  The new altCrit value
	 */
	public void setAltCrit(String aString)
	{
		altCritMult = parseCritMult(aString);
	}

	/**
	 *  Gets the range attribute of the Equipment object
	 *
	 *@return    The range value
	 */
	public Integer getRange()
	{
		Integer myRange = range;
		if (myRange.intValue() == 0)
		{
			final String aRange = getWeaponInfo("RANGE", true);
			if (aRange.length() != 0)
			{
				myRange = new Integer(aRange);
			}
		}

		int r = myRange.intValue() + bonusTo("EQMWEAPON", "RANGEADD", true);
		int i = bonusTo("EQMWEAPON", "RANGEMULT", true);
		double rangeMult = 1.0;
		if (i > 0)
		{
			rangeMult += i - 1;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int postAdd = 0;
		if (aPC != null)
		{
			if (isThrown())
			{
				r += aPC.getTotalBonusTo("RANGEADD", "THROWN", true);
				postAdd = aPC.getTotalBonusTo("POSTRANGEADD", "THROWN", true);
				rangeMult += aPC.getTotalBonusTo("RANGEMULT", "THROWN", true) / 100.0;
			}
			else if (isProjectile())
			{
				r += aPC.getTotalBonusTo("RANGEADD", "PROJECTILE", true);
				postAdd = aPC.getTotalBonusTo("POSTRANGEADD", "PROJECTILE", true);
				rangeMult += aPC.getTotalBonusTo("RANGEMULT", "PROJECTILE", true) / 100.0;
			}
		}
		r *= rangeMult;
		r += postAdd;
		return new Integer(r);
	}

	/**
	 *  Sets the range attribute of the Equipment object
	 *
	 *@param  aString  The new range value
	 */
	public void setRange(String aString)
	{
		try
		{
			range = Delta.decode(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	/**
	 *  Gets the attacks attribute of the Equipment object
	 *
	 *@return    The attacks value
	 */
	public Integer getAttacks()
	{
		return attacks;
	}

	public void setAttacks(Integer argAttacks)
	{
		attacks = argAttacks;
	}

	/**
	 *  Gets the bonusType attribute of the Equipment object
	 *
	 *@return    The bonusType value
	 */
	public String getBonusType()
	{
		return bonusType;
	}

	/**
	 *  Sets the bonusType attribute of the Equipment object
	 *
	 *@param  aString  The new bonusType value
	 */
	public void setBonusType(String aString)
	{
		bonusType = aString;
	}

	/**
	 *  Returns the type list as an ArrayList
	 *
	 *@return    the type list as an ArrayList
	 */
	public ArrayList typeArrayList()
	{
		return typeList();
	}

	/**
	 *  Gets the standard attribute of the Equipment object
	 *
	 *@return    The standard value
	 */
	public boolean isStandard()
	{
		return isType("STANDARD");
	}

	/**
	 *  Gets the wooden attribute of the Equipment object
	 *
	 *@return    The wooden value
	 */
	public boolean isWooden()
	{
		return isType("WOODEN");
	}

	/**
	 *  Gets the melee attribute of the Equipment object
	 *
	 *@return    The melee value
	 */
	public boolean isMelee()
	{
		return isType("MELEE");
	}

	/**
	 *  Gets the ranged attribute of the Equipment object
	 *
	 *@return    The ranged value
	 */
	public boolean isRanged()
	{
		return isType("RANGED");
	}

	/**
	 *  Gets the thrown attribute of the Equipment object
	 *
	 *@return    The thrown value
	 */
	public boolean isThrown()
	{
		return isType("THROWN");
	}

	/**
	 *  Gets the projectile attribute of the Equipment object
	 *
	 *@return    The projectile value
	 */
	public boolean isProjectile()
	{
		//return isType("PROJECTILE");
		return isRanged() && !isThrown();
	}

	/**
	 *  Gets the magic attribute of the Equipment object
	 *
	 *@return    The magic value
	 */
	public boolean isMagic()
	{
		return isType("MAGIC");
	}

	/**
	 *  Gets the masterwork attribute of the Equipment object
	 *
	 *@return    The masterwork value
	 */
	public boolean isMasterwork()
	{
		return isType("MASTERWORK");
	}

	/**
	 *  Gets the armor attribute of the Equipment object
	 *
	 *@return    The armor value
	 */
	public boolean isArmor()
	{
		return isType("ARMOR");
	}

	/**
	 *  Gets the weapon attribute of the Equipment object
	 *
	 *@return    The weapon value
	 */
	public boolean isWeapon()
	{
		return isType("WEAPON");
	}

	/**
	 *  Gets the ammunition attribute of the Equipment object
	 *
	 *@return    The ammunition value
	 */
	public boolean isAmmunition()
	{
		return isType("AMMUNITION");
	}

	/**
	 *  Gets the natural attribute of the Equipment object
	 *
	 *@return    The natural value
	 */
	public boolean isNatural()
	{
		return isType("NATURAL");
	}

	/**
	 *  Gets the shield attribute of the Equipment object
	 *
	 *@return    The shield value
	 */
	public boolean isShield()
	{
		return isType("SHIELD");
	}

	/**
	 *  Gets the extra attribute of the Equipment object
	 *
	 *@return    The extra value
	 */
	public boolean isExtra()
	{
		return isType("EXTRA");
	}

	/**
	 *  Gets the light attribute of the Equipment object
	 *
	 *@return    The light value
	 */
	public boolean isLight()
	{
		return isType("LIGHT");
	}

	/**
	 *  Gets the medium attribute of the Equipment object
	 *
	 *@return    The medium value
	 */
	public boolean isMedium()
	{
		return isType("MEDIUM");
	}

	/**
	 *  Gets the heavy attribute of the Equipment object
	 *
	 *@return    The heavy value
	 */
	public boolean isHeavy()
	{
		return isType("HEAVY");
	}

	/**
	 *  Gets the suit attribute of the Equipment object
	 *
	 *@return    The suit value
	 */
	public boolean isSuit()
	{
		return isType("SUIT");
	}

	/**
	 *  Gets the double attribute of the Equipment object
	 *
	 *@return    The double value
	 */
	public boolean isDouble()
	{
		return isType("DOUBLE");
	}

	/**
	 *  Gets the bludgeoning attribute of the Equipment object
	 *
	 *@return    The bludgeoning value
	 */
	public boolean isBludgeoning()
	{
		return isBludgeoning(true);
	}

	/**
	 *  Gets the bludgeoning attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The bludgeoning value
	 */
	public boolean isBludgeoning(boolean bPrimary)
	{
		return isType("BLUDGEONING", bPrimary);
	}

	/**
	 *  Gets the piercing attribute of the Equipment object
	 *
	 *@return    The piercing value
	 */
	public boolean isPiercing()
	{
		return isPiercing(true);
	}

	/**
	 *  Gets the piercing attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The piercing value
	 */
	public boolean isPiercing(boolean bPrimary)
	{
		return isType("PIERCING", bPrimary);
	}

	/**
	 *  Gets the slashing attribute of the Equipment object
	 *
	 *@return    The slashing value
	 */
	public boolean isSlashing()
	{
		return isSlashing(true);
	}

	/**
	 *  Gets the slashing attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The slashing value
	 */
	public boolean isSlashing(boolean bPrimary)
	{
		return isType("SLASHING", bPrimary);
	}

	/**
	 *  Gets the simple attribute of the Equipment object
	 *
	 *@return    The simple value
	 */
	public boolean isSimple()
	{
		return isType("SIMPLE");
	}

	/**
	 *  Gets the martial attribute of the Equipment object
	 *
	 *@return    The martial value
	 */
	public boolean isMartial()
	{
		return isType("MARTIAL");
	}

	/**
	 *  Gets the exotic attribute of the Equipment object
	 *
	 *@return    The exotic value
	 */
	public boolean isExotic()
	{
		return isType("EXOTIC");
	}

	/**
	 *  Gets the monk attribute of the Equipment object
	 *
	 *@return    The monk value
	 */
	public boolean isMonk()
	{
		return isType("MONK");
	}

	/**
	 *  Gets the unarmed attribute of the Equipment object
	 *
	 *@return    The unarmed value
	 */
	public boolean isUnarmed()
	{
		return isType("UNARMED");
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	private TreeSet eqTypeList()
	{
		return new TreeSet(typeList());
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Float qty()
	{
		if (!isHeaderParent)
		{
			return qty;
		}

		float aQty = 0;
		for (int i = 0; i < getHeaderChildCount(); i++)
		{
			//could just report d_headerChildren.size(), but this is safer.

			aQty += getHeaderChild(i).qty().floatValue();
		}

		return new Float(aQty);
	}

	/**
	 *  Sets the qty attribute of the Equipment object
	 *
	 *@param  aString  The new qty value
	 */
	public void setQty(String aString)
	{
		if (isHeaderParent)
		{
			qty = qty();
		}
		else
		{
			try
			{
				qty = new Float(aString);
			}
			catch (java.lang.NumberFormatException nfe)
			{
				qty = new Float(0);
			}
		}
	}

	/**
	 *  Sets the qty attribute of the Equipment object
	 *
	 *@param  aFloat  The new qty value
	 */
	public void setQty(Float aFloat)
	{
		if (!isHeaderParent)
		{
			qty = aFloat;
		}
		else
		{
			qty = qty();
		}
	}

	public void setProfName(String aString)
	{
		profName = aString;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String rawProfName()
	{
		return profName;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String profName()
	{
		return profName(getHand());
	}

	/**
	 *  Description of the Method
	 *
	 *@param  hand  int that equals number of hands
	 *@return       returns weapon proficiency string
	 */
	public String profName(final int hand)
	{
		String aWProf = profName;
		if (aWProf.length() == 0)
		{
			aWProf = getName();
		}

		final int iOffs = aWProf.indexOf("[Hands]");
		if (iOffs >= 0)
		{
			//
			// Generate weapon proficiency name based on number of hands of race
			// eg. for:
			// "Sword (Bastard/[Hands])"
			// we should get either:
			// "Sword (Bastard/1-H)" or "Sword (Bastard/2-H)"
			//
			// The basic logic should be: if the weapon is Light,
			// or is one-handed and being used in two hands,
			// then use the 2-H proficiency.
			// The reason being that the 2-H version of these profs
			// will be lesser than the 1-H version (2-H is probably
			// Martial, and 1-H is exotic. For races which can only
			// use these weapons in 2 hands, the prof must be the
			// harder one (probably exotic).
			// Bryan McRoberts (merton_monk@yahoo.com) 10/20/01
			//
			final String w1String = aWProf.substring(0, iOffs) + "1-H" + aWProf.substring(iOffs + 7);
			final String w2String = aWProf.substring(0, iOffs) + "2-H" + aWProf.substring(iOffs + 7);
			final WeaponProf wp = Globals.getWeaponProfNamed(w1String);
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if ((wp == null) || Globals.isWeaponLightForPC(aPC, this) || (Globals.isWeaponOneHanded(aPC, this, wp) && (hand == BOTH_HANDS)))
			{
				aWProf = w2String;
			}
			else
			{
				aWProf = w1String;
			}

		}
		return aWProf;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Float numberCarried()
	{
		if (!isHeaderParent)
		{
			Equipment eqParent = (Equipment)getParent();
			if (isEquipped() || (eqParent == null))
			{
				return carried;
			}
			for (; eqParent != null; eqParent = (Equipment)eqParent.getParent())
			{
				if (eqParent.isEquipped() || ((eqParent.getParent() == null) && (eqParent.numberCarried().intValue() != 0)))
				{
					return carried;
				}
			}
			return new Float(0);
		}

		float aQty = 0;
		for (int i = 0; i < getHeaderChildCount(); i++)
		{
			final Equipment eq = getHeaderChild(i);
			Equipment eqParent = (Equipment)eq.getParent();
			if (eq.isEquipped() || (eqParent == null))
			{
				aQty += eq.numberCarried().floatValue();
			}
			else
			{
				for (; eqParent != null; eqParent = (Equipment)eqParent.getParent())
				{
					if (eqParent.isEquipped() || ((eqParent.getParent() == null) && (eqParent.numberCarried().intValue() != 0)))
					{
						aQty += eq.numberCarried().floatValue();
						break;
					}
				}
			}
		}

		return new Float(aQty);
	}

	/**
	 *  Gets the equipped attribute of the Equipment object
	 *
	 *@return    The equipped value
	 */
	public boolean isEquipped()
	{
		return equipped;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  o  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public int compareTo(Object o)
	{
		Equipment e = (Equipment)o;
		return this.keyName.compareTo(e.keyName);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  o  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public boolean equals(Object o)
	{
		return (o != null) && (o instanceof Equipment) &&
		  ((o == (Object)this) ||
		  this.keyName.equals(((Equipment)o).keyName));
	}

	public boolean equalTo(Object o)
	{
		return super.equals(o);
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public int hashCode()
	{
		return this.keyName.hashCode();
	}

	/**
	 *  Return the set of equipment type names as a sorted set of strings.
	 *
	 *@return    The equipmentTypes value
	 */
	public static SortedSet getEquipmentTypes()
	{
		return s_equipmentTypes;
	}

	/**
	 *  Description of the Method
	 */
	public static void clearEquipmentTypes()
	{
		s_equipmentTypes.clear();
	}

	/**
	 * Returns the list of virtual feats this item bestows upon its weilder.
	 *
	 *@return		ArrayList of virtual feats this item bestows upon its weilder.
	 */
	public ArrayList getVFeatList()
	{
		return vFeatList;
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder.
	 * @param vList a | delimited list of feats to add to the list

	 */
	public void addVFeatList(String vList)
	{
		final StringTokenizer aTok = new StringTokenizer(vList, "|", false);
		while (aTok.hasMoreTokens())
			vFeatList.add(aTok.nextToken());
	}

	/**
	 *  Return the list of elements from eqList that are weapons in which the
	 *  current player character is proficient.
	 *
	 *@param  eqList  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public static ArrayList selectProficientWeapons(Collection eqList)
	{
		ArrayList result = new ArrayList();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			result.ensureCapacity(eqList.size() / 10);
			Iterator i = eqList.iterator();

			Equipment e = null;
			while (i.hasNext())
			{
				e = (Equipment)i.next();
				if (aPC.isProficientWith(e))
				{
					result.add(e);
				}
			}
		}
		return result;
	}

	/**
	 *  Return the list of elements from eqList that are marked as equipped.
	 *
	 *@param  eqList  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public static ArrayList selectEquipped(Collection eqList)
	{
		ArrayList result = new ArrayList(eqList.size() / 2);
		Iterator i = eqList.iterator();

		Equipment e = null;
		while (i.hasNext())
		{
			e = (Equipment)i.next();
			if (e.isEquipped())
			{
				result.add(e);
			}
		}
		return result;
	}

	/**
	 *  Select a subset of a list of equipment base on the type string. If
	 *  <code>eqList</code> is sorted, the result will be sorted as well.
	 *
	 *@param  eqList  Description of the Parameter
	 *@param  type    Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public static ArrayList selectEquipment(Collection eqList, String type)
	{
		ArrayList result = new ArrayList();
		if (s_equipmentTypes.contains(type))
		{
			Iterator i = eqList.iterator();
			result.ensureCapacity(eqList.size() / 5);
			Equipment e = null;
			while (i.hasNext())
			{
				e = (Equipment)i.next();
				if (e.isType(type))
				{
					result.add(e);
				}
			}
		}
		return result;
	}

	/**
	 *  Sets the numberCarried attribute of the Equipment object
	 *
	 *@param  aNumber  The new numberCarried value
	 */
	public void setNumberCarried(Float aNumber)
	{
		if (!isHeaderParent)
		{
			carried = aNumber;
		}
		else
		{
			carried = numberCarried();
		}
	}

	/**
	 *  Gets the numberEquipped attribute of the Equipment object
	 *
	 *@return    The numberEquipped value
	 */
	public int getNumberEquipped()
	{
		return numberEquipped;
	}

	/**
	 *  Sets the numberEquipped attribute of the Equipment object
	 *
	 *@param  num  The new numberEquipped value
	 */
	public void setNumberEquipped(int num)
	{
		numberEquipped = num;
	}

	/**
	 *  Gets the reach attribute of the Equipment object
	 *
	 *@return    The reach value
	 */
	public int getReach()
	{
		return reach;
	}

	/**
	 *  Sets the reach attribute of the Equipment object
	 *
	 *@param  newReach  The new reach value
	 */
	public void setReach(int newReach)
	{
		reach = newReach;
	}

	/**
	 *  Sets the isEquipped attribute of the Equipment object
	 *
	 *@param  aFlag  The new isEquipped value
	 */
	public void setIsEquipped(boolean aFlag)
	{
		setIsEquipped(aFlag, inHand);
	}

	/**
	 *  Sets the isEquipped attribute of the Equipment object
	 *
	 *@param  aFlag  The new isEquipped value
	 *@param  hand   The new isEquipped value
	 */
	public void setIsEquipped(boolean aFlag, int hand)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aFlag != equipped)
		{
			aPC.setVirtualFeatsStable(false);
			aPC.setAggregateFeatsStable(false);
		}
		if (aFlag)
		{
			if (isType("HEADGEAR"))
			{
				equipped = aPC.canEquip("HEADGEAR");
			}
			else if (isType("EYEGEAR"))
			{
				equipped = aPC.canEquip("EYEGEAR");
			}
			else if (isType("CAPE"))
			{
				equipped = aPC.canEquip("CAPE");
			}
			else if (isType("AMULET"))
			{
				equipped = aPC.canEquip("AMULET");
			}
			else if (isSuit())
			{
				equipped = aPC.canEquip("SUIT");
			}
			else if (isType("ROBE"))
			{
				equipped = aPC.canEquip("ROBE");
			}
			else if (isType("SHIRT"))
			{
				equipped = aPC.canEquip("SHIRT");
			}
			else if (isType("BRACER"))
			{
				equipped = aPC.canEquip("BRACER");
			}
			else if (isType("GLOVE"))
			{
				equipped = aPC.canEquip("GLOVE");
			}
			else if (isType("RING"))
			{
				equipped = aPC.canEquip("RING");
			}
			else if (isType("BELT"))
			{
				equipped = aPC.canEquip("BELT");
			}
			else if (isType("BOOT"))
			{
				equipped = aPC.canEquip("BOOT");
			}
			else if ((isWeapon() && aPC.canEquip("Weapon")) ||
			  (isShield() && aPC.canEquip("Shield")))
			{
				int iRaceHands = aPC.getRace().getHands();
				int handsInUse = aPC.handsFull();
				if (isWeapon())
				{
					String aWProf = profName(hand);
					WeaponProf wp = Globals.getWeaponProfNamed(aWProf);
					if (wp == null)
					{
						final int idx = aWProf.indexOf('(');
						if (idx > 0)
						{
							aWProf = aWProf.substring(0, idx).trim();
							wp = Globals.getWeaponProfNamed(aWProf);
						}
					}
					if (wp == null)
					{
						equipped = false;
						GuiFacade.showMessageDialog(null, "Cannot equip weapon - no valid weapon proficiency for " + getName() + " loaded.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						Globals.debugPrint("Globals: " + Globals.getWeaponProfList() + "\n" + "Proficiency name: " + aWProf + " " + this);
						return;
					}

					//if (wp.isOneHanded())
					if (Globals.isWeaponOneHanded(aPC, this, wp))
					{
						handsInUse++;
					}
					//if (wp.isTwoHanded())
					if (Globals.isWeaponTwoHanded(aPC, this, wp))
					{
						handsInUse += 2;
					}
					//if (wp.isTooLarge())
					if (Globals.isWeaponTooLargeForPC(aPC, this))
					{
						handsInUse += 99;
					}
					// Add something gross, in case we have a race with tons of hands
				}
				else
				{
					handsInUse += getHands();
				}
				// shields take up 1 hand, except bucklers. Adding getHands() means we cover all bases, including future (potential) 2-handed shields.

				if (handsInUse > iRaceHands)
				{
					equipped = false;
					GuiFacade.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				equipped = true;
			}
			else
			{
				equipped = aFlag;
			}
			if (!equipped)
			{
				GuiFacade.showMessageDialog(null, "Character cannot equip any more of this item type.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
		{
			equipped = aFlag;
		}
	}

	/**
	 *  Gets the modifiersAllowed attribute of the Equipment object
	 *
	 *@return    The modifiersAllowed value
	 */
	public boolean getModifiersAllowed()
	{
		return modifiersAllowed;
	}

	public void setModifiersAllowed(boolean argModifiersAllowed)
	{
		modifiersAllowed = argModifiersAllowed;
	}

	/**
	 *  Gets the modifiersRequired attribute of the Equipment object
	 *
	 *@return    The modifiersRequired value
	 */
	public boolean getModifiersRequired()
	{
		return modifiersRequired;
	}

	public void setModifiersRequired(boolean argModifiersRequired)
	{
		modifiersRequired = argModifiersRequired;
	}

	public void setCostMod(String aString)
	{
		try
		{
			costMod = new BigDecimal(aString);
		}
		catch (NumberFormatException e)
		{
			costMod = new BigDecimal("0");
		}
	}

	public void setCostMod(BigDecimal aCost)
	{
		costMod = aCost;
	}

	public BigDecimal getCostMod()
	{
		return costMod;
	}

	public void setWeightMod(String aString)
	{
		try
		{
			weightMod = new Float(aString);
		}
		catch (NumberFormatException e)
		{
			weightMod = new Float(0);
		}
	}

	public void setWeightMod(Float aFloat)
	{
		weightMod = aFloat;
	}

	public Float getWeightMod()
	{
		return weightMod;
	}

	private ArrayList cloneEqModList(boolean primary)
	{
		ArrayList clonedList = new ArrayList();
		for (Iterator e = getEqModifierList(primary).iterator(); e.hasNext();)
		{
			EquipmentModifier eqMod = (EquipmentModifier)e.next();
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eqMod = (EquipmentModifier)eqMod.clone();
			}
			clonedList.add(eqMod);
		}
		return clonedList;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Object clone()
	{
		Equipment eq = (Equipment)super.clone();

		//
		// These get modified by equipment modifiers so DO NOT use the function or we will get
		// doubled bonuses
		//
		eq.cost = cost;
		eq.size = size;
		eq.baseCost = baseCost;
		eq.sizeBase = sizeBase;
		eq.baseItem = baseItem;

// set DR
		eq.seteDR(eDR.toString());
		eq.setACCheck(acCheck.toString());
		eq.setCritRange(critRange);
		eq.setRange(range.toString());
		eq.setSpellFailure(spellFailure.toString());
		eq.setWeight(weight.toString());
		eq.setMaxDex(maxDex.toString());
		eq.setDamage(damage);
		eq.setAltDamage(altDamage);
		//////////////////////////////////////////////////////////

		eq.setMoveString(moveString());
		eq.setTypeString(super.getType());
		// none of the types associated with modifiers
		eq.critMult = critMult;
		eq.isSpecified = isSpecified;
		eq.profName = profName;
		eq.carried = carried;
		eq.inHand = inHand;
		eq.altCritMult = altCritMult;
		eq.hands = hands;
		eq.bonusType = bonusType;
		eq.numberEquipped = numberEquipped;
		eq.reach = reach;
		eq.qty = qty;
		eq.containerWeightCapacity = containerWeightCapacity;
		eq.d_acceptsChildren = d_acceptsChildren;
		if (d_acceptsTypes != null)
			eq.d_acceptsTypes = new HashMap(d_acceptsTypes);
		eq.containerConstantWeight = containerConstantWeight;
		if (d_childTypes != null)
			eq.d_childTypes = new HashMap(d_childTypes);
		eq.containerContentsString = containerContentsString;
		eq.containerCapacityString = containerCapacityString;
		if (d_containedEquipment != null)
			eq.d_containedEquipment = new ArrayList(d_containedEquipment);
		eq.eqModifierList = cloneEqModList(true);
		eq.altEqModifierList = cloneEqModList(false);
		eq.modifiersAllowed = modifiersAllowed;
		eq.modifiersRequired = modifiersRequired;

		//header crap
		eq.hasHeaderParent = hasHeaderParent;
		eq.headerParent = headerParent;
		if (d_headerChildren != null)
			eq.d_headerChildren = new ArrayList(d_headerChildren);
		return eq;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public final int whatHand()
	{
		return inHand;
	}

	/**
	 *  Gets the hand attribute of the Equipment object
	 *
	 *@return    The hand value
	 */
	public final int getHand()
	{
		if (inHand >= 4)
		{
			return 4;
		}
		return inHand;
	}

	/**
	 *  Sets the hand attribute of the Equipment object
	 *
	 *@param  hand  The new hand value
	 */
	public final void setHand(int hand)
	{
		if (isWeapon())
		{
			String wpName = profName(hand);
			WeaponProf wp = Globals.getWeaponProfNamed(wpName);
			if (wp == null)
			{
				final int idx = wpName.indexOf('(');
				if (idx > 0)
				{
					wpName = wpName.substring(0, idx).trim();
					wp = Globals.getWeaponProfNamed(wpName);
				}
			}

			if (wp == null)
			{
				GuiFacade.showMessageDialog(null, "Cannot find a weapon prof for " + name + "--" + wpName, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (wp.getType().equals("Natural"))
			{
				//Don't worry if it can be placed in those hands if it is a natural weapon.

				if (modifiedName().endsWith("Primary") && !isOnlyNaturalWeapon && hand != PRIMARY_HAND && hand != NEITHER_HAND)
				{
					GuiFacade.showMessageDialog(null, "Can only place primary Natural weapon in Primary \"Hand\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Secondary") && hand != SECONDARY_HAND && hand != NEITHER_HAND)
				{
					GuiFacade.showMessageDialog(null, "Can only place secondary Natural weapon in Off \"Hand\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Primary") && isOnlyNaturalWeapon && hand != BOTH_HANDS && hand != NEITHER_HAND)
				{
					GuiFacade.showMessageDialog(null, "Can only place sole Natural weapon in both \"hands\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				if ((hand == BOTH_HANDS) && Globals.isWeaponLightForPC(aPC, this) && !Globals.isWeaponTwoHanded(aPC, this, wp))
				{
					GuiFacade.showMessageDialog(null, "Cannot place light weapon in both hands.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if ((hand != NEITHER_HAND) && (hand != BOTH_HANDS) && Globals.isWeaponTwoHanded(aPC, this, wp))
				{
					GuiFacade.showMessageDialog(null, "Two handed weapon must be in Neither or Both hands.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}
		inHand = hand;
	}

	public static boolean isWeaponTwoHanded(PlayerCharacter pc, Equipment weapon, WeaponProf wp)
	{
		if ((pc != null) && (weapon != null))
		{
			if ((pc.sizeInt() == (Globals.sizeInt(weapon.getSize()) - 1)) || ((wp != null) && (wp.getHands() == 2)))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String preReqString()
	{
		return preReqStrings();
	}

	public boolean save(BufferedWriter output)
	{
		FileAccess.write(output, "BASEITEM:" + formatSaveLine("\t", ":"));
		FileAccess.newLine(output);
		return true;
	}

	public String formatSaveLine(String sep, String endPart)
	{
		StringBuffer sbuf = new StringBuffer(100);

		Equipment base = null;
		if (baseItem.length() != 0)
		{
			base = Globals.getEquipmentNamed(baseItem);
			sbuf.append(baseItem);
			sbuf.append(sep).append("NAME").append(endPart).append(getName());
		}
		else
		{
			base = this;
			sbuf.append(getName());
		}

		if (!size.equals(base.getSize()))
		{
			sbuf.append(sep).append("SIZE").append(endPart).append(size);
		}

		String aString = getEqModifierString(true);			// key1.key2|assoc1|assoc2.key3.key4
		if (aString.length() > 0)
		{
			sbuf.append(sep).append("EQMOD").append(endPart).append(aString);
		}

		aString = getEqModifierString(false);				// key1.key2|assoc1|assoc2.key3.key4
		if (aString.length() > 0)
		{
			sbuf.append(sep).append("ALTEQMOD").append(endPart).append(aString);
		}

		aString = getRawSpecialProperties();
		if ((aString.length() > 0) && !aString.equals(base.getRawSpecialProperties()))
		{
			sbuf.append(sep).append("SPROP").append(endPart).append(aString);
		}
		if (!costMod.equals(new BigDecimal("0")))
		{
			sbuf.append(sep).append("COSTMOD").append(endPart).append(costMod.toString());
		}
		if (weightMod.doubleValue() != 0.0)
		{
			sbuf.append(sep).append("WEIGHTMOD").append(endPart).append(weightMod.toString());
		}
		return sbuf.toString();
	}

	public void load(String aLine)
	{
		load(aLine, "\t", ":");
	}

	public void load(String aLine, String sep, String endPart)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, sep, false);
		final int endPartLen = endPart.length();
		String newSize = size;
		baseItem = getKeyName();
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (aString.startsWith("NAME" + endPart))
			{
				setName(aString.substring(4 + endPartLen));
			}
			else if (aString.startsWith("SIZE" + endPart))
			{
				newSize = aString.substring(4 + endPartLen);
			}
			else if (aString.startsWith("EQMOD" + endPart))
			{
				addEqModifiers(aString.substring(5 + endPartLen), true);
			}
			else if (aString.startsWith("ALTEQMOD" + endPart))
			{
				addEqModifiers(aString.substring(8 + endPartLen), false);
			}
			else if (aString.startsWith("SPROP" + endPart))
			{
				setSpecialProperties(aString.substring(5 + endPartLen));
			}
			else if (aString.startsWith("COSTMOD" + endPart))
			{
				setCostMod(aString.substring(7 + endPartLen));
			}
			else if (aString.startsWith("WEIGHTMOD" + endPart))
			{
				setWeightMod(aString.substring(9 + endPartLen));
			}
		}
		resizeItem(newSize);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aType  Description of the Parameter
	 *@param  aName  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	public int bonusTo(String aType, String aName)
	{
		return super.bonusTo(aType, aName);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aType     Description of the Parameter
	 *@param  aName     Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public int bonusTo(String aType, String aName, boolean bPrimary)
	{

		// go through hashmap and zero out all entries that deal with this bonus request
		final String aBonusKey = new StringBuffer(aType.toUpperCase()).append(".").append(aName.toUpperCase()).append(".").toString();
		for (Iterator e = getBonusMap().keySet().iterator(); e.hasNext();)
		{
			String aKey = e.next().toString();
			if (aKey.startsWith(aBonusKey))
			{
				getBonusMap().put(aKey, "0");
			}
		}

		int iBonus = 0;
		bonusPrimary = bPrimary;
		if (bPrimary)
		{
			super.bonusTo(aType, aName, this);
		}

		final ArrayList eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
				//
				// Only add bonuses for items that are not ignored.
				// eg. Masterwork is ignored for Adamantine
				//
				if (!willIgnore(aEqMod.getKeyName(), bPrimary))
				{
					aEqMod.bonusTo(aType, aName, this);
				}
			}
		}

		for (Iterator i = getBonusMap().keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			if (aKey.startsWith(aBonusKey))
			{
				iBonus += Integer.parseInt((String)getBonusMap().get(aKey));
			}
		}
		return iBonus;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  eqModKey  Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	private boolean willIgnore(String eqModKey, boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
				if (aEqMod.willIgnore(eqModKey))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *  Gets the eqModifierKeyed attribute of the Equipment object
	 *
	 *@param  eqModKey  Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return           The eqModifierKeyed value
	 */
	public EquipmentModifier getEqModifierKeyed(String eqModKey, boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
				if (aEqMod.getKeyName().equals(eqModKey))
				{
					return aEqMod;
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Remove a list equipment modifiers and their specified associated information
	 * eg. Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6
	 *
	 *  Removes a feature from the EqModifiers attribute of the Equipment object
	 *
	 *@param  aString   The feature to be removed from the EqModifiers attribute
	 *@param  bPrimary  The feature to be removed from the EqModifiers attribute
	 */
	public void removeEqModifiers(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aEqModName = aTok.nextToken();
			if (!aEqModName.equalsIgnoreCase(Constants.s_NONE))
			{
				removeEqModifier(aEqModName, bPrimary);
			}
		}
	}

	/**
	 * Remove an equipment modifier and specified associated information
	 * eg. Bane|Vermin|Fey
	 * eg. Keen
	 *  Removes a feature from the EqModifier attribute of the Equipment object
	 *
	 *@param  aString   The feature to be removed from the EqModifier attribute
	 *@param  bPrimary  The feature to be removed from the EqModifier attribute
	 */
	private void removeEqModifier(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final String eqModKey = aTok.nextToken();
		final EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);
		if (eqMod == null)
		{
			return;
		}
		//
		// Remove the associated choices
		//
		while (aTok.hasMoreTokens())
		{
			final String x = aTok.nextToken().replace('=', '|');
			for (int i = eqMod.getAssociatedCount() - 1; i >= 0; i--)
			{
				final String aChoice = eqMod.getAssociated(i);
				if (aChoice.startsWith(x))
				{
					eqMod.removeAssociated(i);
				}
			}
		}
		if (eqMod.getAssociatedCount() == 0)
		{
			removeEqModifier(eqMod, bPrimary);
		}
	}

	/**
	 *
	 * Add a list equipment modifiers and their associated information
	 * eg. Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6
	 *
	 *  Adds a feature to the EqModifiers attribute of the Equipment object
	 *
	 *@param  aString   The feature to be added to the EqModifiers attribute
	 *@param  bPrimary  The feature to be added to the EqModifiers attribute
	 */
	public void addEqModifiers(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aEqModName = aTok.nextToken();
			if (!aEqModName.equalsIgnoreCase(Constants.s_NONE))
			{
				addEqModifier(aEqModName, bPrimary);
			}
		}

		ArrayList eqModList = getEqModifierList(bPrimary);
		Globals.sortPObjectList(eqModList);
	}

	/**
	 * Add an equipment modifier and its associated information
	 * eg. Bane|Vermin|Fey
	 * eg. Keen
	 *  Adds a feature to the EqModifier attribute of the Equipment object
	 *
	 *@param  aString   The feature to be added to the EqModifier attribute
	 *@param  bPrimary  The feature to be added to the EqModifier attribute
	 */
	private void addEqModifier(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final String eqModKey = aTok.nextToken();
		EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);
		if (eqMod == null)
		{
			eqMod = Globals.getModifierKeyed(eqModKey);
			if (eqMod == null)
			{
				Globals.errorPrint("Could not find EquipmentModifier: " + eqModKey);
				return;
			}
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eqMod = (EquipmentModifier)eqMod.clone();
			}

			ArrayList eqModList = getEqModifierList(bPrimary);
			eqModList.add(eqMod);
		}
		//
		// Add the associated choices
		//
		if (eqMod.getChoiceString().length() != 0)
		{
			while (aTok.hasMoreTokens())
			{
				final String x = aTok.nextToken();
				eqMod.addAssociated(x.replace('=', '|'));
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  bPrimary  Description of the Parameter
	 */
	private void removeUnqualified(boolean bPrimary)
	{
		ArrayList eqModList = getEqModifierList(bPrimary);
		for (; ;)
		{
			boolean bRemoved = false;
			for (int i = eqModList.size() - 1; i >= 0; i--)
			{
				final EquipmentModifier eqMod = (EquipmentModifier)eqModList.get(i);
				if (!eqMod.passesPreReqTests(this))
				{
					eqModList.remove(i);
					bRemoved = true;
				}
			}
			if (!bRemoved)
			{
				break;
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  eqMod     Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 */
	public void removeEqModifier(EquipmentModifier eqMod, boolean bPrimary)
	{
		ArrayList eqModList = getEqModifierList(bPrimary);
		EquipmentModifier eq = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);
		if (eq == null)
		{
			return;
		}
		//
		// Get a response from user (if one required). Remove the modifier if all associated choices
		// are deleted
		//
		if ((eq.getAssociatedCount() == 0) || (eq.getChoice(0, this, false) == 0))
		{
			eqModList.remove(eq);
			removeUnqualified(bPrimary);
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  eqMod     Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public boolean canAddModifier(EquipmentModifier eqMod, boolean bPrimary)
	{
		//
		// Make sure we are qualified
		//
		bonusPrimary = bPrimary;
		if (!modifiersAllowed || !eqMod.passesPreReqTests(this))
		{
			return false;
		}
		//
		// Don't allow adding of modifiers with %CHOICE cost to secondary head, as
		// cost is only calculated for these modifiers on primary head
		//
		if (!bPrimary && (eqMod.getCost().indexOf("%CHOICE") > -1))
		{
			return false;
		}
		return true;
	}

	/**
	 *  Callback function from PObject.passesPreReqTestsForList()
	 *
	 *@param  aType  Description of the Parameter
	 *@return        The preType value
	 */
	public boolean isPreType(String aType)
	{
		//
		// PRETYPE:EQMODTYPE=MagicalEnhancement
		// PRETYPE:[EQMOD=Holy],EQMOD=WEAP+5
		// PRETYPE:.IF.TYPE=Armor.Shield.Weapon.THEN.EQMODTYPE=MagicalEnhancement.ELSE.
		//
		if (aType.startsWith(".IF.TYPE="))
		{
			final StringTokenizer aTok = new StringTokenizer(aType.substring(9), ".");
			boolean typeFound = false;
			String truePart = "";
			String falsePart = "";

			int idx = aType.indexOf(".THEN.");
			if (idx < 0)
			{
				return false;
			}
			truePart = aType.substring(idx + 6);
			aType = aType.substring(0, idx);
			idx = truePart.indexOf(".ELSE.");
			if (idx >= 0)
			{
				falsePart = truePart.substring(idx + 6);
				truePart = truePart.substring(0, idx);
			}

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();
				if (isType(aString, bonusPrimary))
				{
					typeFound = true;
					break;
				}
			}
			if (typeFound)
			{
				aType = truePart;
			}
			else
			{
				aType = falsePart;
			}
			if (aType.length() == 0)
			{
				return true;
			}
		}

		if (aType.startsWith("EQMODTYPE="))
		{
			aType = aType.substring(10);
			for (Iterator e = getEqModifierList(bonusPrimary).iterator(); e.hasNext();)
			{
				final EquipmentModifier eqMod = (EquipmentModifier)e.next();
				if (eqMod.isType(aType))
				{
					return true;
				}
			}
			return false;
		}
		else if (aType.startsWith("EQMOD="))
		{
			aType = aType.substring(6);
			if (getEqModifierKeyed(aType, bonusPrimary) != null)
			{
				return true;
			}
			return false;
		}
		return isType(aType, bonusPrimary);
	}

	/**
	 *  Adds a feature to the EqModifier attribute of the Equipment object
	 *
	 *@param  eqMod     The feature to be added to the EqModifier attribute
	 *@param  bPrimary  The feature to be added to the EqModifier attribute
	 */
	public void addEqModifier(EquipmentModifier eqMod, boolean bPrimary)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		boolean bImporting = false;
		if ((aPC != null) && aPC.isImporting())
		{
			bImporting = true;
		}

		if (!bImporting && !canAddModifier(eqMod, bPrimary))
		{
			return;
		}

		//
		// Remove any modifiers that this one will replace
		//
		ArrayList eqModList = getEqModifierList(bPrimary);
		for (int i = eqModList.size() - 1; i >= 0; i--)
		{
			final EquipmentModifier aEq = (EquipmentModifier)eqModList.get(i);
			if (eqMod.willReplace(aEq.getKeyName()))
			{
				eqModList.remove(i);
			}
		}
		if (eqMod.isType("BaseMaterial"))
		{
			for (int i = eqModList.size() - 1; i >= 0; i--)
			{
				final EquipmentModifier aEq = (EquipmentModifier)eqModList.get(i);
				if (aEq.isType("BaseMaterial"))
				{
					eqModList.remove(i);
				}
			}
		}
		else if (eqMod.isType("MagicalEnhancement"))
		{
			for (int i = eqModList.size() - 1; i >= 0; i--)
			{
				final EquipmentModifier aEq = (EquipmentModifier)eqModList.get(i);
				if (aEq.isType("MagicalEnhancement"))
				{
					eqModList.remove(i);
				}
			}
		}

		//
		// Add the modifier if it's not already there
		//
		EquipmentModifier eq = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);
		if (eq == null)
		{
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eq = (EquipmentModifier)eqMod.clone();
				if (eq == null)
				{
					return;
				}
			}
			else
			{
				eq = eqMod;
			}
			eqModList.add(eq);
		}

		//
		// Get a response from user (if one required). Remove the modifier if all associated choices
		// are deleted
		//
		if (!bImporting && (eq.getChoice(1, this, true) == 0))
		{
			eqModList.remove(eq);
		}
		Globals.sortPObjectList(eqModList);

		setBase();
	}

	/**
	 *  Gets the eqModifierList attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The eqModifierList value
	 */
	public ArrayList getEqModifierList(boolean bPrimary)
	{
		if (bPrimary)
		{
			return eqModifierList;
		}
		return altEqModifierList;
	}

	/**
	 * return the list of modifier keys as a period-delimeted string
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The eqModifierString value
	 */
	public String getEqModifierString(boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
		StringBuffer aString = new StringBuffer(eqModList.size() * 10);
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			if (aString.length() != 0)
			{
				aString.append('.');
			}
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			aString.append(eqMod.getKeyName());
			//
			// Add the modifiers
			//
			for (int e2 = 0; e2 < eqMod.getAssociatedCount(); e2++)
			{
				final String strMod = eqMod.getAssociated(e2);
				aString.append('|').append(strMod.replace('|', '='));
			}
		}
		return aString.toString();
	}

	/**
	 *  Returns a list of the types of this item.
	 *
	 *@return           a list of the types of this item.
	 */
	public ArrayList typeList()
	{
		return typeList(true);
	}

	/**
	 *  Returns a list of the types of this item.
	 *
	 *@param  bPrimary  ???
	 *@return           a list of the types of this item.
	 */
	private ArrayList typeList(boolean bPrimary)
	{
		//
		// Use the primary type(s) if none defined for secondary
		//
		ArrayList typeList;
		if (bPrimary || getAltTypeCount() == 0)
		{
			typeList = new ArrayList();
			addMyTypeTo(typeList);
		}
		else
		{
			if (!isDouble())
			{
				return new ArrayList();
			}
			typeList = new ArrayList(getAltTypeCount());
			if (altTypeList != null) typeList.addAll(altTypeList);
		}

		ArrayList modTypeList = new ArrayList();

		//
		// Add in all type modfiers from "ADDTYPE" modifier
		//
		EquipmentModifier aEqMod = getEqModifierKeyed("ADDTYPE", bPrimary);
		if (aEqMod != null)
		{
			for (int e = 0; e < aEqMod.getAssociatedCount(); e++)
			{
				final String aType = aEqMod.getAssociated(e);
				if (!typeList.contains(aType))
				{
					modTypeList.add(aType);
				}
			}
		}

		final ArrayList eqModList = getEqModifierList(bPrimary);
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			aEqMod = (EquipmentModifier)e.next();
			if (!willIgnore(aEqMod.getKeyName(), bPrimary))
			{
				//
				// If we've just replaced the armor type, then make sure it is
				// not in the equipment modifier list
				//
				final String armorType = aEqMod.replaceArmorType(typeList);
				if (armorType != null)
				{
					final int idx = modTypeList.indexOf(armorType);
					if (idx >= 0)
					{
						modTypeList.remove(idx);
					}
				}

				final ArrayList eqModTypeList = aEqMod.getItemType();
				for (Iterator e2 = eqModTypeList.iterator(); e2.hasNext();)
				{
					final String aType = (String)e2.next();
					if (!typeList.contains(aType) && !modTypeList.contains(aType))
					{
						modTypeList.add(aType);
					}
				}
			}
		}
		typeList.addAll(modTypeList);

		//
		// Make sure MAGIC tag is the 1st entry
		//
		final int idx = typeList.indexOf("MAGIC");
		if (idx > 0)
		{
			typeList.remove(idx);
			typeList.add(0, "MAGIC");
		}
		return typeList;
	}

	/**
	 *  Gets the type attribute of the Equipment object
	 *
	 *@return    The type
	 */
	public String getType()
	{
		return getType(true);
	}

	/**
	 *  Get the type list as a period-delimited string
	 *
	 *@param  bPrimary  ???
	 *@return           The type value
	 */
	public String getType(boolean bPrimary)
	{
		final ArrayList typeList = typeList(bPrimary);
		final int typeSize = typeList.size();
		StringBuffer aType = new StringBuffer(typeSize * 5); //Just a guess.
		for (Iterator e = typeList.iterator(); e.hasNext();)
		{
			if (aType.length() != 0)
			{
				aType.append(".");
			}
			aType.append((String)e.next());
		}
		return aType.toString();
	}

	/**
	 *  Returns the type with the requested index
	 *
	 *@param  index  the index
	 *@return    the type with the requested index
	 */
	public String typeIndex(int index)
	{
		final ArrayList tList = typeList();
		if ((index < 0) || (index >= tList.size()))
		{
			return "";
		}

		return (String)tList.get(index);
	}

	/**
	 *  Gets the type attribute of the Equipment object
	 *
	 *@param  aType  Description of the Parameter
	 *@return        The type value
	 */
	public boolean isType(String aType)
	{
		return isType(aType, true);
	}

	/**
	 *  Gets the type attribute of the Equipment object
	 *
	 *@param  aType     Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return           The type value
	 */
	public boolean isType(String aType, boolean bPrimary)
	{
		if (!bPrimary && !isDouble())
		{
			return false;
		}

		final ArrayList tList = typeList(bPrimary);
		return tList.contains(aType.toUpperCase());
	}

	/**
	 *  Gets the eitherType attribute of the Equipment object
	 *
	 *@param  aType  Description of the Parameter
	 *@return        The eitherType value
	 */
	public boolean isEitherType(String aType)
	{
		return isType(aType, true) | isType(aType, false);
	}

	/**
	 *  Gets the specialAbilityList attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The specialAbilityList value
	 */
	public ArrayList getSpecialAbilityList(boolean bPrimary)
	{
		return getSpecialAbilityList(getEqModifierList(bPrimary));
	}

	/**
	 *  Gets the specialAbilityList attribute of the Equipment object
	 *
	 *@param  eqModList  Description of the Parameter
	 *@return            The specialAbilityList value
	 */
	private ArrayList getSpecialAbilityList(final ArrayList eqModList)
	{
		ArrayList saList = new ArrayList();
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			saList.addAll(aEqMod.getSpecialProperties());
		}
		return saList;
	}

	/*
 *  same as getSpecialAbilityList except if
 *  if you have the same ability twice, it only
 *  lists it once with (2) at the end.
 *
 *@param  bPrimary  Description of the Parameter
 *@return           The specialAbilityTimesList value
 */
	public ArrayList getSpecialAbilityTimesList(boolean bPrimary)
	{
		final ArrayList specialAbilityList = getSpecialAbilityList(bPrimary);
		return getSpecialAbilityTimesList(specialAbilityList, bPrimary);
	}

	/**
	 *  same as getSpecialAbilityList except if
	 *  if you have the same ability twice, it only
	 *  lists it once with (2) at the end.
	 *
	 *@param  specialAbilityList  Description of the Parameter
	 *@param  bPrimary            Description of the Parameter
	 *@return                     The specialAbilityTimesList value
	 */
	public ArrayList getSpecialAbilityTimesList(ArrayList specialAbilityList, boolean bPrimary)
	{
		ArrayList aList = new ArrayList();
		int[] times = new int[specialAbilityList.size()];
		for (int x = 0; x < times.length; x++)
		{
			times[x] = 0;
		}

		//
		// First, get a list of the unique abilities, counting the duplicates
		//
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			String aString = (String)i.next();
			int idx = aList.indexOf(aString);
			if (idx < 0)
			{
				idx = aList.size();
				aList.add(aString);
			}
			times[idx] += 1;
		}

		//
		// Get values for all variables
		//
		for (int idx = 0; idx < aList.size(); idx++)
		{
			String aString = (String)aList.get(idx);
			final int pos_pipe = aString.lastIndexOf("|");
			final int pos_perc = aString.lastIndexOf("%");
			if (pos_pipe >= 0 && pos_perc == -1)
			{
				Globals.errorPrint("Bad SA: tag '" + aString + "'");
				// The Archer Class' use of the ADD:WEAPONBONUS(TOHIT|2+((CL=Archer)/5) tag is causing this to pop up. It shouldn't.
				// GuiFacade.showMessageDialog(null, "Bad SA: tag '" + aString + "'", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				continue;
			}
			if (pos_pipe >= 0)
			{
				final int sInt = getVariableValue(aString.substring(pos_pipe + 1), "", "", bPrimary).intValue();
				final StringTokenizer aTok = new StringTokenizer(aString.substring(0, pos_pipe), "%", true);
				StringBuffer newAbility = new StringBuffer();
				while (aTok.hasMoreTokens())
				{
					final String nextTok = aTok.nextToken();
					if (nextTok.equals("%"))
					{
						newAbility.append(Integer.toString(sInt));
					}
					else
					{
						newAbility.append(nextTok);
					}
				}
				aString = newAbility.toString();
				aList.set(idx, aString);
				if (sInt == 0)
				{
					times[idx] = 0;
				}
			}
			if (times[idx] > 1)
			{
				aList.set(idx, aString + " (" + times[idx] + ")");
			}
		}

		//
		// Remove any abilities whose occurance is 0 after calculating expression
		//
		for (int idx = aList.size() - 1; idx >= 0; idx--)
		{
			if (times[idx] == 0)
			{
				aList.remove(idx);
			}
		}
		return aList;
	}

	/**
	 *  Calculates the plusForCosting attribute of the Equipment object
	 *  ***NOT USED***
	 *@return    The plusForCosting value
	 */
	public int calcPlusForCosting()
	{
		int iPlus = 0;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			iPlus += aEqMod.getPlus();
		}
		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			iPlus += aEqMod.getPlus();
		}
		return iPlus;
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 *@param  aDamage  The base damage
	 *@param  aSize    The size to adjust for
	 *@return          The adjusted damage
	 */
	public String adjustDamage(String aDamage, String aSize)
	{
		return Globals.adjustDamage(aDamage, getSize(), aSize);
	}

	/**
	 *@param  aSize	The size to adjust for
	 *@return        The costAdjustedForSize value
	 */
	public BigDecimal getCostAdjustedForSize(String aSize)
	{
		BigDecimal c = getBaseCost();
		//
		// Scale everything to medium before conversion
		//
		double mult = Globals.sizeAdjustmentCostMultiplier(aSize, typeList()) / Globals.sizeAdjustmentCostMultiplier(getBaseSize(), typeList());
		c = c.multiply(new BigDecimal(mult));
		//
		// TODO:Non-humanoid races can also double the cost (armor)
		//
		return c;
	}

	/**
	 *  Gets the acModAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  The size to adjust for
	 */
	private void adjustACForSize(Equipment baseEq, String aSize)
	{
		if (bonusList != null && isArmor())
		{
			double mult = Globals.sizeAdjustmentACModMultiplier(aSize, baseEq.typeList()) / Globals.sizeAdjustmentACModMultiplier(baseEq.getSize(), baseEq.typeList());
			final ArrayList baseEqBonusList = baseEq.getBonusList();
			//acMod = new Integer(new Float(acMod.doubleValue() * mult).intValue());

			//
			// Go through the bonus list looking for COMBAT|AC|x and resize bonus
			// Assumption: baseEq.bonusList and this.bonusList only differ in COMBAT|AC|x bonuses
			//
			for (int i = 0; i < baseEqBonusList.size(); i++)
			{
				String aString = (String)baseEqBonusList.get(i);
				if (aString.startsWith("COMBAT|AC|"))
				{
					int iOffs = aString.indexOf('|', 10);
					if (iOffs > 10)
					{
						Integer acCombatBonus = new Integer(aString.substring(10, iOffs));
						acCombatBonus = new Integer(new Float(acCombatBonus.doubleValue() * mult).intValue());
						aString = aString.substring(0, 10) + acCombatBonus.toString() + aString.substring(iOffs);
						bonusList.set(i, aString);
					}
				}
			}

		}
	}

	/**
	 *  Gets the weightAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  the size to adjust for
	 *@return        The weightAdjustedForSize value
	 */
	public Float getWeightAdjustedForSize(String aSize)
	{
		double mult = Globals.sizeAdjustmentWeightMultiplier(aSize, typeList()) / Globals.sizeAdjustmentWeightMultiplier(getSize(), typeList());
		return new Float(getWeight().doubleValue() * mult);
	}

	/**
	 *  Gets the damageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  The size to adjust for
	 *@return        The damageAdjustedForSize value
	 */
	public String getDamageAdjustedForSize(String aSize, boolean bPrimary)
	{
		String baseDamage = bPrimary ? damage : altDamage;
		if (isWeapon())
		{
			if (baseDamage.length() == 0)
			{
				baseDamage = getWeaponInfo("DAMAGE", bPrimary);
			}
			if (baseDamage.length() != 0)
			{
				return adjustDamage(baseDamage, aSize);
			}
		}
		return baseDamage;
	}

	public String getDamageAdjustedForSize(String aSize)
	{
		return getDamageAdjustedForSize(aSize, true);
	}

	/**
	 *  Gets the altDamageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  The size to adjust for
	 *@return        The altDamageAdjustedForSize value
	 */
	public String getAltDamageAdjustedForSize(String aSize)
	{
		return getDamageAdjustedForSize(aSize, false);
	}

	/**
	 *  This is a severely hacked-down version of the PlayerCharacter.getVariableValue
	 *
	 *@param  aString  ???
	 *@param  src      ???
	 *@param  subSrc   ???
	 *@return          The variable value
	 */
	public Float getVariableValue(String aString, String src, String subSrc)
	{
		return getVariableValue(aString, src, subSrc, bonusPrimary);
	}

	/**
	 *  This is a severely hacked-down version of the PlayerCharacter.getVariableValue
	 *
	 *@param  aString   ???
	 *@param  src       ???
	 *@param  subSrc    ???
	 *@param  bPrimary  ???
	 *@return           The variable value
	 */
	private Float getVariableValue(String aString, String src, String subSrc, boolean bPrimary)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf("(") > -1)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
			String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(bString, src, subSrc, bPrimary) + aString.substring(y + 1);
		}
		String delimiter = "+-/*";
		String valString = "";
		int mode = 0;
		//0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0;
		//1,11=min, 2,12=max, 3,13=req, 10 = int
		if (aString.startsWith(".IF."))
		{
			StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			StringBuffer bString = new StringBuffer();
			Float val1 = null;
			// first value
			Float val2 = null;
			// other value in comparison
			Float valt = null;
			// value if comparison is true
			Float valf = null;
			// value if comparison is false
			int comp = 0;
			while (aTok.hasMoreTokens())
			{
				String cString = aTok.nextToken();
				if (cString.equals("GT") || cString.equals("GTEQ") || cString.equals("EQ") || cString.equals("LTEQ") || cString.equals("LT"))
				{
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = new StringBuffer();
					if (cString.equals("LT"))
					{
						comp = 1;
					}
					else if (cString.equals("LTEQ"))
					{
						comp = 2;
					}
					else if (cString.equals("EQ"))
					{
						comp = 3;
					}
					else if (cString.equals("GT"))
					{
						comp = 4;
					}
					else if (cString.equals("GTEQ"))
					{
						comp = 5;
					}
				}
				else if (cString.equals("THEN"))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = new StringBuffer();
				}
				else if (cString.equals("ELSE"))
				{
					valt = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = new StringBuffer();
				}
				else
				{
					bString.append(cString);
				}
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString.toString(), src, subSrc, bPrimary);
				total = valt;
				switch (comp)
				{
					case 1:
						if (val1.doubleValue() >= val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 2:
						if (val1.doubleValue() > val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 3:
						if (val1.doubleValue() != val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 4:
						if (val1.doubleValue() <= val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 5:
						if (val1.doubleValue() < val2.doubleValue())
						{
							total = valf;
						}
						break;
					default:
						Globals.errorPrint("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				Globals.debugPrint("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
				return total;
			}
		}
		for (int i = 0; i < aString.length(); i++)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 || delimiter.lastIndexOf(aString.charAt(i)) > -1 ||
			  (valString.length() > 3 && (valString.endsWith("MIN") || valString.endsWith("MAX") || valString.endsWith("REQ"))))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}
				if (valString.equals("SIZE"))
				{
					valString = String.valueOf(Globals.sizeInt(getSize()));
				}
				else if (valString.equals("WT"))
				{
					if (calculatingCost && weightAlreadyUsed)
					{
						valString = "0";
					}
					else
					{
						if (calculatingCost && isAmmunition())
						{
							Float unitWeight = new Float(weight.doubleValue() / getBaseQty());
							valString = unitWeight.toString();
						}
						else
						{
							valString = weight.toString();
						}
						weightAlreadyUsed = true;
					}
				}
				else if (valString.equals("BASECOST"))
				{
					valString = getBaseCost().toString();
				}
				else if (valString.equals("DMGDIE"))
				{
					final RollInfo aRollInfo = new RollInfo(getDamage());
					valString = Integer.toString(aRollInfo.sides);
				}
				else if (valString.equals("DMGDICE"))
				{
					final RollInfo aRollInfo = new RollInfo(getDamage());
					valString = Integer.toString(aRollInfo.times);
				}
				else if (valString.equals("RANGE"))
				{
					valString = range.toString();
				}
				else if (valString.equals("CRITMULT"))
				{
					if (bPrimary)
					{
						valString = String.valueOf(getCritMultiplier());
					}
					else
					{
						valString = String.valueOf(getAltCritMultiplier());
					}
				}
				else
				{
					for (int j = 0; j < Globals.s_ATTRIBSHORT.length; j++)
					{
						if (valString.equals(Globals.s_ATTRIBSHORT[j]))
						{
							valString = String.valueOf(Globals.getCurrentPC().getStatList().getStatModFor(Globals.s_ATTRIBSHORT[j]));
							break;
						}
					}
				}

				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						valString = new Integer(getVariableValue(valString.substring(0, valString.length() - 6), "", "", bPrimary).intValue()).toString();
					}
					if (valString.endsWith(".INTVAL"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 7), "", "", bPrimary).toString();
						endMode += 10;
					}
					if (valString.endsWith("MIN"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "", bPrimary).toString();
						nextMode = 0;
						endMode += 1;
					}
					else if (valString.endsWith("MAX"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "", bPrimary).toString();
						nextMode = 0;
						endMode += 2;
					}
					else if (valString.endsWith("REQ"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "", bPrimary).toString();
						nextMode = 0;
						endMode += 3;
					}
					else if (aString.charAt(i) == '+')
					{
						nextMode = 0;
					}
					else if (aString.charAt(i) == '-')
					{
						nextMode = 1;
					}
					else if (aString.charAt(i) == '*')
					{
						nextMode = 2;
					}
					else if (aString.charAt(i) == '/')
					{
						nextMode = 3;
					}
				}
				try
				{
					if (valString.length() > 0)
					{
						switch (mode)
						{
							case 0:
								total = new Float(total.doubleValue() + Double.parseDouble(valString));
								break;
							case 1:
								total = new Float(total.doubleValue() - Double.parseDouble(valString));
								break;
							case 2:
								total = new Float(total.doubleValue() * Double.parseDouble(valString));
								break;
							case 3:
								total = new Float(total.doubleValue() / Double.parseDouble(valString));
								break;
							default:
								/**
								 * What should be done here?
								 */
						}
					}
				}
				catch (NumberFormatException exc)
				{
					//
					// Everything else has failed, try the current PC
					//
					final PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC != null)
					{
						return aPC.getVariableValue(aString, src);
					}
					GuiFacade.showMessageDialog(null, "Equipment:Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				mode = nextMode;
				nextMode = 0;
				valString = "";
				if (total1 == null && endMode % 10 != 0)
				{
					total1 = total;
					total = new Float(0.0);
				}
			}
		}
		if (total1 != null)
		{
			if (endMode % 10 == 1)
			{
				total = new Float(Math.min(total.doubleValue(), total1.doubleValue()));
			}
			if (endMode % 10 == 2)
			{
				total = new Float(Math.max(total.doubleValue(), total1.doubleValue()));
			}
			if (endMode % 10 == 3)
			{
				if (total1.doubleValue() < total.doubleValue())
				{
					total = new Float(0.0);
				}
				else
				{
					total = total1;
				}
			}
		}
		if (endMode / 10 > 0)
		{
			total = new Float(total.intValue());
		}
		return total;
	}

	/**
	 *  Returns true if the equipment modifier is visible
	 *
	 *@param  eqMod  The equipment modifier
	 *@return        The visible value
	 */
	public boolean isVisible(final EquipmentModifier eqMod)
	{
		int vis = eqMod.getVisible();
		if (vis == EquipmentModifier.VISIBLE_QUALIFIED)
		{
			return eqMod.passesPreReqTests(this);
		}
		return vis == EquipmentModifier.VISIBLE_YES;
	}

	/**
	 *  Sets the base attribute of the Equipment object
	 */
	private void setBase()
	{
		if (baseItem.length() == 0)
		{
			baseItem = getKeyName();
		}

		if (profName.length() == 0)
		{
			final Equipment eq = Globals.getEquipmentKeyed(baseItem);
			if (eq != null)
			{
				profName = eq.rawProfName();
				if (profName.length() == 0)
				{
					profName = eq.getName();
				}
			}
		}

		//
		// Scan through the modifiers checking for one that requires an item
		// have a different weapon proficiency
		//
		EquipmentModifier profType = null;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			final String aString = aEqMod.getProficiency();
			if (aString.length() != 0)
			{
				profType = aEqMod;
				break;
			}
		}
		//
		// If we haven't found one yet, check the secondary head
		//
		if (profType == null)
		{
			for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
				final String aString = aEqMod.getProficiency();
				if (aString.length() != 0)
				{
					profType = aEqMod;
					break;
				}
			}
		}
		//
		// If we've found a modifier that requires a different weapon prof, then generate the proficiency's name
		//
		if (profType != null)
		{
			ArrayList profTypeInfo = Utility.split(profType.getProficiency(), '.');
			if (profTypeInfo.size() == 2)
			{
				StringBuffer proficiencyName = new StringBuffer(profName);
				if (profName.endsWith(")"))
				{
					proficiencyName.setLength(proficiencyName.length() - 1);
					proficiencyName.append('/');
				}
				else
				{
					proficiencyName.append(" (");
				}
				proficiencyName.append((String)profTypeInfo.get(1)).append(')');
				profName = proficiencyName.toString();
				//
				// Strip out the [Hands] variable as, according to the so-called sage,
				// "if someone can wield a weapon 1 handed they can always wield it 2 handed"
				// so we'll just force them to take the 1-handed variety.
				//
				int iOffs = profName.indexOf("[Hands]");
				if (iOffs >= 0)
				{
					int skipLength = 7;
					if (profName.charAt(iOffs - 1) == '/')
					{
						iOffs -= 1;
						skipLength = 8;
					}
					profName = profName.substring(0, iOffs) + profName.substring(iOffs + skipLength);
				}

				WeaponProf wp = Globals.getWeaponProfNamed(profName);
				if (wp == null)
				{
					wp = new WeaponProf();
					try
					{
						pcgen.persistence.lst.WeaponProfLoader.parseLine(wp, profName + "\tTYPE:" + (String)profTypeInfo.get(0) + "\tSIZE:" + getSize(), null, 0);
					}
					catch (pcgen.persistence.PersistenceLayerException exc)
					{
						//XXX Should this really be ignored?
					}
					Globals.getWeaponProfList().add(wp);
				}
				Globals.sortPObjectList(Globals.getWeaponProfList());
			}
		}

		if (getSize().length() == 0)
		{
			setSize(Constants.s_SIZESHORT[Constants.SIZE_M]);
		}
	}

	/**
	 *  Change the size of an item
	 *
	 *@param  newSize  the new size for the item
	 */
	public void resizeItem(String newSize)
	{
		setBase();

		final int iOldSize = Globals.sizeInt(getSize());
		int iNewSize = Globals.sizeInt(newSize);

		if (iNewSize != iOldSize)
		{
			setSize(newSize);
			Equipment eq = Globals.getEquipmentKeyed(baseItem);
			if (eq != null)
			{
				setCost(eq.getCostAdjustedForSize(newSize).toString());
				setWeight(eq.getWeightAdjustedForSize(newSize).toString());
				adjustACForSize(eq, newSize);
				setDamage(eq.getDamageAdjustedForSize(newSize));
				setAltDamage(eq.getAltDamageAdjustedForSize(newSize).toString());

				//
				// Adjust the capacity of the container (if it is one)
				//
				if (containerCapacityString.length() > 0)
				{
					final double mult = Globals.sizeAdjustmentCapacityMultiplier(newSize, eq.typeList());
					if (containerWeightCapacity.intValue() != -1)
					{
						containerWeightCapacity = new Float(eq.containerWeightCapacity.doubleValue() * mult);
					}

					if (getAcceptsTypeCount() > 0)
					{
						for (Iterator e = eq.d_acceptsTypes.keySet().iterator(); e.hasNext();)
						{
							final String aString = (String)e.next();
							Float aWeight = eq.getAcceptsType(aString);
							if (aWeight.intValue() != -1)
							{
								aWeight = new Float(aWeight.doubleValue() * mult);
								setAcceptsType(aString, aWeight);
							}
						}
					}
					updateContainerCapacityString();
				}
			}

			//
			// Since we've just resized the item, we need to modify any PRESIZE prerequisites
			//
			for (int i = 0; i < getPreReqCount(); i++)
			{
				String aBonus = getPreReq(i);
				if (aBonus.startsWith("PRESIZE"))
				{
					int idx = aBonus.indexOf(":") + 1;
					// must be at least 7 to be valid, so can ignore -1
					if (idx > 0)
					{
						int iOldPre = Globals.sizeInt(aBonus.substring(idx));
						iNewSize += iOldPre - iOldSize;
						if ((iNewSize >= Constants.SIZE_F) && (iNewSize <= Constants.SIZE_C))
						{
							aBonus = aBonus.substring(0, idx) + Constants.s_SIZESHORT[iNewSize];
							setPreReq(i, aBonus);
						}
					}
				}
			}
		}
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public String nameItemFromModifiers()
	{
		final String itemName = getItemNameFromModifiers();
		cleanTypes();
		setName(itemName);
		return getName();
	}

	public String getItemNameFromModifiers()
	{
		ArrayList list1 = new ArrayList(eqModifierList);
		ArrayList list2 = new ArrayList(altEqModifierList);
		ArrayList comn = new ArrayList();

		//
		// Remove any modifiers on the base item so they don't confuse the naming
		//
		if (baseItem.length() == 0)
		{
			return getName();
		}

		final Equipment baseEquipment = Globals.getEquipmentKeyed(baseItem);
		if (baseEquipment != null)
		{
			for (Iterator e = baseEquipment.getEqModifierList(true).iterator(); e.hasNext();)
			{
				int idx = list1.indexOf(e.next());
				if (idx >= 0)
				{
					list1.remove(idx);
				}
			}
			for (Iterator e = baseEquipment.getEqModifierList(false).iterator(); e.hasNext();)
			{
				int idx = list2.indexOf(e.next());
				if (idx >= 0)
				{
					list2.remove(idx);
				}
			}
		}

		//
		// Get all the modifiers that apply to the entire item into a separate list
		//
		for (int i = list1.size() - 1; i >= 0; i--)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)list1.get(i);
			if (eqMod.getChoiceString().indexOf("TYPE=ALL") >= 0)
			{
				list1.remove(i);
				continue;
			}
			if (!eqMod.getAssignToAll())
			{
				continue;
			}
			comn.add(0, eqMod);
			list1.remove(i);
		}

		//
		// remove the common modifiers from the alternate list
		//
		for (int i = list2.size() - 1; i >= 0; i--)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)list2.get(i);
			if (!eqMod.getAssignToAll())
			{
				continue;
			}

			final int j = comn.indexOf(eqMod);
			if (j >= 0)
			{
				list2.remove(i);
			}
			else
			{
				Globals.errorPrint("eqMod expected but not found: " + eqMod.getName());
			}
		}

		//
		// Look for a modifier named "masterwork" (assumption: this is marked as "assigntoall")
		//
		String eqMaster = "";
		for (Iterator e = comn.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (eqMod.getName().equalsIgnoreCase("MASTERWORK"))
			{
				eqMaster = eqMod.getName();
				break;
			}
		}

		String magic1 = getMagicBonus(eqModifierList);
		String desc1 = getNameFromModifiers(list1, magic1, "");
		String magic2 = "";
		String desc2 = "";
		if (isDouble())
		{
			magic2 = getMagicBonus(altEqModifierList);
			desc2 = getNameFromModifiers(list2, magic2, "");
		}
		StringBuffer common = new StringBuffer(getNameFromModifiers(comn, magic1 + magic2, eqMaster));

		StringBuffer itemName;
		final String baseName = getBaseItemName().trim();

		//
		// Start with the base name, less any modifiers
		//
		int idx = baseName.indexOf('(');
		if (idx >= 0)
		{
			itemName = new StringBuffer(baseName.substring(0, idx - 1).trim());
		}
		else
		{
			itemName = new StringBuffer(baseName);
		}


		//
		// Add magic bonus(es) to description
		//
		if ((magic1.length() != 0) || (magic2.length() != 0))
		{
			itemName.append(' ');
			if (magic1.length() != 0)
			{
				itemName.append(magic1);
			}
			else
			{
				itemName.append('-');
			}
			if (isDouble())
			{
				itemName.append('/');
				if (magic2.length() != 0)
				{
					itemName.append(magic2);
				}
				else
				{
					itemName.append('-');
				}
			}
		}

		//
		// Tack on the original modifiers
		//
		if (idx >= 0)
		{
			itemName.append(' ');
			itemName.append(baseName.substring(idx));
		}


		//
		// Strip off the ending ')' in anticipation of more modifiers
		//
		idx = itemName.toString().lastIndexOf(')');
		if (idx >= 0)
		{
			itemName.setLength(idx);
			itemName.append('/');
		}
		else
		{
			itemName.append(" (");
		}

		//
		// Put size in name if not the same as the base item
		//
		final int iSize = Globals.sizeInt(getSize());
		if (Globals.sizeInt(getBaseSize()) != iSize)
		{
			if (common.length() != 0)
			{
				common.append('/');
			}
			common.append(Constants.s_SIZELONG[iSize]);
		}

		//
		// add the modifier description(s)
		//
		if ((desc1.length() == 0) && (desc2.length() == 0))
		{
			itemName.append(common);
		}
		else if (!isDouble())
		{
			itemName.append(desc1).append('/').append(common);
		}
		else
		{
			if (common.length() != 0)
			{
				itemName.append(common).append(';');
			}
			if (desc1.length() != 0)
			{
				itemName.append(desc1);
			}
			else
			{
				itemName.append('-');
			}
			itemName.append(';');
			if (desc2.length() != 0)
			{
				itemName.append(desc2);
			}
			else
			{
				itemName.append('-');
			}
		}

		//
		// If there were no modifiers, then drop the trailing '/'
		//
		if (itemName.toString().endsWith("/") || itemName.toString().endsWith(";"))
		{
			itemName.setLength(itemName.length() - 1);
		}
		itemName.append(")");

		//
		// If there were no modifiers, then strip the empty parenthesis
		//
		idx = itemName.toString().indexOf(" ()");
		if (idx >= 0)
		{
			itemName.setLength(idx);
		}
		return itemName.toString();
	}

	/**
	 *  Look for a modifier that grants type "magic"
	 *
	 *@param  eqModList  Description of the Parameter
	 *@return            The magicBonus value
	 */
	private final String getMagicBonus(final ArrayList eqModList)
	{
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (eqMod.isType("MagicalEnhancement"))
			{
				return eqMod.getName();
			}
		}
		return "";
	}

	/**
	 *  Gets the nameFromModifiers attribute of the Equipment object
	 *
	 *@param  eqModList  The list of modifiers
	 *@param  eqMagic    ???
	 *@param  eqMaster   ???
	 *@return            The nameFromModifiers value
	 */
	private String getNameFromModifiers(final ArrayList eqModList, final String eqMagic, final String eqMaster)
	{
		//
		// Get a sorted list so that the description will always come
		// out the same reguardless of the order we've added the modifiers
		//
		ArrayList eqList = new ArrayList(eqModList);
		Globals.sortPObjectList(eqList);

		StringBuffer sMod = new StringBuffer(70);
		if ((eqMagic.length() == 0) && (eqMaster.length() != 0))
		{
			sMod.append(eqMaster);
		}
		for (Iterator e = eqList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (!eqMod.getName().equals(eqMagic) && !eqMod.getName().equals(eqMaster))
			{
				if (sMod.length() != 0)
				{
					sMod.append('/');
				}
				sMod.append(eqMod.toString());
			}
		}

		return sMod.toString();
	}

	/**
	 *  Strip sizes and "Standard" from type string
	 */
	private void cleanTypes()
	{
		final String aType = super.getType();
		final StringTokenizer aTok = new StringTokenizer(aType, ".", false);
		StringBuffer aCleaned = new StringBuffer(aType.length());
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			int i;
			for (i = Constants.SIZE_F; i <= Constants.SIZE_C; i++)
			{
				if (aString.equalsIgnoreCase(Constants.s_SIZELONG[i]))
				{
					break;
				}
			}
			//
			// Ignore size or "Standard" unless previous tag
			// was "ARMOR" and this is "MEDIUM"
			//
			if ((i <= Constants.SIZE_C) || aString.equalsIgnoreCase("Standard"))
			{
				if ((i != Constants.SIZE_M) || !aCleaned.toString().toUpperCase().endsWith("ARMOR"))
				{
					continue;
				}
			}

			//
			// Make sure "Magic" is the first thing in the list
			//
			if (aString.equalsIgnoreCase("Magic"))
			{
				if (aCleaned.length() > 0)
				{
					aCleaned.insert(0, '.');
				}
				aCleaned.insert(0, aString);
			}
			else
			{
				if (aCleaned.length() > 0)
				{
					aCleaned.append(".");
				}
				aCleaned.append(aString);
			}
		}
		setType(aCleaned.toString());
	}

	/**
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return <code>true</code>, if this instance is a container;
	 *         <code>false</code>, otherwise
	 */
	public boolean isContainer()
	{
		return acceptsChildren();
	}

	/**
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return a list with all Equipment objects this container holds;
	 *         if this instance is no container, the list will be empty.
	 */
	public Collection getContents()
	{
		ArrayList contents = new ArrayList();

		Equipment aEquip;
		for (int it = 0; it < getContainedEquipmentCount(); it++)
		{
			aEquip = getContainedEquipment(it);

			if (aEquip.getCarried().floatValue() > 0.0f)
			{
				contents.add(aEquip);
			}
		}

		return contents;
	}

	/**
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return the location where this object is carried/equipped,
	 *         i.e. which hand, or the name of the container, or none.
	 */
	public String getLocation()
	{
		if (getCarried().floatValue() > 0.0f)
		{
			if (getHand() > 0)
			{
				return "Hand (" + s_handNames[getHand()] + ")";
			}
			else
			{
				if (getParent() != null)
				{
					return getParent().getName();
				}
			}
		}

		return Constants.s_NONE;
	}

	//
	// As per p.176 of DMG
	//
	private boolean isMagicLimitedType()
	{
		boolean limited = false;
		if (isType("HEADGEAR") || isType("EYEGEAR") || isType("CAPE")
		  || isType("AMULET") || isSuit() || isType("ROBE")
		  || isType("SHIRT") || isType("BRACER") || isType("GLOVE")
		  || isType("RING") || isType("BELT") || isType("BOOT")
		)
		{
			limited = true;
		}
		return limited;
	}

	private boolean ignoresCostDouble()
	{
		boolean noDouble = false;
		if (isType("MANTLE")		// Mantle of Spell Resistance doesn't double cost
		  || isType("POTION")
		  || isType("SCROLL")
		  || isType("STAFF")
		  || isType("WAND")
		)
		{
			noDouble = true;
		}
		return noDouble;
	}

	public boolean isLimitedType()
	{
		boolean limited = false;
		if (isSuit() || isType("ROBE") ||
		  isType("HEADGEAR") || isType("EYEGEAR") || isType("MASK") ||
		  isType("AMULET") || isType("NECKLACE") ||
		  isType("CAPE") || isType("CLOAK") ||
		  isType("SHIRT") || isType("CLOTHING") || isType("VEST") ||
		  isType("BRACER") || isType("ARMWEAR") ||
		  isType("GLOVE") ||
		  isType("BELT") ||
		  isType("BOOT"))
		{
			limited = true;
		}
		return limited;
	}

	public int getMinCharges()
	{
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			final int minCharges = eqMod.getMinCharges();
			if (minCharges > 0)
			{
				return minCharges;
			}
		}
		return 0;
	}

	public int getMaxCharges()
	{
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			final int maxCharges = eqMod.getMaxCharges();
			if (maxCharges > 0)
			{
				return maxCharges;
			}
		}
		return 0;
	}

	public void setRemainingCharges(int remainingCharges)
	{
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (eqMod.getMinCharges() > 0)
			{
				eqMod.setRemainingCharges(remainingCharges);
			}
		}
	}

	public int getRemainingCharges()
	{
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (eqMod.getMinCharges() > 0)
			{
				return eqMod.getRemainingCharges();
			}
		}
		return -1;
	}

	public int getUsedCharges()
	{
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			if (eqMod.getMinCharges() > 0)
			{
				return eqMod.getUsedCharges();
			}
		}
		return -1;
	}

}
