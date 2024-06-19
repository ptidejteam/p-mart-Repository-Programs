/*
 * WeaponProf.java
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

import java.util.*;
import java.io.File;
import javax.swing.JOptionPane;

// Migration to XML resources
import pcgen.xml.FindXML;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <code>WeaponProf</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class WeaponProf extends PObject implements Comparable
{
  String type = new String();
  String size = "S";
  Integer attacks = new Integer(1);
  Integer hands = new Integer(1);

  public String toString()
  {
    return name;
  }
  public String type()
  {
    return type;
  }
  private void setType(String aString)
  {
    type = aString;
    Globals.weaponTypes.add(aString);
    Globals.weaponTypes.add(aString.toUpperCase());
  }
  public String size()
  {
    return size;
  }
  public int sizeInt()
  {
    String sizeString ="FDTSMLHGC";
    int i = sizeString.lastIndexOf(size().charAt(0));
    return i;
  }
  private void setHands(String aString)
  {
    hands = new Integer(aString);
  }
  public boolean isLight()
  {
    boolean aBool = (Globals.currentPC!=null && Globals.currentPC.sizeInt()>sizeInt());
    return aBool;
  }
  public boolean isOneHanded()
  {
    boolean aBool = (Globals.currentPC!=null && Globals.currentPC.sizeInt()>=sizeInt() && hands.intValue()==1);
    return aBool;
  }
  public boolean getTwoHandedStrBonus()
  {
    boolean aBool = (Globals.currentPC!=null && Globals.currentPC.sizeInt()==sizeInt() && hands.intValue()==1);
    return aBool;
  }
  public boolean isTwoHanded()
  {
    boolean aBool = (Globals.currentPC!=null && Globals.currentPC.sizeInt()==sizeInt()-1 || hands.intValue()==2);
    return aBool;
  }
  public boolean isTooLarge()
  {
    boolean aBool = (Globals.currentPC!=null && Globals.currentPC.sizeInt()<sizeInt()-1);
    return aBool;
  }
  public int hands()
  {
    if (isOneHanded())
      return 1;
    if (isTwoHanded())
      return 2;
    else
      return 3;
  }
  private void setSize(String aString)
  {
    size = aString;
  }
  public boolean meetsPreReqs()
  {
    return passesPreReqTests();
  }
  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    String tabdelim = "\t";
    StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
    int colMax = colToken.countTokens();
    int col=0;
    Integer anInt = new Integer(0);
    if (colMax==0)
      return;
    for (col=0; col<colMax; col++) {
      String aString = (String)colToken.nextToken();
      if (col==0)
        setName(aString);
      else if (aString.startsWith("TYPE"))
        setType(aString.substring(5));
      else if (aString.startsWith("SIZE"))
        setSize(aString.substring(5));
      else if (aString.startsWith("HANDS"))
        setHands(aString.substring(6));
      else if (aString.startsWith("BONUS"))
        addBonusList(aString.substring(6));
      else if (aString.startsWith("DEFINE"))
        variableList.add("0|"+aString.substring(7));
      else if (aString.startsWith("PRE"))
        preReqArrayList.add(aString);
      else
        JOptionPane.showMessageDialog
          (null, "Illegal weapon proficiency info " +
           sourceFile.getName() + ":" + Integer.toString(lineNum) +
           " \"" + aString + "\"");
    }

  }

  public int compareTo(Object o1)
  {
    return keyName.compareTo(((WeaponProf)o1).keyName);
  }

  public boolean equals(Object o1)
  {
    return keyName.equals(((WeaponProf)o1).keyName);
  }

  public int hashCode()
  {
    return keyName.hashCode();
  }
}
