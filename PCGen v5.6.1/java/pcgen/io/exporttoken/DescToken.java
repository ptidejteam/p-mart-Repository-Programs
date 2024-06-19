/*
 * DescToken.java
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
import java.util.ArrayList;
import java.util.List;
import pcgen.core.PlayerCharacter;

//DESC
//DESC,text delimiter
public class DescToken extends Token
{
	public static final String TOKENNAME = "DESC";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		StringTokenizer tok = new StringTokenizer(tokenSource, ",", false);
		tok.nextToken();
		StringBuffer sb = new StringBuffer();
		String delim = " ";
		if(tok.hasMoreTokens())
		{
			delim = tok.nextToken();
		}

		List descList = getDescToken(pc);
		for (int i = 0; i < descList.size(); ++i)
		{
			if(i > 0)
			{
				sb.append(", ");
			}
			sb.append((String) descList.get(i));
		}
		return sb.toString();
	}

	public static List getDescToken(PlayerCharacter pc)
	{
		List descList = new ArrayList();
		StringTokenizer tok = new StringTokenizer(pc.getDescription(), "\r\n", false);

		while (tok.hasMoreTokens())
		{
			descList.add(tok.nextToken());
		}

		return descList;
	}
}

