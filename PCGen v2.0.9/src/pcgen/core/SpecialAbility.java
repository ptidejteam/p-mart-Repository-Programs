/*
 * SpecialAbility.java
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
 * <code>SpecialAbility</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class SpecialAbility extends Object
{
  String name = new String();
  String desc = new String();
  
  public String name()
  {
    return name;
  }
  public String toString()
  {
    return name;
  }
  private void setName(String aString)
  {
    name = aString;
  }
  public String desc()
  {
    return desc;
  }
  private void setDesc(String aString)
  {
    desc = aString;
  }
  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    StringTokenizer aTok = new StringTokenizer(inputLine,"\t",false);
    int i = 0;
    while(aTok.hasMoreElements())
    {
      String aString = (String)aTok.nextElement();
      if (i==0)
        setName(aString);
      else if (i==1)
        setDesc(aString);
      else {
        JOptionPane.showMessageDialog
          (null, "Illegal special ability info " + sourceFile.getName() +
           ":" + Integer.toString(lineNum) + " \"" + aString + "\"");
      }
      i++;
    }
  }
}
