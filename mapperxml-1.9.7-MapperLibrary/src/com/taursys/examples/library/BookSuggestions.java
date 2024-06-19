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
package com.taursys.examples.library;

import javax.sql.DataSource;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import com.taursys.html.HTMLInputText;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.servlet.respond.ClientRedirectResponder;
import com.taursys.servlet.respond.HTMLResponder;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;

/**
 * Servlet Form to send book suggestions to someone. This example does not
 * actually e-mail the items. All components are manually created on this page
 * rather than using the HTMLComponentFactory (see BookAdd).
 * @author
 * @version 1.0
 */
public class BookSuggestions extends ServletForm {
  BookDAO dao = new BookDAO();
  HTMLResponder htmlResponder = new HTMLResponder();
  ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  Template books = new Template();
  TextField catalogNo = new TextField();
  TextField title = new TextField();
  TextField keywords = new TextField();
  TextField cost = new TextField();
  TextField dateAdded = new TextField();
  TextField errorMessage = new TextField();
  VOCollectionValueHolder bookHolder = new VOCollectionValueHolder();
  Trigger defaultButton = new Trigger();
  Trigger sendButton = new Trigger();
  HTMLInputText suggested = new HTMLInputText();
  HTMLInputText email = new HTMLInputText();

  /**
   * Constructs a new BookSuggestions and initializes component properties.
   */
  public BookSuggestions() {
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
    clientRedirectResponder.setServletForm(this);
    clientRedirectResponder.setRedirectURL("MainMenu.sf");
    htmlResponder.setServletForm(this);

    books.setId("bookList");
    books.setCollectionValueHolder(bookHolder);

    catalogNo.setPropertyName("catalogNo");
    catalogNo.setValueHolder(bookHolder);
    catalogNo.setId("catalogNo");

    title.setPropertyName("title");
    title.setValueHolder(bookHolder);
    title.setId("title");

    keywords.setPropertyName("keywords");
    keywords.setValueHolder(bookHolder);
    keywords.setId("keywords");

    cost.setPropertyName("cost");
    cost.setValueHolder(bookHolder);
    cost.setId("cost");

    dateAdded.setPropertyName("dateAdded");
    dateAdded.setValueHolder(bookHolder);
    dateAdded.setId("dateAdded");

    errorMessage.setId("errorMessage");

    defaultButton.setParameter("action");
    defaultButton.setText("Default");
    defaultButton.setDefaultTrigger(true);
    defaultButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        defaultButton_actionPerformed(e);
      }
    });

    sendButton.setParameter("action");
    sendButton.setText("Send");
    sendButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        sendButton_actionPerformed(e);
      }
    });

    suggested.setPropertyName("catalogNo");
    suggested.setValueHolder(bookHolder);
    suggested.setId("suggested");

    email.setParameter("email");
    email.setId("email");
    books.add(keywords);
    books.add(cost);
    books.add(dateAdded);
    books.add(title);
    books.add(catalogNo);
    books.add(suggested);

    this.add(books);
    this.add(defaultButton);
    this.add(sendButton);
    this.add(email);
    this.add(errorMessage);
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
        getClass().getResourceAsStream("BookSuggestions.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Get datasource for DAO
    dao.setDataSource(
        (DataSource)getRequest().getAttribute("dataSource"));
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
   * The default button is "pressed" when no action is specified
   */
  void defaultButton_actionPerformed(TriggerEvent e) throws Exception {
    bookHolder.setCollection(dao.getAll());
    setResponder(htmlResponder);
    errorMessage.setText("");
  }

  /**
   * The delete button will delete the selected book.
   */
  void sendButton_actionPerformed(TriggerEvent e) throws Exception {
    // do something
    String[] suggestions = getRequest().getParameterValues("suggested");
    String text = "Here are some reading suggestions: \n";
    for (int i = 0; i < suggestions.length; i++) {
      BookVO vo = dao.getByPrimaryKey(suggestions[i]);
      text += vo.getCatalogNo() + " " + vo.getTitle() + "\n";
    }
    // Send e-mail
    sendMail(email.getText(), text, "Book Suggestions");
    // Redirect to Main Menu
    setResponder(clientRedirectResponder);
  }

  public void sendMail(String address, String message, String subject)
      throws Exception {
    // Demo code only - replace with e-mail code as appropriate
    System.out.println("Sending message to:" + address);
    System.out.println("Subject:" + subject);
    System.out.println("Message:" + message);
  }
}
