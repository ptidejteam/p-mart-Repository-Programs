package com.taursys.examples.simpleweb;

import com.taursys.examples.simpleweb.delegate.BusinessDelegate;
import com.taursys.html.HTMLComponentFactory;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.servlet.ServletForm;

/**
 * Servlet Form to demonstrate use of HTMLComponentFactory
 * @author M Phelan
 * @version 1.0
 */
public class ComponentFactoryPage extends ServletForm {
  private VOCollectionValueHolder invoice = new VOCollectionValueHolder();
  private VOCollectionValueHolder invoiceItem = new VOCollectionValueHolder();
  private BusinessDelegate bd;

  /**
   * Constructs a new ComponentFactoryPage and initializes component properties.
   */
  public ComponentFactoryPage() {
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
    this.setDocumentURI("resource:///forms/ComponentFactoryPage.html");
    invoice.setAlias("Invoice");
    invoice.setValueObjectClass(com.taursys.examples.simpleweb.delegate.InvoiceVO.class);
    invoiceItem.setValueObjectClass(com.taursys.examples.simpleweb.delegate.InvoiceItemVO.class);
    invoiceItem.setAlias("InvoiceItem");
    invoiceItem.setParentValueHolder(invoice);
    invoiceItem.setParentPropertyName("items");
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
      new ValueHolder[] {invoice, invoiceItem});
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    invoice.setCollection(bd.getAllInvoices());
  }
}
