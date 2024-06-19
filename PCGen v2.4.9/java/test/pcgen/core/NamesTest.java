/*
 * User: John K. Watson
 * Date: Feb 6, 2002
 * Time: 10:10:32 PM
 * 
 */
package test.pcgen.core;

import junit.framework.*;
import pcgen.core.*;

import java.io.*;
import java.util.*;

public class NamesTest extends TestCase {

  public NamesTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void testTheNames() {
    Names.getInstance().init("orc");
//    for (Iterator iterator = Names.getInstance().getRuleList().iterator(); iterator.hasNext();) {
//      String rule = (String) iterator.next();
//      System.out.println("rule = " + rule);
//    }
//    for (Iterator iterator = Names.getInstance().getSyllablesByName("[SYL1]").iterator(); iterator.hasNext();) {
//      String syl1 = (String) iterator.next();
//      System.out.println("syl1 = " + syl1);
//    }

    assertTrue("I got null rules!", Names.getInstance().getRuleDefinitions() != null);
    assertTrue("I didn't get any rules!", Names.getInstance().getRuleDefinitions().length > 0);
    assertTrue("There was nothing in syl1", Names.getInstance().getSyllablesByName("[SYL1]").length > 0);
  }

  public void testRandomName() {
    Names.getInstance().init("orc");
//    System.out.println("random orc name: " + Names.getInstance().getRandomName());
    assertTrue("got a null name!", Names.getInstance().getRandomName() != null);
    assertTrue("got a zero-length name!", Names.getInstance().getRandomName().length() > 0);
  }

  public void testHober() {
    Names.getInstance().init("HOBER");
    assertTrue("got a null name!", Names.getInstance().getRandomName() != null);
    assertTrue("got a zero-length name!", Names.getInstance().getRandomName().length() > 0);
    System.out.println("random hober name: " + Names.getInstance().getRandomName());

  }
  public void testArabic() {
    Names.getInstance().init("Arabic");
    assertTrue("got a null name!", Names.getInstance().getRandomName() != null);
    assertTrue("got a zero-length name!", Names.getInstance().getRandomName().length() > 0);
    System.out.println("random arabic name: " + Names.getInstance().getRandomName());

  }


  public void testGettingNameFiles() throws Exception {
    assertTrue("got null back!", Names.findAllNamesFiles() != null);
    assertTrue("got empty array back!", Names.findAllNamesFiles().length > 0);
    for (int i = 0; i < Names.findAllNamesFiles().length; i++) {
      String s = Names.findAllNamesFiles()[i];
      System.out.println("s = " + s);
      assertTrue("file ended with .nam!", ! s.endsWith(".nam"));
    }

  }
}
