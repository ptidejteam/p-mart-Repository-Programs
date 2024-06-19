/*
 * Campaign.java
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
import java.io.*;
import javax.swing.JOptionPane;
import pcgen.core.PObject;

/**
 * <code>Campaign</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Campaign extends PObject
{
  boolean isLoaded = false;
  Integer rank = new Integer(9);
  String game = "";
  public boolean isLoaded()
  {
    return isLoaded;
  }
  public void setIsLoaded(boolean aBool)
  {
    isLoaded = aBool;
  }
  public Integer rank()
  {
    return rank;
  }
  private void setRank(Integer aRank)
  {
    rank = aRank;
  }
  private void setGame(String aGame)
  {
    game = aGame;
  }
  public String game()
  {
    return game;
  }
  public void parseLine(String inputLine, File sourceFile, int lineNum)
  {
    if (inputLine.startsWith("CAMPAIGN:"))
    {
      setName(inputLine.substring(9));
    }
    else if (inputLine.startsWith("RANK:"))
    {
      setRank(Integer.valueOf(inputLine.substring(5)));
    }
    else if (inputLine.startsWith("GAME:"))
    {
      setGame(inputLine.substring(5)); 
    }
  }
}
