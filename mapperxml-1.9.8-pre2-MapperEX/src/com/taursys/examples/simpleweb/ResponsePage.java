/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.simpleweb;

import com.taursys.servlet.ServletForm;
import com.taursys.servlet.respond.ClientRedirectResponder;
import com.taursys.servlet.respond.ErrorResponder;
import com.taursys.servlet.respond.HTMLResponder;
import com.taursys.servlet.respond.NoResponseResponder;
import com.taursys.servlet.respond.StreamResponder;
import com.taursys.xml.Parameter;

/**
 * ResponsePage Servlet Form Example demonstrating how to modify response.
 * @author
 * @version 1.0
 */
public class ResponsePage extends ServletForm {
  Parameter response = new Parameter();
  HTMLResponder htmlResponder = new HTMLResponder();
  ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  ErrorResponder errorResponder = new ErrorResponder();
  NoResponseResponder noResponseResponder = new NoResponseResponder();
  StreamResponder streamResponder = new StreamResponder();

  /**
   * Constructs a new ResponsePage and initializes component properties.
   */
  public ResponsePage() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Set component properties here.  It is not recommended to put anything
   * other than property settings in here.  Any Exception will cause the
   * constructor to fail.
   */
  private void jbInit() throws Exception {
    this.setDocumentURI("resource:///forms/ResponsePage.html");
    response.setParameter("response");
    response.setDefaultValue("html");
    clientRedirectResponder.setRedirectURL("/mapperex/index.html");
    errorResponder.setStatusCode(403);
    errorResponder.setMessage("This is the error response - forbidden");
    streamResponder.setContentType("image/gif");
    this.add(response);
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    String resp = response.getText();
    if (resp.equals("html")) {
      setResponder(htmlResponder);
    } else if (resp.equals("redirect")) {
      setResponder(clientRedirectResponder);
    } else if (resp.equals("error")) {
      setResponder(errorResponder);
    } else if (resp.equals("none")) {
      setResponder(noResponseResponder);
    } else if (resp.equals("stream")) {
      streamResponder.setInputStream(
        getClass().getResourceAsStream("mapperlogo.gif"));
      setResponder(streamResponder);
    } else {
      throw new Exception("Unknown response type requested: " + resp);
    }
  }
}
