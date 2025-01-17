/*
 * TempBonusToken.java
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

import java.util.StringTokenizer;
import pcgen.core.PlayerCharacter;

//TEMPBONUS
public class TempBonusToken extends Token {
	public static final String TOKENNAME = "TEMPBONUS";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		if(aTok.hasMoreTokens()) {
			int tempIndex = 0;
			try {
				tempIndex = Math.max(0, Integer.parseInt(aTok.nextToken()) - 1);
			}
			catch(Exception e) {}
			if (tempIndex < pc.getNamedTempBonusList().size()) {
			}
			String subToken = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";
			if("NAME".equals(subToken)) {
			}
		}
		return retString;
	}

	public static String getNameToken(PlayerCharacter pc, int tempIndex) {
		return pc.getNamedTempBonusList().get(tempIndex) + "";
	}
}

