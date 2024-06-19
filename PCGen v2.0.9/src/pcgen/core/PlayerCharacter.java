/*
 * PlayerCharacter.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PlayerCharacter extends Object
{
	String name = new String();
	String playersName = new String();
	ArrayList classList = new ArrayList();
	ArrayList skillList = new ArrayList();
	ArrayList featList = new ArrayList();
	ArrayList domainList = new ArrayList();
	int skillPoints = 0; // pool of skills remaining to distribute
	int feats = 0; // pool of feats remaining to distribute
	int[] stats = new int[6];
	int remainingPool = 0;
	int costPool = 0;
	Race race = null;
	int poolAmount = 0; // pool of stats remaining to distribute
	boolean isImporting = false;
	int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	Deity deity = null;
	int height = 0; // in inches
	int weight = 0; // in pounds
	int age = 0; // in years
	String gender = "M";
	String handed = "Right";
	ArrayList specialAbilityList = new ArrayList();
	TreeSet languagesList = new TreeSet();
	TreeSet weaponProfList = new TreeSet();
	String eyeColor = "";
	String skinColor = "";
	String hairColor = "";
	String hairLength = "";
	String speechTendency = "";
	String phobias = "";
	String interests = "";
	String catchPhrase = "";
	String trait1 = "";
	String trait2 = "";
	String residence = "";
	String location = "";
	private TreeMap equipmentList = new TreeMap();
	boolean inLabel = false;
	boolean canWrite = true;
	Float gold = new Float(0);
	String bio = new String();
	String description = new String();
	boolean existsOnly = false;
	boolean noMoreItems = false;
	boolean checkBefore = false;
	Equipment primaryWeapon = null;
	Equipment secondaryWeapon[] = new Equipment[1];
	Integer experience = new Integer(0);
	ArrayList miscList = new ArrayList(3);
	int currentHP = 0;
	ArrayList spellBooks = new ArrayList();
	String statNames = "STRDEXCONINTWISCHA";
	int weaponMod = 0;
	int initiative = 0;
	ArrayList variableList = new ArrayList();
	boolean dirtyFlag = false; //Whether the character has changed since last saved.
	String fileName = ""; //This may be different from character name...
	int bonusWeaponChoices = 0;
	private static int loopValue = 0;
	private static String loopVariable = "";
	private static int decrement;
	private String lastLineParsed = "";


	private static HashMap s_sizes = new HashMap();
	private final static int[][] size_array = {
		/* sizeInt, sizeMod, grappleSizeMod */
		{0, 8, -16},
		{1, 4, -12},
		{2, 2, -8},
		{3, 1, -4},
		{4, 0, 0},
		{5, -1, -4},
		{6, -2, -8},
		{7, -4, -12},
		{8, -8, -16},
	};

	public String size()
	{
		if (race == null)
			return "M";
		String size = race.size();
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
				size = "M";
			else if (size.equals("M"))
				size = "L";
			else if (size.equals("L"))
				size = "H";
			else if (size.equals("H"))
				size = "G";
			else if (size.equals("G"))
				size = "C";
			else
				break;
		}
		if (size.equals(""))
			return "M";
		return size;
	}

	/** NOTE: Returns 0 if size() is bad... */
	public int sizeInt()
	{
		String key = size();
		return sizeIntForSize(key);
	}

	public int sizeIntForSize(String key)
	{
		return intForSize(key);
	}

	private int intForSize(String key)
	{
		if (key != null && s_sizes.containsKey(key))
		{
			return ((int[])s_sizes.get(key))[0];
		}
		else
		{
			//Not really the right thing to do, but...
			return 0;
		}
	}

	public int modForSize()
	{
		return ((int[])s_sizes.get(size()))[1];
	}

	public int grappleModForSize()
	{
		return ((int[])s_sizes.get(size()))[2];
	}

	public int strModForSize()
	{
		String size = race.size();
		int strmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
			{
				strmod += 2;
				size = "T";
			}
			else if (size.equals("T"))
			{
				strmod += 4;
				size = "S";
			}
			else if (size.equals("S"))
			{
				strmod += 4;
				size = "M";
			}
			else if (size.equals("M"))
			{
				strmod += 8;
				size = "L";
			}
			else if (size.equals("L"))
			{
				strmod += 8;
				size = "H";
			}
			else if (size.equals("H"))
			{
				strmod += 8;
				size = "G";
			}
			else if (size.equals("G"))
			{
				strmod += 8;
				size = "C";
			}
			else
				break;
		}
		return strmod;
	}

	public int conModForSize()
	{
		String size = race.size();
		int conmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
			{
				conmod += 2;
				size = "M";
			}
			else if (size.equals("M"))
			{
				conmod += 4;
				size = "L";
			}
			else if (size.equals("L"))
			{
				conmod += 4;
				size = "H";
			}
			else if (size.equals("H"))
			{
				conmod += 4;
				size = "G";
			}
			else if (size.equals("G"))
			{
				conmod += 4;
				size = "C";
			}
			else
				break;
		}
		return conmod;
	}

	public int dexModForSize()
	{
		String size = race.size();
		int dexmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
			{
				dexmod -= 2;
				size = "D";
			}
			else if (size.equals("D"))
			{
				dexmod -= 2;
				size = "T";
			}
			else if (size.equals("T"))
			{
				dexmod -= 2;
				size = "S";
			}
			else if (size.equals("S"))
			{
				dexmod -= 2;
				size = "M";
			}
			else if (size.equals("M"))
			{
				dexmod -= 2;
				size = "L";
			}
			else if (size.equals("L"))
			{
				dexmod -= 2;
				size = "H";
			}
			else if (size.equals("H"))
				break;
			else if (size.equals("G"))
				break;
			else
				break;
		}
		return dexmod;
	}

	public int naturalArmorModForSize()
	{
		String size = race.size();
		int naturalarmormod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
				size = "M";
			else if (size.equals("M"))
			{
				naturalarmormod += 2;
				size = "L";
			}
			else if (size.equals("L"))
			{
				naturalarmormod += 3;
				size = "H";
			}
			else if (size.equals("H"))
			{
				naturalarmormod += 4;
				size = "G";
			}
			else if (size.equals("G"))
			{
				naturalarmormod += 5;
				size = "C";
			}
			else
				break;
		}
		return naturalarmormod;
	}

	/** Gets the filename of the character. */
	public String getFileName()
	{
		return fileName;
	}

	/** Sets the filename of the character. */
	public void setFileName(String newFileName)
	{
		fileName = newFileName;
	}

	/** Gets whether the character has been changed since last saved. */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/** Sets the character changed since last save. */
	public void setDirty(boolean dirtyState)
	{
		dirtyFlag = dirtyState;
	}

	/**
	 * @return true if character is currently being read from file.
	 */
	public boolean getIsImporting()
	{
		return isImporting;
	}

	public void setImporting(boolean newIsImporting)
	{
		isImporting = newIsImporting;
	}

	public String name()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
	}

	public String playersName()
	{
		return playersName;
	}

	public void setPlayersName(String aString)
	{
		playersName = aString;
	}

	public int bonusWeaponChoices()
	{
		return bonusWeaponChoices;
	}

	public PCClass getClassNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.name().equals(aString))
				return aClass;
		}
		return null;
	}

	public PCClass getClassKeyed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.keyName().equals(aString))
				return aClass;
		}
		return null;
	}

	public PCClass getSpellClassAtIndex(int ix)
	{
		PCClass aClass = null;
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			aClass = (PCClass)i.next();
			if (aClass.spellList().size() > 0)
				ix--;
			else
				aClass = null;
			if (ix == -1)
				break;
		}
		if (ix == -1 && aClass != null && aClass.spellList().size() > 0)
			return aClass;
		return null;
	}

	public void addVariable(String variableString)
	{
		variableList.add(variableString);
	}

	public void removeVariable(String variableString)
	{
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.startsWith(variableString))
			{
				e.remove();
			}
		}
	}

	public boolean hasVariable(String variableString)
	{
		StringTokenizer aTok;
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			aTok = new StringTokenizer((String)e.next(), "|", false);
			aTok.nextToken(); //src
			aTok.nextToken(); //subSrc
			if (((String)aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				return true;
		}

		return false;
	}

	public Float getVariable(String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc)
	{
		Float value = new Float(0.0);
		int found = 0;
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			String vString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(vString, "|", false);
			String src = aTok.nextToken();
			if (matchSrc.length() > 0 && !src.equals(matchSrc))
				continue;
			String subSrc = aTok.nextToken();
			if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
				continue;
			String nString = aTok.nextToken();
			if (nString.equals(variableString))
			{
				String sString = aTok.nextToken();
				Float newValue = getVariableValue(sString, src, subSrc);
				if (found == 0)
					value = newValue;
				else if (isMax)
					value = new Float(Math.max(value.doubleValue(), newValue.doubleValue()));
				else
					value = new Float(Math.min(value.doubleValue(), newValue.doubleValue()));
				found = 1;
				if (!loopVariable.equals(""))
				{
					while (loopValue > decrement)
					{
						loopValue -= decrement;
						value = new Float(value.doubleValue() + getVariableValue(sString, src, subSrc).doubleValue());
					}
					loopValue = 0;
					loopVariable = "";
				}
			}
		}
		if (includeBonus == true)
		{
			int i = getTotalBonusTo("VAR", variableString, true);
			value = new Float(value.doubleValue() + i);
		}
		return value;
	}

	public int alignment()
	{
		return alignment;
	}

  public int movement(int x)
  {
    int move = race().movement(x).intValue();
    boolean isMedium = race().movement(x).intValue() == 30;
    ArrayList aArrayList = getEquipmentOfType("Armor", 1);
    Equipment eq = null;
    Iterator e = null;
    int bonus = 0;
    int i = 0;
    i = Globals.loadTypeForStrength(adjStats(0), totalWeight());
    switch (i)
    {
      case 1:
      case 2:
        move -= (move/15)*5;
        break;
      case 3:
        move = 0;
    }
    if (aArrayList.size() == 0 && i == 0) // assume any armor or load cancels MOVE:
    {
      for (e = classList.iterator(); e.hasNext();)
      {
        PCClass aClass = (PCClass)e.next();
        Integer anInt = new Integer(aClass.getMoveForLevel(aClass.level().intValue()));
        //bonus = Math.max(bonus, anInt.intValue() - move);
        move += anInt.intValue();
      }
    }
    else // assume BONUS:MOVE can be applied to less than label armor
    {
      String loads = "LIGHT MEDIUMHEAVY OVERLOAD";
      for (e = aArrayList.iterator(); e.hasNext();)
      {
        Equipment e1 = (Equipment)e.next();
        if (e1.isHeavy())
          i = 2;
        else if (e1.isMedium())
          i = Math.max(i, 1);
        else if (e1.isLight())
          i = Math.max(i, 0);
        else
          i = 3;
        if (e1.moveString().lastIndexOf(",") > 0)
        {
          if (isMedium)
            move = Math.min(move, Integer.parseInt(e1.moveString().substring(0, e1.moveString().lastIndexOf(','))));
          else
            move = Math.min(move, Integer.parseInt(e1.moveString().substring(e1.moveString().lastIndexOf(',') + 1)));
        }
      }
    }
    for (e = classList.iterator(); e.hasNext();)
    {
      PCClass aClass = (PCClass)e.next();
      String label = "LIGHT";
      if (i < 4)
        bonus = Math.max(bonus, aClass.getBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", aClass.level().intValue()));
      if (i < 3)
        bonus = Math.max(bonus, aClass.getBonusTo("MOVE", "LIGHTMEDIUMHEAVY", aClass.level().intValue()));
      if (i < 2)
        bonus = Math.max(bonus, aClass.getBonusTo("MOVE", "LIGHTMEDIUM", aClass.level().intValue()));
      if (i < 1)
        bonus = Math.max(bonus, aClass.getBonusTo("MOVE", "LIGHT", aClass.level().intValue()));
    }
    if (i < 4)
      bonus = Math.max(bonus, race().bonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD"));
    if (i < 3)
      bonus = Math.max(bonus, race().bonusTo("MOVE", "LIGHTMEDIUMHEAVY"));
    if (i < 2)
      bonus = Math.max(bonus, race().bonusTo("MOVE", "LIGHTMEDIUM"));
    if (i < 1)
      bonus = Math.max(bonus, race().bonusTo("MOVE", "LIGHT"));
    System.out.println("##HERE: i: " + i + " move: " + move + " bonus: " + bonus);
    move += bonus;
    bonus = -999;
    bonus = Math.max(bonus, getEquipmentBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false));
    if (bonus == 0) bonus = -999;
    if (i < 3)
      bonus = Math.max(bonus, getEquipmentBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false));
    if (bonus == 0) bonus = -999;
    if (i < 2)
      bonus = Math.max(bonus, getEquipmentBonusTo("MOVE", "LIGHTMEDIUM", false));
    if (bonus == 0) bonus = -999;
    if (i < 1)
      bonus = Math.max(bonus, getEquipmentBonusTo("MOVE", "LIGHT", false));
    if (bonus != -999)
      move += bonus;
    return move;
  }


	public int totalAC()
	{
		return acMod() + race().startingAC().intValue() + naturalArmorModForSize() + modToFromEquipment("AC");
	}

	public int flatFootedAC()
	{
		int i = totalAC();
		for (Iterator e = specialAbilityList().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.endsWith("Dex bonus to AC)"))
				return i;
		}
		return i - (adjStats(1) / 2 - 5);
	}

	public int acMod()
	{
		int acmod = modForSize();
		int max = modToFromEquipment("MAXDEX");
		int ab = adjStats(1) / 2 - 5;
		if (ab > max)
			ab = max;
		acmod += ab;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			Integer anInt = new Integer(aClass.getACForLevel(aClass.level().intValue()));
			acmod += anInt.intValue();
		}
		acmod += getTotalBonusTo("COMBAT", "AC", true);
		return acmod;
	}

	private int acAbilityMod()
	{
		int acmod = adjStats(1) / 2 - 5;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			acmod += Integer.parseInt(aClass.getACForLevel(aClass.level().intValue()));
		}
		int max = modToFromEquipment("MAXDEX");
		if (acmod > max)
			acmod = max;
		acmod += getTotalBonusTo("COMBAT", "AC", true);
		return acmod;
	}

	private int acSizeMod()
	{
		int acmod = modForSize();
		return acmod;
	}

	private int modFromArmorOnWeaponRolls()
	{
		int bonus = 0;
		for (Iterator e = getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (!isProficientWith(eq))
				bonus += eq.acCheck().intValue();
		}
		if (!hasFeat("Shield Proficiency"))
		{
			ArrayList aArrayList = getEquipmentOfType("Shield", 1);
			if (aArrayList.size() > 0)
			{
				Equipment eq = (Equipment)aArrayList.get(0);
				bonus += eq.acCheck().intValue();
			}
		}
		weaponMod = bonus;
		return bonus;
	}

	private int modToFromEquipment(String typeName)
	{
		int bonus = 0;
		int used = 0;
		int old = 0;
		if (typeName.equals("MAXDEX"))
			bonus = adjStats(1) / 2 - 5;
		int hold = -1;
		int i = 0;
		i = Globals.loadTypeForStrength(adjStats(0), totalWeight());
		if (i == 1 && typeName.equals("ACCHECK"))
		{
			old = -3;
		}
		else if (i == 2 && typeName.equals("ACCHECK"))
		{
			old = -6;
		}
		else if (i == 1 && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 3;
		}
		else if (i == 2 && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 1;
		}
		else if (i == 3 && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 0;
		}
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() == false)
				continue;
			if (typeName.equals("AC"))
				bonus += eq.acMod().intValue();
			else if (typeName.equals("ACCHECK"))
				bonus += eq.acCheck().intValue();
			else if (typeName.equals("SPELLFAILURE"))
				bonus += eq.spellFailure().intValue();
			else if (typeName.equals("MAXDEX"))
			{
				old = eq.maxDex().intValue();
				if (old == 100)
					continue;
				if (used == 0 || bonus > old)
					bonus = old;
				used = 1;
			}
		}
		if (typeName.equals("SPELLFAILURE"))
			bonus += getTotalBonusTo("MISC", "SPELLFAILURE", true);
		if (typeName.equals("ACCHECK"))
			bonus = Math.min(bonus, old);
		return bonus;
	}

	public int initiativeMod()
	{
		int initmod = adjStats(1) / 2 - 5 + race().initMod.intValue() + race().bonusTo("COMBAT", "Initiative");
		initmod += getFeatBonusTo("COMBAT", "Initiative", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			initmod += aClass.initMod();
			initmod += aClass.getBonusTo("COMBAT", "Initiative", aClass.level().intValue());
		}
		int bonus = getEquipmentBonusTo("COMBAT", "Initiative", false);
		initmod += bonus;
		return initmod;
	}

	public String getAttackString(int index)
	{
		return getAttackString(index, 0);
	}

	public String getAttackString(int index, int bonus)
	{
		// 0 = melee; 1 = ranged; 2 = unarmed
		ArrayList ab = new ArrayList(10);
		int total = 0;
		int mod = getTotalBonusTo("TOHIT", "TOHIT", false) + modForSize() + bonus;
		int attacks = 1;
		int subTotal = 0;
		int maxCycle = 0;
		StringBuffer attackString = new StringBuffer();
		for (total = 0; total < 10; total++)
			ab.add(new Integer(0));
		total = 0;
		int nonTotal = 0;
		for (int i = 0; i < classList.size(); i++)
		{
			PCClass aClass = (PCClass)classList.get(i);
			int b = aClass.baseAttackBonus(index);
			int c = aClass.attackCycle(index);
			int d = ((Integer)ab.get(c)).intValue() + b;
			maxCycle = Math.max(maxCycle, d);
			ab.set(c, new Integer(d));
			mod += b;
			subTotal += b;
			if (c != 3)
				nonTotal += b;
		}
		for (int x = 2; x < 10; x++)
			if (((Integer)ab.get(x)).intValue() > ((Integer)ab.get(attacks)).intValue())
				attacks = x;
		total = ((Integer)ab.get(attacks)).intValue();
		if (total == 0)
			attacks = 5;
		if (attacks != 5)
		{
			if (total / attacks < subTotal / 5)
			{
				attacks = 5;
				total = subTotal;
			}
			else
			{
				mod -= nonTotal;
				subTotal -= nonTotal;
			}
		}
		while (attackString.length() == 0 || total > 0 || subTotal > 0)
		{
			if (attackString.length() > 0)
				attackString.append("/");
			if (mod > 0)
				attackString.append("+");
			attackString.append(mod);
			mod -= attacks;
			total -= attacks;
			subTotal -= attacks;
		}
		return attackString.toString();
	}

	public String getUnarmedDamageString(boolean includeCrit, boolean includeStrBonus)
	{
		int i = 2;
		String retString = "1d2";
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			String aString = aClass.getUdamForLevel(aClass.level().intValue(), includeCrit, includeStrBonus);
			StringTokenizer aTok = new StringTokenizer(aString, " dD+-(x)", false);
			Integer sides = new Integer(0);
			if (aTok.countTokens() > 1)
			{
				aTok.nextToken();
				sides = new Integer(aTok.nextToken());
			}
			if (sides.intValue() > i)
			{
				i = sides.intValue();
				retString = aString;
			}
		}
		return retString;
	}

	public void setAlignment(int index)
	{
		// 0 = LG, 3 = NG, 6 = CG
		// 1 = LN, 4 = TN, 7 = CN
		// 2 = LE, 5 = NE, 8 = CE
		if (this.race.canBeAlignment(Integer.toString(index)))
		{
			alignment = index;
		}
		else
		{
			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
	}

	public ArrayList classList()
	{
		return classList;
	}

	public void incrementClassLevel(int mod, PCClass aClass)
	{
		if (mod > 0 && !aClass.isQualified())
			return;
		if (aClass.isMonster() && totalHitDice() >= race.maxHitDiceAdvancement())
		{
			JOptionPane.showMessageDialog(null, "Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement() + ". This characters current number of Monster Hit Dice is " + totalHitDice());
			return;
		}
		PCClass bClass = getClassNamed(aClass.name());
		if (bClass == null && mod > 0)
		{
			bClass = (PCClass)aClass.clone();
			classList.add(bClass);
			languagesList.addAll(bClass.languageAutos());
		}
		if (bClass == null)
			return;
		if (mod > 0)
		{
			for (int i = 0; i < mod; i++)
				bClass.addLevel(false);
		}
		else if (mod < 0)
		{
			for (int i = 0; i < -mod; i++)
				bClass.subLevel();
		}
		if (bClass.getBonusTo("DOMAIN", "NUMBER", bClass.level().intValue()) > 0)
		{
			bClass = getClassNamed("Domain");
			if (bClass != null)
				bClass.setLevel(new Integer(0));
		}
	}

	public void setClassLevel(PCClass aClass)
	{
		if (!aClass.isQualified())
			return;
		PCClass bClass = getClassNamed(aClass.name());
		if (bClass == null)
		{
			bClass = (PCClass)aClass.clone();
			classList.add(bClass);
		}
		bClass.setLevel(aClass.level());
	}

	public Skill addSkill(Skill addSkill)
	{
		Skill aSkill = null;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			aSkill = (Skill)e.next();
			if (aSkill.keyName().equals(addSkill.keyName()))
				return aSkill;
		}
		aSkill = (Skill)addSkill.clone();
		skillList.add(aSkill);
		return aSkill;
	}

	public int skillPoints()
	{
		return skillPoints;
	}

	public void setSkillPoints(int anInt)
	{
		skillPoints = anInt;
	}

	public Float getMaxRank(String skillName, PCClass aClass)
	{
		double i = (double)(totalLevels() + 3.0);
		Skill aSkill = Globals.getSkillNamed(skillName);
		if (aSkill.isExclusive().startsWith("Y"))
		{
			i = 3.0;
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				PCClass bClass = (PCClass)e.next();
				if (aSkill.isClassSkill(bClass))
					i += bClass.level().doubleValue();
			}
			if (i == 3.0)
				i = (totalLevels() + 3.0) / 2.0;
		}
		else if (!aSkill.isClassSkill(classList) && (aSkill.costForPCClass(aClass).intValue() == 1))
			i = (int)(i / 2.0);
		else if (!aSkill.isClassSkill(classList))
			i = i / 2.0;
		return new Float(i);
	}

	public int feats()
	{
		return feats;
	}

	public void setFeats(int anInt)
	{
		feats = anInt;
	}

	/**
	 * Returns the list of names of available feats of given type.
	 */

	public ArrayList getAvailableFeatNames(String featType)
	{
		ArrayList featList = new ArrayList();
		for (int index = 0; index < Globals.featList.size(); index++)
		{
			Feat aFeat = (Feat)Globals.featList.get(index);
			if (aFeat.matchesType(featType) &&
				qualifiesForFeat(aFeat.keyName()) &&
				(!hasFeat(aFeat.keyName()) ||
				aFeat.multiples()))
			{
				featList.add(aFeat.keyName());
			}
		}
		return featList;
	}

	public void adjustHpRolls(int increment)
	{
		if (race.hitDice() != 0)
			race.adjustHpRolls(increment);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			aClass.adjustHpRolls(increment);
		}
	}

	public int hitPoints()
	{
		int total = 0;
		if (race.hitDice() != 0)
			total = race.hitPoints();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			total += aClass.hitPoints();
		}
		total += getTotalBonusTo("HP", "CURRENTMAX", true);
		return total;
	}

	public int getTotalBonusTo(String aType, String aName, boolean aBool)
	{
		int i = 0;
		i = getClassBonusTo(aType, aName);
		i += getEquipmentBonusTo(aType, aName, aBool);
		i += getFeatBonusTo(aType, aName, aBool);
		i += getDomainBonusTo(aType, aName);
		if (deity() != null)
			i += deity().bonusTo(aType, aName);
		i += race().bonusTo(aType, aName);
		if (aType.startsWith("WEAPONPROF="))
			i += getWeaponProfBonusTo(aType.substring(11), aName);

		return i;
	}

	private int getWeaponProfBonusTo(String aType, String aName)
	{
		int bonus = 0;
		if (weaponProfList().contains(aType))
			bonus = Globals.getWeaponProfNamed(aType).bonusTo(aType, aName);
		return bonus;
	}

	public int currentHP()
	{
		return currentHP;
	}

	public int[] stats()
	{
		return stats;
	}

	public void setStats(int intArray[])
	{
		stats = intArray;
	}

	public int adjStats(int i)
	{
		if (race.isNonability(i))
			return 10;
		int x = stats()[i];
		String aName = statNames.substring(i * 3, i * 3 + 3);
		x += getTotalBonusTo("STAT", aName, true);
		if (race() != null)
			x += race().statMod(i);
		if (i == 0)
			x += strModForSize();
		if (i == 1)
			x += dexModForSize();
		if (i == 2)
			x += conModForSize();
		return x;
	}
/*  public int[] adjStats()
  {
    int[] aList = new int[6];
    for (int i = 0; i < 6; i++)
    {
      aList[i] = stats()[i];
      String aName = statNames.substring(i * 3, i * 3 + 3);
      aList[i] += getTotalBonusTo("STAT", aName, true);
      if (race() != null)
        aList[i] += race().statMod(i);
    }
    return aList;
  }
*/
	public Race race()
	{
		return race;
	}

	public void setRace(Race aRace)
	{
		if (race != null)
		{
			for (Iterator e = featList.iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat)e.next();
				if (race().hasFeat(aFeat.name()))
					e.remove();
			}
			race().removeSpecialAbilitiesForRace();
			languagesList.removeAll(race().languageAutos());
			weaponProfList.removeAll(race().weaponProfAutos());
		}
		race = null;
		if (aRace != null)
			race = (Race)aRace.clone();
		if (race != null)
		{
			race().addSpecialAbilitiesForRace();
			StringTokenizer aTok = new StringTokenizer(race().featList, "|", false);
			while (aTok.hasMoreTokens())
			{
				Feat aFeat = Globals.getFeatNamed(aTok.nextToken());
				if (aFeat != null && !this.hasFeat(aFeat.name()))
					modFeat(aFeat.name(), true, aFeat.name().endsWith("Proficiency"));
			}
			getAutoLanguages();
			getAutoWeaponProfs();
			if (!isImporting && !dirtyFlag)
			{
				race.rollHeightWeight();
			}
		}
	}

	private void setRace(String aString)
	{
		for (int i = 0; i < Globals.raceList.size(); i++)
			if (Globals.raceList.get(i).toString().equals(aString))
			{
				setRace((Race)Globals.raceList.get(i));
				break;
			}
	}

	public void changeSpecialAbilitiesForLevel(int level, boolean addIt, ArrayList aArrayList)
	{
		if (Globals.currentPC == null)
			return;
		Integer anInt = new Integer(level);
		for (Iterator e = aArrayList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(aString, ":", false);
			Integer thisInt = new Integer(aTok.nextToken());
			String aList = aTok.nextToken();
			if (anInt.intValue() == thisInt.intValue())
			{
				StringTokenizer aStrTok = new StringTokenizer(aList, ",", false);
				while (aStrTok.hasMoreTokens())
				{
					String thisString = (String)aStrTok.nextToken();
					if (aString.indexOf('%') > -1)
					{
						changeSpecialAbilityNamed(thisString, addIt);
					}
					else
					{
						if (thisString.endsWith("(SPECIALS)"))
						{
							int adjustment = (addIt == true?1:-1);
							int i = thisString.lastIndexOf('(');
							String aName = thisString.substring(0, i).trim();
							String aDesc = new String();
							String bString = "";
							String eString = new String();
							for (Iterator e1 = specialAbilityList().iterator(); e1.hasNext();)
							{
								bString = (String)e1.next();
								if (bString.startsWith(aName))
								{
									aDesc = bString.substring(bString.lastIndexOf('(') + 1, bString.length() - 1);
									eString = bString;
								}
							}
							SpecialAbility sa = Globals.getSpecialAbility(aName, aDesc, adjustment);
							String cString = "1";
							String dString = "2";
							if ((sa != null || addIt == false) && !aDesc.equals(""))
							{
								if (sa != null)
								{
									cString = new String(eString);
									dString = new String(sa.name() + " (" + sa.desc() + ")");
									for (i = 0; i < 10; i++)
									{
										cString = cString.replace((char)('0' + i), ' ');
										dString = dString.replace((char)('0' + i), ' ');
									}
								}
								if (addIt == false || (addIt == true && cString.equals(dString)))
									specialAbilityList().remove(eString);
							}
							if (sa != null && !hasSpecialAbility(sa.name() + " (" + sa.desc() + ")"))
								specialAbilityList().add(sa.name() + " (" + sa.desc() + ")");
						}
						else if (addIt == true)
							specialAbilityList().add(thisString);
						else
							specialAbilityList().remove(thisString);
					}
				}
			}
		}
	}

	private int firstNonDigit(String str, int start)
	{
		final int len = str.length();
		while (start < len && Character.isDigit(str.charAt(start)))
		{
			++start;
		}
		return start;
	}

	public void changeSpecialAbilityNamed(String aString, boolean addIt)
	{
		try
		{
			StringTokenizer aTok = new StringTokenizer(aString, "%|", false);
			String bString = aTok.nextToken();
			String cString = "";
			int sInt = Integer.parseInt(aString.substring(aString.lastIndexOf("|") + 1));
			Iterator e = specialAbilityList().iterator();
			while (e.hasNext())
			{
				cString = (String)e.next();
				if (cString.startsWith(bString))
				{
					final int nonDigit = firstNonDigit(cString, bString.length());
					if (nonDigit > bString.length())
					{
						int anInt =
							Integer.parseInt(cString.substring(bString.length(), nonDigit));
						if (addIt)
							sInt = anInt + sInt;
						else
							sInt = anInt - sInt;
						e.remove();       // remove the current element
						break;
					}
				}
			}
			e = null;
			aTok = new
				StringTokenizer(aString.substring(0, aString.lastIndexOf("|")),
					"%", true);
			StringBuffer newAbility = new StringBuffer();
			while (aTok.hasMoreTokens())
			{
				String nextTok = aTok.nextToken();
				if (nextTok.equals("%"))
					newAbility.append(Integer.toString(sInt));
				else
					newAbility.append(nextTok);
			}
			specialAbilityList().add(newAbility.toString());
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("Trapped number format exception for: '" +
				aString + "' check LST files.");
		}
	}

	public int poolAmount()
	{
		return poolAmount;
	}

	public void setPoolAmount(int anInt)
	{
		poolAmount = anInt;
	}

	public int costPool()
	{
		return costPool;
	}

	public void setCostPool(int i)
	{
		costPool = i;
	}

	public int totalLevels()
	{
		int i = 0;
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (!aClass.isMonster())
				totalLevels += aClass.level().intValue();
		}
		return totalLevels;
	}

	public int totalPCLevels()
	{
		int i = 0;
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.isPC())
				totalLevels += aClass.level().intValue();
		}
		return totalLevels;
	}

	public int totalNPCLevels()
	{
		int i = 0;
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.isNPC())
				totalLevels += aClass.level().intValue();
		}
		return totalLevels;
	}

	public int totalMonsterLevels()
	{
		int i = 0;
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.isMonster())
				totalLevels += aClass.level().intValue();
		}
		return totalLevels;
	}

	public int totalHitDice()
	{
		return race.hitDice() + totalMonsterLevels();
	}

	public int CR()
	{
		int CR = race.CR();
		float hitDieRatio = (float)totalHitDice() / race.hitDice();
		while (hitDieRatio >= 2)
		{
			CR += 2;
			hitDieRatio /= 2;
		}
		if (hitDieRatio >= 1.5)
			CR += 1;
		CR += totalPCLevels();
		int NPCLevels = totalNPCLevels();
		if (NPCLevels == 1)
			CR += 1;
		else if (NPCLevels != 0)
			CR += NPCLevels - 1;
		return CR;
	}


	public String classString(boolean abbreviations)
	{
		StringBuffer classStringBuffer = new StringBuffer();
		Integer anInt = new Integer(0);
		int i, x = 0;
		PCClass aClass = null;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			aClass = (PCClass)(e.next());
			if (aClass.level().intValue() > 0)
			{
				if (x != 0)
					classStringBuffer.append(" ");
				x++;
				if (abbreviations == true)
					classStringBuffer = classStringBuffer.append(aClass.abbrev).append(" ").append(aClass.level().toString());
				else
					classStringBuffer = classStringBuffer.append(aClass.classLevelString());
			}
		}
		return classStringBuffer.toString();
	}

	public int remainingPool()
	{
		return remainingPool;
	}

	public void setRemainingPool(int pool)
	{
		remainingPool = pool;
	}

	public boolean hasFeat(String featName)
	{
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.name().equalsIgnoreCase(featName))
				return true;
		}
		return false;
	}

	public Feat getFeatNamed(String featName)
	{
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.name().equals(featName))
				return aFeat;
		}
		return null;
	}

	public Feat getFeatKeyed(String featName)
	{
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.keyName().equals(featName))
				return aFeat;
		}
		return null;
	}

	public boolean qualifiesForFeat(String featName)
	{
		Feat aFeat = Globals.getFeatNamed(featName);
		if (aFeat != null)
			return qualifiesForFeat(aFeat);
		return false;
	}

	public boolean qualifiesForFeat(Feat aFeat)
	{
		return aFeat.canBeSelectedBy(this);
	}

	public boolean isSpellCaster(int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			PCClass aClass = (PCClass)e1.next();
			if (!aClass.spellType().equals("None") && aClass.level().intValue() >= minLevel)
				return true;
		}
		return false;
	}
	// addIt only makes sense for non-multiple feats
	public int modFeat(String featName, boolean addIt, boolean addAll)
	{
		int i = 0, retVal = addIt?1:0;
		Feat aFeat = getFeatNamed(featName);
		if (addIt == true) // adding feat for first time
		{
			if (aFeat == null)
			{
				aFeat = (Feat)Globals.getFeatNamed(featName).clone();
				featList.add(aFeat);
			}
		}
		if (aFeat == null)
			return retVal;
		int j = (int)(aFeat.associatedList().size() * aFeat.cost()) + feats;
		String choiceType = "";
		if (aFeat.choiceString().lastIndexOf('|') > -1)
			choiceType = aFeat.choiceString().substring(0, aFeat.choiceString().lastIndexOf('|'));
		if (Globals.weaponTypes.contains(choiceType))
		{

			Set weaponProfs = getWeaponProfs(choiceType);
			j += weaponProfs.size() - (int)(aFeat.associatedList().size() * aFeat.cost());
			for (Iterator setIter = weaponProfs.iterator(); setIter.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)setIter.next();
				if (!aFeat.associatedList().contains(aProf.name()))
					aFeat.associatedList().add(aProf.name());
			}

		}
		aFeat.modAdds(addIt);
		if (addAll == false)
		{
			aFeat.modChoices(addIt);
		}
		else
		{
			if (aFeat.choiceString().lastIndexOf("|") > -1 &&
				Globals.weaponTypes.contains(aFeat.choiceString().substring(0, aFeat.choiceString().lastIndexOf("|"))))
				addWeaponProf(aFeat.choiceString().substring(0, aFeat.choiceString().lastIndexOf("|")));
		}
		if (aFeat.multiples() == true && addAll == false)
			retVal = (aFeat.associatedList().size() > 0)? 1:0;
		if (addIt == true && getFeatNamed(featName) == null)
			featList.add(aFeat);
		if (retVal == 0)
		{
			featList.remove(aFeat);
		}
		if (addIt == false && aFeat.multiples() == false)
			j++;
		else if (addIt == true && aFeat.multiples() == false)
			j--;
		else
			j -= (int)(aFeat.associatedList().size() * aFeat.cost());
		if (addAll == false && !aFeat.name().equals("Spell Mastery"))
			setFeats(j);
		return retVal;
	}

	public void modFeatsFromList(String aList, boolean addIt, boolean all)
	{
		if (totalLevels() == 0)
		{
			featList.clear();
			return;
		}
		StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Feat aFeat = getFeatNamed(aString);
			StringTokenizer bTok = null;
			if (aFeat == null)
			{
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
				{
					bTok = new StringTokenizer(aString, "()", true);
					String bString = bTok.nextToken();
					bTok = new StringTokenizer(aString.substring(bString.length() + 1, aString.lastIndexOf(")")), ",", false);
					aString = bString.replace('(', ' ').replace(')', ' ').trim();
				}
				else
				{
					aFeat = (Feat)aFeat.clone();
					featList.add(aFeat);
				}
			}
			if (aFeat == null)
			{
				if (addIt == false)
					return;
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
					return;
				aFeat = (Feat)aFeat.clone();
				featList.add(aFeat);
			}
			if (bTok != null && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if (aString.equals("DEITYWEAPON"))
					{
						WeaponProf wp = null;
						if (deity() != null)
							wp = Globals.getWeaponProfNamed(deity().favoredWeapon());
						if (wp != null)
						{
							if (addIt == true)
								aFeat.associatedList().add(wp.name());
							else
								aFeat.associatedList().remove(wp.name());
						}
					}
					else
					{
						if (addIt == true)
							aFeat.associatedList().add(aString);
						else
							aFeat.associatedList().remove(aString);
					}
				}
				if (aFeat.name().endsWith("Weapon Proficiency"))
				{
					for (Iterator e = aFeat.associatedList().iterator(); e.hasNext();)
					{
						String wprof = (String)e.next();
						WeaponProf wp = Globals.getWeaponProfNamed(wprof);
						if (wp != null)
							addWeaponProf(wprof);
					}
				}
			}
			else
			{
				if (all == false && aFeat.multiples() == false)
				{
					if (addIt == true)
						setFeats(feats() + 1);
					else
						setFeats(feats() - 1);
				}
				modFeat(aString, addIt, all);
			}
		}
	}

	public boolean hasSkill(String skillName)
	{
		return (getSkillNamed(skillName) != null);
	}

	public Skill getSkillNamed(String skillName)
	{
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.name().equalsIgnoreCase(skillName))
				return aSkill;
		}
		return null;
	}

	public Skill getSkillKeyed(String skillName)
	{
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.keyName().equals(skillName))
				return aSkill;
		}
		return null;
	}

	public ArrayList skillList()
	{
		return skillList;
	}

	public int getBonus(int type, boolean addBonuses)
	{
		// 0 = attack bonus; 1 = fort; 2 = reflex; 3 = will; 4 = Monk
		int bonus = 0;
		switch (type)
		{
			case 1:
				bonus = race.FortSave();
				break;
			case 2:
				bonus = race.RefSave();
				break;
			case 3:
				bonus = race.WillSave();
				break;
		}
		if (addBonuses == true)
		{
			switch (type)
			{
				case 0:
					bonus += getFeatBonusTo("TOHIT", "TOHIT", false);
					bonus += race().bonusTo("TOHIT", "TOHIT");
					bonus += getEquipmentBonusTo("TOHIT", "TOHIT", false);
					bonus += modForSize();
					break;
				case 1:
					bonus += getTotalBonusTo("CHECKS", "Fortitude", true);
					break;
				case 2:
					bonus += getTotalBonusTo("CHECKS", "Reflex", true);
					break;
				case 3:
					bonus += getTotalBonusTo("CHECKS", "Willpower", true);
					break;
				case 4:
					bonus += modForSize();
					break;
			}
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			switch (type)
			{
				case 0:
				case 4:
					bonus += aClass.baseAttackBonus(0);
					break;
				case 1:
					bonus += aClass.fortitudeCheckBonus();
					break;
				case 2:
					bonus += aClass.reflexCheckBonus();
					break;
				case 3:
					bonus += aClass.willCheckBonus();
					break;
//      case 4: if (aClass.name().equals("Monk"))
//            bonus+=aClass.baseAttackBonus(0);
//          break;
			}
		}
		return bonus;
	}

	public int baseAttackBonus(int type)
	{
		int bonus = race.BAB();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			bonus += aClass.baseAttackBonus(type);
		}
		return bonus;
	}

	public int getDomainMax()
	{
		int i = getTotalBonusTo("DOMAIN", "NUMBER", false);
		return i;
	}

	public void setDomainNumber(Domain aDomain, int index)
	{
		if (index < 0)
			return;
		int i = getDomainMax();
		if (index >= i)
		{
			JOptionPane.showMessageDialog(null, "This character can only have " + new Integer(i) + " domains.");
			return;
		}
		if (domainList.size() <= index)
			domainList.add(aDomain);
		else
		{
			domainList.set(index, aDomain);
		}
		modDomainClass(true);
		PCClass aClass = getClassNamed("Domain");
		if (aClass != null)
			aClass.setLevel(new Integer(0));
	}

	public ArrayList domainList()
	{
		return domainList;
	}

	public void addDomainNamed(String domainName)
	{
		Domain aDomain = Globals.getDomainNamed(domainName);
		if (aDomain != null)
			domainList.add(aDomain);
	}

	public Domain getDomainNamed(String domainName)
	{
		for (int i = 0; i < domainList.size(); i++)
		{
			Domain aDomain = (Domain)domainList.get(i);
			if (aDomain.name().equals(domainName))
				return aDomain;
		}
		return null;
	}

	public void addDomainKeyed(String domainName)
	{
		Domain aDomain = Globals.getDomainKeyed(domainName);
		if (aDomain != null)
			domainList.add(aDomain);
	}

	public void modDomainClass(boolean addIt)
	{
		boolean flag = false;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.name().startsWith("Domain"))
			{
				if (addIt == false)
					e.remove();
				flag = true;
				break;
			}
		}
		if (addIt == true && flag == false)
		{
			PCClass aClass = Globals.getClassNamed("Domain");
			if (aClass != null)
			{
				aClass = (PCClass)aClass.clone();
				aClass.setLevel(new Integer(0));
				classList.add(aClass);
			}
		}
	}

	public String domainClassName()
	{
		StringBuffer aString = new StringBuffer("Domain (");
		for (int i = 0; i < domainList.size(); i++)
		{
			if (i > 0)
				aString.append(",");
			aString.append(((Domain)domainList.get(i)).name());
		}
		aString.append(")");
		return aString.toString();
	}

	public Deity deity()
	{
		return deity;
	}

	private void setDeity(String aString)
	{
		for (int i = 0; i < Globals.deityList.size(); i++)
			if (Globals.deityList.get(i).toString().equals(aString))
				setDeity((Deity)Globals.deityList.get(i));
	}

	public boolean canSelectDeity(Deity aDeity)
	{
		if (aDeity == null)
		{
			deity = null;
			return false;
		}
		return aDeity.canBeSelectedBy(classList,
			alignment,
			race.name(),
			gender);
	}

	public boolean setDeity(Deity aDeity)
	{
		if (!canSelectDeity(aDeity))
			return false;
		changeDeity(false);
		deity = aDeity;
		changeDeity(true);
		return true;
	}

	private void changeDeity(boolean addIt)
	{
		if (deity != null && deity().getSpecialAbility().length() > 0)
		{
			ArrayList aArrayList = new ArrayList();
			StringTokenizer aTok = new StringTokenizer(deity().getSpecialAbility(), "|", false);
			while (aTok.hasMoreTokens())
				aArrayList.add(aTok.nextToken());
			for (int i = 0; i < 20; i++)
				changeSpecialAbilitiesForLevel(i, false, aArrayList);
		}
	}

	public int age()
	{
		return age;
	}

	public void setAge(int i)
	{
		age = i;
	}

	public int height()
	{
		return height;
	}

	public void setHeight(int i)
	{
		height = i;
	}

	public int weight()
	{
		return weight;
	}

	public void setWeight(int i)
	{
		weight = i;
	}

	public String gender()
	{
		return gender;
	}

	public void setGender(String aString)
	{
		gender = aString;
	}

	public ArrayList specialAbilityList()
	{
		return specialAbilityList;
	}

	public boolean hasSpecialAbility(String abilityName)
	{
		for (Iterator e = specialAbilityList().iterator(); e.hasNext();)
			if (e.next().toString().equalsIgnoreCase(abilityName))
				return true;
		return false;
	}

	public String handed()
	{
		return handed;
	}

	public void setHanded(String aString)
	{
		handed = aString;
	}

	public SortedSet languagesList()
	{
		return languagesList;
	}

	public SortedSet getAutoLanguages()
	{
		SortedSet autoLangs = new TreeSet();
		autoLangs.addAll(race().languageAutos());
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			autoLangs.addAll(aClass.languageAutos());
		}
		languagesList.addAll(autoLangs);
		return autoLangs;
	}

	public SortedSet getBonusLanguages()
	{
		SortedSet bonusLangs = new TreeSet();
		String aLang = null;
		Iterator e = null;
		for (e = race().languageBonus().iterator(); e.hasNext();)
		{
			aLang = (String)e.next();
			if (aLang.equals("ALL"))
			{
				bonusLangs.addAll(Globals.languageList);
			}
			else
			{
				if (Globals.languageList.contains(aLang))
				{
					bonusLangs.add(aLang);
				}
			}
		}

		for (e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.languageBonus().iterator(); e1.hasNext();)
			{
				aLang = (String)e1.next();
				if (Globals.languageList.contains(aLang))
				{
					bonusLangs.add(aLang);
				}
			}
		}
		bonusLangs.removeAll(languagesList);
		return bonusLangs;
	}

	public void addLanguage(String aString, boolean filter)
	{
		if (!filter || Globals.languageList.contains(aString))
		{
			languagesList.add(aString);
		}
	}

	/**
	 * Return the total number of languages that the player character can
	 * know.  This includes extra languages from intelligence, speak
	 * language skill, and race.
	 */
	public int languageNum()
	{
		int i = adjStats(3) / 2 - 5;
		Skill speakLang = getSkillNamed("Speak Language");
		Race pcRace = race();
		if (i < 0)
			i = 0;
		if (speakLang != null)
		{
			i += speakLang.rank().intValue();
		}
		if (pcRace != null)
		{
			i += pcRace.langNum();
		}
		return i;
	}

	public TreeSet weaponProfList()
	{
		return weaponProfList;
	}

	public TreeSet getWeaponProfs(String type)
	{
		TreeSet result = new TreeSet();
		SortedSet alreadySeen = new TreeSet();
		for (Iterator e = race().weaponProfs.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			String typeString = aTok.nextToken();
			if (typeString.equals(type))
			{
				String wpString = aTok.nextToken();
				WeaponProf aProf = Globals.getWeaponProfNamed(wpString);
				if (aProf != null)
				{
					if (weaponProfList().contains(aProf.name()))
					{
						result.add(aProf);
					}
					else
					{
						alreadySeen.add(aProf);
					}
				}
			}
		}
		for (Iterator e = Globals.weaponProfList.iterator(); e.hasNext();)
		{
			WeaponProf aProf = (WeaponProf)e.next();
			if (aProf.type().equalsIgnoreCase(type) &&
				!alreadySeen.contains(aProf) &&
				weaponProfList().contains(aProf.name()))
				result.add(aProf);
		}
		return result;
	}

	public SortedSet getAutoWeaponProfs()
	{
		SortedSet results = new TreeSet();
		Iterator e = null;
		for (e = race().weaponProfAutos().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (Globals.weaponTypes.contains(aString))
			{
				for (Iterator e1 = Globals.weaponProfList.iterator();
						 e1.hasNext();)
				{
					WeaponProf aProf = (WeaponProf)e1.next();
					if (aProf.type().equalsIgnoreCase(aString))
					{
						results.add(aProf.name());
						addWeaponProf(aProf.name());
					}
				}
			}
			else
			{
				results.add(aString);
				addWeaponProf(aString);
			}
		}

		for (e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.weaponProfAutos().iterator(); e1.hasNext();)
			{
				String aString = (String)e1.next();
				StringTokenizer aTok = new StringTokenizer(aString, ",", false);
				String eString = "";
				while (aTok.hasMoreTokens())
					eString = aTok.nextToken();
				if (eString.equals("M"))
					aString = aString.substring(0, aString.lastIndexOf(','));
				if (eString.equals(aString) || (eString.equals("M") && race != null && sizeInt() >= 4))
				{
					if (Globals.weaponTypes.contains(aString))
					{
						for (Iterator e2 = Globals.weaponProfList.iterator();
								 e2.hasNext();)
						{
							WeaponProf aProf = (WeaponProf)e2.next();
							if (aProf.type().equalsIgnoreCase(aString))
							{
								results.add(aProf.name());
								addWeaponProf(aProf.name());
							}
						}
					}
					else
					{
						results.add(aString);
						addWeaponProf(aString);
					}
				}
			}
		}
		return results;
	}

	public SortedSet getBonusWeaponProfs()
	{
		SortedSet results = new TreeSet(race().weaponProfBonus());
		bonusWeaponChoices = 0;
		if (results.size() > 0)
			bonusWeaponChoices = 1;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (results.addAll(aClass.weaponProfBonus()))
				bonusWeaponChoices++;
		}
		return results;
	}

	public void addWeaponProf(String aString)
	{
		if (Globals.weaponTypes.contains(aString))
		{
			for (Iterator e = Globals.weaponProfList.iterator(); e.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)e.next();
				if (aProf.type().equalsIgnoreCase(aString))
					addWeaponProf(aProf.name());
			}
			return;
		}
		weaponProfList().add(aString);
	}

	public int weaponProfNum()
	{
		int i = 0;
		Iterator e = null;
		Iterator e1 = null;
		SortedSet currentProf = (SortedSet)weaponProfList().clone();
		SortedSet autoProfs = getAutoWeaponProfs();
		ArrayList raceProfs = race().weaponProfBonus();
		if (raceProfs.size() > 0)
		{
			for (e = raceProfs.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				if (currentProf.contains(aString))
				{
					currentProf.remove(aString);
					if (!autoProfs.contains(aString))
					{
						i--;
						break;
					}
				}
			}
			i++;
		}
		for (Iterator e2 = classList.iterator(); e2.hasNext();)
		{
			PCClass aClass = (PCClass)e2.next();
			raceProfs = aClass.weaponProfBonus();
			for (e = raceProfs.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				if (currentProf.contains(aString))
				{
					currentProf.remove(aString);
					i--;
					break;
				}
			}
			if (raceProfs.size() > 0)
				i++;
		}
		return i;
	}

	public ArrayList aggregateSpellList(String aType, String school, String subschool, int minLevel)
	{
		return aggregateSpellList(aType, school, subschool, minLevel, minLevel);
	}

	public ArrayList aggregateSpellList(String aType, String school, String subschool, int minLevel, int maxLevel)
	{
		ArrayList aArrayList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			String cName = aClass.keyName();
			if (aClass.castAs.length() > 0)
				cName = aClass.castAs;
			if (Globals.debugMode)
				System.out.println("Cast As:" + cName);
			if (aType.equals("Any") || aType.equals(aClass.spellType()))
				for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
				{
					Spell aSpell = (Spell)e1.next();
					if (((school.length() == 0 || school.equals(aSpell.school())) ||
						(subschool.length() == 0 || subschool.equals(aSpell.subschool()))) &&
						aSpell.levelForClass(cName) >= minLevel && aSpell.levelForClass(cName) <= maxLevel)
					{
						if (Globals.debugMode)
							System.out.println(school + "==" + aSpell.school() + " " + minLevel + "==" + aSpell.levelForClass(cName));
						aArrayList.add(aSpell);
					}
				}
		}
		return aArrayList;
	}

	public String eyeColor()
	{
		return eyeColor;
	}

	public void setEyeColor(String aString)
	{
		eyeColor = aString;
	}

	public String skinColor()
	{
		return skinColor;
	}

	public void setSkinColor(String aString)
	{
		skinColor = aString;
	}

	public String hairColor()
	{
		return hairColor;
	}

	public void setHairColor(String aString)
	{
		hairColor = aString;
	}

	public String hairLength()
	{
		return hairLength;
	}

	public void setHairLength(String aString)
	{
		hairLength = aString;
	}

	public String speechTendency()
	{
		return speechTendency;
	}

	public void setSpeechTendency(String aString)
	{
		speechTendency = aString;
	}

	public String phobias()
	{
		return phobias;
	}

	public void setPhobias(String aString)
	{
		phobias = aString;
	}

	public String interests()
	{
		return interests;
	}

	public void setInterests(String aString)
	{
		interests = aString;
	}

	public String catchPhrase()
	{
		return catchPhrase;
	}

	public void setCatchPhrase(String aString)
	{
		catchPhrase = aString;
	}

	public String trait1()
	{
		return trait1;
	}

	public void setTrait1(String aString)
	{
		trait1 = aString;
	}

	public String trait2()
	{
		return trait2;
	}

	public void setTrait2(String aString)
	{
		trait2 = aString;
	}

	public String residence()
	{
		return residence;
	}

	public void setResidence(String aString)
	{
		residence = aString;
	}

	public String location()
	{
		return location;
	}

	public void setLocation(String aString)
	{
		location = aString;
	}

	public Float totalWeight()
	{
		float totalWeight = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.carried.compareTo(new Float(0)) > 0)
				totalWeight += eq.weight().floatValue() * eq.carried.floatValue();
		}
		return new Float(totalWeight);
	}

	public TreeMap equipmentList()
	{
		return equipmentList;
	}

	public Equipment getEquipmentNamed(String aString)
	{
		Equipment match = (Equipment)equipmentList.get(aString);
		if ((match != null) && aString.equals(match.name()))
			return match;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.name().equals(aString))
				return eq;
		}
		return null;
	}

	public boolean isProficientWith(Equipment eq)
	{
		if (eq.isWeapon())
		{
			WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
			return (wp != null && weaponProfList().contains(wp.name()));
		}
		else if (eq.isArmor())
		{
			String aString = eq.typeString();
			StringTokenizer aTok = new StringTokenizer(aString, ".", false);
			while (aTok.hasMoreTokens())
				if (aTok.nextToken().equals("ARMOR"))
					break;
			if (aTok.hasMoreTokens())
			{
				String aName = aTok.nextToken().toLowerCase();
				boolean flag = hasFeat("Armor Proficiency (" + aName + ")");
				return flag;
			}
		}
		else if (eq.isShield())
		{
			return hasFeat("Shield Proficiency");
		}
		return false;
	}

	// status: 1 (equipped) 2 (not equipped) 3 (none)
	public ArrayList getEquipmentOfType(String typeName, int status)
	{
		ArrayList aArrayList = new ArrayList();
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.typeStringContains(typeName) && (status == 3 ||
				(status == 2 && !eq.isEquipped()) ||
				(status == 1 && eq.isEquipped())))
				aArrayList.add(eq);
		}
		return aArrayList;
	}

	public int handsFull()
	{
		ArrayList aArrayList = getEquipmentOfType("Weapon", 1);
		ArrayList bArrayList = getEquipmentOfType("Shield", 1);
		Iterator e = null;
		int hands = 0;
		WeaponProf wp = null;
		Equipment eq = null;
		for (e = aArrayList.iterator(); e.hasNext();)
		{
			eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				wp = Globals.getWeaponProfNamed(eq.profName());
				if (wp == null)
				{
					JOptionPane.showMessageDialog(null, "No entry in weapons.lst for " + eq.profName() + ". Weapons must be in that file to equip them.");
					if (Globals.debugMode)
					{
						System.out.println("Globals: " + Globals.weaponProfList);
						System.out.println("Prof: " + eq.profName());
					}
					hands += 3;
				}
				else
				{
/*					if (wp.isOneHanded())
					{
						hands += Math.max(1, eq.hands);
					}
					if (wp.isTwoHanded())
					{
						hands += Math.max(2, eq.hands);
					}
                  */
					switch (eq.getHand())
					{
						case 0:
							break;
						case 1:
							hands += Math.max(1, eq.hands);
							break;
						case 2:
							hands += Math.max(1, eq.hands);
							break;
						case 3:
							hands += Math.max(2, eq.hands);
							break;
						case 4:
							hands += Math.max(2, eq.numberEquipped());
							break;
					}
				}
			}
		}
		for (e = bArrayList.iterator(); e.hasNext();)
		{
			eq = (Equipment)e.next();
			if (eq.isEquipped())
				hands += eq.hands;
		}
		return hands;
	}

	public boolean canEquip(String typeName)
	{
		ArrayList aArrayList = getEquipmentOfType(typeName, 1);
		if (typeName.equals("RING"))
			return aArrayList.size() < race.hands();
		else if (typeName.equals("Weapon") || typeName.equals("Shield"))
		{
			int hands = handsFull();
			if (hands > race.hands())
			{
				JOptionPane.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.");
				return false;
			}
			return true;
		}
		else
			return aArrayList.size() == 0;
	}

	public int handsTakenExceptFor(Equipment eq)
	{
		int hands = 3; //0=Primary;1=Off-Hand;2=Both;3=Neither
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq1 = (Equipment)mapIter.next();
			if (!eq1.equals(eq))
			{
				if (eq1.getHand() == Equipment.PRIMARY_HAND)
				{
					if (hands == 3)
						hands = 0;
					else if (hands == 1)
						hands = 2;
				}
				else if (eq1.getHand() == Equipment.SECONDARY_HAND)
				{
					if (hands == 3)
						hands = 1;
					else if (hands == 0)
						hands = 2;
				}
				else if (eq1.getHand() == Equipment.BOTH_HANDS)
					hands = 2;
				else if (eq1.getHand() == Equipment.TWOWEAPON_HANDS)
				{
					WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
					if (wp != null)
						hands = 2 * wp.hands();
					else
						hands = 2;
				}
			}
		}
		return hands;
	}

	public Float gold()
	{
		return gold;
	}

	public void setGold(String aString)
	{
		gold = new Float(aString);
	}

	public void adjustGold(float delta)
	{
		gold = new Float(gold.floatValue() + delta);
	}

	public String bio()
	{
		return bio;
	}

	public void setBio(String aString)
	{
		bio = aString;
	}

	public String description()
	{
		return description;
	}

	public void setDescription(String aString)
	{
		description = aString;
	}

	public void determinePrimaryOffWeapon()
	{
		int i = 0;
		primaryWeapon = null;
		secondaryWeapon = new Equipment[race.hands() - 1];
		int x = 0;
		int y = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() == false)
				continue;
			if (!eq.typeStringContains("Weapon"))
				continue;
			if (eq.getHand() == Equipment.PRIMARY_HAND
				|| (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon == null)
				|| eq.getHand() == Equipment.TWOWEAPON_HANDS)
				primaryWeapon = eq;
			else if (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon != null)
				secondaryWeapon[x++] = eq;
			if (eq.getHand() == Equipment.SECONDARY_HAND)
				secondaryWeapon[x++] = eq;
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
				for (y = 0; y < eq.numberEquipped() - 1; y++)
					secondaryWeapon[x++] = eq;
		}
	}

	private boolean isPrimaryWeapon(Equipment eq)
	{
		return (eq != null && eq == primaryWeapon || eq.getHand() == Equipment.PRIMARY_HAND);
	}

	private boolean isSecondaryWeapon(Equipment eq)
	{
		if (eq == null || eq.getHand() != Equipment.SECONDARY_HAND)
			return false;
		for (int x = 0; x < secondaryWeapon.length; x++)
			if (eq == secondaryWeapon[x])
				return true;
		return false;
	}

	public Integer experience()
	{
		return experience;
	}

	public void setExperience(Integer anInt)
	{
		experience = new Integer(anInt.toString());
	}

	public ArrayList miscList()
	{
		return miscList;
	}

	private ArrayList getLineForMiscList(int index)
	{
		ArrayList aArrayList = new ArrayList();
		StringTokenizer aTok = new StringTokenizer((String)miscList().get(index), "\r\n", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	public int getClassBonusTo(String type, String aName)
	{
		int bonus = 0;
		int[] statBonus = new int[6];
		for (int i = 0; i < 6; i++)
			statBonus[i] = 0;
		int x = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aName.equals("AC"))
			{
				int j = aClass.bonusBasedOnStat(type, aName, aClass.level().intValue());
				if (j >= 0 && j < 6)
					statBonus[j] = Math.max(statBonus[j], aClass.getBonusTo(type, aName, aClass.level().intValue()));
				else
					bonus += aClass.getBonusTo(type, aName, aClass.level().intValue());
			}
			else
				bonus += aClass.getBonusTo(type, aName, aClass.level().intValue());
		}
		if (aName.equals("AC"))
		{
			for (int i = 0; i < 6; i++)
				bonus += statBonus[i];
			ArrayList aArrayList = getEquipmentOfType("Armor", 1);
			for (int i = aArrayList.size() - 1; i >= 0; i--)
			{
				Equipment eq = (Equipment)aArrayList.get(i);
				if (!eq.typeStringContains("SUIT"))
					break;
				aArrayList.remove(i);
			}
			if (aArrayList.size() > 0)
				bonus = 0;
		}
		return bonus;
	}

	public int getDomainBonusTo(String type, String aName)
	{
		int bonus = 0;
		for (Iterator e = domainList.iterator(); e.hasNext();)
		{
			bonus += ((Domain)e.next()).bonusTo(type, aName);
		}
		return bonus;
	}

	public int getEquipmentBonusTo(String type, String aName, boolean stacks)
	{
		int bonus = 0;
		if (stacks == false)
			bonus = -999;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped())
			{
				if (stacks == true)
					bonus += eq.bonusTo(type, aName);
				else
					bonus = Math.max(bonus, eq.bonusTo(type, aName));
			}
		}
		if (bonus == -999)
			bonus = 0;
		return bonus;
	}

	public int getFeatBonusTo(String type, String aName, boolean subSearch)
	{
		int i = 0;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			int j = aFeat.bonusTo(type, aName);
			if (j == 0)
				j = aFeat.bonusTo(type, "LIST");
			int k = Math.max(1, (int)(aFeat.associatedList().size() * aFeat.cost()));
			if (subSearch == true && aFeat.associatedList().size() > 0)
			{
				k = 0;
				for (Iterator f = aFeat.associatedList().iterator(); f.hasNext();)
				{
					if (f.next().toString().equals(name))
						k++;
				}
			}
			if (k == 0 && j != 0)
				i += j;
			else
				i += j * k;
		}
		return i;
	}

	public Integer defense()
	{
		int i = 0;
		int y = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isArmor())
				return new Integer(totalAC());
		}
		i = race().startingAC().intValue() + naturalArmorModForSize() + (adjStats(1) / 2) - 5;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.defense(y).intValue();
			y++;
		}
		i += getFeatBonusTo("CLASS", "DEFENSE", true) + race().bonusTo("CLASS", "DEFENSE");
		return new Integer(i);
	}

	public Integer woundPoints()
	{
		int i = adjStats(2);
		i += getTotalBonusTo("HP", "WOUNDPOINTS", false);
		return new Integer(i);
	}

	public Integer reputation()
	{
		int i = race().bonusTo("CLASS", "REPUTATION");
		i += getEquipmentBonusTo("CLASS", "REPUTATION", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.getBonusTo("CLASS", "REPUTATION", aClass.level().intValue());
			String aString = aClass.reputationString;
			int k = Integer.parseInt(aString);
			switch (k)
			{
/*Best*/  case 0:
					i += 3 + aClass.level().intValue() / 2;
					break;
/*MHigh*/  case 1:
					i += 1 + aClass.level().intValue() / 2;
					break;
/*MLow*/  case 2:
					i += aClass.level().intValue() / 2;
					break;
/*Low*/    case 3:
					i += aClass.level().intValue() / 3;
					break;
/*NPCH*/  case 4:
					i += (aClass.level().intValue() + 1) / 3;
					break;
/*NPCL*/  case 5:
					i += aClass.level().intValue() / 4;
					break;
/*PHigh*/  case 6:
					if (aClass.level().intValue() % 3 != 0) i++;
					break;
/*PLow*/  case 7:
					i += aClass.level().intValue() / 2;
					break;
			}
		}
		int y = totalLevels();
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.levelsPerRepIncrease.intValue() != 0)
				i += y / aFeat.levelsPerRepIncrease.intValue();
		}
		i += getFeatBonusTo("CLASS", "REPUTATION", true);
		return new Integer(i);
	}

	public ArrayList spellBooks()
	{
		return spellBooks;
	}

	/** return value indicates if book was actually added or not */
	public boolean addSpellBook(String aName)
	{
		if (aName.length() > 0 && !spellBooks.contains(aName))
		{
			spellBooks.add(aName);
			return true;
		}
		return false;
	}

	/** return value indicates whether or not a book was actually removed */
	public boolean delSpellBook(String aName)
	{
		if (aName.length() > 0 && !aName.equals("Known Spells") && spellBooks.contains(aName))
		{
			spellBooks.remove(aName);
			for (Iterator i = classList.iterator(); i.hasNext();)
			{
				PCClass aClass = (PCClass)i.next();
				for (Iterator ii = aClass.spellList().iterator(); ii.hasNext();)
				{
					Spell aSpell = (Spell)ii.next();
					aSpell.removeFromSpellBook(aName);
				}
			}
			return true;
		}
		return false;
	}

	public String addSpell(String className, int spellLevel, ArrayList aFeatList, String spellName, String bookName)
	{
		PCClass aClass = null;
		Spell aSpell = null;
		if (spellName == null || spellName.length() == 0)
			return "Invalid spell name.";
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (className != null)
		{
			aClass = (PCClass)getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null)
			return "No class named " + className;
		if (bookName.equals("Known Spells"))
			aSpell = (Spell)Globals.getSpellNamed(spellName);
		else
			aSpell = aClass.getSpellNamed(spellName);
		if (aSpell == null)
			return "Could not find " + spellName + " for " + className;
		if (aFeatList != null)
			for (Iterator i = aFeatList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				spellLevel += aFeat.addSpellLevel();
			}
		int known = aClass.getKnownForLevel(aClass.level().intValue(), spellLevel);
		int cast = aClass.getCastForLevel(aClass.level().intValue(), spellLevel, bookName);
		boolean isDivine = aClass.spellType().equals("Divine");
		boolean isDefault = bookName.equals("Known Spells");
		if (isDivine && isDefault)
			return "The Known Spells spellbook for this class has each spell in it once.";
		if (aClass.memorizeSpells && !isDefault &&
			aClass.memorizedSpellForLevelBook(spellLevel, bookName) >= cast)
			return "You cannot memorize any additional spells in this list.";
		if (!aSpell.isInSpecialty(aClass.specialtyList) && (aClass.prohibitedStringContains(aSpell.school()) || aClass.prohibitedStringContains(aSpell.descriptorList)))
		{
			return spellName + " is prohibited.";
		}
		boolean addIt = true;
		Spell bSpell = aClass.getSpellNamed(aSpell.name());
		if (bSpell != null)
		{
			if (isDefault)
				return "The Known Spells spellbook contains all spells of this level that you know. You " +
					"cannot place spells in multiple times.";
			if (aClass.prohibitedStringContains(bSpell.school()))
				return "This spell is prohibited.";
			bSpell.addToSpellBook(bookName, !isDefault);
			bSpell.selectSpellBook(bookName);
			addIt = false;
		}
		if (addIt == true)
		{
			if (aClass.memorizedSpellForLevelBook(spellLevel, bookName) < known ||
				(known == 0 && cast > 0) ||
				(aClass.memorizeSpells && isDefault))
			{
				if (aClass.getKnownSpellsFromSpecialty() > 0)
				{
					if (!aSpell.isInSpecialty(aClass.specialtyList))
					{
						int num = aClass.getSpellsInSpecialtyForLevel(spellLevel);
						if (num < aClass.getKnownSpellsFromSpecialty())
							return "First " + aClass.getKnownSpellsFromSpecialty() + " spells known must come from specialty (" + aClass.specialtyList.toString() + ")";
					}
				}
				Spell newSpell = (Spell)aSpell.clone();
				if (aClass.castAs.length() > 0)
					className = aClass.castAs;
				String aString = className + "," + spellLevel;
				newSpell.setClassLevels(aString);
				aClass.spellList().add(newSpell);
				newSpell.addToSpellBook(bookName, !isDefault);
				newSpell.selectSpellBook(bookName);
			}
			else
				return "You cannot memorize any more spells in this book for this level.";
		}
		return "";
	}

	public String delSpell(String className, int spellLevel, ArrayList aFeatList, String spellName, String bookName)
	{
		PCClass aClass = null;
		Spell aSpell = null;
		if (spellName == null || spellName.length() == 0)
			return "Invalid spell name.";
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (className != null)
		{
			aClass = (PCClass)getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")));
		}
		if (aClass == null)
			return "No class named" + className;
		aSpell = aClass.getSpellNamed(spellName);
		if (aSpell == null)
			return "Could not find " + spellName + " for " + className;
		if (aFeatList != null)
			for (Iterator i = aFeatList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				spellLevel += aFeat.addSpellLevel();
			}

		aSpell.removeFromSpellBook(bookName);
		if (aSpell.spellBooks().size() == 0)
			aClass.spellList().remove(aSpell);
		return "";
	}

	public ArrayList vFeatList()
	{
		ArrayList aArrayList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.vFeatList().iterator(); e1.hasNext();)
			{
				StringTokenizer aTok = new StringTokenizer((String)e1.next(), ":", false);
				Integer level = new Integer(aTok.nextToken());
				if (level.intValue() <= aClass.level().intValue())
				{
					aTok = new StringTokenizer(aTok.nextToken(), "|", false);
					while (aTok.hasMoreTokens())
					{
						Feat aFeat = Globals.getFeatNamed(aTok.nextToken());
						if (aFeat != null)
							aArrayList.add(aFeat);
					}
				}
			}
		}
		StringTokenizer aTok = new StringTokenizer(race().vFeatList, "|", false);
		while (aTok.hasMoreTokens())
		{
			Feat aFeat = Globals.getFeatNamed(aTok.nextToken());
			if (aFeat != null)
				aArrayList.add(aFeat);
		}
		return aArrayList;
	}

	public ArrayList aggregateFeatList()
	{
		ArrayList aArrayList = (ArrayList)featList.clone();
		for (Iterator e = vFeatList().iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (!aArrayList.contains(aFeat))
				aArrayList.add(aFeat);
		}
		return aArrayList;
	}

	public boolean save(BufferedWriter output)
	{
		FileAccess fa = new FileAccess();
		saveNameLine(fa, output);
		saveStatsLine(fa, output);
		saveClassesLine(fa, output);
		saveFeatsLine(fa, output);
		saveSkillsLine(fa, output);
		saveDeityLine(fa, output);
		saveRaceLine(fa, output);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			saveClassLine(e, fa, output);
		}
		saveLanguagesLine(fa, output);
		saveWeaponProfsLine(fa, output);
		saveUnusedPointsLine(fa, output);
		saveMiscLine(fa, output);
		saveEquipmentLine(fa, output);
		saveGoldBioDescriptionLine(fa, output);
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			saveClassesSkillLine(e1, fa, output);
		}
		saveExperienceAndMiscListLine(fa, output);
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			saveClassSpecialtyAndSaveLine(e1, fa, output);
		}
		setDirty(false);
		return true;
	}

	public boolean load(BufferedReader input)
	{
		FileAccess fa = new FileAccess();
		isImporting = true;
		String aLine = "";
		try
		{
			StringTokenizer aTok = null;
			loadNameLine(fa, input);
			loadStatsLine(fa, input);
			loadClassesLine(fa, input);
			loadFeatsLine(fa, input);
			loadSkillsLine(fa, input);
			//Note, the following order is neccessary, for historical reasons...
			String deityLine = fa.readLine(input);
			loadRaceLine(fa, input);
			handleDeityLine(fa, input, deityLine);
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				loadClassLine(e, fa, input);
			}
			loadLanguagesLine(fa, input);
			loadWeaponProfLine(fa, input);
			loadUnusedPointsLine(fa, input);
			loadMiscLine(fa, input);
			loadEquipmentLine(fa, input);
			loadGoldBioDescriptionLine(fa, input);
			for (Iterator e = classList.iterator(); e.hasNext(); e.next())
			{
				loadClassesSkillLine(fa, input);
			}
			loadExperienceAndMiscLine(fa, input);
			loadClassSpecialtyAndSaveLines(fa, input);
		}
		catch (Exception es)
		{
			es.printStackTrace();
			JOptionPane.showMessageDialog(null, "Problem with line:" + lastLineParsed);
		}
		isImporting = false;
		return true;
	}


	private void saveClassSpecialtyAndSaveLine(Iterator e1, FileAccess fa, BufferedWriter output)
	{
		PCClass aClass = (PCClass)e1.next();
		fa.write(output, aClass.keyName() + ":");
		for (Iterator e2 = aClass.specialtyList.iterator(); e2.hasNext();)
			fa.write(output, "SPECIAL" + (String)e2.next() + ":");
		for (int i = 0; i < aClass.saveList.size(); i++)
		{
			String bString = (String)aClass.saveList.get(i);
			fa.write(output, bString + ":");
		}
		fa.newLine(output);
	}

	private String escapeColons( String in)
	{
		String retStr = new String("");	
		for( int j=0; j<in.length();j++)
		{
			if(in.charAt(j) != ':') retStr +=  in.charAt(j);
			else retStr +=  "\\" + in.charAt(j);
		}
		return retStr;
	}

	private void saveExperienceAndMiscListLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, experience().toString() + ":");
		for (int i = 0; i < 3; i++)
		{
			fa.write(output, escapeColons(miscList().get(i).toString()) + " :");
		}
		fa.newLine(output);
	}

	private void saveClassesSkillLine(Iterator e1, FileAccess fa, BufferedWriter output)
	{
		PCClass aClass = (PCClass)e1.next();
		fa.write(output, aClass.keyName() + ":");
		for (Iterator e = aClass.skillList().iterator(); e.hasNext();)
		{
			fa.write(output, e.next() + " :");
		}
		fa.newLine(output);
	}

	private void saveGoldBioDescriptionLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, gold().toString() + ":" + escapeColons(bio) + " :" + escapeColons(description) + " :");
		fa.newLine(output);
	}

	private void saveEquipmentLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = equipmentList.values().iterator();
				 setIter.hasNext();)
		{
			Equipment eq = (Equipment)setIter.next();
			fa.write(output, eq.keyName() + " :" + eq.qty().toString() + ":");
			if (eq.isEquipped())
				fa.write(output, "Y:");
			else
				fa.write(output, "N:");
			if (eq.carried.compareTo(new Float(0)) > 0)
				fa.write(output, eq.carried + ":");
			else
				fa.write(output, "N:");
			fa.write(output, Equipment.getHandName(eq.getHand()) + ":");
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
				fa.write(output, eq.numberEquipped() + ":");
		}
		fa.newLine(output);
	}

	private void saveMiscLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, eyeColor + " :" + skinColor + " :" + hairColor + " :" +
			hairLength + " :" + speechTendency + " :" + phobias + " :" +
			interests + " :" + trait1 + " :" + trait2 + " :" + catchPhrase +
			" :" + location + " :" + residence + " :");
		fa.newLine(output);
	}

	private void saveUnusedPointsLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, String.valueOf(this.skillPoints()) + ":");
		fa.write(output, String.valueOf(this.feats()));
		fa.newLine(output);
	}

	private void saveWeaponProfsLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = weaponProfList().iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next() + ":");
		}
		fa.newLine(output);
	}

	private void saveLanguagesLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = languagesList().iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next() + ":");
		}
		fa.newLine(output);
	}

	private void saveClassLine(Iterator e, FileAccess fa, BufferedWriter output)
	{
		PCClass aClass = (PCClass)e.next();
		for (Iterator s = aClass.spellList().iterator(); s.hasNext();)
		{
			Spell aSpell = (Spell)s.next();
			fa.write(output, aSpell.keyName());
			for (int j = 0; j < aSpell.spellBooks().size(); j++)
				fa.write(output, "|" + aSpell.spellBooks().get(j).toString() + "|" +
					aSpell.times().get(j).toString());
			fa.write(output, ":");
		}
		fa.newLine(output);
	}

	private void saveRaceLine(FileAccess fa, BufferedWriter output)
	{
		Integer anInt;
		fa.write(output, this.race().keyName() + ":");
		fa.write(output, String.valueOf(alignment) + ":");
		fa.write(output, String.valueOf(height) + ":");
		fa.write(output, String.valueOf(weight) + ":");
		fa.write(output, String.valueOf(age) + ":");
		fa.write(output, gender + ":");
		fa.write(output, handed());
		if (this.race().hitDice() != 0)
			for (int j = 0; j < this.race().hitDice(); j++)
			{
				fa.write(output, ":" + this.race().hitPointList[j].toString());
			}
		fa.newLine(output);
	}

	private void saveDeityLine(FileAccess fa, BufferedWriter output)
	{
		if (deity != null)
			fa.write(output, deity().keyName() + ":");
		for (int i = 0; i < domainList.size(); i++)
			fa.write(output, ((Domain)domainList.get(i)).keyName() + ":");
		fa.newLine(output);
	}

	private void saveSkillsLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			fa.write(output, aSkill.keyName() + ":" + aSkill.rank().toString() + ":");
			for (int i = 0; i < aSkill.associatedList.size(); i++)
				fa.write(output, aSkill.associatedList.get(i).toString() + ":");
		}
		fa.newLine(output);
	}

	private void saveFeatsLine(FileAccess fa, BufferedWriter output)
	{
		Integer anInt;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			fa.write(output, aFeat.toString());
			for (Iterator fe = aFeat.saveList.iterator(); fe.hasNext();)
				fa.write(output, "[" + fe.next().toString());
			fa.write(output, ":" + String.valueOf(aFeat.associatedList().size()) + ":");

			for (Iterator f = aFeat.associatedList().iterator(); f.hasNext();)
				fa.write(output, f.next().toString() + ":");
		}
		fa.newLine(output);
	}

	private void saveClassesLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			fa.write(output, aClass.keyName() + ":" + aClass.subClassName() + " :" + aClass.prohibitedString() + " :");
			fa.write(output, aClass.level().toString() + ":");
			for (int j = 0; j < aClass.level().intValue(); j++)
			{
				fa.write(output, aClass.hitPointList[j].toString() + ":");
			}
			fa.write(output, aClass.skillPool().toString() + ":");
			fa.write(output, aClass.spellBaseStat() + ":");
		}
		fa.newLine(output);
	}

	private void saveStatsLine(FileAccess fa, BufferedWriter output)
	{
		for (int i = 0; i < 6; i++)
		{
			fa.write(output, String.valueOf(stats()[i]) + ":");
		}
		fa.write(output, String.valueOf(poolAmount()) + ":" + String.valueOf(costPool));
		fa.newLine(output);
	}

	private void saveNameLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, name + ":" + playersName);
		fa.newLine(output);
	}

	private String loadClassLine(Iterator e, FileAccess fa, BufferedReader input)
	{
		StringTokenizer aTok;
		String aName;
		int k;
		Integer anInt;
		String aString;
		PCClass aClass = (PCClass)e.next();
		lastLineParsed = fa.readLine(input);
		aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			String spellLine = (String)aTok.nextToken();
			StringTokenizer bTok = new StringTokenizer(spellLine, "|", false);
			aName = (String)bTok.nextToken();
			Spell aSpell = aClass.getSpellNamed(aName);
			if (aSpell == null)
			{
				aSpell = (Spell)Globals.getSpellNamed(aName);
				if (aSpell != null)
				{
					aSpell = (Spell)aSpell.clone();
					String className = new String(aClass.keyName());
					if (aClass.castAs.length() > 0)
						className = aClass.castAs;
					k = aSpell.levelForClass(className);
					anInt = new Integer(k);
					aString = className + "," + anInt.toString();
					if (className.equals("Domain"))
					{
						aString = "";
						for (Iterator s1 = domainList.iterator(); s1.hasNext();)
						{
							Domain aDomain = (Domain)s1.next();
							k = aSpell.levelForClass(aDomain.keyName());
							if (k >= 0)
							{
								if (aString.length() > 0)
									aString = aString + ",";
								aString = aString + aDomain.keyName() + "," + new Integer(k).toString();
							}
						}
					}
					aSpell.setClassLevels(aString);
					aClass.spellList().add(aSpell);
				}
			}
			if (aSpell != null && bTok.countTokens() == 0)
			{
				aSpell.addToSpellBook("Known Spells", false);
				addSpellBook("Known Spells");
			}
			if (aSpell != null)
				while (bTok.hasMoreTokens())
				{
					String bookName = bTok.nextToken();
					addSpellBook(bookName);
					if (aSpell.spellBooks().contains(bookName))
					{
						if (bTok.hasMoreTokens())
							bTok.nextToken(); // this book already exists, so burn the token
					}
					else
					{
						aSpell.spellBooks().add(bookName);
						if (bTok.hasMoreTokens())
							aSpell.times().add(new Integer(bTok.nextToken()));
						else
							aSpell.times().add(new Integer(1));
					}
				}
		}
		return lastLineParsed;
	}

	private void loadExperienceAndMiscLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		int k;
		String cString = "";
		boolean nextLine = true;
		while (i <= 3)
		{
			if (nextLine == true)
				lastLineParsed = fa.readLine(input);
			k = lastLineParsed.indexOf(':');
			while( k>0 && lastLineParsed.charAt(k-1) == '\\' )
				k = lastLineParsed.indexOf(':',k+1);
			if( k<0 || lastLineParsed.charAt(k-1) == '\\') k=-1; 
			if (k == -1)
			{
				cString = cString.concat(lastLineParsed);
				cString = cString.concat("\r\n");
				nextLine = true;
			}
			else
			{
				k = lastLineParsed.indexOf(':');
				while( lastLineParsed.charAt(k-1) == '\\' )
					k = lastLineParsed.indexOf(':',k+1);
				cString = cString.concat(lastLineParsed.substring(0, k));
				System.out.println("Line "+i+": "+cString);
				switch (i)
				{
					case 0:
						setExperience(new Integer(cString));
						break;
					case 1:
					case 2:
					case 3:
						String tempStr = "";
						for(int j=0; j<cString.length(); j++)
						{
							if( cString.charAt(j) != '\\' )
								tempStr += cString.charAt(j);
							else
							{
								if( j+1 < cString.length() && cString.charAt(j+1) !=':')
									tempStr += "\\";	
							}	
						}
						miscList().set(i - 1, tempStr.trim());
						break;
				}
				i++;
				if (i < 4)
					lastLineParsed = lastLineParsed.substring(k + 1);
				cString = "";
				nextLine = false;
			}
		}
	}

	private void loadClassSpecialtyAndSaveLines(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		StringTokenizer aTok;
		String cString;
		while (i < classList.size())
		{
			lastLineParsed = fa.readLine(input);
			if (lastLineParsed == null) return;
			aTok = new StringTokenizer(lastLineParsed, ":", false);
			String bString = aTok.nextToken();
			PCClass aClass = getClassKeyed(bString);
			i++;
			if (aClass == null || aClass.keyName().equals("Domain"))
				continue;
			while (aTok.hasMoreTokens())
			{
				cString = aTok.nextToken();
				if (cString.startsWith("SPECIAL"))
					aClass.specialtyList.add(cString.substring(7));
				else
				{
					if (cString.startsWith("BONUS"))
					{
						aClass.bonusList().add(cString.substring(6));
						if (cString.lastIndexOf("|PCLEVEL|") > -1)
						{
							StringTokenizer cTok = new StringTokenizer(cString.substring(cString.lastIndexOf("PCLEVEL")), "|", false);
							cTok.nextToken(); // should be PCLEVEL
							if (cTok.hasMoreTokens())
								specialAbilityList.add("Bonus Caster Level for " + cTok.nextToken());
						}
					}
					else if (!specialAbilityList.contains(cString))
						specialAbilityList.add(cString);
					if (!aClass.saveList.contains(cString) || cString.startsWith("BONUS"))
						aClass.saveList.add(cString);
				}
			}
		}
	}

	private void loadClassesSkillLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		String aString = aTok.nextToken();
		PCClass aClass = (PCClass)getClassKeyed(aString);
		if (aClass == null)
		{
			return; //Is this right? Shouldn't an exception be thrown instead?
		}
		while (aTok.hasMoreTokens())
		{
			if (aClass != null)
			{
				aClass.skillList().add(aTok.nextToken().trim());
			}
		}
	}

	private void loadGoldBioDescriptionLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		int k;
		String cString = "";
		boolean nextLine = true;
		while (i <= 2)
		{
			if (nextLine == true)
				lastLineParsed = fa.readLine(input);
			k = lastLineParsed.indexOf(':');
			while( k>0 && lastLineParsed.charAt(k-1) == '\\' )
				k = lastLineParsed.indexOf(':',k+1);
			if( k<0 || lastLineParsed.charAt(k-1) == '\\') k=-1; 
			if (k == -1)
			{
				cString = cString.concat(lastLineParsed);
				cString = cString.concat("\r\n");
				nextLine = true;
			}
			else
			{
				k = lastLineParsed.indexOf(':');
				while( lastLineParsed.charAt(k-1) == '\\' )
					k = lastLineParsed.indexOf(':',k+1);
				cString = cString.concat(lastLineParsed.substring(0, k));
				System.out.println("Line "+i+": "+cString);
				String tempStr = "";
				for(int j=0; j<cString.length(); j++)
				{
					if( cString.charAt(j) != '\\' )
						tempStr += cString.charAt(j);
					else
					{
						if( j+1 < cString.length() && cString.charAt(j+1) !=':')
							tempStr += "\\";	
					}	
				}
				switch (i)
				{
					case 0:
						setGold(tempStr);
						break;
					case 1:
						setBio(tempStr);
						break;
					case 2:
						setDescription(tempStr);
						break;
				}
				i++;
				if (i < 4)
					lastLineParsed = lastLineParsed.substring(k + 1);
				cString = "";
				nextLine = false;
			}
		}
	}

	private void loadEquipmentLine(FileAccess fa, BufferedReader input)
	{
		String aName;
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		Equipment eq = null;
		while (aTok.hasMoreTokens())
		{
			aName = aTok.nextToken().trim();
			eq = (Equipment)Globals.getEquipmentNamed(aName);
			if (eq != null)
			{
				eq = (Equipment)eq.clone();
				eq.setQty(aTok.nextToken());
				eq.setIsEquipped(aTok.nextToken().equals("Y"));
				eq.carried = parseCarried(eq.qty(), aTok.nextToken());
				eq.setHand(Equipment.getHandNum(aTok.nextToken()));
				if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
					eq.setNumberEquipped(Integer.parseInt(aTok.nextToken()));
				equipmentList.put(eq.keyName(), eq);
			}
		}
	}

	private void loadMiscLine(FileAccess fa, BufferedReader input)
	{
		StringTokenizer aTok;
		int i = 0;
		String aString;
		lastLineParsed = fa.readLine(input);
		aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken().trim();
			i++;
			switch (i)
			{
				case 1:
					setEyeColor(aString);
					break;
				case 2:
					setSkinColor(aString);
					break;
				case 3:
					setHairColor(aString);
					break;
				case 4:
					setHairLength(aString);
					break;
				case 5:
					setSpeechTendency(aString);
					break;
				case 6:
					setPhobias(aString);
					break;
				case 7:
					setInterests(aString);
					break;
				case 8:
					setTrait1(aString);
					break;
				case 9:
					setTrait2(aString);
					break;
				case 10:
					setCatchPhrase(aString);
					break;
				case 11:
					setLocation(aString);
					break;
				case 12:
					setResidence(aString);
					break;
			}
		}
	}

	private void loadUnusedPointsLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		setSkillPoints(Integer.parseInt(aTok.nextToken()));
		setFeats(Integer.parseInt(aTok.nextToken()));
	}

	private void loadWeaponProfLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			addWeaponProf(aTok.nextToken());
		}
	}

	private void loadLanguagesLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			addLanguage(aTok.nextToken(), false);
		}
	}

	private void loadRaceLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		String aName;
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":");
		int l = 0;
		Integer[] hitPointList = null;
		int x = 0;
		while (aTok.hasMoreElements())
		{
			aName = (String)aTok.nextElement();
			if (i > 0 && i < 5)
			{
				l = Integer.parseInt(aName);
			}
			switch (i++)
			{
				case 0:
					setRace(Globals.getRaceKeyed(aName));
					if (this.race().hitDice() != 0)
						hitPointList = new Integer[this.race().hitDice()];
					break;
				case 1:
					setAlignment(l);
					break;
				case 2:
					setHeight(l);
					break;
				case 3:
					setWeight(l);
					break;
				case 4:
					setAge(l);
					break;
				case 5:
					setGender(aName);
					break;
				case 6:
					setHanded(aName);
					break;
				default:
					l = Integer.parseInt(aName);
					hitPointList[x++] = new Integer(l);
					if (x == this.race().hitDice())
					{
						this.race().setHitPointList(hitPointList);
						return;
					}
					break;
			}
		}
	}

	private void handleDeityLine(FileAccess fa, BufferedReader input, String deityLine)
	{
		int i = 0;
		StringTokenizer aTok = new StringTokenizer(deityLine, ":", false);
		while (aTok.hasMoreElements())
		{
			String aName = (String)aTok.nextElement();
			switch (i++)
			{
				case 0:
					setDeity(aName);
					break;
				default:
					addDomainKeyed(aName);
					if (domainList.size() == i)
						((Domain)domainList.get(i - 1)).setIsLocked(true);
					break;
			}
		}

	}

	private void loadSkillsLine(FileAccess fa, BufferedReader input)
	{
		String aName;
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreElements())
		{
			aName = (String)aTok.nextElement();
			if (!aTok.hasMoreTokens())
				return;
			Float aFloat = new Float((String)aTok.nextElement());
			Skill aSkill = this.getSkillKeyed(aName);
			if (aSkill == null)
			{
				for (int i = 0; i < Globals.skillList.size(); i++)
					if (aName.equals(Globals.skillList.get(i).toString()))
					{
						aSkill = (Skill)Globals.skillList.get(i);
						aSkill = (Skill)aSkill.clone();
						skillList.add(aSkill);
						break;
					}
			}
			if (aSkill != null)
			{
				aSkill.setRank(aFloat);
				if (aSkill.choiceList().size() > 0 && aFloat.intValue() > 0)
				{
					for (int i = 0; i < aFloat.intValue(); i++)
						aSkill.associatedList.add(aTok.nextToken());
				}
			}
		}
	}

	private void loadFeatsLine(FileAccess fa, BufferedReader input)
	{
		String aName;
		String aString;
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			aName = aTok.nextToken();
			int l = Integer.parseInt(aTok.nextToken());
			StringTokenizer bTok = new StringTokenizer(aName, "[", false);
			aName = bTok.nextToken();
			Feat aFeat = Globals.getFeatKeyed(aName);
			if (aFeat != null)
			{
				aFeat = (Feat)aFeat.clone();
				modFeat(aFeat.keyName(), true, !aFeat.multiples());
				if (aFeat.multiples() && aFeat.associatedList().size() == 0 && getFeatKeyed(aFeat.keyName()) == null)
					featList.add(aFeat);
				aFeat = getFeatKeyed(aFeat.keyName());
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if (aString.startsWith("BONUS") && aString.length() > 6)
						aFeat.bonusList.add(aString.substring(6));
					aFeat.saveList.add(aString);
				}
			}
			else
				aFeat = new Feat();
			for (int j = 0; j < l; j++)
			{
				aString = aTok.nextToken();
				if (!aFeat.associatedList().contains(aString))
					aFeat.associatedList().add(aString);
			}
		}
	}

	private void loadClassesLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		String aName = null;
		boolean getNext = true;
		String aString = "";
		while (aTok.hasMoreTokens())
		{
			if (getNext)
				aName = aTok.nextToken();
			else
				aName = aString;
			getNext = true;
			if (!aTok.hasMoreTokens())
				break;
			boolean needCopy = true;
			PCClass aClass = getClassKeyed(aName);
			if (aClass == null)
				aClass = Globals.getClassKeyed(aName);
			else
				needCopy = false;
			if (aClass == null)
				aTok.nextToken();
			else
			{
				if (needCopy == true)
				{
					aClass = (PCClass)aClass.clone();
					classList.add(aClass);
				}
				aClass.setSubClassName(aTok.nextToken().trim());
				aClass.setProhibitedString(aTok.nextToken().trim());
				int l = Integer.parseInt(aTok.nextToken());
				for (int k = 0; k < l; k++)
				{
					aClass.addLevel(false);
					aClass.hitPointList()[k] = new Integer(aTok.nextToken());
				}
				aClass.skillPool = new Integer(aTok.nextToken());

				if (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
					String statString = "STRDEXCONINTWISCHA";
					if ((statString.lastIndexOf(aString.toUpperCase()) > -1) || aString.equalsIgnoreCase("None"))
						aClass.setSpellBaseStat(aString);
					else
						getNext = false;
				}
			}
		}
		currentHP = hitPoints();
	}

	private void loadStatsLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens() && i < 6)
		{
			stats()[i++] = Integer.parseInt(aTok.nextToken());
		}
		if (aTok.hasMoreTokens())
			poolAmount = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			costPool = Integer.parseInt(aTok.nextToken());
	}

	private void loadNameLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		name = aTok.nextToken();
		if (aTok.hasMoreTokens())
			playersName = aTok.nextToken();
	}

	private Float parseCarried(Float qty, String aName)
	{
		float carried = 0.0F;
		if (aName.equals("Y"))
		{
			carried = qty.floatValue();
		}
		else if (aName.equals("N"))
		{
			carried = 0.0F;
		}
		else
		{
			try
			{
				carried = Float.parseFloat(aName);
			}
			catch (Exception e)
			{
				carried = 0.0F;
			}
		}
		return new Float(carried);
	}

	public boolean print(File aFile, BufferedWriter output)
	{
		Feat aFeat = null;
		Feat bFeat = null;
		FileInputStream aStream = null;
		populateSkills(Globals.includeSkills);
		try
		{
			aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			byte[] inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			String aString = new String(inputLine);
			StringTokenizer aTok = new StringTokenizer(aString, "\r\n", false);
			FileAccess fa = new FileAccess();
			boolean flag = true;
			skillList = Globals.sortPObjectList(skillList);
			featList = Globals.sortPObjectList(featList);
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				PCClass aClass = (PCClass)e.next();
				aClass.spellList = Globals.sortPObjectList(aClass.spellList());
			}
			determinePrimaryOffWeapon();
			PCClass aClass = getClassNamed("Ranger");
			modFromArmorOnWeaponRolls();
			if (aClass != null && (getFeatNamed("Ambidexterity") == null || getFeatNamed("Two-Weapon Fighting") == null))
			{
				if (getFeatNamed("Ambidexterity") == null)
					aFeat = Globals.getFeatNamed("Ambidexterity");
				if (getFeatNamed("Two-Weapon Fighting") == null)
					bFeat = Globals.getFeatNamed("Two-Weapon Fighting");
				for (Iterator mapIter = equipmentList.values().iterator();
						 mapIter.hasNext();)
				{
					Equipment eq = (Equipment)mapIter.next();
					if (eq.isArmor() && eq.isEquipped() && (eq.isHeavy() || eq.isMedium()))
					{
						aFeat = null;
						bFeat = null;
						break;
					}
				}
				if (aFeat != null)
					featList.add(aFeat);
				if (bFeat != null)
					featList.add(bFeat);
			}
			boolean inPipe = false;
			String tokString = "";
			while (aTok.hasMoreTokens())
			{
				String aLine = aTok.nextToken();
				if (!inPipe && aLine.lastIndexOf("|") == -1)
				{
					replaceToken(aLine, output);
					fa.newLine(output);
				}
				else if ((inPipe && aLine.lastIndexOf("|") == -1) || (!inPipe && aLine.lastIndexOf("|") == 0))
				{
					tokString = tokString + aLine.substring(aLine.lastIndexOf("|") + 1);
					inPipe = true;
				}
				else
				{
					if (inPipe == false && aLine.charAt(0) == '|')
						inPipe = true;
					StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
					flag = bTok.countTokens() == 1;
					int count = bTok.countTokens();
					while (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						if (!inPipe)
							replaceToken(bString, output);
						else
						{
							if (bTok.hasMoreTokens() || flag || (inPipe && !bTok.hasMoreTokens() && aLine.charAt(aLine.length() - 1) == '|'))
							{
								replaceToken(tokString + bString, output);
								tokString = "";
							}
							else
								tokString = tokString + bString;
						}
						if (bTok.hasMoreTokens() || flag)
							inPipe = !inPipe;
					}
					if (inPipe && aLine.charAt(aLine.length() - 1) == '|')
						inPipe = false;
					if (!inPipe)
						fa.newLine(output);
				}
			}
		}
		catch (Exception exc)
		{
		}
		finally
		{
			if (aStream != null)
			{
				try
				{
					aStream.close();
				}
				catch (IOException ioe)
				{
				}
			}
		}
		if (aFeat != null)
			featList.remove(aFeat);
		if (bFeat != null)
			featList.remove(bFeat);
		return true;
	}

	public int replaceToken(String aString, BufferedWriter output)
	{
		try
		{
			FileAccess fa = new FileAccess();
			Iterator e;
			Iterator setIter;
			Integer anInt = null;
			int len = 1;
			if (!aString.startsWith("%") && !canWrite)
				return 0;
			if (aString.equals("%"))
			{
				inLabel = false;
				canWrite = true;
				return 0;
			}
			if (aString.startsWith("%") && aString.length() > 1 &&
				aString.lastIndexOf('<') == -1 && aString.lastIndexOf('>') == -1)
			{
				boolean found = false;
				canWrite = true;
				if (aString.substring(1).startsWith("WEAPON"))
				{
					anInt = new Integer(aString.substring(aString.length() - 1));
					ArrayList aArrayList = getEquipmentOfType("Weapon", 3);
					if (anInt.intValue() >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("DOMAIN"))
				{
					int i = Integer.parseInt(aString.substring(7));
					canWrite = (i <= domainList.size());
					return 0;
				}
				if (aString.substring(1).startsWith("SPELLLISTBOOK"))
				{
					int dot = aString.lastIndexOf(".");
					int classNum = new Integer(aString.substring(14, dot)).intValue();
					int levelNum = new Integer(aString.substring(dot + 1)).intValue();
					canWrite = false;
					if (classNum < 0 || classNum >= classList.size())
						return 0;
					PCClass aClass = (PCClass)classList.get(classNum);
					String bString = aClass.keyName();
					if (aClass.castAs.length() > 0)
						bString = aClass.castAs;
					if (bString.startsWith("Domain"))
						bString = domainClassName();
					for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
					{
						Spell aSpell = (Spell)e1.next();
						if (aSpell.levelForClass(bString) == levelNum)
						{
							canWrite = true;
							break;
						}
					}
					return 0;
				}
				StringTokenizer aTok = new StringTokenizer(aString.substring(1), ",", false);
				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(cString, "=", false);
					String bString = bTok.nextToken();
					if (bTok.hasMoreTokens())
						anInt = new Integer(bTok.nextToken());
					else
						anInt = new Integer(0);
					PCClass aClass = null;
					PCClass bClass = null;
					for (e = Globals.classList.iterator(); e.hasNext();)
					{
						bClass = (PCClass)e.next();
						if (bClass.name().equals(bString))
							break;
						bClass = null;
					}
					found = bClass != null;
					aClass = getClassNamed(bString);
					if (bClass != null && aClass != null)
						canWrite = (aClass.level().intValue() >= anInt.intValue());
					else if (bClass != null && aClass == null)
						canWrite = false;
					else if (bString.startsWith("SPELLLISTCLASS"))
					{
						found = true;
						int i = Integer.parseInt(bString.substring(14));
						aClass = getSpellClassAtIndex(i);
						canWrite = (aClass != null);
					}
				}
				if (found == true)
				{
					inLabel = true;
					return 0;
				}
			}
			if (aString.startsWith("FOR.") || aString.startsWith("DFOR."))
			{
				int x = 0;
				int i = 0;
				StringTokenizer aTok;
				if (aString.startsWith("DFOR."))
					aTok = new StringTokenizer(aString.substring(5), ",", false);
				else
					aTok = new StringTokenizer(aString.substring(4), ",", false);
				Integer cMin = new Integer(0);
				Integer cMax = new Integer(100);
				Integer cStep = new Integer(1);
				Integer cStepLine = new Integer(0);
				Integer cStepLineMax = new Integer(0);
				String cString = "";
				String cStartLineString = "";
				String cEndLineString = "";
				String bString = null;
				existsOnly = false;
				noMoreItems = false;
				checkBefore = false;
				boolean isDFor = false;
				while (aTok.hasMoreTokens())
				{
					bString = aTok.nextToken();
					switch (i++)
					{
						case 0:
							Float aFloat = getVariableValue(bString, "", "");
							cMin = new Integer(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
							break;
						case 1:
							aFloat = getVariableValue(bString, "", "");
							cMax = new Integer(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
							break;
						case 2:
							aFloat = getVariableValue(bString, "", "");
							cStep = new Integer(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
							if (aString.startsWith("DFOR."))
							{
								isDFor = true;
								bString = aTok.nextToken();
								aFloat = getVariableValue(bString, "", "");
								cStepLineMax = new Integer(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
								bString = aTok.nextToken();
								aFloat = getVariableValue(bString, "", "");
								cStepLine = new Integer(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
							}
							break;
						case 3:
							cString = bString;
							break;
						case 4:
							cStartLineString = bString;
							break;
						case 5:
							cEndLineString = bString;
							break;
						case 6:
							existsOnly = !bString.equals("0");
							if (bString.equals("2")) checkBefore = true;
							break;
					}
				}
				int iStart = cMin.intValue();
				int iNow = iStart;
				if (!isDFor)
					cStepLine = new Integer(1);
				while (iStart < cMax.intValue())
				{
					if (x++ == 0)
						fa.write(output, cStartLineString);
					iNow = iStart;
					if (!isDFor)
						cStepLineMax = new Integer(iNow + cStep.intValue());
					while (iNow < cStepLineMax.intValue() || (isDFor && iNow < cMax.intValue()))
					{
						aTok = new StringTokenizer(cString, "\\", false);
						int j = 0;
						while (aTok.hasMoreTokens())
						{
							String eString = aTok.nextToken();
							int index = eString.lastIndexOf('%');
							if (index < eString.length() - 1 && eString.charAt(index + 1) != '.')
								index = -1;
							String fString = "";
							String gString = "";
							String hString = eString;
							if (index > -1)
							{
								fString = eString.substring(0, index);
								if (index + 1 < eString.length())
									gString = eString.substring(index + 1);
								hString = fString + new Integer(iNow).toString() + gString;
							}
							if (eString.equals("%0") || eString.equals("%1"))
							{
								int cInt = iNow + new Integer(eString.substring(1)).intValue();
								fa.write(output, new Integer(cInt).toString());
							}
							else
							{
								replaceToken(hString, output);
							}
							if (checkBefore == true && noMoreItems == true)
							{
								iNow = cMax.intValue();
								iStart = cMax.intValue();
								if (j == 0)
									existsOnly = false;
								break;
							}
							j++;
						}
						iNow += cStepLine.intValue();
						if (cStepLine.intValue() == 0)
							break;
					}
					if (cStepLine.intValue() > 0 || (cStepLine.intValue() == 0 && x == cStep.intValue()) || (existsOnly == noMoreItems == true))
					{
						fa.write(output, cEndLineString);
						fa.newLine(output);
						x = 0;
						if (existsOnly && noMoreItems)
							break;
					}
					iStart += cStep.intValue();
				}
				existsOnly = false;
				noMoreItems = false;
				return 0;
			}
			if (aString.startsWith("STAT"))
			{
				int i = (int)aString.charAt(4) - 48;
				boolean x = aString.length() > 5;
				len = 1;
				if (x == false)
				{
					if (race.isNonability(i))
						fa.write(output, "--");
					else
						fa.write(output, new Integer(adjStats(i)).toString());
				}
				else
				{
					if (race.isNonability(i))
						fa.write(output, "0");
					else
					{
						anInt = new Integer(adjStats(i) / 2 - 5);
						if (anInt.intValue() > 0)
							fa.write(output, "+");
						fa.write(output, anInt.toString());
					}
				}
			}
			else if (aString.equals("BIO"))
				fa.write(output, bio);
			else if (aString.equals("DESC"))
				fa.write(output, description);
			else if (aString.equals("NAME"))
				fa.write(output, name());
			else if (aString.equals("RACE"))
				fa.write(output, race().name());
			else if (aString.equals("AGE"))
				fa.write(output, new Integer(age).toString());
			else if (aString.equals("HEIGHT"))
				fa.write(output, new Integer(height / 12).toString() + "' " + new Integer(height % 12).toString() + " inches");
			else if (aString.equals("WEIGHT"))
				fa.write(output, new String(new Integer(weight).toString() + " pounds"));
			else if (aString.equals("COLOR.EYE"))
				fa.write(output, eyeColor());
			else if (aString.equals("COLOR.HAIR"))
				fa.write(output, hairColor());
			else if (aString.equals("COLOR.SKIN"))
				fa.write(output, skinColor());
			else if (aString.equals("LENGTH.HAIR"))
				fa.write(output, hairLength());
			else if (aString.equals("PERSONALITY1"))
				fa.write(output, trait1());
			else if (aString.equals("PERSONALITY2"))
				fa.write(output, trait2());
			else if (aString.equals("SPEECHTENDENCY"))
				fa.write(output, speechTendency());
			else if (aString.equals("CATCHPHRASE"))
				fa.write(output, catchPhrase());
			else if (aString.equals("RESIDENCE"))
				fa.write(output, residence());
			else if (aString.equals("LOCATION"))
				fa.write(output, location());
			else if (aString.equals("PHOBIAS"))
				fa.write(output, phobias());
			else if (aString.equals("INTERESTS"))
				fa.write(output, interests());
			else if (aString.equals("TOTALLEVELS"))
				fa.write(output, new Integer(totalLevels()).toString());
			else if (aString.equals("CR"))
				fa.write(output, "" + CR());
			else if (aString.equals("FACE"))
				fa.write(output, race.face());
			else if (aString.equals("REACH"))
				fa.write(output, String.valueOf(race.reach()));
			else if (aString.equals("ALIGNMENT"))
			{
				String alstring = "Lawful Good";
				switch (alignment)
				{
					case 0:
						alstring = "Lawful Good";
						break;
					case 1:
						alstring = "Lawful Neutral";
						break;
					case 2:
						alstring = "Lawful Evil";
						break;
					case 3:
						alstring = "Neutral Good";
						break;
					case 4:
						alstring = "True Neutral";
						break;
					case 5:
						alstring = "Neutral Evil";
						break;
					case 6:
						alstring = "Chaotic Good";
						break;
					case 7:
						alstring = "Chaotic Neutral";
						break;
					case 8:
						alstring = "Chaotic Evil";
						break;
				}
				fa.write(output, alstring);
			}
			else if (aString.equals("ALIGNMENT.SHORT"))
			{
				String alstring = "LG";
				switch (alignment)
				{
					case 0:
						alstring = "LG";
						break;
					case 1:
						alstring = "LN";
						break;
					case 2:
						alstring = "LE";
						break;
					case 3:
						alstring = "NG";
						break;
					case 4:
						alstring = "TN";
						break;
					case 5:
						alstring = "NE";
						break;
					case 6:
						alstring = "CG";
						break;
					case 7:
						alstring = "CN";
						break;
					case 8:
						alstring = "CE";
						break;
				}
				fa.write(output, alstring);
			}
			else if (aString.equals("GENDER"))
				fa.write(output, gender);
			else if (aString.equals("HANDED"))
				fa.write(output, handed);
			else if (aString.equals("PROHIBITEDLIST"))
			{
				for (e = classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (aClass.level().intValue() > 0)
					{
						if (!aClass.prohibitedString().equals("None"))
							fa.write(output, aClass.prohibitedString());
					}
				}
			}
			else if (aString.startsWith("CLASS"))
			{
				int i = 0;
				int y = 0;
				int cmp = 0;
				if (aString.equals("CLASSLIST"))
					cmp = 1;
				else if (aString.lastIndexOf("ABB") > -1)
				{
					i = (int)aString.charAt(8) - 48;
					cmp = 2;
				}
				else
					i = (int)aString.charAt(5) - 48;
				if (aString.endsWith("LEVEL"))
					cmp = 3;
				len = 0;
				int classSize = classList.size();
				if (getClassNamed("Domain") != null)
					classSize--;
				if (classSize <= i && existsOnly == true)
				{
					noMoreItems = true;
					return 0;
				}
				for (e = classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (aClass.name().equals("Domain"))
						continue;
					if (cmp == 1 && y++ > 0)
						fa.write(output, " ");
					if (aClass.level().intValue() > 0)
						i--;
					if (i == -1 || cmp == 1)
					{
						len = 1;
						if (cmp < 2)
						{
							if (aClass.subClassName().equals("None") || aClass.subClassName().equals(""))
								fa.write(output, aClass.name());
							else
								fa.write(output, aClass.subClassName());
						}
						if (cmp == 1 || cmp == 3)
							fa.write(output, aClass.level().toString());
						if (cmp == 2)
							fa.write(output, aClass.abbrev);
						if (cmp != 1)
							break;
					}
				}
			}
			else if (aString.equals("EXP.CURRENT"))
			{
				fa.write(output, experience().toString());
			}
			else if (aString.equals("EXP.NEXT"))
			{
				fa.write(output, new Integer(Globals.minExpForLevel(totalLevels() + 1 + race.LevelAdjustment())).toString());
			}
			else if (aString.startsWith("REFLEX"))
			{
				anInt = new Integer(0);
				if (aString.endsWith(".TOTAL"))
					anInt = new Integer(getBonus(2, true) + adjStats(1) / 2 - 5);
				else if (aString.endsWith(".BASE"))
					anInt = new Integer(getBonus(2, false));
				else if (aString.endsWith(".RACE"))
					anInt = new Integer(race().bonusTo("CHECKS", "Reflex"));
				else if (aString.endsWith(".MAGIC"))
					anInt = new Integer(getBonus(2, true) - getBonus(2, false) - race().bonusTo("CHECKS", "Reflex"));
				else
					anInt = new Integer(getBonus(2, true));
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.startsWith("FORTITUDE"))
			{
				anInt = new Integer(0);
				if (aString.endsWith(".TOTAL"))
					anInt = new Integer(getBonus(1, true) + adjStats(2) / 2 - 5);
				else if (aString.endsWith(".BASE"))
					anInt = new Integer(getBonus(1, false));
				else if (aString.endsWith(".RACE"))
					anInt = new Integer(race().bonusTo("CHECKS", "Fortitude"));
				else if (aString.endsWith(".MAGIC"))
					anInt = new Integer(getBonus(1, true) - getBonus(1, false) - race().bonusTo("CHECKS", "Fortitude"));
				else
					anInt = new Integer(getBonus(1, true));
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.startsWith("WILL"))
			{
				anInt = new Integer(0);
				if (aString.endsWith(".TOTAL"))
					anInt = new Integer(getBonus(3, true) + adjStats(4) / 2 - 5);
				else if (aString.endsWith(".BASE"))
					anInt = new Integer(getBonus(3, false));
				else if (aString.endsWith(".RACE"))
					anInt = new Integer(race().bonusTo("CHECKS", "Willpower"));
				else if (aString.endsWith(".MAGIC"))
					anInt = new Integer(getBonus(3, true) - getBonus(3, false) - race().bonusTo("CHECKS", "Willpower"));
				else
					anInt = new Integer(getBonus(3, true));
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("TOTALAC"))
			{
				anInt = new Integer(totalAC());
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("FLATAC"))
			{
				anInt = new Integer(flatFootedAC());
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("BASEAC"))
			{
				anInt = new Integer(race().startingAC().intValue() + naturalArmorModForSize());
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("ACMOD"))
			{
				anInt = new Integer(acMod());
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("ACABILITYMOD"))
			{
				anInt = new Integer(acAbilityMod());
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("ACSIZEMOD"))
			{
				anInt = new Integer(acSizeMod());
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("EQUIP.AC"))
			{
				anInt = new Integer(modToFromEquipment("AC"));
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("MAXDEX") || aString.equals("ACCHECK") ||
				aString.equals("SPELLFAILURE"))
			{
				anInt = new Integer(modToFromEquipment(aString));
				if (anInt.intValue() > 0 && !aString.equals("SPELLFAILURE"))
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("INITIATIVEMOD"))
			{
				anInt = new Integer(initiativeMod());
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("INITIATIVEBONUS"))
			{
				anInt = new Integer(initiativeMod() - (adjStats(1) / 2 - 5));
				if (anInt.intValue() > 0)
					fa.write(output, "+");
				fa.write(output, anInt.toString());
			}
			else if (aString.equals("MOVEMENT"))
			{
				fa.write(output, race.movementType(0) + " " + movement(0) + "'");
				for (int x = 1; x < race().movements().length; x++)
					fa.write(output, ", " + race.movementType(x) + " " + movement(x) + "'");
			}
			else if (aString.equals("SIZE"))
			{
				fa.write(output, size());
			}
			else if (aString.startsWith("FEATLIST"))
			{
				String delim = aString.substring(8);
				if (delim.equals(""))
					delim = ",";
				int i = 0;
				len = featList.size();
				for (e = featList.iterator(); e.hasNext();)
				{
					if (i > 0)
						fa.write(output, delim);
					Feat aFeat = (Feat)e.next();
					fa.write(output, aFeat.qualifiedName());
					i++;
				}
			}
			else if (aString.startsWith("VFEATLIST"))
			{
				String delim = aString.substring(8);
				if (delim.equals(""))
					delim = ",";
				int i = 0;
				ArrayList aArrayList = vFeatList();
				len = aArrayList.size();
				for (e = aArrayList.iterator(); e.hasNext();)
				{
					if (i > 0)
						fa.write(output, delim);
					Feat aFeat = (Feat)e.next();
					fa.write(output, aFeat.qualifiedName());
					i++;
				}
			}
			else if (aString.startsWith("FEAT"))
			{
				anInt = new Integer(0);
				int j = aString.lastIndexOf(".");
				int i = -1;
				if (j == -1)
					i = new Integer(aString.substring(4)).intValue();
				else
					i = new Integer(aString.substring(4, j)).intValue();
				if (featList.size() <= i && existsOnly == true)
					noMoreItems = true;
				len = featList.size();
				for (e = featList.iterator(); e.hasNext();)
				{
					Feat aFeat = (Feat)e.next();
					if (i == 0)
					{
						if (aString.endsWith(".DESC"))
							fa.write(output, aFeat.description());
						else
							fa.write(output, aFeat.qualifiedName());
					}
					i--;
				}

			}
			else if (aString.startsWith("VFEAT"))
			{
				anInt = new Integer(0);
				ArrayList aArrayList = vFeatList();
				int j = aString.lastIndexOf(".");
				int i = -1;
				if (j == -1)
					i = new Integer(aString.substring(4)).intValue();
				else
					i = new Integer(aString.substring(4, j)).intValue();
				if (aArrayList.size() <= i && existsOnly == true)
					noMoreItems = true;
				len = aArrayList.size();
				for (e = aArrayList.iterator(); e.hasNext();)
				{
					Feat aFeat = (Feat)e.next();
					if (i == 0)
					{
						if (aString.endsWith(".DESC"))
							fa.write(output, aFeat.description());
						else
							fa.write(output, aFeat.qualifiedName());
					}
					i--;
				}

			}
			else if (aString.equals("SKILLLISTMODS"))
			{
				int i = 0;
				for (e = skillList().iterator(); e.hasNext();)
				{
					Skill aSkill = (Skill)e.next();
					int modSkill = -1;
					if (aSkill.keyStat().compareToIgnoreCase("none") != 0)
						modSkill = aSkill.modifier().intValue() - adjStats(aSkill.statIndex(aSkill.keyStat())) / 2 - 5;
					if (aSkill.rank().intValue() > 0 || modSkill > 0)
					{
						anInt = new Integer(aSkill.modifier().intValue() + aSkill.rank().intValue());
						if (i > 0)
							fa.write(output, ", ");
						fa.write(output, aSkill.name() + " +" + anInt.toString());
						i++;

					}
				}
			}
			else if (aString.startsWith("SKILL"))
			{
				anInt = new Integer(0);
				StringTokenizer aTok = new StringTokenizer(aString, ".");
				String fString = aTok.nextToken();
				Skill aSkill = null;
				if (fString.length() > 5)
				{
					int i = new Integer(fString.substring(5)).intValue();
					if (i >= skillList().size() - 1 && existsOnly == true)
						noMoreItems = true;
					if (i > skillList().size() - 1)
						len = 0;
					else
						aSkill = (Skill)skillList().get(i);
				}
				else
				{
					fString = aTok.nextToken();
					aSkill = this.getSkillNamed(fString);
					if (aSkill == null)
						aSkill = Globals.getSkillNamed(fString);
				}

				int cmp = 0;
				if (aString.endsWith(".TOTAL"))
					cmp = 1;
				else if (aString.endsWith(".RANK"))
					cmp = 2;
				else if (aString.endsWith(".MOD"))
					cmp = 3;
				else if (aString.endsWith(".ABILITY"))
					cmp = 4;
				else if (aString.endsWith(".ABMOD"))
					cmp = 5;
				else if (aString.endsWith(".MISC"))
					cmp = 6;
				else if (aString.endsWith(".UNTRAINED"))
					cmp = 7;
				else if (aString.endsWith(".EXCLUSIVE"))
					cmp = 8;
				if (aSkill != null)
				{
					if ((cmp == 5 || cmp == 6) && aSkill.keyStat().equals("None"))
						fa.write(output, "n/a");
					else
						switch (cmp)
						{
							case 0:
								fa.write(output, aSkill.qualifiedName());
								break;
							case 1:
								fa.write(output, new Integer(aSkill.rank().intValue() + aSkill.modifier().intValue()).toString());
								break;
							case 2:
								fa.write(output, aSkill.rank().toString());
								break;
							case 3:
								fa.write(output, aSkill.modifier().toString());
								break;
							case 4:
								fa.write(output, aSkill.keyStat());
								break;
							case 5:
								fa.write(output, new Integer((adjStats(aSkill.statIndex(aSkill.keyStat())) / 2) - 5).toString());
								break;
							case 6:
								fa.write(output, new Integer(aSkill.modifier().intValue() - adjStats(aSkill.statIndex(aSkill.keyStat())) / 2 + 5).toString());
								break;
							case 7:
								fa.write(output, aSkill.untrained());
								break;
							case 8:
								fa.write(output, aSkill.isExclusive());
								break;
						}
				}
			}
			else if (aString.equals("DEITY"))
			{
				if (deity() != null)
					fa.write(output, deity().name());
				else
					len = 0;
			}
			else if (aString.startsWith("DOMAIN"))
			{
				boolean flag = aString.endsWith("POWER");
				Domain aDomain = null;
				if (domainList.size() > (int)aString.charAt(6) - 49)
					aDomain = (Domain)domainList.get((int)aString.charAt(6) - 49);
				if (aDomain == null)
				{
					if (existsOnly == true)
						noMoreItems = true;
					return 0;
				}
				else if (flag == true)
					fa.write(output, aDomain.grantedPower());
				else
					fa.write(output, aDomain.name());
			}
			else if (aString.startsWith("SPECIALLIST"))
			{
				String delim = aString.substring(11);
				if (delim.equals(""))
					delim = ",";
				int i = 0;
				len = specialAbilityList().size();
				for (e = specialAbilityList().iterator(); e.hasNext();)
				{
					if (i++ > 0)
						fa.write(output, delim);
					fa.write(output, (String)e.next());
				}
			}
			else if (aString.startsWith("SPECIALABILITY"))
			{
				anInt = new Integer(0);
				int i = anInt.parseInt(aString.substring(14, aString.length()));
				if (i >= specialAbilityList().size() && existsOnly == true)
					noMoreItems = true;
				len = specialAbilityList().size();
				for (e = specialAbilityList.iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					if (i-- == 0)
						fa.write(output, bString);
				}
			}
			else if (aString.equals("ATTACK.MELEE"))
			{
				fa.write(output, getAttackString(0));
			}
			else if (aString.equals("ATTACK.MELEE.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(1)).toString());
			}
			else if (aString.equals("ATTACK.RANGED"))
			{
				fa.write(output, getAttackString(1));
			}
			else if (aString.equals("ATTACK.RANGED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(2)).toString());
			}
			else if (aString.equals("ATTACK.UNARMED"))
			{
				fa.write(output, getAttackString(2));
			}
			else if (aString.equals("ATTACK.UNARMED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(3)).toString());
			}
			else if (aString.equals("ATTACK.MELEE.TOTAL"))
			{
				fa.write(output, getAttackString(0, adjStats(0) / 2 - 5));
			}
			else if (aString.equals("ATTACK.RANGED.TOTAL"))
			{
				fa.write(output, getAttackString(1, adjStats(1) / 2 - 5));
			}
			else if (aString.equals("ATTACK.UNARMED.TOTAL"))
			{
				fa.write(output, getAttackString(2, adjStats(0) / 2 - 5));
			}
			else if (aString.startsWith("DAMAGE.UNARMED"))
			{
				fa.write(output, getUnarmedDamageString(true, true));
			}
			// SPELLMEMx.x.x.x.LABEL classNum.bookNum.level.spellnumber
			// LABEL is TIMES,NAME,RANGE,etc. if not supplied it defaults to NAME
			else if (aString.startsWith("SPELLMEM"))
			{
				StringTokenizer aTok = new StringTokenizer(aString.substring(8), ".", false);
				int classNum = Integer.parseInt(aTok.nextToken());
				int bookNum = Integer.parseInt(aTok.nextToken());
				int spellLevel = Integer.parseInt(aTok.nextToken());
				int spellNumber = Integer.parseInt(aTok.nextToken());
				String aLabel = "NAME";
				if (aTok.hasMoreTokens())
					aLabel = aTok.nextToken();
				String altLabel = "";
				if (aTok.hasMoreTokens())
					altLabel = aTok.nextToken();
				PCClass aClass = getSpellClassAtIndex(classNum);
				if (aClass == null && existsOnly == true)
					noMoreItems = true;
				if (aClass != null)
				{
					String bookName = "";
					int i = 0;
					if (bookNum == -1 || aClass.memorizeSpells == false)
					{
						bookName = "Known Spells";
						i = spellBooks().size();
					}
					for (; i < spellBooks().size(); i++)
					{
						bookName = (String)spellBooks().get(i);
						if (!bookName.equals("Known Spells"))
							bookNum--;
						if (bookNum == -1)
							break;
						bookName = "";
					}
					if (aClass.memorizeSpells == false)
						bookName = "Known Spells";
					if (!bookName.equals(""))
					{
						Spell aSpell = null;
						int j = spellNumber;
						boolean moreSpells = false;
						for (i = 0; i < aClass.spellList().size(); i++)
						{
							aSpell = (Spell)aClass.spellList().get(i);
							String classString = aClass.keyName();
							if (aClass.castAs.length() > 0)
								classString = aClass.castAs;
							if (classString.equals("Domain"))
								classString = domainClassName();
							if (aSpell.spellBooks().contains(bookName))
							{
								if (aSpell.levelForClass(classString) >= spellLevel)
									moreSpells = true;
								if (aSpell.levelForClass(classString) == spellLevel)
									spellNumber--;
							}
							if (spellNumber == -1)
								break;
						}
						if (inLabel == true && moreSpells == false && checkBefore == true)
							canWrite = false;
						if (spellNumber == -1 && aSpell != null)
						{
							Spell bSpell = null;
							for (e = Globals.spellList.iterator(); e.hasNext();)
							{
								bSpell = (Spell)e.next();
								if (bSpell.name().equals(aSpell.name()))
									break;
								bSpell = null;
							}
							if (aLabel.equals("NAME"))
								fa.write(output, aSpell.name());
							else if (aLabel.equals("TIMES"))
								fa.write(output, aSpell.timesForSpellBook(bookName).toString());
							else if (bSpell != null)
							{
								if (aLabel.equals("RANGE"))
									fa.write(output, bSpell.range());
								else if (aLabel.equals("COMPONENTS"))
									fa.write(output, bSpell.componentList());
								else if (aLabel.equals("CASTINGTIME"))
									fa.write(output, bSpell.castingTime());
								else if (aLabel.equals("DURATION"))
									fa.write(output, bSpell.duration());
								else if (aLabel.equals("EFFECT"))
									fa.write(output, bSpell.effect());
								else if (aLabel.equals("EFFECTTYPE"))
									fa.write(output, bSpell.effectType());
								else if (aLabel.equals("SAVEINFO"))
									fa.write(output, bSpell.saveInfo());
								else if (aLabel.equals("SCHOOL"))
									fa.write(output, bSpell.school());
								else if (aLabel.equals("SUBSCHOOL"))
									fa.write(output, bSpell.subschool());
								else if (aLabel.equals("SR"))
									fa.write(output, bSpell.SR());
								else if (aLabel.startsWith("BONUSSPELL"))
								{
									String sString = "*";
									if (aLabel.length() > 10)
										sString = aLabel.substring(10);
									if (bSpell.isInSpecialty(aClass.specialtyList))
										fa.write(output, sString);
									else
										fa.write(output, altLabel);
								}
							}
						}
						else if (existsOnly == true)
							noMoreItems = true;
					}
					else if (existsOnly == true)
						noMoreItems = true;
				}
			}
			else if (aString.startsWith("SPELLLIST"))
			//SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
			{
				int cmp = 0;
				if (aString.substring(9, 13).equals("TYPE"))
					cmp = 3;
				else if (aString.substring(9, 13).equals("BOOK"))
					cmp = 2;
				else if (aString.substring(9, 14).equals("KNOWN"))
					cmp = 1;
				else if (aString.substring(9, 13).equals("CAST"))
					cmp = 0;
				else if (aString.substring(9, 14).equals("CLASS"))
					cmp = 4;
				else if (aString.substring(9, 11).equals("DC"))
					cmp = 5;
				else
					cmp = -1;
				int i = 13;
				if (cmp == 1 || cmp == 4)
					i = 14;
				else if (cmp == 5)
					i = 11;
				anInt = null;
				if (cmp != 3 && cmp != 4)
					anInt = new Integer(aString.substring(i + 2, i + 3));
				else
					anInt = new Integer(0);
				int level = anInt.intValue();
				anInt = new Integer(aString.substring(i, i + 1));
				i = anInt.intValue(); // class index
				int y = 0;
				PCClass aClass = getSpellClassAtIndex(i);
				if (aClass != null)
				{
					int stat = -1;
					if (aClass.spellBaseStat().length() > 2)
						stat = statNames.lastIndexOf(aClass.spellBaseStat()) / 3;
					int knownNum = 0;
					int spellNum = aClass.spellList().size();
					String castNum = aClass.getCastForLevelString(aClass.level().intValue(), level, "Known Spells");
					knownNum = aClass.getKnownForLevel(aClass.level().intValue(), level);
					spellNum = aClass.spellList().size();
					String cString = aClass.keyName();
					if (aClass.castAs.length() > 0)
						cString = aClass.castAs;
					if (cString.startsWith("Domain"))
						cString = domainClassName();
					if (spellNum == 0 && !aClass.spellType().equals("Divine"))
						return 0;
					switch (cmp)
					{
						case 0:
							fa.write(output, castNum);
							break;
						case 1:
							anInt = new Integer(knownNum);
							fa.write(output, anInt.toString());
							break;
						case 2:
							for (Iterator se = aClass.spellList().iterator(); se.hasNext();)
							{
								Spell sp = (Spell)se.next();
								aString = cString;
								if (sp.levelForClass(aString) == level)
								{
									if (y++ > 0)
										fa.write(output, ", ");
									fa.write(output, sp.name());
								}
								if (y == 0 && existsOnly == true)
									noMoreItems = true;
							}
							break;
						case 3:
							fa.write(output, aClass.spellType());
							break;
						case 4:
							fa.write(output, aClass.name());
							break;
						case 5:
							if (stat == -1)
								fa.write(output, "-1");
							else
							{
								String statString = statNames.substring(stat * 3, stat * 3 + 3);
								int a = adjStats(stat) / 2 - 5;
								if (statString.equals(aClass.spellBaseStat()))
									a += getTotalBonusTo("STAT", "BASESPELLSTAT", true) / 2;
								a += getTotalBonusTo("STAT", "CAST=" + statString, true) / 2;
								fa.write(output, new Integer(10 +
									getTotalBonusTo("STAT", aClass.name(), true) / 2 +
									getTotalBonusTo("SPELL", "DC", true) +
									level + a).toString());
							}
					}
				}
			}
			else if (aString.equals("HP"))
				fa.write(output, new Integer(hitPoints()).toString());
			else if (aString.equals("LANGUAGES"))
			{
				int c = 0;
				for (setIter = languagesList().iterator(); setIter.hasNext();)
				{
					if (c > 0)
						fa.write(output, ", ");
					fa.write(output, (String)setIter.next());
					c++;
				}
			}
			else if (aString.equals("WEAPONPROFS"))
			{
				int c = 0;
				for (setIter = weaponProfList().iterator(); setIter.hasNext();)
				{
					if (c > 0)
						fa.write(output, "; ");
					fa.write(output, (String)setIter.next());
					c++;
				}
			}
			else if (aString.startsWith("ARMOR"))
			{
				anInt = new Integer(aString.substring(5, aString.lastIndexOf('.')));
				int i = 0;
				StringTokenizer aTok = new StringTokenizer(aString, ".", false);
				aString = aTok.nextToken();
				aString = aTok.nextToken();
				ArrayList aArrayList = getEquipmentOfType("Armor", 3);
				ArrayList bArrayList = getEquipmentOfType("Shield", 3);
				for (e = bArrayList.iterator(); e.hasNext();)
					aArrayList.add(e.next());
				if (anInt.intValue() >= aArrayList.size() - 1 && existsOnly == true)
				{
					len = 0;
					noMoreItems = true;
				}
				if (anInt.intValue() < aArrayList.size())
				{
					Equipment eq = (Equipment)aArrayList.get(anInt.intValue());
					if (aString.startsWith("NAME"))
					{
						if (eq.isEquipped())
							fa.write(output, "*");
						fa.write(output, eq.name());
					}
					else if (aString.startsWith("TOTALAC"))
					{
						if (eq.acMod().intValue() > 0)
							fa.write(output, "+");
						fa.write(output, eq.acMod().toString());
					}
					else if (aString.startsWith("BASEAC"))
					{
						if (eq.acMod().intValue() > 0)
							fa.write(output, "+");
						fa.write(output, eq.acMod().toString());
					}
					else if (aString.startsWith("MAXDEX"))
					{
						if (eq.maxDex().intValue() > 0)
							fa.write(output, "+");
						fa.write(output, eq.maxDex().toString());
					}
					else if (aString.startsWith("ACCHECK"))
					{
						if (eq.acCheck().intValue() > 0)
							fa.write(output, "+");
						fa.write(output, eq.acCheck().toString());
					}
					else if (aString.startsWith("SPELLFAIL"))
					{
						fa.write(output, eq.spellFailure().toString());
					}
					else if (aString.startsWith("MOVE"))
					{
						aTok = new StringTokenizer(eq.moveString(), ",", false);
						aString = "";
						if ((size().equals("M") || size().equals("S")) &&
							aTok.countTokens() > 0)
						{
							aString = aTok.nextToken();
							if (size().equals("S") && aTok.countTokens() > 1)
								aString = aTok.nextToken();
						}
						fa.write(output, aString);
					}
				}
			}
			else if (aString.startsWith("WEAPON"))
			{
				if (aString.substring(6, 7).equals("P"))
					anInt = new Integer(-1); // primary
				else if (aString.substring(6, 7).equals("O"))
					anInt = new Integer(-2); // off-hand
				else if (aString.substring(6, 7).equals("H"))
					anInt = new Integer(-3); // unarmed
				else
					anInt = new Integer(aString.substring(6, aString.lastIndexOf('.')));
				int i = 0;
				StringTokenizer aTok = new StringTokenizer(aString, ".", false);
				aString = aTok.nextToken();
				aString = aTok.nextToken();
				Equipment eq = null;
				if (anInt.intValue() == -1)
				{
					i = -1;
					eq = primaryWeapon;
				}
				else if (anInt.intValue() == -2)
				{
					i = -2;
					eq = secondaryWeapon[0];
				}
				else if (anInt.intValue() == -3)
				{
					i = -3;
					eq = getEquipmentNamed("Unarmed Strike");
				}
				else
				{
					ArrayList aArrayList = getEquipmentOfType("Weapon", 3);
					if (anInt.intValue() < aArrayList.size())
						eq = (Equipment)aArrayList.get(anInt.intValue());
					if (anInt.intValue() == aArrayList.size() - 1 && existsOnly == true)
						noMoreItems = true;
				}
				if (eq != null)
				{
					boolean isDouble = (eq.getHand() == Equipment.BOTH_HANDS &&
						eq.typeStringContains("DOUBLE"));
					int index = 0;
					Integer bInt = new Integer(0);
					if (aString.startsWith("NAME"))
					{
						if (eq.isEquipped())
							fa.write(output, "*");
						fa.write(output, eq.name());
					}
					else if (aString.startsWith("LONGNAME"))
					{
						if (eq.isEquipped())
							fa.write(output, "*");
						fa.write(output, eq.longName());
					}
					else if (aString.startsWith("ATTACKS"))
						fa.write(output, eq.attacks().toString());
					else if (aString.startsWith("CRIT"))
					{
						int mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEMULT", true);
						bInt = new Integer(eq.critRange());
						if (mult > 0)
							bInt = new Integer(bInt.intValue() * mult);
						bInt = new Integer(bInt.intValue() + getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEADD", true));
						bInt = new Integer(21 - bInt.intValue());
						fa.write(output, bInt.toString());
						if (bInt.intValue() < 20)
							fa.write(output, "-20");
					}
					else if (aString.startsWith("MULT"))
					{
						int mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITMULTADD", true);
						bInt = new Integer(eq.critMult().substring(1));
						bInt = new Integer(bInt.intValue() + mult);
						fa.write(output, bInt.toString());
						if (isDouble && eq.altCrit.length() > 0)
						{
							mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITMULTADD", true);
							bInt = new Integer(eq.altCrit.substring(1));
							bInt = new Integer(bInt.intValue() + mult);
							fa.write(output, "/" + bInt.toString());
						}
					}
					else if (aString.startsWith("RANGE"))
						fa.write(output, eq.range().toString() + "'");
					else if (aString.startsWith("TYPE"))
					{
						if (eq.typeStringContains("BLUDGEONING"))
							fa.write(output, "B");
						if (eq.typeStringContains("PIERCING"))
							fa.write(output, "P");
						if (eq.typeStringContains("SLASHING"))
							fa.write(output, "S");
					}
					else if (aString.startsWith("HIT") || aString.startsWith("TOTALHIT"))
					{
						String mString = getAttackString(0, adjStats(0) / 2 - 5);
						String rString = getAttackString(0, adjStats(1) / 2 - 5);
						if (eq.typeStringContains("MONK"))
						{
							String m1String = getAttackString(2, adjStats(0) / 2 - 5);
							if (m1String.length() > mString.length())
								mString = m1String;
						}
						index = 0;
						int secondaryBonus = 0;
						int primaryBonus = 0;
						if (isPrimaryWeapon(eq) || isSecondaryWeapon(eq) || isDouble)
						{
							if (eq.getHand() != Equipment.TWOWEAPON_HANDS && isSecondaryWeapon(eq) && primaryWeapon != null)
								index = -10;
							else if (isSecondaryWeapon(eq) && primaryWeapon == null)
								index = -4;
							else if ((secondaryWeapon != null && secondaryWeapon[0]!=null) || isDouble)
								index = -6;
							if (primaryWeapon != null && secondaryWeapon[0] != null && Globals.getWeaponProfNamed(secondaryWeapon[0].profName()).isLight() || isDouble)
							{
								index += 2;
							}
							if (isDouble || (primaryWeapon != null && isSecondaryWeapon(eq)))
								secondaryBonus = getTotalBonusTo("COMBAT", "TOHIT-SECONDARY", true);
							if (isDouble || (secondaryWeapon != null && isPrimaryWeapon(eq)))
								primaryBonus = getTotalBonusTo("COMBAT", "TOHIT-PRIMARY", true);
						}
						WeaponProf wp = null;
						wp = Globals.getWeaponProfNamed(eq.profName());
						index += primaryBonus;
						for (Iterator ei = eq.typeVector().iterator(); ei.hasNext();)
						{
							String tString = ei.next().toString();
							index += getTotalBonusTo("TOHIT", "TYPE=" + tString, true);
						}
						if (!isDouble && eq.getHand() != Equipment.TWOWEAPON_HANDS)
							index += secondaryBonus;
						index += eq.bonusTo("WEAPON", "TOHIT");
						if (wp == null || !weaponProfList().contains(wp.name()))
							index -= 4; // non-proficiency penalty
						if (wp != null)
						{
							if (Globals.debugMode)
								System.out.println(wp.name() + " " + getTotalBonusTo("WEAPONPROF=" + wp.name(), "TOHIT", true));
							index += getTotalBonusTo("WEAPONPROF=" + wp.name(), "TOHIT", true);
						}
						Integer numInt = new Integer(-1);
						if (aString.startsWith("TOTALHIT") && anInt.intValue() > -1)
						{
							if (!aString.endsWith("TOTALHIT"))
								numInt = new Integer(aString.substring(8));
						}
						int k = index;
						bInt = new Integer(index + weaponMod);
						StringTokenizer zTok = null;
						if (eq.typeStringContains("MELEE"))
							zTok = new StringTokenizer(mString, "+/", false);
						else if (eq.typeStringContains("RANGED"))
							zTok = new StringTokenizer(rString, "+/", false);
						int count = 0;
						int x = 0;
						int max = 1 + getTotalBonusTo("COMBAT", "SECONDARYATTACKS", true);
						int extra_attacks = eq.bonusTo("COMBAT", "ATTACKS");  // BONUS:COMBAT|ATTACKS|* represent extra attacks at BaB
						// such as from a weapon of 'Speed'
						if (primaryWeapon == null)
							max = 100;
						do
						{
							index = 0;
							if (isSecondaryWeapon(eq) && x >= max)
								break;
							if (zTok != null)
							{
								if (zTok.hasMoreTokens())
									index = Integer.parseInt(zTok.nextToken());
								else
									break;
							}
							numInt = new Integer(numInt.intValue() - 1);
							if (numInt.intValue() < 0)
							{
								if (count > 0)
									fa.write(output, "/");
								if (index + bInt.intValue() > 0)
									fa.write(output, "+");
								fa.write(output, new Integer(bInt.intValue() + index).toString());

								// Here we handle extra attacks provided by the BONUS:COMBAT|ATTACKS|* tag
								// These are at the characters BaB
								while (extra_attacks-- > 0)
								{
									fa.write(output, "/");
									if (index + bInt.intValue() > 0)
										fa.write(output, "+");
									fa.write(output, new Integer(bInt.intValue() + index).toString());
								}

								if (x == 0 && (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS))
								{
									fa.write(output, "/");
									if (index - primaryBonus + bInt.intValue() + secondaryBonus - 4 > 0)
										fa.write(output, "+");
									fa.write(output, new Integer(index - primaryBonus + bInt.intValue() + secondaryBonus - 4).toString());
								}
								count++;
							}
							if (numInt.intValue() < -1)
								numInt = new Integer(-1);
							else if (numInt.intValue() == -1)
								numInt = new Integer(-2);
							x++;
						} while (numInt.intValue() >= -1);

						if (anInt.intValue() == -1 && primaryWeapon.equals(secondaryWeapon[0]))
						{
							if (aString.equals("TOTALHIT"))
							{
								StringTokenizer bTok = null;
								if (eq.typeStringContains("MELEE"))
									bTok = new StringTokenizer(mString, "/", false);
								else if (eq.typeStringContains("RANGED"))
									bTok = new StringTokenizer(rString, "/", false);
								if (bTok != null)
									k += Integer.parseInt(bTok.nextToken());
							}
							bInt = new Integer(k);
							fa.write(output, "/");
							if (k > 0)
								fa.write(output, "+");
							fa.write(output, bInt.toString());
						}
					}
					else if (aString.startsWith("CATEGORY"))
					{
						if (eq.typeStringContains("SIMPLE"))
							fa.write(output, "SIMPLE");
						else if (eq.typeStringContains("MARTIAL"))
							fa.write(output, "MARTIAL");
						else if (eq.typeStringContains("EXOTIC"))
							fa.write(output, "EXOTIC");
						else
							fa.write(output, "NON-STANDARD");
						fa.write(output, "-");
						if (eq.typeStringContains("MELEE"))
							fa.write(output, "MELEE");
						else if (eq.typeStringContains("RANGED"))
							fa.write(output, "RANGED");
						else
							fa.write(output, "NON-STANDARD");
					}
					else if (aString.startsWith("HAND"))
						fa.write(output, Equipment.getHandName(eq.getHand()));
					else if (aString.startsWith("MAGICDAMAGE"))
					{
						anInt = new Integer(eq.bonusTo("WEAPON", "DAMAGE") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE"));
						if (anInt.intValue() > 0)
							fa.write(output, "+");
						fa.write(output, anInt.toString());
					}
					else if (aString.startsWith("MAGICHIT"))
					{
						anInt = new Integer(eq.bonusTo("WEAPON", "TOHIT") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "TOHIT"));
						if (anInt.intValue() > 0)
							fa.write(output, "+");
						fa.write(output, anInt.toString());
					}
					else if (aString.startsWith("FEAT"))
					{
						anInt = new Integer(getFeatBonusTo("WEAPON", "TOHIT", true) + getFeatBonusTo("WEAPONPROF=" + eq.profName(), "TOHIT", true));
						if (anInt.intValue() > 0)
							fa.write(output, "+");
						fa.write(output, anInt.toString());
					}
					else if (aString.endsWith("DAMAGE"))
					{
						String bString = new String(eq.damage());
						int bonus = 0;
						if (eq.typeStringContains("MONK") && eq.typeStringContains("UNARMED"))
						{
							String cString = getUnarmedDamageString(false, false);
							StringTokenizer bTok = new StringTokenizer(bString, " d+-", false);
							bTok.nextToken();
							String b1String = bTok.nextToken();
							bTok = new StringTokenizer(cString, " d+-", false);
							bTok.nextToken();
							String c1String = bTok.nextToken();
							if (Integer.parseInt(b1String) < Integer.parseInt(c1String))
								bString = cString;
						}
						bInt = new Integer(0);
						if (!aString.startsWith("BASE"))
						{
							for (index = 0; index < bString.length(); index++)
							{
								if (bString.charAt(index) == '+')
								{
									bInt = new Integer(bString.substring(index + 1));
									break;
								}
								else if (bString.charAt(index) == '-')
								{
									bInt = new Integer(bString.substring(index));
									break;
								}
							}
							if (eq.typeStringContains("MELEE") || eq.typeStringContains("THROWN"))
							{
								if (isSecondaryWeapon(eq) && eq != primaryWeapon)
									bInt = new Integer(bInt.intValue() + (adjStats(0) / 2 - 5) / 2);
								else
									bInt = new Integer(bInt.intValue() + adjStats(0) / 2 - 5);
							}
							bonus = eq.bonusTo("WEAPON", "DAMAGE");
							for (Iterator ei = eq.typeVector().iterator(); ei.hasNext();)
								bonus += getTotalBonusTo("DAMAGE", "TYPE=" + ei.next().toString(), true);
							WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
							if (!isDouble && eq.typeStringContains("MELEE") &&
								wp != null && adjStats(0) / 2 > 5 &&
								eq.getHand() == Equipment.BOTH_HANDS)
							{
								if (wp.isOneHanded() && !wp.isLight())
									bonus += (adjStats(0) / 2 - 5) / 2;
								if (wp.isTwoHanded())
									bonus += (adjStats(0) / 2 - 5) / 2;
							}
							bInt = new Integer(bInt.intValue() + bonus + getTotalBonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE", true));
							bString = bString.substring(0, index);
						}
						fa.write(output, bString);
						if (bInt.intValue() > 0)
							fa.write(output, "+");
						if (bInt.intValue() != 0)
							fa.write(output, bInt.toString());
						if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS)
						{
							if (isDouble && eq.altDamage.length() > 0)
							{
								bInt = new Integer(0);
								bString = new String(eq.altDamage);
								if (bString.lastIndexOf("-") > -1)
								{
									bInt = new Integer(bString.substring(bString.lastIndexOf("-")));
									bString = bString.substring(0, bString.lastIndexOf("-"));
								}
								else if (bString.lastIndexOf("+") > -1)
								{
									bInt = new Integer(bString.substring(bString.lastIndexOf("+") + 1));
									bString = bString.substring(0, bString.lastIndexOf("+"));
								}
							}
							else if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
								bInt = new Integer(bInt.intValue() - (adjStats(0) / 2 - 5));
							bonus = (adjStats(0) / 2 - 5) / 2; // only get half strength bonus
							bInt = new Integer(bInt.intValue() + bonus);
							fa.write(output, "/");
							fa.write(output, bString);
							if (bInt.intValue() > 0)
								fa.write(output, "+");
							if (bInt.intValue() != 0)
								fa.write(output, bInt.toString());
						}
					}
					else if (aString.startsWith("SIZE"))
					{
						fa.write(output, eq.size());
					}
					else if (aString.startsWith("SPROP"))
					{
						fa.write(output, eq.getSpecialProperties());
					}
					else if (aString.startsWith("REACH"))
					{
						fa.write(output, "" + race.reach() + eq.reach());
					}
				}
				else if (existsOnly == true)
					noMoreItems = true;
			}
			else if (aString.startsWith("EQ"))
			{
				Collection aList = equipmentList().values();
				StringTokenizer aTok = null;
				int i = 0;
				if (aString.startsWith("EQTYPE"))
				{
					aTok = new StringTokenizer(aString.substring(6), ".", false);
					aList = this.getEquipmentOfType(aTok.nextToken(), 3);
					anInt = new Integer(aTok.nextToken());
				}
				else
				{
					aTok = new StringTokenizer(aString.substring(2), ".", false);
					anInt = new Integer(aTok.nextToken());
				}
				aString = aTok.nextToken();
				Equipment eq = null;
				if (anInt.intValue() >= 0 && anInt.intValue() < aList.size())
				{
					setIter = aList.iterator();
					for (int count = anInt.intValue(); count > 0; --count, setIter.next()) ;
					eq = (Equipment)setIter.next();
				}
				if (existsOnly && (anInt.intValue() < 0 || anInt.intValue() >= aList.size() - 1))
					noMoreItems = true;
				if (eq != null)
				{
					if (aString.equals("LONGNAME"))
						fa.write(output, eq.longName());
					else if (aString.equals("NAME"))
						fa.write(output, eq.name());
					else if (aString.equals("WT"))
						fa.write(output, eq.weight().toString());
					else if (aString.equals("COST"))
						fa.write(output, eq.cost().toString());
					else if (aString.equals("QTY"))
						fa.write(output, eq.qty().toString());
					else if (aString.equals("EQUIPPED") && eq.isEquipped())
						fa.write(output, "Y");
					else if (aString.equals("EQUIPPED") && !eq.isEquipped())
						fa.write(output, "N");
					else if (aString.equals("CARRIED"))
					{
						fa.write(output, String.valueOf(eq.numberCarried()));
					}
					else if (aString.equals("ACMOD"))
						fa.write(output, eq.acMod().toString());
					else if (aString.equals("MAXDEX"))
						fa.write(output, eq.maxDex().toString());
					else if (aString.equals("ACCHECK"))
						fa.write(output, eq.acCheck().toString());
					else if (aString.equals("MOVE"))
						fa.write(output, eq.moveString());
					else if (aString.equals("TYPE"))
						fa.write(output, eq.typeString());
					else if (aString.startsWith("TYPE") && aString.length() > 4)
					{
						int x = Integer.parseInt(aString.substring(4));
						fa.write(output, eq.typeIndex(x));
					}
					else if (aString.equals("SPELLFAILURE"))
						fa.write(output, eq.spellFailure().toString());
					else if (aString.equals("SIZE"))
						fa.write(output, eq.size());
					else if (aString.equals("DAMAGE"))
						fa.write(output, eq.damage());
					else if (aString.equals("CRITRANGE"))
						fa.write(output, eq.critRange());
					else if (aString.equals("CRITMULT"))
						fa.write(output, eq.critMult());
					else if (aString.equals("ALTDAMAGE"))
						fa.write(output, eq.altDamage());
					else if (aString.equals("ALTCRIT"))
						fa.write(output, eq.altCrit());
					else if (aString.equals("RANGE"))
						fa.write(output, eq.range().toString());
					else if (aString.equals("ATTACKS"))
						fa.write(output, eq.attacks().toString());
					else if (aString.equals("PROF"))
						fa.write(output, eq.profName());
				}
			}
			else if (aString.equals("TOTAL.WEIGHT"))
			{

				Float totalWeight = totalWeight();
				fa.write(output, totalWeight.toString() + " lbs");
			}
			else if (aString.equals("TOTAL.CAPACITY"))
			{
				fa.write(output, Globals.maxLoadForStrengthAndSize(adjStats(0), size()).toString());
			}
			else if (aString.equals("TOTAL.LOAD"))
			{
				int i = Globals.loadTypeForStrength(adjStats(0), totalWeight());
				switch (i)
				{
					case 0:
						fa.write(output, "Light");
						break;
					case 1:
						fa.write(output, "Medium");
						break;
					case 2:
						fa.write(output, "Heavy");
						break;
					default:
						fa.write(output, "Overload");
						break;
				}
			}
			else if (aString.startsWith("MISC."))
			{
				int i = -1;
				if (aString.substring(5).startsWith("FUNDS"))
					i = 0;
				else if (aString.substring(5).startsWith("COMPANIONS"))
					i = 1;
				else if (aString.substring(5).startsWith("MAGIC"))
					i = 2;
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = "";

				if (i >= 0)
				{
					ArrayList stringList = getLineForMiscList(i);
					for (i = 0; i < stringList.size(); i++)
						fa.write(output, (String)stringList.get(i) + aString);
				}
			}
			else if (aString.equals("GOLD"))
				fa.write(output, gold().toString());
			else if (aString.equals("DEFENSE"))
				fa.write(output, defense().toString());
			else if (aString.startsWith("DEFENSE.CLASS"))
			{
				anInt = new Integer(aString.substring(13));
				if (anInt.intValue() >= classList.size() && existsOnly == true)
					noMoreItems = true;
				if (anInt.intValue() >= classList.size())
					return 0;
				PCClass aClass = (PCClass)classList.get(anInt.intValue());
				fa.write(output, aClass.defense(anInt.intValue()).toString());
			}
			else if (aString.equals("WOUNDPOINTS"))
				fa.write(output, woundPoints().toString());
			else if (aString.equals("REPUTATION"))
				fa.write(output, reputation().toString());
			else if (aString.equals("POOL.CURRENT"))
				fa.write(output, new Integer(poolAmount).toString());
			else if (aString.equals("POOL.COST"))
				fa.write(output, new Integer(costPool).toString());
			else if (aString.equals("PLAYERNAME"))
				fa.write(output, playersName);
			else if (aString.equals("VISION"))
				fa.write(output, race().vision());
			else if (aString.startsWith("WEIGHT."))
			{
				int i = 1;
				if (aString.endsWith("MEDIUM"))
					i = 2;
				else if (aString.endsWith("HEAVY"))
					i = 3;
				fa.write(output, new Float(i * Globals.maxLoadForStrengthAndSize(adjStats(0), size()).intValue() / 3).toString());
			}
			else if (aString.equals("RACE.ABILITYLIST"))
			{
				int i = 0;
				for (e = race().specialAbilities().iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					if (i++ > 0)
						fa.write(output, ", ");
					fa.write(output, bString);
				}
			}
			else if (aString.startsWith("VAR."))
			{
				boolean isMin = aString.lastIndexOf(".MINVAL") > -1;
				int index = aString.length();
				if (aString.lastIndexOf(".INTVAL") > -1)
					index = aString.lastIndexOf(".INTVAL");
				if (aString.lastIndexOf(".MINVAL") > -1)
					index = Math.min(index, aString.lastIndexOf(".MINVAL"));
				Float val = getVariable(aString.substring(4, index), !isMin, true, "", "");
				if (val.doubleValue() > 0.0)
					fa.write(output, "+");
				if (aString.lastIndexOf(".INTVAL") > -1)
					fa.write(output, val.toString().substring(0, val.toString().lastIndexOf(".")));
				else
					fa.write(output, val.toString());
			}
			else
			{
				len = aString.trim().length();
				fa.write(output, aString);
			}
			return len;
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error replacing " + aString);
			return 0;
		}
	}
	// e.g. getVariableValue("3+CHA","CLASS:Cleric","1") for Turn Undead
	public Float getVariableValue(String aString, String src, String subSrc)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf("(") > -1)
		{
			int x = innerMostStringStart(aString);
			int y = innerMostStringEnd(aString);
			String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(bString, src, subSrc) + aString.substring(y + 1);
		}
		String delimiter = "+-/*";
		String valString = "";
		int mode = 0; //0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0; //1,11=min, 2,12=max, 3,13=req, 10 = int
		if (aString.startsWith(".IF."))
		{
			StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			String bString = "";
			Float val1 = null; // first value
			Float val2 = null; // other value in comparison
			Float valt = null; // value if comparison is true
			Float valf = null; // value if comparison is false
			int comp = 0;
			while (aTok.hasMoreTokens())
			{
				String cString = aTok.nextToken();
				if (cString.equals("GT") || cString.equals("GTEQ") || cString.equals("EQ") || cString.equals("LTEQ") || cString.equals("LT"))
				{
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
					if (cString.equals("LT"))
						comp = 1;
					else if (cString.equals("LTEQ"))
						comp = 2;
					else if (cString.equals("EQ"))
						comp = 3;
					else if (cString.equals("GT"))
						comp = 4;
					else if (cString.equals("GTEQ")) comp = 5;
				}
				else if (cString.equals("THEN"))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else if (cString.equals("ELSE"))
				{
					valt = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else
					bString = bString + cString;
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString, src, subSrc);
				total = valt;
				switch (comp)
				{
					case 1:
						if (val1.doubleValue() >= val2.doubleValue())
							total = valf;
						break;
					case 2:
						if (val1.doubleValue() > val2.doubleValue())
							total = valf;
						break;
					case 3:
						if (val1.doubleValue() != val2.doubleValue())
							total = valf;
						break;
					case 4:
						if (val1.doubleValue() <= val2.doubleValue())
							total = valf;
						break;
					case 5:
						if (val1.doubleValue() < val2.doubleValue())
							total = valf;
						break;
					default:
						System.out.println("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				if (Globals.debugMode)
					System.out.println("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
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
					valString = valString.substring(0, valString.length() - 1);
				if (valString.length() > 2 && valString.startsWith("%") && valString.endsWith("%"))
				{
					if (Globals.debugMode)
						System.out.println(valString + " " + loopVariable + " " + loopValue);
					if (loopVariable.equals("")) // start the loop
					{
						StringTokenizer lTok = new StringTokenizer(valString, "%:", false);
						loopVariable = lTok.nextToken();
						String vString = loopVariable.toString();
						decrement = 1;
						if (lTok.hasMoreTokens())
							decrement = Integer.parseInt(lTok.nextToken());
						loopValue = 0;
						if (hasVariable(loopVariable))
						{
							loopValue = getVariable(loopVariable, true, true, "", "").intValue();
							loopVariable = vString;
						}
					}
					if (loopValue == 0)
						loopVariable = "";
					valString = new Integer(loopValue).toString();
					if (Globals.debugMode)
						System.out.println("loopVariable=" + loopVariable + " loopValue=" + loopValue);
				}
				if (valString.equals("SPELLBASESTATSCORE"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
						valString = aClass.spellBaseStat() + "SCORE";
					else
						valString = "0";
				}
				if (valString.equals("SPELLBASESTAT"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
						valString = aClass.spellBaseStat();
					else
						valString = "0";
				}
				if (valString.length() > 0 && statNames.lastIndexOf(valString) > -1)
				{
					int stat = statNames.lastIndexOf(valString) / 3;
					valString = new Integer(adjStats(stat) / 2 - 5).toString();
					if (Globals.debugMode)
						System.out.println("MOD=" + valString);
				}
				else if (valString.length() == 8 && statNames.lastIndexOf(valString.substring(0, 3)) > -1
					&& valString.endsWith("SCORE"))
				{
					int stat = statNames.lastIndexOf(valString.substring(0, 3)) / 3;
					valString = new Integer(adjStats(stat)).toString();
					if (Globals.debugMode)
						System.out.println("SCORE=" + valString);
				}
				else if (valString.equals("CL"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
						valString = aClass.level().toString();
					else
						valString = "0";
				}
				else if (valString.equals("TL"))
					valString = new Integer(totalLevels()).toString();
				else if (valString.equals("SHIELDACHECK"))
				{
					ArrayList aArrayList = getEquipmentOfType("Shield", 1);
					if (aArrayList.size() > 0)
						valString = ((Equipment)aArrayList.get(0)).acCheck().toString();
					else
						valString = "0";
				}
				else if (valString.equals("COUNT[SKILLS]"))
				{
					skillList.trimToSize();
					valString = new Integer(skillList().size()).toString();
				}
				else if (valString.equals("COUNT[FEATS]"))
				{
					featList.trimToSize();
					valString = new Integer(featList.size()).toString();
				}
				else if (valString.equals("COUNT[CLASSES]"))
				{
					classList.trimToSize();
					valString = new Integer(classList.size()).toString();
				}
				else if (valString.equals("COUNT[DOMAINS]"))
				{
					domainList.trimToSize();
					valString = new Integer(domainList.size()).toString();
				}
				else if (valString.equals("COUNT[EQUIPMENT]"))
				{
					valString = Integer.toString(equipmentList.size());
				}
				else if (valString.equals("COUNT[SA]"))
				{
					specialAbilityList.trimToSize();
					valString = new Integer(specialAbilityList().size()).toString();
				}
				else if (valString.length() > 0)
				{
					if (hasVariable(valString))
						valString = getVariable(valString, true, true, "", "").toString();
					else
					{
						double a = 0;
						try
						{
							a = new Float(valString).doubleValue();
						}
						catch (NumberFormatException exc)
						{
							a = getTotalBonusTo("VAR", valString, true);
						}
						if (a != 0.0)
							valString = new Float(a).toString();
					}
				}
				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						valString = new Integer(getVariableValue(valString.substring(0, valString.length() - 6), "", "").intValue()).toString();
					}
					if (valString.endsWith(".INTVAL"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 7), "", "").toString();
//            nextMode = 0;
						endMode += 10;
					}
					if (valString.endsWith("MIN"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
						nextMode = 0;
						endMode += 1;
					}
					else if (valString.endsWith("MAX"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
						nextMode = 0;
						endMode += 2;
					}
					else if (valString.endsWith("REQ"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
						nextMode = 0;
						endMode += 3;
					}
					else if (aString.charAt(i) == '+')
						nextMode = 0;
					else if (aString.charAt(i) == '-')
						nextMode = 1;
					else if (aString.charAt(i) == '*')
						nextMode = 2;
					else if (aString.charAt(i) == '/')
						nextMode = 3;
				}
				try
				{
					if (valString.length() > 0)
						switch (mode)
						{
							case 0:
								total = new Float(total.doubleValue() + new Float(valString).doubleValue());
								break;
							case 1:
								total = new Float(total.doubleValue() - new Float(valString).doubleValue());
								break;
							case 2:
								total = new Float(total.doubleValue() * new Float(valString).doubleValue());
								break;
							case 3:
								total = new Float(total.doubleValue() / new Float(valString).doubleValue());
								break;
						}
				}
				catch (Exception exc)
				{
					JOptionPane.showMessageDialog(null, "Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")");
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
				total = new Float(Math.min(total.doubleValue(), total1.doubleValue()));
			if (endMode % 10 == 2)
				total = new Float(Math.max(total.doubleValue(), total1.doubleValue()));
			if (endMode % 10 == 3)
			{
				if (total1.doubleValue() < total.doubleValue())
					total = new Float(0.0);
				else
					total = total1;
			}
		}
		if (endMode / 10 > 0)
			total = new Float(new Integer(total.intValue()).toString());
		return total;
	}

	public int innerMostStringStart(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
				current--;
		}
		return index;
	}

	public int innerMostStringEnd(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current > hi)
					hi = current;
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
					index = i;
				current--;
			}
		}
		return index;
	}

	/** <code>rollStats</code> roll 6 random stats. Will need to be changed if we
	 * ever go to using a truely variable number of stats. Also, at the moment, it
	 * rolls using 4d6 drop lowest method. Other methods can be implemented by
	 * changing the argument to something other than 0.
	 * Method:
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * @param method the method to be used for rolling.
	 */
	public void rollStats(int method)
	{
		int dice, stat = 0;
		int low, roll = 0;
		///Random roller = new Random();
		int[] currentStats = stats;
		for (stat = 0; stat < 6; stat++)
		{
			//low = 6;
			currentStats[stat] = 0;
			if (Globals.purchaseStatMode)
			{
				currentStats[stat] = 8;
				continue;
			}
			switch (method)
			{
				case 0:
					roll = 0;
					break;
				case 1:
					roll = RollingMethods.roll(4, 6, true);
					break;
				case 2:
					roll = RollingMethods.roll(3, 6);
					break;
				case 3:
					roll = RollingMethods.roll(5, 6, true);
					break;
				case 4:
					roll = RollingMethods.roll(4, 6, true, 1);
					break;
				case 5:
					roll = RollingMethods.roll(4, 6, true, 2);
					break;
				case 6:
					roll = RollingMethods.roll(3, 6, 5);
					break;
			}
			currentStats[stat] = currentStats[stat] + roll;
			// The following line should never happen.
			//if currentstats[stat] < 3) currentStat[stat] = 3;
		}
		this.setPoolAmount(0);
		this.costPool = 0;
		languagesList.clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	final private boolean includeSkill(Skill skill, int level)
	{
		return (level == 2) || skill.isRequired ||
			(skill.rank().floatValue() > 0) ||
			((level == 1) && skill.untrained().startsWith("Y"));
	}

	final private void addNewSkills(int level)
	{
		List addItems = new LinkedList();
		Iterator skillIter = Globals.skillList.iterator();
		while (skillIter.hasNext())
		{
			Skill aSkill = (Skill)skillIter.next();
			if (includeSkill(aSkill, level) &&
				(Globals.binarySearchPObject(skillList, aSkill.keyName()) ==
				null))
			{
				addItems.add(aSkill.clone());
			}
		}
		skillList.addAll(addItems);
	}

	final private void removeExcessSkills(int level)
	{
		Iterator skillIter = skillList.iterator();
		while (skillIter.hasNext())
		{
			Skill skill = (Skill)skillIter.next();
			if (!includeSkill(skill, level))
				skillIter.remove();
		}
	}

	final private void populateSkills(int level)
	{
		Globals.sortPObjectList(skillList);
		removeExcessSkills(level);
		addNewSkills(level);
	}

	public PlayerCharacter()
	{
		int i;
		s_sizes.put("F", size_array[0]);
		s_sizes.put("D", size_array[1]);
		s_sizes.put("T", size_array[2]);
		s_sizes.put("S", size_array[3]);
		s_sizes.put("M", size_array[4]);
		s_sizes.put("L", size_array[5]);
		s_sizes.put("H", size_array[6]);
		s_sizes.put("G", size_array[7]);
		s_sizes.put("C", size_array[8]);

		Globals.currentPC = this;
		for (i = 0; i < 6; i++)
			stats[i] = 0;
		setRace((Race)Globals.raceList.get(0));
		setName("");
		skillPoints = 0;
		feats = 0;
		rollStats(Globals.rollMethod);
		miscList.add("");
		miscList.add("");
		miscList.add("");
		addSpellBook("Known Spells");
		populateSkills(Globals.includeSkills);

	}
}
