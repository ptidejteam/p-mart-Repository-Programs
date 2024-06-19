/*
 * TotalToken.java
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

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

//TOTAL.WEIGHT
//TOTAL.VALUE
//TOTAL.CAPACITY
//TOTAL.LOAD
public class TotalToken extends Token {
	public static final String TOKENNAME = "TOTAL";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		if("TOTAL.WEIGHT".equals(tokenSource)) {
			retString = getWeightToken(pc);
		}
		else if("TOTAL.VALUE".equals(tokenSource)) {
			retString = getValueToken(pc);
		}
		else if("TOTAL.CAPACITY".equals(tokenSource)) {
			retString = getCapacityToken(pc);
		}
		else if("TOTAL.LOAD".equals(tokenSource)) {
			retString = getLoadToken(pc);
		}
		return retString;
	}

	public static String getWeightToken(PlayerCharacter pc) {
		return pc.totalWeight() + " " + Globals.getWeightUnit();
	}
	public static String getValueToken(PlayerCharacter pc) {
		return BigDecimalHelper.trimZeros(pc.totalValue()) + " " + Globals.getCurrencyDisplay();
	}

	public static String getCapacityToken(PlayerCharacter pc) {
		return Globals.maxLoadForLoadScore(pc.getVariableValue("LOADSCORE", "").intValue()).toString();
	}

	public static String getLoadToken(PlayerCharacter pc) {
		int load = Globals.loadTypeForLoadScore(pc.getVariableValue("LOADSCORE", "").intValue(), pc.totalWeight());
		switch (load) {
			case Constants.LIGHT_LOAD:
				return "Light";
			case Constants.MEDIUM_LOAD:
				return "Medium";
			case Constants.HEAVY_LOAD:
				return "Heavy";
			case Constants.OVER_LOAD:
				return "Overload";
			default:
				Logging.errorPrint("Unknown load constant detected in TokenTotal.getLoadToken, the constant was " + load + ".");
				return "Unknown";
		}
	}
}

