/*
 * PObjectLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: PObjectLoader.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

package pcgen.persistence.lst;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceManager;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class PObjectLoader
{

	/** Creates a new instance of PObjectLoader */
	private PObjectLoader()
	{
	}

	public static boolean parseTag(PObject obj, String aTag)
	{
		return parseTagLevel(obj, aTag, -9);
	}

	// return true if tag is parsed here
	public static boolean parseTagLevel(PObject obj, String aTag, int anInt)
	{
		obj.setNewItem(false);
		if (aTag.startsWith("SOURCE"))
		{
			final String src = PersistenceManager.getCurrentSource();
			if (src.length() > 0)
			{
				obj.setSource(PersistenceManager.saveSource(src), aTag);
			}
			else
			{
				obj.setSource(aTag);
			}
			return true;
		}
		else if (aTag.startsWith("AUTO:"))
		{
			obj.setAutoString(aTag.substring(5));
			return true;
		}
		else if (aTag.startsWith("BONUS:"))
		{
			if (anInt > -9)
			{
				obj.addBonusList(anInt + "|" + aTag.substring(6));
			}
			else
			{
				obj.addBonusList(aTag.substring(6));
			}
			return true;
		}
		else if (aTag.startsWith("CSKILL:"))
		{
			obj.setCSkillList(aTag.substring(7));
			return true;
		}
		else if (aTag.startsWith("CCSKILL:"))
		{
			obj.setCcSkillList(aTag.substring(8));
			return true;
		}
		else if (aTag.startsWith("CHOOSE:") && !aTag.startsWith("CHOOSE:LANGAUTO"))
		{
			obj.setChoiceString(aTag.substring(7));
			return true;
		}
		else if (aTag.startsWith("DEFINE:"))
		{
			if (anInt > -9)
			{
				obj.addVariable(anInt + "|" + aTag.substring(7));
			}
			else
			{
				obj.addVariable("0|" + aTag.substring(7));
			}
			return true;
		}
		else if (aTag.startsWith("DR:"))
		{
			obj.setDR(anInt, aTag.substring(3));
			return true;
		}
		else if (aTag.startsWith("KEY:"))
		{
			obj.setKeyName(aTag.substring(4));
			return true;
		}
		else if (aTag.startsWith("KIT:"))
		{
			if (anInt > -9)
			{
				obj.setKitString(anInt + "|" + aTag.substring(4));
			}
			else
			{
				obj.setKitString("0|" + aTag.substring(4));
			}
			return true;
		}
		else if (aTag.startsWith("LANGAUTO:"))
		{
			obj.setLanguageAutos(aTag.substring(9));
			return true;
		}
		else if (aTag.startsWith("NAME:"))
		{
			obj.setName(aTag.substring(5));
			return true;
		}
		else if (aTag.startsWith("NAMEISPI:"))
		{
			obj.setNameIsPI(aTag.substring(9).startsWith("Y"));
			return true;
		}
		else if (aTag.startsWith("OUTPUTNAME:"))
		{
			obj.setOutputName(aTag.substring(11));
			return true;
		}
		else if (aTag.startsWith("PRE") || aTag.startsWith("!PRE") || aTag.startsWith("RESTRICT:"))
		{
			obj.addPreReq(aTag);
			return true;
		}
		else if (aTag.startsWith("REGION:"))
		{
			if (anInt > -9)
			{
				obj.setRegionString(anInt + "|" + aTag.substring(7));
			}
			else
			{
				obj.setRegionString("0|" + aTag.substring(7));
			}
			return true;
		}
		else if (aTag.startsWith("SA:"))
		{
			obj.setSpecialAbilityList(aTag.substring(3), anInt);
			return true;
		}
		else if (aTag.startsWith("SPELL:") && !(obj instanceof Campaign))
		{
			obj.addSpells(aTag.substring(6));
			return true;
		}
		else if (aTag.startsWith("SR:"))
		{
			obj.setSR(aTag.substring(3));
			return true;
		}
		else if (aTag.startsWith("UDAM:"))
		{
			obj.addUdamList(aTag.substring(5));
			return true;
		}
		else if (aTag.startsWith("UMULT:"))
		{
			if (anInt > -9)
			{
				obj.addUmult(anInt + "|" + aTag.substring(6));
			}
			else
			{
				obj.addUmult(aTag.substring(6));
			}
			return true;
		}
		else if (aTag.startsWith("VISION:"))
		{
			obj.setVision(aTag.substring(7));
			return true;
		}
		else if (aTag.startsWith("WEAPONAUTO:"))
		{
			obj.setWeaponProfAutos(aTag.substring(11));
			return true;
		}
		return false;
	}

}
