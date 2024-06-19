/*
 * EQSetToken.java
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

import pcgen.core.PlayerCharacter;

//EQSET.START
//EQSET.END
//EQSET.NUMBER
//EQSET.NAME
public class EQSetToken extends Token {
	public static final String TOKENNAME = "EQSET";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		if ("EQSET.START".equals(tokenSource)) {
			//TODO: Does Nothing here, only on EQSheet exports, should that be fixed?
		}
		else if ("EQSET.END".equals(tokenSource)) {
			//TODO: Does Nothing here, only on EQSheet exports, should that be fixed?
		}
		else if ("EQSET.NUMBER".equals(tokenSource)) {
			retString = getNumberToken(pc) + "";
		}
		else if ("EQSET.NAME".equals(tokenSource)) {
			retString = getNameToken(pc);
		}
		return retString;
	}

	public static int getNumberToken(PlayerCharacter pc) {
		return pc.getEquipSetNumber();
	}

	public static String getNameToken(PlayerCharacter pc) {
		return pc.getCurrentEquipSetName();
	}
}

