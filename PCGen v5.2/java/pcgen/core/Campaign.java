/*
 * Campaign.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import pcgen.util.GuiFacade;

/**
 * <code>Campaign</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Campaign extends PObject
{
	private boolean isLoaded = false;
	private Integer rank = new Integer(9);
	private String game = "";
	private String infoText = "";
	private StringBuffer section15Text = null;
	String pubNameLong = "";
	String pubNameShort = "";
	String pubNameWeb = "";
	private boolean showInMenu = false;
	private boolean isD20 = false;
	private boolean isOGL = false;
	private boolean isLicensed = false;
	private Properties options = null;
	private StringBuffer licenseText = null;
	private String destination = ""; // path to pcc file relative to data directory
	private String bookType = "";
	private List lines = new ArrayList();
	private Map publisherMap = new HashMap();
	private List licenseFiles = new ArrayList();
	private String setting = "";
	private String genre = "";

	public Object clone()
	{
		Campaign aObj = null;
		try
		{
			aObj = (Campaign) super.clone();
			aObj.isLoaded = isLoaded;
			aObj.rank = rank;
			aObj.game = game;
			aObj.infoText = infoText;
			aObj.section15Text = section15Text;
			aObj.showInMenu = showInMenu;
			aObj.isD20 = isD20;
			aObj.isOGL = isOGL;
			aObj.isLicensed = isLicensed;
			aObj.options = (Properties) options.clone();
			aObj.licenseText = licenseText;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		finally
		{
			return aObj;
		}
	}

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * Sets whether the campaign is loaded.
	 * @param loaded
	 */
	public void setIsLoaded(boolean loaded)
	{
		isLoaded = loaded;
	}

	/**
	 * @return the 'load rank' of the campaign.
	 */
	public Integer getRank()
	{
		return rank;
	}

	/**
	 * Sets the 'load rank' of the campaign.
	 * @param argRank the wanted load rank
	 */
	public void setRank(Integer argRank)
	{
		rank = argRank;
	}

	/**
	 * Returns the name of the game this campaign is intended for.
	 * @return the name of the game
	 */
	public String getGame()
	{
		return game;
	}

	/**
	 * Sets the name of the game this campaign is intended for.
	 * @param argGame the game
	 */
	public void setGame(String argGame)
	{
		game = argGame;
	}

	/**
	 * Returns whether this campaign should be listed in the campaigns menu
	 * @return true if this campaign should be listed in the campaigns menu
	 */
	public boolean canShowInMenu()
	{
		return showInMenu;
	}

	/**
	 * Sets whether this campaign should be listed in the campaigns menu.
	 */
	public void setShowInMenu(boolean argShow)
	{
		showInMenu = argShow;
	}

	/**
	 * Returns whether this campaign is licensed
	 * @return true if this campaign is licensed
	 */
	public boolean isLicensed()
	{
		return isLicensed;
	}

	/**
	 * Sets whether this campaign is licensed.
	 */
	public void setIsLicensed(boolean argShow)
	{
		isLicensed = argShow;
	}

	/**
	 * @return the options which are to apply to this campaign
	 */
	public Properties getOptions()
	{
		return options;
	}

	public void setOptions(Properties options)
	{
		this.options = options;
	}

	/**
	 * @return the info on this campaign
	 */
	public String getInfoText()
	{
		return infoText;
	}

	public void setInfoText(String info)
	{
		infoText = info;
	}

	public void setIsD20(boolean argIsD20)
	{
		isD20 = argIsD20;
	}

	/**
	 * @return whether or not the d20 info will pop up when this campaign is loaded
	 */
	public boolean isD20()
	{
		return isD20;
	}

	public void setIsOGL(boolean argIsOGL)
	{
		isOGL = argIsOGL;
	}

	/**
	 * @return whether or not the OGL will pop up when this campaign is loaded
	 */
	public boolean isOGL()
	{
		return isOGL;
	}

	public void addSection15Info(String section15Info)
	{
		if (section15Info.equals(".CLEAR"))
		{
			section15Text = null;
			return;
		}

		if (section15Text == null)
		{
			section15Text = new StringBuffer(section15Info.length() + 6);
		}
		section15Text.append(section15Info).append("<br>");
	}

	public void setSection15Info(StringBuffer sb)
	{
		section15Text = sb;
	}

	/**
	 * Returns the section 15 info for this campaign's source(book).
	 * @return the section 15 info
	 */
	public String getSection15Info()
	{
		if (section15Text != null)
		{
			return section15Text.toString();
		}
		return "";
	}

	public List getSection15AsList()
	{
		List aList = new ArrayList();
		if (section15Text != null)
		{
			String aString = getSection15Info();
			int i = aString.indexOf("<br>");
			while (i >= 0)
			{
				aList.add(aString.substring(0, i));
				aString = aString.substring(i + 4);
				i = aString.indexOf("<br>");
			}
			if (aString.length() > 0)
			{
				aList.add(aString);
			}
		}
		return aList;
	}

	public void addLicense(String license)
	{
		if (license.equals(".CLEAR"))
		{
			licenseText = null;
			return;
		}

		if (license.startsWith("FILE="))
		{
			licenseFiles.add(license.substring(5));
			return;
		}

		if (licenseText == null)
		{
			licenseText = new StringBuffer(license.length() + 6);
		}

		licenseText.append(license).append("<br>");
	}

	public void setLicense(StringBuffer sb)
	{
		licenseText = sb;
	}

	/**
	 * Returns the license info for this campaign's source(book).
	 * @return the license
	 */
	public String getLicense()
	{
		if (licenseText != null)
		{
			return licenseText.toString();
		}
		return "";
	}

	public List getLicenseFiles()
	{
		return licenseFiles;
	}

	public List getLicenseAsList()
	{
		List aList = new ArrayList();
		if (licenseText != null)
		{
			String aString = getLicense();
			int i = aString.indexOf("<br>");
			while (i >= 0)
			{
				aList.add(aString.substring(0, i));
				aString = aString.substring(i + 4);
				i = aString.indexOf("<br>");
			}
			if (aString.length() > 0)
			{
				aList.add(aString);
			}
		}
		return aList;
	}

	public List getOptionsList()
	{
		List aList = new ArrayList();
		if (options != null)
		{
			for (Enumeration e = options.propertyNames(); e.hasMoreElements();)
			{
				aList.add(e.nextElement());
			}
		}
		return aList;
	}

	public void setDestination(String arg)
	{
		destination = arg;
	}

	public String getDestination()
	{
		return destination;
	}

	public String getPubNameShort()
	{
		return pubNameShort;
	}

	public void setPubNameShort(String arg)
	{
		addPublisher("SHORT:" + arg);
		pubNameShort = arg;
	}

	public String getPubNameLong()
	{
		return pubNameLong;
	}

	public void setPubNameLong(String arg)
	{
		addPublisher("LONG:" + arg);
		pubNameLong = arg;
	}

	public String getPubNameWeb()
	{
		return pubNameWeb;
	}

	public void setPubNameWeb(String arg)
	{
		addPublisher("WEB:" + arg);
		pubNameWeb = arg;
	}

	public void setBookType(String arg)
	{
		bookType = arg;
	}

	public String getBookType()
	{
		return bookType;
	}

	public List getLines()
	{
		return lines;
	}

	public void addLine(String arg)
	{
		if (arg.equals(".CLEAR"))
		{
			lines.clear();
		}
		else
		{
			lines.add(arg);
		}
	}

	public void addPublisher(String argPublisher)
	{
		String publisher;
		if (argPublisher.startsWith("PUBNAME"))
		{
			publisher = argPublisher.substring(7);
		}
		else
		{
			publisher = argPublisher;
		}
		String key = publisher.substring(0, publisher.indexOf(":"));
		publisherMap.put(key, publisher.substring(publisher.indexOf(":") + 1));
	}

	public String getPublisherWithKey(String key)
	{
		String val = (String) publisherMap.get(key);
		return (val != null) ? val : "";
	}

	public String getSetting()
	{
		return setting;
	}

	public void setSetting(String arg)
	{
		setting = arg;
	}

	public String getGenre()
	{
		return genre;
	}

	public void setGenre(String arg)
	{
		genre = arg;
	}
}
