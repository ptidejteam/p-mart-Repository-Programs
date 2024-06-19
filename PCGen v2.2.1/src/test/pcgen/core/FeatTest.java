/*
 * @(#) $Id: FeatTest.java,v 1.1 2006/02/20 22:01:50 vauchers Exp $
 * GNU LESSER GENERAL PUBLIC LICENSE
 */
package test.pcgen.core;

import junit.framework.*;
import java.util.Vector;
import junit.extensions.*;
import java.io.*;

import pcgen.core.*;

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

	public FeatTest(String name) {
		super (name);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(FeatTest.class);
	}

	public static Test suite() {
    // quick method, adds all methods beginning with "test"
		return new TestSuite(FeatTest.class); 
	}

	public void setUp()	{
    try
    {
      testFile = File.createTempFile("featTest", "tmp");
    }
    catch (IOException e)
    {
      fail("Could not create temporary file.");
    }
    alertnessFeat = new Feat();
    alertnessFeat.parseLine("Alertness	TYPE:General	DESC:+2 on Listen and Spot checks	BONUS:SKILL|Listen,Spot|2", testFile, 1);
    ambidexterityFeat = new Feat();
    ambidexterityFeat.parseLine("Ambidexterity	PRESTAT:DEX=15	PREHANDSEQ:2	TYPE:General.Fighter	DESC:You ignore all penalties for using your off-hand	BONUS:COMBAT|TOHIT-SECONDARY|4", testFile, 1);
    simpleWeaponFeat = new Feat();
    simpleWeaponFeat.parseLine("Simple Weapon Proficiency	TYPE:General	DESC:You are proficient with all simple weapons. Non-proficiency suffers -4 to hit.	ADD:WEAPONPROFS|Simple", testFile, 1);
	}

	public void tearDown() {
    alertnessFeat = null;
    ambidexterityFeat = null;
    simpleWeaponFeat = null;
    testFile.delete();
	}

  public void testAlertness() {
    assertEquals( "Alertness", alertnessFeat.getName() );
	}

  public void testAmbidexterity() {
    assertEquals( "Ambidexterity", ambidexterityFeat.getName() );
	}
    
  public void testSimpleWeapon() {
    assertEquals( "Simple Weapon Proficiency", simpleWeaponFeat.getName() );
	}

}
