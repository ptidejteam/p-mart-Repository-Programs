/*
 * Names.java
 * Copyright 2001 (C) Mario Bonassin
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
 * $Id: Names.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */

package pcgen.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <code>Names</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class Names
{
	private final Map allTheSyllablesForEachRule = new HashMap();  // this is a map of syllable name to list of possible syllables.
	private final List ruleDefinitions = new ArrayList();

	private static final Names theInstance = new Names();
	private static final String TAB_CHARACTER = "\t";

	/**
	 * make sure you init this instance after getting access to it.
	 */
	public static Names getInstance()
	{
		return theInstance;
	}

	//don't ever call this, ya hear?
	private Names()
	{
	}

	private static final String NAMES_DIRECTORY = SettingsHandler.getPcgenSystemDir() +
		File.separator + "bio" + File.separator + "names" + File.separator;

	public static String[] findAllNamesFiles()
	{
		final File directory = new File(NAMES_DIRECTORY);
		final String[] fileNames = directory.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				if (name.toLowerCase().endsWith(".nam"))
				{
					return true;
				}
				return false;
			}
		});
		final List result = new ArrayList();
		for (int i = 0; i < fileNames.length; ++i)
		{
			result.add(fileNames[i].substring(0, fileNames[i].length() - 4));
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	public void init(String fileName)
	{
		clearAllRules();
		parseFile(NAMES_DIRECTORY + fileName + ".nam");
	}

	private void parseFile(String argFileName)
	{
		final String fileName = Utility.fixFilenamePath(argFileName);

		String currentLine = "";

		BufferedReader br = null;
		try
		{
			final File namesPartSource = new File(fileName);

			String currentSyllable = null;

			boolean canWrite = true;
			boolean inRulesSection = false;

			br = new BufferedReader(new FileReader(namesPartSource));
			while ((currentLine = br.readLine()) != null)
			{

				if (null == currentLine || (currentLine.length() > 0 && currentLine.charAt(0) == '#') || currentLine.startsWith("//") || "".equals(currentLine.trim()))
				{
//					GlobalsdebugPrint("found a comment: " + currentLine);
					continue;
				}
				if (currentLine.startsWith("[/PRE]"))
				{
					canWrite = true;
				}
				if (currentLine.startsWith("[PRE") && currentLine.indexOf(':') >= 0)
				{
					final StringTokenizer tabTok = new StringTokenizer(currentLine.substring(1, currentLine.length() - 1), "\t", false);
					ArrayList aList = new ArrayList();
					while (tabTok.hasMoreTokens())
						aList.add(tabTok.nextToken());
					PObject obj = new PObject();
					canWrite = obj.passesPreReqTestsForList(aList);
					continue;
				}
				if (!canWrite)
				{
					continue;
				}
				if (currentLine.startsWith("[RULES]"))
				{
//					Globals.debugPrint("found the [RULES] tag...");
					inRulesSection = true;
					continue;
				}
				if (currentLine.length() > 0 && currentLine.charAt(0) == '[')
				{
					inRulesSection = false;
				}
				if (inRulesSection)
				{
//					Globals.debugPrint("adding a rule def: " + currentLine);
					ruleDefinitions.add(currentLine);
					continue;
				}
				//This is where the syllable types are saved to sylRuleList and the list themselves
				//are read into a corresponding list.
				if ((currentLine.length() > 0 && currentLine.charAt(0) == '[') && currentLine.endsWith("]"))
				{
//					Globals.debugPrint("found a new syllable : " + currentLine);
					currentSyllable = currentLine;
					allTheSyllablesForEachRule.put(currentLine, new ArrayList());
					continue;
				}

				// if we make it here, then we actually have a syllable fragment in hand.
//				Globals.debugPrint("current line: " + currentLine);
				((List) allTheSyllablesForEachRule.get(currentSyllable)).add(currentLine);
			}
		}
		catch (FileNotFoundException exception)
		{
			if (!"pcgen.ini".equals(fileName))
			{
				Globals.debugPrint("ERROR:" + fileName +
					" error " + currentLine +
					" Exception type:" + exception.getClass().getName() +
					" Message:" + exception.getMessage());
				exception.printStackTrace();
			}
		}
		catch (IOException exception)
		{
			if (!("pcgen.ini".equals(fileName)))
			{
				Globals.debugPrint("ERROR:" + fileName +
					" error " + currentLine +
					" Exception type:" + exception.getClass().getName() +
					" Message:" + exception.getMessage());
				exception.printStackTrace();
			}
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException ioe)
				{
					// nothing to do about it
				}
			}
		}
	}

	private void clearAllRules()
	{
		ruleDefinitions.clear();
		allTheSyllablesForEachRule.clear();
	}

	//should be private but the NamesTest.java makes a call to this so it is package-private for testing
	String[] getRuleDefinitions()
	{
		return (String[]) ruleDefinitions.toArray(new String[ruleDefinitions.size()]);
	}

	//should be private but the NamesTest.java makes a call to this so it is package-private for testing
	String[] getSyllablesByName(String name)
	{
		final List syllables = (List) allTheSyllablesForEachRule.get(name);
		return (String[]) syllables.toArray(new String[syllables.size()]);
	}

	/**
	 * This randomly generates a name based on the current name file.
	 * @throws RuntimeException if init() has not been called with a valid name file.
	 */
	public String getRandomName()
	{
		final NameRule ruleToUse = chooseARandomRule(buildTheRuleSyllableMapping());
		if (ruleToUse == null)
		{
			Globals.debugPrint("Couldn't find a name rule to use.");
			throw new RuntimeException("No random name available. Try again.");
		}
		return constructTheName(ruleToUse);
	}

	private List buildTheRuleSyllableMapping()
	{
		final List rules = new ArrayList();
		if (getRuleDefinitions().length > 0)
		{
			for (int i = 0; i < getRuleDefinitions().length; ++i)
			{
				final String rule = getRuleDefinitions()[i];
				final StringTokenizer newlineStr = new StringTokenizer(rule, TAB_CHARACTER, false);
				//the first token is the "chance" for this rule...
				NameRule newRule = new NameRule(Integer.parseInt(newlineStr.nextToken()));
				// then we add all the syllable names to the list for this rule.
				while (newlineStr.hasMoreTokens())
				{
					final String syllableName = newlineStr.nextToken();
//					Globals.debugPrint("adding syllable name: " + syllableName);
					newRule.addSyllable(syllableName);
				}
				rules.add(newRule);
			}
		}
		return rules;
	}

	private String constructTheName(NameRule ruleToUse)
	{
		final StringBuffer buf = new StringBuffer(30);
		final String[] ruleSyllables = ruleToUse.getRuleSyllables();
		for (int i = 0; i < ruleSyllables.length; ++i)
		{
			if (ruleSyllables[i].startsWith("[{") && ruleSyllables[i].endsWith("}]"))
			{
				String FileName = ruleSyllables[i].substring(2, ruleSyllables[i].length() - 2);
				Names otherFile = new Names();
				otherFile.init(FileName);
				String Name = otherFile.getRandomName();
				buf.append(Name);
			}
			else
			{
				buf.append(getRandomSyllableByName(ruleSyllables[i]));
			}
		}
		return buf.toString();
	}

	private static NameRule chooseARandomRule(List rules)
	{
		NameRule ruleToUse = null;
		final int roll;
		int y;
		roll = RollingMethods.roll(1, 100);
		for (y = 0; y < rules.size(); ++y)
		{
			if (roll <= ((NameRule) rules.get(y)).getChance())
			{
				ruleToUse = (NameRule) rules.get(y);
				break;
			}
		}
		return ruleToUse;
	}

	private String getRandomSyllableByName(String name)
	{
		if (getSyllablesByName(name) != null)
		{
			if (getSyllablesByName(name).length > 0)
			{
				final int roll = RollingMethods.roll(1, getSyllablesByName(name).length);
				return getSyllablesByName(name)[roll - 1];
			}
		}
		return "";
	}

}
