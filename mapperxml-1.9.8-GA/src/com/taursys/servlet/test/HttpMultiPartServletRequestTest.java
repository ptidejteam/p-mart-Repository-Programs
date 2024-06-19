/**
 * JUnitTest case
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
package com.taursys.servlet.test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;

import junit.framework.TestCase;

import com.taursys.servlet.HttpMultiPartServletRequest;
import com.taursys.servlet.MultiPartRequestContentException;
import com.taursys.servlet.MultiPartRequestSizeException;

/*
 * Reference Information from W3C about multipart/form-data format

The following example illustrates "multipart/form-data" encoding. Suppose we 
have the following form:

 <FORM action="http://server.com/cgi/handle"
       enctype="multipart/form-data"
       method="post">
   <P>
   What is your name? <INPUT type="text" name="submit-name"><BR>
   What files are you sending? <INPUT type="file" name="files"><BR>
   <INPUT type="submit" value="Send"> <INPUT type="reset">
 </FORM>

If the user enters "Larry" in the text input, and selects the text file 
"file1.txt", the user agent might send back the following data:

   Content-Type: multipart/form-data; boundary=AaB03x

   --AaB03x
   Content-Disposition: form-data; name="submit-name"

   Larry
   --AaB03x
   Content-Disposition: form-data; name="files"; filename="file1.txt"
   Content-Type: text/plain

   ... contents of file1.txt ...
   --AaB03x--

If the user selected a second (image) file "file2.gif", the user agent 
might construct the parts as follows:

   Content-Type: multipart/form-data; boundary=AaB03x

   --AaB03x
   Content-Disposition: form-data; name="submit-name"

   Larry
   --AaB03x
   Content-Disposition: form-data; name="files"
   Content-Type: multipart/mixed; boundary=BbC04y

   --BbC04y
   Content-Disposition: file; filename="file1.txt"
   Content-Type: text/plain

   ... contents of file1.txt ...
   --BbC04y
   Content-Disposition: file; filename="file2.gif"
   Content-Type: image/gif
   Content-Transfer-Encoding: binary

   ...contents of file2.gif...
   --BbC04y--
   --AaB03x--

 */

/**
 * JUnitTest case for class: HttpMultiPartServletRequest 
 */
public class HttpMultiPartServletRequestTest extends TestCase {
  public static final String TEST_BOUNDARY =
      "---------------------------14693480941749698586855636226";
  public static final String TEST_START_BOUNDARY = "--" + TEST_BOUNDARY;
  public static final String TEST_CONTENT_TYPE = "multipart/form-data; boundary=" +
      TEST_BOUNDARY;

  public HttpMultiPartServletRequestTest(String _name) {
    super(_name);
  }

  /* setUp method for test case */
  protected void setUp() {
  }

  /* tearDown method for test case */
  protected void tearDown() {
  }

  /**
   * Test for boundary detection and setup with a clean request
   */
  public void testSetupBoundaryGoodContentType() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType(TEST_CONTENT_TYPE);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    assertEquals(TEST_BOUNDARY, parser.getBoundary());
  }

  /**
   * Test for boundary detection and setup with a request with extra spacing
   */
  public void testSetupBoundaryExtraSpaceContentType() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType(
        "  multipart/form-data ;   boundary =   " + TEST_BOUNDARY);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    assertEquals(TEST_BOUNDARY, parser.getBoundary());
  }

  /**
   * Test for boundary detection and setup with a bad content type
   */
  public void testSetupBoundaryBadContentType() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    String contentType = "multipart/x-www-form-urlencoded; boundary=" + TEST_BOUNDARY;
    request.setContentType(contentType);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.setupBoundary();
      fail("Expected Servlet Exception");
    } catch (MultiPartRequestContentException ex) {
      assertEquals("Content type is not multipart/form-data: contentType=" + contentType, ex.getMessage());
    }
  }

  /**
   * Test for boundary detection and setup with no boundary
   */
  public void testSetupBoundaryNoBoundary() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType("multipart/form-data");
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.setupBoundary();
      fail("Expected Servlet Exception");
    } catch (MultiPartRequestContentException ex) {
      assertEquals("Content type is missing boundary attribute: contentType=multipart/form-data", ex.getMessage());
    }
  }

  /**
   * Test for boundary detection and setup with different attribute
   */
  public void testSetupBoundaryDifferentAttribute() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType("multipart/form-data; something=xxx");
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.setupBoundary();
      fail("Expected Servlet Exception");
    } catch (MultiPartRequestContentException ex) {
      assertEquals("Content type is missing boundary attribute: contentType=multipart/form-data; something=xxx", ex.getMessage());
    }
  }

  /**
   * Test for boundary detection and setup with Empty content type
   */
  public void testSetupBoundaryEmpty() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType("");
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.setupBoundary();
      fail("Expected Servlet Exception");
    } catch (MultiPartRequestContentException ex) {
      assertEquals("Content type is not multipart/form-data: contentType=", ex.getMessage());
    }
  }

  /**
   * Test for boundary detection and setup with NULL content type or boundary
   */
  public void testSetupBoundaryNull() throws Exception {
    TestHttpServletRequest request = new TestHttpServletRequest();
    request.setContentType(null);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.setupBoundary();
      fail("Expected Servlet Exception");
    } catch (MultiPartRequestContentException ex) {
      assertEquals("Unknown Content Type", ex.getMessage());
    }
  }

  /**
   * Test for processing block (not last) checking for Bad/NULL Parameter
   */
  public void testProcessBlockBadParameter() throws Exception {
    String testRequest =
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();

    parser.processBlock();
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
    assertNull("Retrieving invalid parameter", parser.getParameter("bad"));
    assertNull("Retrieving invalid parameter String array", parser.getParameterValues("bad"));
    assertNull("Retrieving invalid parameter Object array", parser.getParameterByteArrays("bad"));
  }

  /**
   * Test for processing block (not last) checking for Parameter
   */
  public void testProcessBlockNotLastParameter() throws Exception {
    String testRequest =
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();

    parser.processBlock();
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
    Enumeration e = parser.getParameterNames();
    while (e.hasMoreElements()) {
      if (((String)e.nextElement()).equals("email"))
        return; //found
    }
    fail("Email parameter is not present");
  }

  /**
   * Test for processing block (not last) checking for Content
   */
  public void testProcessBlockNotLastContent() throws Exception {
    String testRequest =
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // Do test
    parser.processBlock();
    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
  }

  /**
   * Test for processing block (last) checking for Content
   */
  public void testProcessBlockLastContent() throws Exception {
    String testRequest =
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // Do test
    parser.processBlock();
    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertTrue("Expected IS end of data", parser.isEndOfData());
  }

  /**
   * Test for processing block  with an Empty file
   */
  public void testProcessBlockEmptyFile() throws Exception {
    byte[] byteData = new byte[] {};
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";

    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');
    bos.write(TEST_START_BOUNDARY.getBytes());
    bos.write('\r');
    bos.write('\n');

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // run test
    parser.processBlock();
    // measure
    assertEquals("", parser.getParameter("theFile_FileName"));
    assertEquals("application/octet-stream", parser.getParameter("theFile_ContentType"));
    assertEquals("", parser.getParameter("theFile"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
    byte[] byteResult = parser.getParameterByteArray("theFile_ByteArray");
    assertEquals("Byte Array Size", byteData.length, byteResult.length);
    for (int i = 0; i < byteData.length; i++) {
      assertEquals("Byte @" + i, byteData[i], byteResult[i]);
    }
  }

  /**
   * Test for processing block  with an Filename containing special
   * characters: " :;=".
   */
  public void testProcessBlockFilenameSpecialChars() throws Exception {
    byte[] byteData = new byte[] {};
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String fileName = "My: File; =Name.xls";
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"" + fileName + "\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";

    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');
    bos.write(TEST_START_BOUNDARY.getBytes());
    bos.write('\r');
    bos.write('\n');

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // run test
    parser.processBlock();
    // measure
    assertEquals(fileName, parser.getParameter("theFile_FileName"));
  }

  /**
   * Test for processing block  with a small Binary file
   */
  public void testProcessBlockSmallBinaryFile() throws Exception {
    byte[] byteData = new byte[] {
      20,0,55,-127,115,22,15,12,10,-102,26,-98,82,-126,-127,0,0,10,10,0
    };
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";

    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');
    bos.write(TEST_START_BOUNDARY.getBytes());
    bos.write('\r');
    bos.write('\n');

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // run test
    parser.processBlock();
    // measure
    assertEquals("", parser.getParameter("theFile_FileName"));
    assertEquals("application/octet-stream", parser.getParameter("theFile_ContentType"));
    assertEquals(new String(byteData), parser.getParameter("theFile"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
    byte[] byteResult = parser.getParameterByteArray("theFile_ByteArray");
    assertEquals("Byte Array Size", byteData.length, byteResult.length);
    for (int i = 0; i < byteData.length; i++) {
      assertEquals("Byte @" + i, byteData[i], byteResult[i]);
    }
  }

  /**
   * Test for processing block with a large Binary file
   */
  public void testProcessBlockLargeBinaryFile() throws Exception {
    byte[] byteData = new byte[250000];
    Random random = new Random(6995);
    random.nextBytes(byteData);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";

    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');
    bos.write(TEST_START_BOUNDARY.getBytes());
    bos.write('\r');
    bos.write('\n');

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();
    // run test
    parser.processBlock();
    // measure
    assertEquals("", parser.getParameter("theFile_FileName"));
    assertEquals("application/octet-stream", parser.getParameter("theFile_ContentType"));
    assertEquals(new String(byteData), parser.getParameter("theFile"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
    byte[] byteResult = parser.getParameterByteArray("theFile_ByteArray");
    assertEquals("Byte Array Size", byteData.length, byteResult.length);
    for (int i = 0; i < byteData.length; i++) {
      assertEquals("Byte @" + i, byteData[i], byteResult[i]);
    }
  }

  /**
   * Test for processing block with small text/plain file
   */
  public void testProcessBlockNotLastFilename() throws Exception {
    String testValue =
        "R & B Project\r\n" +
        "-------------\r\n" +
        "\r\n" +
        "History of project\r\n" +
        "        Development of SRS TO-02-0259 $34K\r\n" +
        "        System Design & Mentoring TO-02-0440 $40K\r\n" +
        "        Implementation & Mentoring Phase I TO-02-0491 $51K\r\n" +
        "        Implementation & Mentoring Phase II TO-02-0513 $55K\r\n" +
        "   Future of Project\r\n" +
        "        Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)\r\n" +
        "\r\n";
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"RBProjectPresentation.txt\"\r\n" +
      "Content-Type: text/plain\r\n" +
      "\r\n" +
      testValue + "\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();

    parser.processBlock();
    assertEquals("RBProjectPresentation.txt", parser.getParameter("theFile_FileName"));
    assertEquals("text/plain", parser.getParameter("theFile_ContentType"));
    assertEquals(testValue, parser.getParameter("theFile"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
  }

  /**
   * Test for processing block with small text/html file
   */
  public void testProcessBlockSmallHtmlFile() throws Exception {
    String testValue =
        "<html>\r\n" +
        "<head><title>Retirement and Benefits SBS Project</title></head>\r\n" +
        "<body>\r\n" +
        "  <h1>History of project</h1>\r\n" +
        "  <ul>\r\n" +
        "    <li>Development of SRS TO-02-0259 $34K</li>\r\n" +
        "    <li>System Design & Mentoring TO-02-0440 $40K</li>\r\n" +
        "    <li>Implementation & Mentoring Phase I TO-02-0491 $51K</li>\r\n" +
        "    <li>Implementation & Mentoring Phase II TO-02-0513 $55K</li>\r\n" +
        "  </ul>\r\n" +
        "  <h1>Future of Project</h1>\r\n" +
        "  <ul>\r\n" +
        "    <li>Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)</li>\r\n" +
        "  </ul>\r\n" +
        "</body>\r\n" +
        "</html>\r\n" +
        "\r\n";
    String testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"RBProjectPresentation.html\"\r\n" +
      "Content-Type: text/html\r\n" +
      "\r\n" +
      testValue + "\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    parser.setupBoundary();

    parser.processBlock();
    assertEquals("RBProjectPresentation.html", parser.getParameter("theFile_FileName"));
    assertEquals("text/html", parser.getParameter("theFile_ContentType"));
    assertEquals(testValue, parser.getParameter("theFile"));
    assertTrue("Expected NOT end of data", !parser.isEndOfData());
  }

  /**
   * Test for ParseRequest for multiple items with Text File
   */
  public void testParseRequest() throws Exception {
    String testValue =
        "R & B Project\r\n" +
        "-------------\r\n" +
        "\r\n" +
        "History of project\r\n" +
        "        Development of SRS TO-02-0259 $34K\r\n" +
        "        System Design & Mentoring TO-02-0440 $40K\r\n" +
        "        Implementation & Mentoring Phase I TO-02-0491 $51K\r\n" +
        "        Implementation & Mentoring Phase II TO-02-0513 $55K\r\n" +
        "   Future of Project\r\n" +
        "        Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)\r\n" +
        "\r\n";
    String testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"RBProjectPresentation.txt\"\r\n" +
      "Content-Type: text/plain\r\n" +
      "\r\n" +
      testValue + "\r\n" +
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"action\"\r\n" +
      "\r\n" +
      "Send\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);

    parser.parseRequest();
    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertEquals("RBProjectPresentation.txt", parser.getParameter("theFile_FileName"));
    assertEquals("text/plain", parser.getParameter("theFile_ContentType"));
    assertEquals(testValue, parser.getParameter("theFile"));
    assertEquals("Send", parser.getParameter("action"));
    assertTrue("Expected end of data", parser.isEndOfData());
  }

  /**
   * Test for ParseRequest for Text file with NO extension. Typically
   * the browser will report Content-Type: application/octet-stream.
   */
  public void testParseRequestNoFileExtension() throws Exception {
    String testValue =
        "R & B Project\r\n" +
        "-------------\r\n" +
        "\r\n" +
        "History of project\r\n" +
        "        Development of SRS TO-02-0259 $34K\r\n" +
        "        System Design & Mentoring TO-02-0440 $40K\r\n" +
        "        Implementation & Mentoring Phase I TO-02-0491 $51K\r\n" +
        "        Implementation & Mentoring Phase II TO-02-0513 $55K\r\n" +
        "   Future of Project\r\n" +
        "        Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)\r\n" +
        "\r\n";
    String testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"RBProjectPresentation\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n" +
      testValue + "\r\n" +
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"action\"\r\n" +
      "\r\n" +
      "Send\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);

    parser.parseRequest();

    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertEquals("RBProjectPresentation", parser.getParameter("theFile_FileName"));
    assertEquals("application/octet-stream", parser.getParameter("theFile_ContentType"));
    assertEquals(testValue, parser.getParameter("theFile"));
    assertEquals("Send", parser.getParameter("action"));
    assertTrue("Expected end of data", parser.isEndOfData());
  }

  /**
   * Tests all the getParameterX methods with a text file
   * Invokes all methods for all parameter names
   * Only invokes method - does not measure results
   * 
   * @throws Exception
   */
  public void testGetParameterMethodsWithTextFile() throws Exception {
    String testValue = "R & B Project\r\n"
        + "-------------\r\n"
        + "\r\n"
        + "History of project\r\n"
        + "        Development of SRS TO-02-0259 $34K\r\n"
        + "        System Design & Mentoring TO-02-0440 $40K\r\n"
        + "        Implementation & Mentoring Phase I TO-02-0491 $51K\r\n"
        + "        Implementation & Mentoring Phase II TO-02-0513 $55K\r\n"
        + "   Future of Project\r\n"
        + "        Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)\r\n"
        + "\r\n";
    String testRequest = TEST_START_BOUNDARY
        + "\r\n"
        + "Content-Disposition: form-data; name=\"email\"\r\n"
        + "\r\n"
        + "anyone@anywhere.com\r\n"
        + TEST_START_BOUNDARY
        + "\r\n"
        + "Content-Disposition: form-data; name=\"theFile\"; filename=\"RBProjectPresentation\"\r\n"
        + "Content-Type: application/octet-stream\r\n" + "\r\n" + testValue
        + "\r\n" + TEST_START_BOUNDARY + "\r\n"
        + "Content-Disposition: form-data; name=\"action\"\r\n" + "\r\n"
        + "Send\r\n" + TEST_START_BOUNDARY + "--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(
        request);

    parser.parseRequest();
    Enumeration names = parser.getParameterNames();
    while (names.hasMoreElements()) {
      String key = (String) names.nextElement();
      parser.getParameterValues(key);
      parser.getParameter(key);
      parser.getParameterByteArray(key);
      parser.getParameterByteArrays(key);
    }
  }
  
  /**
   * Tests all the getParameterX methods with a text file Invokes all methods
   * for all parameter names Only invokes method - does not measure results
   * 
   * @throws Exception
   */
  public void testGetParameterMethodsWithBinaryFile() throws Exception {
    byte[] byteData = new byte[] { 20, 0, 55, -127, 115, 22, 15, 12, 10, -102,
        26, -98, 82, -126, -127, 0, 0, 10, 10, 0 };
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest = TEST_START_BOUNDARY
    + "\r\n"
    + "Content-Disposition: form-data; name=\"theFile\"; filename=\"\"\r\n"
        + "Content-Type: application/octet-stream\r\n" + "\r\n";

    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');
    bos.write(TEST_START_BOUNDARY.getBytes());
    bos.write('-');
    bos.write('-');
    bos.write('\r');
    bos.write('\n');

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(
        request);

    parser.parseRequest();
    Enumeration names = parser.getParameterNames();
    while (names.hasMoreElements()) {
      String key = (String) names.nextElement();
      parser.getParameterValues(key);
      parser.getParameter(key);
      parser.getParameterByteArray(key);
      parser.getParameterByteArrays(key);
    }
  }

  /**
   * Test for ParseRequest and exceed maximum line length
   */
  public void testParseRequestExceedMaxLineLength() throws Exception {
    char[] longLine = new char[4099];
    Arrays.fill(longLine, 'x');
    longLine[4097] = '\r';
    longLine[4098] = '\n';
    String testValue = String.valueOf(longLine);
    testValue += testValue + testValue + testValue + testValue + "\r\n";
    String testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"LongLines.txt\"\r\n" +
      "Content-Type: text/plain\r\n" +
      "\r\n" +
      testValue + "\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    try {
      parser.parseRequest();
      fail("Expected to exceed max line length");
    } catch (MultiPartRequestSizeException ex) {
      assertEquals("Exception message",
          "Maximum line length exceeded for value named: theFile",
          ex.getMessage());
    }
  }

  /**
   * Test for ParseRequest exceeding maximum binary file size.
   */
  public void testParseRequestExceedMaxFileSize() throws Exception {
    byte[] byteData = new byte[64000];
    Random random = new Random(6995);
    random.nextBytes(byteData);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest =
      TEST_START_BOUNDARY + "\r\n";
    bos.write(testRequest.getBytes());

    testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"data.dat\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";
    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');

    testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"action\"\r\n" +
      "\r\n" +
      "Send\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    bos.write(testRequest.getBytes());

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);
    // reduce max file size
    parser.setMaxFileSize(63999);
    try {
      parser.parseRequest();
      fail("Expected to exceed max file size");
    } catch (MultiPartRequestSizeException ex) {
      assertEquals("Exception message",
          "Maximum file size exceeded for value named: theFile",
          ex.getMessage());
    }
  }

  /**
   * Test for ParseRequest for multiple items with Binary File
   */
  public void testParseRequestBinaryFile() throws Exception {
    byte[] byteData = new byte[64000];
    Random random = new Random(6995);
    random.nextBytes(byteData);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"email\"\r\n" +
      "\r\n" +
      "anyone@anywhere.com\r\n" +
      TEST_START_BOUNDARY + "\r\n";
    bos.write(testRequest.getBytes());

    testRequest =
      "Content-Disposition: form-data; name=\"theFile\"; filename=\"data.dat\"\r\n" +
      "Content-Type: application/octet-stream\r\n" +
      "\r\n";
    bos.write(testRequest.getBytes());
    bos.write(byteData);
    bos.write('\r');
    bos.write('\n');

    testRequest =
      TEST_START_BOUNDARY + "\r\n" +
      "Content-Disposition: form-data; name=\"action\"\r\n" +
      "\r\n" +
      "Send\r\n" +
      TEST_START_BOUNDARY + "--\r\n";
    bos.write(testRequest.getBytes());

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(bos.toByteArray());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);

    parser.parseRequest();
    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertEquals("data.dat", parser.getParameter("theFile_FileName"));
    assertEquals("application/octet-stream", parser.getParameter("theFile_ContentType"));
    assertEquals(new String(byteData), parser.getParameter("theFile"));
    assertEquals("Send", parser.getParameter("action"));
    byte[] byteResult = parser.getParameterByteArray("theFile_ByteArray");
    assertEquals("Byte Array Size", byteData.length, byteResult.length);
    for (int i = 0; i < byteData.length; i++) {
      assertEquals("Byte @" + i, byteData[i], byteResult[i]);
    }
    assertTrue("Expected end of data", parser.isEndOfData());
  }

  /**
   * Test for ParseRequest for multiple items with multi-value parameters
   */
  public void testParseRequestMultiValue() throws Exception {
    String testValue =
        "R & B Project\r\n" +
        "-------------\r\n" +
        "\r\n" +
        "History of project\r\n" +
        "        Development of SRS TO-02-0259 $34K\r\n" +
        "        System Design & Mentoring TO-02-0440 $40K\r\n" +
        "        Implementation & Mentoring Phase I TO-02-0491 $51K\r\n" +
        "        Implementation & Mentoring Phase II TO-02-0513 $55K\r\n" +
        "   Future of Project\r\n" +
        "        Implementation & Mentoring Phase III TO-02-0000 $50K (estimate)\r\n" +
        "\r\n";
    String testValue2 =
        "Another File\r\n" +
        "-------------\r\n" +
        "\r\n" +
        "Present for project\r\n" +
        "        Implementation & Mentoring Phase III TO-02-0563 $55K\r\n" +
        "\r\n";
    String[] expectedColors = new String[] {"red","green","blue"};
    String[] expectedFileNames = new String[] {
        "RBProjectPresentation.txt","AnotherPresentation.txt"};
    String[] expectedFileContents = new String[] {testValue, testValue2};

    String testRequest =
        TEST_START_BOUNDARY + "\r\n" +
        "Content-Disposition: form-data; name=\"email\"\r\n" +
        "\r\n" +
        "anyone@anywhere.com\r\n";

    for (int i = 0; i < expectedColors.length; i++) {
      testRequest += TEST_START_BOUNDARY + "\r\n" +
          "Content-Disposition: form-data; name=\"color\"\r\n" +
          "\r\n" +
          expectedColors[i] + "\r\n";
    }
    for (int i = 0; i < expectedFileNames.length; i++) {
      testRequest += TEST_START_BOUNDARY + "\r\n" +
          "Content-Disposition: form-data; name=\"theFile\"; filename=\"" +
          expectedFileNames[i] + "\"\r\n" +
          "Content-Type: text/plain\r\n" +
          "\r\n" +
          expectedFileContents[i] + "\r\n";
    }
    testRequest += TEST_START_BOUNDARY + "\r\n" +
        "Content-Disposition: form-data; name=\"action\"\r\n" +
        "\r\n" +
        "Send\r\n" +
        TEST_START_BOUNDARY + "--\r\n";

    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType(TEST_CONTENT_TYPE);
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);

    parser.parseRequest();
    assertEquals("anyone@anywhere.com", parser.getParameter("email"));
    assertEquals("RBProjectPresentation.txt", parser.getParameter("theFile_FileName"));
    assertEquals("text/plain", parser.getParameter("theFile_ContentType"));
    assertEquals(testValue, parser.getParameter("theFile"));
    assertEquals("Send", parser.getParameter("action"));
    assertEquals("single color", "red", parser.getParameter("color"));
    String[] colors = parser.getParameterValues("color");
    assertEquals("Number of colors", expectedColors.length, colors.length);
    for (int i = 0; i < colors.length; i++) {
      assertEquals("array color", expectedColors[i], colors[i]);
    }
    String[] filenames = parser.getParameterValues("theFile_FileName");
    assertEquals("Number of file names", expectedFileNames.length, filenames.length);
    for (int i = 0; i < filenames.length; i++) {
      assertEquals("filename", expectedFileNames[i], filenames[i]);
    }
    String[] fileContents = parser.getParameterValues("theFile");
    assertEquals("Number of file contents", expectedFileContents.length, fileContents.length);
    for (int i = 0; i < fileContents.length; i++) {
      assertEquals("fileContents", expectedFileContents[i], fileContents[i]);
    }

    assertTrue("Expected end of data", parser.isEndOfData());
  }
  
  public void testFromHttpUnit() throws Exception {
    String testRequest =
    "----HttpUnit-part0-aSgQ2M\r\n" +
    "Content-Disposition: form-data; name=\"importPayroll\"; filename=\"C:\\\\usr\\\\local\\\\eclipse\\\\workspace\\\\empeReport\\\\testdata\\\\M7019305_NF_01.txt\"\r\n" +
    "Content-Type: text/plain\r\n" +
    "\r\n" +
    "X This is a file in the new format, Event Records only\r\n" +
    "ET701M746839880HIRE08302005DABROWSKI,DIANNE                    F T\r\n" +
    "ET701M746839881HIRE08302005BROWN,PATRICIA P                    P T80\r\n" +
    "ET701M746839882HIRE08302005ROCHELLE,JENNIFER Z                 F T\r\n" +
    "ET701M746839883HIRE08302005BRAUN,STEVEN A                      F T\r\n" +
    "ET701M746839884HIRE08302005OLSSON,JENNIFER Q                   F T\r\n" +
    "ET701M746839885HIRE08302005ESPENEL,HEATHER S                   F T\r\n" +
    "ET701M746839886HIRE08302005PAGE,MARIA U                        F T\r\n" +
    "ET701M746839887HIRE08302005ATKINS,CHARLOTTE A                  F T\r\n" +
    "ET701M746839888HIRE08302005MALTA,BRIAN M                       F T\r\n" +
    "ET701M746839889HIRE08302005BOMBECK,CYNTHIA X                   P T75\r\n" +
    "\r\n" +
    "----HttpUnit-part0-aSgQ2M\r\n" +
    "Content-Disposition: form-data; name=\"event\"\r\n" +
    "Content-Type: text/plain; charset=iso-8859-1\r\n" +
    "\r\n" +
    "Import\r\n" +
    "----HttpUnit-part0-aSgQ2M--\r\n";
    TestHttpServletRequest request = new TestHttpServletRequest();
    TestServletInputStream stream = new TestServletInputStream();
    request.setContentType("multipart/form-data; boundary=--HttpUnit-part0-aSgQ2M");
    stream.setData(testRequest.getBytes());
    request.setInputStream(stream);
    HttpMultiPartServletRequest parser = new HttpMultiPartServletRequest(request);

    parser.parseRequest();

  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = {HttpMultiPartServletRequestTest.class.getName()};
    junit.swingui.TestRunner.main(testCaseList);
  }
}
