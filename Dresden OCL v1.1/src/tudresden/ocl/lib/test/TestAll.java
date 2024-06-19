package tudresden.ocl.lib.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAll extends TestCase {

  public TestAll(String s) {
    super(s);
  }

  public static Test suite() {
    TestSuite suite=new TestSuite();
    suite.addTest( TestBoolean.suite() );
    suite.addTest( TestInteger.suite() );
    suite.addTest( TestReal.suite() );
    suite.addTest( TestString.suite() );
    suite.addTest( TestSet.suite() );
    suite.addTest( TestBag.suite() );
    suite.addTest( TestSequence.suite() );
    suite.addTest( TestArgoNameAdapter.suite() );
    suite.addTest( TestAnyImpl.suite() );
    suite.addTest( TestType.suite() );
    return suite;
  }
}
