/*
 * ExpToken.java
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
import pcgen.core.PlayerCharacter;

//EXP.CURRENT
//EXP.NEXT
//EXP.FACTOR
//EXP.PENALTY
public class ExpToken extends Token {
	public static final String TOKENNAME = "EXP";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		if(aTok.hasMoreTokens()) {
			String token = aTok.nextToken();
			if("CURRENT".equals(token)) {
				retString = getCurrentToken(pc) + "";
			}
			else if("NEXT".equals(token)) {
				retString = getNextToken(pc) + "";
			}
			else if("FACTOR".equals(token)) {
				retString = getFactorToken(pc);
			}
			else if("PENALTY".equals(token)) {
				retString = getPenaltyToken(pc);
			}
		}
		return retString;
	}

	public static int getCurrentToken(PlayerCharacter pc) {
		return pc.getXP();
	}

	public static int getNextToken(PlayerCharacter pc) {
		return pc.minXPForNextECL();
	}

	public static String getFactorToken(PlayerCharacter pc) {
		StringBuffer xpFactor = new StringBuffer(5);
		xpFactor.append((int) (pc.multiclassXPMultiplier() * 100.0));
		xpFactor.append('%');
		return xpFactor.toString();
	}

	public static String getPenaltyToken(PlayerCharacter pc) {
		StringBuffer xpFactor = new StringBuffer(5);
		xpFactor.append(100 - (int) (pc.multiclassXPMultiplier() * 100.0));
		xpFactor.append('%');
		return xpFactor.toString();
	}
}

