package com.taursys.dom.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.TidyDocumentAdapterBuilder;

/* JUnitTest case for class: com.taursys.dom.DOM_1_20000929_DocumentAdapter */
public class Tidy_DOM_2_20001113_DocumentAdapterTest extends TestCase {
  DocumentAdapter adapter = null;

  public Tidy_DOM_2_20001113_DocumentAdapterTest(String _name) {
    super(_name);
  }

  /* setUp method for test case */
  protected void setUp() {
  }

  /* tearDown method for test case */
  protected void tearDown() {
  }

  protected byte[] getHTMLMasterDocumentWithSimpleTextDiv() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <div id=\"headingArea\">Test</div>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLSubDocument() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <div id=\"headingContents\">");
    writer.println("      <table>");
    writer.println("        <tr>");
    writer.println("          <td>Stuff");
    writer.println("          </td>");
    writer.println("        </tr>");
    writer.println("      </table>");
    writer.println("    </div>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getExpectedHTMLMasterDocumentWithSimpleTextDiv() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" PUBLIC \"null\" \"null\">");
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>");
    writer.println("      Test");
    writer.println("    </title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <div id=\"headingArea\"><div id=\"headingContents\">");
    writer.println("      <table>");
    writer.println("        <tr>");
    writer.println("          <td>");
    writer.println("            Stuff");
    writer.println("          </td>");
    writer.println("        </tr>");
    writer.println("      </table>");
    writer.println("    </div>");
    writer.println("  </div>");
    writer.println("    <h1>");
    writer.println("      Test");
    writer.println("    </h1>");
    writer.println("    <br>");
    writer.println("    ");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected DocumentAdapter getDocumentAdapterFromBytesUsingTidy(byte[] bytes) throws Exception {
    TidyDocumentAdapterBuilder builder = new TidyDocumentAdapterBuilder();
    builder.setIndentDocument(true);
    builder.setTidyMark(false);
    DocumentAdapter adapt = builder.build(new ByteArrayInputStream(bytes));
    System.out.println("DocumentAdapterClass: " + adapt);
    return adapt;
  }

  protected void compareDoc(DocumentAdapter adapter, byte[] expectedBytes)
      throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    adapter.write(bos);
    BufferedReader resultsReader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bos.toByteArray())));
    BufferedReader expectedReader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(expectedBytes)));
    // Check each line
    String expected;
    int i = 0;
    while ((expected = expectedReader.readLine()) != null) {
      assertEquals("Contents line #" + i, expected, resultsReader.readLine());
      i++;
    }
  }

  // ========================================================================
  //                                Tests
  // ========================================================================

  public void testImportContentsUsingTidy() throws Exception {
    adapter = getDocumentAdapterFromBytesUsingTidy(getHTMLMasterDocumentWithSimpleTextDiv());
    DocumentAdapter subadapter = getDocumentAdapterFromBytesUsingTidy(getHTMLSubDocument());
    adapter.importContents(subadapter,"headingContents", "headingArea");
    adapter.write(System.out);
    compareDoc(adapter, getExpectedHTMLMasterDocumentWithSimpleTextDiv());
  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = {Tidy_DOM_2_20001113_DocumentAdapterTest.class.getName()};
    junit.swingui.TestRunner.main(testCaseList);
  }
}
