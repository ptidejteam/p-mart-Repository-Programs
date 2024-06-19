package com.taursys.examples.simpleweb;

import com.taursys.dom.DocumentAdapter;
import com.taursys.examples.simpleweb.delegate.BusinessDelegate;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.util.DataTypes;
import com.taursys.xml.Parameter;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;

/**
 * Servlet Form
 * @author Marty Phelan
 * @version 2.0
 */
public class InvoiceListPage extends ServletForm {
  private VOCollectionValueHolder invoices = new VOCollectionValueHolder();
  private VOCollectionValueHolder invoiceItems = new VOCollectionValueHolder();
  private Template invoiceTemplate = new Template();
  private TextField invoiceNumber = new TextField();
  private TextField customerID = new TextField();
  private TextField issueDate = new TextField();
  private TextField terms = new TextField();
  private Template invoiceItemTemplate = new Template();
  private TextField quantity = new TextField();
  private TextField productID = new TextField();
  private TextField unitPrice = new TextField();
  private BusinessDelegate bd;
  private Parameter style = new Parameter(DataTypes.TYPE_INT);
  private DocumentAdapter[] documentAdapters = new DocumentAdapter[4];
  private TextField extension = new TextField();
  private TextField total = new TextField();

  /**
   * Constructs a new InvoiceListPage and initializes component properties.
   */
  public InvoiceListPage() {
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
    invoices.setValueObjectClass(com.taursys.examples.simpleweb.delegate.InvoiceVO.class);
    invoices.setAlias("invoices");
    invoiceItems.setValueObjectClass(com.taursys.examples.simpleweb.delegate.InvoiceItemVO.class);
    invoiceItems.setParentValueHolder(invoices);
    invoiceItems.setParentPropertyName("items");
    invoiceItems.setAlias("invoiceItems");
    invoiceTemplate.setId("invoices");
    invoiceTemplate.setCollectionValueHolder(invoices);
    invoiceNumber.setPropertyName("invoiceNumber");
    invoiceNumber.setValueHolder(invoices);
    invoiceNumber.setId("invoiceNumber");
    customerID.setPropertyName("customerID");
    customerID.setValueHolder(invoices);
    customerID.setId("customerID");
    issueDate.setFormat(java.text.SimpleDateFormat.getInstance());
    issueDate.setFormatPattern("MM/dd/yyyy");
    issueDate.setPropertyName("issueDate");
    issueDate.setValueHolder(invoices);
    issueDate.setId("issueDate");
    terms.setPropertyName("terms");
    terms.setValueHolder(invoices);
    terms.setId("terms");
    invoiceItemTemplate.setId("invoiceItems");
    invoiceItemTemplate.setCollectionValueHolder(invoiceItems);
    quantity.setPropertyName("quantity");
    quantity.setValueHolder(invoiceItems);
    quantity.setId("quantity");
    productID.setPropertyName("productID");
    productID.setValueHolder(invoiceItems);
    productID.setId("productID");
    unitPrice.setFormat(java.text.DecimalFormat.getInstance());
    unitPrice.setFormatPattern("###,##0.00");
    unitPrice.setPropertyName("unitPrice");
    unitPrice.setValueHolder(invoiceItems);
    unitPrice.setId("unitPrice");
    style.setParameter("style");
    extension.setFormat(java.text.DecimalFormat.getInstance());
    extension.setFormatPattern("###,##0.00");
    extension.setPropertyName("extension");
    extension.setValueHolder(invoiceItems);
    extension.setId("extension");
    total.setFormat(java.text.DecimalFormat.getInstance());
    total.setFormatPattern("###,##0.00");
    total.setPropertyName("total");
    total.setValueHolder(invoices);
    total.setId("total");
    invoiceItemTemplate.add(quantity);
    invoiceItemTemplate.add(productID);
    invoiceItemTemplate.add(unitPrice);
    invoiceItemTemplate.add(extension);
    invoiceTemplate.add(invoiceNumber);
    invoiceTemplate.add(customerID);
    invoiceTemplate.add(issueDate);
    invoiceTemplate.add(terms);
    invoiceTemplate.add(invoiceItemTemplate);
    invoiceTemplate.add(total);
    this.add(invoiceTemplate);
    this.add(style);
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
  }

  private void setupDocumentForStyle(Integer iStyle) throws Exception {
    // Determine style number (default if null or out of range)
    int styleNo = 0;
    if (iStyle != null)
      styleNo = iStyle.intValue();
    if (styleNo < 0 || styleNo > 3)
      styleNo = 0;
    // Parse document if not already cached
    if (documentAdapters[styleNo] == null) {
      String doc = "resource:///forms/InvoiceListPage" + styleNo + ".html";
      documentAdapters[styleNo] = getDocumentAdapterBuilder().build(doc);
    }
    setDocumentAdapter(documentAdapters[styleNo]);
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    setupDocumentForStyle((Integer)style.getValue());
    invoices.setCollection(bd.getAllInvoices());
  }
}
