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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;

/**
 * Business Delegate is responsible for obtaining needed data and performing transactions.
 * It may do this by delegating the work to other local or remote objects.
 */
public class BusinessDelegate {
  private DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private ArrayList people = new ArrayList();
  private ArrayList colors = null;
  private String[] codes = null;
  private ArrayList invoices = new ArrayList();
  private ArrayList inventoryItems = new ArrayList();

  /**
   * Constructs a new BusinessDelagate and adds some test records.
   */
  public BusinessDelegate() {
    try {
      initPeople();
      initInvoices();
      initInventoryItems();
    } catch (ParseException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Get the Person who how has the given id.
   */
  public Person getPerson(int id) throws Exception {
    return (Person)people.get(id);
  }

  /**
   * Get a collection of all the People in the system.
   */
  public Collection getAllPeople() throws Exception {
    return people;
  }

  /**
   * Get an ArrayList of all the Invoices in the system.
   */
  public ArrayList getAllInvoices() throws Exception {
    return invoices;
  }

  /**
   * Get an ArrayList of all inventory items in the system
   */
  public ArrayList getAllInventoryItems() throws Exception {
    return inventoryItems;
  }

  /**
   * Get the Invoice for the given invoice number
   */
  public InvoiceVO getInvoice(int invoiceNumber) throws NotFoundException {
    if (invoiceNumber < 200345 || invoiceNumber > 200347)
      throw new NotFoundException(
          "Invoice not found. InvoiceNumber: " + invoiceNumber);
    return (InvoiceVO)invoices.get(invoiceNumber - 200345);
  }

  /**
   * Return a list of colors for testing.  These colors could come from
   * a database or Enterprise bean.
   */
  public List getColorList() {
    if (colors == null) {
      colors = new ArrayList();
      colors.add(new RBGColor("Bright White", "#FFFFFF"));
      colors.add(new RBGColor("Solid Black", "#000000"));
      colors.add(new RBGColor("Bright Red", "#FF0000"));
      colors.add(new RBGColor("Bright Green", "#00FF00"));
      colors.add(new RBGColor("Bright Blue", "#0000FF"));
      colors.add(new RBGColor("Bright Orange", "#FF9900"));
      colors.add(new RBGColor("Bright Yellow", "#FFFF00"));
      colors.add(new RBGColor("Bright Cyan", "#00FFFF"));
      colors.add(new RBGColor("Light Magenta", "#FF00FF"));
    }
    return colors;
  }

  /**
   * Get a String array of color codes. These colors could come from
   * a database or Enterprise bean.
   */
  public String[] getColorCodes() {
    if (codes == null) {
      codes = new String[getColorList().size()];
      Iterator iter = getColorList().iterator();
      int i = 0;
      while (iter.hasNext()) {
        RBGColor item = (RBGColor)iter.next();
        codes[i] = item.getCode();
        i++;
      }
    }
    return codes;
  }

  private void initPeople() throws ParseException {
    people.add(new Person(0, "Picard", "Jean Luc", df.parse("05/05/1955")));
    people.add(new Person(1, "Riker", "Will", df.parse("07/02/1916")));
    people.add(new Person(2, "Crusher", "Beverly", df.parse("10/28/1924")));
  }

  private void initInventoryItems() {
    inventoryItems.add(new InvoiceItemVO(1, "AX-2330", new BigDecimal("122.35")));
    inventoryItems.add(new InvoiceItemVO(1, "BD-4456", new BigDecimal("49.95")));
    inventoryItems.add(new InvoiceItemVO(3, "QR-1002", new BigDecimal("5.95")));
    inventoryItems.add(new InvoiceItemVO(1, "RR-6557", new BigDecimal("70.24")));
    inventoryItems.add(new InvoiceItemVO(5, "QQ-1005", new BigDecimal("6.95")));
    inventoryItems.add(new InvoiceItemVO(2, "PW-8778", new BigDecimal("595.00")));
    inventoryItems.add(new InvoiceItemVO(5, "PX-8778", new BigDecimal("10.59")));
  }

  private void initInvoices() throws ParseException {
    ArrayList items;
    InvoiceVO invoice;
    // Invoice ========================================================
    invoice = new InvoiceVO(200345, df.parse("01/05/2002"), 0, "net 30");
    items = new ArrayList();
    items.add(new InvoiceItemVO(1, "AX-2330", new BigDecimal("122.35")));
    items.add(new InvoiceItemVO(1, "BD-4456", new BigDecimal("49.95")));
    items.add(new InvoiceItemVO(3, "QR-1002", new BigDecimal("5.95")));
    invoice.setItems(items);
    invoices.add(invoice);
    // Invoice ========================================================
    invoice = new InvoiceVO(200346, df.parse("01/06/2002"), 2, "net 30");
    items = new ArrayList();
    items.add(new InvoiceItemVO(1, "RR-6557", new BigDecimal("70.24")));
    items.add(new InvoiceItemVO(5, "QQ-1005", new BigDecimal("6.95")));
    items.add(new InvoiceItemVO(3, "QR-1002", new BigDecimal("5.95")));
    invoice.setItems(items);
    invoices.add(invoice);
    // Invoice ========================================================
    invoice = new InvoiceVO(200347, df.parse("01/06/2002"), 1, "net 30");
    items = new ArrayList();
    items.add(new InvoiceItemVO(2, "PW-8778", new BigDecimal("595.00")));
    items.add(new InvoiceItemVO(5, "PX-8778", new BigDecimal("10.59")));
    items.add(new InvoiceItemVO(3, "QR-1002", new BigDecimal("5.95")));
    invoice.setItems(items);
    invoices.add(invoice);
    // Invoice ========================================================
    invoice = new InvoiceVO(200348, df.parse("01/06/2002"), 1, "net 30");
    items = new ArrayList();
    items.add(new InvoiceItemVO(1, "AX-2330", new BigDecimal("122.35")));
    invoice.setItems(items);
    invoices.add(invoice);
    // Invoice ========================================================
    invoice = new InvoiceVO(200349, df.parse("01/07/2002"), 1, "net 30");
    items = new ArrayList();
    invoice.setItems(items);
    invoices.add(invoice);
    // Invoice ========================================================
    invoice = new InvoiceVO(200350, df.parse("01/07/2002"), 2, "net 30");
    items = new ArrayList();
    items.add(new InvoiceItemVO(1, "RR-6557", new BigDecimal("70.24")));
    items.add(new InvoiceItemVO(3, "QR-1002", new BigDecimal("5.95")));
    invoice.setItems(items);
    invoices.add(invoice);
  }
}