package test.pcgen.core;

import junit.framework.*;
import pcgen.core.Globals;

public class GlobalsTest extends TestCase {

  public void testAttribConstants(){
    int index = Globals.STRENGTH;
    assertEquals("Strength",Globals.s_ATTRIBLONG[index]);
    assertEquals("STR",Globals.s_ATTRIBSHORT[index]);
    index = Globals.DEXTERITY;
    assertEquals("Dexterity",Globals.s_ATTRIBLONG[index]);
    assertEquals("DEX",Globals.s_ATTRIBSHORT[index]);
    index = Globals.CONSTITUTION;
    assertEquals("Constitution",Globals.s_ATTRIBLONG[index]);
    assertEquals("CON",Globals.s_ATTRIBSHORT[index]);
    index = Globals.INTELLIGENCE;
    assertEquals("Intelligence",Globals.s_ATTRIBLONG[index]);
    assertEquals("INT",Globals.s_ATTRIBSHORT[index]);
    index = Globals.WISDOM;
    assertEquals("Wisdom",Globals.s_ATTRIBLONG[index]);
    assertEquals("WIS",Globals.s_ATTRIBSHORT[index]);
    index = Globals.CHARISMA;
    assertEquals("Charisma",Globals.s_ATTRIBLONG[index]);
    assertEquals("CHA",Globals.s_ATTRIBSHORT[index]);
  }

  public void testAlignmentConstants(){
    assertEquals("LG",Globals.s_ALIGNSHORT[0]);
    assertEquals("LN",Globals.s_ALIGNSHORT[1]);
    assertEquals("LE",Globals.s_ALIGNSHORT[2]);
    assertEquals("NG",Globals.s_ALIGNSHORT[3]);
    assertEquals("TN",Globals.s_ALIGNSHORT[4]);
    assertEquals("NE",Globals.s_ALIGNSHORT[5]);
    assertEquals("CG",Globals.s_ALIGNSHORT[6]);
    assertEquals("CN",Globals.s_ALIGNSHORT[7]);
    assertEquals("CE",Globals.s_ALIGNSHORT[8]);
    assertEquals("None",Globals.s_ALIGNSHORT[9]);
    assertEquals("Deity",Globals.s_ALIGNSHORT[10]);
    assertEquals("Lawful Good",Globals.s_ALIGNLONG[0]);
    assertEquals("Lawful Neutral",Globals.s_ALIGNLONG[1]);
    assertEquals("Lawful Evil",Globals.s_ALIGNLONG[2]);
    assertEquals("Neutral Good",Globals.s_ALIGNLONG[3]);
    assertEquals("Neutral",Globals.s_ALIGNLONG[4]);
    assertEquals("Neutral Evil",Globals.s_ALIGNLONG[5]);
    assertEquals("Chaotic Good",Globals.s_ALIGNLONG[6]);
    assertEquals("Chaotic Neutral",Globals.s_ALIGNLONG[7]);
    assertEquals("Chaotic Evil",Globals.s_ALIGNLONG[8]);
    assertEquals(Globals.s_NONESELECTED,Globals.s_ALIGNLONG[9]);
    assertEquals("Deity's",Globals.s_ALIGNLONG[10]);
    assertEquals("Revise the test for new constants.",10,Globals.s_ALIGNLONG.length);
  }

  public void testSizeConstants(){
    assertEquals("Fine",Globals.s_SIZELONG[0]);
    assertEquals("Diminutive",Globals.s_SIZELONG[1]);
    assertEquals("Tiny",Globals.s_SIZELONG[2]);
    assertEquals("Small",Globals.s_SIZELONG[3]);
    assertEquals("Medium",Globals.s_SIZELONG[4]);
    assertEquals("Large",Globals.s_SIZELONG[5]);
    assertEquals("Huge",Globals.s_SIZELONG[6]);
    assertEquals("Gigantic",Globals.s_SIZELONG[7]);
    assertEquals("Colossal",Globals.s_SIZELONG[8]);
    assertEquals("F",Globals.s_SIZESHORT[0]);
    assertEquals("D",Globals.s_SIZESHORT[1]);
    assertEquals("T",Globals.s_SIZESHORT[2]);
    assertEquals("S",Globals.s_SIZESHORT[3]);
    assertEquals("M",Globals.s_SIZESHORT[4]);
    assertEquals("L",Globals.s_SIZESHORT[5]);
    assertEquals("H",Globals.s_SIZESHORT[6]);
    assertEquals("G",Globals.s_SIZESHORT[7]);
    assertEquals("C",Globals.s_SIZESHORT[8]);
    for (int x=0;x<Globals.s_SIZELONG.length;x++){
      assertEquals(x,Globals.sizeInt(Globals.s_SIZELONG[x]));
      assertEquals(Globals.s_SIZESHORT[x],
		   Globals.s_SIZESHORT[Globals.sizeInt(Globals.s_SIZESHORT[x])]);
      assertEquals(Globals.s_SIZESHORT[x],
                   Globals.s_SIZESHORT[Globals.sizeInt(Globals.s_SIZELONG[x])]);
    }
  } 

  // I realize this class isn't all that useful, but I'm being 
  // over-thorough at the moment.
  public void testSimpleConstants(){
    assertEquals("TYPE:",Globals.s_TAG_TYPE);
    assertEquals(0,Globals.RACE_TYPE);
    assertEquals(1,Globals.CLASS_TYPE);
    assertEquals(2,Globals.SKILL_TYPE);
    assertEquals(3,Globals.FEAT_TYPE);
    assertEquals(4,Globals.DOMAIN_TYPE);
    assertEquals(5,Globals.DEITY_TYPE);
    assertEquals(6,Globals.SPELL_TYPE);
    assertEquals(7,Globals.WEAPONPROF_TYPE);
    assertEquals(8,Globals.SCHOOLS_TYPE);
    assertEquals(9,Globals.COLOR_TYPE);
    assertEquals(10,Globals.TRAIT_TYPE);
    assertEquals(11,Globals.EQUIPMENT_TYPE);
    assertEquals(12,Globals.LANGUAGE_TYPE);
    assertEquals(13,Globals.LOAD_TYPE);
    assertEquals(14,Globals.SPECIAL_TYPE);
    assertEquals(15,Globals.CAMPAIGN_TYPE);
    assertEquals(16,Globals.CLASSSKILL_TYPE);
    assertEquals(17,Globals.CLASSSPELL_TYPE);
    assertEquals(18,Globals.REQSKILL_TYPE);
    assertEquals(21,Globals.TEMPLATE_TYPE);
    assertEquals(22,Globals.XP_TYPE);
    assertEquals(23,Globals.NAME_TYPE);
    assertEquals(24,Globals.BONUS_TYPE);
    assertEquals(25,Globals.EQMODIFIER_TYPE);
    fail("Need to determine what Types 19 & 20 are.");
  }

  public void testToolTip(){
    assert("toolTipShown default changed.",Globals.isToolTipTextShown());
    Globals.setToolTipTextShown(false);
    assert("setToolTipShown failed.",!Globals.isToolTipTextShown());
    Globals.setToolTipTextShown(true);
    assert("setToolTipShown failed.",Globals.isToolTipTextShown());
  }

  public void testPreviewTab(){
    assert(!Globals.isPreviewTabShown());
    Globals.setPreviewTabShown(true);
    assert(Globals.isPreviewTabShown());
    Globals.setPreviewTabShown(false);
    assert(!Globals.isPreviewTabShown());
  }

  public void testRandomInt(){
    int x,rand;
    for (x=0;x<10000;x++){
      rand = Globals.getRandomInt(6);
      assert("Random number out of range low:",rand>=0);
      assert("Random number out of range high:",rand<=5);
      try { 
        Globals.getRandomInt(); 
      } catch (Throwable t) {
        fail("Unbounded getRandomInt threw a "+t.toString());
      }
    }
  }

  public void testPCCFilesLocation(){
    String s = Globals.getPccFilesLocation();
    System.out.println(s);
    Globals.setPccFilesLocation("New Location");
    assertEquals("New Location", Globals.getPccFilesLocation());
  }

  public void testStatCost(){
    int statcosts[] = Globals.getStatCost();
    for (int x=0;x<statcosts.length;x++){
      assertEquals(statcosts[x],Globals.getStatCost(x));
    }
  }

  public GlobalsTest(String name){
    super(name);
  }
}
