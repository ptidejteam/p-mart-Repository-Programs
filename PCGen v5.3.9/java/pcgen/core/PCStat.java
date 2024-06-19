/*
 * PCStat.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on August 10, 2002, 11:58 PM
 */
package pcgen.core;

import java.util.StringTokenizer;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.Logging;

/**
 * <code>PCStat</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class PCStat extends PObject
{
	private String abbreviation = ""; // should be 3 characters all caps
	private String statMod = "0"; // a formula defining this stat's modifier
	private int score = 0;
	private int minValue = 0;
	private int maxValue = 1000;

	public Object clone()
	{
		PCStat newObj = null;
		try
		{
			newObj = (PCStat) super.clone();
			newObj.setAbb(abbreviation);
			newObj.setStatMod(statMod);
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return newObj;
	}

	public String getAbb()
	{
		return abbreviation;
	}

	public void setAbb(String aString)
	{
		abbreviation = aString.toUpperCase();
		if (abbreviation.length() != 3)
		{
			Logging.errorPrint("Stat with ABB:" + abbreviation + " should be 3 characters long!");
		}
	}

	public int getBaseScore()
	{
		return score;
	}

	public void setBaseScore(int x)
	{
		score = x;
	}

	String getStatMod()
	{
		return statMod;
	}

	public void setStatMod(final String aString)
	{
		statMod = aString;
	}

	public int getMinValue()
	{
		return minValue;
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public void setStatRange(final String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.countTokens() == 2)
		{
			try
			{
				minValue = Integer.parseInt(aTok.nextToken());
				maxValue = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException ignore)
			{
				//TODO: Should this really be ignored?
			}
		}
		else
		{
			Logging.errorPrint("Error in specified Stat range: " + aString);
		}
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(30);
		sb.append("stat:").append(abbreviation).append(' ');
		sb.append("formula:").append(statMod).append(' ');
		sb.append("score:").append(score);
		return sb.toString();
	}

}
