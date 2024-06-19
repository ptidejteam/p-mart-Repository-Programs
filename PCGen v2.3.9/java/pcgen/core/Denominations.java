/*
 * Denominations.java
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

/**
 * Denominations class provides a collection of Denominations.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Denominations
{
  private ArrayList coinDenominations;
  public Denomination defaultCoin;
  public String region;

  public Denominations()
  {
    coinDenominations = new ArrayList();
  }

  /**
   * Constructor sets the default coin to the passed Denomination
   * object.  The default coin is the one that will be operated on
   * in the absence of any other specifier.
   */
  public Denominations(Denomination defaultCoin)
  {
    this();
    coinDenominations.add(defaultCoin);
    this.defaultCoin = defaultCoin;
  }

  /**
   * Constructor creates a new denomination object and sets the default coin
   * to that object.  The default coin is the one that will be
   * operated on in the absence of any other specifier.
   */
  public Denominations(String coinName, String abbr, int factor, float weight)
  {
    this();
    coinDenominations.add(this.defaultCoin = new Denomination(coinName, abbr, factor, weight));
//    this.defaultCoin = defaultCoin;
  }

  /**
   * Adds a denomination to the collection.
   *
   * @param coin  the Denomination object to be added
   */
  public void addDenomination(Denomination coin)
  {
    Iterator i = coinDenominations.iterator();
    Denomination d;
    String name;
    boolean found = false;

    while (i.hasNext())
    {
      d = (Denomination)i.next();
      name = d.name;
      if (name.equalsIgnoreCase(coin.name))
      {
        d = coin;
        found = true;
        break;
      }
    }

    if (!found)
    {
      coinDenominations.add(coin);
    }
    Collections.sort(coinDenominations);
  }

  /**
   * Creates a Denomination object and adds it to the collection.
   *
   * @param name     the name of the denomination
   * @param abbr     the abbreviation for the denomination
   * @param factor   the factor that describes this denominations's
   *                 relationship to other denominations
   * @param weight   the weight of the coin.
   */
  public void addDenomination(String name, String abbr, int factor, float weight)
  {
    addDenomination(new Denomination(name, abbr, factor, weight));
  }

  /**
   * Creates a Denomination object and adds it to the collection.
   *
   * @param name     the name of the denomination
   * @param abbr     the abbreviation for the denomination
   * @param factor   the factor that describes this denominations's
   *                 relationship to other denominations
   * @param weight   the weight of the coin.
   * @param isDefault  indicates if this is the default coin.
   */
  public void addDenomination(String name, String abbr, int factor,
    float weight, boolean isDefault)
  {
    Denomination newCoin = new Denomination(name, abbr, factor, weight);
    addDenomination(newCoin);
    if (isDefault)
    {
      this.defaultCoin = newCoin;
    }
  }

  /**
   * Passes the coinDenominations ArrayList's iterator back to the caller.
   */
  public Iterator iterator()
  {
    return coinDenominations.iterator();
  }

  public String toString()
  {
    String result = "";

    Iterator i = coinDenominations.iterator();
    Denomination d;
    String name;
    boolean found = false;

    while (i.hasNext())
    {
      d = (Denomination)i.next();
      result += d + "\n";
    }

    return result;
  }

}
