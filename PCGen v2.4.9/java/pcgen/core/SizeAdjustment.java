/*
 * SizeAdjustment.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2001, 4:24 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.*;


/**
 * <code>SizeAdjustment</code>.
 *
 * @author Greg Bingleman <byngl@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class SizeAdjustment extends Object
{
	private String name = new String();
	private ArrayList typeList = new ArrayList();
	private double[] multiple = new double[9];


	public String toString()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
	}

	public String getName()
	{
		return name;
	}

	public void setType(String aString)
	{
		final String typeString = aString.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aType = ((String)aTok.nextToken()).trim();
			if (!typeList.contains(aType))
			{
				typeList.add(aType);
			}
		}
	}

	public ArrayList typeList()
	{
		return typeList;
	}

	public void setMultiple(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.countTokens() != 9)
		{
			JOptionPane.showMessageDialog(null, "Incorrect size count in size adjustment info", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		int i = 0;
		while (aTok.hasMoreTokens())
		{
			final String aMult = (String)aTok.nextToken();
			multiple[i++] = Double.parseDouble(aMult);
		}
	}

	public double getMultiplier(int idx)
	{
		if ((idx < 0) || (idx > 8))
		{
			return 1.0;
		}
		return multiple[idx];
	}


	public boolean isType(ArrayList aTypes)
	{
		for (Iterator e = aTypes.iterator(); e.hasNext();)
		{
			final String type = (String)e.next();
			if (isType(type))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isType(String aType)
	{
		return typeList.contains(aType.toUpperCase());
	}

}
