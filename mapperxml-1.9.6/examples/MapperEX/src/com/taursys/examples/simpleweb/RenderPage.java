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
import java.math.BigDecimal;

/**
 * Servlet Form to demonstrate customization of component rendering.
 * @author Marty Phelan
 * @version 2.0
 */
public class RenderPage extends ServletForm {
  private VOCollectionValueHolder invoiceItems = new VOCollectionValueHolder();
  private Template invoiceItemTemplate = new Template();
  private TextField quantity = new TextField();
  private TextField productID = new TextField();
  private TextField unitPrice = new TextField();
  private TextField notes = new TextField();
  private BusinessDelegate bd;

  /**
   * Constructs a new RenderPage and initializes component properties.
   */
  public RenderPage() {
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
    invoiceItems.setValueObjectClass(com.taursys.examples.simpleweb.InvoiceItemVO.class);
    invoiceItems.setAlias("invoiceItems");
    invoiceItemTemplate.setId("invoiceItems");
    invoiceItemTemplate.setCollectionValueHolder(invoiceItems);
    quantity.setPropertyName("quantity");
    quantity.setValueHolder(invoiceItems);
    quantity.setId("quantity");
    quantity.addRenderListener(new com.taursys.xml.event.RenderListener() {
      public void render(RenderEvent e) {
        quantity_render(e);
      }
    });
    productID.setPropertyName("productID");
    productID.setValueHolder(invoiceItems);
    productID.setId("productID");
    unitPrice.setFormat(java.text.DecimalFormat.getInstance());
    unitPrice.setFormatPattern("###,##0.00");
    unitPrice.setPropertyName("unitPrice");
    unitPrice.setValueHolder(invoiceItems);
    unitPrice.setId("unitPrice");
    unitPrice.addRenderListener(new com.taursys.xml.event.RenderListener() {
      public void render(RenderEvent e) {
        unitPrice_render(e);
      }
    });
    notes.setId("notes");
    invoiceItemTemplate.add(quantity);
    invoiceItemTemplate.add(productID);
    invoiceItemTemplate.add(unitPrice);
    invoiceItemTemplate.add(notes);
    this.add(invoiceItemTemplate);
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
        getClass().getResourceAsStream("RenderPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Fetch reference to the Business delegate
    bd = (BusinessDelegate)
        getRequest().getAttribute(MainServlet.BUSINESS_DELEGATE);
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    invoiceItems.setCollection(bd.getAllInventoryItems());
  }

  /**
   * This method will change the background color of the unitPrice cell based
   * on its value.
   */
  void unitPrice_render(RenderEvent e) {
    try {
      if (((BigDecimal)unitPrice.getValue()).compareTo(new BigDecimal(250)) == 1) {
        getDocumentAdapter().setAttributeText("unitPrice","bgcolor","red");
      } else {
        getDocumentAdapter().setAttributeText("unitPrice","bgcolor","white");
      }
    } catch (ModelException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method will hide or show the notes based on the quantity on hand.
   */
  void quantity_render(RenderEvent e) {
    try {
      if (((Integer)quantity.getValue()).intValue() < 2) {
        notes.setVisible(true);
        notes.setText("Better order more now!!!!");
      } else {
        notes.setVisible(false);
      }
    } catch (ModelException ex) {
      ex.printStackTrace();
    }
  }
}
