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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:13:22 $
 *
 */

package pcgen.persistence.lst;

import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PObject;

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
			obj.setSource(aTag);
		}
		else if (aTag.startsWith("ADD:"))
		{
			obj.addAddList(anInt, aTag.substring(4));
		}
		else if (aTag.startsWith("AUTO:"))
		{
			obj.addAutoArray(aTag.substring(5));
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
		}
		else if (aTag.startsWith("CAMPAIGN:") && !(obj instanceof Campaign))
		{
			// blank intentionally
		}
		else if (aTag.startsWith("CSKILL:"))
		{
			obj.setCSkillList(aTag.substring(7));
		}
		else if (aTag.startsWith("CCSKILL:"))
		{
			obj.setCcSkillList(aTag.substring(8));
		}
		else if (aTag.startsWith("CHOOSE:") && !aTag.startsWith("CHOOSE:LANGAUTO"))
		{
			obj.setChoiceString(aTag.substring(7));
		}
		else if (aTag.startsWith("DEFINE:"))
		{
// This was removed because the use of -9 and 0 are not the same; -9 is for level-independent stuff, 0 is to support apprentice classes
//			if (anInt > -9)
//			{
			obj.addVariable(anInt + "|" + aTag.substring(7));
//			}
//			else
//			{
//				obj.addVariable("0|" + aTag.substring(7));
//			}
		}
		else if (aTag.startsWith("DESC:"))
		{
			obj.setDescription(pcgen.io.EntityEncoder.decode(aTag.substring(5)));
		}
		else if (aTag.startsWith("DESCISPI:"))
		{
			obj.setDescIsPI(aTag.substring(9).startsWith("Y"));
		}
		else if (aTag.startsWith("DR:"))
		{
			if (anInt > -9)
			{
				obj.setDR(anInt + "|" + aTag.substring(3));
			}
			else
			{
				obj.setDR(aTag.substring(3));
			}
		}
		else if (aTag.startsWith("KEY:"))
		{
			obj.setKeyName(aTag.substring(4));
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
		}
		else if (aTag.startsWith("LANGAUTO:"))
		{
			obj.addLanguageAutos(aTag.substring(9));
		}
		else if (aTag.startsWith("MOVE:"))
		{
			if (obj instanceof Equipment)
			{
				return false;
			}
			else
			{
				obj.setMoveRates(aTag.substring(5));
				obj.setMoveRatesFlag(0);
			}
		}
		else if (aTag.startsWith("MOVEA:"))
		{
			obj.setMoveRates(aTag.substring(6));
			obj.setMoveRatesFlag(1);
		}
		else if (aTag.startsWith("MOVECLONE:"))
		{
			obj.setMoveRates(aTag.substring(10));
			obj.setMoveRatesFlag(2);
		}
		else if (aTag.startsWith("NAME:"))
		{
			obj.setName(aTag.substring(5));
		}
		else if (aTag.startsWith("NAMEISPI:"))
		{
			obj.setNameIsPI(aTag.substring(9).startsWith("Y"));
		}
		else if (aTag.startsWith("NATURALATTACKS:"))
		{
			// first entry is primary, others are secondary
			// lets try the format:
			// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon name,num attacks,damage|secondary2.....
			// damage will be of the form XdY+Z or XdY-Z
			obj.setNaturalAttacks(obj, aTag.substring(15));
		}
		else if (aTag.startsWith("OUTPUTNAME:"))
		{
			obj.setOutputName(aTag.substring(11));
		}
		else if (aTag.startsWith("PRE") || aTag.startsWith("!PRE") || aTag.startsWith("RESTRICT:"))
		{
			obj.addPreReq(aTag);
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
		}
		else if (aTag.startsWith("SA:"))
		{
			obj.setSpecialAbilityList(aTag.substring(3), anInt);
		}
		else if (aTag.startsWith("SPELL:") && !(obj instanceof Campaign))
		{
			obj.addSpells(anInt, aTag.substring(6));
		}
		else if (aTag.startsWith("SPELLLEVEL:") && !(obj instanceof Campaign))
		{
			obj.addSpellLevel(aTag.substring(11));
		}
		else if (aTag.startsWith("SR:"))
		{
			if (anInt > -9)
			{
				obj.setSR(anInt + "|" + aTag.substring(3));
			}
			else
			{
				obj.setSR(aTag.substring(3));
			}
		}
		else if (aTag.startsWith(Constants.s_TAG_TYPE))
		{
			obj.setTypeInfo(aTag.substring(Constants.s_TAG_TYPE.length()));
		}
		else if (aTag.startsWith("UDAM:"))
		{
			obj.addUdamList(aTag.substring(5));
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
		}
		else if (aTag.startsWith("VISION:"))
		{
			if (anInt > -9)
			{
				obj.setVision(anInt + "|" + aTag.substring(7));
			}
			else
			{
				obj.setVision(aTag.substring(7));
			}
		}
		else if (aTag.startsWith("WEAPONAUTO:"))
		{
			obj.setWeaponProfAutos(aTag.substring(11));
		}
		else
		{
			return false;
		}
		return true;
	}

}
