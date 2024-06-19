package com.taursys.examples.library;

import com.taursys.servlet.*;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.*;
import com.taursys.xml.*;
import com.taursys.html.*;
import javax.sql.DataSource;
import com.taursys.xml.event.*;
import com.taursys.servlet.respond.*;


/**
 * Servlet Form
 * @author
 * @version 1.0
 */
public class BookSuggestions extends ServletForm {
  HTMLResponder htmlResponder = new HTMLResponder();
  ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  Template books = new Template();
  TextField catalogNo = new TextField();
  TextField title = new TextField();
  TextField keywords = new TextField();
  TextField cost = new TextField();
  TextField dateAdded = new TextField();
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
    BookDAO dao = new BookDAO(
        (DataSource)getRequest().getAttribute("dataSource"));
    bookHolder.setCollection(dao.getAll());
    setResponder(htmlResponder);
  }

  /**
   * The delete button will delete the selected book.
   */
  void sendButton_actionPerformed(TriggerEvent e) throws Exception {
    BookDAO dao = new BookDAO(
        (DataSource)getRequest().getAttribute("dataSource"));
    // do something
    String[] suggestions = getRequest().getParameterValues("suggested");
    String text = "Here are some reading suggestions: \n";
    for (int i = 0; i < suggestions.length; i++) {
      BookVO vo = dao.getByPrimaryKey(suggestions[i]);
      text += vo.getCatalogNo() + " " + vo.getTitle() + "\n";
    }
    // Send e-mail
System.out.println("Message to " + email.getText() + "\n" + text);
    // Redirect to Main Menu
    setResponder(clientRedirectResponder);
  }
}
