/*
 * EquipmentList.java
 * Copyright 2003 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on November 30, 2003, 15:24
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:59 $
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.utils.Utility;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Equipment-related lists and methods extracted from Globals.java.
 * Will probably try to disentangle modifierlist into it's own class later.
 *
 * @author Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class EquipmentList
{
	private static final int EQUIPMENTLISTSIZE = 4000;
	private static final int MODIFIERLISTSIZE = 230;
	/** this is determined by preferences */
	private static boolean bAutoGeneration = false;
	private static List equipmentList = new ArrayList(EQUIPMENTLISTSIZE);
	private static List modifierList = new ArrayList(MODIFIERLISTSIZE);

	/**
	 * Private to ensure utility object can't be instantiated.
	 */
	private EquipmentList()
	{
	}

	/**
	 * Add a piece of equipment to the equipment list.
	 *
	 * @param aEq the equipment to add
	 * @return true if adding succeeded
	 */
	public static boolean addEquipment(final Equipment aEq)
	{
		if (getEquipmentKeyed(aEq.getKeyName()) != null)
		{
			return false;
		}

		if (!aEq.isType(Constants.s_CUSTOM))
		{
			aEq.addMyType(Constants.s_CUSTOM);
		}

		//
		// Make sure all the equipment types are present in the sorted list
		//
		Equipment.getEquipmentTypes().addAll(aEq.typeList());
		getEquipmentList().add(aEq);

		if (!isbAutoGeneration())
		{
			Globals.sortPObjectList(getEquipmentList());
		}

		return true;
	}

	/**
	 * Appends name parts to the newName.
	 *
	 * @param nameList
	 * @param omitString
	 * @param newName
	 */
	private static void appendNameParts(List nameList, final String omitString, final StringBuffer newName)
	{
		for (Iterator e = nameList.iterator(); e.hasNext();)
		{

			final String namePart = (String) e.next();

			if ((omitString.length() != 0) && namePart.equals(omitString))
			{
				continue;
			}

			if (newName.length() > 2)
			{
				newName.append('/');
			}

			newName.append(namePart);
		}
	}

	/**
	 * Automatically add equipment types as requested by user.
	 */
	public static void autoGenerateEquipment()
	{
		setbAutoGeneration(true);

		autogenerateRacialEquipment();

		autogenerateMasterWorkEquipment();

		autogenerateMagicEquipment();

		autogenerateExoticMaterialsEquipment();

		setbAutoGeneration(false);

		Globals.sortPObjectList(getEquipmentList()); // Sort the equipment list
	}

	private static void autogenerateExoticMaterialsEquipment()
	{
		if (SettingsHandler.isAutogenExoticMaterial())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor()
					&& !eq.isShield() && !eq.isWeapon()))
				{
					continue;
				}

				final EquipmentModifier eqDarkwood = getQualifiedModifierNamed("Darkwood", eq);
				final EquipmentModifier eqAdamantine = getQualifiedModifierNamed("Adamantine", eq);
				final EquipmentModifier eqMithral = getQualifiedModifierNamed("Mithral", eq);

				createItem(eq, eqDarkwood);
				createItem(eq, eqAdamantine);
				createItem(eq, eqMithral);
			}
		}
	}

	private static void autogenerateMagicEquipment()
	{
		if (SettingsHandler.isAutogenMagic())
		{
			for (int iPlus = 1; iPlus <= 5; iPlus++)
			{

				final String aBonus = Delta.toString(iPlus);

				for (int i = getEquipmentList().size() - 1; i >= 0; --i)
				{
					Equipment eq = (Equipment) getEquipmentList().get(i);
					//
					// Only apply to non-magical
					// Armor, Shield and Weapon
					//
					if (eq.isMagic() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield()
						&& !eq.isWeapon()))
					{
						continue;
					}
					// Items must be masterwork before
					// you can assign magic to them
					EquipmentModifier eqMod = getQualifiedModifierNamed("Masterwork", eq);
					if (eqMod == null)
					{
						continue;
					}

					eq = (Equipment) eq.clone();
					eq.addEqModifier(eqMod, true);
					if (eq.isWeapon() && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false);
					}

					eqMod = getQualifiedModifierNamed(aBonus, eq);
					createItem(eq, eqMod);
				}
			}
		}
	}

	private static void autogenerateMasterWorkEquipment()
	{
		if (SettingsHandler.isAutogenMasterwork())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor()
					&& !eq.isShield() && !eq.isWeapon()))
				{
					continue;
				}
				final EquipmentModifier eqMasterwork = getQualifiedModifierNamed("Masterwork", eq);
				createItem(eq, eqMasterwork);
			}
		}
	}

	private static void autogenerateRacialEquipment()
	{
		if (SettingsHandler.isAutogenRacial())
		{

			//
			// Go through all loaded races and flag whether or not to make equipment sized for them
			//
			final int[] gensizes = new int[9];
			final List races = new ArrayList(Globals.getRaceMap().values());

			for (Iterator e = races.iterator(); e.hasNext();)
			{

				final Race race = (Race) e.next();
				final int iSize = Globals.sizeInt(race.getSize());
				final int flag = 1;

				gensizes[iSize] |= flag;
			}
			int x = -1;

			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

				//
				// Only apply to Armor, Shield and resizable items
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isArmor() && !eq.isShield()
					&& !eq.isType("RESIZABLE")))
				{
					continue;
				}

				for (int j = 0; j <= SystemCollections.getSizeAdjustmentListSize() - 1; ++j)
				{
					if (x == -1)
					{
						final SizeAdjustment s = SystemCollections.getSizeAdjustmentAtIndex(j);
						if (s.isDefaultSize())
						{
							x = j;
						}
					}
					if (j == x) // skip over default size
					{
						continue;
					}

					if ((gensizes[j] & 0x01) != 0)
					{
						createItem(eq, j);
					}
				}
			}
		}
	}

	/**
	 * Empty the equipment list.
	 */
	protected static void clearEquipmentList()
	{
		equipmentList = new ArrayList(EQUIPMENTLISTSIZE);
	}

	/**
	 * Empty the modifier list.
	 */
	protected static void clearModifierList()
	{
		modifierList = new ArrayList(MODIFIERLISTSIZE);
	}

	private static void createItem(final Equipment eq, final int iSize)
	{
		createItem(eq, null, iSize);
	}

	private static void createItem(final Equipment eq, final EquipmentModifier eqMod)
	{
		createItem(eq, eqMod, -1);
	}

	private static void createItem(Equipment eq, final EquipmentModifier eqMod, final int iSize)
	{
		if (eq == null)
		{
			return;
		}

		try
		{
			// Armor without an armor bonus is an exception
			//
			if (!eq.getModifiersAllowed() || (eq.isArmor() && (eq.getACMod().intValue() == 0) && ((eqMod != null)
				&& !eqMod.getName().equalsIgnoreCase("MASTERWORK"))))
			{
				return;
			}

			eq = (Equipment) eq.clone();

			if (eq == null)
			{
				Logging.errorPrint("could not clone item");
				return;
			}

			if (eqMod != null)
			{
				eq.addEqModifier(eqMod, true);

				if (eq.isWeapon() && eq.isDouble())
				{
					eq.addEqModifier(eqMod, false);
				}
			}

			if ((iSize >= 0) && (iSize <= SystemCollections.getSizeAdjustmentListSize() - 1))
			{
				eq.resizeItem(SystemCollections.getSizeAdjustmentAtIndex(iSize).getName());
			}
			//
			// Change the names, to protect the innocent
			//
			final String sName = eq.nameItemFromModifiers();
			final Equipment eqExists = getEquipmentKeyed(sName);

			if (eqExists != null)
			{
				return;
			}

			final String newType;

			if (isbAutoGeneration())
			{
				newType = "AUTO_GEN";
			}
			else
			{
				newType = Constants.s_CUSTOM;
			}

			if (!eq.isType(newType))
			{
				eq.addMyType(newType);
			}

			//
			// Make sure all the equipment types are present in the sorted list
			//
			Equipment.getEquipmentTypes().addAll(eq.typeList());

			getEquipmentList().add(eq);
		}
		catch (NumberFormatException exception)
		{
			Logging.errorPrint("createItem: exception: " + eq.getName());
		}
	}

	private static Equipment findEquipment(final String aName, final List preNameList, final List postNameList,
		final List sizList, final String omitString)
	{

		final StringBuffer newName = new StringBuffer(80);
		newName.append(" (");

		if (preNameList != null)
		{
			final List nameList = preNameList;
			appendNameParts(nameList, omitString, newName);
		}

		if (sizList != null)
		{
			// Append 1st size if multiple sizes
			//
			if (sizList.size() > 1)
			{
				newName.append((String) sizList.get(0));
			}
		}

		if (postNameList != null)
		{
			appendNameParts(postNameList, omitString, newName);
		}

		if (newName.length() == 2)
		{
			newName.setLength(0);
		}
		else
		{
			newName.append(')');
		}

		final Equipment eq = getEquipmentKeyed(aName + newName);

		return eq;
	}

	/**
	 * Return the equipment that has the passed-in name.
	 * @param baseName the name to return an equipment for
	 * @return the Equipment matching the name
	 */
	public static Equipment getEquipmentFromName(final String baseName)
	{
		final List modList = new ArrayList();
		final List namList = new ArrayList();
		final List sizList = new ArrayList();
		Equipment eq;
		String aName = baseName;
		int i = aName.indexOf('(');

		// Remove all modifiers from item name and
		// split into "size" and "non-size" lists
		if (i >= 0)
		{

			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);

			while (aTok.hasMoreTokens())
			{

				final String cString = aTok.nextToken();
				int iSize;

				for (iSize = 0; iSize <= SystemCollections.getSizeAdjustmentListSize() - 1; ++iSize)
				{
					if (cString.equalsIgnoreCase(SystemCollections.getSizeAdjustmentAtIndex(iSize).getAbbreviation()))
					{
						break;
					}
				}

				if (iSize <= SystemCollections.getSizeAdjustmentListSize() - 1)
				{
					sizList.add(cString);
				}
				else
				{
					if ("Mighty Composite".equalsIgnoreCase(cString))
					{
						modList.add("Mighty");
						modList.add("Composite");
					}
					else
					{
						modList.add(cString);
					}
				}
			}

			aName = aName.substring(0, i).trim();
		}

		// Separate the "non-size" descriptors into 2 Lists.
		// One containing those descriptors whose names match a
		// modifier name, and the other containing those descriptors
		// which are not possibly modifiers
		// (because they're not in the modifier list).
		//
		if (i >= 0)
		{
			for (i = modList.size() - 1; i >= 0; --i)
			{

				final String namePart = (String) modList.get(i);

				if (getModifierNamed(namePart) == null)
				{
					namList.add(0, namePart); // add to the start as otherwise the list will be reversed
					modList.remove(i);
				}
			}
		}

		// Look for magic (or mighty) bonuses
		//
		int[] bonuses = null;
		int bonusCount = 0;
		i = aName.indexOf('+');

		if (i >= 0)
		{

			final StringTokenizer aTok = new StringTokenizer(aName.substring(i), "/", false);
			bonusCount = aTok.countTokens();
			bonuses = new int[bonusCount];

			int idx = 0;

			while (aTok.hasMoreTokens())
			{

				final String cString = aTok.nextToken();
				bonuses[idx++] = Delta.decode(cString).intValue();
			}

			aName = aName.substring(0, i).trim();
		}

		//
		// Mighty bows suffered a (much-needed) renaming
		// (Long|Short)bow +n (Mighty/Composite) --> (Long|Short)bow (+n Mighty/Composite)
		// (Long|Short)bow +x/+n (Mighty/Composite) --> (Long|Short)bow +x (+n Mighty/Composite)
		//
		// Look through the modifier list for MIGHTY,
		// if found add the bonus to the start of the modifier's name
		//
		if (bonusCount > 0)
		{
			for (int idx1 = 0; idx1 < namList.size(); ++idx1)
			{

				String aString = (String) namList.get(idx1);

				if ("Mighty".equalsIgnoreCase(aString))
				{
					aString = Delta.toString(bonuses[bonusCount - 1]) + " " + aString;
					namList.set(idx1, aString);
					bonusCount -= 1;
				}
			}
		}

		//
		// aName   : name of item minus all descriptors held in () as well as any bonuses
		// namList : list of all descriptors which cannot be modifiers
		// modList : list of all descriptors which *might* be modifiers
		// sizList : list of all size descriptors
		//


		String omitString = "";
		String bonusString = "";

		for (; ;)
		{

			final String eqName = aName + bonusString;
			eq = findEquipment(eqName, null, namList, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, null, omitString);

			if (eq != null)
			{
				break;
			}

			// If only 1 size then include it in name
			if (sizList.size() == 1)
			{
				eq = findEquipment(eqName, sizList, namList, null, omitString);

				if (eq == null)
				{
					eq = findEquipment(eqName, namList, sizList, null, omitString);
				}

				if (eq != null)
				{
					sizList.clear();
					break;
				}
			}

			// If we haven't found it yet,
			// try stripping Thrown from name
			if (baseName.indexOf("Thrown") >= 0)
			{
				if (omitString.length() == 0)
				{
					omitString = "Thrown";
					continue;
				}
			}

			// Still haven't found it?
			// Try adding bonus to end of name
			if (bonusCount > 0 && bonuses != null)
			{
				if (bonusString.length() == 0)
				{
					omitString = "";
					bonusString = " " + Delta.toString(bonuses[0]);
					continue;
				}
			}

			break;
		}

		if (eq != null)
		{

			boolean bModified = false;
			boolean bError = false;
			eq = (Equipment) eq.clone();

			//
			// Now attempt to add all the modifiers.
			//
			for (Iterator e = modList.iterator(); e.hasNext();)
			{

				final String namePart = (String) e.next();
				final EquipmentModifier eqMod = getQualifiedModifierNamed(namePart, eq);

				if (eqMod != null)
				{
					eq.addEqModifier(eqMod, true);

					if (eqMod.getAssignToAll() && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false);
						bModified = true;
					}
				}
				else
				{
					Logging.errorPrint("Could not find a qualified modifier named: " + namePart + " for " + eq.getName()
						+ ":" + eq.typeList());
					bError = true;
				}
			}

			// Found what appeared to be the base item,
			// but one of the modifiers is not qualified
			// to be attached to the item
			//
			if (bError)
			{
				return null;
			}

			if (sizList.size() != 0)
			{
				eq.resizeItem((String) sizList.get(0));
				bModified = true;

				if (sizList.size() > 1)
				{
					Logging.errorPrint("Too many sizes in item name, used only 1st of: " + sizList);
				}
			}

			if (bModified)
			{
				eq.nameItemFromModifiers();

				if (!addEquipment(eq))
				{
					eq = getEquipmentNamed(eq.getName());
				}
			}
		}

		return eq;
	}

	/**
	 * Return an Equipment object with the passed-in key.
	 * @param aKey the key
	 * @return the Equipment object matching the key
	 */
	public static Equipment getEquipmentKeyed(final String aKey)
	{
		return (Equipment) Globals.searchPObjectList(getEquipmentList(), aKey);
	}

	/**
	 * Return the equipment list.
	 * @return the equipment list
	 */
	public static List getEquipmentList()
	{
		return equipmentList;
	}

	/**
	 * Return an equipment object matching the passed-in name.
	 * @param name the name to match
	 * @return the Equipment object matching the name
	 */
	public static Equipment getEquipmentNamed(final String name)
	{
		return getEquipmentNamed(name, getEquipmentList());
	}

	/**
	 * Return an equipment object from the passed-in list matching the passed-in name.
	 * @param name the name to match
	 * @param aList the list to search in
	 * @return the Equipment object matching the name
	 */
	public static Equipment getEquipmentNamed(final String name, final List aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{

			final Equipment eq = (Equipment) e.next();

			if (eq.getName().equalsIgnoreCase(name))
			{
				return eq;
			}
		}

		return null;
	}

	/**
	 * Get an Equipment object from the list matching the passed-in type(s).
	 * @param eqList the equipment list to search in
	 * @param desiredTypes a '.' separated list of types to match
	 * @param excludedTypes a '.' separated list of types to NOT match
	 * @return the matching Equipment
	 */
	public static List getEquipmentOfType(final List eqList, final String desiredTypes, final String excludedTypes)
	{

		final List desiredTypeList = Utility.split(desiredTypes, '.');
		final List excludedTypeList = Utility.split(excludedTypes, '.');
		final List typeList = new ArrayList(100);

		if (desiredTypeList.size() != 0)
		{
			for (Iterator e = eqList.iterator(); e.hasNext();)
			{

				final Equipment eq = (Equipment) e.next();
				boolean addIt = true;

				//
				// Must have all of the types in the desired list
				//
				for (Iterator e2 = desiredTypeList.iterator(); e2.hasNext();)
				{
					if (!eq.isType((String) e2.next()))
					{
						addIt = false;
						break;
					}
				}

				if (addIt && (excludedTypeList.size() != 0))
				{

					//
					// Can't have any of the types on the excluded list
					//
					for (Iterator e3 = excludedTypeList.iterator(); e3.hasNext();)
					{
						if (eq.isType((String) e3.next()))
						{
							addIt = false;
							break;
						}
					}
				}

				if (addIt)
				{
					typeList.add(eq);
				}
			}
		}

		return typeList;
	}

	/**
	 * Return a modifier matching the passed-in key.
	 * @param aKey the key to match
	 * @return the Equipment object
	 */
	public static EquipmentModifier getModifierKeyed(final String aKey)
	{
		return (EquipmentModifier) Globals.searchPObjectList(getModifierList(), aKey);
	}

	/**
	 * Return the modifier list.
	 * @return the list
	 */
	public static List getModifierList()
	{
		return modifierList;
	}

	private static EquipmentModifier getModifierNamed(final String aName)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();

			if (aEqMod.getName().equals(aName))
			{
				return aEqMod;
			}
		}

		return null;
	}

	private static EquipmentModifier getQualifiedModifierNamed(final String aName, final Equipment eq)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			if (aEqMod.getName().equals(aName))
			{
				for (Iterator e2 = eq.typeList().iterator(); e2.hasNext();)
				{
					final String t = (String) e2.next();

					if (aEqMod.isType(t))
					{
						//
						// Type matches, passes prereqs?
						//
						if (aEqMod.passesPreReqToGain(eq))
						{
							return aEqMod;
						}
					}
				}
			}
		}

		return null;
	}

	static EquipmentModifier getQualifiedModifierNamed(final String aName, final List aType)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();

			if (aEqMod.getName().equals(aName))
			{
				if (aEqMod.isType("All"))
				{
					return aEqMod;
				}

				for (Iterator e2 = aType.iterator(); e2.hasNext();)
				{
					final String t = (String) e2.next();

					if (aEqMod.isType(t))
					{
						return aEqMod;
					}
				}
			}
		}

		return null;
	}

	private static boolean isbAutoGeneration()
	{
		return bAutoGeneration;
	}

	/**
	 * Set whether magic equipment auto generation should be on.
	 * @param auto true if it should be on
	 */
	public static void setAutoGeneration(final boolean auto)
	{
		setbAutoGeneration(auto);
	}

	private static void setbAutoGeneration(final boolean bAutoGeneration)
	{
		EquipmentList.bAutoGeneration = bAutoGeneration;
	}
}
