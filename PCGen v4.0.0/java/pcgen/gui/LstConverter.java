/*
 * LstConverter.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on June 14, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainSource} also loads any
 * default campaigns.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
class LstConverter extends JFrame
{
	private LstConverter mainClass;
	private List lstNameList = new ArrayList(); // list of file names
	private List lstPathList = new ArrayList(); // list of paths
	private List lstTypeList = new ArrayList(); // list of types
	private List okList = new ArrayList(); // list of items to be run
	private List doneList = new ArrayList(); // list of items already run
	private String basePath = "";
	protected TableSorter sorter = new TableSorter();
	private static final String[] typeTypes =
	  {
		  "UNKNOWN",
		  "RACE",
		  "CLASS",
		  "SPELL",
		  "DEITY",
		  "DOMAIN",
		  "SKILL"
	  };
	private static final String[] okTypes =
	  {
		  "NO",
		  "YES"
	  };

	/**
	 * Screen initialization. Override close.
	 * <p>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 */
	public LstConverter(String argBasePath)
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Convert Required fields to tagged format");
		basePath = argBasePath;
		try
		{
			loadLSTFilesInDirectory(basePath);
			jbInit();
		}
		catch (Exception e) //This is what jbInit throws...
		{
			e.printStackTrace();
		}
	}

	/**
	 * Real screen initialization is done here. Sets up all
	 * the window properties (icon, title, size).
	 * <p>
	 *
	 * @exception  Exception  Any Exception
	 */
	private void jbInit() throws Exception
	{
		JScrollPane lstScrollPane = new JScrollPane();

		final JTable lstTable = new JTable();
		LstTableModel lstTableModel = new LstTableModel();
		sorter.setModel(lstTableModel);
		sorter.addMouseListenerToHeaderInTable(lstTable);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(lstScrollPane, BorderLayout.CENTER);
		setSize(new Dimension(700, 500));
		lstTable.setModel(sorter);
		lstTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		lstTable.getColumnModel().getColumn(1).setPreferredWidth(160);
		lstTable.getColumnModel().getColumn(2).setCellRenderer(new TypeRenderer(typeTypes));
		lstTable.getColumnModel().getColumn(2).setCellEditor(new TypeEditor(typeTypes));
		lstTable.getColumnModel().getColumn(3).setCellRenderer(new OkRenderer(okTypes));
		lstTable.getColumnModel().getColumn(3).setCellEditor(new OkEditor(okTypes));
		lstTable.getColumnModel().getColumn(4).setCellRenderer(new DoneRenderer(okTypes));
		lstTable.getColumnModel().getColumn(4).setCellEditor(new DoneEditor(okTypes));

		lstTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lstTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstScrollPane.setViewportView(lstTable);
		JPanel aPanel = new JPanel();
		getContentPane().add(aPanel, BorderLayout.SOUTH);
		final JButton rButton = new JButton("Race");
		final JButton cButton = new JButton("Class");
		final JButton sButton = new JButton("Spell");
		final JButton dButton = new JButton("Deity");
		final JButton oButton = new JButton("Domain");
		final JButton kButton = new JButton("Skill");
		final JButton aButton = new JButton("All");
		final JButton goButton = new JButton("Run!");
		aPanel.add(goButton);
		aPanel.add(new JLabel("Toggle:"));
		aPanel.add(aButton);
		aPanel.add(rButton);
		aPanel.add(cButton);
		aPanel.add(sButton);
		aPanel.add(dButton);
		aPanel.add(oButton);
		aPanel.add(kButton);
		aButton.setToolTipText("Toggle convert-me status of all known file types");
		rButton.setToolTipText("Toggle convert-me status of all race files");
		cButton.setToolTipText("Toggle convert-me status of all class files");
		sButton.setToolTipText("Toggle convert-me status of all spell/power files");
		dButton.setToolTipText("Toggle convert-me status of all deity files");
		oButton.setToolTipText("Toggle convert-me status of all domain files");
		kButton.setToolTipText("Toggle convert-me status of all skill files");
		goButton.setToolTipText("Convert files from using required fields to use tagged format");
		ActionListener eventListener =
		  new ActionListener()
		  {
			  public void actionPerformed(ActionEvent evt)
			  {
				  if (evt.getSource() == goButton)
				  {
					  go();
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == rButton)
				  {
					  toggleType(1);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == cButton)
				  {
					  toggleType(2);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == sButton)
				  {
					  toggleType(3);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == dButton)
				  {
					  toggleType(4);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == oButton)
				  {
					  toggleType(5);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == kButton)
				  {
					  toggleType(6);
					  lstTable.updateUI();
				  }
				  if (evt.getSource() == aButton)
				  {
					  for (int i = 1; i < 7; i++)
						  toggleType(i);
					  lstTable.updateUI();
				  }
			  }
		  };
		rButton.addActionListener(eventListener);
		cButton.addActionListener(eventListener);
		sButton.addActionListener(eventListener);
		dButton.addActionListener(eventListener);
		oButton.addActionListener(eventListener);
		kButton.addActionListener(eventListener);
		aButton.addActionListener(eventListener);
		goButton.addActionListener(eventListener);
	}

	public void toggleType(int x)
	{
		for (int i = 0; i < okList.size(); i++)
		{
			int y = Integer.parseInt(lstTypeList.get(i).toString());
			if (x == y)
			{
				y = Integer.parseInt(okList.get(i).toString());
				if (y == 0)
					okList.set(i, "1");
				else
					okList.set(i, "0");
			}
		}
	}

	public void go()
	{
		for (int i = 0; i < okList.size(); i++)
		{
			if (okList.get(i).toString().equals("0"))
				continue;
			int thisType = Integer.parseInt(lstTypeList.get(i).toString());
			if (thisType == 0)
				Globals.errorPrint(lstNameList.get(i).toString() + " is UNKNOWN - not converting");
			else
				Globals.debugPrint("Converting ", lstNameList.get(i).toString());
			File aFile = new File(lstPathList.get(i).toString() + File.separatorChar + lstNameList.get(i).toString());
			try
			{
				FileInputStream aStream = new FileInputStream(aFile);
				int length = (int)aFile.length();
				byte[] inputLine = new byte[length];
				aStream.read(inputLine, 0, length);
				FileOutputStream bStream = new FileOutputStream(aFile);
				String aString = new String(inputLine);
				StringTokenizer aTok = new StringTokenizer(aString, "\r\n", true);
				while (aTok.hasMoreTokens())
				{
					String line = aTok.nextToken();
					byte[] outline = line.getBytes();
					if (line.equals("\r") || line.equals("\n") || line.trim().length() == 0 || line.startsWith("#"))
					{
						bStream.write(outline);
						continue;
					}
					StringTokenizer bTok = new StringTokenizer(line, "\t", false);
					boolean hasTagless = false;
					bTok.nextToken();
					if (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						if (bString.indexOf(":") == -1)
							hasTagless = true;
					}
					bTok = new StringTokenizer(line, "\t", true); // reset tokenizer
					int field = 0;
					if (!hasTagless)
						field=100;
					while (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						if (bString.equals("\t"))
						{
							bStream.write(bString.getBytes());
							continue;
						}
						if (field++ == 0)
						{
							bStream.write(bString.getBytes());
							continue;
						}

						switch (thisType)
						{
							case 1: // race
								if ((!hasTagless && bString.startsWith("STATADJ")) ||
									(field > 1 && field < 8))
								{
									int statNum = field-2;
									if (!hasTagless)
									{
										statNum = Integer.parseInt(bString.substring(7,8));
										bString = bString.substring(9);
									}
									if (!bString.equals("0"))
									{
										bString = "BONUS:STAT|"+((PCStat)Globals.getStatList().get(statNum)).getAbb()+"|"+bString;
										bStream.write(bString.getBytes());
									}
								}
								else if (field == 8)
								{
									bString = "FAVCLASS:" + bString;
									bStream.write(bString.getBytes());
								}
								else if (field == 9)
								{
									if (!bString.equals("0"))
									{
										bString = "XTRASKILLPTSPERLVL:" + bString;
										bStream.write(bString.getBytes());
									}
								}
								else if (field == 10)
								{
									if (!bString.equals("0"))
									{
										bString = "STARTFEATS:" + bString;
										bStream.write(bString.getBytes());
									}
								}
								else
									bStream.write(bString.getBytes());
								break;
							case 2: // class
								if (bString.startsWith("INTMODTOSKILLS"))
								{
									bString = bString.substring(3); // remove INT prefix
								}
								else if (bString.startsWith("GOLD:"))
								{
									bString = ""; // tag was removed for license compliance
								}
								else if (field == 2)
									continue; // alignment string is ignored
								else if (field == 3)
									bString = "HD:" + bString;
								else if (field == 4)
									bString = "STARTSKILLPTS:" + bString;
								else if (field == 5)
									bString = "XTRAFEATS:" + bString;
								else if (field == 6)
									bString = "SPELLSTAT:" + bString;
								else if (field == 7)
									bString = "SPELLTYPE:" + bString;
								else if ((field == 8) || bString.startsWith("BAB:"))
								{
									if (bString.startsWith("BAB:"))
									{
										bString = bString.substring(4);
									}
									bString = getFormulaFor(0, bString);
									bString = "BONUS:COMBAT|BAB|" + bString;
//									BAB: was removed in v3.1.0
								}
								else if (field == 9 || (!hasTagless && bString.startsWith("FORTITUDECHECK:")) ||
									(!hasTagless && bString.startsWith("CHECK1:")))
								{
//									FORTITUDECHECK has been replaced in v3.1.0
									if (bString.startsWith("FORT"))
										bString = bString.substring(15);
									if (bString.startsWith("CHECK1"))
										bString = bString.substring(7);
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."+Globals.getCheckList().get(0).toString().toUpperCase()+"|"+bString;
								}
								else if (field == 10 || (!hasTagless && bString.startsWith("REFLEXCHECK:")) ||
									(!hasTagless && bString.startsWith("CHECK2:")))
								{
//									REFLEXCHECK has been replaced in v3.1.0
									if (!hasTagless)
										bString = bString.substring(12);
									if (bString.startsWith("CHECK2"))
										bString = bString.substring(7);
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."+Globals.getCheckList().get(1).toString().toUpperCase()+"|"+bString;
								}
								else if (field == 11 || (!hasTagless && bString.startsWith("WILLPOWERCHECK:")) ||
									(!hasTagless && bString.startsWith("CHECK3:")))
								{
//									WILLPOWERCHECK has been replaced in v3.1.0
									if (!hasTagless)
										bString = bString.substring(15);
									if (bString.startsWith("CHECK3"))
										bString = bString.substring(7);
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."+Globals.getCheckList().get(2).toString().toUpperCase()+"|"+bString;
								}

								bStream.write(bString.getBytes());
								break;
							case 3: // spell
								if (field == 2)
									bString = "SCHOOL:" + bString;
								else if (field == 3)
									bString = "SUBSCHOOL:" + bString;
								else if (field == 4)
									bString = "COMPS:" + bString;
								else if (field == 5)
									bString = "CASTTIME:" + bString;
								else if (field == 6)
									bString = "RANGE:" + bString;
								else if (field == 7)
									bString = "EFFECTS:" + bString;
								else if (field == 8)
									bString = "EFFECTTYPE:" + bString;
								else if (field == 9)
									bString = "DURATION:" + bString;
								else if (field == 10)
									bString = "SAVEINFO:" + bString;
								else if (field == 11)
									bString = "SPELLRES:" + bString;
								bStream.write(bString.getBytes());
								break;
							case 4: // deity
								if (field == 2)
									bString = "DOMAINS:" + bString;
								else if (field == 3)
									bString = "FOLLOWERALIGN:" + bString;
								else if (field == 4)
									bString = "DESC:" + bString;
								else if (field == 5)
									bString = "SYMBOL:" + bString;
								else if (field == 6)
									bString = "DEITYWEAP:" + bString;
								bStream.write(bString.getBytes());
								break;
							case 5: // domain
								if (field == 2)
									bString = "DESC:" + bString;
								bStream.write(bString.getBytes());
								break;
							case 6:  // skill
								if (field == 2)
									bString = "KEYSTAT:" + bString;
								else if (field == 3)
									bString = "EXCLUSIVE:" + bString;
								else if (field == 4)
									bString = "USEUNTRAINED:" + bString;
								bStream.write(bString.getBytes());
								break;
							default:
								Globals.errorPrint("In LstConverter.go the type " + thisType + " is not handled.");
								break;
						}
					} // end while
				}
				okList.set(i, "0");
				doneList.set(i, "1");
				bStream.close();
			}
			catch (Exception e)
			{
				Globals.errorPrint("", e);
			}
		}
	}

	// type==0 is BAB
	// type==1 is Check
	public String getFormulaFor(int type, String formulaString)
	{
		if (type==0)
		{
			if (formulaString.equals("G"))
				return "CL";
			if (formulaString.equals("M"))
				return "3*CL/4";
			if (formulaString.equals("B"))
				return "CL/2";
		}
		else
		{
			if (formulaString.equals("G"))
				return "(CL/2)+2";
			if (formulaString.equals("M"))
				return "1+((CL/5).INTVAL)+(((CL+3)/5).INTVAL)";
			if (formulaString.equals("B"))
				return "CL/3";
		}
		Globals.errorPrint("bad formula String:"+formulaString);
		return formulaString;
	}

	public String getBasePath()
	{
		return basePath;
	}

	public void setMainClass(LstConverter owner)
	{
		mainClass = owner;
	}

	/**
	 * Overridden so we can handle exit on System Close
	 * by calling <code>handleQuit</code>.
	 */
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit();
		}
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 */
	void handleQuit()
	{
		this.dispose();
	}

	private boolean loadLSTFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				try
				{
					aString = aString.toLowerCase();
					if (aString.endsWith(".lst"))
					{
						lstNameList.add(aString);
						lstPathList.add(aFile.getPath());
						int ok = 1;
						if (aString.endsWith("race.lst") || aString.endsWith("races.lst"))
							lstTypeList.add("1");
						else if (aString.endsWith("class.lst") || aString.endsWith("classes.lst"))
							lstTypeList.add("2");
						else if ((aString.endsWith("spell.lst") || aString.endsWith("spells.lst") ||
						  aString.endsWith("power.lst") || aString.endsWith("powers.lst")) &&
						  aString.indexOf("classspell") == -1 && aString.indexOf("classpowers") == -1)
							lstTypeList.add("3");
						else if (aString.endsWith("deity.lst") || aString.endsWith("deities.lst"))
							lstTypeList.add("4");
						else if (aString.endsWith("domain.lst") || aString.endsWith("domains.lst"))
							lstTypeList.add("5");
						else if ((aString.endsWith("skill.lst") || aString.endsWith("skills.lst")) &&
						  aString.indexOf("classskill") == -1)
							lstTypeList.add("6");
						else
						{
							ok = 0;
							lstTypeList.add("0"); // unknown
						}
						if (ok > 0)
							okList.add("1"); // default to OK
						else
							okList.add("0");
						doneList.add("0"); // default to not-done
					}
					else if (aFile.isDirectory())
						loadLSTFilesInDirectory(aFile.getPath() + File.separator + aString);
				}
				catch (Exception e)
				{
					// LATER: This is not an appropriate way to deal with this exception.
					// Deal with it this way because of the way the loading takes place.  XXX
					Globals.errorPrint("LstConverter", e);
				}
				return false;
			}
		});
		return false;
	}

	protected class LstTableModel extends AbstractTableModel
	{
		public LstTableModel()
		{
		}

		public void fireTableDataChanged()
		{
			super.fireTableDataChanged();
		}

		public int getColumnCount()
		{
			return 5;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public int getRowCount()
		{
			return lstNameList.size();
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "File Name";
				case 1:
					return "Path";
				case 2:
					return "Type";
				case 3:
					return "Convert Me";
				case 4:
					return "Converted";
				default:
					Globals.errorPrint("In LstConverter.LstTableModel.getColumnName the column " + columnIndex + " is not handled.");
					break;
			}
			return "Out Of Bounds";
		}

		public boolean isCellEditable(int row, int column)
		{
			return (column == 2 || column == 3);
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (lstNameList.size() <= rowIndex || columnIndex > 4)
				return "Out of Bounds";

			switch (columnIndex)
			{
				case 0:
					return lstNameList.get(rowIndex);
				case 1:
					return lstPathList.get(rowIndex);
				case 2:
					int x = Integer.parseInt(lstTypeList.get(rowIndex).toString());
					return new Integer(x);
				case 3:
					int ok = Integer.parseInt(okList.get(rowIndex).toString());
					return new Integer(ok);
				case 4:
					ok = Integer.parseInt(doneList.get(rowIndex).toString());
					return new Integer(ok);
				default:
					Globals.errorPrint("In LstConverter.LstTableModel.getValueAt the column " + columnIndex + " is not handled.");
					break;
			}
			return null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (aValue == null || columnIndex < 1 || columnIndex > 4)
				return;
			final Integer i = new Integer(aValue.toString());
			switch (columnIndex)
			{
				case 2:
					lstTypeList.set(rowIndex, i);
					// if type is set to UNKNOWN, we can't convert it
					if (i.intValue() == 0)
						okList.set(rowIndex, i);
					break;
				case 3:
					if (i.intValue() == 1)
					{
						final Integer j = new Integer(lstTypeList.get(rowIndex).toString());
						if (j.intValue() == 0)
						{
							JOptionPane.showMessageDialog(null, "Set type to a known type before marking it to be converted.", "Oops!", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					okList.set(rowIndex, i);
					break;
				default:
					Globals.errorPrint("In LstConverter.LstTableModel.setValueAt the column " + columnIndex + " is not handled.");
					break;
			}
		}

	}

	static final class OkRenderer extends JComboBox implements TableCellRenderer
	{

		public OkRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable,
		  Object value,
		  boolean isSelected,
		  boolean hasFocus,
		  int row,
		  int column)
		{
			if (value == null)
				return null;
			int i = -1;
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class OkEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		public OkEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			}
			);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null) return null;
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			return new Integer(getSelectedIndex());
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	static final class TypeRenderer extends JComboBox implements TableCellRenderer
	{

		public TypeRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable,
		  Object value,
		  boolean isSelected,
		  boolean hasFocus,
		  int row,
		  int column)
		{
			if (value == null)
				return null;
			int i = -1;
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class TypeEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		public TypeEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			}
			);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null) return null;
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			return new Integer(getSelectedIndex());
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	static final class DoneRenderer extends JComboBox implements TableCellRenderer
	{

		public DoneRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable,
		  Object value,
		  boolean isSelected,
		  boolean hasFocus,
		  int row,
		  int column)
		{
			if (value == null)
				return null;
			int i = -1;
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class DoneEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		public DoneEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			}
			);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null) return null;
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
				i = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				i = ((Integer)value).intValue();
			if (i < 0 || i >= this.getItemCount())
				i = 0;
			setSelectedIndex(i);
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			return new Integer(getSelectedIndex());
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	}
}
