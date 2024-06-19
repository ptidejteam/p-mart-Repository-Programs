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
 * $Id: LstSystemLoader.java,v 1.1 2006/02/20 23:57:40 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PaperInfo;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class LstSystemLoader implements pcgen.persistence.SystemLoader
{
	static final String TAB_DELIM = "\t";
	static final String NEWLINE_DELIM = "\r\n";
	static List pList = new ArrayList(); // list of PObjects for character spells with subclasses

	/*
	* Define the order in which the file types are ordered so we don't have to
	* keep renumbering them
	*/
	private static final int[] loadOrder = {
		-1
		, LstConstants.STATNAME_TYPE
		, LstConstants.RACE_TYPE
		, LstConstants.CLASS_TYPE
		, LstConstants.SKILL_TYPE
		, LstConstants.FEAT_TYPE
		, LstConstants.DOMAIN_TYPE
		, LstConstants.DEITY_TYPE
		, LstConstants.SPELL_TYPE
		, LstConstants.WEAPONPROF_TYPE
		, LstConstants.SCHOOLS_TYPE
		, LstConstants.COLOR_TYPE
		, LstConstants.TRAIT_TYPE
		, LstConstants.COINS_TYPE		// This needs to be loaded before equipment, to cover costs
		, LstConstants.EQMODIFIER_TYPE		// This needs to be loaded before the equipment, so any modifiers will be found
		, LstConstants.EQUIPMENT_TYPE
		, LstConstants.LANGUAGE_TYPE
		, LstConstants.LOAD_TYPE
		, LstConstants.SPECIAL_TYPE
		, LstConstants.CAMPAIGN_TYPE
		, LstConstants.CLASSSKILL_TYPE
		, LstConstants.CLASSSPELL_TYPE
		, LstConstants.REQSKILL_TYPE
		, LstConstants.TEMPLATE_TYPE
		, LstConstants.XP_TYPE
		, LstConstants.BONUS_TYPE
		, LstConstants.SIZEADJUSTMENT_TYPE
		, LstConstants.PHOBIA_TYPE
		, LstConstants.LOCATION_TYPE
		, LstConstants.INTERESTS_TYPE
		, LstConstants.PHRASE_TYPE
		, LstConstants.HAIRSTYLE_TYPE
		, LstConstants.SPEECH_TYPE
		, LstConstants.PAPERINFO_TYPE
		, -9999
	};

	///////////////////////////////////////////////////////////////////////////
	// Property(s)
	///////////////////////////////////////////////////////////////////////////

	private String currentSource = "";

	private List chosenCampaignSourcefiles = new ArrayList();
	private static int lineNum = 0;

	private List sourceList = new ArrayList();
	private Set sourcesSet = new TreeSet();
	private List sourceFileList = new ArrayList();
	private Map loadedFiles = new HashMap();

	//private String defaultPath = System.getProperty("user.dir");
	//private String defaultPcgPath = defaultPath + File.separator + "characters";

	private List lstExcludeFiles = new ArrayList();
	private List pccFileLines = new ArrayList();
	private List raceFileLines = new ArrayList();
	private List classFileLines = new ArrayList();
	private List skillFileLines = new ArrayList();
	private List featFileLines = new ArrayList();
	private List deityFileLines = new ArrayList();
	private List domainFileLines = new ArrayList();
	private List weaponProfFileLines = new ArrayList();
	private List equipmentFileLines = new ArrayList();
	private List classSkillFileLines = new ArrayList();
	private List classSpellFileLines = new ArrayList();
	private List spellFileLines = new ArrayList();
	private List languageLines = new ArrayList();
	private List reqSkillLines = new ArrayList();
	private List templateFileLines = new ArrayList();
	private List equipmentModifierFileLines = new ArrayList();
	private List coinFileLines = new ArrayList();

	private String skillReq = "";

	private boolean customItemsLoaded = false;

	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////

	/** Creates a new instance of LstSystemLoader */
	public LstSystemLoader()
	{
	}

	///////////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////////
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
	}

	public Set getSources()
	{
		return sourcesSet;
	}

	public boolean isCustomItemsLoaded()
	{
		return customItemsLoaded;
	}

	public int saveSource(String src)
	{
		int idx = sourceList.indexOf(src);
		if (idx >= 0)
		{
			return idx;
		}
		sourceList.add(src);
		sourcesSet.add((src.startsWith("SOURCE:")) ? src.substring(7).trim() : src.trim());
		return sourceList.indexOf(src);
	}

	public String savedSource(int idx)
	{
		if ((idx >= 0) && (idx < sourceList.size()))
		{
			return (String)sourceList.get(idx) + ", ";
		}
		return "";
	}

	public int saveSourceFile(String src)
	{
		int idx = sourceFileList.indexOf(src);
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
			return (String)sourceFileList.get(idx);
		return "";
	}

	public void initialize() throws PersistenceLayerException
	{
		loadPCCFilesInDirectory(Globals.getPccFilesLocation().getAbsolutePath());

		initFile(Globals.getPcgenSystemDir() + File.separator + "schools.lst", LstConstants.SCHOOLS_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "color.lst", LstConstants.COLOR_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "trait.lst", LstConstants.TRAIT_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "specials" + File.separator + "specials.lst", LstConstants.SPECIAL_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "load.lst", LstConstants.LOAD_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "XP.lst", LstConstants.XP_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "specials" + File.separator + "bonusstacks.lst", LstConstants.BONUS_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "sizeAdjustment.lst", LstConstants.SIZEADJUSTMENT_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "paperInfo.lst", LstConstants.PAPERINFO_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "phobia.lst", LstConstants.PHOBIA_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "location.lst", LstConstants.LOCATION_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "interests.lst", LstConstants.INTERESTS_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "phrase.lst", LstConstants.PHRASE_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "hairStyle.lst", LstConstants.HAIRSTYLE_TYPE, new ArrayList());
		initFile(Globals.getPcgenSystemDir() + File.separator + "bio" + File.separator + "speech.lst", LstConstants.SPEECH_TYPE, new ArrayList());

		Globals.sortPObjectList(Globals.getCampaignList());
	}

	/**
	 * This just calls loadPCCFilesInDirectory.
	 * Note:  This only handles added campaigns right now, not removed ones.
	 *
	 * author Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
	 * @see pcgen.persistence.PersistenceManager#refreshCampaigns
	 */
	public void refreshCampaigns()
	{
		loadPCCFilesInDirectory(Globals.getPccFilesLocation().getAbsolutePath());
	}
	///////////////////////////////////////////////////////////////////////////
	// Private Method(s)
	///////////////////////////////////////////////////////////////////////////

	public void emptyLists()
	{
		loadedFiles.clear();

		lstExcludeFiles.clear();
		pccFileLines.clear();
		raceFileLines.clear();
		classFileLines.clear();
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
		chosenCampaignSourcefiles.clear();
		coinFileLines.clear();

		skillReq = "";
	}

	private boolean loadPCCFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				try
				{
					if (aString.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION))
					{
						String fName = aFile.getPath() + File.separator + aString;
						//Test to avoid reloading existing campaigns, so we can safely
						// call loadPCCFilesInDirectory repeatedly. -rlk 2002-03-30
						if (Globals.getCampaignByFilename(fName, false) == null)
						{
							initFile(fName, LstConstants.CAMPAIGN_TYPE, new ArrayList());
						}
					}
					else if (aFile.isDirectory())
						loadPCCFilesInDirectory(aFile.getPath() + File.separator + aString);
				}
				catch (PersistenceLayerException e)
				{
					// LATER: This is not an appropriate way to deal with this exception.
					// Deal with it this way because of the way the loading takes place.
					e.printStackTrace(System.out);
				}
				return false;
			}
		});
		return false;
	}

	public void loadCampaigns(List aSelectedCampaignsList) throws PersistenceLayerException
	{
		Globals.setSorted(false);
		if (aSelectedCampaignsList.size() == 0)
		{
			throw new PersistenceLayerException("You must select at least one campaign to load.");
		}

		////Add empty race
		//createEmptyRace();

		int i = 0;
		Campaign aCamp = null;
		Campaign bCamp = null;
		Campaign cCamp = null;
		for (i = 0; i < aSelectedCampaignsList.size() - 1; i++)
		{
			aCamp = (Campaign)aSelectedCampaignsList.get(i);
			for (int j = i + 1; j < aSelectedCampaignsList.size(); j++)
			{
				bCamp = (Campaign)aSelectedCampaignsList.get(j);
				if (bCamp.getRank().intValue() < aCamp.getRank().intValue())
				{
					aSelectedCampaignsList.set(i, bCamp);
					aSelectedCampaignsList.set(j, aCamp);
					cCamp = (Campaign)aCamp.clone();
					aCamp = bCamp;
					bCamp = cCamp;
				}
			}
		}

		for (i = 0; i < aSelectedCampaignsList.size(); i++)
		{
			aCamp = (Campaign)aSelectedCampaignsList.get(i);
			loadCampaignFile(aCamp);
		}
		List lineList = null;
		List bArrayList = null;
		for (int loadIdx = 0; ; loadIdx++)
		{
			int lineType = loadOrder[loadIdx];

			if (lineType == -9999)
			{
				break;
			}
			lineList = null;
			bArrayList = new ArrayList();
			switch (lineType)
			{
				case -1: // okay - should be 15 (CAMPAIGN_TYPE), but needed to do it at the front and didn't want to bump everything else up one..
					lineList = pccFileLines; // merton_monk  2Nov01
					break;
				case LstConstants.RACE_TYPE:
					lineList = raceFileLines;
					break;
				case LstConstants.CLASS_TYPE:
					lineList = classFileLines;
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
				case LstConstants.COLOR_TYPE:
					continue;
				case LstConstants.TRAIT_TYPE:
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
				case LstConstants.SIZEADJUSTMENT_TYPE:
					continue;
				case LstConstants.STATNAME_TYPE:
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
				case 19:
					continue;
				case 20:
					continue;
				case LstConstants.TEMPLATE_TYPE:
					lineList = templateFileLines;
					break;
				case LstConstants.XP_TYPE:
					continue;
				case LstConstants.BONUS_TYPE:
					continue;
				case LstConstants.EQMODIFIER_TYPE:
					lineList = equipmentModifierFileLines;
					break;
				case LstConstants.PHOBIA_TYPE:
					continue;
				case LstConstants.LOCATION_TYPE:
					continue;
				case LstConstants.INTERESTS_TYPE:
					continue;
				case LstConstants.PHRASE_TYPE:
					continue;
				case LstConstants.HAIRSTYLE_TYPE:
					continue;
				case LstConstants.SPEECH_TYPE:
					continue;
				case LstConstants.PAPERINFO_TYPE:
					continue;
				case LstConstants.COINS_TYPE:
					lineList = coinFileLines;
					break;
				default:
					lineList = null;
					System.out.println("Campaign list corrupt at line: " + i + " no such lineType (" + lineType + ") exists. Stopped parsing campaigns, but not aborting program.");
					return;
			}
			ArrayList cArrayList = null;
			String fileName = null;
			//This relies on new items being added to the *end* of an ArrayList.
			for (int j = 0; j < lineList.size(); j++)
			{
				final String aLine = (String)lineList.get(j);
				final StringTokenizer lineTokenizer = new StringTokenizer(aLine, "|", false);
				int inMode = 0;
				cArrayList = new ArrayList();
				fileName = "";
				String currentToken = null;
				String dString = null;
				while (lineTokenizer.hasMoreTokens())
				{
					currentToken = lineTokenizer.nextToken();
					int openParens = 0;
					int closeParens = 0;
					dString = currentToken.substring(1);
					while (dString.lastIndexOf("(") > -1)
					{
						openParens++;
						dString = dString.substring(0, dString.lastIndexOf("("));
					}
					dString = currentToken;
					while (dString.lastIndexOf(")") > -1)
					{
						closeParens++;
						dString = dString.substring(0, dString.lastIndexOf(")"));
					}
					boolean handled = false;
					if (currentToken.endsWith(".lst") || currentToken.endsWith(".pcc"))
					{
						// load file
						handled = true;

						//*** to handle LSTEXCLUDE commands 12/12/01
						//skip file if it's on the LSTEXCLUDE list
						boolean loadFile = true; //assume we won't find the file in the exclusion list
						Iterator lstExcludeIter = lstExcludeFiles.iterator();
						while (lstExcludeIter.hasNext())
						{
							String lstFilename = (String)lstExcludeIter.next();
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
							if (!loadedFiles.containsKey(currentToken))
							{
								bArrayList = initFile(Globals.getPccFilesLocation().getAbsolutePath() + currentToken, lineType, bArrayList);
								if (lineType == -1)
									initCampaignFromList(bArrayList, aLine);
							}
							if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
							{
								loadedFiles.put(fileName, fileName);
							}
						}
						fileName = currentToken;
						cArrayList.clear();
					}
					if (currentToken.startsWith("(EXCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							currentToken = currentToken.substring(0, currentToken.length() - 1);
						}
						cArrayList.add(currentToken.substring(9));
						if (closeParens > openParens)
						{
							currentToken = ")";
						}
						inMode = -1;
					}
					else if (currentToken.startsWith("(INCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							currentToken = currentToken.substring(0, currentToken.length() - 1);
						}
						cArrayList.add(currentToken.substring(9));
						if (closeParens > openParens)
						{
							currentToken = ")";
						}
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
							PObject anObject = null;
							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								anObject = (PObject)bArrayList.get(k);
								if (cArrayList.contains(anObject.getKeyName()))
								{
									bArrayList.remove(k);
								}
							}
						}
						else if (inMode == 1)
						{
							// include
							PObject anObject = null;
							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								anObject = (PObject)bArrayList.get(k);
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
				if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
				{
					loadedFiles.put(fileName, fileName);
				}
			}
		}

		if (skillReq.length() > 0)
		{
			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				final Skill aSkill = (Skill)e1.next();
				if ((skillReq.equals("UNTRAINED") && aSkill.getUntrained().startsWith("Y")) || skillReq.equals("ALL"))
				{
					aSkill.setRequired(true);
				}
			}
		}

		//
		// Add catch-all feat for weapon proficiencies that cannot be granted as part of a Feat
		// eg. Simple weapons should normally be applied to the Simple Weapon Proficiency feat, but
		// it does not allow multiples (either all or nothing). So monk class weapons will get dumped
		// into this bucket.
		//
		Feat aFeat = new Feat();
		String aLine = "Weapon Proficiency\tTYPE:General\tVISIBLE:Display\tMULT:YES\tSTACK:YES\tDESC:You attack with this specific weapon normally, non-proficiency incurs a -4 to hit penalty.\tSOURCE:PCGen Internal";
		FeatLoader.parseLine(aFeat, aLine, null, -1);
		Globals.getFeatList().add(aFeat);
		Globals.sortPObjectList(Globals.getFeatList());

		//
		// Add internal equipment modifier for adding weapon/armor types to equipment
		//
		EquipmentModifier anObj = new EquipmentModifier();
		aLine = "PCGENi_WEAPON\tTYPE:Weapon\tVISIBLE:No\tCHOOSE:TYPE=ALL\tNAMEOPT:NONAME";
		EquipmentModifierLoader.parseLine(anObj, aLine, null, -1);
		Globals.getModifierList().add(anObj);

		anObj = new EquipmentModifier();
		aLine = "PCGENi_ARMOR\tTYPE:Armor\tVISIBLE:No\tCHOOSE:TYPE=ALL\tNAMEOPT:NONAME";
		EquipmentModifierLoader.parseLine(anObj, aLine, null, -1);
		Globals.getModifierList().add(anObj);

		loadCustomItems();

		//
		// Check all the weapons to see if they are either Melee or Ranged, to avoid
		// problems when we go to export/preview the character
		//
		for (Iterator e2 = Globals.getEquipmentList().iterator(); e2.hasNext();)
		{
			final Equipment aEq = (Equipment)e2.next();
			if (aEq.isWeapon() && !aEq.isMelee() && !aEq.isRanged())
			{
				throw new PersistenceLayerException("Weapon: " + aEq.getName() + " is neither Melee nor Ranged." + "\n" + Constants.s_APPNAME + " cannot calculate \"to hit\" unless one of these is selected." + "\nSource: " + aEq.getSourceFile());
			}
		}


		if (!Globals.wantToLoadMasterworkAndMagic())
		{
			Globals.autoGenerateEquipment();
		}

//
// Test the algorithm for determining the base item from the equipment name. Should load all equipment in order to test properly
//
		//doEquipNameTest();

	}

	/**
	 * Reads the source file for the campaign aCamp and adds the names of files
	 * to be loaded to raceFileLines, classFileLines etc.
	 */

	private void loadCampaignFile(Campaign aCamp) throws PersistenceLayerException
	{
		aCamp.setIsLoaded(true);
		final String aString = aCamp.getSourceFile();

		boolean alreadyChosen = false;
		for (Iterator it = chosenCampaignSourcefiles.iterator(); it.hasNext();)
		{
			final String s = (String)it.next();
			if (s.equals(aString))
			{
				alreadyChosen = true;
				break;
			}
		}
		if (!alreadyChosen)
			chosenCampaignSourcefiles.add(aString);

		List aArrayList = initFile(aString, -1, new ArrayList());
		initCampaignFromList(aArrayList, aString);
	}

	// aArrayList contains an array of the lines as returned from initFile
	private void initCampaignFromList(List aArrayList, String aString) throws PersistenceLayerException
	{
		String aLine = null;
		int rank = 9;
		String tempGame = Constants.DND_MODE;

		for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
		{
			aLine = (String)e1.next();
			if (aLine.startsWith("CAMPAIGN:"))
			{
				continue;
			}
			if (aLine.startsWith("NFO:"))
			{
				continue;
			}
			if (aLine.startsWith("RANK:"))
			{
				rank = Integer.parseInt(aLine.substring(5));
				continue;
			}
			if (aLine.startsWith("GAME:"))
			{
				tempGame = aLine.substring(5);
				continue;
			}
			if (aLine.startsWith("TYPE:"))
			{
				continue;
			}
			if (aLine.startsWith("SOURCE:"))
			{
				continue;
			}

			//*** to handle LSTEXCLUDE commands 12/12/01
			//check here for LST files to exclude from any further loading
			if (aLine.startsWith("LSTEXCLUDE:"))
			{
				String lstList = aLine.substring(11);
				StringTokenizer lstTok = new StringTokenizer(lstList, "|");
				while (lstTok.hasMoreTokens())
				{
					String lstFilename = lstTok.nextToken();
					lstExcludeFiles.add(lstFilename);
				}
				continue;
			}
			//*** to handle LSTEXCLUDE commands 12/12/01

			/* Figure out where the PCC file came from that we're processing, so that
	 we can prepend its path onto any LST file references (or PCC refs, for that matter).
				 If the source line in question already has path info, then don't bother
			*/
			String aSource = "";
			int skipSymbol = 0;
			//if the line doesn't use "@" then it's a relative path, so we need to figure out what that path is
			if (aLine.indexOf("@") == -1)
			{
				int separatorLoc = aString.lastIndexOf(File.separator);
				//just in case aString was composed, rather than from a File object, check to see if the "\" character was used
				if (aString.lastIndexOf("\\") > separatorLoc)
					separatorLoc = aString.lastIndexOf("\\");
				if (aString.lastIndexOf("/") > separatorLoc)
					separatorLoc = aString.lastIndexOf("/");
				if (aString.indexOf("data") != -1)
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
				pccFileLines.add(aSource + aLine.substring(4 + skipSymbol));
			}
			else if (aLine.startsWith("RACE:"))
			{
				String rFile = aSource + aLine.substring(5 + skipSymbol);
				raceFileLines.add(rFile);
			}
			else if (aLine.startsWith("CLASS:"))
			{
				classFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("SKILL:"))
			{
				skillFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("FEAT:"))
			{
				featFileLines.add(aSource + aLine.substring(5 + skipSymbol));
			}
			else if (aLine.startsWith("DOMAIN:"))
			{
				domainFileLines.add(aSource + aLine.substring(7 + skipSymbol));
			}
			else if (aLine.startsWith("DEITY:"))
			{
				deityFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("SPELL:"))
			{
				spellFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("WEAPONPROF:"))
			{
				weaponProfFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("EQUIPMENT:"))
			{
				equipmentFileLines.add(aSource + aLine.substring(10 + skipSymbol));
			}
			else if (aLine.startsWith("LANGUAGE:"))
			{
				languageLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("CLASSSKILL:"))
			{
				classSkillFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("CLASSSPELL:"))
			{
				classSpellFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("REQSKILL:"))
			{
				reqSkillLines.add(aLine.substring(9));
			}
			else if (aLine.startsWith("TEMPLATE:"))
			{
				templateFileLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("EQUIPMOD:"))
			{
				equipmentModifierFileLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("COINS:"))
			{
				coinFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else
			{
				throw new PersistenceLayerException("Invalid line: " + aLine + " in " + aString);
			}
		}
		if (rank <= 1) Globals.setGame(tempGame);
		Globals.debugPrint("RANK: " + rank + " GAME: " + Globals.getGame() + " tempGame: " + tempGame);

		//GameModes modesMenu = new GameModes();
		//modesMenu.updateMenu();

	}

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are:
	 *   0 - race,
	 *   1 - class,
	 *   2 - skill,
	 *   3 - feat,
	 *   4 - domain,
	 *   5 - deity,
	 *   6 - spell,
	 *   7 - weapon prof,
	 *   8 - school,
	 *   9 - color,
	 *   10 - trait,
	 *   11 - equipment,
	 *   12 - language,
	 *   13 - carrying load,
	 *   14, 16, 17, 18 - text file,
	 *   15 - campaign,
	 *   20 - pipe ("|") separated list.
	 *   21 - template
	 *   22 - CR to XP conversion values
	 *   23 - name
	 *   24 -
	 *   25 - Equipment modifiers
	 *   26 - Size adjustment
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
	 */
	public List initFile(String argFileName, int fileType, List aList) throws PersistenceLayerException
	{
		byte[] inputLine = null;
		final String fileName = argFileName.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		final File aFile = new File(fileName);
		PObject anObj = null;
		String aString = null;
		String aLine = "";
		lineNum = 0;


		/*
		 * bsmeister 1/16/2002
		 * Coin denominations load themselves, not depending on the try loop
		 */
		if (fileType == LstConstants.COINS_TYPE)
		{
			Globals.getDenominationList().parseFiles(fileName);
			return aList;
		}

		try
		{
			FileInputStream aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			aString = new String(inputLine);
			String newlinedelim = "\r\n";
			StringTokenizer newlineStr = new StringTokenizer(aString, newlinedelim, false);
			setCurrentSource("");
			String nameString = "";
			while (newlineStr.hasMoreTokens())
			{
				boolean isModItem = false;
				aLine = newlineStr.nextToken();
				++lineNum;
				//
				// Ignore commented-out lines
				if (aLine.startsWith("#;"))
				{
					if (!Globals.wantToLoadMasterworkAndMagic())
					{
						continue;
					}
					aLine = aLine.substring(2);
				}
				if (aLine.startsWith("#") || aLine.trim().length()==0)
				{
					continue;
				}

				if (aLine.startsWith("SOURCE") && fileType != LstConstants.CAMPAIGN_TYPE)
				{
					setCurrentSource(aLine);
					continue;
				}
				// check for special case of CLASS:name.MOD
				isModItem = aLine.endsWith(".MOD");
				if (isModItem && aLine.startsWith("CLASS:"))
					nameString = aLine.substring(0, aLine.length() - 4);
				// first field is usually name (only exception is class-level lines
				// see if name ends with .MOD, if so, use already-existing item instead
				// of creating a new one. merton_monk@yahoo.com 2/8/2002
				if (aLine.indexOf("\t") > 4)
				{
					final StringTokenizer t = new StringTokenizer(aLine, "\t", false);
					nameString = t.nextToken();
					isModItem = nameString.endsWith(".MOD");
					if (isModItem)
					{
						nameString = nameString.substring(0, nameString.length() - 4);
					}
				}
				switch (fileType)
				{
					case LstConstants.RACE_TYPE:
						if (!isModItem)
						{
							anObj = new Race();
							aList.add(anObj);
						}
						else
						{
							anObj = Globals.getRaceNamed(nameString);
							if (anObj == null)
							{
								for (Iterator ir = aList.iterator(); ir.hasNext();)
								{
									final Race aRace = (Race)ir.next();
									if (aRace.getName().equals(nameString))
									{
										anObj = aRace;
										break;
									}
								}
							}
						}
						if (anObj != null)
						{
							RaceLoader.parseLine((Race)anObj, aLine, aFile, lineNum);
						}
						break;

					case LstConstants.CLASS_TYPE:
						if (aLine.startsWith("CLASS:"))
						{
							if (!isModItem)
							{
								anObj = new PCClass();
								aList.add(anObj);
							}
							else
							{
								anObj = Globals.getClassNamed(nameString.substring(6));
								if (anObj == null)
								{
									anObj = Globals.getClassNamed(nameString.substring(6), aList);
								}
							}
						}
						if (anObj != null)
						{
							PCClassLoader.parseLine((PCClass)anObj, aLine, aFile, lineNum);
						}
						break;
					case LstConstants.SKILL_TYPE:
						if (!isModItem)
						{
							anObj = new Skill();
							aList.add(anObj);
						}
						else
							anObj = Globals.getSkillNamed(nameString);
						if (anObj != null)
							SkillLoader.parseLine((Skill)anObj, aLine, aFile, lineNum);
						break;
					case LstConstants.FEAT_TYPE:
						if (!isModItem)
						{
							anObj = new Feat();
							aList.add(anObj);
						}
						else
							anObj = Globals.getFeatNamed(nameString);
						if (anObj != null)
							FeatLoader.parseLine((Feat)anObj, aLine, aFile, lineNum);
						break;
					case LstConstants.DOMAIN_TYPE:
						if (!isModItem)
						{
							anObj = new Domain();
							aList.add(anObj);
						}
						else
							anObj = Globals.getDomainNamed(nameString);
						if (anObj != null)
							DomainLoader.parseLine((Domain)anObj, aLine, aFile, lineNum);
						break;

					case LstConstants.DEITY_TYPE:
						if (!isModItem)
						{
							anObj = new Deity();
							aList.add(anObj);
						}
						else
						{
							anObj = Globals.getDeityNamed(nameString);
							if (anObj == null)
							{
								anObj = Globals.getDeityNamed(nameString, aList);
							}
						}
						if (anObj != null)
						{
							DeityLoader.parseLine((Deity)anObj, aLine, aFile, lineNum);
						}
						break;
					case LstConstants.SPELL_TYPE:
						if (!isModItem)
						{
							anObj = new Spell();
							aList.add(anObj);
						}
						else
							anObj = Globals.getSpellNamed(nameString);
						if (anObj != null)
							SpellLoader.parseLine((Spell)anObj, aLine, aFile, lineNum);
						break;
					case LstConstants.WEAPONPROF_TYPE:
						if (!isModItem)
						{
							anObj = new WeaponProf();
							aList.add(anObj);
						}
						else
							anObj = Globals.getWeaponProfNamed(nameString);
						if (anObj != null)
							WeaponProfLoader.parseLine((WeaponProf)anObj, aLine, aFile, lineNum);
						break;
					case LstConstants.SCHOOLS_TYPE:
						Globals.getSchoolsList().add(aLine);
						break;
					case LstConstants.COLOR_TYPE:
						Globals.getColorList().add(aLine);
						break;
					case LstConstants.TRAIT_TYPE:
						Globals.getTraitList().add(aLine);
						break;
					case LstConstants.EQUIPMENT_TYPE:
						if (!isModItem)
						{
							anObj = new Equipment();
							aList.add(anObj);
						}
						else
						{
							anObj = Globals.getEquipmentNamed(nameString);
							if (anObj == null)
							{
								anObj = Globals.getEquipmentNamed(nameString, aList);
							}
						}
						if (anObj != null)
						{
							EquipmentLoader.parseLine((Equipment)anObj, aLine, aFile, lineNum);
						}
						break;

					case LstConstants.LANGUAGE_TYPE:
						if (!isModItem)
						{
							anObj = new Language();
							aList.add(anObj);
						}
						else
							anObj = Globals.getLanguageNamed(nameString);
						if (anObj != null)
							LanguageLoader.parseLine((Language)anObj, aLine, aFile, lineNum);
//						Language aLang = new Language();
//						aLang.parseLine(aLine, aFile, lineNum);
//						aList.add(aLang);
						break;
					case LstConstants.LOAD_TYPE:
						Globals.getLoadStringsList().add(aLine);
						break;
					case LstConstants.SPECIAL_TYPE:
						SpecialAbility sp = new SpecialAbility();
						SpecialAbilityLoader.parseLine(sp, aLine, aFile, lineNum);
						Globals.getSpecialsList().add(sp);
						break;
					case LstConstants.SIZEADJUSTMENT_TYPE:
						SizeAdjustment sadj = new SizeAdjustment();
						SizeAdjustmentLoader.parseLine(sadj, aLine, aFile, lineNum);
						Globals.getSizeAdjustmentList().add(sadj);
						break;
					case LstConstants.STATNAME_TYPE:
						StringTokenizer st = new StringTokenizer(aLine, ":", false);
						try
						{
							if (lineNum == 1)
							{
								int i = Integer.parseInt(st.nextToken());
								if (i > 0)
								{
									Globals.setAttribLong(new String[i]);
									Globals.setAttribShort(new String[i]);
									Globals.setAttribRoll(new boolean[i]);
								}
							}
							else if (st.countTokens() != 3)
							{
								throw new PersistenceLayerException("Invalid entry in Stat Name file: " + aLine);
							}
							else
							{
								Globals.setAttribLong(lineNum - 2, st.nextToken());					// stat name

								final String shortAttrib = st.nextToken();
								Globals.setAttribShort(lineNum - 2, shortAttrib);					// stat abbreviation
								if (shortAttrib != null)
								{
									Globals.appendStatName(shortAttrib);
								}
								Globals.setAttribRoll(lineNum - 2, Integer.parseInt(st.nextToken()) != 0);	// stat rolled
							}
						}
						catch (NumberFormatException e)
						{
							throw new PersistenceLayerException(e, "Exception in Stat Name file: " + aLine);
						}
						break;
					case -1: // if we're in the process of loading campaigns/sources when
						// another source is loaded via PCC:, then it's fileType=-1
					case LstConstants.CLASSSKILL_TYPE:
					case LstConstants.CLASSSPELL_TYPE:
					case LstConstants.REQSKILL_TYPE:
						aList.add(aLine);
						break;
					case LstConstants.CAMPAIGN_TYPE:
						if (anObj == null)
						{
							anObj = new Campaign();
							Globals.getCampaignList().add(anObj);
						}
						if (anObj != null)
						{
							CampaignLoader.parseLine((Campaign)anObj, aLine, aFile, lineNum);
						}
						break;
					case 20:
						StringTokenizer aTok = new StringTokenizer(aLine, "|", false);
						while (aTok.hasMoreTokens())
						{
							aString = aTok.nextToken();
							if (aString.startsWith("ESK:"))
							{
								Globals.setExcSkillCost(Integer.parseInt(aString.substring(4)));
							}
							else if (aString.startsWith("CCSC:"))
							{
								Globals.setIntCrossClassSkillCost(Integer.parseInt(aString.substring(5)));
							}
							else if (aString.startsWith("X:"))
							{
								StringTokenizer bTok = new StringTokenizer(aString.substring(2), ":", false);
								int x = 0;
								int y = 0;
								int width = 500;
								int height = 200;
								if (bTok.hasMoreTokens())
								{
									x = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									y = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									width = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									height = Integer.parseInt(bTok.nextToken());
								}
								aList.add(new Integer(x));
								aList.add(new Integer(y));
								aList.add(new Integer(height));
								aList.add(new Integer(width));
							}
						}
						break;
					case LstConstants.TEMPLATE_TYPE:
						if (!isModItem)
						{
							anObj = new PCTemplate();
							aList.add(anObj);
						}
						else
						{
							anObj = Globals.getTemplateNamed(nameString);
						}
						if (anObj != null)
						{
							PCTemplateLoader.parseLine((PCTemplate)anObj, aLine, aFile, lineNum);
						}
						break;
					case LstConstants.XP_TYPE:
						Globals.getXPList().add(aLine);
						break;
					case LstConstants.BONUS_TYPE:
						Globals.getBonusStackList().add(aLine.toUpperCase());
						break;
					case LstConstants.EQMODIFIER_TYPE:
						anObj = new EquipmentModifier();
						EquipmentModifierLoader.parseLine((EquipmentModifier)anObj, aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case LstConstants.PHOBIA_TYPE:
						Globals.getPhobiaList().add(aLine);
						break;
					case LstConstants.LOCATION_TYPE:
						Globals.getLocationList().add(aLine);
						break;
					case LstConstants.INTERESTS_TYPE:
						Globals.getInterestsList().add(aLine);
						break;
					case LstConstants.PHRASE_TYPE:
						Globals.getPhraseList().add(aLine);
						break;
					case LstConstants.HAIRSTYLE_TYPE:
						Globals.getHairStyleList().add(aLine);
						break;
					case LstConstants.SPEECH_TYPE:
						Globals.getSpeechList().add(aLine);
						break;
					case LstConstants.PAPERINFO_TYPE:
						PaperInfo psize = new PaperInfo();
						PaperInfoLoader.parseLine(psize, aLine, aFile, lineNum);
						Globals.getPaperInfoList().add(psize);
						break;
					default:
						//What to do here?
				}

				if ((anObj == null) && isModItem)
				{
					System.out.println("Cannot apply .MOD: " + nameString + " not found");
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
						if (anObj != null)
						{
							anObj.setSourceFile(aFile.getAbsolutePath());
						}
						break;

					case LstConstants.SCHOOLS_TYPE:
					case LstConstants.COLOR_TYPE:
					case LstConstants.TRAIT_TYPE:
					case LstConstants.LANGUAGE_TYPE:
					case LstConstants.LOAD_TYPE:
					case LstConstants.SPECIAL_TYPE:
					case LstConstants.CLASSSKILL_TYPE:
					case LstConstants.CLASSSPELL_TYPE:
					case LstConstants.REQSKILL_TYPE:
					case LstConstants.TEMPLATE_TYPE:
					case LstConstants.XP_TYPE:
					case LstConstants.BONUS_TYPE:
					case LstConstants.SIZEADJUSTMENT_TYPE:
					case LstConstants.STATNAME_TYPE:
					case LstConstants.PHOBIA_TYPE:
					case LstConstants.LOCATION_TYPE:
					case LstConstants.INTERESTS_TYPE:
					case LstConstants.PHRASE_TYPE:
					case LstConstants.HAIRSTYLE_TYPE:
					case LstConstants.SPEECH_TYPE:
					case LstConstants.PAPERINFO_TYPE:
						break;
					default:
						//What to do here?
				}
			}
			aStream.close();
			//
			// Need to do this here not in getOptionsFromProperties(), as the paperinfo names will not have been loaded
			//
			if (fileType == LstConstants.PAPERINFO_TYPE)
			{
				Globals.selectPaper(Globals.getPCGenOption("paperName", "A4"));
			}
		}
		catch (IOException e)
		{
			if (!fileName.equals("pcgen.ini"))
			{
				throw new PersistenceLayerException(e, "ERROR:" + fileName + "\nerror " + aLine + "\nException type:" + e.getClass().getName() + "\nMessage:" + e.getMessage());
			}
		}
		setCurrentSource("");
		return aList;
	}

	private List adds(int lineType, List aArrayList)
	{
		String aClassName = "";
		for (int i = 0; i < aArrayList.size(); i++)
		{
			switch (lineType)
			{
				case LstConstants.RACE_TYPE:
					Race race = (Race)aArrayList.get(i);
					Globals.getRaceMap().put(race.getKeyName(), race);
					break;
				case LstConstants.CLASS_TYPE:
					PCClass bClass = Globals.getClassKeyed(((PCClass)aArrayList.get(i)).getKeyName());
					if (bClass == null)
					{
						Globals.getClassList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.SKILL_TYPE:
					Skill aSkill = Globals.getSkillKeyed(((Skill)aArrayList.get(i)).getKeyName());
					if (aSkill == null)
					{
						Globals.getSkillList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.FEAT_TYPE:
					Feat aFeat = Globals.getFeatKeyed(((Feat)aArrayList.get(i)).getKeyName());
					if (aFeat == null)
					{
						Globals.getFeatList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.DOMAIN_TYPE:
					Domain aDomain = Globals.getDomainKeyed(((Domain)aArrayList.get(i)).getKeyName());
					if (aDomain == null)
					{
						Globals.getDomainList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.DEITY_TYPE:
					Deity aDeity = Globals.getDeityKeyed(((Deity)aArrayList.get(i)).getKeyName());
					if (aDeity == null)
					{
						Globals.getDeityList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.SPELL_TYPE:
					Spell spell = (Spell)aArrayList.get(i);
					Globals.getSpellMap().put(spell.getKeyName(), spell);
					break;
				case LstConstants.WEAPONPROF_TYPE:
					WeaponProf wp = Globals.getWeaponProfKeyed(((WeaponProf)aArrayList.get(i)).getKeyName());
					if (wp == null)
					{
						Globals.getWeaponProfList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.EQUIPMENT_TYPE:
					Equipment eq = Globals.getEquipmentKeyed(((Equipment)aArrayList.get(i)).getKeyName());
					if (eq == null)
					{
						Globals.getEquipmentList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.LANGUAGE_TYPE:
					Language lang = Globals.getLanguageNamed(((Language)aArrayList.get(i)).getKeyName());
					if (lang == null)
					{
						Globals.getLanguageList().add(aArrayList.get(i));
					}
					break;
				case LstConstants.CLASSSKILL_TYPE:
					parseClassSkillFrom((String)aArrayList.get(i));
					break;
				case LstConstants.CLASSSPELL_TYPE:
					aClassName = parseClassSpellFrom((String)aArrayList.get(i), aClassName);
					break;
				case LstConstants.REQSKILL_TYPE:
					String aString = (String)aArrayList.get(i);
					if (aString.equals("ALL") || aString.equals("UNTRAINED"))
					{
						skillReq = aString;
					}
					else
					{
						aSkill = Globals.getSkillKeyed(aString);
						if (aSkill != null)
						{
							aSkill.setRequired(true);
						}
					}
					break;
				case LstConstants.TEMPLATE_TYPE:
					PCTemplate aTemplate = Globals.getTemplateKeyed(((PCTemplate)aArrayList.get(i)).getKeyName());
					if (aTemplate == null)
					{
						Globals.getTemplateList().add(aArrayList.get(i));
					}
					break;

				case LstConstants.EQMODIFIER_TYPE:
					EquipmentModifier aModifier = Globals.getModifierKeyed(((EquipmentModifier)aArrayList.get(i)).getKeyName());
					if (aModifier == null)
					{
						Globals.getModifierList().add(aArrayList.get(i));
					}
					break;
				default:
					//What to do here?
			}
		}
		aArrayList.clear();
		return aArrayList;
	}

	public void loadCustomItems()
	{
		customItemsLoaded = true;
		if (!Globals.getSaveCustomEquipment())
		{
			return;
		}
		BufferedReader br = null;
		Globals.setAutoGeneration(true);
		try
		{
			br = new BufferedReader(new FileReader(Globals.getPccFilesLocation().getAbsolutePath() + File.separator + "customsources" + File.separator + "customEquipment.lst"));
			for (; br != null;)
			{
				String aLine = br.readLine();
				if (aLine == null)
				{
					break;
				}
				if (aLine.startsWith("BASEITEM:"))
				{
					int idx = aLine.indexOf('\t', 9);
					if (idx < 10)
					{
						continue;
					}
					String baseItemKey = aLine.substring(9, idx);
					aLine = aLine.substring(idx + 1);
					Equipment aEq = Globals.getEquipmentKeyed(baseItemKey);
					if (aEq != null)
					{
						aEq = (Equipment)aEq.clone();
						aEq.load(aLine);
						Globals.addEquipment(aEq);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
				ex.printStackTrace();
			}
		}
	}

	private void parseClassSkillFrom(String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		String className = aTok.nextToken();
		PCClass aClass = Globals.getClassKeyed(className);
		String aName = className;
		if (aClass != null)
			aName = aClass.getKeyName();
		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
			aTok = new StringTokenizer(className, "|", false);
			String aString = null;
			Skill aSkill = null;
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
					Skill bSkill = null;
					for (Iterator e = Globals.getSkillList().iterator(); e.hasNext();)
					{
						bSkill = (Skill)e.next();
						if (bSkill.getKeyName().startsWith(aStringParen))
						{
							bSkill.getClassList().add(aName);
						}
					}
				}
			}
		}
	}

	private String parseClassSpellFrom(String aLine, String aName)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		String aString = aTok.nextToken();
		if (aString.startsWith("CLASS"))
		{
			aName = "";
			if (aString.length() > 6)
				aName = aString.substring(6);

			// first look for an actual class

			PObject aClass = (PObject)Globals.getClassKeyed(aName);
			//
			// If the class does not have any spell-casting, then it must either
			// be a domain or a subclass
			//
			if (aClass != null)
			{
				if (((PCClass)aClass).getSpellType().equalsIgnoreCase(Constants.s_NONE))
				{
					aClass = null;
				}
			}

			// then look for a domain

			if (aClass == null)
				aClass = (PObject)Globals.getDomainKeyed(aName);

			// if it's not one of those, leave it since it might be a subclass

			if (aClass != null)
				aName = aClass.getKeyName();
		}
		else if (aTok.hasMoreTokens())
		{
			PObject aClass = (PObject)Globals.getClassKeyed(aName);
			//
			// If the class does not have any spell-casting, then it must either
			// be a domain or a subclass
			//
			if (aClass != null)
			{
				if (((PCClass)aClass).getSpellType().equalsIgnoreCase(Constants.s_NONE))
				{
					aClass = null;
				}
			}

			if (aClass == null) // if not a class, look for a domain
				aClass = (PObject)Globals.getDomainKeyed(aName);
			if (aClass == null) // then it must be a subclass
			{
				for (Iterator i = pList.iterator(); i.hasNext();)
				{
					aClass = (PObject)i.next();
					if (aClass.getName().equals(aName))
						break;
					else
						aClass = null;
				}
				if (aClass == null)
				{
					aClass = new PObject();
					aClass.setName(aName);
					pList.add(aClass);
				}
			}
			final int level = Integer.parseInt(aString);
			final String bString = aTok.nextToken();
			aTok = new StringTokenizer(bString, "|", false);
			while (aTok.hasMoreTokens())
			{
				Spell aSpell = Globals.getSpellKeyed(aTok.nextToken().trim());
				if (aSpell != null)
				{
					CharacterSpell cs = new CharacterSpell(aClass, aClass, aSpell);
					cs.setLevel(level);
					Globals.addCharacterSpellList(cs);
				}
			}
		}
		return aName;
	}

}
