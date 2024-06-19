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

import com.taursys.html.HTMLInputText;
import com.taursys.model.VOValueHolder;
import com.taursys.util.DataTypes;
import com.taursys.xml.TextField;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;
import com.taursys.xml.Parameter;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import java.io.File;
import java.io.FileOutputStream;
import com.taursys.servlet.*;

/**
 * UploadPage Servlet Form demonstrates upload file functionality.
 */
public class UploadPage extends ServletForm {
  HTMLInputText email = new HTMLInputText();
  Parameter file = new Parameter();
  Trigger sendButton = new Trigger();
  TextField contents = new TextField();
  TextField emailAddress = new TextField();
  TextField fileName = new TextField();
  TextField contentType = new TextField();

  /**
   * Constructs a new SecondPage and initializes component properties.
   */
  public UploadPage() {
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
    email.setParameter("email");
    email.setId("email");
    file.setParameter("file");
    sendButton.setParameter("action");
    sendButton.setText("Send");
    sendButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        sendButton_actionPerformed(e);
      }
    });
    contents.setId("contents");
    emailAddress.setParameter("email");
    emailAddress.setId("emailAddress");
    fileName.setParameter("file_FileName");
    fileName.setId("fileName");
    contentType.setParameter("file_ContentType");
    contentType.setId("contentType");
    this.add(email);
    this.add(file);
    this.add(sendButton);
    this.add(contents);
    this.add(emailAddress);
    this.add(fileName);
    this.add(contentType);
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
    // Use Xerces to Parse document to a DOM and store as this form's document
    // You can use any method you like to create the DOM
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(
        getClass().getResourceAsStream("UploadPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
  }

  /**
   * Display text file or write binary file to disk
   */
  void sendButton_actionPerformed(TriggerEvent e) throws Exception {
    if (getRequest() instanceof HttpMultiPartServletRequest) {
      if (contentType.getText().startsWith("text")) {
        contents.setText(file.getText());
      } else {
        File outFile = new File("/tmp/SavedFile.bin");
        FileOutputStream os = new FileOutputStream(outFile);
        HttpMultiPartServletRequest rq = (HttpMultiPartServletRequest)getRequest();
        os.write(rq.getParameterByteArray("file_ByteArray"));
        os.close();
        contents.setText(
            "binary data - saved, not displayed. Size (bytes)="
            + rq.getParameterByteArray("file_ByteArray").length);
      }
    } else {
      // no file submitted
      contents.setText("No file sent");
    }
  }

  /**
   * Handle any form level exceptions - specifically a multi-part size exception.
   */
  protected void handleException(Exception ex) throws java.lang.Exception {
    if (ex instanceof MultiPartRequestSizeException) {
      contents.setText(ex.getMessage());
      sendResponse();
    } else {
      super.handleException(ex);
    }
  }
}
