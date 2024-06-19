/*
 * WieldCategory.java
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on November 21, 2003, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:33 $
 *
 */

package pcgen.core.character;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;

/**
 * <code>WieldCategory.java</code>
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class WieldCategory
{

	/*
	 * WieldCategory contains the following:
	 * Hands: Minimum hands required to wield this category of weapon
	 * Finessable: Can this weapon be used with weapon finesse feat?
	 * Damage Multiplier: Multiplier to damage based on hands used
	 * PREVAR and SWITCH map: If meet PREVAR, then switch category
	 */

	private String name = "";
	private boolean finessBool = false;
	private int hands = 999;
	private int sizeDiff = 0;
	private Map damageMultMap = new HashMap();
	private Map switchMap = new HashMap();
	private Map wcStepMap = new HashMap();

	/**
	 * New constructor
	 */
	public WieldCategory(String aName)
	{
		name = aName;
	}

	/**
	 * Name routines
	 */
	public void setName(String aString)
	{
		name = aString;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Minumum hands required to wield this category of weapon
	 */
	public void setHands(int x)
	{
		hands = x;
	}

	public int getHands()
	{
		return hands;
	}

	/**
	 * Number of size categories object size is different than Equip size
	 **/
	public void setSizeDiff(int x)
	{
		sizeDiff = x;
	}

	public int getObjectSizeInt(Equipment eq)
	{
		int eqSize = eq.sizeInt();
		return (eqSize + sizeDiff);
	}

	/**
	 * Can weapon be used with weapon finesse Feat?
	 */
	public void setFinessable(boolean aBool)
	{
		finessBool = aBool;
	}

	public boolean isFinessable()
	{
		return finessBool;
	}

	/**
	 * Damage multiplier map based on hands wielded with
	 */
	public void addDamageMultMap(String aKey, String aVal)
	{
		damageMultMap.put(aKey, aVal);
	}

	public int getDamageMult(int hands)
	{
		String aKey = Integer.toString(hands);
		Integer anInt = new Integer(1);
		if (damageMultMap.containsKey(aKey))
		{
			anInt = Integer.getInteger((String) damageMultMap.get(aKey));
		}
		return anInt.intValue();
	}

	/**
	 * Map of PREVAR and wield category to switch to
	 */
	public void addSwitchMap(String aKey, String aVal)
	{
		switchMap.put(aKey, aVal);
	}

	public String getWieldCategory(PlayerCharacter aPC, Equipment eq)
	{
		if ((aPC == null) || (eq == null))
		{
			return name;
		}
		for (Iterator pc = switchMap.keySet().iterator(); pc.hasNext();)
		{
			String aKey = (String) pc.next();
			String aType = aKey.substring(0, aKey.indexOf(":"));
			String preVar = aKey.substring(aKey.indexOf(":") + 1);
			if (PrereqHandler.passesPreVar(preVar, aType, aPC, eq))
			{
				return (String) switchMap.get(aKey);
			}
		}
		return name;
	}

	/**
	 * Map of Steps up or down the wield category chain
	 **/
	public void setWCStep(int aInt, String aVal)
	{
		String aKey = (new Integer(aInt)).toString();
		wcStepMap.put(aKey, aVal);
	}

	/**
	 * Wield Category step is used to figure a bonus to WIELDCATEGORY
	 * Thus it should always return the best possible wield category
	 * and never a "bad" wield category
	 **/
	public String getWieldCategoryStep(int aBump)
	{
		String aKey = new Integer(aBump).toString();
		String newWC = (String) wcStepMap.get(aKey);
		if (newWC != null)
		{
			return newWC;
		}
		return name;
	}

}

