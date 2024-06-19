/*
 * DirToken.java
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
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

//DIR.PCGEN
//DIR.TEMPLATES
//DIR.PCG
//DIR.HTML
//DIR.TEMP
public class DirToken extends Token {
	public static final String TOKENNAME = "DIR";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		String dirType = "";
		if(aTok.hasMoreTokens()) {
			dirType = aTok.nextToken();
		}

		if("PCGEN".equals(dirType)) {
			retString = getPCGenToken();
		}
		else if("TEMPLATES".equals(dirType)) {
			retString = getTemplatesToken();
		}
		else if("PCG".equals(dirType)) {
			retString = getPcgToken();
		}
		else if("HTML".equals(dirType)) {
			retString = getHtmlToken();
		}
		else if("TEMP".equals(dirType)) {
			retString = getTempToken();
		}
		else {
			Logging.errorPrint("DIR: Unknown Dir: " + dirType);
			retString = dirType;
		}
		return retString;
	}

	public static String getPCGenToken() {
		return SettingsHandler.getPcgenSystemDir().getAbsolutePath();
	}
	public static String getTemplatesToken() {
		return SettingsHandler.getPcgenOutputSheetDir().getAbsolutePath();
	}
	public static String getPcgToken() {
		return SettingsHandler.getPcgPath().getAbsolutePath();
	}
	public static String getHtmlToken() {
		return SettingsHandler.getHTMLOutputSheetPath();
	}
	public static String getTempToken() {
		return SettingsHandler.getTempPath().getAbsolutePath();
	}
}

