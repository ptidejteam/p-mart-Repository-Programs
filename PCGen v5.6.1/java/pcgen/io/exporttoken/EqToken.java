/*
 * EqToken.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.util.BigDecimalHelper;

//EQ
public class EqToken extends Token
{
	public static final String TOKENNAME = "EQ";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse reading
		String bFilter = "";
		String befTrue = "";
		String aftTrue = "";
		String befFalse = "";
		String aftFalse = "";
		StringTokenizer bTok = new StringTokenizer(tokenSource, "~");

		if (bTok.countTokens() >= 3)
		{
			bFilter = bTok.nextToken();
			befTrue = bTok.nextToken();
			aftTrue = bTok.nextToken();

			if (bTok.hasMoreTokens())
			{
				befFalse = bTok.nextToken();
				aftFalse = bTok.nextToken();
			}

			tokenSource = tokenSource.substring(0, bFilter.lastIndexOf('.'));
		}

		bTok = new StringTokenizer(bFilter, ".");

		boolean if_detected = false;

		while (bTok.hasMoreTokens())
		{
			String bString = bTok.nextToken();

			if ("IF".equals(bString))
			{
				if_detected = true;
			}
			else
			{
				if (if_detected)
				{
					bFilter = bFilter + "." + bString;
				}
				else
				{
					bFilter = bString;
				}
			}
		}

		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		aTok.nextToken();

		//Merge
		String token = aTok.nextToken();
		int merge = Constants.MERGE_ALL;
		if (token.indexOf("MERGE") >= 0)
		{
			merge = returnMergeType(token);
			token = aTok.nextToken();
		}

		//Get List
		List eqList = new ArrayList();
		for (Iterator e = pc.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			eqList.add(eq);
		}

		int temp = -1;
		//Begin Not code...
		while (aTok.hasMoreTokens())
		{
			if ("NOT".equalsIgnoreCase(token))
			{
				eqList = listNotType(pc, eqList, aTok.nextToken());
			}
			else if ("ADD".equalsIgnoreCase(token))
			{
				eqList = listAddType(pc, eqList, aTok.nextToken());
			}
			else if ("IS".equalsIgnoreCase(token))
			{
				eqList = listIsType(pc, eqList, aTok.nextToken());
			}
			else
			{
				// In the end of the above, bString would
				// be valid token, that should go into temp.
				try
				{
					temp = Integer.parseInt(token);
				}
				catch (NumberFormatException exc)
				{
					// not an error!
				}
			}

			if (temp >= 0)
			{
				break;
			}
			else
			{
				token = aTok.nextToken();
			}
		}

		String tempString = aTok.nextToken();
		String retString = "";

		if ((temp >= 0) && (temp < eqList.size()))
		{
			Equipment eq = (Equipment)eqList.get(temp);
			retString = getEqToken(pc, eq, tempString, aTok);

			// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse treatment
			if (!"".equals(bFilter))
			{
				aTok = new StringTokenizer(bFilter, ".");
	
				boolean result = false;
				boolean and_operation = false;
	
				while (aTok.hasMoreTokens())
				{
					String bString = aTok.nextToken();
	
					if ("AND".equals(bString))
					{
						and_operation = true;
					}
					else if ("OR".equals(bString))
					{
						and_operation = false;
					}
					else
					{
						if (and_operation)
						{
							result = (result && eq.isType(bString));
						}
						else
						{
							result = (result || eq.isType(bString));
						}
					}
				}
	
				if (result)
				{
					retString = befTrue + retString + aftTrue;
				}
				else
				{
					retString = befFalse + retString + aftFalse;
				}
			}
		}
		
		return retString;
	}

	public static String getAcCheckToken(Equipment eq)
	{
		return getAcCheckTokenInt(eq) + "";
	}

	public static int getAcCheckTokenInt(Equipment eq)
	{
		return eq.acCheck().intValue();
	}

	public static String getAcModToken(Equipment eq)
	{
		return getAcModTokenInt(eq) + "";
	}

	public static int getAcModTokenInt(Equipment eq)
	{
		return eq.getACMod().intValue();
	}

	public static String getAltCritMultToken(Equipment eq)
	{
		return eq.getAltCritMult();
	}

	public static String getAltCritRangeToken(Equipment eq)
	{
		return eq.getAltCritRange();
	}

	public static String getAltDamageToken(Equipment eq)
	{
		return eq.getAltDamage();
	}

	public static String getAttacksToken(Equipment eq)
	{
		return getAttacksTokenDouble(eq) + "";
	}

	public static double getAttacksTokenDouble(Equipment eq)
	{
		return eq.bonusTo("COMBAT", "ATTACKS", true);
	}

	public static String getCarriedToken(Equipment eq)
	{
		return getCarriedTokenFloat(eq) + "";
	}

	public static float getCarriedTokenFloat(Equipment eq)
	{
		return eq.numberCarried().floatValue();
	}

	public static String getChargesToken(Equipment eq)
	{
		String retString = "";
		int charges = getChargesTokenInt(eq);
		if (charges >= 0)
		{
			retString = charges + "";
		}
		return retString;
	}

	public static int getChargesTokenInt(Equipment eq)
	{
		return eq.getRemainingCharges();
	}

	public static String getChargesUsedToken(Equipment eq)
	{
		String retString = "";
		int charges = getChargesUsedTokenInt(eq);
		if (charges >= 0)
		{
			retString = charges + "";
		}
		return retString;
	}

	public static int getChargesUsedTokenInt(Equipment eq)
	{
		return eq.getUsedCharges();
	}

	public static String getContentWeightToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(Double.toString(getContentWeightTokenDouble(eq)));
	}

	public static double getContentWeightTokenDouble(Equipment eq)
	{
			if (eq.getChildCount() == 0)
			{
				return 0.0;
			}
			else
			{
				return eq.getContainedWeight(true).doubleValue();
			}
	}

	public static String getContentsToken(PlayerCharacter pc, Equipment eq, StringTokenizer tokenizer)
	{
		if (tokenizer.hasMoreTokens())
		{
			String bType = tokenizer.nextToken();
			String aSubTag = "NAME";

			if (tokenizer.hasMoreTokens())
			{
				aSubTag = tokenizer.nextToken();
			}

			try
			{
				int contentsIndex = Integer.parseInt(bType);
				return getEqToken(pc, eq.getContainedByIndex(contentsIndex), aSubTag, tokenizer);
			}
			catch (NumberFormatException e)
			{
				return eq.getContainerByType(bType, aSubTag);
			}
		}
		else
		{
			return getContentsToken(eq);
		}
	}

	public static String getContentsToken(Equipment eq)
	{
		return eq.getContainerContentsString();
	}

	public static String getContentsNumToken(Equipment eq)
	{
		return getContentsNumTokenInt(eq) + "";
	}

	public static int getContentsNumTokenInt(Equipment eq)
	{
		return eq.getContents().size();
	}

	public static String getCostToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(eq.getCost());
	}

	public static String getCritMultToken(Equipment eq)
	{
		return eq.getCritMult();
	}

	public static String getCritRangeToken(Equipment eq)
	{
		return eq.getCritRange();
	}

	public static String getDamageToken(Equipment eq)
	{
		return eq.getDamage();
	}

	public static String getEdrToken(Equipment eq)
	{
		return getEdrTokenInt(eq) + "";
	}

	public static int getEdrTokenInt(Equipment eq)
	{
		return eq.eDR().intValue();
	}

	public static String getEquippedToken(Equipment eq)
	{
		return getEquippedTokenBoolean(eq) ? "Y" : "N";
	}

	public static boolean getEquippedTokenBoolean(Equipment eq)
	{
		return eq.isEquipped();
	}

	public static String getIsTypeToken(Equipment eq, String type)
	{
		return getIsTypeTokenBoolean(eq, type) ? "TRUE" : "FALSE";
	}

	public static boolean getIsTypeTokenBoolean(Equipment eq, String type)
	{
		return eq.isType(type);
	}

	public static String getLocationToken(Equipment eq)
	{
		return eq.getParentName();
	}

	public static String getLongNameToken(Equipment eq)
	{
		return eq.longName();
	}

	public static String getMaxChargesToken(Equipment eq)
	{
		String retString = "";
		int charges = getMaxChargesTokenInt(eq);
		if (charges >= 0)
		{
			retString = charges + "";
		}
		return retString;
	}

	public static int getMaxChargesTokenInt(Equipment eq)
	{
		return eq.getMaxCharges();
	}

	public static String getMaxDexToken(Equipment eq)
	{
		return getMaxDexTokenInt(eq) + "";
	}

	public static int getMaxDexTokenInt(Equipment eq)
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

	public static String getNoteToken(Equipment eq)
	{
		return eq.getNote();
	}

	public static String getOutputNameToken(Equipment eq)
	{
		return eq.parseOutputName(eq.getOutputName());
	}

	public static String getProfToken(Equipment eq)
	{
		return eq.profName();
	}

	public static String getQtyToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(Double.toString(getQtyDoubleToken(eq)));
	}

	public static double getQtyDoubleToken(Equipment eq)
	{
		return eq.qty();
	}

	public static String getRangeToken(Equipment eq)
	{
		return Globals.displayDistanceInUnitSet(Globals.convertDistanceToUnitSet(eq.getRange().intValue()))
		 + Globals.getDistanceUnit();
	}

	public static String getSizeToken(Equipment eq)
	{
		return eq.getSize();
	}

	public static String getSizeLongToken(Equipment eq)
	{
		return SystemCollections.getSizeAdjustmentAtIndex(Globals.sizeInt(eq.getSize())).getName();
	}

	public static String getSpellFailureToken(Equipment eq)
	{
		return getSpellFailureTokenInt(eq) + "";
	}

	public static int getSpellFailureTokenInt(Equipment eq)
	{
		return eq.spellFailure().intValue();
	}

	public static String getSpropToken(Equipment eq)
	{
		return eq.getSpecialProperties();
	}

	public static String getTotalWeightToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(getTotalWeightTokenDouble(eq) + "");
	}

	public static double getTotalWeightTokenDouble(Equipment eq)
	{
		return getContentWeightTokenDouble(eq) + getWtTokenDouble(eq);
	}

	public static String getTotalWtToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(Double.toString(getTotalWtTokenDouble(eq)));
	}

	public static double getTotalWtTokenDouble(Equipment eq)
	{
		return eq.qty() * eq.getWeightAsDouble();
	}

	public static String getTypeToken(Equipment eq)
	{
		return eq.getType();
	}

	public static String getTypeToken(Equipment eq, int num)
	{
		return eq.typeIndex(num);
	}

	public static String getWtToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(eq.getWeight().toString());
	}

	public static double getWtTokenDouble(Equipment eq)
	{
		return eq.getWeightAsDouble();
	}

	public static List listNotType(PlayerCharacter pc, List eqList, String type)
	{
		return PlayerCharacter.removeEqType(eqList, type);
	}

	public static List listAddType(PlayerCharacter pc, List eqList, String type)
	{
		return pc.addEqType(eqList, type);
	}

	public static List listIsType(PlayerCharacter pc, List eqList, String type)
	{
		return PlayerCharacter.removeNotEqType(eqList, type);
	}

	protected static String getEqToken(PlayerCharacter pc, Equipment eq, String token, StringTokenizer tokenizer)
	{
		String retString = "";

		if ("LONGNAME".equals(token))
		{
			retString = getLongNameToken(eq);
		}
		else if ("NAME".equals(token) || "OUTPUTNAME".equals(token))
		{
			retString = getNameToken(eq);
		}
		else if ("NOTE".equals(token))
		{
			retString = getNoteToken(eq);
		}
		else if ("WT".equals(token) || "ITEMWEIGHT".equals(token))
		{
			retString = getWtToken(eq);
		}
		else if ("TOTALWT".equals(token))
		{
			retString = getTotalWtToken(eq);
		}
		else if ("TOTALWEIGHT".equals(token))
		{
			retString = getTotalWeightToken(eq);
		}
		else if ("ISTYPE".equals(token))
		{
			retString = getIsTypeToken(eq, tokenizer.nextToken());
		}
		else if ("CONTENTWEIGHT".equals(token))
		{
			retString = getContentWeightToken(eq);
		}
		else if ("COST".equals(token))
		{
			retString = getCostToken(eq);
		}
		else if ("QTY".equals(token))
		{
			retString = getQtyToken(eq);
		}
		else if ("EQUIPPED".equals(token))
		{
			retString = getEquippedToken(eq);
		}
		else if ("CARRIED".equals(token))
		{
			retString = getCarriedToken(eq);
		}
		else if ("CONTENTSNUM".equals(token))
		{
			retString = getContentsNumToken(eq);
		}
		else if ("LOCATION".equals(token))
		{
			retString = getLocationToken(eq);
		}
		else if ("ACMOD".equals(token))
		{
			retString = getAcModToken(eq);
		}
		else if ("MAXDEX".equals(token))
		{
			retString = getMaxDexToken(eq);
		}
		else if ("ACCHECK".equals(token))
		{
			retString = getAcCheckToken(eq);
		}
		else if ("EDR".equals(token))
		{
			retString = getEdrToken(eq);
		}
		else if ("MOVE".equals(token))
		{
			retString = getMoveToken(eq);
		}
		else if ("TYPE".equals(token))
		{
			if(tokenizer.hasMoreTokens())
			{
				try
				{
					int num = Integer.parseInt(tokenizer.nextToken());
					return getTypeToken(eq, num);
				}
				catch(NumberFormatException e)
				{
				    // TODO - This exception needs to be handled
				}
			}
			return getTypeToken(eq);
		}
		else if ("SPELLFAILURE".equals(token))
		{
			retString = getSpellFailureToken(eq);
		}
		else if ("SIZE".equals(token))
		{
			retString = getSizeToken(eq);
		}
		else if ("SIZELONG".equals(token))
		{
			retString = getSizeLongToken(eq);
		}
		else if ("DAMAGE".equals(token))
		{
			retString = getDamageToken(eq);
		}
		else if ("CRITRANGE".equals(token))
		{
			retString = getCritRangeToken(eq);
		}
		else if ("CRITMULT".equals(token))
		{
			retString = getCritMultToken(eq);
		}
		else if ("ALTDAMAGE".equals(token))
		{
			retString = getAltDamageToken(eq);
		}
		else if ("ALTCRITMULT".equals(token) || "ALTCRIT".equals(token))
		{
			retString = getAltCritMultToken(eq);
		}
		else if ("ALTCRITRANGE".equals(token))
		{
			retString = getAltCritRangeToken(eq);
		}
		else if ("RANGE".equals(token))
		{
			retString = getRangeToken(eq);
		}
		else if ("ATTACKS".equals(token))
		{
			retString = getAttacksToken(eq);
		}
		else if ("PROF".equals(token))
		{
			retString = getProfToken(eq);
		}
		else if ("SPROP".equals(token))
		{
			retString = getSpropToken(eq);
		}
		else if ("CHARGES".equals(token))
		{
			retString = getChargesToken(eq);
		}
		else if ("CHARGESUSED".equals(token))
		{
			retString = getChargesUsedToken(eq);
		}
		else if ("MAXCHARGES".equals(token))
		{
			retString = getMaxChargesToken(eq);
		}
		else if ("CONTENTS".equals(token))
		{
			retString = getContentsToken(pc, eq, tokenizer);
		}
		return retString;
	}

	protected static int returnMergeType(String type)
	{
		int merge = Constants.MERGE_ALL;

		if ("MERGENONE".equals(type))
		{
			merge = Constants.MERGE_NONE;
		}
		else if ("MERGELOC".equals(type))
		{
			merge = Constants.MERGE_LOCATION;
		}
		else if ("MERGEALL".equals(type))
		{
			merge = Constants.MERGE_ALL;
		}

		return merge;
	}
}

