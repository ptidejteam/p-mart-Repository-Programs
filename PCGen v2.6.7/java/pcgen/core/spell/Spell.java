/*
 * Spell.java
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

package pcgen.core.spell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PObject;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class Spell extends PObject
{
	private String school = "";
	private String subschool = "";
	private String componentList = "";
	private String castingTime = "";
	private String range = "";
	private String effect = "";
	private String effectType = "";
	private String duration = "";
	private String saveInfo = "";
	private String SR = "";
	private List descriptorList = new ArrayList();
	private String stat = "";
	private int castingThreshold = 0;
	private int minLVL = 0;
	private int maxLVL = 9;
	private String creatableItem = "";
	private BigDecimal cost = new BigDecimal("0");
	private int xpCost = 0;
	private List variantList = null; //Lazy initialization, it's rarely, if ever, used.

	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////

	public Spell()
	{
		super();
	}

	///////////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////////

	public String getStat()
	{
		return stat;
	}

	public void setStat(String stat)
	{
		this.stat = stat;
	}

	public List getDescriptorList()
	{
		return descriptorList;
	}

	public String descriptor()
	{
		return getDescriptor(", ");
	}

	public String getDescriptor(String delimiter)
	{
		StringBuffer retVal = new StringBuffer(descriptorList.size() * 5);
		for (Iterator i = descriptorList.iterator(); i.hasNext();)
		{
			final String aString = (String)i.next();
			if (retVal.length() > 0)
				retVal.append(delimiter);
			retVal.append(aString);
		}
		return retVal.toString();
	}

	public void addDescriptors(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		String token;
		while (aTok.hasMoreTokens())
		{
			token = aTok.nextToken();
			descriptorList.add(token);
			Globals.getDescriptors().add(token);
		}
	}

	public String info()
	{
		return "SCHOOL:" + getSchool() + " SUB:" + getSubschool() +
			" COMP:" + getComponentList() + " CAST:" + getCastingTime() +
			" RANGE:" + getRange() + " EFFECT:" + getEffect() +
			" TYPE:" + getEffectType() + " SAVE:" + getSaveInfo() +
			" SR:" + getSR();
	}

	public String getSchool()
	{
		return school;
	}

	public void setSchool(String aString)
	{
		school = aString;
	}

	public String getSubschool()
	{
		return subschool;
	}

	public void setSubschool(String aString)
	{
		subschool = aString;
		Globals.getSubschools().add(subschool);
	}

	public int getCastingThreshold()
	{
		return castingThreshold;
	}

	public void setCastingThreshold(int castingThreshold)
	{
		this.castingThreshold = castingThreshold;
	}

	public int getMinLVL()
	{
		return minLVL;
	}

	public void setMinLVL(int minLVL)
	{
		this.minLVL = minLVL;
	}

	public int getMaxLVL()
	{
		return maxLVL;
	}

	public void setMaxLVL(int maxLVL)
	{
		this.maxLVL = maxLVL;
	}

	public boolean checkLVLRange(int checkLvl)
	{
		if (minLVL <= checkLvl && checkLvl <= maxLVL)
			return true;
		else
			return false;
	}

	public String getComponentList()
	{
		return componentList;
	}

	public void setComponentList(String aString)
	{
		componentList = aString;
	}

	public String getCastingTime()
	{
		return castingTime;
	}

	public void setCastingTime(String aString)
	{
		castingTime = aString;
		Globals.getCastingTimes().add(castingTime);
	}

	public String getRange()
	{
		return range;
	}

	public void setRange(String aString)
	{
		range = aString;
		Globals.getRanges().add(range);
	}

	public String getEffect()
	{
		return effect;
	}

	public void setEffect(String aString)
	{
		effect = aString;
	}

	public String getEffectType()
	{
		return effectType;
	}

	public void setEffectType(String aString)
	{
		effectType = aString;
		Globals.getEffectTypes().add(effectType);
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String aString)
	{
		duration = aString;
	}

	public String getSaveInfo()
	{
		return saveInfo;
	}

	public void setSaveInfo(String aString)
	{
		saveInfo = aString;
	}

	public String getSR()
	{
		return SR;
	}

	public void setSR(String aString)
	{
		SR = aString;
		Globals.getSRs().add(SR);
	}


	public String getCreatableItem()
	{
		return creatableItem;
	}

	public void setCreatableItem(String creatableItem)
	{
		this.creatableItem = creatableItem;
	}

	public void setCost(String aString)
	{
		try
		{
			cost = new BigDecimal(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	public void setXpCost(String aString)
	{
		try
		{
			xpCost = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			// ignore
		}
	}

	public int getXpCost()
	{
		return xpCost;
	}

	public void setVariants(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		//Initialize lazily
		if (aTok.hasMoreTokens() && variantList == null)
		{
			variantList = new ArrayList();
		}
		while (aTok.hasMoreTokens())
		{
			variantList.add(aTok.nextToken());
		}
	}

	public ArrayList getVariants()
	{
		//Initialize lazily
		if (variantList == null)
		{
			variantList = new ArrayList();
		}
		return (ArrayList)variantList;
	}

	///////////////////////////////////////////////////////////////////////////
	// Public method(s)
	///////////////////////////////////////////////////////////////////////////
	public Object clone()
	{
		Spell aSpell = (Spell)super.clone();
		aSpell.setSchool(school);
		aSpell.setSubschool(subschool);
		aSpell.isSpecified = isSpecified;
		aSpell.setComponentList(componentList);
		aSpell.setCastingTime(castingTime);
		aSpell.setRange(range);
		aSpell.setEffect(effect);
		aSpell.setEffectType(effectType);
		aSpell.setDuration(duration);
		aSpell.setSaveInfo(saveInfo);
		aSpell.setSR(SR);
		aSpell.descriptorList = descriptorList;
		aSpell.stat = stat;
		aSpell.setCastingThreshold(castingThreshold);
		aSpell.setMinLVL(minLVL);
		aSpell.setMaxLVL(maxLVL);
		aSpell.creatableItem = creatableItem;
		aSpell.cost = cost;
		aSpell.xpCost = xpCost;
		aSpell.variantList = variantList;
		return aSpell;
	}

	public String toString()
	{
		return name;
	}

}
