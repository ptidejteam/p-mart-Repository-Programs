/*
 * BaseMovementToken.java
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

import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * BASEMOVEMENT related stuff
 * possible tokens are
 * BASEMOVEMENT.type.load.flag
 * where
 * type    := "WALK" and other Movement Types|a numeric value
 * so 0 is the first movement type etc.
 * load     := "LIGHT"|"MEDIUM"|"HEAVY"|"OVERLOAD"
 * flag     := "TRUE"|"FALSE"
 * TRUE = Add Movement Measurement type to String.
 * FALSE = Dont Add Movement Measurement type to String
 * del     := "."
 * <p/>
 * i.e. BASEMOVEMENT.0.LIGHT.TRUE
 * Would output 30' for a normal human
 * and    BASEMOVEMENT.0.LIGHT.FALSE
 * Would output 30 for the same human.
 * <p/>
 */
public class BaseMovementToken extends Token
{
	public static final String TOKENNAME = "BASEMOVEMENT";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		String retString = "";
		if ((pc.getRace() != null) && !pc.getRace().equals(Globals.s_EMPTYRACE))
		{
			StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
			aTok.nextToken(); //clear BASEMOVEMENT Token
			String moveType = "WALK";
			int load = Constants.LIGHT_LOAD;
			boolean flag = true;
			
			//Move Type
			if(aTok.hasMoreElements()) {
				moveType = aTok.nextToken();
				try
				{
					int movNum = Integer.parseInt(moveType);
					if (movNum < pc.getNumberOfMovements())
					{
						moveType = pc.getMovementType(movNum);
					}
				}
				catch (NumberFormatException e)
				{
				    // TODO - This exception needs to be handled
				}
			}
			
			//Encumberance Level
			if(aTok.hasMoreElements()) {
				String loadName = aTok.nextToken();
				if ("MEDIUM".equals(loadName))
				{
					load = Constants.MEDIUM_LOAD;
				}
				else if ("HEAVY".equals(loadName))
				{
					load = Constants.HEAVY_LOAD;
				}
				else if ("OVERLOAD".equals(loadName))
				{
					load = Constants.OVER_LOAD;
				}
			}
			
			//Display Movement Measurement type?
			if(aTok.hasMoreElements()) {
				flag =  "TRUE".equals((aTok.nextToken()).toUpperCase());
			}
			retString = getBaseMovementToken(pc, moveType, load, flag);
		}
		return retString;
	}

	public static String getBaseMovementToken(PlayerCharacter pc, String moveType, int loadType, boolean displayFlag)
	{
		for (int i = 0; i < pc.getNumberOfMovements(); i++)
		{
			if (pc.getMovementType(i).toUpperCase().equals(moveType.toUpperCase()))
			{
				if (displayFlag)
				{
					return moveType +
					 " " +
					 Globals.displayDistanceInUnitSet(Globals.convertDistanceToUnitSet(pc.basemovement(i, loadType))) +
					 Globals.getDistanceUnit();
				}
				else {
					return Globals.displayDistanceInUnitSet(Globals.convertDistanceToUnitSet(pc.basemovement(i, loadType)));
				}
			}
		}
		return "";
	}
}

