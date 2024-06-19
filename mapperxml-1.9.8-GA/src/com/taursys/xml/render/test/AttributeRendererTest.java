/**
 * AttributeRendererTest
 *
 * Copyright (c) 2002
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.xml.render.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.taursys.dom.DocumentAdapter;
import com.taursys.xml.Attribute;
import com.taursys.xml.Button;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.Form;
import com.taursys.xml.TextField;
import com.taursys.xml.event.RenderDispatcher;
import com.taursys.xml.event.RenderEvent;

/**
 * Unit test for AttributeRenderer
 * @author marty
 */
public class AttributeRendererTest extends TestCase {

  private Form form;
  private DocumentElement element;
  private Attribute attribute;
  private TextField textField;

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    form = new Form();
    form.setDocument(getDocumentFromBytes(getHTMLInitial()));
    element = new DocumentElement();
    element.setId("myImage");
    attribute = new Attribute();
    textField = new TextField();
    textField.setId("myHeading");
  }

  /**
   * Constructor for AttributeRendererTest.
   * @param arg0
   */
  public AttributeRendererTest(String arg0) {
    super(arg0);
  }

  protected byte[] getHTMLInitial() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br/>");
    writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\"/>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLInitialWithButton() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br/>");
    writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\"/>");
    writer.println("    <input type=\"submit\" id=\"myButton\" name=\"action\" value=\"PressMe\"/>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLResultsWithButton() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\">");
    writer.println("    <input id=\"myButton\" name=\"action\" style=\"cool\" type=\"submit\" value=\"PressMe\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLExpectedDefault() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLExpectedDefaultWithValue() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\" value=\"ABC\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLExpectedClassWithValue() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" class=\"ABC\" id=\"myImage\" src=\"img.gif\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLExpectedSrcRemoved() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1 id=\"myHeading\">Test</h1>");
    writer.println("    <br>");
    writer.println("    <img alt=\"img\" id=\"myImage\">");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected byte[] getHTMLExpectedStyledHeading() {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(bos);
      writer.println("<html>");
      writer.println("  <head>");
      writer.println("    <title>Test</title>");
      writer.println("  </head>");
      writer.println("  <body>");
      writer.println("    <h1 id=\"myHeading\" style=\"color: red;\">Testing</h1>");
      writer.println("    <br>");
      writer.println("    <img alt=\"img\" id=\"myImage\" src=\"img.gif\">");
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

  protected void renderAndMeasure(Form form, byte[] expectedBytes)
      throws Exception {
    ((RenderDispatcher)form.getDispatcher(
        RenderEvent.class.getName())).dispatch();
    DocumentAdapter adapter = form.getDocumentAdapter();
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

  public void testRenderDefault() throws Exception {
    element.addAttribute(attribute);
    form.add(element);
    renderAndMeasure(form, getHTMLExpectedDefault());
  }

  public void testRenderDefaultWithValue() throws Exception {
    attribute.setText("ABC");
    element.addAttribute(attribute);
    form.add(element);
    renderAndMeasure(form, getHTMLExpectedDefaultWithValue());
  }

  public void testRenderClassWithValue() throws Exception {
    attribute.setAttributeName("class");
    attribute.setText("ABC");
    element.addAttribute(attribute);
    form.add(element);
    renderAndMeasure(form, getHTMLExpectedClassWithValue());
  }

  public void testRenderSrcRemove() throws Exception {
    attribute.setAttributeName("src");
    attribute.setText("ABC");
    attribute.setVisible(false);
    element.addAttribute(attribute);
    form.add(element);
    renderAndMeasure(form, getHTMLExpectedSrcRemoved());
  }
  
  public void testRenderSrcBlank() throws Exception {
    attribute.setAttributeName("src");
    attribute.setText("");
    attribute.setVisible(true);
    element.addAttribute(attribute);
    form.add(element);
    renderAndMeasure(form, getHTMLExpectedSrcRemoved());
  }
  
  public void testRenderTextFieldWithValue() throws Exception {
    attribute.setAttributeName("style");
    attribute.setText("color: red;");
    form.add(textField);
    textField.addAttribute(attribute);
    textField.setText("Testing");
    renderAndMeasure(form, getHTMLExpectedStyledHeading());
  }

  public void testRenderTextFieldWithValueAddedEarly() throws Exception {
    attribute.setAttributeName("style");
    attribute.setText("color: red;");
    textField.addAttribute(attribute);
    textField.setText("Testing");
    form.add(textField);
    renderAndMeasure(form, getHTMLExpectedStyledHeading());
  }
  
  public void testRenderButtonWithStyle() throws Exception {
    form.setDocument(getDocumentFromBytes(getHTMLInitialWithButton()));
    Button button = new Button();
    button.setId("myButton");
    button.createAttribute("style");
    button.setAttributeText("style","cool");
    form.add(button);
    renderAndMeasure(form, getHTMLResultsWithButton());
  }

}
