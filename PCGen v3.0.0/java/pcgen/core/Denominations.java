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

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Denominations class provides a collection of Denominations.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */

public class Denominations implements iDefaultRankConstants
{
	private ArrayList coinDenominations;
	private Denomination defaultCoin;
	private String region;
	private int defaultRank = DEFAULT_NONE;

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
		this.defaultRank = DEFAULT_SPECIFIED;
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
		this.defaultRank = DEFAULT_SPECIFIED;
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
			name = d.getName();
			if (name.equalsIgnoreCase(coin.getName()))
			{
				d = coin;
				found = true;
				break;
			}
		}

		if (!found)
		{
			coinDenominations.add(coin);
			String ucName = coin.getName().toUpperCase();

			if (ucName.equals("GOLD") && defaultRank < DEFAULT_GOLD)
			{
				this.defaultCoin = coin;
				this.defaultRank = DEFAULT_GOLD;
			}
			else if (defaultRank < DEFAULT_INCLUDES_GOLD &&
				(ucName.indexOf(" GOLD ") >= 0
				|| ucName.startsWith("GOLD ")
				|| ucName.endsWith(" GOLD")))
			{
				this.defaultCoin = coin;
				this.defaultRank = DEFAULT_INCLUDES_GOLD;
			}

		}
		Collections.sort(coinDenominations);
		if (coinDenominations.size() > 0 && defaultRank <= DEFAULT_HIGHEST_VALUE)
		{
			this.defaultCoin = (Denomination)coinDenominations.get(0);
			this.defaultRank = DEFAULT_HIGHEST_VALUE;
		}
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
		if (isDefault && defaultRank < DEFAULT_SPECIFIED)
		{
			this.defaultCoin = newCoin;
			this.defaultRank = DEFAULT_SPECIFIED;
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
		StringBuffer result = new StringBuffer();

		Iterator i = coinDenominations.iterator();
		Denomination d;

		while (i.hasNext())
		{
			d = (Denomination)i.next();
			result.append(d).append("\n");
		}

		return result.toString();
	}

	public Denomination getDefaultCoin()
	{
		return defaultCoin;
	}

	public void setDefaultCoin(Denomination defaultCoin)
	{
		this.defaultCoin = defaultCoin;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public int getDefaultRank()
	{
		return defaultRank;
	}

	public void setDefaultRank(int defaultRank)
	{
		this.defaultRank = defaultRank;
	}

}
