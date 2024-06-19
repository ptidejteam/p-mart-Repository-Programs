/*
 * PObject.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.Chooser;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PObject extends Object implements Cloneable, Serializable, Comparable
{

	protected boolean isNewItem = true;
	protected boolean isSpecified = false;
	protected ArrayList bonusList = null;
	private ArrayList variableList = null;
	protected String name = "";
	private ArrayList saveList = null;
	protected int sourceFileIndex = -1;
	protected int sourceIndex = -1;
	protected boolean visible = true;
	private String source = "";
	private String sourcePage = "";
	protected String qualifyString = "alwaysValid";
	protected String keyName = "";
	protected String choiceString = "";
	private ArrayList preReqList = null;
	private ArrayList characterSpellList = null;
	private ArrayList selectedWeaponProfBonus = null;
	private ArrayList associatedList = null;
	private ArrayList myTypeList = null;
	protected int baseQuantity = 1;
	protected HashMap bonusMap = new HashMap();
	public static ArrayList emptyBonusList = new ArrayList();

	public Object clone()
	{
		PObject retVal = null;
		try
		{
			retVal = (PObject)super.clone();
			if (characterSpellList!=null)
				retVal.characterSpellList = (ArrayList)characterSpellList.clone();
			retVal.isSpecified = isSpecified;
			if (bonusList!=null)
				retVal.bonusList = (ArrayList)bonusList.clone();
			if (variableList != null)
				retVal.variableList = (ArrayList)variableList.clone();
			retVal.setName(name);
			retVal.saveList = null; // starts out empty
			retVal.sourceFileIndex = sourceFileIndex;
			retVal.sourceIndex = sourceIndex;
			retVal.visible = visible;
			retVal.source = source;
			retVal.sourcePage = sourcePage;
			retVal.qualifyString = qualifyString;
			retVal.setKeyName(keyName);
			retVal.choiceString = choiceString;
			if (preReqList !=null)
				retVal.preReqList = (ArrayList)preReqList.clone();
			if (associatedList != null)
				retVal.associatedList = (ArrayList)associatedList.clone();
			if (myTypeList != null)
				retVal.myTypeList = (ArrayList)myTypeList.clone();
			retVal.baseQuantity = baseQuantity;
			if (selectedWeaponProfBonus!=null)
				retVal.selectedWeaponProfBonus = (ArrayList)selectedWeaponProfBonus.clone();

		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		return retVal;
	}

	public String toString()
	{
		return name;
	}

	public int compareTo(Object obj)
	{
		//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
		return this.name.compareTo(((PObject)obj).name);
	}

	public void setSourceFile(String sourceFile)
	{
		sourceFileIndex = PersistenceManager.saveSourceFile(sourceFile);
	}

	public String getSourcePage()
	{
		String pageString = "";
		if (source.startsWith("SOURCEPAGE:"))
			pageString = source.substring(11);
		return pageString;
	}

	public String getSourceShort()
	{
		if (source.equals("") && (sourceIndex < 0))
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
			bigString = bigString.substring(0, bigString.length() - 2);
		if (bigString.length() > 0)
			bigString += "|";
		bigString += source;
		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		String shortString = "";
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCESHORT:"))
				shortString = aString.substring(12);
		}
		// When I say short, I mean short!
		// Truncate at 8 chars
		if (shortString.length() > 8)
			shortString = shortString.substring(0, 8);
		return shortString;
	}


	public String getSource()
	{
		if (source.equals("") && (sourceIndex < 0))
		{
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		}

		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
			bigString = bigString.substring(0, bigString.length() - 2);
		if (bigString.length() > 0)
			bigString += "|";
		bigString += source;
		StringBuffer sString = new StringBuffer();
		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		String pageString = "";
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCEPAGE:"))
				pageString = aString.substring(11);
			switch (Globals.getSourceDisplay())
			{
				case Constants.SOURCELONG:
					if (aString.startsWith("SOURCELONG:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
							sString.append(" ");
						if (aString.startsWith("SOURCELONG:"))
							sString.append(aString.substring(11));
						else if (sString.length() == 0)
							sString.append(aString.substring(7));
						else
							pageString = aString.substring(7);
					}
					break;
				case Constants.SOURCESHORT:
					if (aString.startsWith("SOURCESHORT:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
							sString.append(" ");
						if (aString.startsWith("SOURCESHORT:"))
							sString.append(aString.substring(12));
						else if (sString.length() == 0)
							sString.append(aString.substring(7));
					}
					break;
				case Constants.SOURCEWEB:
					if (aString.startsWith("SOURCEWEB:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
							sString.append(" ");
						if (aString.startsWith("SOURCEWEB:"))
							sString.append(aString.substring(10));
						else if (sString.length() == 0)
							sString.append(aString.substring(7));
					}
					break;
			}
		}

		if (pageString.length() > 0 && Globals.getSourceDisplay() != Constants.SOURCEWEB)
		{
			if (sString.length() > 0)
			{
				sString.append(", ").append(pageString);
			}
			else
			{
				return pageString;
			}
		}
		return sString.toString();
	}

	public void setSource(int srcIdx, String aSource)
	{
		this.sourceIndex = srcIdx;
		setSource(aSource);
	}

	public void setSource(final String aSource)
	{
		StringBuffer src = new StringBuffer(source);
		if (src.length() > 0)
			src.append("|");
		source = src.append(aSource).toString();
	}

	public String getSourceWeb()
	{
		if (source.equals("") && (sourceIndex < 0))
		{
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		}

		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
			bigString = bigString.substring(0, bigString.length() - 2);
		if (bigString.length() > 0)
			bigString += "|";
		bigString += source;
		StringBuffer sString = new StringBuffer();
		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCEWEB:"))
			{
				sString.append(aString.substring(10));
				break;
			}
		}
		return sString.toString();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			name = aString;
			keyName = aString;
		}
	}

	public String getKeyName()
	{
		return keyName;
	}

	public void setKeyName(String aString)
	{
		keyName = aString;
	}

	public String getSourceFile()
	{
		return PersistenceManager.savedSourceFile(sourceFileIndex);
	}

	public void addVariable(String entry)
	{
		if (variableList==null) variableList=new ArrayList();
		variableList.add(entry);
	}

	public String getVariable(int i)
	{
		return (String)variableList.get(i);
	}

	public int getVariableCount()
	{
		if (variableList==null) return 0;
		return variableList.size();
	}

	public void addSelectedWeaponProfBonus(String entry)
	{
		if (selectedWeaponProfBonus==null) selectedWeaponProfBonus=new ArrayList();
		selectedWeaponProfBonus.add(entry);
	}

	public void clearSelectedWeaponProfBonus()
	{
		selectedWeaponProfBonus=null;
	}

	public String getSelectedWeaponProfBonus(int i)
	{
		return (String)selectedWeaponProfBonus.get(i);
	}

	public int getSelectedWeaponProfBonusCount()
	{
		if (selectedWeaponProfBonus==null) return 0;
		return selectedWeaponProfBonus.size();
	}

	public void addSelectedWeaponProfBonusTo(Collection collection)
	{
		if (selectedWeaponProfBonus!=null)
			collection.addAll(selectedWeaponProfBonus);
	}

	public void clearCharacterSpells()
	{
		characterSpellList=null;
	}

	public CharacterSpell getCharacterSpell(int i)
	{
		return (CharacterSpell)characterSpellList.get(i);
	}

	public void addCharacterSpell(CharacterSpell spell)
	{
		if (characterSpellList==null) characterSpellList=new ArrayList();
		characterSpellList.add(spell);
	}

	public boolean removeCharacterSpell(CharacterSpell spell)
	{
		if (characterSpellList==null) return false;
		return characterSpellList.remove(spell);
	}

	public int getCharacterSpellCount()
	{
		if (characterSpellList==null) return 0;
		return characterSpellList.size();
	}

	public void sortCharacterSpellList()
	{
		if (characterSpellList!=null)
			Collections.sort(characterSpellList);
	}

	//
	// return an ArrayList of CharacterSpell with following criteria:
	// Spell aSpell  (ignored if null),
	// book          (ignored if ""),
	// level         (ignored if < 0),
	// owner         (Race, Domain or PCClass, ignored if null)
	// owningClass   (ignored if null)
	// usually owner = owningClass, but owner could be a Domain and
	// owningClass a PCClass
	//
	public ArrayList getCharacterSpell(Spell aSpell, String book, int level, PObject owner, PCClass owningClass)
	{
		ArrayList aList = new ArrayList();
		if (getCharacterSpellCount()==0)
			return aList;
		String inOwnerS = "";
		String inOwningCS = "";

		if (owner != null)
			inOwnerS = owner.toString();
		if (owningClass != null)
			inOwningCS = owningClass.toString();

		for (int i = 0; i<getCharacterSpellCount(); i++)
		{
			final CharacterSpell cs = getCharacterSpell(i);
			final Spell bSpell = (Spell)cs.getSpell();
			String csOwnerS = "";
			String csOwningCS = "";

			if (cs.getOwner() != null)
				csOwnerS = cs.getOwner().toString();
			if (cs.getOwningClass() != null)
				csOwningCS = cs.getOwningClass().toString();

			if ((aSpell == null || aSpell.equals(bSpell)) &&
				(book.length() == 0 || cs.getSpellBook().equals(book)) &&
				(level == -1 || cs.getAdjustedLevel() == level) &&
				(owner == null || csOwnerS.equals(inOwnerS)) &&
				(owningClass == null || csOwningCS.equals(inOwningCS)))
			{
				aList.add(cs);
			}
		}
		return aList;
	}

	//
	// return the first CharacterSpell with the following criteria:
	// Spell aSpell  (ignored if null)
	// book          (ignored if "")
	// level         (ignored if < 0)
	// owner         (Race, Domain or PCClass, ignored if null)
	// owningClass   (ignored if null)
	// usually owner = owningClass, but owner could be a Domain and
	// owningClass a PCClass
	//
	public CharacterSpell getFirstCharacterSpell(Spell aSpell, String book, int level, PObject owner, PCClass owningClass)
	{
		if (getCharacterSpellCount()==0)
			return null;
		String inOwnerS = "";
		String inOwningCS = "";

		if (owner != null)
			inOwnerS = owner.toString();
		if (owningClass != null)
			inOwningCS = owningClass.toString();

		for (int i = 0; i<getCharacterSpellCount(); i++)
		{
			final CharacterSpell cs = getCharacterSpell(i);
			final Spell bSpell = (Spell)cs.getSpell();
			String csOwnerS = "";
			String csOwningCS = "";

			if (cs.getOwner() != null)
				csOwnerS = cs.getOwner().toString();
			if (cs.getOwningClass() != null)
				csOwningCS = cs.getOwningClass().toString();

			if ((aSpell == null || aSpell.equals(bSpell)) &&
				(book.length() == 0 || cs.getSpellBook().equals(book)) &&
				(level == -1 || cs.getAdjustedLevel() == level) &&
				(owner == null || csOwnerS.equals(inOwnerS)) &&
				(owningClass == null || csOwningCS.equals(inOwningCS)))
			{
				return cs;
			}
		}
		return null;
	}

	public String getChoiceString()
	{
		return choiceString;
	}

	public void setChoiceString(String choiceString)
	{
		this.choiceString = choiceString;
	}

	public boolean isInList(String aType)
	{
		return false;
	}

	public ArrayList getBonusList()
	{
		if (bonusList!=null)
			return bonusList;
		return emptyBonusList;
	}

	public String getBonusListString()
	{
		String s = getBonusList().toString();

		if (s.equals("[]"))

		{
			return "";
		}
		// Don't display the surrounding brackets.
		else
		{
			return s.substring(1, s.length() - 1);
		}
	}

	public void addBonusList(String aString)
	{
		if (bonusList==null)
			bonusList = new ArrayList();
		bonusList.add(aString);
	}

	/**
	 * Return the number of saves in the list
	 * @author: Scott Ellsworth 20020601
	 *
	 * @return save list
	 */

	public int getSaveCount()
	{
		if (saveList==null) return 0;
		return saveList.size();
	}

	public boolean containsSave(String save)
	{
		if (saveList==null) return false;
		return saveList.contains(save);
	}

	public String getSave(int i)
	{
		return (String)saveList.get(i);
	}

	public void addSave(String aString)
	{
		if (saveList==null) saveList=new ArrayList();
		saveList.add(aString);
	}

	public void removeSave(String bonusString)
	{
		if (saveList!=null)
		{
			int index = saveList.indexOf(bonusString);
			if (index >= 0)
			{
				saveList.remove(index);
			}
			else
			{
				Globals.debugPrint("removeBonus: Could not find bonus: " + bonusString + " in saveList.");
			}
		}
		else
		{
			Globals.debugPrint("removeBonus: Could not find bonus: " + bonusString + " in saveList.");
		}
	}

	public int getAssociatedCount()
	{
		if (associatedList==null) return 0;
		return associatedList.size();
	}

	public boolean containsAssociated(String associated)
	{
		if (associatedList==null) return false;
		return associatedList.contains(associated);
	}

	public String getAssociated(int i)
	{
		return (String)associatedList.get(i);
	}

	public void clearAssociated()
	{
		associatedList=null;
	}

	public void addAssociated(String aString)
	{
		if (associatedList==null) associatedList=new ArrayList();
		associatedList.add(aString);
	}

	public void addAllToAssociated(Collection collection)
	{
		if (associatedList==null) associatedList=new ArrayList();
		associatedList.addAll(collection);
	}

	public void addAssociatedTo(Collection collection)
	{
		if (associatedList!=null)
			collection.addAll(associatedList);
	}

	public void setAssociated(int index, String aString)
	{
		associatedList.set(index, aString);
	}

	public void sortAssociated()
	{
		if (associatedList!=null)
			Collections.sort(associatedList);
	}

	public Object removeAssociated(int i)
	{
		if (associatedList==null)
			throw new IndexOutOfBoundsException("size is 0, i="+i);
		return associatedList.remove(i);
	}

	public boolean removeAssociated(String associated)
	{
		if (associatedList==null)
			return false;
		boolean ret=associatedList.remove(associated);
		if (associatedList.size()==0)
			associatedList=null;
		return ret;
	}

	public void addPreReq(String preReq)
	{
		if (preReq.equals("PRE:.CLEAR"))
			preReqList = null;
		else
		{
			if (preReqList==null)
				preReqList = new ArrayList();
			preReqList.add(preReq);
		}
	}

	public void addPreReqTo(Collection collection)
	{
		if (preReqList!=null)
			collection.addAll(preReqList);
	}

	public String getPreReq(int i)
	{
		return (String)preReqList.get(i);
	}

	public int getPreReqCount()
	{
		if (preReqList==null)
			return 0;
		return preReqList.size();
	}

	public boolean removePreReq(String preReq)
	{
		if (preReqList==null)
			return false;
		return preReqList.remove(preReq);
	}

	public void setPreReq(int index, String aString)
	{
		preReqList.set(index, aString);
	}

	public void addMyType(String myType)
	{
		if (myTypeList==null)
			myTypeList = new ArrayList();
		myTypeList.add(myType);
	}

	public void addMyTypeTo(Collection collection)
	{
		if (myTypeList!=null)
			collection.addAll(myTypeList);
	}

	public void clearMyType()
	{
		myTypeList = null;
	}

	public boolean containsMyType(String myType)
	{
		if (myTypeList==null) return false;
		return myTypeList.contains(myType);
	}

	public String getMyType(int i)
	{
		return (String)myTypeList.get(i);
	}

	public int getMyTypeCount()
	{
		if (myTypeList==null)
			return 0;
		return myTypeList.size();
	}

	public boolean removeMyType(String myType)
	{
		if (myTypeList==null)
			return false;
		return myTypeList.remove(myType);
	}

	public HashMap getBonusMap()
	{
		return bonusMap;
	}

	public void setType(String aString)
	{
		clearMyType();
		addType(aString);
	}

	public void addType(String aString)
	{
		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if (!containsMyType(aType))
			{
				addMyType(aType);
			}
		}
	}

	public void removeType(String aString)
	{
		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			removeMyType(aType);
		}
	}

	public String getType()
	{
		StringBuffer aType = new StringBuffer(getMyTypeCount() * 5); //Just a guess.
		for (int i = 0; i < getMyTypeCount(); i++)
		{
			if (i != 0)
				aType.append(".");
			aType.append(getMyType(i));
		}
		return aType.toString();
	}

	public boolean isType(String aType)
	{
		return containsMyType(aType.toUpperCase());
	}

	public int getBaseQty()
	{
		return baseQuantity;
	}

	public void setBaseQty(String aString)
	{
		baseQuantity = Integer.parseInt(aString);
	}

	public int bonusTo(String aType, String aName)
	{
		return bonusTo(aType, aName, Globals.getCurrentPC());
	}

	public int bonusTo(String aType, String aName, Object obj)
	{
		return bonusTo(aType, aName, obj, bonusList);
	}

	public int bonusTo(String aType, String aName, Object obj, ArrayList aBonusList)
	{
		if (aBonusList==null || aBonusList.size() == 0)
			return 0;

		int retVal = 0;
		int iTimes = 1;

		if (aType.equals("VAR") || aType.equals("HP"))
		{
			iTimes = Math.max(1, getAssociatedCount());
			//
			// SALIST will stick BONUS:VAR|... into bonus list so don't multiply
			//
			if (choiceString.startsWith("SALIST|") && choiceString.indexOf("|VAR|") >= 0)
			{
				iTimes = 1;
			}
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		final String aTypePlusName = new StringBuffer(aType).append(".").append(aName).append(".").toString();

		for (Iterator b = aBonusList.iterator(); b.hasNext();)
		{
			String bString = ((String)b.next()).toUpperCase();

			if (getAssociatedCount() != 0)
			{
				final int idx = bString.indexOf("%VAR");
				if ((idx >= 0) && (getAssociatedCount() != 0))
				{
					final String firstPart = bString.substring(0, idx);
					final String secondPart = bString.substring(idx + 4);
					for (int i = 1; i < getAssociatedCount(); i++)
					{
						String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart).toString();
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes);
					}
					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString();
				}
			}
			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes);
		}
		return retVal;
	}


	private int calcBonus(String bString, String aType, String aName, String aTypePlusName, Object obj, int iTimes)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);
		if (aTok.countTokens() < 3)
		{
			System.out.println("Badly formed BONUS:" + bString);
			return 0;
		}
		String aString = aTok.nextToken();

		if ((!aString.equals(aType) && !aString.endsWith("%LIST")) ||
			(aString.endsWith("%LIST") && !isInList(aType)))
			return 0;

		final String aList = aTok.nextToken();
		aString = aTok.nextToken();

		int aBonus = 0;
		if (obj instanceof PlayerCharacter)
		{
			aBonus = ((PlayerCharacter)obj).getVariableValue(aString, "").intValue();
		}
		else if (obj instanceof Equipment)
		{
			aBonus = ((Equipment)obj).getVariableValue(aString, "", "").intValue();
		}
		else
		{
			try
			{
				aBonus = Integer.parseInt(aString);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				System.out.println("NumberFormatException in BONUS:" + bString);
			}
		}

		ArrayList preReqList = new ArrayList();
		String possibleBonusTypeString = null;
		while (aTok.hasMoreTokens())
		{
			final String pString = aTok.nextToken();
			if (pString.startsWith("PRE") || pString.startsWith("!PRE"))
				preReqList.add(pString);
			else if (pString.startsWith("TYPE=") || pString.startsWith("STAT="))
				possibleBonusTypeString = pString.substring(5);
		}
		// must meet criteria before adding any bonuses
		if (!passesPreReqTestsForList(preReqList))
		{
			return 0;
		}

		int bonus = 0;
		if (aList.equals("LIST") && isInList(aName))
		{
			bonus += aBonus;
		}
		final StringTokenizer bTok = new StringTokenizer(aList, ",", false);
		if (aList.equals("LIST"))
		{
			bTok.nextToken();
		}

		String bonusTypeString = null;
		while (bTok.hasMoreTokens())
		{
			if (bTok.nextToken().equals(aName))
			{
				bonus += aBonus;
				bonusTypeString = possibleBonusTypeString;
			}
		}

		if (obj instanceof PlayerCharacter)
		{
			((PlayerCharacter)obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		else if (obj instanceof Equipment)
		{
			((Equipment)obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		else
		{
			setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		return bonus * iTimes;
	}


	public void getChoices(String aChoice, ArrayList selectedBonusList, ArrayList availableList, ArrayList selectedList, boolean process)
	{
		getChoices(aChoice, selectedBonusList, this, availableList, selectedList, true);
	}

	public void getChoices(String aChoice, ArrayList selectedBonusList)
	{
		ArrayList availableList = new ArrayList();
		ArrayList selectedList = new ArrayList();
		getChoices(aChoice, selectedBonusList, this, availableList, selectedList, true);
	}

	public void getChoices(String aChoice, ArrayList selectedBonusList, PObject theObj, ArrayList availableList, ArrayList selectedList, boolean process)
	{
		if (!choiceString.startsWith("SPELLLEVEL") && !aChoice.startsWith("SPELLLEVEL") &&
			!aChoice.startsWith("WEAPONPROF"))
			return;

		if (aChoice.length() == 0)
		{
			aChoice = choiceString;
		}
		StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be SPELLLEVEL or WEAPONPROF
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String tempString = aTok.nextToken();

		final int stat = Globals.getStatFromAbbrev(tempString);
		int pool = 0;
		try
		{
			if (stat >= 0)
			{
				pool = aPC.calcStatMod(stat);
			}
			else
			{
				pool = Integer.parseInt(tempString);
			}
		}
		catch (NumberFormatException e)
		{
			//Should this be ignored?
		}

		boolean dupsAllowed = true;
		String title = "";
		ArrayList otherArrayList = new ArrayList();
		ArrayList cArrayList = new ArrayList();
		if (aChoice.startsWith("SPELLLEVEL"))
		{
			getSpellTypeChoices(aChoice, availableList, cArrayList); // get appropriate choices for chooser
		}
		else if (aChoice.startsWith("WEAPONPROF")) // determine appropriate choices for chooser
		{
			dupsAllowed = false;
			title = "Weapon Choice(s)";
			theObj.addSelectedWeaponProfBonusTo(selectedList);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean adding = false;
				String cString = aString;
				if (aString.lastIndexOf("[") > -1)
				{
					final StringTokenizer bTok = new StringTokenizer(aString, "[]", false);
					final String bString = bTok.nextToken();
					adding = true;
					while (bTok.hasMoreTokens())
					{
						otherArrayList.add(bString + "|" + bTok.nextToken());
					}
					aString = bString;
					Globals.debugPrint("bString=" + bString);
				}
				if (aString.equals("DEITYWEAPON"))
				{
					if (aPC.getDeity() != null)
					{
						StringBuffer weaponList = new StringBuffer((String)aPC.getDeity().getFavoredWeapon());
						if (weaponList.toString().equalsIgnoreCase("ALL") || weaponList.toString().equalsIgnoreCase("ANY"))
						{
							weaponList.setLength(0);
							for (Iterator wi = Globals.getWeaponProfList().iterator(); wi.hasNext();)
							{
								if (weaponList.length() > 0)
									weaponList.append("|");
								weaponList.append((String)wi.next().toString());
							}
						}
						StringTokenizer bTok = new StringTokenizer(weaponList.toString(), "|", false);
						while (bTok.hasMoreTokens())
						{
							String bString = bTok.nextToken();
							availableList.add(bString);
							if (adding)
							{
								final StringTokenizer cTok = new StringTokenizer(cString, "[]", false);
								cTok.nextToken(); //Read and throw away a token
								while (cTok.hasMoreTokens())
									otherArrayList.add(bString + "|" + cTok.nextToken());
							}
						}
					}
				}
				else if (aString.startsWith("TYPE="))
				{
					final StringTokenizer bTok = new StringTokenizer(aString.substring(5), ".", false);
					ArrayList typeList = new ArrayList();
					int iSize = -1;
					while (bTok.hasMoreTokens())
					{
						final String bString = bTok.nextToken();
						if (bString.startsWith("SIZE="))
						{
							iSize = Globals.sizeInt(bString.substring(5));
						}
						else
						{
							typeList.add(bString);
						}
					}

					for (Iterator ei = Globals.getEquipmentList().iterator(); ei.hasNext();)
					{
						final Equipment aEq = (Equipment)ei.next();
						if (!aEq.isWeapon())
						{
							continue;
						}

						boolean bOkay = true;
						for (Iterator ti = typeList.iterator(); ti.hasNext();)
						{
							if (!aEq.isType((String)ti.next()))
							{
								bOkay = false;
								break;
							}
							if (iSize >= 0)
							{
								bOkay &= Globals.sizeInt(aEq.getSize()) == iSize;
							}
							if (bOkay)
							{
								WeaponProf wp = Globals.getWeaponProfNamed(aEq.profName());
								if ((wp != null) && !availableList.contains(wp.getName()))
								{
									final String bString = wp.getName();
									availableList.add(bString);
									final StringTokenizer cTok = new StringTokenizer(cString, "[]", false);
									if (cTok.hasMoreTokens())
									{
										cTok.nextToken(); //Read and throw away a token
										while (cTok.hasMoreTokens())
										{
											otherArrayList.add(bString + "|" + cTok.nextToken());
										}
									}
								}
							}
						}
					}
				}
				else
				{
					availableList.add(aString);
				}
			}
		}

		if (!process)
		{
			return;
		}

		Chooser c = new Chooser();
		c.setAllowsDups(dupsAllowed);
		if (title.length() != 0)
		{
			c.setTitle(title);
		}
		pool -= c.getSelectedList().size();

		c.setPool(Math.max(0, pool));
		c.setPoolFlag(false);			// Allow cancel as clicking the x will cancel anyways
		//Collections.sort(availableList);
		Globals.sortChooserLists(availableList, selectedList);
		c.setAvailableList(availableList);
		c.setSelectedList(selectedList);
		c.setUniqueList(cArrayList);
		c.show();


		// Currently:
		// WEAPONPROF|x|choice1[WEAPONPROF][FEAT=xxx]|choice2[WEAPONPROF][FEAT=xxx]|...|choicen[WEAPONPROF][FEAT=xxx][FEAT=yyy]...
		//
		// wouldn't this be better?
		// WEAPONPROF|x|choice1|choice2|...|choicen[WEAPONPROF][FEAT=xxx][FEAT=yyy]...
		if (aChoice.startsWith("WEAPONPROF"))
		{
			theObj.clearSelectedWeaponProfBonus();
			aPC.setAutomaticFeatsStable(false);
			for (int index = 0; index < c.getSelectedList().size(); index++)
			{
				if (otherArrayList.isEmpty())
					continue;
				String aString = (String)c.getSelectedList().get(index);
				for (Iterator e = otherArrayList.iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					aTok = new StringTokenizer(bString, "|", false);
					if (aTok.nextToken().equals(aString))
					{
						String cString = aTok.nextToken();
						if (cString.startsWith("WEAPONPROF"))
						{
							theObj.addSelectedWeaponProfBonus(aString);
						}
						//
						// TODO: This needs to be added to the automatic feat list
						//
						else if (cString.startsWith("FEAT="))
						{
							if (theObj instanceof Domain)
							{
								theObj.clearAssociated();
								Feat aFeat = (Feat)Globals.getFeatKeyed(cString.substring(5)).clone();
								if (aFeat != null)
								{
									theObj.addAssociated("FEAT?" + aFeat.getName() + "(" + aString + ")");
								}
							}
							else
							{
								Feat aFeat = aPC.getFeatNamed(cString.substring(5));
								if (aFeat == null)
								{
									aFeat = (Feat)Globals.getFeatKeyed(cString.substring(5)).clone();
									if (aFeat != null)
									{
										aPC.addFeat(aFeat);
									}
								}
								if ((aFeat != null) && !aFeat.containsAssociated(aString))
								{
									aFeat.addAssociated(aString);
								}
							}
						}
					}
				}
			}
		}
		else
		{
			for (int index = 0; index < c.getSelectedList().size(); index++)
			{
				final String aString = (String)c.getSelectedList().get(index);
				if (selectedBonusList.isEmpty())
					continue;
				for (Iterator e = selectedBonusList.iterator(); e.hasNext();)
				{
					final String bString = (String)e.next();
					applyBonus(bString, aString, theObj);
				}
			}
		}
		return;
	}

	public void getSpellTypeChoices(String aChoice, ArrayList availList, ArrayList uniqueList)
	{
		StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be SPELLLEVEL
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			while (!aString.startsWith("CLASS=") && !aString.startsWith("TYPE=") && aTok.hasMoreTokens())
				aString = aTok.nextToken();
			if (!aTok.hasMoreTokens())
				break;
			boolean endIsUnique = false;
			final int minLevel = Integer.parseInt(aTok.nextToken());
			String mString = aTok.nextToken();
			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString = mString.substring(0, mString.lastIndexOf(".A"));
			}
			int maxLevel = minLevel;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aString.startsWith("CLASS="))
			{
				final PCClass aClass = aPC.getClassKeyed(aString.substring(6));
				for (int i = 0; i < mString.length(); i++)
				{
					if (mString.length() > 7 + i && mString.substring(i, i + 8).equals("MAXLEVEL"))
					{
						int j = -1;
						final int aLevel = aClass.getLevel().intValue() - 1;
						String bString = "0";
						if (aLevel >= 0) // some classes, like "Domain" are level 0, so this index would be -1
							bString = aClass.getCastList().get(aLevel).toString();
						if (!bString.equals("0"))
						{
							final StringTokenizer bTok = new StringTokenizer(bString, ",", false);
							j = bTok.countTokens() - 1;
						}
						bString = "";
						if (mString.length() > i + 8)
							bString = mString.substring(i + 8);
						mString = mString.substring(0, i) + new Integer(j).toString() + bString;
						i--; // back up one since we just did a replacement
					}
				}
				maxLevel = aPC.getVariableValue(mString, "").intValue();
				if (aClass != null)
				{
					final String prefix = aClass.getName() + " ";
					for (int i = minLevel; i <= maxLevel; i++)
					{
						final String bString = prefix + i;
						if (!availList.contains(bString))
							availList.add(bString);
						if (i == maxLevel && endIsUnique)
							uniqueList.add(bString);
					}
				}
			}
			if (aString.startsWith("TYPE="))
			{
				aString = aString.substring(5);
				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass)e.next();
					if (aClass.getSpellType().equals(aString))
					{
						if (mString.startsWith("MAXLEVEL"))
						{
							int aLevel = aClass.getLevel().intValue() - 1;
							for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
							{
								final PCClass bClass = (PCClass)e1.next();
								aLevel += bClass.getBonusTo("PCLEVEL", aClass.getName(), 0);
							}

							String bString = "0";
							if (aLevel >= 0) // some classes, like "Domain" are level 0, so this index would be -1
								bString = aClass.getCastList().get(aLevel).toString();
							if (bString.equals("0"))
								maxLevel = -1;
							else
							{
								final StringTokenizer bTok = new StringTokenizer(bString, ",", false);
								maxLevel = bTok.countTokens() - 1;
							}
							if (mString.length() > 8)
							{
								mString = mString.substring(8);
								maxLevel += Delta.decode(mString).intValue();
							}
						}
						final String prefix = aClass.getName() + " ";
						for (int i = minLevel; i <= maxLevel; i++)
						{
							final String bString = prefix + i;
							if (!availList.contains(bString))
								availList.add(bString);
							if (i == maxLevel && endIsUnique)
								uniqueList.add(bString);
						}
					}
				}
			}
		}
	}

	public String makeBonusString(String bonusString, String chooseString)
	{
		// assumption is that the chooseString is in the form class/type[space]level
		// if bonusString has "TYPE=%" or "CLASS=%" replace that with name from chooseString
		// if bonusString has "LEVEL=%" replace that with level part of chooseString
		int i = chooseString.lastIndexOf(" ");
		String classString = "";
		String levelString = "";
		if (bonusString.startsWith("BONUS:"))
			bonusString = bonusString.substring(6);
		boolean lockIt = bonusString.endsWith(".LOCK");
		if (lockIt)
			bonusString = bonusString.substring(0, bonusString.lastIndexOf(".LOCK"));

		if (i > -1)
		{
			classString = chooseString.substring(0, i);
			if (i < chooseString.length())
				levelString = chooseString.substring(i + 1);
		}
		while (bonusString.lastIndexOf("TYPE=%") > -1)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString + bonusString.substring(i + 6);
		}
		while (bonusString.lastIndexOf("CLASS=%") > -1)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString + bonusString.substring(i + 7);
		}
		while (bonusString.lastIndexOf("LEVEL=%") > -1)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString + bonusString.substring(i + 7);
		}
		if (lockIt)
		{
			i = bonusString.lastIndexOf("|");
			final Float val = Globals.getCurrentPC().getVariableValue(bonusString.substring(i + 1), "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}
		return bonusString;
	}

	public void removeBonus(String bonusString, String chooseString, PObject theObj)
	{
		bonusString = makeBonusString(bonusString, chooseString);
		int index = -1;
		if (bonusList!=null)
			bonusList.indexOf(bonusString);
		if (index >= 0)
		{
			bonusList.remove(index);
		}
		else
		{
			Globals.debugPrint("removeBonus: Could not find bonus: " + bonusString + " in bonusList.");
		}
		removeSave("BONUS|" + bonusString);
	}

	public void removeBonus(String bonusString, String chooseString)
	{
		removeBonus(bonusString, chooseString, this);
	}

	public void applyBonus(String bonusString, String chooseString, PObject theObj)
	{
		bonusString = makeBonusString(bonusString, chooseString);
		addBonusList(bonusString);
		addSave("BONUS|" + bonusString);
	}

	public void applyBonus(String bonusString, String chooseString)
	{
		applyBonus(bonusString, chooseString, this);
	}

	public String getQualifyString()
	{
		return qualifyString;
	}

	public void setQualifyString(String aString)
	{
		qualifyString = aString;
	}

	//PreReqs for all objects
	public boolean passesPreReqTests()
	{
		if (getPreReqCount()==0)
			return true;
		return passesPreReqTestsForList(preReqList);
	}

	public boolean passesPreReqTests(PObject p)
	{
		if (getPreReqCount()==0)
			return true;
		return passesPreReqTestsForList(p, preReqList);
	}

	//PreReqs for specified list
	public boolean passesPreReqTestsForList(ArrayList anArrayList)
	{
		//
		// Nothing to add, so allow it to be added always
		//
		if (anArrayList.size() == 0)
			return true;

		final PlayerCharacter aPC = Globals.getCurrentPC();

		if (aPC == null)
			return false;
		if (aPC.getClassList().size() == 0)
		{
			final PCClass aClass = Globals.getClassNamed(name);
			if (aClass != null && aClass == this && aClass.multiPreReqs())
			{
				return true;
			}
		}
		return passesPreReqTestsForList(aPC, null, anArrayList);
//		return passesPreReqTestsForList(null, anArrayList);
	}

	public boolean passesPreReqTestsForList(PObject aObj, ArrayList anArrayList)
	{
		return passesPreReqTestsForList(Globals.getCurrentPC(), aObj, anArrayList);
	}

	/**
	 * misc prereq tags:
	 * <ul>
	 *   <li> RESTRICT
	 * </ul>
	 *
	 * possible PRExxx tags:
	 * <ul>
	 *   <li> PREALIGN
	 *
	 *   <li> PRECLASS
	 *   <li> PRECLASSLEVELMAX
	 *   <li> PRELEVEL
	 *   <li> PRELEVELMAX
	 *   <li> PREHD
	 *
	 *   <li> PREDEITY
	 *   <li> PREDEITYALIGN
	 *   <li> PREDEITYDOMAIN
	 *   <li> PREDOMAIN
	 *
	 *   <li> PREFORT
	 *   <li> PREFORTBASE
	 *   <li> PREREFLEX
	 *   <li> PREREFLEXBASE
	 *   <li> PREWILL
	 *   <li> PREWILLBASE
	 *
	 *   <li> PREHANDSEQ
	 *   <li> PREHANDSGT
	 *   <li> PREHANDSGTEQ
	 *   <li> PREHANDSLT
	 *   <li> PREHANDSLTEQ
	 *   <li> PREHANDSNEQ
	 *
	 *   <li> PRESIZEEQ
	 *   <li> PRESIZEGT
	 *   <li> PRESIZEGTEQ
	 *   <li> PRESIZELT
	 *   <li> PRESIZELTEQ
	 *   <li> PRESIZENEQ
	 *
	 *   <li> PRESKILL
	 *   <li> PRESKILLMULT
	 *   <li> PRESKILLTOT
	 *
	 *   <li> PRESPELL
	 *   <li> PRESPELLCAST
	 *   <li> PRESPELLTYPE
	 *   <li> PRESPELLSCHOOL
	 *   <li> PRESPELLSCHOOLSUB
	 *
	 *   <li> PREATT
	 *   <li> PREUATT
	 *
	 *   <li> PREGENDER
	 *   <li> PREFEAT
	 *   <li> PREITEM
	 *   <li> PRELANG
	 *   <li> PREMOVE
	 *   <li> PRERACE
	 *   <li> PRETEMPLATE
	 *   <li> PRESTAT
	 *   <li> PRESA
	 *   <li> PRETYPE
	 *   <li> PREVAR
	 *   <li> PREWEAPONPROF
	 *
	 *   <li> PREFORCEPTS
	 *   <li> PREDSIDEPTS
	 *
	 *   <li> PREREPUTATION
	 *   <li> PREREPUTATIONLTEQ
	 * </ul>
	 */
	public boolean passesPreReqTestsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList)
	{
		boolean qValue = false; // Qualify overide testing.
		boolean qualifyValue = false; // Qualify overide testing.

		if (anArrayList.isEmpty())
			return true;

		boolean flag = false;
		boolean invertFlag = false; // Invert return value for !PRExxx tags

		ArrayList aFeatList = new ArrayList();
		if (aPC != null)
		{
			aFeatList = (ArrayList)aPC.aggregateFeatList();
		}

		Globals.debugPrint("PreReq:" + name);
		String aType = "";
		String aList = "";
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			flag = false;
			invertFlag = false;
			final String preString = (String)e.next();
			final StringTokenizer aaTok = new StringTokenizer(preString, ":", false);
			aType = aaTok.nextToken();
			if (aaTok.hasMoreTokens())
				aList = aaTok.nextToken();
			else
				aList = "";

//check for inverse prereqs.  They start with a "!"
			if (aType.startsWith("!"))
			{
				invertFlag = true;
				aType = aType.substring(1);
			}

// This adds 'Q' onto the PRExxx syntax. Which overrides QUALIFY.
// Why do this? It allows some prereqs to be over-ridden but not all.
// Which is needed for things like regional feats.

			if (aList.equals("Q"))
			{
				qValue = true;
				Globals.debugPrint("aList: " + aList);
			}
			if (aList.equals("Q") && aaTok.hasMoreTokens())
			{
				aList = aaTok.nextToken();
			}


			if (aPC != null)
			{
				if (aPC.checkQualifyList(this.getName()) && qValue == false)
				{
					Globals.debugPrint("In Qualify list:" + this + " -> Qualify list:" + aPC.getQualifyList());
					qualifyValue = true;
				}
				else
				{
					Globals.debugPrint("Not In Qualify list:" + this);
				}
			}

// e.g. PRETYPE:Thrown|Melee,Ranged
			if (aType.equals("PRETYPE"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",|", true);
				int iLogicType = 0;	// AND
				flag = true;
				if (aObj != null)
				{
					while (aTok.hasMoreTokens())
					{
						String aString = aTok.nextToken();
						if (aString.equals(","))
						{
							iLogicType = 0;
						}
						else if (aString.equals("|"))
						{
							iLogicType = 1;
						}
						else
						{
							boolean bIsType;
							boolean bInvert = false;
							if (aString.startsWith("[") && aString.endsWith("]"))
							{
								aString = aString.substring(1, aString.length() - 1);
								bInvert = true;
							}


							if (aObj instanceof Equipment)
							{
								bIsType = ((Equipment)aObj).isPreType(aString);
							}
							else
							{
								bIsType = aObj.isType(aString);
							}
							if (bInvert)
							{
								bIsType = !bIsType;
							}

							if (iLogicType == 0)
							{
								flag &= bIsType;
							}
							else
							{
								flag |= bIsType;
							}
						}
					}
				}
			}

			if (aPC != null)
			{
				int number = 0;
				if (aType.equals("PREFEAT"))
				{
					StringTokenizer aTok = new StringTokenizer(aList, "|");
					aList = aTok.nextToken();
					boolean countMults = false;
					if (aTok.hasMoreTokens())
						countMults = aTok.nextToken().equals("CHECKMULT");
					aTok = new StringTokenizer(aList, ",");
					// the number of feats which must match
					try
					{
						number = Integer.parseInt(aTok.nextToken());
					}
					catch (NumberFormatException exceptn)
					{
						System.out.println("Exception in PREFEAT:" + aList + "\nAssuming 1 required");
						number = 1;
						aTok = new StringTokenizer(aList, ",");
					}
					while (aTok.hasMoreTokens() && number > 0)
					{
						String aString = aTok.nextToken();
						StringTokenizer bTok = new StringTokenizer(aString, "(", false);
						String pString = bTok.nextToken();
						int i = -1;
						if (pString.length() != aString.length())
							i = pString.length(); // begin of subchoices
						String featName = null;
						String subName = null;
						int j = -1;
						boolean isType = aString.startsWith("TYPE=");
						if (i > -1)
						{
							featName = aString.substring(0, i).trim();
							subName = aString.substring(i + 1, aString.length() - 1);
							j = subName.lastIndexOf('%');
							if (j > -1)
								subName = subName.substring(0, j);
						}
						else
							featName = aString;
						boolean foundIt = false;
						if (!aFeatList.isEmpty())
						{
							for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
							{
								if (foundIt && isType == false || number <= 0)
									break;
								Feat aFeat = (Feat)e1.next();
								if ((isType == false && (aFeat.getName().equals(featName) || aFeat.getName().equals(aString))) ||
									(isType && aFeat.isType(featName.substring(5))))
								{
									if (subName != null && (aFeat.getName().equals(aString) || aFeat.containsAssociated(subName)) ||
										subName == null)
									{
										number--;
										if (aFeat.isMultiples() && countMults)
											number -= (aFeat.getAssociatedCount() - 1);
										foundIt = true;
									}
									else if (subName != null && j > -1) // search for match
									{
										for (int k=0; k<aFeat.getAssociatedCount(); k++)
										{
											String fString = aFeat.getAssociated(k);
											if (fString.startsWith(subName.substring(0, j)))
											{
												number--;
												foundIt = true;
												if (!countMults)
													break;
											}
										}
									}
								}
							}
						}
					}
					flag = (number <= 0);
				}
				else if (aType.equals("PRESKILL") || aType.equals("PRESKILLMULT"))
				// PRESKILLMULT will eventually work as follows:
				/*
				 * PRESKILLMULT:num,skill_list=rank
				 *
				 * This tag will set a flag on the character. It only works as a
				 * prereq if the rank in the skill divided by the rank needed is
				 * equal to the flag.
				 *
				 * The flag starts at 1, but goes up by one everytime the prereq
				 * is met.
				 *
				 * So the first feat would require 'rank' in the skill, the
				 * second feat 'rank*2', and so on...
				 *
				 * That would support the way regional feats are described in
				 * FR.
				 *
				 * However for now this is merely a dummy tag so we don't have to
				 * redesign the lst files when this tag gets working.
				 * --- arcady 11/5/2001
				 */
				// e.g. PRESKILL:1,Hide,Move Silent=3
				{
					int i = aList.lastIndexOf('=');
					int ranks = Integer.parseInt(aList.substring(i + 1));
					final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
					// the number of feats which must match
					number = Integer.parseInt(aTok.nextToken());
					ArrayList sList = (ArrayList)aPC.getSkillList().clone();
					ArrayList tList = new ArrayList();
					while (aTok.hasMoreTokens() && number > 0)
					{
						String aString = aTok.nextToken();
						StringTokenizer bTok = new StringTokenizer(aString, "(", false);
						String pString = bTok.nextToken();
						i = -1;
						if (pString.length() != aString.length())
							i = pString.length();
						String skillName = null;
						String subName = null;
						boolean isType = aString.startsWith("TYPE.");
						int j = -1;
						if (i > -1)
						{
							j = -1;
							skillName = aString.substring(0, i);
							subName = aString.substring(i + 1, aString.length() - 1);
							j = subName.lastIndexOf('%');
							if (j > -1)
							{
								subName = subName.substring(0, j);
							}
						}
						else
						{
							skillName = aString;
							j = aString.lastIndexOf('%');
						}
						boolean foundIt = false;
						Skill aSkill = null;
						for (Iterator e1 = sList.iterator(); e1.hasNext();)
						{
							if (foundIt && isType == false || number <= 0)
								break;
							aSkill = (Skill)e1.next();
							String aSkillName = aSkill.getName();
							if (isType == false && (aSkillName.equals(skillName) || aSkillName.equals(aString) ||
								(j >= 0 && aSkillName.startsWith(aString.substring(0, j)))))
							{
								if (aSkill.getRank().intValue() < ranks || tList.contains(aSkillName))
								{
									aSkill = null;
									continue;
								}
								if (j > -1 && aSkillName.startsWith(aString.substring(0, j)))
									break;
								if (j == -1 && aSkillName.equals(aString))
									break;
								aSkill = null;
							}
							else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) != -1)))
							{
								if (aSkill.getRank().intValue() < ranks || tList.contains(aSkillName))
								{
									aSkill = null;
									continue;
								}
								if (j > -1 && aSkill.getType().startsWith(aString.substring(5, j)))
									break;
								if (j == -1 && (aSkill.getType().indexOf(aString.substring(5)) != -1))
									break;
								aSkill = null;
							}
							aSkill = null;
						}
						if (aSkill != null && j > -1)
						{
							sList.remove(aSkill);
						}
						flag = (aSkill != null);
						if (flag)
						{
							if (aSkill != null)
								tList.add(aSkill.getName());
							number--;
							foundIt = true;
						}
					}
					flag = (number == 0);
				}
				else if (aType.equals("PRESKILLTOT"))
				// e.g. PRESKILLTOT:Hide,Move Silent=3
				{
					int i = aList.lastIndexOf('=');
					int ranks = Integer.parseInt(aList.substring(i + 1));
					final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
					// the number of feats which must match
					// number = Integer.parseInt(aTok.nextToken());
					ArrayList sList = (ArrayList)aPC.getSkillList().clone();
					ArrayList tList = new ArrayList();
					while (aTok.hasMoreTokens() && ranks > 0)
					{
						String aString = aTok.nextToken();
						StringTokenizer bTok = new StringTokenizer(aString, "(", false);
						String pString = bTok.nextToken();
						i = -1;
						if (pString.length() != aString.length())
							i = pString.length();
						String skillName = null;
						String subName = null;
						boolean isType = aString.startsWith("TYPE.");
						int j = -1;
						if (i > -1)
						{
							j = -1;
							skillName = aString.substring(0, i);
							subName = aString.substring(i + 1, aString.length() - 1);
							j = subName.lastIndexOf('%');
							if (j > -1)
							{
								subName = subName.substring(0, j);
							}
						}
						else
						{
							skillName = aString;
							j = aString.lastIndexOf('%');
						}
						boolean foundIt = false;
						Skill aSkill = null;
						for (int si = 0; si < sList.size(); si++)
						{
							if ((foundIt && isType == false && j == -1) || ranks <= 0)
								break;
							aSkill = (Skill)sList.get(si);
							if (isType == false && (aSkill.getName().equals(skillName) || aSkill.getName().equals(aString) ||
								(j >= 0 && aSkill.getName().startsWith(aString.substring(0, j)))))
							{
								if (tList.contains(aSkill.getName()))
								{
									aSkill = null;
									continue;
								}
								if (j > -1 && aSkill.getName().startsWith(aString.substring(0, j)))
									foundIt = true;
								if (j == -1 && aSkill.getName().equals(aString))
									foundIt = true;
								if (!foundIt)
									aSkill = null;
							}
							else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) != -1)))
							{
								foundIt = false;
								if (tList.contains(aSkill.getName()))
								{
									aSkill = null;
									continue;
								}
								if (j > -1 && aSkill.getType().startsWith(aString.substring(5, j)))
									foundIt = true;
								if (j == -1 && (aSkill.getType().indexOf(aString.substring(5)) != -1))
									foundIt = true;
								if (!foundIt)
									aSkill = null;
							}
							if (aSkill != null && j > -1)
							{
								sList.remove(aSkill);
								si--; // to adjust for incrementer
							}
							flag = (aSkill != null);
							if (flag)
							{
								if (aSkill != null) //Only here to shut jlint up
								{
									tList.add(aSkill.getName());
									ranks -= aSkill.getRank().intValue();
								}
							}
						}
					}
					flag = (ranks <= 0);
				}
				else if (aType.equals("PRECLASS"))
				{
					int i = aList.lastIndexOf('=');
					final int preClass = Integer.parseInt(aList.substring(i + 1));
					final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
					while (aTok.hasMoreTokens())
					{
						String aString = aTok.nextToken();
						if (aString.equalsIgnoreCase("Spellcaster"))
						{
							flag = aPC.isSpellCaster(preClass);
						}
						else
						{
							PCClass aClass = aPC.getClassNamed(aString);
							flag = (aClass != null && aClass.getLevel().intValue() >= preClass);
						}
						if (flag)
						{
							break;
						}
					}
				}
				// If any class is over in level this should return false.
				else if (aType.equals("PRECLASSLEVELMAX"))
				{
					int i = aList.lastIndexOf('=');
					boolean oneOver = false;
					final int preClass = Integer.parseInt(aList.substring(i + 1));
					final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
					while (aTok.hasMoreTokens())
					{
						String aString = aTok.nextToken();
						if (aString.equals("Spellcaster"))
						{
							oneOver = aPC.isSpellCastermax(preClass);
						}
						else
						{
							PCClass aClass = aPC.getClassNamed(aString);
							if (aClass != null && aClass.getLevel().intValue() <= preClass)
							{
								oneOver = true;
							}
						}
					}
					flag = oneOver;
				}
				else if (aType.equals("PRELEVEL"))
				{
					final int preLevel = Integer.parseInt(aList);
					flag = (aPC.totalLevels() >= preLevel);
				}
				else if (aType.equals("PRELEVELMAX"))
				{
					final int preLevelmax = Integer.parseInt(aList);
					flag = (aPC.totalLevels() <= preLevelmax);
				}
				else if (aType.equals("PREHD"))
				{
					/*
					 * either PREHD:xxx+ or PREHD:xxx-yyy
					 * with xxx being the minimum requirement
					 * and yyy being the maximum requirement
					 *
					 * author: Thomas Behr 13-03-02
					 */
					StringTokenizer aTok = new StringTokenizer(aList, "+-");
					final int preHDMin = (aTok.hasMoreTokens())
						? Integer.parseInt(aTok.nextToken())
						: Integer.MIN_VALUE;
					final int preHDMax = (aTok.hasMoreTokens())
						? Integer.parseInt(aTok.nextToken())
						: Integer.MAX_VALUE;
					;
					final int hitDice = aPC.totalHitDice();
					flag = (hitDice >= preHDMin) && (hitDice <= preHDMax);
				}
				else if (aType.equals("PREFORCEPTS"))
				{
					final int preFPoints = Integer.parseInt(aList);
					final int myfhold = Integer.parseInt(aPC.getFPoints());
					flag = (myfhold >= preFPoints);
				}
				else if (aType.equals("PREDSIDEPTS"))
				{
					final int preDPoints = Integer.parseInt(aList);
					final int mydhold = Integer.parseInt(aPC.getDPoints());
					flag = (mydhold >= preDPoints);
				}
				else if (aType.equals("RESTRICT"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					while (aTok.hasMoreTokens() && flag == false)
					{
						final String aString = aTok.nextToken();
						final PCClass aClass = aPC.getClassNamed(aString);
						flag = (aClass == null);
					}
				}
				else if (aType.equals("PRERACE"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
					while (aTok.hasMoreTokens())
					{
						final String aString = aTok.nextToken();
						final int min = Math.min(aString.length(), aPC.getRace().getName().length());
						final String tString = aString.substring(0, min);
						final String rString = aPC.getRace().getName().substring(0, min);
						if (aString.startsWith("[") && aString.endsWith("]"))
						{
							flag = !rString.equalsIgnoreCase(aString.substring(1, min - 1));
						}
						else
							flag = rString.equalsIgnoreCase(tString);
						if (!aString.startsWith("[") && flag)
							break;
						if (aString.startsWith("[") && flag == false)
							break;
					}
				}
				else if (aType.equals("PRETEMPLATE"))
				{
					if (!aPC.getTemplateList().isEmpty())
					{
						final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
						while (aTok.hasMoreTokens())
						{
							String templateName = aTok.nextToken();
							final int wildCard = templateName.indexOf("%");
							if (wildCard > -1)  //handle wildcards (always assume they end the line)
							{
								templateName = templateName.substring(0, wildCard);
								for (Iterator templates = aPC.getTemplateList().iterator(); templates.hasNext();)
								{
									final PCTemplate aTemplate = (PCTemplate)templates.next();
									if (aTemplate.getName().startsWith(templateName))
									{
										flag = true;
										break;
									}
								}
							}
							else
							{
								flag = (aPC.getTemplateNamed(templateName) != null);
							}
							if (flag)
							{
								break;
							}
						}
					}
				}
				else if (aType.equals("PREATT"))
				{
					final int anInt = Integer.parseInt(aList);
					int att = aPC.getBonus(0, true);
					att -= aPC.modForSize();
					flag = att >= anInt;
				}
				else if (aType.equals("PREREPUTATION"))
				{
					final int anInt = Integer.parseInt(aList);
					String rep = String.valueOf(aPC.reputation());
					int repf = Integer.parseInt(rep);
					flag = repf >= anInt;
				}
				else if (aType.equals("PREREPUTATIONLTEQ"))
				{
					final int anInt = Integer.parseInt(aList);
					final String rep = String.valueOf(aPC.reputation());
					final int repf = Integer.parseInt(rep);
					flag = repf <= anInt;
				}
				else if (aType.equals("PREUATT"))
				{
					final int requiredValue = Integer.parseInt(aList);
					int att = 0;
					if (!aPC.getClassList().isEmpty())
					{
						for (Iterator e2 = aPC.getClassList().iterator(); e2.hasNext();)
						{
							final PCClass aClass = (PCClass)e2.next();
							String s = aClass.getUattForLevel(aClass.getLevel().intValue());
							if (s.length() == 0 || s.equals("0"))
								att = Math.max(att, aClass.baseAttackBonus());
							else
							{
								final StringTokenizer bTok = new StringTokenizer(s, ",", false);
								s = bTok.nextToken();
								att = Math.max(att, Integer.parseInt(s));
							}
						}
					}
					flag = att >= requiredValue;
				}
				else if (aType.equals("PRESTAT"))
				{
					int i = Globals.getStatFromAbbrev(aList.substring(0,3));
					if (i >= 0)
					{
						flag = aPC.adjStats(i) >=
							Integer.parseInt(aList.substring(aList.lastIndexOf('=') + 1));
					}
					else
					{
						System.out.println("The stat " + aList + " is unknown.");
					}
				}
				else if (aType.equals("PRESPELLTYPE"))
				// e.g. PRESPELLTYPE=Arcane,1,1
				// e.g. PRESPELLTYPE=Arcane|Divine,1,1 means either Arcane *or* Divine
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					final String aString = aTok.nextToken();
					number = Integer.parseInt(aTok.nextToken());
					final int minlevel = Integer.parseInt(aTok.nextToken());
					final StringTokenizer bTok = new StringTokenizer(aString, "|", false);
					flag = false;
					boolean tempFlag = false;
					while (bTok.hasMoreTokens() && tempFlag == false)
					{
						ArrayList aArrayList = aPC.aggregateSpellList(bTok.nextToken(), "", "", minlevel, 20);
						tempFlag = (aArrayList.size() >= number);
						if (tempFlag)
						{
							flag = tempFlag;
						}
					}
				}
				else if (aType.equals("PRESPELLSCHOOL"))
				{
					// e.g. PRESPELLSCHOOL:Divination,7,1
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					String aString = aTok.nextToken();
					number = Integer.parseInt(aTok.nextToken());
					int minlevel = Integer.parseInt(aTok.nextToken());
					ArrayList aArrayList = aPC.aggregateSpellList("Any", aString, "A", minlevel, 20);
					Globals.debugPrint("Spells=" + aArrayList.size());
					flag = (aArrayList.size() >= number);
				}
				else if (aType.equals("PRESPELLSCHOOLSUB"))
				{
					// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					String aString = aTok.nextToken();
					number = Integer.parseInt(aTok.nextToken());
					int minlevel = Integer.parseInt(aTok.nextToken());
					ArrayList aArrayList = aPC.aggregateSpellList("Any", "A", aString, minlevel, 20);
					flag = (aArrayList.size() >= number);
				}
				else if (aType.equals("PRESPELL"))
				{
					// e.g. PRESPELL:3,
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					number = Integer.parseInt(aTok.nextToken());
					ArrayList aArrayList = aPC.aggregateSpellList("Any", "", "", 0, 20);
					while (aTok.hasMoreTokens())
					{
						String bString = aTok.nextToken();
						if (!aArrayList.isEmpty())
						{
							for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
							{
								final Spell aSpell = (Spell)e1.next();
								if (aSpell.getName().equals(bString))
								{
									number--;
									break;
								}
							}
						}
						if (number == 0)
							break;
					}
					flag = (number <= 0);
				}
				else if (aType.equals("PRESPELLCAST"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					ArrayList classList = (ArrayList)aPC.getClassList().clone();
					PCClass aClass = null;
					while (aTok.hasMoreTokens())
					{
						String aString = aTok.nextToken();
						if (aString.startsWith("MEMORIZE"))
						{
							if (!classList.isEmpty())
							{
								for (Iterator e1 = classList.iterator(); e1.hasNext();)
								{
									aClass = (PCClass)e1.next();
									if ((aClass.getMemorizeSpells() && aString.endsWith("N")) ||
										(!aClass.getMemorizeSpells() && aString.endsWith("Y")))
									{
										e1.remove();
									}
								}
							}
						}
						else if (aString.startsWith("TYPE"))
						{
							if (!classList.isEmpty())
							{
								for (Iterator e1 = classList.iterator(); e1.hasNext();)
								{
									aClass = (PCClass)e1.next();
									if (aString.substring(5).lastIndexOf(aClass.getSpellType()) == -1)
									{
										e1.remove();
									}
								}
							}
						}
						flag = classList.size() > 0;
						if (!flag)
							break;
					}
				}
				else if (aType.equals("PRESA"))
				{
					StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					// wrap this in a try catch to make sure
					// that the first Token is a number
					try
					{
						number = Integer.parseInt(aTok.nextToken());
					}
					catch (NumberFormatException exceptn)
					{
						System.out.println("Exception in PRESA:" + aList + "\nAssuming 1 required");
						number = 1;
						aTok = new StringTokenizer(aList, ",");
					}
					Globals.debugPrint("number of PreSA to Match: " + number);
					while (aTok.hasMoreTokens())
					{
						final String aString = aTok.nextToken();
						boolean bFound = false;
						if (!aPC.getSpecialAbilityList().isEmpty())
						{
							for (Iterator e1 = aPC.getSpecialAbilityList().iterator(); e1.hasNext();)
							{
								final String e1String = (String)e1.next();
								if (e1String.startsWith(aString))
								{
									number--;
									bFound = true;
									break;
								}
							}
						}
						//
						// Now check any templates
						//
						if (!bFound)
						{
							if (!aPC.getTemplateList().isEmpty())
							{
								for (Iterator e1 = aPC.getTemplateList().iterator(); e1.hasNext();)
								{
									final PCTemplate aTempl = (PCTemplate)e1.next();
									final ArrayList SAs = (ArrayList)aTempl.getSAs(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize());
									for (Iterator e2 = SAs.iterator(); e2.hasNext();)
									{
										final String e1String = (String)e2.next();
										if (e1String.startsWith(aString))
										{
											number--;
											break;
										}
									}
								}
							}
						}
						if (number == 0)
							break;
					}
					flag = (number <= 0);
				}
				else if (aType.equals("PRELANG"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					final int storedValue = Integer.parseInt(aTok.nextToken());
					number = storedValue;
					while (aTok.hasMoreTokens() && number > 0)
					{
						String aString = aTok.nextToken();
						if (aPC.getLanguagesList().contains(aString))
							number--;
					}
					if (aList.lastIndexOf("ANY") > -1)
						flag = storedValue <= aPC.getLanguagesList().size();
					else
						flag = (number == 0);
				}
				else if (aType.equals("PREWEAPONPROF"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					flag = false;
					while (aTok.hasMoreTokens())
					{
						String aString = aTok.nextToken();
						final boolean hasIt = !aString.startsWith("[");
						if (!hasIt)
						{
							aString = aString.substring(1, Math.max(aString.length() - 1, aString.lastIndexOf("]")));
						}
						if (aString.equals("DEITYWEAPON") && aPC.getDeity() != null)
						{
							for (Iterator weapIter = Utility.split(aPC.getDeity().getFavoredWeapon(), '|').iterator(); !flag && weapIter.hasNext();)
							{
								flag = aPC.hasWeaponProfNamed((String)weapIter.next());
							}
						}
						else
						{
							flag = aPC.hasWeaponProfNamed(aString);
						}
						if (!hasIt)
						{
							flag = !flag;
						}
					}
				}
				else if (aType.equals("PREITEM"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					flag = false;
					number = Integer.parseInt(aTok.nextToken());
					while (aTok.hasMoreTokens())
					{
						if (aPC.getEquipmentList().isEmpty())
							break;
						String aString = aTok.nextToken();
						for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
						{
							Equipment eq = (Equipment)e1.next();
							if (aString.startsWith("TYPE="))
							{
								if (eq.getType().indexOf(aString.substring(5).toUpperCase()) > -1)
								{
									number--;
									break;
								}
							}
							else
							{  //not a TYPE string
								if (aString.indexOf("%") > -1)  //handle wildcards (always assume they end the line)
								{
									if (eq.getName().startsWith(aString.substring(0, aString.indexOf("%"))))
									{
										number--;
										break;
									}
								}
								else if (eq.getName().equals(aString))  //just a straight String compare
								{
									number--;
									break;
								}
							}
						}
						if (number == 0)
							break;
					}
					flag = (number <= 0);
				}
				else if (aType.startsWith("PREVAR"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					flag = true;
					int i = 0;
					if (aType.endsWith("GT"))
					{
						i = 0;
					}
					else if (aType.endsWith("GTEQ"))
					{
						i = 1;
					}
					else if (aType.endsWith("LT"))
					{
						i = 2;
					}
					else if (aType.endsWith("LTEQ"))
					{
						i = 3;
					}
					else if (aType.endsWith("NEQ"))
					{
						i = 4;
					}
					else if (aType.endsWith("EQ"))
					{
						i = 5;
					}
					while (aTok.hasMoreTokens() && flag)
					{
						final String varName = aTok.nextToken();
						String valString = "0";
						if (aTok.hasMoreTokens())
							valString = aTok.nextToken();
						final float aFloat = aPC.getVariableValue(valString, "").floatValue();
						final float bFloat;
						if ((!Globals.isApplyLoadPenaltyToACandSkills()) && (varName.equals("ENCUMBERANCE")))
							bFloat = 0;
						else
							bFloat = aPC.getVariable(varName, true, true, "", "").floatValue();
						switch (i)
						{
							case 0:
								flag = (aFloat < bFloat);
								break;
							case 1:
								flag = (aFloat <= bFloat);
								break;
							case 2:
								flag = (aFloat > bFloat);
								break;
							case 3:
								flag = (aFloat >= bFloat);
								break;
							case 4:
								flag = (aFloat != bFloat);
								break;
							case 5:
								flag = (aFloat == bFloat);
								break;
							default:
								//What to do here?
						}
					}
				}
				else if (aType.equals("PREGENDER"))
				{
					flag = aPC.getGender().startsWith(aList);
				}
				else if (aType.equals("PREDEITY"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					flag = false;
					while (aTok.hasMoreTokens())
					{
						final String aString = aTok.nextToken();
						flag = ((aString.equals("Y") && aPC.getDeity() != null) ||
							(aString.equals("N") && aPC.getDeity() == null) ||
							(aPC.getDeity() != null && aPC.getDeity().getName().equals(aString)));
						if (flag)
							break;
					}
				}
				else if (aType.equals("PREDEITYDOMAIN")) //for Mynex
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					number = Integer.parseInt(aTok.nextToken());
					while (aTok.hasMoreTokens() && number > 0)
					{
						final String bString = aTok.nextToken();
						if (aPC.getDeity() != null)
						{
							if (aPC.getDeity().domainListString().indexOf(bString) > -1)
								number--;
						}
					}
					flag = (number <= 0);
				}
				else if (aType.equals("PREDEITYALIGN"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					flag = false;
					while (aTok.hasMoreTokens())
					{
						final String aString = Constants.s_ALIGNSHORT[Integer.parseInt(aTok.nextToken())];
						if (aPC.getDeity() != null)
						{
							flag = (aPC.getDeity().getDeityAlignment().equals(aString));
							if (flag)
								break;
						}
					}
				}
				else if (aType.equals("PREALIGN"))
				{
					flag = false;
					final int alignment = aPC.getAlignment();
					final String alString = String.valueOf(alignment);
					flag = (aList.lastIndexOf(alString) > -1);
					if (!flag && (aList.lastIndexOf("10") > -1) && (aPC.getDeity() != null))
					{
						flag = aPC.getDeity().allowsAlignment(alignment);
					}
				}
				else if (aType.equals("PREFORT"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int fort = aPC.getBonus(1, true) + aPC.calcStatMod(Constants.CONSTITUTION);
					flag = (fort >= nextInt);
				}
				else if (aType.equals("PREWILL"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int will = aPC.getBonus(3, true) + aPC.calcStatMod(Constants.WISDOM);
					flag = (will >= nextInt);
				}
				else if (aType.equals("PREREFLEX"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int ref = aPC.getBonus(2, true) + aPC.calcStatMod(Constants.DEXTERITY);
					flag = (ref >= nextInt);
				}
				else if (aType.equals("PREFORTBASE"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int base = aPC.getBonus(1, false);
					flag = (base >= nextInt);
				}
				else if (aType.equals("PREWILLBASE"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int base = aPC.getBonus(3, false);
					flag = (base >= nextInt);
				}
				else if (aType.equals("PREREFLEXBASE"))
				{
					final int nextInt = Integer.parseInt(aList);
					final int base = aPC.getBonus(2, false);
					flag = (base >= nextInt);
				}
				else if (aType.equals("PREDOMAIN"))
				{
					final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
					number = Integer.parseInt(aTok.nextToken());
					while (aTok.hasMoreTokens() && number > 0)
					{
						final String bString = aTok.nextToken();
						if (aPC.getCharacterDomainNamed(bString) != null)
							number--;
					}
					flag = (number <= 0);
				}
				else if (aType.equals("PRESIZEEQ"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize == sizeInt);
				}
				else if (aType.equals("PRESIZELT"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize < sizeInt);
				}
				else if (aType.equals("PRESIZELTEQ"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize <= sizeInt);
				}
				else if (aType.equals("PRESIZEGT"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize > sizeInt);
				}
				else if (aType.equals("PRESIZEGTEQ"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize >= sizeInt);
				}
				else if (aType.equals("PRESIZENEQ"))
				{
					final int sizeInt = aPC.sizeIntForSize(aList);
					final int pcSize = aPC.sizeInt();
					flag = (pcSize != sizeInt);
				}
				else if (aType.equals("PREMOVE"))
				{
					if (aPC == null || aPC.getRace() == null || aPC.getRace().getMovementTypes() == null)
					{
						flag = false;
					}
					else
					{
						final StringTokenizer movereqs = new StringTokenizer(aList, ",");
						while (movereqs.hasMoreTokens())
						{
							flag = false;
							final StringTokenizer movereq = new StringTokenizer(movereqs.nextToken(), "=");
							if (movereq.countTokens() < 2)
							{
								continue;
							}
							final String moveType = movereq.nextToken();
							final int moveAmount = Integer.parseInt(movereq.nextToken());
							//for (int x = 0; x < aPC.getRace().getMovementTypes().length; x++)
							for (int x = 0; x < aPC.getMovements().length; x++)
							{
								//if (moveType.equals(aPC.getRace().getMovementType(x)) && aPC.getRace().getMovement(x).intValue() >= moveAmount)
								if (moveType.equals(aPC.getMovementType(x)) && aPC.getMovement(x).intValue() >= moveAmount)
								{
									flag = true;
									break;
								}
							}
							if (!flag)
							{
								break;
							}
						}
					}
				}
				else if (aType.equals("PREHANDSEQ"))
				{
					flag = aPC.getRace().getHands() == Integer.parseInt(aList);
				}
				else if (aType.equals("PREHANDSLT"))
				{
					flag = aPC.getRace().getHands() < Integer.parseInt(aList);
				}
				else if (aType.equals("PREHANDSLTEQ"))
				{
					flag = aPC.getRace().getHands() <= Integer.parseInt(aList);
				}
				else if (aType.equals("PREHANDSGT"))
				{
					flag = aPC.getRace().getHands() > Integer.parseInt(aList);
				}
				else if (aType.equals("PREHANDSGTEQ"))
				{
					flag = aPC.getRace().getHands() >= Integer.parseInt(aList);
				}
				else if (aType.equals("PREHANDSNEQ"))
				{
					flag = aPC.getRace().getHands() != Integer.parseInt(aList);
				}
				else if (!aType.equals("PRETYPE"))
				{
					flag = true; // if a PRExxx tag isn't known, don't fail the test on account of it!
				}
			}

			Globals.debugPrint(aType + ":" + aList + "=" + flag + " qValue:" + qValue + " qualifyValue:" + qualifyValue);
			if (qValue)
			{
				qualifyValue = false;
			}
			qValue = false;

			if (invertFlag)
			{
				flag = !flag;
			}
			if (flag == false && qualifyValue == false)
			{
				return flag;
			}
		}
		if (qualifyValue)
		{
			flag = true;
		}
		return flag;
	}

	///Creates the requirement string for printing.
	public String preReqStrings()
	{
		if (getPreReqCount()==0)
			return "";
		return preReqStringsForList(preReqList);
	}

	public String preReqStringsForList(ArrayList anArrayList)
	{
		if (anArrayList.isEmpty())
			return "";
		StringBuffer pString = new StringBuffer(anArrayList.size() * 20);

		StringTokenizer aTok = null;
		String aType = null;
		String aList = null;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			aTok = new StringTokenizer(aString, ":", false);
			aType = aTok.nextToken();
			aList = aTok.nextToken();
			int i = 0;
			if (pString.length() > 0)
				pString.append("  ");
			if (aType.startsWith("!"))
			{
				pString.append("!");
				aType = aType.substring(1);
			}
			if (aType.equals("PRECLASS"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("CLASS:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString.append(",");
					pString.append(aTok.nextToken());
				}
			}
			else if (aType.equals("PREATT"))
			{
				pString.append("ATT=");
				pString.append(aList);
			}
			else if (aType.equals("PREUATT"))
			{
				pString.append("UATT=");
				pString.append(aList);
			}
			else if (aType.equals("PRESTAT"))
			{
				pString.append("STAT:").append(aType).append("=").append(aList);
			}
			else if (aType.equals("PREDEITYDOMAIN"))
			{
				pString.append("Deity Domain=");
				pString.append(aList);
			}
			else if (aType.equals("PREDEITYALIGN"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("Deity Alignment:");
				while (aTok.hasMoreTokens())
				{
					final int raceNumber = Integer.parseInt(aTok.nextToken());
					if ((raceNumber >= 0) && (raceNumber < Constants.s_ALIGNLONG.length))
					{
						if (i++ > 0)
							pString.append(",");
						pString.append(Constants.s_ALIGNSHORT[raceNumber]);
					}
					else
					{
						GuiFacade.showMessageDialog(null, "Invalid alignment: " + raceNumber, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			else if (aType.equals("PREALIGN"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("Alignment:");
				while (aTok.hasMoreTokens())
				{
					final int raceNumber = Integer.parseInt(aTok.nextToken());
					if ((raceNumber >= 0) && (raceNumber < Constants.s_ALIGNLONG.length))
					{
						if (i++ > 0)
							pString.append(",");
						pString.append(Constants.s_ALIGNSHORT[raceNumber]);
					}
					else
					{
						GuiFacade.showMessageDialog(null, "Invalid alignment: " + raceNumber, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			else
			{
				pString.append(aType.substring(3)).append(":");
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString.append(",");
					pString.append(aTok.nextToken());
				}
			}
		}
		return pString.toString();
	}

	public String preReqHTMLStrings()
	{
		if (getPreReqCount()==0)
			return "";
		return preReqHTMLStringsForList(preReqList);
	}

	public String preReqHTMLStrings(boolean includeHeader)
	{
		if (getPreReqCount()==0)
			return "";
		return preReqHTMLStringsForList(null, preReqList, includeHeader);
	}

	public String preReqHTMLStrings(PObject p)
	{
		if (getPreReqCount()==0)
			return "";
		return preReqHTMLStringsForList(p, preReqList);
	}


	public String preReqHTMLStringsForList(ArrayList anArrayList)
	{
		return preReqHTMLStringsForList(null, anArrayList);
	}

	public String preReqHTMLStringsForList(PObject aObj, ArrayList anArrayList)
	{
		return preReqHTMLStringsForList(aObj, anArrayList, true);
	}

	public String preReqHTMLStringsForList(PObject aObj, ArrayList anArrayList, boolean includeHeader)
	{
		if (anArrayList.isEmpty())
			return "";
		StringBuffer pString = new StringBuffer(anArrayList.size() * 20);
		if (includeHeader)
			pString.append("<html>");
		ArrayList newList = new ArrayList();
		int iter = 0;
		int fontColor = Globals.getPrereqFailColor();
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			newList.clear();
			newList.add(aString);
			if (iter++ > 0)
				pString.append(" ");
			String bString = preReqStringsForList(newList);

			boolean flag;
			if (aObj == null)
				flag = passesPreReqTestsForList(newList);
			else
				flag = passesPreReqTestsForList(aObj, newList);

			if (!flag)
			{
				if (fontColor != 0)
				{
					pString.append("<font color=\"#").append(Integer.toHexString(fontColor)).append("\">");
				}
				pString.append("<i>");
			}
			// seems that ALIGN and STAT have problems in HTML display, so wrapping in <font> tag.
			else
				pString.append("<font>");

			pString.append(bString);

			if (!flag)
				pString.append("</i>");
			if (flag || (fontColor != 0))
				pString.append("</font>");
		}
		if (includeHeader)
			pString.append("</html>");
		return pString.toString();
	}

	// matchType==0 means equals
	// matchType==1 means starts with
	// matchType==2 means ends with
	public boolean hasPreReqOf(int matchType, String matchString)
	{
		if (getPreReqCount()==0)
			return false;
		for (int i = 0; i<getPreReqCount(); i++)
		{
			String aString = getPreReq(i);
			if ((matchType == 0 && aString.equalsIgnoreCase(matchString)) || (matchType == 1 && aString.startsWith(matchString)) || (matchType == 2 && aString.endsWith(matchString)))
				return true;
		}
		return false;
	}

	public void setBonusStackFor(int bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
		}

		int index = -2;
		StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);
		// e.g. "COMBAT.AC.Dodge"
		if (aTok.countTokens() > 2)
		{
			String aString = aTok.nextToken(); // should be bonus category e.g. "COMBAT"
			aString = aTok.nextToken(); // should be bonus name e.g. "AC"
			aString = aTok.nextToken(); // should be bonus type e.g. whatever
			if (aString != null && !aString.equalsIgnoreCase("null"))
				index = Globals.getBonusStackList().indexOf(aString); // e.g. Dodge
		}
		if (index == -1) // meaning, a non-stacking bonus
		{
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey == null)
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(Math.max(bonus, Integer.parseInt(aKey))));
		}
		else // stacking bonuses
		{
			if (bonusType == null)
				bonusType = "";
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey == null)
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(bonus + Integer.parseInt(aKey)));
		}
	}

//	public ArrayList getSelectedWeaponProfBonus()
//	{
//		return selectedWeaponProfBonus;
//	}

//	public void setSelectedWeaponProfBonus(ArrayList aList)
//	{
//		selectedWeaponProfBonus = aList;
//	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	public boolean isNewItem()
	{
		return isNewItem;
	}

	public void setNewItem(boolean newItem)
	{
		this.isNewItem = newItem;
	}
}
