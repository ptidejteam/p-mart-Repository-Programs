/*
 * Coin.java
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
 * Coin class encapsulates a particular denomination of coin,
 * adding an amount to the other fields.
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */
package pcgen.core;

public class Coin
{

	protected Denomination denom;
	protected long amount;

	/**
	 * Constructor creates a new coin corresponding to a denomination,
	 * and sets the amount to the passed amount
	 *
	 * @param denom  the name of the denomination
	 * @param amount the amount of coins
	 */
	public Coin(Denomination denom, long amount)
	{
		this.denom = denom;
		this.amount = amount;
	}

	/**
	 * Constructor creates a new coin corresponding to a denomination,
	 * and sets the amount to the passed amount
	 *
	 * @param aDenom   the abbreviation for the denomination
	 */
	public Coin(Denomination aDenom)
	{
		this(aDenom, 0L);
	}

	/**
	 * Compares this object to the passed denomination for
	 * equality of the name.
	 *
	 * @param d   An object of type denomination.
	 *
	 * @return    True if the object's names are equal.
	 */
	public boolean isDenomination(Denomination d)
	{
		return (denom.name.equals(d.name));
	}

	/**
	 * Compares this object to the passed coin for equality
	 * of the name.
	 *
	 * @param c   An object of type Coin.
	 *
	 * @return    True if the object's denomination names are
	 *            equal.
	 */
	public boolean isDenomination(Coin c)
	{
		return (denom.name.equals(c.denom.name));
	}


	/**
	 * Sets the value of the coin to the passed amount.
	 */
	public void setAmount(long amount)
	{
		this.amount = amount;
	}

	/**
	 * Retrieves the current value of the coin.
	 */
	public long getAmount()
	{
		return amount;
	}

	/**
	 * Retrieves the current value of the coin in terms of
	 * the base coin.
	 */
	public long getFactoredAmount()
	{
		return amount * denom.factor;
	}

	/**
	 * Adds the passed amount to the current value of the coin.
	 *
	 * @param amount   the amount to be added
	 *
	 * @return   the new value of the coin
	 */
	public long add(long amount)
	{
		return (this.amount += amount);
	}

	/**
	 * Get a string representation of the coin value.
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name of the coin
	 */
	public String toString()
	{
		return toString(false);
	}

	/**
	 * Get a string representation of the coin value, optionally using
	 * the abbreviation.
	 *
	 * @param useAbbr    a boolean value which determines whether to
	 *                   use the abbreviation (true) or the name (false)
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name or abbreviation of the coin
	 */
	public String toString(final boolean useAbbr)
	{
		return amount + " " + (useAbbr ? denom.abbr : denom.name);
	}

	public String getName()
	{
		return denom.name;
	}

	public String getAbbr()
	{
		return denom.name;
	}

	public int getFactor()
	{
		return denom.factor;
	}

	public float getWeight()
	{
		return denom.weight * amount;
	}

	public double getCoinWeight()
	{
		return denom.weight;
	}

	public boolean equals(Coin c)
	{
		return isDenomination(c);
	}
}
