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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:15 $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;

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
	private List gameModeList = new LinkedList();
	private String infoText = "";
	private StringBuffer section15Text = null;
	private String pubNameLong = "";
	private String pubNameShort = "";
	private String pubNameWeb = "";
	private boolean showInMenu = false;
	private boolean isD20 = false;
	private boolean isOGL = false;
	private boolean isLicensed = false;
	private Properties options = new Properties();
	private StringBuffer licenseText = null;
	private String destination = ""; // path to pcc file relative to data directory
	private String bookType = "";
	private Map publisherMap = new HashMap();
	private List licenseFiles = new ArrayList();
	private String setting = "";
	private String genre = "";
	private String help;

	private List lines = new ArrayList();

	public Object clone()
	{
		Campaign newCampaign = null;
		try
		{
			newCampaign = (Campaign) super.clone();
			newCampaign.isLoaded = isLoaded;
			newCampaign.rank = rank;
			newCampaign.gameModeList = new LinkedList();
			newCampaign.infoText = infoText;
			newCampaign.section15Text = section15Text;
			newCampaign.showInMenu = showInMenu;
			newCampaign.isD20 = isD20;
			newCampaign.isOGL = isOGL;
			newCampaign.isLicensed = isLicensed;
			newCampaign.options = (Properties) options.clone();
			newCampaign.licenseText = licenseText;
			newCampaign.help = help;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return newCampaign;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from.  In this case, it will return (this).
	 * @return Campaign instance referencing the file containing the
	 * 		source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return this;
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
	public List getGameModeList()
	{
		return gameModeList;
	}

	/**
	 * Returns the game modes in a Human readable format
	 **/
	public String getGameModeString()
	{
		StringBuffer ab = new StringBuffer(20);
		for (Iterator gm = gameModeList.iterator(); gm.hasNext();)
		{
			String gmName = (String) gm.next();
			ab.append(gmName);
			if (gm.hasNext())
			{
				ab.append(", ");
			}
		}
		return ab.toString();
	}

	/**
	 * Sets the name of the game this campaign is intended for.
	 * @param argGame name or '|' delimited list of names
	 */
	public void setGameMode(String argGame)
	{
		StringTokenizer aTok = new StringTokenizer(argGame, "|");
		while (aTok.hasMoreTokens())
		{
			String aName = aTok.nextToken();
			gameModeList.add(aName);
		}
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param argGame	name of gameMode to test for
	 * @return		boolean if present	
	 **/
	public boolean isGameMode(String argGame)
	{
		return gameModeList.contains(argGame);
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param argGame	name of gameMode to test for
	 * @return		boolean if present	
	 **/
	public boolean isGameMode(List aList)
	{
		if (aList.size() <= 0)
		{
			return false;
		}
		for (Iterator gm = aList.iterator(); gm.hasNext();)
		{
			String gmName = (String) gm.next();
			if (gameModeList.contains(gmName))
			{
				return true;
			}
		}
		return false;
	}

	public String getHelp()
	{
		return help;
	}

	public void setHelp(String arg)
	{
		help = arg;
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
			String fileName = license.substring(5);
			if (!Utility.isURL(fileName))
			{
				fileName = Utility.fixFilenamePath(fileName);
			}
			licenseFiles.add(fileName);
			return;
		}

		if (licenseText == null)
		{
			licenseText = new StringBuffer(license.length() + 6);
		}

		licenseText.append(license).append("<br>");
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
	
	private List lstExcludeFiles = new ArrayList();
	private List pccFileList = new ArrayList();
	private List raceFileList = new ArrayList();
	private List classFileList = new ArrayList();
	private List companionmodFileList = new ArrayList();
	private List skillFileList = new ArrayList();
	private List featFileList = new ArrayList();
	private List deityFileList = new ArrayList();
	private List domainFileList = new ArrayList();
	private List weaponProfFileList = new ArrayList();
	private List equipmentFileList = new ArrayList();
	private List classSkillFileList = new ArrayList();
	private List classSpellFileList = new ArrayList();
	private List spellFileList = new ArrayList();
	private List languageFileList = new ArrayList();
	private List reqSkillFileList = new ArrayList();
	private List templateFileList = new ArrayList();
	private List equipmodFileList = new ArrayList();
	private List coinFileList = new ArrayList();
	private List kitFileList = new ArrayList();
	private List bioSetFileList = new ArrayList();
	/**
	 * Returns the bioSetFileList.
	 * @return List
	 */
	public List getBioSetFileList()
	{
		return bioSetFileList;
	}

	/**
	 * Returns the classFileList.
	 * @return List
	 */
	public List getClassFileList()
	{
		return classFileList;
	}

	/**
	 * Returns the classSkillFileList.
	 * @return List
	 */
	public List getClassSkillFileList()
	{
		return classSkillFileList;
	}

	/**
	 * Returns the classSpellFileList.
	 * @return List
	 */
	public List getClassSpellFileList()
	{
		return classSpellFileList;
	}

	/**
	 * Returns the coinFileList.
	 * @return List
	 */
	public List getCoinFileList()
	{
		return coinFileList;
	}

	/**
	 * Returns the companionmodFileList.
	 * @return List
	 */
	public List getCompanionmodFileList()
	{
		return companionmodFileList;
	}

	/**
	 * Returns the deityFileList.
	 * @return List
	 */
	public List getDeityFileList()
	{
		return deityFileList;
	}

	/**
	 * Returns the domainFileList.
	 * @return List
	 */
	public List getDomainFileList()
	{
		return domainFileList;
	}

	/**
	 * Returns the equipmentFileList.
	 * @return List
	 */
	public List getEquipmentFileList()
	{
		return equipmentFileList;
	}

	/**
	 * Returns the equipmodFileList.
	 * @return List
	 */
	public List getEquipmodFileList()
	{
		return equipmodFileList;
	}

	/**
	 * Returns the featFileList.
	 * @return List
	 */
	public List getFeatFileList()
	{
		return featFileList;
	}

	/**
	 * Returns the kitFileList.
	 * @return List
	 */
	public List getKitFileList()
	{
		return kitFileList;
	}

	/**
	 * Returns the languageFileList.
	 * @return List
	 */
	public List getLanguageFileList()
	{
		return languageFileList;
	}

	/**
	 * Returns the lstExcludeFiles.
	 * @return List
	 */
	public List getLstExcludeFiles()
	{
		return lstExcludeFiles;
	}

	/**
	 * Returns the pccFileList.
	 * @return List
	 */
	public List getPccFileList()
	{
		return pccFileList;
	}

	/**
	 * Returns the publisherMap.
	 * @return Map
	 */
	public Map getPublisherMap()
	{
		return publisherMap;
	}

	/**
	 * Returns the raceFileList.
	 * @return List
	 */
	public List getRaceFileList()
	{
		return raceFileList;
	}

	/**
	 * Returns the reqSkillFileList.
	 * @return List
	 */
	public List getReqSkillFileList()
	{
		return reqSkillFileList;
	}

	/**
	 * Returns the skillFileList.
	 * @return List
	 */
	public List getSkillFileList()
	{
		return skillFileList;
	}

	/**
	 * Returns the spellFileList.
	 * @return List
	 */
	public List getSpellFileList()
	{
		return spellFileList;
	}

	/**
	 * Returns the templateFileList.
	 * @return List
	 */
	public List getTemplateFileList()
	{
		return templateFileList;
	}

	/**
	 * Returns the weaponProfFileList.
	 * @return List
	 */
	public List getWeaponProfFileList()
	{
		return weaponProfFileList;
	}

}
