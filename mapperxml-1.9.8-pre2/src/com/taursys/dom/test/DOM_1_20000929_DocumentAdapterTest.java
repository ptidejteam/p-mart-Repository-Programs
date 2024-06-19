package com.taursys.dom.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.taursys.dom.DOM_1_20000929_DocumentAdapter;
import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.TidyDocumentAdapterBuilder;

/* JUnitTest case for class: com.taursys.dom.DOM_1_20000929_DocumentAdapter */
public class DOM_1_20000929_DocumentAdapterTest extends TestCase {
  DOM_1_20000929_DocumentAdapter adapter = null;

  public DOM_1_20000929_DocumentAdapterTest(String _name) {
    super(_name);
  }

  /* setUp method for test case */
  protected void setUp() {
  }

  /* tearDown method for test case */
  protected void tearDown() {
  }

  protected byte[] getEmptyXMLDocBytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<marty>");
    writer.println("</marty>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLDoc1Bytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p1\">Test</p>");
    writer.println("    <p id=\"p2\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLDoc1BytesParagraphExpected() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p1\">Marty</p>");
    writer.println("    <p id=\"p2\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLDoc1BytesParagraphExpectedForTidy() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" PUBLIC \"null\" \"null\">");
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>");
    writer.println("      Test");
    writer.println("    </title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>");
    writer.println("      Test");
    writer.println("    </h1>");
    writer.println("    <p id=\"p1\">Marty");
    writer.println("    </p>");
    writer.println("    <p id=\"p2\">");
    writer.println("      Test");
    writer.println("    </p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLDoc1BytesEmptyParagraphExpected() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p1\"></p>");
    writer.println("    <p id=\"p2\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getXHTMLBytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println(
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
        + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");

    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p1\">Test</p>");
    writer.println("    <p id=\"p2\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLNoXMLBytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p3\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLWithEndingSlash() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <br/>");
    writer.println("    <img alt=\"img\" src=\"img.gif\"/>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLWithoutEndingSlash() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
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
    writer.println("    <br/>");
    writer.println("    <img alt=\"img\" src=\"img.gif\"/>");
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
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <div id=\"headingArea\"><div id=\"headingContents\">");
    writer.println("      <table>");
    writer.println("        <tr>");
    writer.println("          <td>Stuff");
    writer.println("          </td>");
    writer.println("        </tr>");
    writer.println("      </table>");
    writer.println("    </div></div>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected Document getDocumentFromBytes(byte[] bytes) throws Exception {
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(new ByteArrayInputStream(bytes));
    parser.parse(is);
    return parser.getDocument();
  }

  protected Document getDocumentFromBytesUsingTidy(byte[] bytes) throws Exception {
    TidyDocumentAdapterBuilder builder = new TidyDocumentAdapterBuilder();
    builder.setIndentDocument(true);
    builder.setTidyMark(false);
    DocumentAdapter adapt = builder.build(new ByteArrayInputStream(bytes));
    return adapt.getDocument();
  }

  /** Returns a sorted list of attributes. */
  protected Attr[] sortAttributes(NamedNodeMap attrs) {
    int len = (attrs != null) ? attrs.getLength() : 0;
    Attr array[] = new Attr[len];
    for (int i = 0; i < len; i++) {
      array[i] = (Attr)attrs.item(i);
    }
    for (int i = 0; i < len - 1; i++) {
      String name = array[i].getNodeName();
      int index = i;
      for (int j = i + 1; j < len; j++) {
        String curName = array[j].getNodeName();
        if (curName.compareTo(name) < 0) {
          name = curName;
          index = j;
        }
      }
      if (index != i) {
        Attr temp = array[i];
        array[i] = array[index];
        array[index] = temp;
      }
    }
    return array;
  }

  protected void dump(Document doc) {
    System.out.println("Begin dump ===========================================");
    dump(doc,"");
    System.out.println("End dump ===========================================");
  }

  protected void dump(Node node, String level) {
    System.out.print(level);
    System.out.print(node.getNodeName());
    Attr attrs[] = sortAttributes(node.getAttributes());
    for (int i = 0; i < attrs.length; i++) {
      Attr attr = attrs[i];
      System.out.print(' ');
      System.out.print(attr.getNodeName());
      System.out.print("=");
      System.out.print(attr.getNodeValue());
    }
    System.out.println(" value=" + node.getNodeValue());

    Node child = node.getFirstChild();
    while (child != null) {
      dump(child, level+"  ");
      child = child.getNextSibling();
    }
  }

  protected void dumpDocInfo(Document document) {
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
    System.out.println("Doc localName=" + document.getLocalName());
    System.out.println("Doc prefix=" + document.getPrefix());
    System.out.println("Doc namespaceURI=" + document.getNamespaceURI());
    System.out.println("Doc nodeName=" + document.getNodeName());
    System.out.println("Doc documentElement.nodeName=" + document.getDocumentElement().getNodeName());
    DocumentType docType = document.getDoctype();
    System.out.println("Doc type=" + docType);
    if (docType != null) {
      System.out.println("  DocType name=" +  docType.getName());
      System.out.println("  DocType localName=" +  docType.getLocalName());
      System.out.println("  DocType namespaceURI=" +  docType.getNamespaceURI());
      System.out.println("  DocType nodeName=" +  docType.getNodeName());
      System.out.println("  DocType nodeValue=" +  docType.getNodeValue());
      System.out.println("  DocType prefix=" +  docType.getPrefix());
      System.out.println("  DocType publicId=" +  docType.getPublicId());
      System.out.println("  DocType systemId=" +  docType.getSystemId());
    }
  }

  protected void parseWrite(byte[] bytes) throws Exception {
    parseWrite(bytes, bytes);
  }

  protected void parseWrite(byte[] sourceBytes, byte[] expectedBytes)
      throws Exception {
    Document doc = getDocumentFromBytes(sourceBytes);
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    adapter.write(bos);
    BufferedReader resultsReader = new BufferedReader(
      new InputStreamReader(new ByteArrayInputStream(bos.toByteArray())));
    BufferedReader expectedReader = new BufferedReader(
      new InputStreamReader(new ByteArrayInputStream(expectedBytes)));
    // Check each line
    String expected;
    int i = 0;
    while ((expected = expectedReader.readLine()) != null) {
      assertEquals("Contents line #" + i, expected, resultsReader.readLine());
      i++;
    }
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

  public void testCreate() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    assertEquals("Document", doc, adapter.getDocument());
    assertNotNull("p2 Element not found", adapter.getElementById("p2"));
  }

  /* test for method setDocument(..) */
  public void testSetDocument() throws Exception {
    Document doc = getDocumentFromBytes(getEmptyXMLDocBytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    doc = getDocumentFromBytes(getHTMLDoc1Bytes());
    adapter.setDocument(doc);
    assertEquals("Document", doc, adapter.getDocument());
    assertNotNull("p2 Element not found", adapter.getElementById("p2"));
  }
  
  public void testSetElementTextValue() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    adapter.setElementText("p1", "Marty");
    compareDoc(adapter, getHTMLDoc1BytesParagraphExpected());
  }

  public void testGetElementTextValue() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    String results = DOM_1_20000929_DocumentAdapter.getElementText(adapter.getElementById("p1"));
    assertEquals("Value", "Test", results);
  }

  public void testGetElementTextValueUsingTidy() throws Exception {
    Document doc = getDocumentFromBytesUsingTidy(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    String results = DOM_1_20000929_DocumentAdapter.getElementText(adapter.getElementById("p1"));
    assertEquals("Value", "\n      Test", results);
  }

  public void testSetElementTextValueUsingTidy() throws Exception {
    Document doc = getDocumentFromBytesUsingTidy(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    adapter.setElementText("p1", "Marty");
    compareDoc(adapter, getHTMLDoc1BytesParagraphExpectedForTidy());
  }

  public void testSetElementNullValue() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLDoc1Bytes());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    adapter.setElementText("p1", null);
    compareDoc(adapter, getHTMLDoc1BytesEmptyParagraphExpected());
  }

  public void testSetElementAddTextValue() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLDoc1BytesEmptyParagraphExpected());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
    adapter.setElementText("p1", "Marty");
    compareDoc(adapter, getHTMLDoc1BytesParagraphExpected());
  }

  public void testImportContentsUsingXerces() throws Exception {
    Document master = getDocumentFromBytes(getHTMLMasterDocumentWithSimpleTextDiv());
    adapter = new DOM_1_20000929_DocumentAdapter(master);
    Document sub = getDocumentFromBytes(getHTMLSubDocument());
    DocumentAdapter subadapter = new DOM_1_20000929_DocumentAdapter(sub);
    adapter.importContents(subadapter,"headingContents", "headingArea");
    adapter.write(System.out);
    compareDoc(adapter, getExpectedHTMLMasterDocumentWithSimpleTextDiv());
  }

  /* test for method write(..) */
  public void testWrite_HTML() throws Exception {
    parseWrite(getHTMLDoc1Bytes());
  }

  /* test for method write(..) */
  public void testWrite_XHTML() throws Exception {
    parseWrite(getXHTMLBytes());
  }

  /* test for method write(..) */
  public void testWrite_HTMLNoXML() throws Exception {
    parseWrite(getHTMLNoXMLBytes());
  }

  /* test for method write(..) */
  public void testWrite_HTMLWithoutEndingSlash() throws Exception {
    parseWrite(getHTMLWithEndingSlash(), getHTMLWithoutEndingSlash());
  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = {DOM_1_20000929_DocumentAdapterTest.class.getName()};
    junit.swingui.TestRunner.main(testCaseList);
  }
}
