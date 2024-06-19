/*
 * EquipmentLoader.java
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
 * $Id: EquipmentLoader.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Delta;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class EquipmentLoader
{

	/** Creates a new instance of EquipmentLoader */
	private EquipmentLoader()
	{
	}

	public static void parseLine(Equipment equipment, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		if (!equipment.isNewItem())
		{
			col = 1; // .MOD skips required fields
			colToken.nextToken(); // skip name
		}
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (PObjectLoader.parseTag(equipment, colString))
			{
				continue;
			}
			final int aLen = colString.length();
			if (col == 0)
			{
				equipment.setName(colString);
			}
			else if ((aLen > 3) && colString.startsWith("AC:"))
			{
				final String aBonus = "BONUS:COMBAT|AC|" + colString.substring(3) + "|TYPE=Armor.REPLACE";
				PObjectLoader.parseTag(equipment, aBonus);
			}
			else if ((aLen > 8) && colString.startsWith("ACCHECK:"))
			{
				equipment.setACCheck(colString.substring(8));
			}
			else if ((aLen > 12) && colString.startsWith("ALTCRITICAL:"))
			{
				equipment.setAltCrit(colString.substring(12));
			}
			else if ((aLen > 10) && colString.startsWith("ALTDAMAGE:"))
			{
				equipment.setAltDamage(colString.substring(10));
			}
			else if (colString.startsWith("ALTEQMOD:"))
			{
				equipment.addEqModifiers(colString.substring(9), false);
			}
			else if ((aLen > 8) && colString.startsWith("ALTTYPE:"))
			{
				equipment.setAltTypeList(colString.substring(8));
			}
			else if ((aLen > 8) && colString.startsWith("ATTACKS:"))
			{
				equipment.setAttacks(Delta.decode(colString.substring(8)));
			}
			else if (colString.startsWith("BASEITEM:"))
			{
				equipment.setBaseItem(colString.substring(9));
			}
			else if (colString.startsWith("BASEQTY:"))
			{
				equipment.setBaseQty(colString.substring(8));
			}
			else if ((aLen > 10) && colString.startsWith("BONUSTYPE:"))
			{
				equipment.setBonusType(colString.substring(10));
			}
			else if (aLen > 7 && colString.startsWith("CHOOSE:"))
			{
				equipment.setChoiceString(colString.substring(7));
			}
			//else if ((aLen > 5) && colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	equipment.setTypeString(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else if (colString.startsWith("CONTAINS:"))
			{
				equipment.setContainer(colString.substring(9));
			}
			else if ((aLen > 5) && (colString.startsWith("Cost:") || colString.startsWith("COST:")))
			{
				equipment.setCost(colString.substring(5), true);
			}
			else if ((aLen > 9) && colString.startsWith("CRITMULT:"))
			{
				equipment.setCritMult(colString.substring(9));
			}
			else if ((aLen > 10) && colString.startsWith("CRITRANGE:"))
			{
				equipment.setCritRange(colString.substring(10));
			}
			else if ((aLen > 7) && colString.startsWith("DAMAGE:"))
			{
				equipment.setDamage(colString.substring(7));
			}
			else if ((aLen > 9) && colString.startsWith("DEFBONUS:"))
			{
				equipment.setDefBonus(colString.substring(7));
			}
			else if ((aLen > 4) && colString.startsWith("EDR:"))
			{
				Globals.debugPrint("Equipment DR tag found.");
				equipment.seteDR(colString.substring(4));
			}
			else if (colString.startsWith("EQMOD:"))
			{
				equipment.addEqModifiers(colString.substring(6), true);
			}
			else if ((aLen > 6) && colString.startsWith("HANDS:"))
			{
				try
				{
					equipment.setHands(Delta.parseInt(colString.substring(6)));
				}
				catch (NumberFormatException nfe)
				{
					throw new PersistenceLayerException("Illegal number of required hands " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
			else if ((aLen > 9) && colString.startsWith("LONGNAME:"))
			{
				equipment.setLongName(colString.substring(9));
			}
			else if ((aLen > 7) && colString.startsWith("MAXDEX:"))
			{
				equipment.setMaxDex(colString.substring(7));
			}
			else if (colString.startsWith("MODS:"))
			{
				switch (colString.charAt(5))
				{
					case 'R':
					case 'r':
						equipment.setModifiersAllowed(true);
						equipment.setModifiersRequired(true);
						break;

					case 'Y':
					case 'y':
						equipment.setModifiersAllowed(true);
						equipment.setModifiersRequired(false);
						break;

					case 'N':
					case 'n':
						equipment.setModifiersAllowed(false);
						equipment.setModifiersRequired(false);
						break;
					default:
						Globals.errorPrint("In EquipmentLoader.parseline the MODS: " + colString.charAt(5) + " is not handled.");
						break;

				}
			}
			else if ((aLen > 5) && colString.startsWith("MOVE:"))
			{
				equipment.setMoveString(colString.substring(5));
			}
			else if ((aLen > 12) && colString.startsWith("PROFICIENCY:"))
			{
				equipment.setProfName(colString.substring(12));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				equipment.setQualifyString(colString.substring(8));
			}
			else if ((aLen > 6) && colString.startsWith("RANGE:"))
			{
				equipment.setRange(colString.substring(6));
			}
			else if ((aLen > 6) && colString.startsWith("RATEOFFIRE:"))
			{
				equipment.setRateOfFire(colString.substring(11));
			}
			else if (colString.startsWith("REACH:"))
			{
				equipment.setReach(Integer.parseInt(colString.substring(6)));
			}
			else if ((aLen > 5) && colString.startsWith("SIZE:"))
			{
				equipment.setSize(colString.substring(5), true);
			}
			else if ((aLen > 13) && colString.startsWith("SPELLFAILURE:"))
			{
				equipment.setSpellFailure(colString.substring(13));
			}
			else if ((aLen > 6) && colString.startsWith("SPROP:"))
			{
				equipment.setSpecialProperties(colString.substring(6));
			}
/*			else if ((aLen > 6) && colString.startsWith("TECHLEVEL:"))
			{
				equipment.setTechLevel(colString.substring(10));
			}*/
			else if (colString.startsWith("VFEAT:"))
			{
				equipment.addVFeatList(colString.substring(6));
			}
			else if ((aLen > 3) && colString.startsWith("WT:"))
			{
				equipment.setWeight(colString.substring(3));
			}
			else
			{
				throw new PersistenceLayerException("Illegal equipment info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			col++;
		}

		final String bonusType = equipment.getBonusType();

		if (equipment.isArmor())
		{
			if (bonusType == null)
			{
				equipment.setBonusType("Armor");
				return;
			}
			if (bonusType.lastIndexOf("Armor") > -1)
			{
				return;
			}
			equipment.setBonusType(bonusType + "Armor");
		}
		else if (equipment.isShield())
		{
			if (bonusType == null)
			{
				equipment.setBonusType("Shield");
				return;
			}
			if (bonusType.lastIndexOf("Shield") > -1)
			{
				return;
			}
			equipment.setBonusType(bonusType + "Shield");
		}
	}
}
