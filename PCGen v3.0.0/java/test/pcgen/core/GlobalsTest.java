package test.pcgen.core;

import java.io.File;
import junit.framework.TestCase;
import pcgen.core.Constants;
import pcgen.core.*;
import pcgen.persistence.lst.LstConstants;

public class GlobalsTest extends TestCase
{

	public void testAttribConstants()
	{
		int index = Constants.STRENGTH;
		assertEquals("Strength", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.STRENGTH_ABBR, Globals.s_ATTRIBSHORT[index]);
		index = Constants.DEXTERITY;
		assertEquals("Dexterity", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.DEXTERITY_ABBR, Globals.s_ATTRIBSHORT[index]);
		index = Constants.CONSTITUTION;
		assertEquals("Constitution", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.CONSTITUTION_ABBR, Globals.s_ATTRIBSHORT[index]);
		index = Constants.INTELLIGENCE;
		assertEquals("Intelligence", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.INTELLIGENCE_ABBR, Globals.s_ATTRIBSHORT[index]);
		index = Constants.WISDOM;
		assertEquals("Wisdom", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.WISDOM_ABBR, Globals.s_ATTRIBSHORT[index]);
		index = Constants.CHARISMA;
		assertEquals("Charisma", Globals.s_ATTRIBLONG[index]);
		assertEquals(Constants.CHARISMA_ABBR, Globals.s_ATTRIBSHORT[index]);
	}

	public void testAlignmentConstants()
	{
		assertEquals("LG", Constants.s_ALIGNSHORT[0]);
		assertEquals("LN", Constants.s_ALIGNSHORT[1]);
		assertEquals("LE", Constants.s_ALIGNSHORT[2]);
		assertEquals("NG", Constants.s_ALIGNSHORT[3]);
		assertEquals("TN", Constants.s_ALIGNSHORT[4]);
		assertEquals("NE", Constants.s_ALIGNSHORT[5]);
		assertEquals("CG", Constants.s_ALIGNSHORT[6]);
		assertEquals("CN", Constants.s_ALIGNSHORT[7]);
		assertEquals("CE", Constants.s_ALIGNSHORT[8]);
		assertEquals("None", Constants.s_ALIGNSHORT[9]);
		assertEquals("Deity", Constants.s_ALIGNSHORT[10]);
		assertEquals("Lawful Good", Constants.s_ALIGNLONG[0]);
		assertEquals("Lawful Neutral", Constants.s_ALIGNLONG[1]);
		assertEquals("Lawful Evil", Constants.s_ALIGNLONG[2]);
		assertEquals("Neutral Good", Constants.s_ALIGNLONG[3]);
		assertEquals("Neutral", Constants.s_ALIGNLONG[4]);
		assertEquals("Neutral Evil", Constants.s_ALIGNLONG[5]);
		assertEquals("Chaotic Good", Constants.s_ALIGNLONG[6]);
		assertEquals("Chaotic Neutral", Constants.s_ALIGNLONG[7]);
		assertEquals("Chaotic Evil", Constants.s_ALIGNLONG[8]);
		assertEquals(Constants.s_NONESELECTED, Constants.s_ALIGNLONG[9]);
		assertEquals("Deity's", Constants.s_ALIGNLONG[10]);
		assertEquals("Revise the test for new constants.", 10, Constants.s_ALIGNLONG.length);
	}

	public void testSizeConstants()
	{
		assertEquals("Fine", Constants.s_SIZELONG[0]);
		assertEquals("Diminutive", Constants.s_SIZELONG[1]);
		assertEquals("Tiny", Constants.s_SIZELONG[2]);
		assertEquals("Small", Constants.s_SIZELONG[3]);
		assertEquals("Medium", Constants.s_SIZELONG[4]);
		assertEquals("Large", Constants.s_SIZELONG[5]);
		assertEquals("Huge", Constants.s_SIZELONG[6]);
		assertEquals("Gigantic", Constants.s_SIZELONG[7]);
		assertEquals("Colossal", Constants.s_SIZELONG[8]);
		assertEquals("F", Constants.s_SIZESHORT[0]);
		assertEquals("D", Constants.s_SIZESHORT[1]);
		assertEquals("T", Constants.s_SIZESHORT[2]);
		assertEquals("S", Constants.s_SIZESHORT[3]);
		assertEquals("M", Constants.s_SIZESHORT[4]);
		assertEquals("L", Constants.s_SIZESHORT[5]);
		assertEquals("H", Constants.s_SIZESHORT[6]);
		assertEquals("G", Constants.s_SIZESHORT[7]);
		assertEquals("C", Constants.s_SIZESHORT[8]);
		for (int x = 0; x < Constants.s_SIZELONG.length; x++)
		{
			assertEquals(x, Globals.sizeInt(Constants.s_SIZELONG[x]));
			assertEquals(Constants.s_SIZESHORT[x],
				Constants.s_SIZESHORT[Globals.sizeInt(Constants.s_SIZESHORT[x])]);
			assertEquals(Constants.s_SIZESHORT[x],
				Constants.s_SIZESHORT[Globals.sizeInt(Constants.s_SIZELONG[x])]);
		}
	}

	// I realize this class isn't all that useful, but I'm being
	// over-thorough at the moment.
	public void testSimpleConstants()
	{
		assertEquals("TYPE:", Constants.s_TAG_TYPE);
		assertEquals(0, LstConstants.RACE_TYPE);
		assertEquals(1, LstConstants.CLASS_TYPE);
		assertEquals(2, LstConstants.SKILL_TYPE);
		assertEquals(3, LstConstants.FEAT_TYPE);
		assertEquals(4, LstConstants.DOMAIN_TYPE);
		assertEquals(5, LstConstants.DEITY_TYPE);
		assertEquals(6, LstConstants.SPELL_TYPE);
		assertEquals(7, LstConstants.WEAPONPROF_TYPE);
		assertEquals(8, LstConstants.SCHOOLS_TYPE);
		assertEquals(9, LstConstants.COLOR_TYPE);
		assertEquals(10, LstConstants.TRAIT_TYPE);
		assertEquals(11, LstConstants.EQUIPMENT_TYPE);
		assertEquals(12, LstConstants.LANGUAGE_TYPE);
		assertEquals(13, LstConstants.LOAD_TYPE);
		assertEquals(14, LstConstants.SPECIAL_TYPE);
		assertEquals(15, LstConstants.CAMPAIGN_TYPE);
		assertEquals(16, LstConstants.CLASSSKILL_TYPE);
		assertEquals(17, LstConstants.CLASSSPELL_TYPE);
		assertEquals(18, LstConstants.REQSKILL_TYPE);
		assertEquals(21, LstConstants.TEMPLATE_TYPE);
		assertEquals(22, LstConstants.XP_TYPE);
		assertEquals(24, LstConstants.BONUS_TYPE);
		assertEquals(25, LstConstants.EQMODIFIER_TYPE);
		fail("Need to determine what Types 19 & 20 are.");
	}

	public void testToolTip()
	{
		assertTrue("toolTipShown default changed.", SettingsHandler.isToolTipTextShown());
		SettingsHandler.setToolTipTextShown(false);
		assertTrue("setToolTipShown failed.", !SettingsHandler.isToolTipTextShown());
		SettingsHandler.setToolTipTextShown(true);
		assertTrue("setToolTipShown failed.", SettingsHandler.isToolTipTextShown());
	}

	public void testPreviewTab()
	{
		assertTrue(!SettingsHandler.isPreviewTabShown());
		SettingsHandler.setPreviewTabShown(true);
		assertTrue(SettingsHandler.isPreviewTabShown());
		SettingsHandler.setPreviewTabShown(false);
		assertTrue(!SettingsHandler.isPreviewTabShown());
	}

	public void testRandomInt()
	{
		int x,rand;
		for (x = 0; x < 10000; x++)
		{
			rand = Globals.getRandomInt(6);
			assertTrue("Random number out of range low:", rand >= 0);
			assertTrue("Random number out of range high:", rand <= 5);
			try
			{
				Globals.getRandomInt();
			}
			catch (Throwable t)
			{
				fail("Unbounded getRandomInt threw a " + t.toString());
			}
		}
	}

	public void testPCCFilesLocation()
	{
		String s = SettingsHandler.getPccFilesLocation().getAbsolutePath();
		System.out.println(s);
		File f = new File(File.separator);
		SettingsHandler.setPccFilesLocation(f);
		assertEquals(f.getAbsolutePath(), SettingsHandler.getPccFilesLocation().getAbsolutePath());
	}

	public void testStatCost()
	{
		int statcosts[] = SettingsHandler.getAbilityScoreCost();

		for (int x = 0; x < statcosts.length; x++)
		{
			assertEquals(statcosts[x], SettingsHandler.getAbilityScoreCost(x));
		}
	}

	public GlobalsTest(String name)
	{
		super(name);
	}
}
