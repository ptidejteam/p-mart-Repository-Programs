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
 * $Id: Spell.java,v 1.1 2006/02/21 01:16:28 vauchers Exp $
 */

package pcgen.core.spell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class Spell extends PObject
{
	private SortedSet school = new TreeSet();
	private SortedSet subschool = new TreeSet();
	private SortedSet componentList = new TreeSet();
	private SortedSet castingTime = new TreeSet();
	private SortedSet range = new TreeSet();
	private String target = "";
	private String spellStat = "";
	private SortedSet duration = new TreeSet();
	private SortedSet saveInfo = new TreeSet();
	private SortedSet spellResistance = new TreeSet();
	private List descriptorList = new ArrayList();
	private int castingThreshold = 0;
	//private int minLVL = 0;
	//private int maxLVL = 9;
	private String creatableItem = "";
	private BigDecimal cost = BigDecimalHelper.ZERO;
	private int xpCost = 0;
	private List variantList = null; //Lazy initialization, it's rarely, if ever, used.
	private HashMap levelInfo = null;
	private Map preReqMap = null;

	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////

	public Spell()
	{
		super();
	}

	public List getDescriptorList()
	{
		return descriptorList;
	}

	public String descriptor()
	{
		return getDescriptor(", ");
	}

	private String getDescriptor(String delimiter)
	{
		final StringBuffer retVal = new StringBuffer(descriptorList.size() * 5);
		final Iterator i = descriptorList.iterator();
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
			Globals.addSpellDescriptorSet(token);
		}
	}

	public boolean descriptorListContains(List aList)
	{
		return Utility.containsAny(descriptorList, aList);
	}

	public String getSchool()
	{
		final String s = school.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setSchool(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			school.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				school.add(aString);
			}
		}
	}

	public boolean schoolContains(List aList)
	{
		return Utility.containsAny(school, aList);
	}

	public String getSubschool()
	{
		final String s = subschool.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setSubschool(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			subschool.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				subschool.add(aString);
				if (aString.length() != 0)
				{
					Globals.getSubschools().add(aString);
				}
			}
		}
	}

	public boolean subschoolContains(List aList)
	{
		return Utility.containsAny(subschool, aList);
	}

	public int getCastingThreshold()
	{
		return castingThreshold;
	}

	public void setCastingThreshold(int arg)
	{
		castingThreshold = arg;
	}

	public String getComponentList()
	{
		final String s = componentList.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setComponentList(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			castingTime.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				componentList.add(aString);
				Globals.addSpellComponentSet(aString);
			}
		}
	}

	public String getCastingTime()
	{
		final String s = castingTime.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setCastingTime(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			castingTime.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				castingTime.add(aString);
				Globals.addSpellCastingTimesSet(aString);
			}
		}
	}

	public String getRange()
	{
		final String s = range.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setRange(String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			range.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				range.add(aString);
				Globals.addSpellRangesSet(aString);
			}
		}
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(final String aString)
	{
		target = aString;
		if (aString.length() != 0)
		{
			Globals.addSpellTargetSet(aString);
		}
//		hmmm.... not sure what to do with effectTypes (merton_monk@yahoo.com, 12/10/02)
//		Globals.getEffectTypes().add(aString);
	}

	public String getDuration()
	{
		final String s = duration.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setDuration(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			duration.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				duration.add(aString);
				Globals.addDurationSet(aString);
			}
		}
	}

	public String getSaveInfo()
	{
		final String s = saveInfo.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setSaveInfo(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			saveInfo.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				saveInfo.add(aString);
				Globals.addSpellSaveInfoSet(aString);
			}
		}
	}

	public String getSpellResistance()
	{
		final String s = spellResistance.toString();
		return s.substring(1, s.length() - 1);
	}

	public void setSpellResistance(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			spellResistance.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				spellResistance.add(aString);
				Globals.addSpellSrSet(aString);
			}
		}
	}

	/**
	 * This method gets the information about the levels at which classes
	 * and domains may cast the spell.
	 * 
	 * Modified 8 Sept 2003 by Sage_Sam for bug #801469 
	 * 
	 * @return Map containing the class levels and domains that 
	 * 	may cast the spell
	 */
	public Map getLevelInfo()
	{
		Map wLevelInfo = null;
		
		if( levelInfo != null )
		{
			wLevelInfo = (HashMap) levelInfo.clone();
		}
		
		final PlayerCharacter pc = Globals.getCurrentPC();
		if( pc!=null )
		{
			if( wLevelInfo == null )
			{
				wLevelInfo = new HashMap();
			}
			
			wLevelInfo.putAll(pc.getSpellInfoMap("CLASS|" + getName()));
			wLevelInfo.putAll(pc.getSpellInfoMap("DOMAIN|" + getName()));
		}
		
		return wLevelInfo;
	}

	/**
	 * appends aString to the existing levelString
	 * if key=".CLEAR" then clear the levelString
	 * else levelString should be in form of source|name|level
	 * where source is CLASS or DOMAIN
	 * name is the name of the CLASS or DOMAIN
	 * and level is an integer representing the level of the spell for the named CLASS or DOMAIN
	 */
	public void setLevelInfo(final String key, final String aLevel)
	{
		try
		{
			setLevelInfo(key, Integer.parseInt(aLevel));
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Could not set level info.", exc);
		}
	}

	public void setLevelInfo(final String key, final int level)
	{
		if (".CLEAR".equals(key))
		{
			levelInfo = null;
		}
		else
		{
			if (level == -1)
			{
				if (levelInfo != null)
				{
					levelInfo.remove(key);
				}
			}
			else
			{
				if (levelInfo == null)
				{
					levelInfo = new HashMap();
				}
				levelInfo.put(key, new Integer(level));
			}
		}
	}

	/**
	 * typeString should be CLASS or DOMAIN
	 * listString should be name,name,name=level|name,name=level|etc
	 * where name is the name of a class or domain
	 * and level is an integer for this spell's level for the named class/domain
	 */
	public void setLevelList(final String typeString, String listString)
	{
		if (listString.equals(".CLEAR"))
		{
			levelInfo = null;
			return;
		}
		String preReqTag = null;
		final int i = listString.lastIndexOf('[');
		int j = listString.lastIndexOf(']');
		if (j < i)
		{
			j = listString.length();
		}
		if (i >= 0)
		{
			preReqTag = listString.substring(i + 1, j);
			listString = listString.substring(0, i);
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
					Logging.errorPrint("Badly formed data: " + listString);
					return;
				}
				final String aLevel = bTok.nextToken();
				final StringTokenizer cTok = new StringTokenizer(nameList, ",", false);
				while (cTok.hasMoreTokens())
				{
					final String aClass = cTok.nextToken();
					if (preReqTag != null)
					{
						if (preReqMap == null)
						{
							preReqMap = new HashMap();
						}
						preReqMap.put(typeString + "|" + aClass, preReqTag);
					}
					setLevelInfo(typeString + "|" + aClass, aLevel);
				}
			}
		}

	}

	public int levelForKey(final String mType, final String sType)
	{
		int result = -1;
		Map wLevelInfo = getLevelInfo();
		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			Integer lvl = (Integer) wLevelInfo.get(mType + "|" + sType);
			if (lvl == null)
			{
				lvl = (Integer) wLevelInfo.get(mType + "|ALL");
			}
			if (lvl == null && mType.equals("CLASS"))
			{
				PCClass aClass = Globals.getClassKeyed(sType);
				if (aClass != null)
				{
					StringTokenizer aTok = new StringTokenizer(aClass.getType(), ".", false);
					while (aTok.hasMoreTokens() && lvl == null)
					{	
						lvl = (Integer) wLevelInfo.get(mType + "|TYPE."+aTok.nextToken());
					}
				}
			}
			if (lvl != null)
			{
				result = lvl.intValue();
			}
		}
		return result;
	}

	public int[] levelForKey(final String key)
	{
		if ((levelInfo == null) || (levelInfo.size() == 0))
		{
			final int[] temp = new int[1];
			temp[0] = -1;
			return temp;
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
		if (preReqMap != null && preReqMap.containsKey(key))
		{
			List qList = new ArrayList();
			qList.add(preReqMap.get(key));
			if (!passesPreReqToGainForList(qList))
			{
				return false;
			}
		}
		final int[] levelInt = levelForKey(key);
		for (int i = 0; i < levelInt.length; ++i)
		{
			// always match if levelMatch==-1
			if ((levelMatch == -1 && levelInt[i] >= 0) || (levelMatch >= 0 && levelInt[i] == levelMatch))
			{
				return true;
			}
		}

		//If it's not regularly on the list, check if some SPELLLEVEL tag added it.
		return (Globals.getCurrentPC().getSpellLevelforKey(key + "|" + getName(), levelMatch));
	}

	public int getFirstLevelForKey(final String key)
	{
		final int[] levelInt = levelForKey(key);
		int result = -1;
		if (levelInt.length > 0)
		{
			result = levelInt[0];
		}
		return result;
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
		catch (NumberFormatException ignore)
		{
			// ignore
		}
		catch (StringIndexOutOfBoundsException ignore)
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
		catch (NumberFormatException ignore)
		{
			//ignore
		}
	}

	public int getXPCost()
	{
		return xpCost;
	}

	public void setStat(String aStat)
	{
		spellStat = aStat;
	}

	public String getStat()
	{
		return spellStat;
	}

	/**
	 * returns DC for a spell for aPC and SpellInfo
	 **/
	public int getDCForPlayerCharacter(PlayerCharacter aPC, SpellInfo si)
	{
		return getDCForPlayerCharacter(aPC, si, null, 0);
	}

	/**
	 * returns DC for a spell for aPC and either SpellInfo or PCClass
	 * SPELLLEVEL variable is set to inLevel
	 * @param aPC
	 * @param si
	 * @param aClass
	 * @param inLevel
	 * @return
	 */

	public int getDCForPlayerCharacter(PlayerCharacter aPC, SpellInfo si, PCClass aClass, int inLevel)
	{
		CharacterSpell cs;
		PObject ow = null;
		int spellLevel = inLevel;
		String bonDomain = "";
		String bonClass = "";
		String cdName = "";
		String spellType = "";
		String aClassName = "";
		int metaDC = 0;
		int spellIndex = 0;

		if (si != null)
		{
			cs = si.getOwner();
			if (cs != null)
			{
				spellLevel = si.getActualLevel();
				ow = cs.getOwner();
			}

			if (si.getFeatList() != null)
			{
				for (Iterator i = si.getFeatList().iterator(); i.hasNext();)
				{
					Feat aFeat = (Feat) i.next();
					spellLevel -= aFeat.getAddSpellLevel();
					metaDC += aFeat.bonusTo("DC", "FEATBONUS");
				}
			}
		}
		else
		{
			ow = aClass;
		}

		if (ow instanceof Domain)
		{
			bonDomain = "DOMAIN." + ow.getName();
			CharacterDomain aCD = aPC.getCharacterDomainForDomain(ow.getName());
			if ((aCD != null) && aCD.isFromPCClass())
			{
				String a = aCD.getObjectName();
				aClass = aPC.getClassNamed(a);
			}
		}
		if ((aClass != null) || (ow instanceof PCClass))
		{
			if ((aClass == null) || (ow instanceof PCClass))
			{
				aClass = (PCClass) ow;
			}
			bonClass = "CLASS." + aClass.getName();
			aClassName = "CLASS:" + aClass.getName();
			spellType = aClass.getSpellType();
			spellIndex = aClass.baseSpellIndex();
		}

		// set the spell Level used in aPC.getVariableValue()
		aPC.setSpellLevelTemp(spellLevel);

		// must be done after spellLevel is set above
		int dc = aPC.getVariableValue(Globals.getGameModeBaseSpellDC(), aClassName).intValue() + metaDC;
		dc += (int) aPC.getTotalBonusTo("DC", "ALLSPELLS");

		if (spellIndex == -2)
		{
			// get the BASESPELLSTAT from the spell itself
			String statName = getStat();
			if (statName.length() > 0)
			{
				dc += (int) aPC.getStatList().getStatModFor(statName);
			}
		}
		if (getName().length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", getName());
		}
		// DOMAIN.name
		if (bonDomain.length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", bonDomain);
		}
		// CLASS.name
		if (bonClass.length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", bonClass);
		}

		dc += (int) aPC.getTotalBonusTo("DC", "TYPE." + spellType);

		Iterator i = getMyTypeIterator();
		if (spellType.equals("ALL"))
		{
			while (i.hasNext())
			{
				String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "TYPE." + aType);
			}
		}

		i = school.iterator();
		if (i != null)
		{
			while (i.hasNext())
			{
				String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "SCHOOL." + aType);
			}
		}
		i = subschool.iterator();
		if (i != null)
		{
			while (i.hasNext())
			{
				String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "SUBSCHOOL." + aType);
			}
		}
		i = descriptorList.iterator();
		if (i != null)
		{
			while (i.hasNext())
			{
				String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "DESCRIPTOR." + aType);
			}
		}
		aPC.setSpellLevelTemp(0);	// reset

		return dc;
	}

	public void setVariants(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String variant = aTok.nextToken();
			if (variant.equals(".CLEAR"))
			{
				variantList = null;
			}
			else
			{
				if (variantList == null)
				{
					variantList = new ArrayList();
				}
				if (variant.length() != 0)
				{
					variantList.add(variant);
				}
			}
		}
	}

	public List getVariants()
	{
		//Initialize lazily
		if (variantList == null)
		{
			variantList = new ArrayList();
		}
		return (ArrayList) variantList;
	}

	////////////////////////////////////////////////////////////
	// Public method(s)
	////////////////////////////////////////////////////////////
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
			aSpell.target = target;
			aSpell.duration = duration;
			aSpell.saveInfo = saveInfo;
			aSpell.SR = SR;
			aSpell.spellResistance = spellResistance;
			aSpell.descriptorList = descriptorList;
			aSpell.setCastingThreshold(castingThreshold);
			//aSpell.setMinLVL(minLVL);
			//aSpell.setMaxLVL(maxLVL);
			aSpell.creatableItem = creatableItem;
			aSpell.cost = cost;
			aSpell.xpCost = xpCost;
			aSpell.variantList = variantList;
			if (levelInfo != null)
			{
				aSpell.levelInfo = (HashMap) levelInfo.clone();
			}
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return aSpell;
	}

	private void appendPCCText(final StringBuffer sb, Set ts, final String tag)
	{
		for (Iterator e = ts.iterator(); e.hasNext();)
		{
			sb.append('\t').append(tag).append(':').append(e.next().toString());
		}
	}

	public String getPCCText()
	{
		String aString;

		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());

		appendPCCText(txt, castingTime, "CASTTIME");

		appendPCCText(txt, componentList, "COMPS");

		txt.append("\tCOST:").append(getCost().toString());

		//CLASSES:
		//DOMAINS:
		if (getLevelInfo() != null)
		{
			final List classList = new ArrayList();
			final List domainList = new ArrayList();
			final List miscList = new ArrayList();

			for (Iterator e = getLevelInfo().entrySet().iterator(); e.hasNext();)
			{
				final Map.Entry entry = (Map.Entry) e.next();
				aString = entry.getKey().toString();
				if (aString.startsWith("CLASS|"))
				{
					classList.add(aString.substring(6) + '=' + entry.getValue().toString());
				}
				else if (aString.startsWith("DOMAIN|"))
				{
					domainList.add(aString.substring(7) + '=' + entry.getValue().toString());
				}
				else
				{
					miscList.add(aString + '|' + entry.getValue().toString());
				}
			}

			if (classList.size() != 0)
			{
				txt.append("\tCLASSES:").append(Utility.join(classList, "|"));
			}
			if (domainList.size() != 0)
			{
				txt.append("\tDOMAINS:").append(Utility.join(domainList, "|"));
			}
			if (miscList.size() != 0)
			{
				txt.append("\tSPELLLEVEL:").append(Utility.join(miscList, "|"));
			}
		}

		if (getCastingThreshold() != 0)
		{
			txt.append("\tCT:").append(getCastingThreshold());
		}

		aString = getDescriptor("|");
		if (aString.length() != 0)
		{
			txt.append("\tDESCRIPTOR:").append(aString);
		}

		appendPCCText(txt, duration, "DURATION");

		aString = getCreatableItem();
		if (aString.length() != 0)
		{
			txt.append("\tITEM:").append(aString);
		}

		appendPCCText(txt, range, "RANGE");
		appendPCCText(txt, saveInfo, "SAVEINFO");
		appendPCCText(txt, school, "SCHOOL");
		appendPCCText(txt, spellResistance, "SPELLRES");
		appendPCCText(txt, subschool, "SUBSCHOOL");

		aString = getTarget();
		if (aString.length() != 0)
		{
			txt.append("\tTARGETAREA:").append(aString);
		}

		if ((variantList != null) && (variantList.size() != 0))
		{
			txt.append("\tVARIANTS:").append(Utility.join(variantList, "|"));
		}

		if (getXPCost() != 0)
		{
			txt.append("\tXPCOST:").append(getXPCost());
		}

		txt.append(super.getPCCText(false));
		return txt.toString();
	}

}
