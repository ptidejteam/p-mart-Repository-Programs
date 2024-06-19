package tudresden.ocl.lib.test;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import tudresden.ocl.lib.ArgoNameAdapter;
import tudresden.ocl.lib.Ocl;
import tudresden.ocl.lib.OclRoot;

public class TestArgoNameAdapter extends TestCase {

  public TestArgoNameAdapter(String s) {
    super(s);
  }

  public void testAdapt() {
    ArgoNameAdapter ana=new ArgoNameAdapter();
    Ocl.setNameAdapter(ana);
    String[] arrayPerson={"person", "myPerson"};
    String[] arrayX={"X", "myX"};

    String[] resPerson=ana.getNames("person");
    for (int i=0; i<resPerson.length; i++) {
      assertTrue( resPerson[i].equals(arrayPerson[i]) );
    }
    String[] resX=ana.getNames("X");
    for (int i=0; i<resX.length; i++) {
      assertTrue( resX[i].equals(arrayX[i]) );
    }

    Person p=new Person();
    OclRoot person=Ocl.getFor(p);
    assertTrue( person.getFeatureAsCollection("job").isEmpty().isTrue() );
    Vector v=new Vector();
    v.add(new Object());
    v.add(new Object());
    p.setJob(v);
    assertTrue( person.getFeatureAsCollection("job").isEmpty().not().isTrue() );
    assertTrue( person.getFeatureAsCollection("job").size().isEqualTo(Ocl.getFor(2)).isTrue() );
  }

  public static Test suite() {
    TestSuite suite=new TestSuite();
    suite.addTest( new TestArgoNameAdapter("testAdapt") );
    return suite;
  }
}

