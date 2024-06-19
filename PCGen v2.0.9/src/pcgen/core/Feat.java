/*
 * Feat.java
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
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import pcgen.util.Delta;
import pcgen.gui.Chooser;

/**
 * <code>Feat</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Feat extends PObject
{
	private ArrayList associatedList = new ArrayList();
  String description = new String();
  String type = new String();
  ArrayList skillNameList = new ArrayList(); // skill prereqs
  boolean stacks = false;
  boolean multiples = false;
  String addString = new String();
  ArrayList cSkillList = new ArrayList();
  ArrayList ccSkillList = new ArrayList();
  Integer levelsPerRepIncrease = new Integer(0);
  int addSpellLevel = 0; // for metamagic feats increase in spelllevel
  double cost = 1.0;

  public Object clone()
  {
    Feat aFeat = (Feat)super.clone();
    associatedList = (ArrayList)associatedList.clone();
    aFeat.description = description();
    aFeat.skillNameList = skillNameList();
    aFeat.type = type();
    aFeat.multiples = multiples();
    aFeat.stacks = stacks();
    aFeat.addString = addString();
    aFeat.cSkillList = (ArrayList)cSkillList().clone();
    aFeat.ccSkillList = (ArrayList)ccSkillList().clone();
    aFeat.levelsPerRepIncrease = levelsPerRepIncrease;
    aFeat.isSpecified = isSpecified;
    aFeat.addSpellLevel = addSpellLevel;
    return aFeat;
  }

  public String toString()
  {
    return name;
  }

  public String qualifiedName()
  {
    StringBuffer aStrBuf = new StringBuffer(name);
    if (associatedList().size() > 0 && !name.endsWith("Weapon Proficiency"))
    {
      aStrBuf.append(" (");
      int i = 0;
      if (choiceString().length() == 0 || (multiples == true && stacks == true))
      {
        aStrBuf.append(Integer.toString((int)(associatedList().size()*cost)));
        aStrBuf.append("x)");
      }
      else
      {
        for (Iterator e = associatedList().iterator(); e.hasNext();)
        {
          if (i > 0)
            aStrBuf.append(", ");
          aStrBuf.append((String)e.next());
          i++;
        }
        aStrBuf.append(')');
      }
    }
    return aStrBuf.toString();
  }

  public String getRequirements()
  {
    return preReqStrings();
  }

  public boolean canBeSelectedBy(PlayerCharacter aPC)
  {
    return passesPreReqTests();
  }

  private void setDescription(String newString)
  {
    description = newString;
  }

  public String description()
  {
    return description;
  }

  public String type()
  {
    return type;
  }

  private void setType(String aString)
  {
    if (aString.lastIndexOf(":") > -1)
      type = aString.substring(aString.lastIndexOf(":"));
    else
      type = aString;
  }

  /**
   * Returns true if the feat matches the given type (the type is
   * contained in the type string of the feat).
   */

  public boolean matchesType (String featType)
  {
  	return type.toUpperCase().lastIndexOf(featType.toUpperCase()) > -1;
  }


  public ArrayList typeList()
  {
    ArrayList aArrayList = new ArrayList();
    StringTokenizer aTok = new StringTokenizer(type, ".", false);
    while (aTok.hasMoreTokens())
      aArrayList.add(aTok.nextToken());
    return aArrayList;
  }

  public int addSpellLevel()
  {
    return addSpellLevel;
  }

  private void setSkillNameList(String skillList)
  {
    String commadelim = ",";
    StringTokenizer colToken =
      new StringTokenizer(skillList, commadelim, false);
    final int colMax = colToken.countTokens();
    int col = 0;
    if (colMax == 0)
      return;
    skillNameList.ensureCapacity(skillNameList.size() + colMax);
    for (col = 0; col < colMax; col++)
    {
      skillNameList.add(colToken.nextToken());
    }
  }

  public ArrayList skillNameList()
  {
    return skillNameList;
  }

  public boolean multiples()
  {
    return multiples;
  }

  private void setMultiples(String aString)
  {
    if (aString.startsWith("Y"))
      multiples = true;
    else
      multiples = false;
  }

  public boolean stacks()
  {
    return stacks;
  }

  private void setStacks(String aString)
  {
    if (aString.startsWith("Y"))
      stacks = true;
    else
      stacks = false;
  }

  public String addString()
  {
    return addString;
  }

  private void setAddString(String aString)
  {
    addString = aString;
  }

  public void modAdds(boolean addIt)
  {
    StringTokenizer aTok = new StringTokenizer(addString, "|", false);
    if (aTok.countTokens() != 2 || Globals.currentPC == null)
      return;
    String addType = new String(aTok.nextToken());
    String addSec = new String(aTok.nextToken());
    if (addType.equals("WEAPONPROFS"))
    {
      if (addIt == true)
      {
        if (Globals.weaponTypes.contains(addSec))
        {
          for (Iterator e = Globals.getWeaponProfs(addSec).iterator(); e.hasNext();)
          {
            WeaponProf aProf = (WeaponProf)e.next();
            if (!Globals.currentPC.weaponProfList().contains(aProf.name()))
              Globals.currentPC.weaponProfList().add(aProf.name());
          }
        }
        else
          Globals.currentPC.weaponProfList().add(addSec);
      }
      else
      {
        for (Iterator setIter = Globals.currentPC.weaponProfList().iterator();
             setIter.hasNext();)
        {
          String aString = (String)setIter.next();
          if (aString.equals(addSec))
          {
            setIter.remove();
            return;
          }
        }

        for (int j = 0; j < Globals.weaponProfList.size(); j++)
        {
          WeaponProf aProf = (WeaponProf)Globals.weaponProfList.get(j);
          if (aProf.type().equalsIgnoreCase(addSec))
            Globals.currentPC.weaponProfList().remove(aProf.name());
        }
      }
    }
  }

  public void modChoices(boolean addIt)
  {
    StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);

    if (aTok.countTokens() < 1 || Globals.currentPC == null || Globals.currentPC.isImporting == true)
      return;
    int i = 0,j = (int)((Globals.currentPC.feats() + associatedList().size())/cost),num = 0;
    ArrayList aArrayList = new ArrayList(); // available list of choices
    ArrayList bArrayList = new ArrayList(); // selected list of choices
    ArrayList rootArrayList = new ArrayList();
    String choiceType = aTok.nextToken();
    String choiceSec = name();
    Chooser c = new Chooser();
    c.setPoolFlag(false); // user is not required to make any changes
    c.setAllowsDups(stacks()); // only stackable feats can be duped
    c.setVisible(false);
    c.setPool((int)(Globals.currentPC.feats()/cost));
    Iterator e = null;
    String title = "Choices";
    if (Globals.weaponTypes.contains(choiceType))
    {
      title = choiceType + " Weapon Choice";
      ArrayList tArrayList = Globals.getWeaponProfs(choiceType);
      for (e = tArrayList.iterator(); e.hasNext();)
      {
        WeaponProf aProf = (WeaponProf)e.next();
        aArrayList.add(aProf);
      }
      SortedSet pcProfs = (SortedSet)
        Globals.currentPC.getWeaponProfs(choiceType).clone();
      bArrayList.addAll(pcProfs);
      for (Iterator setIter = pcProfs.iterator(); setIter.hasNext();)
      {
        WeaponProf aProf = (WeaponProf)setIter.next();
        Globals.currentPC.weaponProfList().remove(aProf.name());
      }
      j -= (int)(associatedList.size()*cost);
      associatedList = (ArrayList)bArrayList.clone();
    }
    else if (choiceType.equals("SCHOOLS"))
    {
      title = "School Choice";
      aArrayList = Globals.schoolsList;
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("SPELLLIST"))
    {
      title = "Spell Choice";
      PCClass aClass = Globals.currentPC.getClassNamed("Wizard");
      if (aClass != null && aClass.level().intValue() > 0 && Globals.currentPC.adjStats(3) > 11)
      {
        for (e = aClass.spellList().iterator(); e.hasNext();)
        {
          Spell aSpell = (Spell)e.next();
          if (!associatedList().contains(aSpell.keyName()))
            aArrayList.add(aSpell);
        }
        bArrayList = (ArrayList)associatedList.clone();
        num = bArrayList.size();
        c.setPool(Globals.currentPC.adjStats(3) / 2 - 5);
      }
    }
    else if (choiceType.equals("SKILLS"))
    {
      title = "Skill Choice";
      for (e = Globals.currentPC.skillList().iterator(); e.hasNext();)
      {
        Skill aSkill = (Skill)e.next();
        aArrayList.add(aSkill);
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("SKILLSNAMED"))
    {
      title = "Skill Choice";
      while (aTok.hasMoreTokens())
      {
        String aString = aTok.nextToken();
        boolean startsWith = false;
        if (aString.endsWith("%"))
        {
          startsWith = true;
          aString = aString.substring(0, aString.length() - 1);
        }
        for (Iterator e1 = Globals.skillList.iterator(); e1.hasNext();)
        {
          Skill aSkill = (Skill)e1.next();
          if (aSkill.keyName().equals(aString) || (startsWith == true && aSkill.keyName().startsWith(aString)))
            aArrayList.add(aSkill);
        }
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("SKILLLIST") || choiceType.equals("CCSKILLLIST") ||
      choiceType.equals("NONCLASSSKILLLIST"))
    {
      title = "Skill Choice";
      if (aTok.hasMoreTokens())
        choiceSec = new String(aTok.nextToken());
      if (choiceSec.length() > 0 && !choiceSec.equals("LIST"))
      {
        aTok = new StringTokenizer(choiceSec, ",", false);
        while (aTok.hasMoreTokens())
          aArrayList.add(aTok.nextToken());
      }
      else
      {
        for (e = Globals.skillList.iterator(); e.hasNext();)
        {
          Skill aSkill = (Skill)e.next();
          if (choiceType.equals("NONCLASSSKILLLIST") && (aSkill.costForPCClassList(Globals.currentPC.classList).intValue() == 1 ||
            aSkill.isExclusive().startsWith("Y")))
            continue;
          if (aSkill.rootName.length() == 0)
            aArrayList.add(aSkill);
          if (aSkill.rootName.length() > 0 && !rootArrayList.contains(aSkill.rootName))
            aArrayList.add(aSkill.rootName);
          if (aSkill.rootName.length() > 0 && !rootArrayList.contains(aSkill.rootName))
            rootArrayList.add(aSkill.rootName);
        }
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("SPELLLEVEL"))
    {
      // this will need to be re-worked at some point when I can think
      // of a better way.  This feat is different from the others in that
      // it requires a bonus to be embedded in the choice.  Probably this
      // whole feat methodology needs to be re-thought as its getting a bit
      // bloated - a generic way to embed bonuses could be done to simplify
      // this all tremendously instead of so many special cases.
      ArrayList aBonusList = new ArrayList();
      int m = choiceString.length();
      int k=-1;
      StringTokenizer cTok = new StringTokenizer(choiceString,"[]",false);
      String choices = cTok.nextToken();
      while (cTok.hasMoreTokens())
          aBonusList.add(cTok.nextToken());
      getChoices(choices, aBonusList);
      associatedList().clear();
      for(Iterator ii = saveList.iterator();ii.hasNext();ii.next())
        associatedList.add("placeholder");
    }
    else if (choiceType.equals("WEAPONFOCUS"))
    {
      title = "WeaponFocus Choice";
      Feat aFeat = Globals.currentPC.getFeatNamed("Weapon Focus");
      aArrayList = (ArrayList)aFeat.associatedList().clone();
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("WEAPONPROFS"))
    {
      title = "Weapon Prof Choice";
      while (aTok.hasMoreTokens())
      {
        String aString = aTok.nextToken();
        if (aString.equals("LIST"))
          for (Iterator setIter = Globals.currentPC.weaponProfList().iterator();
               setIter.hasNext();)
          {
            String bString = (String)setIter.next();
            if (!aArrayList.contains(bString))
              aArrayList.add(bString);
          }
        else if (aString.startsWith("Size."))
        {
          if (Globals.currentPC.sizeInt() >=
            Globals.currentPC.sizeIntForSize(aString.substring(5, 6)) &&
            Globals.currentPC.weaponProfList().contains(aString.substring(7)) &&
            !aArrayList.contains(aString.substring(7)))
            aArrayList.add(aString.substring(7));
        }
        else if (aString.startsWith("WSize."))
        {
          for (Iterator setIter = Globals.currentPC.weaponProfList().iterator();
               setIter.hasNext();)
          {
            String bString = (String)setIter.next();
            WeaponProf wp = Globals.getWeaponProfNamed(bString);
            if (wp == null)
              continue;
            if (aString.endsWith("Light") && wp.isLight() && !aArrayList.contains(bString))
              aArrayList.add(bString);
            if (aString.endsWith("1 handed") && wp.isOneHanded() && !aArrayList.contains(bString))
              aArrayList.add(bString);
            if (aString.endsWith("2 handed") && wp.isTwoHanded() && !aArrayList.contains(bString))
              aArrayList.add(bString);
          }
        }
        else if (aString.startsWith("SpellCaster."))
        {
          if (Globals.currentPC.isSpellCaster(1) && !aArrayList.contains(aString.substring(12)))
            aArrayList.add(aString.substring(12));
        }
        else if (aString.startsWith("ADD."))
        {
          if (!aArrayList.contains(aString.substring(4)))
            aArrayList.add(aString.substring(4));
        }
        else if (aString.startsWith("TYPE."))
        {
          for (Iterator setIter = Globals.currentPC.weaponProfList().iterator();
               setIter.hasNext();)
          {
            String bString = (String)setIter.next();
            WeaponProf wp = Globals.getWeaponProfNamed(bString);
            if (wp == null)
              continue;
            Equipment eq = Globals.getEquipmentKeyed(wp.keyName());
            if (eq == null)
              continue;
            if (eq.typeString().lastIndexOf(aString.substring(5)) > -1 && !aArrayList.contains(wp.name()))
              aArrayList.add(wp.name());
          }
        }
        else
        {
          if (Globals.currentPC.weaponProfList().contains(aString) &&
            !aArrayList.contains(aString))
            aArrayList.add(aString);
        }
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("HP"))
    {
      if (aTok.hasMoreTokens())
        choiceSec = aTok.nextToken();
      aArrayList.add(choiceSec);
      for (Iterator e1 = associatedList.iterator(); e1.hasNext(); e1.next())
        bArrayList.add(choiceSec);
    }
    else if (choiceType.startsWith("FEAT="))
    {
      Feat aFeat = Globals.currentPC.getFeatNamed(choiceType.substring(5));
      if (aFeat != null)
        aArrayList = aFeat.associatedList();
      bArrayList = (ArrayList)associatedList.clone();
    }
    else if (choiceType.equals("FEATLIST"))
    {
      bArrayList = (ArrayList)associatedList.clone();
      while (aTok.hasMoreTokens())
      {
        String aString = aTok.nextToken();
        if (aString.startsWith("TYPE="))
        {
          aString = aString.substring(5);
          if (!stacks && aArrayList.contains(aString))
            continue;
          for (Iterator e1 = Globals.currentPC.featList.iterator(); e1.hasNext();)
          {
            Feat aFeat = (Feat)e1.next();
            if (aFeat.type().equals(aString) && (stacks || (!stacks && !aArrayList.contains(aFeat.name()))))
              aArrayList.add(aFeat.name());
          }
        }
        else if (Globals.currentPC.getFeatNamed(aString) != null)
        {
          if (stacks || (!stacks && !aArrayList.contains(aString)))
            aArrayList.add(aString);
        }
      }
    }
    else if (choiceType.equals("SPELLCLASSES"))
    {
      title = "Spellcaster Classes";
      for (e = Globals.currentPC.classList.iterator(); e.hasNext();)
      {
        PCClass aClass = (PCClass)e.next();
        if (!aClass.spellBaseStat().equals("None"))
          aArrayList.add(aClass);
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    else
    {
      title = "Selections";
      aArrayList.add(choiceType);
      while (aTok.hasMoreTokens())
      {
        String aString = aTok.nextToken();
        if (stacks || (!stacks && !aArrayList.contains(aString)))
          aArrayList.add(aString);
      }
      bArrayList = (ArrayList)associatedList.clone();
    }
    title = title + " (" + name + ")";
    c.setTitle(title);
    if (!choiceType.equals("SPELLLEVEL"))
    {
      c.setAvailableList(aArrayList);
      c.setSelectedList(bArrayList);
      c.show();
    }
    if (choiceType.equals("SPELLLIST"))
    {
      int x = Globals.currentPC.feats();
      if (c.getSelectedList().size() > num)
        Globals.currentPC.setFeats(x - 1);
      if (c.getSelectedList().size() < num)
        Globals.currentPC.setFeats(x + 1);
    }
    for (e = cSkillList.iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (!aString.equals("LIST") && !Globals.getFeatNamed(name).cSkillList.contains(aString))
        e.remove();
    }
    if (!choiceType.equals("SPELLLEVEL"))
    {
      ccSkillList().clear();
      associatedList().clear();
    }
    for (i = 0; i < c.getSelectedList().size(); i++)
    {
      if (choiceType.equals("HP"))
        associatedList().add("CURRENTMAX");
      else if (multiples && !stacks)
      {
        if (!associatedList.contains(c.getSelectedList().get(i)))
          associatedList.add(c.getSelectedList().get(i));
      }
      else
        associatedList().add(c.getSelectedList().get(i));
      if (choiceType.equals("SKILLLIST") || choiceType.equals("NONCLASSSKILLLIST"))
      {
        String aString = (String)c.getSelectedList().get(i);
        if (rootArrayList.contains(aString))
        {
          for (Iterator e2 = Globals.skillList.iterator(); e2.hasNext();)
          {
            Skill aSkill = (Skill)e2.next();
            if (aSkill.rootName.equals(aString))
              cSkillList().add(aSkill.name());
          }
        }
        else
          cSkillList().add(aString);
      }
      else if (choiceType.equals("CCSKILLLIST"))
      {
        String aString = (String)c.getSelectedList().get(i);
        if (rootArrayList.contains(aString))
        {
          for (Iterator e2 = Globals.skillList.iterator(); e2.hasNext();)
          {
            Skill aSkill = (Skill)e2.next();
            if (aSkill.rootName.equals(aString))
              ccSkillList().add(aSkill.name());
          }
        }
        else
          ccSkillList().add(aString);
      }
      if (Globals.weaponTypes.contains(choiceType))
        Globals.currentPC.addWeaponProf(c.getSelectedList().get(i).toString());
    }
    if (!choiceType.equals("SPELLLIST"))
      Globals.currentPC.setFeats((int)((j - associatedList().size() + bArrayList.size())*cost));
  }

  public String choiceString()
  {
    return choiceString;
  }

  private void setChoiceString(String aString)
  {
    choiceString = aString;
  }

  public ArrayList associatedList()
  {
    return associatedList;
  }
  public double cost()
  {
    return cost;
  }

  public boolean isInList(String aType)
  {
    if (aType.lastIndexOf("=") > -1)
      aType = aType.substring(aType.lastIndexOf("=") + 1);
    if (aType.lastIndexOf("+") > -1) // truncate at + sign if following character is a number
    {
      final String numString = "0123456789";
      final String aString = aType.substring(aType.lastIndexOf("+")+1);
      if (numString.lastIndexOf(aString.substring(0,1))>0)
        aType = aType.substring(0, aType.lastIndexOf("+"));
    }
    if (aType.lastIndexOf("-") > -1) // truncate at - sign if following character is a number
    {
      final String numString = "0123456789";
      final String aString = aType.substring(aType.lastIndexOf("-")+1);
      if (numString.lastIndexOf(aString.substring(0,1))>0)
        aType = aType.substring(0, aType.lastIndexOf("-"));
    }
    return associatedList.contains(aType);
  }

  public int addStatBonuses(String aString)
  {
    int retVal = 0;
    for (Iterator e = associatedList.iterator(); e.hasNext();)
    {
      PCClass aClass = (PCClass)Globals.currentPC.getClassNamed((String)e.next());
      if (aClass != null && aClass.spellBaseStat().equals(aString))
        retVal++;
    }
    return retVal;
  }

  public ArrayList cSkillList()
  {
    return cSkillList;
  }

  private void setCSkillList(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, "|", false);
    while (aTok.hasMoreTokens())
      cSkillList().add(aTok.nextToken());
  }

  public boolean hasCSkill(String aName)
  {
    if (cSkillList().contains(aName))
      return true;
    if (cSkillList().contains("LIST"))
    {
      for (Iterator e = associatedList.iterator(); e.hasNext();)
      {
        String aString = (String)e.next();
        if (aName.startsWith(aString) || aString.startsWith(aName))
          return true;
      }
    }
    for (Iterator e = cSkillList().iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (aString.lastIndexOf("%") > -1)
      {
        aString = aString.substring(0, aString.length() - 1);
        if (aName.startsWith(aString))
          return true;
      }
    }
    return false;
  }

  public ArrayList ccSkillList()
  {
    return ccSkillList;
  }

  private void setCCSkillList(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString, "|", false);
    while (aTok.hasMoreTokens())
      ccSkillList().add(aTok.nextToken());
  }

  public boolean hasCCSkill(String aName)
  {
    if (cSkillList().contains(name()))
      return true;
    for (Iterator e = ccSkillList().iterator(); e.hasNext();)
    {
      String aString = (String)e.next();
      if (aString.lastIndexOf("%") > -1)
      {
        aString = aString.substring(0, aString.length() - 1);
        if (aName.startsWith(aString))
          return true;
      }
    }
    return false;
  }

  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    String tabdelim = "\t";
    String commadelim = ",";
    StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
    int colMax = colToken.countTokens();
    int col = 0;
    if (colMax == 0)
      return;
    for (col = 0; col < colMax; col++)
    {
      String aCol = colToken.nextToken();
      int len = aCol.length();
      String subs = aCol.substring(0, 3);
      if (col == 0)
        setName(aCol);
      else if (aCol.startsWith("ADD"))
        setAddString(aCol.substring(4));
      else if (aCol.startsWith("BONUS"))
        addBonusList(aCol.substring(6));
      else if (aCol.startsWith("DESC"))
        setDescription(aCol.substring(5));
      //Is this like PRESKILl
      else if (aCol.startsWith("SKILL:"))
        setSkillNameList(aCol.substring(6));
      else if (aCol.startsWith("TYPE"))
        setType(aCol.substring(5));
      else if (aCol.startsWith("MULT"))
        setMultiples(aCol.substring(5));
      else if (aCol.startsWith("STACK"))
        setStacks(aCol.substring(6));
      else if (aCol.startsWith("CHOOSE"))
        setChoiceString(aCol.substring(7));

      else if ((len > 14) && aCol.startsWith("ADDSPELLLEVEL:"))
      {
        try
        {
          addSpellLevel = pcgen.util.Delta.parseInt(aCol.substring(14));
        }
        catch (NumberFormatException nfe)
        {
          JOptionPane.showMessageDialog(null, "Bab addSpellLevel " + aCol);
        }
      }
      else if (aCol.startsWith("CSKILL"))
        setCSkillList(aCol.substring(7));
      else if (aCol.startsWith("CCSKILL"))
        setCCSkillList(aCol.substring(7));

      else if (aCol.startsWith("REP"))
      {
        try
        {
          levelsPerRepIncrease = pcgen.util.Delta.decode(aCol.substring(4));
        }
        catch (NumberFormatException nfe)
        {
          JOptionPane.showMessageDialog(null, "Bad level per value " + aCol);
        }
      }
      else if (aCol.startsWith("DEFINE"))
        variableList.add("0|" + aCol.substring(7));
      else if (aCol.startsWith("KEY:"))
        setKeyName(aCol.substring(4));
      else if (aCol.startsWith("PRE"))
        preReqArrayList.add(aCol);
      else if (aCol.startsWith("COST"))
      {
        cost = Double.parseDouble(aCol.substring(5));
      }
      else
        JOptionPane.showMessageDialog
          (null, "Illegal feat info " +
          sourceFile.getName() + ":" + Integer.toString(lineNum) +
          " \"" + aCol + "\"");
    }
  }
}
