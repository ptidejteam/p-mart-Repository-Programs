/*
 * WeightToken.java
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

//WEIGHT
//WEIGHT.NOUNIT
//WEIGHT.LIGHT
//WEIGHT.MEDIUM
//WEIGHT.HEAVY
public class WeightToken extends Token {
	public static final String TOKENNAME = "WEIGHT";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		if ("WEIGHT".equals(tokenSource)) {
			retString = getWeightToken(pc);
		}
		else if ("WEIGHT.NOUNIT".equals(tokenSource)) {
			retString = getNoUnitToken(pc);
		}
		else if ("WEIGHT.LIGHT".equals(tokenSource)) {
			retString = getLightToken(pc) + "";
		}
		else if ("WEIGHT.MEDIUM".equals(tokenSource)) {
			retString = getMediumToken(pc) + "";
		}
		else if ("WEIGHT.HEAVY".equals(tokenSource)) {
			retString = getHeavyToken(pc) + "";
		}
		return retString;
	}

	public static String getWeightToken(PlayerCharacter pc) {
		return Globals.displayWeightInUnitSet(pc.getWeight()) + " " + Globals.getWeightUnit();
	}

	public static String getNoUnitToken(PlayerCharacter pc) {
		return Globals.displayWeightInUnitSet(pc.getWeight());
	}

	public static double getLightToken(PlayerCharacter pc) {
		double weightInPounds = 1 * Globals.maxLoadForLoadScore(pc.getVariableValue("LOADSCORE", "").intValue()).intValue() / 3;
		return Globals.convertWeightToUnitSet(weightInPounds);
	}

	public static double getMediumToken(PlayerCharacter pc) {
		double weightInPounds = 2 * Globals.maxLoadForLoadScore(pc.getVariableValue("LOADSCORE", "").intValue()).intValue() / 3;
		return Globals.convertWeightToUnitSet(weightInPounds);
	}

	public static double getHeavyToken(PlayerCharacter pc) {
		double weightInPounds = 3 * Globals.maxLoadForLoadScore(pc.getVariableValue("LOADSCORE", "").intValue()).intValue() / 3;
		return Globals.convertWeightToUnitSet(weightInPounds);
	}
}

