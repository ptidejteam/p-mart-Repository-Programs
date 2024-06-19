/*
 * HPRollToken.java
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
import pcgen.core.PCClass;

// HPROLL.x
// HPROLL.x.ROLL
// HPROLL.x.STAT
// HPROLL.x.TOTAL
public class HPRollToken extends Token {
	public static final String TOKENNAME = "HPROLL";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		String bString;
		String retString = "";
		bString = aTok.nextToken();
		int levelOffset = Integer.valueOf(aTok.nextToken()).intValue() - 1;

		if (aTok.hasMoreTokens()) {
			bString = aTok.nextToken();
		}
		if (bString.startsWith("HPROLL")) {
			bString = "ROLL";
		}
		if ((levelOffset >= pc.getLevelInfoSize()) || (levelOffset < 0)) {
			return "0";
		}

		if ("ROLL".equals(bString)) {
			retString = getRollToken(pc, levelOffset) + "";
		}
		else if ("STAT".equals(bString)) {
			retString = getStatToken(pc, levelOffset) + "";
		}
		else if ("TOTAL".equals(bString)) {
			retString = getTotalToken(pc, levelOffset) + "";
		}
		return retString;
	}

	public static int getHPRollToken(PlayerCharacter pc, int level) {
		return getRollToken(pc, level);
	}

	public static int getRollToken(PlayerCharacter pc, int level) {
		int classLevel = pc.getLevelInfoClassLevel(level) - 1;
		int hpRoll = 0;

		PCClass pcClass = pc.getClassKeyed(pc.getLevelInfoClassKeyName(level));
		if (pcClass != null) {
			hpRoll = pcClass.getHitPoint(classLevel).intValue();
		}

		return hpRoll;
	}

	public static int getStatToken(PlayerCharacter pc, int level) {
		return (int) pc.getStatBonusTo("HP", "BONUS");
	}

	public static int getTotalToken(PlayerCharacter pc, int level) {
		return getRollToken(pc, level) + getStatToken(pc, level);
	}
}

