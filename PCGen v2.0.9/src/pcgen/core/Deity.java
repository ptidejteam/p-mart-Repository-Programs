/*
 * Deity.java
 * Copyright 2001 (C) Bryan McRoberts (mocha@mcs.net)
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

import java.util.*;
import java.io.File;
import javax.swing.JOptionPane;

// Migration to XML resources
import pcgen.xml.FindXML;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * This class stores all the pertinent information about a deity in
 * a d20 world.  A deity can support various domains of spells,
 * various alignments, various races, etc.
 * 
 * @author  Bryan McRoberts
 * @version $Revision: 1.1 $
 */
import pcgen.util.Delta;

/**
 * <code>Deity</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Deity extends PObject
{
  private ArrayList domainList = new ArrayList();
  private String alignments = "";
  private String description = "";
  private String holyItem = "";
  private String favoredWeapon = "ALL";
  private ArrayList raceList = new ArrayList();
  private String alignment = "";
  private String specialAbility = "";
  private boolean d_allDomains = false;

  public final static String[] s_alignStrings = 
  {
    "LG",
    "LN",
    "LE",
    "NG",
    "TN",
    "NE",
    "CG",
    "CN",
    "CE"
  };

  public String toString()
  {
    return name;
  }

  public ArrayList domainList()
  {
    return domainList;
  }

  public String domainListString()
  {
    StringBuffer aString = new StringBuffer();
    for (Iterator e = domainList.iterator(); e.hasNext();)
    {
      if (aString.length() > 0) aString.append(',');
      aString.append((String)e.next());
    }
    return aString.toString();
  }

  public boolean hasDomainNamed(String aString)
  {
    ArrayList dList = domainList();
    return d_allDomains || dList.contains(aString);
  }

  private void setDomainList(String aString)
  {
    String delim = ",";
    ArrayList dList = domainList();
    StringTokenizer colToken = new StringTokenizer(aString, delim, false);
    dList.clear();             // remove all previous contents
    d_allDomains = false;
    dList.ensureCapacity(colToken.countTokens());
    while (colToken.hasMoreTokens()) 
    {
      String nextTok = colToken.nextToken();
      dList.add(nextTok);
      d_allDomains = d_allDomains || nextTok.equals("ALL");
    }
  }

  public String alignments()
  {
    return alignments;
  }

  public String alignment()
  {
    return alignment;
  }
  
  private void setAlignments(String aString)
  {
    alignments = aString;
  }

  public boolean allowsAlignment(int index)
  {
    boolean flag = alignments.lastIndexOf(String.valueOf(index))>-1;
    if (Globals.debugMode)
      System.out.println("AL="+flag);
    return flag;
  }

  final private boolean acceptableClass(Iterator classList)
  {
    boolean flag = (!classList.hasNext());
    while (classList.hasNext() && flag==false)
    {
      PCClass aClass = (PCClass)classList.next();
      String  deityString = aClass.deityString;
      if (deityString.length() > 0) 
      {
        if (deityString.equals("ANY") || deityString.equals("ALL"))
          flag=true;
        StringTokenizer aTok = new StringTokenizer(deityString, "|", false);
        while (aTok.hasMoreTokens())
        {
          if (name().equals(aTok.nextToken())) 
          {
            flag=true;
          }
        }
      }
    }
    if (Globals.debugMode)
      System.out.println("CLASS="+flag);
    return flag;
  }

  private final boolean acceptablePreReqs()
  {
    return passesPreReqTests();
  }

  /**
   * Check whether this deity can be selected by a character with the
   * given classes, alignment, race and gender.
   *
   * @param classList a vector of PCClass objects.
   * @param alignment 0 through 8 inclusive
   * @param raceName  the name of the characters race.
   * @param gender    M or F.
   * @return <code>true</code> means the deity can be a selected by a
   * character with the given properties; <code>false</code> means the
   * character cannot.
   */
  public boolean canBeSelectedBy(ArrayList classList,
				 int    anAlignment,
				 String raceName,
				 String gender)
  {
    boolean result = false;
    if (Globals.debugMode)
      System.out.println("DEITY="+name);
    try
    {
      result =
        acceptableClass(classList.iterator()) &&
        allowsAlignment(anAlignment) &&
        acceptablePreReqs();
    }
    catch (NumberFormatException nfe)
    {
      result = false;
    }
    return result;
  }

  public String description()
  {
    return description;
  }

  private void setDescription(String aString)
  {
    description = aString;
  }

  public String holyItem()
  {
    return holyItem;
  }

  private void setHolyItem(String aString)
  {
    holyItem = aString;
  }

  public String favoredWeapon()
  {
    return favoredWeapon;
  }

  private void setFavoredWeapon(String aString)
  {
    favoredWeapon = aString;
  }

  public ArrayList raceList()
  {
    return raceList;
  }

  private void setRaceList(String aString)
  {
    StringTokenizer aTok = new StringTokenizer(aString,"|",false);
    raceList.clear();           // remove previous contents
    while(aTok.hasMoreTokens())
      raceList.add(aTok.nextToken());
  }

  public final String getSpecialAbility() 
  {
    return specialAbility;
  }

  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    String tabdelim = "\t";
    StringTokenizer colToken =
      new StringTokenizer(inputLine, tabdelim, false);
    int col=0;
    while (colToken.hasMoreTokens())
    {
      String colString = colToken.nextToken();
      int colLen = colString.length();
      if ((colLen > 5) && colString.startsWith("RACE:"))
        setRaceList(colString.substring(5));
      else if ((colLen > 6) && colString.startsWith("ALIGN:"))
        alignment = colString.substring(6);
      else if ((colLen > 7) && colString.startsWith("DEFINE"))
        variableList.add("0|"+colString.substring(7));
      else if ((colLen > 4) && colString.startsWith("KEY:"))
        setKeyName(colString.substring(4));
      else if (colString.startsWith("PRE"))
        preReqArrayList.add(colString);
      else if ((colLen > 3) && colString.startsWith("SA:"))
        specialAbility = specialAbility + colString.substring(3);
      else if (col==0)
      {
        setName(colString);
        col++;
      }
      else if (col==1)
      {
        setDomainList(colString);
        col++;
      }
      else if (col==2)
      {
        setAlignments(colString);
        col++;
      }
      else if (col==3)
      {
        setDescription(colString);
        col++;
      }
      else if (col==4)
      {
        setHolyItem(colString);
        col++;
      }
      else if (col==5)
      {
        setFavoredWeapon(colString);
        col++;
      }
      else
        JOptionPane.showMessageDialog
          (null, "Illegal deity info " +
           sourceFile.getName() + ":" + Integer.toString(lineNum) +
           " \"" + colString + "\"");
    }
  }  
}
