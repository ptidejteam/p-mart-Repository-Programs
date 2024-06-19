/*
 * AttackToken.java
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
import pcgen.util.Delta;
import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;

//ATTACK.GRAPPLE.BASE
//ATTACK.GRAPPLE.EPIC
//ATTACK.GRAPPLE.MISC
//ATTACK.GRAPPLE.SIZE
//ATTACK.GRAPPLE.STAT
//ATTACK.GRAPPLE.TOTAL
//ATTACK.MELEE.BASE
//ATTACK.MELEE.EPIC
//ATTACK.MELEE.MISC
//ATTACK.MELEE.SIZE
//ATTACK.MELEE.STAT
//ATTACK.MELEE.TOTAL
//ATTACK.RANGED.BASE
//ATTACK.RANGED.EPIC
//ATTACK.RANGED.MISC
//ATTACK.RANGED.SIZE
//ATTACK.RANGED.STAT
//ATTACK.RANGED.TOTAL
//ATTACK.UNARMED.BASE
//ATTACK.UNARMED.EPIC
//ATTACK.UNARMED.SIZE
//ATTACK.UNARMED.TOTAL
public class AttackToken extends Token {
	public static final String TOKENNAME = "ATTACK";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		if(aTok.hasMoreTokens()) {
			String attackType = aTok.nextToken();
			String modifier = "";
			try {
				modifier = aTok.nextToken();
			}
			catch(Exception e) {
			}
			if(attackType.equals("GRAPPLE")) {
				retString = getGrappleToken(pc, modifier);
			}
			else if(attackType.equals("MELEE")) {
				retString = getMeleeToken(pc, modifier);
			}
			else if(attackType.equals("RANGED")) {
				retString = getRangedToken(pc, modifier);
			}
			else if(attackType.equals("UNARMED")) {
				retString = getUnarmedToken(pc, modifier);
			}
		}
		return retString;
	}

	public static String getGrappleToken(PlayerCharacter pc, String modifier) {
		if(modifier.equals("BASE")) {
			return getGrappleBaseToken(pc) + "";
		}
		else if(modifier.equals("EPIC")) {
			return getGrappleEpicToken(pc) + "";
		}
		else if(modifier.equals("MISC")) {
			return Delta.toString(getGrappleMiscToken(pc));
		}
		else if(modifier.equals("SIZE")) {
			return Delta.toString(getGrappleSizeToken(pc));
		}
		else if(modifier.equals("STAT")) {
			return getGrappleStatToken(pc) + "";
		}
		else if(modifier.equals("TOTAL")) {
			return getGrappleTotalToken(pc);
		}
		else {
			return pc.getAttackString(Constants.ATTACKSTRING_MELEE);
		}
	}
	public static int getGrappleBaseToken(PlayerCharacter pc) {
		return pc.baseAttackBonus();
	}

	public static int getGrappleEpicToken(PlayerCharacter pc) {
		return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
	}

	public static int getGrappleMiscToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.GRAPPLE") -
		 (int) pc.getStatBonusTo("TOHIT", "TYPE.GRAPPLE") -
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
		int miscBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.GRAPPLE") -
		 (int) pc.getStatBonusTo("COMBAT", "TOHIT.GRAPPLE") -
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") -
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT.GRAPPLE") -
		 (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
		return miscBonus + tohitBonus;
	}

	public static int getGrappleSizeToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE.GRAPPLE");
		int sizeBonus =
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT.GRAPPLE");
		return sizeBonus + tohitBonus;
	}

	public static int getGrappleStatToken(PlayerCharacter pc) {
		final int tohitBonus = (int) pc.getStatBonusTo("TOHIT", "TYPE.GRAPPLE");
		final int statBonus = (int) pc.getStatBonusTo("COMBAT", "TOHIT.GRAPPLE");
		return statBonus + tohitBonus;
	}

	public static String getGrappleTotalToken(PlayerCharacter pc) {
		final int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.GRAPPLE");
		final int totalBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.GRAPPLE");
		return pc.getAttackString(Constants.ATTACKSTRING_MELEE, totalBonus + tohitBonus);
	}

	public static String getMeleeToken(PlayerCharacter pc, String modifier) {
		if(modifier.equals("BASE")) {
			return getMeleeBaseToken(pc) + "";
		}
		else if(modifier.equals("EPIC")) {
			return getMeleeEpicToken(pc) + "";
		}
		else if(modifier.equals("MISC")) {
			return Delta.toString(getMeleeMiscToken(pc));
		}
		else if(modifier.equals("SIZE")) {
			return Delta.toString(getMeleeSizeToken(pc));
		}
		else if(modifier.equals("STAT")) {
			return Delta.toString(getMeleeStatToken(pc));
		}
		else if(modifier.equals("TOTAL")) {
			return getMeleeTotalToken(pc);
		}
		else {
			return pc.getAttackString(Constants.ATTACKSTRING_MELEE);
		}
	}

	public static int getMeleeBaseToken(PlayerCharacter pc) {
		return pc.baseAttackBonus();
	}

	public static int getMeleeEpicToken(PlayerCharacter pc) {
		return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
	}

	public static int getMeleeMiscToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.MELEE") -
		 (int) pc.getStatBonusTo("TOHIT", "TYPE.MELEE") -
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
		int miscBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.MELEE") -
		 (int) pc.getStatBonusTo("COMBAT", "TOHIT.MELEE") -
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") -
		 (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
		return miscBonus + tohitBonus;
	}

	public static int getMeleeSizeToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE.MELEE");
		int sizeBonus =
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT.MELEE");
		return sizeBonus + tohitBonus;
	}

	public static int getMeleeStatToken(PlayerCharacter pc) {
		int tohitBonus = (int) pc.getStatBonusTo("TOHIT", "TYPE.MELEE");
		int statBonus = (int) pc.getStatBonusTo("COMBAT", "TOHIT.MELEE");
		return statBonus + tohitBonus;
	}

	public static String getMeleeTotalToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.MELEE");
		int totalBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.MELEE");
		return pc.getAttackString(Constants.ATTACKSTRING_MELEE, totalBonus + tohitBonus);
	}

	public static String getRangedToken(PlayerCharacter pc, String modifier) {
		if(modifier.equals("BASE")) {
			return getRangedBaseToken(pc) + "";
		}
		else if(modifier.equals("EPIC")) {
			return getRangedEpicToken(pc) + "";
		}
		else if(modifier.equals("MISC")) {
			return Delta.toString(getRangedMiscToken(pc));
		}
		else if(modifier.equals("SIZE")) {
			return Delta.toString(getRangedSizeToken(pc));
		}
		else if(modifier.equals("STAT")) {
			return Delta.toString(getRangedStatToken(pc));
		}
		else if(modifier.equals("TOTAL")) {
			return getRangedTotalToken(pc);
		}
		else {
			return pc.getAttackString(Constants.ATTACKSTRING_RANGED);
		}
	}

	public static int getRangedBaseToken(PlayerCharacter pc) {
		return pc.baseAttackBonus();
	}

	public static int getRangedEpicToken(PlayerCharacter pc) {
		return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
	}

	public static int getRangedMiscToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.RANGED") -
		 (int) pc.getStatBonusTo("TOHIT", "TYPE.RANGED") -
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
		int miscBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.RANGED") -
		 (int) pc.getStatBonusTo("COMBAT", "TOHIT.RANGED") -
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") -
		 (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
		return miscBonus + tohitBonus;
	}

	public static int getRangedSizeToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE.RANGED");
		int sizeBonus =
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT.RANGED");
		return sizeBonus + tohitBonus;
	}

	public static int getRangedStatToken(PlayerCharacter pc) {
		int tohitBonus = (int) pc.getStatBonusTo("TOHIT", "TYPE.RANGED");
		int statBonus = (int) pc.getStatBonusTo("COMBAT", "TOHIT.RANGED");
		return statBonus + tohitBonus;
	}

	public static String getRangedTotalToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.RANGED");
		int totalBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.RANGED");
		return pc.getAttackString(Constants.ATTACKSTRING_MELEE, totalBonus + tohitBonus);
	}

	public static String getUnarmedToken(PlayerCharacter pc, String modifier) {
		if(modifier.equals("BASE")) {
			return getRangedBaseToken(pc) + "";
		}
		else if(modifier.equals("EPIC")) {
			return getRangedEpicToken(pc) + "";
		}
		else if(modifier.equals("SIZE")) {
			return Delta.toString(getRangedSizeToken(pc));
		}
		else if(modifier.equals("TOTAL")) {
			return getRangedTotalToken(pc);
		}
		else {
			return pc.getAttackString(Constants.ATTACKSTRING_UNARMED);
		}
	}

	public static int getUnarmedBaseToken(PlayerCharacter pc) {
		return pc.baseAttackBonus();
	}

	public static int getUnarmedEpicToken(PlayerCharacter pc) {
		return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
	}

	public static int getUnarmedSizeToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE.MELEE");
		int sizeBonus =
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT.MELEE");
		return  sizeBonus + tohitBonus;
	}

	public static String getUnarmedTotalToken(PlayerCharacter pc) {
		int tohitBonus =
		 (int) pc.getTotalBonusTo("TOHIT", "TOHIT") +
		 (int) pc.getTotalBonusTo("TOHIT", "TYPE.MELEE");
		int totalBonus =
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT") +
		 (int) pc.getTotalBonusTo("COMBAT", "TOHIT.MELEE");
		return pc.getAttackString(Constants.ATTACKSTRING_UNARMED, totalBonus + tohitBonus);
	}
}


