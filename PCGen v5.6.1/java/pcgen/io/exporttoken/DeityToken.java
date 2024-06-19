/*
 * DeityToken.java
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

import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.core.utils.Utility;

//DEITY (Defaults to OUTPUTNAME)
//DEITY.NAME
//DIETY.OUTPUTNAME
//DIETY.DOMAINLIST
//DIETY.FOLLOWERALIGNMENT
//DIETY.ALIGNMENT
//DIETY.DESCRIPTION
//DIETY.HOLYITEM
//DIETY.FAVOREDWEAPON
//DIETY.PANTHEONLIST
//DIETY.SOURCE
//DIETY.SA
public class DeityToken extends Token {
	public static final String TOKENNAME = "DEITY";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		if (pc.getDeity() != null) {
			StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
			String subTag = "OUTPUTNAME";
			Deity deity = pc.getDeity();
			if (aTok.countTokens() > 1) {
				aTok.nextToken();
				subTag = aTok.nextToken();
			}
			if ("NAME".equals(subTag)) {
				retString = getNameToken(deity);
			}
			else if ("OUTPUTNAME".equals(subTag)) {
				retString = getOutputNameToken(deity);
			}
			else if ("DOMAINLIST".equals(subTag)) {
				retString = getDomainListToken(deity);
			}
			else if ("FOLLOWERALIGNMENT".equals(subTag)) {
				retString = getFollowerAlignmentToken(deity);
			}
			else if ("ALIGNMENT".equals(subTag)) {
				retString = getAlignmentToken(deity);
			}
			else if ("DESCRIPTION".equals(subTag)) {
				retString = getDescriptionToken(deity);
			}
			else if ("HOLYITEM".equals(subTag)) {
				retString = getHolyItemToken(deity);
			}
			else if ("FAVOREDWEAPON".equals(subTag)) {
				retString = getFavoredWeaponToken(deity);
			}
			else if ("PANTHEONLIST".equals(subTag)) {
				retString = getPantheonListToken(deity);
			}
			else if ("SOURCE".equals(subTag)) {
				retString = getSourceToken(deity);
			}
			else if ("SA".equals(subTag)) {
				retString = getSAToken(deity);
			}
		}
		return retString;
	}

	public static String getNameToken(Deity deity) {
		return deity.getName();
	}

	public static String getOutputNameToken(Deity deity) {
		return deity.getOutputName();
	}

	public static String getDomainListToken(Deity deity) {
		String retString = "";
		List dList = deity.getDomainList();
		boolean firstLine = true;
		for (int i = 0; i < deity.getDomainList().size(); i++) {
			if (!firstLine) {
				retString += ", ";
			}
			firstLine = false;

			retString += ((Domain) deity.getDomainList().get(i)).getName();
		}
		return retString;
	}

	public static String getFollowerAlignmentToken(Deity deity) {
		String retString = "";
		boolean firstLine = true;
		String fAlignment = deity.getFollowerAlignments();
		for (int i = 0; i < fAlignment.length(); i++) {
			if (!firstLine) {
				retString += ", ";
			}
			firstLine = false;

			retString += SystemCollections.getShortAlignmentAtIndex((int) fAlignment.charAt(i) - 48);
		}
		return retString;
	}

	public static String getAlignmentToken(Deity deity) {
		return deity.getAlignment();
	}

	public static String getDescriptionToken(Deity deity) {
		return deity.getDescription();
	}

	public static String getHolyItemToken(Deity deity) {
		return deity.getHolyItem();
	}

	public static String getFavoredWeaponToken(Deity deity) {
		return deity.getFavoredWeapon();
	}

	public static String getPantheonListToken(Deity deity) {
		return Utility.join(deity.getPantheonList(), ", ");
	}

	public static String getSourceToken(Deity deity) {
		return deity.getSource();
	}

	public static String getSAToken(Deity deity) {
		if (deity.getSpecialAbilityList() != null) {
			return Utility.join(deity.getSpecialAbilityList(), ", ");
		}
		return "";
	}
}

