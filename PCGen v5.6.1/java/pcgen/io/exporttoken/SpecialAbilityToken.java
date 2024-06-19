/*
 * SpecialAbilityToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
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
import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

//SPECIALABILITY.x
//SPECIALABILITY.x.DESCRIPTION
public class SpecialAbilityToken extends Token
{
	public static final String TOKENNAME = "SPECIALABILITY";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		int i = 0;
		if (aTok.hasMoreTokens())
		{
			i = Integer.parseInt(aTok.nextToken());
		}

		if (aTok.hasMoreTokens())
		{
			String subToken = aTok.nextToken();

			if ("DESCRIPTION".equals(subToken))
			{
				return getDescriptionToken(pc, i);
			}
		}
		return getSpecialAbilityToken(pc, i);
	}

	public static String getSpecialAbilityToken(PlayerCharacter pc, int specialIndex)
	{
		if(specialIndex >=0 && specialIndex < pc.getSpecialAbilityTimesList().size())
		{
			return pc.getSpecialAbilityTimesList().get(specialIndex).toString();
		}
		return "";
	}

	public static String getDescriptionToken(PlayerCharacter pc, int specialIndex)
	{
		if(specialIndex >=0 && specialIndex < pc.getSpecialAbilityTimesList().size())
		{
			if (SettingsHandler.isROG())
			{
				if ("EMPTY".equals(pc.getDescriptionLst()))
				{
					pc.loadDescriptionFilesInDirectory("descriptions");
				}

				String description = "";
				String search = "SA" + ":" + pc.getSpecialAbilityTimesList().get(specialIndex).toString() + Constants.s_LINE_SEP;
				int pos = pc.getDescriptionLst().indexOf(search);

				if (pos >= 0)
				{
					description = pc.getDescriptionLst().substring(pos + search.length());
					description = description.substring(0, description.indexOf("####") - 1).trim();
				}

				return description;
			}
		}
		return "";
	}
}

