/*
 * MainCampaign.java
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import pcgen.core.Campaign;
import pcgen.core.Globals;

/**
 *  <code>MainCampaign</code> .
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
class MainCampaign extends JPanel
{
	final String[] nameList = {"Name", "Rank", "Loaded", "Game", "File"};
	final Object[][] data = new Object[Globals.campaignList.size()][nameList.length];
	TableSorter sorter = null;
	private JTableEx campaignTable = null;
	private final static int SOURCEFILE = 4;

	public MainCampaign()
	{
		initComponent();
	}

	private void initComponent()
	{
		campaignTable = new JTableEx();
		for (int row = 0; row < Globals.campaignList.size(); row++)
		{
			Campaign aCamp = (Campaign)Globals.campaignList.get(row);
			for (int col = 0; col < nameList.length; col++)
			{
				Object retVal = null;
				switch (col)
				{
					case 0:
						retVal = aCamp.name();
						break;
					case 1:
						retVal = aCamp.rank().toString();
						break;
					case 2:
						if (aCamp.isLoaded())
						{
							retVal = "Y";
						}
						else
						{
							retVal = "N";
						}
						break;
					case 3:
						retVal = aCamp.game();
						break;
					case SOURCEFILE:
						retVal = aCamp.getSourceFile();
						break;
				}
				if (retVal != null)
				{
					data[row][col] = retVal;
				}
			}
		}
		TableModel dataModel =
			new AbstractTableModel()
			{
				public void setValueAt(Object aValue, int row, int column)
				{
					data[row][column] = aValue;
				}

				// These methods always need to be implemented.

				public int getColumnCount()
				{
					return nameList.length;
				}


				public int getRowCount()
				{
					return Globals.campaignList.size();
				}


				public Object getValueAt(int row, int col)
				{
					return data[row][col];
				}


				// The default implementations of these methods in
				// AbstractTableModel would work, but we can refine them.
				public String getColumnName(int column)
				{
					return nameList[column];
				}


				public Class getColumnClass(int col)
				{
					return getValueAt(0, col).getClass();
				}


				public boolean isCellEditable(int row, int col)
				{
					return col == 1;
				}
			};
		this.setLayout(new BorderLayout());
		sorter = new TableSorter(dataModel);
		campaignTable = new JTableEx(sorter);

		sorter.addMouseListenerToHeaderInTable(campaignTable);

		campaignTable.setBounds(0, 0, 251, 293);
		campaignTable.setAutoCreateColumnsFromModel(true);
		campaignTable.setDoubleBuffered(true);
		campaignTable.setColumnSelectionAllowed(false);
		campaignTable.getTableHeader().setReorderingAllowed(false);
		TableColumn col = campaignTable.getColumnModel().getColumn(2);
		col.setMaxWidth(90);
		col.setMinWidth(90);
		col = campaignTable.getColumnModel().getColumn(1);
		col.setMaxWidth(60);
		col.setMinWidth(60);
		JScrollPane scrollPane = new JScrollPane(campaignTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setColumnHeaderView(campaignTable.getTableHeader());
		scrollPane.setBackground(new Color(255, 255, 255));
		scrollPane.setPreferredSize(new Dimension(300, 210));
		this.add(scrollPane, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		FlowLayout aFlow = new FlowLayout();
		aFlow.setAlignment(FlowLayout.CENTER);
		panel.setLayout(aFlow);
		JButton aButton = new JButton("Load");
		aButton.addActionListener(
			new java.awt.event.ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					loadCampaigns_actionPerformed(e);
				}
			});
		panel.add(aButton);
		aButton = new JButton("Select All");
		aButton.addActionListener(
			new java.awt.event.ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					selectAllCampaigns_actionPerformed(e);
				}
			});
		panel.add(aButton);
		aButton = new JButton("Clear All");
		aButton.addActionListener(
			new java.awt.event.ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					deselectAllCampaigns_actionPerformed(e);
				}
			});
		panel.add(aButton);
		aButton = new JButton("Unload All");
		aButton.addActionListener(
			new java.awt.event.ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					unloadAllCampaigns_actionPerformed(e);
				}
			});
		panel.add(aButton);
		this.add(panel, BorderLayout.SOUTH);
		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				requestDefaultFocus();
			}
		});

		if (Globals.loadCampaignsAtStart)
		{
			selectCampaigns(Globals.chosenCampaignSourcefiles);
			if (campaignTable.getSelectedRowCount() > 0)
				loadCampaigns();
		}
	}

	private void loadCampaigns_actionPerformed(ActionEvent e)
	{
		loadCampaigns();
	}

	private void loadCampaigns()
	{
		int i = campaignTable.getSelectedRowCount();
		ArrayList campaigns = new ArrayList();
		int[] cList = campaignTable.getSelectedRows();
		while (i-- > 0)
		{
			sorter.setValueAt("Y", cList[i], 2);
			campaigns.add(Globals.getCampaignNamed((String)sorter.getValueAt(cList[i], 0)));
		}

		Globals.loadCampaigns(campaigns);
		Globals.sortCampaigns();

		//There *is* a better way to do this. I just haven't thought of it yet.
		if (getParent() != null && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = (PCGen_Frame1)getParent().getParent().getParent().getParent().getParent();//.getParent();
			parent.enableNew(true);
			parent.enableImport(true);
		}
		campaignTable.updateUI();
	}

	private void selectAllCampaigns_actionPerformed(ActionEvent e)
	{
		campaignTable.selectAll();
		campaignTable.updateUI();
	}

	private void deselectAllCampaigns_actionPerformed(ActionEvent e)
	{
		campaignTable.clearSelection();
		campaignTable.updateUI();
	}

	private void unloadAllCampaigns_actionPerformed(ActionEvent e)
	{
		Globals.emptyLists();
		int x = 0;
		for (Iterator i = Globals.campaignList.iterator(); i.hasNext();)
		{
			data[x++][2] = "N";
			Campaign aCamp = (Campaign)i.next();
			aCamp.setIsLoaded(false);
		}

		//There *is* a better way to do this. I just haven't thought of it yet.
		PCGen_Frame1 parent = (PCGen_Frame1)getParent().getParent().getParent().getParent().getParent();//.getParent();
		parent.enableNew(false);
		parent.enableImport(false);
		campaignTable.updateUI();
	}

	/**
	 *  Pass this a Collection of campaign file names. These will be selected in the
	 *  table.
	 *
	 * @param  campaigns  A Collection of campaign file names.
	 * @since
	 */
	private void selectCampaigns(Collection campaigns)
	{
		ListSelectionModel selModel = campaignTable.getSelectionModel();
		Campaign aCamp = null;
		Iterator iter = campaigns.iterator();
		campaigns: while (iter.hasNext())
		{
			String element = (String)iter.next();
			for (int i = 0; i < data.length; i++)
			{
				aCamp = (Campaign)Globals.campaignList.get(i);
				if (aCamp.getSourceFile().equals(element))
				{
					selModel.addSelectionInterval(i, i);
					continue campaigns;
				}
			}
		}
	}

}

