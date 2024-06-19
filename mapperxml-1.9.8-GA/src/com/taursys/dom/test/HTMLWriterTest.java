package com.taursys.dom.test;

import junit.framework.TestCase;
import com.taursys.dom.*;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import java.io.*;

import org.w3c.dom.*;

/* JUnitTest case for class: com.taursys.dom.HTMLWriter */
public class HTMLWriterTest extends TestCase {
  DOM_1_20000929_DocumentAdapter adapter = null;

  public HTMLWriterTest(String _name) {
    super(_name);
  }

  /* setUp method for test case */
  protected void setUp() {
  }

  /* tearDown method for test case */
  protected void tearDown() {
  }

  protected byte[] getHTMLWithScriptForXerces() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("    <script language=\"JavaScript\" type=\"text/JavaScript\">");
    writer.println("    <!--");
    writer.println("");
    writer.println("    function MM_swapTextCamera() {");
    writer.println("      if (document.getElementById(\"Camera\")) {");
    writer.println("        var txt = document.createTextNode(\"Camera\");");
    writer.println("        var elem = document.getElementById(\"SiteName\");");
    writer.println("        var oldTxt = elem.replaceChild(txt, elem.firstChild);");
    writer.println("        document.getElementById(\"ImageWeather\").style.visibility = \"hidden\";");
    writer.println("        document.getElementById(\"ImageWeather\").style.display = \"none\";");
    writer.println("        document.getElementById(\"ImageCamera\").style.visibility = \"visible\";");
    writer.println("        document.getElementById(\"ImageCamera\").style.display = \"inline\";");
    writer.println("        document.getElementById(\"cameraInstructions\").style.visibility = \"visible\";");
    writer.println("        document.getElementById(\"cameraInstructions\").style.display = \"inline\";");
    writer.println("        document.getElementById(\"weatherInstructions\").style.visibility = \"hidden\";");
    writer.println("        document.getElementById(\"weatherInstructions\").style.display = \"none\";");
    writer.println("      }");
    writer.println("    }");
    writer.println("    //      -->");
    writer.println("    </script>");
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

  protected byte[] getHTMLWithScriptExpectedForXerces() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("    <script language=\"JavaScript\" type=\"text/JavaScript\">");
    writer.println("    <!-- ");
    writer.println("");
    writer.println("    function MM_swapTextCamera() {");
    writer.println("      if (document.getElementById(\"Camera\")) {");
    writer.println("        var txt = document.createTextNode(\"Camera\");");
    writer.println("        var elem = document.getElementById(\"SiteName\");");
    writer.println("        var oldTxt = elem.replaceChild(txt, elem.firstChild);");
    writer.println("        document.getElementById(\"ImageWeather\").style.visibility = \"hidden\";");
    writer.println("        document.getElementById(\"ImageWeather\").style.display = \"none\";");
    writer.println("        document.getElementById(\"ImageCamera\").style.visibility = \"visible\";");
    writer.println("        document.getElementById(\"ImageCamera\").style.display = \"inline\";");
    writer.println("        document.getElementById(\"cameraInstructions\").style.visibility = \"visible\";");
    writer.println("        document.getElementById(\"cameraInstructions\").style.display = \"inline\";");
    writer.println("        document.getElementById(\"weatherInstructions\").style.visibility = \"hidden\";");
    writer.println("        document.getElementById(\"weatherInstructions\").style.display = \"none\";");
    writer.println("      }");
    writer.println("    }");
    writer.println("    //      -->");
    writer.println("");
    writer.println("    </script>");
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

  protected Document getDocumentFromBytes(byte[] bytes) throws Exception {
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(new ByteArrayInputStream(bytes));
    parser.parse(is);
    return parser.getDocument();
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
    String value = node.getNodeValue();
    if (value == null) {
      value = "null";
    } else if (value.equals("")) {
      value = "empty-string";
    } else {
      value = "[" + value + "]";
    }
    System.out.println(" VALUE=" + value);

    Node child = node.getFirstChild();
    while (child != null) {
      dump(child, level+"  ");
      child = child.getNextSibling();
    }
  }

  protected void parseWrite(byte[] bytes) throws Exception {
    parseWrite(bytes, bytes);
  }

  protected void parseWrite(byte[] sourceBytes, byte[] expectedBytes)
      throws Exception {
    Document doc = getDocumentFromBytes(sourceBytes);
    parseWrite(doc, expectedBytes);
  }

  protected void parseWrite(Document doc, byte[] expectedBytes)
      throws Exception {
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
  }

  protected void parseWrite(DocumentAdapter adapter, byte[] expectedBytes)
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

  /* test for method write(..) */
  public void testWrite_HTMLWithScriptXerces() throws Exception {
    Document doc = getDocumentFromBytes(getHTMLWithScriptForXerces());
    adapter = new DOM_1_20000929_DocumentAdapter(doc);
//    dump(doc);
//    System.out.println("=====================================");
//    adapter.write(System.out);
    parseWrite(adapter, getHTMLWithScriptExpectedForXerces());
  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = {HTMLWriterTest.class.getName()};
    junit.swingui.TestRunner.main(testCaseList);
  }
}
