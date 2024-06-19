package com.taursys.examples.simpleweb;

import com.taursys.html.*;
import com.taursys.html.render.*;
import com.taursys.model.*;
import com.taursys.servlet.*;
import com.taursys.servlet.respond.*;
import com.taursys.util.DataTypes;
import com.taursys.xml.*;
import com.taursys.xml.event.*;
import com.taursys.xml.render.*;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Servlet Form
 * @author Marty Phelan
 * @version 2.0
 */
public class SubFormNewsPage extends ServletForm {
  private VOListValueHolder newsItems = new VOListValueHolder();
  private BusinessDelegate bd;
  private VOComparator sorter = new VOComparator();

  /**
   * Constructs a new SubFormNewsPage and initializes component properties.
   */
  public SubFormNewsPage() {
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
    sorter.setPropertyName("releaseDate");
    sorter.setAscendingOrder(false);
    newsItems.setValueObjectClass(com.taursys.examples.simpleweb.NewsItemVO.class);
    newsItems.setAlias("NewsItem");
    newsItems.setComparator(sorter);
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
        getClass().getResourceAsStream("SubFormNewsPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Fetch reference to the Business delegate
    bd = (BusinessDelegate)
        getRequest().getAttribute(MainServlet.BUSINESS_DELEGATE);
    // Use HTMLComponentFactory to create components
    HTMLComponentFactory.getInstance().createComponents(this,
      new ValueHolder[] {newsItems});
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    newsItems.setList(bd.getAllNewsItems());
    newsItems.sort();
  }
}
