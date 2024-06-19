/*
 * InfoRace.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 */


/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

public class InfoRace extends FilterAdapterPanel
{
	private JScrollPane raceScrollPane;
	private JTableEx raceTable;
	protected RaceTableModel raceTableModel = new RaceTableModel();
	private JLabel lblRace = new JLabel("Race:");
	private JLabel raceText = new JLabel();
	protected TableSorter sorter = new TableSorter();

	private final JLabel avaLabel = new JLabel("Available Templates");
	private final JLabel selLabel = new JLabel("Selected Templates");
	private JScrollPane allTemplatesPane;
	private JTableEx allTemplatesTable;
	private JScrollPane currentTemplatesPane;
	private JTableEx currentTemplatesTable;
	private JPanel jPanelCtrl;
	private JButton leftButton;
	private JButton rightButton;
	private JLabel jLabelFilterPCs;
	private JComboBox jcbFilterPCs;
	protected AllTemplatesTableModel allTemplatesDataModel = new AllTemplatesTableModel();
	protected TableSorter sortedAllTemplatesModel = new TableSorter();
	protected PCTemplatesTableModel currentTemplatesDataModel = new PCTemplatesTableModel();
	protected TableSorter sortedCurrentTemplatesModel = new TableSorter();
	private boolean hasBeenSized = false;
	private JButton selButton = new JButton("Select");

	/**
	 *
	 */
	private static final String[] ALL_TEMPLATES_COLUMN_NAMES = new String[]{
		"Q", "Name", "Level Adj", "Modifiers", "Prereqs", "Source File"};

	/** the list from which to pull the templates to use. */
//  	private ArrayList displayTemplates = Globals.getTemplateList();
	private ArrayList currentPCdisplayTemplates = new ArrayList(0);

	JScrollPane cScroll = new JScrollPane();
	JLabelPane infoLabel = new JLabelPane();
	JPanel center = new JPanel();
	Border etched;
	TitledBorder titled;
	JSplitPane splitPane;
	JSplitPane bsplit;
	JSplitPane asplit;
	private static boolean needsUpdate = true;
	protected static PlayerCharacter aPC = null;

	private class RacePopupMenu extends JPopupMenu
	{
		private class RaceActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddRaceActionListener extends RaceActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selButton.doClick();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddRaceActionListener(), "Select", (char)0, accelerator, "Select Race", "Add16.gif", true);
		}

		RacePopupMenu()
		{
			RacePopupMenu.this.add(createAddMenuItem("Select", "control EQUALS"));
		}
	}

	private class RacePopupListener extends MouseAdapter
	{
		private RacePopupMenu menu;
		JTable table;

		RacePopupListener(JTable atable, RacePopupMenu aMenu)
		{
			menu = aMenu;
			table = atable;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
								menuItem.doClick(2);
								return;
							}
						}
					}
					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};
			table.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				int selRow = table.getSelectedRow();
				if (selRow == -1) return;
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}


	private void hookupPopupMenu(JTable atable)
	{
		atable.addMouseListener(new RacePopupListener(atable, new RacePopupMenu()));
	}

	public InfoRace()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Race");
		aPC = Globals.getCurrentPC();

		initComponents();
		hookupPopupMenu(raceTable);
		selectRaceByName(aPC.getRace().getName());

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		raceScrollPane = new JScrollPane();
		sorter.setModel(raceTableModel);
		raceTable = new JTableEx();
		sorter.addMouseListenerToHeaderInTable(raceTable);

		JPanel racePanel = new JPanel();
		racePanel.setLayout(new BorderLayout());
		JPanel tPanel = new JPanel();
		tPanel.setLayout(new FlowLayout());
		tPanel.add(lblRace);
		lblRace.setPreferredSize(new Dimension(35, 20));
		tPanel.add(raceText);
		raceText.setPreferredSize(new Dimension(120, 25));
		raceText.setBorder(BorderFactory.createEtchedBorder());
		raceText.setHorizontalAlignment(JTextField.CENTER);
		raceText.setBackground(Color.black);
		selButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int i = raceTable.getSelectedRow();
				if (i < 0)
					return;
				String raceNamed = raceTable.getValueAt(i, 0).toString();
				Race r = Globals.getRaceNamed(raceNamed);
				if (!r.equals(aPC.getRace()))
				{
					aPC.setRace(r);
					final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
					rootFrame.forceUpdate_InfoFeats();
					rootFrame.forceUpdate_InfoSkills();
					rootFrame.forceUpdate_InfoSpells();
					if (aPC.getRace().hitDice() != 0)
					{
						aPC.getRace().rollHp();
					}
					updateCharacterInfo();
				}
			}
		});
		tPanel.add(selButton);
		racePanel.add(tPanel, BorderLayout.NORTH);

		raceTable.setModel(sorter);
		raceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		raceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		raceScrollPane.setPreferredSize(new Dimension(20, 20));
		raceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					int i = raceTable.getSelectedRow();
					if (i < 0)
						return;
					String raceNamed = raceTable.getValueAt(i, 0).toString();
					Race r = Globals.getRaceNamed(raceNamed);
					setInfoLabelText(r);
				}
			}
		});
		final int[] cols = {0, 3, 4, 5, 6, 7, 8};
		raceTable.setOptimalColumnWidths(cols);

		raceTable.getColumnModel().getColumn(3).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		raceTable.getColumnModel().getColumn(7).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));

		raceScrollPane.setViewportView(raceTable);
		racePanel.add(raceScrollPane, BorderLayout.CENTER);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Race Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(center.getBackground());
		cScroll.setViewportView(infoLabel);

		center.setLayout(new BorderLayout());

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, racePanel, cScroll);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout());
		aPanel.add(avaLabel);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, "Click to add selected Template");
		rightButton.setEnabled(true);
		aPanel.add(rightButton);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		Utility.setDescription(sButton, "Click to change the orientation of the tables");
		sButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int splitOrientation = asplit.getOrientation();
				if (splitOrientation == JSplitPane.VERTICAL_SPLIT)
					splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
				else
					splitOrientation = JSplitPane.VERTICAL_SPLIT;
				asplit.setOrientation(splitOrientation);
				asplit.setDividerLocation(0.5d);
			}
		});
		aPanel.add(sButton);
		avaLabel.setPreferredSize(new Dimension(120, 20));
		JPanel avaTemplatePanel = new JPanel();
		avaTemplatePanel.setLayout(new BorderLayout());
		avaTemplatePanel.add(aPanel, BorderLayout.NORTH);


		allTemplatesTable = new JTableEx();
		allTemplatesPane = new JScrollPane(allTemplatesTable);
		sortedAllTemplatesModel.setModel(allTemplatesDataModel);
		allTemplatesTable.setModel(sortedAllTemplatesModel);
		allTemplatesPane.setViewportView(allTemplatesTable);
		allTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		avaTemplatePanel.add(allTemplatesPane, BorderLayout.CENTER);

		allTemplatesTable.getColumnModel().getColumn(0).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		allTemplatesTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));

		currentTemplatesPane = new JScrollPane();
		sortedCurrentTemplatesModel.setModel(currentTemplatesDataModel);
		currentTemplatesTable = new JTableEx();
		sortedCurrentTemplatesModel.addMouseListenerToHeaderInTable(currentTemplatesTable);

		JPanel selTemplatePanel = new JPanel();
		selTemplatePanel.setLayout(new BorderLayout());
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());
		bPanel.add(selLabel);

		selLabel.setPreferredSize(new Dimension(100, 20));
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove selected Template");
		leftButton.setEnabled(true);
		bPanel.add(leftButton);
		selTemplatePanel.add(bPanel, BorderLayout.NORTH);
		currentTemplatesTable.setModel(sortedCurrentTemplatesModel);
		currentTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentTemplatesTable.setDoubleBuffered(false);
		currentTemplatesPane.setViewportView(currentTemplatesTable);
		selTemplatePanel.add(currentTemplatesPane, BorderLayout.CENTER);

		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, avaTemplatePanel, selTemplatePanel);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				int s = splitPane.getDividerLocation();
				if (s > 0)
					Globals.setPCGenOption("InfoRace.splitPane", s);
				s = asplit.getDividerLocation();
				if (s > 0)
					Globals.setPCGenOption("InfoRace.asplit", s);
				s = bsplit.getDividerLocation();
				if (s > 0)
					Globals.setPCGenOption("InfoRace.bsplit", s);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeTemplate(evt);
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addTemplate(evt);
			}
		});
	}

	private void selectRaceByName(String setRaceName)
	{
		raceTable.getSelectionModel().clearSelection();

		final int raceTableRowCount = raceTable.getRowCount();
		for (int row = 0; row < raceTableRowCount; row++)
		{
			final String raceName = (String)raceTable.getValueAt(row, 0);
			if (raceName.equals(setRaceName))
			{
				raceTable.getSelectionModel().setSelectionInterval(row, row);
				return;
			}
		}

	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Select a Race and desired Templates."); //just clear it till there's something worth putting here
		updateCharacterInfo();
		int width;
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		if (!hasBeenSized)
		{
			s = Globals.getPCGenOption("InfoRace.splitPane", (int)(InfoRace.this.getSize().getWidth() * 75.0 / 100.0));
			t = Globals.getPCGenOption("InfoRace.bsplit", (int)(InfoRace.this.getSize().getHeight() - 120));
			u = Globals.getPCGenOption("InfoRace.asplit", (int)(InfoRace.this.getSize().getWidth() * 75.0 / 100.0));

			// set the prefered width on raceTable
			for (int i = 0; i < raceTable.getColumnCount(); i++)
			{
				TableColumn sCol = raceTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Race", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(raceTable, "Race", i));
			}
			// set the prefered width on allTemplatesTable
			for (int i = 0; i < allTemplatesTable.getColumnCount(); i++)
			{
				TableColumn sCol = allTemplatesTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Tamplate", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(allTemplatesTable, "Tamplate", i));
			}
			if (s > 0)
				hasBeenSized = true;
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			Globals.setPCGenOption("InfoRace.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			Globals.setPCGenOption("InfoRace.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			Globals.setPCGenOption("InfoRace.asplit", u);
		}
	}

	private void forceUpdate()
	{
		//java.awt.Container c = this.getParent();
		//while (c != null)
		//{
		//	if (c.getClass().getName().equals("pcgen.gui.CharacterInfo"))
		//	{
		//		((CharacterInfo)c).featList_Changed();
		//		break;
		//	}
		//	c = c.getParent();
		//}
		final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
		rootFrame.featList_Changed();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoDomain();
		PCGen_Frame1.forceUpdate_InfoInventory();
		rootFrame.hpTotal_Changed();
	}

	// This recalculates the states of everything based upon the currently selected
	// character.
	public void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		try
		{
			raceText.setText(aPC.getRace().toString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(aPC.getRace());
		}
		catch (Exception exc)
		{
			raceText.setText("");
			setInfoLabelText(null);
		}
		allTemplatesDataModel.updateModel();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		raceTableModel.updateModel();

		needsUpdate = false;
	}

	private void addTemplate(ActionEvent evt)
	{
		if (allTemplatesTable.getSelectedRowCount() <= 0)
			return;
		aPC.setDirty(true);
		PCTemplate theTmpl = allTemplatesDataModel.get(sortedAllTemplatesModel.getRowTranslated(allTemplatesTable.getSelectedRow()));
		if ((theTmpl != null) && theTmpl.isQualified())
		{
			PCTemplate aTmpl = aPC.getTemplateNamed(theTmpl.getName());
			if (aTmpl == null)
			{
				aPC.addTemplate(theTmpl);
				forceUpdate();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Already have that template.");
			}
		}
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	private void removeTemplate(ActionEvent evt)
	{
		if (currentTemplatesTable.getSelectedRowCount() <= 0)
			return;
		aPC.setDirty(true);
		PCTemplate theTmpl = (PCTemplate)currentPCdisplayTemplates.get(sortedCurrentTemplatesModel.getRowTranslated(currentTemplatesTable.getSelectedRow()));
		if (!theTmpl.isRemovable())
		{
			JOptionPane.showMessageDialog(null, "That Template is not Removable", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		aPC.removeTemplate(theTmpl);
		forceUpdate();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	private void setInfoLabelText(Race aRace)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		if ((aRace != null) && !aRace.getName().startsWith("<none"))
		{
			b.append("<b>").append(aRace.getName()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aRace.getType());
			String bString = aRace.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}

		}
		b.append("</html>");
		infoLabel.setText(b.toString());
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	protected class RaceTableModel extends AbstractTableModel
	{
		private String[] lastColValue = new String[9];
		private int lastRow = -1;
//  		private ArrayList raceList = new ArrayList(Globals.getRaceMap().values());
		private ArrayList raceList = new ArrayList();

		public RaceTableModel()
		{
			updateModel();
		}

		public void fireTableDataChanged()
		{
			lastRow = -1;
			super.fireTableDataChanged();
		}

		public void updateModel()
		{
			raceList.clear();
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final Race aRace = (Race)it.next();
				if (accept(aRace))
				{
					raceList.add(aRace);
				}

			}
// Make sure empty race is in all lists
			if (!raceList.contains(Globals.s_EMPTYRACE))
			{
				raceList.add(0, Globals.s_EMPTYRACE);
			}

			fireTableDataChanged();
		}

		public void setFilter(String filterID, int index)
		{
			final ArrayList races = new ArrayList(Globals.getRaceMap().values());
			Iterator i;

			if (filterID.equals("All"))
			{
				raceList = races;
			}
			else if (filterID.equals("Qualified"))
			{
				raceList = new ArrayList();
				i = races.iterator();
				while (i.hasNext())
				{
					final Race theRace = (Race)i.next();
					if (theRace.passesPreReqTests())
					{
						raceList.add(theRace);
					}
				}
			}
			else if (index >= 2)
			{
				raceList = new ArrayList();
				i = races.iterator();
				while (i.hasNext())
				{
					final Race theRace = (Race)i.next();
					if (theRace.getType().equalsIgnoreCase(filterID))
						raceList.add(theRace);
				}
			}
			//
			// Make sure empty race is in all lists
			//
			if (!raceList.contains(Globals.s_EMPTYRACE))
			{
				raceList.add(0, Globals.s_EMPTYRACE);
			}
			fireTableDataChanged();
		}


		public int getColumnCount()
		{
			return 9;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public int getRowCount()
		{
			return raceList.size();
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Name";
				case 1:
					return "Stat Adjustments";
				case 2:
					return "PreReqs";
				case 3:
					return "Size";
				case 4:
					return "Speed";
				case 5:
					return "Vision";
				case 6:
					return "Favored Class";
				case 7:
					return "Lvl Adj";
				case 8:
					return "Source";
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (raceList.size() != 0)
			{
				if ((columnIndex < 0) || (columnIndex > 8))
				{
					return "Out of Bounds";
				}

				//
				// Maintain a copy of the last data displayed for this row so we don't call the
				// functions any more than we need to. Otherwise, as you drag the mouse across a
				// cell we will repeatedly recalculate the cell contents.
				//
				if (rowIndex != lastRow)
				{
					lastColValue = new String[9];
					lastRow = rowIndex;
				}
				else if (lastColValue[columnIndex] != null)
				{
					return lastColValue[columnIndex];
				}

				final Race race = (Race)raceList.get(rowIndex);
				String sRet = "";
				switch (columnIndex)
				{
					case 0:
						sRet = race.toString();
						break;

					case 1:
						final StringBuffer aString = new StringBuffer();
						for (int i = 0; i < Globals.s_ATTRIBSHORT.length; i++)
						{
							if (race.isNonability(i))
							{
								if (aString.length() > 0)
								{
									aString.append(" ");
								}
								aString.append(Globals.s_ATTRIBSHORT[i]).append(":Nonability");
							}
							else
							{
								if (race.getStatMod(i) != 0)
								{
									if (aString.length() > 0)
									{
										aString.append(" ");
									}
									aString.append(Globals.s_ATTRIBSHORT[i]).append(":").append(race.getStatMod(i));
								}
							}
						}
						sRet = aString.toString();
						break;

					case 2:
						sRet = race.preReqHTMLStrings();
						break;

					case 3:
						sRet = race.getSize();
						break;

					case 4:
						if (race.getMovements() != null)
						{
							final StringBuffer movelabel = new StringBuffer(race.getMovementType(0)).append(" ").append(race.getMovement(0)).append(Globals.getAbbrMovementDisplay());
							for (int i = 1; i < race.getMovements().length; i++)
							{
								movelabel.append(race.getMovementType(i)).append(" ").append(race.getMovement(i)).append(Globals.getAbbrMovementDisplay());
							}
							sRet = movelabel.toString();
						}
						break;

					case 5:
						sRet = race.getVision();
						break;

					case 6:
						sRet = (!race.getFavoredClass().equals("."))
							? race.getFavoredClass()
							: "Various";
						break;

					case 7:
						return new Integer(race.getLevelAdjustment());

					case 8:
						sRet = race.getSource();
						break;
				}
				lastColValue[columnIndex] = sRet;
				return sRet;
			}
			return null;
		}
	}

	/** TableModel to handle the full list of templates.
	 *  It pulls its data straight from the Globals.getTemplateList() ArrayList.
	 */
	public class AllTemplatesTableModel extends AbstractTableModel
	{
		public int curFilter;
		private int prevGlobalTemplateCount;
		private ArrayList displayTemplates = new ArrayList();

		/**
		 *
		 */
		public AllTemplatesTableModel()
		{
			updateModel();
		}

		public PCTemplate get(int index)
		{
			return (PCTemplate)displayTemplates.get(index);
		}

		public void updateModel()
		{
			displayTemplates.clear();

			for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
			{
				final PCTemplate aPCTemplate = (PCTemplate)it.next();
				if ((aPCTemplate.isVisible() % 2 == 1) && accept(aPCTemplate))
				{
					displayTemplates.add(aPCTemplate);
				}
			}

			fireTableDataChanged();
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
						if (pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3)
							displayTemplates.add(pcTmpl);
					}
					break;
				case 1: // Qualified
					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if ((pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3) && pcTmpl.isQualified())
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
		 * @return the name.. of the specified column
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
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if ((aPC != null) && (aPC.getTemplateList() != null))
			{
				PCTemplate t = (PCTemplate)currentPCdisplayTemplates.get(rowIndex);
				switch (columnIndex)
				{
					case 0:
						return t.toString();
					case 1:
// Column header is Removable - so why display the "Visible" properties?
// leaving this code commented out in case we want to use it
// merton_monk 3/16/02
/*						int visResult = t.isVisible();
						switch (visResult)
						{
							case 0:
								return "No";
							case 1:
								return "Yes";
							case 2:
								return "Export";
							case 3:
								return "Display";
						}
*/
						return (t.isRemovable() ? "Yes" : "No");
//						return "Error";
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
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC == null)
			{
				currentPCdisplayTemplates = new ArrayList(0);
			}
			else
			{
				prevGlobalTemplateCount = aPC.getTemplateList().size();
				currentPCdisplayTemplates = new ArrayList(prevGlobalTemplateCount);
				switch (filterID)
				{
					case 0:
						for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate)it.next();
							if (pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3)
								currentPCdisplayTemplates.add(pcTmpl);
						}
						break;
					case 1:
						for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate)it.next();
							if (pcTmpl.isVisible() == 0 || pcTmpl.isVisible() == 2)
								currentPCdisplayTemplates.add(pcTmpl);
						}
						break;
					case 2:
						currentPCdisplayTemplates.addAll(aPC.getTemplateList());
						break;
				}
			}
			currentPCdisplayTemplates.trimToSize();
			fireTableDataChanged();
			curFilter = filterID;
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllRaceFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		raceTableModel.updateModel();
		allTemplatesDataModel.updateModel();
		selectRaceByName(Globals.getCurrentPC().getRace().getName());
	}

	/**
	 * specifies wheter the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}
}

