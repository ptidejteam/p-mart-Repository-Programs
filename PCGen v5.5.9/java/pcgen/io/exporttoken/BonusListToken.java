/*
 * BonusListToken.java
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

import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.PlayerCharacter;

//BONUSLIST
public class BonusListToken extends Token {
	public static final String TOKENNAME = "BONUSLIST";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		return getBonusListToken(tokenSource, pc);
	}

	public static String getBonusListToken(String tokenSource, PlayerCharacter pc) {
		StringTokenizer bTok = new StringTokenizer(tokenSource.substring(10), ".", false);
		String bonusString = "";
		String substring = "";
		String typeSeparator = " ";
		String delim = ", ";
		String retString = "";
		if (bTok.hasMoreTokens()) {
			bonusString = bTok.nextToken();
		}
		if (bTok.hasMoreTokens()) {
			substring = bTok.nextToken();
		}
		if (bTok.hasMoreTokens()) {
			typeSeparator = bTok.nextToken();
		}
		if (bTok.hasMoreTokens()) {
			delim = bTok.nextToken();
		}
		int typeLen = bonusString.length() + substring.length() + 2;
		if (substring.length() > 0 && bonusString.length() > 0) {
			int total = (int) pc.getTotalBonusTo(bonusString, substring);
			if ("TOTAL".equals(typeSeparator)) {
				retString += total;
				return "";
			}
			boolean needDelim = false;
			String prefix = bonusString + "." + substring + ".";
			for (Iterator bi = pc.getActiveBonusMap().keySet().iterator(); bi.hasNext();) {
				String aKey = bi.next().toString();
				if (aKey.startsWith(prefix)) {
					if (needDelim) {
						retString += delim;
					}
					if (aKey.length() > typeLen) {
						retString += aKey.substring(typeLen);
					}
					else {
						retString += "None";
					}
					retString += typeSeparator;
					retString += pc.getActiveBonusMap().get(aKey);
					needDelim = true;
				}
			}
		}
		return retString;
	}
}


