/*
 * CharacterFeat.java
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

/*
 * $Header: /home/cvs/cvsgelo/cvsroot/guehene-dpl-pcgen\040v2.5.3/java/pcgen/core/CharacterFeat.java,v 1.1 2006/02/20 23:54:34 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * A feat that a character posesses, including any character
 * specific details. Lightweight object that links to the full
 * Feat definition.
 */
public class CharacterFeat
{
	// extends PObject implements Serializable

	///////////////////////////////////////
	// Fields - Attributes

	private ArrayList associatedList = new ArrayList();

	///////////////////////////////////////
	// Fields - Associations
	private Feat feat;

	///////////////////////////////////////
	// Constructors

	/**
	 * A character feat can only be constructed with a
	 * reference to the parent Feat.
	 */
	public CharacterFeat(Feat _feat)
	{
		feat = _feat;
	}

	///////////////////////////////////////
	// Methods - Accessors

	/**
	 * Returns sub-choices made for this feat, for this character.
	 * @return ArrayList of Strings of sub-choices made.
	 */
	public ArrayList getAssociatedList()
	{
		return associatedList;
	}

	public void addAssociatedList(String s)
	{
		associatedList.add(s);
	}

	public void removeAssociatedList(String s)
	{
		associatedList.remove(s);
	}

	public void setAssociatedList(ArrayList newAssociatedList)
	{
		associatedList = newAssociatedList;
	}

	// Take values from full Feat, as necessary.
	public String getName()
	{
		return feat.getName();
	}

	///////////////////////////////////////
	// Methods - Associations

	/*  public void setFeat(Feat _feat){ feat=_feat; } */
	public Feat getFeat()
	{
		return feat;
	}

	///////////////////////////////////////
	// Methods - Operations

	// Take values from full Feat, as necessary.
	public String toString()
	{
		return feat.toString();
	}

	/**
	 * Name of the full Feat, plus any sub-choices made for this
	 * character. Starts with the name of the feat, and then
	 * (for types other than weapon proficiencies), either appends
	 * a count of the times the feat is applied e.g. " (3x)",
	 * or a list of the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the feat
		// don't do for Weapon Profs
		StringBuffer aStrBuf = new StringBuffer(feat.getName());
		if (associatedList.size() > 0 && !feat.getName().endsWith("Weapon Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (feat.getChoiceString().length() == 0 || (feat.isMultiples() && feat.isStacks()))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append(Integer.toString((int)(associatedList.size() * feat.getCost())));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in getAssociatedList, e.g. " (Sub1, Sub2, ...)"
				for (Iterator e = associatedList.iterator(); e.hasNext();)
				{
					if (i > 0)
						aStrBuf.append(", ");
					aStrBuf.append((String)e.next());
					i++;
				}
				aStrBuf.append(')');
			}
		}

		return aStrBuf.toString();
	}


	/**
	 * Enhanced getAssociatedList.contains(), which parses the input
	 * parameter for "=", "+num" and "-num" to extract the value to look
	 * for.
	 */
	public boolean isInList(String aType)
	{
		if (aType.lastIndexOf("=") > -1)
			aType = aType.substring(aType.lastIndexOf("=") + 1);
		if (aType.lastIndexOf("+") > -1) // truncate at + sign if following character is a number
		{
			final String numString = "0123456789";
			final String aString = aType.substring(aType.lastIndexOf("+") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("+"));
		}
		if (aType.lastIndexOf("-") > -1) // truncate at - sign if following character is a number
		{
			final String numString = "0123456789";
			final String aString = aType.substring(aType.lastIndexOf("-") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("-"));
		}
		return associatedList.contains(aType);
	}


	/**
	 * Opens a Chooser to allow sub-choices for this feat.
	 * The actual items allowed to choose from are based on
	 * choiceString, as applied to current character. Choices
	 * already made (getAssociatedList) are indicated in list B.
	 */
	public void modChoices(boolean addIt)
	{
		String choiceString = feat.getChoiceString();
		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aTok.countTokens() < 1 || aPC == null || aPC.isImporting())
			return;
		int i = 0;
		int j = (int)((aPC.getFeats() + associatedList.size()) / feat.getCost());
		int num = 0;
		ArrayList availableList = new ArrayList(); // available list of choices
		ArrayList selectedList = new ArrayList(); // selected list of choices
		ArrayList aBonusList = new ArrayList();
		ArrayList rootArrayList = new ArrayList();
		String choiceType = aTok.nextToken();
		String choiceSec = getName();
		Chooser chooser = new Chooser();
		chooser.setPoolFlag(false); // user is not required to make any changes
		chooser.setAllowsDups(feat.isStacks()); // only stackable feats can be duped
		chooser.setVisible(false);
		chooser.setPool((int)(aPC.getFeats() / feat.getCost()));
		Iterator iter = null;
		String title = "Choices";
		if (Globals.getWeaponTypes().contains(choiceType))
		{
			title = choiceType + " Weapon Choice";
			ArrayList tArrayList = Globals.getWeaponProfs(choiceType);
			for (iter = tArrayList.iterator(); iter.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)iter.next();
				availableList.add(aProf);
			}
			SortedSet pcProfs = (SortedSet)
				aPC.getWeaponProfs(choiceType).clone();
			selectedList.addAll(pcProfs);
			for (Iterator setIter = pcProfs.iterator(); setIter.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)setIter.next();
				aPC.getWeaponProfList().remove(aProf.getName());
			}
			j -= (int)(associatedList.size() * feat.getCost());
			associatedList = new ArrayList(selectedList);
		}
		else if (choiceType.equals("SCHOOLS"))
		{
			title = "School Choice";
			availableList = Globals.getSchoolsList();
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("SPELLLIST"))
		{
			title = "Spell Choice";
			final PCClass aClass = aPC.getClassNamed("Wizard");
			if (aClass != null && aClass.getLevel().intValue() > 0 && aPC.adjStats(Constants.INTELLIGENCE) > 11)
			{
				for (iter = aClass.spellList().iterator(); iter.hasNext();)
				{
					Spell aSpell = (Spell)iter.next();
					if (!associatedList.contains(aSpell.getKeyName()))
						availableList.add(aSpell);
				}
				selectedList = new ArrayList(associatedList);
				num = selectedList.size();
				chooser.setPool(aPC.calcStatMod(Constants.INTELLIGENCE));
			}
		}
		else if (choiceType.equals("SALIST"))
		{
			title = "Special Ability Choice";
			feat.buildSALIST(choiceString, availableList, aBonusList);
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("SKILLS"))
		{
			title = "Skill Choice";
			for (iter = aPC.getSkillList().iterator(); iter.hasNext();)
			{
				Skill aSkill = (Skill)iter.next();
				availableList.add(aSkill);
			}
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("SKILLSNAMED"))
		{
			title = "Skill Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean startsWith = false;
				if (aString.endsWith("%"))
				{
					startsWith = true;
					aString = aString.substring(0, aString.length() - 1);
				}
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					Skill aSkill = (Skill)e1.next();
					if (aSkill.getKeyName().equals(aString) || (startsWith && aSkill.getKeyName().startsWith(aString)))
					{
						availableList.add(aSkill);
					}
				}
			}
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("SKILLLIST") || choiceType.equals("CCSKILLLIST") ||
			choiceType.equals("NONCLASSSKILLLIST"))
		{
			title = "Skill Choice";
			if (aTok.hasMoreTokens())
				choiceSec = new String(aTok.nextToken());
			if (choiceSec.length() > 0 && !choiceSec.equals("LIST"))
			{
				aTok = new StringTokenizer(choiceSec, ",", false);
				while (aTok.hasMoreTokens())
					availableList.add(aTok.nextToken());
			}
			else
			{
				for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
				{
					Skill aSkill = (Skill)iter.next();
					if (choiceType.equals("NONCLASSSKILLLIST") && (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1 ||
						aSkill.isExclusive().startsWith("Y")))
						continue;
					if (aSkill.getRootName().length() == 0)
						availableList.add(aSkill);
					if (aSkill.getRootName().length() > 0 && !rootArrayList.contains(aSkill.getRootName()))
						rootArrayList.add(aSkill.getRootName());
					if (aSkill.getRootName().length() > 0 && rootArrayList.contains(aSkill.getRootName()))
						availableList.add(aSkill);
				}
			}
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("SPELLLEVEL"))
		{
			// this will need to be re-worked at some point when I can think
			// of a better way.  This feat is different from the others in that
			// it requires a bonus to be embedded in the choice.  Probably this
			// whole feat methodology needs to be re-thought as its getting a bit
			// bloated - a generic way to embed bonuses could be done to simplify
			// this all tremendously instead of so many special cases.
			StringTokenizer cTok = new StringTokenizer(choiceString, "[]", false);
			String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
				aBonusList.add(cTok.nextToken());
			feat.getChoices(choices, aBonusList);
			associatedList.clear();
			for (Iterator ii = feat.saveList.iterator(); ii.hasNext(); ii.next())
				associatedList.add("placeholder");
		}
		else if (choiceType.equals("WEAPONFOCUS"))
		{
			title = "WeaponFocus Choice";
			//TODO: Should be CharacterFear
			Feat weaponFocusCharacterFeat = aPC.getFeatNamed("Weapon Focus");
			availableList = (ArrayList)weaponFocusCharacterFeat.getAssociatedList().clone();
			//bArrayList = (ArrayList)getAssociatedList.clone();
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("WEAPONPROFS"))
		{
			title = "Weapon Prof Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.equals("LIST"))
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						String bString = (String)setIter.next();
						if (!availableList.contains(bString))
							availableList.add(bString);
					}
				else if (aString.startsWith("Size."))
				{
					if (aPC.sizeInt() >=
						aPC.sizeIntForSize(aString.substring(5, 6)) &&
						aPC.getWeaponProfList().contains(aString.substring(7)) &&
						!availableList.contains(aString.substring(7)))
						availableList.add(aString.substring(7));
				}
				else if (aString.startsWith("WSize."))
				{
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						String bString = (String)setIter.next();
						WeaponProf wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
							continue;
						if (aString.endsWith("Light") && wp.isLight() && !availableList.contains(bString))
							availableList.add(bString);
						if (aString.endsWith("1 handed") && wp.isOneHanded() && !availableList.contains(bString))
							availableList.add(bString);
						if (aString.endsWith("2 handed") && wp.isTwoHanded() && !availableList.contains(bString))
							availableList.add(bString);
					}
				}
				else if (aString.startsWith("SpellCaster."))
				{
					if (aPC.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
						availableList.add(aString.substring(12));
				}
				else if (aString.startsWith("ADD."))
				{
					if (!availableList.contains(aString.substring(4)))
						availableList.add(aString.substring(4));
				}
				else if (aString.startsWith("TYPE."))
				{
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						String bString = (String)setIter.next();
						WeaponProf wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
							continue;
						Equipment eq = Globals.getEquipmentKeyed(wp.getKeyName());
						if (eq == null)
							continue;
						if (eq.getType().lastIndexOf(aString.substring(5)) > -1 && !availableList.contains(wp.getName()))
							availableList.add(wp.getName());
					}
				}
				else
				{
					if (aPC.getWeaponProfList().contains(aString) &&
						!availableList.contains(aString))
						availableList.add(aString);
				}
			}
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("HP"))
		{
			if (aTok.hasMoreTokens())
				choiceSec = aTok.nextToken();
			availableList.add(choiceSec);
			for (Iterator e1 = associatedList.iterator(); e1.hasNext(); e1.next())
				selectedList.add(choiceSec);
		}
		else if (choiceType.startsWith("FEAT="))
		{
			//TODO: Should be CharacterFeat
			Feat otherCharacterFeat = aPC.getFeatNamed(choiceType.substring(5));
			if (otherCharacterFeat != null)
				availableList = otherCharacterFeat.getAssociatedList();
			selectedList = new ArrayList(associatedList);
		}
		else if (choiceType.equals("FEATLIST"))
		{
			selectedList = new ArrayList(associatedList);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE="))
				{
					aString = aString.substring(5);
					if (!feat.isStacks() && availableList.contains(aString))
						continue;
					// TODO: should be characterFeat & CharacterFeat
					for (Iterator e1 = aPC.aggregateFeatList().iterator(); e1.hasNext();)
					{
						Feat currentCharacterFeat = (Feat)e1.next();
						if (currentCharacterFeat.getType().equals(aString) && (feat.isStacks() || (!feat.isStacks() && !availableList.contains(currentCharacterFeat.getName()))))
							availableList.add(currentCharacterFeat.getName());
					}
				}
				else if (aPC.getFeatNamed(aString) != null)
				{
					if (feat.isStacks() || (!feat.isStacks() && !availableList.contains(aString)))
						availableList.add(aString);
				}
			}
		}
		else if (choiceType.equals("SPELLCLASSES"))
		{
			title = "Spellcaster Classes";
			for (iter = aPC.getClassList().iterator(); iter.hasNext();)
			{
				PCClass aClass = (PCClass)iter.next();
				if (!aClass.getSpellBaseStat().equals("None"))
					availableList.add(aClass);
			}
			selectedList = new ArrayList(associatedList);
		}
		else
		{
			title = "Selections";
			availableList.add(choiceType);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (feat.isStacks() || (!feat.isStacks() && !availableList.contains(aString)))
					availableList.add(aString);
			}
			selectedList = new ArrayList(associatedList);
		}

		title = title + " (" + getName() + ")";
		chooser.setTitle(title);
		if (!choiceType.equals("SPELLLEVEL"))
		{
			Globals.sortChooserLists(availableList, selectedList);
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.show();
		}

		if (choiceType.equals("SPELLLIST"))
		{
			int x = aPC.getFeats();
			if (chooser.getSelectedList().size() > num)
				aPC.setFeats(x - 1);
			if (chooser.getSelectedList().size() < num)
				aPC.setFeats(x + 1);
		}
		else if (choiceType.equals("SALIST"))
		{
			//
			// remove previous selections from special abilities
			// aBonusList contains all possible selections in form: <displayed info>|<special ability>
			//
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				for (int x = 0; x < aBonusList.size(); x++)
				{
					final String bString = (String)aBonusList.get(x);
					if (bString.startsWith(aString + "|"))
					{
						feat.removeBonus(bString.substring(bString.indexOf('|') + 1), "");
						break;
					}
				}
			}
		}

		for (iter = feat.getCSkillList().iterator(); iter.hasNext();)
		{
			String aString = (String)iter.next();
			if (!aString.equals("LIST") && !Globals.getFeatNamed(getName()).getCSkillList().contains(aString))
				iter.remove();
		}
		if (!choiceType.equals("SPELLLEVEL"))
		{
			feat.getCcSkillList().clear(); // Why?
			associatedList.clear();
		}
		for (i = 0; i < chooser.getSelectedList().size(); i++)
		{
			if (choiceType.equals("HP"))
				associatedList.add("CURRENTMAX");
			else if (feat.isMultiples() && !feat.isStacks())
			{
				if (!associatedList.contains(chooser.getSelectedList().get(i)))
					associatedList.add(chooser.getSelectedList().get(i));
			}
			else
			{
				final String aString = (String)chooser.getSelectedList().get(i);
				associatedList.add(aString);
				// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
				for (int x = 0; x < aBonusList.size(); x++)
				{
					final String bString = (String)aBonusList.get(x);
					if (bString.startsWith(aString + "|"))
					{
						feat.addBonusList(bString.substring(bString.indexOf('|') + 1));
						break;
					}
				}
			}

			if (choiceType.equals("SKILLLIST") || choiceType.equals("NONCLASSSKILLLIST"))
			{
				String aString = (String)chooser.getSelectedList().get(i);
				if (rootArrayList.contains(aString))
				{
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
						Skill aSkill = (Skill)e2.next();
						if (aSkill.getRootName().equals(aString))
							feat.getCSkillList().add(aSkill.getName());
					}
				}
				else
					feat.getCSkillList().add(aString);
			}
			else if (choiceType.equals("CCSKILLLIST"))
			{
				String aString = (String)chooser.getSelectedList().get(i);
				if (rootArrayList.contains(aString))
				{
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
						Skill aSkill = (Skill)e2.next();
						if (aSkill.getRootName().equals(aString))
							feat.getCcSkillList().add(aSkill.getName());
					}
				}
				else
					feat.getCcSkillList().add(aString);
			}
			if (Globals.getWeaponTypes().contains(choiceType))
				aPC.addWeaponProf(chooser.getSelectedList().get(i).toString());
		}
		if (!choiceType.equals("SPELLLIST"))
			aPC.setFeats((int)((j - associatedList.size() + selectedList.size()) * feat.getCost()));
	}


	// ??? related to getAssociatedList
	// doesn't appear to be used anywhere!
	public int addStatBonuses(String aString)
	{
		int retVal = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (Iterator e = associatedList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)aPC.getClassNamed((String)e.next());
			if (aClass != null && aClass.getSpellBaseStat().equals(aString))
				retVal++;
		}
		return retVal;
	}

	// ??? related to getAssociatedList
	// used in Skill
	public boolean hasCSkill(String aName)
	{
		if (feat.getCSkillList().contains(aName))
			return true;
		if (feat.getCSkillList().contains("LIST"))
		{
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				if (aName.startsWith(aString) || aString.startsWith(aName))
					return true;
			}
		}
		for (Iterator e = feat.getCSkillList().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

}
