/*
 * CampaignLoader.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: CampaignLoader.java,v 1.1 2006/02/20 23:54:41 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import pcgen.core.Campaign;
import pcgen.core.Constants;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class CampaignLoader
{

	/** Creates a new instance of CampaignLoader */
	private CampaignLoader()
	{
	}

	public static void parseLine(Campaign campaign, String inputLine, File sourceFile, int lineNum)
	{
		if (PObjectLoader.parseTag(campaign, inputLine))
			return;
		if (inputLine.startsWith("CAMPAIGN:"))
		{
			campaign.setName(inputLine.substring(9));
		}
		else if (inputLine.startsWith("RANK:"))
		{
			campaign.setRank(Integer.valueOf(inputLine.substring(5)));
		}
		else if (inputLine.startsWith("GAME:"))
		{
			campaign.setGame(inputLine.substring(5));
		}
		else if (inputLine.startsWith(Constants.s_TAG_TYPE))
		{
			campaign.setType(inputLine.substring(Constants.s_TAG_TYPE.length()));
		}
	}
}
