/*
 * UnitSet.java
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
 * Created on February 25, 2002, 10:15 PM
 *
 * $Id: UnitSet.java,v 1.1 2006/02/21 01:33:15 vauchers Exp $
 */

package pcgen.core;

/**
 * <code>UnitSet</code>.
 *
 * @author Stefan Radermacher <stefan@zaister.de>
 * @version $Revision: 1.1 $
 */

public final class UnitSet
{
	private String name;
	private String heightUnit;
	private double heightFactor;
	private String heightDisplayPattern;
	private String distanceUnit;
	private double distanceFactor;
	private String distanceDisplayPattern;
	private String weightUnit;
	private double weightFactor;
	private String weightDisplayPattern;

	public String getName()
	{
		return name;
	}

	public String getHeightUnit()
	{
		return heightUnit;
	}

	public double getHeightFactor()
	{
		return heightFactor;
	}

	public String getHeightDisplayPattern()
	{
		return heightDisplayPattern;
	}

	public String getDistanceUnit()
	{
		return distanceUnit;
	}

	public double getDistanceFactor()
	{
		return distanceFactor;
	}

	public String getDistanceDisplayPattern()
	{
		return distanceDisplayPattern;
	}

	public String getWeightUnit()
	{
		return weightUnit;
	}

	public double getWeightFactor()
	{
		return weightFactor;
	}

	public String getWeightDisplayPattern()
	{
		return weightDisplayPattern;
	}

	public void setName(String n)
	{
		name = n;
	}

	public void setHeightUnit(String hu)
	{
		heightUnit = hu;
	}

	public void setHeightFactor(double hf)
	{
		heightFactor = hf;
	}

	public void setHeightDisplayPattern(String hd)
	{
		heightDisplayPattern = hd;
	}

	public void setDistanceUnit(String du)
	{
		distanceUnit = du;
	}

	public void setDistanceFactor(double df)
	{
		distanceFactor = df;
	}

	public void setDistanceDisplayPattern(String dd)
	{
		distanceDisplayPattern = dd;
	}

	public void setWeightUnit(String wu)
	{
		weightUnit = wu;
	}

	public void setWeightFactor(double wf)
	{
		weightFactor = wf;
	}

	public void setWeightDisplayPattern(String wd)
	{
		weightDisplayPattern = wd;
	}

}
