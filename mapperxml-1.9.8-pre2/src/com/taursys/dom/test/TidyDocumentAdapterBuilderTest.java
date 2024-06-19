/*
 * Created on Jul 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.taursys.dom.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilderException;
import com.taursys.dom.TidyDocumentAdapterBuilder;

/**
 * @author marty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TidyDocumentAdapterBuilderTest extends TestCase {

  /**
   * @param arg0
   */
  public TidyDocumentAdapterBuilderTest(String arg0) {
    super(arg0);
  }

  private TidyDocumentAdapterBuilder builder;

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    builder = new TidyDocumentAdapterBuilder();
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  protected byte[] getHTMLWithScriptForTidy() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("    <script id=\"testscript\" language=\"JavaScript\" type=\"text/JavaScript\">");
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
    writer.println("    <p>This is a test paragraph");
    writer.println("       broken over two lines</p>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLWithScriptExpectedForTidy() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" PUBLIC \"null\" \"null\">");
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>");
    writer.println("      Test");
    writer.println("    </title>");
    writer.println("    <script id=\"testscript\" language=\"JavaScript\" type=\"text/JavaScript\">");
    writer.println("      ");
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
    writer.println("    <h1>");
    writer.println("      Test");
    writer.println("    </h1>");
    writer.println("    <p>");
    writer.println("      This is a test paragraph broken over two lines");
    writer.println("    </p>");
    writer.println("    <br>");
    writer.println("    ");
    writer.println("    <img alt=\"img\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected void compareDoc(DocumentAdapter adapter, byte[] expectedBytes) throws IOException
      {
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

  /*
   * Class under test for DocumentAdapter build(InputStream)
   */
  public void testBuild() throws IOException {
    try {
      builder.setOnlyErrors(true);
      builder.setErrout(new PrintWriter(new ByteArrayOutputStream()));
      builder.setTidyMark(false);
      DocumentAdapter da =
        builder.build(new ByteArrayInputStream(getHTMLWithScriptForTidy()));
      System.out.println(da);
      compareDoc(da, getHTMLWithScriptExpectedForTidy());
    } catch (DocumentAdapterBuilderException e) {
      e.printStackTrace();
      fail("Exception: " + e.getMessage());
    }
  }

}
