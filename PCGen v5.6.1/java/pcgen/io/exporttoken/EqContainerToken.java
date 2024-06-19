/*
 * EqContainerToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:05 $
 *
 */
package pcgen.io.exporttoken;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.BigDecimalHelper;

//EQCONTAINER.x.ACCHECK
//EQCONTAINER.x.ACMOD
//EQCONTAINER.x.ALTCRIT
//EQCONTAINER.x.ALTDAMAGE
//EQCONTAINER.x.ATTACKS
//EQCONTAINER.x.CARRIED
//EQCONTAINER.x.CONTENTS.?
//EQCONTAINER.x.CONTENTWEIGHT
//EQCONTAINER.x.COST
//EQCONTAINER.x.CRITMULT
//EQCONTAINER.x.CRITRANGE
//EQCONTAINER.x.DAMAGE
//EQCONTAINER.x.EDR
//EQCONTAINER.x.EQUIPPED
//EQCONTAINER.x.ITEMWEIGHT
//EQCONTAINER.x.LOCATION
//EQCONTAINER.x.LONGNAME
//EQCONTAINER.x.MAXDEX
//EQCONTAINER.x.MOVE
//EQCONTAINER.x.NAME
//EQCONTAINER.x.OUTPUTNAME
//EQCONTAINER.x.PROF
//EQCONTAINER.x.QTY
//EQCONTAINER.x.RANGE
//EQCONTAINER.x.SIZE
//EQCONTAINER.x.SPELLFAILURE
//EQCONTAINER.x.SPROP
//EQCONTAINER.x.TOTALWEIGHT
//EQCONTAINER.x.TYPE.?
//EQCONTAINER.x.WT
public class EqContainerToken extends Token
{
	public static final String TOKENNAME = "EQCONTAINER";
	public static final String INDENT = "\t";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		aTok.nextToken(); //clear EQCONTAINER Token
		Equipment eq = null;
		if(aTok.hasMoreElements()) {
			try {
				int containerNo = Integer.parseInt(aTok.nextToken());
				eq = getContainer(pc, containerNo);
			}
			catch (NumberFormatException e) {
			    // TODO - This exception needs to be handled
			}
		}
		
		if(eq != null) {
			String property = "NAME";
			if(aTok.hasMoreElements()) {
				property = aTok.nextToken();
			}

			if (property.equals("ACCHECK"))
			{
				retString = getAcCheckToken(eq) + "";
			}
			else if (property.equals("ACMOD"))
			{
				retString = getAcModToken(eq) + "";
			}
			else if (property.equals("ALTCRIT"))
			{
				retString = getAltCritToken(eq);
			}
			else if (property.equals("ALTDAMAGE"))
			{
				retString = getAltDamageToken(eq);
			}
			else if (property.equals("ATTACKS"))
			{
				retString = getAttacksToken(eq) + "";
			}
			else if (property.equals("CARRIED"))
			{
				retString = getCarriedToken(eq) + "";
			}
			else if (property.equals("CONTENTS"))
			{
				retString = getContentsToken(eq, aTok);
			}
			else if (property.equals("CONTENTWEIGHT"))
			{
				retString = BigDecimalHelper.trimZeros(getContentWeightToken(eq) + "");
			}
			else if (property.equals("COST"))
			{
				retString = BigDecimalHelper.trimZeros(getCostToken(eq));
			}
			else if (property.equals("CRITMULT"))
			{
				retString = getCritMultToken(eq);
			}
			else if (property.equals("CRITRANGE"))
			{
				retString = getCritRangeToken(eq);
			}
			else if (property.equals("DAMAGE"))
			{
				retString = getDamageToken(pc, eq);
			}
			else if (property.equals("EDR"))
			{
				retString = getEdrToken(eq) + "";
			}
			else if (property.equals("EQUIPPED"))
			{
				retString = getEquippedToken(eq);
			}
			else if (property.equals("ITEMWEIGHT"))
			{
				retString = BigDecimalHelper.trimZeros(getItemWeightToken(eq) + "");
			}
			else if (property.equals("LOCATION"))
			{
				retString = getLocationToken(eq);
			}
			else if (property.equals("LONGNAME"))
			{
				retString = getLongNameToken(eq);
			}
			else if (property.equals("MAXDEX"))
			{
				retString = getMaxDexToken(eq) + "";
			}
			else if (property.equals("MOVE"))
			{
				retString = getMoveToken(eq);
			}
			else if (property.equals("NAME") || property.equals("OUTPUTNAME"))
			{
				retString = getNameToken(eq);
			}
			else if (property.equals("PROF"))
			{
				retString = getProfToken(eq);
			}
			else if (property.equals("QTY"))
			{
				retString = BigDecimalHelper.trimZeros(Double.toString((getQuantityToken(eq))));
			}
			else if (property.equals("RANGE"))
			{
				retString = getRangeToken(eq) + "";
			}
			else if (property.equals("SIZE"))
			{
				retString = getSizeToken(eq);
			}
			else if (property.equals("SPELLFAILURE"))
			{
				retString = getSpellFailureToken(eq) + "";
			}
			else if (property.equals("SPROP"))
			{
				retString = getSPropToken(eq);
			}
			else if (property.equals("TOTALWEIGHT") || property.equals("WT"))
			{
				retString = BigDecimalHelper.trimZeros(getTotalWeightToken(eq) + "");
			}
			else if (property.equals("TYPE"))
			{
				retString = getTypeToken(eq, aTok);
			}
		}
		return retString;
	}

	public static int getAcCheckToken(Equipment eq)
	{
		return eq.acCheck().intValue();
	}

	public static int getAcModToken(Equipment eq)
	{
		return eq.getACMod().intValue();
	}

	public static String getAltCritToken(Equipment eq)
	{
		return eq.getAltCritMult();
	}

	public static String getAltDamageToken(Equipment eq)
	{
		return eq.getAltDamage();
	}

	public static double getAttacksToken(Equipment eq)
	{
		return eq.bonusTo("COMBAT", "ATTACKS", true);
	}

	public static float getCarriedToken(Equipment eq)
	{
		return eq.numberCarried().floatValue();
	}

	//TODO: Pull this processing out of equipment
	public static String getContentsToken(Equipment eq, StringTokenizer aTok)
	{
		String retString = "";
		if (aTok.hasMoreTokens())
		{
			String aType = aTok.nextToken();
			String aSubTag = "NAME";

			if (aTok.hasMoreTokens())
			{
				aSubTag = aTok.nextToken();
			}

			retString = eq.getContainerByType(aType, aSubTag);
		}
		else
		{
			retString = eq.getContainerContentsString();
		}
		return retString;
	}

	public static float getContentWeightToken(Equipment eq)
	{
		if (eq.getChildCount() == 0)
		{
			return 0;
		}
		else
		{
			return eq.getContainedWeight().floatValue();
		}
	}

	public static BigDecimal getCostToken(Equipment eq)
	{
		return eq.getCost();
	}

	public static String getCritMultToken(Equipment eq)
	{
		return eq.getCritMult();
	}

	public static String getCritRangeToken(Equipment eq)
	{
		return eq.getCritRange();
	}

	public static String getDamageToken(PlayerCharacter pc, Equipment eq)
	{
		String retString = eq.getDamage();

		if ((pc != null) && (eq.isNatural()))
		{
			retString = Globals.adjustDamage(retString, Globals.sizeInt(pc.getRace().getSize()), pc.sizeInt());
		}

		return retString;
	}

	public static int getEdrToken(Equipment eq)
	{
		return eq.eDR().intValue();
	}

	public static String getEquippedToken(Equipment eq)
	{
		if (eq.isEquipped())
		{
			return "Y";
		}
		else
		{
			return "N";
		}
	}

	public static float getItemWeightToken(Equipment eq)
	{
		return eq.getWeight().floatValue();
	}

	public static String getLocationToken(Equipment eq)
	{
		return eq.getParentName();
	}

	public static String getLongNameToken(Equipment eq)
	{
		String retString = "";
		int depth = eq.itemDepth();

		while (depth > 0)
		{
			retString += INDENT;
			--depth;
		}

		return retString + eq.longName();
	}

	public static int getMaxDexToken(Equipment eq)
	{
		return eq.getMaxDex().intValue();
	}

	public static String getMoveToken(Equipment eq)
	{
		return eq.moveString();
	}

	public static String getNameToken(Equipment eq)
	{
		return eq.parseOutputName(eq.getOutputName());
	}

	public static String getProfToken(Equipment eq)
	{
		return eq.profName();
	}

	public static double getQuantityToken(Equipment eq)
	{
		return eq.qty();
	}

	public static int getRangeToken(Equipment eq)
	{
		return eq.getRange().intValue();
	}

	public static String getSizeToken(Equipment eq)
	{
		return eq.getSize();
	}

	public static int getSpellFailureToken(Equipment eq)
	{
		return eq.spellFailure().intValue();
	}

	public static String getSPropToken(Equipment eq)
	{
		return eq.getSpecialProperties();
	}

	public static float getTotalWeightToken(Equipment eq)
	{
		return getContentWeightToken(eq) + getItemWeightToken(eq);
	}

	public static String getTypeToken(Equipment eq, StringTokenizer aTok)
	{
		String retString = "";
		if (aTok.hasMoreTokens())
		{
			try
			{
				int x = Integer.parseInt(aTok.nextToken());
				retString = eq.typeIndex(x);
			}
			catch (NumberFormatException e)
			{
			    // TODO - This exception needs to be handled
			}
		}
		else
		{
			retString = eq.getType();
		}
		return retString;
	}

	private Equipment getContainer(PlayerCharacter pc, int no)
	{
		List eqList = pc.getEquipmentListInOutputOrder();
		for(int i = 0; i < eqList.size(); i++)
		{
			Equipment eq = (Equipment)eqList.get(i);
			if(eq.isContainer())
			{
				no--;
			}
			
			if(no < 0)
			{
				return eq;
			}
		}
		return null;
	}
}

