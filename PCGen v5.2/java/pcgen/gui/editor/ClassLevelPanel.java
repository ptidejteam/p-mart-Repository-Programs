/*
 * ClassLevelPanel
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id: ClassLevelPanel.java,v 1.1 2006/02/21 01:13:27 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.LevelAbility;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.TableSorter;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>ClassLevelPanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */

public class ClassLevelPanel extends JPanel implements PObjectUpdater
{
  static final long serialVersionUID = 1485178774957708877L;
	private JTextField level = new JTextField();
	private JButton addBtn = new JButton();
	private JButton delBtn = new JButton();
	private JComboBox tagList = new JComboBox();
	private LevelModel levelModel = new LevelModel();
	private JTableEx levelTable = new JTableEx();
	private JScrollPane levelPane;
	private TableSorter sortedLevelModel = new TableSorter();
	private static ArrayList levelTagList = new ArrayList();

	private PCClass obj = null;


	/** Creates new form ClassLevelPanel */
	public ClassLevelPanel()
	{
		initComponents();
		initComponentContents();
	}

	private void initComponentContents()
	{
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		JLabel tempLabel;
		for (int i = 0; i < LevelTag.validTags.length; ++i)
		{
			tagList.addItem(LevelTag.validTags[i]);
		}
/*		tagList.addItem("ADD");
		tagList.addItem("ADDOMAINS");
		tagList.addItem("BONUS");
		tagList.addItem("DEFINE");
		tagList.addItem("CAST");
		tagList.addItem("KNOWN");
		tagList.addItem("SPELL");
		tagList.addItem("DR");
		tagList.addItem("SA");
		tagList.addItem("SR");
		tagList.addItem("FEAT");
		tagList.addItem("FEATAUTO");
		tagList.addItem("VFEAT");
		tagList.addItem("KIT");
		tagList.addItem("REGION");
		tagList.addItem("TEMPLATE");
		tagList.addItem("UMULT");
		tagList.addItem("UDAM");
*/		tagList.setSelectedIndex(0);
		level.setText("1     ");

		setLayout(new BorderLayout());

		levelPane = new JScrollPane(levelTable);

		sortedLevelModel.setModel(levelModel);
		levelTable.setModel(sortedLevelModel);

		levelTable.setColAlign(0, SwingConstants.CENTER);

		levelPane.setViewportView(levelTable);
		levelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sortedLevelModel.addMouseListenerToHeaderInTable(levelTable);
		levelTable.getColumnModel().getColumn(0).setPreferredWidth(5);
		levelTable.getColumnModel().getColumn(1).setPreferredWidth(10);

		add(levelPane, BorderLayout.CENTER);

		JPanel sth = new JPanel();
		sth.setLayout(new FlowLayout());

		tempLabel = new JLabel("Level:");
		sth.add(tempLabel);

		sth.add(level);

		tempLabel = new JLabel("Tag:");
		sth.add(tempLabel);

		sth.add(tagList);

		addBtn.setText(PropertyFactory.getString("in_add"));
		sth.add(addBtn);
		addBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				String tag = tagList.getSelectedItem().toString();
				if (tag.equals("CAST") || tag.equals("KNOWN"))
				{
					String cols[] = {"Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7", "Level 8", "Level 9"};
					String values[] = {"", "", "", "", "", "", "", "", "", ""};
					MatrixFrame mf = new MatrixFrame(cols, 10, values, tag);
					String v = "";
					for (int col = 0; col < 10; ++col)
					{
						if (col > 0)
						{
							v += ",";
						}
						v += mf.fields[col];
					}
					int x = 0;
					while (v.endsWith(",") && x++ < 9)
					{
						v = v.substring(0, v.length() - 1);
					}
					if (!v.equals(""))
					{
						LevelTag lt = new LevelTag(level.getText().trim(), tag, v.trim(), true);
						levelTagList.add(lt);
						levelModel.updateModel();
					}
				}
				else if (tag.equals("FEAT") || tag.equals("VFEAT") || tag.equals("FEATAUTO"))
				{
					ListFrame lf = new ListFrame("Choices for " + tag, Globals.getFeatList());
					String v = lf.getSelectedList();
					if (!v.equals(""))
					{
						LevelTag lt = new LevelTag(level.getText().trim(), tag, v.trim(), true);
						levelTagList.add(lt);
						levelModel.updateModel();
					}
				}
				else
				{
					Object selectedValue = JOptionPane.showInputDialog(null, "Enter the value for " + tag, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE, null, null, "");
					if (selectedValue == null)
					{
						return;
					}
					LevelTag lt = new LevelTag(level.getText().trim(), tag, selectedValue.toString().trim(), true);
					levelTagList.add(lt);
					levelModel.updateModel();
				}
			}
		});

		delBtn.setText(PropertyFactory.getString("in_remove"));
		sth.add(delBtn);
		delBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeLevelTag();
			}
		});

		add(sth, BorderLayout.SOUTH);

	}

	private void removeLevelTag()
	{
		int x = levelTable.getSelectedRow();
		if ((x >= 0) && (x < levelTagList.size()))
		{
			x = sortedLevelModel.getRowTranslated(x);
			LevelTag lt = (LevelTag) levelTagList.get(x);
			if (!lt.needsSaving())
			{ // this is a pre-existing one that needs to be removed
				if (obj == null)
				{
					return;
				}

				boolean bRemoved = false;
				switch (lt.getTagVal())
				{
					case LevelTag.TAG_ADD:
						obj.removeLevelAbility(lt.getLevel(), lt.getValue());
						bRemoved = true;
						break;

					case LevelTag.TAG_ADDDOMAINS:
					case LevelTag.TAG_BONUS:
					case LevelTag.TAG_CAST:
					case LevelTag.TAG_DEFINE:
					case LevelTag.TAG_DR:
					case LevelTag.TAG_FEAT:
						break;
						
					case LevelTag.TAG_FEATAUTO:
						obj.getFeatAutos().remove(Integer.toString(lt.getLevel()) + "|" + lt.getValue());
						bRemoved = true;
						break;
						
					case LevelTag.TAG_KIT:
					case LevelTag.TAG_KNOWN:
					case LevelTag.TAG_REGION:
					case LevelTag.TAG_SA:
					case LevelTag.TAG_SPELL:
					case LevelTag.TAG_SR:
					case LevelTag.TAG_TEMPLATE:
					case LevelTag.TAG_UDAM:
					case LevelTag.TAG_UMULT:
					case LevelTag.TAG_VFEAT:
					default:
						break;
				}
				// TODO: based upon the lt.getTag() value (e.g. "ADD") remove it from the appropriate list in the class
				// for now the easy solution is to not do anything except give a warning
				if (!bRemoved)
				{
					Logging.errorPrint("This tag " + lt.getTag() + ":" + lt.getValue() + " needs to be hand-deleted from customClasses.lst");
					JOptionPane.showMessageDialog(null, "This tag " + lt.getTag() + ":" + lt.getValue() + " needs to be hand-deleted from customClasses.lst", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			// if this isn't a pre-existing level tag, then go ahead and remove it (new ones can be removed without worry)
			levelTagList.remove(x);
			levelModel.updateModel();
		}
	}


	public void updateView(PObject po)
	{
		if (!(po instanceof PCClass))
		{
			return;
		}
		levelTagList.clear();
		obj = (PCClass) po;
		ArrayList aList = obj.getLevelAbilityList();
		if (aList != null)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				LevelAbility la = (LevelAbility) i.next();
				LevelTag lt = new LevelTag(la.level(), LevelTag.TAG_ADD, la.getList());
				levelTagList.add(lt);
			}
		}
		aList = obj.getAddDomains();
		if (aList != null)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				String v = (String) i.next();
				final int sepPos = v.indexOf('|');
				String l = v.substring(0, sepPos);
				String t = v.substring(sepPos + 1);
				LevelTag lt = new LevelTag(l, LevelTag.TAG_ADDDOMAINS, t);
				levelTagList.add(lt);
			}
		}
		for (int x = 0; x < obj.getBonusList().size(); ++x)
		{
			String c = (String) obj.getBonusList().get(x);
			int y = c.indexOf('|');
			LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_BONUS, c.substring(y + 1));
			levelTagList.add(lt);
		}
		for (int x = 0; x < obj.getVariableCount(); ++x)
		{
			String c = obj.getVariableDefinition(x);
			int y = c.indexOf('|');
			LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_DEFINE, c.substring(y + 1));
			levelTagList.add(lt);
		}
		for (int x = 0; x < obj.getCastList().size(); ++x)
		{
			String c = (String) obj.getCastList().get(x);
			if (!c.equals("0"))
			{
				LevelTag lt = new LevelTag(x + 1, LevelTag.TAG_CAST, c);
				levelTagList.add(lt);
			}
		}
		Object oList[] = obj.getKnownList().toArray();
		for (int x = 0; x < oList.length; ++x)
		{
			String c = (String) oList[x];
			if (!c.equals("0"))
			{
				LevelTag lt = new LevelTag(x + 1, LevelTag.TAG_KNOWN, c);
				levelTagList.add(lt);
			}
		}

		boolean flag = true;
		int index = 0;
		while (flag)
		{
			String src = obj.getSpellListString(index++, "|");
			if (src != null)
			{
				int y = src.indexOf('|');
				String lev = src.substring(y + 1);
				src = src.substring(0, y);
				LevelTag lt = new LevelTag(src, LevelTag.TAG_SPELL, lev);
				levelTagList.add(lt);
			}
			else
			{
				flag = false;
			}
		}

		flag = true;
		index = 0;
		while (flag)
		{
			String src = obj.getDRListString(index++, "|");
			if (src != null)
			{
				int y = src.indexOf('|');
				String lev = src.substring(y + 1);
				src = src.substring(0, y);
				LevelTag lt = new LevelTag(src, LevelTag.TAG_DR, lev);
				levelTagList.add(lt);
			}
			else
			{
				flag = false;
			}
		}

		aList = obj.getSpecialAbilityList();
		if ((aList != null) && (aList.size() != 0))
		{
			for (Iterator se = aList.iterator(); se.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) se.next();
				String src = sa.getSource();
				String lev = src.substring(src.lastIndexOf('|') + 1);
				LevelTag lt = new LevelTag(lev, LevelTag.TAG_SA, sa.toString());
				levelTagList.add(lt);
			}
		}

		flag = true;
		index = 0;
		while (flag)
		{
			String src = obj.getSRListString(index++, "|");
			if (src != null)
			{
				int y = src.indexOf('|');
				String lev = src.substring(y + 1);
				src = src.substring(0, y);
				LevelTag lt = new LevelTag(src, LevelTag.TAG_SR, lev);
				levelTagList.add(lt);
			}
			else
			{
				flag = false;
			}
		}

		aList = obj.getFeatList();
		if ((aList != null) && (aList.size() != 0))
		{
			for (Iterator se = aList.iterator(); se.hasNext();)
			{
				String c = (String) se.next();
				int y = c.indexOf(':');
				LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_FEAT, c.substring(y + 1));
				levelTagList.add(lt);
			}
		}
		Collection aCol = obj.getFeatAutos();
		if (aCol != null)
		{
			for (Iterator se = aCol.iterator(); se.hasNext();)
			{
				String c = (String) se.next();
				int y = c.indexOf('|');
				try
				{
					LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_FEATAUTO, c.substring(y + 1));
					levelTagList.add(lt);
				}
				catch (Exception exc)
				{
					Logging.errorPrint("Unrecognized FEATAUTO format:" + c, exc);
				}
			}
		}
		aCol = obj.vFeatList();
		if (aCol != null)
		{
			for (Iterator se = aCol.iterator(); se.hasNext();)
			{
				String c = (String) se.next();
				int y = c.indexOf(':');
				LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_VFEAT, c.substring(y + 1));
				levelTagList.add(lt);
			}
		}
//		String s = obj.getKitString();
//		if (s != null && !s.equals(""))
//		{
//			int y = s.indexOf('|');
//			String l = "1";
//			if (y > 0)
//			{
//				l = s.substring(0, y);
//				s = s.substring(y + 1);
//			}
//			LevelTag lt = new LevelTag(l, LevelTag.TAG_KIT, s);
//			levelTagList.add(lt);
//		}

		String s;
		for (int iKit = 0; ; ++iKit)
		{
			s = obj.getKitString(iKit);
			if (s == null)
			{
				break;
			}
			int y = s.indexOf('|');
			String l = "1";
			if (y > 0)
			{
				l = s.substring(0, y);
				s = s.substring(y + 1);
			}
			LevelTag lt = new LevelTag(l, LevelTag.TAG_KIT, s);
			levelTagList.add(lt);
		}

		s = obj.getRegionString();
		if (s != null && !s.equals(""))
		{
			int y = s.indexOf('|');
			String l = "1";
			if (y > 0)
			{
				l = s.substring(0, y);
				s = s.substring(y + 1);
			}
			LevelTag lt = new LevelTag(l, LevelTag.TAG_REGION, s);
			levelTagList.add(lt);
		}
		for (int x = 0; x < obj.getTemplates().size(); ++x)
		{
			String c = (String) obj.getTemplates().get(x);
			int y = c.indexOf('|');
			LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_TEMPLATE, c.substring(y + 1));
			levelTagList.add(lt);
		}
		aList = obj.getUmultList();
		if ((aList != null) && (aList.size() != 0))
		{
			for (Iterator se = aList.iterator(); se.hasNext();)
			{
				String c = (String) se.next();
				int y = c.indexOf('|');
				LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_UMULT, c.substring(y + 1));
				levelTagList.add(lt);
			}
		}
		aList = obj.getUdamList();
		if (aList != null)
		{
			for (int x = 0; x < aList.size(); ++x)
			{
				String c = (String) aList.get(x);
				if (!c.equals(""))
				{
					LevelTag lt = new LevelTag(x + 1, LevelTag.TAG_UDAM, c);
					levelTagList.add(lt);
				}
			}
		}
		levelModel.updateModel();
	}

	public void updateData(PObject po)
	{
		if (!(po instanceof PCClass))
		{
			return;
		}
		PCClass obj = (PCClass) po;
		try
		{
			URL a = new URL("http://www.somewhere.com");
			String s = obj.getSourceFile();
			if (s != null && s.trim().length()>0)
				a = new URL(obj.getSourceFile());
			for (Iterator i = levelTagList.iterator(); i.hasNext();)
			{
				LevelTag lt = (LevelTag) i.next();
				if (lt.needsSaving())
				{
					String b = lt.getLevel() + "\t" + lt.getTag() + ":" + lt.getValue();
					PCClassLoader.parseLine(obj, b, a, -1);
				}
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
	}

	/**
	 *
	 * A TableModel to handle the list of tags by level.
	 *
	 **/
	private static final class LevelModel extends AbstractTableModel
	{
		static final long serialVersionUID = 1485178774957708877L;
		private final String[] colNames = {"Level", "Tag", "Value"};

		private LevelModel()
		{
		}

		private void updateModel()
		{
			fireTableDataChanged();
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return colNames.length;
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(final int columnIndex)
		{
			if (columnIndex == 0)
			{
				return Integer.class;
			}
			return String.class;
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			return ClassLevelPanel.levelTagList.size();
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name.. of the specified column
		 */
		public String getColumnName(final int columnIndex)
		{
			return (columnIndex >= 0 && columnIndex < colNames.length) ? colNames[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			if ((rowIndex >= 0) && (rowIndex < ClassLevelPanel.levelTagList.size()))
			{
				LevelTag lt = (LevelTag) ClassLevelPanel.levelTagList.get(rowIndex);

				switch (columnIndex)
				{
					case 0:
						return new Integer(lt.getLevel());

					case 1:
						return lt.getTag();

					case 2:
						return lt.getValue();

					default:
						Logging.errorPrint("In ClassLevelPanel.LevelModel.getValueAt the column " + columnIndex + " is not supported.");
						break;
				}
			}
			return null;
		}
	}

	private static final class LevelTag
	{
		static final String[] validTags =
		{
			"ADD",
			"ADDDOMAINS",
			"BONUS",
			"CAST",
			"DEFINE",
			"DR",
			"FEAT",
			"FEATAUTO",
			"KIT",
			"KNOWN",
			"REGION",
			"SA",
			"SPELL",
			"SR",
			"TEMPLATE",
			"UDAM",
			"UMULT",
			"VFEAT"
		};
		private static final int TAG_ADD = 0;
		private static final int TAG_ADDDOMAINS = 1;
		private static final int TAG_BONUS = 2;
		private static final int TAG_CAST = 3;
		private static final int TAG_DEFINE = 4;
		private static final int TAG_DR = 5;
		private static final int TAG_FEAT = 6;
		private static final int TAG_FEATAUTO = 7;
		private static final int TAG_KIT = 8;
		private static final int TAG_KNOWN = 9;
		private static final int TAG_REGION = 10;
		private static final int TAG_SA = 11;
		private static final int TAG_SPELL = 12;
		private static final int TAG_SR = 13;
		private static final int TAG_TEMPLATE = 14;
		private static final int TAG_UDAM = 15;
		private static final int TAG_UMULT = 16;
		private static final int TAG_VFEAT = 17;

		
		private int level;
		private int tagVal;
		private String value;
		private boolean needsSaving;

		public LevelTag(String l, String t, String v)
		{
			this(Integer.parseInt(l), t, v, false);
		}

		public LevelTag(String l, String t, String v, boolean b)
		{
			this(Integer.parseInt(l), t, v, b);
		}
		
		LevelTag(final String l, final int ttag, final String val)
		{
			this(Integer.parseInt(l), ttag, val);
		}

		LevelTag(final int l, final int ttag, final String val)
		{
			setData(l, ttag, val, false);
		}
		
		LevelTag(final int llevel, final String ttag, final String val)
		{
			this(llevel, ttag, val, false);
		}

		LevelTag(final int llevel, final String ttag, final String val, boolean saveIt)
		{
			setData(llevel, parseTag(ttag), val, saveIt);
		}
		
		void setData(final int llevel, int ttagval, final String val, boolean saveIt)
		{
			if (ttagval >= validTags.length)
			{
				ttagval = -1;
			}
			level = llevel;
			tagVal = ttagval;
			value = val;
			needsSaving = saveIt;
		}
		

		private int parseTag(final String ttag)
		{
			for (int i = 0; i < validTags.length; ++i)
			{
				if (validTags[i].equalsIgnoreCase(ttag))
				{
					return i;
				}
			}
			return -1;
		}

		public int getLevel()
		{
			return level;
		}

		public String getTag()
		{
			if (tagVal >= 0)
			{
				return validTags[tagVal];
			}
			return "Unknown";
		}
		
		public int getTagVal()
		{
			return tagVal;
		}

		public String getValue()
		{
			return value;
		}

		public boolean needsSaving()
		{
			return needsSaving;
		}
	}

	static class MatrixFrame extends JDialog
	{
		int columns;
		String colNames[];
		String values[];
		JTextField textField[];
		public String fields[];

		public MatrixFrame(String colNs[], int colNum, String vals[], String title)
		{
			super(Globals.getRootFrame(), title, true);
			colNames = colNs;
			columns = colNum;
			values = vals;
			initComponents();
			setSize(new Dimension(60 * columns, 140));
			setVisible(true);
			pack();
		}

		private void initComponents()
		{
			getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			textField = new JTextField[columns];

			for (int col = 0; col < columns; ++col)
			{
				JLabel tempLabel = new JLabel(colNames[col]);
				gridBagConstraints = buildConstraints(gridBagConstraints, col, 0, true);
				getContentPane().add(tempLabel, gridBagConstraints);

				textField[col] = new JTextField();
				gridBagConstraints = buildConstraints(gridBagConstraints, col, 1, true);
				getContentPane().add(textField[col], gridBagConstraints);
			}
			JButton btn = new JButton(PropertyFactory.getString("in_ok"));
			gridBagConstraints = buildConstraints(gridBagConstraints, columns - 2, 2, true);
			getContentPane().add(btn, gridBagConstraints);
			btn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setVisible(false);
					fields = new String[columns];
					for (int col = 0; col < columns; ++col)
					{
						fields[col] = textField[col].getText();
					}
				}
			});

			btn = new JButton(PropertyFactory.getString("in_cancel"));
			gridBagConstraints = buildConstraints(gridBagConstraints, columns - 1, 2, true);
			getContentPane().add(btn, gridBagConstraints);
			btn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setVisible(false);
					fields = new String[columns];
					for (int col = 0; col < columns; ++col)
					{
						fields[col] = "";
					}
				}
			});
		}

		private GridBagConstraints buildConstraints(GridBagConstraints gridBagConstraints, int gridx, int gridy, boolean useInsets)
		{
			gridBagConstraints.gridx = gridx;
			gridBagConstraints.gridy = gridy;
			if (useInsets)
			{
				gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			}
			return gridBagConstraints;
		}

	}

	static class ListFrame extends JDialog
	{
		AvailableSelectedPanel asPanel = new AvailableSelectedPanel();

		public ListFrame(String title, List aList)
		{
			super(Globals.getRootFrame(), title, true);
			initComponents();
			asPanel.setAvailableList(aList, true);
			setSize(new Dimension(400, 400));
			setVisible(true);
			pack();
		}

		private void initComponents()
		{
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(asPanel, BorderLayout.CENTER);
			JButton btn = new JButton(PropertyFactory.getString("in_ok"));
			getContentPane().add(btn, BorderLayout.SOUTH);
			btn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setVisible(false);
				}
			});
		}

		public String getSelectedList()
		{
			Object sels[] = asPanel.getSelectedList();
			if (sels.length == 0)
			{
				return "";
			}
			String ret = sels[0].toString();
			for (int i = 1; i < sels.length; ++i)
			{
				ret += "|" + sels[i].toString();
			}
			return ret;
		}

	}
}


