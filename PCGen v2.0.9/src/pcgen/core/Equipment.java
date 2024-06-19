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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import pcgen.util.Delta;

public class Equipment extends PObject
  implements Serializable, EquipmentCollection, Comparable
{
  /**
   * Maintain a list of all equipment types.
   */
  private static TreeSet s_equipmentTypes = new TreeSet();

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
          "Unable to interpret hand setting: " + handDesc);
      return 0;
    }
  }

  Float cost = new Float(0);
  Integer acMod = new Integer(0);
  Integer maxDex = new Integer(100);
  Integer acCheck = new Integer(0);
  Integer spellFailure = new Integer(0);
  String moveString = "";
  Float weight = new Float(0);
  private String typeString = "";
  private TreeSet d_equipmentTypes = new TreeSet();
  String modifiedName = ""; // where extras can be placed
  String size = "";
  String damage = "";
  String critRange = "";
  String critMult = "";
  String altDamage = "";
  String altCrit = "";
  Integer range = new Integer(0);
  Float qty = new Float(0);
  boolean equipped = false;
  String longName = "";
  Integer attacks = new Integer(1);
  String profName = "";
  Float carried = new Float(0);
  private int inHand = NEITHER_HAND;
  int hands = 1;
  ArrayList d_containedEquipment = new ArrayList(0);
  EquipmentCollection d_parent = null;
  String bonusType = null;
  String specialProperties = "";
  int numberEquipped = 0;
  int reach = 0;
  
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
    if (modifiedName.length() > 0)
      return name + " (" + modifiedName + ")";
    return name;
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

  public void removeChild(int i)
  {
    d_containedEquipment.remove(i);
  }

  public int indexOfChild(Object child)
  {
    return d_containedEquipment.indexOf(child);
  }

  public void setParent(EquipmentCollection parent)
  {
    d_parent = parent;
  }

  public EquipmentCollection getParent()
  {
    return d_parent;
  }

  public void insertChild(int i, Object child)
  {
    d_containedEquipment.add(i, child);
  }

  public boolean acceptsChildren()
  {
    return true;
  }

  public boolean canContain(Object obj)
  {
    return (obj instanceof Equipment);
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
      return name;
    return longName;
  }

  private void setLongName(String aString)
  {
    longName = aString;
  }

  public Float cost()
  {
    return cost;
  }

  private void setCost(String aString)
  {
    try
    {
      cost = new Float(aString);
    }
    catch (NumberFormatException nfe)
    {
      // ignore
    }
  }

  public Integer acMod()
  {
    return acMod;
  }

  private void setAcMod(String aString)
  {
    try
    {
      acMod = pcgen.util.Delta.decode(aString);
    }
    catch (NumberFormatException nfe)
    {
      // ignore
    }
  }

  public Integer maxDex()
  {
    return maxDex;
  }

  private void setMaxDex(String aString)
  {
    try
    {
      maxDex = pcgen.util.Delta.decode(aString);
    }
    catch (NumberFormatException nfe)
    {
      // ignore
    }
  }

  public Integer acCheck()
  {
    return acCheck;
  }

  private void setAcCheck(String aString)
  {
    try
    {
      acCheck = pcgen.util.Delta.decode(aString);
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
      spellFailure = pcgen.util.Delta.decode(aString);
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
    StringTokenizer aTok =
      new StringTokenizer(typeString, ".", false);
    while (aTok.hasMoreTokens())
    {
      String type = aTok.nextToken().toUpperCase();
      d_equipmentTypes.add(type);
      s_equipmentTypes.add(type);
    }
    if (d_equipmentTypes.contains("WEAPON"))
    {
      if (critRange().length() == 0) critRange = "1";
      if (critMult().length() == 0) critMult = "x2";
    }
  }

  public String modifiedName()
  {
    return modifiedName;
  }

  private void setModifiedName(String aString)
  {
    modifiedName = aString;
  }

  public String size()
  {
    return size;
  }

  private void setSize(String aString)
  {
    size = aString;
  }

  public String damage()
  {
    return damage;
  }

  private void setDamage(String aString)
  {
    damage = aString;
  }

  public String critRange()
  {
    return critRange;
  }

  private void setCritRange(String aString)
  {
    critRange = aString;
  }

  public String critMult()
  {
    return critMult;
  }

  private void setCritMult(String aString)
  {
    critMult = aString;
  }

  public Integer range()
  {
    return range;
  }

  private void setRange(String aString)
  {
    try
    {
      range = pcgen.util.Delta.decode(aString);
    }
    catch (NumberFormatException nfe)
    {
      // ignore
    }
  }

  public Integer attacks()
  {
    return attacks;
  }

  private void setAttacks(String aString)
  {
    try
    {
      attacks = pcgen.util.Delta.decode(aString);
    }
    catch (NumberFormatException nfe)
    {
      // ignore
    }
  }

  public String altDamage()
  {
    return altDamage;
  }

  public String altCrit()
  {
    return altCrit;
  }

  public String bonusType()
  {
    return bonusType;
  }
  
  public void setBonusType(String aString)
  {
    bonusType = aString;
  }
  public Vector typeVector()
  {
    Vector aVector = new Vector();
    StringTokenizer aTok = new StringTokenizer(typeString,".",false);
    while(aTok.hasMoreTokens())
      aVector.addElement(aTok.nextToken());
    return aVector;
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
    StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
    String aString = "";
    while (i-- >= 0 && aTok.hasMoreTokens())
    {
      aString = aTok.nextToken();
    }
    return aString;
  }

  public Float qty()
  {
    return qty;
  }

  public void setQty(String aString)
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

  public void setQty(Float aFloat)
  {
    qty = aFloat;
  }

  public String profName()
  {
    if (profName.length() == 0)
      return name();
    else
      return profName;
  }

  public Float numberCarried()
  {
    return carried;
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
    if (Globals.currentPC != null)
    {
      result.ensureCapacity(eqList.size() / 10);
      Iterator i = eqList.iterator();
      while (i.hasNext())
      {
        Equipment e = (Equipment)i.next();
        if (Globals.currentPC.isProficientWith(e))
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
    while (i.hasNext())
    {
      Equipment e = (Equipment)i.next();
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
      while (i.hasNext())
      {
        Equipment e = (Equipment)i.next();
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
    carried = aNumber;
  }

  public int numberEquipped()
  {
    return numberEquipped;
  }
  
  public void setNumberEquipped(int num)
  {
    numberEquipped = num;
  }
  
  public int reach()
  {
    return reach;
  }
  
  public void setReach(int newReach)
  {
    reach = newReach;
  }
  
  public void setIsEquipped(boolean aFlag)
  {
    int i = 0;
    if (aFlag == true)
    {
      if (typeStringContains("SUIT"))
        equipped = Globals.currentPC.canEquip("SUIT");
      else if (typeStringContains("SHIRT"))
        equipped = Globals.currentPC.canEquip("SHIRT");
      else if (typeStringContains("HEADGEAR"))
        equipped = Globals.currentPC.canEquip("HEADGEAR");
      else if (typeStringContains("EYEGEAR"))
        equipped = Globals.currentPC.canEquip("EYEGEAR");
      else if (typeStringContains("CAPE"))
        equipped = Globals.currentPC.canEquip("CAPE");
      else if (typeStringContains("AMULET"))
        equipped = Globals.currentPC.canEquip("AMULET");
      else if (typeStringContains("ROBE"))
        equipped = Globals.currentPC.canEquip("ROBE");
      else if (typeStringContains("BRACER"))
        equipped = Globals.currentPC.canEquip("BRACER");
      else if (typeStringContains("GLOVE"))
        equipped = Globals.currentPC.canEquip("GLOVE");
      else if (typeStringContains("RING"))
        equipped = Globals.currentPC.canEquip("RING");
      else if (typeStringContains("BELT"))
        equipped = Globals.currentPC.canEquip("BELT");
      else if (typeStringContains("BOOT"))
        equipped = Globals.currentPC.canEquip("BOOT");
      else if (typeStringContains("WEAPON") && Globals.currentPC.canEquip("Weapon") ||
        typeStringContains("SHIELD") && Globals.currentPC.canEquip("Shield"))
      {
        int handsInUse = Globals.currentPC.handsFull();
        if (typeStringContains("Weapon"))
        {
          WeaponProf wp = Globals.getWeaponProfNamed(profName());
          if (wp == null)
          {
            equipped = false;
            JOptionPane.showMessageDialog(null, "Cannot equip weapon - no valid weapon proficiency for " + name() + " loaded.");
            if (Globals.debugMode)
            {
              System.out.println("Globals: " + Globals.weaponProfList);
              System.out.println("Proficiency name: " + profName() + " " + this);
            }
            return;
          }
          if (wp.isOneHanded())
            handsInUse++;
          if (wp.isTwoHanded())
            handsInUse += 2;
          if (wp.isTooLarge())
            handsInUse += 3;
        }
        else
          handsInUse++; // shields take up 1 hand
        if (handsInUse > Globals.currentPC.race().hands())
        {
          equipped = false;
          JOptionPane.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.");
          return;
        }
        equipped = true;
      }
      else
        equipped = aFlag;
      if (equipped == false)
        JOptionPane.showMessageDialog(null, "Character cannot equip any more of this item type.");
    }
    else
      equipped = aFlag;
  }

  public Object clone()
  {
    Equipment eq = (Equipment)super.clone();
    eq.setCost(cost().toString());
    eq.setWeight(weight().toString());
    eq.setAcMod(acMod().toString());
    eq.setMaxDex(maxDex().toString());
    eq.setAcCheck(acCheck().toString());
    eq.setSpellFailure(spellFailure().toString());
    eq.setMoveString(moveString());
    eq.setTypeString(typeString());
    eq.setSize(size());
    eq.setDamage(damage());
    eq.setCritRange(critRange());
    eq.setCritMult(critMult());
    eq.setRange(range().toString());
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
    return eq;
  }

  public final int whatHand()
  {
    return inHand;   
  }
  
  public final int getHand()
  {
    if(inHand >= 4)
      return 4;
    return inHand;
  }

  public final void setHand(int hand)
  {
    if (isWeapon())
    {
      WeaponProf wp = Globals.getWeaponProfNamed(profName());
      if (wp==null)
      {
        JOptionPane.showMessageDialog(null, "Cannot find a valid weapon prof for "+name+" loaded.");
        return;
      }
      if (wp.isLight() && hand==BOTH_HANDS && !wp.isTwoHanded())
      {
        JOptionPane.showMessageDialog(null, "Cannot place light weapon in both hands.");
        return;
      }
      if (wp.isTwoHanded() && hand!=NEITHER_HAND && hand!=BOTH_HANDS)
      {
        JOptionPane.showMessageDialog(null, "Two handed weapon must be in Neither or Both hands.");
        return;
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
    StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
    int col = 0;
    while (aTok.hasMoreTokens())
    {
      String aString = aTok.nextToken();
      final int aLen = aString.length();
      if (col == 0)
        setName(aString);
      else if ((aLen > 5) && aString.startsWith("Cost:"))
        setCost(aString.substring(5));
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
        attacks = pcgen.util.Delta.decode(aString.substring(8));
      else if ((aLen > 12) && aString.startsWith("PROFICIENCY"))
        profName = aString.substring(12);
      else if ((aLen > 6) && aString.startsWith("BONUS"))
        addBonusList(aString.substring(6));
      else if ((aLen > 7) && aString.startsWith("DEFINE"))
        variableList.add("0|" + aString.substring(7));
      else if ((aLen > 4) && aString.startsWith("KEY:"))
        setKeyName(aString.substring(4));
      else if (aString.startsWith("PRE"))
        preReqArrayList.add(aString);
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
          hands = pcgen.util.Delta.parseInt(aString.substring(6));
        }
        catch (NumberFormatException nfe)
        {
          JOptionPane.showMessageDialog
            (null, "Illegal number of required hands " +
            sourceFile.getName() + ":" + Integer.toString(lineNum)
            + " \"" + aString + "\"");
        }
      }
      else
      {
        JOptionPane.showMessageDialog
          (null, "Illegal equipment info " +
          sourceFile.getName() + ":" + Integer.toString(lineNum)
          + " \"" + aString + "\"");
      }
      col++;
    }
    if(isArmor()) {
      if(bonusType == null) {
        bonusType = "Armor";
        return;
      }
      if(bonusType.lastIndexOf("Armor")>-1)
        return;
      bonusType += "Armor";
    }
    if(isShield()) {
      if(bonusType == null) {
        bonusType = "Shield";
        return;
      }
      if(bonusType.lastIndexOf("Shield")>-1)
        return;
      bonusType += "Shield";
    }
  }
}
