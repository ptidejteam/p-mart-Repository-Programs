/*
 * NoteToken.java
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
import pcgen.core.NoteItem;

//NOTE
public class NoteToken extends Token
{
	public static final String TOKENNAME = "NOTE";

	public String getTokenName()
	{
		return TOKENNAME;
	}

	public String getToken(String tokenSource, PlayerCharacter pc)
	{
		StringTokenizer tok = new StringTokenizer(tokenSource, ".");
		tok.nextToken();
		StringBuffer sb = new StringBuffer();

		String name = tok.nextToken();
		List noteList = getNoteList(pc, name);

		String beforeHeader = "<b>";
		String afterHeader = "</b><br>";
		String beforeValue = "";
		String afterValue = "<br>";
		String token = "ALL";

		if(tok.hasMoreTokens())
		{
			beforeHeader = tok.nextToken();
			if ("NAME".equals(beforeHeader))
			{
				token = "NAME";
				beforeHeader = afterHeader = beforeValue = afterValue = "";
				if (tok.hasMoreTokens() && !"ALL".equals(token))
				{
					beforeHeader = tok.nextToken();
				}
				if (tok.hasMoreTokens())
				{
					afterHeader = tok.nextToken();
				}
			}
			else if ("VALUE".equals(beforeHeader))
			{
				token = "VALUE";
				beforeHeader = afterHeader = beforeValue = afterValue = "";
				if (tok.hasMoreTokens())
				{
					beforeValue = tok.nextToken();
				}
				if (tok.hasMoreTokens())
				{
					afterValue = tok.nextToken();
				}
			}
			else if ("ALL".equals(beforeHeader))
			{
				token = "ALL";
				if (tok.hasMoreTokens())
				{
					beforeHeader = tok.nextToken();
				}
				if (tok.hasMoreTokens())
				{
					afterHeader = tok.nextToken();
				}
				if (tok.hasMoreTokens())
				{
					beforeValue = tok.nextToken();
				}
				if (tok.hasMoreTokens())
				{
					afterValue = tok.nextToken();
				}
			}
		}

		for (int i = 0; i < noteList.size(); i++)
		{
			NoteItem ni = (NoteItem) noteList.get(i);

			if ("ALL".equals(token))
			{
				sb.append(ni.getExportString(beforeHeader, afterHeader, beforeValue, afterValue));
			}
			else if ("NAME".equals(token))
			{
				sb.append(ni.getName());
			}
			else if ("VALUE".equals(token))
			{
				StringTokenizer cTok = new StringTokenizer(ni.getValue(), "\r\n");

				while (cTok.hasMoreTokens())
				{
					sb.append(beforeValue);
					sb.append(cTok.nextToken());
					sb.append(afterValue);
				}
			}
		}

		return sb.toString();
	}

	public static List getNoteList(PlayerCharacter pc, String name) {
		List noteList = new ArrayList();

		if ("ALL".equals(name))
		{
			noteList = (ArrayList) pc.getNotesList().clone();
		}
		else
		{
			try
			{
				int i = Integer.parseInt(name);

				if ((i >= 0) || (i < pc.getNotesList().size()))
				{
					noteList.add(pc.getNotesList().get(i));
				}
			}
			catch (NumberFormatException e)
			{
				noteList = (ArrayList) pc.getNotesList().clone();

				for (int i = noteList.size() - 1; i >= 0; --i)
				{
					NoteItem ni = (NoteItem) noteList.get(i);

					if (!ni.getName().equalsIgnoreCase(name))
					{
						noteList.remove(i);
					}
				}
			}
		}
		return noteList;
	}
}

