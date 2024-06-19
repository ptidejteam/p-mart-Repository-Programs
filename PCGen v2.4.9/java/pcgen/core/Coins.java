/*
 * Coins.java
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
 * Coins class encapsulates a collection of coins objects,
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Coins
{

	private ArrayList coins;
	public Coin defaultCoin;

	/**
	 * Parse a string into an amount and a name.
	 *
	 * @param coinAmount  a string containing the amount and name of
	 *                    the coin to set.  The string should have the
	 *                    amount first, with an optional sign, followed
	 *                    by zero or more spaces, and the name or
	 *                    abbreviation of the coin to be set.
	 *                    e.g.: "2 gp", "3 silver", "1pp".  The name
	 *                    or abbreviation must be that of a coin in the
	 *                    collection, or the method will return null.
	 *
	 * @return   a Coin object with the amount and name.
	 *
	 */
	public static Coin parseCoin(String coinAmount, Coins someCoins)
	{
		coinAmount = coinAmount.trim();
		Coin tempCoin = null; // new Coin("", "", 0, 0);

		if (coinAmount.length() > 0)
		{

			String coinName = "";
			long amount = 0L;
			int index = 0;

			// First, parse the passed string into it's constituent parts
			// If there is a plus sign at the beginning of the string,
			// strip it off, since the "parseType" functions apparently
			// don't deal with those.
			if (coinAmount.charAt(index) == '+')
				coinAmount = coinAmount.substring(++index);

			int pos = index;
			while (pos < coinAmount.length())
			{

				final char c = coinAmount.charAt(pos);
				if (c < '0' || c > '9')
				{
					amount = Long.parseLong(coinAmount.substring(index, pos));
					coinName = coinAmount.substring(pos);
					coinName = coinName.trim();
					break;
				}

				++pos;
			}

			// If we made it through the entire string without meeting something
			// that wasn't a number, then assume the default coin.
			if (pos >= coinAmount.length())
			{
				if (someCoins.defaultCoin != null)
				{
					coinName = someCoins.defaultCoin.getName();
					amount = Long.parseLong(coinAmount);
				}
				else
				{
					return null;
				}
			}

			tempCoin = someCoins.cloneCoin(coinName);
			if (tempCoin != null)
			{
				tempCoin.amount = amount;
			}

		}

		return tempCoin;

	}

	/**
	 * Constructor creates a new Coins collection, using the passed coin
	 * object as the default coin.  The default coin is the one that will
	 * be operated on in the absence of any other specifier.
	 */
	public Coins(Coin defaultCoin)
	{
		coins = new ArrayList();
		coins.add(defaultCoin);
		this.defaultCoin = defaultCoin;
	}

	/**
	 * Constructor creates a new Coins collection, creating a new Coin
	 * object from the passed denomination, and using that Coin as the
	 * default coin.
	 *
	 * @param defaultCoin  the denomination of the default coin.
	 */
	public Coins(Denomination defaultCoin)
	{
		coins = new ArrayList();
		coins.add(this.defaultCoin = new Coin(defaultCoin));
	}

	/**
	 * Constructor creates a new Coins collection, creating new Coin
	 * objects from the passed Denominationa object,  The passed
	 * Denominations object's defaultCoin will be used to set the
	 * new Coins object's defaultCoin.
	 *
	 * @param currency  a Denominations object from which create
	 *                  Coin objects.
	 */
	public Coins(Denominations currency)
	{

		coins = new ArrayList();
		Iterator i = currency.iterator();
		while (i.hasNext())
		{
			final Denomination d = (Denomination)i.next();
			final Coin c = new Coin(d);
			coins.add(c);
			if (currency.defaultCoin != null && c.isDenomination(currency.defaultCoin))
			{
				this.defaultCoin = c;
			}
		}
	}

	/**
	 * Add a new coin object to the collection.
	 *
	 * @param coin  a Coin object to be added.
	 */
	public void addCoin(Coin coin)
	{
		coins.add(coin);
		Collections.sort(coins);
	}

	/**
	 * Add a new coin object to the collection, based
	 * on the passed denomination.
	 *
	 * @param denom  a Denomination object from which to
	 *               create a coin..
	 */
	public void addCoin(Denomination denom)
	{
		addCoin(new Coin(denom));
	}

	/**
	 * Return the coins ArrayList's iterator to the caller.
	 */
	public Iterator iterator()
	{
		return coins.iterator();
	}

	/**
	 * Get a string representation of the value of all the
	 * coins in the collection.
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name of the coin, for each coin.
	 */
	public String toString()
	{
		return toString(false);
	}

	/**
	 * Get a string representation of the value of all the coins
	 * in the collection, optionally using the abbreviations.
	 *
	 * @param useAbbr    a boolean value which determines whether to
	 *                   use the abbreviations (true) or the names (false)
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name or abbreviation of the coin, for each coin
	 */
	public String toString(boolean useAbbr)
	{
		//String result = "";
		StringBuffer result = new StringBuffer();
		Iterator i = coins.iterator();
		while (i.hasNext())
		{
			final Coin c = (Coin)i.next();
			result.append(c.toString(useAbbr)).append(", ");
		}

		if (result.length() > 2)
			result = new StringBuffer(result.substring(0, result.length() - 2));

		return result.toString();
	}

	/**
	 * Clone a coin from the collection
	 *
	 * @param  name the name of the coin to clone
	 *
	 * return  a coin object which is a copy of the one from
	 *         the collection
	 */
	public Coin cloneCoin(String name)
	{
		Coin c = getCoin(name);

		if (c == null)
			return null;
		else
			return new Coin(c.denom, c.amount);
	}

	/**
	 * Get a reference to a coin in the collection by searching for the name.
	 *
	 * @param name   the name of the coin to get.
	 *
	 * @return   a reference to the coin.  If no coin with the
	 *           passed name is found, then null is returned.
	 */
	public Coin getCoin(String name)
	{
		Iterator i = coins.iterator();
		Coin c;

		while (i.hasNext())
		{
			c = (Coin)i.next();
			if (c.denom.name.equalsIgnoreCase(name) || c.denom.abbr.equalsIgnoreCase(name))
			{
				return c;
			}
		}

		return null;
	}

	/**
	 * Get the total weight of the specified coins
	 *
	 * @param name   the name of the coin type.
	 *
	 * @return       the total weight of the coins of the
	 *               specified type in the collection.
	 *
	 */
	public float getWeight(String name)
	{
		Coin c = getCoin(name);

		if (c != null)
			return c.getAmount() * c.getWeight();
		else
			return 0F;
	}

	/**
	 * Get the total weight of all coins in the collection
	 *
	 * @return    the total weight of all coins in the collection.
	 *
	 */
	public double getWeight()
	{
		Iterator i = coins.iterator();
		Coin c;
		double weight = 0F;

		while (i.hasNext())
		{
			c = (Coin)i.next();
			weight += c.getWeight();
		}

		return weight;
	}

	public Coin nextValuedCoin(Coin c, boolean higher)
	{
		int coinIndex = coins.indexOf(c);

		if (coinIndex == 0 || coinIndex == coins.size() - 1)
		{
			return null;
		}

		coinIndex += (higher ? -1 : 1);
		if (coinIndex >= 0 && coinIndex < coins.size())
		{
			return (Coin)coins.get(coinIndex);
		}
		else
		{
			return null;
		}
	}

}
