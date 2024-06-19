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


/**
 * <code>Campaign</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Campaign extends PObject
{
	private boolean isLoaded = false;
	private Integer rank = new Integer(9);
	private String game = "";
	private String nfo = "";

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * Sets whether the campaign is loaded.
	 * @param loaded
	 */
	public void setIsLoaded(boolean loaded)
	{
		isLoaded = loaded;
	}

	/**
	 * @return the 'load rank' of the campaign.
	 */
	public Integer getRank()
	{
		return rank;
	}

	public void setRank(Integer rank)
	{
		this.rank = rank;
	}

	/**
	 * @return the name of the game this campaign is intended for
	 */
	public String getGame()
	{
		return game;
	}

	public void setGame(String game)
	{
		this.game = game;
	}

	/**
	 * @return the info on this campaign
	 */
	public String getNfo()
	{
		return nfo;
	}

	public void setNfo(String info)
	{
		this.nfo = info;
	}

	public String toString()
	{
		return this.name;
	}
}
