/*
 * DenominationList.java
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
 * DenominationList.java
 *
 * Implements a list of coin denominations by region.
 * The real purpose of this class is to provide a method
 * to parse a file of coins.  All else pretty much stays
 * the same.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */

package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class DenominationList extends ArrayList implements iParsingClient
{

	String currentFile;

	public Denominations getGlobalDenominations()
	{
		return getRegionalDenominations("Global");
	}

	public Denominations getRegionalDenominations(String regionName)
	{
		Denominations d = null;
		String region;

		regionName = "Global";

		for (int i = 0; i < size(); i++)
		{
			d = (Denominations)get(i);
			region = d.region;
			if (region.equalsIgnoreCase(regionName))
			{
				return d;
			}
		}

		return null;
	}

	public void parsedTokens(ArrayList items)
	{
		Iterator i = items.iterator();

		String errorText = "";

		String region = "Global";
		String name = "";
		String abbr = "";
		int factor = 0;
		float weight = 0f;
		BigDecimal bdWeight;
		boolean isDefault = false;
		boolean hasError = false;

		String item;
		String ucItem;

		while (i.hasNext())
		{
			item = (String)i.next();
			ucItem = item.toUpperCase();

			if (ucItem.startsWith("COIN"))
			{
				item = item.substring(4);
				item = item.replace('(', ' ');
				item = item.replace(')', ' ');
				name = item.trim();
			}
			else if (ucItem.startsWith("COST:"))
			{
				try
				{
					factor = Integer.parseInt(item.substring(5));
				}
				catch (NumberFormatException e)
				{
					hasError = true;
					errorText += ", Invalid Coin Cost";
					factor = 1;
				}
			}
			else if (ucItem.startsWith("ABBR:"))
			{
				abbr = item.substring(5);
			}

//      else if ( ucItem.startsWith("REGION:") )
//      {
//        region = item.substring(7);
//      }

			else if (ucItem.startsWith("WT:"))
			{
				bdWeight = new BigDecimal(item.substring(3));
				weight = bdWeight.floatValue();
//        weight = Float.parseFloat(item.substring(3));
			}
			else if (ucItem.equals("DEFAULT"))
			{
				isDefault = true;
			}

		}

		if (!((name.equals("") && abbr.equals("")) || factor == 0))
		{
			if (name.equals(""))
				name = new String(abbr);
			else if (abbr.equals(""))
				abbr = new String(name);

			Denominations d = getRegionalDenominations(region);
			if (d == null)
			{
				d = new Denominations();
				d.region = region;
				this.add(d);
			}

			d.addDenomination(name, abbr, factor, weight, isDefault);
		}

		if (hasError)
		{
			System.out.println("Coin error - File " + currentFile + ", Coin " + name + errorText);
		}
	}

	public DenominationList()
	{
		super();
	}

	public DenominationList(String fileList)
	{
		super();
		parseFiles(fileList);
	}

	public DenominationList(DenominationList d)
	{
		super(d);
	}

	public void parseFiles(String fileList)
	{
		StringTokenizer fileNames = new StringTokenizer(fileList, "|", false);

		ensureCapacity(fileNames.countTokens());

		while (fileNames.hasMoreTokens())
		{
			parseFile(fileNames.nextToken());
		}
	}


	private void parseFile(String fileName)
	{
		currentFile = fileName;
		String[] delimiters = {"\r\n", "\t"};
		FileContentParser fcp = new FileContentParser(fileName, delimiters, this);

		fcp.parse();
		currentFile = "";
	}

}
