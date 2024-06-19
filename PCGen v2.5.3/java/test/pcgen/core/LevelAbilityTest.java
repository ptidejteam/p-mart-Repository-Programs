/*
 * LevelAbility.java
 * Copyright 2001 (C) Dmitry Jemerov <yole@spb.cityline.ru>
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
 * Created on July 23, 2001, 8:40 PM
 */

package test.pcgen.core;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import pcgen.core.*;
import pcgen.gui.Chooser;

import java.util.ArrayList;

/**
 * JUnit 3.6 testcases for <code>pcgen.core.LevelAbility</code>.
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 */

public class LevelAbilityTest extends TestCase
{
	Race emptyRace = new Race();
	PCClass pcClass;

	public LevelAbilityTest(String name)
	{
		super(name);
		emptyRace.setName(Globals.s_NONESELECTED);
		Globals.getRaceMap().put(emptyRace.getName(), emptyRace);
	}

	public void setUp()
	{
		pcClass = new PCClass();
		new PlayerCharacter();
		Globals.getCurrentPC().incrementClassLevel(1, pcClass);
	}

	public void tearDown()
	{
		Globals.setCurrentPC(null);
		pcClass = null;
	}

	public void testLanguage()
	{
		LevelAbility ability = LevelAbility.createAbility(pcClass, 1, "Language(Elven,Dwarvish)");
		assertTrue(ability.level() == 1);
		assertTrue(ability.canProcess() == true);

		Chooser c = new Chooser();
		String bString = ability.prepareChooser(c);
		assertTrue(c.getPool() == 1);

		ArrayList choicesList = ability.getChoicesList(bString);
		assertEquals(choicesList.size(), 2);
		String s = (String)choicesList.get(0);
		assertEquals("Dwarvish", s);
		s = (String)choicesList.get(1);
		assertEquals("Elven", s);
	}

}
