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
 *
 * $Id: Spell.java,v 1.1 2006/02/21 00:57:52 vauchers Exp $
 */

package pcgen.core.spell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.util.GuiFacade;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class Spell extends PObject
{
	private TreeSet school = new TreeSet();
	private TreeSet subschool = new TreeSet();
	private TreeSet componentList = new TreeSet();
	private TreeSet castingTime = new TreeSet();
	private TreeSet range = new TreeSet();
	private TreeSet effect = new TreeSet();
	private TreeSet effectType = new TreeSet();
	private TreeSet duration = new TreeSet();
	private TreeSet saveInfo = new TreeSet();
	private TreeSet spellResistance = new TreeSet();
	private List descriptorList = new ArrayList();
	private String stat = "";
	private int castingThreshold = 0;
	private int minLVL = 0;
	private int maxLVL = 9;
	private String creatableItem = "";
	private BigDecimal cost = new BigDecimal("0");
	private int xpCost = 0;
	private List variantList = null; //Lazy initialization, it's rarely, if ever, used.
	private String levelString = "";

	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////

	public Spell()
	{
		super();
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
		final StringBuffer retVal = new StringBuffer(descriptorList.size() * 5);
		Iterator i = descriptorList.iterator();
		while (i.hasNext())
		{
			final String aString = (String) i.next();
			if (retVal.length() > 0)
			{
				retVal.append(delimiter);
			}
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
	public boolean descriptorListContains(ArrayList aList)
	{
		for(Iterator i = aList.iterator(); i.hasNext(); )
		{
			if (descriptorList.contains(i.next()))
				return true;
		}
		return false;
	}

	public String getSchool()
	{
		String s = school.toString();
		return s.substring(1,s.length()-1);
	}

	public void setSchool(String aString)
	{
		school.add(aString);
	}

	public boolean schoolContains(ArrayList aList)
	{
		for(Iterator i = aList.iterator(); i.hasNext(); )
		{
			if (school.contains(i.next()))
				return true;
		}
		return false;
	}

	public String getSubschool()
	{
		String s = subschool.toString();
		return s.substring(1,s.length()-1);
	}

	public void setSubschool(String aString)
	{
		subschool.add(aString);
		Globals.getSubschools().add(aString);
	}

	public boolean subschoolContains(ArrayList aList)
	{
		for(Iterator i = aList.iterator(); i.hasNext(); )
		{
			if (subschool.contains(i.next()))
				return true;
		}
		return false;
	}

	public int getCastingThreshold()
	{
		return castingThreshold;
	}

	public void setCastingThreshold(int arg)
	{
		castingThreshold =arg;
	}

	public void setMinLVL(int minLVL)
	{
		this.minLVL = minLVL;
	}

	public void setMaxLVL(int maxLVL)
	{
		this.maxLVL = maxLVL;
	}

	public String getComponentList()
	{
		String s = componentList.toString();
		return s.substring(1,s.length()-1);
	}

	public void setComponentList(String aString)
	{
		componentList.add(aString);
	}

	public String getCastingTime()
	{
		String s = castingTime.toString();
		return s.substring(1,s.length()-1);
	}

	public void setCastingTime(String aString)
	{
		castingTime.add(aString);
		Globals.getCastingTimes().add(aString);
	}

	public String getRange()
	{
		String s = range.toString();
		return s.substring(1,s.length()-1);
	}

	public void setRange(String aString)
	{
		range.add(aString);
		Globals.getRanges().add(aString);
	}

	public String getEffect()
	{
		String s = effect.toString();
		return s.substring(1,s.length()-1);
	}

	public void setEffect(String aString)
	{
		effect.add(aString);
	}

	public String getEffectType()
	{
		String s = effectType.toString();
		return s.substring(1,s.length()-1);
	}

	public void setEffectType(String aString)
	{
		effectType.add(aString);
		Globals.getEffectTypes().add(aString);
	}

	public String getDuration()
	{
		String s = duration.toString();
		return s.substring(1,s.length()-1);
	}

	public void setDuration(String aString)
	{
		duration.add(aString);
	}

	public String getSaveInfo()
	{
		String s = saveInfo.toString();
		return s.substring(1,s.length()-1);
	}

	public void setSaveInfo(String aString)
	{
		saveInfo.add(aString);
	}

	public String getSpellResistance()
	{
		String s = spellResistance.toString();
		return s.substring(1,s.length()-1);
	}

	public void setSpellResistance(String aString)
	{
		spellResistance.add(aString);
		Globals.getSRs().add(aString);
	}

	public String getLevelString()
	{
		return levelString;
	}

	/*
	 * appends aString to the existing levelString
	 * if aString=".CLEAR" then clear the levelString
	 * else levelString should be in form of source|name|level
	 * where source is CLASS or DOMAIN
	 * name is the name of the CLASS or DOMAIN
	 * and level is an integer representing the level of the spell for the named CLASS or DOMAIN
	 */
	public void setLevelString(final String aString)
	{
		if (".CLEAR".equals(aString))
		{
			levelString = "";
		}
		else
		{
			if (levelString.length() > 0)
			{
				levelString += '|';
			}
			levelString += aString;
		}
	}

	/*
	 * typeString should be CLASS or DOMAIN
	 * listString should be name,name,name=level|name,name=level|etc.
	 * where name is the name of a class or domain
	 * and level is an integer for this spell's level for the named class/domain
	 */
	public void setLevelList(final String typeString, final String listString)
	{
		if (listString.equals(".CLEAR"))
		{
			levelString = "";
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(listString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String aList = aTok.nextToken(); // could be name=x or name,name=x
			final StringTokenizer bTok = new StringTokenizer(aList, "=", false);
			while (bTok.hasMoreTokens())
			{
				final String nameList = bTok.nextToken();
				if (!bTok.hasMoreTokens())
				{
					Globals.errorPrint("Badly formed data: " + listString);
					return;
				}
				final String aLevel = bTok.nextToken();
				final StringTokenizer cTok = new StringTokenizer(nameList, ",", false);
				while (cTok.hasMoreTokens())
				{
					setLevelString(typeString + "|" + cTok.nextToken() + "|" + aLevel);
				}
			}
		}

	}

	public int levelForKey(final String mType, final String sType)
	{
		if (levelString.length() == 0)
		{
			return -1;
		}
		final StringTokenizer aTok = new StringTokenizer(levelString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String main = aTok.nextToken(); // should be CLASS or DOMAIN
			if (!aTok.hasMoreTokens())
			{
				break;
			}
			final String sub = aTok.nextToken(); // should be name of main object
			if (!aTok.hasMoreTokens())
			{
				break;
			}
			final int lev = Integer.parseInt(aTok.nextToken());
			if (main.equals(mType) && sub.equals(sType))
			{
				return lev;
			}
		}
		return -1;
	}

	public int[] levelForKey(final String key)
	{
		if (levelString.length() == 0 || key.indexOf("|") == -1)
		{
			return new int[0];
		}
		// should consist of CLASS|name and DOMAIN|name pairs
		final StringTokenizer aTok = new StringTokenizer(key, "|", false);
		final int[] levelInt = new int[aTok.countTokens() / 2];
		int i = 0;
		while (aTok.hasMoreTokens())
		{
			final String objectType = aTok.nextToken();
			if (aTok.hasMoreTokens())
			{
				final String objectName = aTok.nextToken();
				levelInt[i++] = levelForKey(objectType, objectName);
			}
		}
		return levelInt;
	}

	public boolean levelForKeyContains(final String key, final int levelMatch)
	{
		final int[] levelInt = levelForKey(key);
		for (int i = 0; i < levelInt.length; ++i)
		{
			// always match if levelMatch==-1
			if (levelMatch == -1 || levelInt[i] == levelMatch)
			{
				return true;
			}
		}
		return false;
	}

	public int getFirstLevelForKey(final String key)
	{
		final int[] levelInt = levelForKey(key);
		if (levelInt.length > 0)
		{
			return levelInt[0];
		}
		return -1;
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
		catch (StringIndexOutOfBoundsException e)
		{
			//thrown when aString is ""
		}
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	public void setXPCost(String aString)
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

	public int getXPCost()
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
		return (ArrayList) variantList;
	}

	///////////////////////////////////////////////////////////////////////////
	// Public method(s)
	///////////////////////////////////////////////////////////////////////////
	public Object clone()
	{
		Spell aSpell = null;
		try
		{
			aSpell = (Spell) super.clone();
			aSpell.school = school;
			aSpell.subschool = subschool;
			aSpell.isSpecified = isSpecified;
			aSpell.componentList = componentList;
			aSpell.castingTime = castingTime;
			aSpell.range = range;
			aSpell.effect = effect;
			aSpell.effectType = effectType;
			aSpell.duration = duration;
			aSpell.saveInfo = saveInfo;
			aSpell.setSR(SR);
			aSpell.spellResistance = spellResistance;
			aSpell.descriptorList = descriptorList;
			aSpell.stat = stat;
			aSpell.setCastingThreshold(castingThreshold);
			aSpell.setMinLVL(minLVL);
			aSpell.setMaxLVL(maxLVL);
			aSpell.creatableItem = creatableItem;
			aSpell.cost = cost;
			aSpell.xpCost = xpCost;
			aSpell.variantList = variantList;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return aSpell;
		}
	}

	public String toString()
	{
		return name;
	}

}
