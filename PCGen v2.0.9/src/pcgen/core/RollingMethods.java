/*
 * RollingMethods.java
 * Copyright 2001 (C) Mario Bonassin
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
import java.util.Enumeration;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * <code>RollingMethods</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class RollingMethods
{
  
  // Random number creator
  private static Random random = new Random(System.currentTimeMillis());
  
  /*
   * this is just one random number between 1 and sides, good for %
   */
  public static int roll(int sides)
  {
    return random.nextInt(sides) + 1;
  }
  

  /*
  * this is the basic 2d6 function
  */
  public static int roll(int times, int sides)
  {
    int total =0;
    for(int rolls = 0; rolls < times; rolls++) 
    {
      total += random.nextInt(sides) + 1;
      //System.out.println("each " +total);
    }
    return total;
  }
  
  /*
   * this is roll #d# drop lowest.
   *
   */
  public static int roll(int times, int sides, boolean low)
  {
    int total=0;
    int lowest = 6;
    for(int rolls = 0; rolls < times; rolls++) 
    {
      int roll = random.nextInt(sides) + 1;
      if (roll<lowest)
      {
        lowest = roll;
      }
      total += roll;
      //System.out.println("each roll-" +roll);
    }
    total = total - lowest;
    //System.out.println("each roll total "+total);
    return total;
  }

  /*
   * this is roll #d# drop multi lowest.
   *
   
  public static int roll(int times, int sides, boolean low, boolean multi)
  {
    int total=0;
    int lowest1 = 6;
    int lowest2 = 6;
    for(int rolls = 0; rolls < times; rolls++) 
    {
      int roll = random.nextInt(sides) + 1;
      if (roll<lowest1)
      {
        lowest1 = roll;
      }
      else if (roll>lowest1 && roll<lowest2)
      {
        lowest2 = roll;
      }
      total += roll;
      System.out.println("each roll-" +roll);
    }
    total = total - lowest1;
    total = total - lowest2;
    System.out.println("each roll total "+total);
    return total;
  }*/
    
  /*
   *  #d# reroll # or less and drop lowest.
   *
   */
  public static int roll(int times, int sides, boolean low, int reroll)
  {
    int total=0;
    int lowest = 6;
    for(int rolls = 0; rolls < times; rolls++) 
    {
      int roll = random.nextInt(sides) + 1;
      //System.out.println("sep " +roll);
      if (roll<=reroll)
      {
        roll = random.nextInt(sides) + 1;
        //System.out.println("rerolls "+roll);
      }
      if (roll<lowest)
      {
        lowest = roll;
      }
      total += roll;
    }
    total = total - lowest;
    //System.out.println("final total "+total);
    return total;
  } 
  
  /*
   *  #d# +/- #  ie 4d6 +1
   *
   */  
  public static int roll(int times, int sides, int modifier) 
  {
    int total = 0;
    for(int rolls = 0; rolls < times; rolls++) 
    {
      total += random.nextInt(sides) + 1;
      //System.out.println("modi "+total);
    }
    total += modifier;
    return total;
  }

  /*
   *  takes a string in the form of "2d6 -2" and returns the result.
   *  the form may use either 'd' or 'D' and '+' or '-'. 
   *  there must be a space between the sides and +/- sign.  like '#d# +#'
   */
  public static int roll(String method) 
  {
    StringTokenizer st = new StringTokenizer(method, "dD +", false);
    Integer times, sides, modifier;
    try 
    {
      times = new Integer(st.nextToken());
      sides = new Integer(st.nextToken());
      if (st.hasMoreTokens()) 
      {
        modifier = new Integer(st.nextToken());
      } 
      else 
      {
        modifier = new Integer(0);
      }
      return (roll(times.intValue(), sides.intValue()) + modifier.intValue());
     } 
     catch (Exception e) 
     {
       return 0;
     }
   }

 /*public static void main(String args[])
  {

        
      System.out.println("1.  " + roll(5,6,true,true));
      System.out.println("2.  " + roll(100));
      System.out.println("3.  " + roll(4,6,true,1));
      System.out.println("4.  " + roll(4,6,true,2));
      System.out.println("5.  " + roll(3,6,5));
      System.out.println("6.  " + roll(3,6,-5));
      System.out.println("7.  " + roll("4D6 +1"));
      System.out.println("8.  " + roll("4d6 -3"));
      

}*/
}