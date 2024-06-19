/*
 * Equipment.java
 * Copyright 2001 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:16:13 $
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.bonus.BonusObj;
import pcgen.core.money.DenominationList;
import pcgen.core.money.Denominations;
import pcgen.core.money.Purse;
import pcgen.core.utils.EmptyIterator;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.io.FileAccess;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.WeaponProfLoader;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.JEPResourceChecker;
import pcgen.util.Logging;
import pcgen.util.PJEP;

/**
 *  <code>Equipment</code>.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * created    December 27, 2001
 * @version    $Revision: 1.1 $
 **/
public final class Equipment extends PObject implements Serializable, EquipmentCollection, Comparable
{
	private static final String EQMOD_WEIGHT = "_WEIGHTADD";

	// The item is held in neither hand
	public static final int EQUIPPED_NEITHER = 0;
	public static final String EQUIPPED_NEITHER_STR = "Equipped";

	// The item is held in the primary hand
	public static final int EQUIPPED_PRIMARY = 1;
	public static final String EQUIPPED_PRIMARY_STR = "Hand (Primary)";

	// The item is held in the secondary hand
	public static final int EQUIPPED_SECONDARY = 2;
	public static final String EQUIPPED_SECONDARY_STR = "Hand (Off-hand)";

	// The item is held in both hands
	public static final int EQUIPPED_BOTH = 3;
	public static final String EQUIPPED_BOTH_STR = "Hand (Both)";

	// The item is either a double weapon or one of a pair of weapons
	public static final int EQUIPPED_TWO_HANDS = 4;
	public static final String EQUIPPED_TWO_HANDS_STR = "Hand (Two-Weapons)";

	// The item is held in neither hand and equipped for a temporary bonus
	public static final int EQUIPPED_TEMPBONUS = 5;
	public static final String EQUIPPED_TEMPBONUS_STR = "Temp Bonus";

	// The item is carried but not equipped
	public static final int CARRIED_NEITHER = 6;
	public static final String CARRIED_NEITHER_STR = "Carried";

	// The item is contained by another item
	public static final int CONTAINED = 7;
	public static final String CONTAINED_STR = "Contained";

	// The item is not carried.  For a String representation,
	public static final int NOT_CARRIED = 8;
	public static final String NOT_CARRIED_STR = "Not Carried";

	// These are now initialized in the static{} initializer
	public static final String[] locationStringList = new String[9];

	private static SortedSet s_equipmentTypes = new TreeSet();

	private Integer acCheck = new Integer(0);
	//private Integer acMod = new Integer(0);
	private int altCritMult = 0;
	private String altCritRange = "";
	private String altDamage = "";
	private List altEqModifierList = new ArrayList();
	private List altTypeList = null;
	/** if is true a BAB of 13 yields 13/8/3, if false, merely 13. */
	private boolean attacksProgress = true;
	private BigDecimal baseCost = BigDecimalHelper.ZERO;
	// support tracking of cost as coins.
	private Purse coinCost;
	private Purse baseCoinCost;

	private String baseItem = "";
	private boolean bonusPrimary = true;
	private String bonusType = null;
	private boolean calculatingCost = false;
	private Float carried = new Float(0);
	private String containerCapacityString = "";
	private String containerContentsString = "";
	private boolean containerConstantWeight = false;
	private Float containerWeightCapacity = new Float(0);
	private Integer containerReduceWeight = new Integer(0);
	private BigDecimal cost = BigDecimalHelper.ZERO;
	private BigDecimal costMod = BigDecimalHelper.ZERO;
	private String critRange = "";
	private int critMult = 0;
	private EquipmentCollection d_parent = null;
	private List d_containedEquipment = null;
	private boolean d_acceptsChildren = false;
	private Map d_acceptsTypes = null;
	private Map d_childTypes = null;
	private Map treasureList = null;
	private String damage = "";
	// effective DR vales for Armor
	private Integer eDR = new Integer(-1);
	private List eqModifierList = new ArrayList();
	private int slots = 1;
	private String indexedUnderType = "";
	private boolean isOnlyNaturalWeapon = false;
	private int location = NOT_CARRIED;
	private boolean equipped = false;
	private String longName = "";
	private String appliedBonusName = "";
	private Integer maxDex = new Integer(100);
	private String modifiedName = "";
	private boolean modifiersAllowed = true;
	private boolean modifiersRequired = false;
	private String moveString = "";
	private int numberEquipped = 0;
	private int outputIndex = 0;
	private String profName = "";
	private double qty = 0.0;
	private Integer range = new Integer(0);
	// How fast the weapon can be fired.
	private String rateOfFire = "";
	private int reach = 0;
	private static final long serialVersionUID = 1;
	private String size = SystemCollections.getDefaultSizeAdjustment().getAbbreviation();
	private String sizeBase = "";
	private String specialProperties = "";
	private Integer spellFailure = new Integer(0);
	// The equipment's techlevel.
	private String techLevel = "";
	// player added note
	private String noteString = "";

	private List vFeatList = null;		// virtual feat list
	private double weight = 0.0;
	private boolean weightAlreadyUsed = false;
	private BigDecimal weightMod = BigDecimalHelper.ZERO;

	private int memberKit = -1;
	private boolean automatic = false;

	public void setMemberOfKit(final int kitNo)
	{
		memberKit = kitNo;
	}

	public int getMemberOfKit()
	{
		return memberKit;
	}

	static
	{
		locationStringList[EQUIPPED_NEITHER] = EQUIPPED_NEITHER_STR;
		locationStringList[EQUIPPED_PRIMARY] = EQUIPPED_PRIMARY_STR;
		locationStringList[EQUIPPED_SECONDARY] = EQUIPPED_SECONDARY_STR;
		locationStringList[EQUIPPED_BOTH] = EQUIPPED_BOTH_STR;
		locationStringList[EQUIPPED_TWO_HANDS] = EQUIPPED_TWO_HANDS_STR;
		locationStringList[EQUIPPED_TEMPBONUS] = EQUIPPED_TEMPBONUS_STR;
		locationStringList[CARRIED_NEITHER] = CARRIED_NEITHER_STR;
		locationStringList[CONTAINED] = CONTAINED_STR;
		locationStringList[NOT_CARRIED] = NOT_CARRIED_STR;
	}

	/**	bsmeister
	 * 10/22/2002
	 * This initialization block is to make sure that
	 * the coinCost object is properly initialized or,
	 * if there is no denominationlist, doesn't generate
	 * an error
	 * I didn't see any other initialization blocks,
	 * nor a constructor in which to put it	 Not
	 * wanting to mess anything up, I didn't create a
	 * new constructor  If creating a constructor is
	 * appropriate, please let me know and I will do so
	 **/
	{
		final DenominationList dl = Globals.getDenominationList();
		if (dl != null)
		{
			final Denominations d = dl.getGlobalDenominations();
			if (d != null)
			{
				coinCost = new Purse(d);
				baseCoinCost = new Purse(d);
			}
		}
	}

	/**
	 * Returns the weapon's rate of fire
	 * Defaults to empty string
	 * @return The weapon's rate of fire
	 */
	public String getRateOfFire()
	{
		return rateOfFire;
	}

	/**
	 * Set the weapon's rate of fire
	 * @param rateOfFire A free-format string.
	 **/
	public void setRateOfFire(String rateOfFire)
	{
		this.rateOfFire = rateOfFire;
	}

	/**
	 * Returns the equipment's tech level
	 * Defaults to empty string
	 * @return The equipment's tech level
	 * Left alone for a while, will remove in a month unless it is used.
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 **/
	public String getTechLevel()
	{
		return techLevel;
	}

	/**
	 * Sets the weapons tech level
	 * Left alone for a while, will remove in a month unless it is used.
	 * @param aString
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 */
	public void setTechLevel(String aString)
	{
		techLevel = aString;
	}

	/**
	 * return the player added note for this item
	 **/
	public String getNote()
	{
		return noteString;
	}

	/**
	 * set's the player added note for this item
	 **/
	public void setNote(final String aString)
	{
		noteString = aString;
	}

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
	 **/
	public Integer eDR()
	{
		int check = eDR.intValue() + (int) bonusTo("EQMARMOR", "EDR", true);
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
		try
		{
			eDR = new Integer(aString);
		}
		catch (NumberFormatException nfe)
		{
			eDR = new Integer(0);
			// ignore
		}
	}

	/**
	 * setter
	 *
	 */
	private void addContainedEquipment(Equipment e)
	{
		if (d_containedEquipment == null)
		{
			d_containedEquipment = new ArrayList();
		}
		d_containedEquipment.add(e);
	}

	/**
	 *  contains
	 *
	 *@return true if containedEquipment contains the passed item
	 */
	public boolean containsContainedEquipment(Equipment e)
	{
		if (d_containedEquipment == null)
		{
			return false;
		}
		return d_containedEquipment.contains(e);
	}

	/**
	 *  count
	 *
	 *@return number of containedEquipment objects
	 */
	public int getContainedEquipmentCount()
	{
		if (d_containedEquipment == null)
		{
			return 0;
		}
		return d_containedEquipment.size();
	}

	/**
	 * accessor
	 *
	 *@return containedEquipment object
	 */
	public Equipment getContainedEquipment(int i)
	{
		return (Equipment) d_containedEquipment.get(i);
	}

	/**
	 * accessor
	 *
	 *@return index of containedEquipment object
	 */
	private int getContainedEquipmentIndexOf(Equipment e)
	{
		if (d_containedEquipment == null)
		{
			return -1;
		}
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
	 *  Returns the number of slots required to use this item.
	 *
	 *@return    the number of slots required to use this item.
	 */
	public int getHands()
	{
		return getSlots();
	}

	public void setHands(int argHands)
	{
		slots = argHands;
	}

	/**
	 * The number of "Slots" that this item requires
	 * The slot type is derived from system/special/equipmentslot.lst
	 **/
	public int getSlots()
	{
		int iSlots = slots;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			iSlots += (int) aEqMod.bonusTo("EQM", "HANDS", this);
			iSlots += (int) aEqMod.bonusTo("EQM", "SLOTS", this);
		}

		if (iSlots < 0)
		{
			iSlots = 0;
		}
		return iSlots;
	}

	/**
	 * Set the number of "Slots" required to equip this item
	 **/
	public void setSlots(int i)
	{
		slots = i;
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
	 *@param  slotNumber  the slot for which a name is wanted
	 *@return	      the name of this slot
	 */
	public static String getLocationName(int slotNumber)
	{
		if ((slotNumber < 0) || (slotNumber > locationStringList.length))
		{
			return locationStringList[0];
		}
		else
		{
			return locationStringList[slotNumber];
		}
	}

	/**
	 *  Returns the number of a slot
	 *@param  locDesc  The name of a location one wants to know the number of
	 *@return	    the number of a location
	 */
	public static int getLocationNum(String locDesc)
	{
		for (int i = 0; i < locationStringList.length; ++i)
		{
			if (locationStringList[i].equals(locDesc))
			{
				return i;
			}
		}

		if (locDesc.equals(Constants.s_NONE))
		{
			return NOT_CARRIED;
		}

		if (locDesc.startsWith(CONTAINED_STR))
		{
			return CONTAINED;
		}

		try
		{
			return Integer.parseInt(locDesc);
		}
		catch (NumberFormatException nfe)
		{
			// Assume that the string is the name of another equipment item
			return CONTAINED;
			//GuiFacade.showMessageDialog(null, "Unable to interpret hand setting: " + handDesc, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			//return NOT_CARRIED;
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
	 **/
	public String getInterestingDisplayString()
	{
		final StringBuffer s = new StringBuffer();
		String t = getSpecialProperties();

		if (t == null)
		{
			t = "";
		}

		for (Iterator mI = getActiveBonuses().iterator(); mI.hasNext();)
		{
			BonusObj aBonus = (BonusObj) mI.next();
			String eqBonus = aBonus.toString();
			if ((eqBonus.length() > 0) && !eqBonus.startsWith("EQM"))
			{
				if (s.length() != 0)
				{
					s.append(", ");
				}
				s.append(eqBonus);
			}
		}
/*
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			EquipmentModifier eqMod = (EquipmentModifier) e.next();
			for (Iterator mI = eqMod.getBonusList().iterator(); mI.hasNext();)
			{
				BonusObj aBonus = (BonusObj) mI.next();
				String eqModBonus = aBonus.toString();
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
*/

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
		final List list1 = new ArrayList(eqModifierList);
		final List list2 = new ArrayList(altEqModifierList);
		final List comn = new ArrayList();

		extractListFromCommon(comn, list1);

		removeCommonFromList(list2, comn, "SPROP: eqMod expected but not found: ");

		final String common = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(comn), true));
		final String saList1 = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list1), true));
		final String saList2 = Utility.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(list2), false));
		final StringBuffer sp = new StringBuffer(specialProperties.length() + common.length() + saList1.length() + saList2.length() + 5);

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
	 * Remove the common modifiers from the alternate list.
	 * @param altList
	 * @param commonList
	 * @param errMsg
	 */
	private void removeCommonFromList(final List altList, final List commonList, String errMsg)
	{
		for (int i = altList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) altList.get(i);
			if (!eqMod.getAssignToAll())
			{
				continue;
			}

			final int j = commonList.indexOf(eqMod);
			if (j >= 0)
			{
				altList.remove(i);
			}
			else
			{
				Logging.errorPrint(errMsg + eqMod.getName());
			}
		}
	}

	/**
	 *  Sets special properties of an Equipment.
	 *
	 *@param  aProp	 The properties to set
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
		return toString(true);
	}

	public String toString(boolean addCharges)
	{
		final StringBuffer aString = new StringBuffer(name);

		if (modifiedName.length() > 0)
		{
			aString.append(" (").append(modifiedName).append(")");
		}

		if (addCharges && (getRemainingCharges() > getMinCharges()) && (getRemainingCharges() < getMaxCharges()))
		{
			aString.append("(").append(getRemainingCharges()).append(")");
		}
		return aString.toString();
	}

	/**
	 * Set's the Temporary Bonuses name used for Display on Output Sheets
	 **/
	public void setAppliedName(final String aString)
	{
		appliedBonusName = aString;
	}

	public String getAppliedName()
	{
		if (appliedBonusName.length() > 0)
		{
			final StringBuffer aString = new StringBuffer();
			aString.append(" [").append(appliedBonusName).append("]");
			return aString.toString();
		}
		return "";
	}

	/**
	 *@param  aString
	 */
	private void setIndexedUnderType(String aString)
	{
		indexedUnderType = aString;
	}

	/**
	 *  Gets the indexedUnderType attribute of the Equipment object
	 *
	 *@return    The indexedUnderType value
	 */
	private String isIndexedUnderType()
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
	 *  Gets the simple name attribute of the Equipment object
	 *
	 *@return    The name value
	 */
	public String getSimpleName()
	{
		return name;
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
	 *@param  child	 The child to remove
	 */
	public void removeChild(Object child)
	{
		final int i = indexOfChild(child);
		Equipment anEquip = (Equipment) child;
		final Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", new Float(getChildType("Total").floatValue() - qtyRemoved.floatValue()));
		final String aString = anEquip.isIndexedUnderType();
		setChildType(aString, new Float(getChildType(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		removeContainedEquipment(i);
		updateContainerContentsString();
		anEquip = this;
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment) anEquip.getParent();
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
		removeChild(getChild(childIndex));
	}

	/**
	 *  Gets the index of a child
	 *
	 *@param  child	 The child
	 *@return	 the index of the child
	 */
	public int indexOfChild(Object child)
	{
		if (!(child instanceof Equipment))
		{
			return -1;
		}
		return getContainedEquipmentIndexOf((Equipment) child);
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
		Equipment anEquip = (Equipment) getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment) anEquip.getParent();
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
		Equipment anEquip = (Equipment) getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment) anEquip.getParent();
			++i;
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
	 *  @return    The parentName
	 */
	public String getParentName()
	{
		final Equipment anEquip = (Equipment) getParent();
		if (anEquip != null)
		{
			return anEquip.toString();
		}
		if (isEquipped())
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
	 * Why ask why? Have some pie!
	 *
	 *@param  child	 The child to add
	 **/
	public void insertChild(Object child)
	{
		if (child == null)
		{
			return;
		}

		Equipment anEquip = (Equipment) child;
		Float aFloat = anEquip.numberCarried();
		Float bFloat = aFloat;

		final String aString = pickChildType(anEquip.eqTypeList(), aFloat);

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
			anEquip = (Equipment) anEquip.getParent();
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

			final Equipment anEquip = (Equipment) obj;

			if (checkChildWeight(new Float(anEquip.getWeightAsDouble() * anEquip.numberCarried().floatValue())))
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
	 *@return	   true if has child type
	 */
	private boolean containsChildType(String aString)
	{
		if (d_childTypes == null)
		{
			return false;
		}
		return d_childTypes.containsKey(aString);
	}

	/**
	 *  Gets the acceptsTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return	   The acceptsTypes value
	 */
	private Float getChildType(String aString)
	{
		if (d_childTypes == null)
		{
			return null;
		}
		return (Float) d_childTypes.get(aString);
	}

// --Recycle Bin START (7/17/03 11:56 PM):
//	/**
//	 *  Gets the number of child types
//	 *
//	 *@return  The number of distinct types
//	 */
//	private int getChildTypeCount()
//	{
//		if (d_childTypes == null)
//		{
//			return 0;
//		}
//		return d_childTypes.size();
//	}
// --Recycle Bin STOP (7/17/03 11:56 PM)

	/**
	 *  Sets the child type value
	 *
	 *@param  parameter  Description of the Parameter
	 *@param  childType child type
	 */
	public void setChildType(String parameter, Float childType)
	{
		if (d_childTypes == null)
		{
			d_childTypes = new HashMap();
		}
		d_childTypes.put(parameter, childType);
	}

	/**
	 *  Checks whether the proposed type is one that is accepted
	 *
	 *@param  aString  Description of the Parameter
	 *@return	   The acceptsTypes value
	 */
	private boolean acceptsType(String aString)
	{
		if (d_acceptsTypes == null)
		{
			return false;
		}
		return d_acceptsTypes.containsKey(aString.toUpperCase());
	}

	/**
	 *  Gets the number of accepted types
	 *
	 *@return  The number of distinct types
	 */
	private int getAcceptsTypeCount()
	{
		if (d_acceptsTypes == null)
		{
			return 0;
		}
		return d_acceptsTypes.size();
	}

	/**
	 *  Gets the acceptsTypes attribute of the Equipment object
	 *
	 *@param  aString  Description of the Parameter
	 *@return	   The acceptsTypes value
	 */
	private Float getAcceptsType(String aString)
	{
		if (d_acceptsTypes == null)
		{
			return null;
		}
		return (Float) d_acceptsTypes.get(aString.toUpperCase());
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
		{
			d_acceptsTypes = new HashMap();
		}
		d_acceptsTypes.put(parameter.toUpperCase(), acceptsType);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aTypeList  Description of the Parameter
	 *@param  aQuant     Description of the Parameter
	 *@return	     Description of the Return Value
	 */
	private String pickChildType(SortedSet aTypeList, Float aQuant)
	{
		String canContain = "";
		Float acceptsType = getAcceptsType("TOTAL");
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
			final Iterator e = aTypeList.iterator();
			String aString;
			while (e.hasNext() && "".equals(canContain))
			{
				aString = (String) e.next();

				if (acceptsType(aString))
				{
					if (containsChildType(aString) && (getChildType(aString).floatValue() + aQuant.floatValue()) <= getAcceptsType(aString).floatValue())
					{
						canContain = aString;
					}
					else if (aQuant.floatValue() <= getAcceptsType(aString).floatValue())
					{
						canContain = aString;
					}
				}
			}

			if (("".equals(canContain)) && acceptsType("Any"))
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
	 *@return	     Description of the Return Value
	 */
	private boolean checkContainerCapacity(SortedSet aTypeList, Float aQuant)
	{
		if (acceptsType("Any"))
		{
			if (getAcceptsType("Any").intValue() == -1)
			{
				return true;
			}
		}

		return !("".equals(pickChildType(aTypeList, aQuant)));
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aFloat  Description of the Parameter
	 *@return	  Description of the Return Value
	 */
	private boolean checkChildWeight(Float aFloat)
	{
		if (containerWeightCapacity.intValue() == -1)
		{
			return true;
		}
		if ((aFloat.floatValue() + getContainedWeight().floatValue()) <= containerWeightCapacity.floatValue())
		{
			return true;
		}
		return false;
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
	private BigDecimal getBaseCost()
	{
		return baseCost;
	}

	/*
	 * bsmeister
	 * 10/22/2002
	 * For now, this just gets the BigDecimal version, and then converts
	 * it to a Purse object.
	 */
	public Purse getBaseCoinCost()
	{
		if (baseCoinCost != null)
		{
			final BigDecimal dbCost = getBaseCost();
			baseCoinCost.setAmount(dbCost.doubleValue());
		}
		return baseCoinCost;
	}

	/*
	 * bsmeister
	 * 10/22/2002
	 * For now, this just gets the BigDecimal version, and then converts
	 * it to a Purse object.  At some future time, it should actually
	 * do the things that the BigDecimal version is doing.
	 */
	public Purse getCoinCost()
	{
		if (coinCost != null)
		{
			final BigDecimal dbCost = getCost();
			coinCost.setAmount(dbCost.doubleValue());
		}
		return coinCost;
	}

	/**
	 *  Gets the cost attribute of the Equipment object
	 *
	 *@return    The cost value
	 */
	public BigDecimal getCost()
	{
		BigDecimal c = BigDecimalHelper.ZERO;

		//
		// Do pre-sizing cost increment.
		// eg. in the case of adamantine armor, want to add
		// the cost of the metal before the armor gets resized.
		//
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
		double mult = 1.0;
		SizeAdjustment newSA = SystemCollections.getSizeAdjustmentNamed(getSize());
		SizeAdjustment currSA = SystemCollections.getSizeAdjustmentNamed(getBaseSize());
		if ((newSA != null) && (currSA != null))
		{
			mult = newSA.getBonusTo("ITEMCOST", typeList(), 1.0) / currSA.getBonusTo("ITEMCOST", typeList(), 1.0);
		}
		c = c.multiply(new BigDecimal(mult));

		BigDecimal itemCost = cost.add(c);

		final List modifierCosts = new ArrayList();

		BigDecimal nonDoubleCost = BigDecimalHelper.ZERO;

		c = BigDecimalHelper.ZERO;
		int iPlus = 0;
		int altPlus = 0;
		calculatingCost = true;
		weightAlreadyUsed = false;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			int iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}

			BigDecimal eqModCost;
			String costFormula = aEqMod.getCost();
			if ((aEqMod.getAssociatedCount() > 0) && !costFormula.equals(aEqMod.getCost(0)))
			{
				eqModCost = BigDecimalHelper.ZERO;
				for (int idx = 0; idx < aEqMod.getAssociatedCount(); ++idx)
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
		}

		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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

		c = c.add(getCostFromPluses(iPlus, altPlus));

		//
		// Items with values less than 1 gp have their prices rounded up to 1 gp per item
		// eg. 20 Arrows cost 1 gp, or 5 cp each. 1 MW Arrow costs 7 gp.
		//
		// Masterwork and Magical ammo is made in batches of 50, so the MW cost per item
		// should be 6 gp. This would give a cost of 6.05 gp per arrow, 6.1 gp per bolt and 6.01 gp
		// per bullet.
		//
		if (c.compareTo(BigDecimalHelper.ZERO) != 0)
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
				for (int i = modifierCosts.size() - 2; i >= 0; --i)
				{
					c = c.add((BigDecimal) modifierCosts.get(i));
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
	 *@param  bBase	   The new cost value
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
		catch (NumberFormatException ignore)
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
		final int mod = (int) bonusTo("EQMARMOR", "AC", true) + (int) bonusTo("COMBAT", "AC", true);
		return new Integer(mod);
	}

	/**
	 *  Gets the maxDex attribute of the Equipment object
	 *
	 *@return    The maxDex value
	 */
	public Integer getMaxDex()
	{
		int mdex = maxDex.intValue() + (int) bonusTo("EQMARMOR", "MAXDEX", true);
		if (mdex > Constants.MAX_MAXDEX)
		{
			mdex = Constants.MAX_MAXDEX;
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
	 **/
	public void setMaxDex(String aString)
	{
		try
		{
			maxDex = Delta.decode(aString);
		}
		catch (NumberFormatException ignore)
		{
			// ignore
		}
	}

	/**
	 *  Gets the AC attribute of the Equipment object
	 *@return    The acBonus value
	 **/
	public Integer getACBonus()
	{
		int dbon = (int) bonusTo("COMBAT", "AC", true);
		dbon += (int) bonusTo("EQMARMOR", "ACBONUS", true);
		return new Integer(dbon);
	}

	/**
	 * Gets the contained Weight this object
	 * recursis all child objects to get their contained weight
	 *
	 *@return    The containedWeight value
	 **/
	public Float getContainedWeight()
	{
		return getContainedWeight(false);
	}

	/**
	 * Gets the contained Weight this object
	 * recursis all child objects to get their contained weight
	 *
	 *@param  effective  Should we recurse child objects?
	 *@return	     The containedWeight value
	 **/
	public Float getContainedWeight(boolean effective)
	{
		Float total = new Float(0);

		if ((containerConstantWeight && !effective) || getChildCount() == 0)
		{
			return total;
		}
		else
		{
			for (int e = 0; e < getContainedEquipmentCount(); ++e)
			{
				Equipment anEquip = getContainedEquipment(e);
				if (anEquip.getContainedEquipmentCount() > 0)
				{
					total = new Float(total.floatValue() + anEquip.getWeightAsDouble() + anEquip.getContainedWeight().floatValue());
				}
				else
				{
					total = new Float(total.floatValue() + anEquip.getWeightAsDouble() * anEquip.getCarried().floatValue());
				}
			}
		}

		if (containerReduceWeight.intValue() > 0)
		{
			total = new Float(total.floatValue() * (containerReduceWeight.floatValue() / 100));
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
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		if (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if ((bString != null) && bString.charAt(0) == '*')
			{
				containerConstantWeight = true;
				bString = bString.substring(1);
			}
			if ((bString != null) && (bString.indexOf('%') > 0))
			{
				int pos = bString.indexOf('%');
				String redString = bString.substring(0, pos);
				bString = bString.substring(pos + 1);
				try
				{
					containerReduceWeight = new Integer(redString);
				}
				catch (NumberFormatException ex)
				{
					Logging.errorPrint("Error in CONTAINS line: "+aString);
					containerReduceWeight = new Integer(0);
				}
			}
			try
			{
				containerWeightCapacity = new Float(bString);
			}
			catch (NumberFormatException ex)
			{
				Logging.errorPrint("Error in CONTAINS line: "+aString);
				containerWeightCapacity = new Float(-1);
			}
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
		String itemType;
		Float itemNumber;
		while (aTok.hasMoreTokens())
		{
			final StringTokenizer typeTok = new StringTokenizer(aTok.nextToken(), "=");
			itemType = typeTok.nextToken();
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
			if (!"Any".equals(itemType) && !"Total".equals(itemType))
			{
				setAcceptsType(itemType, itemNumber);
			}
			else
			{
				setAcceptsType(itemType, itemNumber);
			}
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
	 * Creates the containerCapacityString from children of this object
	 **/
	private void updateContainerCapacityString()
	{
		final StringBuffer tempStringBuffer = new StringBuffer();
		boolean comma = false;

		if (containerWeightCapacity.intValue() != -1)
		{
			tempStringBuffer.append(containerWeightCapacity).append(' ').append(Globals.getWeightDisplay());
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
				String aString = (String) e.next();

				if (getAcceptsType(aString).intValue() != -1)
				{
					tempStringBuffer.append(getAcceptsType(aString).floatValue()).append(' ');
					tempStringBuffer.append(aString);
					comma = true;
				}
				else if (!"TOTAL".equals(aString))
				{
					comma = true;
					tempStringBuffer.append(aString);
				}
			}
		}
		containerCapacityString = tempStringBuffer.toString();
	}

	/**
	 * Updates the containerContentsString from childrin of this item
	 **/
	public void updateContainerContentsString()
	{
		containerContentsString = "";
		final StringBuffer tempStringBuffer = new StringBuffer(getChildCount() * 20);
		// Make sure there's no bug here.
		if (acceptsChildren() && (getContainedWeight(true).floatValue() > 0.0f))
		{
			tempStringBuffer.append(getContainedWeight(true)).append(' ').append(Globals.getWeightDisplay());
		}

		for (int e = 0; e < getChildCount(); ++e)
		{
			final Equipment anEquip = (Equipment) getChild(e);
			if (anEquip.getQty().floatValue() > 0.0f)
			{
				tempStringBuffer.append(", ");
				tempStringBuffer.append(BigDecimalHelper.trimZeros(anEquip.getQty().toString()));
				tempStringBuffer.append(" ");
				tempStringBuffer.append(anEquip);
			}
		}
		containerContentsString = tempStringBuffer.toString();
	}

	/**
	 * calculates the value of all items in this container
	 * If this container contains containers, also add the value
	 * of all items within that container, etc, etc, etc.
	 **/
	public double getContainedValue()
	{
		double total = 0;
		if (getChildCount() == 0)
		{
			return total;
		}
		for (int e = 0; e < getContainedEquipmentCount(); ++e)
		{
			Equipment anEquip = getContainedEquipment(e);
			if (anEquip.getContainedEquipmentCount() > 0)
			{
				total += anEquip.getContainedValue();
			}
			else
			{
				total += anEquip.getCost().floatValue();
			}
		}
		return total;
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
		int check = acCheck.intValue() + (int) bonusTo("EQMARMOR", "ACCHECK", true);
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
		int fail = spellFailure.intValue() + (int) bonusTo("EQMARMOR", "SPELLFAILURE", true);
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
		catch (NumberFormatException ignore)
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
			final int eqLoad;
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
			final StringTokenizer aTok = new StringTokenizer(moveString, ",");
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
				final StringBuffer retString = new StringBuffer(moveString.length());
				for (int i = 0; i < tokenCount; ++i)
				{
					if (i != 0)
					{
						retString.append(',');
					}
					retString.append(Globals.calcEncumberedMove(eqLoad, baseMove, true));
					baseMove -= 10;
				}
				return retString.toString();
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
		return new Float(getWeightAsDouble());
	}

	public double getWeightAsDouble()
	{
		double f = bonusTo("EQM", "WEIGHTMULT", true);
		if (Utility.doublesEqual(f, 0.0))
		{
			f = 1.0;
		}
		double aWeight = weight * f;

		f = bonusTo("EQM", "WEIGHTDIV", true);
		if (Utility.doublesEqual(f, 0))
		{
			f = 1;
		}
		aWeight /= f;

/*		SizeAdjustment saSize = Globals.getSizeAdjustmentNamed(getSize());
		SizeAdjustment saBase = Globals.getSizeAdjustmentNamed(getBaseSize());
		if ((saSize != null) && (saBase != null))
		{
			final double saDbl = saSize.getBonusTo("ITEMWEIGHT", typeList(), 1.0);
			final double saBaseDbl = saBase.getBonusTo("ITEMWEIGHT", typeList(), 1.0);
			final double mult = saBaseDbl / saDbl;
			aWeight /= mult;
		}*/
		aWeight += bonusTo("EQM", "WEIGHTADD", true);
		aWeight += weightMod.doubleValue();

		return aWeight;
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
			weight = Double.parseDouble(aString);
		}
		catch (NumberFormatException ignore)
		{
			// ignore
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aString  Description of the Parameter
	 *@return	   Description of the Return Value
	 */
	boolean typeStringContains(String aString)
	{
		return isType(aString);
	}

	protected void doGlobalUpdate(final String aString)
	{
		s_equipmentTypes.add(aString);
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
	 *  Adds to the altTypeList attribute of the Equipment object
	 *
	 *@param  argAltType  The new altTypeList value
	 */
	public void addToAltTypeList(String argAltType)
	{
		String altType = argAltType.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(altType, ".");
		while (aTok.hasMoreTokens())
		{
			final String type = aTok.nextToken();
			addAltType(type);
		}
	}

	private int getAltTypeCount()
	{
		if (altTypeList == null)
		{
			return 0;
		}
		return altTypeList.size();
	}

	private void addAltType(String type)
	{
		if (altTypeList == null)
		{
			altTypeList = new ArrayList(1);
		}
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
	private String getBaseSize()
	{
		return sizeBase;
	}

	/**
	 *  Sets the size attribute of the Equipment object
	 *
	 *@param  sizeString  The new size value
	 */
	private void setSize(String sizeString)
	{
		setSize(sizeString, false);
	}

	/**
	 *  Sets the size attribute of the Equipment object
	 *
	 *@param  sizeString  The new size value
	 *@param  bBase	      The new size value
	 */
	public void setSize(String sizeString, boolean bBase)
	{
		if (sizeString.length() > 1)
		{
			sizeString = sizeString.toUpperCase().substring(1);
		}
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
			for (int i = 0; i < eqMod.getAssociatedCount(); ++i)
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

	private String getDamage(boolean bPrimary)
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
		int iMod = iSize + (int) bonusTo("EQMWEAPON", "DAMAGESIZE", bPrimary);
		if (iMod < 0)
		{
			iMod = 0;
		}
		else if (iMod >= SystemCollections.getSizeAdjustmentListSize() - 1)
		{
			iMod = SystemCollections.getSizeAdjustmentListSize() - 1;
		}
		return adjustDamage(baseDamage, ((SizeAdjustment) SystemCollections.getSizeAdjustmentAtIndex(iMod)).getAbbreviation());
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
	 *@return	    The bonusToHit value
	 */
	public int getBonusToHit(boolean bPrimary)
	{
		return (int) bonusTo("WEAPON", "TOHIT", bPrimary);
	}

	/**
	 *  Gets the bonusToDamage attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return	    The bonusToDamage value
	 */
	public int getBonusToDamage(boolean bPrimary)
	{
		return (int) bonusTo("WEAPON", "DAMAGE", bPrimary);
	}

	/**
	 *  Gets the critRangeDouble attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return	    The critRangeDouble value
	 */
	public int getCritRangeDouble(boolean bPrimary)
	{
		return (int) bonusTo("EQMWEAPON", "CRITRANGEDOUBLE", bPrimary);
	}

	/**
	 *  Gets the critRangeAdd attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return	    The critRangeAdd value
	 */
	public int getCritRangeAdd(boolean bPrimary)
	{
		return (int) bonusTo("EQMWEAPON", "CRITRANGEADD", bPrimary);
	}

	/**
	 *  Gets the rawCritRange attribute of the Equipment object
	 *
	 *@return    The rawCritRange value
	 */
	public int getRawCritRange()
	{
		return getRawCritRange(true);
	}

	/**
	 *  Gets the rawCritRange attribute of the Equipment object
	 *
	 *@param  bPrimary  True=Primary Head
	 *@return    The rawCritRange value
	 */

	public int getRawCritRange(boolean bPrimary)
	{
		String cr = bPrimary ? critRange : altCritRange;
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
			catch (NumberFormatException ignore)
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

	private String getCritRange(boolean bPrimary)
	{
		String cr = bPrimary ? critRange : altCritRange;
		if (cr.length() == 0)
		{
			cr = getWeaponInfo("CRITRANGE", bPrimary);
		}
		if ((cr.length() == 0) || (!bPrimary && !isDouble()))
		{
			return "";
		}

		return Integer.toString(getRawCritRange(bPrimary) * (getCritRangeDouble(bPrimary) + 1) + getCritRangeAdd(bPrimary));
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
	 *@param  mult	Description of the Parameter
	 *@return	Description of the Return Value
	 */
	private static String multAsString(int mult)
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
	 *@param  mult	Description of the Parameter
	 *@return	Description of the Return Value
	 */
	private static int multAsInt(int mult)
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
	 *@return	   Description of the Return Value
	 */
	private int parseCritMult(String aString)
	{
		if (aString.length() > 0 && aString.charAt(0) == 'x')
		{
			try
			{
				return Integer.parseInt(aString.substring(1));
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("parseCritMult:" + getName() + ":" + aString);
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
	private int getCritMultiplier()
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
	 *  Sets the altCritMult attribute of the Equipment object
	 *
	 *@param  aString  The new altCritMult value
	 */

	public void setAltCritMult(String aString)
	{
		altCritMult = parseCritMult(aString);
	}

	/**
	 *  Sets the altCritRange attribute of the Equipment object
	 *
	 *@param  aString  The new altCritRange value
	 */

	public void setAltCritRange(String aString)
	{
		altCritRange = aString;
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

		int r = myRange.intValue() + (int) bonusTo("EQMWEAPON", "RANGEADD", true);
		final int i = (int) bonusTo("EQMWEAPON", "RANGEMULT", true);
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
				r += (int) aPC.getTotalBonusTo("RANGEADD", "THROWN");
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "THROWN");
				rangeMult += (int) aPC.getTotalBonusTo("RANGEMULT", "THROWN") / 100.0;
			}
			else if (isProjectile())
			{
				r += (int) aPC.getTotalBonusTo("RANGEADD", "PROJECTILE");
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "PROJECTILE");
				rangeMult += (int) aPC.getTotalBonusTo("RANGEMULT", "PROJECTILE") / 100.0;
			}
		}
		r *= rangeMult;
		r += postAdd;
		// If it's a ranged, thrown or projectile, it must have a range
		if ((isRanged() || isThrown() || isProjectile()) && (r <= 0))
		{
			r = 10;
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
		catch (NumberFormatException ignore)
		{
			// ignore
		}
	}


	/**
	 *  Gets the range list of the Equipment object, adding the 30' range, if not present and required
	 *
	 *  @param addShortRange  boolean
	 *  @return    The range list
	 */

	public List getRangeList(boolean addShortRange)
	{
		List aList = new ArrayList();
		final int baseRange = getRange().intValue();
		int aRange = baseRange, maxIncrements = 0;

		if (isRanged())
		{
			if (isThrown())
			{
				maxIncrements = 5;
			}
			else
			{
				maxIncrements = 10;
			}
		}

		for (int numIncrements = 0; numIncrements < maxIncrements; ++numIncrements)
		{
			if (aRange == SettingsHandler.getGame().getShortRangeDistance())
			{
				addShortRange = false;
			}
			if (aRange > SettingsHandler.getGame().getShortRangeDistance() && addShortRange)
			{
				aList.add(Integer.toString(SettingsHandler.getGame().getShortRangeDistance()));
				addShortRange = false;
			}
			aList.add(Integer.toString(aRange));
			aRange += baseRange;
		}
		return aList;
	}

//	/**
//	 *  Gets the attacks attribute of the Equipment object
//	 *
//	 *@return    The attacks value
//	 */
//	public Integer getAttacks()
//	{
//		return attacks;
//	}
//
//	public void setAttacks(Integer argAttacks)
//	{
//		attacks = argAttacks;
//	}

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
	private boolean isProjectile()
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
	boolean isMasterwork()
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
	 * @deprecated -- this is dead code; sage_sam 04 Sept 2003
	 * @return    Description of the Return Value
	 */
	private SortedSet eqTypeList()
	{
		return new TreeSet(typeList());
	}

	/**
	 *  Get the quantity of items
	 *
	 *@return    Description of the Return Value
	 */
	public double qty()
	{
		return qty;
	}

	/**
	 *  Get the quantity of items
	 *
	 *@return    return a Float of the quantity
	 **/
	public Float getQty()
	{
		return new Float(qty);
	}

	/**
	 *  Sets the qty attribute of the Equipment object
	 *
	 *@param  aString  The new qty value
	 */
	public void setQty(String aString)
	{
		try
		{
			setQty(Double.parseDouble(aString));
		}
		catch (NumberFormatException nfe)
		{
			qty = 0.0;
		}
	}

	/**
	 *  Sets the qty attribute of the Equipment object
	 *
	 *@param  aFloat  The new qty value
	 */
	public void setQty(Float aFloat)
	{
		setQty(aFloat.doubleValue());
	}

	void setQty(double argQty)
	{
		qty = argQty;
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
	String rawProfName()
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
		return profName(getLocation());
	}

	/**
	 *  Description of the Method
	 *
	 *@param  hand	int that equals number of hands
	 *@return	returns weapon proficiency string
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
			// "Sword (Bastard/Exotic)" or "Sword (Bastard/Martial)"
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
			final String w1String = aWProf.substring(0, iOffs) + "Exotic" + aWProf.substring(iOffs + 7);
			final String w2String = aWProf.substring(0, iOffs) + "Martial" + aWProf.substring(iOffs + 7);

			final PlayerCharacter aPC = Globals.getCurrentPC();
			final WeaponProf wpMartial = Globals.getWeaponProfNamed(w2String);
			//
			// Check to see if non-handed weaponprof exists and, if it does, whether or not the PC can wield
			// the weapon 1-handed. If PC can, then use this proficiency
			//
			if ((wpMartial != null) && Globals.isWeaponOneHanded(aPC, this, wpMartial))
			{
				aWProf = w2String;
			}
			else
			{
				final WeaponProf wpExotic = Globals.getWeaponProfNamed(w1String);
				if ((wpExotic == null) || (Globals.isWeaponOneHanded(aPC, this, wpExotic) && (hand == EQUIPPED_BOTH)))
				{
					aWProf = w2String;
				}
				else
				{
					aWProf = w1String;
				}
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
		Equipment eqParent = (Equipment) getParent();
		if (isEquipped() || (eqParent == null))
		{
			return carried;
		}
		for (; eqParent != null; eqParent = (Equipment) eqParent.getParent())
		{
			if (eqParent.isEquipped() || ((eqParent.getParent() == null) && (eqParent.numberCarried().intValue() != 0)))
			{
				return carried;
			}
		}
		return new Float(0);
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
		final Equipment e = (Equipment) o;
		return getName().compareToIgnoreCase(e.getName());
	}

	/**
	 *  Description of the Method
	 *
	 *@param  o  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public boolean equals(Object o)
	{
		return (o != null) && (o instanceof Equipment) && ((o == this) || getName().equals(((Equipment) o).getName()));
	}

	boolean equalTo(Object o)
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
		return getName().hashCode();
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
	static void clearEquipmentTypes()
	{
		s_equipmentTypes.clear();
	}

	/**
	 * Returns the list of virtual feats this item bestows upon its weilder.
	 *
	 *@return		List of virtual feats this item bestows upon its weilder.
	 */
	List getVFeatList()
	{
		List vFeats = null;
		if (vFeatList != null)
		{
			vFeats = new ArrayList(vFeatList);
		}

		boolean bPrimary;
		bPrimary = true;
		vFeats = addEqModList(bPrimary, vFeats);
		bPrimary = false;
		vFeats = addEqModList(bPrimary, vFeats);
		return vFeats;
	}

	/**
	 * Adds
	 * @param bPrimary
	 * @param argVFeats
	 * @return
	 */
	private List addEqModList(boolean bPrimary, List argVFeats)
	{
		List eqModList = getEqModifierList(bPrimary);
		List vFeats = argVFeats;
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
				if (aEqMod.hasVFeats())
				{
					if (vFeats == null)
					{
						vFeats = new ArrayList();
					}
					vFeats.addAll(aEqMod.getVFeatList());
				}
			}
		}
		return vFeats;
	}

	public boolean hasVFeats()
	{
		final List vFeats = getVFeatList();
		return (vFeats != null) && (vFeats.size() > 0);
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder.
	 * @param vList a | delimited list of feats to add to the list

	 */
	public void addVFeatList(String vList)
	{
		final StringTokenizer aTok = new StringTokenizer(vList, "|");
		while (aTok.hasMoreTokens())
		{
			if (vFeatList == null)
			{
				vFeatList = new ArrayList();
			}
			vFeatList.add(aTok.nextToken());
		}
	}

	/**
	 *  Adds a List to the Treasure Lists this item is on.
	 *
	 *@param  listName  Name of the List
	 *@param  argWeight    weight of item in taht list (for random generation)
	 */
	public void addTreasureList(String listName, int argWeight)
	{
		if (treasureList == null)
		{
			treasureList = new HashMap();
		}
		//Sigh, java 1.5 will make it no longer necessary to turn this into an Integer
		treasureList.put(listName, new Integer(argWeight));
	}

	/**
	 *  Sets the numberCarried attribute of the Equipment object.
	 *
	 *@param  aNumber  The new numberCarried value
	 */
	public void setNumberCarried(Float aNumber)
	{
		carried = aNumber;
	}

	/**
	 *  Gets the numberEquipped attribute of the Equipment object.
	 *
	 *@return    The numberEquipped value
	 */
	public int getNumberEquipped()
	{
		return numberEquipped;
	}

	/**
	 *  Sets the numberEquipped attribute of the Equipment object.
	 *
	 *@param  num  The new numberEquipped value
	 */
	public void setNumberEquipped(int num)
	{
		numberEquipped = num;
		if (num > 0)
		{
			equipped = true;
		}
	}

	/**
	 *  Gets the reach attribute of the Equipment object.
	 *
	 *@return    The reach value
	 */
	public int getReach()
	{
		return reach;
	}

	/**
	 *  Sets the reach attribute of the Equipment object.
	 *
	 *@param  newReach  The new reach value
	 */
	public void setReach(int newReach)
	{
		reach = newReach;
	}

	/**
	 *  Sets the isEquipped attribute of the Equipment object.
	 *
	 *@param  aFlag	 The new isEquipped value
	 */
	public void setIsEquipped(boolean aFlag)
	{
		equipped = aFlag;
		if (equipped)
		{
			activateBonuses();
		}
		else
		{
			deactivateBonuses();
		}
	}

	/**
	 *  Gets the modifiersAllowed attribute of the Equipment object.
	 *
	 *@return    The modifiersAllowed value
	 */
	boolean getModifiersAllowed()
	{
		return modifiersAllowed;
	}

	public void setModifiersAllowed(boolean argModifiersAllowed)
	{
		modifiersAllowed = argModifiersAllowed;
	}

	/**
	 *  Gets the modifiersRequired attribute of the Equipment object.
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
			costMod = BigDecimalHelper.ZERO;
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
			weightMod = new BigDecimal(aString);
		}
		catch (NumberFormatException e)
		{
			weightMod = BigDecimalHelper.ZERO;
		}
	}

	private List cloneEqModList(boolean primary)
	{
		final List clonedList = new ArrayList();
		for (Iterator e = getEqModifierList(primary).iterator(); e.hasNext();)
		{
			EquipmentModifier eqMod = (EquipmentModifier) e.next();
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eqMod = (EquipmentModifier) eqMod.clone();
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
		Equipment eq = null;
		try
		{
			eq = (Equipment) super.clone();
			//
			// These get modified by equipment modifiers so
			// DO NOT use the function or we'll get doubled bonuses
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
			eq.weight = weight;
			eq.setMaxDex(maxDex.toString());
			eq.setDamage(damage);
			eq.setAltDamage(altDamage);
			//////////////////////////////////////////////////////////

			eq.setMoveString(moveString());
			//eq.setTypeString(super.getType());
			// none of the types associated with modifiers
			eq.critMult = critMult;
			eq.isSpecified = isSpecified;
			eq.profName = profName;
			eq.carried = carried;
			eq.equipped = equipped;
			eq.location = location;
			eq.altCritMult = altCritMult;
			eq.altCritRange = altCritRange;
			eq.slots = slots;
			eq.bonusType = bonusType;
			eq.numberEquipped = numberEquipped;
			eq.reach = reach;
			eq.qty = qty;
			eq.outputIndex = outputIndex;
			eq.containerWeightCapacity = containerWeightCapacity;
			eq.containerReduceWeight = containerReduceWeight;
			eq.d_acceptsChildren = d_acceptsChildren;
			if (d_acceptsTypes != null)
			{
				eq.d_acceptsTypes = new HashMap(d_acceptsTypes);
			}
			eq.containerConstantWeight = containerConstantWeight;
			if (d_childTypes != null)
			{
				eq.d_childTypes = new HashMap(d_childTypes);
			}
			eq.containerContentsString = containerContentsString;
			eq.containerCapacityString = containerCapacityString;
			if (d_containedEquipment != null)
			{
				eq.d_containedEquipment = new ArrayList(d_containedEquipment);
			}
			eq.eqModifierList = cloneEqModList(true);
			eq.altEqModifierList = cloneEqModList(false);
			eq.modifiersAllowed = modifiersAllowed;
			eq.modifiersRequired = modifiersRequired;

		}
		catch (CloneNotSupportedException e)
		{
			GuiFacade.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return eq;
	}

	/**
	 *  Gets the hand attribute of the Equipment object
	 *	@return	 int containing the location value
	 */
	public int getLocation()
	{
		return location;
	}

	/**
	 *  Sets the location attribute of the Equipment object
	 *  @param newLocation int containing the new location value
	 */
	public void setLocation(int newLocation)
	{
		if ((newLocation < EQUIPPED_NEITHER) || (newLocation > NOT_CARRIED))
		{
			GuiFacade.showMessageDialog(null, "Location " + newLocation + " unknown.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}

		if (newLocation == EQUIPPED_TEMPBONUS)
		{
			location = newLocation;
			equipped = true;
		}
		else if ((newLocation >= EQUIPPED_NEITHER) && (newLocation <= EQUIPPED_TWO_HANDS))
		{
			location = newLocation;
			equipped = true;
		}
		else
		{
			if (newLocation == NOT_CARRIED)
			{
				location = NOT_CARRIED;
				equipped = false;
			}
			else
			{
				location = CARRIED_NEITHER;
				equipped = false;
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public boolean meetsPreReqs()
	{
		return passesPreReqToGain();
	}

	boolean save(BufferedWriter output)
	{
		FileAccess.write(output, "BASEITEM:" + formatSaveLine("\t", ":"));
		FileAccess.newLine(output);
		return true;
	}

	public String formatSaveLine(String sep, String endPart)
	{
		final StringBuffer sbuf = new StringBuffer(100);

		Equipment base;
		if (baseItem.length() != 0)
		{
			base = Globals.getEquipmentNamed(baseItem);
			sbuf.append(baseItem);
			sbuf.append(sep).append("NAME").append(endPart).append(toString(false));
		}
		else
		{
			base = this;
			sbuf.append(toString(false));
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
		if (!costMod.equals(BigDecimalHelper.ZERO))
		{
			sbuf.append(sep).append("COSTMOD").append(endPart).append(costMod.toString());
		}
		return sbuf.toString();
	}

	public void load(String aLine)
	{
		load(aLine, "\t", ":");
	}

	public void load(String aLine, String sep, String endPart)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, sep);
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
	 * returns all BonusObj's that are "active"
	 **/
	public List getActiveBonuses()
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (aBonus.isApplied())
			{
				aList.add(aBonus);
			}
		}
		final List eqModList = getEqModifierList(true);
		if (!eqModList.isEmpty())
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
				aList.addAll(aEqMod.getActiveBonuses());
			}
		}
		return aList;
	}

	/**
	 * get a list of BonusObj's of aType and aName
	 * @param  aType    a TYPE of bonus (such as "COMBAT" or "SKILL")
	 * @param  aName    the NAME of the bonus (such as "ATTACKS" or "SPOT")
	 * @param  bPrimary used for double weapons (head1 vs head2)
	 * @return a list of bonusObj's of aType and aName
	 **/
	public List getBonusListOfType(String aType, String aName, boolean bPrimary)
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if ((aBonus.getTypeOfBonus().indexOf(aType) >= 0) &&
				(aBonus.getBonusInfo().indexOf(aName) >= 0))
			{
				aList.add(aBonus);
			}
		}
		final List eqModList = getEqModifierList(bPrimary);
		if (!eqModList.isEmpty())
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
				aList.addAll(aEqMod.getBonusListOfType(aType, aName));
			}
		}
		return aList;
	}

	/**
	 *  Description of the Method
	 *@param  aType	    a TYPE of BONUS (such as "COMBAT" or "AC")
	 *@param  aName	    the NAME of the BONUS (such as "ATTACKS" or "ARMOR")
	 *@param  bPrimary  should we ask the parent object also?
	 *@return	    returns a double which is the sum of all bonuses
	 **/
	public double bonusTo(String aType, String aName, boolean bPrimary)
	{
		return bonusTo(aType, aName, this, bPrimary);
	}

	public double bonusTo(String aType, String aName, Object anObj, boolean bPrimary)
	{
		final String aBonusKey = new StringBuffer(aType.toUpperCase()).append('.').append(aName.toUpperCase()).append('.').toString();

		// go through bonus hashmap and zero out all
		// entries that deal with this bonus request
		for (Iterator e = getBonusMap().keySet().iterator(); e.hasNext();)
		{
			String aKey = e.next().toString();
			if (aKey.startsWith(aBonusKey))
			{
				putBonusMap(aKey, "0");
			}
		}

		double iBonus = 0;
		bonusPrimary = bPrimary;
		if (bPrimary)
		{
			super.bonusTo(aType, aName, this);

			// now do temp bonuses
			List tbList = new ArrayList();
			for (Iterator b = getTempBonusList().iterator(); b.hasNext();)
			{
				BonusObj aBonus = (BonusObj) b.next();
				if (!tbList.contains(aBonus))
				{
					tbList.add(aBonus);
				}
			}
			super.bonusTo(aType, aName, anObj, tbList);
		}

		final List eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
				iBonus += Float.parseFloat((String) getBonusMap().get(aKey));
			}
		}
		return iBonus;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  eqModKey  Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return	    Description of the Return Value
	 */
	private boolean willIgnore(String eqModKey, boolean bPrimary)
	{
		final List eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
	 *@return	    The eqModifierKeyed value
	 */
	public EquipmentModifier getEqModifierKeyed(String eqModKey, boolean bPrimary)
	{
		final List eqModList = getEqModifierList(bPrimary);
		if (eqModList.size() != 0)
		{
			for (Iterator e = eqModList.iterator(); e.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
	 * Remove a list equipment modifiers and their associated information
	 * eg: Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6
	 *
	 *  Removes a feature from the EqModifiers attribute of the Equipment object
	 *
	 *@param  aString   The feature to be removed from the EqModifiers attribute
	 *@param  bPrimary  The feature to be removed from the EqModifiers attribute
	 */
	public void removeEqModifiers(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
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
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
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
			for (int i = eqMod.getAssociatedCount() - 1; i >= 0; --i)
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
	 * Add a list equipment modifiers and their associated information
	 * eg: Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6
	 *
	 *  Adds a feature to the EqModifiers attribute of the Equipment object
	 *
	 *@param  aString   The feature to be added to the EqModifiers attribute
	 *@param  bPrimary  The feature to be added to the EqModifiers attribute
	 */
	public void addEqModifiers(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		while (aTok.hasMoreTokens())
		{
			final String aEqModName = aTok.nextToken();
			if (!aEqModName.equalsIgnoreCase(Constants.s_NONE))
			{
				addEqModifier(aEqModName, bPrimary);
			}
		}

		final List eqModList = getEqModifierList(bPrimary);
		Globals.sortPObjectList(eqModList);
	}

	/**
	 * Add an equipment modifier and its associated information
	 * eg: Bane|Vermin|Fey
	 * eg: Keen
	 *  Adds a feature to the EqModifier attribute of the Equipment object
	 *
	 *@param  aString   The feature to be added to the EqModifier attribute
	 *@param  bPrimary  The feature to be added to the EqModifier attribute
	 */
	public void addEqModifier(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		// The type of EqMod, eg: ABILITYPLUS
		final String eqModKey = aTok.nextToken();

		EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);
		//
		// If not already attached, then add a new one
		//
		if (eqMod == null)
		{
			if (eqModKey.equals(EQMOD_WEIGHT))
			{
				if (aTok.hasMoreTokens())
				{
					setWeightMod(aTok.nextToken().replace(',', '.'));
				}
				return;
			}

			eqMod = Globals.getModifierKeyed(eqModKey);
			if (eqMod == null)
			{
				Logging.errorPrint("Could not find EquipmentModifier: " + eqModKey);
				return;
			}
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eqMod = (EquipmentModifier) eqMod.clone();
			}

			final List eqModList = getEqModifierList(bPrimary);
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
		final List eqModList = getEqModifierList(bPrimary);
		for (; ;)
		{
			boolean bRemoved = false;
			for (int i = eqModList.size() - 1; i >= 0; --i)
			{
				final EquipmentModifier eqMod = (EquipmentModifier) eqModList.get(i);
				if (!eqMod.passesPreReqToGain(this))
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
	 *@param  eqMod	    Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 */
	public void removeEqModifier(EquipmentModifier eqMod, boolean bPrimary)
	{
		final List eqModList = getEqModifierList(bPrimary);
		final EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);
		if (aMod == null)
		{
			return;
		}
		//
		// Get a response from user (if one required). Remove the modifier if all associated choices
		// are deleted
		//
		if ((aMod.getAssociatedCount() == 0) || (aMod.getChoice(0, this, false) == 0))
		{
			eqModList.remove(aMod);
			removeUnqualified(bPrimary);
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  eqMod	    Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return	    Description of the Return Value
	 */
	public boolean canAddModifier(EquipmentModifier eqMod, boolean bPrimary)
	{
		//
		// Make sure we are qualified
		//
		bonusPrimary = bPrimary;
		if (!modifiersAllowed || !eqMod.passesPreReqToGain(this))
		{
			return false;
		}
		//
		// Don't allow adding of modifiers with %CHOICE cost to secondary head, as
		// cost is only calculated for these modifiers on primary head
		//
		if (!bPrimary && (eqMod.getCost().indexOf("%CHOICE") >= 0))
		{
			return false;
		}
		return true;
	}

	/**
	 *  Callback function from PObject.passesPreReqToGainForList()
	 *
	 *@param  aType	 Description of the Parameter
	 *@return	 The preType value
	 */
	boolean isPreType(String aType)
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
			String truePart;
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

		if (aType.startsWith("EQMODTYPE=") || aType.startsWith("EQMODTYPE."))
		{
			aType = aType.substring(10);
			for (Iterator e = getEqModifierList(bonusPrimary).iterator(); e.hasNext();)
			{
				final EquipmentModifier eqMod = (EquipmentModifier) e.next();
				if (eqMod.isType(aType))
				{
					return true;
				}
			}
			return false;
		}
		else if (aType.startsWith("EQMOD=") || aType.startsWith("EQMOD."))
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
	 *@param  eqMod	    The feature to be added to the EqModifier attribute
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
		final List eqModList = getEqModifierList(bPrimary);
		for (int i = eqModList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier aMod = (EquipmentModifier) eqModList.get(i);
			if (eqMod.willReplace(aMod.getKeyName()))
			{
				eqModList.remove(i);
			}
		}
		if (eqMod.isType("BaseMaterial"))
		{
			for (int i = eqModList.size() - 1; i >= 0; --i)
			{
				final EquipmentModifier aMod = (EquipmentModifier) eqModList.get(i);
				if (aMod.isType("BaseMaterial"))
				{
					eqModList.remove(i);
				}
			}
		}
		else if (eqMod.isType("MagicalEnhancement"))
		{
			for (int i = eqModList.size() - 1; i >= 0; --i)
			{
				final EquipmentModifier aMod = (EquipmentModifier) eqModList.get(i);
				if (aMod.isType("MagicalEnhancement"))
				{
					eqModList.remove(i);
				}
			}
		}

		//
		// Add the modifier if it's not already there
		//
		EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);
		if (aMod == null)
		{
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				aMod = (EquipmentModifier) eqMod.clone();
				if (aMod == null)
				{
					return;
				}
			}
			else
			{
				aMod = eqMod;
			}
			eqModList.add(aMod);
		}

		//
		// Get a response from user (if one required).
		// Remove the modifier if all associated choices are deleted
		//
		if (!bImporting && (aMod.getChoice(1, this, true) == 0))
		{
			eqModList.remove(aMod);
		}
		Globals.sortPObjectList(eqModList);

		setBase();
	}

	/**
	 *  Return an iterator through the eqModifierList attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return	    An iterator through EquipmentMod objects
	 */
	public Iterator getEqModifierIterator(boolean bPrimary)
	{
		if (bPrimary)
		{
			if (eqModifierList == null)
			{
				return EmptyIterator.EMPTY_ITERATOR;
			}
			return eqModifierList.iterator();
		}
		if (altEqModifierList == null)
		{
			return EmptyIterator.EMPTY_ITERATOR;
		}
		return altEqModifierList.iterator();
	}

	/**
	 *  Gets the eqModifierList attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return	    The eqModifierList value
	 */
	public List getEqModifierList(boolean bPrimary)
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
	 *@return	    The eqModifierString value
	 */
	private String getEqModifierString(boolean bPrimary)
	{
		final List eqModList = getEqModifierList(bPrimary);
		final StringBuffer aString = new StringBuffer(eqModList.size() * 10);
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			if (aString.length() != 0)
			{
				aString.append('.');
			}
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			aString.append(eqMod.getKeyName());
			//
			// Add the modifiers
			//
			for (int e2 = 0; e2 < eqMod.getAssociatedCount(); ++e2)
			{
				final String strMod = eqMod.getAssociated(e2);
				aString.append('|').append(strMod.replace('|', '='));
			}
		}

		if (bPrimary && (weightMod.compareTo(BigDecimalHelper.ZERO) != 0))
		{
			if (aString.length() != 0)
			{
				aString.append('.');
			}
			aString.append(EQMOD_WEIGHT).append('|').append(weightMod.toString().replace('.', ','));
		}
		return aString.toString();
	}

	/**
	 *  Returns a list of the types of this item.
	 *
	 *@return	    a list of the types of this item.
	 */
	public List typeList()
	{
		return typeList(true);
	}

	/**
	 *  Returns a list of the types of this item.
	 *
	 *@param  bPrimary  ???
	 *@return	    a list of the types of this item.
	 */
	private List typeList(boolean bPrimary)
	{
		//
		// Use the primary type(s) if none defined for secondary
		//
		final List typeList;
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
			if (altTypeList != null)
			{
				typeList.addAll(altTypeList);
			}
		}

		final List modTypeList = new ArrayList();

		//
		// Add in all type modfiers from "ADDTYPE" modifier
		//
		EquipmentModifier aEqMod = getEqModifierKeyed("ADDTYPE", bPrimary);
		if (aEqMod != null)
		{
			for (int e = 0; e < aEqMod.getAssociatedCount(); ++e)
			{
				String aType = aEqMod.getAssociated(e);
				aType = aType.toUpperCase();
				if (!typeList.contains(aType))
				{
					modTypeList.add(aType);
				}
			}
		}

		final List eqModList = getEqModifierList(bPrimary);
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			aEqMod = (EquipmentModifier) e.next();
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

				final List eqModTypeList = aEqMod.getItemType();
				for (Iterator e2 = eqModTypeList.iterator(); e2.hasNext();)
				{
					String aType = (String) e2.next();
					aType = aType.toUpperCase();
					// If it's BOTH & MELEE, we cannot add RANGED or THROWN to it
					// BOTH is only used after the split of a Thrown weapon in 2 (melee and ranged)
					if (typeList.contains("BOTH") && typeList.contains("MELEE") && ("RANGED".equals(aType) || "THROWN".equals(aType)))
					{
						continue;
					}
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
	 *@return	    The type value
	 */
	String getType(boolean bPrimary)
	{
		final List typeList = typeList(bPrimary);
		final int typeSize = typeList.size();
		final StringBuffer aType = new StringBuffer(typeSize * 5); //Just a guess.
		for (Iterator e = typeList.iterator(); e.hasNext();)
		{
			if (aType.length() != 0)
			{
				aType.append('.');
			}
			aType.append((String) e.next());
		}
		return aType.toString();
	}

	/**
	 *  Returns the type with the requested index
	 *
	 *@param  index	 the index
	 *@return    the type with the requested index
	 */
	public String typeIndex(int index)
	{
		final List tList = typeList();
		if ((index < 0) || (index >= tList.size()))
		{
			return "";
		}

		return (String) tList.get(index);
	}

	/**
	 *  Gets the type attribute of the Equipment object
	 *
	 *@param  aType	 Description of the Parameter
	 *@return	 The type value
	 */
	public boolean isType(String aType)
	{
		return isType(aType, true);
	}

	/**
	 *  Gets the type attribute of the Equipment object
	 *
	 *@param  aType	    Description of the Parameter
	 *@param  bPrimary  Description of the Parameter
	 *@return	    The type value
	 */
	public boolean isType(String aType, boolean bPrimary)
	{
		if (!bPrimary && !isDouble())
		{
			return false;
		}

		final List tList = typeList(bPrimary);
		return tList.contains(aType.toUpperCase());
	}

	/**
	 *  Gets the eitherType attribute of the Equipment object
	 *
	 *@param  aType	 Description of the Parameter
	 *@return	 The eitherType value
	 */
	public boolean isEitherType(String aType)
	{
		return isType(aType, true) | isType(aType, false);
	}

	/**
	 *  Gets the specialAbilityList attribute of the Equipment object
	 *
	 *@param  eqModList  Description of the Parameter
	 *@return	     The specialAbilityList value
	 */
	private static List getSpecialAbilityList(final List eqModList)
	{
		final List saList = new ArrayList();
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			saList.addAll(aEqMod.getSpecialProperties());
		}
		return saList;
	}

	/**
	 *  same as getSpecialAbilityList except if
	 *  if you have the same ability twice, it only
	 *  lists it once with (2) at the end.
	 *
	 *@param  argSpecialAbilityList	 Description of the Parameter
	 *@param  bPrimary	      Description of the Parameter
	 *@return		      The specialAbilityTimesList value
	 */
	private List getSpecialAbilityTimesList(List argSpecialAbilityList, boolean bPrimary)
	{
		final List aList = new ArrayList();
		final int[] times = new int[argSpecialAbilityList.size()];
		for (int x = 0; x < times.length; ++x)
		{
			times[x] = 0;
		}

		//
		// First, get a list of the unique abilities, counting the duplicates
		//
		for (Iterator i = argSpecialAbilityList.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
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
		for (int idx = 0; idx < aList.size(); ++idx)
		{
			String aString = (String) aList.get(idx);
			final int pos_pipe = aString.lastIndexOf('|');
			final int pos_perc = aString.lastIndexOf('%');
			if ((pos_pipe >= 0) && (pos_perc < 0))
			{
				Logging.errorPrint("Bad SA: tag '" + aString + "'");
				// The Archer Class' use of the ADD:WEAPONBONUS(TOHIT|2+((CL=Archer)/5) tag is causing this to pop up. It shouldn't.
				// GuiFacade.showMessageDialog(null, "Bad SA: tag '" + aString + "'", Globals.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
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
					if ("%".equals(nextTok))
					{
						newAbility.append(sInt);
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
		for (int idx = aList.size() - 1; idx >= 0; --idx)
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
		int iCount;
		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}
			iPlus += iCount * aEqMod.getPlus();
		}
		for (Iterator e = altEqModifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			iCount = aEqMod.getAssociatedCount();
			if (iCount < 1)
			{
				iCount = 1;
			}
			iPlus += iCount * aEqMod.getPlus();
		}
		return iPlus;
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 *@param  aDamage  The base damage
	 *@param  aSize	   The size to adjust for
	 *@return	   The adjusted damage
	 */
	private String adjustDamage(String aDamage, String aSize)
	{
		if (!aDamage.equalsIgnoreCase("special") && !aDamage.equals("-"))
		{
			return Globals.adjustDamage(aDamage, getSize(), aSize);
		}
		return aDamage;
	}

	/**
	 *@param  aSize	The size to adjust for
	 *@return	 The costAdjustedForSize value
	 */
	private BigDecimal getCostAdjustedForSize(String aSize)
	{
		BigDecimal c = getBaseCost();
		//
		// Scale everything to medium before conversion
		//
		SizeAdjustment saSize = SystemCollections.getSizeAdjustmentNamed(aSize);
		SizeAdjustment saBase = SystemCollections.getSizeAdjustmentNamed(getBaseSize());
		if (saSize == null || saBase == null)
		{
			return c;
		}
		final double saDbl = saSize.getBonusTo("ITEMCOST", typeList(), 1.0);
		final double saBaseDbl = saBase.getBonusTo("ITEMCOST", typeList(), 1.0);
		final double mult = saDbl / saBaseDbl;
		c = c.multiply(new BigDecimal(mult));
		//
		// TODO:Non-humanoid races can also double the cost (armor)
		//
		return c;
	}

	/**
	 *  Gets the acModAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize	 The size to adjust for
	 */
	private void adjustACForSize(Equipment baseEq, String aSize)
	{
		if (getBonusList() != null && isArmor())
		{
			double mult = 1.0;
			SizeAdjustment newSA = SystemCollections.getSizeAdjustmentNamed(aSize);
			SizeAdjustment currSA = SystemCollections.getSizeAdjustmentNamed(baseEq.getSize());
			if ((newSA != null) && (currSA != null))
			{
				mult = newSA.getBonusTo("ACVALUE", baseEq.typeList(), 1.0) / currSA.getBonusTo("ACVALUE", baseEq.typeList(), 1.0);
			}
			final List baseEqBonusList = baseEq.getBonusList();
			//
			// Go through the bonus list looking for COMBAT|AC|x and resize bonus
			// Assumption: baseEq.bonusList and this.bonusList only differ in COMBAT|AC|x bonuses
			//
			for (int i = 0; i < baseEqBonusList.size(); ++i)
			{
				BonusObj aBonus = (BonusObj) baseEqBonusList.get(i);
				String aString = aBonus.toString();
				if (aString.startsWith("COMBAT|AC|"))
				{
					final int iOffs = aString.indexOf('|', 10);
					if (iOffs > 10)
					{
						Integer acCombatBonus = new Integer(aString.substring(10, iOffs));
						acCombatBonus = new Integer(new Float(acCombatBonus.doubleValue() * mult).intValue());
						aString = aString.substring(0, 10) + acCombatBonus.toString() + aString.substring(iOffs);
						removeBonusList(aBonus);
						addBonusList(aString, this);
					}
				}
			}

		}
	}

	/**
	 *  Gets the weightAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize	 the size to adjust for
	 *@return	 The weightAdjustedForSize value
	 */
	private Float getWeightAdjustedForSize(String aSize)
	{
		SizeAdjustment newSA = SystemCollections.getSizeAdjustmentNamed(aSize);
		SizeAdjustment currSA = SystemCollections.getSizeAdjustmentNamed(getSize());
		if ((newSA == null) || (currSA == null))
		{
			return new Float(getWeightAsDouble());
		}
		final double mult = newSA.getBonusTo("ITEMWEIGHT", typeList(), 1.0) / currSA.getBonusTo("ITEMWEIGHT", typeList(), 1.0);
		return new Float(getWeightAsDouble() * mult);
	}

	/**
	 *  Gets the damageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize	 The size to adjust for
	 *@return	 The damageAdjustedForSize value
	 */
	private String getDamageAdjustedForSize(String aSize, boolean bPrimary)
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

	private String getDamageAdjustedForSize(String aSize)
	{
		return getDamageAdjustedForSize(aSize, true);
	}

	/**
	 *  Gets the altDamageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize	 The size to adjust for
	 *@return	 The altDamageAdjustedForSize value
	 */
	private String getAltDamageAdjustedForSize(String aSize)
	{
		return getDamageAdjustedForSize(aSize, false);
	}

	/**
	 *  a hacked-down version of the PlayerCharacter.getVariableValue
	 **/
	Float getVariableValue(String aString, String src, String subSrc)
	{
		return getVariableValue(aString, src, subSrc, bonusPrimary);
	}

	/**
	 * hacked-down version of the PlayerCharacter.getVariableValue
	 *
	 * Q: Why does this exist? Cut-n-paste code should be refactored away.
	 * A: Because things like "EQHANDS" and "DMGDICE" don't exist in the
	 *    PlayerCharacter.getVariableValue function
	 *
	 *@param  aString   ???
	 *@param  src	    ???
	 *@param  subSrc    ???
	 *@param  bPrimary  ???
	 *@return	    The variable value
	 */
	public Float getVariableValue(String aString, String src, String subSrc, boolean bPrimary)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf('(') >= 0)
		{
			final int x = Utility.innerMostStringStart(aString);
			final int y = Utility.innerMostStringEnd(aString);
			if (y < x)
			{
				Logging.errorPrint("Missing closing parenthesis: " + aString);
				return total;
			}
			String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(bString, src, subSrc, bPrimary) + aString.substring(y + 1);
		}
		final String delimiter = "+-/*";
		String valString = "";
		int mode = 0;
		//0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0;
		//1,11=min, 2,12=max, 3,13=req, 10 = int
		if (aString.startsWith(".IF."))
		{
			final StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			StringBuffer bString = new StringBuffer();
			Float val1 = null;
			// first value
			Float val2 = null;
			// other value in comparison
			Float valt = null;
			// value if comparison is true
			Float valf;
			// value if comparison is false
			int comp = 0;
			while (aTok.hasMoreTokens())
			{
				String cString = aTok.nextToken();
				if ("GT".equals(cString) || "GTEQ".equals(cString) || "EQ".equals(cString) || "LTEQ".equals(cString) || "LT".equals(cString))
				{
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = new StringBuffer();
					if ("LT".equals(cString))
					{
						comp = 1;
					}
					else if ("LTEQ".equals(cString))
					{
						comp = 2;
					}
					else if ("EQ".equals(cString))
					{
						comp = 3;
					}
					else if ("GT".equals(cString))
					{
						comp = 4;
					}
					else if ("GTEQ".equals(cString))
					{
						comp = 5;
					}
				}
				else if ("THEN".equals(cString))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc, bPrimary);
					// truncat final . character
					aTok.nextToken();
					// discard next . character
					bString = new StringBuffer();
				}
				else if ("ELSE".equals(cString))
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
						if (!Utility.doublesEqual(val1.doubleValue(), val2.doubleValue()))
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
						Logging.errorPrint("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}

				return total;
			}
		}
		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 || delimiter.lastIndexOf(aString.charAt(i)) > -1 || (valString.length() > 3 && (valString.endsWith("MIN") || valString.endsWith("MAX") || valString.endsWith("REQ"))))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}
				if ("SIZE".equals(valString))
				{
					valString = String.valueOf(Globals.sizeInt(getSize()));
				}
				else if ("WT".equals(valString))
				{
					if (calculatingCost && weightAlreadyUsed)
					{
						valString = "0";
					}
					else
					{
						if (calculatingCost && isAmmunition())
						{
							Float unitWeight = new Float(weight / getBaseQty());
							valString = unitWeight.toString();
						}
						else
						{
							valString = String.valueOf(weight);
						}
						weightAlreadyUsed = true;
					}
				}
				else if ("BASECOST".equals(valString))
				{
					valString = getBaseCost().toString();
				}
				else if ("DMGDIE".equals(valString))
				{
					final RollInfo aRollInfo = new RollInfo(getDamage());
					valString = Integer.toString(aRollInfo.sides);
				}
				else if ("DMGDICE".equals(valString))
				{
					final RollInfo aRollInfo = new RollInfo(getDamage());
					valString = Integer.toString(aRollInfo.times);
				}
				else if ("EQACCHECK".equals(valString))
				{
					valString = acCheck.toString();
				}
				else if ("EQHANDS".equals(valString))
				{
					valString = Integer.toString(slots);
				}
				else if ("EQSPELLFAIL".equals(valString))
				{
					valString = spellFailure.toString();
				}
				else if ("RANGE".equals(valString))
				{
					valString = range.toString();
				}
				else if ("CRITMULT".equals(valString))
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
					for (int j = 0; j < Globals.s_ATTRIBSHORT.length; ++j)
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
						//valString = new Integer(getVariableValue(valString.substring(0, valString.length() - 6), "", "", bPrimary).intValue()).toString();
						valString = String.valueOf(getVariableValue(valString.substring(0, valString.length() - 6), "", "", bPrimary).intValue());
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
					// Everything else has failed,
					// try the same function in PC
					//
					final PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC != null)
					{
						return aPC.getVariableValue(aString, src);
					}
					GuiFacade.showMessageDialog(null, "Equipment:Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
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
	 *@param  eqMod	 The equipment modifier
	 *@return	 The visible value
	 */
	public boolean isVisible(final EquipmentModifier eqMod)
	{
		final int vis = eqMod.getVisible();
		if (vis == EquipmentModifier.VISIBLE_QUALIFIED)
		{
			return eqMod.passesPreReqToGain(this);
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
			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
				final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
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
			final List profTypeInfo = Utility.split(profType.getProficiency(), '.');
			if (profTypeInfo.size() == 2)
			{
				final StringBuffer proficiencyName = new StringBuffer(profName);
				if (profName.endsWith(")"))
				{
					proficiencyName.setLength(proficiencyName.length() - 1);
					proficiencyName.append('/');
				}
				else
				{
					proficiencyName.append(" (");
				}
				//
				// Only add if not already there
				//
				if (!proficiencyName.toString().endsWith((String) profTypeInfo.get(1) + '/'))
				{
					proficiencyName.append((String) profTypeInfo.get(1));
				}
				else
				{
					proficiencyName.setLength(proficiencyName.length() - 1);
				}
				proficiencyName.append(')');
				profName = proficiencyName.toString();
				//
				// Strip out the [Hands] variable as, according to the so-called sage,
				// "if someone can wield a weapon 1 handed they can always wield it 2 handed"
				// so we'll just force them to take the 1-handed variety.
				//
				int iOffs = profName.indexOf("[Hands]");
				if (iOffs >= 0)
				{
					profName = profName.substring(0, iOffs) + profName.substring(iOffs + 7);
				}

				WeaponProf wp = Globals.getWeaponProfNamed(profName);
				if (wp == null)
				{
					wp = new WeaponProf();
					try
					{
						WeaponProfLoader.parseLine(wp, profName + "\tTYPE:" + (String) profTypeInfo.get(0) + "\tSIZE:" + getSize(), null, 0);
					}
					catch (PersistenceLayerException exc)
					{
						//XXX Should this really be ignored?
					}
					Globals.addWeaponProf(wp);
				}
			}
		}

		if (getSize().length() == 0)
		{
			setSize("M");
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
			final Equipment eq = Globals.getEquipmentKeyed(baseItem);
			if (eq != null)
			{
				setCost(eq.getCostAdjustedForSize(newSize).toString());
				setWeight(eq.getWeightAdjustedForSize(newSize).toString());
				adjustACForSize(eq, newSize);
				setDamage(eq.getDamageAdjustedForSize(newSize));
				setAltDamage(eq.getAltDamageAdjustedForSize(newSize));

				//
				// Adjust the capacity of the container (if it is one)
				//
				if (containerCapacityString.length() > 0)
				{
					double mult = 1.0;
					SizeAdjustment newSA = SystemCollections.getSizeAdjustmentNamed(newSize);
					if (newSA != null)
					{
						mult = newSA.getBonusTo("ITEMCAPACITY", eq.typeList(), 1.0);
					}
					if (containerWeightCapacity.intValue() != -1)
					{
						containerWeightCapacity = new Float(eq.containerWeightCapacity.doubleValue() * mult);
					}

					if (getAcceptsTypeCount() > 0)
					{
						for (Iterator e = eq.d_acceptsTypes.keySet().iterator(); e.hasNext();)
						{
							final String aString = (String) e.next();
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
			for (int i = 0; i < getPreReqCount(); ++i)
			{
				String aBonus = getPreReq(i);
				if (aBonus.startsWith("PRESIZE"))
				{
					int idx = aBonus.indexOf(':') + 1;
					// must be at least 7 to be valid, so can ignore -1
					if (idx > 0)
					{
						final int iOldPre = Globals.sizeInt(aBonus.substring(idx));
						iNewSize += iOldPre - iOldSize;
						if ((iNewSize >= 0) && (iNewSize <= SystemCollections.getSizeAdjustmentListSize() - 1))
						{
							aBonus = aBonus.substring(0, idx) + SystemCollections.getSizeAdjustmentAtIndex(iNewSize).getAbbreviation();
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
		setOutputName("");
		return getName();
	}

	public String getItemNameFromModifiers()
	{
		final List modList = new ArrayList(eqModifierList);
		final List altModList = new ArrayList(altEqModifierList);
		final List commonList = new ArrayList();

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
				final int idx = modList.indexOf(e.next());
				if (idx >= 0)
				{
					modList.remove(idx);
				}
			}
			for (Iterator e = baseEquipment.getEqModifierList(false).iterator(); e.hasNext();)
			{
				final int idx = altModList.indexOf(e.next());
				if (idx >= 0)
				{
					altModList.remove(idx);
				}
			}
		}

		for (int i = modList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) modList.get(i);
			if (eqMod.getVisible() == EquipmentModifier.VISIBLE_NO)
			{
				modList.remove(i);
				continue;
			}
		}

		extractListFromCommon(commonList, modList);

		removeCommonFromList(altModList, commonList, "eqMod expected but not found: ");

		//
		// Look for a modifier named "masterwork" (assumption: this is marked as "assigntoall")
		//
		String eqMaster = "";
		for (Iterator e = commonList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			if ("MASTERWORK".equalsIgnoreCase(eqMod.getName()))
			{
				eqMaster = eqMod.getName();
				break;
			}
		}

		final String magic1 = getMagicBonus(eqModifierList);
		final String desc1 = getNameFromModifiers(modList, magic1, "");
		String magic2 = "";
		String desc2 = "";
		if (isDouble())
		{
			magic2 = getMagicBonus(altEqModifierList);
			desc2 = getNameFromModifiers(altModList, magic2, "");
		}
		final StringBuffer common = new StringBuffer(getNameFromModifiers(commonList, magic1 + magic2, eqMaster));

		final StringBuffer itemName;
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
		final int iSize = Globals.sizeInt(getSize(), 4);
		if (Globals.sizeInt(getBaseSize(), 4) != iSize)
		{
			if (common.length() != 0)
			{
				common.append('/');
			}
			common.append((SystemCollections.getSizeAdjustmentAtIndex(iSize)).getName());
		}

		//
		// add the modifier description(s)
		//
		if ((desc1.length() == 0) && (desc2.length() == 0))
		{
			itemName.append(String.valueOf(common));
		}
		else if (!isDouble())
		{
			itemName.append(desc1).append('/').append(String.valueOf(common));
		}
		else
		{
			if (common.length() != 0)
			{
				itemName.append(String.valueOf(common)).append(';');
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
		itemName.append(')');

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
	 * Get all the modifiers that apply to the entire item into a separate list
	 * @param commonList The list to extract from
	 * @param extractList The list to extract.
	 */
	private static void extractListFromCommon(final List commonList, final List extractList)
	{
		for (int i = extractList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) extractList.get(i);
			if (!eqMod.getAssignToAll())
			{
				continue;
			}
			commonList.add(0, eqMod);
			extractList.remove(i);
		}
	}

	/**
	 *  Look for a modifier that grants type "magic"
	 *
	 *@param  eqModList  Description of the Parameter
	 *@return	     The magicBonus value
	 */
	private static String getMagicBonus(final List eqModList)
	{
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
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
	 *@return	     The nameFromModifiers value
	 */
	private static String getNameFromModifiers(final List eqModList, final String eqMagic, final String eqMaster)
	{
		//
		// Get a sorted list so that the description will always come
		// out the same reguardless of the order we've added the modifiers
		//
		final List eqList = new ArrayList(eqModList);
		Globals.sortPObjectList(eqList);

		final StringBuffer sMod = new StringBuffer(70);
		if ((eqMagic.length() == 0) && (eqMaster.length() != 0))
		{
			sMod.append(eqMaster);
		}
		for (Iterator e = eqList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
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
	 *  Strip sizes and "Standard" from type string.
	 */
	private void cleanTypes()
	{
		final String aType = super.getType();
		final StringTokenizer aTok = new StringTokenizer(aType, ".");
		final StringBuffer aCleaned = new StringBuffer(aType.length());
		aCleaned.append(".CLEAR");
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			int i;
			for (i = 0; i <= SystemCollections.getSizeAdjustmentListSize() - 1; ++i)
			{
				if (aString.equalsIgnoreCase(((SizeAdjustment) SystemCollections.getSizeAdjustmentAtIndex(i)).getName()))
				{
					break;
				}
			}
			//
			// Ignore size or "Standard" unless previous tag
			// was "ARMOR" and this is "MEDIUM"
			//
			if ("Standard".equalsIgnoreCase(aString))
			{
				continue;
			}
			if (i < SystemCollections.getSizeAdjustmentListSize())
			{
				SizeAdjustment sa = SystemCollections.getSizeAdjustmentAtIndex(i);
				if ((!sa.isDefaultSize()) || !aCleaned.toString().toUpperCase().endsWith("ARMOR"))
				{
					continue;
				}
			}

			//
			// Make sure "Magic" is the first thing in the list
			//
			if ("Magic".equalsIgnoreCase(aString))
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
					aCleaned.append('.');
				}
				aCleaned.append(aString);
			}
		}
		setTypeInfo(aCleaned.toString());
	}

	/**
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return <code>true</code>, if this instance is a container;
	 *	   <code>false</code>, otherwise
	 */
	public boolean isContainer()
	{
		return acceptsChildren();
	}

	/**
	 * @param aType	    Type and sequencer (e.g. Liquid3)
	 * @param aSubTag   SubTag (NAME or SPROP)
	 * @return a String containing the specified subtag
	 */
	public String getContainerByType(String aType, String aSubTag)
	{
		final List contents = new ArrayList(getContents());

		// Separate the Type from the sequencer (Liquid from 3)
		int typeIndex = -1;
		int numCharToRemove = 0;
		for (int i = aType.length() - 1; i > 0; i--)
		{
			if (aType.charAt(i) >= '0' && aType.charAt(i) <= '9')
			{
				if (typeIndex == -1)
				{
					typeIndex = 0;
				}
				typeIndex = Integer.parseInt(aType.substring(i));
				numCharToRemove++;
			}
			else
			{
				i = 0;
			}
		}
		if (numCharToRemove > 0)
		{
			aType = aType.substring(0, aType.length() - numCharToRemove);
		}

		for (Iterator it = contents.iterator(); it.hasNext();)
		{
			if (!((Equipment) it.next()).isType(aType))
			{
				it.remove();
			}
		}
		if (typeIndex < contents.size())
		{
			if ("SPROP".equals(aSubTag))
			{
				return ((Equipment) contents.get(typeIndex)).getRawSpecialProperties();
			}
			else
			{
				return ((Equipment) contents.get(typeIndex)).getName();
			}
		}
		else
		{
			return " ";
		}
	}

	/**
	 * @param index	     integer indicating which object (contained in this object) to return
	 * @return the equipment object contained at this position.
	 */
	public Equipment getContainedByIndex(int index)
	{
		final List contents = new ArrayList(getContents());

		if (contents.size() > 0)
		{
			if (index <= contents.size())
			{
				return ((Equipment) contents.get(index));
			}
		}
		return null;
	}

	/**
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return a list with all Equipment objects this container holds;
	 *	   if this instance is no container, the list will be empty.
	 */
	public Collection getContents()
	{
		final List contents = new ArrayList();

		Equipment aEquip;
		for (int it = 0; it < getContainedEquipmentCount(); ++it)
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
	 * As per p.176 of DMG.
	 * @return
	 */
	private boolean isMagicLimitedType()
	{
		boolean limited = false;
		if (isType("HEADGEAR") || isType("EYEGEAR") || isType("CAPE") || isType("AMULET") || isSuit() || isType("ROBE") || isType("SHIRT") || isType("BRACER") || isType("GLOVE") || isType("RING") || isType("BELT") || isType("BOOT"))
		{
			limited = true;
		}
		return limited;
	}

	private boolean ignoresCostDouble()
	{
		boolean noDouble = false;
		if (isType("MANTLE")		// Mantle of Spell Resistance doesn't double cost
			|| isType("POTION") || isType("SCROLL") || isType("STAFF") || isType("WAND"))
		{
			noDouble = true;
		}
		return noDouble;
	}

	public int getMinCharges()
	{
		for (Iterator e = getEqModifierIterator(true); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
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
		for (Iterator e = getEqModifierIterator(true); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
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
		for (Iterator e = getEqModifierIterator(true); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			if (eqMod.getMinCharges() > 0)
			{
				eqMod.setRemainingCharges(remainingCharges);
			}
		}
	}

	public int getRemainingCharges()
	{
		for (Iterator e = getEqModifierIterator(true); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			if (eqMod.getMinCharges() > 0)
			{
				return eqMod.getRemainingCharges();
			}
		}
		return -1;
	}

	public int getUsedCharges()
	{
		for (Iterator e = getEqModifierIterator(true); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			if (eqMod.getMinCharges() > 0)
			{
				return eqMod.getUsedCharges();
			}
		}
		return -1;
	}

	/**
	 * Tack on the cost of the magical enhancement(s).
	 * @param iPlus
	 * @param altPlus
	 * @return
	 */
	private BigDecimal getCostFromPluses(final int iPlus, final int altPlus)
	{
		if (((iPlus != 0) || (altPlus != 0)) && (JEPResourceChecker.getMissingResourceCount() == 0))
		{
			PJEP myParser = new PJEP();
			myParser.addVariable("PLUS", iPlus);
			myParser.addVariable("ALTPLUS", altPlus);
			myParser.addVariable("BASECOST", getBaseCost().doubleValue());
			if (isAmmunition())
			{
				myParser.addVariable("BASEQTY", getBaseQty());
			}

///////////////////////////////
// Test relative speeds of new and old methods.
// Preliminary testing shows that the new method is approximately 3 to 4 times faster
// Byngl - January 29, 2003
///////////////////////////////
/*			long start;
			long end;
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; ++i)
			{
				myParser.parseExpression("1000*(PLUS+ALTPLUS)*(PLUS+ALTPLUS)*getvar(\"CL=Fighter\",TRUE)");
			}
			end = System.currentTimeMillis();
			System.err.println("time1:" + (end - start));


			PlayerCharacter aPC = Globals.getCurrentPC();
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; ++i)
			{
				aPC.getVariableValue("1000*(1+0)*(1+0)*CL=Fighter", "");
			}
			end = System.currentTimeMillis();
			System.err.println("time2:" + (end - start));
*/
///////////////////////////////

			String typeMatched;
			//
			// Look for an expression for all of this item's types
			// If there is more than 1, use the most expensive.
			//
			String costExpr;
			BigDecimal maxCost = null;
			List itemTypes = typeList();
			for (int idx = 0; idx < itemTypes.size(); ++idx)
			{
				typeMatched = (String) itemTypes.get(idx);
				costExpr = SettingsHandler.getGame().getPlusCalculation(typeMatched);
				if (costExpr != null)
				{
					final BigDecimal thisCost = evaluateCost(myParser, costExpr);
					if ((maxCost == null) || (thisCost.compareTo(maxCost) > 1))
					{
						maxCost = thisCost;
					}
				}
			}
			if (maxCost != null)
			{
				return maxCost;
			}

			//
			// No cost formula found, check for catch-all definition
			//
			typeMatched = "ANY";
			costExpr = SettingsHandler.getGame().getPlusCalculation(typeMatched);
			if (costExpr != null)
			{
				return evaluateCost(myParser, costExpr);
			}
		}
		return BigDecimalHelper.ZERO;
	}

	private BigDecimal evaluateCost(PJEP myParser, final String costExpr)
	{
		myParser.parseExpression(costExpr);
		if (!myParser.hasError())
		{
			final Object result = myParser.getValueAsObject();

			if (result != null)
			{
				return new BigDecimal(result.toString());
			}
		}

		Logging.errorPrint("Bad equipment cost expression: " + costExpr);

		return BigDecimalHelper.ZERO;
	}

	public boolean isAutomatic()
	{
		return automatic;
	}

	public void setAutomatic(boolean arg)
	{
		automatic = arg;
	}
}
