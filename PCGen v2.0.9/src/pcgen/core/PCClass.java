/*
 * PCClass.java
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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PCClass extends PObject
{
  String alignments = new String();
  String subClassName = "None";
  String subClassString = "None";
  String prohibitedString = "None";
  int hitDie = 0;
  int skillPoints = 0;
  int initialFeats = 0;
  String spellBaseStat = "WIS";
  String spellType = "Divine";
  String attackBonusType = new String();
  String fortitudeCheckType = new String();
  String reflexCheckType = new String();
  String willCheckType = new String();
  ArrayList knownList = new ArrayList();
  ArrayList castList = new ArrayList();
  ArrayList uattList = new ArrayList();
  ArrayList udamList = new ArrayList();
  ArrayList acList = new ArrayList();
  ArrayList languageAutos = new ArrayList();
  ArrayList languageBonus = new ArrayList();
  ArrayList weaponProfAutos = new ArrayList();
  ArrayList weaponProfBonus = new ArrayList();
  Integer level = new Integer(0);
  Integer[] hitPointList = new Integer[1];
  ArrayList spellList = new ArrayList();
  ArrayList featList = new ArrayList();
  ArrayList vFeatList = new ArrayList();
  ArrayList addList = new ArrayList();
  ArrayList levelAbilityList = new ArrayList();
  ArrayList specialAbilityList = new ArrayList();
  ArrayList subSpecialAbilityList = new ArrayList();
  ArrayList umult = new ArrayList();
  Integer skillPool = new Integer(0);
  String goldString = new String();
  String specialsString = new String();
  ArrayList skillList = new ArrayList();
  String defenseString = "1,1";
  String reputationString = "1";
  String abbrev = new String();
  boolean memorizeSpells = true;
  int initMod = 0;
  public boolean multiPreReqs = false;
  String deityString = "ANY";
  ArrayList specialtyList = new ArrayList();
  int maxLevel = 20;
  ArrayList knownSpellsList = new ArrayList();
  String attackCycle = "";
  String castAs = "";
  int knownSpellsFromSpecialty = 0;
  String classType = null;
  String preRaceType = null;  //since I don't want this to be counted as making it a prestige class.
  boolean intModToSkills = true;
  int levelsPerFeat = 3;

  public Object clone()
  {
    PCClass aClass = (PCClass)super.clone();
    aClass.setSubClassName(subClassName());
    aClass.setSubClassString(subClassString());
    aClass.setProhibitedString(prohibitedString());
    aClass.setAlignments(alignments());
    aClass.setHitDie(hitDie);
    aClass.setSkillPoints(skillPoints);
    aClass.setInitialFeats(initialFeats);
    aClass.setSpellBaseStat(spellBaseStat);
    aClass.setSpellType(spellType);
    aClass.setAttackBonusType(attackBonusType);
    aClass.setFortitudeCheckType(fortitudeCheckType);
    aClass.setReflexCheckType(reflexCheckType);
    aClass.setWillCheckType(willCheckType);
    aClass.knownList = (ArrayList)knownList().clone();
    aClass.castList = (ArrayList)castList().clone();
    aClass.uattList = (ArrayList)uattList().clone();
    aClass.udamList = (ArrayList)udamList().clone();
    aClass.umult = (ArrayList)umult.clone();
    aClass.acList = (ArrayList)acList().clone();
    aClass.languageAutos = (ArrayList)languageAutos().clone();
    aClass.languageBonus = (ArrayList)languageBonus().clone();
    aClass.weaponProfAutos = (ArrayList)weaponProfAutos().clone();
    aClass.weaponProfBonus = (ArrayList)weaponProfBonus().clone();
    aClass.hitPointList = (Integer[])hitPointList().clone();
    aClass.spellList = (ArrayList)spellList().clone();
    aClass.featList = (ArrayList)featList().clone();
    aClass.vFeatList = (ArrayList)vFeatList().clone();

    aClass.levelAbilityList = (ArrayList)levelAbilityList.clone();
    for (Iterator it = levelAbilityList.iterator(); it.hasNext();)
    {
      LevelAbility ab = (LevelAbility)it.next();
      ab.setOwner(aClass);
    }

    aClass.specialAbilityList = (ArrayList)specialAbilityList().clone();
    aClass.subSpecialAbilityList = (ArrayList)subSpecialAbilityList().clone();
    aClass.setGoldString(goldString);
    aClass.setSpecialsString(specialsString);
    aClass.setDefenseString(defenseString);
    aClass.setReputationString(reputationString);
    aClass.abbrev = abbrev;
    aClass.memorizeSpells = memorizeSpells;
    aClass.multiPreReqs = multiPreReqs;
    aClass.isSpecified = isSpecified;
    aClass.deityString = deityString;
    aClass.maxLevel = maxLevel;
    aClass.knownSpellsList = (ArrayList)knownSpellsList.clone();
    aClass.attackCycle = attackCycle;
    aClass.castAs = castAs;
    aClass.classType = classType;
    aClass.preRaceType = preRaceType;
    aClass.intModToSkills = intModToSkills;
    aClass.levelsPerFeat = levelsPerFeat;
    aClass.initMod = initMod;
    return aClass;
  }

  /**
   * @return true if the character memorizes spells (wizard, cleric) false if not (sorcerer, bard)
   */
  public boolean memorizesSpells()
  {
    return memorizeSpells;
  }

  public boolean multiPreReqs()
  {
    return multiPreReqs;
  }

  public String qualifiedNameString()
  {
    String aString = null;
    if (Globals.currentPC != null && (!allowsAlignment(Globals.currentPC.alignment()) || !canBePrestige()))
      aString = "* ";
    else
      aString = "";
    if (aString != null)
    {
      aString = aString.concat(name);
    }
    else
    {
      aString = name;
    }
    return aString;
  }

  public String toString()
  {
    return name;
  }

  public void setName(String newName)
  {
    super.setName(newName);
    int i = 3;
    if (abbrev.equals(""))
    {
      if (newName.length() < 3)
        i = newName.length();
      abbrev = newName.substring(0, i);
    }
  }

  public void setCastAs(String aString)
  {
    castAs = aString;
  }

  public String castAs()
  {
    return castAs;
  }

  public String subClassName()
  {
    if (subClassName == null)
      subClassName = "";
    return subClassName;
  }

  public String displayClassName()
  {
    if (subClassName.length() > 0 && !subClassName.equals("None"))
      return subClassName;
    return name;
  }

  public void setSubClassName(String aString)
  {
    subClassName = aString;
  }

  public String subClassString()
  {
    return subClassString;
  }

  private void setSubClassString(String aString)
  {
    subClassString = aString;
  }

  public String prohibitedString()
  {
    return prohibitedString;
  }

  public void setProhibitedString(String aString)
  {
    prohibitedString = aString;
  }

  public boolean prohibitedStringContains(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);
    while (aTok.hasMoreTokens())
      if (aTok.nextToken().equals(aString))
        return true;
    return false;
  }

  public boolean prohibitedStringContains(ArrayList aList)
  {
    for (Iterator e = aList.iterator(); e.hasNext();)
      if (prohibitedStringContains((String)e.next()))
        return true;
    return false;
  }

  public ArrayList bonusList()
  {
    return bonusList;
  }

  public int getBonusTo(String type, String mname, int asLevel)
  {
    int i = 0;
    PlayerCharacter aPC = Globals.currentPC;

    for (Iterator e = bonusList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      StringTokenizer aTok = new StringTokenizer(aString, "|", false);
      int anInt = Integer.parseInt(aTok.nextToken());
      String aType = aTok.nextToken();
      if (!aType.equals(type))
        continue;
      String aName = aTok.nextToken();
      StringTokenizer bTok = new StringTokenizer(aName, ",", false);
      while (bTok.hasMoreTokens())
      {
        aName = bTok.nextToken();
        if (anInt <= asLevel && aType.equals(type) &&
          aName.equals(mname))
        {
          aString = aTok.nextToken();
          i += aPC.getVariableValue(aString, "", "").intValue();
        }
      }
    }
    return i;
  }

  public int bonusBasedOnStat(String type, String mname, int asLevel)
  {
    int i = -1;
    for (Iterator e = bonusList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      StringTokenizer aTok = new StringTokenizer(aString, "|", false);
      int anInt = Integer.parseInt(aTok.nextToken());
      if (anInt <= asLevel && aTok.nextToken().equals(type) &&
        aTok.nextToken().lastIndexOf(mname) > -1)
      {
        aString = aTok.nextToken();
        if (aString.length() > 2)
          i = (statNames.lastIndexOf(aString.substring(0, 3)) + 3) / 3;
        if (i == 0)
          i = -1;
        else
        {
          i--;
          break;
        }
      }
    }
    return i;
  }

  public String alignments()
  {
    return alignments;
  }

  private void setAlignments(String newAlign)
  {
    alignments = newAlign;
  }

  public boolean allowsAlignment(int index)
  {
    PlayerCharacter aPC = Globals.currentPC;
    for (int i = 0; i < alignments().length(); i++)
    {
      if (alignments().charAt(i) >= '0' && alignments().charAt(i) <= '9' &&
        Integer.parseInt(alignments().substring(i, i + 1)) == index)
        return true;
      if (alignments().charAt(i) == 'D' && aPC.deity() != null &&
        aPC.deity().allowsAlignment(index))
        return true;
    }
    return false;
  }

  private boolean canBePrestige()
  {
    return passesPreReqTests();
  }

  public int maxLevel()
  {
    return maxLevel;
  }

  public String prestigeString()
  {
    return preReqStrings();
  }

  public int hitDie()
  {
    return hitDie;
  }

  private void setHitDie(int dice)
  {
    hitDie = dice;
  }

  public int skillPoints()
  {
    return skillPoints;
  }

  private void setSkillPoints(int points)
  {
    skillPoints = points;
  }

  public int initialFeats()
  {
    return initialFeats;
  }

  private void setInitialFeats(int feats)
  {
    initialFeats = feats;
  }

  public String spellBaseStat()
  {
    return spellBaseStat;
  }

  public void setSpellBaseStat(String baseStat)
  {
    spellBaseStat = baseStat;
  }

  public String spellType()
  {
    return spellType;
  }

  private void setSpellType(String newType)
  {
    spellType = newType;
  }

  public String attackBonusType()
  {
    return attackBonusType;
  }

  private void setAttackBonusType(String aString)
  {
    attackBonusType = aString;
  }

  public String fortitudeCheckType()
  {
    return fortitudeCheckType;
  }

  private void setFortitudeCheckType(String aString)
  {
    fortitudeCheckType = aString;
  }

  public String reflexCheckType()
  {
    return reflexCheckType;
  }

  private void setReflexCheckType(String aString)
  {
    reflexCheckType = aString;
  }

  public String willCheckType()
  {
    return willCheckType;
  }

  private void setWillCheckType(String aString)
  {
    willCheckType = aString;
  }

  public boolean intModToSkills()
  {
    return intModToSkills;
  }

  public void setIntModToSkills(boolean bool)
  {
    intModToSkills = bool;
  }

  public int levelsPerFeat()
  {
    return levelsPerFeat;
  }

  public void setLevelsPerFeat(int newLevels)
  {
    if (newLevels < 0)
      return;
    levelsPerFeat = newLevels;
  }

  public ArrayList knownList()
  {
    return knownList;
  }

  public int baseSpellIndex()
  {
    if (spellBaseStat().equals("WIS"))
      return 4;
    else if (spellBaseStat().equals("INT"))
      return 3;
    else if (spellBaseStat().equals("CHA"))
      return 5;
    return 0;
  }

  public int getKnownForLevel(int pcLevel, int spellLevel)
  {
    int total = 0;
    PlayerCharacter aPC = Globals.currentPC;
    total = aPC.getTotalBonusTo("SPELLKNOWN", "CLASS=" + keyName() + ";LEVEL=" + spellLevel, true) +
      aPC.getTotalBonusTo("SPELLKNOWN", "TYPE=" + spellType() + ";LEVEL=" + spellLevel, true);
    for (Iterator e1 = aPC.classList.iterator(); e1.hasNext();)
    {
      PCClass aClass = (PCClass)e1.next();
      pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
    }
    if (aPC.adjStats(baseSpellIndex()) < 10 + spellLevel)
      return total;
    for (Iterator e = knownList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (pcLevel == 1)
      {
        StringTokenizer aTok = new StringTokenizer(aString, ",", false);
        while (aTok.hasMoreTokens())
        {
          Integer t = new Integer((String)aTok.nextElement());
          if (spellLevel == 0)
          {
            total += t.intValue();
            break;
          }
          spellLevel--;
        }
      }
      pcLevel--;
      if (pcLevel < 1)
        break;
    }
    return total;
  }

  public ArrayList castList()
  {
    return castList;
  }

  public int getKnownSpellsFromSpecialty()
  {
    return knownSpellsFromSpecialty;
  }

  public String getCastForLevelString(int pcLevel, int spellLevel, String bookName)
  {
    int total = 0;
    int stat = 0;
    PlayerCharacter aPC = Globals.currentPC;
    total = aPC.getTotalBonusTo("SPELLCAST", "CLASS=" + keyName() + ";LEVEL=" + spellLevel, true) +
      aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + spellType() + ";LEVEL=" + spellLevel, true);
    stat = aPC.adjStats(baseSpellIndex());
    String statString = statNames.substring(baseSpellIndex() * 3, baseSpellIndex() * 3 + 3);
    stat += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true);
    stat += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);
    if (stat < 10 + spellLevel)
      return new Integer(total).toString();
    int adj = 0;
    if (specialtyList.size() > 0)
      adj = 1;
    int temp = spellLevel;
    int spells = 0;
    if (name.equals("Domain") && spellLevel == 0)
      return "0";
    for (Iterator e1 = aPC.classList.iterator(); e1.hasNext();)
    {
      PCClass aClass = (PCClass)e1.next();
      pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
      if (Globals.debugMode)
      {
        for (Iterator xi = aClass.bonusList().iterator(); xi.hasNext();)
        {
          System.out.println(xi.next().toString());
        }
      }
      if (name().startsWith("Domain") && aClass.getBonusTo("DOMAIN", "NUMBER", aClass.level().intValue()) > 0)
        if (aClass.getCastForLevel(aClass.level().intValue(), spellLevel) > spells)
          spells = aClass.getCastForLevel(aClass.level().intValue(), spellLevel);
    }
    if (spells > 0)
      return "1";
    if (name().startsWith("Domain"))
      return "0";
    if (pcLevel > castList.size())
      pcLevel = castList.size();
    for (Iterator e = castList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (pcLevel == 1)
      {
        StringTokenizer aTok = new StringTokenizer(aString, ",", false);
        while (aTok.hasMoreTokens())
        {
          Integer t = new Integer((String)aTok.nextElement());
          if (spellLevel == 0)
          {
            total += t.intValue();
            if ((stat - 10) / 2 >= temp && temp > 0)
            {
              total += (((stat - 10) / 2 - temp) / 4) + 1;
            }
            break;
          }
          spellLevel--;
        }
      }
      pcLevel--;
      if (pcLevel < 1)
        break;
    }
    String aString = new Integer(total).toString();
    if (adj > 0)
      aString = aString + "+" + new Integer(adj).toString();
    return aString;
  }

  public int getCastForLevel(int pcLevel, int spellLevel)
  {
    return getCastForLevel(pcLevel, spellLevel, "Known Spells");
  }

  public int getCastForLevel(int pcLevel, int spellLevel, String bookName)
  {
    int total = 0;
    int stat = 0;
    PlayerCharacter aPC = Globals.currentPC;
    total = aPC.getTotalBonusTo("SPELLCAST", "CLASS=" + keyName() + ";LEVEL=" + spellLevel, true) +
      aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + spellType() + ";LEVEL=" + spellLevel, true);
    stat = aPC.adjStats(baseSpellIndex());
    String statString = statNames.substring(baseSpellIndex() * 3, baseSpellIndex() * 3 + 3);
    stat += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true);
    stat += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);
    if (stat < 10 + spellLevel)
      return total;
    int adj = 0;
    if (!bookName.equals("Known Spells") && specialtyList.size() > 0)
    {
      for (Iterator e = spellList().iterator(); e.hasNext();)
      {
        Spell aSpell = (Spell)e.next();
        if (aSpell.levelForClass(name) == spellLevel && aSpell.spellBooks().contains(bookName))
        {
          if (specialtyList.contains(aSpell.school))
            adj = 1;
          for (int j = 0; j < aSpell.descriptorList.size(); j++)
            if (specialtyList.contains((String)aSpell.descriptorList.get(j)))
              adj = 1;
        }
        if (adj != 0)
          break;
      }
    }
    int temp = spellLevel;
    int spells = 0;
    if (name.equals("Domain") && spellLevel == 0)
      return 0;
    for (Iterator e1 = aPC.classList.iterator(); e1.hasNext();)
    {
      PCClass aClass = (PCClass)e1.next();
      pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
      if (Globals.debugMode)
      {
        for (Iterator xi = aClass.bonusList().iterator(); xi.hasNext();)
        {
          System.out.println(xi.next().toString());
        }
      }
      if (name().startsWith("Domain") && aClass.getBonusTo("DOMAIN", "NUMBER", aClass.level().intValue()) > 0)
        if (aClass.getCastForLevel(aClass.level().intValue(), spellLevel) > spells)
          spells = aClass.getCastForLevel(aClass.level().intValue(), spellLevel);
    }
    if (spells > 0)
      return 1;
    if (name().startsWith("Domain"))
      return 0;
    if (pcLevel > castList.size())
      pcLevel = castList.size();
    for (Iterator e = castList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (pcLevel == 1)
      {
        StringTokenizer aTok = new StringTokenizer(aString, ",", false);
        while (aTok.hasMoreTokens())
        {
          Integer t = new Integer((String)aTok.nextElement());
          if (spellLevel == 0)
          {
            total += t.intValue() + adj;
            if ((stat - 10) / 2 >= temp && temp > 0)
            {
              total += (((stat - 10) / 2 - temp) / 4) + 1;
            }
            break;
          }
          spellLevel--;
        }
      }
      pcLevel--;
      if (pcLevel < 1)
        break;
    }
    return total;
  }

  public ArrayList uattList()
  {
    return uattList;
  }

  public String getUattForLevel(int aLevel)
  {
    String aString = "0";
    if (uattList().isEmpty())
      return aString;
    for (Iterator e = uattList().iterator(); e.hasNext();)
    {
      String bString = (String)e.next();
      if (aLevel == 1)
        return bString;
      aLevel--;
      if (aLevel < 1)
        break;
    }
    return null;
  }

  public String getUMultForLevel(int aLevel)
  {
    String aString = "0";
    for (Iterator e = umult.iterator(); e.hasNext();)
    {
      String bString = (String)e.next();
      if (aLevel <= new Integer(bString.substring(0, bString.lastIndexOf("|"))).intValue())
        aString = bString.substring(bString.lastIndexOf("|") + 1);

    }
    return aString;
  }

  public ArrayList udamList()
  {
    return udamList;
  }

  public String getUdamForLevel(int aLevel, boolean includeCrit, boolean includeStrBonus)
  {
    String aString;
    int i = 0;
    if (Globals.currentPC.size().equals("S"))
      aString = "1d2";
    else
    {
      i = 1;
      aString = "1d3";
    }
    for (Iterator e = udamList().iterator(); e.hasNext();)
    {
      String bString = (String)e.next();
      if (aLevel == 1)
      {
        StringTokenizer aTok = new StringTokenizer(bString, ",", false);
        while (i > -1 && aTok.hasMoreTokens())
        {
          String cString = aTok.nextToken();
          if (i == 0)
          {
            aString = cString;
            break;
          }
          i--;
        }
      }
      aLevel--;
      if (aLevel < 1)
        break;
    }
    if (includeStrBonus == true && Globals.currentPC.adjStats(0) / 2 > 5)
      aString = aString + "+";
    if (includeStrBonus == true && Globals.currentPC.adjStats(0) / 2 != 5)
      aString = aString + new Integer(Globals.currentPC.adjStats(0) / 2 - 5).toString();
    if (includeCrit == true)
    {
      String dString = getUMultForLevel(aLevel);
      if (!dString.equals("0"))
        aString = aString + "(x" + dString + ")";
    }
    return aString;
  }


  public String getMoveForLevel(int aLevel)
  {
    String aString = "0";
    int iAmount; // Amount of Progression Bonus
    int iCount;  // Number of Times to Add Progression Bonus
    int iTotal;  // Total Movement after Progression


    /**
     * This is a Kludge for determining if SA Exists
     * Please Change if there is a more efficient way
     * J. Bennett
     */
    if (!specialAbilityList.contains(Integer.toString(aLevel) + ":FastMove"))
      return aString;
    int move = Globals.currentPC.race.movement().intValue();
    iTotal = 0;
    if (aLevel <= 2)
    {
      iCount = 0;
    }
    else
    {
      iCount = aLevel / 3;
    }

    /**
     * Base Movement of 20 is the exception to the rule
     * Move Progression is in following pattern: +5, +10, +5, +5, +10 ...
     */

    if (move == 20)
    {
      int i = 0;
      while (i < iCount)
      {
        if ((i % 5) == 1 || (i % 5) == 4)
          iAmount = 10;
        else
          iAmount = 5;
        iTotal += iAmount;
        i++;
      }
    }
    else
    {
      iTotal += (((move / 15) * 5) * iCount);
    }
    return Integer.toString(iTotal);
  }

  public ArrayList acList()
  {
    return acList;
  }

  public String getACForLevel(int aLevel)
  {
    String aString = "0";
    if (acList().isEmpty())
      return aString;
    for (Iterator e = acList().iterator(); e.hasNext();)
    {
      String bString = (String)e.next();
      if (aLevel == 1)
        return bString;
      aLevel--;
      if (aLevel < 1)
        break;
    }
    return aString;
  }

  public ArrayList languageAutos()
  {
    return languageAutos;
  }

  private void setLanguageAutos(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, ",", false);
    while (aTok.hasMoreTokens())
      languageAutos().add(aTok.nextToken());
  }

  public ArrayList languageBonus()
  {
    return languageBonus;
  }

  private void setLanguageBonus(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, ",", false);
    while (aTok.hasMoreTokens())
      languageBonus().add(aTok.nextToken());
  }

  public ArrayList weaponProfAutos()
  {
    return weaponProfAutos;
  }

  private void setWeaponProfAutos(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, "|", false);
    while (aTok.hasMoreTokens())
      weaponProfAutos().add(aTok.nextToken());
  }

  public ArrayList weaponProfBonus()
  {
    return weaponProfBonus;
  }

  private void setWeaponProfBonus(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, "|", false);
    while (aTok.hasMoreTokens())
      weaponProfBonus().add(aTok.nextToken());
  }

  public Integer level()
  {
    return level;
  }

  public String Type()
  {
    return classType;
  }

  public void setType(String newType)
  {
    if (!newType.equals("NPC") && !newType.equals("Monster"))
    {
      JOptionPane.showMessageDialog(null, "Invalid Class Type in class " + name);
      return;
    }
    classType = newType;
  }

  public void setLevel(Integer newLevel)
  {
    if (newLevel.intValue() >= 0)
      level = newLevel;
    PlayerCharacter aPC = Globals.currentPC;
    if (spellType().equals("Divine") || (newLevel.intValue() != 0 && knownSpellsList.size() > 0))
    {
      for (Iterator e = Globals.spellList.iterator(); e.hasNext();)
      {
        Spell aSpell = (Spell)e.next();
        int i = aSpell.levelForClass(name);
        if (i == -1 && name.startsWith("Domain"))
          i = aSpell.levelForClass(aPC.domainClassName());
        if (i >= 0 && (spellType.equals("Divine") || isAutoKnownSpell(aSpell.keyName(), i)))
        {
          boolean addIt = getCastForLevel(level().intValue(), i) > 0;
          Spell bSpell = null;
          for (Iterator e1 = spellList().iterator(); e1.hasNext();)
          {
            bSpell = (Spell)e1.next();
            if (bSpell.name().equals(aSpell.name()))
            {
              if (addIt == false)
              {
                e1.remove();
              }
              break;
            }
            bSpell = null;
          }
          if (addIt == true && bSpell == null)
          {
            Spell newSpell = (Spell)aSpell.clone();
            String className = "";
            if (castAs.length() == 0)
              className = new String(name);
            else
              className = castAs;
            String aString = className + "," + new Integer(i).toString();
            if (className.equals("Domain"))
            {
              aString = "";
              for (Iterator e2 = aPC.domainList.iterator(); e2.hasNext();)
              {
                Domain aDomain = (Domain)e2.next();
                if (aSpell.levelForClass(aDomain.name()) == i)
                {
                  if (aString.length() > 0)
                    aString = aString + ",";
                  aString = aString + aDomain.name() + "," + new Integer(i).toString();
                }
              }
            }
            newSpell.setClassLevels(aString);
            spellList().add(newSpell);
            if (!newSpell.spellBooks().contains("Known Spells"))
              newSpell.addToSpellBook("Known Spells", false);
          }
        }
      }
    }
    if (!name().startsWith("Domain") && aPC.getDomainMax() > 0)
    {
      PCClass aClass = aPC.getClassNamed("Domain");
      if (aClass != null)
        aClass.setLevel(new Integer(0));
    }
  }

  public void addLevel(boolean levelMax)
  {
    Integer newLevel = new Integer(level.intValue() + 1);
    if (isMonster())
      levelMax = false;
    if (newLevel.intValue() > maxLevel && levelMax == true)
    {
      JOptionPane.showMessageDialog(null, "This class cannot be raised above level " + new Integer(maxLevel).toString());
      return;
    }
    PlayerCharacter aPC = Globals.currentPC;
    int total = aPC.totalLevels();
    if (total == 0)
      aPC.setFeats(aPC.race().bonusInitialFeats());
    setLevel(newLevel);
    int spMod = 0;
    if (isMonster() && !intModToSkills)
      spMod = skillPoints() + aPC.race().bonusSkillsPerLevel();
    else
    {
      spMod = skillPoints() + aPC.race().bonusSkillsPerLevel() + (aPC.adjStats(3) / 2) - 5;
      if (spMod < 1)
        spMod = 1;
    }
    if (getBonusTo("DOMAIN", "NUMBER", newLevel.intValue()) > 0)
      aPC.modDomainClass(true);
    if (isMonster())
    {
      if (levelsPerFeat != 0)
        if (aPC.totalHitDice() % levelsPerFeat == 0)
          aPC.setFeats(aPC.feats() + 1);
    }
    else
      changeFeatsForLevel(newLevel.intValue(), true);
    addAddsForLevel(newLevel.intValue());
    aPC.changeSpecialAbilitiesForLevel(newLevel.intValue(), true, specialAbilityList());
    changeSubSpecialAbilitiesForLevel(newLevel.intValue(), false);
    changeSpecials();
    addVariablesForLevel(newLevel.intValue());
    if (newLevel.intValue() == 1 && !aPC.isImporting)
      checkForSubClass();
    rollHp();

    if (aPC.isImporting == false)
    {
      for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
      {
        LevelAbility ability = (LevelAbility)e.next();
        if (ability.level() == newLevel.intValue() && ability.canProcess())
          ability.process();
      }
    }
    if (!isMonster() && aPC.totalLevels() > total)
    {
      total = aPC.totalLevels();
      if (aPC.isImporting == false)
      {
        // We do not want to do these calculations a second
        // time when are importing a character.  The feat number
        // and the stat point pool are already saved in the import file.
        if (aPC.experience().intValue() < minExpForLevel(total + aPC.race().LevelAdjustment()).intValue())
          aPC.setExperience(minExpForLevel(total + aPC.race().LevelAdjustment()));
        if (total % 3 == 0)
          aPC.setFeats(aPC.feats() + 1);
        if (total % 4 == 0)
        {
          aPC.setPoolAmount(aPC.poolAmount() + 1);
          JOptionPane.showMessageDialog(null, "You can increment a stat on the Stat tab.");
        }
      }
    }
    else
      return;
    if (total == 1)
    {
      if (Globals.purchaseStatMode)
        aPC.setPoolAmount(0);
      spMod *= aPC.race().initialSkillMultiplier;
      aPC.race().rollAgeForClass(name());
      skillPool = new Integer(spMod);
      aPC.getAutoWeaponProfs();
      aPC.setGold(rollGold().toString());
    }
    else
      skillPool = new Integer(skillPool().intValue() + spMod);
    spMod += aPC.skillPoints();
    aPC.setSkillPoints(spMod);
    if (aPC.experience().intValue() < minExpForLevel(total + aPC.race().LevelAdjustment()).intValue())
      aPC.setExperience(new Integer(minExpForLevel(total + aPC.race().LevelAdjustment()).intValue()));
    else if (aPC.experience().intValue() >= minExpForLevel(total + 1 + aPC.race().LevelAdjustment()).intValue())
      JOptionPane.showMessageDialog(null, "You can advance another level with your experience.");
  }

  public void subLevel()
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC != null)
    {
      int total = aPC.totalLevels();
      int spMod = 0;
      if (isMonster() && !intModToSkills)
        spMod = skillPoints() + aPC.race().bonusSkillsPerLevel();
      else
        spMod = skillPoints() + aPC.race().bonusSkillsPerLevel() + (aPC.adjStats(3) / 2) - 5;

      // XXX Why is the feat decrementing done twice (here and in
      // subAddsForLevel())? The code works correctly, but I don't know
      // why.
      // Also, the use of instanceof is kinda ugly.
      for (Iterator e1 = levelAbilityList.iterator(); e1.hasNext();)
      {
        LevelAbility ability = (LevelAbility)e1.next();
        if (ability.level() == level.intValue() && ability instanceof LevelAbilityFeat)
          aPC.setFeats(aPC.feats() - 1);
      }
      Integer zeroInt = new Integer(0);
      Integer newLevel = new Integer(level.intValue() - 1);
      if (level.intValue() > 0)
      {
        hitPointList[level.intValue() - 1] = zeroInt;
      }
      setLevel(newLevel);
      if (!isMonster())
        changeFeatsForLevel(newLevel.intValue() + 1, false);
      else if (levelsPerFeat != 0)
        if ((aPC.totalHitDice() + 1) % levelsPerFeat == 0)
          aPC.setFeats(aPC.feats() - 1);
      subAddsForLevel(newLevel.intValue() + 1);
      aPC.changeSpecialAbilitiesForLevel(newLevel.intValue() + 1, false, specialAbilityList());
      changeSubSpecialAbilitiesForLevel(newLevel.intValue() + 1, true);
      changeSpecials();
      aPC.removeVariable("CLASS:" + name() + "|" + new Integer(level.intValue() + 1).toString());
      if (newLevel.intValue() == 0)
        setSubClassName("None");
      if (newLevel.intValue() == 0 && deityString.length() > 0)
      {
        if (aPC.totalLevels() == 0 || getBonusTo("DOMAIN", "NUMBER", newLevel.intValue()) > 0)
        {
          aPC.setDeity(null);
          aPC.domainList.clear();
          aPC.modDomainClass(true);
        }
      }
      if (!isMonster() && total > aPC.totalLevels())
      {
        total = aPC.totalLevels();
        if (aPC.experience().intValue() >= minExpForLevel(total + 1 + aPC.race().LevelAdjustment()).intValue())
        {
          int minXP = minExpForLevel(total + 1 + aPC.race().LevelAdjustment()).intValue() - 1;
          if (minXP >= 1)
            minXP--;
          else
            minXP = 0;
          aPC.setExperience(new Integer(minXP));
        }
        if (total % 3 == 2)
          aPC.setFeats(aPC.feats() - 1);
        if (total % 4 == 3)
        {
          aPC.setPoolAmount(aPC.poolAmount() - 1);
          JOptionPane.showMessageDialog(null, "You lost a stat point due to level decrease. See the Stat tab.");
        }
      }
      if (!isMonster() && total == 0)
      {
        aPC.setSkillPoints(0);
        aPC.setFeats(0);
        aPC.skillList().clear();
        aPC.featList.clear();
        aPC.weaponProfList().removeAll(Globals.weaponProfList);
      }
      else
      {
        aPC.setSkillPoints(aPC.skillPoints() - spMod);
        skillPool = new Integer(skillPool().intValue() - spMod);
      }
      if (level().intValue() == 0)
        aPC.classList.remove(this);
    }
    else
    {
      System.out.println("No current pc in subLevel()? How did this happen?");
      return;
    }
  }

  static public Integer minExpForLevel(int aLevel)
  {
    Integer min = new Integer(0);
    for (int i = 1; i < aLevel; i++)
      min = new Integer(min.intValue() + 1000 * i);
    return min;
  }

  public int memorizedSpellForLevelBook(int aLevel, String bookName)
  {
    int i = 0;
    PlayerCharacter aPC = Globals.currentPC;
    for (Iterator e = spellList().iterator(); e.hasNext();)
    {
      Spell aSpell = (Spell)e.next();
      int j = -1;
      if (aSpell.levelForClass(name) == aLevel ||
        (name.equals("Domain") && aSpell.levelForClass(aPC.domainClassName()) == aLevel))
        j = aSpell.spellBooks().indexOf(bookName);
      if (j >= 0)
      {
        Integer anInt = (Integer)aSpell.times().get(j);
        i += anInt.intValue();
      }
    }
    return i;
  }

  private void changeSpecials()
  {
    if (specialsString().length() == 0)
      return;
    String className = "";
    Integer adj = new Integer(0);
    String abilityName = "";
    String levelString = "";
    StringTokenizer aTok = new StringTokenizer(specialsString, "|", false);
    ArrayList saList = new ArrayList();
    if (aTok.hasMoreTokens())
      abilityName = aTok.nextToken();
    if (aTok.hasMoreTokens())
      className = aTok.nextToken();
    if (aTok.hasMoreTokens())
      aTok.nextToken(); // adj will be summed later
    if (aTok.hasMoreTokens())
      levelString = aTok.nextToken();
    // first, remove all special abilities by this name
    Iterator e = null;
    PlayerCharacter aPC = Globals.currentPC;
    for (int i = aPC.specialAbilityList().size() - 1; i >= 0; i--)
    {
      String aString = (String)aPC.specialAbilityList().get(i);
      if (aString.startsWith(abilityName))
        aPC.specialAbilityList().remove(aString);
    }
    // next, determine total 'levels' of ability
    for (e = aPC.classList.iterator(); e.hasNext();)
    {
      PCClass aClass = (PCClass)e.next();
      if (aClass.specialsString().length() > 0 && aClass.specialsString().startsWith(abilityName))
      {
        aTok = new StringTokenizer(aClass.specialsString(), "|", false);
        aTok.nextToken();
        aTok.nextToken();
        if (aTok.hasMoreTokens())
          adj = new Integer(adj.intValue() + Integer.parseInt(aTok.nextToken()) + aClass.level().intValue());
        if (aTok.hasMoreTokens())
          levelString = aTok.nextToken(); // need this
      }
    }
    // next add abilities for level based upon levelString
    PCClass aClass = aPC.getClassNamed(className);
    if (aClass == null)
    {
      for (e = Globals.classList.iterator(); e.hasNext();)
      {
        aClass = (PCClass)e.next();
        if (aClass.name().equals(className))
        {
          aTok = new StringTokenizer(aClass.specialsString(), "|", false);
          aTok.nextToken();
          aTok.nextToken();
          aTok.nextToken();
          levelString = aTok.nextToken(); // required
          break;
        }
        aClass = null;
      }
    }
    if (aClass != null && levelString.length() > 0)
    {
      aTok = new StringTokenizer(levelString, ",", false);
      int i = 0;
      Integer aLevel = new Integer(0);
      while (aTok.hasMoreTokens() && adj.intValue() >= aLevel.intValue())
      {
        aLevel = new Integer(aTok.nextToken());
        if (adj.intValue() >= aLevel.intValue())
          saList.add(Globals.specialsList.get(i++));
      }
    }
    for (int i = saList.size() - 1; i >= 0; i--)
    {
      if (i >= saList.size())
        i = saList.size() - 1;
      if (i < 0)
        break;
      SpecialAbility sa1 = (SpecialAbility)saList.get(i);
      String sn1 = new String(sa1.desc());
      for (int k = 0; k < 10; k++)
        sn1 = sn1.replace((char)('0' + k), ' ');
      String sn2 = new String();
      for (int j = i - 1; j >= 0; j--)
      {
        SpecialAbility sa2 = (SpecialAbility)saList.get(j);
        sn2 = new String(sa2.desc());
        for (int k = 0; k < 10; k++)
          sn2 = sn2.replace((char)('0' + k), ' ');
        if (sn1.equals(sn2))
          saList.remove(sa2);
      }
    }
    for (e = saList.iterator(); e.hasNext();)
    {
      SpecialAbility sa = (SpecialAbility)e.next();
      aPC.specialAbilityList().add(sa.name() + " (" + sa.desc() + ")");
    }
  }

  private void addVariablesForLevel(int aLevel)
  {
    for (Iterator e = variableList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      StringTokenizer aTok = new StringTokenizer(aString, "|", false);
      String bString = aTok.nextToken();
      Integer a = new Integer(bString);
      if (a.intValue() == aLevel)
        Globals.currentPC.addVariable("CLASS:" + name + "|" + aString);
    }
  }

  public void setHitPointList(Integer[] newList)
  {
    hitPointList = newList;
  }

  public int hitPoints()
  {
    int i,total = 0;
    for (i = 0; i < level.intValue() && i < hitPointList.length; i++)
      if (hitPointList[i] != null)
        total += hitPointList[i].intValue();
    return total;
  }

  public Integer[] hitPointList()
  {
    return hitPointList;
  }

  public void adjustHpRolls(int increment)
  {
    if (level.intValue() == 0)
      return;
    int i;
    for (i = 0; i < level.intValue(); i++)
    {
      Integer roll;
      int a = hitPointList[i].intValue() + increment;
      if (a > 1)
        roll = new Integer(hitPointList[i].intValue() + increment);
      else
        roll = new Integer(1);
      hitPointList[i] = roll;
    }
  }

  /**
   * Rolls hp for the current level according to the rules set in options.
   */
  public void rollHp()
  {
    int roll = 0;
    int min = 1 + Globals.currentPC.getTotalBonusTo("HD", "MIN", true) + Globals.currentPC.getTotalBonusTo("HD", "MIN;CLASS=" + name, true);
    int max = hitDie() + Globals.currentPC.getTotalBonusTo("HD", "MAX", true) + Globals.currentPC.getTotalBonusTo("HD", "MAX;CLASS=" + name, true);

    //Shouldn't really have to be called. I think this should be handled by the level raising code.
    fixHitpointList();


    if (level.intValue() == 0)
    {
      hitPointList[0] = new Integer(0);
    }
    else if (level.intValue() == 1 && Globals.hpMaxAtFirstLevel)
    {
      roll = max;
    }
    else
    {
      Random roller = new Random();
      switch (Globals.hpRollMethod)
      {
        case Globals.HP_STANDARD:
          roll = Math.abs(roller.nextInt(max - min + 1)) + min;
          break;
        case Globals.HP_AUTOMAX:
          roll = max;
          break;
        case Globals.HP_PERCENTAGE:
          roll = (int)(Globals.hpPct / 100) * (Math.abs(roller.nextInt(max - min + 1)) + min);
          break;
        case Globals.HP_RPGA:
          roll = (int)Math.floor((max + min) / 2) + 1;
        default:
      }
    }
    roll += (Globals.currentPC.adjStats(2) / 2) - 5;
    if (roll < 2)
    {
      roll = 1;
    }
    hitPointList[level.intValue() - 1] = new Integer(roll);
    Globals.currentPC.currentHP = Globals.currentPC.hitPoints();
  }

  private void fixHitpointList()
  {
    if (hitPointList.length < level.intValue())
    {
      int i;
      Integer[] newList = new Integer[level.intValue()];
      for (i = 0; i < hitPointList.length; ++i)
      {
        newList[i] = hitPointList[i];
      }
      while (i < newList.length)
      {
        newList[i++] = new Integer(0);
      }
      setHitPointList(newList);
    }
  }

  public int baseAttackBonus(int type)
    // type=0 generic; type=1 melee; type=2 ranged; type=3 unarmed; type=4 thrown;
    // type=5 projectile
  {
    int i = 0;
    if (level.intValue() == 0)
      return 0;
    i += this.getBonusTo("TOHIT", "TOHIT", level.intValue());
    if (attackBonusType().equals("G"))
      return i + level.intValue();
    else if (attackBonusType().equals("M"))
      return i + 3 * (level.intValue()) / 4;
    else if (attackBonusType().equals("B"))
      return i + level.intValue() / 2;
    else if (attackBonusType().equals("O"))
    {
      String aString = Globals.currentPC.getVariable("BAB", true, false, "CLASS:" + name, "").toString();
      return i +
        Integer.parseInt(aString.substring(0, aString.lastIndexOf(".")));
    }
    return i;
  }

  public int fortitudeCheckBonus()
  {
    if (level.intValue() == 0)
      return 0;
    int i = checkBonus(fortitudeCheckType, "FORTBASE");
    return i;
  }

  public int checkBonus(String bonusType, String defString)
  {
    if (bonusType.equals("G"))
      return 2 + level.intValue() / 2;
    else if (bonusType.equals("B"))
      return level.intValue() / 3;
    else if (bonusType.equals("M"))
      return 1 + level.intValue() / 5 + (3 + level.intValue()) / 5;
    else if (bonusType.equals("O"))
    {
      String aString = Globals.currentPC.getVariable(defString, true, false, "CLASS:" + name, "").toString();
      return Integer.parseInt(aString.substring(0, aString.lastIndexOf(".")));
    }
    return 0;
  }

  public int reflexCheckBonus()
  {
    if (level.intValue() == 0)
      return 0;
    int i = checkBonus(reflexCheckType, "REFLEXBASE");
    return i;
  }

  public int willCheckBonus()
  {
    if (level.intValue() == 0)
      return 0;
    int i = checkBonus(willCheckType, "WILLBASE");
    return i;
  }

  public String classLevelString()
  {
    String aString;
    if (!subClassName().equals("None") && !subClassName().equals(""))
      aString = new String(subClassName());
    else
      aString = new String(name());
    aString = aString.concat(" " + level.toString());
    return aString;
  }

  public ArrayList featList()
  {
    return featList;
  }
  // int level e.g. 1
  // featList Shield Proficieny,Armor Proficiency (light)
  public void addFeatList(int aLevel, String aFeatList)
  {
    Integer anInt = new Integer(aLevel);
    String aString = anInt.toString() + ":" + aFeatList;
    featList.add(aString);
  }

  public ArrayList vFeatList()
  {
    return vFeatList;
  }
  // int level e.g. 1
  // featList Shield Proficieny,Armor Proficiency (light)
  public void addVFeatList(int aLevel, String aFeatList)
  {
    Integer anInt = new Integer(aLevel);
    String aString = anInt.toString() + ":" + aFeatList;
    vFeatList.add(aString);
  }

  private String getToken(int tokenNum, String aList, String delim)
  {
    StringTokenizer aTok = new StringTokenizer(aList, delim, false);
    while (aTok.hasMoreElements() && tokenNum >= 0)
    {
      String aString = aTok.nextToken();
      if (tokenNum == 0)
        return aString;
      tokenNum--;
    }
    return null;
  }

  /**
   * This method adds or deletes feats for a level.
   * @param aLevel the level to affect
   * @param addThem whether to add or remove feats
   */
  public void changeFeatsForLevel(int aLevel, boolean addThem)
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC == null)
      return;
    for (Iterator e = featList.iterator(); e.hasNext();)
    {
      String feats = (String)e.next();
      Integer thisInt = new Integer(getToken(0, feats, ":"));
      String aList = getToken(1, feats, ":");
      if (aLevel == thisInt.intValue())
        aPC.modFeatsFromList(aList, addThem, aLevel == 1);
    }
  }

  public void subFeatsForLevel(int aLevel)
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC == null)
      return;
    Integer anInt = new Integer(aLevel);
    for (Iterator e = featList.iterator(); e.hasNext();)
    {
      String feats = (String)e.next();
      Integer thisInt = new Integer(getToken(0, feats, ":"));
      String aList = getToken(1, feats, ":");
      if (anInt.intValue() == thisInt.intValue())
        aPC.modFeatsFromList(aList, false, aLevel == 0);
    }
  }

  public ArrayList specialAbilityList()
  {
    return specialAbilityList;
  }

  public void addSpecialAbilityList(int aLevel, String aList)
  {
    Integer anInt = new Integer(aLevel);
    String aString = anInt.toString() + ":" + aList;
    specialAbilityList.add(aString);
  }

  public ArrayList subSpecialAbilityList()
  {
    return subSpecialAbilityList;
  }

  public void addSubSpecialAbilityList(int aLevel, String aList)
  {
    Integer anInt = new Integer(aLevel);
    String aString = anInt.toString() + ":" + aList;
    subSpecialAbilityList.add(aString);
  }

  public void changeSubSpecialAbilitiesForLevel(int aLevel, boolean addIt)
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC == null)
      return;
    Integer anInt = new Integer(aLevel);
    for (Iterator e = subSpecialAbilityList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      Integer thisInt = new Integer(getToken(0, aString, ":"));
      String aList = getToken(1, aString, ":");
      if (anInt.intValue() == thisInt.intValue())
      {
        StringTokenizer aStrTok = new StringTokenizer(aList, ",", false);
        while (aStrTok.hasMoreTokens())
        {
          String thisString = (String)aStrTok.nextToken();
          if (addIt == true)
            aPC.specialAbilityList().add(thisString);
          else
            aPC.specialAbilityList().remove(thisString);
        }
      }
    }
  }

  public ArrayList spellList()
  {
    return spellList;
  }

  public Spell getSpellNamed(String spellName)
  {
    for (Iterator i = spellList.iterator(); i.hasNext();)
    {
      Spell aSpell = (Spell)i.next();
      if (aSpell.name().equals(spellName))
        return aSpell;
    }
    return null;
  }

  public int getSpellsInSpecialtyForLevel(int level)
  {
    int retVal = 0;
    for (Iterator i = spellList.iterator(); i.hasNext();)
    {
      Spell aSpell = (Spell)i.next();
      if (aSpell.isInSpecialty(specialtyList) && aSpell.levelForClass(name) == level)
        retVal++;
    }
    return retVal;
  }

  public void addAddList(int aLevel, String aList)
  {
    levelAbilityList.add(LevelAbility.createAbility(this, aLevel, aList));
  }

  public void addAddsForLevel(int aLevel)
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC == null)
      return;

    for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
    {
      LevelAbility ability = (LevelAbility)e.next();
      if (ability.level() == aLevel)
        ability.addForLevel();
    }
  }

  public void subAddsForLevel(int aLevel)
  {
    PlayerCharacter aPC = Globals.currentPC;
    if (aPC == null)
      return;

    for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
    {
      LevelAbility ability = (LevelAbility)e.next();
      if (ability.level() == aLevel)
        ability.subForLevel();
    }
  }

  private void checkForSubClass()
  {
    if (subClassString.lastIndexOf('|') <= 0)
    {
      return;
    }

    // Tokenize the class
    StringTokenizer aTok = new StringTokenizer(subClassString, "|");

    Integer anInt = new Integer(aTok.nextToken());

    ArrayList choiceNames = new ArrayList();
    ArrayList fileNames = new ArrayList();
    ArrayList choiceNum = new ArrayList();

    boolean showAdditionalChoices = false;
    while (aTok.hasMoreTokens())
    {
      String choice = aTok.nextToken();
      if (choice.startsWith("FILE="))
      {
        int i = choice.lastIndexOf('.');
        String fileName = choice.substring("FILE=".length());
        fileName = fileName.replace('\\', File.separatorChar);
        fileName = fileName.replace('/', File.separatorChar);
        fileNames.add(fileName);
        choice = choice.substring("FILE=".length(), i);
        showAdditionalChoices = true;
      }
      else
      {
        fileNames.add("NO");
      }

      choiceNames.add(choice);

      if (aTok.hasMoreTokens())
      {
        choiceNum.add(new Integer(aTok.nextToken()));
      }
      else
      {
        choiceNum.add(new Integer(1));
      }
    }

    if (showAdditionalChoices)
    {
      // Show a new chooser
      Chooser c = new Chooser();
      c.setPool(1);
      c.setPoolFlag(false);
      c.setAvailableList(choiceNames);
      if (choiceNames.size() == 1)
        c.setSelectedList(choiceNames);
      else if (choiceNames.size() != 0)
        c.show();

      List selectedList = c.getSelectedList();
      if (!selectedList.isEmpty())
      {
        String theChoice = (String)selectedList.get(0);
        if (theChoice.equals("None"))
        {
          return;
        }

        int i = choiceNames.indexOf(theChoice);

        String fileName = (String)fileNames.get(i);
        if (!fileName.equals("NO"))
        {
          File aFile = new File((String)fileName);

          anInt = new Integer(0);

          try
          {
            // Read the file lines into a list
            List availableList = new ArrayList(10);

            BufferedReader reader = new BufferedReader(new FileReader(aFile));

            String line = reader.readLine();
            while (line != null)
            {
              availableList.add(line.trim());
              line = reader.readLine();
            }

            Chooser c1 = new Chooser();
            c1.setAvailableList(availableList);
            c1.setMessageText("Select an item.  The second column is the cost. " +
              "If this cost is non-zero, you will be asked to also " +
              "select items from this list to give up.");
            c1.setPool(((Integer)choiceNum.get(i)).intValue());
            c1.setPoolFlag(true);
            c1.show();

            setProhibitedString("");
            boolean setNone = true;
            specialtyList.clear();
            for (i = 0; i < c1.getSelectedList().size(); i++)
            {
              String aString = "";
              int j = 0;
              for (j = 0; j < availableList.size(); j++)
              {
                aString = (String)availableList.get(j);
                if (aString.startsWith(c1.getSelectedList().get(i).toString()))
                  break;
              }
              StringTokenizer tabTok = new StringTokenizer(aString, "\t", false);
              j = 0;
              setNone = false;
              Integer cost = new Integer(0);
              while (tabTok.hasMoreTokens())
              {
                String bString = tabTok.nextToken();
                switch (j++)
                {
                  case 0:
                    specialtyList.add(bString);
                    break;
                  case 1:
                    cost = new Integer(bString);
                    break;
                  case 2:
                    setSubClassName(bString);
                    break;
                  default:
                    if (bString.startsWith("SPELLBASESTAT="))
                    {
                      setSpellBaseStat(bString.substring(14));
                      if (Globals.debugMode)
                        System.out.println("Base Spell Stat for " + name + " =" + spellBaseStat + ".");
                    }
                    else if (bString.startsWith("KNOWNSPELLSFROMSPECIALTY="))
                    {
                      knownSpellsFromSpecialty = Integer.parseInt(bString.substring(25));
                      if (Globals.debugMode)
                        System.out.println(name + " has " + knownSpellsFromSpecialty + " specialtySpells per level");
                    }
                    else
                    {
                      if (prohibitedString.length() > 0)
                        prohibitedString = prohibitedString.concat(",");
                      prohibitedString = prohibitedString + bString;
                    }
                    cost = new Integer(0);
                    break;
                }
              }
              if (cost.intValue() > 0)
              {
                // choose prohibiteds
                // Remove the selected option from the available options
                availableList.remove(aString);
                // Reset the available list
                c1.setAvailableList(availableList);
                // Clear the selected items
                c1.clearSelectedList();
                c1.setPool(cost.intValue());
                c1.setCostColumnNumber(1);
                c1.setMessageText("Now make your prohibited selections. The " +
                  "cost must equal the cost of your previous selection.");
                c1.show();

                i = -1;
                if (c1.getSelectedList().size() > 0)
                {
                  for (i = 0; i < c1.getSelectedList().size(); i++)
                  {
                    aString = (String)c1.getSelectedList().get(i).toString();
                    tabTok = new StringTokenizer(aString, "\t", false);
                    String bString = tabTok.nextToken();
                    if (prohibitedString.length() > 0)
                      prohibitedString = prohibitedString.concat(",");
                    if (tabTok.countTokens() > 2)
                    {
                      bString = tabTok.nextToken();
                      bString = tabTok.nextToken();
                    }
                    prohibitedString = prohibitedString.concat(bString.trim());
                  }
                }
                else
                  setSubClassName("None");
              }
              if (setNone)
                setProhibitedString("None");
            }
          }
          catch (Exception exception)
          {
            JOptionPane.showMessageDialog(null, (String)fileNames.get(i) + " error" + exception.getMessage());
          }
        }
        else
        {
          setSubClassName("");
        }
      }
    }
  }

  public Integer skillPool()
  {
    return skillPool;
  }

  public void setSkillPool(int i)
  {
    skillPool = new Integer(i);
  }

  public String goldString()
  {
    return goldString;
  }

  private void setGoldString(String aString)
  {
    goldString = aString;
  }

  public Integer rollGold()
  {
    Random roller = new Random();
    Integer dice = new Integer(4);
    Integer sides = new Integer(4);
    Integer mult = new Integer(10);
    StringTokenizer aTok = new StringTokenizer(goldString, ",", false);
    if (aTok.hasMoreTokens())
      dice = new Integer(aTok.nextToken());
    if (aTok.hasMoreTokens())
      sides = new Integer(aTok.nextToken());
    if (aTok.hasMoreTokens())
      mult = new Integer(aTok.nextToken());
    int total = 0;
    for (int roll = 0; roll < dice.intValue(); roll++)
    {
      int i = roller.nextInt(sides.intValue());
      if (i < 0) i = -i;
      total += i + 1;
    }
    total *= mult.intValue();
    return new Integer(total);
  }

  public String specialsString()
  {
    return specialsString;
  }

  private void setSpecialsString(String aString)
  {
    specialsString = aString;
  }

  public ArrayList skillList()
  {
    return skillList;
  }

  public String defenseString()
  {
    return defenseString;
  }

  private void setDefenseString(String aString)
  {
    defenseString = aString;
  }

  public Integer defense(int y)
  {
    String aString = defenseString;
    int i = 0;
    if (aString.length() > 0 && aString.indexOf(',') > -1)
    {
      int k = Integer.parseInt(aString.substring(0, aString.indexOf(',')));
      int m = Integer.parseInt(aString.substring(aString.lastIndexOf(',') + 1));
      if (y > 0)
        i += m;
      switch (k)
      {
/*Best*/  case 0:
          i += 4 + level().intValue() / 2;
          break;
/*Middle*/  case 1:
/*Prestige*/case 4:
          i += 3 + level().intValue() / 5;
          if (i >= 2)
            i += (level().intValue() + 3) / 5;
          if (k == 4)
            i -= 2;
          break;
/*Low*/    case 2:
          i += 2 + level().intValue() / 3;
          break;
/*NPC*/    case 3:
          i += level().intValue() / 3;
          break;
      }
    }
    i += getBonusTo("CLASS", "DEFENSE", level.intValue());
    return new Integer(i);
  }

  public String reputationString()
  {
    return reputationString;
  }

  private void setReputationString(String aString)
  {
    reputationString = aString;
  }

  private void addKnownSpellsList(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, "|", false);
    while (aTok.hasMoreTokens())
      knownSpellsList.add(aTok.nextToken());
  }

  public boolean isAutoKnownSpell(String spellName, int spellLevel)
  {
    for (Iterator e = knownSpellsList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (aString.startsWith("LEVEL=") && Integer.parseInt(aString.substring(6)) == spellLevel)
        return true;
      if (aString.equals(spellName))
        return true;
    }
    return false;
  }

  private void setAttackCycle(String aString)
  {
    attackCycle = aString;
  }

  public int attackCycle(int index)
  {
    StringTokenizer aTok = new StringTokenizer(attackCycle, "|", false);
    while (aTok.hasMoreTokens())
    {
      String aString = aTok.nextToken();
      if ((index == 0 && aString.equals("BAB")) || (index == 2 && aString.equals("UAB")))
        return Integer.parseInt(aTok.nextToken());
    }
    return 5;
  }

  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    String tabdelim = "\t";
    StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
    int colMax = colToken.countTokens();
    int col = 0;
    Integer anInt = new Integer(0);
    if (colMax == 0)
      return;
    int option = 15;
    if (inputLine.startsWith("CLASS:"))
    {
      option = 0;
      for (col = 0; col < 20; col++)
        castList.add("0");
    }
    for (col = 0; col < colMax; col++)
    {
      String colString = new String(colToken.nextToken());
      if (option < 15)
        option = col;
      if (col == 0 && option == 15)
      {
        anInt = new Integer(colString);
        continue;
      }
      switch (option)
      {
        case 0:
          setName(colString.substring(6));
          break;
        case 1:
          setAlignments(colString);
          break;
        case 2:
          setHitDie(anInt.parseInt(colString));
          break;
        case 3:
          setSkillPoints(anInt.parseInt(colString));
          break;
        case 4:
          setInitialFeats(anInt.parseInt(colString));
          break;
        case 5:
          setSpellBaseStat(colString);
          break;
        case 6:
          setSpellType(colString);
          break;
        case 7:
          setAttackBonusType(colString);
          break;
        case 8:
          setFortitudeCheckType(colString);
          break;
        case 9:
          setReflexCheckType(colString);
          break;
        case 10:
          setWillCheckType(colString);
          break;
        default:
          if (colString.startsWith("LANGAUTO"))
            setLanguageAutos(colString.substring(9));
          else if (colString.startsWith("LANGBONUS"))
            setLanguageBonus(colString.substring(10));
          else if (colString.startsWith("WEAPONAUTO"))
            setWeaponProfAutos(colString.substring(11));
          else if (colString.startsWith("WEAPONBONUS"))
            setWeaponProfBonus(colString.substring(12));
          else if (colString.substring(0, 2).equals("AC"))
            acList().add(colString.substring(3));
          else if (colString.startsWith("SA") || colString.startsWith("SUBSA"))
          {
            boolean flag = colString.startsWith("SA");
            int index = 3;
            if (flag == false) index = 6;
            StringTokenizer aTok = new StringTokenizer(colString.substring(index), ",", false);
            while (aTok.hasMoreTokens())
            {
              SpecialAbility sa = new SpecialAbility();
              sa.parseLine(aTok.nextToken(), sourceFile, lineNum);
              if (flag == true)
              {
                addSpecialAbilityList(anInt.intValue(), sa.name());
              }
              else
              {
                addSubSpecialAbilityList(anInt.intValue(), sa.name());
              }
            }
          }
          else if (colString.startsWith("ADD:"))
            addAddList(anInt.intValue(), colString.substring(4));
          else if (colString.startsWith("FEAT:"))
            addFeatList(anInt.intValue(), colString.substring(5));
          else if (colString.startsWith("VFEAT:"))
            addVFeatList(anInt.intValue(), colString.substring(6));
          else if (colString.startsWith("CAST:"))
          {
            castList().set(anInt.intValue() - 1, colString.substring(5));
          }
          else if (colString.startsWith("UATT:"))
            uattList().add(colString.substring(5));
          else if (colString.startsWith("UMULT:"))
            umult.add(anInt.toString() + "|" + colString.substring(6));
          else if (colString.startsWith("UDAM:"))
            udamList().add(colString.substring(5));
          else if (colString.startsWith("KNOWNSPELLS:"))
            addKnownSpellsList(colString.substring(12));
          else if (colString.startsWith("KNOWN:"))
            knownList().add(colString.substring(6));
          else if (colString.startsWith("SUBCLASS:"))
            setSubClassString(anInt.toString() + "|" + colString.substring(9));
          else if (colString.startsWith("GOLD:"))
            setGoldString(colString.substring(5));
          else if (colString.startsWith("PRERACETYPE:"))
            preRaceType = colString.substring(12);
          else if (colString.startsWith("PRE") || colString.startsWith("RESTRICT:"))
            preReqArrayList.add(colString);
          else if (colString.startsWith("SPECIALS:"))
            setSpecialsString(colString.substring(9));
          else if (colString.startsWith("DEFINE:"))
            variableList.add(anInt.toString() + "|" + colString.substring(7));
          else if (colString.startsWith("DEF:"))
            setDefenseString(colString.substring(4));
          else if (colString.startsWith("REP:"))
            setReputationString(colString.substring(4));
          else if (colString.startsWith("ABB:"))
            abbrev = colString.substring(4);
          else if (colString.startsWith("MEMORIZE:"))
            memorizeSpells = colString.endsWith("Y");
          else if (colString.startsWith("BONUS:"))
            addBonusList(anInt.toString() + "|" + colString.substring(6));
          else if (colString.startsWith("MULTIPREREQS"))
            multiPreReqs = true;
          else if (colString.startsWith("DEITY:"))
            deityString = colString.substring(6);
          else if (colString.startsWith("VISIBLE:"))
            visible = colString.substring(8).startsWith("Y");
          else if (colString.startsWith("MAXLEVEL:"))
            maxLevel = Integer.parseInt(colString.substring(9));
          else if (colString.startsWith("KEY:"))
            setKeyName(colString.substring(4));
          else if (colString.startsWith("ATTACKCYCLE:"))
            setAttackCycle(colString.substring(12));
          else if (colString.startsWith("CASTAS:"))
            setCastAs(colString.substring(7));
          else if (colString.startsWith("PROHIBITED:"))
            setProhibitedString(colString.substring(11));
          else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
            knownSpellsFromSpecialty = anInt.parseInt(colString.substring(25));
          else if (colString.startsWith("TYPE:"))
            setType(colString.substring(5));
          else if (colString.startsWith("INTMODTOSKILLS:"))
            intModToSkills = !colString.substring(15).equals("No");
          else if (colString.startsWith("LEVELSPERFEAT:"))
            setLevelsPerFeat(Integer.parseInt(colString.substring(14)));
          else
          {
            JOptionPane.showMessageDialog
              (null, "Illegal class info " +
              sourceFile.getName() + ":" + Integer.toString(lineNum) +
              " \"" + colString + "\"");
          }
          break;
      }
    }
  }

  public boolean isQualified()
  {
    if (Globals.currentPC == null)
      return false;
    if (classType != null && classType.equals("Monster") && preRaceType != null && !preRaceType.equals(Globals.currentPC.race().type()))
      return false;
    if (!allowsAlignment(Globals.currentPC.alignment()) || !canBePrestige())
      return false;
    return true;
  }

  public boolean isPrestige()
  {
    if (preReqArrayList.size() == 0)
      return false;
    return true;
  }

  public boolean isPC()
  {
    if (classType == null)
      return true;
    return false;
  }

  public boolean isNPC()
  {
    if (classType != null && classType.equals("NPC"))
      return true;
    return false;
  }

  public boolean isMonster()
  {
    if (classType != null && classType.equals("Monster"))
      return true;
    return false;
  }

  /**
   * Increases or decreases the initiative modifier by the given value.
   */

  public void addInitMod(int initModDelta)
  {
    initMod = initMod + initModDelta;
  }

  /**
   * Returns the initiative modifier.
   */

  public int initMod()
  {
    return initMod;
  }

}
