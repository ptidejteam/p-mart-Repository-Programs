/*
 * HeightToken.java
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
 * Last Edited: $Date: 2006/02/21 01:33:05 $
 *
 */

package pcgen.io.exporttoken;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

//HEIGHT
//HEIGHT.FOOTPART
//HEIGHT.INCHPART
public class HeightToken extends Token {
	public static final String TOKENNAME = "HEIGHT";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		if ("HEIGHT".equals(tokenSource)) {
			retString = getHeightToken(pc);
		}
		else if ("HEIGHT.FOOTPART".equals(tokenSource)) {
			retString = getFootPartToken(pc) + "";
		}
		else if ("HEIGHT.INCHPART".equals(tokenSource)) {
			retString = getInchPartToken(pc) + "";
		}
		return retString;
	}

	public static String getHeightToken(PlayerCharacter pc) {
		String retString = "";
		if ("ftin".equals(Globals.getUnitSet().getHeightUnit())) {
			retString = Integer.toString((int)(pc.getHeight() / 12)) + "' " + Integer.toString((int)(pc.getHeight() % 12)) + "\"";
		}
		else {
			retString = Globals.displayHeightInUnitSet(pc.getHeight()) + " " + Globals.getHeightUnit();
		}
		return retString;
	}

	public static int getFootPartToken(PlayerCharacter pc) {
		return pc.getHeightInInches() / 12;
	}

	public static int getInchPartToken(PlayerCharacter pc) {
		return pc.getHeightInInches() % 12;
	}
}

