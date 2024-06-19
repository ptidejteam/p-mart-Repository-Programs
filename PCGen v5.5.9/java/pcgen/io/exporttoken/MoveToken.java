/*
 * MoveToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
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
 * Last Edited: $Date: 2006/02/21 01:28:06 $
 *
 */

package pcgen.io.exporttoken;

import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

//MOVE prints out all movename/move pairs
//MOVE.x prints out movename/move pair
//MOVE.x.NAME and
//MOVE.x.RATE produce the appropriate parts.
public class MoveToken extends Token {
	public static final String TOKENNAME = "MOVE";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		if(aTok.hasMoreTokens())
		{
			int moveIndex;
			moveIndex = Integer.parseInt(aTok.nextToken());
			if(aTok.hasMoreTokens())
			{
				String subToken = aTok.nextToken();
				if ("NAME".equals(subToken))
				{
					retString = getNameToken(pc, moveIndex);
				}
				else if ("RATE".equals(subToken))
				{
					retString = getRateToken(pc, moveIndex);
				}
			}
			else
			{
				retString = getMoveXToken(pc, moveIndex);
			}
			retString = getMoveToken(pc);
			//TODO: merge all of MovementToken here, and eliminate MovementToken
		}
		return retString;
	}

	public static String getMoveToken(PlayerCharacter pc)
	{
		String retString = "";
		boolean firstLine = true;
		for (int i = 0; i < pc.getNumberOfMovements(); i++)
		{
			if(!firstLine)
			{
				retString += ", ";
			}
			firstLine = false;

			retString += getMoveXToken(pc, i);
		}
		return retString;
	}

	public static String getMoveXToken(PlayerCharacter pc, int moveIndex)
	{
		return getNameToken(pc, moveIndex) + " " + getRateToken(pc, moveIndex);
	}

	public static String getNameToken(PlayerCharacter pc, int moveIndex)
	{
		return pc.getMovementType(moveIndex);
	}

	public static String getRateToken(PlayerCharacter pc, int moveIndex)
	{
		return Globals.displayDistanceInUnitSet(pc.movement(moveIndex)) + Globals.getDistanceUnit();
	}
}

