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
 * @author ${_author}
 * @version 2.0
 */
public class InventoryEditPage extends ServletForm {
  private VOListValueHolder inventoryHolder = new VOListValueHolder();
  private VOListValueHolder warehouseHolder = new VOListValueHolder();
  private BusinessDelegate bd;
  private HTMLSelect warehouseField = new HTMLSelect();
//  private HTMLInputText removeBox = new HTMLInputText();
  private HTMLInputText removeBoxOutput = new HTMLInputText();
  private Template inventoryTemplate = new Template();

  /**
   * Constructs a new InventoryEditPage and initializes component properties.
   */
  public InventoryEditPage() {
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
    inventoryHolder.setAlias("Inventory");
    inventoryHolder.setValueObjectClass(InvoiceItemVO.class);

    warehouseHolder.setAlias("Warehouse");
    warehouseHolder.setValueObjectClass(WarehouseVO.class);
    warehouseHolder.setParentValueHolder(inventoryHolder);
    warehouseHolder.setParentPropertyName("warehouses");

    warehouseField.setDisplayPropertyName("description");
    warehouseField.setList(warehouseHolder);
    warehouseField.setListPropertyNames(new String[] {"warehouseID"});
    warehouseField.setPropertyName("warehouseID");
    warehouseField.setId("Inventory__warehouseID");
    warehouseField.setValueHolder(inventoryHolder);
    warehouseField.setParameter("warehouseID");

//    removeBox.addInputListener(new com.taursys.xml.event.InputListener() {
//      public void inputReceived(InputEvent e) throws Exception {
//        removeBox_inputReceived(e);
//      }
//    });
//    removeBox.setParameter("remove");

    removeBoxOutput.setId("Inventory__productID__2");
    removeBoxOutput.addRenderListener(new com.taursys.xml.event.RenderListener() {
      public void render(RenderEvent e) throws RenderException {
        removeBoxOutput_render(e);
      }
    });
    removeBoxOutput.setParameter("remove");
    removeBoxOutput.setPropertyName("productID");
    removeBoxOutput.setValueHolder(inventoryHolder);

    inventoryTemplate.setId("Inventory__TEMPLATE_NODE");
    inventoryTemplate.setCollectionValueHolder(inventoryHolder);

//    inventoryTemplate.add(removeBox);
    inventoryTemplate.add(removeBoxOutput);
    inventoryTemplate.add(warehouseField);
    this.add(inventoryTemplate);
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
        getClass().getResourceAsStream("InventoryEditPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Fetch reference to the Business delegate
    bd = (BusinessDelegate)
        getRequest().getAttribute(MainServlet.BUSINESS_DELEGATE);
    // Use HTMLComponentFactory to create components
    HTMLComponentFactory.getInstance().createComponents(this,
      new ValueHolder[] {inventoryHolder});
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    inventoryHolder.setList(bd.getAllInventoryItems());
  }

  void removeBox_inputReceived(InputEvent e) throws Exception {
    System.out.println("removeBox value="+e.getValue());
  }

  void removeBoxOutput_render(RenderEvent e) throws RenderException {
System.out.println("rendering remove box");
  }
}
