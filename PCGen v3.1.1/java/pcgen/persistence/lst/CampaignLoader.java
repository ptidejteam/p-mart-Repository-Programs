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
 * $Id: CampaignLoader.java,v 1.1 2006/02/21 00:05:41 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.Properties;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
class CampaignLoader
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
		else if (inputLine.startsWith("INFOTEXT:"))
		{
			campaign.setInfoText(inputLine.substring(9));
		}
		else if (inputLine.startsWith(Constants.s_TAG_TYPE))
		{
			campaign.setType(inputLine.substring(Constants.s_TAG_TYPE.length()));
		}
		else if (inputLine.startsWith("COPYRIGHT:"))
		{
			if (!campaign.getCopyrightFlag())
			{
				Globals.section15.append("<br><b>Source Material:</b>").append(campaign.getSourceInForm(Constants.SOURCELONG)+"<br>");
				Globals.section15.append("<b>Section 15 Entry in Source Material:</b><br>");
				campaign.setCopyrightFlag(true);
			}
			//Globals.section15.append(campaign.getSourceInForm(Constants.SOURCELONG)).append(" (c)").append(inputLine.substring(10)).append("<br>");
			Globals.section15.append(inputLine.substring(10)).append("<br>");
		}
		else if (inputLine.startsWith("SHOWINMENU:"))
		{
			campaign.setShowInMenu(Boolean.valueOf(inputLine.substring(11)).booleanValue());
		}
		else if (inputLine.startsWith("OPTION:"))
		{
			// We store a set of options with the campaign, so add this one in now.
			// That way when the campaign is selected the options can be set too.
			Properties options = campaign.getOptions();
			if (options == null)
			{
				options = new Properties();
				campaign.setOptions(options);
			}
			int equalsPos = inputLine.indexOf("=");
			if (equalsPos >= 0)
			{
				String optName = inputLine.substring(7, equalsPos);
				String optValue = inputLine.substring(equalsPos+1);
				options.setProperty(optName, optValue);
			}
			else
			{
				Globals.errorPrint("Invalid option line in source file " + sourceFile.getName() + " : " + inputLine);
			}
		}
	}
}
