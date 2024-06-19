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
package com.taursys.examples.simpleweb.delegate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Business Delegate is responsible for obtaining needed data and performing transactions.
 * It may do this by delegating the work to other local or remote objects.
 */
public class BusinessDelegate {
  private static BusinessDelegate _bd;
  private DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private ArrayList people = new ArrayList();
  private ArrayList colors = null;
  private String[] codes = null;
  private ArrayList invoices = new ArrayList();
  private ArrayList inventoryItems = null;
  private ArrayList newsItems = new ArrayList();
  private ArrayList smallPartsWarehouseList = null;
  private ArrayList largePartsWarehouseList = null;

  /**
   * Constructs a new BusinessDelagate and adds some test records.
   */
  public BusinessDelegate() {
    try {
      initPeople();
      initInvoices();
      initNewsItems();
    } catch (ParseException ex) {
      ex.printStackTrace();
    }
  }

  public static BusinessDelegate getInstance() throws Exception {
    if (_bd == null) {
      _bd = new BusinessDelegate();
    }
    return _bd;
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
    if (inventoryItems == null) {
      inventoryItems = new ArrayList();
      inventoryItems.add(new InvoiceItemVO(1, 3004, "AX-2330",
          new BigDecimal("122.35"), getSmallPartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(1, 3002, "BD-4456",
          new BigDecimal("49.95"), getSmallPartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(3, 1001, "QR-1002",
          new BigDecimal("5.95"), getLargePartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(1, 1004, "RR-6557",
          new BigDecimal("70.24"), getLargePartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(5, 1002, "QQ-1005",
          new BigDecimal("6.95"), getLargePartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(2, 1001, "PW-8778",
          new BigDecimal("595.00"), getLargePartsWarehouseList()));
      inventoryItems.add(new InvoiceItemVO(5, 3003, "MX-8778",
          new BigDecimal("10.59"), getSmallPartsWarehouseList()));
    }
    return inventoryItems;
  }

  /**
   * Get an ArrayList of all news items in the system
   */
  public ArrayList getAllNewsItems() throws Exception {
    return newsItems;
  }

  /**
   * Get the list of all warehouses for small parts
   */
  public ArrayList getSmallPartsWarehouseList() throws Exception {
    if (smallPartsWarehouseList == null) {
      smallPartsWarehouseList = new ArrayList();
      smallPartsWarehouseList.add(new WarehouseVO(3001, "Westlake warehouse"));
      smallPartsWarehouseList.add(new WarehouseVO(3002, "Northgate warehouse"));
      smallPartsWarehouseList.add(new WarehouseVO(3003, "Freemont warehouse"));
      smallPartsWarehouseList.add(new WarehouseVO(3004, "University warehouse"));
    }
    return smallPartsWarehouseList;
  }

  /**
   * Get the list of all warehouses for large parts
   */
  public ArrayList getLargePartsWarehouseList() throws Exception {
    if (largePartsWarehouseList == null) {
      largePartsWarehouseList = new ArrayList();
      largePartsWarehouseList.add(new WarehouseVO(1001, "Ranier yard"));
      largePartsWarehouseList.add(new WarehouseVO(1002, "West Marginal yard"));
      largePartsWarehouseList.add(new WarehouseVO(1003, "Auburn yard"));
      largePartsWarehouseList.add(new WarehouseVO(1004, "Kent yard"));
    }
    return largePartsWarehouseList;
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

  private void initNewsItems() throws ParseException {
    newsItems.add(
      new NewsItemVO(
          1000,
          df.parse("4/18/2002"),
          "We're Back!!",
          "Please excuse the mess!! Mapper is in the process " +
          "of moving back to SourceForge.net as its main site. " +
          "The older Enhydra version of Mapper will be moved to this site, as " +
          "well as the new MapperXML and MapperGUI projects. " +
          "Check back often. We should have most content available soon."
          ));
    newsItems.add(
      new NewsItemVO(
          1001,
          df.parse("4/22/2002"),
          "Site construction continues",
          "Additional material " +
          "will be posted throughout this week."
          ));
    newsItems.add(
      new NewsItemVO(
          1002,
          df.parse("4/28/2002"),
          "New release available",
          "Additional examples " +
          "available in Quick Guide.  Site construction continues and more " +
          "materials to come."
          ));
    newsItems.add(
      new NewsItemVO(
          1003,
          df.parse("5/7/2002"),
          "What's new",
          "Additional examples available in Quick Guide. " +
          "Initial version of code generator nearing completion."
          ));
    newsItems.add(
      new NewsItemVO(
          1004,
          df.parse("5/21/2002"),
          "New release 1.9.2 available",
          "Includes more " +
          "components and features. Also includes initial release of template " +
          "code generator."
          ));
    newsItems.add(
      new NewsItemVO(
          1005,
          df.parse("5/25/2002"),
          "New release 1.9.3 available",
          "New packaging " +
          "includes Examples and JavaDoc. Other minor changes to run with " +
          "Tomcat 3 and 4."
          ));
    newsItems.add(
      new NewsItemVO(
          1006,
          df.parse("7/10/2002"),
          "New release 1.9.4 available",
          "Includes new " +
          "Respondor subcomponent, new Checkbox component, support for " +
          "defaulting values, Other minor enhancements and javadoc " +
          "updates."
          ));
    newsItems.add(
      new NewsItemVO(
          1007,
          df.parse("7/14/2002"),
          "Updated MapperXML Documentation",
          "This document is under construction, but has the Overview section " +
          "completed."
          ));
    newsItems.add(
      new NewsItemVO(
          1008,
          df.parse("8/11/2002"),
          "New version 1.9.5 available",
          "Added support " +
          "for multipart type requests. Includes new example projects and " +
          "script/bat files to run CodeGen application. Other minor changes " +
          "and bug fixes."
          ));
    newsItems.add(
      new NewsItemVO(
          1009,
          df.parse("11/08/2002"),
          "New version 1.9.6 available",
          "New ComponentFactory to " +
          "automatically create and bind components, Support for master/detail " +
          "in value holders, Updated Documentation, and many more changes."
          ));
  }
}