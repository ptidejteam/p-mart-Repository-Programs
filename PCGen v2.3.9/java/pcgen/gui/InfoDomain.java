/*
 * InfoDomain.java
 * Copyright 2001 (C) Mario Bonassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */
 
/**
 * This class is responsible for drawing the domain related window - including
 * indicating what deity and domains are available, which ones are selected, and handling
 * the selection/de-selection of both.  

 
/**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 * modified by Bryan McRoberts (merton_monk@yahoo.com) to connect to pcgen.core package
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import pcgen.core.CharacterDomain;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class InfoDomain extends JPanel
{
  private JLabel deityName = new JLabel("Name");
  private JTableEx domainTable = null;
  private JTableEx deityTable = null;
  private DeityModel dataModel = new DeityModel();
  private DomainModel domModel = new DomainModel();

  private final Object[] deiLongValues = {"Namexxxxxxx", "Domainsxxxxxxxxxx", "Alignment", "Sourcexxxxxxx"};
  private static String[] s_columnNames = {"Name", "Domains", "Aligment", "Source"};
  private static String[] s_domainColList = {"Domain", "Source"};
  private final Object[] domLongValues = {"Domainxxxx", "Sourcexxxx" };

  private JLabel deityLabel = new JLabel("Deity Selected: ");
  private boolean ALLOW_ROW_SELECTION = true;
  private TableSorter sorter = null;
  private TableSorter sorter2 = null;
  Border etched;
  TitledBorder titled;
  protected static PlayerCharacter aPC = null;
  
  private JScrollPane domainScroll = new JScrollPane();
  private JScrollPane deityScroll = new JScrollPane();
  private JLabel domainInfo = new JLabel();
  private JLabel deityInfo = new JLabel();
  private JLabel domSelected = new JLabel("Domains Selected: ");
  private JLabel ofLabel = new JLabel("of");
  private WholeNumberField domChosen= new WholeNumberField(0, 0);
  private WholeNumberField domTotal= new WholeNumberField(0, 0);
  protected static ArrayList pcDomainList = new ArrayList();
  static boolean needsUpdate = true; 

  
  

  public InfoDomain()
  {
    aPC = Globals.getCurrentPC();
    initComponents();
  }

  //Set up GridBag Constraints
  void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
  {
    gbc.gridx = gx;
    gbc.gridy = gy;
    gbc.gridwidth = gw;
    gbc.gridheight = gh;
    gbc.weightx = wx;
    gbc.weighty = wy;
  }  

  private void initComponents()
  {
    //Deity table set up
    setToolTipText("Selected deity and domains are in bold.  Double-click on a deity or domain to select it.");
    sorter = new TableSorter(dataModel);
    deityTable = new JTableEx(sorter);
    sorter.addMouseListenerToHeaderInTable(deityTable);
    //Set up column sizes.
    TableColumn column = null;
    Component comp = null;
    int cellWidth = 0;
    for (int i = 0; i < s_columnNames.length; i++)
    {
      column = deityTable.getColumnModel().getColumn(i);
      if (i == 0)
        cellWidth = 45;
      else
      {
        comp = deityTable.getDefaultRenderer(dataModel.getColumnClass(i)).getTableCellRendererComponent(deityTable, deiLongValues[i], false, false, 0, i);
        cellWidth = comp.getPreferredSize().width;
      }
      column.setPreferredWidth(cellWidth);
    }
    deityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    deityTable.setToolTipText("Selected deity is in bold. dbl-click to change selection.");
    
    //Domain table Setup

    sorter2 = new TableSorter(domModel);
    domainTable = new JTableEx(sorter2);
      
    //Set up tool tips for the domain table
    domainTable.setToolTipText("Select domains are in bold. dbl-click to change selection.");
    final MouseListener deityMouse = new  MouseAdapter() {
      public void mouseClicked(MouseEvent f) {
        ListSelectionModel lsm = deityTable.getSelectionModel();
        if (f.getClickCount() == 1) {
          final int row = lsm.getMinSelectionIndex();
          if (row < 0)
            return;
          final int selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
          if (selectedRow < 0)
            return;
          final String selectedDeityName = dataModel.getValueAt(selectedRow, -1).toString();
          final Deity aDeity = Globals.getDeityNamed(selectedDeityName);
          if (aDeity!=null) {
            deityInfo.setText(PCGen_Frame1.breakupString("<html><b>"+selectedDeityName+
                     " &nbsp;Description</b>:"+aDeity.getDescription()+
                     " &nbsp;<b>Requirements</b>:"+aDeity.preReqHTMLStrings(false)+
                     " &nbsp;<b>Favored Weapon</b>:"+aDeity.getFavoredWeapon()+
                     " &nbsp;<b>Holy Item</b>:"+aDeity.getHolyItem()+"</html>", (int)deityScroll.getSize().getWidth()));
          }
          return;
        }
        if (f.getClickCount() == 2) {
          int row = lsm.getMinSelectionIndex();
          if (row < 0)
            return;
          final int selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
          if (selectedRow < 0)
            return;
          Globals.getCurrentPC().setDirty(true);
          InfoDomain.this.selectDeityIndex(selectedRow);
          return;
        }
        else {
          return;
        }
      }
    };
    deityTable.addMouseListener(deityMouse);

      final MouseListener domainMouse = new MouseAdapter() {
        public void mouseClicked(MouseEvent f) {
          ListSelectionModel lsm = domainTable.getSelectionModel();
          if (f.getClickCount() == 1) {
            int row = lsm.getMinSelectionIndex();
            if (row < 0)
              return;
            final int selectedRow = sorter2.getRowTranslated(row);
            if (selectedRow < 0)
              return;
            final String domainName = domModel.getValueAt(selectedRow, -1).toString();
            if (domainName!=null) {
              final Domain aDomain = Globals.getDomainNamed(domainName);
              if (aDomain!=null) {
                domainInfo.setText(PCGen_Frame1.breakupString("<html><b>"+aDomain.getName()+
                           " &nbsp;Granted Power</b>:"+aDomain.getGrantedPower()+
                           " &nbsp;<b>Requirements</b>:"+aDomain.preReqHTMLStrings(false)+
                           "</html>",(int)domainScroll.getSize().getWidth()));
              }
            }
            return;
          }
          if (f.getClickCount() == 2) {
            int row = lsm.getMinSelectionIndex();
            if (row < 0)
              return;
            final int selectedRow = sorter2.getRowTranslated(row);
            if (selectedRow < 0)
              return;
            Globals.getCurrentPC().setDirty(true);
            final String domainName = InfoDomain.this.domModel.getValueAt(selectedRow,-1).toString();
            Domain aDomain = Globals.getDomainNamed(domainName);
            if (aDomain == null || !aDomain.qualifiesForDomain()) {
              JOptionPane.showMessageDialog(null, "You do not qualify for "+domainName, "PCGen", JOptionPane.INFORMATION_MESSAGE);
              return;
            }
            for(int i=0;i<aPC.getCharacterDomainList().size();i++) {
              final CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(i);
              if (aCD==null)
                continue;
              final Domain bDomain = aCD.getDomain();
              if (bDomain!=null && bDomain.getName().equalsIgnoreCase(domainName)) {
                pcDomainList.remove(domainName);
                aCD.setDomain(null);
                break;
              }
              if (bDomain==null || i==aPC.getCharacterDomainList().size()-1) {
                aDomain = (Domain)aDomain.clone();
                if (bDomain!=null)
                  pcDomainList.remove(bDomain.getName());
                aCD.setDomain(aDomain);
                aDomain.setIsLocked(true);
                pcDomainList.add(domainName);
                break;
              }
            }
            domChosen.setValue(pcDomainList.size());
            domModel.fireTableDataChanged();
            return;
          }
          else {
            return;
          }
        }
      };
      // true by default
      domainTable.addMouseListener(domainMouse);

    JScrollPane scrollPane = new JScrollPane(deityTable);    
    JScrollPane scrollPane2 = new JScrollPane(domainTable);
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    this.setLayout(gridbag);

    buildConstraints(c, 0, 0, 2, 1, 0, 80);
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    gridbag.setConstraints(scrollPane, c);
    this.add(scrollPane);
    
    buildConstraints(c, 2, 0, 1, 1, 6, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    JLabel label = new JLabel();
    gridbag.setConstraints(label, c);
    this.add(label);
    
    buildConstraints(c, 3, 0, 5, 1, 0, 0);
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    gridbag.setConstraints(scrollPane2, c);
    this.add(scrollPane2);
    
    buildConstraints(c, 0, 1, 1, 1, 27, 3);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    JLabel aLabel= new JLabel("Deity:");
    gridbag.setConstraints(aLabel, c);
    this.add(aLabel);
    
    buildConstraints(c, 1, 1, 1, 1, 27, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(deityName, c);
    this.add(deityName);
   
    buildConstraints(c, 3, 1, 1, 1, 10, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    gridbag.setConstraints(domSelected, c);
    this.add(domSelected);
    
    buildConstraints(c, 4, 1, 1, 1, 10, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(domChosen, c);
    domChosen.setEditable(false);
    this.add(domChosen);
    
    buildConstraints(c, 5, 1, 1, 1, 2, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    gridbag.setConstraints(ofLabel, c);
    this.add(ofLabel);
     
    buildConstraints(c, 6, 1, 1, 1, 10, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(domTotal, c);
    domTotal.setEditable(false);
    this.add(domTotal);
    
    buildConstraints(c, 7, 1, 1, 1, 8, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    label = new JLabel();
    gridbag.setConstraints(label, c);
    this.add(label);

    //domField.setMinimumSize(new Dimension(50, 21));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    //Info Panels.
    buildConstraints(c, 0, 2, 2, 1, 0, 40);
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Deity Info");
    title1.setTitleJustification(TitledBorder.CENTER);
    deityScroll.setBorder(title1);
    deityScroll.setViewportView(deityInfo);
    deityScroll.setToolTipText("Any requirements you don't meet are in italics.");
    gridbag.setConstraints(deityScroll, c);
    this.add(deityScroll);
   
    buildConstraints(c, 3, 2, 5, 1, 0, 0);
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    TitledBorder title2 = BorderFactory.createTitledBorder(etched, "Domain Info");
    title2.setTitleJustification(TitledBorder.CENTER);
    domainScroll.setBorder(title2);
    domainScroll.setViewportView(domainInfo);
    domainScroll.setToolTipText("Any requirements you don't meet are in italics.");
    gridbag.setConstraints(domainScroll, c);
    this.add(domainScroll);

    addComponentListener(new java.awt.event.ComponentAdapter()
    {
      public void componentShown(java.awt.event.ComponentEvent evt)
      {
        formComponentShown(evt);
      }
    });

  }

    // executed when the component is shown
    private void formComponentShown(ComponentEvent evt)
    {
      requestDefaultFocus();
      PCGen_Frame1.getStatusBar().setText("Selected deity and domains are bolded. Unqualified deities "+
		" and domains are in red.");
      updateCharacterInfo();
    }

    

  /** <code>updateCharacterInfo</code> update data listening for a changed PC
   */
  public void updateCharacterInfo()
  {
    final PlayerCharacter bPC = Globals.getCurrentPC();
    if (aPC==bPC && !needsUpdate)
      return;

    pcDomainList.clear();
    ArrayList dataList = new ArrayList();
    aPC=bPC;
    if (aPC!=null && aPC.getDeity()!=null)
      deityName.setText(aPC.getDeity().getName());
    else
      deityName.setText("None");
    if (aPC != null)
    {
      for (int i = 0; i < aPC.getCharacterDomainList().size(); i++)
      {
        final CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(i);
        // for each domain, if it's not in the list already, add it
        if (aCD!=null && aCD.getDomain()!=null)
          if (!dataList.contains(aCD.getDomain().getName())) {
            if (!Filter.showQualifiedOnly || (Filter.showQualifiedOnly && aCD.getDomain().qualifiesForDomain()))
              dataList.add(aCD.getDomain().getName()); // list of available choices
            pcDomainList.add(aCD.getDomain().getName()); // list of pc's choices
          }
      }
      domTotal.setValue(aPC.getCharacterDomainList().size());
      domChosen.setValue(pcDomainList.size());
      if (aPC.getDeity()!=null) {
        final StringTokenizer aTok = new StringTokenizer(aPC.getDeity().domainListString(),"|,",false);
        while(aTok.hasMoreElements()) {
          final String aString = aTok.nextToken();
          boolean addIt=true;
          if (Filter.showQualifiedOnly) {
            final Domain aDomain = Globals.getDomainNamed(aString);
            addIt = (aDomain!=null && aDomain.qualifiesForDomain());
          }
          if (addIt && !dataList.contains(aString))
            dataList.add(aString);
        }
      }
    }
    domModel.setData(dataList);
    sorter2.setModel(domModel);
    sorter2.tableChanged(null);
    domModel.fireTableDataChanged();
  }

  public void selectDeityIndex(int selectedRow)
  {
    final String selectedDeityName = dataModel.getValueAt(selectedRow, -1).toString();
    String domains = "";
    ArrayList dataList = new ArrayList();
    int currentDomainNum = 0;
    int totalDomainNum = aPC.getCharacterDomainList().size();

    final Deity aDeity = Globals.getDeityNamed(selectedDeityName);
    if (aDeity==null || !aPC.canSelectDeity(aDeity)) {
      final ListSelectionModel lsm = deityTable.getSelectionModel();
      JOptionPane.showMessageDialog(null, "You do not meet the requirements for "+selectedDeityName+".", "PCGen", JOptionPane.INFORMATION_MESSAGE);
      lsm.clearSelection();
      return;
    }
    aPC.setDeity(aDeity);
    deityName.setText(selectedDeityName);
    pcDomainList.clear();
    for (Iterator ii = aPC.getCharacterDomainList().iterator(); ii.hasNext();)
    {
      final CharacterDomain aCD = (CharacterDomain)ii.next();
      final Domain aDomain = aCD.getDomain();
      boolean addIt = (Filter.showQualifiedOnly==false || (aDomain!=null && aDomain.qualifiesForDomain()));
      if (aDomain != null && !dataList.contains(aDomain.getName())) {
        if (addIt)
          dataList.add(aDomain.getName());
        pcDomainList.add(aDomain.getName());
      }
    }

    StringTokenizer aTok = new StringTokenizer(aDeity.domainListString(),"|,", false);
    if (aDeity.domainListString().equalsIgnoreCase("ALL"))
    {
      for (Iterator i = Globals.getDomainList().iterator(); i.hasNext();)
      {
        final Domain aDomain = (Domain)i.next();
        boolean addIt = (Filter.showQualifiedOnly==false || (aDomain!=null && aDomain.qualifiesForDomain()));
        if (addIt && !dataList.contains(aDomain.getName()))
          dataList.add(aDomain.getName());
      }
    }
    else
    {
      while (aTok.hasMoreTokens())
      {
        String aToken = aTok.nextToken();
        final Domain aDomain = Globals.getDomainNamed(aToken);
        boolean addIt = (Filter.showQualifiedOnly==false || (aDomain!=null && aDomain.qualifiesForDomain()));

        if (addIt && aDomain != null && !dataList.contains(aToken)) {
          dataList.add(aToken);
        }
      }
    }
    domModel.setData(dataList);
    sorter2.setModel(domModel);
    sorter2.tableChanged(null);
    domModel.fireTableDataChanged();
    dataModel.fireTableDataChanged();
  }


/**
 * This is the Model that populate the table for Deities
 */
  public static final class DeityModel extends AbstractTableModel
  {
    private ArrayList deitys = Globals.getDeityList();
   /**
     * Return the value of a grid cell by using the information from the global
     * feat list.
     *
     * @see pcgen.core.Globals
     */
    public Object getValueAt(int row, int column)
    {
      Object retVal = "";
      if (deitys != null && row >= 0 && row < deitys.size())
      {
        final Deity aDeity = (Deity)deitys.get(row);
        final PlayerCharacter aPC = InfoDomain.aPC;
        switch (column)
        {
          case -1: // sneaky case to just get the name sans html tags
            return aDeity.getName();
          case 0:
            if (aPC!=null && aPC.getDeity()!=null && aPC.getDeity().getName().equals(aDeity.getName()))
              retVal = "<html><b>"+aDeity.getName()+"</b></html>";
            else if (aPC!=null && aPC.canSelectDeity(aDeity))
              retVal = aDeity.getName();
            else
              retVal = "<html><font color=red>"+aDeity.getName()+"</font></html>";
            break;
          case 1:
            retVal = aDeity.domainListString();
            break;
          case 2:
            retVal = aDeity.getAlignment();
            break;
          case 3:
            retVal = aDeity.getSource();
            break;

          }
      }
      return retVal;
    }

    /**
     * Return the current number of rows in the table based on the value from
     * the global feat list.
     *
     * @return the number of rows
     */
    public int getRowCount()
    {
      if (Globals.getDeityList() != null)
      {
        return deitys.size();
      }
      else
      {
        return 0;
      }
    }

    /**
     * Return the column name.
     *
     * @param column the number of the column 0...getColumnCount()-1.
     * @return the name of the column
     */
    public String getColumnName(int column)
    {
      return s_columnNames[column];
    }

    /**
     * Return the number of columns in the table.
     *
     * @return the number of columns
     */
    public int getColumnCount()
    {
      return s_columnNames.length;
    }

    public Class getColumnClass(int columnIndex)
    {
      return String.class;
    }

    public boolean isCellEditable(int row, int column)
    {
      return false;
    }
  }


/**
 * This is the Model that populate the table for Domains
 */

  public class DomainModel extends AbstractTableModel
  {
    protected ArrayList dataList = new ArrayList();

    public DomainModel()
    {
    }

    /* return list of domains associated with the current deity */
    /* sets the list of appropriate choices */
    /* returns the list of selections in order of selection */
    public ArrayList dataList()
    {
      return dataList;
    }

    public void setData(ArrayList aList)
    {
      dataList = aList;
    }

    // These methods always need to be implemented.
    public int getColumnCount()
    {
      return s_domainColList.length;
    }

    public int getRowCount()
    {
      return dataList.size();
    }

    public Object getValueAt(int row, int col)
    {
      if (row<0 || row >= dataList.size())
        return "";
      String retVal = (String)dataList.get(row);
      switch (col)
      {
        case -1: // return name of item without html tags
          break;
        case 0:
          final Domain aDomain = Globals.getDomainNamed(retVal);
          if (InfoDomain.pcDomainList.contains(dataList.get(row)))
            retVal = "<html><b>"+retVal+"</b></html>";
          else if (aDomain!=null && !aDomain.qualifiesForDomain())
            retVal = "<html><font color=red>"+retVal+"</font></html>";
          break;
        case 1: try {
                  retVal = Globals.getDomainNamed(retVal).getSource();
                } catch (Exception exc) {
                  System.out.println("Error getting domain");
                }
          break;
      }
      return retVal;
    }

    // The default implementations of these methods in
    // AbstractTableModel would work, but we can refine them.
    public String getColumnName(int column)
    {
      return s_domainColList[column];
    }

    public Class getColumnClass(int col)
    {
      return getValueAt(0, col).getClass();
    }

  }

}

