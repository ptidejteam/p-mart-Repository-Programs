/*
 * Utility.java
 * Copyright 2002 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on Feb 18, 2002, 5:20:42 PM
 *
 * $Id: Utility.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
 */
package pcgen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
import pcgen.util.GuiFacade;

/**
 * <code>Utility</code>.
 *
 * Assorted generic-ish functionality moved from Globals and PlayerCharacter (the two biggest classes in the project.)
 * Some of this code seems awfully similar, and should probably be further refactored.
 *
 * @author Jonas Karlsson <pjak@yahoo.com>
 * @version $Revision: 1.1 $
 */

public final class Utility
{
	private Utility()
	{
		super();
	}

	/**
	 * Stick a comma between every character of a string
	 */
	public static String commaDelimit(String oldString)
	{
		final int oldStringLength = oldString.length();
		final StringBuffer newString = new StringBuffer(oldStringLength);
		for (int i = 0; i < oldStringLength; ++i)
		{
			if (i != 0)
			{
				newString.append(',');
			}
			newString.append(oldString.charAt(i));
		}
		return newString.toString();
	}

	/**
	 * Simple passthrough
	 * Calls unSplit(stringArray, ',') to do the work
	 */
	public static String commaDelimit(Collection stringArray)
	{
		return unSplit(stringArray, ',');
	}

	public static int innerMostStringStart(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;
				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				--current;
			}
		}
		return index;
	}

	public static int innerMostStringEnd(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;
				if (current > hi)
				{
					hi = current;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
				{
					index = i;
				}
				--current;
			}
		}
		return index;
	}

	public static String ordinal(int iValue)
	{
		String suffix = "th";
		if ((iValue < 4) || (iValue > 20))
		{
			switch (iValue % 10)
			{
				case 1:
					suffix = "st";
					break;
				case 2:
					suffix = "nd";
					break;
				case 3:
					suffix = "rd";
					break;
				default:
					break;
			}
		}
		return Integer.toString(iValue) + suffix;
	}

	/**
	 *  Turn a 'separator' separated string into a ArrayList of strings, each
	 *  corresponding to one trimmed 'separator'-separated portion of the original
	 *  string.
	 *
	 * @param  aString    The string to be split
	 * @param  separator  The separator that separates the string.
	 * @return            an ArrayList of Strings
	 */
	public static ArrayList split(String aString, char separator)
	{
		int elems = 1;
		int beginIndex = 0;
		int endIndex;

		if (aString.trim().length() == 0)
		{
			return new ArrayList(0);
		}

		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == separator)
			{
				++elems;
			}
		}
		final ArrayList result = new ArrayList(elems);
		for (int i = 0; i < elems; ++i)
		{
			endIndex = aString.indexOf(separator, beginIndex);
			if (endIndex == -1)
			{
				endIndex = aString.length();
			}
			result.add(aString.substring(beginIndex, endIndex).trim());

			// Skip separator

			beginIndex = endIndex + 1;
		}
		return result;
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String unSplit(Collection strings, String separator)
	{
		final StringBuffer result = new StringBuffer(strings.size() * 20); //Better than 16, which is default...
		final Iterator iter = strings.iterator();
		while (iter.hasNext())
		{
			String element = (String) iter.next();
			result.append(element);
			if (iter.hasNext())
			{
				result.append(separator);
			}
		}
		return result.toString();
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String unSplit(Collection strings, char separator)
	{
		return unSplit(strings, new String(separator + " "));
	}

	public static String replaceString(String in, String find, String newStr)
	{
		final char[] working = in.toCharArray();
		final StringBuffer sb = new StringBuffer(in.length() + newStr.length());
		int startindex = in.indexOf(find);
		if (startindex < 0)
		{
			return in;
		}
		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; ++i)
			{
				sb.append(working[i]);
			}
			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; ++i)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	public static String escapeColons(String in)
	{
		final StringBuffer retStr = new StringBuffer(in.length());
		for (int j = 0; j < in.length(); ++j)
		{
			final char charAtJ = in.charAt(j);
			if (charAtJ != ':')
			{
				retStr.append(charAtJ);
			}
			else
			{
				retStr.append("\\").append(charAtJ);
			}
		}
		return retStr.toString();
	}

	public static String escapeColons2(String in)
	{
		return replaceString(in, ":", "&#59;");
	}

	public static String unEscapeColons2(String in)
	{
		return replaceString(in, "&#59;", ":");
	}

	static boolean modChoices(PObject obj, ArrayList availableList, ArrayList selectedList, boolean process)
	{
		availableList.clear();
		selectedList.clear();

		final String choiceString = obj.getChoiceString();

		if (choiceString.startsWith("WEAPONPROF|") || choiceString.startsWith("ARMORPROF|"))
		{
			obj.getChoices(choiceString, null, availableList, selectedList);
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if ((aTok.countTokens() < 1) || (aPC == null) || aPC.isImporting())
		{
			return false;
		}

		int numChoices = -1;

		double cost = 1.0;
		Feat aFeat = null;
		boolean stacks = false;
		boolean multiples = false;
		if (obj instanceof Feat)
		{
			aFeat = (Feat) obj;
			cost = aFeat.getCost();
			stacks = aFeat.isStacks();
			multiples = aFeat.isMultiples();
			//
			// Force enough choices to cost an integer
			//
			//if ((cost > 0.0) && (cost < 1.0))
			//{
			//	numChoices = new Double(1.0 / cost).intValue();
			//}
		}

		int i;
		final int totalPossibleSelections = (int) ((aPC.getFeats() + obj.getAssociatedCount()) / cost);
		final ArrayList uniqueList = new ArrayList();
		final ArrayList aBonusList = new ArrayList();
		final ArrayList rootArrayList = new ArrayList();
		String choiceType = aTok.nextToken();
		String choiceSec = obj.getName();
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);			// user is not required to make any changes
		chooser.setAllowsDups(stacks);		// only stackable feats can be duped
		chooser.setVisible(false);
		Iterator iter;
		String title = "Choices";

		int idxSelected = -1;

		int maxNewSelections = (int) (aPC.getFeats() / cost);
		int requestedSelections = -1;
		for (; ;)
		{
			if (choiceType.startsWith("COUNT="))
			{
				requestedSelections = aPC.getVariableValue(choiceType.substring(6), "").intValue();
			}
			else if (choiceType.startsWith("NUMCHOICES="))
			{
				numChoices = aPC.getVariableValue(choiceType.substring(11), "").intValue();
			}
			else
			{
				break;
			}
			if (!aTok.hasMoreTokens())
			{
				Globals.errorPrint("not enough tokens: " + choiceString);
				return false;
			}
			choiceType = aTok.nextToken();
		}

		if (Globals.weaponTypesContains(choiceType))
		{
			title = choiceType + " Weapon Choice";
			final ArrayList tArrayList = Globals.getWeaponProfs(choiceType);
			WeaponProf tempProf;
			for (iter = tArrayList.iterator(); iter.hasNext();)
			{
				tempProf = (WeaponProf) iter.next();
				availableList.add(tempProf.getName());
			}

			//SortedSet pcProfs = (SortedSet)aPC.getWeaponProfs(choiceType).clone();
			obj.addAssociatedTo(selectedList);
			//totalPossibleSelections -= (int)(obj.getAssociatedCount() * cost);
		}
		//
		// CHOOSE:COUNT=1|STAT|Con
		//
		else if ("STAT".equals(choiceType))
		{
			title = "Stat Choice";
			final ArrayList excludeList = new ArrayList();
			while (aTok.hasMoreTokens())
			{
				final String sExclude = aTok.nextToken();
				int iStat = Globals.getStatFromAbbrev(sExclude);
				if (iStat >= 0)
				{
					excludeList.add(Globals.s_ATTRIBSHORT[iStat]);
				}
			}
			for (int x = 0; x < Globals.s_ATTRIBSHORT.length; ++x)
			{
				if (!excludeList.contains(Globals.s_ATTRIBSHORT[x]))
				{
					availableList.add(Globals.s_ATTRIBSHORT[x]);
				}
			}

			obj.addAssociatedTo(selectedList);
		}
		else if ("SCHOOLS".equals(choiceType))
		{
			title = "School Choice";
			availableList.addAll(Globals.getSchoolsList());
			obj.addAssociatedTo(selectedList);
		}
		//
		// Thought: Possible future format
		// CHOOSE: SPELLLIST|#|CLASS=xxx,TYPE=xxx,SPELLBOOK=?
		//
		else if ("SPELLLIST".equals(choiceType))
		{
			if (process && (aFeat != null))
			{
				ArrayList aList = new ArrayList();
				aList.add("New");
				FeatMultipleChoice fmc;
				StringBuffer sb = new StringBuffer(100);
				for (int j = 0; j < aFeat.getAssociatedCount(); ++j)
				{
					fmc = (FeatMultipleChoice) aFeat.getAssociatedList().get(j);
					sb.append(aFeat.getName()).append(" (");
					sb.append(fmc.getChoiceCount());
					sb.append(" of ").append(fmc.getMaxChoices()).append(") ");
					for (i = 0; i < fmc.getChoiceCount(); ++i)
					{
						if (i != 0)
						{
							sb.append(',');
						}
						sb.append(fmc.getChoice(i));
					}

					aList.add(sb.toString());
					sb.setLength(0);
				}

				Object selectedValue;
				if (aList.size() > 1)
				{
					selectedValue = javax.swing.JOptionPane.showInputDialog(null, "Please select the instance of the feat you wish to" + Constants.s_LINE_SEP + "modify, or New, from the list below.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, aList.toArray(), aList.get(0));
				}
				else
				{
					selectedValue = aList.get(0);
				}
				if (selectedValue == null)
				{
					return false;
				}
				idxSelected = aList.indexOf(selectedValue) - 1;
			}

			boolean needSpellbook;
			switch (aTok.nextToken().charAt(0))
			{
				case '1':
				case 'Y':
					needSpellbook = true;
					break;

				default:
					needSpellbook = false;
					break;
			}

			title = "Spell Choice";
			PObject aClass;
			ArrayList classes = null;
			for (int j = 0; ; ++j)
			{
				aClass = aPC.getSpellClassAtIndex(j);
				if (aClass == null)
				{
					break;
				}
				if ((aClass instanceof PCClass) && ((PCClass) aClass).getSpellBookUsed() == needSpellbook)
				{
					if (classes == null)
					{
						classes = new ArrayList();
					}
					classes.add(aClass);
				}
			}

			//
			// Add all spells from all classes that match the spellbook requirement
			// Allow the number of selections to be the maximum allowed by the classes' spell base stat
			//
			if (classes != null)
			{
				maxNewSelections = 0;
				for (int j = 0; j < classes.size(); ++j)
				{
					aClass = (PObject) classes.get(j);
					final ArrayList aList = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1);
					for (iter = aList.iterator(); iter.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell) iter.next();
						final Spell aSpell = cs.getSpell();
						if (!obj.containsAssociated(aSpell.getKeyName()))
						{
							if (!availableList.contains(aSpell.getName()))
							{
								availableList.add(aSpell.getName());
							}
						}
					}
					i = aPC.getStatList().getStatModFor(((PCClass) aClass).getSpellBaseStat());
					if (i > maxNewSelections)
					{
						maxNewSelections = i;
					}
				}

				//
				// Remove all previously selected items from the available list
				//
				final ArrayList assocList = obj.getAssociatedList();
				if (assocList != null)
				{
					for (int j = 0; j < assocList.size(); ++j)
					{
						FeatMultipleChoice fmc = (FeatMultipleChoice) assocList.get(j);
						final ArrayList choices = fmc.getChoices();
						if (choices != null)
						{
							for (int k = 0; k < choices.size(); ++k)
							{
								if (j == idxSelected)
								{
									selectedList.add(choices.get(k));
								}
								else
								{
									availableList.remove(choices.get(k));
								}
							}
						}
					}
				}

				//
				// Set up remaining choices for pre-existing selection
				//
				if (idxSelected >= 0)
				{
					FeatMultipleChoice fmc = (FeatMultipleChoice) obj.getAssociatedObject(idxSelected);
					requestedSelections = maxNewSelections = fmc.getMaxChoices();
				}
			}
		}
		else if ("SALIST".equals(choiceType))
		{
			// SALIST:Smite|VAR|%|1
			title = "Special Ability Choice";
			buildSALIST(choiceString, availableList, aBonusList);
			obj.addAssociatedTo(selectedList);
		}
		else if ("SKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			for (iter = aPC.getSkillList().iterator(); iter.hasNext();)
			{
				final Skill aSkill = (Skill) iter.next();
				availableList.add(aSkill.getName());
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("CSKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			Skill aSkill;
			for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
			{
				aSkill = (Skill) iter.next();
				if (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1)
				{
					availableList.add(aSkill.getName());
				}
			}
			obj.addAssociatedTo(selectedList);
		}

		// SKILLSNAMEDTOCSKILL --- Make one of the named skills a class skill.
		else if ("SKILLSNAMED".equals(choiceType) || "SKILLSNAMEDTOCSKILL".equals(choiceType) || "SKILLSNAMEDTOCCSKILL".equals(choiceType))
		{
			title = "Skill Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean startsWith = false;
				/* TYPE in chooser
					--- arcady 10/21/2001
				*/
				if (aString.startsWith("TYPE."))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						if (aSkill.isType(aString.substring(5)))
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				if (aString.endsWith("%"))
				{
					startsWith = true;
					aString = aString.substring(0, aString.length() - 1);
				}
				Skill aSkill;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.getKeyName().equals(aString) || (startsWith && aSkill.getKeyName().startsWith(aString)))
					{
						availableList.add(aSkill.getName());
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("SKILLLIST".equals(choiceType) || "CCSKILLLIST".equals(choiceType) || "NONCLASSSKILLLIST".equals(choiceType))
		{
			title = "Skill Choice";
			if (aTok.hasMoreTokens())
			{
				choiceSec = new String(aTok.nextToken());
			}
			if (choiceSec.length() > 0 && !"LIST".equals(choiceSec))
			{
				aTok = new StringTokenizer(choiceSec, ",", false);
				while (aTok.hasMoreTokens())
				{
					availableList.add(aTok.nextToken());
				}
			}
			else  // if it was LIST
			{
				Skill aSkill;
				for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
				{
					aSkill = (Skill) iter.next();
					if ("NONCLASSSKILLLIST".equals(choiceType) && (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1 || aSkill.isExclusive()))
					{
						continue; // builds a list of Cross class skills
					}
					final int rootNameLength = aSkill.getRootName().length();
					if (rootNameLength == 0 || aSkill.getRootName().equals(aSkill.getName())) //all skills have ROOTs now, so go ahead and add it if the name and root are identical
					{
						availableList.add(aSkill.getName());
					}
					final boolean rootArrayContainsRootName = rootArrayList.contains(aSkill.getRootName());
					if (rootNameLength > 0 && !rootArrayContainsRootName)
					{
						rootArrayList.add(aSkill.getRootName());
					}
					if (rootNameLength > 0 && rootArrayContainsRootName)
					{
						availableList.add(aSkill.getName());
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("SPELLLEVEL".equals(choiceType))
		{
			// this will need to be re-worked at some point when I can think
			// of a better way.  This feat is different from the others in that
			// it requires a bonus to be embedded in the choice.  Probably this
			// whole feat methodology needs to be re-thought as its getting a bit
			// bloated - a generic way to embed bonuses could be done to simplify
			// this all tremendously instead of so many special cases.
			final StringTokenizer cTok = new StringTokenizer(choiceString, "[]", false);
			final String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
			{
				aBonusList.add(cTok.nextToken());
			}

			PObject.getSpellTypeChoices(choices, availableList, uniqueList); // get appropriate choices for chooser
			obj.addAssociatedTo(selectedList);

			for (Iterator e1 = selectedList.iterator(); e1.hasNext();)
			{
				String aString = (String) e1.next();
				for (Iterator e2 = aBonusList.iterator(); e2.hasNext();)
				{
					String bString = (String) e2.next();
					obj.removeBonus(bString, aString);
				}
			}
		}
		else if ("WEAPONFOCUS".equals(choiceType))
		{
			title = "WeaponFocus Choice";
			final Feat wfFeat = aPC.getFeatNamed("Weapon Focus");
			wfFeat.addAssociatedTo(availableList);
			obj.addAssociatedTo(selectedList);
		}
		else if ("WEAPONPROFS".equals(choiceType))
		{
			title = "Weapon Prof Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if ("LIST".equals(aString))
				{
					String bString;
					for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
					{
						bString = (String) setIter.next();
						if (!availableList.contains(bString))
						{
							availableList.add(bString);
						}
					}
				}
				else if (aString.startsWith("Size."))
				{
					if (aPC.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)) && aPC.getWeaponProfList().contains(aString.substring(7)) && !availableList.contains(aString.substring(7)))
					{
						availableList.add(aString.substring(7));
					}
				}
				else if (aString.startsWith("WSize."))
				{
					String bString;
					WeaponProf wp;
					StringTokenizer bTok = new StringTokenizer(aString, ".", false);
					bTok.nextToken(); // should be WSize
					String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
					ArrayList typeList = new ArrayList();
					while (bTok.hasMoreTokens()) // any additional constraints
					{
						String dString = bTok.nextToken().toUpperCase();
						typeList.add(dString);
					}
					for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
					{
						bString = (String) setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
						{
							continue;
						}

						//
						// get an Equipment object based on the named WeaponProf
						//
						Equipment eq = Globals.getEquipmentNamed(wp.getName());
						if (eq == null)
						{
							//
							// Sword (Bastard/Exotic), Sword (Bastard/Martial), Katana (Martial), Katana(Exotic)
							//
							int len = 0;
							if (bString.endsWith("Exotic)"))
							{
								len = 7;
							}
							if ((len == 0) && bString.endsWith("Martial)"))
							{
								len = 8;
							}
							if (len != 0)
							{
								if (bString.charAt(bString.length() - len - 1) == '/')
								{
									++len;
								}
								String tempString = bString.substring(0, bString.length() - len) + ")";
								if (tempString.endsWith("()"))
								{
									tempString = tempString.substring(0, tempString.length() - 3).trim();
								}
								eq = Globals.getEquipmentNamed(tempString);

							}
							else
							{
								//
								// Couldn't find equipment with matching name, look for 1st weapon that uses it
								//
								for (Iterator eqIter = Globals.getEquipmentList().iterator(); eqIter.hasNext();)
								{
									final Equipment tempEq = (Equipment) eqIter.next();
									if (tempEq.isWeapon())
									{
										if (tempEq.profName().equals(wp.getName()))
										{
											eq = tempEq;
											break;
										}
									}
								}
							}
						}

						boolean isValid = false; // assume we match unless...
						if (eq != null)
						{
							if (typeList.size() == 0)
							{
								isValid = true;
							}
							else
							{
								//
								// search all the optional type strings, just one match passes the test
								//
								for (Iterator wpi = typeList.iterator(); wpi.hasNext();)
								{
									final String wpString = (String) wpi.next();
									if (eq.isType(wpString))
									{
										isValid = true; // if it contains even one of the TYPE strings, it passes
										break;
									}
								}
							}
						}
						if (!isValid)
						{
							continue;
						}

						if (!availableList.contains(bString))
						{
							if ("Light".equals(sString) && Globals.isWeaponLightForPC(aPC, eq))
							{
								availableList.add(bString);
							}
							if ("1 handed".equals(sString) && Globals.isWeaponOneHanded(aPC, eq, wp))
							{
								availableList.add(bString);
							}
							if ("2 handed".equals(sString) && Globals.isWeaponTwoHanded(aPC, eq, wp))
							{
								availableList.add(bString);
							}
						}
					}
				}
				else if (aString.startsWith("SpellCaster."))
				{
					if (aPC.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
					{
						availableList.add(aString.substring(12));
					}
				}
				else if (aString.startsWith("ADD."))
				{
					if (!availableList.contains(aString.substring(4)))
					{
						availableList.add(aString.substring(4));
					}
				}
				else if (aString.startsWith("TYPE."))
				{
					String sString = aString.substring(5);
					boolean adding = true;
					Iterator setIter = aPC.getWeaponProfList().iterator();
					if (sString.startsWith("Not."))
					{
						sString = sString.substring(4);
						setIter = availableList.iterator();
						adding = false;
					}
					String bString;
					WeaponProf wp;
					Equipment eq;
					for (; setIter.hasNext();)
					{
						bString = (String) setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
						{
							continue;
						}
						eq = Globals.getEquipmentKeyed(wp.getKeyName());
						if (eq == null)
						{
							if (!wp.isType("Natural"))	//natural weapons are not in the global eq.list
							{
								continue;
							}

							if (Globals.isDebugMode())
							{
								Globals.debugPrint("Prof Name: -" + wp.getName() + "- " + adding);
							}

							if (adding && !availableList.contains(wp.getName()))
							{
								availableList.add(wp.getName());
							}
						}
						else if (eq.typeStringContains(sString))
						{
							// if this item is of the desired type, add it to the list
							if (adding && !availableList.contains(wp.getName()))
							{
								availableList.add(wp.getName());
							}
							// or try to remove it and reset the iterator since remove cause fits
							else if (!adding && availableList.contains(wp.getName()))
							{
								availableList.remove(wp.getName());
								setIter = availableList.iterator();
							}
						}
					}
				}
				else
				{
					if (aPC.getWeaponProfList().contains(aString) && !availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("HP".equals(choiceType))
		{
			if (aTok.hasMoreTokens())
			{
				choiceSec = aTok.nextToken();
			}
			availableList.add(choiceSec);
			for (int e1 = 0; e1 < obj.getAssociatedCount(); ++e1)
			{
				selectedList.add(choiceSec);
			}
		}
		else if (choiceType.startsWith("FEAT="))
		{
			final Feat theFeat = aPC.getFeatNamed(choiceType.substring(5));
			if (theFeat != null)
			{
				theFeat.addAssociatedTo(availableList);
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("FEATLIST".equals(choiceType))
		{
			obj.addAssociatedTo(selectedList);
			String aString;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (aString.startsWith("TYPE="))
				{
					aString = aString.substring(5);
					if (!stacks && availableList.contains(aString))
					{
						continue;
					}
					for (Iterator e1 = aPC.aggregateFeatList().iterator(); e1.hasNext();)
					{
						final Feat theFeat = (Feat) e1.next();
						if (theFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							availableList.add(theFeat.getName());
						}
					}
				}
				else if (aPC.getFeatNamed(aString) != null)
				{
					if (stacks || (!stacks && !availableList.contains(aString)))
						availableList.add(aString);
				}
			}
		}
		else if ("FEATSELECT".equals(choiceType))
		{
			obj.addAssociatedTo(selectedList);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE="))
				{
					aString = aString.substring(5);
					if (!stacks && availableList.contains(aString))
					{
						continue;
					}
					for (int z = 0; z < Globals.getFeatList().size(); ++z)
					{
						final Feat theFeat = Globals.getFeatListFeat(z);
						if (theFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							availableList.add(theFeat.getName());
						}
					}
				}
				else
				{
					Feat theFeat = Globals.getFeatNamed(aString);
					if (theFeat != null)
					{
						String subName = "";
						if (!aString.equalsIgnoreCase(theFeat.getName()))
						{
							subName = aString.substring(theFeat.getName().length());
							aString = theFeat.getName();
							final int idx = subName.indexOf('(');
							if (idx > -1)
							{
								subName = subName.substring(idx + 1);
							}
						}
						if (theFeat.isMultiples())
						{
							//
							// If already have taken the feat, use it so we can remove
							// any choices already selected
							//
							final Feat pcFeat = aPC.getFeatNamed(aString);
							if (pcFeat != null)
							{
								theFeat = pcFeat;
							}

							int percIdx = subName.indexOf('%');
							if (percIdx > -1)
							{
								subName = subName.substring(0, percIdx);
							}
							else if (subName.length() != 0)
							{
								int idx = subName.lastIndexOf(')');
								if (idx > -1)
								{
									subName = subName.substring(0, idx);
								}
							}

							ArrayList xavailableList = new ArrayList();	// available list of choices
							ArrayList xselectedList = new ArrayList();		// selected list of choices
							theFeat.modChoices(true, xavailableList, xselectedList, false);

							//
							// Remove any that don't match
							//
							if (subName.length() != 0)
							{
								for (int n = xavailableList.size() - 1; n >= 0; --n)
								{
									final String xString = (String) xavailableList.get(n);
									if (!xString.startsWith(subName))
									{
										xavailableList.remove(n);
									}
								}
								//
								// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
								// If you have no ranks in Craft (Basketweaving), the available list will be empty
								//
								// Make sure that the specified feat is available, even though it does not meet the prerequisite
								//
								if ((percIdx == -1) && (xavailableList.size() == 0))
								{
									xavailableList.add(aString + "(" + subName + ")");
								}
							}
							//
							// Remove any already selected
							//
							if (!theFeat.isStacks())
							{
								for (Iterator e = xselectedList.iterator(); e.hasNext();)
								{
									int idx = xavailableList.indexOf(e.next().toString());
									if (idx > -1)
									{
										xavailableList.remove(idx);
									}
								}
							}
							for (Iterator e = xavailableList.iterator(); e.hasNext();)
							{
								availableList.add(aString + "(" + (String) e.next() + ")");
							}
						}
						else
						{
							availableList.add(aString);
						}
					}
				}
			}
		}
		else if ("FEATADD".equals(choiceType))
		{
			title = "Add a Feat";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE="))
				{
					String featType = aString.substring(5);
					for (int z = 0; z < Globals.getFeatList().size(); ++z)
					{
						final Feat theFeat = Globals.getFeatListFeat(z);
						if (theFeat.isType(featType) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							if (Globals.getFeatNamed(theFeat.getName()).passesPreReqTests())
							{
								if ((Globals.getFeatNamed(theFeat.getName()).isStacks()) || (aPC.getFeatNamed(theFeat.getName()) == null))
									availableList.add(theFeat.getName());
							}
						}
					}
				}
				else
				{
					StringTokenizer bTok = new StringTokenizer(aString, ",", false);
					String featName = bTok.nextToken().trim();
					String subName = "";
					aFeat = Globals.getFeatNamed(featName);

					if (aFeat == null)
					{
						Globals.debugPrint("Feat not found: ", featName);
						return false;
					}

					if (!featName.equalsIgnoreCase(aFeat.getName()))
					{
						subName = featName.substring(aFeat.getName().length());
						featName = aFeat.getName();
						int si = subName.indexOf('(');
						if (si > -1)
						{
							subName = subName.substring(si + 1);
						}
					}

					if (aFeat.passesPreReqTests())
					{
						if (aFeat.isMultiples())
						{
							//
							// If already have taken the feat, use it so we can remove
							// any choices already selected
							//
							final Feat pcFeat = aPC.getFeatNamed(featName);
							if (pcFeat != null)
							{
								aFeat = pcFeat;
							}

							final int percIdx = subName.indexOf('%');
							if (percIdx > -1)
							{
								subName = subName.substring(0, percIdx);
							}
							else if (subName.length() != 0)
							{
								final int idx = subName.lastIndexOf(')');
								if (idx > -1)
								{
									subName = subName.substring(0, idx);
								}
							}

							final ArrayList aavailableList = new ArrayList();	// available list of choices
							final ArrayList sselectedList = new ArrayList();		// selected list of choices
							aFeat.modChoices(true, availableList, selectedList, false);

							//
							// Remove any that don't match
							//
							if (subName.length() != 0)
							{
								for (int n = aavailableList.size() - 1; n >= 0; --n)
								{
									String bString = (String) aavailableList.get(n);
									if (!bString.startsWith(subName))
									{
										aavailableList.remove(n);
									}
								}
								//
								// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
								// If you have no ranks in Craft (Basketweaving), the available list will be empty
								//
								// Make sure that the specified feat is available, even though it does not meet the prerequisite
								//
								if ((percIdx == -1) && (aavailableList.size() == 0))
								{
									aavailableList.add(subName);
								}
							}
							//
							// Remove any already selected
							//
							if (!aFeat.isStacks())
							{
								for (Iterator e = sselectedList.iterator(); e.hasNext();)
								{
									int idx = aavailableList.indexOf(e.next().toString());
									if (idx > -1)
									{
										aavailableList.remove(idx);
									}
								}
							}
							for (Iterator e = aavailableList.iterator(); e.hasNext();)
							{
								availableList.add(featName + "(" + (String) e.next() + ")");
							}
							return false;
						}
						else if (!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName))
						{
							availableList.add(aString);
						}
					}
				}
			}
			//process == true;
		}
		else if ("SPELLCLASSES".equals(choiceType))
		{
			title = "Spellcaster Classes";
			PCClass aClass;
			for (iter = aPC.getClassList().iterator(); iter.hasNext();)
			{
				aClass = (PCClass) iter.next();
				if (!aClass.getSpellBaseStat().equals(Constants.s_NONE))
				{
					availableList.add(aClass.getName());
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("ARMORTYPE".equals(choiceType))
		{
			title = "Armor Type Choice";
			String temptype;
			for (int z = 0; z < Globals.getFeatList().size(); ++z)
			{
				final Feat theFeat = Globals.getFeatListFeat(z);
				if (theFeat.getName().startsWith("Armor Proficiency ("))
				{
					int idxbegin = theFeat.getName().indexOf("(");
					int idxend = theFeat.getName().indexOf(")");
					temptype = theFeat.getName().substring((idxbegin + 1), idxend);
					if (aPC.getFeatNamed(theFeat.getName()) != null)
						availableList.add(temptype);
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else
		{
			title = "Selections";
			availableList.add(choiceType);
			String aString;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (stacks || (!stacks && !availableList.contains(aString)))
				{
					availableList.add(aString);
				}
			}
			obj.addAssociatedTo(selectedList);
		}

		if (!process)
		{
			return false;
		}

		if (requestedSelections < 0)
		{
			requestedSelections = maxNewSelections;
		}
		else
		{
			requestedSelections -= selectedList.size();
			requestedSelections = Math.min(requestedSelections, maxNewSelections);
		}

		final int preSelectedSize = selectedList.size();
		if (numChoices > 0)
		{
			//
			// Make sure that we don't try to make the user choose more selections than are available
			// or we'll be in an infinite loop...
			//
			numChoices = Math.min(numChoices, availableList.size() - preSelectedSize);
			requestedSelections = numChoices;
		}
		chooser.setPool(requestedSelections);

		title = title + " (" + obj.getName() + ')';
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);
		for (; ;)
		{
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.show();

			final int selectedSize = chooser.getSelectedList().size() - preSelectedSize;
			if (numChoices > 0)
			{
				if (selectedSize != numChoices)
				{
					GuiFacade.showMessageDialog(null, "You must make " + (numChoices - selectedSize) + " more selection(s).", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
					continue;
				}
			}
			break;
		}

		if ("SPELLLIST".equals(choiceType))
		{
			final int x = aPC.getFeats();
			if (idxSelected >= 0)
			{
				obj.removeAssociated(idxSelected);
				if (chooser.getSelectedList().size() == 0)
				{
					aPC.setFeats(x + 1);
				}
			}
			else if (chooser.getSelectedList().size() != 0)
			{
				aPC.setFeats(x - 1);
			}
		}
		else if ("SALIST".equals(choiceType))
		{
			//
			// remove previous selections from special abilities
			// aBonusList contains all possible selections in form: <displayed info>|<special ability>
			//
			for (int e = 0; e < obj.getAssociatedCount(); ++e)
			{
				String aString = obj.getAssociated(e);
				final String prefix = aString + "|";
				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						obj.removeBonus(bString.substring(bString.indexOf('|') + 1), "");
						break;
					}
				}
			}
		}

		if (aFeat != null && aFeat.getCSkillList() != null)
		{
			for (iter = aFeat.getCSkillList().iterator(); iter.hasNext();)
			{
				final String tempString = (String) iter.next();
				if (!"LIST".equals(tempString) /*&& !Globals.getFeatNamed(obj.getName()).getCSkillList().contains(tempString)*/)
				{
					final Feat bFeat = Globals.getFeatNamed(obj.getName());
					if (bFeat != null)
					{
						if (bFeat.getCSkillList() != null)
						{
							if (bFeat.getCSkillList().contains(tempString))
							{
								iter.remove();
							}
						}
					}
				}
			}
			aFeat.setCcSkillList(".CLEAR");
		}

		if (!"SPELLLIST".equals(choiceType))
		{
			obj.clearAssociated();
		}

		String objPrefix = "";
		if (obj instanceof Domain)
		{
			objPrefix = choiceType + '?';
		}

		FeatMultipleChoice fmc = null;
		for (i = 0; i < chooser.getSelectedList().size(); ++i)
		{
			final String chosenItem = (String) chooser.getSelectedList().get(i);
			if ("HP".equals(choiceType))
			{
				obj.addAssociated(objPrefix + "CURRENTMAX");
			}
			else if ("SPELLLEVEL".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus(bString, chosenItem);
				}
			}
			else if ("SPELLLIST".equals(choiceType))
			{
				if (fmc == null)
				{
					fmc = new FeatMultipleChoice();
					fmc.setMaxChoices(maxNewSelections);
					obj.addAssociated(fmc);
				}
				fmc.addChoice(chosenItem);
			}
			else if ("ARMORTYPE".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus("ARMORPROF=" + bString, chosenItem);
				}
			}
			else if (multiples && !stacks)
			{
				if (!obj.containsAssociated(objPrefix + chosenItem))
				{
					obj.addAssociated(objPrefix + chosenItem);
				}
			}
			else
			{
				final String prefix = chosenItem + "|";
				obj.addAssociated(objPrefix + chosenItem);
				// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						obj.addBonusList(bString.substring(bString.indexOf('|') + 1));
						break;
					}
				}
			}

			if (aFeat != null)
			{
				if ("SKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCSKILL".equals(choiceType) || "NONCLASSSKILLLIST".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();
							if (aSkill.getRootName().equals(chosenItem))
							{
								aFeat.setCSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						aFeat.setCSkillList(chosenItem);
					}
				}
				else if ("CCSKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCCSKILL".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();
							if (aSkill.getRootName().equals(chosenItem))
							{
								aFeat.setCcSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						aFeat.setCcSkillList(chosenItem);
					}
				}
				else if ("FEATADD".equals(choiceType))
				{
					if (!aPC.hasFeat(chosenItem))
					{
						aPC.setFeats(aPC.getFeats() + 1);
					}
					aPC.modFeat(chosenItem, true, false);
				}
			}

			if (Globals.weaponTypesContains(choiceType))
			{
				aPC.addWeaponProf(objPrefix + chosenItem);
			}
		}
		if (!"SPELLLIST".equals(choiceType))
		{
			int featCount = aPC.getFeats();
			if (numChoices > 0)
			{
				final int iCost = new Double(cost).intValue();
				featCount -= iCost;
			}
			else
			{
				featCount = (int) ((totalPossibleSelections - selectedList.size()) * cost);
			}
			aPC.setFeats(featCount);
		}

		//
		// This will get assigned by autofeat (if a feat)
		//
		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
		return true;
	}

	public static void buildSALIST(String aChoice, ArrayList aAvailable, ArrayList aBonus)
	{
		// SALIST:Smite|VAR|%|1
		// SALIST:Turn ,Rebuke|VAR|%|1

		String aString;
		String aPost = "";
		int iOffs = aChoice.indexOf('|', 7);
		if (iOffs < 0)
		{
			aString = aChoice;
		}
		else
		{
			aString = aChoice.substring(7, iOffs);
			aPost = aChoice.substring(iOffs + 1);
		}

		final ArrayList saNames = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(aString, ",");
		while (aTok.hasMoreTokens())
		{
			saNames.add(aTok.nextToken());
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();
		final ArrayList aSAList = aPC.getSpecialAbilityList();

		//
		// Add special abilities due to templates
		//
		final ArrayList aTemplateList = aPC.getTemplateList();
		for (Iterator e1 = aTemplateList.iterator(); e1.hasNext();)
		{
			final PCTemplate aTempl = (PCTemplate) e1.next();
			final ArrayList SAs = aTempl.getSpecialAbilityList(aPC.getTotalLevels(), aPC.totalHitDice());
			if (SAs == null || SAs.isEmpty()) // null pointer/empty check
			{
				continue;
			}
			for (Iterator e2 = SAs.iterator(); e2.hasNext();)
			{
				final String aSA = (String) e2.next();
				if (!aSAList.contains(aSA))
				{
					aSAList.add(aSA);
				}
			}
		}

		for (Iterator e2 = saNames.iterator(); e2.hasNext();)
		{
			aString = (String) e2.next();
			for (Iterator e1 = aSAList.iterator(); e1.hasNext();)
			{
				String aSA = ((SpecialAbility) (e1.next())).getName();
				if (aSA.startsWith(aString))
				{
					String aVar = "";
					//
					// Trim off variable portion of SA, and save variable name
					// (eg. "Smite Evil %/day|SmiteEvil" --> aSA = "Smite Evil", aVar = "SmiteEvil")
					//
					iOffs = aSA.indexOf('|');
					if (iOffs >= 0)
					{
						aVar = aSA.substring(iOffs + 1);
						iOffs = aSA.indexOf('%');
						if (iOffs >= 0)
						{
							aSA = aSA.substring(0, iOffs).trim();
						}
					}
					if (!aAvailable.contains(aSA))
					{
						aAvailable.add(aSA);
						//
						// Check for variable substitution
						//
						iOffs = aPost.indexOf('%');
						if (iOffs >= 0)
						{
							aVar = aPost.substring(0, iOffs) + aVar + aPost.substring(iOffs + 1);
						}
						aBonus.add(aSA + "|" + aVar);
					}
				}
			}
		}
	}

	/**
	 * Changes a path to make sure all instances of \ or / are replaced with File.separatorChar
	 *
	 * @param argFileName The path to be fixed
	 * @return
	 */
	public static String fixFilenamePath(final String argFileName)
	{
		return argFileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
//		final int length = argFileName.length();
//		StringBuffer fileName = new StringBuffer(length);
//		for (int i = 0; i < length; ++i)
//		{
//			final char curChar = argFileName.charAt(i);
//			if (curChar == '\\' || curChar == '/')
//			{
//				fileName.append(File.separatorChar);
//			}
//			else
//			{
//				fileName.append(curChar);
//			}
//		}
//		return fileName.toString();
	}

	/** java doesn't provide an xnor operaton, so we roll our own */

	public static boolean xnor(boolean a, boolean b)
	{
		return (a && b) || (!a && !b);
	}


	/**
	 * Add all iterated items to a collection.
	 *
	 * @param c The <code>Collection</code>
	 * @param it The <code>Iterator</code>
	 */
	public static void collectionAddIterator(Collection c, Iterator it)
	{
		while (it.hasNext())
		{
			c.add(it.next());
		}
	}
}
