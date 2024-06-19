/*
 * Created on Aug 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.taursys.xml.test;

import com.taursys.dom.DocumentAdapter;
import com.taursys.xml.Form;

import junit.framework.TestCase;

/**
 * @author marty
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FormTest extends TestCase {

  /**
   * Constructor for FormTest.
   * @param arg0
   */
  public FormTest(String arg0) {
    super(arg0);
  }
  
  class TestForm extends Form {
    public void initForm() throws Exception {
      super.initForm();
    }
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testInitFormValidResourceThis() throws Exception {
    TestForm form = new TestForm();
    form.setDocumentURI("resource:///com/taursys/xml/test/test.html");
    form.initForm();
    DocumentAdapter da = form.getDocumentAdapter();
    assertNotNull("DocumentAdapter is null", da);
    assertNotNull("Document is null", da.getDocument());
  }

  public void testInitFormValidResourceSpecific() throws Exception {
    TestForm form = new TestForm();
    form.setDocumentURI("resource://com.taursys.xml.Form/com/taursys/xml/test/test.html");
    form.initForm();
    DocumentAdapter da = form.getDocumentAdapter();
    assertNotNull("DocumentAdapter is null", da);
    assertNotNull("Document is null", da.getDocument());
  }

  public void testInitFormValidURL() throws Exception {
    TestForm form = new TestForm();
    String testdir = System.getProperty("com.taursys.test.dir");
    assertNotNull(
        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
        testdir);
    testdir = testdir.replaceAll("\\\\","/");
    form.setDocumentURI("file:///" + testdir + "/src/com/taursys/xml/test/test.html");
    form.initForm();
    DocumentAdapter da = form.getDocumentAdapter();
    assertNotNull("DocumentAdapter is null", da);
    assertNotNull("Document is null", da.getDocument());
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(FormTest.class);
  }

}
