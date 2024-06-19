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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:47 $
 *
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.Properties;
import pcgen.core.Campaign;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class CampaignLoader
{

	/** Creates a new instance of CampaignLoader */
	private CampaignLoader()
	{
	}

	public static void parseLine(Campaign campaign, String inputLine, URL sourceURL)
	{
		if (campaign == null)
		{
			return;
		}

		if (PObjectLoader.parseTag(campaign, inputLine))
		{
			return;
		}
		if (inputLine.startsWith("BOOKTYPE:"))
		{
			campaign.setBookType(inputLine.substring(9));
		}
		else if (inputLine.startsWith("CAMPAIGN:"))
		{
			campaign.setName(inputLine.substring(9));
		}
		else if (inputLine.startsWith("COPYRIGHT:"))
		{
			campaign.addSection15Info(inputLine.substring(10));
		}
		else if (inputLine.startsWith("GAME:"))
		{
			String s = inputLine.substring(5);
			if (s.equals("DnD"))
				s = "3e";
			campaign.setGame(s);
		}
		else if (inputLine.startsWith("GAMEMODE:"))
		{
			String s = inputLine.substring(9);
			if (s.equals("DnD"))
				s = "3e";
			campaign.setGame(s);
		}
		else if (inputLine.startsWith("GENRE:"))
		{
			campaign.setGenre(inputLine.substring(6));
		}
		else if (inputLine.startsWith("INFOTEXT:"))
		{
			campaign.setInfoText(inputLine.substring(9));
		}
		else if (inputLine.startsWith("ISOGL:"))
		{
			campaign.setIsOGL(inputLine.charAt(6) == 'Y');
		}
		else if (inputLine.startsWith("ISD20:"))
		{
			campaign.setIsD20(inputLine.charAt(6) == 'Y');
		}
		else if (inputLine.startsWith("ISLICENSED:"))
		{
			campaign.setIsLicensed(inputLine.charAt(11) == 'Y');
		}
		else if (inputLine.startsWith("LICENSED:"))
		{
			campaign.setIsLicensed(inputLine.charAt(9) == 'Y');
		}
		else if (inputLine.startsWith("LICENSE:"))
		{
			campaign.addLicense(inputLine.substring(8));
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
			final int equalsPos = inputLine.indexOf("=");
			if (equalsPos >= 0)
			{
				String optName = inputLine.substring(7, equalsPos);
				if (optName.toLowerCase().startsWith("pcgen.options."))
				{
					optName = optName.substring("pcgen.options.".length());
				}
				final String optValue = inputLine.substring(equalsPos + 1);
				options.setProperty(optName, optValue);
			}
			else
			{
				Logging.errorPrint("Invalid option line in source file " + sourceURL.toString() + " : " + inputLine);
			}
		}
		else if (inputLine.startsWith("PUBNAMELONG:"))
		{
			campaign.setPubNameLong(inputLine.substring(12));
		}
		else if (inputLine.startsWith("PUBNAMESHORT:"))
		{
			campaign.setPubNameShort(inputLine.substring(13));
		}
		else if (inputLine.startsWith("PUBNAMEWEB:"))
		{
			campaign.setPubNameWeb(inputLine.substring(11));
		}
		else if (inputLine.startsWith("RANK:"))
		{
			campaign.setRank(Integer.valueOf(inputLine.substring(5)));
		}
		else if (inputLine.startsWith("SETTING:"))
		{
			campaign.setSetting(inputLine.substring(8));
		}
		else if (inputLine.startsWith("SHOWINMENU:"))
		{
			campaign.setShowInMenu(new Boolean(inputLine.substring(11)).booleanValue());
		}
		else if (inputLine.length() > 1)
		{
			campaign.addLine(inputLine);
		}
	}
}
