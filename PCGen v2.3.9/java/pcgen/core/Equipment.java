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
 */
package pcgen.core;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.util.Delta;

/**
 *  <code>Equipment</code>.
 *
 *@author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 *@created    December 27, 2001
 *@version    $Revision: 1.1 $
 */
public class Equipment extends PObject
	implements Serializable, EquipmentCollection, Comparable
{
	/**
	 *  The item is held in neither hand
	 */
	public final static int NEITHER_HAND = 0;
	/**
	 *  The item is held in the primary hand
	 */
	public final static int PRIMARY_HAND = 1;
	/**
	 *  The item is held in the secondary hand
	 */
	public final static int SECONDARY_HAND = 2;
	/**
	 *  The item is held in both hands
	 */
	public final static int BOTH_HANDS = 3;
	/**
	 *  The item is either a double weapon or one of a pair of weapons
	 */
	public final static int TWOWEAPON_HANDS = 4;
	private final static String[] s_handNames = {
		"Neither",
		"Primary",
		"Off-hand",
		"Both",
		"Two-Weapons"
	};
	private static TreeSet s_equipmentTypes = new TreeSet();

	private BigDecimal cost = new BigDecimal("0.00");
	private Integer acMod = new Integer(0);
	private Integer maxDex = new Integer(100);
	private Integer acCheck = new Integer(0);
	private Integer spellFailure = new Integer(0);
	private String moveString = "";
	private Float weight = new Float(0);
	private String modifiedName = "";
	// where extras can be placed
	private String size = "";
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
	private ArrayList d_containedEquipment = new ArrayList(0);
	private boolean d_acceptsChildren = false;
	private HashMap d_acceptsTypes = new HashMap();
	private HashMap d_childTypes = new HashMap();
	private Float containerWeightCapacity = new Float(0);
	private String containerCapacityString = "";
	private String containerContentsString = "";
	private boolean containerConstantWeight = false;
	private Equipment headerParent = null;
	private ArrayList d_headerChildren = new ArrayList(0);
	private boolean hasHeaderParent = false;
	private boolean isHeaderParent = false;
	private boolean isOnlyNaturalWeapon = false;
	/** if is true a BAB of 13 yields 13/8/3, if false, merely 13. */
	private boolean attacksProgress = true;
	private ArrayList eqModifierList = new ArrayList();
	private ArrayList altEqModifierList = new ArrayList();
	private ArrayList altTypeList = new ArrayList();
	private BigDecimal baseCost = new BigDecimal("0");
	private String sizeBase = "";
	private String baseItem = "";
	private boolean modifiersAllowed = true;

	private boolean calculatingCost = false;
	private boolean weightAlreadyUsed = false;

	/**
	 *  Used in callbacks from PObject.bonusTo() (getVariableValue(),
	 *  setBonusStackFor()) and PObject.passesPreReqTestsForList() (isPreType())
	 */
	private boolean bonusPrimary = true;


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
	 *@param  attacksProgress  whether to give several attacks.
	 */
	public void setAttacksProgress(boolean attacksProgress)
	{
		this.attacksProgress = attacksProgress;
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
	 *  ???
	 *
	 *@return    ???
	 */
	public HashMap getD_childTypes()
	{
		return d_childTypes;
	}


	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public ArrayList getD_containedEquipment()
	{
		return d_containedEquipment;
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
	 *@param  carried  the number of items of this type that are carried.
	 */
	public void setCarried(Float carried)
	{
		this.carried = carried;
	}


	/**
	 *  Returns the alternate damage for this item.
	 *
	 *@return    the alternate damage for this item.
	 */
	public String getAltDamage()
	{
		final int iSize = Globals.sizeInt(getSize());
		int iMod = iSize + bonusTo("EQMWEAPON", "DAMAGESIZE", false);
		if (iMod < Globals.SIZE_F)
		{
			iMod = Globals.SIZE_F;
		}
		if (iMod > Globals.SIZE_C)
		{
			iMod = Globals.SIZE_C;
		}
		return adjustDamage(altDamage, Globals.s_SIZESHORT[iMod]);
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
			JOptionPane.showMessageDialog
				(null,
					"Unable to interpret hand setting: " + handDesc, "PCGen", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}


	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public String choiceString()
	{
		return choiceString;
	}


	/**
	 * ???
	 *@param  aString
	 */
	private void setChoiceString(String aString)
	{
		choiceString = aString;
	}


	/**
	 *  Removes all items from this container.
	 */
	public void clearContainedEquipment()
	{
		d_containedEquipment.clear();
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
		d_childTypes.clear();
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
		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = (Equipment)e.next();
			aChild.setParent(anEquip);
		}

		d_containedEquipment.clear();
		//clear the last junk

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
		d_childTypes = new HashMap(lastHchild.d_childTypes);
		d_containedEquipment = new ArrayList(lastHchild.d_containedEquipment);
		setParent(lastHchild.getParent());

		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = (Equipment)e.next();
			aChild.setParent(this);
		}

		d_headerChildren.clear();
	}


	/**
	 *  Removes all children funky hack, may have problems with container
	 */
	public void quickRemoveAllChildren()
	{
		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = (Equipment)e.next();
			aChild.d_parent = null;
		}
		d_containedEquipment.clear();
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
	public void addHeaderChild(Object anObject)
	{
		d_headerChildren.add((Equipment)anObject);
	}


	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 *@return           ???
	 */
	public int indexOfHeaderChild(Object anObject)
	{
		return d_headerChildren.indexOf(anObject);
	}


	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public int countHeaderChildren()
	{
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
	 *@return    ???
	 */
	public ArrayList getHeaderChildren()
	{
		return d_headerChildren;
	}


	/**
	 *  ???
	 *
	 *@param  anArray  ???
	 */
	public void setHeaderChildren(ArrayList anArray)
	{
		d_headerChildren.clear();
		d_headerChildren.addAll(anArray);
	}


	/**
	 *  Removes a header child.
	 *
	 *@param  anObject  the header child to remove
	 */
	public void removeHeaderChild(Object anObject)
	{
		d_headerChildren.remove(anObject);
	}


	/**
	 *  Removes all header children.
	 */
	public void clearHeaderChildren()
	{
		d_headerChildren.clear();
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
				System.out.println("SPROP: eqMod expected but not found: " + eqMod.getName());
			}
		}

		final String common = Globals.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(comn), true));
		final String saList1 = Globals.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list1), true));
		final String saList2 = Globals.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list2), false));
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
			aString.append(" -").append(headerParent.indexOfHeaderChild(this) + 1).append("- ");
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


	/**
	 *  Gets the childCount attribute of the Equipment object
	 *
	 *@return    The childCount value
	 */
	public int getChildCount()
	{
		return d_containedEquipment.size();
	}


	/**
	 *  Gets a child of the Equipment object
	 *
	 *@param  childIndex  The index of the child to get
	 *@return    The child value
	 */
	public Object getChild(int childIndex)
	{
		return d_containedEquipment.get(childIndex);
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
		d_childTypes.put("Total", new Float(getChildTypes("Total").floatValue() - qtyRemoved.floatValue()));
		String aString = anEquip.isIndexedUnderType();
		d_childTypes.put(aString, new Float(getChildTypes(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		d_containedEquipment.remove(i);
		updateContainerContentsString();
		anEquip = (Equipment)this;
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
		Equipment anEquip = (Equipment)d_containedEquipment.get(childIndex);
		Float qtyRemoved = anEquip.numberCarried();
		d_childTypes.put("Total", new Float(getChildTypes("Total").floatValue() - qtyRemoved.floatValue()));
		String aString = anEquip.isIndexedUnderType();
		d_childTypes.put(aString, new Float(getChildTypes(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		d_containedEquipment.remove(childIndex);
		updateContainerContentsString();
		anEquip = (Equipment)this;
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
		return d_containedEquipment.indexOf(child);
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

		if (d_childTypes.containsKey(aString))
		{
			aFloat = new Float(getChildTypes(aString).floatValue() + aFloat.floatValue());
		}

		bFloat = new Float(getChildTypes("Total").floatValue() + bFloat.floatValue());
		d_childTypes.put(aString, aFloat);
		d_childTypes.put("Total", bFloat);
		d_containedEquipment.add(anEquip);
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
				if (true)
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
					return 3;
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
	 *  Gets the childTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return          The childTypes value
	 */
	private Float getChildTypes(String aString)
	{
		return (Float)d_childTypes.get(aString);
	}


	/**
	 *  Gets the acceptsTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return          The acceptsTypes value
	 */
	private Float getAcceptsTypes(String aString)
	{
		return (Float)d_acceptsTypes.get(aString);
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
		if (getChildTypes("Total") == null)
		{
			d_childTypes.put("Total", new Float(0));
		}
		if ((getChildTypes("Total").floatValue() + aQuant.floatValue()) <= getAcceptsTypes("Total").floatValue())
		{

			Iterator e = aTypeList.iterator();
			String aString = null;
			while (e.hasNext() && canContain.equals(""))
			{
				aString = (String)e.next();

				if (Globals.isDebugMode())
				{
					System.out.println("Testing " + aString);
				}

				if (d_acceptsTypes.containsKey(aString))
				{
					if (Globals.isDebugMode())
					{
						System.out.println("Accepts " + aString);
					}

					if (d_childTypes.containsKey(aString))
					{
						if ((getChildTypes(aString).floatValue() + aQuant.floatValue()) <= getAcceptsTypes(aString).floatValue())
						{
							canContain = aString;
						}
					}
					else
					{
						if (aQuant.floatValue() <= getAcceptsTypes(aString).floatValue())
						{
							canContain = aString;
						}
					}
				}
			}

			if ((canContain.equals("")) && d_acceptsTypes.containsKey("Any"))
			{
				if (!d_childTypes.containsKey("Any"))
				{
					d_childTypes.put("Any", new Float(0));
				}

				if ((getChildTypes("Any").floatValue() + aQuant.floatValue()) <= getAcceptsTypes("Any").floatValue())
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
		if (d_acceptsTypes.containsKey("Any"))
		{
			if (((Float)d_acceptsTypes.get("Any")).intValue() == -1)
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
	private void setLongName(String aString)
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
			int iCount = aEqMod.associatedList.size();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(aEqMod.getPreCost(), "", "", true).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(getBaseQty() * iCount)));
		}
		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.associatedList.size();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(aEqMod.getPreCost(), "", "", false).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(getBaseQty() * iCount)));
		}
		//
		// c has cost of the item's modifications at the item's original size
		//
		double mult = Globals.sizeAdjustmentCostMultiplier(getSize(), typeList()) / Globals.sizeAdjustmentCostMultiplier(getBaseSize(), typeList());
		c = c.multiply(new BigDecimal(mult));

		BigDecimal itemCost = cost.add(c);

		c = new BigDecimal("0");
		int iPlus = 0;
		calculatingCost = true;
		weightAlreadyUsed = false;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.associatedList.size();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final String costFormula = aEqMod.getCost();
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(costFormula, "", "", true).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(getBaseQty() * iCount)));
			iPlus += aEqMod.getPlus() * iCount;
		}
		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			int iCount = aEqMod.associatedList.size();
			if (iCount < 1)
			{
				iCount = 1;
			}
			final String costFormula = aEqMod.getCost();
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(costFormula, "", "", false).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(getBaseQty() * iCount)));
			iPlus += aEqMod.getPlus() * iCount;
		}
		calculatingCost = false;

		//
		// Tack on the cost of the magical enhancement
		//
		if (iPlus != 0)
		{
			BigDecimal aPlus = new BigDecimal(iPlus * iPlus);
			if (isArmor() || isShield())
			{
				// Cost += iPlus * iPlus * 1000
				aPlus = aPlus.multiply(new BigDecimal("1000"));
				c = c.add(aPlus);
			}
			else if (isWeapon() || isAmmunition())
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
				aPlus = aPlus.multiply(new BigDecimal(getBaseQty()));
				c = c.add(aPlus);
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
		if (c.compareTo(new BigDecimal(0)) != 0)
		{
			//
			// Convert to double and use math.ceil as ROUND_CEILING doesn't appear to work
			// on BigDecimal.divide
			final int baseQ = getBaseQty();
			itemCost = new BigDecimal(Math.ceil(itemCost.doubleValue() / baseQ) * baseQ);
		}
		return c.add(itemCost);
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
	private void setCost(String aString, boolean bBase)
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
	public Integer getAcMod()
	{
		int mod = acMod.intValue() + bonusTo("EQMARMOR", "AC", true);
		return new Integer(mod);
	}


	/**
	 *  Sets the acMod attribute of the Equipment object
	 *
	 *@param  aString  The new acMod value
	 */
	public void setAcMod(String aString)
	{
		try
		{
			acMod = Delta.decode(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
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
	private void setMaxDex(String aString)
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
			Equipment anEquip = null;
			for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)
			{
				anEquip = (Equipment)e.next();
				if (anEquip.getChildCount() > 0)
				{
					total = new Float(total.floatValue() + anEquip.getWeight().floatValue() + anEquip.getContainedWeight().floatValue());
				}
				else
				{
					total = new Float(total.floatValue() + anEquip.getWeight().floatValue() * anEquip.getCarried().floatValue());
				}
			}
		}

		return total;
	}


	/**
	 *  Sets the container attribute of the Equipment object
	 *
	 *@param  aString  The new container value
	 */
	private void setContainer(String aString)
	{
		//-1 means unlimited
		boolean limited = true;
		Float aFloat = new Float(0);
		d_acceptsChildren = true;
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.hasMoreTokens())
		{
			String bString = (String)aTok.nextToken();
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
			d_acceptsTypes.put("Any", new Float(-1));
		}
		String itemType = null;
		Float itemNumber = null;
		while (aTok.hasMoreTokens())
		{
			final StringTokenizer typeTok = new StringTokenizer((String)aTok.nextToken(), "=", false);
			itemType = (String)typeTok.nextToken();
			itemNumber = new Float(0);
			if (typeTok.hasMoreTokens())
			{
				itemNumber = new Float((String)typeTok.nextToken());

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
			{
				d_acceptsTypes.put(itemType.toUpperCase(), itemNumber);
			}
			else
			{
				d_acceptsTypes.put(itemType, itemNumber);
			}
		}

		if (!d_acceptsTypes.containsKey("Total"))
		{
			if (!limited)
			{
				aFloat = new Float(-1);
			}
			d_acceptsTypes.put("Total", aFloat);
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

		String aString = null;
		for (Iterator e = d_acceptsTypes.keySet().iterator(); e.hasNext();)
		{
			if (comma)
			{
				tempStringBuffer.append(", ");
				comma = false;
			}
			aString = (String)e.next();

			if (((Float)d_acceptsTypes.get(aString)).intValue() != -1)
			{
				tempStringBuffer.append(((Float)d_acceptsTypes.get(aString)).floatValue()).append(" ");
				tempStringBuffer.append(aString);
				comma = true;
			}
			else if (!aString.equals("Total"))
			{
				comma = true;
				tempStringBuffer.append(aString);
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
			 */
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
	public void setAcCheck(String aString)
	{
		try
		{
			acCheck = Delta.decode(aString);
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
	private void setSpellFailure(String aString)
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
		return moveString;
	}


	/**
	 *  Sets the moveString attribute of the Equipment object
	 *
	 *@param  aString  The new moveString value
	 */
	private void setMoveString(String aString)
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
		return new Float(aWeight / f);
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
////		return d_equipmentTypes.contains(aString.toUpperCase());
	}


	/**
	 *  Sets the typeString attribute of the Equipment object
	 *
	 *@param  aString  The new typeString value
	 */
	private void setTypeString(String aString)
	{
		setType(aString);

		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase(), ".", false);
		while (aTok.hasMoreTokens())
		{
			final String type = aTok.nextToken();
//			d_equipmentTypes.add(type);
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
	private void setAltTypeList(String aString)
	{
		aString = aString.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String type = aTok.nextToken();
			altTypeList.add(type);
//			d_equipmentTypes.add(type);
//			s_equipmentTypes.add(type);
		}
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
	private void setSize(String sizeString, boolean bBase)
	{
		size = sizeString;
		if (bBase)
		{
			sizeBase = sizeString;
		}
	}


	/**
	 *  Gets the damage attribute of the Equipment object
	 *
	 *@return    The damage value
	 */
	public String getDamage()
	{
		final int iSize = Globals.sizeInt(getSize());
		int iMod = iSize + bonusTo("EQMWEAPON", "DAMAGESIZE", true);
		if (iMod < Globals.SIZE_F)
		{
			iMod = Globals.SIZE_F;
		}
		if (iMod > Globals.SIZE_C)
		{
			iMod = Globals.SIZE_C;
		}
		return adjustDamage(damage, Globals.s_SIZESHORT[iMod]);
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
		if (critRange.length() != 0)
		{
			try
			{
				return Integer.parseInt(critRange);
			}
			catch (NumberFormatException nfe)
			{
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
		if (critRange.length() == 0)
		{
			return "";
		}

		return Integer.toString(getRawCritRange() * (getCritRangeDouble(true) + 1) + getCritRangeAdd(true));
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
		if (!isDouble() || (critRange.length() == 0))
		{
			return "";
		}
		return Integer.toString(getRawCritRange() * (getCritRangeDouble(false) + 1) + getCritRangeAdd(false));
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
	private String multAsString(int mult)
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
	private int multAsInt(int mult)
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
				System.out.println("parseCritMult:" + getName() + ":" + aString);
			}
		}
		return -1;
	}


	/**
	 *  Gets the critMult attribute of the Equipment object
	 *
	 *@return    The critMult value
	 */
	public String getCritMult()
	{
		return multAsString(critMult);
	}


	/**
	 *  Gets the critMultiplier attribute of the Equipment object
	 *
	 *@return    The critMultiplier value
	 */
	public int getCritMultiplier()
	{
		return multAsInt(critMult);
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
		return multAsString(altCritMult);
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
		return multAsInt(altCritMult);
	}


	/**
	 *  Sets the altCrit attribute of the Equipment object
	 *
	 *@param  aString  The new altCrit value
	 */
	private void setAltCrit(String aString)
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
		int r = range.intValue() + bonusTo("EQMWEAPON", "RANGEADD", true);
		int i = bonusTo("EQMWEAPON", "RANGEMULT", true);
		if (i > 0)
		{
			r *= i;
		}
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
	 *  returns the superclass's typelist.
	 *
	 *@return    Description of the Return Value
	 */
	public ArrayList rawTypeList()
	{
		return super.typeList();
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
		return isType("PROJECTILE");
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
//		return d_equipmentTypes;
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
		for (Iterator e = d_headerChildren.iterator(); e.hasNext();)
		{
			//could just report d_headerChildren.size(), but this is safer.

			aQty += ((Equipment)e.next()).qty().floatValue();
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
	 *@param  hand  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	private String profName(final int hand)
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
			// eg. for "Sword (Bastard/[Hands])"
			// we should get "Sword (Bastard/1-H)", "Sword (Bastard/2-H)"
			//
			/*
			 *  if (hand == BOTH_HANDS)
			 *  aWProf = aWProf.substring(0,iOffs) + "2-H" + aWProf.substring(iOffs+7);
			 *  else
			 *  aWProf = aWProf.substring(0,iOffs) + "1-H" + aWProf.substring(iOffs+7);
			 */
			// The basic logic should be: if the weapon is Light, or is one-handed and being used
			// in two hands, then use the 2-H proficiency.  The reason being that the 2-H version
			// of these profs will be lesser than the 1-H version (2-H is probably Martial, and 1-H
			// is exotic. For races which can only use these weapons in 2 hands, the prof must be the
			// harder one (probably exotic). Bryan McRoberts (merton_monk@yahoo.com) 10/20/01
			final String w1String = aWProf.substring(0, iOffs) + "1-H" + aWProf.substring(iOffs + 7);
			final String w2String = aWProf.substring(0, iOffs) + "2-H" + aWProf.substring(iOffs + 7);
			final WeaponProf wp = Globals.getWeaponProfNamed(w1String);
			if (wp == null || wp.isLight() || (wp.isOneHanded() && hand == BOTH_HANDS))
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
			return carried;
		}

		float aQty = 0;
		for (Iterator e = d_headerChildren.iterator(); e.hasNext();)
		{
			aQty += ((Equipment)e.next()).numberCarried().floatValue();
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
		if (aFlag)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (isSuit())
			{
				equipped = aPC.canEquip("SUIT");
			}
			else if (isType("SHIRT"))
			{
				equipped = aPC.canEquip("SHIRT");
			}
			else if (isType("HEADGEAR"))
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
			else if (isType("ROBE"))
			{
				equipped = aPC.canEquip("ROBE");
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
			else if (isWeapon() && aPC.canEquip("Weapon") ||
				isShield() && aPC.canEquip("Shield"))
			{
				int iRaceHands = aPC.getRace().getHands();
				int handsInUse = aPC.handsFull();
				if (isWeapon())
				{
					String aWProf = profName(hand);
					WeaponProf wp = Globals.getWeaponProfNamed(aWProf);
					if (wp == null)
					{
						equipped = false;
						JOptionPane.showMessageDialog(null, "Cannot equip weapon - no valid weapon proficiency for " + getName() + " loaded.", "PCGen", JOptionPane.ERROR_MESSAGE);
						if (Globals.isDebugMode())
						{
							System.out.println("Globals: " + Globals.getWeaponProfList());
							System.out.println("Proficiency name: " + aWProf + " " + this);
						}
						return;
					}

					if (wp.isOneHanded())
					{
						handsInUse++;
					}
					if (wp.isTwoHanded())
					{
						handsInUse += 2;
					}
					if (wp.isTooLarge())
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
					JOptionPane.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", "PCGen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				equipped = true;
			}
			else
			{
				equipped = aFlag;
			}
			if (equipped == false)
			{
				JOptionPane.showMessageDialog(null, "Character cannot equip any more of this item type.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
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

		eq.setAcCheck(acCheck.toString());
		eq.setAcMod(acMod.toString());
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
//		eq.setTypeString(typeString());
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
		eq.d_acceptsTypes = new HashMap(d_acceptsTypes);
		eq.containerConstantWeight = containerConstantWeight;
		eq.d_childTypes = new HashMap(d_childTypes);
		eq.containerContentsString = containerContentsString;
		eq.containerCapacityString = containerCapacityString;
		eq.d_containedEquipment = new ArrayList(d_containedEquipment);
		eq.eqModifierList = new ArrayList(eqModifierList);
		eq.altEqModifierList = new ArrayList(altEqModifierList);
		eq.modifiersAllowed = modifiersAllowed;

		//header crap
		eq.hasHeaderParent = hasHeaderParent;
		eq.headerParent = headerParent;
		eq.d_headerChildren = new ArrayList(d_headerChildren);
		return eq;
	}


	/**
	 *  Adds a feature to the VariableList attribute of the Equipment object
	 *
	 *@param  aString  The feature to be added to the VariableList attribute
	 */
	public void addVariableList(String aString)
	{
		variableList.add(aString);
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
			WeaponProf wp = Globals.getWeaponProfNamed(profName(hand));
			if (wp == null)
			{
				JOptionPane.showMessageDialog(null, "Cannot find a valid weapon prof for " + name + " loaded.", "PCGen", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (wp.getType().equals("Natural"))
			{
				//Don't worry if it can be placed in those hands if it is a natural weapon.

				if (modifiedName().endsWith("Primary") && !isOnlyNaturalWeapon && hand != PRIMARY_HAND && hand != NEITHER_HAND)
				{
					JOptionPane.showMessageDialog(null, "Can only place primary Natural weapon in Primary \"Hand\".", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Secondary") && hand != SECONDARY_HAND && hand != NEITHER_HAND)
				{
					JOptionPane.showMessageDialog(null, "Can only place secondary Natural weapon in Off \"Hand\".", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Primary") && isOnlyNaturalWeapon && hand != BOTH_HANDS && hand != NEITHER_HAND)
				{
					JOptionPane.showMessageDialog(null, "Can only place sole Natural weapon in both \"hands\".", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else
			{
				if (wp.isLight() && hand == BOTH_HANDS && !wp.isTwoHanded())
				{
					JOptionPane.showMessageDialog(null, "Cannot place light weapon in both hands.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (wp.isTwoHanded() && hand != NEITHER_HAND && hand != BOTH_HANDS)
				{
					JOptionPane.showMessageDialog(null, "Two handed weapon must be in Neither or Both hands.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}
		inHand = hand;
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


	/**
	 *  Description of the Method
	 *
	 *@param  aLine       Description of the Parameter
	 *@param  sourceFile  Description of the Parameter
	 *@param  lineNum     Description of the Parameter
	 */
	public void parseLine(String aLine, File sourceFile, int lineNum)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		int col = 0;
		String aString = null;
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (super.parseTag(aString))
			{
				continue;
			}
			final int aLen = aString.length();
			if (col == 0)
			{
				setName(aString);
			}
			else if ((aLen > 5) && aString.startsWith("Cost:"))
			{
				setCost(aString.substring(5), true);
			}
			else if (aLen > 7 && aString.startsWith("CHOOSE"))
			{
				setChoiceString(aString.substring(7));
			}
			else if ((aLen > 3) && aString.startsWith("AC:"))
			{
				setAcMod(aString.substring(3));
			}
			else if ((aLen > 7) && aString.startsWith("MAXDEX:"))
			{
				setMaxDex(aString.substring(7));
			}
			else if ((aLen > 8) && aString.startsWith("ACCHECK:"))
			{
				setAcCheck(aString.substring(8));
			}
			else if ((aLen > 5) && aString.startsWith("MOVE:"))
			{
				setMoveString(aString.substring(5));
			}
			else if ((aLen > 3) && aString.startsWith("WT:"))
			{
				setWeight(aString.substring(3));
			}
			else if ((aLen > 10) && aString.startsWith("BONUSTYPE:"))
			{
				bonusType = aString.substring(10);
			}
			else if ((aLen > 5) && aString.startsWith(Globals.s_TAG_TYPE))
			{
				setTypeString(aString.substring(Globals.s_TAG_TYPE.length()));
			}
			else if ((aLen > 8) && aString.startsWith("ALTTYPE:"))
			{
				setAltTypeList(aString.substring(8));
			}
			else if ((aLen > 5) && aString.startsWith("SIZE:"))
			{
				setSize(aString.substring(5), true);
			}
			else if ((aLen > 13) && aString.startsWith("SPELLFAILURE"))
			{
				setSpellFailure(aString.substring(13));
			}
			else if ((aLen > 7) && aString.startsWith("DAMAGE:"))
			{
				setDamage(aString.substring(7));
			}
			else if ((aLen > 10) && aString.startsWith("CRITRANGE:"))
			{
				setCritRange(aString.substring(10));
			}
			else if ((aLen > 9) && aString.startsWith("CRITMULT"))
			{
				setCritMult(aString.substring(9));
			}
			else if ((aLen > 6) && aString.startsWith("RANGE:"))
			{
				setRange(aString.substring(6));
			}
			else if ((aLen > 9) && aString.startsWith("LONGNAME"))
			{
				setLongName(aString.substring(9));
			}
			else if ((aLen > 8) && aString.startsWith("ATTACKS"))
			{
				attacks = Delta.decode(aString.substring(8));
			}
			else if ((aLen > 12) && aString.startsWith("PROFICIENCY"))
			{
				setProfName(aString.substring(12));
			}
			else if ((aLen > 7) && aString.startsWith("DEFINE"))
			{
				variableList.add("0|" + aString.substring(7));
			}
			else if ((aLen > 4) && aString.startsWith("KEY:"))
			{
				setKeyName(aString.substring(4));
			}
			else if (aString.startsWith("PRE"))
			{
				preReqArrayList.add(aString);
			}
			else if (aString.startsWith("QUALIFY:"))
			{
				addToQualifyListing(aString.substring(8));
			}
			else if ((aLen > 10) && aString.startsWith("ALTDAMAGE:"))
			{
				setAltDamage(aString.substring(10));
			}
			else if ((aLen > 12) && aString.startsWith("ALTCRITICAL:"))
			{
				setAltCrit(aString.substring(12));
			}
			else if (aString.startsWith("REACH"))
			{
				reach = Integer.parseInt(aString.substring(6));
			}
			else if (aString.startsWith("BASEQTY:"))
			{
				setBaseQty(aString.substring(8));
			}
			else if (aString.startsWith("EQMOD:"))
			{
				addEqModifiers(aString.substring(6), true);
			}
			else if (aString.startsWith("ALTEQMOD:"))
			{
				addEqModifiers(aString.substring(9), false);
			}
			else if (aString.startsWith("MODS:"))
			{
				modifiersAllowed = aString.substring(5).startsWith("Y");
			}
			else if ((aLen > 6) && aString.startsWith("SPROP:"))
			{
				specialProperties = aString.substring(6);
			}
			else if ((aLen > 6) && aString.startsWith("HANDS:"))
			{
				try
				{
					hands = Delta.parseInt(aString.substring(6));
				}
				catch (NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog
						(null, "Illegal number of required hands " +
						sourceFile.getName() + ":" + Integer.toString(lineNum)
						+ " \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (aString.startsWith("CONTAINS:"))
			{
				setContainer(aString.substring(9));
			}
			else
			{
				JOptionPane.showMessageDialog
					(null, "Illegal equipment info " +
					sourceFile.getName() + ":" + Integer.toString(lineNum)
					+ " \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
			}
			col++;
		}
		if (isArmor())
		{
			if (bonusType == null)
			{
				bonusType = "Armor";
				return;
			}
			if (bonusType.lastIndexOf("Armor") > -1)
			{
				return;
			}
			bonusType += "Armor";
		}
		if (isShield())
		{
			if (bonusType == null)
			{
				bonusType = "Shield";
				return;
			}
			if (bonusType.lastIndexOf("Shield") > -1)
			{
				return;
			}
			bonusType += "Shield";
		}
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
		int iBonus = 0;
		bonusMap.clear();
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

		String aBonusKey = aType.toUpperCase() + "." + aName.toUpperCase() + ".";
		for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			if (aKey.startsWith(aBonusKey))
			{
				iBonus += Integer.parseInt((String)bonusMap.get(aKey));
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
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			if (aEqMod.willIgnore(eqModKey))
			{
				return true;
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
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			if (aEqMod.getKeyName().equals(eqModKey))
			{
				return aEqMod;
			}
		}
		return null;
	}


	/**
	 *
	 * Add a list equipment modifiers and their associated information
	 * eg. Bane:Vermin:Fey.Keen.Vorpal
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
			final String aEqMod = aTok.nextToken();
			if (!aEqMod.equalsIgnoreCase(Globals.s_NONE))
			{
				addEqModifier(aEqMod, bPrimary);
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
				System.out.println("Could not find EquipmentModifier: " + eqModKey);
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
		while (aTok.hasMoreTokens())
		{
			final String x = aTok.nextToken();
			eqMod.associatedList.add(x);
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
		if ((eq.associatedList.size() == 0) || (eq.getChoice(0) == 0))
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

		setBase();

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
				if (eqMod.isType("BaseMaterial"))
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
		if (!bImporting && (eq.getChoice(1) == 0))
		{
			eqModList.remove(eq);
		}
		Globals.sortPObjectList(eqModList);
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
			for (Iterator e2 = eqMod.associatedList.iterator(); e2.hasNext();)
			{
				aString.append('|').append((String)e2.next());
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
		if (bPrimary || (altTypeList.size() == 0))
		{
			typeList = new ArrayList(super.typeList());
		}
		else
		{
			if (!isDouble())
			{
				return new ArrayList();
			}
			typeList = new ArrayList(altTypeList);
		}
		ArrayList modTypeList = new ArrayList();

		final ArrayList eqModList = getEqModifierList(bPrimary);
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
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
			saList.addAll(aEqMod.getSpecialAbilities());
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
				System.out.println("Bad SA: tag '" + aString + "'");
				// The Archer Class' use of the ADD:WEAPONBONUS(TOHIT|2+((CL=Archer)/5) tag is causing this to pop up. It shouldn't.
				// JOptionPane.showMessageDialog(null, "Bad SA: tag '" + aString + "'", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
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
				final String aString = (String)aList.get(idx);
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
	 *@return        The acModAdjustedForSize value
	 */
	public Integer getAcModAdjustedForSize(String aSize)
	{
		int iAc = getAcMod().intValue();
		if (isArmor())
		{
			double mult = Globals.sizeAdjustmentAcModMultiplier(aSize, typeList()) / Globals.sizeAdjustmentAcModMultiplier(getSize(), typeList());
			iAc = new Float(getAcMod().doubleValue() * mult).intValue();
		}
		return new Integer(iAc);
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
	public String getDamageAdjustedForSize(String aSize)
	{
		if (!isWeapon())
		{
			return damage;
		}
		return adjustDamage(damage, aSize);
	}


	/**
	 *  Gets the altDamageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  The size to adjust for
	 *@return        The altDamageAdjustedForSize value
	 */
	public String getAltDamageAdjustedForSize(String aSize)
	{
		if (!isWeapon())
		{
			return altDamage;
		}
		return adjustDamage(altDamage, aSize);
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
			int x = Globals.innerMostStringStart(aString);
			int y = Globals.innerMostStringEnd(aString);
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
			String bString = "";
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
					bString = "";
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
					bString = "";
				}
				else if (cString.equals("ELSE"))
				{
					valt = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = "";
				}
				else
				{
					bString = bString + cString;
				}
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString, src, subSrc, bPrimary);
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
						System.out.println("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				if (Globals.isDebugMode())
				{
					System.out.println("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
				}
				return total;
			}
		}
		for (int i = 0; i < aString.length(); i++)
		{
			valString = valString + aString.substring(i, i + 1);
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
						valString = weight.toString();
						weightAlreadyUsed = true;
					}
				}
				else if (valString.equals("DMGDIE"))
				{
					final Globals.rollInfo aRollInfo = Globals.getDiceInfo(getDamage());
					valString = Integer.toString(aRollInfo.sides);
				}
				else if (valString.equals("DMGDICE"))
				{
					final Globals.rollInfo aRollInfo = Globals.getDiceInfo(getDamage());
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
							valString = "99";
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
						}
					}
				}
				catch (Exception exc)
				{
					JOptionPane.showMessageDialog(null, "Equipment:Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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
		if (vis == eqMod.VISIBLE_QUALIFIED)
		{
			return eqMod.passesPreReqTests(this);
		}
		return vis == eqMod.VISIBLE_YES;
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
			Equipment eq = Globals.getEquipmentKeyed(baseItem);
			if (eq != null)
			{
				profName = eq.rawProfName();
				if (profName.length() == 0)
				{
					profName = eq.getName();
				}
			}
		}

		if (getSize().length() == 0)
		{
			setSize(Globals.s_SIZESHORT[Globals.SIZE_M]);
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
				setAcMod(eq.getAcModAdjustedForSize(newSize).toString());
				setDamage(eq.getDamageAdjustedForSize(newSize).toString());
				setAltDamage(eq.getAltDamageAdjustedForSize(newSize).toString());

				//
				// Adjust the capacity of the container (if it is one)
				//
				if (containerCapacityString.length() > 0)
				{
					final double mult = Globals.sizeAdjustmentMultiplier(newSize, eq.typeList(), "Capacity");
					if (containerWeightCapacity.intValue() != -1)
					{
						containerWeightCapacity = new Float(eq.containerWeightCapacity.doubleValue() * mult);
					}

					for (Iterator e = eq.d_acceptsTypes.keySet().iterator(); e.hasNext();)
					{
						final String aString = (String)e.next();
						Float aWeight = (Float)eq.d_acceptsTypes.get(aString);
						if (aWeight.intValue() != -1)
						{
							aWeight = new Float(aWeight.doubleValue() * mult);
							d_acceptsTypes.put(aString, aWeight);
						}
					}
					updateContainerCapacityString();
				}
			}

			//
			// Since we've just resized the item, we need to modify any PRESIZE prerequisites
			//
			for (int i = 0; i < preReqArrayList.size(); i++)
			{
				String aBonus = (String)preReqArrayList.get(i);
				if (aBonus.startsWith("PRESIZE"))
				{
					int idx = aBonus.indexOf(':') + 1;
					// must be at least 7 to be valid, so can ignore -1
					if (idx > 0)
					{
						int iOldPre = Globals.sizeInt(aBonus.substring(idx));
						iNewSize += iOldPre - iOldSize;
						if ((iNewSize >= Globals.SIZE_F) && (iNewSize <= Globals.SIZE_C))
						{
							aBonus = aBonus.substring(0, idx) + Globals.s_SIZESHORT[iNewSize];
							preReqArrayList.set(i, aBonus);
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
				System.out.println("eqMod expected but not found: " + eqMod.getName());
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
			common.append(Globals.s_SIZELONG[iSize]);
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
		cleanTypes();
		setName(itemName.toString());
		return getName();
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
			if (eqMod.getItemType().contains("MAGIC"))
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
			String aString = (String)aTok.nextToken();
			int i;
			for (i = Globals.SIZE_F; i <= Globals.SIZE_C; i++)
			{
				if (aString.equalsIgnoreCase(Globals.s_SIZELONG[i]))
				{
					break;
				}
			}
			//
			// Ignore size or "Standard" unless previous tag was "ARMOR" and this is "MEDIUM"
			//
			if ((i <= Globals.SIZE_C) || aString.equalsIgnoreCase("Standard"))
			{
				if ((i != Globals.SIZE_M) || !aCleaned.toString().toUpperCase().endsWith("ARMOR"))
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

}
