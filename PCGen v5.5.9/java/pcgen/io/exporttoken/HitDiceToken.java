/*
 * HitDiceToken.java
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
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.util.Delta;

//HITDICE
public class HitDiceToken extends Token {
	public static final String TOKENNAME = "HITDICE";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		return getHitDiceToken(pc);
	}

	public static String getHitDiceToken(PlayerCharacter pc) {
		String retString = "";
		String del = "";
		if (pc.getRace().hitDice() > 0) {
			retString += "(" + Integer.toString(pc.getRace().hitDice()) + "d" +
			 Integer.toString(pc.getRace().getHitDiceSize()) + ")";
			del = "+";
		}

		PCClass aClass;
		String aaClassLevel;
		Integer aClassHitDie;
		String aaCLassHitDie;

		for (Iterator it = pc.getClassList().iterator(); it.hasNext();) {
			aClass = (PCClass) it.next();
			aaClassLevel = Integer.toString(aClass.getLevel());
			aClassHitDie = new Integer(aClass.getHitDie());
			aaCLassHitDie = aClassHitDie.toString();

			retString += del + "(" + aaClassLevel + "d" + aaCLassHitDie + ")";
			del = "+";
		}

		//
		// Get CON bonus contribution to hitpoint total
		//
		int temp = (int) pc.getStatBonusTo("HP", "BONUS");
		temp *= (pc.getTotalLevels() + pc.getRace().hitDice());
		//
		// Add in feat bonus
		//
		temp += (int) pc.getTotalBonusTo("HP", "CURRENTMAX");
		if (temp != 0) {
			retString += Delta.toString(temp);
		}
		return retString;
	}
}


