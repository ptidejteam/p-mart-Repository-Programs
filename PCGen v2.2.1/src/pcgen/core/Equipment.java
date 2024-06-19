/*
 * Equipment.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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

public class Equipment extends PObject
	implements Serializable, EquipmentCollection, Comparable
{
	/**
	 * Maintain a list of all equipment types.
	 */

	public static final int NEITHER_HAND = 0;
	public static final int PRIMARY_HAND = 1;
	public static final int SECONDARY_HAND = 2;
	public static final int BOTH_HANDS = 3;
	public static final int TWOWEAPON_HANDS = 4;
	private static final String[] s_handNames = {
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
	private String typeString = "";
	private TreeSet d_equipmentTypes = new TreeSet();
	private String modifiedName = ""; // where extras can be placed
	private String size = "";
	private String damage = "";
	private String critRange = "";
	private String critMult = "";
	private String altDamage = "";
	private String altCrit = "";
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
	//private Float[] childWeight = new Float[2]; // [0] is weight contained, [1] is max weight capacity
	private Float containerWeightCapacity = new Float(0);
	private String containerCapacityString = "";
	private String containerContentsString = "";
	private boolean containerConstantWeight = false;
	private Equipment headerParent = null;
	private ArrayList d_headerChildren = new ArrayList(0);
	private boolean hasHeaderParent = false;
	private boolean isHeaderParent = false;
	private boolean isOnlyNaturalWeapon = false;
	private boolean attacksProgress = true;//if is true a BAB of 13 yields 13/8/3, if false, merely 13.

	public boolean isAttacksProgress()
	{
		return attacksProgress;
	}

	public void setAttacksProgress(boolean attacksProgress)
	{
		this.attacksProgress = attacksProgress;
	}

	public boolean isOnlyNaturalWeapon()
	{
		return isOnlyNaturalWeapon;
	}

	public void setOnlyNaturalWeapon(boolean onlyNaturalWeapon)
	{
		isOnlyNaturalWeapon = onlyNaturalWeapon;
	}

	public void setIsHeaderParent(boolean aHeaderParent)
	{
		isHeaderParent = aHeaderParent;
	}

	public boolean isHeaderParent()
	{
		return isHeaderParent;
	}

	public boolean getHasHeaderParent()
	{
		return hasHeaderParent;
	}

	public HashMap getD_childTypes()
	{
		return d_childTypes;
	}

	public ArrayList getD_containedEquipment()
	{
		return d_containedEquipment;
	}

	public int getHands()
	{
		return hands;
	}

	public Float getCarried()
	{
		return carried;
	}

	public void setCarried(Float carried)
	{
		this.carried = carried;
	}

	public String getAltCrit()
	{
		return altCrit;
	}

	public String getAltDamage()
	{
		return altDamage;
	}

	public static String getHandName(int i)
	{
		return s_handNames[i];
	}

	public static int getHandNum(String handDesc)
	{
		for (int i = 0; i < s_handNames.length; ++i)
		{
			if (s_handNames[i].equals(handDesc)) return i;
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

	public String choiceString()
	{
		return choiceString;
	}

	private void setChoiceString(String aString)
	{
		choiceString = aString;
	}


	public void clearContainedEquipment()
	{
		d_containedEquipment.clear();
	}

	public Equipment createHeaderParent()  //returns pointer to Equipment of first headerChild
	{
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
		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)  //funky hack, may have problems with container inside a container.
		{
			Equipment aChild = (Equipment)e.next();
			aChild.setParent(anEquip);
		}

		d_containedEquipment.clear(); //clear the last junk

		return anEquip;
	}

	public void collapseHeaderParent()
	{
		Equipment lastHchild = getHeaderChild(0);
		hasHeaderParent = false;
		isHeaderParent = false;
		qty = new Float(1);
		d_childTypes = new HashMap(lastHchild.d_childTypes);
		d_containedEquipment = new ArrayList(lastHchild.d_containedEquipment);
		setParent(lastHchild.getParent());

		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)  //funky hack, may have problems with container inside a container.
		{
			Equipment aChild = (Equipment)e.next();
			aChild.setParent(this);
		}

		d_headerChildren.clear();
	}

	public void quickRemoveAllChildren()
	{
		for (Iterator e = d_containedEquipment.iterator(); e.hasNext();)  //funky hack, may have problems with container inside a container.
		{
			Equipment aChild = (Equipment)e.next();
			aChild.d_parent = null;
		}
		d_containedEquipment.clear();
	}

	public void setHeaderParent(Object anObject)
	{
		headerParent = (Equipment)anObject;
	}

	public void addHeaderChild(Object anObject)
	{
		d_headerChildren.add((Equipment)anObject);
	}

	public int indexOfHeaderChild(Object anObject)
	{
		return d_headerChildren.indexOf(anObject);
	}

	public int countHeaderChildren()
	{
		return d_headerChildren.size();
	}

	public Equipment getHeaderChild(int i)
	{
		return (Equipment)d_headerChildren.get(i);
	}

	public Equipment getHeaderParent()
	{
		return headerParent;
	}

	public ArrayList getHeaderChildren()
	{
		return d_headerChildren;
	}

	public void setHeaderChildren(ArrayList anArray)
	{
		d_headerChildren.clear();
		d_headerChildren.addAll(anArray);
	}

	public void removeHeaderChild(Object anObject)
	{
		d_headerChildren.remove(anObject);
	}

	public void clearHeaderChildren()
	{
		d_headerChildren.clear();
	}

	public String getSpecialProperties()
	{
		return specialProperties;
	}

	public void setSpecialProperties(String aProp)
	{
		specialProperties = aProp;
	}

	public String toString()
	{
		StringBuffer aString = new StringBuffer(name);
		if (hasHeaderParent)
			aString.append(" -").append(headerParent.indexOfHeaderChild(this) + 1).append("- ");

		if (modifiedName.length() > 0)
			aString.append(" (").append(modifiedName).append(")");
		return aString.toString();
	}

	public void setIndexedUnderType(String aString)
	{
		indexedUnderType = aString;
	}

	public String isIndexedUnderType()
	{
		return indexedUnderType;
	}


	public String getName()
	{
		return toString();
	}

	public int getChildCount()
	{
		return d_containedEquipment.size();
	}

	public Object getChild(int i)
	{
		return d_containedEquipment.get(i);
	}

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

	public void removeChild(int i)
	{
		Equipment anEquip = (Equipment)d_containedEquipment.get(i);
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

	public int indexOfChild(Object child)
	{
		return d_containedEquipment.indexOf(child);
	}

	public void setParent(EquipmentCollection parent)
	{
		d_parent = parent;
	}

	public Equipment getUberParent()
	{
		if (getParent() == null)
			return this;
		Equipment anEquip = (Equipment)getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
		}
		return anEquip;
	}

	public int itemDepth()
	{
		if (getParent() == null)
			return 0;
		int i = 1;
		Equipment anEquip = (Equipment)getParent();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			i++;
		}
		return i;
	}


	public EquipmentCollection getParent()
	{
		return d_parent;
	}

	public String getParentName()
	{
		Equipment anEquip = (Equipment)getParent();
		if (anEquip != null)
			return anEquip.toString();
		if (equipped)
			return "Equipped";
		if (numberCarried().intValue() > 0)
			return "Carried";
		return "";
	}

	public void insertChild(Object child)
	{
//			if(Globals.getDebugMode())
//        		System.out.println("Inside insertChild");
		Equipment anEquip = (Equipment)child;
		Float aFloat = anEquip.numberCarried();
		Float bFloat = aFloat;

		String aString = pickChildType(anEquip.typeList(), aFloat);


		if (d_childTypes.containsKey(aString))
		{
			aFloat = new Float(getChildTypes(aString).floatValue() + aFloat.floatValue());
		}

		bFloat = new Float(getChildTypes("Total").floatValue() + bFloat.floatValue());
		d_childTypes.put(aString, aFloat);
		d_childTypes.put("Total", bFloat);
		d_containedEquipment.add(anEquip);
		anEquip.setIndexedUnderType(aString);
		anEquip.setParent(this);//hmm probably not needed; but as it currently isn't hurting anything...

		updateContainerContentsString();
		while (anEquip.getParent() != null)
		{
			anEquip = (Equipment)anEquip.getParent();
			anEquip.updateContainerContentsString();
		}

	}

	public boolean acceptsChildren()
	{
		return d_acceptsChildren;
	}

	public int canContain(Object obj) //0 on object error, 1 on can fit, 2 on too heavy, 3 on properties problem (unimplemented), 4 on capacity error
	{
		if (obj instanceof Equipment)
		{

			Equipment anEquip = (Equipment)obj;

			if (checkChildWeight(new Float(anEquip.weight().floatValue() * anEquip.numberCarried().floatValue())))
			{
				if (true) // canHold(my HashMap()))  //quick hack since the properties hashmap doesn't exist
				{

					if (checkContainerCapacity(anEquip.typeList(), anEquip.numberCarried())) //the qty value is a temporary hack - insert all or nothing.  should reset person to be a container, with capacity=capacity
					{
						return 1;
					}
					else
						return 4;
				}
				else
					return 3;
			}
			else
				return 2;
		}
		return 0;
	}

	private Float getChildTypes(String aString)
	{
		return (Float)d_childTypes.get(aString);
	}

	private Float getAcceptsTypes(String aString)
	{
		return (Float)d_acceptsTypes.get(aString);
	}

	private String pickChildType(TreeSet aTypeList, Float aQuant)
	{
		String canContain = "";
		if (getChildTypes("Total") == null)
			d_childTypes.put("Total", new Float(0));
		if ((getChildTypes("Total").floatValue() + aQuant.floatValue()) <= getAcceptsTypes("Total").floatValue())
		{

			Iterator e = aTypeList.iterator();
			String aString = null;
			while (e.hasNext() && canContain.equals(""))
			{
				aString = (String)e.next();

				if (Globals.isDebugMode())
					System.out.println("Testing " + aString);

				if (d_acceptsTypes.containsKey(aString))
				{
					if (Globals.isDebugMode())
						System.out.println("Accepts " + aString);

					if (d_childTypes.containsKey(aString))
					{
						if ((getChildTypes(aString).floatValue() + aQuant.floatValue()) <= getAcceptsTypes(aString).floatValue())
							canContain = aString;
					}
					else
					{
						if (aQuant.floatValue() <= getAcceptsTypes(aString).floatValue())
							canContain = aString;
					}
				}
			}


			if ((canContain.equals("")) && d_acceptsTypes.containsKey("Any"))
			{
				if (!d_childTypes.containsKey("Any"))
					d_childTypes.put("Any", new Float(0));

				if ((getChildTypes("Any").floatValue() + aQuant.floatValue()) <= getAcceptsTypes("Any").floatValue())
					canContain = "Any";
			}


		}
		return canContain;
	}

	private boolean checkContainerCapacity(TreeSet aTypeList, Float aQuant)
	{
		if (d_acceptsTypes.containsKey("Any"))
		{
			if (((Float)d_acceptsTypes.get("Any")).intValue() == -1)
				return true;
		}

		if (pickChildType(aTypeList, aQuant).equals(""))
			return false;
		else
			return true;
	}

	private boolean checkChildWeight(Float aFloat)
	{
		if ((aFloat.floatValue() + getContainedWeight().floatValue()) <= containerWeightCapacity.floatValue() || containerWeightCapacity.intValue() == -1)
			return true;
		else
			return false;
	}


	public boolean canHold(HashMap properties)
	{
		return true;
	}

	public void updateProperties(HashMap properties, boolean additive)
	{
		// no action
	}

	public String longName()
	{
		if (longName.length() == 0)
			return toString();
		return longName;
	}

	private void setLongName(String aString)
	{
		longName = aString;
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	private void setCost(String aString)
	{
		try
		{
			cost = new BigDecimal(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	public Integer getAcMod()
	{
		return acMod;
	}

	private void setAcMod(String aString)
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

	public Integer getMaxDex()
	{
		return maxDex;
	}

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

	public Float getContainedWeight()
	{
		Float total = new Float(0);

		if (containerConstantWeight || getChildCount() == 0)
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
					total = new Float(total.floatValue() + anEquip.weight().floatValue() + anEquip.getContainedWeight().floatValue());
				else
					total = new Float(total.floatValue() + anEquip.weight().floatValue() * anEquip.getCarried().floatValue());
			}
		}

		return total;
	}

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
			containerWeightCapacity = new Float(-1);

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
					aFloat = new Float(aFloat.floatValue() + itemNumber.floatValue());
			}
			else
			{
				limited = false;
				itemNumber = new Float(-1);
			}
			if (!itemType.equals("Any") && !itemType.equals("Total"))
				d_acceptsTypes.put(itemType.toUpperCase(), itemNumber);
			else
				d_acceptsTypes.put(itemType, itemNumber);
		}

		if (!d_acceptsTypes.containsKey("Total"))
		{
			if (!limited)
				aFloat = new Float(-1);
			d_acceptsTypes.put("Total", aFloat);
		}

		updateContainerCapacityString();
		updateContainerContentsString();
	}

	private void updateContainerCapacityString()
	{
		StringBuffer tempStringBuffer = new StringBuffer();
		boolean comma = false;

		if (containerWeightCapacity.intValue() != -1)
		{
			tempStringBuffer.append(containerWeightCapacity).append(" lbs");
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

	public void updateContainerContentsString()
	{
		containerContentsString = "";
		StringBuffer tempStringBuffer = new StringBuffer();
		// Make sure there's no bug here.
		if (acceptsChildren())
			tempStringBuffer.append(getContainedWeight()).append(" lbs");

		for (int e = 0; e < getChildCount(); e++)
		{
			final Equipment anEquip = (Equipment)getChild(e);
			if (anEquip.getCarried().compareTo(new Float(0)) > 0)
				tempStringBuffer.append(", ").append(anEquip.numberCarried()).append(" ").append(anEquip);
		}
		containerContentsString = tempStringBuffer.toString();
	}

	public String getContainerCapacityString()
	{
		return containerCapacityString;
	}

	public String getContainerContentsString()
	{
		return containerContentsString;
	}

	public Integer acCheck()
	{
		return acCheck;
	}

	private void setAcCheck(String aString)
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

	public Integer spellFailure()
	{
		return spellFailure;
	}

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

	public String moveString()
	{
		return moveString;
	}

	private void setMoveString(String aString)
	{
		moveString = aString;
	}

	public Float weight()
	{
		return weight;
	}

	private void setWeight(String aString)
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

	public String typeString()
	{
		return typeString;
	}

	public boolean typeStringContains(String aString)
	{
		return d_equipmentTypes.contains(aString.toUpperCase());
	}

	private void setTypeString(String aString)
	{
		typeString = aString.toUpperCase();
		final StringTokenizer aTok =
			new StringTokenizer(typeString, ".", false);

		String type = null;
		while (aTok.hasMoreTokens())
		{
			type = aTok.nextToken().toUpperCase();
			d_equipmentTypes.add(type);
			s_equipmentTypes.add(type);
		}
		if (d_equipmentTypes.contains("WEAPON"))
		{
			if (getCritRange().length() == 0) critRange = "1";
			if (getCritMult().length() == 0) critMult = "x2";
		}
	}

	public String modifiedName()
	{
		return modifiedName;
	}

	public void setModifiedName(String aString)
	{
		modifiedName = aString;
	}

	public String getSize()
	{
		return size;
	}

	private void setSize(String aString)
	{
		size = aString;
	}

	public String getDamage()
	{
		return damage;
	}

	private void setDamage(String aString)
	{
		damage = aString;
	}

	public String getCritRange()
	{
		return critRange;
	}

	private void setCritRange(String aString)
	{
		critRange = aString;
	}

	public String getCritMult()
	{
		return critMult;
	}

	private void setCritMult(String aString)
	{
		critMult = aString;
	}

	public Integer getRange()
	{
		return range;
	}

	private void setRange(String aString)
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

	public Integer getAttacks()
	{
		return attacks;
	}

	public String getBonusType()
	{
		return bonusType;
	}

	public void setBonusType(String aString)
	{
		bonusType = aString;
	}

	public ArrayList typeArrayList()
	{
		ArrayList types = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
			types.add(aTok.nextToken());
		return types;
	}

	public boolean isWooden()
	{
		return typeStringContains("WOODEN");
	}

	public boolean isMelee()
	{
		return typeStringContains("MELEE");
	}

	public boolean isRanged()
	{
		return typeStringContains("RANGED");
	}

	public boolean isThrown()
	{
		return typeStringContains("THROWN");
	}

	public boolean isProjectile()
	{
		return typeStringContains("PROJECTILE");
	}

	public boolean isArmor()
	{
		return typeStringContains("ARMOR");
	}

	public boolean isWeapon()
	{
		return typeStringContains("WEAPON");
	}

	public boolean isNatural()
	{
		return typeStringContains("NATURAL");
	}

	public boolean isShield()
	{
		return typeStringContains("SHIELD");
	}

	public boolean isExtra()
	{
		return typeStringContains("EXTRA");
	}

	public boolean isLight()
	{
		return typeStringContains("LIGHT");
	}

	public boolean isMedium()
	{
		return typeStringContains("MEDIUM");
	}

	public boolean isHeavy()
	{
		return typeStringContains("HEAVY");
	}

	public TreeSet typeList()
	{
		return d_equipmentTypes;
	}

	public String typeIndex(int i)
	{
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		String aString = "";
		while (i-- >= 0 && aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
		}
		return aString;
	}

	public Float qty()
	{
		if (!isHeaderParent)
			return qty;

		float aQty = 0;
		for (Iterator e = d_headerChildren.iterator(); e.hasNext();)//could just report d_headerChildren.size(), but this is safer.
		{
			aQty += ((Equipment)e.next()).qty().floatValue();
		}

		return new Float(aQty);
	}

	public void setQty(String aString)
	{
		if (isHeaderParent)
			qty = qty();
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

	public void setQty(Float aFloat)
	{
		if (!isHeaderParent)
			qty = aFloat;
		else
			qty = qty();
	}

	public String profName()
	{
		return profName(getHand());
	}

	private String profName(int hand)
	{
		String aWProf = profName;
		if (aWProf.length() == 0)
		{
			aWProf = getName();
		}

		int iOffs = aWProf.indexOf("[Hands]");
		if (iOffs >= 0)
		{
		//
		// Generate weapon proficiency name based on number of hands of race
		// eg. for "Sword (Bastard/[Hands])"
		// we should get "Sword (Bastard/1-H)", "Sword (Bastard/2-H)"
		//
/*			if (hand == BOTH_HANDS)
				aWProf = aWProf.substring(0,iOffs) + "2-H" + aWProf.substring(iOffs+7);
			else
				aWProf = aWProf.substring(0,iOffs) + "1-H" + aWProf.substring(iOffs+7);
*/
			// The basic logic should be: if the weapon is Light, or is one-handed and being used
			// in two hands, then use the 2-H proficiency.  The reason being that the 2-H version
			// of these profs will be lesser than the 1-H version (2-H is probably Martial, and 1-H
			// is exotic. For races which can only use these weapons in 2 hands, the prof must be the
			// harder one (probably exotic). Bryan McRoberts (merton_monk@yahoo.com) 10/20/01
			String w1String = aWProf.substring(0,iOffs) + "1-H" + aWProf.substring(iOffs+7);
			String w2String = aWProf.substring(0,iOffs) + "2-H" + aWProf.substring(iOffs+7);
			WeaponProf wp = Globals.getWeaponProfNamed(w1String);
			if (wp==null || wp.isLight() || (wp.isOneHanded() && hand == BOTH_HANDS))
				aWProf = w2String;
			else
				aWProf = w1String;
		
		}
		return aWProf;
	}


	public Float numberCarried()
	{
		if (!isHeaderParent)
			return carried;

		float aQty = 0;
		for (Iterator e = d_headerChildren.iterator(); e.hasNext();)
		{
			aQty += ((Equipment)e.next()).numberCarried().floatValue();
		}

		return new Float(aQty);

	}

	public boolean isEquipped()
	{
		return equipped;
	}

	public int compareTo(Object o)
	{
		Equipment e = (Equipment)o;
		return this.keyName.compareTo(e.keyName);
	}

	public boolean equals(Object o)
	{
		return (o != null) && (o instanceof Equipment) &&
			((o == (Object)this) ||
			this.keyName.equals(((Equipment)o).keyName));
	}

	public int hashCode()
	{
		return this.keyName.hashCode();
	}

	/**
	 * Return the set of equipment type names as a sorted set of strings.
	 */
	public static SortedSet getEquipmentTypes()
	{
		return s_equipmentTypes;
	}

	public static void clearEquipmentTypes()
	{
		s_equipmentTypes.clear();
	}

	/**
	 * Return the list of elements from eqList that are weapons in which
	 * the current player character is proficient.
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
	 * Return the list of elements from eqList that are marked as equipped.
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
	 * Select a subset of a list of equipment base on the type string.
	 * If <code>eqList</code> is sorted, the result will be sorted as
	 * well.
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
				if (e.typeStringContains(type))
				{
					result.add(e);
				}
			}
		}
		return result;
	}

	public void setNumberCarried(Float aNumber)
	{
		if (!isHeaderParent)
			carried = aNumber;
		else
			carried = numberCarried();
	}

	public int getNumberEquipped()
	{
		return numberEquipped;
	}

	public void setNumberEquipped(int num)
	{
		numberEquipped = num;
	}

	public int getReach()
	{
		return reach;
	}

	public void setReach(int newReach)
	{
		reach = newReach;
	}

	public void setIsEquipped(boolean aFlag)
	{
		setIsEquipped(aFlag,inHand);
	}

	public void setIsEquipped(boolean aFlag,int hand)
	{
		if (aFlag)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (typeStringContains("SUIT"))
				equipped = aPC.canEquip("SUIT");
			else if (typeStringContains("SHIRT"))
				equipped = aPC.canEquip("SHIRT");
			else if (typeStringContains("HEADGEAR"))
				equipped = aPC.canEquip("HEADGEAR");
			else if (typeStringContains("EYEGEAR"))
				equipped = aPC.canEquip("EYEGEAR");
			else if (typeStringContains("CAPE"))
				equipped = aPC.canEquip("CAPE");
			else if (typeStringContains("AMULET"))
				equipped = aPC.canEquip("AMULET");
			else if (typeStringContains("ROBE"))
				equipped = aPC.canEquip("ROBE");
			else if (typeStringContains("BRACER"))
				equipped = aPC.canEquip("BRACER");
			else if (typeStringContains("GLOVE"))
				equipped = aPC.canEquip("GLOVE");
			else if (typeStringContains("RING"))
				equipped = aPC.canEquip("RING");
			else if (typeStringContains("BELT"))
				equipped = aPC.canEquip("BELT");
			else if (typeStringContains("BOOT"))
				equipped = aPC.canEquip("BOOT");
			else if (typeStringContains("WEAPON") && aPC.canEquip("Weapon") ||
				typeStringContains("SHIELD") && aPC.canEquip("Shield"))
			{
				int iRaceHands = aPC.getRace().GetHands();
				int handsInUse = aPC.handsFull();
				if (typeStringContains("Weapon"))
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
						handsInUse++;
					if (wp.isTwoHanded())
						handsInUse += 2;
					if (wp.isTooLarge())
						handsInUse += 99;		// Add something gross, in case we have a race with tons of hands
				}
				else
					handsInUse += getHands(); // shields take up 1 hand, except bucklers. Adding getHands() means we cover all bases, including future (potential) 2-handed shields.

				if (handsInUse > iRaceHands)
				{
					equipped = false;
					JOptionPane.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", "PCGen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				equipped = true;
			}
			else
				equipped = aFlag;
			if (equipped == false)
				JOptionPane.showMessageDialog(null, "Character cannot equip any more of this item type.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			equipped = aFlag;
	}

	public Object clone()
	{
		Equipment eq = (Equipment)super.clone();
		eq.setCost(getCost().toString());
		eq.setWeight(weight().toString());
		eq.setAcMod(getAcMod().toString());
		eq.setMaxDex(getMaxDex().toString());
		eq.setAcCheck(acCheck().toString());
		eq.setSpellFailure(spellFailure().toString());
		eq.setMoveString(moveString());
		eq.setTypeString(typeString());
		eq.setSize(getSize());
		eq.setDamage(getDamage());
		eq.setCritRange(getCritRange());
		eq.setCritMult(getCritMult());
		eq.setRange(getRange().toString());
		eq.isSpecified = isSpecified;
		eq.profName = profName;
		eq.carried = carried;
		eq.inHand = inHand;
		eq.altDamage = altDamage;
		eq.altCrit = altCrit;
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

		//header crap
		eq.hasHeaderParent = hasHeaderParent;
		eq.headerParent = headerParent;
		eq.d_headerChildren = new ArrayList(d_headerChildren);

		return eq;
	}


	public final int whatHand()
	{
		return inHand;
	}

	public final int getHand()
	{
		if (inHand >= 4)
			return 4;
		return inHand;
	}


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

			if (wp.getType().equals("Natural"))	//Don't worry if it can be placed in those hands if it is a natural weapon.
			{
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

	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	public String preReqString()
	{
		return preReqStrings();
	}

	public void parseLine(String aLine, File sourceFile, int lineNum)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		int col = 0;
		String aString = null;
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (super.parseTag(aString))
				continue;
			final int aLen = aString.length();
			if (col == 0)
				setName(aString);
			else if ((aLen > 5) && aString.startsWith("Cost:"))
				setCost(aString.substring(5));
			else if (aLen > 7 && aString.startsWith("CHOOSE"))
				setChoiceString(aString.substring(7));
			else if ((aLen > 3) && aString.startsWith("AC:"))
				setAcMod(aString.substring(3));
			else if ((aLen > 7) && aString.startsWith("MAXDEX:"))
				setMaxDex(aString.substring(7));
			else if ((aLen > 8) && aString.startsWith("ACCHECK:"))
				setAcCheck(aString.substring(8));
			else if ((aLen > 5) && aString.startsWith("MOVE:"))
				setMoveString(aString.substring(5));
			else if ((aLen > 3) && aString.startsWith("WT:"))
				setWeight(aString.substring(3));
			else if ((aLen > 10) && aString.startsWith("BONUSTYPE:"))
				bonusType = aString.substring(10);
			else if ((aLen > 5) && aString.startsWith("TYPE:"))
				setTypeString(aString.substring(5));
			else if ((aLen > 5) && aString.startsWith("SIZE:"))
				setSize(aString.substring(5));
			else if ((aLen > 13) && aString.startsWith("SPELLFAILURE"))
				setSpellFailure(aString.substring(13));
			else if ((aLen > 7) && aString.startsWith("DAMAGE:"))
				setDamage(aString.substring(7));
			else if ((aLen > 10) && aString.startsWith("CRITRANGE:"))
				setCritRange(aString.substring(10));
			else if ((aLen > 9) && aString.startsWith("CRITMULT"))
				setCritMult(aString.substring(9));
			else if ((aLen > 6) && aString.startsWith("RANGE:"))
				setRange(aString.substring(6));
			else if ((aLen > 9) && aString.startsWith("LONGNAME"))
				setLongName(aString.substring(9));
			else if ((aLen > 8) && aString.startsWith("ATTACKS"))
				attacks = Delta.decode(aString.substring(8));
			else if ((aLen > 12) && aString.startsWith("PROFICIENCY"))
				profName = aString.substring(12);
			else if ((aLen > 7) && aString.startsWith("DEFINE"))
				variableList.add("0|" + aString.substring(7));
			else if ((aLen > 4) && aString.startsWith("KEY:"))
				setKeyName(aString.substring(4));
			else if (aString.startsWith("PRE"))
				preReqArrayList.add(aString);
			else if (aString.startsWith("QUALIFY:"))
				addToQualifyListing(aString.substring(8));
			else if ((aLen > 10) && aString.startsWith("ALTDAMAGE:"))
				altDamage = aString.substring(10);
			else if ((aLen > 12) && aString.startsWith("ALTCRITICAL:"))
				altCrit = aString.substring(12);
			else if (aString.startsWith("REACH"))
				reach = Integer.parseInt(aString.substring(6));
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
				return;
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
				return;
			bonusType += "Shield";
		}
	}
}
