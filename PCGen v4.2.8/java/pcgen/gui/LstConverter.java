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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainSource} also loads any
 * default campaigns.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */

interface LstConverterConstants
{
	int CONVERT_RACETYPE = 1;
	int CONVERT_CLASSTYPE = 2;
	int CONVERT_SPELLTYPE = 3;
	int CONVERT_DEITYTYPE = 4;
	int CONVERT_DOMAINTYPE = 5;
	int CONVERT_SKILLTYPE = 6;
	int CONVERT_FEATTYPE = 7;
	int CONVERT_TEMPLATETYPE = 8;
}

final class LstConverter extends JFrame
{
	private List lstNameList = new ArrayList(); // list of file names
	private List lstPathList = new ArrayList(); // list of paths
	private List lstTypeList = new ArrayList(); // list of types
	private List okList = new ArrayList(); // list of items to be run
	private List doneList = new ArrayList(); // list of items already run
	private String basePath = "";
	private TableSorter sorter = new TableSorter();
	private static final String[] typeTypes = {"UNKNOWN", "RACE", "CLASS", "SPELL", "DEITY", "DOMAIN", "SKILL", "FEAT", "TEMPLATE"};
	private static final String[] okTypes = {"NO", "YES"};

	/**
	 * Screen initialization. Override close.
	 * <p>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 */
	LstConverter(String argBasePath)
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
			Globals.errorPrint("Error while initing form", e);
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
		final JButton fButton = new JButton("Feat");
		final JButton tButton = new JButton("Template");
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
		aPanel.add(fButton);
		aPanel.add(tButton);
		aButton.setToolTipText("Toggle convert-me status of all known file types");
		rButton.setToolTipText("Toggle convert-me status of all race files");
		cButton.setToolTipText("Toggle convert-me status of all class files");
		sButton.setToolTipText("Toggle convert-me status of all spell/power files");
		dButton.setToolTipText("Toggle convert-me status of all deity files");
		oButton.setToolTipText("Toggle convert-me status of all domain files");
		kButton.setToolTipText("Toggle convert-me status of all skill files");
		fButton.setToolTipText("Toggle convert-me status of all feat files");
		tButton.setToolTipText("Toggle convert-me status of all template files");
		goButton.setToolTipText("Convert files from using required fields to use tagged format");
		ActionListener eventListener = new ActionListener()
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
				if (evt.getSource() == fButton)
				{
					toggleType(7);
					lstTable.updateUI();
				}
				if (evt.getSource() == tButton)
				{
					toggleType(8);
					lstTable.updateUI();
				}
				if (evt.getSource() == aButton)
				{
					for (int i = 1; i < 9; i++)
					{
						toggleType(i);
					}
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
		fButton.addActionListener(eventListener);
		tButton.addActionListener(eventListener);
		aButton.addActionListener(eventListener);
		goButton.addActionListener(eventListener);
	}

	private void toggleType(int x)
	{
		for (int i = 0; i < okList.size(); i++)
		{
			int y = Integer.parseInt(lstTypeList.get(i).toString());
			if (x == y)
			{
				y = Integer.parseInt(okList.get(i).toString());
				if (y == 0)
				{
					okList.set(i, "1");
				}
				else
				{
					okList.set(i, "0");
				}
			}
		}
	}

	private void go()
	{
		for (int i = 0; i < okList.size(); i++)
		{
			if ("0".equals(okList.get(i).toString()))
			{
				continue;
			}
			int thisType = Integer.parseInt(lstTypeList.get(i).toString());
			if (thisType == 0)
			{
				Globals.errorPrint(lstNameList.get(i).toString() + " is UNKNOWN - not converting");
			}
			else
			{
				Globals.debugPrint("Converting ", lstNameList.get(i).toString());
			}
			File conversionSource = new File(lstPathList.get(i).toString() + File.separatorChar + lstNameList.get(i).toString());
			try
			{
				//BufferedReader conversionReader = new BufferedReader(new FileReader(conversionSource));
				BufferedReader conversionReader = new BufferedReader(new InputStreamReader(new FileInputStream(conversionSource), "UTF-8"));
				int length = (int) conversionSource.length();
				char[] sourceInput = new char[length];
				conversionReader.read(sourceInput, 0, length);
				conversionReader.close();

				//BufferedWriter conversionWriter = new BufferedWriter(new FileWriter(conversionSource));
				BufferedWriter conversionWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conversionSource), "UTF-8"));
				String sourceInputString = new String(sourceInput);
				StringTokenizer sourceTokenizer = new StringTokenizer(sourceInputString, "\r\n", true);
				while (sourceTokenizer.hasMoreTokens())
				{
					String line = sourceTokenizer.nextToken();
					if ("\r".equals(line) || "\n".equals(line) || line.trim().length() == 0 || (line.length() > 0 && line.charAt(0) == '#'))
					{
						conversionWriter.write(line);
						continue;
					}
					StringTokenizer lineTokenizer = new StringTokenizer(line, "\t", false);
					boolean hasTagless = false;
					lineTokenizer.nextToken();
					if (lineTokenizer.hasMoreTokens())
					{
						String bString = lineTokenizer.nextToken();
						if (bString.indexOf(":") == -1)
						{
							hasTagless = true;
						}
					}
					lineTokenizer = new StringTokenizer(line, "\t", true); // reset tokenizer
					int field = 0;
					if (!hasTagless)
					{
						field = 100;
					}
					while (lineTokenizer.hasMoreTokens())
					{
						String bString = lineTokenizer.nextToken();
						if ("\t".equals(bString))
						{
							conversionWriter.write(bString);
							continue;
						}
						if (field++ == 0)
						{
							conversionWriter.write(bString);
							continue;
						}
						if (bString.startsWith("PREFORT:"))
						{
							conversionWriter.write("PRECHECK:1," + 
								Globals.getCheckList().get(0).toString().toUpperCase() + 
								"=" + bString.substring(8));
							continue;
						}
						else if (bString.startsWith("PREREFLEX:"))
						{
							conversionWriter.write("PRECHECK:1," + 
								Globals.getCheckList().get(1).toString().toUpperCase() + 
								"=" + bString.substring(10));
							continue;
						}
						else if (bString.startsWith("PREWILL:"))
						{
							conversionWriter.write("PRECHECK:1," + 
								Globals.getCheckList().get(2).toString().toUpperCase() + 
								"=" + bString.substring(8));
							continue;
						}
						else if (bString.startsWith("PREFORTBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1," + 
								Globals.getCheckList().get(0).toString().toUpperCase() + 
								"=" + bString.substring(12));
							continue;
						}
						else if (bString.startsWith("PREREFLEXBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1," + 
								Globals.getCheckList().get(1).toString().toUpperCase() + 
								"=" + bString.substring(14));
							continue;
						}
						else if (bString.startsWith("PREWILLBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1," + 
								Globals.getCheckList().get(2).toString().toUpperCase() + 
								"=" + bString.substring(12));
							continue;
						}

						switch (thisType)
						{
							case LstConverterConstants.CONVERT_RACETYPE: // race
								if ((!hasTagless && bString.startsWith("STATADJ")) || (field > 1 && field < 8))
								{
									int statNum = field - 2;
									if (!hasTagless)
									{
										statNum = Integer.parseInt(bString.substring(7, 8));
										bString = bString.substring(9);
									}
									if (!"0".equals(bString))
									{
										bString = "BONUS:STAT|" + ((PCStat) Globals.getStatList().get(statNum)).getAbb() + "|" + bString;
										conversionWriter.write(bString);
									}
								}
								else if (field == 8)
								{
									bString = "FAVCLASS:" + bString;
									conversionWriter.write(bString);
								}
								else if (field == 9)
								{
									if (!"0".equals(bString))
									{
										bString = "XTRASKILLPTSPERLVL:" + bString;
										conversionWriter.write(bString);
									}
								}
								else if (field == 10)
								{
									if (!"0".equals(bString))
									{
										bString = "STARTFEATS:" + bString;
										conversionWriter.write(bString);
									}
								}
								else
									conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_CLASSTYPE: // class
								if (bString.startsWith("INTMODTOSKILLS"))
								{
									bString = bString.substring(3); // remove INT prefix
								}
								else if (bString.startsWith("GOLD:") || bString.startsWith("AGESET:"))
								{
									bString = ""; // tag was removed for license compliance
								}
								else if (field == 2)
								{
									continue; // alignment string is ignored
								}
								else if (field == 3)
								{
									bString = "HD:" + bString;
								}
								else if (field == 4)
								{
									bString = "STARTSKILLPTS:" + bString;
								}
								else if (field == 5)
								{
									bString = "XTRAFEATS:" + bString;
								}
								else if (field == 6)
								{
									bString = "SPELLSTAT:" + bString;
								}
								else if (field == 7)
								{
									bString = "SPELLTYPE:" + bString;
								}
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
								else if (field == 9 || (!hasTagless && bString.startsWith("FORTITUDECHECK:")) || (!hasTagless && bString.startsWith("CHECK1:")))
								{
//									FORTITUDECHECK has been replaced in v3.1.0
									if (bString.startsWith("FORT"))
									{
										bString = bString.substring(15);
									}
									if (bString.startsWith("CHECK1"))
									{
										bString = bString.substring(7);
									}
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE." + Globals.getCheckList().get(0).toString().toUpperCase() + "|" + bString;
								}
								else if (field == 10 || (!hasTagless && bString.startsWith("REFLEXCHECK:")) || (!hasTagless && bString.startsWith("CHECK2:")))
								{
//									REFLEXCHECK has been replaced in v3.1.0
									if (!hasTagless)
									{
										bString = bString.substring(12);
									}
									if (bString.startsWith("CHECK2"))
									{
										bString = bString.substring(7);
									}
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE." + Globals.getCheckList().get(1).toString().toUpperCase() + "|" + bString;
								}
								else if (field == 11 || (!hasTagless && bString.startsWith("WILLPOWERCHECK:")) || (!hasTagless && bString.startsWith("CHECK3:")))
								{
//									WILLPOWERCHECK has been replaced in v3.1.0
									if (!hasTagless)
									{
										bString = bString.substring(15);
									}
									if (bString.startsWith("CHECK3"))
									{
										bString = bString.substring(7);
									}
									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE." + Globals.getCheckList().get(2).toString().toUpperCase() + "|" + bString;
								}

								conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_SPELLTYPE: // spell
								if (bString.startsWith("EFFECTS:"))
								{
									bString = "DESC:" + bString.substring(8);
								}
								else if (bString.startsWith("EFFECTTYPE:"))
								{
									bString = "TARGETAREA:" + bString.substring(11);
								}
								else if (field == 2)
								{
									bString = "SCHOOL:" + bString;
								}
								else if (field == 3)
								{
									bString = "SUBSCHOOL:" + bString;
								}
								else if (field == 4)
								{
									bString = "COMPS:" + bString;
								}
								else if (field == 5)
								{
									bString = "CASTTIME:" + bString;
								}
								else if (field == 6)
								{
									bString = "RANGE:" + bString;
								}
								else if (field == 7)
								{
									bString = "DESC:" + bString;
								}
								else if (field == 8)
								{
									bString = "TARGETAREA:" + bString;
								}
								else if (field == 9)
								{
									bString = "DURATION:" + bString;
								}
								else if (field == 10)
								{
									bString = "SAVEINFO:" + bString;
								}
								else if (field == 11)
								{
									bString = "SPELLRES:" + bString;
								}
								conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_DEITYTYPE: // deity
								if (field == 2)
								{
									bString = "DOMAINS:" + bString;
								}
								else if (field == 3)
								{
									bString = "FOLLOWERALIGN:" + bString;
								}
								else if (field == 4)
								{
									bString = "DESC:" + bString;
								}
								else if (field == 5)
								{
									bString = "SYMBOL:" + bString;
								}
								else if (field == 6)
								{
									bString = "DEITYWEAP:" + bString;
								}
								conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_DOMAINTYPE: // domain
								if (field == 2)
								{
									bString = "DESC:" + bString;
								}
								conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_SKILLTYPE:  // skill
								if (field == 2)
								{
									bString = "KEYSTAT:" + bString;
								}
								else if (field == 3)
								{
									bString = "EXCLUSIVE:" + bString;
								}
								else if (field == 4)
								{
									bString = "USEUNTRAINED:" + bString;
								}
								conversionWriter.write(bString);
								break;
							case LstConverterConstants.CONVERT_FEATTYPE:  // feat
							case LstConverterConstants.CONVERT_TEMPLATETYPE:  // template
								conversionWriter.write(bString);
								break;
							default:
								Globals.errorPrint("In LstConverter.go the type " + thisType + " is not handled.");
								break;
						}
					} // end while
				}
				okList.set(i, "0");
				doneList.set(i, "1");
				conversionWriter.close();
			}
			catch (Exception e)
			{
				Globals.errorPrint("", e);
			}
		}
	}

	// type==0 is BAB
	// type==1 is Check
	private static String getFormulaFor(int type, String formulaString)
	{
		String formula = formulaString;
		if (type == 0)
		{
			if ("G".equals(formulaString))
			{
				formula = "CL";
			}
			if ("M".equals(formulaString))
			{
				formula = "3*CL/4";
			}
			if ("B".equals(formulaString))
			{
				formula = "CL/2";
			}
		}
		else
		{
			if ("G".equals(formulaString))
			{
				formula = "(CL/2)+2";
			}
			if ("M".equals(formulaString))
			{
				formula = "1+((CL/5).INTVAL)+(((CL+3)/5).INTVAL)";
			}
			if ("B".equals(formulaString))
			{
				formula = "CL/3";
			}
		}
		if (formula.equals(formulaString))
		{
			Globals.errorPrint("bad formula String:" + formulaString);
		}
		return formula;
	}

	public String getBasePath()
	{
		return basePath;
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
	private void handleQuit()
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
						{
							lstTypeList.add("1");
						}
						else if (aString.endsWith("class.lst") || aString.endsWith("classes.lst"))
						{
							lstTypeList.add("2");
						}
						else if ((aString.endsWith("spell.lst") || aString.endsWith("spells.lst") || aString.endsWith("power.lst") || aString.endsWith("powers.lst")) && aString.indexOf("classspell") == -1 && aString.indexOf("classpowers") == -1)
						{
							lstTypeList.add("3");
						}
						else if (aString.endsWith("deity.lst") || aString.endsWith("deities.lst"))
						{
							lstTypeList.add("4");
						}
						else if (aString.endsWith("domain.lst") || aString.endsWith("domains.lst"))
						{
							lstTypeList.add("5");
						}
						else if ((aString.endsWith("skill.lst") || aString.endsWith("skills.lst")) && aString.indexOf("classskill") == -1)
						{
							lstTypeList.add("6");
						}
						else if (aString.endsWith("feat.lst") || aString.endsWith("feats.lst"))
						{
							lstTypeList.add("7");
						}
						else if (aString.endsWith("template.lst") || aString.endsWith("templates.lst"))
						{
							lstTypeList.add("8");
						}
						else
						{
							ok = 0;
							lstTypeList.add("0"); // unknown
						}
						if (ok > 0)
						{
							okList.add("1"); // default to OK
						}
						else
						{
							okList.add("0");
						}
						doneList.add("0"); // default to not-done
					}
					else if (aFile.isDirectory())
					{
						loadLSTFilesInDirectory(aFile.getPath() + File.separator + aString);
					}
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

	private final class LstTableModel extends AbstractTableModel
	{
		private LstTableModel()
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
			{
				return "Out of Bounds";
			}

			switch (columnIndex)
			{
				case 0:
					return lstNameList.get(rowIndex);
				case 1:
					return lstPathList.get(rowIndex);
				case 2:
					final int x = Integer.parseInt(lstTypeList.get(rowIndex).toString());
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
			{
				return;
			}
			final Integer i = new Integer(aValue.toString());
			switch (columnIndex)
			{
				case 2:
					lstTypeList.set(rowIndex, i);
					// if type is set to UNKNOWN, we can't convert it
					if (i.intValue() == 0)
					{
						okList.set(rowIndex, i);
					}
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

	private static final class OkRenderer extends JComboBox implements TableCellRenderer
	{

		private OkRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (value == null)
			{
				return null;
			}
			int i = -1;
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class OkEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		private OkEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null)
			{
				return null;
			}
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	private static final class TypeRenderer extends JComboBox implements TableCellRenderer
	{

		private TypeRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (value == null)
			{
				return null;
			}
			int i = -1;
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class TypeEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		private TypeEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null)
			{
				return null;
			}
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	private static final class DoneRenderer extends JComboBox implements TableCellRenderer
	{

		private DoneRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (value == null)
			{
				return null;
			}
			int i = -1;
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
			setSelectedIndex(i);
			return this;
		}

	}

	private static final class DoneEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		private DoneEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			int i = -1;
			if (value == null)
			{
				return null;
			}
			d_originalValue = getSelectedIndex();
			if (value instanceof String)
			{
				i = Integer.parseInt((String) value);
			}
			else if (value instanceof Integer)
			{
				i = ((Integer) value).intValue();
			}
			if (i < 0 || i >= this.getItemCount())
			{
				i = 0;
			}
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	}
}
