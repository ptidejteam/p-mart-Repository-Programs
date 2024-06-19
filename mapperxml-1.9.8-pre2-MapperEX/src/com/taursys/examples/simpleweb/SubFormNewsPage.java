package com.taursys.examples.simpleweb;

import com.taursys.examples.simpleweb.delegate.BusinessDelegate;
import com.taursys.html.HTMLComponentFactory;
import com.taursys.model.VOComparator;
import com.taursys.model.VOListValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.servlet.ServletForm;

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
    this.setDocumentURI("resource:///forms/SubFormNewsPage.html");
    sorter.setPropertyName("releaseDate");
    sorter.setAscendingOrder(false);
    newsItems.setValueObjectClass(com.taursys.examples.simpleweb.delegate.NewsItemVO.class);
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
    // Fetch reference to the Business delegate
    bd = BusinessDelegate.getInstance();
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
