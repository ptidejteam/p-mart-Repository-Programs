/*
 * @(#) $Id: FeatTest.java,v 1.1 2006/02/20 23:57:30 vauchers Exp $
 * GNU LESSER GENERAL PUBLIC LICENSE
 */
package test.pcgen.core;

import java.io.File;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.core.Feat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.FeatLoader;

/**
 * JUnit testcases for <code>pcgen.core.Feat</code>.
 *
 * @version $Revision: 1.1 $
 */

public class FeatTest extends TestCase
{
	Feat alertnessFeat;
	Feat ambidexterityFeat;
	Feat simpleWeaponFeat;
	File testFile;

	public FeatTest(String name)
	{
		super(name);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(FeatTest.class);
	}

	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(FeatTest.class);
	}

	public void setUp()
	{
		try
		{
			testFile = File.createTempFile("featTest", "tmp");
		}
		catch (IOException e)
		{
			fail("Could not create temporary file.");
		}
		try
		{
			alertnessFeat = new Feat();
			FeatLoader.parseLine(alertnessFeat, "Alertness	TYPE:General	DESC:+2 on Listen and Spot checks	BONUS:SKILL|Listen,Spot|2", testFile, 1);
			ambidexterityFeat = new Feat();
			FeatLoader.parseLine(ambidexterityFeat, "Ambidexterity	PRESTAT:DEX=15	PREHANDSEQ:2	TYPE:General.Fighter	DESC:You ignore all penalties for using your off-hand	BONUS:COMBAT|TOHIT-SECONDARY|4", testFile, 1);
			simpleWeaponFeat = new Feat();
			FeatLoader.parseLine(simpleWeaponFeat, "Simple Weapon Proficiency	TYPE:General	DESC:You are proficient with all simple weapons. Non-proficiency suffers -4 to hit.	ADD:WEAPONPROFS|Simple", testFile, 1);
		}
		catch (PersistenceLayerException e)
		{
			fail("PersistenceLayerException not expected." + e.getMessage());
		}
	}

	public void tearDown()
	{
		alertnessFeat = null;
		ambidexterityFeat = null;
		simpleWeaponFeat = null;
		testFile.delete();
	}

	public void testAlertness()
	{
		assertEquals("Alertness", alertnessFeat.getName());
	}

	public void testAmbidexterity()
	{
		assertEquals("Ambidexterity", ambidexterityFeat.getName());
	}

	public void testSimpleWeapon()
	{
		assertEquals("Simple Weapon Proficiency", simpleWeaponFeat.getName());
	}

}
