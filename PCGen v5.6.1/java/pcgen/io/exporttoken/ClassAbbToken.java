/*
 * ClassAbbToken.java
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
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

//CLASSABB.x
public class ClassAbbToken extends Token  {
	public static final String TOKENNAME = "CLASSABB";

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

		return getClassAbbToken(pc, i);
	}

	public static String getClassAbbToken(PlayerCharacter pc, int classNumber) {
		String retString = "";
		if(pc.getClassList().size() > classNumber) {
			PCClass pcClass = (PCClass)pc.getClassList().get(classNumber);
			String subClassName = pcClass.getDisplayClassName();
			if (!pcClass.getName().equals(subClassName)) {
				PCClass subClass = pcClass.getSubClassNamed(subClassName);
				if (subClass != null) {
					pcClass = subClass;
				}
			}
			retString = pcClass.getAbbrev();
		}
		return retString;
	}
}

