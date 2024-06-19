/*
 * PreerquisiteTestFactory.java Copyright 2003 (C) Chris Ward
 * <frugal@purplewombat.co.uk>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on November 28, 2003
 * 
 * Current Ver: $Revision: 1.1 $ Last Editor: $Author: vauchers $ Last Edited: $Date: 2006/02/21 01:33:20 $
 *  
 */
package pcgen.core.prereq;

import java.util.HashMap;
import java.util.Map;

import pcgen.util.Logging;

/**
 * @author wardc
 *  
 */
public class PrerequisiteTestFactory {
	private static PrerequisiteTestFactory instance = null;
	private static Map testLookup = new HashMap();

	/**
	 * @return Returns the instance.
	 */
	public static PrerequisiteTestFactory getInstance() {
		if (instance == null)
			instance = new PrerequisiteTestFactory();
		return instance;
	}

	private PrerequisiteTestFactory() {
		register(new PreAlign());
		register(new PreArmourProficiency());
		register(new PreArmourType());
		register(new PreAttack());
		register(new PreBaseSize());
		register(new PreBirthPlace());
		register(new PreCity());
		register(new PreCheck());
		register(new PreClass());
		register(new PreClassLevelMax());
		register(new PreDamageReduction());
		register(new PreDefaultMonster());
		register(new PreDeity());
		register(new PreDeityAlign());
		register(new PreDeityDomain());
		register(new PreDomain());
		register(new PreEquip());
		register(new PreEquippedBoth());
		register(new PreEquippedPrimary());
		register(new PreEquippedSecondary());
		register(new PreEquippedTwoWeapon());
		register(new PreFeat());
		register(new PreGender());
		register(new PreHands());
		register(new PreHD());
		register(new PreHP());
		register(new PreItem());
		register(new PreLanguage());
		register(new PreLegs());
		register(new PreLevel());
		register(new PreLevelMax());
		register(new PreMove());
		register(new PreRace());
		register(new PreRegion());
		register(new PreShieldProficiency());
		register(new PreSize());
		register(new PreSkill());
		register(new PreSkillTotal());
		register(new PreSpell());
		register(new PreSpecialAbility());
		register(new PreSpellBook());
		register(new PreSpellCast());
		register(new PreSpellResistance());
		register(new PreSpellSchool());
		register(new PreSpellSchoolSub());
		register(new PreSpellType());
		register(new PreStat());
		register(new PreSubClass());
		register(new PreTemplate());
		register(new PreUnarmedAttack());
		register(new PreVision());
		register(new PreWield());
		register(new PreWeaponProficiency());
	}

	private void register(PrerequisiteTest testClass) {
		String[] kindsHandled = testClass.kindsHandled();
		for (int i = 0; i < kindsHandled.length; i++) {
			Object test = testLookup.get(kindsHandled[i]);
			if (test != null) {
				Logging.errorPrint(
					"Error registering '"
						+ testClass.getClass().getName()
						+ "' as test '"
						+ kindsHandled[i]
						+ "'. The test is already registered to '"
						+ test.getClass().getName()
						+ "'");
			}
			testLookup.put(kindsHandled[i], testClass);
		}
	}

	public PrerequisiteTest getTest(String kind) {
		PrerequisiteTest test = (PrerequisiteTest) testLookup.get(kind);
		return test;
	}

}
