/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */


import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.Priority;
import org.apache.log4j.Logger;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.awt.Rectangle;

public class AppenderTable extends JTable {


  static Logger cat = Logger.getLogger(AppenderTable.class.getName());

  PatternLayout layout;

  public
  AppenderTable() {
    layout = new PatternLayout("%r %p %c [%t] -  %m");
    this.setDefaultRenderer(Object.class, new Renderer());
  }

  public
  void add(LoggingEvent event) {
    ((AppenderTableModel)getModel()).add(event);
  }

  public
  Dimension getPreferredSize() {
    System.out.println("getPreferredSize() called");
    return super.getPreferredSize();
  }

  static public void main(String[] args) {

    if(args.length != 1) {
      System.err.println(
      "Usage: java AppenderTable bufferSize\n"+
      "  where bufferSize is the size of the cyclic buffer in the TableModel\n");
      return;
    }

    JFrame frame = new JFrame("JTableAppennder test");
    Container container = frame.getContentPane();

    AppenderTable tableAppender = new AppenderTable();
    
    AppenderTableModel model = new 
                              AppenderTableModel(Integer.parseInt(args[0]));
    tableAppender.setModel(model);
    //appender.createDefaultColumnsFromModel();    


    JScrollPane sp = new JScrollPane(tableAppender);
    sp.setPreferredSize(new Dimension(250, 80));
    
    container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
    container.add(sp);

    JButton button = new JButton("ADD");
    container.add(button);
    

    button.addActionListener(new JTableAddAction(tableAppender));

    frame.setVisible(true);
    frame.setSize(new Dimension(700,700));

    long before = System.currentTimeMillis();

    int RUN = 100;
    int i = 0;
    while(i++ < RUN) {      
      LoggingEvent event = new LoggingEvent("x", cat, Priority.ERROR, 
					    "Message "+i, null);
      tableAppender.add(event);
    }

    long after = System.currentTimeMillis();
    System.out.println("Time taken :"+ ((after-before)*1000/RUN));

  }

  class Renderer extends JTextArea implements TableCellRenderer {

    Object o = new Object();
    int i = 0;

    public
    Renderer() {
      System.out.println("Render() called ----------------------");      
    }

    public Component getTableCellRendererComponent(JTable table,
						   Object value,
						   boolean isSelected,
						   boolean hasFocus,
						   int row,
						   int column) {

      System.out.println(o + " ============== " + i++);
      //LogLog.error("=======", new Exception());
      //setIcon(longIcon);
      if(value instanceof LoggingEvent) {
	LoggingEvent event = (LoggingEvent) value;
	String str = layout.format(event);
	setText(str);
      } else {
	setText(value.toString());
      }


      return this;
    }
  }
}

class AppenderTableModel extends AbstractTableModel {

  CyclicBuffer cb;
  
  AppenderTableModel(int size) {
    cb = new CyclicBuffer(size);
  }

  public
  void add(LoggingEvent event) {
    //System.out.println("JListViewModel.add called");
    cb.add(event);
    int j = cb.length();

    fireTableDataChanged();

  }
  public 
  int getColumnCount() { 
    return 1; 
  }

  public int getRowCount() { 
    return cb.length();
  }

  //public
  //Class getColumnClass(int index) {
  //  System.out.println("getColumnClass called " + index);
  //  return LoggingEvent.class;
  //}

  public 
  Object getValueAt(int row, int col) {
    return cb.get(row);
  }
}


class JTableAddAction implements ActionListener {
    
  int j;
  AppenderTable appender;

  Logger cat = Logger.getLogger("x");
  
  public
  JTableAddAction(AppenderTable appender) {
    this.appender = appender;
    j = 0;
  }
    
  public
  void actionPerformed(ActionEvent e) {
    System.out.println("Action occured");

    LoggingEvent event = new LoggingEvent("x", cat, Priority.DEBUG, 
					    "Message "+j, null);
    
    if(j % 5 == 0) {
      //event.throwable = new Exception("hello "+j);
    }
    j++;
    appender.add(event);
  }
}
