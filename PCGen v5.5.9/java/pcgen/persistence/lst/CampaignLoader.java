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
 * Last Edited: $Date: 2006/02/21 01:28:23 $
 *
 */

package pcgen.persistence.lst;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class CampaignLoader extends LstLineFileLoader
{

	/**
	 * Creates a new instance of CampaignLoader
	 */
	public CampaignLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#parseLine(PObject, String, URL)
	 */
	public void parseLine(String inputLine, URL sourceURL)
	{
		// KIT tags are handled differently between campaign objects and all other subclasses of PObject
		if (inputLine.startsWith("KIT:"))
		{
			addCampaignSource( sourceURL, campaign.getKitFileList(), inputLine, 4 );
		}
		else if (PObjectLoader.parseTag(campaign, inputLine))
		{
			return;
		}
		else if (inputLine.startsWith("BOOKTYPE:"))
		{
			campaign.setBookType(inputLine.substring(9));
		}
		else if (inputLine.startsWith("CAMPAIGN:"))
		{
			campaign.setName(inputLine.substring(9));
			campaign.setSourceFile(sourceURL.toString());
		}
		else if (inputLine.startsWith("COPYRIGHT:"))
		{
			campaign.addSection15Info(inputLine.substring(10));
		}
		else if (inputLine.startsWith("GAME:"))
		{
			campaign.setGameMode(inputLine.substring(5));
		}
		else if (inputLine.startsWith("GAMEMODE:"))
		{
			campaign.setGameMode(inputLine.substring(9));
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
		//check here for LST files to exclude from any further loading
		else if (inputLine.startsWith("LSTEXCLUDE:"))
		{
			final String lstList = inputLine.substring(11);
			final StringTokenizer lstTok = new StringTokenizer(lstList, "|");
			while (lstTok.hasMoreTokens())
			{
				final String lstFilename = lstTok.nextToken();
				addCampaignSource( sourceURL, campaign.getLstExcludeFiles(), lstFilename, 0 );
			}
		}
		else if (inputLine.startsWith("PCC:"))
		{
			addFileSource( sourceURL, campaign.getPccFileList(), inputLine, 4 );
		}
		else if (inputLine.startsWith("RACE:"))
		{
			addCampaignSource( sourceURL, campaign.getRaceFileList(), inputLine, 5 );
		}
		else if (inputLine.startsWith("CLASS:"))
		{
			addCampaignSource( sourceURL, campaign.getClassFileList(), inputLine, 6 );
		}
		else if (inputLine.startsWith("COMPANIONMOD:"))
		{
			addCampaignSource( sourceURL, campaign.getCompanionmodFileList(), inputLine, 13 );
		}
		else if (inputLine.startsWith("SKILL:"))
		{
			addCampaignSource( sourceURL, campaign.getSkillFileList(), inputLine, 6 );
		}
		else if (inputLine.startsWith("FEAT:"))
		{
			addCampaignSource( sourceURL, campaign.getFeatFileList(), inputLine, 5 );
		}
		else if (inputLine.startsWith("DOMAIN:"))
		{
			addCampaignSource( sourceURL, campaign.getDomainFileList(), inputLine, 7 );
		}
		else if (inputLine.startsWith("DEITY:"))
		{
			addCampaignSource( sourceURL, campaign.getDeityFileList(), inputLine, 6 );
		}
		else if (inputLine.startsWith("SPELL:"))
		{
			addCampaignSource( sourceURL, campaign.getSpellFileList(), inputLine, 6 );
		}
		else if (inputLine.startsWith("WEAPONPROF:"))
		{
			addCampaignSource( sourceURL, campaign.getWeaponProfFileList(), inputLine, 11 );
		}
		else if (inputLine.startsWith("EQUIPMENT:"))
		{
			addCampaignSource( sourceURL, campaign.getEquipmentFileList(), inputLine, 10 );
		}
		else if (inputLine.startsWith("LANGUAGE:"))
		{
			addCampaignSource( sourceURL, campaign.getLanguageFileList(), inputLine, 9 );
		}
		else if (inputLine.startsWith("CLASSSKILL:"))
		{
			addCampaignSource( sourceURL, campaign.getClassSkillFileList(), inputLine, 11 );
		}
		else if (inputLine.startsWith("CLASSSPELL:"))
		{
			addCampaignSource( sourceURL, campaign.getClassSpellFileList(), inputLine, 11 );
		}
		else if (inputLine.startsWith("REQSKILL:"))
		{
			addCampaignSource( sourceURL, campaign.getReqSkillFileList(), inputLine, 9 );
		}
		else if (inputLine.startsWith("TEMPLATE:"))
		{
			addCampaignSource( sourceURL, campaign.getTemplateFileList(), inputLine, 9 );
		}
		else if (inputLine.startsWith("EQUIPMOD:"))
		{
			addCampaignSource( sourceURL, campaign.getEquipmodFileList(), inputLine, 9 );
		}
		else if (inputLine.startsWith("COINS:"))
		{
			addCampaignSource( sourceURL, campaign.getCoinFileList(), inputLine, 6 );
		}
		else if (inputLine.startsWith("BIOSET:"))
		{
			addFileSource( sourceURL, campaign.getBioSetFileList(), inputLine, 7 );
		}
		else
		{
			Logging.errorPrint("Unparsed line: " + inputLine + " in " + sourceURL.toString());
		}
	}

	/**
	 * This method adds a source file to the provided list of sources after
	 * converting its format to that of a URL or absolute path.
	 *
	 * @param pccPath    URL where the Campaign that contained the source was at
	 * @param sourceList List of source files to add the path to
	 * @param inputLine  String line from teh PCC file containing the filepath
	 *                   or URL to add to the source List (and preceded by the type tag)
	 * @param tagLength  int length of the tag preceding the filePath/URL
	 * @see #convertFilePath
	 */
	private void addCampaignSource(URL pccPath, List sourceList, String inputLine, int tagLength)
	{
		sourceList.add( 
			new CampaignSourceEntry(
				campaign,
				convertFilePath(pccPath, inputLine.substring(tagLength)) 
			) 
		);
	}

	/**
	 * This method adds a source file to the provided list of sources after
	 * converting its format to that of a URL or absolute path.
	 * @see convertFilePath
	 * 
	 * @param pccPath URL where the Campaign that contained the source was at
	 * @param sourceList List of source files to add the path to
	 * @param inputLine String line from teh PCC file containing the filepath
	 *  or URL to add to the source List (and preceded by the type tag)
	 * @param tagLength int length of the tag preceding the filePath/URL
	 */
	private static void addFileSource(URL pccPath, List sourceList, String inputLine, int tagLength)	{
		sourceList.add( convertFilePath(pccPath, inputLine.substring(tagLength) ) );
	}

	/**
	 * This method converts the provided filePath to either a URL
	 * or absolute path as appropriate.
	 *
	 * @param pccPath  URL where the Campaign that contained the source was at
	 * @param basePath String path that is to be converted
	 * @return String containing the converted absolute path or URL
	 *         (as appropriate)
	 */
	private static String convertFilePath(URL pccPath, String basePath)
	{
		String convertedPath = "";

		if (basePath.length() <= 0)
		{
			return convertedPath;
		}

		// Check if the basePath was a complete URL to begin with
		if (Utility.isURL(basePath))
		{
			convertedPath = basePath;
			// if it's a URL, then we are all done
			return convertedPath;
		}
		else
		{
			/* Figure out where the PCC file came from that we're
			 * processing, so that we can prepend its path onto
			 * any LST file references (or PCC refs, for that
			 * matter) that are relative. If the source line in
			 * question already has path info, then don't bother
			 */
			if (basePath.charAt(0) == '@')
			{
				final String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
				convertedPath = SettingsHandler.getPccFilesLocation().getAbsolutePath()
					+ File.separator
					+ pathNoLeader;
			}
			// the line doesn't use "@" then it's a relative path,
			else //if (aLine.indexOf('@') < 0)
			{
				/*
				 * 1) If the path starts with '/data',
				 * assume it means the PCGen data dir
				 * 2) Otherwise, assume that the path is
				 * relative to the current PCC file URL
				 */
				final String pathNoLeader = trimLeadingFileSeparator(basePath);
				if (pathNoLeader.startsWith("data/"))
				{
					convertedPath = SettingsHandler.getPccFilesLocation()
						+ pathNoLeader.substring(4);
				}
				else
				{
					convertedPath = pccPath.getPath();
					// URLs always use forward slash; take off the file name
					int separatorLoc = convertedPath.lastIndexOf("/");
					convertedPath = convertedPath.substring(0, separatorLoc) + "/" + basePath;
				}
			}

			// Not a URL; make sure to fix the path syntax
			convertedPath = Utility.fixFilenamePath(convertedPath);
		}

		// Make sure the path starts with a separator
		if (!convertedPath.startsWith(File.separator))
		{
			convertedPath = File.separator + convertedPath;
		}

		// Return the final result
		try
		{
			return new URL("file:" + convertedPath).toString();
		}
		catch (MalformedURLException e)
		{
			Logging.errorPrint("failed to convert " + convertedPath + " to true URL.");
			return convertedPath;
		}
	}

	/**
	 * This method trims the leading file separator or URL separator from the
	 * front of a string.
	 *
	 * @param basePath String containing the base path to trim
	 * @return String containing the trimmed path String
	 */
	private static String trimLeadingFileSeparator(String basePath)
	{
		String pathNoLeader = basePath;
		if (pathNoLeader.startsWith("/") || pathNoLeader.startsWith(File.separator))
		{
			pathNoLeader = pathNoLeader.substring(1);
		}
		return pathNoLeader;
	}

	/**
	 * This method finishes the campaign being loaded by saving its section 15
	 * information as well as adding it to Globals, if it has not already been
	 * loaded.
	 */
	protected void finishCampaign()
	{
		if (Globals.getCampaignByFilename(campaign.getSourceFile(), false) == null)
		{
			final String sect15 = campaign.getSection15Info();
			if ( (sect15!=null) && (sect15.trim().length()>0) )
			{
				Globals.getSection15().append("<br><b>Source Material:</b>");
				Globals.getSection15().append(campaign.getSourceInForm(Constants.SOURCELONG));
				Globals.getSection15().append("<br>");
				Globals.getSection15().append("<b>Section 15 Entry in Source Material:</b><br>");
				Globals.getSection15().append(sect15);
			}

			Globals.addCampaign(campaign);
		}
	}

	/**
	 * This method initializes any campaigns that include other campaigns,
	 * avoiding an infinite loop in the event of recursive i.e. interdependent
	 * campaigns.
	 *
	 * @throws PersistenceLayerException if an error occurs reading a
	 *                                   newly-encountered campaign
	 */
	public void initRecursivePccFiles() throws PersistenceLayerException
	{
		// This may modify the globals list; need a local copy so
		// the iteration doesn't fail.
		List initialCampaigns = new ArrayList(Globals.getCampaignList());

		Iterator iter = initialCampaigns.iterator();
		while (iter.hasNext())
		{
			initRecursivePccFiles((Campaign) iter.next());
		}
	}

	/**
	 * This method initializes any campaigns that include other campaigns,
	 * avoiding an infinite loop in the event of recursive i.e. interdependent
	 * campaigns.  This specific overloading will recurse down a the given
	 * campaign object dependency tree, then return.
	 *
	 * @param baseCampaign Campaign object that may or may not require
	 *                     other campaigns
	 * @throws PersistenceLayerException if an error occurs reading a
	 *                                   newly-encountered campaign
	 */
	private void initRecursivePccFiles(Campaign baseCampaign)
		throws PersistenceLayerException
	{
		if (baseCampaign == null)
		{
			return;
		}

		// Add all sub-files to the main campaign, regardless of exclusions
		Iterator subIter = baseCampaign.getPccFileList().iterator();
		while (subIter.hasNext())
		{
			final String fName = (String) subIter.next();
			if (fName.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION))
			{
				Campaign globalSubCampaign = Globals.getCampaignByFilename(fName, false);
				if (globalSubCampaign == null)
				{
					try
					{
						loadLstFile(fName);
						globalSubCampaign = Globals.getCampaignByFilename(fName, false);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Recursive init failed on file " + fName, e);
					}
				}

				// add all sub-subs etc to the list
				initRecursivePccFiles(globalSubCampaign);

				// add subfiles to the parent campaign for loading
				initRecursivePccFiles(baseCampaign, globalSubCampaign);
			}
		}

		// Now that all files are added, make a single pass to strip
		// out the excluded files
		stripLstExcludes(baseCampaign);
	}

	/**
	 * This method adds all files from the included campaigns to this one.
	 * It then strips out the excluded files via a call to stripLstExcludes.
	 *
	 * @param baseCampaign Campaign that includes another campaign
	 * @param subCampaign  Campaign included by the baseCampaign
	 */
	private void initRecursivePccFiles(
		Campaign baseCampaign,
		Campaign subCampaign)
	{
		if (subCampaign == null)
		{
			return;
		}

		baseCampaign.getLstExcludeFiles().addAll(subCampaign.getLstExcludeFiles());
		baseCampaign.getRaceFileList().addAll(subCampaign.getRaceFileList());
		baseCampaign.getClassFileList().addAll(subCampaign.getClassFileList());
		baseCampaign.getCompanionmodFileList().addAll(subCampaign.getCompanionmodFileList());
		baseCampaign.getSkillFileList().addAll(subCampaign.getSkillFileList());
		baseCampaign.getFeatFileList().addAll(subCampaign.getFeatFileList());
		baseCampaign.getDeityFileList().addAll(subCampaign.getDeityFileList());
		baseCampaign.getDomainFileList().addAll(subCampaign.getDomainFileList());
		baseCampaign.getWeaponProfFileList().addAll(subCampaign.getWeaponProfFileList());
		baseCampaign.getEquipmentFileList().addAll(subCampaign.getEquipmentFileList());
		baseCampaign.getClassSkillFileList().addAll(subCampaign.getClassSkillFileList());
		baseCampaign.getClassSpellFileList().addAll(subCampaign.getClassSpellFileList());
		baseCampaign.getSpellFileList().addAll(subCampaign.getSpellFileList());
		baseCampaign.getLanguageFileList().addAll(subCampaign.getLanguageFileList());
		baseCampaign.getReqSkillFileList().addAll(subCampaign.getReqSkillFileList());
		baseCampaign.getTemplateFileList().addAll(subCampaign.getTemplateFileList());
		baseCampaign.getEquipmodFileList().addAll(subCampaign.getEquipmodFileList());
		baseCampaign.getCoinFileList().addAll(subCampaign.getCoinFileList());
		baseCampaign.getKitFileList().addAll(subCampaign.getKitFileList());
		baseCampaign.getBioSetFileList().addAll(subCampaign.getBioSetFileList());
	}

	/**
	 * This method makes sure that the excluded files are stripped out of the
	 * campaign files.
	 *
	 * @param baseCampaign Campaign to filter the excluded files out of
	 */
	private void stripLstExcludes(Campaign baseCampaign)
	{
		Iterator iter = baseCampaign.getLstExcludeFiles().iterator();
		while (iter.hasNext())
		{
			Object excludeFile = iter.next();

			removeExclude(excludeFile, baseCampaign.getRaceFileList());
			removeExclude(excludeFile, baseCampaign.getClassFileList());
			removeExclude(excludeFile, baseCampaign.getCompanionmodFileList());
			removeExclude(excludeFile, baseCampaign.getSkillFileList());
			removeExclude(excludeFile, baseCampaign.getFeatFileList());
			removeExclude(excludeFile, baseCampaign.getDeityFileList());
			removeExclude(excludeFile, baseCampaign.getDomainFileList());
			removeExclude(excludeFile, baseCampaign.getWeaponProfFileList());
			removeExclude(excludeFile, baseCampaign.getEquipmentFileList());
			removeExclude(excludeFile, baseCampaign.getClassSkillFileList());
			removeExclude(excludeFile, baseCampaign.getClassSpellFileList());
			removeExclude(excludeFile, baseCampaign.getSpellFileList());
			removeExclude(excludeFile, baseCampaign.getLanguageFileList());
			removeExclude(excludeFile, baseCampaign.getReqSkillFileList());
			removeExclude(excludeFile, baseCampaign.getTemplateFileList());
			removeExclude(excludeFile, baseCampaign.getEquipmodFileList());
			removeExclude(excludeFile, baseCampaign.getCoinFileList());
			removeExclude(excludeFile, baseCampaign.getKitFileList());
			removeExclude(excludeFile, baseCampaign.getBioSetFileList());
		}
	}

	/**
	 * Due to the INCLUDE and EXCLUDE syntax, this method is required in
	 * order to properly remove the excluded file designated in the method
	 * call from the file list.
	 *  
	 * @param Object CampaignSourceEntry or String source entry to exclude
	 * @param lstFileList List of LST files to sanitize
	 */
	private void removeExclude(Object exclude, List lstFileList)
	{
		Iterator iter = lstFileList.iterator();
		while (iter.hasNext())
		{
			Object lstSource = iter.next();
			if( lstSource.equals(exclude))
			{
				iter.remove();
			}
		}
	}
	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#loadLstFile(java.lang.String)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		campaign = new Campaign();

		super.loadLstFile(fileName);
		
		finishCampaign();
	}

	private Campaign campaign = null;
}
