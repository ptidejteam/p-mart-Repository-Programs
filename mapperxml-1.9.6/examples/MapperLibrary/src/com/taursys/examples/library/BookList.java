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

/**
 * Servlet Form for listing library books
 * @author ${author}
 * @version 1.0
 */
public class BookList extends ServletForm {
  VOCollectionValueHolder bookHolder = new VOCollectionValueHolder();
  Trigger defaultButton = new Trigger();
  Trigger deleteButton = new Trigger();
  Parameter catalogNoParm = new Parameter();
  Parameter searchKey = new Parameter();

  /**
   * Constructs a new BookList and initializes component properties.
   */
  public BookList() {
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
    bookHolder.setAlias("Book");

    defaultButton.setParameter("action");
    defaultButton.setText("Default");
    defaultButton.setDefaultTrigger(true);
    defaultButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        defaultButton_actionPerformed(e);
      }
    });

    deleteButton.setParameter("action");
    deleteButton.setText("Delete");
    deleteButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        deleteButton_actionPerformed(e);
      }
    });

    catalogNoParm.setParameter("catalogNo");
    searchKey.setParameter("searchKey");
    this.add(defaultButton);
    this.add(deleteButton);
    this.add(catalogNoParm);
    this.add(searchKey);
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
        getClass().getResourceAsStream("BookList.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Auto-create remaining display components
    HTMLComponentFactory.getInstance().createComponents(this,
        new ValueHolder[] {bookHolder});
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
  }

  /**
   * The delete button will delete the selected book.
   */
  void deleteButton_actionPerformed(TriggerEvent e) throws Exception {
    BookDAO dao = new BookDAO(
        (DataSource)getRequest().getAttribute("dataSource"));
    dao.deleteByPrimaryKey(catalogNoParm.getText());
    bookHolder.setCollection(dao.getAll());
  }

  /**
   * Testing method - tests Document and component creation
   */
  static public void main(String[] args) {
    try {
      BookList page = new BookList();
      page.initForm();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
