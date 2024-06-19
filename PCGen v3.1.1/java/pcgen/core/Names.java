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
 */

package pcgen.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class Names
{
  private Map allTheSyllablesForEachRule = new HashMap();  // this is a map of syllable name to list of possible syllables.
  private List ruleDefinitions = new ArrayList();

  private static Names theInstance = new Names();
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
    File directory = new File(NAMES_DIRECTORY);
    String[] fileNames = directory.list(new FilenameFilter()
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
    List result = new ArrayList();
    for (int i = 0; i < fileNames.length; i++)
    {
      result.add(fileNames[i].substring(0, fileNames[i].length() - 4));
    }
    return (String[])result.toArray(new String[result.size()]);
  }

  public void init(String fileName)
  {
    clearAllRules();
    parseFile(NAMES_DIRECTORY + fileName + ".nam");
  }

  //todo: make this cleaner by reading line by line, instead of slurping it up and tokenizing on eols.
  private void parseFile(String argFileName)
  {
    String currentSyllable = null;
    byte[] inputLine = null;
    final String fileName = Utility.fixFilenamePath(argFileName);
    File namesPartSource = new File(fileName);

    String aString = null;
    String currentLine = "";
    try
    {
      FileInputStream aStream = new FileInputStream(namesPartSource);

      int length = (int)namesPartSource.length();
      inputLine = new byte[length];
      aStream.read(inputLine, 0, length);
      aString = new String(inputLine);
      String newlinedelim = "\r\n";
      StringTokenizer allTheLines = new StringTokenizer(aString, newlinedelim, false);
      boolean inRulesSection = false;
      while (allTheLines.hasMoreTokens())
      {
        currentLine = allTheLines.nextToken();
        if (null == currentLine || currentLine.startsWith("#") || currentLine.startsWith("//") || "".equals(currentLine.trim()))
        {
//          GlobalsdebugPrint("found a comment: " + currentLine);
          continue;
        }
        if (currentLine.startsWith("[RULES]"))
        {
//          Globals.debugPrint("found the [RULES] tag...");
          inRulesSection = true;
          continue;
        }
        if (currentLine.startsWith("["))
        {
          inRulesSection = false;
        }
        if (inRulesSection)
        {
//          Globals.debugPrint("adding a rule def: " + currentLine);
          ruleDefinitions.add(currentLine);
          continue;
        }
        //This is where the syllable types are saved to sylRuleList and the list themselves
        //are read into a corresponding list.
        if (currentLine.startsWith("[") && currentLine.endsWith("]"))
        {
//          Globals.debugPrint("found a new syllable : " + currentLine);
          currentSyllable = currentLine;
          allTheSyllablesForEachRule.put(currentLine, new ArrayList());
          continue;
        }

        // if we make it here, then we actually have a syllable fragment in hand.
//        Globals.debugPrint("current line: " + currentLine);
        ((List)allTheSyllablesForEachRule.get(currentSyllable)).add(currentLine);

      }
      aStream.close();
    }
    catch (FileNotFoundException exception)
    {
      if (!fileName.equals("pcgen.ini"))
      {
        Globals.debugPrint("ERROR:" + fileName + " error " + currentLine + " Exception type:" + exception.getClass().getName() + " Message:" + exception.getMessage());
        exception.printStackTrace();
      }
    }
    catch (IOException exception)
    {
      if (!fileName.equals("pcgen.ini"))
      {
        Globals.debugPrint("ERROR:" + fileName + " error " + currentLine + " Exception type:" + exception.getClass().getName() + " Message:" + exception.getMessage());
        exception.printStackTrace();
      }
    }
  }

  private void clearAllRules()
  {
    ruleDefinitions.clear();
    allTheSyllablesForEachRule.clear();
  }

  // public for testing
  public String[] getRuleDefinitions()
  {
    return (String[])ruleDefinitions.toArray(new String[ruleDefinitions.size()]);
  }

  // public for testing
  public String[] getSyllablesByName(String name)
  {
    List syllables = (List)allTheSyllablesForEachRule.get(name);
    return (String[])syllables.toArray(new String[syllables.size()]);
  }

  /**
   * This randomly generates a name based on the current name file.
   * @throws RuntimeException if init() has not been called with a valid name file.
   */
  public String getRandomName()
  {
    NameRule ruleToUse = chooseARandomRule(buildTheRuleSyllableMapping());
    if (ruleToUse == null)
    {
      Globals.debugPrint("Couldn't find a name rule to use.");
      throw new RuntimeException("No random name available. Try again.");
    }
    return constructTheName(ruleToUse);
  }

  private List buildTheRuleSyllableMapping()
  {
    List rules = new ArrayList();
    if (getRuleDefinitions().length > 0)
    {
      for (int i = 0; i < getRuleDefinitions().length; i++)
      {
        String rule = getRuleDefinitions()[i];
        StringTokenizer newlineStr = new StringTokenizer(rule, TAB_CHARACTER, false);
        //the first token is the "chance" for this rule...
        NameRule newRule = new NameRule(Integer.parseInt(newlineStr.nextToken()));
        // then we add all the syllable names to the list for this rule.
        while (newlineStr.hasMoreTokens())
        {
          String syllableName = newlineStr.nextToken();
//          Globals.debugPrint("adding syllable name: " + syllableName);
          newRule.addSyllable(syllableName);
        }
        rules.add(newRule);
      }
    }
    return rules;
  }

  private String constructTheName(NameRule ruleToUse)
  {
    StringBuffer buf = new StringBuffer();
    String[] ruleSyllables = ruleToUse.getRuleSyllables();
    for (int i = 0; i < ruleSyllables.length; i++)
    {
      if(ruleSyllables[i].startsWith("[{") && ruleSyllables[i].endsWith("}]")) {
          String FileName = ruleSyllables[i].substring(2, ruleSyllables[i].length()-2);
          Names otherFile = new Names();
          otherFile.init(FileName);
          String Name = otherFile.getRandomName();
          buf.append(Name);
      }
      else {
          buf.append(getRandomSyllableByName(ruleSyllables[i]));
      }
    }
    return buf.toString();
  }

  private NameRule chooseARandomRule(List rules)
  {
    NameRule ruleToUse = null;
    int roll;
    int y;
    roll = RollingMethods.roll(1, 100);
    for (y = 0; y < rules.size(); y++)
    {
      if (roll <= ((NameRule)rules.get(y)).getChance())
      {
        ruleToUse = (NameRule)rules.get(y);
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
        int roll = RollingMethods.roll(1, getSyllablesByName(name).length);
        return getSyllablesByName(name)[roll - 1];
      }
      else
      {
        return "";
      }
    }
    return "";
  }

}
