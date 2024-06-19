/*
 * KitProf.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 28, 2002, 11:50 PM
 *
 * $Id: KitProf.java,v 1.1 2006/02/21 01:08:01 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <code>KitFeat</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class KitProf extends BaseKit implements Serializable
{
	private final ArrayList profList = new ArrayList();
	private boolean racialProf = false;

	private static final long serialVersionUID = 1;

	public KitProf(String argProfList)
	{
		final StringTokenizer aTok = new StringTokenizer(argProfList, "|");
		while (aTok.hasMoreTokens())
		{
			profList.add(aTok.nextToken());
		}
	}

	public String toString()
	{
		final int maxSize = profList.size();
		final StringBuffer info = new StringBuffer(maxSize * 10);
		if ((choiceCount != 1) || (maxSize != 1))
		{
			info.append(choiceCount).append(" of ");
		}
		for (int i = 0; i < maxSize; ++i)
		{
			if (i != 0)
			{
				info.append(", ");
			}
			info.append((String) profList.get(i));
		}
		return info.toString();
	}

	public ArrayList getProfList()
	{
		return profList;
	}

	public boolean isRacial()
	{
		return racialProf;
	}

	public void setRacialProf(boolean argRacial)
	{
		racialProf = argRacial;
	}
}
