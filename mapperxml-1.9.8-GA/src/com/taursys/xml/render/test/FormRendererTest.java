/*
 * Created on Jul 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.taursys.xml.render.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EventObject;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.xml.Form;
import com.taursys.xml.event.InitFormEvent;

/**
 * @author marty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FormRendererTest extends TestCase {

  public FormRendererTest(String name) {
    super(name);
  }

  protected void compareDoc(DocumentAdapter adapter, InputStream expectedStream) throws IOException
      {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    adapter.write(bos);
    BufferedReader resultsReader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bos.toByteArray())));
    BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
       expectedStream));
    // Check each line
    String expected;
    int i = 0;
    while ((expected = expectedReader.readLine()) != null) {
      assertEquals("Contents line #" + i, expected, resultsReader.readLine());
      i++;
    }
  }
  
  class TestForm extends Form {

    public void dispatchRender() throws Exception {
      super.dispatchRender();
    }

    public void processEvent(EventObject e) throws Exception {
      super.processEvent(e);
    }
  }

  public void testRenderSubFormWithTidy() throws Exception {
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.TIDY_BUILDER);
    TestForm master = new TestForm();
    Form subform = new Form();
    master.setDocumentURI("resource:///com/taursys/xml/render/test/FormRendererTestMaster.html");
    subform.setDocumentURI("resource:///com/taursys/xml/render/test/FormRendererTestSubform.html");
    subform.setId("headArea");
    subform.setSourceId("headContent");
    master.add(subform);
    master.processEvent(new InitFormEvent(this));
    master.dispatchRender();
//    master.getDocumentAdapter().write(System.out);
    compareDoc(master.getDocumentAdapter(), this.getClass().getResourceAsStream("FormRendererTestExpectedMasterTidy.html"));
  }

  public void testRenderSubFormWithXerces() throws Exception {
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.XERCES_BUILDER);
    TestForm master = new TestForm();
    Form subform = new Form();
    master.setDocumentURI("resource:///com/taursys/xml/render/test/FormRendererTestMaster.html");
    subform.setDocumentURI("resource:///com/taursys/xml/render/test/FormRendererTestSubform.html");
    subform.setId("headArea");
    subform.setSourceId("headContent");
    master.add(subform);
    master.processEvent(new InitFormEvent(this));
    master.dispatchRender();
//    master.getDocumentAdapter().write(System.out);
    compareDoc(master.getDocumentAdapter(), this.getClass().getResourceAsStream("FormRendererTestExpectedMasterXerces.html"));
  }
}
