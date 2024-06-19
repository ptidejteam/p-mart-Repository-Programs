/*
 * InfoTemplates.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 19, 2001, 7:36 PM
 */
package pcgen.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

/**
 * <code>InfoTemplates</code>
 *
 * @author  Kurt Wimmer <kwimmer@home.com>
 * @version $Revision: 1.1 $
 */
public class InfoTemplates extends JPanel
{
	private JScrollPane allTemplatesPane;
	private JTableEx allTemplatesTable;
	private JScrollPane currentTemplatesPane;
	private JTableEx currentTemplatesTable;
	private JPanel jPanelCtrl;
	private JButton jButtonMinus;
	private JButton jButtonPlus;
	private JLabel jLabelFilterAll;
	private JComboBox jcbFilterAll;
	private JLabel jLabelFilterPCs;
	private JComboBox jcbFilterPCs;

	protected AllTemplatesTableModel allTemplatesDataModel = new AllTemplatesTableModel();
	protected TableSorter sortedAllTemplatesModel = new TableSorter();
	protected PCTemplatesTableModel currentTemplatesDataModel = new PCTemplatesTableModel();
	protected TableSorter sortedCurrentTemplatesModel = new TableSorter();
	/**
	 *
	 */
	private static final String[] ALL_TEMPLATES_COLUMN_NAMES = new String[]{
		"Q", "Name", "Level Adj", "Modifiers", "Prereqs", "Source File"};
	/**
	 *
	 */
	private static final int FILTER_ALL = 0;
	private static final int FILTER_QUALIFIED = 1;

	/** the list from which to pull the templates to use. */
	private ArrayList displayTemplates = Globals.getTemplateList();
	private ArrayList currentPCdisplayTemplates = new ArrayList(0);

	/** Creates new form InfoTemplates */
	public InfoTemplates()
	{
		initComponents();
		allTemplatesDataModel.setFilter(allTemplatesDataModel.curFilter);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		allTemplatesDataModel.setFilter(allTemplatesDataModel.curFilter);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		// Basic template windows
		allTemplatesPane = new JScrollPane();
		sortedAllTemplatesModel.setModel(allTemplatesDataModel);
		allTemplatesTable = new JTableEx();
		currentTemplatesPane = new JScrollPane();
		sortedCurrentTemplatesModel.setModel(currentTemplatesDataModel);
		currentTemplatesTable = new JTableEx();
		sortedCurrentTemplatesModel.addMouseListenerToHeaderInTable(currentTemplatesTable);

		// Template Control widgets
		jPanelCtrl = new JPanel();
		jButtonMinus = new JButton();
		jButtonPlus = new JButton();
		jLabelFilterAll = new JLabel();
		jcbFilterAll = new JComboBox();
		jLabelFilterPCs = new JLabel();
		jcbFilterPCs = new JComboBox();

		// Set to Grid Layout for the Template "tab"
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConsTmpltPanel;

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(false);
				formComponentShown(evt);
			}
		});

		// Configure the Templates Panes
		allTemplatesPane.setMinimumSize(new Dimension(220, 300));
		allTemplatesTable.setModel(sortedAllTemplatesModel);
		//allTemplatesTable.setDoubleBuffered(false);
		allTemplatesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		allTemplatesTable.setShowVerticalLines(false);
		allTemplatesTable.setMinimumSize(new Dimension(200, 240));
		allTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final int[] cols = {0,1,2,3,4,5};   //change this when I add more coloumns
		allTemplatesTable.setOptimalColumnWidths(cols);
/*    TableColumn col = allTemplatesTable.getColumnModel().getColumn(0);
    col.setMaxWidth(15);
    col.setMinWidth(15);
    col = allTemplatesTable.getColumnModel().getColumn(2);
    col.setMaxWidth(45);
    col = allTemplatesTable.getColumnModel().getColumn(4);
    col.setMaxWidth(60);
    col = allTemplatesTable.getColumnModel().getColumn(5);
    col.setMaxWidth(60);
    col = allTemplatesTable.getColumnModel().getColumn(6);
    col.setMaxWidth(60);*/
		sortedAllTemplatesModel.addMouseListenerToHeaderInTable(allTemplatesTable);

		allTemplatesPane.setViewportView(allTemplatesTable);

		gridBagConsTmpltPanel = new GridBagConstraints();
		gridBagConsTmpltPanel.gridx = 0;
		gridBagConsTmpltPanel.gridy = 0;
		gridBagConsTmpltPanel.gridwidth = 3;
		gridBagConsTmpltPanel.gridheight = 5;
		gridBagConsTmpltPanel.fill = GridBagConstraints.BOTH;
		gridBagConsTmpltPanel.ipadx = 17;
		gridBagConsTmpltPanel.anchor = GridBagConstraints.NORTH;
		gridBagConsTmpltPanel.weightx = 2.0;
		gridBagConsTmpltPanel.weighty = 25.0;
		add(allTemplatesPane, gridBagConsTmpltPanel);

		// Configure and pack the currentTemplates
		currentTemplatesTable.setModel(sortedCurrentTemplatesModel);
		currentTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentTemplatesTable.setDoubleBuffered(false);
		currentTemplatesPane.setViewportView(currentTemplatesTable);

		gridBagConsTmpltPanel = new GridBagConstraints();
		gridBagConsTmpltPanel.gridx = 0;
		gridBagConsTmpltPanel.gridy = 5;
		gridBagConsTmpltPanel.gridheight = 7;
		gridBagConsTmpltPanel.fill = GridBagConstraints.BOTH;
		gridBagConsTmpltPanel.weightx = 0.5;
		gridBagConsTmpltPanel.weighty = 0.84;
		add(currentTemplatesPane, gridBagConsTmpltPanel);

		// Select grid layout for the control panel
		jPanelCtrl.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConsCtrlPanel;

		// Configure Control Panel
		jPanelCtrl.setPreferredSize(new Dimension(300, 100));
		jPanelCtrl.setMinimumSize(new Dimension(200, 100));
		jPanelCtrl.setAlignmentY(0.0F);
		jPanelCtrl.setAlignmentX(0.0F);
		jPanelCtrl.setMaximumSize(new Dimension(500, 100));

		// Configure the PLUS button
		jButtonPlus.setText("+");
		jButtonPlus.setAlignmentY(0.0F);
		jButtonPlus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				buttonPlusActionPerformed(evt);
			}
		});
		// Pack PLUS button onto control panel
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 0;
		gridBagConsCtrlPanel.gridy = 0;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.ipady = 3;
		gridBagConsCtrlPanel.gridheight = 2;
		gridBagConsCtrlPanel.insets = new Insets(0, 0, 0, 5);
		jPanelCtrl.add(jButtonPlus, gridBagConsCtrlPanel);

		// Configure the MINUS button
		jButtonMinus.setText("-");
		jButtonMinus.setAlignmentY(0.0F);
		jButtonMinus.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonMinus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				buttonMinusActionListener(evt);
			}
		});
		// pack MINUS button onto control panel
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 0;
		gridBagConsCtrlPanel.gridy = 3;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.ipady = 3;
		gridBagConsCtrlPanel.gridheight = 2;
		gridBagConsCtrlPanel.insets = new Insets(0, 0, 0, 5);
		jPanelCtrl.add(jButtonMinus, gridBagConsCtrlPanel);

		// Configure Filter All Label
		jLabelFilterAll.setText("Filter All Templates");
		jLabelFilterAll.setForeground(Color.black);
		jLabelFilterAll.setHorizontalAlignment(SwingConstants.TRAILING);
		jLabelFilterAll.setAlignmentY(1.0F);
		// Pack Filter All Label
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 1;
		gridBagConsCtrlPanel.gridy = 0;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.anchor = GridBagConstraints.WEST;
		gridBagConsCtrlPanel.insets = new Insets(0, 0, 0, 5);
		jPanelCtrl.add(jLabelFilterAll, gridBagConsCtrlPanel);

		// Configure Filter All Box
		jcbFilterAll.setModel(new DefaultComboBoxModel(new String[]{ "All", "Qualified" }));
		jcbFilterAll.setAlignmentY(0.0F);
		jcbFilterAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				filterAllActionPerformed(evt);
			}
		});
		// Pack Filter All Box
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 1;
		gridBagConsCtrlPanel.gridy = 1;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.fill = GridBagConstraints.NONE;
		gridBagConsCtrlPanel.anchor = GridBagConstraints.WEST;
		gridBagConsCtrlPanel.weightx = 0.8;
		jPanelCtrl.add(jcbFilterAll, gridBagConsCtrlPanel);

		// Configure Filter PCs Current Label
		jLabelFilterPCs.setText("Filter Current Templates");
		jLabelFilterPCs.setForeground(Color.black);
		jLabelFilterPCs.setHorizontalAlignment(SwingConstants.TRAILING);
		jLabelFilterPCs.setAlignmentY(1.0F);
		// Pack Filter PCs Current Label
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 1;
		gridBagConsCtrlPanel.gridy = 3;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.anchor = GridBagConstraints.WEST;
		gridBagConsCtrlPanel.insets = new Insets(0, 0, 0, 5);
		jPanelCtrl.add(jLabelFilterPCs, gridBagConsCtrlPanel);

		// Configure Filter PCs Current Box
		jcbFilterPCs.setModel(new DefaultComboBoxModel(new String[]{ "Visible", "Invisible", "All" }));
		jcbFilterPCs.setAlignmentY(0.0F);
		jcbFilterPCs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				filterPCsActionPerformed(evt);
			}
		});
		// Pack Filter PCs Current Box
		gridBagConsCtrlPanel = new GridBagConstraints();
		gridBagConsCtrlPanel.gridx = 1;
		gridBagConsCtrlPanel.gridy = 4;
		gridBagConsCtrlPanel.ipadx = 3;
		gridBagConsCtrlPanel.fill = GridBagConstraints.NONE;
		gridBagConsCtrlPanel.anchor = GridBagConstraints.WEST;
		gridBagConsCtrlPanel.weightx = 0.8;
		jPanelCtrl.add(jcbFilterPCs, gridBagConsCtrlPanel);

		// Pack Control Panel into Template Panel
		gridBagConsTmpltPanel = new GridBagConstraints();
		gridBagConsTmpltPanel.gridx = 1;
		gridBagConsTmpltPanel.gridy = 5;
		gridBagConsTmpltPanel.gridheight = 7;
		gridBagConsTmpltPanel.gridwidth = 2;
		//    gridBagConsTmpltPanel.padx = 10;
		//    gridBagConsTmpltPanel.pady = 10;
		gridBagConsTmpltPanel.fill = GridBagConstraints.HORIZONTAL;
		gridBagConsCtrlPanel.weightx = 2.0;
		gridBagConsTmpltPanel.insets = new Insets(5, 0, 5, 0);
		add(jPanelCtrl, gridBagConsTmpltPanel);
	}


	private void formComponentShown(ComponentEvent evt)
	{
		PCGen_Frame1.getStatusBar().setText("Add Templates to your Character.");
	}


	private void filterAllActionPerformed(ActionEvent evt)
	{
		allTemplatesDataModel.setFilter(jcbFilterAll.getSelectedIndex());
	}


	private void filterPCsActionPerformed(ActionEvent evt)
	{
		currentTemplatesDataModel.setFilter(jcbFilterPCs.getSelectedIndex());
	}


	private void buttonMinusActionListener(ActionEvent evt)
	{
		if (currentTemplatesTable.getSelectedRowCount() <= 0)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCTemplate theTmpl = (PCTemplate)currentPCdisplayTemplates.get(sortedCurrentTemplatesModel.getRowTranslated(currentTemplatesTable.getSelectedRow()));
		if (!theTmpl.isRemovable())
		{
			JOptionPane.showMessageDialog(null, "That Template is not Removable", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Globals.getCurrentPC().removeTemplate(theTmpl);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}


	private void buttonPlusActionPerformed(ActionEvent evt)
	{
		if (allTemplatesTable.getSelectedRowCount() <= 0)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCTemplate theTmpl = (PCTemplate)displayTemplates.get(sortedAllTemplatesModel.getRowTranslated(allTemplatesTable.getSelectedRow()));
		if ((theTmpl != null) && theTmpl.isQualified())
		{
			PCTemplate aTmpl = Globals.getCurrentPC().getTemplateNamed(theTmpl.getName());
			if (aTmpl == null)
				Globals.getCurrentPC().addTemplate(theTmpl);
			else
				JOptionPane.showMessageDialog(null, "Already have that template.");
		}
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}


	/** TableModel to handle the full list of templates.
	 *  It pulls its data straight from the Globals.getTemplateList() Vector.
	 */
	public class AllTemplatesTableModel extends AbstractTableModel
	{
		public int curFilter;
		private int prevGlobalTemplateCount;

		/**
		 *
		 */
		public AllTemplatesTableModel()
		{
			setFilter(0);
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list of filters
		 * will require a modification of this method.
		 * at the moment:
		 * 0: All
		 * 1: Qualified
		 * @param filterID the filter type
		 */
		public void setFilter(int filterID)
		{
			prevGlobalTemplateCount = Globals.getTemplateList().size();
			displayTemplates = new ArrayList();

			switch (filterID)
			{
				case 0: // All
					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if (pcTmpl.isVisible())
							displayTemplates.add(pcTmpl);
					}
					break;

				case 1: // Qualified
					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if (pcTmpl.isVisible() && pcTmpl.isQualified())
							displayTemplates.add(pcTmpl);
					}
					break;
			}

			fireTableDataChanged();
			curFilter = filterID;
		}

		/**
		 * Re-fetches and re-filters the data from the global template list.
		 */

		public void updateFilter()
		{
			setFilter(curFilter);
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return ALL_TEMPLATES_COLUMN_NAMES.length;
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			if (prevGlobalTemplateCount != Globals.getTemplateList().size())
				updateFilter();
			return (displayTemplates != null) ? displayTemplates.size() : 0;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name of the specified column
		 */
		public String getColumnName(int columnIndex)
		{
			return (columnIndex >= 0 && columnIndex < ALL_TEMPLATES_COLUMN_NAMES.length) ?
				ALL_TEMPLATES_COLUMN_NAMES[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (displayTemplates != null)
			{
				PCTemplate t = (PCTemplate)displayTemplates.get(rowIndex),
					pc = Globals.getCurrentPC().getTemplateNamed(t.toString());
				if (pc != null)
				{
					t = pc;
				}

				switch (columnIndex)
				{
					case 0:
						return t.isQualified() ? "Y" : "N";

					case 1:
						return t.toString();

					case 2:
						return "" + t.getLevelAdjustment();

					case 3:
						return t.modifierString();

					case 4:
						return t.preReqStrings();

					case 5:
						return t.getSource();
				}
			}

			return null;
		}
	}

	/**
	 *
	 */
	public class PCTemplatesTableModel extends AbstractTableModel
	{
		public int curFilter;
		private int prevGlobalTemplateCount;

		public int getColumnCount()
		{
			return 2;
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getRowCount()
		{
			return currentPCdisplayTemplates.size();
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Template";
				case 1:
					return "Removable";
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if ((Globals.getCurrentPC() != null) && (Globals.getCurrentPC().getTemplateList() != null))
			{
				PCTemplate t = (PCTemplate)currentPCdisplayTemplates.get(rowIndex);
				switch (columnIndex)
				{
					case 0:
						return t.toString();
					case 1:
						return t.isVisible()?"Yes":"No";
				}
			}
			return null;
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list of filters
		 * will require a modification of this method.
		 * at the moment:
		 * 0: Visible
		 * 1: Invisible
		 * 2: All
		 * @param filterID the filter type
		 */
		public void setFilter(int filterID)
		{
			if (Globals.getCurrentPC() == null)
				currentPCdisplayTemplates = new ArrayList(0);
			prevGlobalTemplateCount = Globals.getCurrentPC().getTemplateList().size();
			currentPCdisplayTemplates = new ArrayList();

			switch (filterID)
			{
				case 0:
					for (Iterator it = Globals.getCurrentPC().getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if (pcTmpl.isVisible())
							currentPCdisplayTemplates.add(pcTmpl);
					}
					break;

				case 1:
					for (Iterator it = Globals.getCurrentPC().getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if (!pcTmpl.isVisible())
							currentPCdisplayTemplates.add(pcTmpl);
					}
					break;

				case 2:
					for (Iterator it = Globals.getCurrentPC().getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						currentPCdisplayTemplates.add(pcTmpl);
					}
					break;

			}

			fireTableDataChanged();
			curFilter = filterID;
		}
	}
}
