/*
 * DomainToken.java
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
import pcgen.core.Domain;
import pcgen.core.CharacterDomain;
import pcgen.core.PlayerCharacter;

//DOMAIN.x
//DOMAIN.x.POWER
public class DomainToken extends Token {
	public static final String TOKENNAME = "DOMAIN";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		if(aTok.hasMoreTokens()) {
			int domainIndex = 0;
			try {
				domainIndex = Math.max(0, Integer.parseInt(aTok.nextToken()) - 1);
			}
			catch(Exception e) {}

			if(aTok.hasMoreTokens()) {
				String subToken = aTok.nextToken();
				if("POWER".equals(subToken)) {
					retString = getPowerToken(pc, domainIndex);
				}
			}
			else {
				retString = getDomainToken(pc, domainIndex);
			}

		}
		return retString;
	}

	public static String getDomainToken(PlayerCharacter pc, int domainIndex) {
		try {
			Domain domain = ((CharacterDomain) pc.getCharacterDomainList().get(domainIndex)).getDomain();
			return domain.getOutputName();
		}
		catch(Exception e) {
			return "";
		}
	}

	public static String getPowerToken(PlayerCharacter pc, int domainIndex) {
		try {
			Domain domain = ((CharacterDomain) pc.getCharacterDomainList().get(domainIndex)).getDomain();
			return domain.piDescString();
		}
		catch(Exception e) {
			return "";
		}
	}
}

