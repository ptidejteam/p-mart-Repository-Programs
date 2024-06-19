/*
 * BonusToken.java
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
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.PObject;
import pcgen.core.utils.Utility;

//BONUS
public class BonusToken extends Token {
	public static final String TOKENNAME = "BONUS";

	public String getTokenName() {
		return TOKENNAME;
	}

	//TODO: Bonuses need to be stripped out, and there need to be methods for the various types.
	public String getToken(String tokenSource, PlayerCharacter pc) {
		return getBonusToken(tokenSource, pc);
	}

	public static String getBonusToken(String tokenSource, PlayerCharacter pc) {
		StringTokenizer bonusTok = new StringTokenizer(tokenSource, ".", false);

		// tokenSource should follow this format:
		//  BONUS.COMBAT.AC.TOTAL
		// or
		//  BONUS.COMBAT.AC.Armor
		// or
		//  BONUS.COMBAT.AC.TOTAL.!BASE.!Armor.!Ability.!Size

		// First token should be: BONUS
		bonusTok.nextToken();
		// next should be category of bonus: COMBAT
		final String aType = bonusTok.nextToken();
		// next should be name of bonus: AC
		final String aName = bonusTok.nextToken();

		double total = 0;
		int decimals = 0;
		double lastValue = 0;
		int signIt = 1;

		while (bonusTok.hasMoreTokens()) {
			String bucket = bonusTok.nextToken();
			if (Utility.doublesEqual(total, 0.0) && "LISTING".equals(bucket)) {
				return pc.listBonusesFor(aType + "." + aName);
			}
			if ((bucket.startsWith("PRE") || bucket.startsWith("!PRE")) && bucket.indexOf(':') >= 0) {
				PObject newPO = new PObject();
				newPO.addPreReq(bucket);
				if (!newPO.passesPreReqToGain()) {
					total -= lastValue * signIt;
					lastValue = 0;
				}
				continue;
			}
			if (bucket.startsWith("MIN=")) {
				double x = Float.parseFloat(bucket.substring(4));
				if (lastValue < x) {
					total -= lastValue - x;
				}
				continue;
			}
			else if (bucket.startsWith("MAX=")) {
				double x = Float.parseFloat(bucket.substring(4));
				x = Math.min(x, lastValue);
				total -= lastValue - x;
				lastValue = 0;
				continue;
			}
			signIt = 1;
			if ((bucket.length() > 0) && (bucket.charAt(0) == '!')) {
				signIt = -1;
				bucket = bucket.substring(1);
			}
			if (bucket.equals("EQTYPE") && bonusTok.hasMoreTokens()) {
				bucket += "." + bonusTok.nextToken();
			}
			if ("TOTAL".equals(bucket)) {
				lastValue = pc.getTotalBonusTo(aType, aName);
			}
			else if (bucket.startsWith("DEC=")) {
				decimals = Integer.parseInt(bucket.substring(4));
			}
			else if (bucket.startsWith("TYPE=") || bucket.startsWith("EQTYPE.")) {
				lastValue = 0;
				String restOfBucket;
				if (bucket.startsWith("TYPE=")) {
					restOfBucket = bucket.substring(5);
				}
				else {
					restOfBucket = bucket.substring(7);
				}
				List aList = pc.getEquipmentOfType(restOfBucket, "", 1);
				if (!aList.isEmpty()) {
					for (Iterator e = aList.iterator(); e.hasNext();) {
						Equipment eq = (Equipment) e.next();
						lastValue += eq.bonusTo(aType, aName, true);
					}
				}
			}
			else {
				lastValue = pc.getBonusDueToType(aType, aName, bucket);
			}
			total += lastValue * signIt;
		}
		return String.valueOf((int) (total * Math.pow(10, decimals)) / (int) Math.pow(10, decimals));
	}
}

