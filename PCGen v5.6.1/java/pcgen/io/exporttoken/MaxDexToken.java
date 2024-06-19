/*
 * MaxDexToken.java
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

import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.util.Delta;

//MAXDEX
public class MaxDexToken extends Token  {
	public static final String TOKENNAME = "MAXDEX";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		return getMaxDexToken(tokenSource, pc);
	}

	//TODO: Rip the processing of this token out of PlayerCharacter
	public static String getMaxDexToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		int mod = pc.modToFromEquipment(tokenSource);
		if (mod != Constants.MAX_MAXDEX) {
			retString = Delta.toString(mod);
		}
		return retString;
	}
}

