/*
 * PointBuyMethod.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on August 17, 2002, 11:45 PM
 *
 * $Id: PointBuyMethod.java,v 1.1 2006/02/21 01:10:50 vauchers Exp $
 */
package pcgen.core;

/**
 * <code>PointBuyMethod</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class PointBuyMethod
{
	private String methodName = "";
	private int points = 0;

	public PointBuyMethod(String argMethodName, int argPoints)
	{
		methodName = argMethodName;
		points = argPoints;
	}

	public final String toString()
	{
		return methodName;
	}

	public final String getMethodName()
	{
		return methodName;
	}

	public final int getPoints()
	{
		return points;
	}
}
