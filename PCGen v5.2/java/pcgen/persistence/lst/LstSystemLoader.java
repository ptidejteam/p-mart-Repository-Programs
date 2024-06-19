/*
 * LstSystemLoader.java
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
 * Last Edited: $Date: 2006/02/21 01:13:22 $
 *
 */

package pcgen.persistence.lst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.BioSet;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.CustomData;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Feat;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.LevelInfo;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PaperInfo;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSlot;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.pcGenGUI;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.GuiFacade;
import pcgen.util.Logging;

/**
 * ???
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 **/
public final class LstSystemLoader implements SystemLoader
{

	// list of PObjects for character spells with subclasses
	private static final List pList = new ArrayList();

	private boolean showOGL = false;
	private boolean showD20 = false;
	private boolean showLicensed = true;
	private StringBuffer licensesToDisplayString = new StringBuffer();
	private List licenseFiles = new ArrayList();

	private static final int INVALID_LINE_TYPE = -9999;
	/**
	 * Define the order in which the file types are ordered
	 * so we don't have to keep renumbering them
	 **/
	private static final int[] loadOrder = {
		-1
		, LstConstants.STATNAME_TYPE
		, LstConstants.MISCGAMEINFO_TYPE
		, LstConstants.POINTBUY_TYPE
		, LstConstants.WEAPONPROF_TYPE
		, LstConstants.SKILL_TYPE		// loaded before classes to properly handle class skills
		, LstConstants.LANGUAGE_TYPE		// loaded before races to handle auto known languages
		, LstConstants.RACE_TYPE
		, LstConstants.CLASS_TYPE
		, LstConstants.FEAT_TYPE
		, LstConstants.DOMAIN_TYPE
		, LstConstants.DEITY_TYPE
		, LstConstants.SPELL_TYPE
		, LstConstants.SCHOOLS_TYPE
		//, LstConstants.COLOR_TYPE
		//, LstConstants.TRAIT_TYPE
		, LstConstants.COINS_TYPE		// loaded before equipment to cover costs
		, LstConstants.EQMODIFIER_TYPE		// loaded before the equipment so any modifiers will be found
		, LstConstants.EQUIPMENT_TYPE
		, LstConstants.LOAD_TYPE
		, LstConstants.SPECIAL_TYPE
		, LstConstants.CAMPAIGN_TYPE
		, LstConstants.CLASSSKILL_TYPE
		, LstConstants.CLASSSPELL_TYPE
		, LstConstants.REQSKILL_TYPE
		, LstConstants.TEMPLATE_TYPE
		//, LstConstants.XP_TYPE
		, LstConstants.BONUS_TYPE
		, LstConstants.EQUIPSLOT_TYPE
		, LstConstants.SIZEADJUSTMENT_TYPE
		//, LstConstants.PHOBIA_TYPE
		//, LstConstants.INTERESTS_TYPE
		//, LstConstants.PHRASE_TYPE
		//, LstConstants.HAIRSTYLE_TYPE
		//, LstConstants.SPEECH_TYPE
		, LstConstants.PAPERINFO_TYPE
		, LstConstants.COMPANIONMOD_TYPE
		, LstConstants.KIT_TYPE
		, LstConstants.BIO2_TYPE		// BIO_TYPE is for main setting, BIO2_TYPE is for user overrides
		, LstConstants.TRAITS_TYPE
		, LstConstants.LOCATIONS_TYPE
		, INVALID_LINE_TYPE
	};

	/////////////////////////////////////////////////////////////////
	// Property(s)
	/////////////////////////////////////////////////////////////////

	private String currentSource = "";

	private final List chosenCampaignSourcefiles = new ArrayList();
	private static int lineNum = 0;

	private final Set sourcesSet = new TreeSet();
	private final List sourceFileList = new ArrayList();
	private final Map loadedFiles = new HashMap();
	private final List recursivePCCFileList = new ArrayList();

	private final List lstExcludeFiles = new ArrayList();
	private final List pccFileLines = new ArrayList();
	private final List raceFileLines = new ArrayList();
	private final List classFileLines = new ArrayList();
	private final List companionmodFileLines = new ArrayList();
	private final List skillFileLines = new ArrayList();
	private final List featFileLines = new ArrayList();
	private final List deityFileLines = new ArrayList();
	private final List domainFileLines = new ArrayList();
	private final List weaponProfFileLines = new ArrayList();
	private final List equipmentFileLines = new ArrayList();
	private final List classSkillFileLines = new ArrayList();
	private final List classSpellFileLines = new ArrayList();
	private final List spellFileLines = new ArrayList();
	private final List languageLines = new ArrayList();
	private final List reqSkillLines = new ArrayList();
	private final List templateFileLines = new ArrayList();
	private final List equipmentModifierFileLines = new ArrayList();
	private final List coinFileLines = new ArrayList();
	private final List kitFileLines = new ArrayList();
	private final List bioSetLines = new ArrayList();

	// Used to store MODs to later processing
	// I had to store both the line and the filetype where this line was in
	private final List modLines = new ArrayList();
	private final List modFileType = new ArrayList();

	// Used to store FORGETs for later processing; works much like MODs.
	// Patch [651150] and Feature Request [650672].  sk4p 10 Dec 2002
	private final List forgetLines = new ArrayList();
	private final List forgetFileType = new ArrayList();

	private String skillReq = "";

	private boolean customItemsLoaded = false;

	////////////////////////////////////////////////////////////
	// Constructor(s)
	////////////////////////////////////////////////////////////

	/** Creates a new instance of LstSystemLoader */
	public LstSystemLoader()
	{
	}

	////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	////////////////////////////////////////////////////////////
	public String getCurrentSource()
	{
		return currentSource;
	}

	public void setCurrentSource(String aString)
	{
		currentSource = aString;
	}

	public List getChosenCampaignSourcefiles()
	{
		return chosenCampaignSourcefiles;
	}

	public void setChosenCampaignSourcefiles(List l)
	{
		chosenCampaignSourcefiles.clear();
		chosenCampaignSourcefiles.addAll(l);
		SettingsHandler.getOptions().setProperty("pcgen.files.chosenCampaignSourcefiles", Utility.join(chosenCampaignSourcefiles, ','));
	}

	public Set getSources()
	{
		return sourcesSet;
	}

	public boolean isCustomItemsLoaded()
	{
		return customItemsLoaded;
	}

	public void setCustomItemsLoaded(boolean argLoaded)
	{
		customItemsLoaded = argLoaded;
	}

	public int saveSourceFile(String src)
	{
		final int idx = sourceFileList.indexOf(src);
		if (idx >= 0)
		{
			return idx;
		}
		sourceFileList.add(src);
		return sourceFileList.indexOf(src);
	}

	public String savedSourceFile(int idx)
	{
		if ((idx >= 0) && (idx < sourceFileList.size()))
		{
			return (String) sourceFileList.get(idx);
		}
		return "";
	}

	public void initialize() throws PersistenceLayerException
	{
		loadGameModes();

		loadPCCFilesInDirectory(SettingsHandler.getPccFilesLocation().getAbsolutePath());
		loadPCCFilesInRecursiveList();

		final String prefix = SettingsHandler.getPcgenSystemDir() + File.separator;
		final ArrayList aList = new ArrayList();
		initFile(prefix + "schools.lst", LstConstants.SCHOOLS_TYPE, aList);
		initFile(prefix + "specials" + File.separator + "specials.lst", LstConstants.SPECIAL_TYPE, aList);
		initFile(prefix + "specials" + File.separator + "bonusstacks.lst", LstConstants.BONUS_TYPE, aList);
		initFile(prefix + "specials" + File.separator + "equipmentslots.lst", LstConstants.EQUIPSLOT_TYPE, aList);
		initFile(prefix + "sizeAdjustment.lst", LstConstants.SIZEADJUSTMENT_TYPE, aList);
		initFile(prefix + "paperInfo.lst", LstConstants.PAPERINFO_TYPE, aList);
		initFile(prefix + "bio" + File.separator + "traits.lst", LstConstants.TRAITS_TYPE, aList, false);
		initFile(prefix + "bio" + File.separator + "locations.lst", LstConstants.LOCATIONS_TYPE, aList, false);
		initFile(prefix + "bio" + File.separator + "biosettings.lst", LstConstants.BIO_TYPE, aList);
//		initFile(prefix + "contexthelp.lst", LstConstants.HELPCONTEXT_TYPE, aList);
		initFile(prefix + "pointbuymethods.lst", LstConstants.POINTBUY_TYPE, new ArrayList(), false);

		Globals.sortPObjectList(Globals.getCampaignList());
	}

	/**
	 * This just calls loadPCCFilesInDirectory.
	 * Note:  This only handles added campaigns right now, not removed ones
	 *
	 * author Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
	 * @see pcgen.persistence.PersistenceManager#refreshCampaigns
	 */
	public void refreshCampaigns()
	{
		loadPCCFilesInDirectory(SettingsHandler.getPccFilesLocation().getAbsolutePath());
	}

	////////////////////////////////////////////////////////////
	// Private Method(s)
	////////////////////////////////////////////////////////////

	public void emptyLists()
	{
		loadedFiles.clear();
		chosenCampaignSourcefiles.clear();
		licensesToDisplayString = new StringBuffer();

		releaseFileData();
		BioSet.clearUserMap();

		skillReq = "";
	}

	private void releaseFileData()
	{
		lstExcludeFiles.clear();
		pccFileLines.clear();
		raceFileLines.clear();
		classFileLines.clear();
		companionmodFileLines.clear();
		skillFileLines.clear();
		featFileLines.clear();
		deityFileLines.clear();
		domainFileLines.clear();
		templateFileLines.clear();
		weaponProfFileLines.clear();
		equipmentFileLines.clear();
		classSkillFileLines.clear();
		classSpellFileLines.clear();
		spellFileLines.clear();
		reqSkillLines.clear();
		languageLines.clear();
		equipmentModifierFileLines.clear();
		coinFileLines.clear();
		kitFileLines.clear();
		bioSetLines.clear();
	}

	private void loadPCCFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				try
				{
					if (aString.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION))
					{
						final String fName = aFile.getPath() + File.separator + aString;
						//Test to avoid reloading existing campaigns, so we can safely
						// call loadPCCFilesInDirectory repeatedly. -rlk 2002-03-30
						if (Globals.getCampaignByFilename(fName, false) == null)
						{
							initFile(fName, LstConstants.CAMPAIGN_TYPE, new ArrayList());
						}
					}
					else if (aFile.isDirectory())
					{
						loadPCCFilesInDirectory(aFile.getPath() + File.separator + aString);
					}
				}
				catch (PersistenceLayerException e)
				{
					// LATER: This is not an appropriate way to deal with this exception.
					// Deal with it this way because of the way the loading takes place.  XXX
					Logging.errorPrint("PersistanceLayer", e);
				}
				return false;
			}
		});
		return;
	}

	private void loadPCCFilesInRecursiveList()
	{
		final ArrayList aList = new ArrayList(recursivePCCFileList);
		for (Iterator as = aList.iterator(); as.hasNext();)
		{
			final String fName = (String) as.next();
			if (fName.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION))
			{
				if (Globals.getCampaignByFilename(fName, false) == null)
				{
					try
					{
						initFile(fName, LstConstants.CAMPAIGN_TYPE, new ArrayList());
					}
					catch (Exception e)
					{
						Logging.errorPrint("PersistanceLayer", e);
					}
				}
			}
		}
	}

	private void loadPCCFileFromNet(String urlString)
	{
		try
		{
			if (urlString.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION))
			{
				if (Globals.getCampaignByFilename(urlString, false) == null)
				{
					initFile(urlString, LstConstants.CAMPAIGN_TYPE, new ArrayList());
				}
			}
		}
		catch (Exception e)
		{
			Logging.errorPrint("PersistanceLayer", e);
		}
	}

	public void loadCampaigns(final List aSelectedCampaignsList) throws PersistenceLayerException
	{
		Globals.setSorted(false);
		sourcesSet.clear();
		licenseFiles.clear();
		if (aSelectedCampaignsList.size() == 0)
		{
			throw new PersistenceLayerException("You must select at least one campaign to load.");
		}

		// 21 Nov 2002: Put load inside a try/finally block to make sure
		// that file lines were cleared even if an exception occurred.
		// -- sage_sam
		try
		{
			int i;
			Campaign aCamp;
			Campaign bCamp;
			Campaign cCamp;
			for (i = 0; i < aSelectedCampaignsList.size() - 1; ++i)
			{
				aCamp = (Campaign) aSelectedCampaignsList.get(i);
				sourcesSet.add(aCamp.getSourceInForm(Constants.SOURCELONG));
				int aCampRank = aCamp.getRank().intValue();
				for (int j = i + 1; j < aSelectedCampaignsList.size(); ++j)
				{
					bCamp = (Campaign) aSelectedCampaignsList.get(j);
					if (bCamp.getRank().intValue() < aCampRank)
					{
						aSelectedCampaignsList.set(i, bCamp);
						aSelectedCampaignsList.set(j, aCamp);
						cCamp = (Campaign) aCamp.clone();
						aCamp = bCamp;
						bCamp = cCamp;
						aCampRank = aCamp.getRank().intValue();
					}
				}
			}

			if (SettingsHandler.isOptionAllowedInSources())
			{
				SettingsHandler.setOptionsProperties(); // Prime options based on currently selected preferences
			}
			Globals.getSection15().setLength(0);
			showOGL = false;
			showD20 = false;
			showLicensed = false;

			for (i = 0; i < aSelectedCampaignsList.size(); ++i)
			{
				aCamp = (Campaign) aSelectedCampaignsList.get(i);
				loadCampaignFile(aCamp);
				if (SettingsHandler.isOptionAllowedInSources())
				{
					setCampaignOptions(aCamp);
				}
			}
			//
			// This was added in v1.64. Why? This will read from options.ini, replacing anything that's been changed, not
			// just campaign-specific items. Commenting out as it breaks loading after selecting game mode on the Campaign menu.
			// Game mode reverts back to game mode saved in options.ini
			// - Byngl Sept 15, 2002
			// This allows options to be set by campaign files. It doesn't read directly from options.ini,
			// only from the properties. The setOptionsProperties call added above should prime these with
			// the current values before we load the campaigns.
			// - James Dempsey 09 Oct 2002
			if (SettingsHandler.isOptionAllowedInSources())
			{
				SettingsHandler.getOptionsFromProperties();
			}

			List lineList;
			List bArrayList;
			for (int loadIdx = 0; ; ++loadIdx)
			{
				final int lineType = loadOrder[loadIdx];

				if (lineType == INVALID_LINE_TYPE)
				{
					break;
				}
				lineList = null;
				bArrayList = new ArrayList();
				switch (lineType)
				{
					case -1: // ok - should be 15 (CAMPAIGN_TYPE), but needed to do it at the front and didn't want to bump everything else up one..
						lineList = pccFileLines; // merton_monk  2Nov01
						break;

					case LstConstants.RACE_TYPE:
						lineList = raceFileLines;
						break;

					case LstConstants.CLASS_TYPE:
						lineList = classFileLines;
						break;

					case LstConstants.COMPANIONMOD_TYPE:
						lineList = companionmodFileLines;
						break;

					case LstConstants.SKILL_TYPE:
						lineList = skillFileLines;
						break;

					case LstConstants.FEAT_TYPE:
						lineList = featFileLines;
						break;

					case LstConstants.DOMAIN_TYPE:
						lineList = domainFileLines;
						break;

					case LstConstants.DEITY_TYPE:
						lineList = deityFileLines;
						break;

					case LstConstants.SPELL_TYPE:
						lineList = spellFileLines;
						break;

					case LstConstants.WEAPONPROF_TYPE:
						lineList = weaponProfFileLines;
						break;

					case LstConstants.SCHOOLS_TYPE:
						continue;

					case LstConstants.TRAITS_TYPE:
						continue;

					case LstConstants.EQUIPMENT_TYPE:
						lineList = equipmentFileLines;
						break;

					case LstConstants.LANGUAGE_TYPE:
						lineList = languageLines;
						break;

					case LstConstants.LOAD_TYPE:
						continue;

					case LstConstants.SPECIAL_TYPE:
						continue;

					case LstConstants.EQUIPSLOT_TYPE:
						continue;

					case LstConstants.SIZEADJUSTMENT_TYPE:
						continue;

					case LstConstants.STATNAME_TYPE:
						continue;

					case LstConstants.MISCGAMEINFO_TYPE:
						continue;

					case LstConstants.POINTBUY_TYPE:
						continue;

					case LstConstants.CAMPAIGN_TYPE: // this is the campaign/source type, but needs to be first, so it's done at -1
						continue;

					case LstConstants.CLASSSKILL_TYPE:
						lineList = classSkillFileLines;
						break;

					case LstConstants.CLASSSPELL_TYPE:
						lineList = classSpellFileLines;
						break;

					case LstConstants.REQSKILL_TYPE:
						lineList = reqSkillLines;
						break;

					case LstConstants.TEMPLATE_TYPE:
						lineList = templateFileLines;
						break;

					case LstConstants.BONUS_TYPE:
						continue;

					case LstConstants.EQMODIFIER_TYPE:
						lineList = equipmentModifierFileLines;
						break;

					case LstConstants.LOCATIONS_TYPE:
						continue;

					case LstConstants.PAPERINFO_TYPE:
						continue;

					case LstConstants.COINS_TYPE:
						lineList = coinFileLines;
						break;

					case LstConstants.KIT_TYPE:
						lineList = kitFileLines;
						break;

					case LstConstants.BIO2_TYPE:
						lineList = bioSetLines;
						break;

					default:
						lineList = null;
						Logging.errorPrint("Campaign list corrupt at line: " + i + " no such lineType (" + lineType + ") exists. Stopped parsing campaigns, but not aborting program.");
						return;
				}

				List cArrayList;
				String fileName;
				//This relies on new items being added to the *end* of an ArrayList.
				for (int j = 0; j < lineList.size(); ++j)
				{
					final String aLine = (String) lineList.get(j);
					final StringTokenizer lineTokenizer = new StringTokenizer(aLine, "|", false);
					int inMode = 0;
					cArrayList = new ArrayList();
					fileName = "";
					String currentToken;
					String dString;
					while (lineTokenizer.hasMoreTokens())
					{
						currentToken = lineTokenizer.nextToken();
						int openParens = 0;
						int closeParens = 0;
						dString = currentToken.substring(1);
						while (dString.lastIndexOf('(') >= 0)
						{
							++openParens;
							dString = dString.substring(0, dString.lastIndexOf('('));
						}
						dString = currentToken;
						while (dString.lastIndexOf(')') >= 0)
						{
							++closeParens;
							dString = dString.substring(0, dString.lastIndexOf(')'));
						}
						boolean handled = false;
						if (currentToken.endsWith(".lst") || currentToken.endsWith(".pcc"))
						{
							// load file
							handled = true;

							//*** to handle LSTEXCLUDE commands 12/12/01
							//skip file if it's on the LSTEXCLUDE list
							boolean loadFile = true; //assume we won't find the file in the exclusion list
							final Iterator lstExcludeIter = lstExcludeFiles.iterator();
							while (lstExcludeIter.hasNext())
							{
								final String lstFilename = (String) lstExcludeIter.next();
								//if the file were loading is found on the exclusion list...
								if (currentToken.indexOf(lstFilename) > 0)
								{
									loadFile = false;  //...don't load it
									break;  //exit the while since we've already found our target
								}
							}
							//*** to handle LSTEXCLUDE commands 12/12/01

							if (loadFile)
							{
								bArrayList = adds(lineType, bArrayList);
								//if it is a URL, don't append the path, and don't change the slashes
								try
								{
									if (!Utility.isURL(currentToken))
									{
										currentToken = Utility.fixFilenamePath(SettingsHandler.getPccFilesLocation().getAbsolutePath() + currentToken);
										currentToken = Utility.fileToURL(currentToken);
									}
									else
									{
										currentToken = Utility.fixURLPath(SettingsHandler.getPccFilesLocation().getAbsolutePath(), currentToken);
									}
								}
								catch (MalformedURLException e)
								{
									throw new PersistenceLayerException(e.getMessage());
								}
								if (!loadedFiles.containsKey(currentToken))
								{
									bArrayList = initFile(currentToken, lineType, bArrayList);
									if (lineType == -1)
									{
										initCampaignFromList(bArrayList, currentToken);
									}
								}
							}
							fileName = currentToken;
							cArrayList.clear();
						}
						if (currentToken.startsWith("(EXCLUDE"))
						{
							handled = true;
							fileName = "";
							currentToken = fixCurrentToken(closeParens, openParens, currentToken, cArrayList);
							inMode = -1;
						}
						else if (currentToken.startsWith("(INCLUDE"))
						{
							handled = true;
							fileName = "";
							currentToken = fixCurrentToken(closeParens, openParens, currentToken, cArrayList);
							inMode = 1;
						}
						if (currentToken.endsWith(")") && closeParens > openParens)
						{
							if (!handled)
							{
								cArrayList.add(currentToken.substring(0, currentToken.length() - 1));
							}
							handled = true;
							if (inMode == -1)
							{
								// exclude
								PObject anObject;
								for (int k = bArrayList.size() - 1; k >= 0; --k)
								{
									anObject = (PObject) bArrayList.get(k);
									if (cArrayList.contains(anObject.getKeyName()))
									{
										bArrayList.remove(k);
									}
								}
							}
							else if (inMode == 1)
							{
								// include
								PObject anObject;
								for (int k = bArrayList.size() - 1; k >= 0; --k)
								{
									anObject = (PObject) bArrayList.get(k);
									if (!cArrayList.contains(anObject.getKeyName()))
									{
										bArrayList.remove(k);
									}
								}
							}
							inMode = 0;
							bArrayList = adds(lineType, bArrayList);
						}
						if (!handled && lineType != 12 && lineType != LstConstants.REQSKILL_TYPE)
						{
							cArrayList.add(currentToken);
						}
						else if (!handled)
						{
							bArrayList.add(currentToken);
						}
					}
					bArrayList = adds(lineType, bArrayList);
					if (fileName.length() > 0)
					{
						loadedFiles.put(fileName, fileName);
					}
				}
			}

			if (Globals.getUseGUI())
			{
				if (showOGL && SettingsHandler.showLicense())
				{
					pcGenGUI.showLicense();
					if (showLicensed)
					{
						pcGenGUI.showLicense("Special Licenses", licensesToDisplayString.toString());
						pcGenGUI.showLicense("Special Licenses", licenseFiles);
					}
				}
				if (showD20 && SettingsHandler.showD20Info())
				{
					pcGenGUI.showMandatoryD20Info();
				}
			}

			if (skillReq.length() > 0)
			{
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					final Skill aSkill = (Skill) e1.next();
					if (("UNTRAINED".equals(skillReq) && aSkill.getUntrained().length() > 0 && aSkill.getUntrained().charAt(0) == 'Y') || skillReq.equals("ALL"))
					{
						aSkill.setRequired(true);
					}
				}
			}

			//
			// Add in the default deities (unless they're already there)
			//
			final List gDeities = Globals.getGlobalDeityList();
			if ((gDeities != null) && (gDeities.size() != 0))
			{
				for (Iterator e = gDeities.iterator(); e.hasNext();)
				{
					try
					{
						final String aLine = (String) e.next();
						final Deity aDeity = new Deity();
						DeityLoader.parseLine(aDeity, aLine, null, i++);
						if (Globals.getDeityNamed(aDeity.getName()) == null)
						{
							Globals.getDeityList().add(aDeity);
						}
					}
					catch (Exception exc)
					{
						//TODO Should this really be ignored?
					}
				}
			}


			//
			// Add catch-all feat for weapon proficiencies that cannot be granted as part of a Feat
			// eg. Simple weapons should normally be applied to the Simple Weapon Proficiency feat, but
			// it does not allow multiples (either all or nothing). So monk class weapons will get dumped
			// into this bucket.
			//
			final Feat aFeat = new Feat();
			String aLine = "Weapon Proficiency\tTYPE:General\tVISIBLE:Display\tMULT:YES\tSTACK:YES\tDESC:You attack with this specific weapon normally, non-proficiency incurs a -4 to hit penalty.\tSOURCE:PCGen Internal";
			FeatLoader.parseLine(aFeat, aLine, null, -1);
			Globals.getFeatList().add(aFeat);
			Globals.sortPObjectList(Globals.getFeatList());

			EquipmentModifier anObj = new EquipmentModifier();
			aLine = "Add Type\tKEY:ADDTYPE\tTYPE:ALL\tCOST:0\tNAMEOPT:NONAME\tSOURCE:PCGen Internal\tCHOOSE:COUNT=ALL|desired TYPE(s)|TYPE=EQTYPES";
			EquipmentModifierLoader.parseLine(anObj, aLine, null, -1);
			Globals.getModifierList().add(anObj);

			//
			// Add internal equipment modifier for adding weapon/armor types to equipment
			//
			anObj = new EquipmentModifier();
			aLine = "PCGENi_WEAPON\tTYPE:Weapon\tVISIBLE:No\tCHOOSE:DUMMY\tNAMEOPT:NONAME";
			EquipmentModifierLoader.parseLine(anObj, aLine, null, -1);
			Globals.getModifierList().add(anObj);

			anObj = new EquipmentModifier();
			aLine = "PCGENi_ARMOR\tTYPE:Armor\tVISIBLE:No\tCHOOSE:DUMMY\tNAMEOPT:NONAME";
			EquipmentModifierLoader.parseLine(anObj, aLine, null, -1);
			Globals.getModifierList().add(anObj);

			loadCustomItems();

			for (Iterator e2 = Globals.getRaceMap().values().iterator(); e2.hasNext();)
			{
				final Race aRace = (Race) e2.next();
				if (aRace.getMyTypeCount() == 0)
				{
					Logging.errorPrint("Race " + aRace.getName() + " has no type. Assuming Humanoid.");
					aRace.setTypeInfo("HUMANOID");
				}
			}

			//
			// Check all the weapons to see if they are either Melee or Ranged, to avoid
			// problems when we go to export/preview the character
			//
			for (Iterator e2 = Globals.getEquipmentList().iterator(); e2.hasNext();)
			{
				final Equipment aEq = (Equipment) e2.next();
				if (aEq.isWeapon() && !aEq.isMelee() && !aEq.isRanged())
				{
					throw new PersistenceLayerException("Weapon: " + aEq.getName() + " is neither Melee nor Ranged." + Constants.s_LINE_SEP + Constants.s_APPNAME + " cannot calculate \"to hit\" unless one of these is selected." + Constants.s_LINE_SEP + "Source: " + aEq.getSourceFile());
				}
			}

			if (!SettingsHandler.wantToLoadMasterworkAndMagic())
			{
				Globals.autoGenerateEquipment();
			}

			//
			// Test the algorithm for determining the base item from the
			// equipment name. Should load all equipment in order to test properly
			//
			//doEquipNameTest();


			//for (Iterator e = Globals.getKitInfo().iterator(); e.hasNext(); )
			//{
			//Kit aKit = (Kit)e.next();
			//System.err.println(aKit.getName() + ":" + aKit.getType());
			//}
		}
		finally
		{
			releaseFileData();
		}
	}

	private static String fixCurrentToken(int closeParens, int openParens, String currentToken, List cArrayList)
	{
		if (closeParens > openParens)
		{
			currentToken = currentToken.substring(0, currentToken.length() - 1);
		}
		cArrayList.add(currentToken.substring(9));
		if (closeParens > openParens)
		{
			currentToken = ")";
		}
		return currentToken;
	}

	/**
	 * Reads the source file for the campaign aCamp and adds the names
	 * of files to be loaded to raceFileLines, classFileLines etc.
	 **/
	private void loadCampaignFile(Campaign aCamp) throws PersistenceLayerException
	{
		aCamp.setIsLoaded(true);
		final String aString = aCamp.getSourceFile();
		boolean alreadyChosen = false;
		for (Iterator it = chosenCampaignSourcefiles.iterator(); it.hasNext();)
		{
			final String s = (String) it.next();
			if (s.equals(aString))
			{
				alreadyChosen = true;
				break;
			}
		}
		if (!alreadyChosen)
		{
			chosenCampaignSourcefiles.add(aString);
			SettingsHandler.getOptions().setProperty("pcgen.files.chosenCampaignSourcefiles", Utility.join(chosenCampaignSourcefiles, ','));
		}

		final List aArrayList = initFile(aString, -1, new ArrayList());
		initCampaignFromList(aArrayList, aString);
	}

	/**aArrayList contains an array of the lines as returned from initFile*/
	private void initCampaignFromList(List aArrayList, String aString) throws PersistenceLayerException
	{
		//
		// Try to remove ../ and ./ from file name
		// This should (hopefully) only fail if the file in question doesn't exist
		//
//		try
//		{
//			aString = new File(aString).getCanonicalPath();
//		}
//		catch (IOException exc)
//		{
		//TODO Should this really be ignored?
//		}

		if (Utility.isURL(aString))
		{
			loadPCCFileFromNet(aString);
		}

		final Campaign thisCampaign = Globals.getCampaignByFilename(aString, true);
		if (thisCampaign == null)
		{
			return;
		}

		final String sect15 = thisCampaign.getSection15Info();
		if (!"".equals(sect15))
		{
			Globals.getSection15().append("<br><b>Source Material:</b>").append(thisCampaign.getSourceInForm(Constants.SOURCELONG)).append("<br>");
			Globals.getSection15().append("<b>Section 15 Entry in Source Material:</b><br>");
			Globals.getSection15().append(sect15);
		}
		showOGL |= thisCampaign.isOGL();
		showD20 |= thisCampaign.isD20();
		showLicensed |= thisCampaign.isLicensed();
		if (thisCampaign.isLicensed())
		{
			licensesToDisplayString = licensesToDisplayString.append(thisCampaign.getLicense());
			licenseFiles.addAll(thisCampaign.getLicenseFiles());

		}

		final int rank = thisCampaign.getRank().intValue();
		final String tempGame = thisCampaign.getGame();
		String aLine;

		for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
		{
			aLine = (String) e1.next();
			if (aLine.startsWith("CAMPAIGN:") || aLine.startsWith("COPYRIGHT:") || aLine.startsWith("LICENSE:"))
			{
				continue;
			}
			if (aLine.startsWith("INFOTEXT:") || aLine.startsWith("SETTING:"))
			{
				continue;
			}
			if (aLine.startsWith("RANK:") || aLine.startsWith("GENRE:"))
			{
				//rank = Integer.parseInt(aLine.substring(5));
				continue;
			}
			if (aLine.startsWith("GAME:"))
			{
				//tempGame = aLine.substring(5);
				continue;
			}
			if (aLine.startsWith("GAMEMODE:"))
			{
				continue;
			}
			if (aLine.startsWith(Constants.s_TAG_TYPE))
			{
				continue;
			}
			if (aLine.startsWith("SOURCE:"))
			{
				continue;
			}
			if (aLine.startsWith("SHOWINMENU:"))
			{
				continue;
			}
			if (aLine.startsWith("OPTION:"))
			{
				continue;
			}
			if (aLine.startsWith("ISD20:"))
			{
				continue;
			}
			if (aLine.startsWith("ISOGL:"))
			{
				continue;
			}

			if (aLine.startsWith("LICENSED:"))
			{
				continue;
			}
			if (aLine.startsWith("PUBNAME"))
			{
				continue;
			}
			if (aLine.startsWith("BOOKTYPE"))
			{
				continue;
			}
			//check here for LST files to exclude from any further loading
			if (aLine.startsWith("LSTEXCLUDE:"))
			{
				final String lstList = aLine.substring(11);
				final StringTokenizer lstTok = new StringTokenizer(lstList, "|");
				while (lstTok.hasMoreTokens())
				{
					final String lstFilename = lstTok.nextToken();
					lstExcludeFiles.add(lstFilename);
				}
				continue;
			}

			/* Figure out where the PCC file came from that we're processing, so that
			 * we can prepend its path onto any LST file references (or PCC refs, for that matter).
			 * If the source line in question already has path info, then don't bother
			*/
			String aSource = "";
			int skipSymbol = 0;
			//if the line doesn't use "@" then it's a relative path, so we need to figure out what that path is
			if (aLine.indexOf('@') < 0)
			{
				int separatorLoc = aString.lastIndexOf(File.separator);
				//just in case aString was composed, rather than from a File object, check to see if the "\" character was used
				if (aString.lastIndexOf('\\') > separatorLoc)
				{
					separatorLoc = aString.lastIndexOf('\\');
				}
				if (aString.lastIndexOf('/') > separatorLoc)
				{
					separatorLoc = aString.lastIndexOf('/');
				}
				if (aString.indexOf("data") >= 0)
				{
					aSource = aString.substring(aString.indexOf("data") + 4, separatorLoc + 1);
				}
				else
				{
					aSource = aString.substring(0, separatorLoc + 1);
				}
			}
			//otherwise, we want to use this line almost as-is, we just need to signal the code below to drop the "@"
			else
			{
				skipSymbol = 1;
			}

			if (aLine.startsWith("PCC:"))
			{
				//
				// Don't add file if it's already there
				//
				addSource(pccFileLines, aSource, aLine, 4 + skipSymbol);
				//final String sourceName = aSource + aLine.substring(4 + skipSymbol);
				//if (!pccFileLines.contains(sourceName))
				//{
				//	pccFileLines.add(sourceName);
				//}
			}
			else if (aLine.startsWith("RACE:"))
			{
				addSource(raceFileLines, aSource, aLine, 5 + skipSymbol);
			}
			else if (aLine.startsWith("CLASS:"))
			{
				addSource(classFileLines, aSource, aLine, 6 + skipSymbol);
			}
			else if (aLine.startsWith("COMPANIONMOD:"))
			{
				addSource(companionmodFileLines, aSource, aLine, 13 + skipSymbol);
			}
			else if (aLine.startsWith("SKILL:"))
			{
				addSource(skillFileLines, aSource, aLine, 6 + skipSymbol);
			}
			else if (aLine.startsWith("FEAT:"))
			{
				addSource(featFileLines, aSource, aLine, 5 + skipSymbol);
			}
			else if (aLine.startsWith("DOMAIN:"))
			{
				addSource(domainFileLines, aSource, aLine, 7 + skipSymbol);
			}
			else if (aLine.startsWith("DEITY:"))
			{
				addSource(deityFileLines, aSource, aLine, 6 + skipSymbol);
			}
			else if (aLine.startsWith("SPELL:"))
			{
				addSource(spellFileLines, aSource, aLine, 6 + skipSymbol);
			}
			else if (aLine.startsWith("WEAPONPROF:"))
			{
				addSource(weaponProfFileLines, aSource, aLine, 11 + skipSymbol);
			}
			else if (aLine.startsWith("EQUIPMENT:"))
			{
				addSource(equipmentFileLines, aSource, aLine, 10 + skipSymbol);
			}
			else if (aLine.startsWith("LANGUAGE:"))
			{
				addSource(languageLines, aSource, aLine, 9 + skipSymbol);
			}
			else if (aLine.startsWith("CLASSSKILL:"))
			{
				addSource(classSkillFileLines, aSource, aLine, 11 + skipSymbol);
			}
			else if (aLine.startsWith("CLASSSPELL:"))
			{
				addSource(classSpellFileLines, aSource, aLine, 11 + skipSymbol);
			}
			else if (aLine.startsWith("REQSKILL:"))
			{
				addSource(reqSkillLines, aSource, aLine, 9 + skipSymbol);
			}
			else if (aLine.startsWith("TEMPLATE:"))
			{
				addSource(templateFileLines, aSource, aLine, 9 + skipSymbol);
			}
			else if (aLine.startsWith("EQUIPMOD:"))
			{
				addSource(equipmentModifierFileLines, aSource, aLine, 9 + skipSymbol);
			}
			else if (aLine.startsWith("COINS:"))
			{
				addSource(coinFileLines, aSource, aLine, 6 + skipSymbol);
			}
			else if (aLine.startsWith("KIT:"))
			{
				addSource(kitFileLines, aSource, aLine, 4 + skipSymbol);
			}
			else if (aLine.startsWith("BIOSET:"))
			{
				addSource(bioSetLines, aSource, aLine, 7 + skipSymbol);
			}
			else
			{
				throw new PersistenceLayerException("Invalid line: " + aLine + " in " + aString);
			}
		}

		if (rank <= 1)
		{
			SettingsHandler.setGame(tempGame);
		}
		Logging.debugPrint("RANK: " + rank + " GAME: " + SettingsHandler.getGame().getName() + " tempGame: " + tempGame);

		addCustomFilesToStartOfList();

		//GameModes modesMenu = new GameModes();
		//modesMenu.updateMenu();
	}

	private void addCustomFilesToStartOfList()
	{
		String tempGame;
		//
		// Add the custom bioset file to the start of the list if it exists
		//
		if (new File(CustomData.customBioSetFilePath(true)).exists())
		{
			tempGame = CustomData.customBioSetFilePath(false);
			bioSetLines.remove(tempGame);
			bioSetLines.add(0, tempGame);
		}

		//
		// Add the custom class file to the start of the list if it exists
		//
		if (new File(CustomData.customClassFilePath(true)).exists())
		{
			tempGame = CustomData.customClassFilePath(false);
			classFileLines.remove(tempGame);
			classFileLines.add(0, tempGame);
		}

		//
		// Add the custom deity file to the start of the list if it exists
		//
		if (new File(CustomData.customDeityFilePath(true)).exists())
		{
			tempGame = CustomData.customDeityFilePath(false);
			deityFileLines.remove(tempGame);
			deityFileLines.add(0, tempGame);
		}

		//
		// Add the custom domain file to the start of the list if it exists
		//
		if (new File(CustomData.customDomainFilePath(true)).exists())
		{
			tempGame = CustomData.customDomainFilePath(false);
			domainFileLines.remove(tempGame);
			domainFileLines.add(0, tempGame);
		}

		//
		// Add the custom feat file to the start of the list if it exists
		//
		if (new File(CustomData.customFeatFilePath(true)).exists())
		{
			tempGame = CustomData.customFeatFilePath(false);
			featFileLines.remove(tempGame);
			featFileLines.add(0, tempGame);
		}

		//
		// Add the custom language file to the start of the list if it exists
		//
		if (new File(CustomData.customLanguageFilePath(true)).exists())
		{
			tempGame = CustomData.customLanguageFilePath(false);
			languageLines.remove(tempGame);
			languageLines.add(0, tempGame);
		}

		//
		// Add the custom race file to the start of the list if it exists
		//
		if (new File(CustomData.customRaceFilePath(true)).exists())
		{
			tempGame = CustomData.customRaceFilePath(false);
			raceFileLines.remove(tempGame);
			raceFileLines.add(0, tempGame);
		}

		//
		// Add the custom skill file to the start of the list if it exists
		//
		if (new File(CustomData.customSkillFilePath(true)).exists())
		{
			tempGame = CustomData.customSkillFilePath(false);
			skillFileLines.remove(tempGame);
			skillFileLines.add(0, tempGame);
		}

		//
		// Add the custom spell file to the start of the list if it exists
		//
		if (new File(CustomData.customSpellFilePath(true)).exists())
		{
			tempGame = CustomData.customSpellFilePath(false);
			spellFileLines.remove(tempGame);
			spellFileLines.add(0, tempGame);
		}

		//
		// Add the custom template file to the start of the list if it exists
		//
		if (new File(CustomData.customTemplateFilePath(true)).exists())
		{
			tempGame = CustomData.customTemplateFilePath(false);
			templateFileLines.remove(tempGame);
			templateFileLines.add(0, tempGame);
		}
	}

	/**
	 * Sets the options specified in the campaign aCamp.
	 **/
	private static void setCampaignOptions(Campaign aCamp)
	{
		final Properties options = aCamp.getOptions();
		if (options != null)
		{
			for (Enumeration e = options.propertyNames(); e.hasMoreElements();)
			{
				final String key = (String) e.nextElement();
				final String value = options.getProperty(key);
				SettingsHandler.setPCGenOption(key, value);
			}
		}
	}

	private static void addSource(List sourceList, String relativePath, String file, int skipNum)
	{
		file = file.substring(skipNum);
		if (Utility.isURL(file))
		{
			final int protoSep = file.indexOf(":");
			final String protocol = file.substring(0, protoSep);
			if (protocol.equals("file"))
			{
				final String path = file.substring(protoSep + 1);
				file = protocol + ":" + relativePath + path;
			}
			sourceList.add(file);
		}
		else
		{
			sourceList.add(relativePath + file);
		}
	}

	/**
	 * Takes aString of form @\data\blah\blah\file.name
	 *                    or file:blah/blah/filename.lst
	 * and returns a correctly formated absolute URL
	 **/
	private String parseFileName(String aString)
	{
		String fName = null;

		try
		{
			if (Utility.isURL(aString))
			{
				fName = Utility.fixURLPath(SettingsHandler.getPccFilesLocation().getAbsolutePath(), aString);
			}
			else if (Utility.isURL(aString.substring(1)))
			{
				fName = Utility.fixURLPath(SettingsHandler.getPccFilesLocation().getAbsolutePath(), aString.substring(1));
			}
			// if it doesn't start with "@" then it may be
			// a relative path, so make it absolute
			else if (!aString.startsWith("@"))
			{
				int separatorLoc = aString.lastIndexOf(File.separator);
				// just in case aString was composed, rather than from
				// a File object, check for the "\" character
				if (aString.lastIndexOf('\\') > separatorLoc)
				{
					separatorLoc = aString.lastIndexOf('\\');
				}
				if (aString.lastIndexOf('/') > separatorLoc)
				{
					separatorLoc = aString.lastIndexOf('/');
				}
				if (aString.indexOf("data") >= 0)
				{
					fName = aString.substring(aString.indexOf("data") + 4, separatorLoc + 1);
				}
				else
				{
					fName = aString.substring(0, separatorLoc + 1);
				}
				fName = Utility.fixFilenamePath(SettingsHandler.getPccFilesLocation().getAbsolutePath() + fName);
			}
			else if (aString.startsWith("@"))
			{
				fName = Utility.fixFilenamePath(SettingsHandler.getPccFilesLocation().getAbsolutePath() + aString.substring(1));
			}
		}
		catch (Exception e)
		{
			Logging.errorPrint("PersistanceLayer", e);
		}
		return fName;
	}

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are in LstConstants.java
	 * <p>
	 * The file is opened and read. Lines are parsed by an object
	 * of the relevant type (based on <code>type</code> above), and
	 * then added to the array list.
	 *
	 * @param argFileName    name of the file to load from
	 * @param fileType    type of the file (see above for types).
	 * @param aList       <code>ArrayList</code> with existing data.
	 *                    The new data is appended to this.
	 * @return <code>aList</code>, with new data appended
	 **/
	public List initFile(String argFileName, int fileType, List aList) throws PersistenceLayerException
	{
		return initFile(argFileName, fileType, aList, true);
	}

	private List initFile(String argFileName, int fileType, List aList, boolean throwException) throws PersistenceLayerException
	{
		final byte[] inputLine;
		//Don't changes the slashes if this is a url.
		String fileName = argFileName;
		if (!Utility.isURL(fileName))
		{
			fileName = Utility.fixFilenamePath(fileName);
		}
		PObject anObj = null;
		final String aString;
		String aLine = "";
		lineNum = 0;
		Campaign sourceCampaign = null;
		final Map sourceMap = new HashMap();
		String prevLine = "";

		// preload these, don't depending on the try loop
		switch (fileType)
		{
			case LstConstants.COINS_TYPE:
				Globals.getDenominationList().parseFiles(fileName);
				return aList;

			case LstConstants.STATNAME_TYPE:
				Globals.setAttribLong(null);
				Globals.setAttribShort(null);
				Globals.setAttribRoll(null);
				break;

			default:
				//Do nothing on default, only special cases should be handled here.
				break;
		}

		try
		{
			final URL aURL;
			//Common case first - URL (because this includes file:/ - which most stuff gets translated to)
			if (Utility.isURL(fileName))
			{
				//only load local urls, unless loading of URLs is allowed
				if (!Utility.isNetURL(fileName) || SettingsHandler.isLoadURLs())
				{
					aURL = new URL(fileName);
					if (aURL.getProtocol().equals("file"))
					{
						//TODO: What is this?
					}
					//Object content = aURL.getContent();
					//Class contentClass = content.getClass();
					final InputStreamReader ir = new InputStreamReader(aURL.openStream());
					final StringBuffer sb = new StringBuffer();
					final char[] b = new char[512];
					int n;
					while ((n = ir.read(b)) > 0)
					{
						sb.append(b, 0, n);
					}
					aString = sb.toString();
				}
				else
				{
					//Just to protect people from using web sources without their knowledge, we added a preference.
					throw new PersistenceLayerException("Preferences are currently set to not allow loading of sources from web links.  " + fileName + " is a web link");
				}
			}
			//Uncommon case Plain Old File Name
			else
			{
				final File aFile = new File(fileName);
				final InputStream aStream = new FileInputStream(aFile);
				final int length = (int) aFile.length();
				aURL = aFile.toURL();
				inputLine = new byte[length];
				aStream.read(inputLine, 0, length);
				aString = new String(inputLine);
				aStream.close();
			}

			/*
			 * Need to keep the Windows line separator as newline
			 * delimiter to ensure cross-platform portability.
			 *
			 * author: Thomas Behr 2002-11-13
			 */
			final String newlinedelim = "\r\n";
			final StringTokenizer newlineStr = new StringTokenizer(aString, newlinedelim, false);
			setCurrentSource("");
			String nameString = "";
			if (fileType == LstConstants.BIO_TYPE || fileType == LstConstants.BIO2_TYPE)
			{
				BioSetLoader.clear();
			}
			if (fileType == LstConstants.EQUIPSLOT_TYPE)
			{
				Globals.getEquipSlotList().clear();
			}
			if (fileType == LstConstants.SIZEADJUSTMENT_TYPE)
			{
				Globals.getSizeAdjustmentList().clear();
			}
			if (fileType == LstConstants.PAPERINFO_TYPE)
			{
				Globals.getPaperInfo().clear();
			}
			int traitType = -1;
			while (newlineStr.hasMoreTokens())
			{
				boolean isModItem;
				final boolean isForgetItem;
				aLine = newlineStr.nextToken();
				++lineNum;
				//
				// What the hell kinda of syntax is this?
				// Fix the damn sovereignstone files!
				// JC: 07-12-2002
				//
				// This was added when we were first removing the magic and masterwork items from the equipment .lsts.
				// We separated the equipment in question into their own files. In the .pcc file these were then
				// prefixed with #;. A setting was then added to the options menu to load these items or not.
				// This allowed us to keep the old data in case the new equipment-generating code did not work.
				// It can probably be removed--does anything still use it?
				// Byngl: Aug 13, 2002
				//
				if (aLine.startsWith("#;"))
				{
					if (!SettingsHandler.wantToLoadMasterworkAndMagic())
					{
						continue;
					}
					aLine = aLine.substring(2);
				}
				if (aLine.startsWith("CAMPAIGN:") &&
					(fileType != LstConstants.CAMPAIGN_TYPE && fileType != -1))
				{
					sourceCampaign = Globals.getCampaignNamed(aLine.substring(9));
					continue;
				}
				//
				// Ignore commented-out lines
				// and empty lines
				if (aLine.length() == 0)
				{
					continue;
				}
				if (fileType != LstConstants.CAMPAIGN_TYPE && aLine.length() > 0 && aLine.charAt(0) == '#')
				{
					continue;
				}

				if (aLine.startsWith("SOURCE") && fileType != LstConstants.CAMPAIGN_TYPE)
				{
//					setCurrentSource(aLine);
					final StringTokenizer sTok = new StringTokenizer(aLine, "|", false);
					while (sTok.hasMoreTokens())
					{
						final String arg = sTok.nextToken();
						final String key = arg.substring(6, arg.indexOf(":"));
						final String val = arg.substring(arg.indexOf(":") + 1);
						sourceMap.put(key, val);
					}
					continue;
				}
				String copyName = null; // used for .COPY= cases
				// check for special case of CLASS:name.MOD
				isModItem = aLine.endsWith(".MOD");
				if (isModItem && aLine.startsWith("CLASS:"))
				{
					nameString = aLine.substring(0, aLine.length() - 4);
				}
				isForgetItem = aLine.endsWith(".FORGET");
				if (isForgetItem)
				{
					nameString = aLine.substring(0, aLine.length() - 7);
				}
				// first field is usually name (only exception is class-level lines
				// see if name ends with .MOD, if so, use already-existing item instead
				// of creating a new one. merton_monk@yahoo.com 2/8/2002
				if (aLine.indexOf('\t') > 2)
				{
					final StringTokenizer t = new StringTokenizer(aLine, "\t", false);
					nameString = t.nextToken();
					isModItem = nameString.endsWith(".MOD");
					if (isModItem)
					{
						nameString = nameString.substring(0, nameString.length() - 4);
					}
					else if (nameString.indexOf(".COPY=") > 0)
					{
						copyName = nameString.substring(nameString.indexOf(".COPY=") + 6);
						nameString = nameString.substring(0, nameString.indexOf(".COPY="));
					}
				}
				switch (fileType)
				{
					case LstConstants.RACE_TYPE:
						anObj = intFileTypeRace(isForgetItem, nameString, fileType, isModItem, copyName, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.CLASS_TYPE:
						anObj = initFileTypeClass(aLine, nameString, isForgetItem, fileType, copyName, anObj, aList, aURL, isModItem, fileName, sourceCampaign, sourceMap);
						break;

					case LstConstants.COMPANIONMOD_TYPE:
						anObj = initFileTypeCompanionMod(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.SKILL_TYPE:
						anObj = initFileTypeSkill(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.FEAT_TYPE:
						anObj = initFileTypeFeat(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.DOMAIN_TYPE:
						anObj = initFileTypeDomain(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.DEITY_TYPE:
						anObj = initFileTypeDeity(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.SPELL_TYPE:
						anObj = initFileTypeSpell(isForgetItem, nameString, fileType, anObj, isModItem, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.WEAPONPROF_TYPE:
						anObj = initFileTypeWeaponProf(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.SCHOOLS_TYPE:
						Globals.getSchoolsList().add(aLine);
						break;

					case LstConstants.TRAITS_TYPE:
						traitType = initFileTypeTraits(aLine, traitType);
						break;

					case LstConstants.LOCATIONS_TYPE:
						traitType = initFileTypeLocations(aLine, traitType);
						break;

					case LstConstants.EQUIPMENT_TYPE:
						anObj = initFileTypeEquipment(isForgetItem, nameString, fileType, isModItem, copyName, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.LANGUAGE_TYPE:
						anObj = initFileTypeLanguage(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine);
						break;

					case LstConstants.LOAD_TYPE:
						Globals.getLoadStrings().add(aLine);
						break;

					case LstConstants.SPECIAL_TYPE:
						initFileTypeSpecial(aLine, aURL);
						break;

					case LstConstants.EQUIPSLOT_TYPE:
						initFileTypeEquipSlot(aLine, aURL);
						break;

					case LstConstants.SIZEADJUSTMENT_TYPE:
						initFileTypeSizeAdjustment(nameString, aLine, aURL);
						break;

					case LstConstants.STATNAME_TYPE:
						anObj = initFileTypeStatName(aString, newlinedelim, aLine, anObj, aList, aURL);
						break;

					case LstConstants.MISCGAMEINFO_TYPE:
//						parseMiscGameInfoLine(aLine, aFile, lineNum);
						break;

					case LstConstants.POINTBUY_TYPE:
						parsePointBuyLine(aLine, aURL, lineNum);
						break;

					case -1: // if we're in the process of loading campaigns/sources when
						// another source is loaded via PCC:, then it's fileType=-1
					case LstConstants.CLASSSKILL_TYPE:
					case LstConstants.CLASSSPELL_TYPE:
					case LstConstants.REQSKILL_TYPE:
						aList.add(aLine);
						break;

					case LstConstants.CAMPAIGN_TYPE:
						anObj = initFileTypeCampaign(anObj, aLine, aURL);
						break;

					case LstConstants.TEMPLATE_TYPE:
						anObj = initFileTypeTemplate(isForgetItem, nameString, fileType, isModItem, anObj, sourceCampaign, sourceMap, aList, aLine, aURL);
						break;

					case LstConstants.BONUS_TYPE:
						Globals.getBonusStackList().add(aLine.toUpperCase());
						break;

					case LstConstants.EQMODIFIER_TYPE:
						anObj = initFileTypeEqModifier(sourceCampaign, sourceMap, aLine, aURL, aList);
						break;

					case LstConstants.PAPERINFO_TYPE:
						initFileTypePaperInfo(aLine, aURL);
						break;

//					case LstConstants.HELPCONTEXT_TYPE:
//						Globals.addHelpContextFileList(aLine);
//						break;

					case LstConstants.KIT_TYPE:
						if (aLine.startsWith("REGION:"))
						{
							prevLine = aLine.substring(7);
							continue;
						}
						if (prevLine.length() == 0)
						{
							throw new PersistenceLayerException("Illegal kit info " + aURL.toString() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
						}

						if (aLine.startsWith("STARTPACK:"))
						{
							anObj = new Kit(prevLine);
							anObj.setSourceCampaign(sourceCampaign);
							anObj.setSourceMap(sourceMap);
							Globals.getKitInfo().add(anObj);
						}
						if (anObj != null)
						{
							KitLoader.parseLine((Kit) anObj, aLine, aURL, lineNum);
						}
						break;

					case LstConstants.BIO_TYPE:
					case LstConstants.BIO2_TYPE:
						BioSetLoader.parseLine(fileType, aLine);
						break;

					default:
						Logging.errorPrint("In LstSystemLoader.initValue the fileType " + fileType + " is not handled.");
						break;
				}

				//
				// Save the source file in object
				//
				switch (fileType)
				{
					case LstConstants.RACE_TYPE:
					case LstConstants.CLASS_TYPE:
					case LstConstants.SKILL_TYPE:
					case LstConstants.FEAT_TYPE:
					case LstConstants.DOMAIN_TYPE:
					case LstConstants.DEITY_TYPE:
					case LstConstants.SPELL_TYPE:
					case LstConstants.WEAPONPROF_TYPE:
					case LstConstants.EQUIPMENT_TYPE:
					case LstConstants.CAMPAIGN_TYPE:
					case LstConstants.EQMODIFIER_TYPE:
					case LstConstants.KIT_TYPE:
						if (anObj != null)
						{
							anObj.setSourceFile(aURL.toString());
						}
						break;

					case LstConstants.SCHOOLS_TYPE:
					case LstConstants.TRAITS_TYPE:
					case LstConstants.LANGUAGE_TYPE:
					case LstConstants.LOAD_TYPE:
					case LstConstants.SPECIAL_TYPE:
					case LstConstants.CLASSSKILL_TYPE:
					case LstConstants.CLASSSPELL_TYPE:
					case LstConstants.REQSKILL_TYPE:
					case LstConstants.TEMPLATE_TYPE:
					case LstConstants.BONUS_TYPE:
					case LstConstants.EQUIPSLOT_TYPE:
					case LstConstants.SIZEADJUSTMENT_TYPE:
					case LstConstants.STATNAME_TYPE:
					case LstConstants.MISCGAMEINFO_TYPE:
					case LstConstants.POINTBUY_TYPE:
					case LstConstants.PAPERINFO_TYPE:
					case LstConstants.COMPANIONMOD_TYPE:
//					case LstConstants.HELPCONTEXT_TYPE:
					case LstConstants.BIO_TYPE:
					case LstConstants.BIO2_TYPE:
					case LstConstants.LOCATIONS_TYPE:
					case -1:
						break;
					default:
						Logging.errorPrint("In LstSystemLoader.initValue the fileType " + fileType + " is not handled.");
						break;
				}
			}
			//
			// Need to do this here not in getOptionsFromProperties(), as the paperinfo names will not have been loaded
			//
			if (fileType == LstConstants.PAPERINFO_TYPE)
			{
				Globals.selectPaper(SettingsHandler.getPCGenOption("paperName", "A4"));
			}
		}
		catch (IOException e)
		{
			if (!"pcgen.ini".equals(fileName) && throwException)
			{
				//line separator in Constants.s_LINE_SEP not displaying correctly
				throw new PersistenceLayerException(e, "ERROR:" + fileName + "\n" + "error " + aLine + "\n" + "Exception type:" + e.getClass().getName() + "\n" + "Message:" + e.getMessage());
			}
		}
		setCurrentSource("");
		return aList;
	}

	private PObject initFileTypeClass(String aLine, String nameString, boolean forgetItem, int fileType, String copyName, PObject anObj, List aList, final URL aURL, boolean modItem, String fileName, Campaign sourceCampaign, Map sourceMap) throws PersistenceLayerException
	{
		if (aLine.startsWith("CLASS:"))
		{
			// Now that we've established the line begins with "CLASS:" and is therefore
			// not a level line but for the whole class, let's chop that out, so that we can
			// continue on like all the other file types.  sk4p 10 Dec 2002

			//
			// Possible if someone has got the syntax wrong...
			// eg. CLASS:blah.REMOVE instead of CLASS:blah.FORGET
			//
			if (nameString.length() < 7)
			{
				Logging.errorPrint("Could not process " + aLine);
				return anObj;
			}
			final String classNameString = nameString.substring(6);

			if (forgetItem)
			{
				forgetItem(Globals.getClassNamed(classNameString), classNameString, fileType);
				return anObj;
			}

			if (copyName != null)
			{
				anObj = Globals.getClassNamed(classNameString);
				if (anObj != null)
				{
					try
					{
						anObj = (PObject) anObj.clone();
					}
					catch (CloneNotSupportedException exc)
					{
						GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					anObj.setName(copyName);
					aList.add(anObj);
					PCClassLoader.parseLine((PCClass) anObj, aLine, aURL, lineNum);
				}
				else
				{
					Logging.errorPrint("Could not copy " + classNameString + " to create " + copyName);
				}
				return anObj;
			}

			// If we've gotten to here, there's no copy going on
			anObj = Globals.getClassNamed(classNameString);
			if (!modItem)
			{
				if (anObj != null)
				{
					Logging.debugPrint("Redefining " + classNameString + " in " + fileName);
				}
				else
				{
					anObj = Globals.getClassNamed(classNameString, aList);
				}
			}
			else
			{
				if (anObj == null)
				{
					anObj = Globals.getClassNamed(classNameString, aList);
				}
				if (anObj == null)
				{
					// If it's not loaded yet, add to modLines for later MODing
					modLines.add(aLine);
					modFileType.add(new Integer(fileType));
					Logging.debugPrint("No class " + classNameString + " defined yet in " + fileName);
				}
			}

			if (!modItem && anObj == null)
			{
				anObj = new PCClass();
				anObj.setSourceCampaign(sourceCampaign);
				anObj.setSourceMap(sourceMap);
				aList.add(anObj);
			}
		}

		if (anObj != null)
		{
			PCClassLoader.parseLine((PCClass) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private void initFileTypePaperInfo(String aLine, final URL aURL) throws PersistenceLayerException
	{
		final PaperInfo psize = new PaperInfo();
		PaperInfoLoader.parseLine(psize, aLine, aURL, lineNum);
		Globals.getPaperInfo().add(psize);
		return;
	}

	private PObject initFileTypeEqModifier(Campaign sourceCampaign, Map sourceMap, String aLine, final URL aURL, List aList) throws PersistenceLayerException
	{
		final PObject anObj;
		anObj = new EquipmentModifier();
		anObj.setSourceCampaign(sourceCampaign);
		anObj.setSourceMap(sourceMap);
		EquipmentModifierLoader.parseLine((EquipmentModifier) anObj, aLine, aURL, lineNum);
		aList.add(anObj);
		return anObj;
	}

	private PObject initFileTypeTemplate(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getTemplateNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new PCTemplate();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getTemplateNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			PCTemplateLoader.parseLine((PCTemplate) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeCampaign(PObject anObj, String aLine, final URL aURL)
	{
		if (anObj == null)
		{
			anObj = new Campaign();
			Globals.getCampaignList().add(anObj);
		}
		CampaignLoader.parseLine((Campaign) anObj, aLine, aURL);
		// if a PCC: line exists inside a CAMPAIGN_TYPE file, it might point to another .pcc file
		// which needs to be loaded/parsed after we finish loading all CAMPAIGN_TYPE files in this directory
		// so just add it to the list and process it later
		if (aLine.startsWith("PCC:"))
		{
			final String fName = parseFileName(aLine.substring(4));
			// only add if not already there
			if ((fName != null) && !recursivePCCFileList.contains(fName))
			{
				recursivePCCFileList.add(fName);
			}
		}
		return anObj;
	}

	private PObject initFileTypeStatName(String aString, final String newlinedelim, String aLine, PObject anObj, List aList, final URL aURL) throws PersistenceLayerException
	{
		//
		// Count the number of lines that start with NAME: to get the number of stats.
		// This allows us to add comments and other information to the file
		//
		if (lineNum == 0)
		{
			Globals.s_ATTRIBLONG = null;
			Globals.getCheckList().clear();
			Globals.getAlignmentList().clear();
		}
		if (Globals.s_ATTRIBLONG == null)
		{
			final StringTokenizer tempNewlineStr = new StringTokenizer(aString, newlinedelim, false);
			int statCount = 0;
			while (tempNewlineStr.hasMoreTokens())
			{
				final String lString = tempNewlineStr.nextToken();
				if (lString.startsWith("STATNAME:"))
				{
					++statCount;
				}
			}

			Globals.getStatList().clear();
			Globals.setAttribLong(new String[statCount]);
			Globals.setAttribShort(new String[statCount]);
			Globals.setAttribRoll(new boolean[statCount]);
		}
		if (aLine.startsWith("STATNAME:"))
		{
			anObj = new PCStat();
			aList.add(anObj);
			PCStatLoader.parseLine((PCStat) anObj, aLine, aURL, aList.size());
		}
		else if (aLine.startsWith("CHECKNAME:"))
		{
			anObj = new PObject();
			PCCheckLoader.parseLine(anObj, aLine, aURL, Globals.getCheckList().size());
		}
		else if (aLine.startsWith("BONUSSPELLLEVEL:"))
		{
			BonusSpellLoader.parseLine(aLine, aURL, Globals.getBonusSpellMap().size());
		}
		else if (aLine.startsWith("ALIGNMENTNAME:"))
		{
			anObj = new PCAlignment();
			PCAlignmentLoader.parseLine((PCAlignment) anObj, aLine, aURL, Globals.getAlignmentList().size());
		}
		return anObj;
	}

	private void initFileTypeSizeAdjustment(String nameString, String aLine, final URL aURL) throws PersistenceLayerException
	{
		SizeAdjustment sadj = null;
		if (nameString.startsWith("SIZENAME:"))
		{
			sadj = Globals.getSizeAdjustmentNamed(nameString.substring(9));
			if (sadj == null)
			{
				sadj = new SizeAdjustment();
				Globals.getSizeAdjustmentList().add(sadj);
			}
		}
		if (sadj == null)
		{
			return;
		}
		SizeAdjustmentLoader.parseLine(sadj, aLine, aURL, lineNum);
		return;
	}

	private void initFileTypeEquipSlot(String aLine, final URL aURL) throws PersistenceLayerException
	{
		final EquipSlot eqSlot = new EquipSlot();
		if (aLine.startsWith("NUMSLOTS:"))
		{
			final StringTokenizer eTok = new StringTokenizer(aLine.substring(9), SystemLoader.TAB_DELIM, false);
			while (eTok.hasMoreTokens())
			{
				// parse the default number of each type
				final String cString = eTok.nextToken().trim();
				final StringTokenizer cTok = new StringTokenizer(cString, ":", false);
				if (cTok.countTokens() == 2)
				{
					final String eqSlotType = cTok.nextToken();
					final String aNum = cTok.nextToken();
					Globals.setEquipSlotTypeCount(eqSlotType, aNum);
				}
			}
		}
		else
		{
			EquipSlotLoader.parseLine(eqSlot, aLine, aURL, lineNum);
		}
		Globals.getEquipSlotList().add(eqSlot);
		return;
	}

	private void initFileTypeSpecial(String aLine, final URL aURL) throws PersistenceLayerException
	{
		final SpecialAbility sp = new SpecialAbility();
		SpecialAbilityLoader.parseLine(sp, aLine, aURL, lineNum);
		Globals.getSpecialsList().add(sp);
		return;
	}

	private PObject initFileTypeLanguage(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine)
	{
		if (forgetItem)
		{
			forgetItem(Globals.getLanguageNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new Language();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getLanguageNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			LanguageLoader.parseLine((Language) anObj, aLine);
//							Language aLang = new Language();
//							aLang.parseLine(aLine, aFile, lineNum);
//							aList.add(aLang);
		}
		return anObj;
	}

	private PObject initFileTypeEquipment(boolean forgetItem, String nameString, int fileType, boolean modItem, String copyName, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getEquipmentNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			if (copyName == null)
			{
				anObj = new Equipment();
				anObj.setSourceCampaign(sourceCampaign);
				anObj.setSourceMap(sourceMap);
			}
			else
			{
				anObj = Globals.getEquipmentNamed(nameString);
				if (anObj != null)
				{
					try
					{
						anObj = (PObject) anObj.clone();
					}
					catch (CloneNotSupportedException exc)
					{
						GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					anObj.setName(copyName);
					Logging.debugPrint("Setting name of copy to " + copyName);
				}
				else
				{
					Logging.errorPrint("Could not copy " + nameString + " to create " + copyName);
				}
			}
			if (anObj != null)
			{
				aList.add(anObj);
			}
		}
		else
		{
			anObj = Globals.getEquipmentNamed(nameString);
			if (anObj == null)
			{
				anObj = Globals.getEquipmentNamed(nameString, aList);
			}
		}
		if (modItem && (anObj == null))
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			EquipmentLoader.parseLine((Equipment) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private int initFileTypeLocations(String aLine, int traitType)
	{
		if (aLine.charAt(0) != '[')
		{
			switch (traitType)
			{
				case 0:
					Globals.getLocationList().add(aLine);
					break;
				case 1:
					Globals.getBirthplaceList().add(aLine);
					break;
				case 2:
					Globals.getCityList().add(aLine);
					break;
				default:
					break;
			}
		}
		else
		{
			if (aLine.startsWith("[LOCATION]"))
			{
				traitType = 0;
			}
			else if (aLine.startsWith("[BIRTHPLACE]"))
			{
				traitType = 1;
			}
			else if (aLine.startsWith("[CITY]"))
			{
				traitType = 2;
			}
			else
			{
				traitType = -1;
			}
		}
		return traitType;
	}

	private int initFileTypeTraits(String aLine, int traitType)
	{
		if (aLine.charAt(0) != '[')
		{
			switch (traitType)
			{
				case 0:
					Globals.getTraitList().add(aLine);
					break;
				case 1:
					Globals.getSpeechList().add(aLine);
					break;
				case 2:
					Globals.getPhraseList().add(aLine);
					break;
				case 3:
					Globals.getPhobiaList().add(aLine);
					break;
				case 4:
					Globals.getInterestsList().add(aLine);
					break;
				case 5:
					Globals.getHairStyleList().add(aLine);
					break;
					//case 6:
					//	break;
				default:
					break;
			}
		}
		else
		{
			if (aLine.startsWith("[TRAIT]"))
			{
				traitType = 0;
			}
			else if (aLine.startsWith("[SPEECH]"))
			{
				traitType = 1;
			}
			else if (aLine.startsWith("[PHRASE]"))
			{
				traitType = 2;
			}
			else if (aLine.startsWith("[PHOBIA]"))
			{
				traitType = 3;
			}
			else if (aLine.startsWith("[INTERESTS]"))
			{
				traitType = 4;
			}
			else if (aLine.startsWith("[HAIRSTYLE]"))
			{
				traitType = 5;
			}
			//else if (aLine.startsWith("[HAIRLENGTH]"))
			//{
			//	traitType = 6;
			//}
			else
			{
				traitType = -1;
			}
		}
		return traitType;
	}

	private PObject initFileTypeWeaponProf(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getWeaponProfNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new WeaponProf();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getWeaponProfNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			WeaponProfLoader.parseLine((WeaponProf) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeSpell(boolean forgetItem, String nameString, int fileType, PObject anObj, boolean modItem, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getSpellNamed(nameString), nameString, fileType);
			return anObj;
		}

		anObj = Globals.getSpellNamed(nameString);
		if (!modItem && (anObj != null))
		{
			anObj = new Spell();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			final Object obj = Globals.getSpellMap().get(nameString);
			if (obj instanceof ArrayList)
			{
				((ArrayList) obj).add(anObj);
			}
			else
			{
				final ArrayList a = new ArrayList();
				a.add(obj);
				a.add(anObj);
				Globals.getSpellMap().put(nameString, a);
			}
		}

		if (!modItem && (anObj == null))
		{
			anObj = new Spell();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}

		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			SpellLoader.parseLine((Spell) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeDeity(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getDeityNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new Deity();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getDeityNamed(nameString);
			if (anObj == null)
			{
				anObj = Globals.getDeityNamed(nameString, aList);
			}
			if (anObj == null)
			{
				modLines.add(aLine);
				modFileType.add(new Integer(fileType));
			}
		}
		if (anObj != null)
		{
			DeityLoader.parseLine((Deity) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeDomain(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getDomainNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new Domain();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getDomainNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			DomainLoader.parseLine((Domain) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeFeat(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getFeatNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new Feat();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getFeatNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			FeatLoader.parseLine((Feat) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeSkill(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getSkillNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new Skill();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getSkillNamed(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			SkillLoader.parseLine((Skill) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject initFileTypeCompanionMod(boolean forgetItem, String nameString, int fileType, boolean modItem, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL)
	{
		if (forgetItem)
		{
			forgetItem(Globals.getCompanionMod(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			anObj = new CompanionMod();
			anObj.setSourceCampaign(sourceCampaign);
			anObj.setSourceMap(sourceMap);
			aList.add(anObj);
		}
		else
		{
			anObj = Globals.getCompanionMod(nameString);
		}
		if (anObj == null)
		{
			modLines.add(aLine);
			modFileType.add(new Integer(fileType));
		}
		if (anObj != null)
		{
			CompanionModLoader.parseLine((CompanionMod) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private PObject intFileTypeRace(boolean forgetItem, String nameString, int fileType, boolean modItem, String copyName, PObject anObj, Campaign sourceCampaign, Map sourceMap, List aList, String aLine, final URL aURL) throws PersistenceLayerException
	{
		if (forgetItem)
		{
			forgetItem(Globals.getRaceNamed(nameString), nameString, fileType);
			return anObj;
		}

		if (!modItem)
		{
			if (copyName == null)
			{
				anObj = new Race();
				anObj.setSourceCampaign(sourceCampaign);
				anObj.setSourceMap(sourceMap);
			}
			else
			{
				anObj = Globals.getRaceNamed(nameString);
				if (anObj != null)
				{
					try
					{
						anObj = (PObject) anObj.clone();
					}
					catch (CloneNotSupportedException exc)
					{
						GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					anObj.setName(copyName);
					Logging.debugPrint("Setting name of copy to ", copyName);
				}
				else
				{
					Logging.errorPrint("Could not copy " + nameString + " to create " + copyName);
				}
			}
			if (anObj != null)
			{
				aList.add(anObj);
			}
		}
		else
		{
			anObj = Globals.getRaceNamed(nameString);
			if (anObj == null)
			{
				for (Iterator ir = aList.iterator(); ir.hasNext();)
				{
					final Race aRace = (Race) ir.next();
					if (aRace.getName().equals(nameString))
					{
						Logging.debugPrint("Found " + nameString);
						anObj = aRace;
						break;
					}
				}
			}
			if (anObj == null)
			{
				modLines.add(aLine);
				modFileType.add(new Integer(fileType));
			}
		}
		if (anObj != null)
		{
			RaceLoader.parseLine((Race) anObj, aLine, aURL, lineNum);
		}
		return anObj;
	}

	private List adds(final int lineType, final List aArrayList)
	{
		String aClassName = "";
		for (int i = 0; i < aArrayList.size(); ++i)
		{
			switch (lineType)
			{
				case LstConstants.RACE_TYPE:
					final Race race = (Race) aArrayList.get(i);
					Globals.getRaceMap().put(race.getKeyName(), race);
					break;
				case LstConstants.CLASS_TYPE:
					final PCClass bClass = Globals.getClassKeyed(((PCClass) aArrayList.get(i)).getKeyName());
					if (bClass == null)
					{
						Globals.getClassList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.COMPANIONMOD_TYPE:
					final CompanionMod cMod = Globals.getCompanionMod(((CompanionMod) aArrayList.get(i)).getKeyName());
					if (cMod == null)
					{
						Globals.getCompanionModList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.SKILL_TYPE:
					final Skill aSkill = Globals.getSkillKeyed(((Skill) aArrayList.get(i)).getKeyName());
					if (aSkill == null)
					{
						Globals.getSkillList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.FEAT_TYPE:
					final Feat aFeat = Globals.getFeatKeyed(((Feat) aArrayList.get(i)).getKeyName());
					if (aFeat == null)
					{
						Globals.getFeatList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.DOMAIN_TYPE:
					Globals.addDomain((Domain) aArrayList.get(i));
					break;
				case LstConstants.DEITY_TYPE:
					final Deity aDeity = Globals.getDeityKeyed(((Deity) aArrayList.get(i)).getKeyName());
					if (aDeity == null)
					{
						Globals.getDeityList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.SPELL_TYPE:
					final Spell spell = (Spell) aArrayList.get(i);
					Globals.getSpellMap().put(spell.getKeyName(), spell);
					break;
				case LstConstants.WEAPONPROF_TYPE:
					final WeaponProf wpFromFile = (WeaponProf) aArrayList.get(i);
					final WeaponProf wp = Globals.getWeaponProfKeyed(wpFromFile.getKeyName());
					if (wp == null)
					{
						Globals.addWeaponProf(wpFromFile);
					}
					break;
				case LstConstants.EQUIPMENT_TYPE:
					final Equipment eq = Globals.getEquipmentKeyed(((Equipment) aArrayList.get(i)).getKeyName());
					if (eq == null)
					{
						Globals.getEquipmentList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.LANGUAGE_TYPE:
					final Language lang = Globals.getLanguageNamed(((Language) aArrayList.get(i)).getKeyName());
					if (lang == null)
					{
						Globals.getLanguageList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.CLASSSKILL_TYPE:
					parseClassSkillFrom((String) aArrayList.get(i));
					break;
				case LstConstants.CLASSSPELL_TYPE:
					aClassName = parseClassSpellFrom((String) aArrayList.get(i), aClassName);
					break;

				case LstConstants.REQSKILL_TYPE:
					final String aString = (String) aArrayList.get(i);
					if ("ALL".equals(aString) || "UNTRAINED".equals(aString))
					{
						skillReq = aString;
					}
					else
					{
						final Skill skillKeyed = Globals.getSkillKeyed(aString);
						if (skillKeyed != null)
						{
							skillKeyed.setRequired(true);
						}
					}
					break;

				case LstConstants.TEMPLATE_TYPE:
					final PCTemplate aTemplate = Globals.getTemplateKeyed(((PCTemplate) aArrayList.get(i)).getKeyName());
					if (aTemplate == null)
					{
						Globals.getTemplateList().add(aArrayList.get(i));
					}
					break;

				case LstConstants.EQMODIFIER_TYPE:
					final EquipmentModifier aModifier = Globals.getModifierKeyed(((EquipmentModifier) aArrayList.get(i)).getKeyName());
					if (aModifier == null)
					{
						Globals.getModifierList().add(aArrayList.get(i));
					}
					break;

				case LstConstants.KIT_TYPE:
				case LstConstants.LOCATIONS_TYPE:
					break;

				case LstConstants.BIO_TYPE:
				case LstConstants.BIO2_TYPE:
					break;

				case -1:
					break;

				default:
					Logging.errorPrint("In LstSystemLoader.initValue the lineType " + lineType + " is not handled.");
					break;
			}
		}
		aArrayList.clear();
		return aArrayList;
	}

	private void loadCustomItems()
	{
		customItemsLoaded = true;
		if (!SettingsHandler.getSaveCustomEquipment())
		{
			return;
		}

		final BufferedReader br = CustomData.getCustomEquipmentReader();

		// Why is this here?  This implies it is somehow
		// order-independent and should precede the opening of
		// the file.  This is almost assuredly a bug of some
		// kind waiting to happen.  Aha!  Just look at what is
		// in the "finally" clause below.  --bko XXX
		Globals.setAutoGeneration(true);

		if (br == null)
		{
			return;
		}

		try
		{
			while(br != null)
			{
				String aLine = br.readLine();
				if (aLine == null)
				{
					break;
				}
				if (aLine.startsWith("BASEITEM:"))
				{
					final int idx = aLine.indexOf('\t', 9);
					if (idx < 10)
					{
						continue;
					}
					final String baseItemKey = aLine.substring(9, idx);
					aLine = aLine.substring(idx + 1);
					Equipment aEq = Globals.getEquipmentKeyed(baseItemKey);
					if (aEq != null)
					{
						aEq = (Equipment) aEq.clone();
						aEq.load(aLine);
						Globals.addEquipment(aEq);
					}
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error when loading custom items", e);
		}
		finally
		{
			Globals.setAutoGeneration(false);
			Globals.sortPObjectList(Globals.getEquipmentList());
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error when closing infile after loading custom items", ex);
			}
		}
	}

	private static void parseClassSkillFrom(String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		String className = aTok.nextToken();
		final PCClass aClass = Globals.getClassKeyed(className);
		String aName = className;
		if (aClass != null)
		{
			aName = aClass.getKeyName();
		}
		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
			aTok = new StringTokenizer(className, "|", false);
			String aString;
			Skill aSkill;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				final String aStringParen = aString + "(";
				aSkill = Globals.getSkillKeyed(aString);
				if (aSkill != null)
				{
					aSkill.getClassList().add(aName);
				}
				else
				{
					Skill bSkill;
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						bSkill = (Skill) e.next();
						if (bSkill.getKeyName().startsWith(aStringParen))
						{
							bSkill.getClassList().add(aName);
						}
					}
				}
			}
		}
	}

	private static String parseClassSpellFrom(String aLine, String aName)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		final String aString = aTok.nextToken();
		if (aString.startsWith("DOMAIN:"))
		{
			aName = aString.substring(7);
			final Domain aDom = Globals.getDomainKeyed(aName);
			if (aDom != null)
			{
				aName = "DOMAIN|" + aName;
			}
			else
			{
				aName = "";
			}
		}
		if (aString.startsWith("CLASS:"))
		{
			boolean isClass = true;
			aName = "";
			if (aString.length() > 6)
			{
				aName = aString.substring(6);
			}

			// first look for an actual class

			PObject aClass = Globals.getClassKeyed(aName);
			//
			// If the class does not have any spell-casting, then it must either
			// be a domain or a subclass
			//
			if (aClass != null)
			{
				if (((PCClass) aClass).getSpellType().equalsIgnoreCase(Constants.s_NONE))
				{
					aClass = null;
				}
			}

			// then look for a domain

			if (aClass == null)
			{
				aClass = Globals.getDomainKeyed(aName);
				if (aClass != null)
				{
					isClass = false;
				}
			}

			// if it's not one of those, leave it since it might be a subclass

			if (aClass != null)
			{
				aName = aClass.getKeyName();
			}
			if (isClass)
			{
				aName = "CLASS|" + aName;
			}
			else
			{
				aName = "DOMAIN|" + aName;
			}
		}
		else if (aTok.hasMoreTokens())
		{
			PObject aClass;
			final String name = aName.substring(aName.indexOf('|') + 1);
			if (aName.startsWith("DOMAIN|"))
			{
				aClass = Globals.getDomainNamed(name);
			}
			else if (aName.startsWith("CLASS|"))
			{
				aClass = Globals.getClassNamed(name);
			}
			else
			{
				return aName;
			}

			if (aClass == null) // then it must be a subclass
			{
				for (Iterator i = pList.iterator(); i.hasNext();)
				{
					aClass = (PObject) i.next();
					if (aClass.getName().equals(name))
					{
						break;
					}
					else
					{
						aClass = null;
					}
				}
				if (aClass == null)
				{
					aClass = new PObject();
					aClass.setName(name);
					pList.add(aClass);
				}
			}
			final int level = Integer.parseInt(aString);
			final String bString = aTok.nextToken();
			aTok = new StringTokenizer(bString, "|", false);
			while (aTok.hasMoreTokens())
			{
				final Spell aSpell = Globals.getSpellKeyed(aTok.nextToken().trim());
				if (aSpell != null)
				{
					aSpell.setLevelInfo(aName, level);
				}
			}
		}
		return aName;
	}

	private static void parsePointBuyLine(String inputLine, URL sourceURL, int argLineNum) throws PersistenceLayerException
	{
		final StringTokenizer pbTok = new StringTokenizer(inputLine, "\t");
		boolean bError = false;
		if (pbTok.countTokens() == 2)
		{
			final String token1 = pbTok.nextToken();
			final String token2 = pbTok.nextToken();
			try
			{
				if (token1.startsWith("STAT:") && token2.startsWith("COST:"))
				{
					final int statValue = Integer.parseInt(token1.substring(5));
					final int cost = Integer.parseInt(token2.substring(5));
					SettingsHandler.addPointBuyStatCost(statValue, cost);
				}
				else if (token1.startsWith("METHOD:") && token2.startsWith("POINTS:"))
				{
					final String methodName = token1.substring(7);
					final int points = Integer.parseInt(token2.substring(7));
					SettingsHandler.addPurchaseModeMethod(methodName, points);
				}
				else
				{
					bError = true;
				}
			}
			catch (Exception exc)
			{
				Logging.errorPrint("Exception in parsePointBuyLine:" + Constants.s_LINE_SEP, exc);
				bError = true;
			}
		}
		else
		{
			bError = true;
		}

		if (bError)

		{
			throw new PersistenceLayerException("Illegal point buy info " + sourceURL.toString() + ":" + Integer.toString(argLineNum) + " \"" + inputLine + "\"");
		}
	}

	private static void loadGameModes()
	{
		final String[] gameFiles = getGameFilesList();
		if ((gameFiles == null) || (gameFiles.length == 0))
		{
			return;
		}

		Globals.getGameModeList().clear();
		final String aDirectory = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator;

		for (int i = 0; i < gameFiles.length; ++i)
		{
			final GameMode gm = loadGameModeMiscInfo(gameFiles[i], new File(aDirectory + gameFiles[i] + File.separator + "miscinfo.lst"));
			if (gm != null)
			{
				loadGameModeInfoFile(gm, new File(aDirectory + gameFiles[i] + File.separator + "load.lst"), "load");
				loadGameModeInfoFile(gm, new File(aDirectory + gameFiles[i] + File.separator + "level.lst"), "level");
			}
		}
		Collections.sort(Globals.getGameModeList());
	}

	private static void loadGameModeInfoFile(GameMode gameMode, File aFile, String aType)
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
			String aLine;
			while ((aLine = br.readLine()) != null)
			{
				// Ignore commented-out and empty lines
				if (aLine.length() > 0 && aLine.charAt(0) == '#' || (aLine.length() == 0))
				{
					continue;
				}
				if (aType.equals("load"))
				{
					gameMode.addLoadString(aLine);
				}
				else if (aType.equals("level"))
				{
					final LevelInfo level = new LevelInfo();
					LevelLoader.parseLine(level, aLine, 1);
					gameMode.getLevelInfo().add(level);
				}
			}
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Error when loading game mode " + aType + " info", ex);
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error when trying to close after loading game mode " + aType + " info", ex);
			}
		}
	}

	private static GameMode loadGameModeMiscInfo(String aName, File aFile)
	{
		GameMode gameMode = null;
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
			String aLine ;
			while ((aLine = br.readLine()) != null)
			{
				// Ignore commented-out and empty lines
				if (aLine.length() > 0 && aLine.charAt(0) == '#' || (aLine.length() == 0))
				{
					continue;
				}

				if (gameMode == null)
				{
					gameMode = new GameMode(aName);
					Globals.getGameModeList().add(gameMode);
				}

				GameModeLoader.parseMiscGameInfoLine(gameMode, aLine, aFile, lineNum);
			}
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Error when loading game mode misc info", ex);
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error when trying to clase file after loading game mode misc info", ex);
			}
		}
		return gameMode;
	}

	/**
	 * Get a list of all the directories in system/gameModes/
	 * that contain a file named statsandchecks.lst and miscinfo.lst
	 **/
	private static String[] getGameFilesList()
	{
		final String aDirectory = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator;
		return new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				try
				{
					final File d = new File(aFile, aString);
					if (d.isDirectory())
					{
						File f = new File(d, "statsandchecks.lst");
						if (f.exists())
						{
							f = new File(d, "miscinfo.lst");
							return f.exists();
						}
						return false;
					}
				}
				catch (Exception e)
				{
					Logging.errorPrint("GameModes.listGameFiles", e);
				}
				return false;
			}
		});
	}

	public void loadMod(boolean flagDisplayError)
	{
		PObject anObj;
		String aString;

		if (modLines.size() > 0)
		{
			for (int i = 0; i < modLines.size();)
			{
				anObj = null;
				aString = "";
				StringTokenizer aTok = new StringTokenizer(modLines.get(i).toString(), TAB_DELIM);
				aString = aTok.nextToken();
				aTok = new StringTokenizer(aString, ":", false);
				aTok.nextToken();
				if (aTok.countTokens() > 0)
				{
					aString = aTok.nextToken();
				}
				aString = aString.substring(0, aString.indexOf(".MOD"));
				try
				{
					switch (Integer.valueOf(modFileType.get(i).toString()).intValue())
					{
						case -1:
							i++;
							continue;
						case LstConstants.CLASS_TYPE:
							anObj = Globals.getClassNamed(aString);
							PCClassLoader.parseLine((PCClass) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.RACE_TYPE:
							anObj = Globals.getRaceNamed(aString);
							RaceLoader.parseLine((Race) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.COMPANIONMOD_TYPE:
							anObj = Globals.getCompanionMod(aString);
							CompanionModLoader.parseLine((CompanionMod) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.SKILL_TYPE:
							anObj = Globals.getSkillNamed(aString);
							SkillLoader.parseLine((Skill) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.FEAT_TYPE:
							anObj = Globals.getFeatNamed(aString);
							FeatLoader.parseLine((Feat) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.DOMAIN_TYPE:
							anObj = Globals.getDomainNamed(aString);
							DomainLoader.parseLine((Domain) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.DEITY_TYPE:
							anObj = Globals.getDeityNamed(aString);
							DeityLoader.parseLine((Deity) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.SPELL_TYPE:
							anObj = Globals.getSpellNamed(aString);
							SpellLoader.parseLine((Spell) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.WEAPONPROF_TYPE:
							anObj = Globals.getWeaponProfNamed(aString);
							WeaponProfLoader.parseLine((WeaponProf) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.EQUIPMENT_TYPE:
							anObj = Globals.getEquipmentNamed(aString);
							EquipmentLoader.parseLine((Equipment) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.LANGUAGE_TYPE:
							anObj = Globals.getLanguageNamed(aString);
							LanguageLoader.parseLine((Language) anObj, modLines.get(i).toString());
							modLines.remove(i);
							modFileType.remove(i);
							break;
						case LstConstants.TEMPLATE_TYPE:
							anObj = Globals.getTemplateNamed(aString);
							PCTemplateLoader.parseLine((PCTemplate) anObj, modLines.get(i).toString(), null, 0);
							modLines.remove(i);
							modFileType.remove(i);
							break;
						default:
							Logging.errorPrint("In LstSystemLoader.loadMod the fileType " + modFileType.get(i).toString() + " is not handled.");
							break;
					}
				}
				catch (PersistenceLayerException ignore)
				{
					;
				}
				catch (NullPointerException ignore)
				{
					;
				}
				if (anObj == null)
				{
					if (flagDisplayError) // && (!empty))
					{
						Logging.errorPrint("Cannot apply .MOD: " + aString + " not found");
					}
					i++;
				}
			}
		}

		// Now process all the FORGETs
		// sk4p 10 Dec 2002
		if (forgetLines.size() > 0)
		{
			for (int i = 0; i < forgetLines.size(); i++)
			{
				anObj = null;
				aString = forgetLines.get(i).toString();
				try
				{
					switch (Integer.valueOf(forgetFileType.get(i).toString()).intValue())
					{
						case LstConstants.RACE_TYPE:
							Logging.debugPrint("FORGET: Forgetting race ", aString);
							Globals.getRaceMap().remove(aString);
							break;
						case LstConstants.CLASS_TYPE:
							anObj = Globals.getClassNamed(aString);
							if (anObj != null)
							{
								Logging.debugPrint("FORGET: Forgetting class ", aString);
								Globals.getClassList().remove(anObj);
							}
							else
							{
								Logging.errorPrint("FORGET: Class not found--" + aString);
							}
							break;
						case LstConstants.COMPANIONMOD_TYPE:
							Logging.debugPrint("FORGET: Forgetting companion mod " + aString);
							anObj = Globals.getCompanionMod(aString);
							Globals.getCompanionModList().remove(anObj);
							break;
						case LstConstants.SKILL_TYPE:
							Logging.debugPrint("FORGET: Forgetting skill ", aString);
							anObj = Globals.getSkillNamed(aString);
							Globals.getSkillList().remove(anObj);
							break;
						case LstConstants.FEAT_TYPE:
							Logging.debugPrint("FORGET: Forgetting feat ", aString);
							anObj = Globals.getFeatNamed(aString);
							Globals.getFeatList().remove(anObj);
							break;
						case LstConstants.DOMAIN_TYPE:
							Logging.debugPrint("FORGET: Forgetting domain ", aString);
							anObj = Globals.getDomainNamed(aString);
							Globals.getDomainList().remove(anObj);
							break;
						case LstConstants.DEITY_TYPE:
							Logging.debugPrint("FORGET: Forgetting deity ", aString);
							anObj = Globals.getDeityNamed(aString);
							Globals.getDeityList().remove(anObj);
							break;
						case LstConstants.SPELL_TYPE:
							Logging.debugPrint("FORGET: Forgetting spell ", aString);
							Globals.getSpellMap().remove(aString);
							break;
						case LstConstants.WEAPONPROF_TYPE:
							Logging.debugPrint("FORGET: Forgetting weapon prof ", aString);
							Globals.removeWeaponProfNamed(aString);
							break;
						case LstConstants.EQUIPMENT_TYPE:
							Logging.debugPrint("FORGET: Forgetting equipment ", aString);
							anObj = Globals.getEquipmentNamed(aString);
							Globals.getEquipmentList().remove(anObj);
							break;
						case LstConstants.LANGUAGE_TYPE:
							Logging.debugPrint("FORGET: Forgetting language ", aString);
							anObj = Globals.getLanguageNamed(aString);
							Globals.getLanguageList().remove(anObj);
							break;
						case LstConstants.TEMPLATE_TYPE:
							Logging.debugPrint("FORGET: Forgetting template ", aString);
							anObj = Globals.getTemplateNamed(aString);
							Globals.getTemplateList().remove(anObj);
							break;
						default:
							Logging.errorPrint("In LstSystemLoader.loadMod the fileType " + modFileType.get(i).toString() + " cannot be forgotten.");
							break;
					}
				}
				catch (NullPointerException e)
				{
					;
				}
			}
			forgetLines.clear();
			forgetFileType.clear();
		}
	}

	/**
	 * Called repeatedly to forget items when .FORGET has been applied.
	 * @param itemToForget
	 * @param nameOfItemToForget
	 * @param fileType
	 */
	private void forgetItem(PObject itemToForget, String nameOfItemToForget, int fileType)
	{
		if (itemToForget == null)
		{
			Logging.debugPrint("Forgetting " + nameOfItemToForget + ": Not defined yet");
		}
		forgetLines.add(nameOfItemToForget);
		forgetFileType.add(new Integer(fileType));
	}


}
