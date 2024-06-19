/*
 * ClassToken.java
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
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

//CLASS.x
//CLASS.x.LEVEL
//CLASS.x.SALIST
public class ClassToken extends Token  {
	public static final String TOKENNAME = "CLASS";

	public String getTokenName() {
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc) {
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		int i = 0;
		if (aTok.hasMoreTokens()) {
			i = Integer.parseInt(aTok.nextToken());
		}
		if (aTok.hasMoreTokens()) {
			String subToken = aTok.nextToken();
			if ("LEVEL".equals(subToken)) {
				int level = getLevelToken(pc, i);
				if(level > 0) {
					return level + "";
				}
				else {
					return "";
				}
			}
			else if ("SALIST".equals(subToken)) {
				return getSAListToken(pc, i);
			}
		}
		return getClassToken(pc, i);
	}

	public static String getClassToken(PlayerCharacter pc, int classNumber) {
		String retString = "";
		if(pc.getClassList().size() > classNumber) {
			PCClass pcClass = (PCClass)pc.getClassList().get(classNumber);
			if (Constants.s_NONE.equals(pcClass.getSubClassName()) || "".equals(pcClass.getSubClassName())) {
				//FileAccess.encodeWrite(output, aClass.getName());
				retString = pcClass.getOutputName();
			}
			else {
				retString = pcClass.getSubClassName();
			}
		}
		return retString;
	}

	public static int getLevelToken(PlayerCharacter pc, int classNumber) {
		if(pc.getClassList().size() > classNumber) {
			PCClass pcClass = (PCClass)pc.getClassList().get(classNumber);
			return pcClass.getLevel();
		}
		return 0;
	}

	public static String getSAListToken(PlayerCharacter pc, int classNumber) {
		String retString = "";
		if(pc.getClassList().size() > classNumber) {
			boolean firstLine = true;
			PCClass pcClass = (PCClass)pc.getClassList().get(classNumber);
			for(int i = 0; i < pcClass.getClassSpecialAbilityList().size(); i++) {
				if(!firstLine) {
					retString += ", ";
				}
				firstLine = false;

				retString += pcClass.getClassSpecialAbilityList().get(i).toString();
			}
		}
		return retString;
	}
}

