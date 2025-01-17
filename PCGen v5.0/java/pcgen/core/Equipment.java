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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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
 * Last Edited: $Date: 2006/02/21 01:07:42 $
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
import pcgen.core.bonus.BonusObj;
import pcgen.io.FileAccess;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.WeaponProfLoader;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;
import pcgen.util.JEPResourceChecker;
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

	private static TreeSet s_equipmentTypes = new TreeSet();

	private Integer acCheck = new Integer(0);
	//private Integer acMod = new Integer(0);
	private int altCritMult = 0;
	private String altDamage = "";
	private ArrayList altEqModifierList = new ArrayList();
	private ArrayList altTypeList = null;
	/** if is true a BAB of 13 yields 13/8/3, if false, merely 13. */
	private boolean attacksProgress = true;
	private BigDecimal baseCost = new BigDecimal("0");
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
	private BigDecimal cost = new BigDecimal("0");
	private BigDecimal costMod = new BigDecimal("0");
	private String critRange = "";
	private int critMult = 0;
	private EquipmentCollection d_parent = null;
	private ArrayList d_containedEquipment = null;
	private ArrayList d_headerChildren = null;
	private boolean d_acceptsChildren = false;
	private HashMap d_acceptsTypes = null;
	private HashMap d_childTypes = null;
	private String damage = "";
	private Integer acBonus = new Integer(0);
	// effective DR vales for Armor
	private Integer eDR = new Integer(-1);
	private ArrayList eqModifierList = new ArrayList();
	private int slots = 1;
	private boolean hasHeaderParent = false;
	private Equipment headerParent = null;
	private String indexedUnderType = "";
	private boolean isHeaderParent = false;
	private boolean isOnlyNaturalWeapon = false;
	private int location = NOT_CARRIED;
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
	private String size = Globals.getDefaultSizeAdjustment().getAbbreviation();
	private String sizeBase = "";
	private String specialProperties = "";
	private Integer spellFailure = new Integer(0);
	// The equipment's techlevel.
	private String techLevel = "";
	// player added note
	private String noteString = "";

	private ArrayList vFeatList = null;		// virtual feat list
	private double weight = 0.0;
	private boolean weightAlreadyUsed = false;
	private BigDecimal weightMod = new BigDecimal("0");

	private int memberKit = -1;

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

	/*	bsmeister
		* 10/22/2002
		* This initialization block is to make sure that
		* the coinCost object is properly initialized or,
		* if there is no denominationlist, doesn't generate
		* an error.  I didn't see any other initialization
		* blocks, nor a constructor in which to put it.  Not
		* wanting to mess anything up, I didn't create a
		* new constructor.  If creating a constructor is
		* appropriate, please let me know and I will do so.
		*/
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
	 * Returns the weapon's rate of fire.
	 * Defaults to empty string.
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
	 * Returns the equipment's tech level.
	 * Defaults to empty string.
	 * @return The equipment's tech level
	 **/
	public String getTechLevel()
	{
		return techLevel;
	}

	/**
	 * Sets the weapons tech level
	 * @param techLevel
	 */
	public void setTechLevel(String techLevel)
	{
		this.techLevel = techLevel;
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
	 * Sets the isHeaderParent attribute if the item is the first
	 * of many that that need to be differentiated (i.e. containers)
	 * aHeaderParent has at least 2 of the same item listed with a -#-
	 * after the item name. i.e. when you have 2 backpacks, there are
	 * three Backpack lines...one that says just "Backpack" and one
	 * that says "Backpack -1-" and another "Backpack -2-".
	 *
	 *@param  aHeaderParent The state of this item as a header item.
	 */
	public void setIsHeaderParent(boolean aHeaderParent)
	{
		isHeaderParent = aHeaderParent;
	}

	/**
	 * Returns true if the item is a header parent (the first of
	 * many containers).
	 *
	 *@return The state of the condition
	 */
	public boolean isHeaderParent()
	{
		return isHeaderParent;
	}

	/**
	 * Returns the state of whether this item is one of the many
	 * that are in a list of like items. i.e. "Backpack -1-" would
	 * return true if you had 2 or more of them.
	 *
	 *@return The state of the condition
	 */
	public boolean getHasHeaderParent()
	{
		return hasHeaderParent;
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
	 * ???
	 *
	 */
	private void addContainedEquipmentTo(Collection c)
	{
		if (getContainedEquipmentCount() == 0)
		{
			return;
		}
		c.addAll(d_containedEquipment);
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
	 *@return             the name of this slot
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
	 *@return           the number of a location
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
			//GuiFacade.showMessageDialog(null, "Unable to interpret hand setting: " + handDesc, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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

	/**
	 *  returns pointer to Equipment of first headerChild
	 *
	 *@return    pointer to Equipment of first headerChild
	 */
	public Equipment createHeaderParent()
	{
		//returns pointer to Equipment of first headerChild

		final Equipment anEquip = (Equipment) clone();
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
		anEquip.qty = 1.0;

		//move the pointers from any children to the new parent
		for (int e = 0; e < getContainedEquipmentCount(); ++e)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = getContainedEquipment(e);
			aChild.setParent(anEquip);
		}

		clearContainedEquipment();
		//clear the last junk

		setIsEquipped(false, location);

		return anEquip;
	}

	/**
	 *  ???
	 */
	public void collapseHeaderParent()
	{
		final Equipment lastHchild = getHeaderChild(0);
		hasHeaderParent = false;
		isHeaderParent = false;
		qty = 1.0;
		clearChildTypes();
		if (lastHchild.getChildTypeCount() > 0)
		{
			d_childTypes = new HashMap(lastHchild.d_childTypes);
		}
		clearContainedEquipment();
		if (lastHchild.getContainedEquipmentCount() > 0)
		{
			d_containedEquipment = new ArrayList(lastHchild.getContainedEquipmentCount());
			lastHchild.addContainedEquipmentTo(d_containedEquipment);
		}
		setParent(lastHchild.getParent());

		for (int e = 0; e < getContainedEquipmentCount(); ++e)
		{
			//funky hack, may have problems with container inside a container.

			Equipment aChild = getContainedEquipment(e);
			aChild.setParent(this);
		}

		clearHeaderChildren();

		setCarried(lastHchild.getCarried());
		setIsEquipped(lastHchild.isEquipped(), lastHchild.getLocation());
		setLocation(lastHchild.getLocation());
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 */
	private void setHeaderParent(Object anObject)
	{
		headerParent = (Equipment) anObject;
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 */
	private boolean addHeaderChild(Equipment anObject)
	{
		if (d_headerChildren == null)
		{
			d_headerChildren = new ArrayList();
		}
		return d_headerChildren.add(anObject);
	}

	/**
	 *  ???
	 *
	 *@param  anObject  ???
	 *@return           ???
	 */
	private int indexOfHeaderChild(Equipment anObject)
	{
		if (d_headerChildren == null)
		{
			return -1;
		}
		return d_headerChildren.indexOf(anObject);
	}

	/**
	 *  ???
	 *
	 *@return    ???
	 */
	public int getHeaderChildCount()
	{
		if (d_headerChildren == null)
		{
			return 0;
		}
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
		return (Equipment) d_headerChildren.get(i);
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
	 *  Removes a header child.
	 *
	 *@param  anObject  the header child to remove
	 */
	public boolean removeHeaderChild(Object anObject)
	{
		if (d_headerChildren == null)
		{
			return false;
		}
		if (d_headerChildren.remove(anObject))
		{
			//
			// re-key
			for (Iterator e = d_headerChildren.iterator(); e.hasNext();)
			{
				final Equipment anEquip = (Equipment) e.next();
				anEquip.setKeyName(anEquip.toString());
			}
			if (d_headerChildren.size() == 0)
			{
				clearHeaderChildren();
			}
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
		final StringBuffer s = new StringBuffer(getBonusListString());
		String t = getSpecialProperties();

		if (t == null)
		{
			t = "";
		}

		for (Iterator e = eqModifierList.iterator(); e.hasNext();)
		{
			EquipmentModifier eqMod = (EquipmentModifier) e.next();
			for (Iterator modIterator = eqMod.getBonusList().iterator(); modIterator.hasNext();)
			{
				final String eqModBonus = (String) modIterator.next();
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
		final ArrayList list1 = new ArrayList(eqModifierList);
		final ArrayList list2 = new ArrayList(altEqModifierList);
		final ArrayList comn = new ArrayList();

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
	private void removeCommonFromList(final ArrayList altList, final ArrayList commonList, String errMsg)
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
				Globals.debugPrint(errMsg, eqMod.getName());
			}
		}
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
		return toString(true);
	}

	public String toString(boolean addCharges)
	{
		final StringBuffer aString = new StringBuffer(name);
		if (hasHeaderParent)
		{
			aString.append(" -").append(headerParent.indexOfHeaderChild(this) + 1).append("-");
		}

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
	 *@param  child  The child to remove
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
		Equipment anEquip = getContainedEquipment(childIndex);
		final Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", new Float(getChildType("Total").floatValue() - qtyRemoved.floatValue()));
		final String aString = anEquip.isIndexedUnderType();
		setChildType(aString, new Float(getChildType(aString).floatValue() - qtyRemoved.floatValue()));
		anEquip.setParent(null);
		removeContainedEquipment(childIndex);
		updateContainerContentsString();
		anEquip = this;
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment) anEquip.getParent();
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
	 *
	 *@param  child  The child to add
	 */
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
	 *@return          true if has child type
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
	 *@return          The acceptsTypes value
	 */
	private Float getChildType(String aString)
	{
		if (d_childTypes == null)
		{
			return null;
		}
		return (Float) d_childTypes.get(aString);
	}

	/**
	 *  Gets the number of child types
	 *
	 *@return  The number of distinct types
	 */
	private int getChildTypeCount()
	{
		if (d_childTypes == null)
		{
			return 0;
		}
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
		{
			d_childTypes = new HashMap();
		}
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
	 *@return          The acceptsTypes value
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
	 *@return            Description of the Return Value
	 */
	private String pickChildType(TreeSet aTypeList, Float aQuant)
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

				if (Globals.isDebugMode())
				{
					Globals.debugPrint("Testing ", aString);
				}

				if (acceptsType(aString))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("Accepts ", aString);
					}

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

		return !("".equals(pickChildType(aTypeList, aQuant)));
	}

	/**
	 *  Description of the Method
	 *
	 *@param  aFloat  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	private boolean checkChildWeight(Float aFloat)
	{
		return ((aFloat.floatValue() + getContainedWeight().floatValue()) <= containerWeightCapacity.floatValue() || containerWeightCapacity.intValue() == -1);
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
		BigDecimal c = new BigDecimal("0");

		//
		// Do pre-sizing cost increment.
		// eg. in the case of adamantine armor, want to add the cost of the metal before the
		// armor gets resized.
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
		final double mult = Globals.getSizeAdjustmentNamed(getSize()).getBonusTo("ITEMCOST", typeList(), 1.0) / Globals.getSizeAdjustmentNamed(getBaseSize()).getBonusTo("ITEMCOST", typeList(), 1.0);
		c = c.multiply(new BigDecimal(mult));

		BigDecimal itemCost = cost.add(c);

		final ArrayList modifierCosts = new ArrayList();

		BigDecimal nonDoubleCost = new BigDecimal("0");

		c = new BigDecimal("0");
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
				eqModCost = new BigDecimal("0");
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
			if (Globals.isDebugMode())
			{
				Globals.debugPrint("" + modifierCosts);
			}
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
	 */
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
	 *  Gets the acBonus attribute of the Equipment object
	 *  NOTE: Assumes that the maximum possible acBonus is 100.
	 *
	 *@return    The acBonus value
	 */
	public Integer getACBonus()
	{
		int dbon = acBonus.intValue() + (int) bonusTo("EQMARMOR", "ACBONUS", true);
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
	 *  Sets the acBonus attribute of the Equipment object
	 *
	 *@param  aString  The new acBonus value
	 */
//	public void setACBonus(String aString)
//	{
//		try
//		{
//			acBonus = Delta.decode(aString);
//		}
//		catch (NumberFormatException ignore)
//		{
//			// ignore
//		}
//	}

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
			if (bString != null && bString.charAt(0) == '*')
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
		String itemType;
		Float itemNumber;
		while (aTok.hasMoreTokens())
		{
			final StringTokenizer typeTok = new StringTokenizer(aTok.nextToken(), "=", false);
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
	 *  Description of the Method
	 */
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
	 *  Description of the Method
	 */
	public void updateContainerContentsString()
	{
		containerContentsString = "";
		final StringBuffer tempStringBuffer = new StringBuffer(getChildCount() * 20);
		// Make sure there's no bug here.
		if (acceptsChildren())
		{
			tempStringBuffer.append(getContainedWeight(true)).append(' ').append(Globals.getWeightDisplay());
			/*
			 *  Modified by Emily Smirle (Syndaryl)
			 */
		}

		for (int e = 0; e < getChildCount(); ++e)
		{
			final Equipment anEquip = (Equipment) getChild(e);
			if (anEquip.getCarried().compareTo(new Float(0)) > 0)
			{
				tempStringBuffer.append(", ").append(anEquip.numberCarried()).append(' ').append(anEquip);
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
			if (!Globals.isStarWarsMode())
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
		if (f == 0.0)
		{
			f = 1.0;
		}
		double aWeight = weight * f;

		f = bonusTo("EQM", "WEIGHTDIV", true);
		if (f == 0)
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
	 *@return          Description of the Return Value
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
	 *  Sets the typeString attribute of the Equipment object
	 *
	 *@param  aString  The new typeString value
	 */
//	public void setTypeString(String aString)
//	{
//		setType(aString);
//
//		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase(), ".", false);
//		while (aTok.hasMoreTokens())
//		{
//			final String type = aTok.nextToken();
//			s_equipmentTypes.add(type);
//		}
//		if (isWeapon())
//		{
//			if (getCritRange().length() == 0)
//			{
//				setCritRange("1");
//			}
//			if (getCritMult().length() == 0)
//			{
//				setCritMult("x2");
//			}
//		}
//	}

	/**
	 *  Sets the altTypeList attribute of the Equipment object
	 *
	 *@param  argAltTypeList  The new altTypeList value
	 */
	public void setAltTypeList(String argAltTypeList)
	{
		String altTypeList = argAltTypeList.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(altTypeList, ".", false);
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
		else if (iMod >= Globals.getSizeAdjustmentList().size() - 1)
		{
			iMod = Globals.getSizeAdjustmentList().size() - 1;
		}
		return adjustDamage(baseDamage, ((SizeAdjustment) Globals.getSizeAdjustmentList().get(iMod)).getAbbreviation());
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
		return (int) bonusTo("WEAPON", "TOHIT", bPrimary);
	}

	/**
	 *  Gets the bonusToDamage attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The bonusToDamage value
	 */
	public int getBonusToDamage(boolean bPrimary)
	{
		return (int) bonusTo("WEAPON", "DAMAGE", bPrimary);
	}

	/**
	 *  Gets the critRangeDouble attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The critRangeDouble value
	 */
	public int getCritRangeDouble(boolean bPrimary)
	{
		return (int) bonusTo("EQMWEAPON", "CRITRANGEDOUBLE", bPrimary);
	}

	/**
	 *  Gets the critRangeAdd attribute of the Equipment object
	 *
	 *@param  bPrimary  Description of the Parameter
	 *@return           The critRangeAdd value
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
	 *@param  mult  Description of the Parameter
	 *@return       Description of the Return Value
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
	 *@return          Description of the Return Value
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
				r += (int) aPC.getTotalBonusTo("RANGEADD", "THROWN", true);
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "THROWN", true);
				rangeMult += (int) aPC.getTotalBonusTo("RANGEMULT", "THROWN", true) / 100.0;
			}
			else if (isProjectile())
			{
				r += (int) aPC.getTotalBonusTo("RANGEADD", "PROJECTILE", true);
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "PROJECTILE", true);
				rangeMult += (int) aPC.getTotalBonusTo("RANGEMULT", "PROJECTILE", true) / 100.0;
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
		catch (NumberFormatException ignore)
		{
			// ignore
		}
	}

	public ArrayList getRangeList()
	{
		return getRangeList(false);
	}

	/**
	 *  Gets the range list of the Equipment object, adding the 30' range, if not present and required
	 *
	 *  @param addShortRange  boolean
	 *  @return    The range list
	 */

	public ArrayList getRangeList(boolean addShortRange)
	{
		ArrayList aList = new ArrayList();
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
	 *  Returns the type list as an ArrayList
	 *
	 *@return    the type list as an ArrayList
	 */
	public ArrayList typeArrayList()
	{
		return typeList();
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
	 *
	 *@return    Description of the Return Value
	 */
	private TreeSet eqTypeList()
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
		if (!isHeaderParent)
		{
			return qty;
		}
		double aQty = 0;
		for (int i = 0; i < getHeaderChildCount(); ++i)
		{
			aQty += getHeaderChild(i).qty();
		}

		return aQty;
	}

	/**
	 *  Get the quantity of items
	 *
	 *@return    return a Float of the quantity
	 **/
	public Float getQty()
	{
		if (!isHeaderParent)
		{
			return new Float(qty);
		}
		double aQty = 0;
		for (int i = 0; i < getHeaderChildCount(); ++i)
		{
			aQty += getHeaderChild(i).qty();
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
		if (!isHeaderParent)
		{
			qty = argQty;
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
		if (!isHeaderParent)
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

		float aQty = 0;
		for (int i = 0; i < getHeaderChildCount(); ++i)
		{
			final Equipment eq = getHeaderChild(i);
			Equipment eqParent = (Equipment) eq.getParent();
			if (eq.isEquipped() || (eqParent == null))
			{
				aQty += eq.numberCarried().floatValue();
			}
			else
			{
				for (; eqParent != null; eqParent = (Equipment) eqParent.getParent())
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
		return ((location >= EQUIPPED_NEITHER) && (location <= EQUIPPED_TEMPBONUS));
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
		return getName().compareTo(e.getName());
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
	 *@return		ArrayList of virtual feats this item bestows upon its weilder.
	 */
	ArrayList getVFeatList()
	{
		ArrayList vFeats = null;
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
	private ArrayList addEqModList(boolean bPrimary, ArrayList argVFeats)
	{
		ArrayList eqModList = getEqModifierList(bPrimary);
		ArrayList vFeats = argVFeats;
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
		final ArrayList vFeats = getVFeatList();
		return (vFeats != null) && (vFeats.size() > 0);
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder.
	 * @param vList a | delimited list of feats to add to the list

	 */
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
	 *@param  hand   The new isEquipped value
	 */
	public void setIsEquipped(boolean aFlag, int hand)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aFlag != isEquipped())
		{
			aPC.setVirtualFeatsStable(false);
			aPC.setAggregateFeatsStable(false);
		}

		if (aFlag)
		{
			if (hand == EQUIPPED_TEMPBONUS)
			{
				location = EQUIPPED_TEMPBONUS;
			}
			if ((hand < 0) || (hand > EQUIPPED_TWO_HANDS))
			{
				hand = EQUIPPED_NEITHER;
			}

			//
			// See DMG Limits on Magic Items Worn p.176
			//
			if ((isType("HEADGEAR")) && aPC.canEquip("HEADGEAR"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("EYEGEAR")) && aPC.canEquip("EYEGEAR"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("CAPE")) && aPC.canEquip("CAPE"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("AMULET")) && aPC.canEquip("AMULET"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isSuit()) && aPC.canEquip("SUIT"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("ROBE")) && aPC.canEquip("ROBE"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("SHIRT")) && aPC.canEquip("SHIRT"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("BRACER")) && aPC.canEquip("BRACER"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("GLOVE")) && aPC.canEquip("GLOVE"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("RING")) && aPC.canEquip("RING"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("BELT")) && aPC.canEquip("BELT"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isType("BOOT")) && aPC.canEquip("BOOT"))
			{
				location = EQUIPPED_NEITHER;
			}
			else if ((isWeapon() && aPC.canEquip("Weapon")) || (isShield() && aPC.canEquip("Shield")))
			{
				final int iRaceHands = aPC.getRace().getHands() + (int) aPC.getTotalBonusTo("SLOTS", "HANDS", true);
				// make sure this item doesn't count in hands-count yet
				// so save it's location
				int oldLocation = location;
				// make sure it isn't equipped
				location = NOT_CARRIED;
				// count hands (based on equipped shields and weapons)
				int handsInUse = aPC.handsFull();
				// restore location
				location = oldLocation;
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
						GuiFacade.showMessageDialog(null, "Cannot equip weapon - no valid weapon proficiency for " + getName() + " loaded.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						Globals.debugPrint("Globals: " + Globals.getWeaponProfNames(",", true) + Constants.s_LINE_SEP + "Proficiency name: " + aWProf + " " + this);
						return;
					}

					if (location == EQUIPPED_TEMPBONUS)
					{
						// no extra hands in use
						hand = EQUIPPED_TEMPBONUS;
					}
					else if (Globals.isWeaponOneHanded(aPC, this, wp))
					{
						++handsInUse;
						if ((hand == EQUIPPED_BOTH) || (hand == EQUIPPED_TWO_HANDS))
						{
							++handsInUse;
						}
					}
					else if (Globals.isWeaponTwoHanded(aPC, this, wp))
					{
						handsInUse += 2;
					}

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
					GuiFacade.showMessageDialog(null, "Your hands are too full. Check shields/weapons already equipped.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				location = hand;
			}
			else if (location == EQUIPPED_TEMPBONUS)
			{
				hand = EQUIPPED_TEMPBONUS;
			}
			else
			{
				location = hand;
			}
			if (!isEquipped())
			{
				GuiFacade.showMessageDialog(null, "Character cannot equip any more of this item type.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else // de-equipping item.  Assume it is to still be carried.
		{
			if (hand == NOT_CARRIED)
			{
				location = NOT_CARRIED;
			}
			else
			{
				location = CARRIED_NEITHER;
			}
		}
	}

	/**
	 *  Gets the modifiersAllowed attribute of the Equipment object
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
			weightMod = new BigDecimal(aString);
		}
		catch (NumberFormatException e)
		{
			weightMod = new BigDecimal("0");
		}
	}

	public void setWeightMod(BigDecimal aWeightMod)
	{
		weightMod = aWeightMod;
	}

	public BigDecimal getWeightMod()
	{
		return weightMod;
	}

	private ArrayList cloneEqModList(boolean primary)
	{
		final ArrayList clonedList = new ArrayList();
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
			eq.location = location;
			eq.altCritMult = altCritMult;
			eq.slots = slots;
			eq.bonusType = bonusType;
			eq.numberEquipped = numberEquipped;
			eq.reach = reach;
			eq.qty = qty;
			eq.outputIndex = outputIndex;
			eq.containerWeightCapacity = containerWeightCapacity;
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

			//header crap
			eq.hasHeaderParent = hasHeaderParent;
			eq.headerParent = headerParent;
			if (d_headerChildren != null)
			{
				eq.d_headerChildren = new ArrayList(d_headerChildren);
			}
		}
		catch (CloneNotSupportedException e)
		{
			GuiFacade.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return eq;
		}
	}

	/**
	 *  Gets the hand attribute of the Equipment object
	 *	@return  int containing the location value
	 */
	public final int getLocation()
	{
		if (location == EQUIPPED_TEMPBONUS)
		{
			location = EQUIPPED_TEMPBONUS;
		}
		else if ((location > CONTAINED) || (location < EQUIPPED_NEITHER))
		{
			location = NOT_CARRIED;
		}
		return location;
	}

	/**
	 *  Sets the location attribute of the Equipment object
	 *  @param newLocation int containing the new location value
	 */
	public final void setLocation(int newLocation)
	{
		if ((newLocation < EQUIPPED_NEITHER) || (newLocation > NOT_CARRIED))
		{
			GuiFacade.showMessageDialog(null, "Location " + newLocation + " unknown.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (isWeapon())
		{
			String wpName = profName(newLocation);
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

			if ("Natural".equals(wp.getType()))
			{
				//Don't worry if it can be placed in those hands if it is a natural weapon.

				if (modifiedName().endsWith("Primary") && !isOnlyNaturalWeapon && newLocation != EQUIPPED_PRIMARY && newLocation != EQUIPPED_NEITHER)
				{
					GuiFacade.showMessageDialog(null, "Can only place primary Natural weapon in Primary \"Hand\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Secondary") && newLocation != EQUIPPED_SECONDARY && newLocation != EQUIPPED_NEITHER)
				{
					GuiFacade.showMessageDialog(null, "Can only place secondary Natural weapon in Off \"Hand\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (modifiedName().endsWith("Primary") && isOnlyNaturalWeapon && newLocation != EQUIPPED_BOTH && newLocation != EQUIPPED_NEITHER)
				{
					GuiFacade.showMessageDialog(null, "Can only place sole Natural weapon in both \"hands\".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			else
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				if ((newLocation == EQUIPPED_BOTH) && Globals.isWeaponLightForPC(aPC, this) && !Globals.isWeaponTwoHanded(aPC, this, wp))
				{
					GuiFacade.showMessageDialog(null, "Cannot place light weapon in both hands.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Make sure a two-handed weapon is not
				// equipped to a single hand
				if (((newLocation == EQUIPPED_PRIMARY) || (newLocation == EQUIPPED_SECONDARY) || (newLocation == EQUIPPED_TWO_HANDS)) && Globals.isWeaponTwoHanded(aPC, this, wp) && (!isDouble()))
				{
					GuiFacade.showMessageDialog(null, "Two handed weapon must be in Neither or Both hands.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}

		if (newLocation == EQUIPPED_TEMPBONUS)
		{
			setIsEquipped(true, newLocation);
		}
		else if ((newLocation >= EQUIPPED_NEITHER) && (newLocation <= EQUIPPED_TWO_HANDS))
		{
			setIsEquipped(true, newLocation);
		}
		else
		{
			setIsEquipped(false, newLocation);
		}
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
		if (!costMod.equals(new BigDecimal("0")))
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
	 *@param  aType     a TYPE of BONUS (such as "COMBAT" or "AC")
	 *@param  aName     the NAME of the BONUS (such as "ATTACKS" or "ARMOR")
	 *@param  bPrimary  should we ask the parent object also?
	 *@return           returns a double which is the sum of all bonuses
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
			ArrayList tbList = new ArrayList();
			for (Iterator b = getTempBonusList().iterator(); b.hasNext();)
			{
				BonusObj aBonus = (BonusObj) b.next();
				String bonusString = aBonus.toString();
				if (!tbList.contains(bonusString))
				{
					tbList.add(bonusString);
				}
			}
			super.bonusTo(aType, aName, anObj, tbList);
		}

		final ArrayList eqModList = getEqModifierList(bPrimary);
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
	 *@return           Description of the Return Value
	 */
	private boolean willIgnore(String eqModKey, boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
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
	 *@return           The eqModifierKeyed value
	 */
	public EquipmentModifier getEqModifierKeyed(String eqModKey, boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
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

		final ArrayList eqModList = getEqModifierList(bPrimary);
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
	public void addEqModifier(String aString, boolean bPrimary)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
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
				Globals.errorPrint("Could not find EquipmentModifier: " + eqModKey);
				return;
			}
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0)
			{
				eqMod = (EquipmentModifier) eqMod.clone();
			}

			final ArrayList eqModList = getEqModifierList(bPrimary);
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
		final ArrayList eqModList = getEqModifierList(bPrimary);
		for (; ;)
		{
			boolean bRemoved = false;
			for (int i = eqModList.size() - 1; i >= 0; --i)
			{
				final EquipmentModifier eqMod = (EquipmentModifier) eqModList.get(i);
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
		final ArrayList eqModList = getEqModifierList(bPrimary);
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
		if (!bPrimary && (eqMod.getCost().indexOf("%CHOICE") >= 0))
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

		if (aType.startsWith("EQMODTYPE="))
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
		final ArrayList eqModList = getEqModifierList(bPrimary);
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
		// Get a response from user (if one required). Remove the modifier if all associated choices
		// are deleted
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
	 *@return           An iterator through EquipmentMod objects
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
	private String getEqModifierString(boolean bPrimary)
	{
		final ArrayList eqModList = getEqModifierList(bPrimary);
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

		if (bPrimary && (weightMod.compareTo(new BigDecimal("0")) != 0))
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
		final ArrayList typeList;
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

		final ArrayList modTypeList = new ArrayList();

		//
		// Add in all type modfiers from "ADDTYPE" modifier
		//
		EquipmentModifier aEqMod = getEqModifierKeyed("ADDTYPE", bPrimary);
		if (aEqMod != null)
		{
			for (int e = 0; e < aEqMod.getAssociatedCount(); ++e)
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

				final ArrayList eqModTypeList = aEqMod.getItemType();
				for (Iterator e2 = eqModTypeList.iterator(); e2.hasNext();)
				{
					final String aType = (String) e2.next();
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
	String getType(boolean bPrimary)
	{
		final ArrayList typeList = typeList(bPrimary);
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

		return (String) tList.get(index);
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
	 *@param  eqModList  Description of the Parameter
	 *@return            The specialAbilityList value
	 */
	private static ArrayList getSpecialAbilityList(final ArrayList eqModList)
	{
		final ArrayList saList = new ArrayList();
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
	 *@param  specialAbilityList  Description of the Parameter
	 *@param  bPrimary            Description of the Parameter
	 *@return                     The specialAbilityTimesList value
	 */
	private ArrayList getSpecialAbilityTimesList(ArrayList specialAbilityList, boolean bPrimary)
	{
		final ArrayList aList = new ArrayList();
		final int[] times = new int[specialAbilityList.size()];
		for (int x = 0; x < times.length; ++x)
		{
			times[x] = 0;
		}

		//
		// First, get a list of the unique abilities, counting the duplicates
		//
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
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
	 *@param  aSize    The size to adjust for
	 *@return          The adjusted damage
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
	 *@return        The costAdjustedForSize value
	 */
	private BigDecimal getCostAdjustedForSize(String aSize)
	{
		BigDecimal c = getBaseCost();
		//
		// Scale everything to medium before conversion
		//
		SizeAdjustment saSize = Globals.getSizeAdjustmentNamed(aSize);
		SizeAdjustment saBase = Globals.getSizeAdjustmentNamed(getBaseSize());
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
	 *@param  aSize  The size to adjust for
	 */
	private void adjustACForSize(Equipment baseEq, String aSize)
	{
		if (getBonusList() != null && isArmor())
		{
			final double mult = Globals.getSizeAdjustmentNamed(aSize).getBonusTo("ACVALUE", baseEq.typeList(), 1.0) / Globals.getSizeAdjustmentNamed(baseEq.getSize()).getBonusTo("ACVALUE", baseEq.typeList(), 1.0);
			final ArrayList baseEqBonusList = baseEq.getBonusList();
			//acMod = new Integer(new Float(acMod.doubleValue() * mult).intValue());

			//
			// Go through the bonus list looking for COMBAT|AC|x and resize bonus
			// Assumption: baseEq.bonusList and this.bonusList only differ in COMBAT|AC|x bonuses
			//
			for (int i = 0; i < baseEqBonusList.size(); ++i)
			{
				String aString = (String) baseEqBonusList.get(i);
				if (aString.startsWith("COMBAT|AC|"))
				{
					final int iOffs = aString.indexOf('|', 10);
					if (iOffs > 10)
					{
						Integer acCombatBonus = new Integer(aString.substring(10, iOffs));
						acCombatBonus = new Integer(new Float(acCombatBonus.doubleValue() * mult).intValue());
						aString = aString.substring(0, 10) + acCombatBonus.toString() + aString.substring(iOffs);
						getBonusList().set(i, aString);
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
	private Float getWeightAdjustedForSize(String aSize)
	{
		final double mult = Globals.getSizeAdjustmentNamed(aSize).getBonusTo("ITEMWEIGHT", typeList(), 1.0) / Globals.getSizeAdjustmentNamed(getSize()).getBonusTo("ITEMWEIGHT", typeList(), 1.0);
		return new Float(getWeightAsDouble() * mult);
	}

	/**
	 *  Gets the damageAdjustedForSize attribute of the Equipment object
	 *
	 *@param  aSize  The size to adjust for
	 *@return        The damageAdjustedForSize value
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
	 *@param  aSize  The size to adjust for
	 *@return        The altDamageAdjustedForSize value
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
	 *@param  src       ???
	 *@param  subSrc    ???
	 *@param  bPrimary  ???
	 *@return           The variable value
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
				Globals.errorPrint("Missing closing parenthesis: " + aString);
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
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
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
				else if ("EQHANDS".equals(valString))
				{
					valString = Integer.toString(slots);
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
		final int vis = eqMod.getVisible();
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
			final ArrayList profTypeInfo = Utility.split(profType.getProficiency(), '.');
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
				setAltDamage(eq.getAltDamageAdjustedForSize(newSize).toString());

				//
				// Adjust the capacity of the container (if it is one)
				//
				if (containerCapacityString.length() > 0)
				{
					final double mult = Globals.getSizeAdjustmentNamed(newSize).getBonusTo("ITEMCAPACITY", eq.typeList(), 1.0);
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
						if ((iNewSize >= 0) && (iNewSize <= Globals.getSizeAdjustmentList().size() - 1))
						{
							aBonus = aBonus.substring(0, idx) + ((SizeAdjustment) Globals.getSizeAdjustmentList().get(iNewSize)).getAbbreviation();
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
		final ArrayList modList = new ArrayList(eqModifierList);
		final ArrayList altModList = new ArrayList(altEqModifierList);
		final ArrayList commonList = new ArrayList();

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
			common.append((Globals.getSizeAdjustmentList().get(iSize)).toString());
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
	private static void extractListFromCommon(final ArrayList commonList, final ArrayList extractList)
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
	 *@return            The magicBonus value
	 */
	private static final String getMagicBonus(final ArrayList eqModList)
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
	 *@return            The nameFromModifiers value
	 */
	private static String getNameFromModifiers(final ArrayList eqModList, final String eqMagic, final String eqMaster)
	{
		//
		// Get a sorted list so that the description will always come
		// out the same reguardless of the order we've added the modifiers
		//
		final ArrayList eqList = new ArrayList(eqModList);
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
	 *  Strip sizes and "Standard" from type string
	 */
	private void cleanTypes()
	{
		final String aType = super.getType();
		final StringTokenizer aTok = new StringTokenizer(aType, ".", false);
		final StringBuffer aCleaned = new StringBuffer(aType.length());
		aCleaned.append(".CLEAR");
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			int i;
			for (i = 0; i <= Globals.getSizeAdjustmentList().size() - 1; ++i)
			{
				if (aString.equalsIgnoreCase(((SizeAdjustment) Globals.getSizeAdjustmentList().get(i)).getName()))
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
			if (i < Globals.getSizeAdjustmentList().size())
			{
				SizeAdjustment sa = (SizeAdjustment) Globals.getSizeAdjustmentList().get(i);
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
	 *         <code>false</code>, otherwise
	 */
	public boolean isContainer()
	{
		return acceptsChildren();
	}

	/**
	 * @param aType     Type and sequencer (e.g. Liquid3)
	 * @param aSubTag   SubTag (NAME or SPROP)
	 * @return a String containing the specified subtag
	 */
	public String getContainerByType(String aType, String aSubTag)
	{
		final ArrayList contents = new ArrayList(getContents());

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
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return a list with all Equipment objects this container holds;
	 *         if this instance is no container, the list will be empty.
	 */
	public Collection getContents()
	{
		final ArrayList contents = new ArrayList();

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
	 * Convenience method.
	 *
	 * <br>author: Thomas Behr 27-03-02
	 *
	 * @return description of the location where this object is carried/equipped,
	 *         i.e. which hand, or the name of the container, or none.
	 */
	public String getLocationString()
	{
		if (getCarried().floatValue() > 0.0f)
		{
			if (location >= 0)
			{
				return locationStringList[location];
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
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
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
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
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
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
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
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
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
		for (Iterator e = getEqModifierList(true).iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
			if (eqMod.getMinCharges() > 0)
			{
				return eqMod.getUsedCharges();
			}
		}
		return -1;
	}

	//
	// Tack on the cost of the magical enhancement(s)
	//
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

			String typeMatched = "";
			//
			// Look for an expression for all of this item's types
			// If there is more than 1, use the most expensive.
			//
			String costExpr = null;
			BigDecimal maxCost = null;
			ArrayList itemTypes = typeList();
			for (int idx = 0; idx < itemTypes.size(); ++idx)
			{
				typeMatched = (String) itemTypes.get(idx);
				costExpr = SettingsHandler.getGame().getPlusCalculation(typeMatched);
				if (costExpr != null)
				{
					final BigDecimal thisCost = evaluateCost(myParser, costExpr, typeMatched);
					if ((maxCost == null) || (thisCost.compareTo(maxCost) > 1))
					{
						if (Globals.isDebugMode())
						{
							Globals.debugPrint("Setting max cost=" + thisCost.toString());
						}
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
				return evaluateCost(myParser, costExpr, typeMatched);
			}
		}
		return new BigDecimal("0");
	}

	private BigDecimal evaluateCost(pcgen.util.PJEP myParser, final String costExpr, final String typeMatched)
	{
		if (Globals.isDebugMode())
		{
			Globals.debugPrint(typeMatched + ":" + costExpr);
		}
		myParser.parseExpression(costExpr);
		if (!myParser.hasError())
		{
			final Object result = myParser.getValueAsObject();
			if (Globals.isDebugMode())
			{
				final org.nfunk.jep.SymbolTable st = myParser.getSymbolTable();
				Globals.debugPrint("symbol table:" + st.toString());
				Globals.debugPrint("result=" + result.toString());
			}
			if (result != null)
			{
				return new BigDecimal(result.toString());
			}
		}
		return new BigDecimal("0");
	}

}
